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
            // 即使没有数据也要显示图表（显示为0值）
            // 将数据转换为图表所需格式，并格式化显示值
            val chartData = uiState.durationData.map { (timePoint, duration) ->
                ChartData(
                    value = duration.toFloat(), // 保持原始毫秒值用于图表计算
                    label = timePoint
                )
            }.filter { it.value >= 0f } // 过滤掉负值或无效值
            
            // 显示柱状图，使用自定义格式化函数
            WeBarChart(
                dataSource = chartData,
               // modifier = Modifier.fillMaxWidth().height(300.dp),
                formatter = { formatDuration(it.toLong()) } // 使用自定义格式化函数
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
 