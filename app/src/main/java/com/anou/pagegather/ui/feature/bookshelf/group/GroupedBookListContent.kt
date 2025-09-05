package com.anou.pagegather.ui.feature.bookshelf.group

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
import androidx.compose.material.icons.filled.Folder
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.anou.pagegather.ui.feature.bookshelf.BookListViewModel

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
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "暂无分组",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
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
            // 网格模式 - 参照微信读书布局实现的分组网格
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(groups.size) { index ->
                    val group = groups[index]
                    GroupGridItem(
                        group = group,
                        viewModel = viewModel,
                        onClick = { onGroupClick(group.id, group.name) }
                    )
                }
            }
        } else {
            // 列表模式 - 参照微信读书列表布局
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(groups) { group ->
                        GroupListItem(
                            group = group,
                            viewModel = viewModel,
                            onClick = { onGroupClick(group.id, group.name) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * 分组列表项 - 仿微信读书列表样式
 */
@Composable
private fun GroupListItem(
    group: GroupInfo,
    viewModel: BookListViewModel,
    onClick: () -> Unit
) {
    // 获取该分组下的书籍数量
    val bookCount by viewModel.getGroupBookCount(group.id).collectAsState(initial = 0)
    // 获取该分组下的前5本书
    val books by viewModel.getBooksByGroupId(group.id).collectAsState(initial = emptyList())

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
            // 分组信息和箭头
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 分组名称和书籍数量
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = group.name,
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
                        contentScale = ContentScale.Crop
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
 * 分组卡片组件，显示分组名称和书籍数量（网格模式）
 */
@Composable
private fun GroupGridItem(
    group: GroupInfo,
    viewModel: BookListViewModel,
    onClick: () -> Unit
) {
    // 获取该分组下的书籍数量
    val bookCount by viewModel.getGroupBookCount(group.id).collectAsState(initial = 0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onClick)
            .padding(8.dp), // 添加一些内边距
        horizontalAlignment = Alignment.Start // 左对齐，与微信读书一致
    ) {
        // 分组预览（使用该分组下的书籍封面拼贴）
        GroupPreview(
            group = group,
            viewModel = viewModel,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.72f) // 标准书籍比例
                .clip(RoundedCornerShape(1.dp)) // 微信读书封面几乎没有圆角
        )
        
        Spacer(modifier = Modifier.height(6.dp))
        
        // 分组名称和书籍数量
        Text(
            text = group.name,
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
 * 分组预览组件，显示该分组下的前几本书的封面拼贴
 */
@Composable
private fun GroupPreview(
    group: GroupInfo,
    viewModel: BookListViewModel,
    modifier: Modifier = Modifier
) {
    // 获取该分组下的前9本书
    val books by viewModel.getBooksByGroupId(group.id).collectAsState(initial = emptyList())

    Box(
        modifier = modifier
            .aspectRatio(0.72f) // 添加标准书籍比例，与BookShelfDefaultBookGridItem保持一致
            .background(Color(0xFFF5F5F5))
            .padding(8.dp)
    ) {
        if (books.isEmpty()) {
            // 没有书籍时显示分组名称的首字母
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = group.name.take(1),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
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
        val groupBookCount = books.size
        if (groupBookCount > 0) {
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
                    text = groupBookCount.toString(),
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
                contentScale = ContentScale.Crop
            )
        }
    }
}