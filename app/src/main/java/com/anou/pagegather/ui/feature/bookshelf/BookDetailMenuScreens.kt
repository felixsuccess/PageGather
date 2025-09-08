package com.anou.pagegather.ui.feature.bookshelf

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.anou.pagegather.data.local.entity.NoteEntity
import com.anou.pagegather.data.local.entity.ReadingRecordEntity
import com.anou.pagegather.data.model.BookReadingStatistics
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookReadingHistoryScreen(
    navController: NavController,
    bookId: Long,
    onNavigateToNoteEdit: (Long) -> Unit,
    onBackClick: () -> Unit
) {
    val viewModel: BookReadingHistoryViewModel = hiltViewModel()
    val readingRecords by viewModel.readingRecords.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    // 加载阅读记录
    LaunchedEffect(bookId) {
        viewModel.loadReadingRecords(bookId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("阅读历史") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                error != null -> {
                    Text(
                        text = "加载失败: ${error}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                readingRecords.isEmpty() -> {
                    Text(
                        text = "暂无阅读记录",
                        modifier = Modifier.padding(16.dp)
                    )
                }
                else -> {
                    // 直接显示阅读记录项
                    readingRecords.forEach { record ->
                        ReadingRecordItem(
                            record = record,
                            onDeleteClick = { viewModel.deleteReadingRecord(record) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookExcerptsScreen(
    navController: NavController,
    bookId: Long,
    onNavigateToNoteEdit: (Long) -> Unit,
    onBackClick: () -> Unit
) {
    val viewModel: BookExcerptsViewModel = hiltViewModel()
    val excerpts by viewModel.excerpts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    // 加载书摘
    LaunchedEffect(bookId) {
        viewModel.loadBookExcerpts(bookId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("书摘") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { 
                            // 导航到添加新书摘页面
                            onNavigateToNoteEdit(bookId)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "添加书摘"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                error != null -> {
                    Text(
                        text = "加载失败: ${error}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                excerpts.isEmpty() -> {
                    Text(
                        text = "暂无书摘",
                        modifier = Modifier.padding(16.dp)
                    )
                }
                else -> {
                    // 直接显示书摘项
                    excerpts.forEach { excerpt ->
                        ExcerptItem(
                            excerpt = excerpt,
                            onDeleteClick = { viewModel.deleteExcerpt(excerpt) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookReviewsScreen(
    navController: NavController,
    bookId: Long,
    onNavigateToNoteEdit: (Long) -> Unit,
    onBackClick: () -> Unit
) {
    val viewModel: BookReviewsViewModel = hiltViewModel()
    val reviews by viewModel.reviews.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    // 加载书评
    LaunchedEffect(bookId) {
        viewModel.loadBookReviews(bookId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("书评") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { 
                            // 导航到添加新书评页面
                            onNavigateToNoteEdit(bookId)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "添加书评"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                error != null -> {
                    Text(
                        text = "加载失败: ${error}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                reviews.isEmpty() -> {
                    Text(
                        text = "暂无书评",
                        modifier = Modifier.padding(16.dp)
                    )
                }
                else -> {
                    // 直接显示书评项
                    reviews.forEach { review ->
                        ReviewItem(
                            review = review,
                            onDeleteClick = { viewModel.deleteReview(review) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookRelatedDataScreen(
    navController: NavController,
    bookId: Long,
    onBackClick: () -> Unit
) {
    val viewModel: BookRelatedDataViewModel = hiltViewModel()
    val bookStatistics by viewModel.bookStatistics.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    // 加载书籍统计数据
    LaunchedEffect(bookId) {
        viewModel.loadBookStatistics(bookId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("相关数据") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                error != null -> {
                    Text(
                        text = "加载失败: ${error}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                bookStatistics != null -> {
                    RelatedDataContent(bookStatistics!!, navController, bookId)
                }
                else -> {
                    Text(
                        text = "暂无数据",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ReadingRecordItem(
    record: ReadingRecordEntity,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
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

@Composable
private fun ExcerptItem(
    excerpt: NoteEntity,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 顶部行：时间信息和删除按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatDateTime(excerpt.createdDate),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "删除书摘"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 书摘内容
            Text(
                text = excerpt.quote?.takeIf { it.isNotEmpty() } ?: "无内容",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            
            // 位置信息
//            excerpt.pageNumber?.let { pageNumber ->
//                if (pageNumber > 0) {
//                    Text(
//                        text = "位置: 第${pageNumber}页",
//                        style = MaterialTheme.typography.bodySmall,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant,
//                        modifier = Modifier.padding(vertical = 4.dp)
//                    )
//                }
//            }
        }
    }
}

@Composable
private fun ReviewItem(
    review: NoteEntity,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 顶部行：时间信息和删除按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatDateTime(review.createdDate),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "删除书评"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 书评内容
            Text(
                text = review.idea?.takeIf { it.isNotEmpty() } ?: "无内容",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            
//            // 评分信息
//            review.rating?.let { rating ->
//                if (rating > 0) {
//                    Text(
//                        text = "评分: ${rating} ★",
//                        style = MaterialTheme.typography.bodySmall,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant,
//                        modifier = Modifier.padding(vertical = 4.dp)
//                    )
//                }
//            }
        }
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
private fun RelatedDataContent(
    bookStatistics: BookReadingStatistics,
    navController: NavController,
    bookId: Long
) {
    // 统计卡片
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "阅读统计",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // 显示真实的统计数据
            Text(
                text = "总阅读时长: ${formatDuration(bookStatistics.totalReadingTime)}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            
            Text(
                text = "阅读次数: ${bookStatistics.readingRecordCount}次",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            
            Text(
                text = "平均每次阅读: ${formatDuration(bookStatistics.averageReadingTime)}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            
            bookStatistics.lastReadingTime?.let { lastTime ->
                Text(
                    text = "最后阅读: ${formatDateTime(lastTime)}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
            
            Text(
                text = "当前进度: ${String.format("%.1f", bookStatistics.readingProgress)}%",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
    
    Spacer(modifier = Modifier.height(16.dp))
    
    // 图表卡片
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "阅读进度趋势",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // 这里可以显示阅读进度的图表
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text(
                    text = "阅读进度图表占位符",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
    
    Spacer(modifier = Modifier.height(16.dp))
    
    // 操作按钮
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // 查看详细统计
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable { 
                        // 导航到书籍阅读统计页面
                        navController.navigate("reading/book_statistics/$bookId")
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "详细统计",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        // 查看图表分析
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable { 
                        // 导航到图表分析页面
                        navController.navigate("statistics/charts/$bookId")
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "图表分析",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

private fun formatDateTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

private fun formatDuration(milliseconds: Long): String {
    val seconds = milliseconds / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    
    return when {
        hours > 0 -> "${hours}小时${minutes % 60}分钟"
        minutes > 0 -> "${minutes}分钟${seconds % 60}秒"
        else -> "${seconds}秒"
    }
}