package com.anou.pagegather.ui.feature.reading

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.anou.pagegather.data.local.entity.BookEntity
import com.anou.pagegather.data.local.entity.BookType
import com.anou.pagegather.ui.feature.reading.components.BookSelectorDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualRecordScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddBook: () -> Unit,
    selectedBookId: Long? = null,
    viewModel: SaveRecordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // 手动记录的时间状态
    var selectedDate: Long by remember { mutableStateOf(System.currentTimeMillis()) }
    var startTime: Long by remember { mutableStateOf(System.currentTimeMillis()) }
    var endTime: Long by remember { mutableStateOf(System.currentTimeMillis() + 3600000) } // 默认1小时后
    var showDatePicker: Boolean by remember { mutableStateOf(false) }
    var showStartTimePicker: Boolean by remember { mutableStateOf(false) }
    var showEndTimePicker: Boolean by remember { mutableStateOf(false) }
    
    // 初始化ViewModel
    LaunchedEffect(Unit) {
        viewModel.initialize(RecordSource.MANUAL, 0L, System.currentTimeMillis(), selectedBookId)
    }
    
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateBack()
        }
    }
    
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
                        text = "手动添加阅读记录",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                DateInfoCard(
                    selectedDate = selectedDate,
                    startTime = startTime,
                    endTime = endTime,
                    onDateClick = { showDatePicker = true },
                    onStartTimeClick = { showStartTimePicker = true },
                    onEndTimeClick = { showEndTimePicker = true }
                )
            }
            
            item {
                ManualDataInfoCard(
                    selectedBook = uiState.selectedBook,
                    onBookSelect = { book -> viewModel.selectBook(book) },
                    onNavigateToAddBook = onNavigateToAddBook,
                    viewModel = viewModel,
                    markAsFinished = uiState.markAsFinished,
                    onMarkAsFinishedChange = { marked -> viewModel.setMarkAsFinished(marked) },
                    startProgress = uiState.startProgress,
                    endProgress = uiState.endProgress,
                    onStartProgressChange = { progress -> viewModel.setStartProgress(progress) },
                    onEndProgressChange = { progress -> viewModel.setEndProgress(progress) }
                )
            }
            
            item {
                ActionButtonsCard(
                    onSave = { 
                        // 计算阅读时长
                        val duration = endTime - startTime
                        // 直接调用ViewModel的保存方法，传递计算好的参数
                        viewModel.saveManualRecord(duration, startTime)
                    },
                    onCancel = onNavigateBack,
                    isLoading = uiState.isLoading,
                    canSave = uiState.selectedBook != null
                )
            }
        }
    }
    
    // 日期选择器对话框
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        selectedDate = it
                        // 更新开始和结束时间的日期部分
                        startTime = updateDatePart(startTime, it)
                        endTime = updateDatePart(endTime, it)
                    }
                    showDatePicker = false
                }) {
                    Text("确认")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("取消")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    
    // 开始时间选择器对话框
    if (showStartTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = getHour(startTime),
            initialMinute = getMinute(startTime)
        )
        DatePickerDialog(
            onDismissRequest = { showStartTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    startTime = updateTimePart(selectedDate, timePickerState.hour, timePickerState.minute)
                    // 确保结束时间不早于开始时间
                    if (endTime < startTime) {
                        endTime = startTime + 3600000 // 默认1小时
                    }
                    showStartTimePicker = false
                }) {
                    Text("确认")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartTimePicker = false }) {
                    Text("取消")
                }
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }
    
    // 结束时间选择器对话框
    if (showEndTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = getHour(endTime),
            initialMinute = getMinute(endTime)
        )
        DatePickerDialog(
            onDismissRequest = { showEndTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    endTime = updateTimePart(selectedDate, timePickerState.hour, timePickerState.minute)
                    showEndTimePicker = false
                }) {
                    Text("确认")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndTimePicker = false }) {
                    Text("取消")
                }
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }
}

// 辅助函数：更新时间戳的日期部分
private fun updateDatePart(originalTime: Long, newDate: Long): Long {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = originalTime
    
    val newCalendar = Calendar.getInstance()
    newCalendar.timeInMillis = newDate
    
    // 保留原时间的时分秒
    newCalendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY))
    newCalendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE))
    newCalendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND))
    newCalendar.set(Calendar.MILLISECOND, calendar.get(Calendar.MILLISECOND))
    
    return newCalendar.timeInMillis
}

// 辅助函数：根据日期和时分创建时间戳
private fun updateTimePart(date: Long, hour: Int, minute: Int): Long {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = date
    calendar.set(Calendar.HOUR_OF_DAY, hour)
    calendar.set(Calendar.MINUTE, minute)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}

