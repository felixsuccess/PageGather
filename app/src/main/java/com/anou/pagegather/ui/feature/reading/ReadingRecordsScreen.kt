package com.anou.pagegather.ui.feature.reading

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.anou.pagegather.data.local.entity.ReadingRecordEntity
import com.anou.pagegather.data.local.entity.RecordType
import java.text.SimpleDateFormat
import java.util.*

/**
 * 阅读记录列表页面
 * 展示所有阅读记录的列表视图
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingRecordsScreen(
    onBackClick: () -> Unit,
    viewModel: ReadingRecordsViewModel = hiltViewModel()
) {
    val readingRecords by viewModel.readingRecords.collectAsState()
    var showFilterDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("阅读记录") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "筛选"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (readingRecords.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "暂无阅读记录",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(readingRecords) { record ->
                    ReadingRecordItem(
                        record = record,
                        onDeleteClick = { viewModel.deleteReadingRecord(record) }
                    )
                }
            }
        }
        
        // 筛选对话框
        if (showFilterDialog) {
            FilterDialog(
                onDismiss = { showFilterDialog = false },
                onApply = { date, bookId -> 
                    viewModel.setDateFilter(date)
                    // TODO: 传递其他筛选条件到ViewModel
                    showFilterDialog = false 
                },
                currentDate = null,
                currentBookId = null
            )
        }
    }
}

/**
 * 阅读记录项组件
 */
@Composable
private fun ReadingRecordItem(
    record: ReadingRecordEntity,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 顶部行：时间信息和删除按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatDateTime(record.startTime),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "删除记录"
                    )
                }
            }
            
            // 记录类型
            val recordType = if (record.recordType == RecordType.PRECISE.ordinal) "计时记录" else "手动记录"
            Text(
                text = recordType,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 阅读时长
            Text(
                text = "阅读时长: ${formatDuration(record.duration)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            
            // 进度信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "开始进度: ${String.format("%.1f", record.startProgress)}%",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "结束进度: ${String.format("%.1f", record.endProgress)}%",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            // 备注信息
            record.notes?.let { notes ->
                if (notes.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "备注: $notes",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * 格式化时间显示
 */
private fun formatDateTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

/**
 * 格式化持续时间显示
 */
private fun formatDuration(milliseconds: Long): String {
    val seconds = milliseconds / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    
    return when {
        hours > 0 -> "${hours}小时${minutes % 60}分钟"
        minutes > 0 -> "${minutes}分钟${seconds % 60}秒"
        else -> "${seconds}秒"
    }
}

/**
 * 筛选对话框
 */
@Composable
private fun FilterDialog(
    onDismiss: () -> Unit,
    onApply: (date: String?, bookId: Long?) -> Unit,
    currentDate: String?,
    currentBookId: Long?
) {
    var selectedDate by remember { mutableStateOf(currentDate) }
    var selectedBookId by remember { mutableStateOf(currentBookId) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("筛选条件")
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 日期筛选
                Text(
                    text = "按日期筛选",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val calendar = Calendar.getInstance()
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            selectedDate = dateFormat.format(calendar.time)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("今天")
                    }
                    
                    Button(
                        onClick = {
                            selectedDate = null
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("清除")
                    }
                }
                
                Text(
                    text = "选定日期: ${selectedDate ?: "无"}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onApply(selectedDate, selectedBookId)
                }
            ) {
                Text("应用")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
