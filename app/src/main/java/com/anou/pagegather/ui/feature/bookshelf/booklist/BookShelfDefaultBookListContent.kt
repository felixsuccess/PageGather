package com.anou.pagegather.ui.feature.bookshelf.booklist

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.anou.pagegather.R
import com.anou.pagegather.data.local.entity.BookEntity
import com.anou.pagegather.ui.feature.bookshelf.BookListUIState
import com.anou.pagegather.ui.feature.bookshelf.BookListViewModel
import com.anou.pagegather.ui.theme.Accent

@Composable
fun BookShelfDefaultBookListContent(
    bookListUIState: BookListUIState,
    onBookClick: (Long) -> Unit,
    onAddBookClick: () -> Unit,
    onTimerClick: () -> Unit,
    viewModel: BookListViewModel,
    isGridMode: Boolean,
    useLetterPlaceholderForGrid: Boolean = false // 添加参数控制网格封面显示方式
) {
    val gridState = rememberLazyGridState()
    val listState = rememberLazyListState()
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf<BookEntity?>(null) }

    // 监听滚动位置，实现分页加载
    LaunchedEffect(gridState) {
        snapshotFlow {
            gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }.collect {
            if (it != null && bookListUIState is BookListUIState.Success) {
                // 当滚动到列表底部附近时，加载更多
                if (it >= bookListUIState.books.size - 6) {
                    // Check if we're in a Success state and not already loading more
                    if (!bookListUIState.isLoadingMore) {
                        viewModel.loadMoreBooks()
                    }
                }
            }
        }
    }

    // 监听列表模式下的滚动位置，实现分页加载
    LaunchedEffect(listState) {
        snapshotFlow {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }.collect {
            if (it != null && bookListUIState is BookListUIState.Success) {
                // 当滚动到列表底部附近时，加载更多
                if (it >= bookListUIState.books.size - 6) {
                    // Check if we're in a Success state and not already loading more
                    if (!bookListUIState.isLoadingMore) {
                        viewModel.loadMoreBooks()
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {

        when (bookListUIState) {
            is BookListUIState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is BookListUIState.Empty -> {

                EmptyBooksPlaceholder(
                    onAddBookClick = onAddBookClick,
                    isGridMode = isGridMode, // 使用当前显示模式
                    modifier = Modifier.fillMaxSize()
                )

            }

            is BookListUIState.Error -> {
                // 添加错误状态UI
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = bookListUIState.message)
                }
            }


            is BookListUIState.Success -> {
                if (isGridMode) {
                    // 网格模式
                    LazyVerticalGrid(
                        modifier = Modifier.fillMaxWidth(),
                        columns =  GRID_COLUMNS,
                        contentPadding = GRID_PADDING,
                        state = gridState,
                        verticalArrangement = Arrangement.spacedBy( GRID_SPACING),
                        horizontalArrangement = Arrangement.spacedBy( GRID_SPACING)
                    ) {
                        items(bookListUIState.books.size) { index -> 
                            val book = bookListUIState.books[index]
                            BookShelfDefaultBookGridItem(
                                book = book,
                                onItemClick = { onBookClick(book.id) },
                                onDeleteClick = { showDeleteDialog = book },
                                onEditClick = { /* TODO: 实现编辑功能 */ },
                                onMarkAsFinishedClick = { viewModel.markBookAsFinished(book.id) },
                                onAddToGroupClick = { /* TODO: 实现添加到分组功能 */ },
                                onPinClick = { viewModel.toggleBookPin(book.id) },
                                onAddNoteClick = { /* TODO: 实现记笔记功能 */ },
                                onTimerClick = onTimerClick,
                                useLetterPlaceholder = useLetterPlaceholderForGrid // 传递网格封面显示方式设置
                            )
                        }

                        // 加载更多指示器
                        if (bookListUIState.isLoadingMore) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                } else {
                    // 列表模式
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 12.dp),
                        state = listState,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(bookListUIState.books.size) { index ->
                            val book = bookListUIState.books[index]
                            // 在列表模式下使用更简洁的书籍项显示
                            BookShelfDefaultBookListItem(
                                book = book,
                                onItemClick = { onBookClick(book.id) },
                                onDeleteClick = { showDeleteDialog = book },
                                onEditClick = { /* TODO: 实现编辑功能 */ },
                                onMarkAsFinishedClick = { viewModel.markBookAsFinished(book.id) },
                                onAddToGroupClick = { /* TODO: 实现添加到分组功能 */ },
                                onPinClick = { viewModel.toggleBookPin(book.id) },
                                onAddNoteClick = { /* TODO: 实现记笔记功能 */ },
                                onTimerClick = onTimerClick
                            )
                        }

                        // 加载更多指示器
                        if (bookListUIState.isLoadingMore) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // 删除确认对话框
    showDeleteDialog?.let { book ->
        DeleteBookConfirmDialog(
            bookName = book.name ?: "未知书名",
            onConfirm = {
                viewModel.deleteBook(book) {
                    showDeleteDialog = null
                }
            },
            onDismiss = { showDeleteDialog = null }
        )
    }
}

/***
 * 书架默认展示-网格模式
 *
 */

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookShelfDefaultBookGridItem(
    book: BookEntity,
    onItemClick: () -> Unit,
    onDeleteClick: (() -> Unit)? = null,
    onEditClick: (() -> Unit)? = null,
    onMarkAsFinishedClick: (() -> Unit)? = null,
    onAddToGroupClick: (() -> Unit)? = null,
    onPinClick: (() -> Unit)? = null,
    onAddNoteClick: (() -> Unit)? = null,
    onTimerClick: (() -> Unit)? = null,
    useLetterPlaceholder: Boolean = false // 添加参数控制封面显示方式
) {
    var showDropdownMenu by remember { mutableStateOf(false) }

    // 调整为与分类详情页面一致的视觉风格
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClick)
            // 添加长按手势检测
            .combinedClickable(
                onClick = { /* 点击事件已在clickable上处理 */ },
                onLongClick = if (onDeleteClick != null) { 
                    { showDropdownMenu = true } 
                } else { 
                    null 
                }
            )
            .padding(8.dp), // 添加一些内边距
        horizontalAlignment = Alignment.Start // 左对齐，与微信读书一致
    ) {
        // 书籍封面 - 微信读书风格
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.72f) // 标准书籍比例
                .clip(RoundedCornerShape(1.dp)) // 微信读书封面几乎没有圆角
        ) {
            if (useLetterPlaceholder) {
                // 使用首字母占位符方式
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
            } else {
                // 使用默认图片加载方式
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current).data(book.coverUrl).apply {
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
                }
            }

            // 已读标志 - 微信读书在右上角显示标签
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

    // 长按菜单（仅在有删除功能时显示）
    if (onDeleteClick != null) {
        DropdownMenu(
            expanded = showDropdownMenu,
            onDismissRequest = { showDropdownMenu = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            // 编辑选项
            if (onEditClick != null) {
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "编辑",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp
                            )
                        }
                    },
                    onClick = {
                        showDropdownMenu = false
                        onEditClick()
                    }
                )
            }

            // 标记为已完成选项
            if (onMarkAsFinishedClick != null) {
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "标记为已完成",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp
                            )
                        }
                    },
                    onClick = {
                        showDropdownMenu = false
                        onMarkAsFinishedClick()
                    }
                )
            }

            // 添加到分组选项
            if (onAddToGroupClick != null) {
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "添加到分组",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp
                            )
                        }
                    },
                    onClick = {
                        showDropdownMenu = false
                        onAddToGroupClick()
                    }
                )
            }

            // 置顶选项
            if (onPinClick != null) {
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PushPin,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (book.pinned) "取消置顶" else "置顶",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp
                            )
                        }
                    },
                    onClick = {
                        showDropdownMenu = false
                        onPinClick()
                    }
                )
            }

            // 记笔记选项
            if (onAddNoteClick != null) {
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.NoteAdd,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "记笔记",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp
                            )
                        }
                    },
                    onClick = {
                        showDropdownMenu = false
                        onAddNoteClick()
                    }
                )
            }

            // 阅读计时选项
            if (onTimerClick != null) {
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "阅读计时",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp
                            )
                        }
                    },
                    onClick = {
                        showDropdownMenu = false
                        onTimerClick()
                    }
                )
            }

            DropdownMenuItem(
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "删除",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp
                        )
                    }
                },
                onClick = {
                    showDropdownMenu = false
                    onDeleteClick()
                }
            )
        }
    }
}

