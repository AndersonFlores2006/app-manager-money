package com.example.gestor_money.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gestor_money.data.local.entities.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages WHERE userId = :userId ORDER BY timestamp ASC")
    fun getAllMessages(userId: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity): Long

    @Update
    suspend fun updateMessage(message: ChatMessageEntity)

    @Delete
    suspend fun deleteMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages WHERE userId = :userId")
    suspend fun deleteAllMessages(userId: String)

    @Query("SELECT * FROM chat_messages WHERE id = :id")
    suspend fun getMessageById(id: Long): ChatMessageEntity?

    // Sync queries
    @Query("SELECT * FROM chat_messages WHERE userId = :userId AND syncStatus != 'SYNCED'")
    suspend fun getPendingSyncMessages(userId: String): List<ChatMessageEntity>

    @Query("UPDATE chat_messages SET syncStatus = :status, lastModified = :timestamp WHERE id = :id")
    suspend fun updateSyncStatus(id: Long, status: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE chat_messages SET cloudId = :cloudId, syncStatus = 'SYNCED', lastModified = :timestamp WHERE id = :id")
    suspend fun updateCloudId(id: Long, cloudId: String, timestamp: Long = System.currentTimeMillis())
}