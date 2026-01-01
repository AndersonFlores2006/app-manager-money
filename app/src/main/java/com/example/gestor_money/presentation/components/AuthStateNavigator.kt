package com.example.gestor_money.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gestor_money.presentation.navigation.Screen
import com.example.gestor_money.presentation.screens.add_transaction.AddTransactionScreen
import com.example.gestor_money.presentation.screens.auth.AuthUiState
import com.example.gestor_money.presentation.screens.auth.AuthViewModel
import com.example.gestor_money.presentation.screens.auth.LoginScreen
import com.example.gestor_money.presentation.screens.auth.RegisterScreen
import com.example.gestor_money.presentation.screens.categories.CategoriesScreen
import com.example.gestor_money.presentation.screens.chat.ChatScreen
import com.example.gestor_money.presentation.screens.home.HomeScreen
import com.example.gestor_money.presentation.screens.settings.SettingsScreen
import com.example.gestor_money.presentation.screens.stats.StatsScreen
import com.example.gestor_money.presentation.screens.transactions.TransactionsScreen

/**
 * Composable que maneja la navegación condicional basada en el estado de autenticación
 * - Si el usuario NO está autenticado: muestra Login
 * - Si el usuario SÍ está autenticado: muestra la app principal
 */
@Composable
fun AuthStateNavigator(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState = authViewModel.uiState.collectAsState().value

    if (authState.isAuthenticated) {
        // Usuario autenticado - mostrar la app principal con Scaffold y BottomNavigationBar
        androidx.compose.material3.Scaffold(
            modifier = modifier,
            bottomBar = { BottomNavigationBar(navController = navController) }
        ) { innerPadding ->
            MainAppNavigator(
                navController = navController, 
                modifier = Modifier.padding(innerPadding)
            )
        }
    } else {
        // Usuario NO autenticado - mostrar pantallas de autenticación
        AuthNavigator(navController = navController, modifier = modifier)
    }
}

/**
 * Navegador para las pantallas de autenticación (Login/Register)
 */
@Composable
private fun AuthNavigator(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
        modifier = modifier
    ) {
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController = navController)
        }
    }
}

/**
 * Navegador para las pantallas principales de la aplicación
 */
@Composable
private fun MainAppNavigator(
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
        composable(Screen.Categories.route) {
            CategoriesScreen(navController = navController)
        }
        composable(Screen.Stats.route) {
            StatsScreen(navController = navController)
        }
        composable(Screen.Chat.route) {
            ChatScreen()
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
    }
}