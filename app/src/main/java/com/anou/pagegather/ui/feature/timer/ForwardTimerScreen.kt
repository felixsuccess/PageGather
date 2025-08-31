package com.anou.pagegather.ui.feature.timer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForwardTimerScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToSaveRecord: (Long, Long) -> Unit = { _, _ -> },
    newlyAddedBookId: Long? = null,
    viewModel: ForwardTimerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // 监听showSaveDialog状态，导航到保存记录页面
    LaunchedEffect(uiState.showSaveDialog) {
        if (uiState.showSaveDialog) {
            // 清除对话框状态
            viewModel.clearSaveDialog()
            // 导航到保存记录页面，传递计时数据
            onNavigateToSaveRecord(uiState.elapsedTime, uiState.tempStartTime)
        }
    }
    
    // 处理新添加的书籍ID
    LaunchedEffect(newlyAddedBookId) {
        if (newlyAddedBookId != null && newlyAddedBookId > 0) {
            viewModel.selectNewlyAddedBook(newlyAddedBookId)
        }
    }
    
    LaunchedEffect(newlyAddedBookId) {
        if (newlyAddedBookId != null && newlyAddedBookId > 0) {
            viewModel.selectNewlyAddedBook(newlyAddedBookId)
        }
    }
    
    LaunchedEffect(uiState.showSaveDialog) {
        if (uiState.showSaveDialog && uiState.status == TimerStatus.PAUSED) {
            onNavigateToSaveRecord(uiState.elapsedTime, uiState.tempStartTime)
            viewModel.clearSaveDialog()
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
            LargeTimerDisplay(
                uiState = uiState,
                viewModel = viewModel,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
            
            ForwardTimerControls(
                status = uiState.status,
                onStart = { viewModel.startTimer() },
                onPause = { viewModel.pauseTimer() },
                onStop = { viewModel.stopTimer() },
                onReset = { viewModel.fullReset() },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

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
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = viewModel.formatTime(uiState.elapsedTime),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
            )
            
            Text(
                text = when (uiState.status) {
                    TimerStatus.IDLE -> "准备开始"
                    TimerStatus.RUNNING -> "计时中"
                    TimerStatus.PAUSED -> "已暂停"
                },
                style = MaterialTheme.typography.titleMedium,
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