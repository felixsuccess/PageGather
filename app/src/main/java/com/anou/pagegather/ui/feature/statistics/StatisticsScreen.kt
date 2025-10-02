package com.anou.pagegather.ui.feature.statistics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.anou.pagegather.data.local.entity.ReadingRecordEntity
import com.anou.pagegather.data.local.entity.RecordType
import com.anou.pagegather.ui.components.charts.AdvancedLineChart
import com.anou.pagegather.ui.components.charts.BarChart
import com.anou.pagegather.ui.components.charts.ChartDataPoint
import com.anou.pagegather.ui.components.charts.ChartDataSeries
import com.anou.pagegather.ui.components.charts.LineChart
import com.anou.pagegather.ui.components.charts.PieChart
import com.anou.pagegather.ui.components.charts.PieChartSegment
import com.anou.pagegather.ui.components.charts.SemicircularProgressIndicator
import com.anou.pagegather.ui.feature.statistics.components.BookGroupDistributionChart
import com.anou.pagegather.ui.feature.statistics.components.BookRatingDistributionChart
import com.anou.pagegather.ui.feature.statistics.components.BookSourceDistributionChart
import com.anou.pagegather.ui.feature.statistics.components.BookStatusDistributionChart
import com.anou.pagegather.ui.feature.statistics.components.BookTagDistributionChart
import com.anou.pagegather.ui.feature.statistics.components.BookTypeDistributionChart
import com.anou.pagegather.ui.feature.statistics.components.FinishedBooksChart
import com.anou.pagegather.ui.feature.statistics.components.ReadingDurationDistributionChart
import com.anou.pagegather.ui.feature.statistics.components.ReadingHabitDistributionChart
import com.anou.pagegather.ui.feature.statistics.components.ReadingOverviewCard
import com.anou.pagegather.ui.feature.statistics.components.ReadingTrendChart
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.min

// 定义统计页面的Tab枚举
enum class StatisticsTab(val title: String) {
    OVERVIEW("概览"), TIMELINE("时间线"), STATISTICS("统计")
}

// 时间粒度枚举
enum class TimeGranularity(val title: String) {
    DAY("日"), WEEK("周"), MONTH("月"), YEAR("年")
}

// 时间范围数据类
data class TimeRange(
    val startDate: String, val endDate: String, val label: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    navController: NavController, viewModel: StatisticsViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableStateOf(StatisticsTab.OVERVIEW) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "不积跬步无以至千里",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        // Tab导航栏
        TabRow(
            selectedTabIndex = selectedTab.ordinal,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        ) {
            StatisticsTab.entries.forEach { tab ->
                Tab(selected = selectedTab == tab, onClick = { selectedTab = tab }, text = {
                    Text(
                        text = tab.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (selectedTab == tab) FontWeight.Medium else FontWeight.Normal
                    )
                })
            }
        }

        // Tab内容区域
        when (selectedTab) {
            StatisticsTab.OVERVIEW -> {
                StatisticsOverviewTab(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    navController = navController,
                )
            }

            StatisticsTab.TIMELINE -> {
                StatisticsTimelineTab(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }

            StatisticsTab.STATISTICS -> {
                StatisticsTabContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp), navController = navController
                )
            }
        }
    }
}

@Composable
private fun StatisticsOverviewTab(modifier: Modifier = Modifier, navController: NavController) {




    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 阅读目标卡片
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "今日阅读目标",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "0:00 / 30:00",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 半圆形进度条
                SemicircularProgressIndicator(
                    progress = 0f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                    strokeWidth = 16.dp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    modifier = Modifier
                        .fillMaxWidth() ,
                    textAlign = TextAlign.Center,
                    text = "继续保持，今天还有时间完成目标！",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    modifier = Modifier
                        .fillMaxWidth() ,
                    textAlign = TextAlign.Center,
                    text = "找一本好书，设定一个目标，杨恒每天阅读的好习惯。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 阅读概览卡片
        ReadingOverviewCard()
    }

}

