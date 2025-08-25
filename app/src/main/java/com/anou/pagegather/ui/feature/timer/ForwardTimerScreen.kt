package com.anou.pagegather.ui.feature.timer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.anou.pagegather.data.local.entity.BookEntity
import com.anou.pagegather.data.local.entity.BookType
import com.anou.pagegather.data.local.entity.ReadPositionUnit
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForwardTimerScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToAddBook: () -> Unit = {}, // 添加导航到添加书籍页面的参数
    newlyAddedBookId: Long? = null, // 添加新书籍ID参数
    viewModel: ForwardTimerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    // 处理新添加的书籍
    LaunchedEffect(newlyAddedBookId) {
        if (newlyAddedBookId != null && newlyAddedBookId > 0) {
            // 直接从数据库获取新添加的书籍并选中它
            viewModel.selectNewlyAddedBook(newlyAddedBookId)
        }
    }
    
    // 显示错误信息
    uiState.errorMessage?.let { error ->
        LaunchedEffect(error) {
            // 可以使用 SnackBar 或其他方式显示错误
            viewModel.clearError()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "正向计时器",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 居中放大显示计时器，沾满中间区域
            LargeTimerDisplay(
                uiState = uiState,
                viewModel = viewModel,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
            
            // 底部控制按钮
            ForwardTimerControls(
                status = uiState.status,
                onStart = { viewModel.startTimer() },
                onPause = { viewModel.pauseTimer() },
                onStop = { viewModel.stopTimer() },
                onReset = { viewModel.fullReset() },
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
        
        // 显示保存记录对话框（在PAUSED状态时显示）
        if (uiState.showSaveDialog && uiState.status == TimerStatus.PAUSED) {
            ForwardSaveRecordDialog(
                uiState = uiState,
                viewModel = viewModel,
                onDismiss = { 
                    // 只关闭对话框，不执行任何操作
                    // 具体的操作在对话框内部按钮处理
                },
                onNavigateToAddBook = onNavigateToAddBook, // 传递导航参数
                newlyAddedBookId = newlyAddedBookId // 传递新添加的书籍ID
            )
        }
    }
}

/**
 * 放大的时间显示组件
 */
@Composable
fun LargeTimerDisplay(
    uiState: ForwardTimerUIState,
    viewModel: ForwardTimerViewModel,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = when (uiState.status) {
                TimerStatus.RUNNING -> MaterialTheme.colorScheme.primaryContainer
                TimerStatus.PAUSED -> MaterialTheme.colorScheme.secondaryContainer
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp), // 增加内边距
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // 垂直居中
        ) {
            // 主要时间显示 - 使用更大的字体
            Text(
                text = viewModel.formatTime(uiState.elapsedTime),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 48.sp, // 增大字体大小
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false) // 允许文本根据内容调整大小
            )
            
            // 状态指示
            Text(
                text = when (uiState.status) {
                    TimerStatus.IDLE -> "准备开始"
                    TimerStatus.RUNNING -> "计时中"
                    TimerStatus.PAUSED -> "已暂停"
                },
                style = MaterialTheme.typography.titleMedium, // 使用更大的字体
                color = when (uiState.status) {
                    TimerStatus.RUNNING -> MaterialTheme.colorScheme.primary
                    TimerStatus.PAUSED -> MaterialTheme.colorScheme.secondary
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

/**
 * 计时器控制按钮
 */
@Composable
fun ForwardTimerControls(
    status: TimerStatus,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onStop: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        when (status) {
            TimerStatus.IDLE -> {
                Button(
                    onClick = onStart,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("开始")
                }
            }
            
            TimerStatus.RUNNING -> {
                Button(
                    onClick = onPause,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Pause,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("暂停")
                }
                
                OutlinedButton(
                    onClick = onStop,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("停止")
                }
            }
            
            TimerStatus.PAUSED -> {
                Button(
                    onClick = onStart,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("继续")
                }
                
                OutlinedButton(
                    onClick = onStop,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("停止")
                }
            }
        }
    }
}



/**
 * 书籍选择对话框
 */
@Composable
fun ForwardBookSelectorDialog(
    books: List<BookEntity>,
    selectedBook: BookEntity?,
    onBookSelect: (BookEntity) -> Unit,
    onDismiss: () -> Unit,
    onNavigateToAddBook: () -> Unit  // 添加导航参数
) {
    var searchQuery by remember { mutableStateOf("") }
    
    // 过滤书籍列表
    val filteredBooks = remember(books, searchQuery) {
        if (searchQuery.isBlank()) {
            books
        } else {
            books.filter { 
                it.name?.contains(searchQuery, ignoreCase = true) == true ||
                it.author?.contains(searchQuery, ignoreCase = true) == true
            }
        }
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "选择书籍",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 搜索框
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("搜索书籍") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "搜索"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                
                if (filteredBooks.isEmpty() && searchQuery.isBlank()) {
                    // 如果没有书籍，显示添加书籍的提示
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "暂无书籍",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "请先添加书籍",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else if (filteredBooks.isEmpty() && searchQuery.isNotBlank()) {
                    // 如果搜索无结果
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "未找到匹配的书籍",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    // 书籍列表
                    LazyColumn(
                        modifier = Modifier.weight(1f, fill = false),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(filteredBooks) { book ->
                            val isSelected = selectedBook?.id == book.id
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = isSelected,
                                        onClick = { onBookSelect(book) }
                                    ),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) {
                                        MaterialTheme.colorScheme.primaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.surface
                                    }
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = book.name ?: "",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Medium
                                            )
                                            book.author?.let { author ->
                                                Text(
                                                    text = author,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            Text(
                                                text = "进度: ${book.getProgressPercentage().toInt()}%",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        
                                        // 选中标记
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "已选中",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        
                        // 添加"新建书籍"项
                        item {
                            Card(
                                onClick = { 
                                    onDismiss()  // 先关闭当前对话框
                                    onNavigateToAddBook()  // 然后导航到添加书籍页面
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "添加书籍",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        text = "添加新书籍",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (filteredBooks.isEmpty() && searchQuery.isBlank()) {
                Button(
                    onClick = { 
                        onDismiss()  // 先关闭当前对话框
                        onNavigateToAddBook()  // 然后导航到添加书籍页面
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("添加书籍")
                }
            }
        },
        dismissButton = {
            if (filteredBooks.isNotEmpty() || searchQuery.isNotBlank()) {
                TextButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("取消")
                }
            }
        },
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 6.dp
    )
}

/**
 * 保存记录对话框
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForwardSaveRecordDialog(
    uiState: ForwardTimerUIState,
    viewModel: ForwardTimerViewModel,
    onDismiss: () -> Unit,
    onNavigateToAddBook: () -> Unit, // 添加导航参数
    newlyAddedBookId: Long? = null // 添加新书籍ID参数
) {
    var selectedBook by remember { mutableStateOf<BookEntity?>(uiState.selectedBook) }
    var startProgress by remember { mutableStateOf(uiState.startProgress) }
    var endProgress by remember { mutableStateOf(uiState.currentProgress) }
    var markAsFinished by remember { mutableStateOf(false) } // 标记完成复选框状态
    var showAddBookHint by remember { mutableStateOf(false) } // 添加书籍提示
    val books by viewModel.getAllBooks().collectAsState(initial = emptyList())
    
    // 初始化进度值
    LaunchedEffect(uiState.showSaveDialog) {
        if (uiState.showSaveDialog) {
            // 起始进度从选择的书籍中带出
            val bookStartProgress = uiState.selectedBook?.readPosition ?: 0.0
            startProgress = bookStartProgress
            // 结束进度默认为当前进度
            endProgress = uiState.currentProgress
            selectedBook = uiState.selectedBook
            markAsFinished = false // 默认不标记完成
        }
    }
    
    // 当书籍选择改变时，更新起始进度
    LaunchedEffect(selectedBook) {
        selectedBook?.let { book ->
            startProgress = book.readPosition
        }
    }
    
    // 处理新添加的书籍 - 修复逻辑
    LaunchedEffect(newlyAddedBookId) {
        if (newlyAddedBookId != null && newlyAddedBookId > 0) {
            // 直接从数据库获取新添加的书籍并选中它
            viewModel.selectNewlyAddedBook(newlyAddedBookId)
        }
    }
    
    // 监听viewModel中的选中书籍变化
    LaunchedEffect(uiState.selectedBook) {
        if (uiState.selectedBook != null) {
            selectedBook = uiState.selectedBook
            startProgress = uiState.selectedBook.readPosition
            endProgress = uiState.selectedBook.readPosition
        }
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "保存阅读记录",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 显示计时信息
                Text("本次阅读时长: ${viewModel.formatTime(uiState.elapsedTime)}")
                
                // 显示开始时间和结束时间
                val startTimeText = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    .format(Date(uiState.tempStartTime))
                val endTimeText = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    .format(Date(uiState.tempStartTime + uiState.elapsedTime))
                Text("开始时间: $startTimeText")
                Text("结束时间: $endTimeText")
                
                // 书籍选择
                ForwardBookSelector(
                    books = books,
                    selectedBook = selectedBook,
                    onBookSelect = { book -> selectedBook = book },
                    onNavigateToAddBook = onNavigateToAddBook  // 传递导航参数
                )
                
                // 进度设置（使用与书籍编辑页面相同的逻辑）
                ForwardBookProgressInput(
                    bookType = selectedBook?.type ?: BookType.PAPER_BOOK.code,
                    positionUnit = selectedBook?.positionUnit ?: ReadPositionUnit.PAGE.code,
                    startProgress = startProgress,
                    endProgress = endProgress,
                    onStartPositionChange = { progress -> startProgress = progress },
                    onEndPositionChange = { progress -> endProgress = progress }
                )
                
                // 标记完成复选框
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = markAsFinished,
                        onCheckedChange = { checked -> markAsFinished = checked }
                    )
                    Text("标记为已完成")
                }
            }
        },
        confirmButton = {
            // 保存按钮
            Button(
                onClick = {
                    if (books.isEmpty()) {
                        // 如果没有书籍，显示添加书籍提示
                        showAddBookHint = true
                    } else {
                        viewModel.saveTimerRecord(
                            bookId = selectedBook?.id,
                            startProgress = startProgress,
                            endProgress = endProgress,
                            notes = if (markAsFinished) "标记为已完成" else null,
                            markAsFinished = markAsFinished // 传递标记完成状态
                        )
                        onDismiss() // 关闭对话框
                    }
                },
                enabled = selectedBook != null || books.isEmpty(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(if (books.isEmpty()) "添加书籍" else "保存")
            }
        },
        dismissButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 继续阅读按钮
                TextButton(
                    onClick = {
                        viewModel.continueReading(selectedBook)
                        onDismiss()
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("继续阅读")
                }
                TextButton(
                    onClick = {
                        viewModel.cancelSaveRecord()
                        onDismiss()
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("不保存")
                }
            }
        },
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 6.dp
    )
    
    // 添加书籍提示对话框
    if (showAddBookHint) {
        AlertDialog(
            onDismissRequest = { showAddBookHint = false },
            title = {
                Text(
                    text = "添加书籍",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
            },
            text = {
                Text("检测到您还没有添加任何书籍，请先添加书籍后再保存阅读记录。")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showAddBookHint = false
                        onDismiss()
                        onNavigateToAddBook() // 导航到添加书籍页面
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("去添加书籍")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAddBookHint = false },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("取消")
                }
            },
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 6.dp
        )
    }
}

/**
 * 书籍进度输入组件（与书籍编辑页面相同的逻辑）
 */
@Composable
fun ForwardBookProgressInput(
    bookType: Int,
    positionUnit: Int,
    startProgress: Double,
    endProgress: Double,
    onStartPositionChange: (Double) -> Unit,
    onEndPositionChange: (Double) -> Unit
) {
    var startProgressText by remember { mutableStateOf(startProgress.toString()) }
    var endProgressText by remember { mutableStateOf(endProgress.toString()) }
    
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "阅读进度",
            style = MaterialTheme.typography.titleMedium
        )
        
        if (bookType == BookType.PAPER_BOOK.code) {
            // 纸质书使用页数
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = startProgressText,
                    onValueChange = { value ->
                        if (value.isEmpty() || value.all { it.isDigit() }) {
                            startProgressText = value
                            value.toIntOrNull()?.let { number -> 
                                onStartPositionChange(number.toDouble())
                            }
                        }
                    },
                    label = { Text("起始页数") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                
                Text(
                    text = "→",
                    style = MaterialTheme.typography.bodyLarge
                )
                
                OutlinedTextField(
                    value = endProgressText,
                    onValueChange = { value ->
                        if (value.isEmpty() || value.all { it.isDigit() }) {
                            endProgressText = value
                            value.toIntOrNull()?.let { number -> 
                                onEndPositionChange(number.toDouble())
                            }
                        }
                    },
                    label = { Text("结束页数") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            // 电子书根据位置单位处理
            when (positionUnit) {
                ReadPositionUnit.PAGE.code -> {
                    // 页数
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = startProgressText,
                            onValueChange = { value ->
                                if (value.isEmpty() || value.all { it.isDigit() }) {
                                    startProgressText = value
                                    value.toIntOrNull()?.let { number -> 
                                        onStartPositionChange(number.toDouble())
                                    }
                                }
                            },
                            label = { Text("起始页数") },
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        
                        Text(
                            text = "→",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        
                        OutlinedTextField(
                            value = endProgressText,
                            onValueChange = { value ->
                                if (value.isEmpty() || value.all { it.isDigit() }) {
                                    endProgressText = value
                                    value.toIntOrNull()?.let { number -> 
                                        onEndPositionChange(number.toDouble())
                                    }
                                }
                            },
                            label = { Text("结束页数") },
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                ReadPositionUnit.PERCENT.code -> {
                    // 百分比
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = startProgressText,
                            onValueChange = { value ->
                                if (value.isEmpty() || (value.all { it.isDigit() || it == '.' } && 
                                        value.toDoubleOrNull()?.let { it >= 0 && it <= 100 } == true)) {
                                    startProgressText = value
                                    value.toDoubleOrNull()?.let { number -> 
                                        onStartPositionChange(number)
                                    }
                                }
                            },
                            label = { Text("起始进度 %") },
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                        
                        Text(
                            text = "→",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        
                        OutlinedTextField(
                            value = endProgressText,
                            onValueChange = { value ->
                                if (value.isEmpty() || (value.all { it.isDigit() || it == '.' } && 
                                        value.toDoubleOrNull()?.let { it >= 0 && it <= 100 } == true)) {
                                    endProgressText = value
                                    value.toDoubleOrNull()?.let { number -> 
                                        onEndPositionChange(number)
                                    }
                                }
                            },
                            label = { Text("结束进度 %") },
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                ReadPositionUnit.CHAPTER.code -> {
                    // 章节
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = startProgressText,
                            onValueChange = { value ->
                                if (value.isEmpty() || value.all { it.isDigit() }) {
                                    startProgressText = value
                                    value.toIntOrNull()?.let { number -> 
                                        onStartPositionChange(number.toDouble())
                                    }
                                }
                            },
                            label = { Text("起始章节") },
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        
                        Text(
                            text = "→",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        
                        OutlinedTextField(
                            value = endProgressText,
                            onValueChange = { value ->
                                if (value.isEmpty() || value.all { it.isDigit() }) {
                                    endProgressText = value
                                    value.toIntOrNull()?.let { number -> 
                                        onEndPositionChange(number.toDouble())
                                    }
                                }
                            },
                            label = { Text("结束章节") },
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                else -> {
                    // 默认使用百分比
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = startProgressText,
                            onValueChange = { value ->
                                if (value.isEmpty() || (value.all { it.isDigit() || it == '.' } && 
                                        value.toDoubleOrNull()?.let { it >= 0 && it <= 100 } == true)) {
                                    startProgressText = value
                                    value.toDoubleOrNull()?.let { number -> 
                                        onStartPositionChange(number)
                                    }
                                }
                            },
                            label = { Text("起始进度") },
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                        
                        Text(
                            text = "→",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        
                        OutlinedTextField(
                            value = endProgressText,
                            onValueChange = { value ->
                                if (value.isEmpty() || (value.all { it.isDigit() || it == '.' } && 
                                        value.toDoubleOrNull()?.let { it >= 0 && it <= 100 } == true)) {
                                    endProgressText = value
                                    value.toDoubleOrNull()?.let { number -> 
                                        onEndPositionChange(number)
                                    }
                                }
                            },
                            label = { Text("结束进度") },
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * 书籍选择器
 */
@Composable
fun ForwardBookSelector(
    books: List<BookEntity>,
    selectedBook: BookEntity?,
    onBookSelect: (BookEntity) -> Unit,
    onNavigateToAddBook: () -> Unit  // 添加导航参数
) {
    var showBookSelector by remember { mutableStateOf(false) }
    
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "选择书籍",
            style = MaterialTheme.typography.titleMedium
        )
        
        Card(
            onClick = { showBookSelector = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Book,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = selectedBook?.name ?: "点击选择要关联的书籍",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (selectedBook != null) FontWeight.Medium else FontWeight.Normal
                    )
                }
                
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null
                )
            }
        }
    }
    
    // 书籍选择对话框
    if (showBookSelector) {
        ForwardBookSelectorDialog(
            books = books,
            selectedBook = selectedBook,
            onBookSelect = { book ->
                onBookSelect(book)
                showBookSelector = false
            },
            onDismiss = { showBookSelector = false },
            onNavigateToAddBook = onNavigateToAddBook  // 传递导航参数
        )
    }
}