/***
 * 书架默认展示-列表模式
 *
 */


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookShelfDefaultBookListItem(
    book: BookEntity,
    onItemClick: () -> Unit,
    onDeleteClick: (() -> Unit)? = null,
    onEditClick: (() -> Unit)? = null,
    onMarkAsFinishedClick: (() -> Unit)? = null,
    onAddToGroupClick: (() -> Unit)? = null,
    onPinClick: (() -> Unit)? = null,
    onAddNoteClick: (() -> Unit)? = null,
    onTimerClick: (() -> Unit)? = null
) {
    var showDropdownMenu by remember { mutableStateOf(false) }

    // 列表模式的书籍项设计 - 调整为与分类详情页面一致的视觉风格
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .combinedClickable(
                onClick = onItemClick,
                onLongClick = if (onDeleteClick != null) { 
                    { showDropdownMenu = true } 
                } else { 
                    null 
                }
            ),
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
                // 左侧书籍封面 - 与分类详情页面保持一致
                Box(
                    modifier = Modifier
                        .width(65.dp)
                        .aspectRatio(0.72f) // 与微信读书封面比例一致
                        .clip(RoundedCornerShape(1.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current).data(book.coverUrl).apply {
                            Log.d("BookListScreen", "正在加载封面URL: ${book.coverUrl}")
                        }.crossfade(true).build(),
                        contentDescription = null,
                        error = painterResource(id = R.mipmap.default_cover),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                        onSuccess = {
                            Log.i("BookListScreen", "封面加载成功: ${book.coverUrl}")
                        },
                        onError = { result ->
                            Log.e("BookListScreen", "封面加载失败: ${result.result.throwable}")
                        }
                    )
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

                    // 评分信息 - 保留原有功能
                    if (book.rating > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // 星级评分显示
                            for (i in 1..5) {
                                Icon(
                                    imageVector = if (i <= book.rating) Icons.Default.Star else Icons.Default.StarOutline,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = if (i <= book.rating) Accent else MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                        alpha = 0.4f
                                    )
                                )
                            }
                            Text(
                                text = "${book.rating}.0",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // 日期信息，单独放在底部右侧 - 与分类详情页面保持一致
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
                textAlign = TextAlign.End
            )
        }
        
        // 下拉菜单 - 仅在有删除功能时显示
        if (onDeleteClick != null) {
            DropdownMenu(
                expanded = showDropdownMenu,
                onDismissRequest = { showDropdownMenu = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
                // 编辑选项
                if (onEditClick != null) {
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "编辑",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 14.sp
                                )
                            }
                        },
                        onClick = {
                            showDropdownMenu = false
                            onEditClick()
                        }
                    )
                }

                // 标记为已完成选项
                if (onMarkAsFinishedClick != null) {
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "标记为已完成",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 14.sp
                                )
                            }
                        },
                        onClick = {
                            showDropdownMenu = false
                            onMarkAsFinishedClick()
                        }
                    )
                }

                // 添加到分组选项
                if (onAddToGroupClick != null) {
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Folder,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "添加到分组",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 14.sp
                                )
                            }
                        },
                        onClick = {
                            showDropdownMenu = false
                            onAddToGroupClick()
                        }
                    )
                }

                // 置顶选项
                if (onPinClick != null) {
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PushPin,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = if (book.pinned) "取消置顶" else "置顶",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 14.sp
                                )
                            }
                        },
                        onClick = {
                            showDropdownMenu = false
                            onPinClick()
                        }
                    )
                }

                // 记笔记选项
                if (onAddNoteClick != null) {
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.NoteAdd,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "记笔记",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 14.sp
                                )
                            }
                        },
                        onClick = {
                            showDropdownMenu = false
                            onAddNoteClick()
                        }
                    )
                }

                // 阅读计时选项
                if (onTimerClick != null) {
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "阅读计时",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 14.sp
                                )
                            }
                        },
                        onClick = {
                            showDropdownMenu = false
                            onTimerClick()
                        }
                    )
                }

                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "删除",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 14.sp
                            )
                        }
                    },
                    onClick = {
                        showDropdownMenu = false
                        onDeleteClick()
                    }
                )
            }
        }
    }
}