@Composable
private fun StatisticsTimelineTab(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "时间线功能",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "即将推出...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "图表示例已转移到开发者选项 → 图表展示",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun StatisticsTabContent(modifier: Modifier = Modifier, navController: NavController) {
    var selectedTimeGranularity by remember { mutableStateOf(TimeGranularity.MONTH) }
    var selectedTimeRange by remember { mutableStateOf(getCurrentMonthRange()) }
    
    // 在父组件中集中管理所有ViewModels，避免子组件中的ViewModel回收问题
    val bookTypeViewModel: BookTypeDistributionViewModel = hiltViewModel()
    val bookTypeUiState by bookTypeViewModel.uiState.collectAsState()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            TimeRangeSelector(
                selectedTimeGranularity = selectedTimeGranularity,
                onTimeGranularityChanged = { it: TimeGranularity ->
                    selectedTimeGranularity = it
                    // 根据粒度更新默认时间范围
                    selectedTimeRange = when (it) {
                        TimeGranularity.DAY -> getTodayRange()
                        TimeGranularity.WEEK -> getCurrentWeekRange()
                        TimeGranularity.MONTH -> getCurrentMonthRange()
                        TimeGranularity.YEAR -> getCurrentYearRange()
                    }
                },
                selectedTimeRange = selectedTimeRange,
                onTimeRangeChanged = { it: TimeRange -> selectedTimeRange = it }
            )
            
            // 将LaunchedEffect移到这里，确保在TimeRangeSelector之后执行
            LaunchedEffect(selectedTimeRange.startDate, selectedTimeRange.endDate) {
                bookTypeViewModel.loadBookTypeDataByDateRange(selectedTimeRange.startDate, selectedTimeRange.endDate)
            }
        }
        
        // 年度总览卡片（使用ViewModel获取数据）
        item {




            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth().background(   MaterialTheme.colorScheme.primaryContainer)
                        .padding(16.dp)
                ) {

                    AnnualOverviewCard(timeRange = selectedTimeRange)
                }
            }


        }
        // 阅读时长分布图表
        item {
            Card(
                modifier = Modifier.fillMaxWidth() ,
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth().background(   MaterialTheme.colorScheme.primaryContainer)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "阅读时长分布",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 使用阅读时长分布图表组件，并传递时间范围和时间粒度参数
                    ReadingDurationDistributionChart(
                        timeRange = selectedTimeRange,
                        timeGranularity = selectedTimeGranularity,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            }
        }

        item {
            // 阅读趋势图表
            Card(
                modifier = Modifier
                    .fillMaxWidth() ,
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)

            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth().background(   MaterialTheme.colorScheme.primaryContainer)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "阅读趋势",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 使用专门的阅读趋势图表组件，并传递时间范围和时间粒度参数
                    ReadingTrendChart(
                        timeRange = selectedTimeRange,
                        timeGranularity = selectedTimeGranularity,
                        modifier = Modifier
                            .fillMaxWidth().background(   MaterialTheme.colorScheme.primaryContainer)
                            .height(200.dp)
                    )
                }
            }
        }
        item {
            // 阅读习惯时间分布图表
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth().background(   MaterialTheme.colorScheme.primaryContainer)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "阅读习惯时间分布",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 使用专门的阅读习惯时间分布图表组件，并传递时间范围参数
                    ReadingHabitDistributionChart(
                        timeRange = selectedTimeRange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            }
        }



        item {
            // 书籍类型分布图表
            Card(
                modifier = Modifier
                    .fillMaxWidth() ,
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth().background(   MaterialTheme.colorScheme.primaryContainer)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "书籍类型分布",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 使用固定的Box容器，避免条件渲染导致的滚动消失
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        if (bookTypeUiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        } else if (bookTypeUiState.error != null) {
                            Text(
                                text = "加载数据失败: ${bookTypeUiState.error}",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        } else if (bookTypeUiState.typeData.isNotEmpty()) {
                            // 有真实数据时显示图表
                            val validData = bookTypeUiState.typeData.toList().filter { (_, count) -> count > 0 }
                            
                            if (validData.isNotEmpty()) {
                                val dataColors = listOf(
                                    Color(0xFF10B981), Color(0xFF06B6D4), Color(0xFFEF4444),
                                    Color(0xFF3B82F6), Color(0xFF8B5CF6), Color(0xFFEC4899),
                                    Color(0xFFF59E0B), Color(0xFF8B5A2B), Color(0xFF6366F1),
                                    Color(0xFF84CC16)
                                )
                                val segments = validData.mapIndexed { index, (type, count) ->
                                    PieChartSegment(
                                        value = count.toFloat(),
                                        label = type,
                                        color = dataColors[index % dataColors.size]
                                    )
                                }
                                
                                // 显示真实数据的图表
                                PieChart(
                                    segments = segments,
                                    title = "",
                                    showPercentages = true,
                                    showLegend = true,
                                    isDonut = true,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(bottom = 16.dp)
                                )
                            } else {
                                Text(
                                    text = "暂无有效的书籍类型分布数据",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        } else {
                            Text(
                                text = "暂无书籍类型分布数据",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }


                }
            }
        }


        item {
            // 书籍来源分布图表
            Card(
                modifier = Modifier
                    .fillMaxWidth() ,
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth().background(   MaterialTheme.colorScheme.primaryContainer)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "书籍来源分布",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                     BookSourceDistributionChart(
                        timeRange = selectedTimeRange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            }
        }

        item {
            // 读完书籍图表
            Card(
                modifier = Modifier
                    .fillMaxWidth() ,
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth().background(   MaterialTheme.colorScheme.primaryContainer)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "读完书籍",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 使用专门的读完书籍图表组件，并传递时间范围参数
                    FinishedBooksChart(
                        timeRange = selectedTimeRange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            }
        }


        item {
            // 书籍状态分布图表
            Card(
                modifier = Modifier
                    .fillMaxWidth() ,
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth().background(   MaterialTheme.colorScheme.primaryContainer)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "书籍状态分布",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 使用专门的书籍状态分布图表组件，并传递时间范围参数
                    BookStatusDistributionChart(
                        timeRange = selectedTimeRange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            }
        }


        item {
            // 书籍标签分布图表
            Card(
                modifier = Modifier
                    .fillMaxWidth() ,
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth().background(   MaterialTheme.colorScheme.primaryContainer)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "书籍标签分布",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 使用专门的书籍标签分布图表组件，并传递时间范围参数
                    BookTagDistributionChart(
                        timeRange = selectedTimeRange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            }
        }


        item {
            // 书籍评分分布图表
            Card(
                modifier = Modifier
                    .fillMaxWidth() ,
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth().background(   MaterialTheme.colorScheme.primaryContainer)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "书籍评分分布",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 使用专门的书籍评分分布图表组件，并传递时间范围参数
                    BookRatingDistributionChart(
                        timeRange = selectedTimeRange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            }
        }


        item {
            // 书籍分组分布图表
            Card(
                modifier = Modifier
                    .fillMaxWidth() ,
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth().background(   MaterialTheme.colorScheme.primaryContainer)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "书籍分组分布",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 使用专门的书籍分组分布图表组件，并传递时间范围参数
                    BookGroupDistributionChart(
                        timeRange = selectedTimeRange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            }
        }


        item {
            // 阅读最久的书籍占位符
            Card(
                modifier = Modifier
                    .fillMaxWidth() ,
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth().background(   MaterialTheme.colorScheme.primaryContainer)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "阅读最久的书籍",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "阅读最久的书籍列表（柱状图）",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        item {
            // 偏好阅读类型图表
            Card(
                modifier = Modifier
                    .fillMaxWidth() ,
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth().background(   MaterialTheme.colorScheme.primaryContainer)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "偏好阅读类型",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "偏好阅读类型文史等分析（饼图）",
                        style = MaterialTheme.typography.bodyMedium
                    )


                }
            }
        }


    }
}

/**
 * 统计数据行组件
 */
@Composable
private fun StatisticRow(
    label: String, value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * 格式化持续时间显示
 */
private fun formatDuration(milliseconds: Long): String {
    if (milliseconds <= 0) return "0分钟"

    val seconds = milliseconds / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        days > 0 -> "${days}天${hours % 24}小时"
        hours > 0 -> "${hours}小时${minutes % 60}分钟"
        minutes > 0 -> "${minutes}分钟"
        else -> "${seconds}秒"
    }
}


/**
 * 阅读记录项组件
 */
@Composable
private fun ReadingRecordItem(
    record: ReadingRecordEntity,
    onDeleteClick: () -> Unit,
    onRecordClick: (Long) -> Unit  // 添加点击事件参数
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onRecordClick(record.id) },  // 添加点击事件
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 顶部行：时间信息和删除按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatDateTime(record.startTime),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete, contentDescription = "删除记录"
                    )
                }
            }

            // 记录类型
            val recordType =
                if (record.recordType == RecordType.PRECISE.ordinal) "计时记录" else "手动记录"
            Text(
                text = recordType,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 阅读时长
            Text(
                text = "阅读时长: ${formatDuration(record.duration)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )

            // 进度信息
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "开始进度: ${String.format("%.1f", record.startProgress)}%",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "结束进度: ${String.format("%.1f", record.endProgress)}%",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // 备注信息
            record.notes?.let { notes ->
                if (notes.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "备注: $notes",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * 格式化时间显示
 */
private fun formatDateTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}


// 获取当前是第几周
private fun getWeekOfYear(): Int {
    val calendar = Calendar.getInstance()
    return calendar.get(Calendar.WEEK_OF_YEAR)
}

/**
 * 获取今天的日期范围
 */
private fun getTodayRange(): TimeRange {
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val today = dateFormat.format(calendar.time)
    return TimeRange(today, today, "今天")
}

/**
 * 获取本周的日期范围
 */
private fun getCurrentWeekRange(): TimeRange {
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // 设置为本周第一天（周一）
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    val startDate = dateFormat.format(calendar.time)

    // 正确计算本周最后一天（周日）
    calendar.add(Calendar.DAY_OF_WEEK, 6) // 从周一加6天到周日
    val endDate = dateFormat.format(calendar.time)

    return TimeRange(startDate, endDate, "本周")
}

/**
 * 获取本月的日期范围
 */
private fun getCurrentMonthRange(): TimeRange {
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // 设置为本月第一天
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    val startDate = dateFormat.format(calendar.time)

    // 设置为本月最后一天
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
    val endDate = dateFormat.format(calendar.time)

    return TimeRange(startDate, endDate, "本月")
}

/**
 * 获取本年的日期范围
 */
private fun getCurrentYearRange(): TimeRange {
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // 设置为本年第一天
    calendar.set(Calendar.DAY_OF_YEAR, 1)
    val startDate = dateFormat.format(calendar.time)

    // 设置为本年最后一天
    calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR))
    val endDate = dateFormat.format(calendar.time)

    return TimeRange(startDate, endDate, "本年")
}

// 获取指定日期的范围
private fun getDayRange(year: Int, month: Int, day: Int): TimeRange {
    val calendar = Calendar.getInstance()
    calendar.set(year, month - 1, day) // 月份从0开始
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dateStr = dateFormat.format(calendar.time)
    return TimeRange(dateStr, dateStr, "${year}-${month}-${day}")
}

// 获取指定周的范围
private fun getWeekRange(year: Int, week: Int): TimeRange {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.YEAR, year)
    calendar.set(Calendar.WEEK_OF_YEAR, week)
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val startDate = dateFormat.format(calendar.time)

    // 正确计算周日的日期
    calendar.add(Calendar.DAY_OF_WEEK, 6) // 从周一加6天到周日
    val endDate = dateFormat.format(calendar.time)

    return TimeRange(startDate, endDate, "${year}年第${week}周")
}

// 获取指定月份的范围
private fun getMonthRange(year: Int, month: Int): TimeRange {
    val calendar = Calendar.getInstance()
    calendar.set(year, month - 1, 1) // 月份从0开始
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // 设置为本月第一天
    val startDate = dateFormat.format(calendar.time)

    // 设置为本月最后一天
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
    val endDate = dateFormat.format(calendar.time)

    return TimeRange(startDate, endDate, "${year}年${month}月")
}

// 获取指定年份的范围
private fun getYearRange(year: Int): TimeRange {
    val calendar = Calendar.getInstance()
    calendar.set(year, 0, 1)
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // 设置为本年第一天
    val startDate = dateFormat.format(calendar.time)

    // 设置为本年最后一天
    calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR))
    val endDate = dateFormat.format(calendar.time)

    return TimeRange(startDate, endDate, "${year}年")
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeRangeSelector(
    selectedTimeGranularity: TimeGranularity,
    onTimeGranularityChanged: (TimeGranularity) -> Unit,
    selectedTimeRange: TimeRange,
    onTimeRangeChanged: (TimeRange) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }

    // 从当前选中的时间范围解析出年月日信息用于显示
    val (displayYear, displayMonth, displayDay, displayWeek) = remember(selectedTimeRange) {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        try {
            val startDate = dateFormat.parse(selectedTimeRange.startDate)
            calendar.time = startDate
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH) + 1 // 月份从0开始，所以需要+1
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val week = calendar.get(Calendar.WEEK_OF_YEAR)
            listOf(year, month, day, week)
        } catch (e: Exception) {
            // 解析日期出错时的默认值
            val now = Calendar.getInstance()
            listOf(
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH) + 1,
                now.get(Calendar.DAY_OF_MONTH),
                now.get(Calendar.WEEK_OF_YEAR)
            )
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.background(   MaterialTheme.colorScheme.primaryContainer).padding(16.dp)) {
            // 时间粒度选择（日/周/月/年）
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TimeGranularity.entries.forEach { granularity ->
                    val isSelected = selectedTimeGranularity == granularity
                    val color =
                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onTimeGranularityChanged(granularity) }
                            .padding(vertical = 8.dp)) {
                        Text(
                            text = granularity.title,
                            color = color,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        // 将选中指示器放在文字下方
                        if (isSelected) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(24.dp)
                                    .height(3.dp)
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // 基于统一时间范围的时间信息显示和控制
            DateInfoSection(
                selectedTimeGranularity = selectedTimeGranularity,
                selectedTimeRange = selectedTimeRange,
                displayYear = displayYear,
                displayMonth = displayMonth,
                displayDay = displayDay,
                displayWeek = displayWeek,
                onShowDatePicker = { showDatePicker = true },
                onDateChanged = { timeRange: TimeRange ->
                    onTimeRangeChanged(timeRange)
                }
            )

            if (showDatePicker) {
                SimpleDatePickerDialog(
                    selectedTimeGranularity = selectedTimeGranularity,
                    selectedYear = displayYear,
                    selectedMonth = displayMonth,
                    selectedDay = displayDay,
                    selectedWeek = displayWeek,
                    onYearChanged = { year: Int ->
                        // 根据当前时间粒度更新时间范围
                        val newRange = when (selectedTimeGranularity) {
                            TimeGranularity.DAY -> getDayRange(year, displayMonth, displayDay)
                            TimeGranularity.WEEK -> getWeekRange(year, displayWeek)
                            TimeGranularity.MONTH -> getMonthRange(year, displayMonth)
                            TimeGranularity.YEAR -> getYearRange(year)
                        }
                        onTimeRangeChanged(newRange)
                    },
                    onMonthChanged = { month: Int ->
                        // 根据当前时间粒度更新时间范围
                        val newRange = when (selectedTimeGranularity) {
                            TimeGranularity.DAY -> getDayRange(displayYear, month, displayDay)
                            TimeGranularity.WEEK -> getWeekRange(displayYear, displayWeek)
                            TimeGranularity.MONTH -> getMonthRange(displayYear, month)
                            TimeGranularity.YEAR -> getYearRange(displayYear)
                        }
                        onTimeRangeChanged(newRange)
                    },
                    onDayChanged = { day: Int ->
                        // 根据当前时间粒度更新时间范围
                        val newRange = when (selectedTimeGranularity) {
                            TimeGranularity.DAY -> getDayRange(displayYear, displayMonth, day)
                            TimeGranularity.WEEK -> getWeekRange(displayYear, displayWeek)
                            TimeGranularity.MONTH -> getMonthRange(displayYear, displayMonth)
                            TimeGranularity.YEAR -> getYearRange(displayYear)
                        }
                        onTimeRangeChanged(newRange)
                    },
                    onWeekChanged = { week: Int ->
                        // 根据当前时间粒度更新时间范围
                        val newRange = when (selectedTimeGranularity) {
                            TimeGranularity.DAY -> getDayRange(
                                displayYear,
                                displayMonth,
                                displayDay
                            )

                            TimeGranularity.WEEK -> getWeekRange(displayYear, week)
                            TimeGranularity.MONTH -> getMonthRange(displayYear, displayMonth)
                            TimeGranularity.YEAR -> getYearRange(displayYear)
                        }
                        onTimeRangeChanged(newRange)
                    },
                    onDismiss = { showDatePicker = false }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 显示当前选中的时间范围
            Text(
                text = "选中范围: ${selectedTimeRange.startDate} 至 ${selectedTimeRange.endDate}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

        }
    }
}

@Composable
private fun DateInfoSection(
    selectedTimeGranularity: TimeGranularity,
    selectedTimeRange: TimeRange,
    displayYear: Int,
    displayMonth: Int,
    displayDay: Int,
    displayWeek: Int,
    onShowDatePicker: () -> Unit,
    onDateChanged: (TimeRange) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                // 根据当前选择的时间粒度来调整时间范围
                val newRange = when (selectedTimeGranularity) {
                    TimeGranularity.YEAR -> {
                        val calendar = Calendar.getInstance()
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val startDate = dateFormat.parse(selectedTimeRange.startDate)
                        calendar.time = startDate
                        calendar.add(Calendar.YEAR, -1)
                        val newStartDate = dateFormat.format(calendar.time)

                        // 计算新的结束日期（年末）
                        calendar.set(
                            Calendar.DAY_OF_YEAR,
                            calendar.getActualMaximum(Calendar.DAY_OF_YEAR)
                        )
                        val newEndDate = dateFormat.format(calendar.time)

                        TimeRange(newStartDate, newEndDate, "${calendar.get(Calendar.YEAR)}年")
                    }

                    TimeGranularity.MONTH -> {
                        val calendar = Calendar.getInstance()
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val startDate = dateFormat.parse(selectedTimeRange.startDate)
                        calendar.time = startDate
                        calendar.add(Calendar.MONTH, -1)
                        val newStartDate = dateFormat.format(calendar.time)

                        // 计算新的结束日期（月末）
                        calendar.set(
                            Calendar.DAY_OF_MONTH,
                            calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                        )
                        val newEndDate = dateFormat.format(calendar.time)

                        TimeRange(
                            newStartDate,
                            newEndDate,
                            "${calendar.get(Calendar.YEAR)}年${calendar.get(Calendar.MONTH) + 1}月"
                        )
                    }

                    TimeGranularity.WEEK -> {
                        val calendar = Calendar.getInstance()
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val startDate = dateFormat.parse(selectedTimeRange.startDate)
                        calendar.time = startDate
                        calendar.add(Calendar.WEEK_OF_YEAR, -1)
                        val newStartDate = dateFormat.format(calendar.time)

                        // 计算新的结束日期（周日）
                        calendar.add(Calendar.DAY_OF_WEEK, 6)
                        val newEndDate = dateFormat.format(calendar.time)

                        TimeRange(
                            newStartDate,
                            newEndDate,
                            "${calendar.get(Calendar.YEAR)}年第${calendar.get(Calendar.WEEK_OF_YEAR)}周"
                        )
                    }

                    TimeGranularity.DAY -> {
                        val calendar = Calendar.getInstance()
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val startDate = dateFormat.parse(selectedTimeRange.startDate)
                        calendar.time = startDate
                        calendar.add(Calendar.DAY_OF_MONTH, -1)
                        val newDate = dateFormat.format(calendar.time)

                        TimeRange(
                            newDate,
                            newDate,
                            "${calendar.get(Calendar.YEAR)}年${calendar.get(Calendar.MONTH) + 1}-${
                                calendar.get(Calendar.DAY_OF_MONTH)
                            }"
                        )
                    }
                }
                onDateChanged(newRange)
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous")
            }

            // 根据数据时间粒度的选择处理时间显示
            val labelInfo = when (selectedTimeGranularity) {
                TimeGranularity.YEAR -> "${displayYear}年"
                TimeGranularity.MONTH -> "${displayYear}年${displayMonth}月"
                TimeGranularity.WEEK -> "${displayYear}年第${displayWeek}周"
                TimeGranularity.DAY -> "${displayYear}年${displayMonth}月${displayDay}日"
            }

            Text(
                text = labelInfo,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clickable {
                        // 点击时显示日期选择对话框
                        onShowDatePicker()
                    })

            IconButton(onClick = {
                // 根据当前选择的时间粒度来调整时间范围
                val newRange = when (selectedTimeGranularity) {
                    TimeGranularity.YEAR -> {
                        val calendar = Calendar.getInstance()
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val startDate = dateFormat.parse(selectedTimeRange.startDate)
                        calendar.time = startDate
                        calendar.add(Calendar.YEAR, 1)
                        val newStartDate = dateFormat.format(calendar.time)

                        // 计算新的结束日期（年末）
                        calendar.set(
                            Calendar.DAY_OF_YEAR,
                            calendar.getActualMaximum(Calendar.DAY_OF_YEAR)
                        )
                        val newEndDate = dateFormat.format(calendar.time)

                        TimeRange(newStartDate, newEndDate, "${calendar.get(Calendar.YEAR)}年")
                    }

                    TimeGranularity.MONTH -> {
                        val calendar = Calendar.getInstance()
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val startDate = dateFormat.parse(selectedTimeRange.startDate)
                        calendar.time = startDate
                        calendar.add(Calendar.MONTH, 1)
                        val newStartDate = dateFormat.format(calendar.time)

                        // 计算新的结束日期（月末）
                        calendar.set(
                            Calendar.DAY_OF_MONTH,
                            calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                        )
                        val newEndDate = dateFormat.format(calendar.time)

                        TimeRange(
                            newStartDate,
                            newEndDate,
                            "${calendar.get(Calendar.YEAR)}年${calendar.get(Calendar.MONTH) + 1}月"
                        )
                    }

                    TimeGranularity.WEEK -> {
                        val calendar = Calendar.getInstance()
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val startDate = dateFormat.parse(selectedTimeRange.startDate)
                        calendar.time = startDate
                        calendar.add(Calendar.WEEK_OF_YEAR, 1)
                        val newStartDate = dateFormat.format(calendar.time)

                        // 计算新的结束日期（周日）
                        calendar.add(Calendar.DAY_OF_WEEK, 6)
                        val newEndDate = dateFormat.format(calendar.time)

                        TimeRange(
                            newStartDate,
                            newEndDate,
                            "${calendar.get(Calendar.YEAR)}年第${calendar.get(Calendar.WEEK_OF_YEAR)}周"
                        )
                    }

                    TimeGranularity.DAY -> {
                        val calendar = Calendar.getInstance()
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val startDate = dateFormat.parse(selectedTimeRange.startDate)
                        calendar.time = startDate
                        calendar.add(Calendar.DAY_OF_MONTH, 1)
                        val newDate = dateFormat.format(calendar.time)

                        TimeRange(
                            newDate,
                            newDate,
                            "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${
                                calendar.get(Calendar.DAY_OF_MONTH)
                            }"
                        )
                    }
                }
                onDateChanged(newRange)
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
            }
        }
    }
}


