package org.ivcode.kcomfy

import org.ivcode.kcomfy.impl.KComfyFactory
import org.ivcode.kcomfy.model.PromptJob
import org.ivcode.kcomfy.model.PromptOutput
import java.io.InputStream

/**
 * Primary public API for interacting with the ComfyUI.
 *
 * Instances are {@code AutoCloseable} and should be closed when no longer needed to release
 * any underlying resources.
 */
interface KComfy: AutoCloseable {
    companion object {

        /**
         * Create a new [KComfy] instance configured for the given base URL.
         *
         * @param baseUrl the base HTTP URL of the Comfy core service (for example `http://localhost:3000`)
         * @return a configured [KComfy] instance
         */
        fun create (
            baseUrl: String,
        ) = KComfyFactory().create(
            baseURL = baseUrl,
        )
    }

    /**
     * Submit a prompt job for the provided model descriptor.
     *
     * The `model` parameter is generic to allow different model descriptor types used by implementations.
     *
     * @param T the model descriptor type accepted by the implementation
     * @param model the model descriptor used to create the prompt job
     * @return a [PromptJob] representing the submitted job (contains job id and metadata)
     */
    fun <T: Any> prompt(model: T): PromptJob

    /**
     * Convenience overload that opens the view stream for a given [PromptOutput].
     *
     * Delegates to [view(type, filename, subfolder)].
     *
     * @param output the prompt output metadata describing the view to open
     * @return an [InputStream] containing the view data; caller is responsible for closing it
     */
    fun view(output: PromptOutput) = view(output.type, output.filename, output.subfolder)

    /**
     * Open a stream to read a view (file) produced by a prompt.
     *
     * Implementations must return a readable [InputStream]. The caller is responsible for
     * closing the returned stream when finished. The stream may throw I/O related exceptions
     * while reading; callers should handle them accordingly.
     *
     * @param type the output type (for example a MIME type or domain-specific type identifier)
     * @param filename the filename to read from the remote service or storage
     * @param subfolder optional subfolder or namespace within storage, may be `null`
     * @return an [InputStream] for reading the view content
     * @throws java.io.IOException if the stream cannot be opened or read
     */
    fun view(
        type: String,
        filename: String,
        subfolder: String?
    ): InputStream
}