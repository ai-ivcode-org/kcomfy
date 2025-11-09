package org.ivcode.gradle.s3mvn

import org.gradle.api.Project
import java.net.URI

open class S3MvnPluginExtension {
    var url: URI? = null
    var awsKey: String? = null
    var awsSecret: String? = null
}

/**
 * Get the username or GITHUB_ACTOR
 */
internal fun S3MvnPluginExtension.getAwsKeyOrDefault(project: Project) =
    awsKey ?: project.findProperty("AWS_ACCESS_KEY_ID") as String?

/**
 * Get the token or GITHUB_TOKEN
 */
internal fun S3MvnPluginExtension.getAwsSecretOrDefault(project: Project) =
    awsSecret ?: project.findProperty("AWS_SECRET_ACCESS_KEY") as String?
