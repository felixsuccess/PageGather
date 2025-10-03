package com.anou.pagegather.ui.feature.reading

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.anou.pagegather.data.local.entity.BookEntity
import com.anou.pagegather.data.local.entity.ReadPositionUnit
import com.anou.pagegather.ui.feature.timer.TimerManager


/**
 * 简化版本的保存记录页面
 * 
 * 优势：
 * 1. 从TimerService获取会话数据，无需复杂参数传递
 * 2. 使用TimerManager统一保存逻辑
 * 3. 简化的UI和交互
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveRecordScreen(
    duration: Long = 0L,
    bookId: Long? = null,
    onNavigateBack: () -> Unit = {},
    onSaveComplete: () -> Unit = {},
    onNavigateToBookEdit: () -> Unit = {},
    onReturnToTimer: () -> Unit = {},  // 返回继续计时（不保存）
    onStartNewTimer: (Long) -> Unit = {}  // 保存并开始新计时
) {
    // 获取依赖注入的实例
    val timerManager: TimerManager = hiltViewModel()
    

    
    // UI状态
    var selectedBookId by remember { mutableStateOf(bookId) }
    var selectedBook by remember { mutableStateOf<BookEntity?>(null) }
    
    // 根据传入的bookId获取书籍信息
    LaunchedEffect(bookId) {
        if (bookId != null) {
            try {
                // 从数据库获取真实的书籍信息
                val book = timerManager.getBookById(bookId)
                if (book != null) {
                    selectedBook = book
                    selectedBookId = book.id
                } else {
                    // 如果没找到书籍，创建一个默认对象
                    selectedBook = BookEntity(
                        id = bookId,
                        name = "书籍 ID: $bookId",
                        author = "未知作者",
                        type = 0,
                        totalPagination = 300,
                        readPosition = 0.0
                    )
                }
            } catch (e: Exception) {
                // 获取失败时使用默认值
                selectedBook = BookEntity(
                    id = bookId,
                    name = "书籍 ID: $bookId",
                    author = "未知作者",
                    type = 0,
                    totalPagination = 300,
                    readPosition = 0.0
                )
            }
        }
    }
    
    var startProgress by remember { mutableStateOf(0.0) }
    var endProgress by remember { mutableStateOf(0.0) }
    var notes by remember { mutableStateOf("") }
    var markAsFinished by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var showBookSelector by remember { mutableStateOf(false) }
    
    // 当选择书籍时，自动带出书籍的当前阅读进度
    LaunchedEffect(selectedBook) {
        selectedBook?.let { book ->
            // 直接使用书籍信息中的当前阅读进度
            startProgress = book.readPosition
        }
    }
    
    // 获取TimerManager状态用于监听保存完成
    val managerState by timerManager.uiState.collectAsState()
    
    // 监听保存完成 - 简化逻辑
    LaunchedEffect(managerState.showSaveSuccess, managerState.isLoading, managerState.error) {
        println("DEBUG: 状态变化 - showSaveSuccess: ${managerState.showSaveSuccess}, isLoading: ${managerState.isLoading}, error: ${managerState.error}")
        
        if (managerState.showSaveSuccess) {
            println("DEBUG: 保存成功，调用 onSaveComplete()")
            isLoading = false // 重置加载状态
            timerManager.resetSaveSuccessState() // 重置成功状态
            onSaveComplete()
        }
        
        if (managerState.error != null) {
            println("DEBUG: 保存失败 - ${managerState.error}")
            isLoading = false // 重置加载状态
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "保存阅读记录",
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
            // 可滚动内容
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
            // 计时信息卡片（简化版本）
            if (duration > 0) {
                TimingInfoCard(
                    duration = duration,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // 书籍选择卡片（必选）
            BookSelectionCard(
                selectedBook = selectedBook,
                onBookSelect = { book -> 
                    selectedBook = book
                    selectedBookId = book.id
                },
                onShowBookSelector = { showBookSelector = true },
                onAddNewBook = onNavigateToBookEdit,
                modifier = Modifier.fillMaxWidth()
            )
            
            // 书籍选择对话框
            if (showBookSelector) {
                com.anou.pagegather.ui.feature.reading.components.BookSelectorDialog(
                    selectedBook = selectedBook,
                    onBookSelect = { book ->
                        selectedBook = book
                        selectedBookId = book.id
                        showBookSelector = false
                    },
                    onDismiss = { showBookSelector = false },
                    onNavigateToAddBook = onNavigateToBookEdit
                )
            }
            
            // 阅读详情卡片（进度 + 笔记）
            selectedBook?.let { book ->
                ReadingDetailsCard(
                    selectedBook = book,
                    startProgress = startProgress,
                    endProgress = endProgress,
                    onStartProgressChange = { startProgress = it },
                    onEndProgressChange = { endProgress = it },
                    markAsFinished = markAsFinished,
                    onMarkAsFinishedChange = { markAsFinished = it },
                    notes = notes,
                    onNotesChange = { notes = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            }
            
            // 底部按钮区域
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 保存完成按钮（主要操作 - 保存并返回）
                Button(
                    onClick = {
                        selectedBookId?.let { bookId ->
                            isLoading = true
                            timerManager.saveRecordManually(
                                bookId = bookId,
                                startProgress = startProgress,
                                endProgress = endProgress,
                                notes = notes,
                                duration = duration,
                                startTime = System.currentTimeMillis() - duration,
                                endTime = System.currentTimeMillis()
                            )
                            // 保存操作会触发 LaunchedEffect 中的 onSaveComplete()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedBookId != null && !isLoading && !managerState.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (isLoading || managerState.isLoading) "保存中..." 
                        else if (selectedBookId == null) "请先选择书籍"
                        else "保存完成"
                    )
                }
                
                // 第二行：其他操作按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 返回继续计时按钮（不保存当前记录）
                    OutlinedButton(
                        onClick = onReturnToTimer,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "继续计时",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    
                    // 保存并开始新计时
                    Button(
                        onClick = {
                            selectedBookId?.let { bookId ->
                                isLoading = true
                                timerManager.saveRecordManually(
                                    bookId = bookId,
                                    startProgress = startProgress,
                                    endProgress = endProgress,
                                    notes = notes,
                                    duration = duration,
                                    startTime = System.currentTimeMillis() - duration,
                                    endTime = System.currentTimeMillis()
                                )
                                // 直接开始新计时
                                onStartNewTimer(bookId)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = selectedBookId != null && !isLoading && !managerState.isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            if (isLoading || managerState.isLoading) "保存中..." 
                            else if (selectedBookId == null) "请先选择书籍"
                            else "保存并开始新计时",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

/**
 * 计时信息卡片（简化版本）
 */
