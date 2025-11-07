package org.ivcode.kcomfy.model

import java.util.UUID
import java.util.concurrent.CompletableFuture

/**
 * Represents a prompt processing job within the system.
 *
 * Implementations of this interface represent a single unit of work that can be queued,
 * executed and observed for completion. Implementations should document their concurrency
 * and lifecycle semantics (for example whether instances are single-use or reusable).
 */
interface PromptJob: AutoCloseable {
    /**
     * Unique identifier for the prompt associated with this job.
     *
     * This id is intended to correlate the job with the original prompt payload or record.
     */
    val promptId: UUID

    /**
     * Position of this job in the processing queue at the time of creation.
     *
     * A smaller number indicates an earlier position. Implementations may update this
     * value as the job moves through the queue or may keep the original queued position.
     */
    val queueNumber: Int

    /**
     * Returns the current status of the job.
     *
     * The status indicates whether the job is pending, running, completed successfully,
     * failed, or not found. Consumers should not assume this value is immutable — call
     * this method to obtain the latest status.
     */
    fun getStatus(): JobStatus

    /**
     * Returns a CompletableFuture that will complete with the list of outputs produced
     * by this job once processing finishes.
     *
     * - On successful completion the future completes normally with a `List<PromptOutput>`.
     * - On failure the future completes exceptionally.
     */
    fun getOutputs(): CompletableFuture<List<PromptOutput>>

    /**
     * Releases resources held by this job.
     *
     * Implementations should free any native or pooled resources, cancel or cleanup
     * outstanding work if appropriate, and make the object safe for GC. This method
     * is called when the job is no longer needed.
     *
     * Note that if the job is still running when this method is called, the job may be
     * cancelled or lost. Callers should ensure they have obtained results or handled
     * job completion before closing.
     */
    override fun close()
}

/**
 * Lifecycle and result states for a [PromptJob].
 *
 * Implementations should transition through these states to indicate progress and final outcome.
 */
enum class JobStatus (val isCompleted: Boolean) {
    /** The job is in the queue waiting to be processed */
    PENDING (false),
    /** The job is currently being processed */
    RUNNING (false),
    /** The job has been completed successfully */
    SUCCESS (true),
    /** The job has failed during processing */
    ERROR (true),
    /** The job is not found within the queue or history */
    NONE (false),
}
