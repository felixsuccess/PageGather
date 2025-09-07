package com.anou.pagegather.ui.feature.bookshelf.booksource

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anou.pagegather.data.local.entity.BookEntity
import com.anou.pagegather.data.local.entity.BookSourceEntity
import com.anou.pagegather.ui.feature.bookshelf.BookListViewModel
import com.anou.pagegather.ui.feature.bookshelf.common.BookCategoryGrid
import com.anou.pagegather.ui.feature.bookshelf.common.BookCategoryList
import com.anou.pagegather.ui.feature.bookshelf.common.BookCollage

/**
 * 按来源分组显示书籍的内容
 */
@Composable
fun BookSourcedBookListContent(
    viewModel: BookListViewModel,
    onSourceClick: (sourceId: Long, sourceName: String) -> Unit
) {
    // 从ViewModel获取来源数据
    val bookListState by viewModel.bookListState.collectAsStateWithLifecycle()
    val sources = bookListState.availableSources
    val isGridMode = bookListState.isGridMode
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
             Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(sources) { source ->
                        // 获取该来源下的书籍数量
                        val bookCount by viewModel.getSourceBookCount(source.id).collectAsState(initial = 0)
                        // 获取该来源下的前5本书
                        val books by viewModel.getSourceTopBooks(source.id, 5).collectAsState(initial = emptyList())
                        
                        BookCategoryList(
                            title = source.getDisplayName(),
                            bookCount = bookCount,
                            onClick = { onSourceClick(source.id, source.name) },
                            bookPreviewUrls = books.map { it.coverUrl ?: "" },
                            content = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = "查看详情",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        )
                    }
                }
            }
        }
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

    BookCategoryGrid(
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
                    .clip(RoundedCornerShape(1.dp)) 
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
                text = source.getDisplayName().takeIf { it.isNotEmpty() }?.take(1) ?: "来",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        },
        getCoverUrl = { book -> (book as? BookEntity)?.coverUrl }
    )
}

