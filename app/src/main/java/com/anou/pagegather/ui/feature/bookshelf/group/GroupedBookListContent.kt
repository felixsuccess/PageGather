package com.anou.pagegather.ui.feature.bookshelf.group

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Folder
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
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.anou.pagegather.data.local.entity.BookEntity
import com.anou.pagegather.data.local.entity.BookGroupEntity
import com.anou.pagegather.ui.feature.bookshelf.BookListViewModel
import com.anou.pagegather.ui.feature.bookshelf.common.BookCategoryGrid
import com.anou.pagegather.ui.feature.bookshelf.common.BookCategoryList
import com.anou.pagegather.ui.feature.bookshelf.common.BookCollage

@Composable
fun GroupedBookListContent(
    viewModel: BookListViewModel,
    isGridMode: Boolean, // 添加显示模式参数
    onGroupClick: (groupId: Long, groupName: String) -> Unit
) {
    // 从ViewModel获取分组数据
    val bookListState by viewModel.bookListState.collectAsState()
    val groups = bookListState.availableGroups  // 直接使用BookGroupEntity列表

    // 根据显示模式选择布局
    if (isGridMode) {
         LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 显示未分组书籍项
            item {
                UngroupedGridItem(
                    viewModel = viewModel,
                    onClick = { onGroupClick(-1L, "未分组") }
                )
            }
            
            // 显示所有分组
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
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                // 显示未分组书籍项
                item {
                    val ungroupedBookCount by viewModel.getUngroupedBookCount().collectAsState(initial = 0)
                    val ungroupedBooks by viewModel.getUngroupedBooks().collectAsState(initial = emptyList())
                    
                    BookCategoryList(
                        title = "未分组",
                        bookCount = ungroupedBookCount,
                        onClick = { onGroupClick(-1L, "未分组") },
                        bookPreviewUrls = ungroupedBooks.map { it.coverUrl ?: "" },
                        content = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "查看详情",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
                
                // 显示所有分组
                items(groups) { group ->
                    // 获取该分组下的书籍数量
                    val bookCount by viewModel.getGroupBookCount(group.id).collectAsState(initial = 0)
                    // 获取该分组下的前5本书
                    val books by viewModel.getBooksByGroupId(group.id).collectAsState(initial = emptyList())
                    
                    BookCategoryList(
                        title = group.getDisplayName(),  // 使用getDisplayName()方法
                        bookCount = bookCount,
                        onClick = { onGroupClick(group.id, group.getDisplayName()) },  // 使用getDisplayName()方法
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
 * 未分组卡片组件（网格模式）
 */
@Composable
private fun UngroupedGridItem(
    viewModel: BookListViewModel,
    onClick: () -> Unit
) {
    // 获取未分组书籍数量
    val bookCount by viewModel.getUngroupedBookCount().collectAsState(initial = 0)

    BookCategoryGrid(
        title = "未分组",
        bookCount = bookCount,
        onClick = onClick,
        content = {
            // 未分组书籍预览
            UngroupedPreview(
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
 * 未分组预览组件，显示未分组书籍的封面拼贴
 */
@Composable
private fun UngroupedPreview(
    viewModel: BookListViewModel,
    modifier: Modifier = Modifier
) {
    // 获取未分组的前9本书
    val books by viewModel.getUngroupedBooks().collectAsState(initial = emptyList())

    BookCollage(
        books = books,
        bookCount = books.size,
        modifier = modifier,
        emptyContent = {
            Text(
                text = "未",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        },
        getCoverUrl = { book -> (book as? BookEntity)?.coverUrl }
    )
}

/**
 * 分组卡片组件，显示分组名称和书籍数量（网格模式）
 */
@Composable
private fun GroupGridItem(
    group: BookGroupEntity,  // 修改参数类型
    viewModel: BookListViewModel,
    onClick: () -> Unit
) {
    // 获取该分组下的书籍数量
    val bookCount by viewModel.getGroupBookCount(group.id).collectAsState(initial = 0)

    BookCategoryGrid(
        title = group.getDisplayName(),  // 使用getDisplayName()方法
        bookCount = bookCount,
        onClick = onClick,
        content = {
            // 分组预览（使用该分组下的书籍封面拼贴）
            GroupPreview(
                group = group,
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
 * 分组预览组件，显示该分组下的前几本书的封面拼贴
 */
@Composable
private fun GroupPreview(
    group: BookGroupEntity,  // 修改参数类型
    viewModel: BookListViewModel,
    modifier: Modifier = Modifier
) {
    // 获取该分组下的前9本书
    val books by viewModel.getBooksByGroupId(group.id).collectAsState(initial = emptyList())

    BookCollage(
        books = books,
        bookCount = books.size,
        modifier = modifier,
        emptyContent = {
            Text(
                text = group.getDisplayName().take(1),  // 使用getDisplayName()方法
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        },
        getCoverUrl = { book -> (book as? BookEntity)?.coverUrl }
    )
}