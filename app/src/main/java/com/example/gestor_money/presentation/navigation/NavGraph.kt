package com.example.gestor_money.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gestor_money.presentation.screens.add_transaction.AddTransactionScreen
import com.example.gestor_money.presentation.screens.auth.AuthViewModel
import com.example.gestor_money.presentation.screens.auth.LoginScreen
import com.example.gestor_money.presentation.screens.auth.RegisterScreen
import com.example.gestor_money.presentation.screens.categories.CategoriesScreen
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
        startDestination = Screen.Login.route,
        modifier = modifier
    ) {
        // Pantallas de autenticación
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController = navController)
        }
        
        // Pantallas principales de la app
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.AddTransaction.route) {
            AddTransactionScreen(navController = navController)
        }
        composable(Screen.Transactions.route) {
            TransactionsScreen(navController = navController)
        }
        composable(Screen.Categories.route) {
            CategoriesScreen(navController = navController)
        }
        composable(Screen.Stats.route) {
            StatsScreen(navController = navController)
        }
        composable(Screen.Chat.route) {
            // ChatScreen ya no necesita navController directamente
            ChatScreen()
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
    }
}
