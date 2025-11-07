package org.ivcode.kcomfy.templater

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.MustacheFactory

class MustachePromptRequestTemplateProvider (
    private val mustacheFactory: MustacheFactory = DefaultMustacheFactory(),
    private val objectMapper: ObjectMapper = ObjectMapper()
        .registerModule(KotlinModule.Builder().build())
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
): PromptRequestTemplateProvider {

    override fun createFromResources(path: String): PromptRequestTemplate {
        val mustache = mustacheFactory.compile(path);
        return MustachePromptRequestTemplate(mustache, objectMapper)
    }
}