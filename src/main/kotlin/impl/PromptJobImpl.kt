package org.ivcode.kcomfy.impl

import org.ivcode.kcomfy.model.JobStatus
import org.ivcode.kcomfy.model.PromptJob
import org.ivcode.kcomfy.model.PromptOutput
import java.util.UUID
import java.util.concurrent.CompletableFuture

internal class PromptJobImpl(
    override val promptId: UUID,
    override val queueNumber: Int,
    private val jobManager: KComfyJobManager
) : PromptJob {
    override fun getStatus(): JobStatus {
        return jobManager.getState(promptId)
    }

    override fun getOutputs(): CompletableFuture<List<PromptOutput>> {
        return jobManager.getFuture(promptId)
    }

    override fun close() {
        jobManager.close(promptId)
    }
}