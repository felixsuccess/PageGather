package com.anou.pagegather.ui.feature.reading

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.anou.pagegather.data.model.BookReadingStatistics
import java.text.SimpleDateFormat
import java.util.*

/**
 * 书籍阅读统计页面
 * 展示每本书籍的阅读统计数据
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookReadingStatisticsScreen(
    onBackClick: () -> Unit,
    viewModel: BookReadingStatisticsViewModel = hiltViewModel()
) {
    val bookStatistics by viewModel.bookStatistics.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("书籍阅读统计") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (bookStatistics.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "暂无阅读统计数据",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(bookStatistics) { statistics ->
                    BookStatisticsCard(statistics = statistics)
                }
            }
        }
    }
}

/**
 * 书籍统计卡片组件
 */
@Composable
private fun BookStatisticsCard(statistics: BookReadingStatistics) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 书籍名称
            Text(
                text = statistics.bookName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // 统计数据行
            StatisticRow(
                label = "总阅读时长",
                value = formatDuration(statistics.totalReadingTime)
            )
            
            StatisticRow(
                label = "阅读次数",
                value = "${statistics.readingRecordCount} 次"
            )
            
            StatisticRow(
                label = "平均时长",
                value = formatDuration(statistics.averageReadingTime)
            )
            
            StatisticRow(
                label = "阅读进度",
                value = "${String.format("%.1f", statistics.readingProgress)}%"
            )
            
            // 最后阅读时间
            statistics.lastReadingTime?.let { lastTime ->
                StatisticRow(
                    label = "最后阅读",
                    value = formatDateTime(lastTime)
                )
            }
        }
    }
}

/**
 * 统计数据行组件
 */
@Composable
private fun StatisticRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
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
 * 格式化时间显示
 */
private fun formatDateTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

/**
 * 格式化持续时间显示
 */
private fun formatDuration(milliseconds: Long): String {
    if (milliseconds <= 0) return "0秒"
    
    val seconds = milliseconds / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    
    return when {
        hours > 0 -> "${hours}小时${minutes % 60}分钟"
        minutes > 0 -> "${minutes}分钟${seconds % 60}秒"
        else -> "${seconds}秒"
    }
}