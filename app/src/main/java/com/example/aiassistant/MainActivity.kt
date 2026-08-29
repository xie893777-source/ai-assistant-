package com.example.aiassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aiassistant.ui.screens.ChatScreen
import com.example.aiassistant.ui.screens.SettingsScreen
import com.example.aiassistant.ui.theme.AiAssistantTheme
import com.example.aiassistant.viewmodel.ChatViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AiAssistantTheme { AppNavigation() }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val vm: ChatViewModel = viewModel()
    NavHost(navController = navController, startDestination = "chat") {
        composable("chat") {
            ChatScreen(vm) { navController.navigate("settings") }
        }
        composable("settings") {
            SettingsScreen { navController.popBackStack() }
        }
    }
}
