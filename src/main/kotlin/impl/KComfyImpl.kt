package org.ivcode.kcomfy.impl

import org.ivcode.kcomfy.KComfy
import org.ivcode.kcomfy.annotations.WorkflowTemplate
import org.ivcode.kcomfy.core.api.ComfyCoreApi
import org.ivcode.kcomfy.model.PromptJob
import org.ivcode.kcomfy.templater.PromptRequestTemplate
import org.ivcode.kcomfy.templater.PromptRequestTemplateProvider
import org.ivcode.kcomfy.utils.findOn
import java.io.InputStream
import kotlin.reflect.KClass

internal class KComfyImpl (
    private val comfyApi: ComfyCoreApi,
    private val jobManager: KComfyJobManager,
    private val promptRequestTemplateProvider: PromptRequestTemplateProvider,
): KComfy {
    private val templates = mutableMapOf<KClass<*>, PromptRequestTemplate>()


    override fun <T: Any> prompt(model: T): PromptJob {
        val type = model::class

        val template = getWorkflowTemplate(type)
        val request = template.createTemplate(model)
        val response = comfyApi.postPrompt(request).execute()

        val body = response.body() ?: throw IllegalStateException("Response body is null")

        return PromptJobImpl(
            promptId = body.promptId,
            queueNumber = body.number,
            jobManager = jobManager,
        )
    }

    override fun view(type: String, filename: String, subfolder: String?): InputStream {
        val call = comfyApi.getView(
            type = type,
            filename = filename,
            subfolder = subfolder,
        )

        val response = call.execute()
        val body = response.body() ?: throw IllegalStateException("Response body is null")

        return body.byteStream()
    }

    override fun close() {
        // close the websocket connections
        jobManager.close()
    }

    private fun getWorkflowTemplate(type: KClass<*>): PromptRequestTemplate {
        var template = templates[type]
        if (template != null) {
            return template
        }

        val workflowTemplate = WorkflowTemplate::class.findOn(type)
            ?: throw IllegalArgumentException("Class ${type.qualifiedName} is not annotated with @WorkflowTemplate")

        val path = workflowTemplate.value
        require(path.isNotBlank()) { "Workflow template path cannot be blank" }

        template = promptRequestTemplateProvider.createFromResources(path)
        templates[type] = template

        return template
    }
}