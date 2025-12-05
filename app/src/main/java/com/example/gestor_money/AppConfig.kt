package com.example.gestor_money

object AppConfig {
    // API Keys - Leídos desde .env en tiempo de compilación
    val OLLAMA_API_KEY: String = BuildConfig.OLLAMA_API_KEY
    val GEMINI_API_KEY: String = BuildConfig.GEMINI_API_KEY

    // API URLs
    val OLLAMA_BASE_URL: String = BuildConfig.OLLAMA_BASE_URL
    val GEMINI_BASE_URL: String = BuildConfig.GEMINI_BASE_URL
}