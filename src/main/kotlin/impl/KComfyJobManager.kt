package org.ivcode.kcomfy.impl

import org.ivcode.kcomfy.core.api.ComfyCoreApi
import org.ivcode.kcomfy.core.api.model.HistoryResponse
import org.ivcode.kcomfy.core.api.model.PostHistoryRequest
import org.ivcode.kcomfy.core.api.model.QueueEntry
import org.ivcode.kcomfy.model.JobStatus
import org.ivcode.kcomfy.model.PromptOutput
import org.ivcode.kcomfy.utils.SingleExecutionLock
import org.ivcode.kcomfy.core.ws.ComfyCoreWs
import org.ivcode.kcomfy.core.ws.model.ComfyWsListener
import org.ivcode.kcomfy.core.ws.model.ProgressMessage
import org.ivcode.kcomfy.core.ws.model.ProgressStateMessage
import org.ivcode.kcomfy.core.ws.model.StatusMessage
import org.slf4j.LoggerFactory
import retrofit2.Call
import java.io.IOException
import java.util.*
import java.util.concurrent.CompletableFuture

internal class KComfyJobManager (
    private val api: ComfyCoreApi,
    wsFactory: () -> ComfyCoreWs
): AutoCloseable {
    private val logger = LoggerFactory.getLogger(KComfyJobManager::class.java)

    private val ws: ComfyCoreWs = wsFactory()

    private val lock = SingleExecutionLock()

    @Volatile
    private var jobs: Map<UUID, Job> = emptyMap()

    private val waitThreads: MutableMap<UUID, CompletableFuture<List<PromptOutput>>> = mutableMapOf()

    private val wsListener: ComfyWsListener = object : ComfyWsListener {
        override fun onMessage(message: StatusMessage) {
            logger.debug("on status message {}", message)
            refresh()
        }

        override fun onMessage(message: ProgressStateMessage) {
            logger.debug("on progress state message {}", message)

            val job = jobs[message.promptId]
            if(job == null) {
                refresh()
                return
            }

            job.state = JobStatus.RUNNING
        }

        override fun onMessage(message: ProgressMessage) {
            logger.debug("on progress message {}", message)

            val job = jobs[message.promptId]
            if(job == null) {
                refresh()
                return
            }

            job.state = JobStatus.RUNNING
        }
    }

    init {
        ws.addListener(wsListener)
        ws.start()
    }

    fun getState(promptId: UUID): JobStatus {
        val job = jobs[promptId]
        return job?.state ?: JobStatus.NONE
    }

    fun getFuture(promptId: UUID): CompletableFuture<List<PromptOutput>> {
        var future = waitThreads[promptId]
        if(future != null) {
            return future
        }

        val job = jobs[promptId] ?: run {
            refresh()
            jobs[promptId] ?: throw IOException("Job $promptId not found")
        }

        // if job is completed, return completed future
        if(job.state.isCompleted) {
            return if(job.state == JobStatus.SUCCESS) {
                CompletableFuture.completedFuture(job.outputs)
            } else {
                CompletableFuture<List<PromptOutput>>().apply {
                    completeExceptionally(IOException("Job $promptId failed"))
                }
            }
        }

        // create or get existing future
        future = waitThreads.getOrPut(promptId) {
            CompletableFuture<List<PromptOutput>>()
        }

        return future
    }

    /**
     * Clears the given prompt from the ComfyUI history and queue.
     * Stops a running job if necessary.
     */
    fun close(promptId: UUID) {

        // TODO remove from queue
        api.postHistory(
            PostHistoryRequest(
                delete = listOf(promptId.toString()),
            )
        ).execute()

        refresh()
    }

    /**
     * Refresh the internal state from the API
     *
     * This method is synchronized to prevent multiple concurrent refreshes. Threads
     * that call this method while a refresh is already in progress will wait for
     * the ongoing refresh to complete and then return without performing another refresh.
     */
    private fun refresh() {
        logger.debug("refresh")
        try {
            val knownJobs = jobs.keys.toSet()

            val queueResponse = api.getQueue().executeOrThrow()!!
            val historyResponse = api.getHistoryAll().executeOrThrow(emptyMap())!!

            val pending = queueResponse.queuePending.asJobMap(JobStatus.PENDING)
            val running = queueResponse.queueRunning.asJobMap(JobStatus.RUNNING)
            val history = historyResponse.asJobMap()


            val jobs = mutableMapOf<UUID, Job>()
            jobs.putAll(pending)
            jobs.putAll(running)
            jobs.putAll(history)

            this.jobs = jobs


            // Notify the set of promptIds that are in history because they are done
            historyResponse.forEach { (promptIdStr, history) ->
                val promptId = UUID.fromString(promptIdStr)
                val future = waitThreads.remove(promptId) ?: return@forEach

                // complete with output values
                future.complete(history.toPromptOutputs())
            }

            // Notify the set of promptIds that are no longer known (removed from queue/history)
            (knownJobs - jobs.keys).forEach { promptId ->
                val future = waitThreads.remove(promptId) ?: return@forEach

                // complete with error because the job is gone
                future.completeExceptionally(IOException("job state unknown"))
            }
        } catch (e: Exception) {
            logger.error("error refreshing job states", e)
            throw e
        }
    }

    private fun List<QueueEntry>.asJobMap(state: JobStatus): Map<UUID, Job> {
        return this.associate { entry ->
            val promptId = UUID.fromString(entry.id)

            promptId to Job(
                promptId = promptId,
                state = state,
                lastUpdate = System.currentTimeMillis(),
            )
        }
    }

    private fun Map<String, HistoryResponse>.asJobMap(): Map<UUID, Job> {
        return this.entries.associate { (promptIdStr, hist) ->
            val promptId = UUID.fromString(promptIdStr)
            val state = if (hist.status?.status?.equals("success", ignoreCase = true) == true) {
                JobStatus.SUCCESS
            } else {
                JobStatus.ERROR
            }

            promptId to Job(
                promptId = promptId,
                state = state,
                lastUpdate = System.currentTimeMillis(),
                outputs = hist.toPromptOutputs()
            )
        }
    }

    private fun <T:Any> Call<T>.executeOrThrow(default: T? = null): T? {
        val response = this.execute()
        if(response.isSuccessful) {
            return response.body() ?: default
        } else {
            throw IOException(response.message())
        }
    }

    private fun HistoryResponse.toPromptOutputs(): List<PromptOutput> =
        outputs.entries.flatMap { (node, output) ->
            output.images.map { image ->
                PromptOutput(
                    node = node,
                    filename = image.filename,
                    subfolder = image.subfolder,
                    type = image.type
                )
            }
        }

    override fun close() {
        ws.removeListener(wsListener)
        ws.close()
    }
}

private data class Job (
    val promptId: UUID,
    var state: JobStatus,
    var lastUpdate: Long,
    var outputs: List<PromptOutput>? = null,
)