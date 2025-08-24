package com.anou.pagegather.ui.feature.quickactions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class QuickActionItem(
    val title: String,
    val icon: String,
    val status: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickActionsScreen() {
    val quickActions = listOf(
        QuickActionItem("阅读计时", "⏱️", "已实现"),
        QuickActionItem("添加书籍", "📚", "开发中"),
        QuickActionItem("写笔记", "📝", "开发中"),
        QuickActionItem("全局搜索", "🔍", "开发中"),
        QuickActionItem("今日统计", "📈", "开发中"),
        QuickActionItem("阅读目标", "🎯", "开发中"),
        QuickActionItem("扫码添书", "📱", "规划中"),
        QuickActionItem("随机书摘", "💡", "规划中")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "快捷导航",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quickActions) { action ->
                QuickActionCard(
                    title = action.title,
                    icon = action.icon,
                    status = action.status,
                    isAvailable = action.status == "已实现",
                    onClick = { /* 暂未实现 */ }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuickActionCard(
    title: String,
    icon: String,
    status: String,
    isAvailable: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = if (isAvailable) onClick else { {} },
        enabled = isAvailable,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = icon,
                style = MaterialTheme.typography.headlineMedium
            )
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (isAvailable) 
                        MaterialTheme.colorScheme.onSurface 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Text(
                text = status,
                style = MaterialTheme.typography.bodySmall,
                color = when (status) {
                    "已实现" -> MaterialTheme.colorScheme.primary
                    "开发中" -> MaterialTheme.colorScheme.onSurfaceVariant
                    "规划中" -> MaterialTheme.colorScheme.outline
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

// 快捷功能占位界面
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickNoteScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("快速记录") },
                navigationIcon = {
                    IconButton(onClick = { /* 返回 */ }) {
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
            Text("快速记录功能开发中...")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickReviewScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("快速复习") },
                navigationIcon = {
                    IconButton(onClick = { /* 返回 */ }) {
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
            Text("快速复习功能开发中...")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickBookmarkScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("快速书签") },
                navigationIcon = {
                    IconButton(onClick = { /* 返回 */ }) {
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
            Text("快速书签功能开发中...")
        }
    }
}
