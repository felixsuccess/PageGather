package com.anou.pagegather.ui.feature.statistics.components

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
import com.anou.pagegather.ui.feature.statistics.ReadingHabitDistributionViewModel
import com.anou.pagegather.ui.feature.statistics.TimeRange

/**
 * 阅读习惯时间分布图表组件
 * 显示一天中各个小时的阅读频率（阅读次数）
 */
@Composable
fun ReadingHabitDistributionChart(
    timeRange: TimeRange,
    modifier: Modifier = Modifier,
    viewModel: ReadingHabitDistributionViewModel = hiltViewModel()
) {
    // 当时间范围改变时，重新加载数据
    LaunchedEffect(timeRange) {
        viewModel.loadReadingHabitDataByDateRange(
            timeRange.startDate,
            timeRange.endDate
        )
    }

    // 初始化时加载数据
    LaunchedEffect(Unit) {
        viewModel.loadReadingHabitDataByDateRange(
            timeRange.startDate,
            timeRange.endDate
        )
    }

    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else if (uiState.error != null) {
            Text(
                text = "加载数据失败: ${uiState.error}",
                color = MaterialTheme.colorScheme.error
            )
        } else if (uiState.habitData.isEmpty()) {
            Text(
                text = "暂无阅读习惯分布数据",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            // 准备图表数据
            val chartDataPoints = uiState.habitData.mapIndexed { index, item ->
                ChartDataPoint(
                    x = index.toFloat(),
                    y = item.value,
                    label = item.label,
                    value = "${item.value.toInt()}次"
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