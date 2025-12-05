package com.example.gestor_money.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "categories",
    indices = [Index("userId")]
)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cloudId: String? = null,
    val syncStatus: String = "SYNCED",
    val lastModified: Long = System.currentTimeMillis(),
    val userId: String = "local_user", // User ID from Firebase Auth
    val name: String,
    val icon: String, // Emoji or resource name
    val color: Int, // Hex color
    val type: String // "INCOME" or "EXPENSE"
)


