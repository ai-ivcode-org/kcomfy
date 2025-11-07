package org.ivcode.kcomfy.core.api.model

/**
 * Request payload used to modify the server-side history.
 *
 * This data class represents the body sent to the history endpoint when the client
 * wants to clear the entire history or remove specific history entries.
 *
 * Usage notes:
 * - If `clear` is true, the server should remove all history entries.
 * - If `delete` contains one or more IDs, the server should remove only those entries.
 * - The exact server behavior when both `clear` is true and `delete` is non-empty
 *   is server-defined; clients should avoid sending conflicting instructions.
 *
 * @property clear When true, instructs the server to clear all stored history entries.
 *                 Defaults to false.
 * @property delete A list of history entry identifiers (typically prompt IDs) that
 *                  should be removed. Defaults to an empty list. Identifiers should
 *                  match those returned by the history endpoints.
 */
internal data class PostHistoryRequest (

    /**
     * Instructs the server to clear all history entries when true.
     */
    val clear: Boolean = false,

    /**
     * Identifiers of history entries to delete. When empty (default), no specific
     * deletions are requested.
     */
    val delete: List<String> = listOf(),
)
