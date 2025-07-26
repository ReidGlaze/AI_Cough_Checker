package com.twintipsolutions.cough.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import com.twintipsolutions.cough.ui.theme.AnimatedBackgroundColors

@Composable
fun AnimatedGradientBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "gradient")
    
    // Simple subtle animation like iOS
    val animateOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 20000, // 20 seconds for very smooth animation
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradientAnimation"
    )
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .drawWithContent {
                // Create a smooth moving gradient
                val gradientHeight = size.height * 2
                val offset = (animateOffset - 0.5f) * gradientHeight
                
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = AnimatedBackgroundColors + AnimatedBackgroundColors, // Double the colors for smooth loop
                        startY = offset,
                        endY = offset + gradientHeight
                    )
                )
                drawContent()
            }
    ) {
        content()
    }
}