// 辅助函数：获取小时
private fun getHour(time: Long): Int {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = time
    return calendar.get(Calendar.HOUR_OF_DAY)
}

// 辅助函数：获取分钟
private fun getMinute(time: Long): Int {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = time
    return calendar.get(Calendar.MINUTE)
}

@Composable
fun DateInfoCard(
    selectedDate: Long,
    startTime: Long,
    endTime: Long,
    onDateClick: () -> Unit,
    onStartTimeClick: () -> Unit,
    onEndTimeClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "阅读时间",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDateClick() }
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "日期:")
                Text(
                    text = dateFormat.format(Date(selectedDate)),
                    fontWeight = FontWeight.Medium
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onStartTimeClick() }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "开始时间:")
                    Text(
                        text = timeFormat.format(Date(startTime)),
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onEndTimeClick() }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "结束时间:")
                    Text(
                        text = timeFormat.format(Date(endTime)),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// 重用现有的DataInfoCard和ActionButtonsCard组件
@Composable
fun ManualDataInfoCard(
    selectedBook: BookEntity?,
    onBookSelect: (BookEntity) -> Unit,
    onNavigateToAddBook: () -> Unit,
    markAsFinished: Boolean,
    onMarkAsFinishedChange: (Boolean) -> Unit,
    viewModel: SaveRecordViewModel,
    startProgress: Double,
    endProgress: Double,
    onStartProgressChange: (Double) -> Unit,
    onEndProgressChange: (Double) -> Unit
) {
    var showBookSelector by remember { mutableStateOf(false) }

    val books by viewModel.getAllBooks().collectAsState(initial = emptyList())

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "阅读信息",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "阅读书籍:")
                Row(
                    modifier = Modifier
                        .clickable { showBookSelector = true }
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (selectedBook != null) {
                            Text(
                                text = selectedBook.name ?: "",
                                fontWeight = FontWeight.Medium
                            )
                            selectedBook.author?.let { author ->
                                Text(
                                    text = author,
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        } else {
                            Text(
                                text = "选择书籍",
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "选择书籍",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            var startProgressText by remember { mutableStateOf(startProgress.toString()) }
            var endProgressText by remember { mutableStateOf(endProgress.toString()) }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "开始位置:")
                if ( selectedBook == null || selectedBook.type == BookType.PAPER_BOOK.code    ) {
                OutlinedTextField(
                    value = startProgressText,
                    onValueChange = { value ->
                        if (value.isEmpty() || value.all { it.isDigit() }) {
                            startProgressText = value
                            value.toIntOrNull()?.let { number ->
                                onStartProgressChange(number.toDouble())
                            }
                        }
                    },
                    label = { Text("开始页") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                )

                Text("页")
                } else {
                OutlinedTextField(
                    value = startProgressText,
                    onValueChange = { value ->
                        val number = value.toDoubleOrNull()
                        if (number != null && number in 0.0..100.0) {
                            startProgressText = value
                            onStartProgressChange(number)
                        } else if (value.isEmpty()) {
                            startProgressText = value
                        }
                    },
                    label = { Text("开始进度") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                )

                Text("%")
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "结束位置:")
                if ( selectedBook == null || selectedBook.type == BookType.PAPER_BOOK.code    ) {
                OutlinedTextField(
                    value = endProgressText,
                    onValueChange = { value ->
                        if (value.isEmpty() || value.all { it.isDigit() }) {
                            endProgressText = value
                            value.toIntOrNull()?.let { number ->
                                onEndProgressChange(number.toDouble())
                            }
                        }
                    },
                    label = { Text("结束页") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                )

                Text("页")
                } else {
                OutlinedTextField(
                    value = endProgressText,
                    onValueChange = { value ->
                        val number = value.toDoubleOrNull()
                        if (number != null && number in 0.0..100.0) {
                            endProgressText = value
                            onEndProgressChange(number)
                        } else if (value.isEmpty()) {
                            endProgressText = value
                        }
                    },
                    label = { Text("结束进度") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                )

                Text("%")
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = markAsFinished,
                    onCheckedChange = onMarkAsFinishedChange
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "标记为已完成",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

    if (showBookSelector) {
        BookSelectorDialog(
            selectedBook = selectedBook,
            onBookSelect = { book ->
                onBookSelect(book)
                showBookSelector = false
            },
            onDismiss = { showBookSelector = false },
            onNavigateToAddBook = {
                showBookSelector = false
                onNavigateToAddBook()
            }
        )
    }
}

@Composable
fun ActionButtonsCard(
    onSave: () -> Unit,
    onCancel: () -> Unit,
    isLoading: Boolean,
    canSave: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onSave,
                enabled = canSave && !isLoading,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("保存记录")
            }
            
            TextButton(
                onClick = onCancel,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("取消")
            }
        }
    }
}