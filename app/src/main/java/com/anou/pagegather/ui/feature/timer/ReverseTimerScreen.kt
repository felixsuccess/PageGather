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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
fun ReverseTimerScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: ReverseTimerViewModel = hiltViewModel()
) {
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
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 书籍选择卡片（仅在IDLE状态显示）
                if (uiState.status == TimerStatus.IDLE) {
                    ReverseBookSelectionCard(
                        selectedBook = uiState.selectedBook,
                        onBookSelect = { book -> viewModel.selectBook(book) },
                        viewModel = viewModel
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 时间显示
                ReverseTimerDisplay(
                    uiState = uiState,
                    viewModel = viewModel
                )
                
                // 控制按钮
                ReverseTimerControls(
                    status = uiState.status,
                    onStart = { viewModel.startTimer() },
                    onPause = { viewModel.pauseTimer() },
                    onStop = { viewModel.stopTimer() },
                    onReset = { viewModel.fullReset() }
                )
            }
        }
        
        // 显示保存记录对话框（在PAUSED状态时显示）
        if (uiState.showSaveDialog && uiState.status == TimerStatus.PAUSED) {
            ReverseSaveRecordDialog(
                uiState = uiState,
                viewModel = viewModel,
                onDismiss = { viewModel.cancelSaveRecord() }
            )
        }
    }
}

/**
 * 反向计时器设置组件
 */
@Composable
fun ReverseTimerSetup(
    viewModel: ReverseTimerViewModel,
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
 * 时间显示组件
 */
@Composable
fun ReverseTimerDisplay(
    uiState: ReverseTimerUIState,
    viewModel: ReverseTimerViewModel
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
                text = viewModel.formatTime(uiState.remainingTime),
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
            
            // 显示已用时间
            if (uiState.elapsedTime > 0) {
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
fun ReverseTimerControls(
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
 * 书籍选择卡片
 */
@Composable
fun ReverseBookSelectionCard(
    selectedBook: BookEntity?,
    onBookSelect: (BookEntity) -> Unit,
    viewModel: ReverseTimerViewModel
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
        ReverseBookSelectorDialog(
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
fun ReverseBookSelectorDialog(
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
 * 保存记录对话框
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReverseSaveRecordDialog(
    uiState: ReverseTimerUIState,
    viewModel: ReverseTimerViewModel,
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
                ReverseBookSelector(
                    books = books,
                    selectedBook = selectedBook,
                    onBookSelect = { book -> selectedBook = book }
                )
                
                // 进度设置（使用与书籍编辑页面相同的逻辑）
                ReverseBookProgressInput(
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
                    viewModel.saveTimerRecord(
                        bookId = selectedBook?.id,
                        startProgress = startProgress,
                        endProgress = endProgress,
                        notes = if (markAsFinished) "标记为已完成" else null,
                        markAsFinished = markAsFinished // 传递标记完成状态
                    )
                    onDismiss() // 关闭对话框
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
fun ReverseBookProgressInput(
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
                            value.toIntOrNull()?.let { number -> 
                                onEndPositionChange(number.toDouble())
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
                                    value.toIntOrNull()?.let { number -> 
                                        onStartPositionChange(number.toDouble())
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
                                    value.toIntOrNull()?.let { number -> 
                                        onEndPositionChange(number.toDouble())
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
                                    value.toDoubleOrNull()?.let { number -> 
                                        onStartPositionChange(number)
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
                                    value.toDoubleOrNull()?.let { number -> 
                                        onEndPositionChange(number)
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
                                    value.toIntOrNull()?.let { number -> 
                                        onStartPositionChange(number.toDouble())
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
                                    value.toIntOrNull()?.let { number -> 
                                        onEndPositionChange(number.toDouble())
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
                                    value.toDoubleOrNull()?.let { number -> 
                                        onStartPositionChange(number)
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
                                    value.toDoubleOrNull()?.let { number -> 
                                        onEndPositionChange(number)
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
fun ReverseBookSelector(
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
        ReverseBookSelectorDialog(
            books = books,
            onBookSelect = { book ->
                onBookSelect(book)
                showBookSelector = false
            },
            onDismiss = { showBookSelector = false }
        )
    }
}