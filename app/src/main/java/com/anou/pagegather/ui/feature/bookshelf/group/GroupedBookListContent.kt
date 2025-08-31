package com.anou.pagegather.ui.feature.bookshelf.group

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anou.pagegather.ui.feature.bookshelf.BookListViewModel
import com.anou.pagegather.ui.feature.bookshelf.booklist.GRID_COLUMNS
import com.anou.pagegather.ui.feature.bookshelf.booklist.GRID_PADDING
import com.anou.pagegather.ui.feature.bookshelf.booklist.GRID_SPACING

@Composable
fun GroupedBookListContent(
    viewModel: BookListViewModel,
    isGridMode: Boolean, // 添加显示模式参数
    onGroupClick: (groupId: Long, groupName: String) -> Unit
) {
    // 从ViewModel获取分组数据
    val bookListState by viewModel.bookListState.collectAsState()
    val groups = bookListState.availableGroups.map { group ->
        GroupInfo(group.id, group.name, group.getDisplayName())
    }

    if (groups.isEmpty()) {
        // 空状态
        Box(
            modifier = Modifier.Companion.fillMaxSize(),
            contentAlignment = Alignment.Companion.Center
        ) {
            Column(
                horizontalAlignment = Alignment.Companion.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = null,
                    modifier = Modifier.Companion.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.Companion.height(16.dp))

                Text(
                    text = "暂无分组",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Companion.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp
                )

                Text(
                    text = "创建分组来管理你的书籍",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            }
        }
    } else {
        // 根据显示模式选择布局
        if (isGridMode) {
            // 网格模式
            LazyVerticalGrid(
                columns =  GRID_COLUMNS,
                contentPadding = GRID_PADDING,
                modifier = Modifier.Companion.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy( GRID_SPACING),
                horizontalArrangement = Arrangement.spacedBy( GRID_SPACING)
            ) {
                items(groups.size) { index ->
                    val group = groups[index]
                    GroupGridItem(
                        group = group,
                        onClick = { onGroupClick(group.id, group.name) }
                    )
                }
            }
        } else {
            // 列表模式
            LazyColumn(
                modifier = Modifier.Companion.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(groups.size) { index ->
                    val group = groups[index]
                    GroupListItem(
                        group = group,
                        onClick = { onGroupClick(group.id, group.name) }
                    )
                }
            }
        }
    }
}

@Composable
private fun GroupGridItem(
    group: GroupInfo,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(16.dp),
    ) {
        // 分组图标
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                //.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            ,
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Folder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(60.dp)
            )
        }

        Spacer(modifier = Modifier.Companion.width(16.dp))

        // 分组信息
        Column(
            modifier = Modifier   .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = group.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Companion.Ellipsis,
                fontSize = 15.sp
            )


        }


    }
}

@Composable
private fun GroupListItem(
    group: GroupInfo,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 分组图标
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Folder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.Companion.width(16.dp))

        Text(
            modifier =   Modifier.weight(1f),
            text = group.name,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp
        )

        // 右侧箭头指示器
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}