package com.anou.pagegather.ui.feature.bookshelf.tag

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
import androidx.compose.material.icons.filled.Label
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
import com.anou.pagegather.data.local.entity.TagEntity
import com.anou.pagegather.ui.feature.bookshelf.BookListViewModel

/**
 * 按标签分组显示书籍的内容
 */
@Composable
fun TagBookListContent(
    viewModel: BookListViewModel,
    isGridMode: Boolean,
    onTagClick: (tag: TagEntity) -> Unit
) {
    // 从ViewModel获取标签数据
    val bookListState by viewModel.bookListState.collectAsState()
    val tags = bookListState.availableTags

    if (tags.isEmpty()) {
        // 空状态
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "暂无标签",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "添加标签来管理你的书籍",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
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
                items(tags.size) { index ->
                    val tag = tags[index]
                    TagGridItem(
                        tag = tag,
                        viewModel = viewModel,
                        onClick = { onTagClick(tag) }
                    )
                }
            }
        } else {
            // 列表模式
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(tags.size) { index ->
                    val tag = tags[index]
                    TagListItem(
                        tag = tag,
                        viewModel = viewModel,
                        onClick = { onTagClick(tag) }
                    )
                }
            }
        }
    }
}

/**
 * 标签列表项
 */
@Composable
private fun TagListItem(
    tag: TagEntity,
    viewModel: BookListViewModel,
    onClick: () -> Unit
) {
    // 获取该标签下的书籍数量
    val bookCount by viewModel.getTagBookCount(tag.id).collectAsState(initial = 0)

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
                // 标签名称和书籍数量
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 标签颜色指示器
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(getTagColor(tag.color))
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = tag.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

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
 * 标签卡片组件（网格模式）
 */
@Composable
private fun TagGridItem(
    tag: TagEntity,
    viewModel: BookListViewModel,
    onClick: () -> Unit
) {
    // 获取该标签下的书籍数量
    val bookCount by viewModel.getTagBookCount(tag.id).collectAsState(initial = 0)

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
            // 标签图标
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Label,
                    contentDescription = null,
                    tint = getTagColor(tag.color),
                    modifier = Modifier.size(48.dp)
                )
            }

            // 标签名称和书籍数量
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // 标签颜色指示器
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(getTagColor(tag.color))
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = tag.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

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
 * 获取标签颜色
 */
@Composable
private fun getTagColor(color: String?): Color {
    return when (color) {
        "red" -> Color(0xFFE57373)
        "blue" -> Color(0xFF64B5F6)
        "green" -> Color(0xFF81C784)
        "yellow" -> Color(0xFFFFF176)
        "purple" -> Color(0xFFBA68C8)
        "orange" -> Color(0xFFFFB74D)
        "pink" -> Color(0xFFF06292)
        "indigo" -> Color(0xFF7986CB)
        else -> MaterialTheme.colorScheme.primary
    }
}