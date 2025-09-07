package com.anou.pagegather.ui.feature.bookshelf.tag

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anou.pagegather.data.local.entity.BookEntity
import com.anou.pagegather.data.local.entity.TagEntity
import com.anou.pagegather.ui.feature.bookshelf.BookListViewModel
import com.anou.pagegather.ui.feature.bookshelf.common.BookCategoryGrid
import com.anou.pagegather.ui.feature.bookshelf.common.BookCategoryList
import com.anou.pagegather.ui.feature.bookshelf.common.BookCollage

/**
 * 按标签分组显示书籍的内容
 */
@Composable
fun TagBookListContent(
    viewModel: BookListViewModel,
    onTagClick: (tag: TagEntity) -> Unit
) {
    // 从ViewModel获取标签数据
    val bookListState by viewModel.bookListState.collectAsStateWithLifecycle()
    val tags = bookListState.availableTags
    val isGridMode = bookListState.isGridMode
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
            // 显示未设置标签的书籍项
            item {
                UntaggedGridItem(
                    viewModel = viewModel,
                    onClick = { 
                        // 创建一个特殊的TagEntity来表示未设置标签的书籍
                        val untaggedTag = TagEntity(
                            id = -1L,
                            name = "未设置标签",
                            color = "#9E9E9E",
                            tagOrder = 0,
                            tagType = 0,
                            createdDate = 0,
                            updatedDate = 0,
                            lastSyncDate = 0
                        )
                        onTagClick(untaggedTag)
                    }
                )
            }
            
            // 显示所有标签
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
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                // 显示未设置标签的书籍项
                item {
                    val untaggedBookCount by viewModel.getUntaggedBookCount().collectAsState(initial = 0)
                    val untaggedBooks by viewModel.getUntaggedBooks().collectAsState(initial = emptyList())
                    
                    BookCategoryList(
                        title = "未设置标签",
                        bookCount = untaggedBookCount,
                        onClick = { 
                            // 创建一个特殊的TagEntity来表示未设置标签的书籍
                            val untaggedTag = TagEntity(
                                id = -1L,
                                name = "未设置标签",
                                color = "#9E9E9E",
                                tagOrder = 0,
                                tagType = 0,
                                createdDate = 0,
                                updatedDate = 0,
                                lastSyncDate = 0
                            )
                            onTagClick(untaggedTag)
                        },
                        bookPreviewUrls = untaggedBooks.map { it.coverUrl ?: "" },
                        content = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // 标签颜色指示器
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF9E9E9E))  // 灰色
                                )

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
                
                // 显示所有标签
                items(tags) { tag ->
                    // 获取该标签下的书籍数量
                    val bookCount by viewModel.getTagBookCount(tag.id).collectAsState(initial = 0)
                    // 获取该标签下的前5本书
                    val books by viewModel.getBooksWithTag(tag.id).collectAsState(initial = emptyList())
                    
                    BookCategoryList(
                        title = tag.name,
                        bookCount = bookCount,
                        onClick = { onTagClick(tag) },
                        bookPreviewUrls = books.map { it.coverUrl ?: "" },
                        content = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // 标签颜色指示器
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(tag.getColor())  // 使用新的getColor()方法
                                )

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
 * 未设置标签卡片组件（网格模式）
 */
@Composable
private fun UntaggedGridItem(
    viewModel: BookListViewModel,
    onClick: () -> Unit
) {
    // 获取未设置标签的书籍数量
    val bookCount by viewModel.getUntaggedBookCount().collectAsState(initial = 0)

    BookCategoryGrid(
        title = "未设置标签",
        bookCount = bookCount,
        onClick = onClick,
        content = {
            // 未设置标签书籍预览
            UntaggedPreview(
                viewModel = viewModel,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.72f) // 标准书籍比例
                    .clip(RoundedCornerShape(1.dp)) 
            )
        },
        subtitle = "$bookCount 本"
    )
}

/**
 * 未设置标签预览组件，显示未设置标签书籍的封面拼贴
 */
@Composable
private fun UntaggedPreview(
    viewModel: BookListViewModel,
    modifier: Modifier = Modifier
) {
    // 获取未设置标签的前9本书
    val books by viewModel.getUntaggedBooks().collectAsState(initial = emptyList())

    BookCollage(
        books = books,
        bookCount = books.size,
        modifier = modifier,
        emptyIcon = Icons.AutoMirrored.Filled.Label,
        emptyIconTint = Color(0xFF9E9E9E),  // 灰色
        getCoverUrl = { book -> (book as? BookEntity)?.coverUrl }
    )
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

    BookCategoryGrid(
        title = tag.name,
        bookCount = bookCount,
        onClick = onClick,
        content = {
            // 标签预览（使用该标签下的书籍封面拼贴）
            TagPreview(
                tag = tag,
                viewModel = viewModel,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.72f) // 标准书籍比例
                    .clip(RoundedCornerShape(1.dp)) 
            )
        },
        subtitle = "$bookCount 本"
    )
}

/**
 * 标签预览组件，显示该标签下的前几本书的封面拼贴
 */
@Composable
private fun TagPreview(
    tag: TagEntity,
    viewModel: BookListViewModel,
    modifier: Modifier = Modifier
) {
    // 获取该标签下的前9本书
    val books by viewModel.getBooksWithTag(tag.id).collectAsState(initial = emptyList())

    BookCollage(
        books = books,
        bookCount = books.size,
        modifier = modifier,
        emptyIcon = Icons.AutoMirrored.Filled.Label,
        emptyIconTint = tag.getColor(),  // 使用新的getColor()方法
        getCoverUrl = { book -> (book as? BookEntity)?.coverUrl }
    )
}