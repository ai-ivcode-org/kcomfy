package org.ivcode.kcomfy

import org.ivcode.kcomfy.workflows.CheckpointTextToImage

fun main() {
    val baseUrl = "http://localhost:8000"
    KComfy.create(baseUrl).use { api ->
        api.prompt(CheckpointTextToImage (
            checkpoint = "sdXL_v10VAEFix",
            prompt = "a cartoon style illustration of a happy astronaut riding a horse in space, vibrant colors, detailed, high quality",
            negativePrompt = "lowres, bad anatomy, error body, error hands, error fingers"
        )).use {
            println(it.promptId)
            println(it.queueNumber)

            val outputs = it.getOutputs().get()
            println(outputs)
        }
    }
}