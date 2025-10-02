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
import com.anou.pagegather.ui.components.charts.PieChart
import com.anou.pagegather.ui.components.charts.PieChartSegment
import com.anou.pagegather.ui.components.charts.generateColors
import com.anou.pagegather.ui.feature.statistics.BookGroupDistributionViewModel
import com.anou.pagegather.ui.feature.statistics.TimeRange

/**
 * 书籍分组分布图表组件
 */
@Composable
fun BookGroupDistributionChart(
    timeRange: TimeRange,
    modifier: Modifier = Modifier,
    viewModel: BookGroupDistributionViewModel = hiltViewModel()
) {
    // 当时间范围改变时，重新加载数据
    LaunchedEffect(timeRange) {
        viewModel.loadBookGroupDataByDateRange(timeRange.startDate, timeRange.endDate)
    }

    val uiState by viewModel.uiState.collectAsState()


    // 初始化时加载数据
    LaunchedEffect(Unit) {
        viewModel.loadBookGroupDataByDateRange(timeRange.startDate, timeRange.endDate)
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
        } else if (uiState.groupData.isNotEmpty()) {
            // 将数据转换为图表所需格式
            val validData = uiState.groupData.toList().filter { (_, count) -> count > 0 }
            
            if (validData.isNotEmpty()) {
                val dataColors = generateColors(validData.size)
                val segments = validData.mapIndexed { index, (group, count) ->
                    PieChartSegment(
                        value = count.toFloat(),
                        label = group,
                        color = dataColors[index]
                    )
                }

                PieChart(
                    segments = segments,
                    title = "",
                    showPercentages = true,
                    showLegend = true,
                    isDonut = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(bottom = 16.dp)
                )


            } else {
                Text(
                    text = "暂无有效的书籍分组分布数据",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Text(
                text = "暂无书籍分组分布数据",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}