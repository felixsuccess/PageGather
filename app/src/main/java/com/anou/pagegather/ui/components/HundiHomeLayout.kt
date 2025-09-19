package com.anou.pagegather.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anou.pagegather.data.local.entity.BookEntity
import com.anou.pagegather.ui.theme.extendedColors

/**
 * Hundi 风格的主页布局
 */
@Composable
fun HundiHomeLayout(
    recentBooks: List<BookEntity> = emptyList(),
    readingStats: ReadingStats = ReadingStats(),
    onBookClick: (BookEntity) -> Unit = {},
    onAddBookClick: () -> Unit = {},
    onTimerClick: () -> Unit = {},
    onStatisticsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    HundiGradientBackground(
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 欢迎区域
            item {
                WelcomeSection(
                    onAddBookClick = onAddBookClick,
                    onTimerClick = onTimerClick
                )
            }

            // 统计概览
            item {
                StatisticsOverviewSection(
                    stats = readingStats,
                    onStatisticsClick = onStatisticsClick
                )
            }

            // 最近阅读
            if (recentBooks.isNotEmpty()) {
                item {
                    RecentBooksSection(
                        books = recentBooks,
                        onBookClick = onBookClick
                    )
                }
            }

            // 快速操作
            item {
                QuickActionsSection(
                    onAddBookClick = onAddBookClick,
                    onTimerClick = onTimerClick
                )
            }

            // 底部间距
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

/**
 * 欢迎区域
 */
@Composable
private fun WelcomeSection(
    onAddBookClick: () -> Unit,
    onTimerClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 问候语
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = getGreeting(),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.extendedColors.titleColor,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "今天想读什么书呢？",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.extendedColors.subtitleColor
            )
        }

        // 主要操作按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HundiPrimaryButton(
                text = "开始阅读",
                onClick = onTimerClick,
                modifier = Modifier.weight(1f)
            )
            
            HundiSecondaryButton(
                text = "添加书籍",
                onClick = onAddBookClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * 统计概览区域
 */
@Composable
private fun StatisticsOverviewSection(
    stats: ReadingStats,
    onStatisticsClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "阅读概览",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.extendedColors.titleColor,
                fontWeight = FontWeight.SemiBold
            )
            
            TextButton(onClick = onStatisticsClick) {
                Text(
                    text = "查看更多",
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(
                listOf(
                    StatItem("本月阅读", "${stats.monthlyBooks}", "本书", Icons.Default.MenuBook),
                    StatItem("阅读时长", "${stats.readingHours}", "小时", Icons.Default.Schedule),
                    StatItem("完成进度", "${stats.completionRate}%", "完成", Icons.Default.TrendingUp),
                    StatItem("笔记数量", "${stats.noteCount}", "条", Icons.Default.Note)
                )
            ) { item ->
                HundiStatCard(
                    title = item.title,
                    value = item.value,
                    subtitle = item.subtitle,
                    modifier = Modifier.width(140.dp),
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                )
            }
        }
    }
}

/**
 * 最近阅读区域
 */
@Composable
private fun RecentBooksSection(
    books: List<BookEntity>,
    onBookClick: (BookEntity) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "最近阅读",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.extendedColors.titleColor,
            fontWeight = FontWeight.SemiBold
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(books.take(5)) { book ->
                HundiBookGridCard(
                    book = book,
                    onClick = { onBookClick(book) },
                    modifier = Modifier.width(120.dp)
                )
            }
        }
    }
}

/**
 * 快速操作区域
 */
@Composable
private fun QuickActionsSection(
    onAddBookClick: () -> Unit,
    onTimerClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "快速操作",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.extendedColors.titleColor,
            fontWeight = FontWeight.SemiBold
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(
                listOf(
                    QuickAction("扫描添加", "扫描ISBN快速添加", Icons.Default.QrCodeScanner, onAddBookClick),
                    QuickAction("阅读计时", "开始专注阅读", Icons.Default.Timer, onTimerClick),
                    QuickAction("添加笔记", "记录阅读感悟", Icons.Default.EditNote, {}),
                    QuickAction("阅读目标", "设置阅读计划", Icons.Default.Flag, {})
                )
            ) { action ->
                QuickActionCard(
                    action = action,
                    modifier = Modifier.width(160.dp)
                )
            }
        }
    }
}

/**
 * 快速操作卡片
 */
@Composable
private fun QuickActionCard(
    action: QuickAction,
    modifier: Modifier = Modifier
) {
    HundiCard(
        onClick = action.onClick,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = action.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            
            Text(
                text = action.title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.extendedColors.titleColor,
                fontWeight = FontWeight.SemiBold
            )
            
            Text(
                text = action.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.extendedColors.subtitleColor
            )
        }
    }
}

/**
 * 获取问候语
 */
private fun getGreeting(): String {
    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 5..11 -> "早上好"
        in 12..17 -> "下午好"
        in 18..22 -> "晚上好"
        else -> "夜深了"
    }
}

/**
 * 数据类定义
 */
data class ReadingStats(
    val monthlyBooks: Int = 0,
    val readingHours: Int = 0,
    val completionRate: Int = 0,
    val noteCount: Int = 0
)

data class StatItem(
    val title: String,
    val value: String,
    val subtitle: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

data class QuickAction(
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val onClick: () -> Unit
)