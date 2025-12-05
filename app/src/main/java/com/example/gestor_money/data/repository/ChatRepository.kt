package com.example.gestor_money.data.repository

import com.example.gestor_money.data.local.dao.ChatDao
import com.example.gestor_money.data.local.entities.ChatMessageEntity
import com.example.gestor_money.data.remote.Message
import com.example.gestor_money.data.sync.EntityType
import com.example.gestor_money.data.sync.SyncManager
import com.example.gestor_money.presentation.screens.chat.viewmodel.ChatMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val chatDao: ChatDao
) {

    fun getAllMessages(): Flow<List<ChatMessage>> {
        return chatDao.getAllMessages("local_user").map { entities ->
            entities.map { it.toChatMessage() }
        }
    }

    fun getAllMessagesAsRemote(): Flow<List<Message>> {
        return chatDao.getAllMessages("local_user").map { entities ->
            entities.map { it.toMessage() }
        }
    }

    suspend fun addMessage(role: String, content: String, isError: Boolean = false): Long {
        val entity = ChatMessageEntity(
            userId = "local_user", // Simplificado, sin auth por ahora
            role = role,
            content = content,
            timestamp = System.currentTimeMillis(),
            isError = isError
        )
        return chatDao.insertMessage(entity)
    }

    suspend fun clearAllMessages() {
        chatDao.deleteAllMessages("local_user")
    }
}

// Extension functions
fun ChatMessageEntity.toChatMessage(): ChatMessage {
    return ChatMessage(
        content = this.content,
        isUser = this.role == "user",
        timestamp = this.timestamp,
        isError = this.isError
    )
}

fun ChatMessageEntity.toMessage(): Message {
    return Message(
        role = this.role,
        content = this.content
    )
}