package com.example.gestor_money.presentation.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Transactions : Screen("transactions")
    object AddTransaction : Screen("add_transaction")
    object Categories : Screen("categories")
    object Stats : Screen("stats")
    object Chat : Screen("chat")
    object Settings : Screen("settings")
}
