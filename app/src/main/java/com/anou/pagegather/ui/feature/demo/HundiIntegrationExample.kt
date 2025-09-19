package com.anou.pagegather.ui.feature.demo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anou.pagegather.ui.components.*
import com.anou.pagegather.ui.theme.extendedColors
import com.anou.pagegather.data.local.entity.BookEntity
import com.anou.pagegather.data.local.entity.ReadStatus

/**
 * Hundi 风格集成示例
 * 展示如何在实际项目中使用 Hundi 风格组件
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HundiIntegrationExample() {
    // 示例数据
    val sampleBooks = remember {
        listOf(
            createSampleBookEntity("长安十二时辰", "马伯庸", ReadStatus.READING),
            createSampleBookEntity("燕食记", "葛亮", ReadStatus.FINISHED),
            createSampleBookEntity("食南之徒", "马伯庸", ReadStatus.WANT_TO_READ)
        )
    }
    
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("主页", "书籍", "统计")
    
    Column(modifier = Modifier.fillMaxSize()) {
        // 顶部应用栏
        TopAppBar(
            title = {
                Text(
                    text = "PageGather",
                    fontWeight = FontWeight.Bold
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary
            ),
            actions = {
                IconButton(onClick = { /* 搜索 */ }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "搜索",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        )
        
        // 标签栏
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }
        
        // 内容区域
        when (selectedTab) {
            0 -> HomeTabContent(sampleBooks)
            1 -> BooksTabContent(sampleBooks)
            2 -> StatisticsTabContent()
        }
    }
}

@Composable
private fun HomeTabContent(books: List<BookEntity>) {
    val stats = ReadingStats(
        monthlyBooks = 12,
        readingHours = 48,
        completionRate = 75,
        noteCount = 156
    )
    
    HundiHomeLayout(
        recentBooks = books,
        readingStats = stats,
        onBookClick = { /* 处理书籍点击 */ },
        onAddBookClick = { /* 添加书籍 */ },
        onTimerClick = { /* 开始计时 */ },
        onStatisticsClick = { /* 查看统计 */ }
    )
}

@Composable
private fun BooksTabContent(books: List<BookEntity>) {
    HundiGradientBackground {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 添加书籍按钮
            item {
                HundiPrimaryButton(
                    text = "添加新书籍",
                    onClick = { /* 添加书籍 */ },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // 书籍列表
            items(books) { book ->
                HundiBookCard(
                    book = book,
                    onClick = { /* 点击书籍 */ },
                    onLongClick = { /* 长按书籍 */ }
                )
            }
        }
    }
}

@Composable
private fun StatisticsTabContent() {
    HundiGradientBackground {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 标题
            item {
                Text(
                    text = "阅读统计",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.extendedColors.titleColor,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // 统计卡片网格
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        HundiStatCard(
                            title = "本月阅读",
                            value = "12",
                            subtitle = "本书",
                            modifier = Modifier.weight(1f),
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        )
                        
                        HundiStatCard(
                            title = "阅读时长",
                            value = "48",
                            subtitle = "小时",
                            modifier = Modifier.weight(1f),
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        HundiStatCard(
                            title = "完成进度",
                            value = "75%",
                            subtitle = "完成",
                            modifier = Modifier.weight(1f),
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        )
                        
                        HundiStatCard(
                            title = "笔记数量",
                            value = "156",
                            subtitle = "条",
                            modifier = Modifier.weight(1f),
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Note,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        )
                    }
                }
            }
            
            // 详细统计卡片
            item {
                HundiCard {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "本周阅读目标",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "阅读时长目标",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "12/15 小时",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            
                            HundiProgressIndicator(
                                progress = 0.8f,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            HundiTag(
                                text = "还需 3 小时",
                                backgroundColor = MaterialTheme.extendedColors.warning.copy(alpha = 0.2f),
                                textColor = MaterialTheme.extendedColors.warning
                            )
                            
                            HundiTag(
                                text = "本周第 5 天",
                                backgroundColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 创建示例书籍实体
 */
private fun createSampleBookEntity(
    name: String,
    author: String,
    status: ReadStatus
): BookEntity {
    return BookEntity(
        id = 0,
        name = name,
        author = author,
        readStatus = status.code,
        readPosition = when (status) {
            ReadStatus.READING -> 150.0
            ReadStatus.FINISHED -> 400.0
            else -> 0.0
        },
        totalPosition = 400,
        rating = if (status == ReadStatus.FINISHED) 4.5f else 0f,
        createdDate = System.currentTimeMillis(),
        lastReadDate = if (status == ReadStatus.READING) System.currentTimeMillis() - 3600000 else null
    )
}