package com.anou.pagegather.ui.feature.bookshelf.booklist

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anou.pagegather.R
import com.anou.pagegather.data.local.entity.BookEntity
import com.anou.pagegather.ui.feature.bookshelf.BookListUIState
import com.anou.pagegather.ui.feature.bookshelf.BookListViewModel
import com.anou.pagegather.ui.feature.bookshelf.common.BookGridItem
import com.anou.pagegather.ui.feature.bookshelf.common.BookListItem
import com.anou.pagegather.ui.feature.bookshelf.common.DeleteBookConfirmDialog

@Composable
fun BookShelfDefaultBookListContent(
    bookListUIState: BookListUIState,
    onBookClick: (Long) -> Unit,
    onAddBookClick: () -> Unit,
    onTimerClick: (Long) -> Unit,
    viewModel: BookListViewModel,
    useLetterPlaceholderForGrid: Boolean = false, // 添加参数控制网格封面显示方式
    onNavigateToBookEdit: ((Long) -> Unit)? = null , // 添加导航到书籍编辑页面的回调函数
    onNavigateToNoteEdit: ((Long, Long) -> Unit)? = null // 添加导航到笔记编辑页面的回调函数，参数为(noteId, bookId)
) {
    val gridState = rememberLazyGridState()
    val listState = rememberLazyListState()
    val bookListState by viewModel.bookListState.collectAsState()
    val isGridMode = bookListState.isGridMode
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
                            BookGridItem(
                                book = book,
                                onClick = { onBookClick(book.id) },
                                onDeleteClick = { showDeleteDialog = book },
                                onEditClick = { 
                                    // 实现编辑功能，导航到书籍编辑页面
                                    onNavigateToBookEdit?.invoke(book.id)
                                },
                                onMarkAsFinishedClick = { viewModel.markBookAsFinished(book.id) },
                               
                                onPinClick = { viewModel.toggleBookPin(book.id) },
                                onAddNoteClick = { 
                                    // 实现记笔记功能，调用导航回调函数传递书籍ID
                                    onNavigateToNoteEdit?.invoke(0L, book.id)
                                },
                                onTimerClick = { bookId -> onTimerClick(bookId) },
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
                            BookListItem(
                                book = book,
                                onClick = { onBookClick(book.id) },
                                onDeleteClick = { showDeleteDialog = book },
                                onEditClick = { 
                                    // 实现编辑功能，导航到书籍编辑页面
                                    onNavigateToBookEdit?.invoke(book.id)
                                },
                                onMarkAsFinishedClick = { viewModel.markBookAsFinished(book.id) },
                               
                                onPinClick = { viewModel.toggleBookPin(book.id) },
                                onAddNoteClick = { 
                                    // 实现记笔记功能，调用导航回调函数传递书籍ID
                                    onNavigateToNoteEdit?.invoke(0L, book.id)
                                },
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
 
