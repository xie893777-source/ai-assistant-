package com.example.aiassistant.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp

@Composable
fun RainbowSlider(
    level: Int,
    onLevelChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val rainbowColors = listOf(
        Color(0xFFFF004D), Color(0xFFFF7F00), Color(0xFFFFE600),
        Color(0xFF00E676), Color(0xFF00B0FF), Color(0xFF651FFF), Color(0xFFD500F9)
    )
    val levelDescriptions = listOf("1 · 严谨", "2 · 平衡", "3 · 自然", "4 · 活跃", "5 · 创意 ✨")

    val isMaxLevel = level == 5
    val animatedProgress by animateFloatAsState(
        targetValue = (level - 1) / 4f,
        label = "progress"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isMaxLevel) Brush.linearGradient(rainbowColors)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "思考强度",
                style = MaterialTheme.typography.labelLarge,
                color = if (isMaxLevel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = levelDescriptions[level - 1],
                style = MaterialTheme.typography.labelLarge,
                color = if (isMaxLevel) Color.White else MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.height(8.dp))

        var sliderWidth by remember { mutableIntStateOf(0) }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .onSizeChanged { sliderWidth = it.width }
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        if (sliderWidth > 0) {
                            val ratio = (offset.x / sliderWidth).coerceIn(0f, 1f)
                            onLevelChange((ratio * 4).toInt() + 1)
                        }
                    }
                }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { change, _ ->
                        if (sliderWidth > 0) {
                            val ratio = (change.position.x / sliderWidth).coerceIn(0f, 1f)
                            onLevelChange((ratio * 4).toInt() + 1)
                        }
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize().align(Alignment.Center)) {
                val trackHeight = 8.dp.toPx()
                val y = (size.height - trackHeight) / 2
                drawRoundRect(
                    color = if (isMaxLevel) Color.White.copy(alpha = 0.3f)
                    else Color.Gray.copy(alpha = 0.3f),
                    topLeft = Offset(0f, y),
                    size = Size(size.width, trackHeight),
                    cornerRadius = CornerRadius(trackHeight / 2)
                )
                val activeWidth = size.width * animatedProgress
                if (activeWidth > 0) {
                    drawRoundRect(
                        brush = if (isMaxLevel) Brush.linearGradient(rainbowColors)
                        else Brush.linearGradient(listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )),
                        topLeft = Offset(0f, y),
                        size = Size(activeWidth, trackHeight),
                        cornerRadius = CornerRadius(trackHeight / 2)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArran
