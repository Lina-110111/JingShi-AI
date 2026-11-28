// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    // Compose 编译器插件：AGP 9 使用内置 Kotlin，版本需与内置 Kotlin 2.2.10 保持一致
    alias(libs.plugins.compose.compiler) apply false
}