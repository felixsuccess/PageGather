package com.anou.pagegather.ui.feature.statistics.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.anou.pagegather.ui.components.charts.ChartData
import com.anou.pagegather.ui.components.charts.WeBarChart
import com.anou.pagegather.ui.feature.statistics.FinishedBooksViewModel
import com.anou.pagegather.ui.feature.statistics.TimeRange

/**
 * 读完书籍图表组件
 */
@Composable
fun FinishedBooksChart(
    timeRange: TimeRange,
    modifier: Modifier = Modifier,
    viewModel: FinishedBooksViewModel = hiltViewModel()
) {
    // 当时间范围改变时，重新加载数据
    LaunchedEffect(timeRange) {
        viewModel.loadFinishedBooksDataByDateRange(
            timeRange.startDate, 
            timeRange.endDate
        )
    }
    
    val uiState by viewModel.uiState.collectAsState()
    
    // 初始化时加载数据
    LaunchedEffect(Unit) {
        viewModel.loadFinishedBooksDataByDateRange(
            timeRange.startDate, 
            timeRange.endDate
        )
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
        } else {
            // 创建一个简单的柱状图数据，显示读完的书籍数量
            val chartData = listOf(
                ChartData(
                    value = uiState.finishedBooksCount.toFloat(),
                    label = "读完书籍"
                )
            )
            
            // 显示柱状图
            WeBarChart(
                dataSource = chartData,
                modifier = Modifier.fillMaxWidth().height(200.dp)
            )
        }
    }
}