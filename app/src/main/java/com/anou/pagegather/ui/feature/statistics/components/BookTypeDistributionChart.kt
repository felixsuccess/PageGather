package com.anou.pagegather.ui.feature.statistics.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.anou.pagegather.data.model.BookReadingStatisticsItemData
import com.anou.pagegather.ui.components.charts.generateColors
import com.anou.pagegather.ui.feature.statistics.BookTypeDistributionViewModel
import com.anou.pagegather.ui.feature.statistics.TimeRange
import com.touzalab.composecharts.components.PieChart
import com.touzalab.composecharts.data.PieChartSegment

/**
 * 书籍类型分布图表组件
 */
@Composable
fun BookTypeDistributionChart(
    timeRange: TimeRange,
    modifier: Modifier = Modifier,
    viewModel: BookTypeDistributionViewModel = hiltViewModel()
) {
    // 当时间范围改变时，重新加载数据
    LaunchedEffect(timeRange) {
        viewModel.loadBookTypeDataByDateRange(timeRange.startDate, timeRange.endDate)
    }

    val uiState by viewModel.uiState.collectAsState()

    // 初始化时加载数据
    LaunchedEffect(Unit) {
        viewModel.loadBookTypeDataByDateRange(timeRange.startDate, timeRange.endDate)
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
        } else if (uiState.typeData.isNotEmpty()) {
            // 将数据转换为图表所需格式
            val chartData = uiState.typeData.map { (type, count) ->
                BookReadingStatisticsItemData(
                    value = count.toFloat(),
                    label = type,
                    groupId = 0
                )
            }.filter { it.value >= 0f } // 过滤掉负值或无效值

            // 确保数据不为空且总值大于0后再显示图表
            val totalValue = chartData.sumOf { it.value.toDouble() }.toFloat()
            if (chartData.isNotEmpty() && totalValue > 0f) {
                val segments = mutableListOf<PieChartSegment>()

                val dataCount = chartData.size
                val dataColors = generateColors(dataCount)
                chartData.forEachIndexed { index, item ->
                    segments.add(   PieChartSegment(
                        value = item.value,
                        label = item.label,
                        color = dataColors[index]
                    )
                    )
                }
                PieChart(
                    segments = segments,
                    donut = true,
                    showPercentages = true,
                    title = "书籍类型分布",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(bottom = 16.dp)
                )

            } else {
                Text(
                    text = "暂无有效的书籍类型分布数据",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Text(
                text = "暂无书籍类型分布数据",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}