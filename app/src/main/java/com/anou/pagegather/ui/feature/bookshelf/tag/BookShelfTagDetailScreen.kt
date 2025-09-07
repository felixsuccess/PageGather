package com.anou.pagegather.ui.feature.bookshelf.tag

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.anou.pagegather.data.local.entity.BookEntity
import com.anou.pagegather.ui.feature.bookshelf.BookListViewModel
import com.anou.pagegather.ui.feature.bookshelf.common.BookGridItem
import com.anou.pagegather.ui.feature.bookshelf.common.BookListItem
import com.anou.pagegather.ui.feature.bookshelf.common.DeleteBookConfirmDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookShelfTagDetailScreen(
    tagId: Long,
    tagName: String,
    tagColor: String?,
    onBackClick: () -> Unit,
    onBookClick: (Long) -> Unit,
    viewModel: BookListViewModel = hiltViewModel(), // 通过参数传递 ViewModel 实例
    onNavigateToBookEdit: ((Long) -> Unit)? = null,  // 添加导航到书籍编辑页面的回调函数
    onNavigateToTimer: ((Long) -> Unit)? = null,  // 添加导航到计时器页面的回调函数
    onNavigateToNoteEdit: ((Long) -> Unit)? = null  // 添加导航到笔记编辑页面的回调函数
) {
    // 根据tagId确定数据源
    val books by if (tagId == -1L) {
        // 特殊处理：未设置标签的书籍
        viewModel.getUntaggedBooks().collectAsState(initial = emptyList())
    } else {
        // 正常标签的书籍
        viewModel.getBooksWithTag(tagId).collectAsState(initial = emptyList())
    }
    
    // 使用ViewModel中的实时显示模式状态
    val bookListState by viewModel.bookListState.collectAsState()
    val isGridMode = bookListState.isGridMode
    
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<BookEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // 显示具体标签名称和书籍数量
                    Column {
                        Text(
                            text = tagName,
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
                    IconButton(onClick = {
                        // 在返回时调用onBackClick回调
                        onBackClick()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    // 显示布局切换按钮，调用ViewModel的方法来切换显示模式
                    IconButton(onClick = { viewModel.toggleDisplayMode() }) {
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
                                text = { Text("标签管理") },
                                onClick = {
                                    showMenu = false
                                    // TODO: 实现标签管理功能
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
                // 
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
                            text = if (tagId == -1L) "暂无未设置标签的书籍" else "添加书籍到这个标签",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                // 根据显示模式选择列表或网格布局
                if (isGridMode) {
                    
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3), 
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            horizontal = 8.dp,
                            vertical = 8.dp
                        ), 
                        horizontalArrangement = Arrangement.spacedBy(8.dp), 
                        verticalArrangement = Arrangement.spacedBy(20.dp) 
                    ) {
                        items(books) { book ->
                            BookGridItem(
                                book = book,
                                onClick = { onBookClick(book.id) },
                                // 添加长按菜单相关回调函数
                                onDeleteClick = {
                                    showDeleteDialog = book
                                },
                                onEditClick = {
                                    // 实现编辑功能
                                    onNavigateToBookEdit?.invoke(book.id)
                                },
                                onMarkAsFinishedClick = {
                                    viewModel.markBookAsFinished(book.id)
                                },
                              
                                onPinClick = {
                                    // TODO: 实现置顶功能
                                },
                                onAddNoteClick = {
                                    // 导航到笔记编辑页面，并传递书籍ID作为参数
                                    onNavigateToNoteEdit?.invoke(book.id)
                                },
                                onTimerClick = {
                                    // 导航到正向计时器页面，并传递选中的书籍ID
                                    onNavigateToTimer?.invoke(book.id)
                                }
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
                           BookListItem(
                                book = book,
                                onClick = { onBookClick(book.id) },
                                // 添加长按菜单相关回调函数
                                onDeleteClick = {
                                    showDeleteDialog = book
                                },
                                onEditClick = {
                                    // 实现编辑功能
                                    onNavigateToBookEdit?.invoke(book.id)
                                },
                                onMarkAsFinishedClick = {
                                    viewModel.markBookAsFinished(book.id)
                                },
                              
                                onPinClick = {
                                    // TODO: 实现置顶功能
                                },
                                onAddNoteClick = {
                                    // 导航到笔记编辑页面，并传递书籍ID作为参数
                                    onNavigateToNoteEdit?.invoke(book.id)
                                },
                                onTimerClick = {
                                    // 导航到正向计时器页面，并传递选中的书籍ID
                                    onNavigateToTimer?.invoke(book.id)
                                }
                            )
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
        }
    }
}