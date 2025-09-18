package com.anou.pagegather.ui.feature.statistics.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.anou.pagegather.ui.feature.statistics.ReadingHabitDistributionViewModel
import com.anou.pagegather.ui.feature.statistics.TimeRange
import com.touzalab.composecharts.components.BarChart
import com.touzalab.composecharts.data.DataPoint
import com.touzalab.composecharts.data.DataSeries
import com.touzalab.composecharts.theme.ColorPalettes

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
            val chartDataPoints = mutableListOf<DataPoint>()

            uiState.habitData.forEachIndexed { index, item ->
                chartDataPoints.add(
                    DataPoint(
                        x = item.groupId.toFloat(),
                        y = item.value,
                        label = item.label
                    )
                )
            }


            val salesData = listOf(
                DataSeries(
                    name = "阅读习惯分布",
                    color = ColorPalettes.Default[0],
                    points = chartDataPoints
                )
            )

            BarChart(
                dataSeries = salesData,
                title = "阅读习惯时间分布",
                xAxisTitle = "小时",
                yAxisTitle = "阅读次数",
                stacked = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
            )
        }
    }
}