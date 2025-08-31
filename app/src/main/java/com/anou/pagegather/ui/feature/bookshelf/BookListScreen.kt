package com.anou.pagegather.ui.feature.bookshelf

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.TabRowDefaults.Indicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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

// 常量定义
private val GRID_COLUMNS = GridCells.Fixed(3)
private val GRID_PADDING = PaddingValues(vertical = 12.dp, horizontal = 12.dp)
private val GRID_SPACING = 12.dp
// 常量定义筛选选项
val filterOptions = listOf(
    FilterOption("default", "默认", "显示所有书籍"),
    FilterOption("group", "分组", "按书籍分组筛选"),
    FilterOption("tag", "标签", "按标签筛选书籍"),
    FilterOption("status", "状态", "按阅读状态筛选"),
    FilterOption("source", "来源", "按书籍来源筛选"),
    FilterOption("rating", "评分", "按评分筛选书籍"),
    FilterOption("name", "名称", "按书籍名称排序")
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun BookListScreen(
    modifier: Modifier = Modifier,
    viewModel: BookListViewModel = hiltViewModel(),
    onBookClick: (Long) -> Unit,
    onAddBookClick: () -> Unit,
    onTimerClick: () -> Unit = {},
    onQuickActionsClick: () -> Unit = {},
    onNavigateToGroupDetail: (Long, String) -> Unit = { _, _ -> }  // 添加导航回调参数
) {
    val uiState by viewModel.state.collectAsState()
    val bookListState by viewModel.bookListState.collectAsState()

    var selectedTab by remember { mutableStateOf(filterOptions[0]) }
    var searchQuery by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    var isSearching by remember { mutableStateOf(false) }
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
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 20.sp
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
                            
                            // 添加书籍选项
                            DropdownMenuItem(
                                text = { Text("添加书籍") },
                                onClick = {
                                    showMoreMenu = false
                                    onAddBookClick()
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null
                                    )
                                }
                            )
                            
                            // 阅读计时选项
                            DropdownMenuItem(
                                text = { Text("阅读计时") },
                                onClick = {
                                    showMoreMenu = false
                                    onTimerClick()
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    }
                }
            }
        )

        // 在搜索状态改变时请求焦点
        LaunchedEffect(isSearching) {
            if (isSearching) {
                focusRequester.requestFocus()
            }
        }

        // 书籍筛选选项卡
        BookFilterTabs(
            selectedFilter = selectedTab,
            onFilterSelected = { selectedTab = it },
            isGridMode = bookListState.isGridMode,
            onToggleDisplayMode = { viewModel.toggleDisplayMode() },
            onEnterBatchMode = { viewModel.toggleBatchMode() }  // 传递批量操作回调
        )

        Box(Modifier.weight(1f)) {
            when (selectedTab.code) {
               "default" -> {
                    BookListContent(
                        bookListUIState = uiState,
                        onBookClick = onBookClick,
                        onAddBookClick = onAddBookClick,
                        onTimerClick = onTimerClick,
                        viewModel = viewModel,
                        isGridMode = bookListState.isGridMode
                    )
                }
                "name" -> {
                    BookListContent(
                        bookListUIState = uiState,
                        onBookClick = onBookClick,
                        onAddBookClick = onAddBookClick,
                        onTimerClick = onTimerClick,
                        viewModel = viewModel,
                        isGridMode = bookListState.isGridMode
                    )
                }
               "group" -> {
                    // 按书籍分组筛选
                    GroupedBookListContent(
                        viewModel = viewModel,
                        isGridMode = bookListState.isGridMode, // 传递显示模式参数
                        onGroupClick = { groupId, groupName ->
                            // 导航到分组详情页面
                            onNavigateToGroupDetail(groupId, groupName)
                        }
                    )
                }
                "tag" -> {
                    PreOrderBookListContent()
                }
                "status" -> {
                    PreOrderBookListContent()
                }
                "source" -> {
                    PreOrderBookListContent()
                }
                "rating" -> {
                    PreOrderBookListContent()
                }


                else  ->{
                    DemoGroupListContent()
                }
            }
        }
        
        // 根据是否处于批量模式显示不同的底部栏
        if (bookListState.isBatchMode) {
            // 批量操作工具栏
            BatchOperationToolbar(
                selectedCount = bookListState.selectedBooks.size,
                onClearSelection = { viewModel.clearBookSelection() },
                onSelectAll = { viewModel.selectAllBooks() },
                onExitBatchMode = { viewModel.toggleBatchMode() },
                onDeleteSelected = { 
                    // 获取选中的书籍并执行删除操作
                    val selectedBooks = bookListState.books.filter { it.id in bookListState.selectedBooks }
                    if (selectedBooks.isNotEmpty()) {
                        viewModel.deleteBooksInBatch(selectedBooks)
                    }
                }
            )
        } 

    }  //  TODO://显示底部操作按钮栏
}

