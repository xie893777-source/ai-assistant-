package com.example.aiassistant.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.aiassistant.util.Preferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var apiKey by remember { mutableStateOf(Preferences.getApiKey(context)) }
    var baseUrl by remember { mutableStateOf(Preferences.getBaseUrl(context)) }
    var model by remember { mutableStateOf(Preferences.getModel(context)) }
    var sysPrompt by remember { mutableStateOf(Preferences.getSystemPrompt(context)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = apiKey, onValueChange = { apiKey = it },
                label = { Text("API Key") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = baseUrl, onValueChange = { baseUrl = it },
                label = { Text("API Base URL") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = model, onValueChange = { model = it },
                label = { Text("模型名称") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = sysPrompt, onValueChange = { sysPrompt = it },
                label = { Text("系统提示词") },
                modifier = Modifier.fillMaxWidth(), minLines = 3, maxLines = 6
            )
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    Preferences.save(context, "api_key", apiKey)
                    Preferences.save(context, "base_url", baseUrl)
                    Preferences.save(context, "model", model)
                    Preferences.save(context, "system_prompt", sysPrompt)
                    Toast.makeText(context, "已保存", Toast.LENGTH_SHORT).show()
                    onBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("保存") }
        }
    }
}
