package org.ivcode.kcomfy.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class WorkflowTemplate(
    val value: String
)