@Composable
private fun TimingInfoCard(
    duration: Long,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "本次计时信息",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "计时时长：",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = formatDuration(duration),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * 格式化时长
 */
private fun formatDuration(durationMs: Long): String {
    val totalSeconds = durationMs / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    
    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}

/**
 * 书籍选择卡片
 */
@Composable
private fun BookSelectionCard(
    selectedBook: BookEntity?,
    onBookSelect: (BookEntity) -> Unit,
    onShowBookSelector: () -> Unit,
    onAddNewBook: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (selectedBook != null) 
                MaterialTheme.colorScheme.surfaceVariant 
            else MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "选择书籍 *",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (selectedBook != null) 
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onErrorContainer
                )
                
                if (selectedBook != null) {
                    OutlinedButton(
                        onClick = onShowBookSelector
                    ) {
                        Text("更换")
                    }
                }
            }
            
            if (selectedBook != null) {
                Column {
                    Text(
                        text = selectedBook.name ?: "未知书籍",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    if (!selectedBook.author.isNullOrEmpty()) {
                        Text(
                            text = "作者: ${selectedBook.author}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                Text(
                    text = "请选择要记录的书籍",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onShowBookSelector,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("选择书籍")
                    }
                    
                    OutlinedButton(
                        onClick = onAddNewBook,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("添加新书")
                    }
                }
            }
        }
    }
}

