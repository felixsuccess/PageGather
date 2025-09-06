package com.anou.pagegather.ui.feature.bookshelf.status

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
import androidx.compose.material.icons.filled.Bookmark
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.anou.pagegather.data.local.entity.BookEntity
import com.anou.pagegather.data.local.entity.ReadStatus
import com.anou.pagegather.ui.feature.bookshelf.BookListViewModel
import com.anou.pagegather.ui.feature.bookshelf.common.BookCategoryGrid
import com.anou.pagegather.ui.feature.bookshelf.common.BookCategoryList
import com.anou.pagegather.ui.feature.bookshelf.common.BookCollage

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
                    // 获取该状态下的书籍数量
                    val bookCount by viewModel.getStatusBookCount(status).collectAsState(initial = 0)
                    // 获取该状态下的前5本书
                    val books by viewModel.getBooksByStatus(status.code).collectAsState(initial = emptyList())
                    
                    BookCategoryList(
                        title = status.message,
                        bookCount = bookCount,
                        onClick = { onStatusClick(status) },
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

    BookCategoryGrid(
        title = status.message,
        bookCount = bookCount,
        onClick = onClick,
        content = {
            // 状态预览（使用该状态下的书籍封面拼贴）
            StatusPreview(
                status = status,
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

    BookCollage(
        books = books,
        bookCount = books.size,
        modifier = modifier,
        emptyIcon = Icons.Default.Bookmark,
        emptyIconTint = status.getColor(),
        getCoverUrl = { book -> (book as? BookEntity)?.coverUrl }
    )
}

