package com.twintipsolutions.cough.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun PulseIcon(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    strokeWidth: Float = 3f
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val centerY = height / 2
        
        val path = Path().apply {
            // Start from left
            moveTo(0f, centerY)
            
            // First flat line
            lineTo(width * 0.2f, centerY)
            
            // Small dip before main pulse
            cubicTo(
                width * 0.25f, centerY + height * 0.1f,
                width * 0.3f, centerY + height * 0.1f,
                width * 0.35f, centerY
            )
            
            // Main pulse up
            lineTo(width * 0.4f, centerY - height * 0.4f)
            
            // Main pulse down
            lineTo(width * 0.5f, centerY + height * 0.3f)
            
            // Back to center
            lineTo(width * 0.6f, centerY - height * 0.15f)
            
            // Small bump
            cubicTo(
                width * 0.65f, centerY,
                width * 0.7f, centerY,
                width * 0.75f, centerY
            )
            
            // End flat line
            lineTo(width, centerY)
        }
        
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = strokeWidth)
        )
    }
}