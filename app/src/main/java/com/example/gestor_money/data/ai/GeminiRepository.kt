package com.example.gestor_money.data.ai

import com.example.gestor_money.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiRepository @Inject constructor() {
    
    private val model = GenerativeModel(
        modelName = "gemini-2.0-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )
    
    suspend fun sendMessage(prompt: String): Result<String> {
        return try {
            val response = model.generateContent(prompt)
            Result.success(response.text ?: "No response")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun chat(messages: List<Pair<String, String>>): Result<String> {
        return try {
            val chat = model.startChat()
            
            // Send all messages except the last one as history
            messages.dropLast(1).forEach { (role, message) ->
                if (role == "user") {
                    chat.sendMessage(message)
                }
            }
            
            // Send the last message and get response
            val lastMessage = messages.last().second
            val response = chat.sendMessage(lastMessage)
            
            Result.success(response.text ?: "No response")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
