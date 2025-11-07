package org.ivcode.kcomfy.impl

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import okhttp3.OkHttpClient
import org.ivcode.kcomfy.KComfy
import org.ivcode.kcomfy.core.api.ComfyCoreApi
import org.ivcode.kcomfy.core.ws.ComfyCoreWs
import org.ivcode.kcomfy.templater.MustachePromptRequestTemplateProvider
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

internal class KComfyFactory {

    fun create(
        baseURL: String,
        mapper: ObjectMapper = createDefaultObjectMapper(),
        httpClient: OkHttpClient = createDefaultHttpClient(),
        retrofit: Retrofit = createDefaultRetrofit(baseURL, mapper, httpClient)
    ): KComfy {
        val comfyCodeApi = retrofit.create(ComfyCoreApi::class.java)
        val jobManager = KComfyJobManager(
            api = comfyCodeApi,
            wsFactory = {
                ComfyCoreWs(
                    objectMapper = mapper,
                    baseUrl = baseURL
                )
            },
        )
        val promptRequestTemplateProvider = MustachePromptRequestTemplateProvider(
            objectMapper = mapper
        )

        return KComfyImpl (
            comfyApi = comfyCodeApi,
            jobManager = jobManager,
            promptRequestTemplateProvider = promptRequestTemplateProvider,
        )
    }

    private fun createDefaultObjectMapper() = ObjectMapper().apply {
        registerModule(KotlinModule.Builder().build())
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)
    }

    private fun createDefaultHttpClient() = OkHttpClient.Builder()
        .build()

    private fun createDefaultRetrofit (
        baseURL: String,
        mapper: ObjectMapper,
        httpClient: OkHttpClient
    ) = Retrofit.Builder()
        .baseUrl(baseURL)
        .client(httpClient)
        .addConverterFactory(JacksonConverterFactory.create(mapper))
        .build()
}