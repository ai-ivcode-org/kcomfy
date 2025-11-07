package org.ivcode.kcomfy.core.api.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Represents the server's queue response which returns two arrays:
 * - `queue_running`: currently executing jobs
 * - `queue_pending`: jobs waiting to run
 *
 * Each entry in these arrays is an array-shaped element (see ComfyUI sample):
 * [ index, id, nodesMap, metaObject, outputsArray ]
 */
internal data class QueueResponse(
    @JsonProperty("queue_running")
    val queueRunning: List<QueueEntry> = listOf(),

    @JsonProperty("queue_pending")
    val queuePending: List<QueueEntry> = listOf(),
)

/**
 * A single queue entry. ComfyUI returns entries as JSON arrays; this class
 * deserializes that array into typed fields using a JsonNode-based creator.
 */
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
internal data class QueueEntry (
    @JsonProperty(index = 0) val index: Int,
    @JsonProperty(index = 1) val id: String,
    @JsonProperty(index = 2) val nodes: Map<String, Node>,
    @JsonProperty(index = 3) val meta: Map<String, Any>,
    @JsonProperty(index = 4) val outputs: List<String>?,
)