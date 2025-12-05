package com.example.gestor_money.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gestor_money.presentation.screens.add_transaction.AddTransactionScreen
import com.example.gestor_money.presentation.screens.chat.ChatScreen
import com.example.gestor_money.presentation.screens.home.HomeScreen
import com.example.gestor_money.presentation.screens.settings.SettingsScreen
import com.example.gestor_money.presentation.screens.stats.StatsScreen
import com.example.gestor_money.presentation.screens.transactions.TransactionsScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.AddTransaction.route) {
            AddTransactionScreen(navController = navController)
        }
        composable(Screen.Transactions.route) {
            TransactionsScreen(navController = navController)
        }
        composable(Screen.Stats.route) {
            StatsScreen(navController = navController)
        }
        composable(Screen.Chat.route) {
            ChatScreen(navController = navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
    }
}
