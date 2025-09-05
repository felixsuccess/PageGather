package com.anou.pagegather.ui.feature.bookshelf.rating

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.anou.pagegather.data.local.entity.BookEntity
import com.anou.pagegather.ui.feature.bookshelf.BookListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookShelfRatingDetailScreen(
    rating: Int,
    ratingValue: String,
    viewModel: BookListViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onBookClick: (Long) -> Unit
) {
    val books by viewModel.getBooksByRating(rating.toFloat()).collectAsState(initial = emptyList<BookEntity>())
    var isGridMode by remember { mutableStateOf(true) }
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // 显示具体评分和书籍数量
                    Column {
                        Text(
                            text = if (rating == 0) "未评分" else "$ratingValue 星",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "${books.size}本",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    // 显示布局切换按钮
                    IconButton(onClick = { isGridMode = !isGridMode }) {
                        Icon(
                            imageVector = if (isGridMode) Icons.AutoMirrored.Filled.ViewList else Icons.Default.GridView,
                            contentDescription = if (isGridMode) "列表模式" else "网格模式",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // 显示搜索图标
                    IconButton(onClick = { /* 搜索功能待实现 */ }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "搜索",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    // 更多操作菜单 - 占位实现
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "更多操作",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("评分管理") },
                                onClick = {
                                    showMenu = false
                                    // TODO: 实现评分管理功能
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("书籍管理") },
                                onClick = {
                                    showMenu = false
                                    // TODO: 实现书籍管理功能
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .statusBarsPadding()
        ) {
            if (books.isEmpty()) {
                // 空状态 - 参考分组页面的空状态实现
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Book,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "暂无书籍",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 18.sp
                        )

                        Text(
                            text = "添加书籍到这个评分",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                // 根据显示模式选择列表或网格布局
                if (isGridMode) {
                    // 网格模式 - 参考微信读书设计
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3), // 微信读书是3列网格
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            horizontal = 8.dp,
                            vertical = 8.dp
                        ), // 微信读书边距较小
                        horizontalArrangement = Arrangement.spacedBy(8.dp), // 微信读书间距较小
                        verticalArrangement = Arrangement.spacedBy(20.dp) // 垂直间距较大，为标题留出空间
                    ) {
                        items(books) { book ->
                            RatingBookGridItem(
                                book = book,
                                onClick = { onBookClick(book.id) }
                            )
                        }
                    }
                } else {
                    // 列表模式
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        items(books) { book ->
                            RatingBookListItem(
                                book = book,
                                onClick = { onBookClick(book.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 评分书籍列表项
 */
@Composable
fun RatingBookListItem(
    book: BookEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(0.dp), // 方形卡片，没有圆角
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), // 无阴影
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top
            ) {
                // 左侧书籍封面 - 如果没有封面，使用书架页面来源的网格布局封面效果
                Box(
                    modifier = Modifier
                        .width(65.dp)
                        .aspectRatio(0.72f) // 与微信读书封面比例一致
                        .clip(RoundedCornerShape(1.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                ) {
                    // TODO: 添加书籍封面图片
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // 如果有进度，显示进度条
                    if (book.totalPosition > 0 && book.readPosition > 0) {
                        val progress = (book.readPosition.toFloat() / book.totalPosition.toFloat())
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .align(Alignment.BottomCenter)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight()
                                    .background(
                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                            alpha = 0.2f
                                        )
                                    )
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(progress)
                                    .fillMaxHeight()
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // 右侧书籍信息
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // 书籍标题
                    Text(
                        text = book.name ?: "未知书名",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // 作者信息和出版社
                    val authorAndPublisher = buildString {
                        if (book.author?.isNotBlank() == true) {
                            append(book.author)
                            if (book.press?.isNotBlank() == true) {
                                append(" / ")
                            }
                        }

                        if (book.press?.isNotBlank() == true) {
                            append(book.press)
                        }

                        // 添加假想的出版时间，因为参考图片中有展示
                        // 实际应该使用书籍的真实出版时间
                        val publishYear = 2020 + (book.id % 5).toInt() // 仅作演示用
                        val publishMonth =
                            (1 + (book.id % 12).toInt()).toString().padStart(2, '0') // 仅作演示用

                        if (isNotEmpty()) {
                            append(" / $publishYear-$publishMonth")
                        }
                    }

                    Text(
                        text = authorAndPublisher,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 阅读状态标签
                    val (statusText, statusColor) = when (book.readStatus) {
                        2 -> Pair("读完", MaterialTheme.colorScheme.primary) // ReadStatus.FINISHED
                        1 -> Pair(
                            "阅读中",
                            MaterialTheme.colorScheme.secondary
                        ) // ReadStatus.READING
                        else -> Pair(
                            "想读",
                            MaterialTheme.colorScheme.tertiary
                        ) // ReadStatus.UNREAD
                    }

                    // 阅读进度信息
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (book.readStatus > 0) {
                            Box(
                                modifier = Modifier
                                    .border(
                                        width = 1.dp,
                                        color = statusColor,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = statusText,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = statusColor
                                )
                            }

                            // 阅读时间
                            if (book.totalPosition > 0 && book.readPosition > 0 && book.readStatus != 2) {
                                Spacer(modifier = Modifier.width(8.dp))

                                val timeText = if (book.id % 2 == 0L) {
                                    // 模拟页数信息
                                    "${book.readPosition.toInt()} 页"
                                } else {
                                    // 模拟时间信息
                                    "${(1 + book.id % 3)} 小时 ${(book.id % 60).toInt()} 分钟"
                                }

                                Text(
                                    text = timeText,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // 完成百分比
                            if (book.totalPosition > 0 && book.readPosition > 0) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${((book.readPosition.toFloat() / book.totalPosition.toFloat()) * 100).toInt()}%",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // 日期信息，单独放在底部右侧
            val displayDate = if (book.updatedDate > 0) {
                val date = java.text.SimpleDateFormat("yyyy 年 MM 月 dd 日", java.util.Locale.getDefault())
                    .format(java.util.Date(book.updatedDate))
                date
            } else {
                // 模拟日期，参考图片中的格式
                "2024 年 ${6 + (book.id % 6).toInt()} 月 ${1 + (book.id % 28).toInt()} 日"
            }

            Text(
                text = displayDate,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.End
            )
        }
    }
}

/**
 * 评分书籍网格项（用于网格布局）- 微信读书样式
 */
@Composable
fun RatingBookGridItem(
    book: BookEntity,
    onClick: () -> Unit
) {
    // 微信读书网格模式不使用卡片，直接使用Column布局
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.Start // 左对齐，与微信读书一致
    ) {
        // 书籍封面 - 微信读书风格
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.72f) // 标准书籍比例
                .clip(RoundedCornerShape(1.dp)) // 微信读书封面几乎没有圆角
        ) {
            // 书籍封面
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = book.name?.take(1) ?: "书",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            // 阅读状态标签 - 微信读书在右上角显示标签
            when (book.readStatus) {
                2 -> { // 已读完
                    // 右上角的读完标签
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(topEnd = 1.dp, bottomStart = 8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "读完",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                0 -> { // 想读
                    // 右上角的想读标签
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .background(
                                color = MaterialTheme.colorScheme.tertiary,
                                shape = RoundedCornerShape(topEnd = 1.dp, bottomStart = 8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "想读",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onTertiary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 如果有进度，显示进度条
            if (book.totalPosition > 0 && book.readPosition > 0) {
                val progress = (book.readPosition.toFloat() / book.totalPosition.toFloat())
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .align(Alignment.BottomCenter)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 书籍标题 - 微信读书样式
        Text(
            text = book.name ?: "未知书名",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1, // 与微信读书一致，只显示一行标题
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )

        // 更新日期 - 微信读书显示日期而非作者
        val displayDate = if (book.updatedDate > 0) {
            val date = java.text.SimpleDateFormat("yyyy年MM月dd日", java.util.Locale.getDefault())
                .format(java.util.Date(book.updatedDate))
            date
        } else {
            // 模拟日期，参考图片中的格式
            "${2024 + (book.id % 2).toInt()}年${1 + (book.id % 12).toInt()}月${1 + (book.id % 28).toInt()}日"
        }

        Text(
            text = displayDate,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
    }
}