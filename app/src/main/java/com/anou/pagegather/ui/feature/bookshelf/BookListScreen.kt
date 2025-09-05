package com.anou.pagegather.ui.feature.bookshelf

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.anou.pagegather.ui.feature.bookshelf.booklist.BatchOperationToolbar
import com.anou.pagegather.ui.feature.bookshelf.booklist.BookShelfDefaultBookListContent
import com.anou.pagegather.ui.feature.bookshelf.booklist.useLetterPlaceholderForGrid
import com.anou.pagegather.ui.feature.bookshelf.booksource.BookSourcedBookListContent
import com.anou.pagegather.ui.feature.bookshelf.filter.BookFilterTabs
import com.anou.pagegather.ui.feature.bookshelf.filter.filterOptions
import com.anou.pagegather.ui.feature.bookshelf.group.GroupedBookListContent
import com.anou.pagegather.ui.feature.bookshelf.rating.RatingBookListContent
import com.anou.pagegather.ui.feature.bookshelf.status.StatusBookListContent
import com.anou.pagegather.ui.feature.bookshelf.tag.TagBookListContent

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun BookListScreen(
    modifier: Modifier = Modifier,
    viewModel: BookListViewModel = hiltViewModel(),
    onBookClick: (Long) -> Unit,
    onAddBookClick: () -> Unit,
    onTimerClick: () -> Unit = {},
    onQuickActionsClick: () -> Unit = {},
    onNavigateToGroupDetail: (Long, String) -> Unit = { _, _ -> },  // 添加导航回调参数
    onNavigateToSourceDetail: (Long, String) -> Unit = { _, _ -> },  // 添加来源详情导航回调参数
    onNavigateToTagDetail: (Long, String, String?) -> Unit = { _, _, _ -> },  // 添加标签详情导航回调参数
    onNavigateToStatusDetail: (Int, String) -> Unit = { _, _ -> },  // 添加状态详情导航回调参数
    onNavigateToRatingDetail: (Int, String) -> Unit = { _, _ -> }  // 添加评分详情导航回调参数
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
                    BookShelfDefaultBookListContent(
                        bookListUIState = uiState,
                        onBookClick = onBookClick,
                        onAddBookClick = onAddBookClick,
                        onTimerClick = onTimerClick,
                        viewModel = viewModel,
                        isGridMode = bookListState.isGridMode,
                        useLetterPlaceholderForGrid = useLetterPlaceholderForGrid // 传递网格封面显示方式设置
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

                "name" -> {
                    PreOrderBookListContent()
                }

                "tag" -> {
                    TagBookListContent(
                        viewModel = viewModel,
                        isGridMode = bookListState.isGridMode,
                        onTagClick = { tag ->
                            // 实现标签详情导航
                            onNavigateToTagDetail(tag.id, tag.name, tag.color)
                        }
                    )
                }

                "status" -> {
                    StatusBookListContent(
                        viewModel = viewModel,
                        isGridMode = bookListState.isGridMode,
                        onStatusClick = { status ->
                            // 实现状态详情导航
                            onNavigateToStatusDetail(status.code, status.message)
                        }
                    )
                }

                "source" -> {
                    BookSourcedBookListContent(
                        viewModel = viewModel,
                        isGridMode = bookListState.isGridMode,
                        onSourceClick = { sourceId, sourceName ->
                            // 导航到来源详情页面
                            onNavigateToSourceDetail(sourceId, sourceName)
                        }
                    )
                }

                "rating" -> {
                    RatingBookListContent(
                        viewModel = viewModel,
                        isGridMode = bookListState.isGridMode,
                        onRatingClick = { rating ->
                            // 实现评分详情导航
                            onNavigateToRatingDetail(rating, if (rating == 0) "未评分" else "$rating 星")
                        }
                    )
                }


                else -> {
                    // 使用默认的书籍列表内容作为后备选项
                    BookShelfDefaultBookListContent(
                        bookListUIState = uiState,
                        onBookClick = onBookClick,
                        onAddBookClick = onAddBookClick,
                        onTimerClick = onTimerClick,
                        viewModel = viewModel,
                        isGridMode = bookListState.isGridMode,
                        useLetterPlaceholderForGrid = useLetterPlaceholderForGrid // 传递网格封面显示方式设置
                    )
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
                    val selectedBooks =
                        bookListState.books.filter { it.id in bookListState.selectedBooks }
                    if (selectedBooks.isNotEmpty()) {
                        viewModel.deleteBooksInBatch(selectedBooks)
                    }
                }
            )
        }

    }  //  TODO://显示底部操作按钮栏
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

