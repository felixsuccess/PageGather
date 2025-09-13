package com.anou.pagegather.ui.feature.statistics

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.anou.pagegather.data.local.entity.ReadingRecordEntity
import com.anou.pagegather.data.local.entity.RecordType
import com.anou.pagegather.ui.feature.statistics.components.ReadingOverviewCard
import com.anou.pagegather.ui.navigation.Routes
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// 定义统计页面的Tab枚举
enum class StatisticsTab(val title: String) {
    OVERVIEW("概览"),
    TIMELINE("时间线"),
    STATISTICS("统计")  // 合并后的统计Tab
}

// 时间粒度枚举
enum class TimeGranularity(val title: String) {
    DAY("日"),
    WEEK("周"),
    MONTH("月"),
    YEAR("年")
}

// 时间范围数据类
data class TimeRange(
    val startDate: String,
    val endDate: String,
    val label: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    navController: NavController,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableStateOf(StatisticsTab.OVERVIEW) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
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
                Tab(
                    selected = selectedTab == tab,
                    onClick = { selectedTab = tab },
                    text = {
                        Text(
                            text = tab.title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (selectedTab == tab) FontWeight.Medium else FontWeight.Normal
                        )
                    }
                )
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
                        .padding(16.dp),
                    navController = navController
                )
            }
        }
    }
}

@Composable
private fun StatisticsOverviewTab(modifier: Modifier = Modifier, navController: NavController) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 阅读概览卡片
        ReadingOverviewCard()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 功能按钮区域
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatisticsButton(
                text = "书籍阅读统计",
                onClick = {
                    navController.navigate(Routes.ReadingRoutes.BOOK_READING_STATISTICS)
                }
            )
        }
    }
}

@Composable
private fun StatisticsTimelineTab(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // 阅读趋势图表占位符 (移除了年度报告卡片)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "阅读趋势",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "阅读趋势图表（柱状图）",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun StatisticsTabContent(modifier: Modifier = Modifier, navController: NavController) {
    var selectedTimeGranularity by remember { mutableStateOf<TimeGranularity>(TimeGranularity.MONTH) }
    var selectedTimeRange by remember { mutableStateOf<TimeRange>(getCurrentMonthRange()) }

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
                    selectedTimeRange = when(it) {
                        TimeGranularity.DAY -> getTodayRange()
                        TimeGranularity.WEEK -> getCurrentWeekRange()
                        TimeGranularity.MONTH -> getCurrentMonthRange()
                        TimeGranularity.YEAR -> getCurrentYearRange()
                    }
                },
                selectedTimeRange = selectedTimeRange,
                onTimeRangeChanged = { it: TimeRange -> selectedTimeRange = it },
                onCustomDateRangeSelected = { startDate: String, endDate: String ->
                    // 处理自定义日期范围
                }
            )
        }

        // 阅读时长分布图表占位符
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "阅读时长分布",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "阅读时长分布图表（柱状图）",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item{
            // 阅读趋势图表占位符 (移除了年度报告卡片)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "阅读趋势",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "阅读趋势图表（柱状图）",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }


        }


        item {
            // 阅读习惯时间分布图表占位符
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "阅读习惯时间分布",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "阅读习惯时间分布图表（热力图）",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        
        item {
            // 书籍类型分布图表占位符
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "书籍类型分布",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "书籍类型分布图表（饼图）",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        
        item {
            // 书籍阅读统计占位符
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "书籍阅读统计",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(16.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "书籍阅读统计功能正在开发中...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        
        item {
            // 阅读最久的书籍占位符
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
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
            // 偏好阅读类型占位符
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
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
                        text = "偏好阅读类型分析（饼图）",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = {
                            navController.navigate(Routes.ReadingRoutes.PREFERRED_BOOK_TYPES)
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("查看详情")
                    }
                }
            }
        }

        item {
            // 阅读习惯时间分布占位符
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "阅读习惯时间分布",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(16.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "阅读习惯时间分布分析（热力图）",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = {
                            navController.navigate(Routes.ReadingRoutes.READING_HABIT_DISTRIBUTION)
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("查看详情")
                    }
                }
            }
        }

        item {
            // 书籍类型分布占位符
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "书籍类型分布",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(16.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "书籍类型分布分析（饼图）",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = {
                            navController.navigate(Routes.ReadingRoutes.BOOK_TYPE_DISTRIBUTION)
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("查看详情")
                    }
                }
            }
        }
        
        item {
            // 偏好作者和版权方占位符
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "偏好作者和版权方",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(16.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "偏好作者和版权方分析（云图）",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = {
                            navController.navigate(Routes.ReadingRoutes.PREFERRED_AUTHORS_PUBLISHERS)
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("查看详情")
                    }
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
    label: String,
    value: String
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

@Composable
private fun StatisticsButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(text = text)
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
                        imageVector = Icons.Default.Delete,
                        contentDescription = "删除记录"
                    )
                }
            }
            
            // 记录类型
            val recordType = if (record.recordType == RecordType.PRECISE.ordinal) "计时记录" else "手动记录"
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
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
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
    
    // 设置为本周最后一天（周日）
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
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
    
    // 设置为本周第一天（周一）
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val startDate = dateFormat.format(calendar.time)
    
    // 设置为本周最后一天（周日）
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
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