@Composable
fun SimpleDatePickerDialog(
    selectedTimeGranularity: TimeGranularity,
    selectedYear: Int,
    selectedMonth: Int,
    selectedDay: Int,
    selectedWeek: Int,
    onYearChanged: (Int) -> Unit,
    onMonthChanged: (Int) -> Unit,
    onDayChanged: (Int) -> Unit,
    onWeekChanged: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = when (selectedTimeGranularity) {
                    TimeGranularity.YEAR -> "选择年份"
                    TimeGranularity.MONTH -> "选择月份"
                    TimeGranularity.WEEK -> "选择周数"
                    TimeGranularity.DAY -> "选择日期"
                }
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 根据时间粒度显示相应的选择器
                when (selectedTimeGranularity) {
                    TimeGranularity.YEAR -> {
                        YearPicker(
                            selectedYear = selectedYear,
                            onYearChanged = onYearChanged
                        )
                    }

                    TimeGranularity.MONTH -> {
                        // 使用改进的月份选择器
                        MonthPicker(
                            selectedYear = selectedYear,
                            selectedMonth = selectedMonth,
                            onYearChanged = onYearChanged,
                            onMonthChanged = onMonthChanged
                        )
                    }

                    TimeGranularity.WEEK -> {
                        WeekPicker(
                            selectedYear = selectedYear,
                            selectedWeek = selectedWeek,
                            onYearChanged = onYearChanged,
                            onWeekChanged = onWeekChanged
                        )
                    }

                    TimeGranularity.DAY -> {
                        DayPicker(
                            selectedYear = selectedYear,
                            selectedMonth = selectedMonth,
                            selectedDay = selectedDay,
                            onYearChanged = onYearChanged,
                            onMonthChanged = onMonthChanged,
                            onDayChanged = onDayChanged
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("确定")
            }
        }
    )
}