/**
 * 空书籍占位符
 * 支持两种模式：
 * 1. 网格模式：使用原来的图片(R.mipmap.empty)作为背景 + MenuBook图标作为前景
 * 2. 列表模式：仅使用MenuBook图标，简洁的设计
 *
 * 使用示例：
 * ```kotlin
 * // 网格模式 (传统书架布局)
 * EmptyBooksPlaceholder(
 *     onAddBookClick = { /* ... */ },
 *     isGridMode = true
 * )
 *
 * // 列表模式 (搜索结果或筛选结果)
 * EmptyBooksPlaceholder(
 *     onAddBookClick = { /* ... */ },
 *     isGridMode = false
 * )
 * ```
 *
 * @param onAddBookClick 添加书籍点击事件
 * @param isGridMode 是否为网格模式，true: 网格模式(使用图片), false: 列表模式(使用图标)
 * @param modifier 修饰符
 */
@Composable
private fun EmptyBooksPlaceholder(
    modifier: Modifier = Modifier,
    onAddBookClick: () -> Unit,
    isGridMode: Boolean = true,

    ) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isGridMode) {
                // 网格模式：使用原来的图片 + MenuBook图标组合
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(120.dp)
                ) {
                    // 背景图片
                    Image(
                        painter = painterResource(id = R.mipmap.empty),
                        contentDescription = "空列表",
                        modifier = Modifier
                            .size(100.dp)
                            .alpha(0.7f)
                    )

                    // 前景图标
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.MenuBook,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                }
            } else {
                // 列表模式：仅使用MenuBook图标
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.MenuBook,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "暂无书籍",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 18.sp
            )

            Text(
                text = if (isGridMode) "开始你的阅读之旅吧" else "点击右上角按钮添加书籍",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onAddBookClick,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("添加书籍", fontSize = 14.sp)
            }
        }
    }
}

/**
 * 删除书籍确认对话框
 */
@Composable
fun DeleteBookConfirmDialog(
    bookName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "确认删除",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Text(
                text = "确定要删除书籍 \"》$bookName《\" 吗？\n\n此操作将永久删除该书籍及其所有相关信息（包括阅读记录、笔记等）。",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            androidx.compose.material3.Button(
                onClick = onConfirm,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("删除")
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("取消")
            }
        },
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 6.dp
    )
}

