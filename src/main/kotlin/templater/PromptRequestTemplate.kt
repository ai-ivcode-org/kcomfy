package org.ivcode.kcomfy.templater

import org.ivcode.kcomfy.core.api.model.PromptRequest

interface PromptRequestTemplate {
    fun createTemplate(model: Any): PromptRequest
}