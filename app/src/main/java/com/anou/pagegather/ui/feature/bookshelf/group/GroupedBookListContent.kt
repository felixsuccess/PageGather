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
                        // 获取该分组下的书籍数量
                        val bookCount by viewModel.getGroupBookCount(group.id).collectAsState(initial = 0)
                        // 获取该分组下的前5本书
                        val books by viewModel.getBooksByGroupId(group.id).collectAsState(initial = emptyList())
                        
                        BookCategoryList(
                            title = group.name,
                            bookCount = bookCount,
                            onClick = { onGroupClick(group.id, group.name) },
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

    BookCategoryGrid(
        title = group.name,
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
                    .clip(RoundedCornerShape(1.dp)) // 微信读书封面几乎没有圆角
            )
        }
    )
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

    BookCollage(
        books = books,
        bookCount = books.size,
        modifier = modifier,
        emptyContent = {
            Text(
                text = group.name.take(1),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        },
        getCoverUrl = { book -> (book as? BookEntity)?.coverUrl }
    )
}

