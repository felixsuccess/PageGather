package com.anou.pagegather.ui.components.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp

/**
 * 高级折线图组件，支持多条线和渐变填充
 */
@Composable
fun AdvancedLineChart(
    dataSeries: List<ChartDataSeries>,
    modifier: Modifier = Modifier,
    title: String = "",
    showPoints: Boolean = true,
    showGrid: Boolean = true,
    showArea: Boolean = false,
    yAxisOnRight: Boolean = false, // Y轴标签是否在右侧
    labelSpacing: Float = 24f, // 标签与图表的间距
    animationEnabled: Boolean = true
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
        
        if (dataSeries.isEmpty() || dataSeries.all { it.data.isEmpty() }) {
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
            drawAdvancedLineChart(
                dataSeries = dataSeries,
                showPoints = showPoints,
                showGrid = showGrid,
                showArea = showArea,
                yAxisOnRight = yAxisOnRight,
                labelSpacing = labelSpacing,
                textMeasurer = textMeasurer,
                textStyle = TextStyle(
                    color = textColor,
                    fontSize = with(density) { 12.dp.toSp() }
                )
            )
        }
        
        // 图例
        if (dataSeries.size > 1) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                dataSeries.forEach { series ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Canvas(modifier = Modifier.size(12.dp)) {
                            drawCircle(color = series.color)
                        }
                        Text(
                            text = series.name,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawAdvancedLineChart(
    dataSeries: List<ChartDataSeries>,
    showPoints: Boolean,
    showGrid: Boolean,
    showArea: Boolean,
    yAxisOnRight: Boolean,
    labelSpacing: Float,
    textMeasurer: TextMeasurer,
    textStyle: TextStyle
) {
    // 动态计算padding，与其他图表组件保持一致
    val basePadding = 20f
    val yAxisLabelSpace = 50f + labelSpacing
    
    val leftPadding = if (yAxisOnRight) basePadding else yAxisLabelSpace
    val rightPadding = if (yAxisOnRight) yAxisLabelSpace else basePadding
    val topPadding = basePadding
    val bottomPadding = 30f + labelSpacing // X轴标签空间
    
    val chartWidth = size.width - leftPadding - rightPadding
    val chartHeight = size.height - topPadding - bottomPadding
    
    if (dataSeries.isEmpty()) return
    
    // 计算所有数据的范围
    val allData = dataSeries.flatMap { it.data }
    if (allData.isEmpty()) return
    
    val maxValue = allData.maxOfOrNull { it.y } ?: 0f
    val minValue = allData.minOfOrNull { it.y } ?: 0f
    val valueRange = maxValue - minValue
    
    if (valueRange == 0f) return
    
    val maxDataPoints = dataSeries.maxOfOrNull { it.data.size } ?: 0
    if (maxDataPoints <= 1) return
    
    // 绘制网格线
    if (showGrid) {
        val gridColor = Color.Gray.copy(alpha = 0.3f)
        for (i in 0..5) {
            val y = topPadding + chartHeight - (chartHeight / 5 * i)
            drawLine(
                color = gridColor,
                start = Offset(leftPadding, y),
                end = Offset(leftPadding + chartWidth, y),
                strokeWidth = 1f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f))
            )
        }
        
        for (i in 0 until maxDataPoints) {
            val x = leftPadding + (i.toFloat() / (maxDataPoints - 1)) * chartWidth
            drawLine(
                color = gridColor,
                start = Offset(x, topPadding),
                end = Offset(x, topPadding + chartHeight),
                strokeWidth = 1f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f))
            )
        }
    }
    
    // 绘制每条数据线
    dataSeries.forEach { series ->
        if (series.data.size > 1) {
            // 计算点的坐标
            val points = series.data.mapIndexed { index, point ->
                val x = leftPadding + (index.toFloat() / (series.data.size - 1)) * chartWidth
                val y = topPadding + chartHeight - ((point.y - minValue) / valueRange) * chartHeight
                Offset(x, y)
            }
            
            // 绘制面积（如果启用）
            if (showArea) {
                val areaPath = Path().apply {
                    // 从底部开始
                    moveTo(points.first().x, topPadding + chartHeight)
                    // 先移动到第一个数据点
                    lineTo(points.first().x, points.first().y)
                    
                    // 使用与线条相同的贝塞尔曲线创建平滑的面积边界
                    for (i in 1 until points.size) {
                        val currentPoint = points[i]
                        val previousPoint = points[i - 1]
                        
                        val controlPoint1X = previousPoint.x + (currentPoint.x - previousPoint.x) * 0.5f
                        val controlPoint1Y = previousPoint.y
                        val controlPoint2X = currentPoint.x - (currentPoint.x - previousPoint.x) * 0.5f
                        val controlPoint2Y = currentPoint.y
                        
                        cubicTo(
                            controlPoint1X, controlPoint1Y,
                            controlPoint2X, controlPoint2Y,
                            currentPoint.x, currentPoint.y
                        )
                    }
                    
                    // 回到底部完成面积
                    lineTo(points.last().x, topPadding + chartHeight)
                    close()
                }
                
                // 创建渐变
                val gradient = Brush.verticalGradient(
                    colors = listOf(
                        series.color.copy(alpha = 0.3f),
                        series.color.copy(alpha = 0.1f)
                    ),
                    startY = topPadding,
                    endY = topPadding + chartHeight
                )
                
                drawPath(
                    path = areaPath,
                    brush = gradient
                )
            }
            
            // 绘制折线
            val linePath = Path().apply {
                moveTo(points.first().x, points.first().y)
                
                // 使用贝塞尔曲线创建平滑的线条
                for (i in 1 until points.size) {
                    val currentPoint = points[i]
                    val previousPoint = points[i - 1]
                    
                    val controlPoint1X = previousPoint.x + (currentPoint.x - previousPoint.x) * 0.5f
                    val controlPoint1Y = previousPoint.y
                    val controlPoint2X = currentPoint.x - (currentPoint.x - previousPoint.x) * 0.5f
                    val controlPoint2Y = currentPoint.y
                    
                    cubicTo(
                        controlPoint1X, controlPoint1Y,
                        controlPoint2X, controlPoint2Y,
                        currentPoint.x, currentPoint.y
                    )
                }
            }
            
            drawPath(
                path = linePath,
                color = series.color,
                style = Stroke(
                    width = 3f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
            
            // 绘制数据点
            if (showPoints) {
                points.forEachIndexed { index, point ->
                    // 外圈
                    drawCircle(
                        color = series.color,
                        radius = 6f,
                        center = point
                    )
                    // 内圈
                    drawCircle(
                        color = Color.White,
                        radius = 3f,
                        center = point
                    )
                    
                    // 绘制数值标签（只在有限的点上显示，避免重叠）
                    if (index % (maxDataPoints / 5).coerceAtLeast(1) == 0) {
                        val dataPoint = series.data[index]
                        if (dataPoint.value.isNotEmpty()) {
                            val textResult = textMeasurer.measure(dataPoint.value, textStyle)
                            drawText(
                                textLayoutResult = textResult,
                                topLeft = Offset(
                                    point.x - textResult.size.width / 2,
                                    point.y - textResult.size.height - 12f
                                )
                            )
                        }
                    }
                }
            }
        }
    }
    
    // 绘制X轴标签（使用第一个系列的标签）
    if (dataSeries.isNotEmpty()) {
        val firstSeries = dataSeries.first()
        firstSeries.data.forEachIndexed { index, point ->
            if (point.label.isNotEmpty() && index % (maxDataPoints / 5).coerceAtLeast(1) == 0) {
                val x = leftPadding + (index.toFloat() / (firstSeries.data.size - 1)) * chartWidth
                val labelResult = textMeasurer.measure(point.label, textStyle)
                drawText(
                    textLayoutResult = labelResult,
                    topLeft = Offset(
                        x - labelResult.size.width / 2,
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
            leftPadding + chartWidth + labelSpacing
        } else {
            leftPadding - textResult.size.width - labelSpacing
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
 * 格式化数值显示
 */
private fun formatValue(value: Float): String {
    return when {
        value >= 1000000 -> String.format("%.1fM", value / 1000000)
        value >= 1000 -> String.format("%.1fK", value / 1000)
        value % 1 == 0f -> value.toInt().toString()
        else -> String.format("%.1f", value)
    }
}