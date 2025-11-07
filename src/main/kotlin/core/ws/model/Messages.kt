package org.ivcode.kcomfy.core.ws.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import java.util.UUID

data class BaseMessage (
    val type: String,
    val data: JsonNode
)

data class ProgressMessage (
    val value: Int,
    val max: Int,
    @JsonProperty("prompt_id")
    val promptId: UUID,
    val node: String
)

data class ProgressStateMessage (
    @JsonProperty("prompt_id")
    val promptId: UUID,
    val nodes: Map<String, ProgressStateNode>
)

data class ProgressStateNode (
    val value: Double?,
    val max: Double?,
    val state: String?,

    @JsonProperty("node_id")
    val nodeId: String?,

    @JsonProperty("prompt_id")
    val promptId: UUID?,

    @JsonProperty("display_node_id")
    val displayNodeId: String?,

    @JsonProperty("parent_node_id")
    val parentNodeId: String?,

    @JsonProperty("real_node_id")
    val realNodeId: String?,
)

data class StatusMessage (
    val status: StatusInfo,
    val sid: String?,
)

data class StatusInfo (
    @JsonProperty("exec_info")
    val execInfo: ExecInfo
)

data class ExecInfo (
    @JsonProperty("queue_remaining")
    val queueRemaining: Int,
)