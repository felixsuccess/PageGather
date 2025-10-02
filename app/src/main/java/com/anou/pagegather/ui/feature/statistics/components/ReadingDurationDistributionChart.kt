package com.anou.pagegather.ui.feature.statistics.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.anou.pagegather.ui.components.charts.BarChart
import com.anou.pagegather.ui.components.charts.ChartDataPoint
import com.anou.pagegather.ui.components.charts.formatDurationToHours
import com.anou.pagegather.ui.feature.statistics.ReadingDurationDistributionViewModel
import com.anou.pagegather.ui.feature.statistics.TimeGranularity
import com.anou.pagegather.ui.feature.statistics.TimeRange

/**
 * 阅读时长分布图表组件
 */
@Composable
fun ReadingDurationDistributionChart(
    timeRange: TimeRange,
    timeGranularity: TimeGranularity,
    modifier: Modifier = Modifier,
    viewModel: ReadingDurationDistributionViewModel = hiltViewModel()
) {
    // 当时间范围或时间粒度改变时，重新加载数据
    LaunchedEffect(timeRange, timeGranularity) {
        viewModel.loadReadingDurationDataByDateRangeAndGranularity(
            timeRange.startDate,
            timeRange.endDate,
            timeGranularity
        )
    }

    val uiState by viewModel.uiState.collectAsState()

    // 初始化时加载数据
    LaunchedEffect(Unit) {
        viewModel.loadReadingDurationDataByDateRangeAndGranularity(
            timeRange.startDate,
            timeRange.endDate,
            timeGranularity
        )
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else if (uiState.error != null) {
            Text(
                text = "加载数据失败: ${uiState.error}",
                color = MaterialTheme.colorScheme.error
            )
        } else if (uiState.durationData.isEmpty()) {
            Text(
                text = "暂无阅读时长分布数据",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            val chartDataPoints = uiState.durationData.mapIndexed { index, item ->
                ChartDataPoint(
                    x = index.toFloat(),
                    y = item.value,
                    label = item.label,
                    value = formatDurationToHours(item.value.toLong())
                )
            }

            BarChart(
                data = chartDataPoints,
                title = "",
                showValues = true,
                showGrid = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * 格式化持续时间显示
 * @param milliseconds 毫秒数
 * @return 格式化后的时间字符串
 */
private fun formatDuration(milliseconds: Long): String {
    if (milliseconds <= 0) return "0"

    val seconds = milliseconds / 1000
    val minutes = seconds / 60
    val hours = minutes / 60

    return when {
        hours > 0 -> "${hours}小时${minutes % 60}分钟"
        minutes > 0 -> "${minutes}分钟${seconds % 60}秒"
        else -> "${seconds}秒"
    }
}