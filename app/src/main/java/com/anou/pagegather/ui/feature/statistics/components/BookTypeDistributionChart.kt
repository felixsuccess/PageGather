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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.anou.pagegather.ui.components.charts.PieChart
import com.anou.pagegather.ui.components.charts.PieChartSegment
import com.anou.pagegather.ui.components.charts.generateColors
import com.anou.pagegather.ui.feature.statistics.BookTypeDistributionViewModel
import com.anou.pagegather.ui.feature.statistics.TimeRange

/**
 * 书籍类型分布图表组件
 */
@Composable
fun BookTypeDistributionChart(
    timeRange: TimeRange,
    modifier: Modifier = Modifier,
    viewModel: BookTypeDistributionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // 使用remember保持上一次的有效数据
    val lastValidData = remember { mutableStateOf<List<PieChartSegment>?>(null) }

    // 只保留一个LaunchedEffect，使用具体的日期作为key避免冲突
    LaunchedEffect(timeRange.startDate, timeRange.endDate) {
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
            val validData = uiState.typeData.toList().filter { (_, count) -> count > 0 }
            
            if (validData.isNotEmpty()) {
                val dataColors = generateColors(validData.size)
                val segments = validData.mapIndexed { index, (type, count) ->
                    PieChartSegment(
                        value = count.toFloat(),
                        label = type,
                        color = dataColors[index]
                    )
                }
                
                // 更新缓存的有效数据
                lastValidData.value = segments
                
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
                // 如果当前没有有效数据，但有缓存数据，显示缓存数据
                lastValidData.value?.let { cachedSegments ->
                    PieChart(
                        segments = cachedSegments,
                        title = "",
                        showPercentages = true,
                        showLegend = true,
                        isDonut = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(bottom = 16.dp)
                    )
                } ?: Text(
                    text = "暂无有效的书籍类型分布数据",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            // 如果没有数据，但有缓存数据，显示缓存数据
            lastValidData.value?.let { cachedSegments ->
                PieChart(
                    segments = cachedSegments,
                    title = "",
                    showPercentages = true,
                    showLegend = true,
                    isDonut = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(bottom = 16.dp)
                )
            } ?: Text(
                text = "暂无书籍类型分布数据",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}