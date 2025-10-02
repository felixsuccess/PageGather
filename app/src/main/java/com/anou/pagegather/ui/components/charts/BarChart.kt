package com.anou.pagegather.ui.components.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import kotlin.math.max

/**
 * 原生柱状图组件
 */
@Composable
fun BarChart(
    data: List<ChartDataPoint>,
    modifier: Modifier = Modifier,
    title: String = "",
    barColor: Color = ChartDefaults.primaryColor(),
    showValues: Boolean = true,
    showGrid: Boolean = true,
    smartLabels: Boolean = true, // 是否启用智能标签显示
    roundedCorners: Boolean = true, // 是否启用圆角柱子（默认启用）
    cornerRadius: Float = 12f, // 圆角半径（默认12f）
    yAxisOnRight: Boolean = false, // Y轴标签是否在右侧（默认左侧）
    labelSpacing: Float = 24f // 标签与图表的间距（默认24f，增加间距）
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    
    Column(modifier = modifier) {
        if (title.isNotEmpty()) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        if (data.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "暂无数据",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            return@Column
        }
        
        val textColor = ChartDefaults.onSurfaceColor()
        
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            drawBarChart(
                data = data,
                barColor = barColor,
                showValues = showValues,
                showGrid = showGrid,
                smartLabels = smartLabels,
                roundedCorners = roundedCorners,
                cornerRadius = cornerRadius,
                yAxisOnRight = yAxisOnRight,
                labelSpacing = labelSpacing,
                textMeasurer = textMeasurer,
                textStyle = TextStyle(
                    color = textColor,
                    fontSize = with(density) { 12.dp.toSp() }
                )
            )
        }
    }
}

private fun DrawScope.drawBarChart(
    data: List<ChartDataPoint>,
    barColor: Color,
    showValues: Boolean,
    showGrid: Boolean,
    smartLabels: Boolean,
    roundedCorners: Boolean,
    cornerRadius: Float,
    yAxisOnRight: Boolean,
    labelSpacing: Float,
    textMeasurer: TextMeasurer,
    textStyle: TextStyle
) {
    // 使用动态padding，增加标签间距
    val basePadding = 20f // 基础边距
    val yAxisLabelSpace = 50f + labelSpacing // Y轴标签所需空间
    
    val leftPadding = if (yAxisOnRight) basePadding else yAxisLabelSpace
    val rightPadding = if (yAxisOnRight) yAxisLabelSpace else basePadding
    val topPadding = 20f
    val bottomPadding = 50f + labelSpacing // 增加基础底部间距
    
    val chartWidth = size.width - leftPadding - rightPadding
    val chartHeight = size.height - topPadding - bottomPadding
    
    if (data.isEmpty()) return
    
    val maxValue = data.maxOfOrNull { it.y } ?: 0f
    val minValue = data.minOfOrNull { it.y } ?: 0f
    val valueRange = maxValue - minValue
    
    if (valueRange == 0f) return
    
    val barWidth = chartWidth / data.size * 0.7f
    val barSpacing = chartWidth / data.size * 0.3f
    
    // 绘制网格线
    if (showGrid) {
        val gridColor = Color.Gray.copy(alpha = 0.3f)
        for (i in 0..5) {
            val y = topPadding + chartHeight - (chartHeight / 5 * i)
            drawLine(
                color = gridColor,
                start = Offset(leftPadding, y),
                end = Offset(leftPadding + chartWidth, y),
                strokeWidth = 1f
            )
        }
    }
    
    // 绘制柱状图
    data.forEachIndexed { index, point ->
        val barHeight = if (valueRange > 0) {
            ((point.y - minValue) / valueRange) * chartHeight
        } else {
            0f
        }
        
        val x = leftPadding + index * (barWidth + barSpacing) + barSpacing / 2
        val y = topPadding + chartHeight - barHeight
        
        // 绘制柱子（支持圆角）
        if (roundedCorners && barHeight > 0) {
            // 使用用户指定的圆角半径，但不超过柱子尺寸的限制
            val actualCornerRadius = minOf(cornerRadius, barWidth / 2, barHeight / 2)
            drawRoundRect(
                color = barColor,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(actualCornerRadius, actualCornerRadius)
            )
        } else {
            // 使用普通矩形
            drawRect(
                color = barColor,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight)
            )
        }
        
        // 绘制数值标签
        if (showValues && point.y > 0) {
            val valueText = if (point.value.isNotEmpty()) point.value else point.y.toInt().toString()
            val textResult = textMeasurer.measure(valueText, textStyle)
            drawText(
                textLayoutResult = textResult,
                topLeft = Offset(
                    x + barWidth / 2 - textResult.size.width / 2,
                    y - textResult.size.height - 4f
                )
            )
        }
        
        // 绘制X轴标签（支持智能显示）
        if (point.label.isNotEmpty()) {
            val shouldShowLabel = if (smartLabels) {
                shouldShowXAxisLabel(index, data.size, chartWidth)
            } else {
                true // 如果不启用智能标签，显示所有标签
            }
            
            if (shouldShowLabel) {
                val labelResult = textMeasurer.measure(point.label, textStyle)
                drawText(
                    textLayoutResult = labelResult,
                    topLeft = Offset(
                        x + barWidth / 2 - labelResult.size.width / 2,
                        topPadding + chartHeight + labelSpacing
                    )
                )
            }
        }
    }
    
    // 绘制Y轴标签
    for (i in 0..5) {
        val value = minValue + (valueRange / 5 * i)
        val y = topPadding + chartHeight - (chartHeight / 5 * i)
        val valueText = formatValue(value)
        val textResult = textMeasurer.measure(valueText, textStyle)
        
        val labelX = if (yAxisOnRight) {
            // Y轴标签在右侧
            leftPadding + chartWidth + labelSpacing
        } else {
            // Y轴标签在左侧，右对齐到图表左边缘
            leftPadding - labelSpacing - textResult.size.width
        }
        
        drawText(
            textLayoutResult = textResult,
            topLeft = Offset(
                labelX,
                y - textResult.size.height / 2
            )
        )
    }
}

/**
 * 智能判断是否应该显示X轴标签，避免重叠
 */
private fun shouldShowXAxisLabel(index: Int, totalCount: Int, chartWidth: Float): Boolean {
    // 根据数据量和图表宽度动态调整显示策略
    val estimatedLabelWidth = 60f // 估算每个标签的平均宽度
    val availableWidth = chartWidth
    val maxLabelsCanFit = (availableWidth / estimatedLabelWidth).toInt()
    
    return when {
        // 如果数据点少于等于可容纳的标签数，全部显示
        totalCount <= maxLabelsCanFit -> true
        
        // 如果数据点很多，采用间隔显示策略
        totalCount > 20 -> {
            // 对于超过20个数据点，只显示关键位置的标签
            when {
                index == 0 -> true // 第一个
                index == totalCount - 1 -> true // 最后一个
                totalCount > 30 && index % 5 == 0 -> true // 每5个显示一个
                totalCount in 21..30 && index % 3 == 0 -> true // 每3个显示一个
                else -> false
            }
        }
        
        // 中等数量的数据点，采用均匀间隔
        else -> {
            val step = totalCount / maxLabelsCanFit.coerceAtLeast(1)
            index % step.coerceAtLeast(1) == 0 || index == totalCount - 1
        }
    }
}