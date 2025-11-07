package org.ivcode.kcomfy.core.api.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Lightweight, Jackson-friendly representation of a history entry returned by the
 * Comfy UI history endpoint.
 *
 * This model intentionally keeps several fields as `JsonNode` so Jackson can
 * deserialize the raw JSON without custom, manual parsing logic. Callers can
 * convert these JsonNodes into typed structures as needed using the
 * project's ObjectMapper.
 */
internal data class HistoryResponse(
    val prompt: HistoryPrompt? = null,
    val outputs: Map<String, HistoryOutput> = emptyMap(),
    val status: HistoryStatus? = null,
    val meta: Map<String, Any>? = null,
)

@JsonFormat(shape = JsonFormat.Shape.ARRAY)
internal data class HistoryPrompt (
    @JsonProperty(index = 0) val index: Int,
    @JsonProperty(index = 1) val promptId: String,
    @JsonProperty(index = 2) val nodes: Map<String, Node>,
    @JsonProperty(index = 3) val meta: Map<String, Any>? = null,
    @JsonProperty(index = 4) val outputs: List<String>? = null
)

internal data class HistoryOutput(
    val images: List<HistoryImage> = emptyList()
)

internal data class HistoryImage (
    val filename: String,
    val subfolder: String,
    val type: String,
)

internal data class HistoryStatus(
    @JsonProperty("status_str")
    val status: String,
    val completed: Boolean,
    val messages: List<HistoryStatusMessage>,
)

@JsonFormat(shape = JsonFormat.Shape.ARRAY)
internal data class HistoryStatusMessage(
    @JsonProperty(index = 0) val text: String,
    @JsonProperty(index = 1) val timestamp: HistoryStatusMessageTimestamp,
)

internal data class HistoryStatusMessageTimestamp (
    val nodes: List<Any>?,
    @JsonProperty("prompt_id")
    val promptId: String?,
    val timestamp: Long?,
)