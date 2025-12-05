package com.example.gestor_money.data.remote

data class ChatRequest(
    val model: String,
    val messages: List<Message>,
    val temperature: Double = 0.7,
    val max_tokens: Int = 1000
)

data class Message(
    val role: String, // "system", "user", "assistant"
    val content: String
)

data class ChatResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)
