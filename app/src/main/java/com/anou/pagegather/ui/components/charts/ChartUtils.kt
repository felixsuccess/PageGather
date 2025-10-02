package com.anou.pagegather.ui.components.charts

import androidx.compose.ui.graphics.Color

/**
 * 图表工具函数
 */

/**
 * 生成图表颜色调色板
 */
fun generateColors(count: Int): List<Color> {
    val baseColors = listOf(
        Color(0xFF6366F1), // Indigo
        Color(0xFF8B5CF6), // Violet  
        Color(0xFFEC4899), // Pink
        Color(0xFFF59E0B), // Amber
        Color(0xFF10B981), // Emerald
        Color(0xFF06B6D4), // Cyan
        Color(0xFFEF4444), // Red
        Color(0xFF3B82F6), // Blue
        Color(0xFF84CC16), // Lime
        Color(0xFFF97316), // Orange
        Color(0xFF8B5A2B), // Brown
        Color(0xFF6B7280), // Gray
    )
    
    return if (count <= baseColors.size) {
        baseColors.take(count)
    } else {
        // 如果需要更多颜色，通过调整透明度和色调生成
        val colors = mutableListOf<Color>()
        colors.addAll(baseColors)
        
        var colorIndex = 0
        while (colors.size < count) {
            val baseColor = baseColors[colorIndex % baseColors.size]
            val alpha = 0.7f - (colors.size - baseColors.size) * 0.1f
            colors.add(baseColor.copy(alpha = alpha.coerceAtLeast(0.3f)))
            colorIndex++
        }
        
        colors.take(count)
    }
}

/**
 * 格式化数值显示
 */
fun formatValue(value: Float, unit: String = ""): String {
    return when {
        value >= 1000000 -> "${String.format("%.1f", value / 1000000)}M$unit"
        value >= 1000 -> "${String.format("%.1f", value / 1000)}K$unit"
        value % 1 == 0f -> "${value.toInt()}$unit"
        else -> "${String.format("%.1f", value)}$unit"
    }
}

/**
 * 格式化时长显示（毫秒转换为可读格式）
 */
fun formatDuration(milliseconds: Long): String {
    if (milliseconds <= 0) return "0分钟"

    val seconds = milliseconds / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        days > 0 -> "${days}天${hours % 24}小时"
        hours > 0 -> "${hours}小时${minutes % 60}分钟"
        minutes > 0 -> "${minutes}分钟"
        else -> "${seconds}秒"
    }
}

/**
 * 格式化时长显示（毫秒转换为小时）
 */
fun formatDurationToHours(milliseconds: Long): String {
    val hours = milliseconds / (1000 * 60 * 60).toFloat()
    return when {
        hours >= 1 -> "${String.format("%.1f", hours)}小时"
        else -> {
            val minutes = milliseconds / (1000 * 60)
            "${minutes}分钟"
        }
    }
}