package com.example.aiassistant.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.aiassistant.api.*
import com.example.aiassistant.model.ChatMessage
import com.example.aiassistant.util.Preferences
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _thinkLevel = MutableStateFlow(Preferences.getThinkLevel(getApplication()))
    val thinkLevel: StateFlow<Int> = _thinkLevel

    private fun levelToTemp(level: Int): Double = when (level) {
        1 -> 0.2
        2 -> 0.4
        3 -> 0.7
        4 -> 1.0
        5 -> 1.3
        else -> 0.7
    }

    fun setThinkLevel(level: Int) {
        _thinkLevel.value = level
        Preferences.saveInt(getApplication(), "think_level", level)
    }

    fun sendMessage(userInput: String) {
        if (userInput.isBlank() || _isLoading.value) return
        val context = getApplication<Application>()
        val apiKey = Preferences.getApiKey(context)
        if (apiKey.isBlank()) {
            _error.value = "请先在设置中填写 API Key"
            return
        }

        _messages.value = _messages.value + ChatMessage(role = "user", content = userInput)
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val history = buildHistory(context)
                val request = ChatRequest(
                    model = Preferences.getModel(context),
                    messages = history,
                    stream = true,
                    temperature = levelToTemp(_thinkLevel.value)
                )
                ApiClient.setBaseUrl(Preferences.getBaseUrl(context))
                streamChat("Bearer $apiKey", request)
            } catch (e: Exception) {
                _error.value = "请求失败: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    private fun buildHistory(context: Application): List<MessageBody> {
        val list = mutableListOf<MessageBody>()
        val sys = Preferences.getSystemPrompt(context)
        if (sys.isNotBlank()) list.add(MessageBody("system", sys))
        _messages.value.takeLast(20).forEach {
            if (it.role != "system") list.add(MessageBody(it.role, it.content))
        }
        return list
    }

    private suspend fun streamChat(auth: String, request: ChatRequest) {
        val response = withContext(Dispatchers.IO) {
            ApiClient.apiService.chatStream(auth, request)
        }
        val body = response.body()
        if (body == null || !response.isSuccessful) {
            _error.value = "API 错误: ${response.code()}"
            _isLoading.value = false
            return
        }

        val id = java.util.UUID.randomUUID().toString()
        var msg = ChatMessage(id = id, role = "assistant", content = "", isStreaming = true)
        _messages.value = _messages.value + msg

        val gson = Gson()
        withContext(Dispatchers.IO) {
            body.byteStream().bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    if (line.startsWith("data: ")) {
                        val data = line.removePrefix("data: ").trim()
                        if (data == "[DONE]") return@forEach
                        try {
                            val chunk = gson.fromJson(data, ChatStreamChunk::class.java)
                            val delta = chunk.choices.firstOrNull()?.delta?.content ?: ""
                            if (delta.isNotEmpty()) {
                                msg = msg.copy(content = msg.content + delta)
                                _messages.value = _messages.value.map { if (it.id == id) msg else it }
                            }
                        } catch (_: Exception) {}
                    }
                }
            }
        }
        _messages.value = _messages.value.map { if (it.id == id) msg.copy(isStreaming = false) else it }
        _isLoading.value = false
    }

    fun clearMessages() { _messages.value = emptyList() }
    fun clearError() { _error.value = null }
}
