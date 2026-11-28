package cn.edu.sicnu.cs.hln.jingshiai.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// 统一使用浅色配色，保证首页、识别、游戏、我的四个页面视觉一致
private val JingShiColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = TextOnPrimary,
    primaryContainer = GreenLight,
    onPrimaryContainer = GreenPrimaryDark,
    secondary = GreenPrimaryDark,
    onSecondary = TextOnPrimary,
    secondaryContainer = GreenLight,
    onSecondaryContainer = GreenPrimaryDark,
    background = PageBackground,
    onBackground = TextPrimary,
    surface = CardWhite,
    onSurface = TextPrimary,
    surfaceVariant = GreenLight,
    onSurfaceVariant = TextHint,
    outline = BorderColor
)

@Composable
fun JingShiTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = JingShiColorScheme,
        typography = JingShiTypography,
        content = content
    )
}