@Composable
fun DemoGroupListContent() {
    TODO("Not yet implemented")

    // 分组 ： 聚合排序   按照   分组的组顺序  和   没分组的 和  排序    ； 置顶的在上边， 分组中内部的 书籍排序  进行排序
    //标签：  按照标签 聚合排序
    //状态：  按照 状态 聚合排序



}

/**
 * 批量操作工具栏
 */
@Composable
private fun BatchOperationToolbar(
    selectedCount: Int,
    onClearSelection: () -> Unit,
    onSelectAll: () -> Unit,
    onExitBatchMode: () -> Unit,
    onDeleteSelected: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 左侧：选中数量和操作按钮
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "已选择 $selectedCount 项",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            // 全选按钮
            androidx.compose.material3.TextButton(
                onClick = onSelectAll,
                colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("全选")
            }
            
            // 清空选择按钮
            androidx.compose.material3.TextButton(
                onClick = onClearSelection,
                colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("清空")
            }
        }
        
        // 右侧：删除和退出按钮
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 删除按钮
            androidx.compose.material3.Button(
                onClick = onDeleteSelected,
                shape = RoundedCornerShape(12.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "删除",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("删除")
            }
            
            // 退出批量模式按钮
            androidx.compose.material3.Button(
                onClick = onExitBatchMode,
                shape = RoundedCornerShape(12.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Text("取消")
            }
        }
    }
}

// 定义筛选选项数据类
data class FilterOption(
    val code: String,           // 唯一标识
    val title: String,         // 显示名称
    val remark: String? = null  // 备注/描述（可选）
)

// 定义分组信息数据类
data class GroupInfo(
    val id: Long,              // 分组唯一标识
    val name: String,          // 分组名称
    val remark: String? = null // 分组备注（可选）
)

@Composable
private fun BookFilterTabs(
    selectedFilter: FilterOption,
    onFilterSelected: (FilterOption) -> Unit,
    isGridMode: Boolean,
    onToggleDisplayMode: () -> Unit,
    onEnterBatchMode: () -> Unit
) {
    // 添加设置菜单状态
    var showSettingsMenu by remember { mutableStateOf(false) }
    // 添加排序选项状态
    var showSortOptions by remember { mutableStateOf(false) }
    
    Column {
        // 使用水平滚动的Row来容纳分类选项和设置按钮
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 使用水平滚动的Row来容纳分类选项
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 分类选项按钮 - 使用基础的Box和Text实现
                filterOptions.forEach { option ->
                    val isSelected = selectedFilter.code == option.code
                    // 添加选中状态的动画效果
                    val backgroundColor by animateColorAsState(
                        targetValue = if (isSelected) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.surface,
                        animationSpec = tween(durationMillis = 200)
                    )
                    val textColor by animateColorAsState(
                        targetValue = if (isSelected)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        animationSpec = tween(durationMillis = 200)
                    )
                    
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(backgroundColor)
                            .clickable(
                                enabled = !isSelected // 已选中的选项卡不可点击
                            ) { 
                                 onFilterSelected(option)
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp) // 增加垂直padding以扩大点击区域
                    ) {
                        Text(
                            text = option.title,
                            style = MaterialTheme.typography.labelLarge,
                            color = textColor
                        )
                    }
                }
            }
            
            // 设置按钮 - 固定在右侧
            IconButton(
                onClick = { showSettingsMenu = true }
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "选项设置"
                )
                
                // 设置菜单
                DropdownMenu(
                    expanded = showSettingsMenu,
                    onDismissRequest = { showSettingsMenu = false }
                ) {
                    // 添加排序选项菜单项
                    DropdownMenuItem(
                        text = { Text("排序选项") },
                        onClick = { 
                            showSettingsMenu = false
                            showSortOptions = true
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Sort,
                                contentDescription = null
                            )
                        }
                    )
                    
                    // 添加批量操作菜单项
                    DropdownMenuItem(
                        text = { Text("批量操作") },
                        onClick = { 
                            showSettingsMenu = false
                            onEnterBatchMode()
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.CheckBox,
                                contentDescription = null
                            )
                        }
                    )
                    
                    // 添加显示模式切换菜单项
                    DropdownMenuItem(
                        text = { 
                            Text(
                                if (isGridMode) "切换到列表模式" else "切换到网格模式"
                            ) 
                        },
                        onClick = { 
                            showSettingsMenu = false
                            onToggleDisplayMode()
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (isGridMode) Icons.AutoMirrored.Filled.ViewList else Icons.Default.GridView,
                                contentDescription = null
                            )
                        }
                    )
                }
            }
        }
    }
    
    // 排序选项对话框
    if (showSortOptions) {
        SortOptionsDialog(
            onDismiss = { showSortOptions = false }
        )
    }
}

