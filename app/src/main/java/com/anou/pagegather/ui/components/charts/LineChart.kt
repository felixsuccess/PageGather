package com.anou.pagegather.ui.components.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp

/**
 * 原生折线图组件
 */
@Composable
fun LineChart(
    data: List<ChartDataPoint>,
    modifier: Modifier = Modifier,
    title: String = "",
    lineColor: Color = ChartDefaults.primaryColor(),
    showPoints: Boolean = true,
    showGrid: Boolean = true,
    showArea: Boolean = false,
    smoothCurve: Boolean = false, // 是否使用平滑曲线
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
            drawLineChart(
                data = data,
                lineColor = lineColor,
                showPoints = showPoints,
                showGrid = showGrid,
                showArea = showArea,
                smoothCurve = smoothCurve,
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

private fun DrawScope.drawLineChart(
    data: List<ChartDataPoint>,
    lineColor: Color,
    showPoints: Boolean,
    showGrid: Boolean,
    showArea: Boolean,
    smoothCurve: Boolean,
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
    }
    
    // 计算点的坐标
    val points = data.mapIndexed { index, point ->
        val x = leftPadding + (index.toFloat() / (data.size - 1).coerceAtLeast(1)) * chartWidth
        val y = topPadding + chartHeight - ((point.y - minValue) / valueRange) * chartHeight
        Offset(x, y)
    }
    
    // 绘制面积（如果启用）
    if (showArea && points.size > 1) {
        val areaPath = Path().apply {
            // 从底部开始
            moveTo(points.first().x, topPadding + chartHeight)
            // 先移动到第一个数据点
            lineTo(points.first().x, points.first().y)
            
            if (smoothCurve) {
                // 使用贝塞尔曲线创建平滑的面积边界
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
            } else {
                // 使用直线连接
                points.drop(1).forEach { point ->
                    lineTo(point.x, point.y)
                }
            }
            
            // 回到底部完成面积
            lineTo(points.last().x, topPadding + chartHeight)
            close()
        }
        
        drawPath(
            path = areaPath,
            color = lineColor.copy(alpha = 0.3f)
        )
    }
    
    // 绘制折线
    if (points.size > 1) {
        val linePath = Path().apply {
            moveTo(points.first().x, points.first().y)
            
            if (smoothCurve) {
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
            } else {
                // 使用直线连接
                points.drop(1).forEach { point ->
                    lineTo(point.x, point.y)
                }
            }
        }
        
        drawPath(
            path = linePath,
            color = lineColor,
            style = Stroke(
                width = 3f,
                cap = StrokeCap.Round
            )
        )
    }
    
    // 绘制数据点
    if (showPoints) {
        points.forEachIndexed { index, point ->
            drawCircle(
                color = lineColor,
                radius = 4f,
                center = point
            )
            drawCircle(
                color = Color.White,
                radius = 2f,
                center = point
            )
            
            // 绘制数值标签
            val dataPoint = data[index]
            if (dataPoint.value.isNotEmpty()) {
                val textResult = textMeasurer.measure(dataPoint.value, textStyle)
                
                // 计算标签位置，避免与Y轴标签重合
                val labelX = point.x - textResult.size.width / 2
                val labelY = point.y - textResult.size.height - 12f // 增加与数据点的间距
                
                // 检查是否与Y轴标签区域重合
                val yAxisLabelAreaWidth = if (yAxisOnRight) 0f else leftPadding
                val adjustedLabelX = if (labelX < yAxisLabelAreaWidth) {
                    // 如果重合，将标签移到数据点右侧
                    point.x + 8f
                } else {
                    labelX
                }
                
                drawText(
                    textLayoutResult = textResult,
                    topLeft = Offset(adjustedLabelX, labelY)
                )
            }
        }
    }
    
    // 绘制X轴标签
    data.forEachIndexed { index, point ->
        if (point.label.isNotEmpty()) {
            val x = leftPadding + (index.toFloat() / (data.size - 1).coerceAtLeast(1)) * chartWidth
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