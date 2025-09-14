package com.anou.pagegather.ui.feature.statistics.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import com.anou.pagegather.ui.components.charts.WeBarChart
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
                color = Color.Red
            )
        } else {
            // 准备图表数据
            val chartData = uiState.habitData.map { (hour, count) ->
                com.anou.pagegather.ui.components.charts.ChartData(
                    value = count.toFloat(),
                    label = hour
                )
            }.toList()
            
            // 显示柱状图
            WeBarChart(
                dataSource = chartData,
                modifier = Modifier.fillMaxWidth().height(200.dp)
            )
        }
    }
}