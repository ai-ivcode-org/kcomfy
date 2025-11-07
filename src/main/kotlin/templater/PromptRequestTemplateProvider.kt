package org.ivcode.kcomfy.templater

interface PromptRequestTemplateProvider {
    fun createFromResources(path: String): PromptRequestTemplate
}