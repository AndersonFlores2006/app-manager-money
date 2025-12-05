package com.example.gestor_money.presentation.screens.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.gestor_money.domain.model.Transaction
import com.example.gestor_money.domain.model.TransactionType
import com.example.gestor_money.presentation.navigation.Screen
import com.example.gestor_money.presentation.screens.home.viewmodel.HomeUiState
import com.example.gestor_money.presentation.screens.home.viewmodel.HomeViewModel
import com.example.gestor_money.presentation.theme.Success
import com.example.gestor_money.presentation.theme.Error
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddTransaction.route) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction")
            }
        }
    ) { padding ->
        when (val state = uiState) {
            is HomeUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is HomeUiState.Success -> {
                HomeContent(
                    summary = state.summary,
                    modifier = Modifier.padding(padding)
                )
            }
            is HomeUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun HomeContent(
    summary: com.example.gestor_money.domain.model.FinancialSummary,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Balance Total",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            BalanceCard(balance = summary.balance)
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IncomeExpenseCard(
                    title = "Ingresos",
                    amount = summary.totalIncome,
                    isIncome = true,
                    modifier = Modifier.weight(1f)
                )
                IncomeExpenseCard(
                    title = "Gastos",
                    amount = summary.totalExpense,
                    isIncome = false,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Transacciones Recientes",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        }

        if (summary.recentTransactions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay transacciones aún",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(summary.recentTransactions) { transaction ->
                TransactionItem(transaction = transaction)
            }
        }
    }
}

@Composable
private fun BalanceCard(balance: Double) {
    val animatedBalance by animateFloatAsState(
        targetValue = balance.toFloat(),
        label = "balance"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    )
                )
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = formatCurrency(animatedBalance.toDouble()),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Saldo disponible",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun IncomeExpenseCard(
    title: String,
    amount: Double,
    isIncome: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = if (isIncome) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                    contentDescription = null,
                    tint = if (isIncome) Success else Error,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = formatCurrency(amount),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = if (isIncome) Success else Error
            )
        }
    }
}

@Composable
private fun TransactionItem(transaction: Transaction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.description,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatDate(transaction.date),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "${if (transaction.type == TransactionType.INCOME) "+" else "-"}${formatCurrency(transaction.amount)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (transaction.type == TransactionType.INCOME) Success else Error
            )
        }
    }
}

private fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "PE"))
    return format.format(amount)
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale("es", "ES"))
    return sdf.format(Date(timestamp))
}
