package com.example.gestor_money.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerEffect(
    modifier: Modifier = Modifier,
    width: Float? = null,
    height: Float? = null
) {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f),
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = translateAnim)
    )

    Box(
        modifier = modifier
            .width(width?.dp ?: 200.dp)
            .height(height?.dp ?: 20.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(brush)
    )
}

@Composable
fun ShimmerTransactionItem() {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            ShimmerEffect(modifier = Modifier.weight(1f), height = 16f)
            Spacer(modifier = Modifier.width(16.dp))
            ShimmerEffect(width = 80f, height = 16f)
        }
        Spacer(modifier = Modifier.height(8.dp))
        ShimmerEffect(width = 100f, height = 12f)
        Spacer(modifier = Modifier.height(8.dp))
        ShimmerEffect(width = 60f, height = 12f)
    }
}

@Composable
fun ShimmerBalanceCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color.LightGray.copy(alpha = 0.3f),
                        Color.LightGray.copy(alpha = 0.1f)
                    )
                )
            )
            .padding(24.dp)
    ) {
        Column(verticalArrangement = Arrangement.Center) {
            ShimmerEffect(width = 150f, height = 40f)
            Spacer(modifier = Modifier.height(8.dp))
            ShimmerEffect(width = 120f, height = 16f)
        }
    }
}

@Composable
fun ShimmerIncomeExpenseCard() {
    Box(
        modifier = Modifier
            .height(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.LightGray.copy(alpha = 0.2f))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.SpaceBetween) {
            Row {
                ShimmerEffect(width = 20f, height = 20f)
                Spacer(modifier = Modifier.width(8.dp))
                ShimmerEffect(width = 60f, height = 16f)
            }
            ShimmerEffect(width = 80f, height = 24f)
        }
    }
}