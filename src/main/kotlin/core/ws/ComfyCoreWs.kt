package org.ivcode.kcomfy.core.ws

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.OkHttpClient
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.ivcode.kcomfy.core.ws.model.*

class ComfyCoreWs (
    private val baseUrl: String,
    private val objectMapper: ObjectMapper = ObjectMapper(),
    okhttpFactory: () -> OkHttpClient = { OkHttpClient() }
): AutoCloseable {
    private val okhttp: OkHttpClient = okhttpFactory()

    private var webSocket: WebSocket? = null
    private val listeners = mutableSetOf<ComfyWsListener>()

    @Synchronized
    fun start() {
        if(webSocket != null) {
            throw IllegalStateException("WebSocket is already started")
        }

        // TODO handle wss
        val wsUrl = baseUrl.replaceFirst("http", "ws") + "/ws"

        webSocket = okhttp.newWebSocket(
            okhttp3.Request.Builder()
                .url(wsUrl)
                .build(),

            object : WebSocketListener() {
                override fun onMessage(webSocket: WebSocket, text: String) =
                    this@ComfyCoreWs.onMessage(webSocket, text)
            }
        )
    }

    @Synchronized
    override fun close() {
        if(webSocket == null) {
            return
        }

        webSocket?.close(1000, null)
        webSocket = null
        listeners.clear()

        okhttp.dispatcher().executorService().shutdown()
        okhttp.connectionPool().evictAll()
        okhttp.cache()?.close()
    }

    fun addListener(listener: ComfyWsListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: ComfyWsListener) {
        listeners.remove(listener)
    }

    private fun onMessage(webSocket: WebSocket, text: String) {
        val baseMessage = objectMapper.readValue(text, BaseMessage::class.java)
        when (baseMessage.type) {
            "status" -> {
                val message = objectMapper.treeToValue(baseMessage.data, StatusMessage::class.java)
                listeners.forEach { it.onMessage(message) }
            }
            "progress_state" -> {
                val message = objectMapper.treeToValue(baseMessage.data, ProgressStateMessage::class.java)
                listeners.forEach { it.onMessage(message) }
            }
            "progress" -> {
                val message = objectMapper.treeToValue(baseMessage.data, ProgressMessage::class.java)
                listeners.forEach { it.onMessage(message) }
            }
            else -> {
                println("Unknown message type: ${baseMessage.type}")
            }
        }
    }
}