@Composable
private fun TimeRangeSelector(
    selectedTimeGranularity: TimeGranularity,
    onTimeGranularityChanged: (TimeGranularity) -> Unit,
    selectedTimeRange: TimeRange,
    onTimeRangeChanged: (TimeRange) -> Unit,
    onCustomDateRangeSelected: (startDate: String, endDate: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedYear by remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableStateOf(Calendar.getInstance().get(Calendar.MONTH) + 1) } // 月份从0开始，所以需要+1
    var selectedWeek by remember { mutableStateOf(getWeekOfYear()) }
    var selectedDay by remember { mutableStateOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 时间粒度选择（日/周/月/年）
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TimeGranularity.entries.forEach { granularity ->
                    val isSelected = selectedTimeGranularity == granularity
                    val color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

                    Text(
                        text = granularity.title,
                        color = color,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .clickable { onTimeGranularityChanged(granularity) }
                    )

                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .height(2.dp)
                                .fillMaxWidth(0.2f)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 根据粒度显示不同的时间选择器
            when (selectedTimeGranularity) {
                TimeGranularity.DAY -> {
                    DaySelector(
                        selectedYear = selectedYear,
                        selectedMonth = selectedMonth,
                        selectedDay = selectedDay,
                        onYearChanged = { year ->
                            selectedYear = year
                            // 更新选中的日期范围
                            val newRange = getDayRange(year, selectedMonth, selectedDay)
                            onTimeRangeChanged(newRange)
                        },
                        onMonthChanged = { month ->
                            selectedMonth = month
                            // 更新选中的日期范围
                            val newRange = getDayRange(selectedYear, month, selectedDay)
                            onTimeRangeChanged(newRange)
                        },
                        onDayChanged = { day ->
                            selectedDay = day
                            // 更新选中的日期范围
                            val newRange = getDayRange(selectedYear, selectedMonth, day)
                            onTimeRangeChanged(newRange)
                        }
                    )
                }
                TimeGranularity.WEEK -> {
                    WeekSelector(
                        selectedYear = selectedYear,
                        selectedWeek = selectedWeek,
                        onYearChanged = { year ->
                            selectedYear = year
                            // 更新选中的日期范围
                            val newRange = getWeekRange(year, selectedWeek)
                            onTimeRangeChanged(newRange)
                        },
                        onWeekChanged = { week ->
                            selectedWeek = week
                            // 更新选中的日期范围
                            val newRange = getWeekRange(selectedYear, week)
                            onTimeRangeChanged(newRange)
                        }
                    )
                }
                TimeGranularity.MONTH -> {
                    MonthSelector(
                        selectedYear = selectedYear,
                        selectedMonth = selectedMonth,
                        onYearChanged = { year ->
                            selectedYear = year
                            // 更新选中的日期范围
                            val newRange = getMonthRange(year, selectedMonth)
                            onTimeRangeChanged(newRange)
                        },
                        onMonthChanged = { month ->
                            selectedMonth = month
                            // 更新选中的日期范围
                            val newRange = getMonthRange(selectedYear, month)
                            onTimeRangeChanged(newRange)
                        }
                    )
                }
                TimeGranularity.YEAR -> {
                    YearSelector(
                        selectedYear = selectedYear,
                        onYearChanged = { year ->
                            selectedYear = year
                            // 更新选中的日期范围
                            val newRange = getYearRange(year)
                            onTimeRangeChanged(newRange)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 显示当前选中的时间范围
            Text(
                text = "选中范围: ${selectedTimeRange.startDate} 至 ${selectedTimeRange.endDate}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 自定义日期范围按钮
            Button(
                onClick = { /* 打开自定义日期选择器 */ },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("自定义日期范围")
            }
        }
    }
}


@Composable
private fun DaySelector(
    selectedYear: Int,
    selectedMonth: Int,
    selectedDay: Int,
    onYearChanged: (Int) -> Unit,
    onMonthChanged: (Int) -> Unit,
    onDayChanged: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // 年份选择
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "年份:",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onYearChanged(selectedYear - 1) }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Year")
                }
                
                Text(
                    text = selectedYear.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                
                IconButton(onClick = { onYearChanged(selectedYear + 1) }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Year")
                }
            }
        }
        
        // 月份选择
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "月份:",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { 
                    if (selectedMonth > 1) {
                        onMonthChanged(selectedMonth - 1)
                    } else {
                        onMonthChanged(12)
                        onYearChanged(selectedYear - 1)
                    }
                }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Previous Month")
                }
                
                Text(
                    text = selectedMonth.toString(),
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
                    Icon(Icons.Default.ArrowForward, contentDescription = "Next Month")
                }
            }
        }
        
        // 日期选择
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "日期:",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                    Icon(Icons.Default.ArrowBack, contentDescription = "Previous Day")
                }
                
                Text(
                    text = selectedDay.toString(),
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
                    Icon(Icons.Default.ArrowForward, contentDescription = "Next Day")
                }
            }
        }
    }
}

