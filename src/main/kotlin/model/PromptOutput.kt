package org.ivcode.kcomfy.model

data class PromptOutput (
    val node: String,
    val type: String,
    val filename: String,
    val subfolder: String?,
)
