package com.twintipsolutions.cough.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.twintipsolutions.cough.ui.theme.*

@Composable
fun GlassmorphicCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(25.dp))
            .background(
                // Matches iOS .ultraThinMaterial effect
                Color.White.copy(alpha = 0.1f)
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = GlassmorphicBorderColors,
                    start = androidx.compose.ui.geometry.Offset(0f, 0f),
                    end = androidx.compose.ui.geometry.Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                ),
                shape = RoundedCornerShape(25.dp)
            )
    ) {
        // Add blur backdrop effect
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(radius = 10.dp)
                .background(Color.Black.copy(alpha = 0.05f))
        )
        
        Card(
            modifier = Modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            shape = RoundedCornerShape(25.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                content = content
            )
        }
    }
}