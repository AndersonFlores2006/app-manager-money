package com.example.gestor_money.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.gestor_money.data.local.AppDatabase
import com.example.gestor_money.data.local.dao.BudgetDao
import com.example.gestor_money.data.local.dao.CategoryDao
import com.example.gestor_money.data.local.dao.ChatDao
import com.example.gestor_money.data.local.dao.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Add sync columns to transactions table
            db.execSQL("ALTER TABLE transactions ADD COLUMN cloudId TEXT DEFAULT NULL")
            db.execSQL("ALTER TABLE transactions ADD COLUMN syncStatus TEXT NOT NULL DEFAULT 'SYNCED'")
            db.execSQL("ALTER TABLE transactions ADD COLUMN lastModified INTEGER NOT NULL DEFAULT ${System.currentTimeMillis()}")

            // Add sync columns to categories table
            db.execSQL("ALTER TABLE categories ADD COLUMN cloudId TEXT DEFAULT NULL")
            db.execSQL("ALTER TABLE categories ADD COLUMN syncStatus TEXT NOT NULL DEFAULT 'SYNCED'")
            db.execSQL("ALTER TABLE categories ADD COLUMN lastModified INTEGER NOT NULL DEFAULT ${System.currentTimeMillis()}")

            // Add sync columns to budgets table
            db.execSQL("ALTER TABLE budgets ADD COLUMN cloudId TEXT DEFAULT NULL")
            db.execSQL("ALTER TABLE budgets ADD COLUMN syncStatus TEXT NOT NULL DEFAULT 'SYNCED'")
            db.execSQL("ALTER TABLE budgets ADD COLUMN lastModified INTEGER NOT NULL DEFAULT ${System.currentTimeMillis()}")
        }
    }

    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Add userId column to transactions table
            db.execSQL("ALTER TABLE transactions ADD COLUMN userId TEXT NOT NULL DEFAULT 'local_user'")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_transactions_userId ON transactions(userId)")

            // Add userId column to categories table
            db.execSQL("ALTER TABLE categories ADD COLUMN userId TEXT NOT NULL DEFAULT 'local_user'")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_categories_userId ON categories(userId)")

            // Add userId column to budgets table
            db.execSQL("ALTER TABLE budgets ADD COLUMN userId TEXT NOT NULL DEFAULT 'local_user'")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_budgets_userId ON budgets(userId)")
        }
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "money_manager_db"
        )
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .build()
    }

    @Provides
    fun provideCategoryDao(database: AppDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    fun provideTransactionDao(database: AppDatabase): TransactionDao {
        return database.transactionDao()
    }

    @Provides
    fun provideBudgetDao(database: AppDatabase): BudgetDao {
        return database.budgetDao()
    }

    @Provides
    fun provideChatDao(database: AppDatabase): ChatDao {
        return database.chatDao()
    }
}