@Composable
private fun BookListContent(
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
                        isGridMode = isGridMode, // 使用当前显示模式
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
                if (isGridMode) {
                    // 网格模式
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookItem(
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
        modifier = Modifier
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
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(7f / 10f)
        ) {
            // 封面图片容器，使用更柔和的圆角
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
                    contentScale = androidx.compose.ui.layout.ContentScale.Fit,
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
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .clip(
                            RoundedCornerShape(
                                topStart = 0.dp, topEnd = 8.dp, bottomStart = 8.dp, bottomEnd = 0.dp
                            )
                        )
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))
                ) {
                    Text(
                        text = "已读",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            // 移除左上角操作按钮，改为长按触发菜单
        }
        
        // 书籍信息区域，使用更优雅的排版
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = book.name ?: "未知书名",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp
            )
            
            Text(
                text = book.author ?: "未知作者",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
            
            // 添加更多书籍信息，如评分、阅读进度等
            if (book.rating != null && book.rating > 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // 星级评分显示
                    for (i in 1..5) {
                        Icon(
                            imageVector = if (i <= book.rating) Icons.Default.Star else Icons.Default.StarOutline,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = if (i <= book.rating) Accent else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
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
        androidx.compose.material3.DropdownMenu(
            expanded = showDropdownMenu,
            onDismissRequest = { showDropdownMenu = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            // 编辑选项
            if (onEditClick != null) {
                androidx.compose.material3.DropdownMenuItem(
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
                androidx.compose.material3.DropdownMenuItem(
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
                androidx.compose.material3.DropdownMenuItem(
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
                androidx.compose.material3.DropdownMenuItem(
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
                androidx.compose.material3.DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.NoteAdd,
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
                androidx.compose.material3.DropdownMenuItem(
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
            
            // 删除选项
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookListItem(
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
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            // 添加长按手势检测
            .combinedClickable(
                onClick = { onItemClick() },
                onLongClick = { showDropdownMenu = true }
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 封面图片
        Box(
            modifier = Modifier
                .size(60.dp)
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
                contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                onSuccess = {
                    Log.i("BookListScreen", "封面加载成功: ${book.coverUrl}")
                },
                onError = { result -> 
                    Log.e("BookListScreen", "封面加载失败: ${result.result.throwable}")
                }
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // 书籍信息
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = book.name ?: "未知书名",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = book.author ?: "未知作者",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
            
            // 添加更多书籍信息，如评分、阅读进度等
            if (book.rating != null && book.rating > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                
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
                            tint = if (i <= book.rating) Accent else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
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
                    modifier = Modifier.size(40.dp)
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
                        androidx.compose.material3.DropdownMenuItem(
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
                        androidx.compose.material3.DropdownMenuItem(
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
                        androidx.compose.material3.DropdownMenuItem(
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
                        androidx.compose.material3.DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NoteAdd,
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
                        androidx.compose.material3.DropdownMenuItem(
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

@Composable
private fun GroupedBookListContent(
    viewModel: BookListViewModel,
    isGridMode: Boolean, // 添加显示模式参数
    onGroupClick: (groupId: Long, groupName: String) -> Unit
) {
    // 从ViewModel获取分组数据
    val bookListState by viewModel.bookListState.collectAsState()
    val groups = bookListState.availableGroups.map { group ->
        GroupInfo(group.id, group.name, group.getDisplayName())
    }
    
    if (groups.isEmpty()) {
        // 空状态
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "暂无分组",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp
                )
                
                Text(
                    text = "创建分组来管理你的书籍",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            }
        }
    } else {
        // 根据显示模式选择布局
        if (isGridMode) {
            // 网格模式
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(groups.size) { index ->
                    val group = groups[index]
                    GroupItem(
                        group = group,
                        onClick = { onGroupClick(group.id, group.name) }
                    )
                }
            }
        } else {
            // 列表模式
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(groups.size) { index ->
                    val group = groups[index]
                    GroupItem(
                        group = group,
                        onClick = { onGroupClick(group.id, group.name) }
                    )
                }
            }
        }
    }
}

@Composable
private fun GroupItem(
    group: GroupInfo,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 分组图标
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Folder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // 分组信息
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = group.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp
            )
            
            if (!group.remark.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = group.remark,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        
        // 右侧箭头指示器
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
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
        label = { 
            Text(
                "搜索书籍", 
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            ) 
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "搜索",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "清除",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
        )
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
            
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
            
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
            
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(20.dp))
            
            androidx.compose.material3.Button(
                onClick = onAddBookClick,
                shape = RoundedCornerShape(12.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))
                Text("添加书籍", fontSize = 14.sp)
            }
        }
    }
}

/**
 * 增强版空书籍占位符 - 包含切换按钮用于演示
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
            shape = RoundedCornerShape(20.dp),
            colors = androidx.compose.material3.CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = androidx.compose.material3.CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isGridMode) "网格模式" else "列表模式",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
               Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = isGridMode,
                    onCheckedChange = { isGridMode = it },
                    colors = androidx.compose.material3.SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    )
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

/**
 * 排序选项对话框
 */
@Composable
private fun SortOptionsDialog(
    onDismiss: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "排序选项",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 排序字段选项
                Text(
                    text = "排序字段",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                // 按书名排序
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = false, // TODO: 从ViewModel获取当前排序状态
                        onClick = { /* TODO: 设置按书名排序 */ }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "书名",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                // 按作者排序
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = false, // TODO: 从ViewModel获取当前排序状态
                        onClick = { /* TODO: 设置按作者排序 */ }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "作者",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                // 按添加时间排序
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = true, // TODO: 从ViewModel获取当前排序状态
                        onClick = { /* TODO: 设置按添加时间排序 */ }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "添加时间",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                // 按阅读状态排序
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = false, // TODO: 从ViewModel获取当前排序状态
                        onClick = { /* TODO: 设置按阅读状态排序 */ }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "阅读状态",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                // 排序方向选项
                Text(
                    text = "排序方向",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                // 升序
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = true, // TODO: 从ViewModel获取当前排序方向
                        onClick = { /* TODO: 设置升序 */ }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "升序",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                // 降序
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = false, // TODO: 从ViewModel获取当前排序方向
                        onClick = { /* TODO: 设置降序 */ }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "降序",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        confirmButton = {
            androidx.compose.material3.TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("确定")
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

/**
 * 分组详情页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookShelfGroupDetailScreen(
    groupId: Long,
    viewModel: BookListViewModel,
    onBackClick: () -> Unit,
    onBookClick: (Long) -> Unit
) {
    // 获取分组信息
    val bookListState by viewModel.bookListState.collectAsState()
    val group = bookListState.availableGroups.find { it.id == groupId }
    val groupName = group?.name ?: "未知分组"
    
    // 获取分组内的书籍列表
    val groupBooksState = remember { mutableStateOf<BookListUIState>(BookListUIState.Loading) }
    
    // 加载分组内的书籍
    LaunchedEffect(groupId) {
        viewModel.getBooksByGroupId(groupId).collect { books ->
            groupBooksState.value = if (books.isEmpty()) {
                BookListUIState.Empty
            } else {
                BookListUIState.Success(books, isLoadingMore = false)
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // 顶部导航栏
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            title = {
                Text(
                    text = groupName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 20.sp
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "返回",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        )
        
        // 分组书籍列表
        when (val state = groupBooksState.value) {
            is BookListUIState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            is BookListUIState.Empty -> {
                // 空状态
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "该分组暂无书籍",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 18.sp
                        )
                        
                        Text(
                            text = "添加书籍到此分组",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                    }
                }
            }
            
            is BookListUIState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = state.message)
                }
            }
            
            is BookListUIState.Success -> {
                // 书籍列表
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.books.size) { index ->
                        val book = state.books[index]
                        BookListItem(
                            book = book,
                            onItemClick = { onBookClick(book.id) },
                            onDeleteClick = { /* TODO: 实现删除功能 */ },
                            onEditClick = { /* TODO: 实现编辑功能 */ },
                            onMarkAsFinishedClick = { /* TODO: 实现标记完成功能 */ },
                            onAddToGroupClick = { /* TODO: 实现添加到分组功能 */ },
                            onPinClick = { /* TODO: 实现置顶功能 */ },
                            onAddNoteClick = { /* TODO: 实现记笔记功能 */ },
                            onTimerClick = { /* TODO: 实现计时功能 */ }
                        )
                    }
                }
            }
        }
    }
}
