package com.anou.pagegather.ui.feature.bookshelf.booksource

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.anou.pagegather.R
import com.anou.pagegather.data.local.entity.BookEntity
import com.anou.pagegather.ui.feature.bookshelf.BookListViewModel
import com.anou.pagegather.ui.feature.bookshelf.common.BookGridItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookShelfSourceDetailScreen(
    sourceId: Long,
    sourceName: String,
    viewModel: BookListViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onBookClick: (Long) -> Unit
) {
    val books by viewModel.getBooksBySourceId(sourceId).collectAsState(initial = emptyList())
    var isGridMode by remember { mutableStateOf(true) }
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // 显示具体来源名称和书籍数量
                    Column {
                        Text(
                            text = sourceName,
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
                                text = { Text("来源管理") },
                                onClick = {
                                    showMenu = false
                                    // TODO: 实现来源管理功能
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
                            imageVector = Icons.AutoMirrored.Outlined.MenuBook,
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
                            text = "添加书籍到这个来源",
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
                            BookGridItem(
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
                            SourceBookListItem(
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
 * 微信读书样式的来源书籍列表项
 */
@Composable
fun SourceBookListItem(
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
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current).data(book.coverUrl)
                            .apply {
                                Log.d("BookListScreen", "正在加载封面URL: ${book.coverUrl}")
                            }.crossfade(true).build(),
                        contentDescription = null,
                        error = painterResource(id = R.mipmap.default_cover),
                        modifier = Modifier.fillMaxSize(),
                        // 修改contentScale以保持图片的原始宽高比
                        contentScale = ContentScale.Fit,
                        onSuccess = {
                            Log.i("BookListScreen", "封面加载成功: ${book.coverUrl}")
                        },
                        onError = { result -> 
                            Log.e("BookListScreen", "封面加载失败: ${result.result.throwable}")
                        }
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
                        text = book.name ?: "",
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
                val date = SimpleDateFormat("yyyy 年 MM 月 dd 日", Locale.getDefault())
                    .format(Date(book.updatedDate))
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
                textAlign = TextAlign.End
            )
        }
    }
}

