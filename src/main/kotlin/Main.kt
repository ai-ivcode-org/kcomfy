package org.ivcode.kcomfy

import org.ivcode.kcomfy.workflows.CheckpointTextToImage
import java.io.File

fun main() {
    // Create the ComfyUI API client.
    val kComfy = KComfy.create("http://localhost:8000")

    kComfy.use { api ->
        // Send a prompt request to generate an image using a specific checkpoint.
        api.prompt(CheckpointTextToImage (
            checkpoint = "sdXL_v10VAEFix",
            prompt = "a real photo of a happy astronaut riding a horse in space, vibrant colors, detailed, high quality",
            negativePrompt = "lowres, bad anatomy, error body, error hands, error fingers"
        )).use {
            // Wait for the job to complete. Get the outputs when done.
            val outputs = it.getOutputs().get()

            // Save each output image to a file.
            outputs.forEach { output ->
                val input = api.view(output)
                File(output.filename).outputStream().use { fileOut ->
                    input.copyTo(fileOut)
                }
            }
        } // on prompt-job close, remove jobs from server
    } // on closing KComfy, clean up resources and close web-socket
}