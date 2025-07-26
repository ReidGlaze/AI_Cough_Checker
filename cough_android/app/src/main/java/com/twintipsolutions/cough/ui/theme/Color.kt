package com.twintipsolutions.cough.ui.theme

import androidx.compose.ui.graphics.Color

// Exact colors from iOS app
object CoughColors {
    // White with opacity variations
    val White = Color.White
    val WhiteOpacity90 = Color(0xE6FFFFFF)
    val WhiteOpacity80 = Color(0xCCFFFFFF)
    val WhiteOpacity70 = Color(0xB3FFFFFF)
    val WhiteOpacity60 = Color(0x99FFFFFF)
    val WhiteOpacity50 = Color(0x80FFFFFF)
    val WhiteOpacity40 = Color(0x66FFFFFF)
    val WhiteOpacity30 = Color(0x4DFFFFFF)
    val WhiteOpacity20 = Color(0x33FFFFFF)
    val WhiteOpacity10 = Color(0x1AFFFFFF)
    val WhiteOpacity05 = Color(0x0DFFFFFF)
    
    // Black with opacity
    val BlackOpacity30 = Color(0x4D000000)
    
    // Blue variations
    val Blue = Color(0xFF007AFF) // iOS system blue
    val BlueOpacity80 = Color(0xCC007AFF)
    val BlueOpacity50 = Color(0x80007AFF)
    val BlueOpacity30 = Color(0x4D007AFF)
    
    // Cyan variations
    val Cyan = Color(0xFF00BCD4)
    val CyanOpacity80 = Color(0xCC00BCD4)
    val CyanOpacity60 = Color(0x9900BCD4)
    
    // Purple variations
    val Purple = Color(0xFF9C27B0)
    val PurpleOpacity50 = Color(0x809C27B0)
    val PurpleOpacity30 = Color(0x4D9C27B0)
    val PurpleOpacity20 = Color(0x339C27B0)
    
    // Green variations
    val Green = Color(0xFF4CAF50)
    val Mint = Color(0xFF00C9A7)
    
    // Red/Orange variations
    val Red = Color(0xFFF44336)
    val RedOpacity80 = Color(0xCCF44336)
    val RedOpacity30 = Color(0x4DF44336)
    val Orange = Color(0xFFFF9800)
    
    // Custom RGB colors from iOS
    val CustomBlue1 = Color(0xFF1A3373) // RGB(0.1, 0.2, 0.45)
    val CustomBlue2 = Color(0xFF331A66) // RGB(0.2, 0.1, 0.4)
    val CustomBlue3 = Color(0xFF264080) // RGB(0.15, 0.25, 0.5)
    val CustomBlue4 = Color(0xFF0D2659) // RGB(0.05, 0.15, 0.35)
    val CustomDark1 = Color(0xFF0D1A33) // RGB(0.05, 0.1, 0.2)
    val CustomDark2 = Color(0xFF1A2640) // RGB(0.1, 0.15, 0.25)
    val CustomDark3 = Color(0xFF0D334D) // RGB(0.05, 0.2, 0.3)
    val CustomDark4 = Color(0xFF051A26) // RGB(0.02, 0.1, 0.15)
}

// Gradient color lists
val GlassmorphicBorderColors = listOf(
    CoughColors.WhiteOpacity60,
    CoughColors.WhiteOpacity20
)

val BlueGradientColors = listOf(
    CoughColors.Blue,
    CoughColors.Purple
)

val CyanGradientColors = listOf(
    CoughColors.Cyan,
    CoughColors.Blue
)

val GreenGradientColors = listOf(
    CoughColors.Green,
    CoughColors.Mint
)

val RedGradientColors = listOf(
    CoughColors.Red,
    CoughColors.Orange
)

val WhiteTextGradientColors = listOf(
    CoughColors.White,
    CoughColors.WhiteOpacity80
)

val AnimatedBackgroundColors = listOf(
    Color(0xFF1A237E), // Deep blue-purple
    Color(0xFF4A148C), // Deep purple
    Color(0xFF311B92), // Medium purple-blue
    Color(0xFF1A237E)  // Deep blue-purple
)