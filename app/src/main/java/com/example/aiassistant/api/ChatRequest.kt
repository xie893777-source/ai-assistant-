package com.example.aiassistant.api

data class ChatRequest(
    val model: String = "gpt-3.5-turbo",
    val messages: List<MessageBody>,
    val stream: Boolean = false,
    val temperature: Double = 0.7,
    val max_tokens: Int = 2048
)

data class MessageBody(val role: String, val content: String)

data class ChatResponse(val choices: List<Choice>)
data class Choice(val message: MessageBody, val finish_reason: String?)

data class ChatStreamChunk(val choices: List<StreamChoice>)
data class StreamChoice(val delta: MessageBody, val finish_reason: String?)
