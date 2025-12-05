package com.example.gestor_money.data.ai

import com.example.gestor_money.data.remote.AiApiService
import com.example.gestor_money.data.remote.ChatRequest
import com.example.gestor_money.data.remote.Message
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiRepository @Inject constructor(
    private val aiApiService: AiApiService,
    private val ragEngine: RagEngine,
    private val geminiRepository: GeminiRepository
) {
    private val conversationHistory = mutableListOf<Message>()
    private var useGemini = false // Flag to switch to Gemini on Ollama failure
    
    suspend fun sendMessage(userMessage: String, includeContext: Boolean = true): Result<String> {
        return try {
            // Add user message to history
            conversationHistory.add(Message("user", userMessage))
            
            if (useGemini) {
                return sendWithGemini(userMessage, includeContext)
            }
            
            // Try Ollama first
            val ollamaResult = sendWithOllama(userMessage, includeContext)
            
            ollamaResult.fold(
                onSuccess = { response ->
                    conversationHistory.add(Message("assistant", response))
                    Result.success(response)
                },
                onFailure = { error ->
                    // Fallback to Gemini
                    useGemini = true
                    sendWithGemini(userMessage, includeContext)
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun sendWithOllama(userMessage: String, includeContext: Boolean): Result<String> {
        return try {
            val messages = mutableListOf<Message>()
            
            val systemPrompt = buildString {
                appendLine("Eres un asistente financiero personal experto en finanzas personales, ahorro e inversiones.")
                appendLine("Tu objetivo es ayudar al usuario a gestionar mejor su dinero, ahorrar y tomar decisiones de inversión inteligentes.")
                appendLine("Responde de manera clara, concisa y práctica.")
                appendLine()
                
                if (includeContext) {
                    val context = ragEngine.getFinancialContext()
                    appendLine(context)
                }
            }
            
            messages.add(Message("system", systemPrompt))
            messages.addAll(conversationHistory.takeLast(10))
            
            val request = ChatRequest(
                model = "qwen2.5:7b",
                messages = messages,
                temperature = 0.7,
                max_tokens = 500
            )
            
            val response = aiApiService.chat(request)
            val assistantMessage = response.choices.firstOrNull()?.message?.content
                ?: throw Exception("No response from Ollama")
            
            Result.success(assistantMessage)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun sendWithGemini(userMessage: String, includeContext: Boolean): Result<String> {
        return try {
            val prompt = buildString {
                appendLine("Eres un asistente financiero personal experto.")
                appendLine()
                
                if (includeContext) {
                    val context = ragEngine.getFinancialContext()
                    appendLine(context)
                    appendLine()
                }
                
                appendLine("Usuario: $userMessage")
            }
            
            geminiRepository.sendMessage(prompt).fold(
                onSuccess = { response ->
                    conversationHistory.add(Message("assistant", response))
                    Result.success(response)
                },
                onFailure = { error ->
                    Result.failure(error)
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getInvestmentAdvice(): Result<String> {
        return try {
            val context = ragEngine.getInvestmentContext()
            
            val prompt = buildString {
                appendLine("Eres un asesor de inversiones experto.")
                appendLine("Basándote en el siguiente contexto financiero, proporciona consejos de inversión personalizados:")
                appendLine()
                appendLine(context)
                appendLine()
                appendLine("Proporciona 3-5 recomendaciones de inversión específicas y prácticas.")
            }
            
            if (useGemini) {
                geminiRepository.sendMessage(prompt)
            } else {
                val request = ChatRequest(
                    model = "qwen2.5:7b",
                    messages = listOf(
                        Message("system", prompt),
                        Message("user", "Dame consejos de inversión basados en mi situación financiera")
                    ),
                    temperature = 0.7,
                    max_tokens = 600
                )
                
                try {
                    val response = aiApiService.chat(request)
                    val advice = response.choices.firstOrNull()?.message?.content
                        ?: throw Exception("No response")
                    Result.success(advice)
                } catch (e: Exception) {
                    useGemini = true
                    geminiRepository.sendMessage(prompt)
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun clearHistory() {
        conversationHistory.clear()
    }
}
