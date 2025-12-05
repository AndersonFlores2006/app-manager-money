package com.example.gestor_money.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "chat_messages",
    indices = [Index("userId"), Index("timestamp")]
)
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cloudId: String? = null,
    val syncStatus: String = "SYNCED",
    val lastModified: Long = System.currentTimeMillis(),
    val userId: String = "local_user", // User ID from Firebase Auth
    val role: String, // "user", "assistant", "system"
    val content: String,
    val timestamp: Long,
    val isError: Boolean = false
)