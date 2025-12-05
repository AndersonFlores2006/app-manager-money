package com.example.gestor_money

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.gestor_money.presentation.components.AuthStateNavigator
import com.example.gestor_money.presentation.components.BottomNavigationBar
import com.example.gestor_money.presentation.theme.MoneyManagerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoneyManagerTheme {
                val navController = rememberNavController()
                
                AuthStateNavigator(
                    navController = navController,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}