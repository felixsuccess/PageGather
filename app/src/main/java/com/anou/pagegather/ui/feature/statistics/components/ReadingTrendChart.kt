package com.anou.pagegather.ui.feature.statistics.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.anou.pagegather.ui.feature.statistics.ReadingTrendViewModel
import com.anou.pagegather.ui.feature.statistics.TimeGranularity
import com.anou.pagegather.ui.feature.statistics.TimeRange
import com.touzalab.composecharts.components.BarChart
import com.touzalab.composecharts.components.LineChart
import com.touzalab.composecharts.data.DataPoint
import com.touzalab.composecharts.data.DataSeries
import com.touzalab.composecharts.theme.ColorPalettes


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
        viewModel.loadReadingTrendDataByTimeGranularity(
            timeRange.startDate,
            timeRange.endDate,
            timeGranularity
        )
    }

    val uiState by viewModel.uiState.collectAsState()

    // 初始化时加载数据
    LaunchedEffect(Unit) {
        viewModel.loadReadingTrendDataByTimeGranularity(
            timeRange.startDate,
            timeRange.endDate,
            timeGranularity
        )
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
  
            val chartDataPoints = mutableListOf<DataPoint>()

            uiState.trendData.forEachIndexed { index, item ->
                chartDataPoints.add(
                    DataPoint(x = item.groupId.toFloat(), y = item.value, label = item.label)
                )
            }


            val salesData = listOf(
                DataSeries(
                    name = "阅读趋势",
                    color = ColorPalettes.Default[0],
                    points = chartDataPoints
                )
            )

            BarChart(
                dataSeries = salesData,
                title = "阅读趋势新",
                xAxisTitle = "时间段",
                yAxisTitle = "时长",
                stacked = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
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