package com.anou.pagegather.ui.feature.management

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Source
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.anou.pagegather.data.local.entity.BookSourceEntity
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * 书籍来源管理界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookSourceManagementScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: BookSourceManagementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf<BookSourceEntity?>(null) }
    var draggedIndex by remember { mutableStateOf<Int?>(null) }
    val hapticFeedback = LocalHapticFeedback.current
    val listState = rememberLazyListState()
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "书籍来源管理",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.showAddBookSourceDialog() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "添加来源"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // 搜索框
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::searchBookSources,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            
            // 错误消息显示
            uiState.errorMessage?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(
                            onClick = viewModel::clearError
                        ) {
                            Text(
                                text = "关闭",
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
            
            // 加载状态
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            // 书籍来源列表
            if (uiState.bookSources.isEmpty() && !uiState.isLoading) {
                EmptyBookSourcesPlaceholder(
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    itemsIndexed(uiState.bookSources, key = { _, bookSource -> bookSource.id }) { index, bookSource ->
                        BookSourceItem(
                            bookSource = bookSource,
                            index = index,
                            totalCount = uiState.bookSources.size,
                            onEditClick = { 
                                if (!bookSource.isBuiltIn) {
                                    viewModel.showEditBookSourceDialog(bookSource)
                                }
                            },
                            onDeleteClick = { 
                                if (!bookSource.isBuiltIn) {
                                    showDeleteDialog = bookSource
                                }
                            },
                            onToggleEnabled = { viewModel.toggleBookSourceEnabled(bookSource) },
                            onDragStart = { 
                                draggedIndex = index
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            },
                            onDragEnd = { fromIndex, toIndex ->
                                if (fromIndex != toIndex) {
                                    viewModel.moveBookSource(fromIndex, toIndex)
                                }
                                draggedIndex = null
                            }
                        )
                    }
                }
            }
        }
    }
    
    // 编辑书籍来源对话框
    if (uiState.isEditDialogVisible) {
        BookSourceEditDialog(
            bookSource = uiState.editingBookSource,
            onDismiss = viewModel::hideEditDialog,
            onSave = { name -> viewModel.saveBookSource(name) }
        )
    }
    
    // 删除确认对话框
    showDeleteDialog?.let { bookSource ->
        DeleteConfirmDialog(
            sourceName = bookSource.getDisplayName(),
            onConfirm = {
                viewModel.deleteBookSource(bookSource) {
                    showDeleteDialog = null
                }
            },
            onDismiss = { showDeleteDialog = null }
        )
    }
}

/**
 * 书籍来源条目组件
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookSourceItem(
    bookSource: BookSourceEntity,
    index: Int,
    totalCount: Int,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onToggleEnabled: () -> Unit,
    onDragStart: () -> Unit,
    onDragEnd: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var isDragging by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .offset {
                IntOffset(
                    x = 0,
                    y = if (isDragging) dragOffset.y.roundToInt() else 0
                )
            }
            .zIndex(if (isDragging) 1f else 0f)
            .alpha(if (isDragging) 0.8f else 1f),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDragging) 8.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 拖拽手柄
            Icon(
                imageVector = Icons.Default.DragHandle,
                contentDescription = "拖拽排序",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier
                    .size(24.dp)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = {
                                isDragging = true
                                dragOffset = Offset.Zero
                                onDragStart()
                            },
                            onDragEnd = {
                                val targetIndex = calculateTargetIndex(
                                    dragOffset.y,
                                    index,
                                    totalCount
                                )
                                onDragEnd(index, targetIndex)
                                isDragging = false
                                dragOffset = Offset.Zero
                            },
                            onDrag = { _, dragAmount ->
                                dragOffset = Offset(
                                    0f,
                                    dragOffset.y + dragAmount.y
                                )
                            }
                        )
                    }
            )
            
            // 来源信息
            Row(
                modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = bookSource.getDisplayName(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = if (bookSource.isEnabled) 
                        MaterialTheme.colorScheme.onSurface 
                    else 
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                
                if (bookSource.isBuiltIn) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "内置",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            // 操作按钮
            Row {
                // 启用/禁用切换
                IconButton(
                    onClick = onToggleEnabled
                ) {
                    Icon(
                        imageVector = if (bookSource.isEnabled) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (bookSource.isEnabled) "禁用" else "启用",
                        tint = if (bookSource.isEnabled) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // 编辑按钮（仅自定义来源可编辑）
                if (!bookSource.isBuiltIn) {
                    IconButton(onClick = onEditClick) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "编辑",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    // 删除按钮（仅自定义来源可删除）
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "删除",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

/**
 * 计算拖拽目标位置
 */
private fun calculateTargetIndex(dragOffsetY: Float, currentIndex: Int, totalCount: Int): Int {
    val itemHeight = 80f // 估算的条目高度（包含间距）
    val indexChange = (dragOffsetY / itemHeight).roundToInt()
    return (currentIndex + indexChange).coerceIn(0, totalCount - 1)
}

/**
 * 空状态占位符
 */
@Composable
private fun EmptyBookSourcesPlaceholder(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
// 移除装饰性Source图标
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "暂无书籍来源",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
            
            Text(
                text = "点击右上角按钮添加自定义来源",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}

/**
 * 搜索框组件
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.focusRequester(focusRequester),
        label = { Text("搜索书籍来源") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "搜索"
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { 
                        onQueryChange("")
                        keyboardController?.hide()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "清除"
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp)
    )
}

/**
 * 书籍来源编辑对话框
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookSourceEditDialog(
    bookSource: BookSourceEntity?,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var name by remember { mutableStateOf(bookSource?.name ?: "") }
    val focusRequester = remember { FocusRequester() }
    
    LaunchedEffect(bookSource) {
        name = bookSource?.name ?: ""
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                text = if (bookSource == null) "添加书籍来源" else "编辑书籍来源",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            ) 
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("来源名称") },
                    placeholder = { Text("请输入来源名称") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    if (name.trim().isNotBlank()) {
                        onSave(name.trim())
                    }
                },
                enabled = name.trim().isNotBlank(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(if (bookSource == null) "添加" else "保存")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("取消")
            }
        },
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 6.dp
    )
    
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

/**
 * 删除确认对话框
 */
@Composable
private fun DeleteConfirmDialog(
    sourceName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "确认删除",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
        },
        text = {
            Text(
                text = "确定要删除书籍来源 \"$sourceName\" 吗？\n\n此操作不可撤销。",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("删除")
            }
        },
        dismissButton = {
            TextButton(
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