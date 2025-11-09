# KComfy

Kotlin client for interacting with ComfyUI-style backends — render ComfyUI workflows from Kotlin data classes and manage prompt jobs programmatically.

[![Maven Snapshot](https://img.shields.io/badge/maven-snapshot-blue)](https://maven.ivcode.org.s3.amazonaws.com/snapshot)
[![License: Apache-2.0](https://img.shields.io/badge/license-Apache%202.0-blue)](https://www.apache.org/licenses/LICENSE-2.0)

Table of contents
- [Features](#features)
- [Quickstart](#quickstart)
- [Installation](#installation)
- [Usage](#usage)
- [Workflows](#workflows)

## Features
- Render ComfyUI JSON workflows from Mustache templates and Kotlin data classes
- Retrofit-based HTTP client with WebSocket job updates
- Example workflows and templates included
- Simple job lifecycle management with automatic cleanup

## Quickstart
1. Add KComfy to your project (see Installation).
2. Start a ComfyUI-compatible server (e.g., local ComfyUI on port 8000).
3. Use KComfy to render a workflow and submit a prompt.

## Installation

Kotlin (Gradle Kotlin DSL)
```kotlin
repositories {
    maven {
        url = uri("https://maven.ivcode.org.s3.amazonaws.com/snapshot")
    }
}

dependencies {
    implementation("org.ivcode:kcomfy:0.1-SNAPSHOT")
}
```

## Usage

Minimal example (quick)
```kotlin
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
```

_Note:_ This example assumes you have the checkpoint "sdXL_v10VAEFix" available on your ComfyUI server.

## Workflows

Workflows are Mustache templates plus Kotlin model classes. Templates should be on the classpath (e.g., `src/main/resources`) and are rendered using property names from the data class.

Example file layout:
```
src/
└── main/
    ├── kotlin/
    │   └── workflows/
    │       └── CheckpointTextToImage.kt
    └── resources/
        └── comfy/
            └── checkpoint_text_to_image.json.mustache
```

Example model and template annotation:
```kotlin
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
```

Template notes:
- Templates are rendered using Mustache; `{{prompt}}` maps to the `prompt` property of the data class.
- Place templates in your resources so they are on the runtime classpath.
- See the [`checkpoint_text_to_image.json.mustache`](src/main/resources/comfy/checkpoint_text_to_image.json.mustache) template file to see how the data class properties maps to the template.
