package com.example.aiassistant.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Streaming

interface ApiService {
    @Streaming
    @POST("v1/chat/completions")
    suspend fun chatStream(
        @Header("Authorization") auth: String,
        @Body request: ChatRequest
    ): Response<ResponseBody>
}
