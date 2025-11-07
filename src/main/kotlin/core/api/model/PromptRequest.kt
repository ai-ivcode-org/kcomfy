package org.ivcode.kcomfy.core.api.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Request payload representing a prompt graph sent to the Comfy UI service.
 *
 * The `prompt` map contains node identifiers mapped to their corresponding
 * [Node] definitions. Each node describes its inputs, the concrete node class
 * (serialized as `class_type`), and optional metadata (serialized as `_meta`).
 *
 * This structure mirrors the Comfy UI prompt graph where nodes reference other
 * nodes by id inside their input values.
 *
 * @property prompt mapping of node identifier -> [Node] describing the node configuration.
 */
data class PromptRequest (
    val prompt: Map<String, Node>
)

/**
 * A node within the prompt graph.
 *
 * - `inputs`: a mapping of input names to values. Values are intentionally
 *   typed as `Any` because nodes accept flexible input types (primitive values,
 *   collections, or references to other nodes).
 * - `classType`: the fully-qualified or short name of the node class. This is
 *   serialized to/from JSON using the `class_type` key.
 * - `meta`: a simple string-to-string map containing auxiliary metadata for the
 *   node; it is serialized using the `_meta` key.
 *
 * @property inputs map of input names to their values (flexible runtime types).
 * @property classType node type name (JSON property `class_type`).
 * @property meta metadata map with string keys and values (JSON property `_meta`).
 */
data class Node (
    val inputs: Map<String, Any>,

    @JsonProperty("class_type")
    val classType: String,

    @JsonProperty("_meta")
    val meta: Map<String, String>
)