@Composable
private fun WeekSelector(
    selectedYear: Int,
    selectedWeek: Int,
    onYearChanged: (Int) -> Unit,
    onWeekChanged: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // 年份选择
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "年份:",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onYearChanged(selectedYear - 1) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Previous Year")
                }
                
                Text(
                    text = selectedYear.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                
                IconButton(onClick = { onYearChanged(selectedYear + 1) }) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Next Year")
                }
            }
        }
        
        // 周数选择
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "周数:",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                    Icon(Icons.Default.ArrowBack, contentDescription = "Previous Week")
                }
                
                Text(
                    text = selectedWeek.toString(),
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
                    Icon(Icons.Default.ArrowForward, contentDescription = "Next Week")
                }
            }
        }
    }
}

@Composable
private fun MonthSelector(
    selectedYear: Int,
    selectedMonth: Int,
    onYearChanged: (Int) -> Unit,
    onMonthChanged: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // 年份选择
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "年份:",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onYearChanged(selectedYear - 1) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Previous Year")
                }
                
                Text(
                    text = selectedYear.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                
                IconButton(onClick = { onYearChanged(selectedYear + 1) }) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Next Year")
                }
            }
        }
        
        // 月份选择
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "月份:",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { 
                    if (selectedMonth > 1) {
                        onMonthChanged(selectedMonth - 1)
                    } else {
                        onMonthChanged(12)
                        onYearChanged(selectedYear - 1)
                    }
                }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Previous Month")
                }
                
                Text(
                    text = selectedMonth.toString(),
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
                    Icon(Icons.Default.ArrowForward, contentDescription = "Next Month")
                }
            }
        }
    }
}

@Composable
private fun YearSelector(
    selectedYear: Int,
    onYearChanged: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
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

/**
 * 筛选对话框
 */
@Composable
private fun FilterDialog(
    onDismiss: () -> Unit,
    onApply: (date: String?, bookId: Long?) -> Unit,
    currentDate: String?,
    currentBookId: Long?
) {
    var selectedDate by remember { mutableStateOf(currentDate) }
    var selectedBookId by remember { mutableStateOf(currentBookId) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("筛选条件")
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 日期筛选
                Text(
                    text = "按日期筛选",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            val calendar = Calendar.getInstance()
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            selectedDate = dateFormat.format(calendar.time)
                        }
                    ) {
                        Text("今天")
                    }
                    
                    Button(
                        onClick = {
                            selectedDate = null
                        }
                    ) {
                        Text("清除")
                    }
                }
                
                // TODO: 添加更多日期选项和书籍筛选
                
                Text(
                    text = "选定日期: ${selectedDate ?: "无"}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onApply(selectedDate, selectedBookId)
                }
            ) {
                Text("应用")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}