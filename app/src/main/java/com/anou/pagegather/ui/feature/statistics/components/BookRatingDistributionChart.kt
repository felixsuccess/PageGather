package com.anou.pagegather.ui.feature.statistics.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.anou.pagegather.ui.components.charts.ChartData
import com.anou.pagegather.ui.components.charts.WePieChart
import com.anou.pagegather.ui.feature.statistics.BookRatingDistributionViewModel
import com.anou.pagegather.ui.feature.statistics.TimeRange

/**
 * 书籍评分分布图表组件
 */
@Composable
fun BookRatingDistributionChart(
    timeRange: TimeRange,
    modifier: Modifier = Modifier,
    viewModel: BookRatingDistributionViewModel = hiltViewModel()
) {
    // 当时间范围改变时，重新加载数据
    LaunchedEffect(timeRange) {
        viewModel.loadBookRatingDataByDateRange(timeRange.startDate, timeRange.endDate)
    }
    
    val uiState by viewModel.uiState.collectAsState()
    
    // 初始化时加载数据
    LaunchedEffect(Unit) {
        viewModel.loadBookRatingDataByDateRange(timeRange.startDate, timeRange.endDate)
    }
    
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
        } else if (uiState.ratingData.isNotEmpty()) {
            // 将数据转换为图表所需格式
            val chartData = uiState.ratingData.map { (rating, count) ->
                ChartData(
                    value = count.toFloat(),
                    label = rating
                )
            }.filter { it.value >= 0f } // 过滤掉负值或无效值
            
            // 确保数据不为空且总值大于0后再显示图表
            val totalValue = chartData.sumOf { it.value.toDouble() }.toFloat()
            if (chartData.isNotEmpty() && totalValue > 0f) {
                // WePieChart没有modifier参数，所以我们使用Box来应用尺寸限制
                Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                    WePieChart(dataSource = chartData)
                }
            } else {
                Text(
                    text = "暂无有效的书籍评分分布数据",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Text(
                text = "暂无书籍评分分布数据",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}