package com.anou.pagegather.ui.feature.bookshelf.rating

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
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
import com.anou.pagegather.ui.feature.bookshelf.BookListViewModel
import com.anou.pagegather.ui.feature.bookshelf.common.BookCategoryGrid
import com.anou.pagegather.ui.feature.bookshelf.common.BookCategoryList
import com.anou.pagegather.ui.feature.bookshelf.common.BookCollage

/**
 * 按评分分组显示书籍的内容
 */
@Composable
fun RatingBookListContent(
    viewModel: BookListViewModel,
    isGridMode: Boolean,
    onRatingClick: (rating: Int) -> Unit
) {
    // 评分范围从0到5
    val ratings = listOf(5, 4, 3, 2, 1, 0)

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
            items(ratings.size) { index ->
                val rating = ratings[index]
                RatingGridItem(
                    rating = rating,
                    viewModel = viewModel,
                    onClick = { onRatingClick(rating) }
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
                items(ratings) { rating ->
                    // 获取该评分下的书籍数量
                    val bookCount by viewModel.getRatingBookCount(rating).collectAsState(initial = 0)
                    // 获取该评分下的前5本书
                    val books by viewModel.getBooksByRating(rating.toFloat()).collectAsState(initial = emptyList())
                    
                    BookCategoryList(
                        title = if (rating == 0) "未评分" else "$rating 星",
                        bookCount = bookCount,
                        onClick = { onRatingClick(rating) },
                        bookPreviewUrls = books.map { it.coverUrl ?: "" },
                        content = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // 评分星星
                                repeat(5) { index ->
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = if (index < rating) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = "查看详情",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}



/**
 * 评分卡片组件（网格模式）
 */
@Composable
private fun RatingGridItem(
    rating: Int,
    viewModel: BookListViewModel,
    onClick: () -> Unit
) {
    // 获取该评分下的书籍数量
    val bookCount by viewModel.getRatingBookCount(rating).collectAsState(initial = 0)

    BookCategoryGrid(
        title = if (rating == 0) "未评分" else "$rating 星",
        bookCount = bookCount,
        onClick = onClick,
        content = {
            // 评分预览（使用该评分下的书籍封面拼贴）
            RatingPreview(
                rating = rating,
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
 * 评分预览组件，显示该评分下的前几本书的封面拼贴
 */
@Composable
private fun RatingPreview(
    rating: Int,
    viewModel: BookListViewModel,
    modifier: Modifier = Modifier
) {
    // 获取该评分下的前9本书
    val books by viewModel.getBooksByRating(rating.toFloat()).collectAsState(initial = emptyList())

    BookCollage(
        books = books,
        bookCount = books.size,
        modifier = modifier,
        emptyContent = {
            Row(
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(5) { index ->
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = if (index < rating) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        getCoverUrl = { book -> (book as? BookEntity)?.coverUrl }
    )
}
