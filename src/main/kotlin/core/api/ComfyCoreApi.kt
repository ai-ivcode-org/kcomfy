package org.ivcode.kcomfy.core.api

import okhttp3.ResponseBody
import org.ivcode.kcomfy.core.api.model.*
import retrofit2.Call
import retrofit2.http.*
import java.util.stream.Stream

/**
 * Retrofit API surface for communicating with the Comfy UI backend.
 *
 * This interface declares endpoints exposed by the Comfy UI service. Each method
 * returns a Retrofit [Call] that can be executed synchronously with
 * [retrofit2.Call.execute] or asynchronously with [retrofit2.Call.enqueue].
 *
 * Error handling:
 * - Network errors and non-2xx responses will be surfaced via the Retrofit
 *   Call result or callback.
 * - Deserialization issues will be thrown when executing the call.
 */
internal interface ComfyCoreApi {

    /**
     * Submit a prompt request to the Comfy UI service.
     *
     * Triggers processing of a prompt (for example: image generation or other
     * prompt-driven actions). The request payload is a [PromptRequest] and the
     * server response is deserialized into a [PromptResponse].
     *
     * @param request the prompt request payload to send in the POST body.
     * @return a Retrofit [Call] that yields a [PromptResponse] on success.
     */
    @POST("/prompt")
    fun postPrompt(@Body request: PromptRequest): Call<PromptResponse>

    /**
     * Retrieve the full history list from the service.
     *
     * Returns a list of historical entries represented as [HistoryResponse]
     * objects. The ordering (most-recent-first or server-defined) depends on
     * the server implementation.
     *
     * @return a Retrofit [Call] that yields a List of [HistoryResponse].
     */
    @GET("/history")
    fun getHistoryAll(): Call<Map<String, HistoryResponse>>

    /**
     * Retrieve a single history entry by its prompt ID.
     *
     * Use this to fetch details for a specific prompt/job previously recorded
     * by the service.
     *
     * @param promptId the identifier of the history entry to fetch (path parameter).
     * @return a Retrofit [Call] that yields a [HistoryResponse] for the given ID.
     */
    @GET("/history/{promptId}")
    fun getHistory(@Path("promptId") promptId: String): Call<HistoryResponse>

    /**
     * Modify server-side history entries.
     *
     * Contrary to `post` semantics in many APIs, this endpoint is used to remove
     * or clear history entries on the server, not to add a new history record.
     * It accepts a [PostHistoryRequest] which contains one of two actions:
     * - `clear` (boolean): when true, instructs the server to remove all history entries.
     * - `delete` (list of ids): when provided, instructs the server to delete only
     *    the specified history entry IDs (typically prompt IDs).
     *
     * The server's exact behavior when both fields are present is server-defined;
     * clients should avoid sending conflicting instructions (for example, `clear`
     * and a non-empty `delete` list) unless the server documents a deterministic
     * resolution.
     *
     * On success the endpoint returns no response body.
     *
     * @param request the history modification payload controlling which entries to remove.
     * @return a Retrofit [Call] that yields Unit on success (no response body expected).
     */
    @POST("/history")
    fun postHistory(@Body request: PostHistoryRequest): Call<Unit>


    /**
     * Retrieve the server queue containing currently running and pending jobs.
     *
     * The ComfyUI `/queue` endpoint returns a JSON object with two arrays:
     * - `queue_running`: entries currently executing
     * - `queue_pending`: entries waiting to run
     *
     * Each entry is typically encoded as a JSON array of the form:
     *   [ index, id, nodesMap, metaObject, outputsArray ]
     * Where:
     * - index: numeric position in the queue
     * - id: job identifier (usually a UUID string)
     * - nodesMap: map of node-id -> node definition (the prompt graph for the job)
     * - metaObject: optional metadata for the job
     * - outputsArray: optional list of output node ids or references
     *
     * This method returns a Retrofit [Call] that deserializes the response into
     * a [QueueResponse] model (see `api.model.QueueResponse` / `QueueEntry`).
     *
     * Notes:
     * - The exact shapes of `nodesMap`, `metaObject`, and `outputsArray` can vary
     *   between ComfyUI versions and plugins; `QueueResponse` should handle that
     *   variability (for example by capturing unknown fields into maps).
     * - Handle network errors and non-2xx responses when executing the call.
     *
     * @return a Retrofit [Call] that yields a [QueueResponse] representing running and pending jobs.
     */
    @GET("/queue")
    fun getQueue(): Call<QueueResponse>


    /**
     * Retrieve a raw view (file-like resource) from the service.
     *
     * Query parameters:
     * - `filename` (required): name of the file to retrieve.
     * - `subfolder` (optional): a subfolder path or identifier to locate the file.
     * - `type` (optional): content-type or category hint for the request.
     * - `rand` (optional): numeric value used for randomness or cache-busting.
     *
     * The response is returned as a stream of bytes. Callers should consume and
     * close the stream when handling the response. Note that depending on the
     * Retrofit and converter configuration, returning a Java [Stream] may
     * require a custom converter; alternatively okhttp3.ResponseBody can be used
     * for raw binary streaming.
     *
     * @param filename the filename to request (required).
     * @param subfolder optional subfolder under which the file is located.
     * @param type optional type or content hint for the request.
     * @param rand optional numeric value used by the server for randomness or cache-busting.
     * @return a Retrofit [Call] that yields a [Stream]<[Byte]> representing the raw bytes.
     */
    @GET("/view")
    fun getView(
        @Query("filename") filename: String,
        @Query("subfolder") subfolder: String? = null,
        @Query("type") type: String? = null,
        @Query("rand") rand: Number? = null,
    ): Call<ResponseBody>
}