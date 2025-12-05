package com.example.gestor_money.presentation.screens.transactions

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun TransactionsScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Transactions",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Transaction list coming soon...",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
