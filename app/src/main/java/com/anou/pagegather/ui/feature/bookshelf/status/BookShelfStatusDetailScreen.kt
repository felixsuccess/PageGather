package com.anou.pagegather.ui.feature.bookshelf.status

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookShelfStatusDetailScreen(
    status: Int,
    statusName: String,
    viewModel: BookListViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onBookClick: (Long) -> Unit,
    onNavigateToBookEdit: ((Long) -> Unit)? = null  // 添加导航到书籍编辑页面的回调函数
) {
    val books: List<BookEntity> by viewModel.getBooksByStatus(status).collectAsState(initial = emptyList())
    var isGridMode by remember { mutableStateOf(true) }
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // 显示具体状态名称和书籍数量
                    Column {
                        Text(
                            text = statusName,
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
                                text = { Text("状态管理") },
                                onClick = {
                                    showMenu = false
                                    // TODO: 实现状态管理功能
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
                            text = "添加书籍到这个状态",
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
                                    // TODO: 实现删除功能
                                },
                                onEditClick = {
                                    // 实现编辑功能
                                    onNavigateToBookEdit?.invoke(book.id)
                                },
                                onMarkAsFinishedClick = {
                                    // TODO: 实现标记为已完成功能
                                },
                              
                                onPinClick = {
                                    // TODO: 实现置顶功能
                                },
                                onAddNoteClick = {
                                    // TODO: 实现记笔记功能
                                },
                                onTimerClick = {
                                    // TODO: 实现阅读计时功能
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
                                    // TODO: 实现删除功能
                                },
                                onEditClick = {
                                    // 实现编辑功能
                                    onNavigateToBookEdit?.invoke(book.id)
                                },
                                onMarkAsFinishedClick = {
                                    // TODO: 实现标记为已完成功能
                                },
                              
                                onPinClick = {
                                    // TODO: 实现置顶功能
                                },
                                onAddNoteClick = {
                                    // TODO: 实现记笔记功能
                                },
                                onTimerClick = {
                                    // TODO: 实现阅读计时功能
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}