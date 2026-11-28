package cn.edu.sicnu.cs.hln.jingshiai.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

// 字号层级按现有识别页的实际用法整理，迁移识别页时可直接沿用
val JingShiTypography = Typography(
    // 识别结果物品名称（如“塑料瓶”）
    headlineSmall = TextStyle(
        fontSize = 23.sp,
        lineHeight = 32.sp,
        fontWeight = FontWeight.Bold
    ),
    // 页面标题、识别结果标题
    titleLarge = TextStyle(
        fontSize = 18.sp,
        lineHeight = 26.sp,
        fontWeight = FontWeight.Bold
    ),
    // 区块标题，如“上传识别图片”
    titleMedium = TextStyle(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.Bold
    ),
    // 常规正文
    bodyLarge = TextStyle(
        fontSize = 14.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight.Normal
    ),
    // 页面说明、投放建议
    bodyMedium = TextStyle(
        fontSize = 13.sp,
        lineHeight = 21.sp,
        fontWeight = FontWeight.Normal
    ),
    // 字段名等小号正文
    bodySmall = TextStyle(
        fontSize = 12.sp,
        lineHeight = 18.sp,
        fontWeight = FontWeight.Normal
    ),
    // 按钮文字（Material 3 Button 默认使用 labelLarge）
    labelLarge = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.SemiBold
    ),
    // 底部标语等最小号文字
    labelSmall = TextStyle(
        fontSize = 11.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Normal
    )
)

// 识别页中带字距的英文小标签（如 SMART RECOGNITION、IMAGE UPLOAD）
val EnglishLabelStyle = TextStyle(
    fontSize = 9.sp,
    lineHeight = 14.sp,
    letterSpacing = 0.08.em,
    fontWeight = FontWeight.Normal
)
