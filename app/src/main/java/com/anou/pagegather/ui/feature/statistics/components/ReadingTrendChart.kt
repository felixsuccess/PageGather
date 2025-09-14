package com.anou.pagegather.ui.feature.statistics.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.anou.pagegather.ui.feature.statistics.ReadingTrendViewModel
import com.anou.pagegather.ui.feature.statistics.TimeGranularity
import com.anou.pagegather.ui.feature.statistics.TimeRange
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max
import kotlin.math.min

/**
 * 阅读趋势图表组件
 */
@Composable
fun ReadingTrendChart(
    timeRange: TimeRange,
    timeGranularity: TimeGranularity,
    modifier: Modifier = Modifier,
    viewModel: ReadingTrendViewModel = hiltViewModel()
) {
    // 当时间范围或时间粒度改变时，重新加载数据
    LaunchedEffect(timeRange, timeGranularity) {
        viewModel.loadReadingTrendDataByTimeGranularity(timeRange.startDate, timeRange.endDate, timeGranularity)
    }
    
    val uiState by viewModel.uiState.collectAsState()
    
    // 初始化时加载数据
    LaunchedEffect(Unit) {
        viewModel.loadReadingTrendDataByTimeGranularity(timeRange.startDate, timeRange.endDate, timeGranularity)
    }
    
    Box(modifier = modifier) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
        } else if (uiState.error != null) {
            Text(
                text = "加载数据失败: ${uiState.error}",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(androidx.compose.ui.Alignment.Center)
            )
        } else {
            // 显示阅读趋势图表（即使没有数据也显示，此时会显示为0值）
            ReadingTrendChartContent(
                trendData = uiState.trendData,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun ReadingTrendChartContent(
    trendData: Map<String, Long>,
    modifier: Modifier = Modifier
) {
    // 将数据转换为排序后的列表
    val sortedData = remember(trendData) {
        trendData.toList().sortedBy { it.first }
    }
    
    // 提取日期和阅读时长
    val dates = sortedData.map { it.first }
    val durations = sortedData.map { it.second }
    
    // 计算最大阅读时长用于缩放
    val maxDuration = remember(durations) {
        durations.maxOrNull() ?: 1L
    }
    
    // 计算最小阅读时长
    val minDuration = remember(durations) {
        durations.minOrNull() ?: 0L
    }
    
    // 获取主题颜色
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant
    
    Box(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            
            // 计算点的位置
            val points = mutableListOf<Offset>()
            dates.forEachIndexed { index, _ ->
                val x = index * (canvasWidth / kotlin.math.max(1, dates.size - 1).toFloat())
                val y = canvasHeight - (durations[index] - minDuration).toFloat() / kotlin.math.max(1L, (maxDuration - minDuration)).toFloat() * canvasHeight
                points.add(Offset(x, y))
            }
            
            // 绘制折线
            val path = Path()
            if (points.isNotEmpty()) {
                path.moveTo(points[0].x, points[0].y)
                for (i in 1 until points.size) {
                    path.lineTo(points[i].x, points[i].y)
                }
                
                drawPath(
                    path = path,
                    color = primaryColor,
                    style = Stroke(width = 3f)
                )
                
                // 绘制数据点
                points.forEach { point ->
                    drawCircle(
                        color = primaryColor,
                        radius = 6f,
                        center = point
                    )
                }
            }
        }
        
        // 显示坐标轴标签
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 顶部标签（最大值）
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatDuration(maxDuration),
                    style = TextStyle(fontSize = 12.sp),
                    color = onSurfaceVariantColor
                )
            }
            
            // 底部标签（日期）
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (dates.isNotEmpty()) {
                    Text(
                        text = dates.first(),
                        style = TextStyle(fontSize = 12.sp),
                        color = onSurfaceVariantColor
                    )
                    Text(
                        text = dates.last(),
                        style = TextStyle(fontSize = 12.sp),
                        color = onSurfaceVariantColor
                    )
                }
            }
        }
    }
}

/**
 * 格式化持续时间显示
 */
private fun formatDuration(milliseconds: Long): String {
    if (milliseconds <= 0) return "0分钟"

    val seconds = milliseconds / 1000
    val minutes = seconds / 60
    val hours = minutes / 60

    return when {
        hours > 0 -> "${hours}小时"
        minutes > 0 -> "${minutes}分钟"
        else -> "${seconds}秒"
    }
}