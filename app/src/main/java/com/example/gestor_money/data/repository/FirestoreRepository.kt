package com.example.gestor_money.data.repository

import com.example.gestor_money.data.local.entities.BudgetEntity
import com.example.gestor_money.data.local.entities.CategoryEntity
import com.example.gestor_money.data.local.entities.TransactionEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for syncing data with Firestore
 */
@Singleton
class FirestoreRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    // ============ Transactions ============

    suspend fun syncTransaction(userId: String, transaction: TransactionEntity): Result<String> {
        return try {
            val data = hashMapOf(
                "amount" to transaction.amount,
                "date" to transaction.date,
                "description" to transaction.description,
                "categoryId" to transaction.categoryId,
                "type" to transaction.type,
                "lastModified" to transaction.lastModified
            )

            val docRef = if (transaction.cloudId != null) {
                // Update existing document
                firestore.collection("users")
                    .document(userId)
                    .collection("transactions")
                    .document(transaction.cloudId!!)
                    .also { it.set(data).await() }
            } else {
                // Create new document
                firestore.collection("users")
                    .document(userId)
                    .collection("transactions")
                    .add(data)
                    .await()
            }

            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTransaction(userId: String, cloudId: String): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(userId)
                .collection("transactions")
                .document(cloudId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchAllTransactions(userId: String): Result<List<Map<String, Any>>> {
        return try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("transactions")
                .get()
                .await()

            val transactions = snapshot.documents.mapNotNull { doc ->
                doc.data?.plus("cloudId" to doc.id)
            }
            Result.success(transactions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============ Categories ============

    suspend fun syncCategory(userId: String, category: CategoryEntity): Result<String> {
        return try {
            val data = hashMapOf(
                "name" to category.name,
                "icon" to category.icon,
                "color" to category.color,
                "type" to category.type,
                "lastModified" to category.lastModified
            )

            val docRef = if (category.cloudId != null) {
                firestore.collection("users")
                    .document(userId)
                    .collection("categories")
                    .document(category.cloudId!!)
                    .also { it.set(data).await() }
            } else {
                firestore.collection("users")
                    .document(userId)
                    .collection("categories")
                    .add(data)
                    .await()
            }

            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCategory(userId: String, cloudId: String): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(userId)
                .collection("categories")
                .document(cloudId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============ Budgets ============

    suspend fun syncBudget(userId: String, budget: BudgetEntity): Result<String> {
        return try {
            val data = hashMapOf(
                "categoryId" to budget.categoryId,
                "amount" to budget.amount,
                "month" to budget.month,
                "year" to budget.year,
                "lastModified" to budget.lastModified
            )

            val docRef = if (budget.cloudId != null) {
                firestore.collection("users")
                    .document(userId)
                    .collection("budgets")
                    .document(budget.cloudId!!)
                    .also { it.set(data).await() }
            } else {
                firestore.collection("users")
                    .document(userId)
                    .collection("budgets")
                    .add(data)
                    .await()
            }

            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteBudget(userId: String, cloudId: String): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(userId)
                .collection("budgets")
                .document(cloudId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
