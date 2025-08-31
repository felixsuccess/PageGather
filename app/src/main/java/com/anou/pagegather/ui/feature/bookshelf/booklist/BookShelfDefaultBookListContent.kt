package com.anou.pagegather.ui.feature.bookshelf.booklist

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.anou.pagegather.R
import com.anou.pagegather.data.local.entity.BookEntity
import com.anou.pagegather.data.local.entity.ReadStatus
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
    isGridMode: Boolean
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
        modifier = Modifier.Companion.fillMaxWidth(),
    ) {

        when (bookListUIState) {
            is BookListUIState.Loading -> {
                Box(
                    modifier = Modifier.Companion.fillMaxSize(),
                    contentAlignment = Alignment.Companion.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is BookListUIState.Empty -> {

                EmptyBooksPlaceholder(
                    onAddBookClick = onAddBookClick,
                    isGridMode = isGridMode, // 使用当前显示模式
                    modifier = Modifier.Companion.fillMaxSize()
                )

            }

            is BookListUIState.Error -> {
                // 添加错误状态UI
                Box(
                    modifier = Modifier.Companion.fillMaxSize(),
                    contentAlignment = Alignment.Companion.Center
                ) {
                    Text(text = bookListUIState.message)
                }
            }


            is BookListUIState.Success -> {
                if (isGridMode) {
                    // 网格模式
                    LazyVerticalGrid(
                        modifier = Modifier.Companion.fillMaxWidth(),
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
                                onTimerClick = onTimerClick
                            )
                        }

                        // 加载更多指示器
                        if (bookListUIState.isLoadingMore) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Box(
                                    modifier = Modifier.Companion
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Companion.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                } else {
                    // 列表模式
                    LazyColumn(
                        modifier = Modifier.Companion.fillMaxWidth(),
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
                                    modifier = Modifier.Companion
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Companion.Center
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
    onTimerClick: (() -> Unit)? = null
) {
    var showDropdownMenu by remember { mutableStateOf(false) }

    // 淡雅风格的书籍卡片设计
    Column(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            // 添加长按手势检测
            .combinedClickable(
                onClick = { onItemClick() },
                onLongClick = { showDropdownMenu = true }
            )
    ) {
        Box(
            modifier = Modifier.Companion
                .fillMaxWidth()
                .aspectRatio(7f / 10f)
        ) {
            // 封面图片容器，使用更柔和的圆角
            Box(
                modifier = Modifier.Companion
                    .fillMaxSize()
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(book.coverUrl).apply {
                        Log.d("BookListScreen", "正在加载封面URL: ${book.coverUrl}")
                    }.crossfade(true).build(),
                    contentDescription = null,
                    error = painterResource(id = R.mipmap.default_cover),
                    modifier = Modifier.Companion.fillMaxSize(),
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

            // 已读标志 - 淡雅风格设计
            if (book.readStatus == ReadStatus.FINISHED.ordinal) {
                Box(
                    modifier = Modifier.Companion
                        .align(Alignment.Companion.TopEnd)
                        .clip(
                            RoundedCornerShape(
                                topStart = 0.dp, topEnd = 8.dp, bottomStart = 8.dp, bottomEnd = 0.dp
                            )
                        )
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))
                ) {
                    Text(
                        text = "已读",
                        modifier = Modifier.Companion.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Companion.Medium
                    )
                }
            }

            // 移除左上角操作按钮，改为长按触发菜单
        }

        // 书籍信息区域，使用更优雅的排版
        Column(
            modifier = Modifier.Companion
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = book.name ?: "未知书名",
                maxLines = 1,
                overflow = TextOverflow.Companion.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Companion.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp
            )

            Text(
                text = book.author ?: "未知作者",
                maxLines = 1,
                overflow = TextOverflow.Companion.Ellipsis,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )

            // 添加更多书籍信息，如评分、阅读进度等
            if (book.rating > 0) {
                Row(
                    verticalAlignment = Alignment.Companion.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // 星级评分显示
                    for (i in 1..5) {
                        Icon(
                            imageVector = if (i <= book.rating) Icons.Default.Star else Icons.Default.StarOutline,
                            contentDescription = null,
                            modifier = Modifier.Companion.size(12.dp),
                            tint = if (i <= book.rating) Accent else MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                alpha = 0.4f
                            )
                        )
                    }
                    Text(
                        text = "${book.rating}.0",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }

    // 长按菜单（仅在有删除功能时显示）
    if (onDeleteClick != null) {
        DropdownMenu(
            expanded = showDropdownMenu,
            onDismissRequest = { showDropdownMenu = false },
            modifier = Modifier.Companion.background(MaterialTheme.colorScheme.surface)
        ) {
            // 编辑选项
            if (onEditClick != null) {
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.Companion.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.Companion.size(18.dp)
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
                            verticalAlignment = Alignment.Companion.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.Companion.size(18.dp)
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
                            verticalAlignment = Alignment.Companion.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.Companion.size(18.dp)
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
                            verticalAlignment = Alignment.Companion.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PushPin,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.Companion.size(18.dp)
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
                            verticalAlignment = Alignment.Companion.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.NoteAdd,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.Companion.size(18.dp)
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
                            verticalAlignment = Alignment.Companion.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.Companion.size(18.dp)
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

            // 删除选项
            DropdownMenuItem(
                text = {
                    Row(
                        verticalAlignment = Alignment.Companion.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.Companion.size(18.dp)
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

    // 列表模式的书籍项设计
    Row(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            // 添加长按手势检测
            .combinedClickable(
                onClick = { onItemClick() },
                onLongClick = { showDropdownMenu = true }
            )
            .padding(16.dp),
        verticalAlignment = Alignment.Companion.CenterVertically
    ) {
        // 封面图片
        Box(
            modifier = Modifier.Companion
                .size(60.dp)
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(book.coverUrl).apply {
                    Log.d("BookListScreen", "正在加载封面URL: ${book.coverUrl}")
                }.crossfade(true).build(),
                contentDescription = null,
                error = painterResource(id = R.mipmap.default_cover),
                modifier = Modifier.Companion.fillMaxSize(),
                contentScale = ContentScale.Fit,
                onSuccess = {
                    Log.i("BookListScreen", "封面加载成功: ${book.coverUrl}")
                },
                onError = { result ->
                    Log.e("BookListScreen", "封面加载失败: ${result.result.throwable}")
                }
            )
        }

        Spacer(modifier = Modifier.Companion.width(16.dp))

        // 书籍信息
        Column(
            modifier = Modifier.Companion.weight(1f)
        ) {
            Text(
                text = book.name ?: "未知书名",
                maxLines = 1,
                overflow = TextOverflow.Companion.Ellipsis,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Companion.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.Companion.height(4.dp))

            Text(
                text = book.author ?: "未知作者",
                maxLines = 1,
                overflow = TextOverflow.Companion.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )

            // 添加更多书籍信息，如评分、阅读进度等
            if (book.rating > 0) {
                Spacer(modifier = Modifier.Companion.height(4.dp))

                Row(
                    verticalAlignment = Alignment.Companion.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // 星级评分显示
                    for (i in 1..5) {
                        Icon(
                            imageVector = if (i <= book.rating) Icons.Default.Star else Icons.Default.StarOutline,
                            contentDescription = null,
                            modifier = Modifier.Companion.size(14.dp),
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

        // 右侧操作按钮（仅在有删除功能时显示）
        if (onDeleteClick != null) {
            Box {
                IconButton(
                    onClick = { showDropdownMenu = true },
                    modifier = Modifier.Companion.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "更多操作",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // 下拉菜单
                DropdownMenu(
                    expanded = showDropdownMenu,
                    onDismissRequest = { showDropdownMenu = false },
                    modifier = Modifier.Companion.background(MaterialTheme.colorScheme.surface)
                ) {
                    // 编辑选项
                    if (onEditClick != null) {
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.Companion.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.Companion.size(18.dp)
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
                                    verticalAlignment = Alignment.Companion.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.Companion.size(18.dp)
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
                                    verticalAlignment = Alignment.Companion.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Folder,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.Companion.size(18.dp)
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
                                    verticalAlignment = Alignment.Companion.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PushPin,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.Companion.size(18.dp)
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
                                    verticalAlignment = Alignment.Companion.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NoteAdd,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.Companion.size(18.dp)
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
                                    verticalAlignment = Alignment.Companion.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.Companion.size(18.dp)
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
                                verticalAlignment = Alignment.Companion.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.Companion.size(18.dp)
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
    modifier: Modifier = Modifier.Companion,
    onAddBookClick: () -> Unit,
    isGridMode: Boolean = true,

    ) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Companion.Center
    ) {
        Column(
            horizontalAlignment = Alignment.Companion.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isGridMode) {
                // 网格模式：使用原来的图片 + MenuBook图标组合
                Box(
                    contentAlignment = Alignment.Companion.Center,
                    modifier = Modifier.Companion.size(120.dp)
                ) {
                    // 背景图片
                    Image(
                        painter = painterResource(id = R.mipmap.empty),
                        contentDescription = "空列表",
                        modifier = Modifier.Companion
                            .size(100.dp)
                            .alpha(0.7f)
                    )

                    // 前景图标
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.MenuBook,
                        contentDescription = null,
                        modifier = Modifier.Companion.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                }
            } else {
                // 列表模式：仅使用MenuBook图标
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.MenuBook,
                    contentDescription = null,
                    modifier = Modifier.Companion.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.Companion.height(16.dp))

            Text(
                text = "暂无书籍",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Companion.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 18.sp
            )

            Text(
                text = if (isGridMode) "开始你的阅读之旅吧" else "点击右上角按钮添加书籍",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.Companion.height(20.dp))

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
                    modifier = Modifier.Companion.size(18.dp)
                )
                Spacer(modifier = Modifier.Companion.width(8.dp))
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

