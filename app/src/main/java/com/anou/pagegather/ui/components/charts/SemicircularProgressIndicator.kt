package com.anou.pagegather.ui.components.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * 半圆形进度指示器
 */
@Composable
fun SemicircularProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    trackColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
    strokeWidth: Dp = 16.dp
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.size(200.dp)
        ) {
            drawSemicircularProgress(
                progress = progress.coerceIn(0f, 1f),
                color = color,
                trackColor = trackColor,
                strokeWidth = strokeWidth.toPx()
            )
        }
    }
}

private fun DrawScope.drawSemicircularProgress(
    progress: Float,
    color: Color,
    trackColor: Color,
    strokeWidth: Float
) {
    val center = Offset(size.width / 2, size.height / 2)
    val radius = (size.width - strokeWidth) / 2
    
    // 绘制背景轨道（半圆）
    drawArc(
        color = trackColor,
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(center.x - radius, center.y - radius),
        size = Size(radius * 2, radius * 2),
        style = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round
        )
    )
    
    // 绘制进度弧（半圆）
    if (progress > 0f) {
        drawArc(
            color = color,
            startAngle = 180f,
            sweepAngle = 180f * progress,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round
            )
        )
    }
    
    // 绘制起始点和结束点的圆点
    val startAngle = PI.toFloat()
    val endAngle = PI.toFloat() + PI.toFloat() * progress
    
    // 起始点
    val startX = center.x + cos(startAngle) * radius
    val startY = center.y + sin(startAngle) * radius
    drawCircle(
        color = trackColor,
        radius = strokeWidth / 2,
        center = Offset(startX, startY)
    )
    
    // 当前进度点
    if (progress > 0f) {
        val currentX = center.x + cos(endAngle) * radius
        val currentY = center.y + sin(endAngle) * radius
        drawCircle(
            color = color,
            radius = strokeWidth / 2,
            center = Offset(currentX, currentY)
        )
    }
}