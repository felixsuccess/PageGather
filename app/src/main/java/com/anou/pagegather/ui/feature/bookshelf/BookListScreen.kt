package com.anou.pagegather.ui.feature.bookshelf

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.anou.pagegather.R
import com.anou.pagegather.data.local.entity.BookEntity
import com.anou.pagegather.data.local.entity.ReadStatus
import com.anou.pagegather.ui.theme.Accent
import com.anou.pagegather.ui.theme.TextGray

// 常量定义
private val GRID_COLUMNS = GridCells.Fixed(3)
private val GRID_PADDING = PaddingValues(vertical = 8.dp, horizontal = 8.dp)
private val GRID_SPACING = 8.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun BookListScreen(
    modifier: Modifier = Modifier,
    viewModel: BookListViewModel = hiltViewModel(),
    onBookClick: (Long) -> Unit,
    onAddBookClick: () -> Unit,
    onTimerClick: () -> Unit = {},
    onQuickActionsClick: () -> Unit = {},
) {
    val uiState by viewModel.state.collectAsState()
    val bookListState by viewModel.bookListState.collectAsState()
    val tabTitles = listOf(
        "书库"//, "预购书单"
    )
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    var isSearching by remember { mutableStateOf(false) }
    var showGroupFilter by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            modifier = modifier.fillMaxWidth(),
            title = {
                if (isSearching) {
                    SearchTextField(
                        value = searchQuery,
                        onValueChange = { newValue ->
                            searchQuery = newValue
                            viewModel.searchBooks(newValue)
                        },
                        focusRequester = focusRequester,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(
                        text = "书籍管理",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            actions = {
                if (isSearching) {
                    IconButton(onClick = {
                        searchQuery = ""
                        isSearching = false
                        viewModel.clearSearch()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "取消搜索",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                } else {
                    // 阅读计时按钮
                    IconButton(onClick = onTimerClick) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "阅读计时",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    // 分组筛选按钮
                    IconButton(onClick = {
                        showGroupFilter = !showGroupFilter
                    }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "分组筛选",
                            tint = if (bookListState.selectedGroupId != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    IconButton(onClick = {
                        isSearching = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "搜索",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    IconButton(onClick = { onAddBookClick() }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "添加书籍",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    // 更多选项按钮和下拉菜单
                    Box {
                        IconButton(onClick = {
                            showMoreMenu = !showMoreMenu
                        }) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = "更多选项",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        DropdownMenu(
                            expanded = showMoreMenu,
                            onDismissRequest = { showMoreMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("快捷导航") },
                                onClick = {
                                    showMoreMenu = false
                                    onQuickActionsClick()
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Bolt,
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    }
                }
            }
        )

        // 分组筛选器
        if (showGroupFilter) {
            GroupFilterSelector(
                availableGroups = bookListState.availableGroups,
                selectedGroupId = bookListState.selectedGroupId,
                onGroupSelected = viewModel::selectGroup,
                onClearFilter = viewModel::clearGroupFilter,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
        
        // 当前筛选状态显示
        if (bookListState.selectedGroupId != null) {
            val selectedGroup = bookListState.availableGroups.find { it.id == bookListState.selectedGroupId }
            if (selectedGroup != null) {
                FilterChip(
                    groupName = selectedGroup.getDisplayName(),
                    onRemove = viewModel::clearGroupFilter,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }
        
        // 在搜索状态改变时请求焦点
        LaunchedEffect(isSearching) {
            if (isSearching) {
                focusRequester.requestFocus()
            }
        }

        Box(Modifier.weight(1f)) {
            when (selectedTab) {
                0 -> {
                    BookListContent(
                        bookListUIState = uiState,
                        onBookClick = onBookClick,
                        onAddBookClick = onAddBookClick,
                        viewModel = viewModel
                    )
                }

                1 -> {
                    PreOrderBookListContent()
                }
            }
        }
    }
}

@Composable
private fun BookListContent(
    bookListUIState: BookListUIState,
    onBookClick: (Long) -> Unit,
    onAddBookClick: () -> Unit,
    viewModel: BookListViewModel,
) {
    val gridState = rememberLazyGridState()
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
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {

        when (bookListUIState) {
            is BookListUIState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is BookListUIState.Empty -> {
                // 空列表状态
                // TODO: 临时演示版本，可以在这里切换使用 EnhancedEmptyBooksPlaceholder 来测试不同效果
                val useEnhancedVersion = false // 设置为true使用增强版（带切换开关）
                
                if (useEnhancedVersion) {
                    EnhancedEmptyBooksPlaceholder(
                        onAddBookClick = onAddBookClick,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    EmptyBooksPlaceholder(
                        onAddBookClick = onAddBookClick,
                        isGridMode = true, // 当前是网格模式，使用图片+图标组合
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            is BookListUIState.Error -> {
                // 添加错误状态UI
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    Text(text = bookListUIState.message)
                }
            }


            is BookListUIState.Success -> {
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxWidth(),
                    columns = GRID_COLUMNS,
                    contentPadding = GRID_PADDING,
                    state = gridState,
                    verticalArrangement = Arrangement.spacedBy(GRID_SPACING),
                    horizontalArrangement = Arrangement.spacedBy(GRID_SPACING)
                ) {
                    items(bookListUIState.books.size) { index ->
                        val book = bookListUIState.books[index]
                        BookItem(
                            book = book, 
                            onItemClick = { onBookClick(book.id) },
                            onDeleteClick = { showDeleteDialog = book }
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
 * 删除书籍确认对话框
 */
@Composable
private fun DeleteBookConfirmDialog(
    bookName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "确认删除",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
            )
        },
        text = {
            Text(
                text = "确定要删除书籍 \"》$bookName《\" 吗？\n\n此操作将永久删除该书籍及其所有相关信息（包括阅读记录、笔记等）。",
                style = MaterialTheme.typography.bodyMedium
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

@Composable
fun BookItem(
    book: BookEntity, 
    onItemClick: () -> Unit,
    onDeleteClick: (() -> Unit)? = null
) {
    var showDropdownMenu by remember { mutableStateOf(false) }
    
    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onItemClick() }
                .padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(7f / 10f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(book.coverUrl).apply {
                        Log.d("BookListScreen", "正在加载封面URL: ${book.coverUrl}")
                    }.crossfade(true).build(),
                    contentDescription = null,
                    error = painterResource(id = R.mipmap.default_cover),
                    modifier = Modifier.fillMaxSize(),
                    onSuccess = {
                        Log.i("BookListScreen", "封面加载成功: ${book.coverUrl}")
                    },
                    onError = { result ->
                        Log.e("BookListScreen", "封面加载失败: ${result.result.throwable}")
                    }
                )
                
                // 已读标志
                if (book.readStatus == ReadStatus.FINISHED.ordinal) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 0.dp, topEnd = 8.dp, bottomStart = 8.dp, bottomEnd = 0.dp
                                )
                            )
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            text = "已读",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 10.sp
                        )
                    }
                }
                
                // 右上角操作按钮（仅在有删除功能时显示）
                if (onDeleteClick != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(4.dp)
                    ) {
                        IconButton(
                            onClick = { showDropdownMenu = true },
                            modifier = Modifier
                                .size(24.dp)
                                .background(
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                    CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "更多操作",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        
                        // 下拉菜单
                        androidx.compose.material3.DropdownMenu(
                            expanded = showDropdownMenu,
                            onDismissRequest = { showDropdownMenu = false }
                        ) {
                            androidx.compose.material3.DropdownMenuItem(
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
                                            color = MaterialTheme.colorScheme.error
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
            
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = book.name ?: "未知书名",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = book.author ?: "未知作者",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PreOrderBookListContent() {
    //TODO: 预购书单内容实现
    Column(Modifier.fillMaxSize()) {
        Text("预购书单功能开发中", style = MaterialTheme.typography.titleMedium)
    }
}

/**
 * 分组筛选器
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GroupFilterSelector(
    availableGroups: List<com.anou.pagegather.data.local.entity.BookGroupEntity>,
    selectedGroupId: Long?,
    onGroupSelected: (Long?) -> Unit,
    onClearFilter: () -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "按分组筛选",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                if (selectedGroupId != null) {
                    androidx.compose.material3.TextButton(
                        onClick = onClearFilter,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("清除筛选")
                    }
                }
            }
            
            if (availableGroups.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "暂无分组",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            } else {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(availableGroups) { group ->
                        androidx.compose.material3.FilterChip(
                            selected = selectedGroupId == group.id,
                            onClick = {
                                if (selectedGroupId == group.id) {
                                    onGroupSelected(null)
                                } else {
                                    onGroupSelected(group.id)
                                }
                            },
                            label = {
                                Text(
                                    text = group.getDisplayName(),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * 当前筛选条件显示
 */
@Composable
private fun FilterChip(
    groupName: String,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.primaryContainer,
                RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.FilterList,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(16.dp)
        )
        
        Text(
            text = "分组: $groupName",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        
        IconButton(
            onClick = onRemove,
            modifier = Modifier.size(20.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "移除筛选",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

/**
 * 搜索文本框
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.focusRequester(focusRequester),
        label = { Text("搜索书籍") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "搜索"
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp)
    )
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
    onAddBookClick: () -> Unit,
    isGridMode: Boolean = true,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isGridMode) {
                // 网格模式：使用原来的图片 + MenuBook图标组合
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    // 背景图片
                    Image(
                        painter = painterResource(id = R.mipmap.empty),
                        contentDescription = "空列表",
                        modifier = Modifier
                            .size(120.dp)
                            .alpha(0.8f)
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
            
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "暂无书籍",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
            
            Text(
                text = if (isGridMode) "开始你的阅读之旅吧" else "点击右上角按钮添加书籍",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
            
            androidx.compose.material3.Button(
                onClick = onAddBookClick,
                shape = RoundedCornerShape(8.dp)
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))
                Text("添加书籍")
            }
        }
    }
}

/**
 * 增强版空书籍占位符 - 包含切换按钮用于演示
 * 可以用于演示不同的空状态效果
 */
@Composable
fun EnhancedEmptyBooksPlaceholder(
    onAddBookClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isGridMode by remember { mutableStateOf(true) }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 切换按钮
        androidx.compose.material3.Card(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isGridMode) "网格模式" else "列表模式",
                    style = MaterialTheme.typography.labelSmall
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))
                androidx.compose.material3.Switch(
                    checked = isGridMode,
                    onCheckedChange = { isGridMode = it }
                )
            }
        }
        
        // 空状态内容
        EmptyBooksPlaceholder(
            onAddBookClick = onAddBookClick,
            isGridMode = isGridMode,
            modifier = Modifier.fillMaxSize()
        )
    }
}