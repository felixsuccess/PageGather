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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.anou.pagegather.data.local.entity.BookEntity
import com.anou.pagegather.data.local.entity.BookType
import com.anou.pagegather.ui.feature.reading.components.BookSelectorDialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveRecordScreen(
    source: RecordSource,
    onNavigateBack: () -> Unit,
    onNavigateToAddBook: () -> Unit,
    elapsedTime: Long? = null,
    startTime: Long? = null,
    selectedBookId: Long? = null,
    viewModel: SaveRecordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
        LaunchedEffect(source, elapsedTime, startTime, selectedBookId) {
        if (selectedBookId != null && selectedBookId > 0) {
            println("SaveRecordScreen: 接收到书籍ID = $selectedBookId")
            viewModel.selectNewlyAddedBook(selectedBookId)
        } else {
            println("SaveRecordScreen: selectedBookId 为空或无效: $selectedBookId")
            viewModel.initialize(source, elapsedTime, startTime, null)
        }
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (uiState.source == RecordSource.TIMER) {
                item {
                    TimerInfoCard(
                        elapsedTime = uiState.elapsedTime,
                        startTime = uiState.startTime
                    )
                }
            }

            item {
                DataInfoCard(
                    selectedBook = uiState.selectedBook,
                    onBookSelect = { book -> viewModel.selectBook(book) },
                    onNavigateToAddBook = onNavigateToAddBook,
                    viewModel = viewModel,
                    markAsFinished = uiState.markAsFinished,
                    onMarkAsFinishedChange = { marked -> viewModel.setMarkAsFinished(marked) },
                    startProgress = uiState.startProgress,
                    endProgress = uiState.endProgress,
                    onStartProgressChange = { progress -> viewModel.setStartProgress(progress) },
                    onEndProgressChange = { progress -> viewModel.setEndProgress(progress) },

                )
            }
            
            item {
                ActionButtonsCard(
                    onSave = { viewModel.saveRecord() },
                    onContinueReading = { viewModel.continueReading() },
                    onCancel = onNavigateBack,
                    isLoading = uiState.isLoading,
                    canSave = uiState.selectedBook != null
                )
            }
        }
    }
}

@Composable
fun TimerInfoCard(
    elapsedTime: Long,
    startTime: Long
) {
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
                text = "本次阅读信息",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "阅读时长:")
                Text(
                    text = formatTime(elapsedTime),
                    fontWeight = FontWeight.Medium
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "开始时间:")
                Text(
                    text = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(Date(startTime))
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "结束时间:")
                Text(
                    text = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(Date(startTime + elapsedTime))
                )
            }
        }
    }
}

@Composable
fun DataInfoCard(
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
                text = "本次阅读信息",
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
                  //  verticalAlignment = Alignment.CenterVertically
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
            var endProgressText by remember { mutableStateOf(endProgress.toString()) }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "阅读位置:")
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
            books = books,
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
    onContinueReading: () -> Unit,
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
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onContinueReading,
                    enabled = !isLoading,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("继续阅读")
                }
                
                TextButton(
                    onClick = onCancel,
                    enabled = !isLoading,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("取消")
                }
            }
        }
    }
}

private fun formatTime(timeInMs: Long): String {
    val totalSeconds = timeInMs / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    
    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}