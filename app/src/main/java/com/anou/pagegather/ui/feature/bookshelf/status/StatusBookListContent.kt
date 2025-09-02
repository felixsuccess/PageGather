package com.anou.pagegather.ui.feature.bookshelf.status

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.anou.pagegather.data.local.entity.ReadStatus
import com.anou.pagegather.ui.feature.bookshelf.BookListViewModel

/**
 * 按状态分组显示书籍的内容
 */
@Composable
fun StatusBookListContent(
    viewModel: BookListViewModel,
    isGridMode: Boolean,
    onStatusClick: (status: ReadStatus) -> Unit
) {
    // 定义所有可能的阅读状态
    val statuses = listOf(
        ReadStatus.WANT_TO_READ,
        ReadStatus.READING,
        ReadStatus.FINISHED,
        ReadStatus.ABANDONED
    )

    // 根据显示模式选择布局
    if (isGridMode) {
        // 网格模式
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(statuses.size) { index ->
                val status = statuses[index]
                StatusGridItem(
                    status = status,
                    viewModel = viewModel,
                    onClick = { onStatusClick(status) }
                )
            }
        }
    } else {
        // 列表模式
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(statuses.size) { index ->
                val status = statuses[index]
                StatusListItem(
                    status = status,
                    viewModel = viewModel,
                    onClick = { onStatusClick(status) }
                )
            }
        }
    }
}

/**
 * 状态列表项
 */
@Composable
private fun StatusListItem(
    status: ReadStatus,
    viewModel: BookListViewModel,
    onClick: () -> Unit
) {
    // 获取该状态下的书籍数量
    val bookCount by viewModel.getStatusBookCount(status).collectAsState(initial = 0)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 状态名称和书籍数量
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = getStatusDisplayName(status),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "$bookCount 本",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // 右箭头
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "查看详情",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * 状态卡片组件（网格模式）
 */
@Composable
private fun StatusGridItem(
    status: ReadStatus,
    viewModel: BookListViewModel,
    onClick: () -> Unit
) {
    // 获取该状态下的书籍数量
    val bookCount by viewModel.getStatusBookCount(status).collectAsState(initial = 0)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 状态图标
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = null,
                    tint = getStatusColor(status),
                    modifier = Modifier.size(48.dp)
                )
            }

            // 状态名称和书籍数量
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = getStatusDisplayName(status),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "$bookCount 本",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * 获取状态显示名称
 */
private fun getStatusDisplayName(status: ReadStatus): String {
    return when (status) {
        ReadStatus.WANT_TO_READ -> "想读"
        ReadStatus.READING -> "在读"
        ReadStatus.FINISHED -> "已读"
        ReadStatus.ABANDONED -> "放弃"
    }
}

/**
 * 获取状态对应的颜色
 */
@Composable
private fun getStatusColor(status: ReadStatus): Color {
    return when (status) {
        ReadStatus.WANT_TO_READ -> MaterialTheme.colorScheme.primary
        ReadStatus.READING -> MaterialTheme.colorScheme.secondary
        ReadStatus.FINISHED -> MaterialTheme.colorScheme.tertiary
        ReadStatus.ABANDONED -> MaterialTheme.colorScheme.error
    }
}