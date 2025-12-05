package com.example.gestor_money.data.remote

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AiApiService {
    @Headers("Content-Type: application/json")
    @POST("v1/chat/completions")
    suspend fun chat(@Body request: ChatRequest): ChatResponse
}
