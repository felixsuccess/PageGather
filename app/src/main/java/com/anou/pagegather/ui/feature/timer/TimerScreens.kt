package com.anou.pagegather.ui.feature.timer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    viewModel: TimerViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.setTimerType(TimerType.FORWARD)
    }
    
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
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
        TimerContent(
            modifier = Modifier.padding(paddingValues),
            uiState = uiState,
            viewModel = viewModel
        )
        
        // 显示保存记录对话框（在PAUSED或STOPPED状态时显示）
        if (uiState.showSaveDialog && (uiState.status == TimerStatus.PAUSED )) {
            SaveRecordDialog(
                uiState = uiState,
                viewModel = viewModel,
                onDismiss = { viewModel.cancelSaveRecord() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReverseTimerScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: TimerViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.setTimerType(TimerType.REVERSE)
    }
    
    val uiState by viewModel.uiState.collectAsState()
    
    // 显示错误信息
    uiState.errorMessage?.let { error ->
        LaunchedEffect(error) {
            viewModel.clearError()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "反向计时器",
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
        ) {
            // 目标时间设置（仅在IDLE状态显示）
            if (uiState.status == TimerStatus.IDLE) {
                ReverseTimerSetup(
                    viewModel = viewModel,
                    modifier = Modifier.padding(16.dp)
                )
            }
            
            TimerContent(
                modifier = Modifier.weight(1f),
                uiState = uiState,
                viewModel = viewModel
            )
        }
        
        // 显示保存记录对话框（在PAUSED或STOPPED状态时显示）
        if (uiState.showSaveDialog && (uiState.status == TimerStatus.PAUSED  )) {
            SaveRecordDialog(
                uiState = uiState,
                viewModel = viewModel,
                onDismiss = { viewModel.cancelSaveRecord() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalSettingScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "阅读目标设置",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { /* 暂未实现返回功能 */ }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "🎯",
                    style = MaterialTheme.typography.displayLarge
                )
                Text(
                    text = "阅读目标设置",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "此功能正在开发中...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingPlanScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "阅读计划",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { /* 暂未实现返回功能 */ }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "📅",
                    style = MaterialTheme.typography.displayLarge
                )
                Text(
                    text = "阅读计划",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "此功能正在开发中...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodicReminderScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "定期提醒",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { /* 暂未实现返回功能 */ }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "🔔",
                    style = MaterialTheme.typography.displayLarge
                )
                Text(
                    text = "定期提醒",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "此功能正在开发中...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * 反向计时器设置组件
 */
@Composable
fun ReverseTimerSetup(
    viewModel: TimerViewModel,
    modifier: Modifier = Modifier
) {
    var targetMinutes by remember { mutableStateOf("25") }
    
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "设置目标时间",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = targetMinutes,
                    onValueChange = { value ->
                        if (value.isEmpty() || value.all { it.isDigit() }) {
                            targetMinutes = value
                        }
                    },
                    label = { Text("分钟") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                
                Button(
                    onClick = {
                        val minutes = targetMinutes.toIntOrNull() ?: 0
                        if (minutes > 0) {
                            viewModel.setTargetTime(minutes)
                        }
                    },
                    enabled = targetMinutes.isNotEmpty() && targetMinutes.toIntOrNull()?.let { it > 0 } == true
                ) {
                    Text("设置")
                }
            }
            
            // 快捷时间选择
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(15, 25, 45, 60).forEach { minutes ->
                    FilterChip(
                        onClick = {
                            targetMinutes = minutes.toString()
                            viewModel.setTargetTime(minutes)
                        },
                        label = { Text("${minutes}分钟") },
                        selected = false
                    )
                }
            }
        }
    }
}

/**
 * 计时器主要内容组件
 */
@Composable
fun TimerContent(
    modifier: Modifier = Modifier,
    uiState: TimerUIState,
    viewModel: TimerViewModel
) {
    if (uiState.type == TimerType.FORWARD) {
        // 正向计时器使用特殊布局
        ForwardTimerLayout(
            modifier = modifier,
            uiState = uiState,
            viewModel = viewModel
        )
    } else {
        // 反向计时器使用原有布局
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // 时间显示
            TimerDisplay(
                uiState = uiState,
                viewModel = viewModel
            )
            
            // 控制按钮
            TimerControls(
                status = uiState.status,
                onStart = { viewModel.startTimer() },
                onPause = { viewModel.pauseTimer() },
                onStop = { viewModel.stopTimer() },
                onReset = { viewModel.fullReset() }
            )
        }
    }
}

/**
 * 正向计时器特殊布局
 */
@Composable
fun ForwardTimerLayout(
    modifier: Modifier = Modifier,
    uiState: TimerUIState,
    viewModel: TimerViewModel
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 居中放大显示计时器，沾满中间区域
        LargeTimerDisplay(
            uiState = uiState,
            viewModel = viewModel,
            modifier = modifier.then(
                Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
            )
        )
        
        // 底部控制按钮
        TimerControls(
            status = uiState.status,
            onStart = { viewModel.startTimer() },
            onPause = { viewModel.pauseTimer() },
            onStop = { viewModel.stopTimer() },
            onReset = { viewModel.fullReset() },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        )
    }
}

/**
 * 放大的时间显示组件
 */
@Composable
fun LargeTimerDisplay(
    uiState: TimerUIState,
    viewModel: TimerViewModel,
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
                text = when (uiState.type) {
                    TimerType.FORWARD -> viewModel.formatTime(uiState.elapsedTime)
                    TimerType.REVERSE -> viewModel.formatTime(uiState.remainingTime)
                },
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
 * 书籍选择卡片
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookSelectionCard(
    selectedBook: BookEntity?,
    onBookSelect: (BookEntity) -> Unit,
    viewModel: TimerViewModel
) {
    var showBookSelector by remember { mutableStateOf(false) }
    val books by viewModel.getAllBooks().collectAsState(initial = emptyList())
    
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
                    text = if (selectedBook != null) "当前书籍" else "选择书籍",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = selectedBook?.name ?: "点击选择要阅读的书籍",
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
    
    // 书籍选择对话框
    if (showBookSelector) {
        BookSelectorDialog(
            books = books,
            onBookSelect = { book ->
                onBookSelect(book)
                showBookSelector = false
            },
            onDismiss = { showBookSelector = false }
        )
    }
}

/**
 * 书籍选择对话框
 */
@Composable
fun BookSelectorDialog(
    books: List<BookEntity>,
    onBookSelect: (BookEntity) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择书籍") },
        text = {
            LazyColumn {
                items(books) { book ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .selectable(
                                selected = false,
                                onClick = { onBookSelect(book) }
                            )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
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
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

/**
 * 时间显示组件
 */
@Composable
fun TimerDisplay(
    uiState: TimerUIState,
    viewModel: TimerViewModel
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 主要时间显示
            Text(
                text = when (uiState.type) {
                    TimerType.FORWARD -> viewModel.formatTime(uiState.elapsedTime)
                    TimerType.REVERSE -> viewModel.formatTime(uiState.remainingTime)
                },
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            // 状态指示
            Text(
                text = when (uiState.status) {
                    TimerStatus.IDLE -> "准备开始"
                    TimerStatus.RUNNING -> "计时中"
                    TimerStatus.PAUSED -> "已暂停"
                },
                style = MaterialTheme.typography.labelLarge,
                color = when (uiState.status) {
                    TimerStatus.RUNNING -> MaterialTheme.colorScheme.primary
                    TimerStatus.PAUSED -> MaterialTheme.colorScheme.secondary
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
            
            // 反向计时器显示已用时间
            if (uiState.type == TimerType.REVERSE && uiState.elapsedTime > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "已用时间: ${viewModel.formatTime(uiState.elapsedTime)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


/**
 * 计时器控制按钮
 */
@Composable
fun TimerControls(
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
            
//            TimerStatus.STOPPED -> {
//                Button(
//                    onClick = onReset,
//                    modifier = Modifier.weight(1f)
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Refresh,
//                        contentDescription = null,
//                        modifier = Modifier.size(18.dp)
//                    )
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Text("重新开始")
//                }
//            }
        }
    }
}

/**
 * 保存记录对话框
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveRecordDialog(
    uiState: TimerUIState,
    viewModel: TimerViewModel,
    onDismiss: () -> Unit
) {
    var selectedBook by remember { mutableStateOf<BookEntity?>(uiState.selectedBook) }
    var startProgress by remember { mutableStateOf(uiState.startProgress) }
    var endProgress by remember { mutableStateOf(uiState.currentProgress) }
    var markAsFinished by remember { mutableStateOf(false) } // 标记完成复选框状态
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
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("保存阅读记录") },
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
                BookSelector(
                    books = books,
                    selectedBook = selectedBook,
                    onBookSelect = { selectedBook = it }
                )
                
                // 进度设置（使用与书籍编辑页面相同的逻辑）
                BookProgressInput(
                    bookType = selectedBook?.type ?: BookType.PAPER_BOOK.code,
                    positionUnit = selectedBook?.positionUnit ?: ReadPositionUnit.PAGE.code,
                    startProgress = startProgress,
                    endProgress = endProgress,
                    onStartPositionChange = { startProgress = it },
                    onEndPositionChange = { endProgress = it }
                )
                
                // 标记完成复选框
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = markAsFinished,
                        onCheckedChange = { markAsFinished = it }
                    )
                    Text("标记为已完成")
                }
            }
        },
        confirmButton = {

                
                // 保存按钮
                Button(
                    onClick = {
                        viewModel.saveTimerRecord(
                            bookId = selectedBook?.id,
                            startProgress = startProgress,
                            endProgress = endProgress,
                            notes = if (markAsFinished) "标记为已完成" else null,
                            markAsFinished = markAsFinished // 传递标记完成状态
                        )
                    },
                    enabled = selectedBook != null
                ) {
                    Text("保存")
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
                    }
                ) {
                    Text("继续阅读")
                }
                TextButton(onClick = {
                    viewModel.cancelSaveRecord()
                    onDismiss()
                }) {
                    Text("不保存")
                }
            }
        }
    )
}

/**
 * 书籍进度输入组件（与书籍编辑页面相同的逻辑）
 */
@Composable
fun BookProgressInput(
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
                            value.toIntOrNull()?.let { 
                                onStartPositionChange(it.toDouble())
                            }
                        }
                    },
                    label = { Text("起始页数") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                            value.toIntOrNull()?.let { 
                                onEndPositionChange(it.toDouble())
                            }
                        }
                    },
                    label = { Text("结束页数") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                                    value.toIntOrNull()?.let { 
                                        onStartPositionChange(it.toDouble())
                                    }
                                }
                            },
                            label = { Text("起始页数") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                                    value.toIntOrNull()?.let { 
                                        onEndPositionChange(it.toDouble())
                                    }
                                }
                            },
                            label = { Text("结束页数") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                                    value.toDoubleOrNull()?.let { 
                                        onStartPositionChange(it)
                                    }
                                }
                            },
                            label = { Text("起始进度 %") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
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
                                    value.toDoubleOrNull()?.let { 
                                        onEndPositionChange(it)
                                    }
                                }
                            },
                            label = { Text("结束进度 %") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
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
                                    value.toIntOrNull()?.let { 
                                        onStartPositionChange(it.toDouble())
                                    }
                                }
                            },
                            label = { Text("起始章节") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                                    value.toIntOrNull()?.let { 
                                        onEndPositionChange(it.toDouble())
                                    }
                                }
                            },
                            label = { Text("结束章节") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                                    value.toDoubleOrNull()?.let { 
                                        onStartPositionChange(it)
                                    }
                                }
                            },
                            label = { Text("起始进度") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
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
                                    value.toDoubleOrNull()?.let { 
                                        onEndPositionChange(it)
                                    }
                                }
                            },
                            label = { Text("结束进度") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
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
fun BookSelector(
    books: List<BookEntity>,
    selectedBook: BookEntity?,
    onBookSelect: (BookEntity) -> Unit
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
        BookSelectorDialog(
            books = books,
            onBookSelect = { book ->
                onBookSelect(book)
                showBookSelector = false
            },
            onDismiss = { showBookSelector = false }
        )
    }
}
