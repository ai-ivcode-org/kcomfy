import org.ivcode.kcomfy.KComfy
import org.ivcode.kcomfy.workflows.CheckpointTextToImage
import org.junit.jupiter.api.Test
import java.io.File


class KComfyIT {

    @Test
    fun testCheckpointTextToImage() {
        val kComfy = KComfy.create("http://localhost:8000")

        kComfy.use { api ->
            val job = api.prompt(CheckpointTextToImage(
                checkpoint = "sdXL_v10VAEFix",
                prompt = "a happy astronaut riding a horse in space, cartoon, vibrant"
            ))

            job.use {
                val outputs = it.getOutputs().get()   // wait and fetch outputs
                outputs.forEach { output ->
                    api.view(output).use { input ->
                        File(output.filename).outputStream().use { fileOut ->
                            input.copyTo(fileOut)
                        }
                    }
                }
            }
        }
    }
}