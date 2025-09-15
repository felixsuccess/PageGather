package com.anou.pagegather.ui.feature.statistics.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.anou.pagegather.ui.feature.bookshelf.booklist.GRID_COLUMNS
import com.anou.pagegather.ui.feature.bookshelf.booklist.GRID_PADDING
import com.anou.pagegather.ui.feature.bookshelf.booklist.GRID_SPACING
import com.anou.pagegather.ui.feature.bookshelf.common.BookGridItem
import com.anou.pagegather.ui.feature.statistics.StatisticsOverviewViewModel
import com.anou.pagegather.ui.feature.statistics.StatisticsOverviewUiState

/**
 * 阅读概览卡片组件
 * 展示今日、本周、本月的阅读统计数据
 */
@Composable
fun ReadingOverviewCard(
    viewModel: StatisticsOverviewViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "阅读概览",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.error != null) {
                Box(
                    modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "加载数据失败: ${uiState.error}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } else {
                val gridState = rememberLazyGridState()
                //
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxWidth(),
                    columns = GRID_COLUMNS,
                    contentPadding = GRID_PADDING,
                    state = gridState,
                    verticalArrangement = Arrangement.spacedBy(GRID_SPACING),
                    horizontalArrangement = Arrangement.spacedBy(GRID_SPACING)
                ) {
                    item {
                        // 统计数据行
                        StatisticRow(
                            icon = Icons.Default.AccessTime,
                            label = "今日阅读",
                            value = formatDuration(uiState.todayReadingTime)
                        )


                    }
                    item {
                        StatisticRow(
                            icon = Icons.Default.CalendarToday,
                            label = "本周阅读",
                            value = formatDuration(uiState.weekReadingTime)
                        )

                    }
                    item {

                        StatisticRow(
                            icon = Icons.Default.CalendarToday,
                            label = "本月阅读",
                            value = formatDuration(uiState.monthReadingTime)
                        )

                    }
                    item {
                        StatisticRow(
                            icon = Icons.Default.AccessTime,
                            label = "总阅读时长",
                            value = formatDuration(uiState.totalReadingTime)
                        )

                    }
                    item {
                        StatisticRow(
                            icon = Icons.Default.Book,
                            label = "在读书籍",
                            value = "${uiState.readingBooksCount} 本"
                        )

                    }
                    item {
                        StatisticRow(
                            icon = Icons.Default.Book,
                            label = "读完书籍",
                            value = "${uiState.finishedBooksCount} 本"
                        )

                    }


                    item {
                        StatisticRow(
                            icon = Icons.Default.Book,
                            label = "书籍总数",
                            value = "${uiState.totalBooksCount} 本"
                        )
                    }
                    item {
                        StatisticRow(
                            icon = Icons.Default.CalendarToday,
                            label = "阅读天数",
                            value = "${uiState.readingDaysCount} 天"
                        )
                    }
                    item {
                        StatisticRow(
                            icon = Icons.Default.Edit,
                            label = "笔记数量",
                            value = "${uiState.noteCount} 条"
                        )
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
    icon: ImageVector, label: String, value: String
) {


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
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

    return when {
        hours > 0 -> "${hours}小时${minutes % 60}分钟"
        minutes > 0 -> "${minutes}分钟"
        else -> "${seconds}秒"
    }
}