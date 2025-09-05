package com.anou.pagegather.ui.feature.bookshelf.booksource

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.anou.pagegather.data.local.entity.BookEntity
import com.anou.pagegather.data.local.entity.BookSourceEntity
import com.anou.pagegather.ui.feature.bookshelf.BookListViewModel
import com.anou.pagegather.ui.feature.bookshelf.common.BookCollage
import com.anou.pagegather.ui.feature.bookshelf.common.BookGridItem
import com.anou.pagegather.ui.feature.bookshelf.common.BookPreview

/**
 * 按来源分组显示书籍的内容
 */
@Composable
fun BookSourcedBookListContent(
    viewModel: BookListViewModel,
    isGridMode: Boolean,
    onSourceClick: (sourceId: Long, sourceName: String) -> Unit
) {
    // 从ViewModel获取来源数据
    val bookListState by viewModel.bookListState.collectAsState()
    val sources = bookListState.availableSources

    if (sources.isEmpty()) {
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
                    text = "暂无来源",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "添加来源来管理你的书籍",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        // 根据显示模式选择布局
        if (isGridMode) {
            // 网格模式 - 参照微信读书布局实现的来源网格
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(sources.size) { index ->
                    val source = sources[index]
                    BookSourceGridItem(
                        source = source,
                        viewModel = viewModel,
                        onClick = { onSourceClick(source.id, source.name) }
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
                    items(sources) { source ->
                        BookSourceListItem(
                            source = source,
                            viewModel = viewModel,
                            onClick = { onSourceClick(source.id, source.name) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * 来源列表项 - 仿微信读书列表样式
 */
@Composable
private fun BookSourceListItem(
    source: BookSourceEntity,
    viewModel: BookListViewModel,
    onClick: () -> Unit
) {
    // 获取该来源下的书籍数量
    val bookCount by viewModel.getSourceBookCount(source.id).collectAsState(initial = 0)
    // 获取该来源下的前5本书
    val books by viewModel.getSourceTopBooks(source.id, 5).collectAsState(initial = emptyList())

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
            // 来源信息和箭头
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 来源名称和书籍数量
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = source.getDisplayName(),
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
                for (book in books) {
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
                repeat(5 - books.size) {
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
 * 来源卡片组件，显示来源名称和书籍数量（网格模式）
 */
@Composable
private fun BookSourceGridItem(
    source: BookSourceEntity,
    viewModel: BookListViewModel,
    onClick: () -> Unit
) {
    // 获取该来源下的书籍数量
    val bookCount by viewModel.getSourceBookCount(source.id).collectAsState(initial = 0)

    BookGridItem(
        title = source.getDisplayName(),
        bookCount = bookCount,
        onClick = onClick,
        content = {
            // 来源预览（使用该来源下的书籍封面拼贴）
            SourcePreview(
                source = source,
                viewModel = viewModel,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.72f) // 标准书籍比例
                    .clip(RoundedCornerShape(1.dp)) // 微信读书封面几乎没有圆角
            )
        }
    )
}

/**
 * 来源预览组件，显示该来源下的前几本书的封面拼贴
 */
@Composable
private fun SourcePreview(
    source: BookSourceEntity,
    viewModel: BookListViewModel,
    modifier: Modifier = Modifier
) {
    // 获取该来源下的前9本书
    val books by viewModel.getSourceTopBooks(source.id, 9).collectAsState(initial = emptyList())

    BookCollage(
        books = books,
        bookCount = books.size,
        modifier = modifier,
        emptyContent = {
            Text(
                text = source.getDisplayName().take(1),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        },
        getCoverUrl = { book -> (book as? BookEntity)?.coverUrl }
    )
}

