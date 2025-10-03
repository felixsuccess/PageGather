package com.anou.pagegather.ui.feature.demo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anou.pagegather.ui.components.*
import com.anou.pagegather.ui.theme.extendedColors
import com.anou.pagegather.data.local.entity.BookEntity
import com.anou.pagegather.data.local.entity.ReadStatus

/**
 * Hundi 风格展示界面
 * 包含完整的组件展示和交互演示
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HundiStyleShowcaseScreen(
    onNavigateBack: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("组件展示", "书籍界面", "主页风格")
    
    Column(modifier = Modifier.fillMaxSize()) {
        // 顶部应用栏
        TopAppBar(
            title = {
                Text(
                    text = "Hundi 风格展示",
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "返回"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
            )
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
            0 -> ComponentShowcaseTab()
            1 -> BookInterfaceTab()
            2 -> HomeStyleTab()
        }
    }
}

@Composable
private fun ComponentShowcaseTab() {
    HundiGradientBackground {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 标题
            item {
                Text(
                    text = "Hundi 风格组件库",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.extendedColors.titleColor,
                    fontWeight = FontWeight.Bold
                )
            }

            // 按钮展示
            item {
                ComponentSection(title = "按钮组件") {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        HundiPrimaryButton(
                            text = "主要按钮",
                            onClick = { },
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        HundiSecondaryButton(
                            text = "次要按钮",
                            onClick = { },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // 统计卡片展示
            item {
                ComponentSection(title = "统计卡片") {
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
                                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
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
                }
            }

            // 评分组件展示
            item {
                var rating by remember { mutableFloatStateOf(4.5f) }
                
                ComponentSection(title = "评分组件") {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        HundiRatingCard(
                            rating = rating,
                            onRatingChanged = { rating = it },
                            title = "交互式评分"
                        )
                        
                        HundiCard {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "只读评分显示",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                
                                HundiRatingDisplay(
                                    rating = 3.5f,
                                    showText = true
                                )
                            }
                        }
                    }
                }
            }

            // 标签和进度展示
            item {
                ComponentSection(title = "标签和进度") {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 标签展示
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            HundiTag(text = "历史小说")
                            HundiTag(
                                text = "已完成",
                                backgroundColor = MaterialTheme.extendedColors.success.copy(alpha = 0.2f),
                                textColor = MaterialTheme.extendedColors.success
                            )
                            HundiTag(
                                text = "推荐",
                                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                                textColor = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        // 进度条展示
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "阅读进度",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            
                            HundiProgressIndicator(
                                progress = 0.75f,
                                modifier = Modifier.fillMaxWidth()
                            )
                            
                            Text(
                                text = "75% 完成",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.extendedColors.subtitleColor
                            )
                        }
                    }
                }
            }

            // 颜色展示
            item {
                ComponentSection(title = "配色方案") {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ColorSwatch("主色调", MaterialTheme.colorScheme.primary)
                        ColorSwatch("辅助色", MaterialTheme.colorScheme.secondary)
                        ColorSwatch("强调色", MaterialTheme.extendedColors.accentColor)
                        ColorSwatch("成功色", MaterialTheme.extendedColors.success)
                        ColorSwatch("警告色", MaterialTheme.extendedColors.warning)
                        ColorSwatch("错误色", MaterialTheme.extendedColors.error)
                    }
                }
            }
        }
    }
}

@Composable
private fun BookInterfaceTab() {
    val sampleBooks = remember {
        listOf(
            createSampleBookEntity("长安十二时辰", "马伯庸", ReadStatus.READING),
            createSampleBookEntity("燕食记", "葛亮", ReadStatus.FINISHED),
            createSampleBookEntity("食南之徒", "马伯庸", ReadStatus.WANT_TO_READ)
        )
    }
    
    HundiGradientBackground {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "书籍界面展示",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.extendedColors.titleColor,
                    fontWeight = FontWeight.Bold
                )
            }
            
            item {
                HundiPrimaryButton(
                    text = "添加新书籍",
                    onClick = { },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            items(sampleBooks.size) { index ->
                HundiBookCard(
                    book = sampleBooks[index],
                    onClick = { }
                )
            }
        }
    }
}

@Composable
private fun HomeStyleTab() {
    val sampleBooks = remember {
        listOf(
            createSampleBookEntity("长安十二时辰", "马伯庸", ReadStatus.READING),
            createSampleBookEntity("燕食记", "葛亮", ReadStatus.FINISHED)
        )
    }
    
    val stats = ReadingStats(
        monthlyBooks = 12,
        readingHours = 48,
        completionRate = 75,
        noteCount = 156
    )
    
    HundiHomeLayout(
        recentBooks = sampleBooks,
        readingStats = stats,
        onBookClick = { },
        onAddBookClick = { },
        onTimerClick = { },
        onStatisticsClick = { }
    )
}

@Composable
private fun ComponentSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.extendedColors.titleColor,
            fontWeight = FontWeight.SemiBold
        )
        
        HundiCard {
            Box(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun ColorSwatch(
    name: String,
    color: androidx.compose.ui.graphics.Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.extendedColors.bodyColor
        )
        
        Surface(
            modifier = Modifier.size(32.dp),
            shape = androidx.compose.foundation.shape.CircleShape,
            color = color
        ) {}
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