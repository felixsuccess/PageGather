package com.anou.pagegather.ui.feature.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anou.pagegather.ui.components.*
import com.anou.pagegather.ui.theme.PageGatherTheme
import com.anou.pagegather.data.local.entity.BookEntity
import com.anou.pagegather.data.local.entity.BookType
import com.anou.pagegather.data.local.entity.ReadPositionUnit
import com.anou.pagegather.data.local.entity.ReadStatus
import com.anou.pagegather.data.local.entity.*

/**
 * Hundi 风格预览 Activity
 * 用于展示和测试新的设计风格
 */
class HundiStylePreviewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            PageGatherTheme {
                HundiStylePreviewScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HundiStylePreviewScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("主页风格", "书籍卡片", "组件展示")
    
    Column(modifier = Modifier.fillMaxSize()) {
        // 顶部标签栏
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
            0 -> HomeStylePreview()
            1 -> BookCardPreview()
            2 -> ComponentPreview()
        }
    }
}

@Composable
private fun HomeStylePreview() {
    val sampleBooks = listOf(
        createSampleBook("长安十二时辰", "马伯庸", ReadStatus.READING, 65f),
        createSampleBook("燕食记", "葛亮", ReadStatus.FINISHED, 100f),
        createSampleBook("食南之徒", "马伯庸", ReadStatus.WANT_TO_READ, 0f),
        createSampleBook("千里江山图", "孙甘露", ReadStatus.READING, 30f)
    )
    
    val stats = ReadingStats(
        monthlyBooks = 12,
        readingHours = 48,
        completionRate = 75,
        noteCount = 156
    )
    
    HundiHomeLayout(
        recentBooks = sampleBooks,
        readingStats = stats,
        onBookClick = { /* 处理书籍点击 */ },
        onAddBookClick = { /* 添加书籍 */ },
        onTimerClick = { /* 开始计时 */ },
        onStatisticsClick = { /* 查看统计 */ }
    )
}

@Composable
private fun BookCardPreview() {
    val sampleBooks = listOf(
        createSampleBook("长安十二时辰", "马伯庸", ReadStatus.READING, 65f, 4.5f),
        createSampleBook("燕食记", "葛亮", ReadStatus.FINISHED, 100f, 4.8f),
        createSampleBook("食南之徒", "马伯庸", ReadStatus.WANT_TO_READ, 0f),
        createSampleBook("千里江山图", "孙甘露", ReadStatus.ABANDONED, 25f, 3.5f)
    )
    
    HundiGradientBackground {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "书籍列表卡片",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            
            items(sampleBooks.size) { index ->
                HundiBookCard(
                    book = sampleBooks[index],
                    onClick = { /* 点击处理 */ },
                    onLongClick = { /* 长按处理 */ }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "书籍网格卡片",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    sampleBooks.take(2).forEach { book ->
                        HundiBookGridCard(
                            book = book,
                            onClick = { /* 点击处理 */ },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ComponentPreview() {
    HundiStyleDemoScreen()
}

/**
 * 创建示例书籍数据
 */
private fun createSampleBook(
    title: String,
    author: String,
    status: ReadStatus,
    progress: Float,
    rating: Float = 0f
): BookEntity {
    return BookEntity(
        id = 0,
        name = title,
        author = author,
        isbn = "",
        coverUrl = null,
        press = "示例出版社",
        publishDate = null,
        language = "中文",
        summary = "这是一本示例书籍的描述...",
        type = BookType.PAPER_BOOK.code,
        readStatus = status.code,
        positionUnit = ReadPositionUnit.PAGE.code,
        totalPosition = 400,
        totalPagination = 400,
        totalChapters = null,
        readPosition = (progress * 4).toDouble(), // 假设总共400页
        rating = rating,
        review = null,
        bookSourceId = 1,
        createdDate = System.currentTimeMillis(),
        startReadingDate = if (status != ReadStatus.WANT_TO_READ) System.currentTimeMillis() - 86400000 else null,
        finishedDate = if (status == ReadStatus.FINISHED) System.currentTimeMillis() else null,
        lastReadDate = if (status == ReadStatus.READING) System.currentTimeMillis() - 3600000 else null,
        customSortOrder = 0
    )
}