@Composable
private fun YearPicker(
    selectedYear: Int, onYearChanged: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onYearChanged(selectedYear - 1) }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Year")
        }

        Text(
            text = "${selectedYear}年",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        IconButton(onClick = { onYearChanged(selectedYear + 1) }) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Year")
        }
    }
}

@Composable
private fun MonthPicker(
    selectedYear: Int,
    selectedMonth: Int,
    onYearChanged: (Int) -> Unit,
    onMonthChanged: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 年份选择
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onYearChanged(selectedYear - 1) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Year")
            }

            Text(
                text = "${selectedYear}年",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            IconButton(onClick = { onYearChanged(selectedYear + 1) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Year")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 月份网格选择
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            items(12) { month ->
                val monthNumber = month + 1
                val isSelected = selectedMonth == monthNumber
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(4.dp)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { onMonthChanged(monthNumber) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${monthNumber}月",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun WeekPicker(
    selectedYear: Int, selectedWeek: Int, onYearChanged: (Int) -> Unit, onWeekChanged: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 年份选择
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onYearChanged(selectedYear - 1) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Year")
            }

            Text(
                text = "${selectedYear}年",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            IconButton(onClick = { onYearChanged(selectedYear + 1) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Year")
            }
        }

        // 周数选择
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                if (selectedWeek > 1) {
                    onWeekChanged(selectedWeek - 1)
                } else {
                    // 获取上一年的最后一周
                    val calendar = Calendar.getInstance()
                    calendar.set(selectedYear - 1, 11, 31) // 上一年的12月31日
                    val lastWeekOfYear = calendar.get(Calendar.WEEK_OF_YEAR)
                    onWeekChanged(lastWeekOfYear)
                    onYearChanged(selectedYear - 1)
                }
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Week")
            }

            Text(
                text = "第${selectedWeek}周",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            IconButton(onClick = {
                // 获取当前年的最大周数
                val calendar = Calendar.getInstance()
                calendar.set(selectedYear, 11, 31) // 当前年的12月31日
                val maxWeekOfYear = calendar.get(Calendar.WEEK_OF_YEAR)
                if (selectedWeek < maxWeekOfYear) {
                    onWeekChanged(selectedWeek + 1)
                } else {
                    onWeekChanged(1)
                    onYearChanged(selectedYear + 1)
                }
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Week")
            }
        }
    }
}

@Composable
private fun DayPicker(
    selectedYear: Int,
    selectedMonth: Int,
    selectedDay: Int,
    onYearChanged: (Int) -> Unit,
    onMonthChanged: (Int) -> Unit,
    onDayChanged: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 年份选择
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onYearChanged(selectedYear - 1) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Year")
            }

            Text(
                text = "${selectedYear}年",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            IconButton(onClick = { onYearChanged(selectedYear + 1) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Year")
            }
        }

        // 月份选择
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                if (selectedMonth > 1) {
                    onMonthChanged(selectedMonth - 1)
                } else {
                    onMonthChanged(12)
                    onYearChanged(selectedYear - 1)
                }
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Month")
            }

            Text(
                text = "${selectedMonth}月",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            IconButton(onClick = {
                if (selectedMonth < 12) {
                    onMonthChanged(selectedMonth + 1)
                } else {
                    onMonthChanged(1)
                    onYearChanged(selectedYear + 1)
                }
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month")
            }
        }

        // 日期选择
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                if (selectedDay > 1) {
                    onDayChanged(selectedDay - 1)
                } else {
                    // 需要处理月份变化
                    val calendar = Calendar.getInstance()
                    calendar.set(selectedYear, selectedMonth - 2, 1) // 上个月
                    val lastDayOfPrevMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                    onDayChanged(lastDayOfPrevMonth)
                    if (selectedMonth > 1) {
                        onMonthChanged(selectedMonth - 1)
                    } else {
                        onMonthChanged(12)
                        onYearChanged(selectedYear - 1)
                    }
                }
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Day")
            }

            Text(
                text = "${selectedDay}日",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            IconButton(onClick = {
                val calendar = Calendar.getInstance()
                calendar.set(selectedYear, selectedMonth - 1, 1)
                val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                if (selectedDay < maxDay) {
                    onDayChanged(selectedDay + 1)
                } else {
                    onDayChanged(1)
                    if (selectedMonth < 12) {
                        onMonthChanged(selectedMonth + 1)
                    } else {
                        onMonthChanged(1)
                        onYearChanged(selectedYear + 1)
                    }
                }
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Day")
            }
        }
    }
}

/**
 * 年度总览卡片组件
 * 展示总阅读时长、阅读天数、读完书籍和笔记数量等年度统计数据
 */
@Composable
private fun AnnualOverviewCard(
    timeRange: TimeRange,
    viewModel: StatisticsOverviewViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // 当时间范围改变时，重新加载数据
    LaunchedEffect(timeRange) {
        viewModel.loadStatisticsByDateRange(timeRange.startDate, timeRange.endDate)
    }
    
    // 初始化时加载数据
    LaunchedEffect(Unit) {
        viewModel.loadStatisticsByDateRange(timeRange.startDate, timeRange.endDate)
    }
    
    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (uiState.error != null) {
        Text(
            text = "加载数据失败: ${uiState.error}",
            color = MaterialTheme.colorScheme.error
        )
    } else {
        StatisticRow(
            label = "总阅读时长",
            value = formatDuration(uiState.totalReadingTime)
        )

        Spacer(modifier = Modifier.height(12.dp))

        StatisticRow(
            label = "阅读天数",
            value = "${uiState.readingDaysCount} 天"
        )

        Spacer(modifier = Modifier.height(12.dp))

        StatisticRow(
            label = "读完书籍",
            value = "${uiState.finishedBooksCount} 本"
        )

        Spacer(modifier = Modifier.height(12.dp))

        StatisticRow(
            label = "笔记数量",
            value = "${uiState.noteCount} 条"
        )
    }
}

/**
 * 半圆形进度条组件
 * @param progress 进度值 (0.0 - 1.0)
 * @param modifier 修饰符
 * @param color 进度条颜色
 * @param trackColor 轨道颜色
 * @param strokeWidth 线条宽度
 */
@Composable
fun SemicircularProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    trackColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
    strokeWidth: Dp = 8.dp
) {
    val strokeWidthPx = with(LocalDensity.current) { strokeWidth.toPx() }
    val stroke = Stroke(
        width = strokeWidthPx,
        cap = StrokeCap.Round
    )
    
    // 在Composable上下文中创建TextMeasurer
    val textMeasurer = rememberTextMeasurer()
    val progressText = "${(progress * 100).toInt()}%"
    val textLayoutResult = textMeasurer.measure(
        text = progressText,
        style = MaterialTheme.typography.displayMedium.copy(color = color)
    )
    
    Canvas(modifier = modifier) {
        // 计算绘制区域的中心点和半径
        val center = Offset(size.width / 2, size.height)
        val radius = min(size.width / 2, size.height) - strokeWidthPx / 2
        
        // 绘制背景轨道（半圆）
        drawArc(
            color = trackColor,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = stroke
        )
        
        // 绘制进度圆弧
        val progressAngle = progress.coerceIn(0f, 1f) * 180f
        drawArc(
            color = color,
            startAngle = 180f,
            sweepAngle = progressAngle,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = stroke
        )
        
        // 在半圆中心绘制进度文本
        drawText(
            textLayoutResult = textLayoutResult,
            topLeft = Offset(
                center.x - textLayoutResult.size.width / 2,
                center.y - radius / 2 - textLayoutResult.size.height / 2
            )
        )
    }
}


