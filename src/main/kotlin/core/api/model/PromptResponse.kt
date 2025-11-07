package org.ivcode.kcomfy.core.api.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID


/**
 * Response returned by the server after a prompt request.
 *
 * Represents the essential identifiers and status information for a processed
 * prompt. The structure mirrors the JSON returned by the Comfy UI backend.
 *
 * @property promptId unique identifier assigned to the submitted prompt (JSON property "prompt_id").
 *
 * @property number numeric index or ordinal associated with this response. The
 *         exact meaning depends on server implementation (for example: image index).
 *
 * @property nodeErrors errors produced by individual nodes while processing the
 *         prompt. Kept as [Any] because server responses can contain various
 *         shapes (null, boolean, string, map, or list). Clients should inspect
 *         the runtime type or adapt the model to a more specific type if the
 *         server provides a stable schema for this field.
 */
internal data class PromptResponse (
    @JsonProperty("prompt_id")
    val promptId: UUID,

    val number: Int,

    @JsonProperty("node_errors")
    val nodeErrors: Any,
)
