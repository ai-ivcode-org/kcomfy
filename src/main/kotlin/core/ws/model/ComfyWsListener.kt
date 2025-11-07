package org.ivcode.kcomfy.core.ws.model

interface ComfyWsListener {
    fun onMessage(message: StatusMessage)
    fun onMessage(message: ProgressStateMessage)
    fun onMessage(message: ProgressMessage)
}