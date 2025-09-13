package com.anou.pagegather.ui.feature.statistics.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.error != null) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "加载数据失败: ${uiState.error}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } else {
                // 统计数据行
                StatisticRow(
                    icon = Icons.Default.AccessTime,
                    label = "今日阅读",
                    value = formatDuration(uiState.todayReadingTime)
                )

                Spacer(modifier = Modifier.height(12.dp))

                StatisticRow(
                    icon = Icons.Default.CalendarToday,
                    label = "本周阅读",
                    value = formatDuration(uiState.weekReadingTime)
                )

                Spacer(modifier = Modifier.height(12.dp))

                StatisticRow(
                    icon = Icons.Default.CalendarToday,
                    label = "本月阅读",
                    value = formatDuration(uiState.monthReadingTime)
                )

                Spacer(modifier = Modifier.height(12.dp))

                StatisticRow(
                    icon = Icons.Default.AccessTime,
                    label = "总阅读时长",
                    value = formatDuration(uiState.totalReadingTime)
                )

                Spacer(modifier = Modifier.height(12.dp))

                StatisticRow(
                    icon = Icons.Default.Book,
                    label = "在读书籍",
                    value = "${uiState.readingBooksCount} 本"
                )

                Spacer(modifier = Modifier.height(12.dp))

                StatisticRow(
                    icon = Icons.Default.Book,
                    label = "读完书籍",
                    value = "${uiState.finishedBooksCount} 本"
                )

                Spacer(modifier = Modifier.height(12.dp))

                StatisticRow(
                    icon = Icons.Default.Book,
                    label = "书籍总数",
                    value = "${uiState.totalBooksCount} 本"
                )

                Spacer(modifier = Modifier.height(12.dp))

                StatisticRow(
                    icon = Icons.Default.CalendarToday,
                    label = "阅读天数",
                    value = "${uiState.readingDaysCount} 天"
                )

                Spacer(modifier = Modifier.height(12.dp))

                StatisticRow(
                    icon = Icons.Default.Edit,
                    label = "笔记数量",
                    value = "${uiState.noteCount} 条"
                )
            }
        }
    }
}

/**
 * 统计数据行组件
 */
@Composable
private fun StatisticRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

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
    
    return when {
        hours > 0 -> "${hours}小时${minutes % 60}分钟"
        minutes > 0 -> "${minutes}分钟"
        else -> "${seconds}秒"
    }
}