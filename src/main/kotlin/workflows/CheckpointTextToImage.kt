package org.ivcode.kcomfy.workflows

import org.ivcode.kcomfy.annotations.WorkflowTemplate
import org.ivcode.kcomfy.model.nodes.SamplerName
import org.ivcode.kcomfy.model.nodes.Scheduler
import kotlin.random.Random

/**
 * Workflow template data for checkpoint-based text-to-image generation.
 *
 * This data class is used to populate the template located at
 * `comfy/checkpoint_text_to_image.json.mustache`.
 *
 * All fields are mutable to allow easy modification before rendering the template.
 *
 * @param checkpoint Identifier or path of the model checkpoint to use.
 * @param prompt Positive prompt text describing the desired image.
 * @param negativePrompt Negative prompt text used to discourage features (defaults to empty).
 * @param width Output image width in pixels (default 1024).
 * @param height Output image height in pixels (default 1024).
 * @param batch Number of images to generate in a single batch (default 1).
 * @param steps Number of sampling/diffusion steps (default 30).
 * @param cfg Classifier-free guidance scale; higher values increase adherence to the prompt (default 7.0).
 * @param sampler Sampling algorithm to use (defaults to [SamplerName.EULER]).
 * @param scheduler Scheduling strategy used by the sampler (defaults to [Scheduler.NORMAL]).
 * @param seed Random seed for the generation. Default is a non-negative Long generated with
 *             `Random.nextLong(0, Long.MAX_VALUE)` to avoid negative seeds.
 * @param denoise Denoising strength in [0.0, 1.0] where 1.0 applies full denoising (default 1.0).
 */
@WorkflowTemplate("comfy/checkpoint_text_to_image.json.mustache")
data class CheckpointTextToImage(
    var checkpoint: String,
    var prompt: String,
    var negativePrompt: String = "",
    var width: Int = 1024,
    var height: Int = 1024,
    var batch: Int = 1,
    var steps: Int = 30,
    var cfg: Double = 7.0,
    var sampler: SamplerName = SamplerName.EULER,
    var scheduler: Scheduler = Scheduler.NORMAL,
    var seed: Long = Random.nextLong(0, Long.MAX_VALUE),
    var denoise: Double = 1.0
)