package org.ivcode.kcomfy.templater

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.mustachejava.Mustache
import org.ivcode.kcomfy.core.api.model.Node
import org.ivcode.kcomfy.core.api.model.PromptRequest
import java.io.StringWriter


internal class MustachePromptRequestTemplate (
    private val mustache: Mustache,
    private val objectMapper: ObjectMapper,
): PromptRequestTemplate {

    private val type = object : TypeReference<Map<String, Node>>() {}

    override fun createTemplate(model: Any): PromptRequest {
        val json = StringWriter().use { writer ->
            mustache.execute(writer, model)
            writer.flush()

            writer.toString()
        }

        val nodes = objectMapper.readValue(json, type)
        return PromptRequest(nodes)
    }
}