package com.example.gestor_money.data.ai

import com.example.gestor_money.data.repository.TransactionRepository
import com.example.gestor_money.domain.model.TransactionType
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * RAG Engine: Retrieves relevant financial data to provide context to the AI
 */
@Singleton
class RagEngine @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    suspend fun getFinancialContext(): String {
        val transactions = transactionRepository.getAllTransactions().first()
        
        if (transactions.isEmpty()) {
            return "El usuario aún no tiene transacciones registradas."
        }

        val totalIncome = transactions
            .filter { it.type == "INCOME" }
            .sumOf { it.amount }
        
        val totalExpense = transactions
            .filter { it.type == "EXPENSE" }
            .sumOf { it.amount }
        
        val balance = totalIncome - totalExpense
        
        // Get recent transactions
        val recentTransactions = transactions.take(10)
        val transactionsSummary = recentTransactions.joinToString("\n") { tx ->
            val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(tx.date))
            val type = if (tx.type == "INCOME") "Ingreso" else "Gasto"
            "- $date: $type de S/ ${tx.amount} - ${tx.description}"
        }
        
        // Calculate spending by category (simplified)
        val expensesByDescription = transactions
            .filter { it.type == "EXPENSE" }
            .groupBy { it.description }
            .mapValues { it.value.sumOf { tx -> tx.amount } }
            .entries
            .sortedByDescending { it.value }
            .take(5)
        
        val topExpenses = expensesByDescription.joinToString("\n") { 
            "- ${it.key}: S/ ${it.value}"
        }
        
        return buildString {
            appendLine("=== CONTEXTO FINANCIERO DEL USUARIO ===")
            appendLine()
            appendLine("Balance actual: S/ $balance")
            appendLine("Total ingresos: S/ $totalIncome")
            appendLine("Total gastos: S/ $totalExpense")
            appendLine()
            appendLine("Top 5 categorías de gasto:")
            appendLine(topExpenses.ifEmpty { "No hay gastos registrados" })
            appendLine()
            appendLine("Últimas 10 transacciones:")
            appendLine(transactionsSummary)
            appendLine()
            appendLine("=== FIN DEL CONTEXTO ===")
        }
    }
    
    suspend fun getInvestmentContext(): String {
        val transactions = transactionRepository.getAllTransactions().first()
        val totalIncome = transactions
            .filter { it.type == "INCOME" }
            .sumOf { it.amount }
        val totalExpense = transactions
            .filter { it.type == "EXPENSE" }
            .sumOf { it.amount }
        val balance = totalIncome - totalExpense
        
        return buildString {
            appendLine("Balance disponible: S/ $balance")
            appendLine("Ingresos totales: S/ $totalIncome")
            appendLine("Gastos totales: S/ $totalExpense")
            if (balance > 0) {
                appendLine("Capacidad de ahorro: S/ $balance")
            }
        }
    }
}