/**
 * 阅读详情卡片（进度 + 笔记的整合）
 */
@Composable
private fun ReadingDetailsCard(
    selectedBook: BookEntity,
    startProgress: Double,
    endProgress: Double,
    onStartProgressChange: (Double) -> Unit,
    onEndProgressChange: (Double) -> Unit,
    markAsFinished: Boolean,
    onMarkAsFinishedChange: (Boolean) -> Unit,
    notes: String,
    onNotesChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 卡片标题
            Text(
                text = "阅读详情",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            // 书籍信息摘要
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = selectedBook.name ?: "未知书籍",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                if (!selectedBook.author.isNullOrEmpty()) {
                    Text(
                        text = "作者: ${selectedBook.author}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // 分隔线
            androidx.compose.material3.HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
            
            // 阅读进度部分
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "阅读进度",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                
                // 根据书籍的进度单位显示不同的进度输入方式
                val positionUnit = ReadPositionUnit.entries.find { 
                    it.code == selectedBook.positionUnit 
                } ?: ReadPositionUnit.PAGE
                
                val (progressUnit, maxValue) = when (positionUnit) {
                    ReadPositionUnit.PAGE -> {
                        "页" to (selectedBook.totalPagination?.toDouble() ?: 999.0)
                    }
                    ReadPositionUnit.CHAPTER -> {
                        "章" to (selectedBook.totalChapters?.toDouble() ?: 99.0)
                    }
                    ReadPositionUnit.PERCENT -> {
                        "%" to 100.0
                    }
                }
                
                // 进度提示 - 选择书籍后显示
                if (selectedBook.readPosition > 0) {
                    val progressText = when (positionUnit) {
                        ReadPositionUnit.PERCENT -> "${selectedBook.readPosition.toInt()}%"
                        else -> "${selectedBook.readPosition.toInt()} $progressUnit"
                    }
                    Text(
                        text = "📖 已自动填入当前阅读进度：$progressText",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = if (startProgress == 0.0) "" else startProgress.toInt().toString(),
                        onValueChange = { 
                            if (it.isEmpty()) {
                                onStartProgressChange(0.0)
                            } else {
                                it.toIntOrNull()?.let { value ->
                                    if (value >= 0 && value <= maxValue) {
                                        onStartProgressChange(value.toDouble())
                                    }
                                }
                            }
                        },
                        label = { Text("开始 ($progressUnit)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = if (endProgress == 0.0) "" else endProgress.toInt().toString(),
                        onValueChange = { 
                            if (it.isEmpty()) {
                                onEndProgressChange(0.0)
                            } else {
                                it.toIntOrNull()?.let { value ->
                                    if (value >= 0 && value <= maxValue) {
                                        onEndProgressChange(value.toDouble())
                                    }
                                }
                            }
                        },
                        label = { Text("结束 ($progressUnit)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                
                // 进度提示和完成标记
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (positionUnit) {
                            ReadPositionUnit.PAGE -> 
                                "总页数: ${selectedBook.totalPagination} 页"
                            ReadPositionUnit.CHAPTER -> 
                                "总章节: ${selectedBook.totalChapters ?: 0} 章"
                            ReadPositionUnit.PERCENT -> 
                                "进度范围: 0-100%"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = markAsFinished,
                            onCheckedChange = onMarkAsFinishedChange
                        )
                        Text(
                            text = "已完成",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            
            // 分隔线
            androidx.compose.material3.HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
            
            // 阅读笔记部分
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "阅读笔记",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                
                OutlinedTextField(
                    value = notes,
                    onValueChange = onNotesChange,
                    placeholder = { Text("记录你的阅读感悟、重要内容或想法...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    maxLines = 4
                )
            }
        }
    }
}



/**
 * 格式化时间
 */
private fun formatTime(timestamp: Long): String {
    val date = java.util.Date(timestamp)
    val format = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
    return format.format(date)
}