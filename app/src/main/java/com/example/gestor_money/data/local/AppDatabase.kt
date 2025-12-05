package com.example.gestor_money.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.gestor_money.data.local.dao.BudgetDao
import com.example.gestor_money.data.local.dao.CategoryDao
import com.example.gestor_money.data.local.dao.ChatDao
import com.example.gestor_money.data.local.dao.TransactionDao
import com.example.gestor_money.data.local.entities.BudgetEntity
import com.example.gestor_money.data.local.entities.CategoryEntity
import com.example.gestor_money.data.local.entities.ChatMessageEntity
import com.example.gestor_money.data.local.entities.TransactionEntity

@Database(
    entities = [
        CategoryEntity::class,
        TransactionEntity::class,
        BudgetEntity::class,
        ChatMessageEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao
    abstract fun chatDao(): ChatDao
}
