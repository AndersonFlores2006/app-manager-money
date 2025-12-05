package com.example.gestor_money.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val icon: String, // Emoji or resource name
    val color: Int, // Hex color
    val type: String // "INCOME" or "EXPENSE"
)
