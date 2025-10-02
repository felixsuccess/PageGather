package com.anou.pagegather.ui.components.charts

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 图表默认配置和样式
 */
object ChartDefaults {
    
    val DefaultStrokeWidth = 2.dp
    val DefaultCornerRadius = 8.dp
    val DefaultPadding = 16.dp
    val DefaultSpacing = 8.dp
    
    @Composable
    fun primaryColor(): Color = MaterialTheme.colorScheme.primary
    
    @Composable
    fun surfaceColor(): Color = MaterialTheme.colorScheme.surface
    
    @Composable
    fun onSurfaceColor(): Color = MaterialTheme.colorScheme.onSurface
    
    @Composable
    fun outlineColor(): Color = MaterialTheme.colorScheme.outline
    
    /**
     * 生成图表颜色调色板
     */
    @Composable
    fun chartColors(): List<Color> = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.secondaryContainer,
        MaterialTheme.colorScheme.tertiaryContainer,
        Color(0xFF6366F1), // Indigo
        Color(0xFF8B5CF6), // Violet
        Color(0xFFEC4899), // Pink
        Color(0xFFF59E0B), // Amber
        Color(0xFF10B981), // Emerald
        Color(0xFF06B6D4), // Cyan
    )
}

/**
 * 图表数据点
 */
data class ChartDataPoint(
    val x: Float,
    val y: Float,
    val label: String = "",
    val value: String = ""
)

/**
 * 图表数据系列
 */
data class ChartDataSeries(
    val name: String,
    val data: List<ChartDataPoint>,
    val color: Color
)

/**
 * 饼图数据段
 */
data class PieChartSegment(
    val value: Float,
    val label: String,
    val color: Color
)