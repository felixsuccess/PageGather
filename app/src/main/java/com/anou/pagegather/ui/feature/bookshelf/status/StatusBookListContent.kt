package com.anou.pagegather.ui.feature.bookshelf.status

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
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
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(statuses) { status ->
                    StatusListItem(
                        status = status,
                        viewModel = viewModel,
                        onClick = { onStatusClick(status) }
                    )
                }
            }
        }
    }
}

/**
 * 状态列表项 - 仿微信读书列表样式
 */
@Composable
private fun StatusListItem(
    status: ReadStatus,
    viewModel: BookListViewModel,
    onClick: () -> Unit
) {
    // 获取该状态下的书籍数量
    val bookCount by viewModel.getStatusBookCount(status).collectAsState(initial = 0)
    // 获取该状态下的前5本书
    val books by viewModel.getBooksByStatus(status.code).collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        // 列表项内容
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 状态信息和箭头
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 状态名称和书籍数量
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = status.message,
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

        // 书籍封面预览区域
        if (books.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 显示最多5本书的封面
                for (book in books.take(5)) {
                    AsyncImage(
                        model = book.coverUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(0.72f)
                            .clip(RoundedCornerShape(4.dp)),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                }

                // 如果书籍数量不足5本，添加空白占位
                repeat(5 - books.size.coerceAtMost(5)) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        // 分隔线
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            thickness = DividerDefaults.Thickness, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onClick)
            .padding(8.dp), // 添加一些内边距
        horizontalAlignment = Alignment.Start // 左对齐，与微信读书一致
    ) {
        // 状态预览（使用该状态下的书籍封面拼贴）
        StatusPreview(
            status = status,
            viewModel = viewModel,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.72f) // 标准书籍比例
                .clip(RoundedCornerShape(1.dp)) // 微信读书封面几乎没有圆角
        )
        
        Spacer(modifier = Modifier.height(6.dp))
        
        // 状态名称和书籍数量
        Text(
            text = status.message,
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

/**
 * 状态预览组件，显示该状态下的前几本书的封面拼贴
 */
@Composable
private fun StatusPreview(
    status: ReadStatus,
    viewModel: BookListViewModel,
    modifier: Modifier = Modifier
) {
    // 获取该状态下的前9本书
    val books by viewModel.getBooksByStatus(status.code).collectAsState(initial = emptyList())

    Box(
        modifier = modifier
            .aspectRatio(0.72f) // 添加标准书籍比例，与BookShelfDefaultBookGridItem保持一致
            .background(Color(0xFFF5F5F5))
            .padding(8.dp)
    ) {
        if (books.isEmpty()) {
            // 没有书籍时显示状态名称
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = null,
                    tint = status.getColor(),
                    modifier = Modifier.size(48.dp)
                )
            }
        } else {
            // 根据书籍数量选择不同的布局
            when {
                books.size == 1 -> {
                    // 只有一本书时，显示单封面
                    BookCover(
                        coverUrl = books[0].coverUrl,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                books.size <= 4 -> {
                    // 2-4本书时，显示2x2网格
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            BookCover(
                                coverUrl = books.getOrNull(0)?.coverUrl,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            )
                            if (books.size > 1) {
                                BookCover(
                                    coverUrl = books.getOrNull(1)?.coverUrl,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                )
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }

                        if (books.size > 2) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                BookCover(
                                    coverUrl = books.getOrNull(2)?.coverUrl,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                )
                                if (books.size > 3) {
                                    BookCover(
                                        coverUrl = books.getOrNull(3)?.coverUrl,
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                    )
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }

                else -> {
                    // 5+本书时，显示拼贴布局（类似微信读书）
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // 左侧大封面
                        BookCover(
                            coverUrl = books[0].coverUrl,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )

                        // 右侧小封面列
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            for (i in 1 until minOf(5, books.size)) {
                                BookCover(
                                    coverUrl = books[i].coverUrl,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 显示数量角标
        val statusBookCount = books.size
        if (statusBookCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp)
                    .size(24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = statusBookCount.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * 书籍封面组件
 */
@Composable
private fun BookCover(
    coverUrl: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        if (!coverUrl.isNullOrEmpty()) {
            AsyncImage(
                model = coverUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
        }
    }
}

// 添加Compose预览函数
@Preview(showBackground = true)
@Composable
private fun StatusPreviewPreview() {
    // 创建一个模拟的ReadStatus
    val status = ReadStatus.FINISHED
    
    MaterialTheme {
        // 使用一个简单的Box来模拟StatusPreview的外观，因为ViewModel在预览中难以实例化
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.72f)
                .background(Color(0xFFF5F5F5))
                .padding(8.dp)
        ) {
            // 模拟书籍封面拼贴布局
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // 左侧大封面
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.LightGray)
                )

                // 右侧小封面列
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.Gray)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.Gray)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.Gray)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.Gray)
                    )
                }
            }
            
            // 显示数量角标
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp)
                    .size(24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "5",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StatusGridItemPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // 模拟StatusPreview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.72f)
                    .background(Color(0xFFF5F5F5))
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.LightGray)
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.Gray)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.Gray)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.Gray)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.Gray)
                        )
                    }
                }
                
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                        .size(24.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "5",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(6.dp))
            
            Text(
                text = "已读完",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                 modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "12 本",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
