package com.anou.pagegather.ui.feature.bookshelf

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.anou.pagegather.R
import com.anou.pagegather.data.local.entity.BookEntity
import com.anou.pagegather.data.local.entity.ReadStatus
import com.anou.pagegather.ui.theme.AccentOrange
import com.anou.pagegather.ui.theme.TextGray

// 常量定义
private val GRID_COLUMNS = GridCells.Fixed(3)
private val GRID_PADDING = PaddingValues(vertical = 8.dp, horizontal = 8.dp)
private val GRID_SPACING = 8.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun BookListScreen(
    modifier: Modifier = Modifier,
    viewModel: BookListViewModel = hiltViewModel(),
    onBookClick: (Long) -> Unit,
    onAddBookClick: () -> Unit,
) {
    val uiState by viewModel.state.collectAsState()
    val tabTitles = listOf(
        "书库"//, "预购书单"
    )
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            modifier = modifier.fillMaxWidth(),
            title = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp, 15.dp, 5.dp, 5.dp)
                        .background(MaterialTheme.colorScheme.surface),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(Modifier.weight(1f)) {
                        tabTitles.forEachIndexed { index, title ->
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
                                color = if (selectedTab == index) MaterialTheme.colorScheme.onSurface
                                else TextGray,
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .clickable { selectedTab = index })
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = {
                            /* TODO: 添加搜索功能 */
                        }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "搜索",
                                tint = TextGray
                            )
                        }
                        IconButton(onClick = { onAddBookClick() }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "添加",
                                tint = TextGray
                            )
                        }
                        IconButton(onClick = {
                            /* TODO: 添加更多选项     如列表显示  排序 等 */
                        }) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = "更多选项",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

            },

            )

        Box(Modifier.weight(1f)) {
            when (selectedTab) {
                0 -> {
                    BookListContent(
                        bookListUIState = uiState,
                        onBookClick = onBookClick,
                        onAddBookClick = onAddBookClick,
                        viewModel = viewModel
                    )
                }

                1 -> {
                    PreOrderBookListContent()
                }
            }
        }
    }
}

@Composable
private fun BookListContent(
    bookListUIState: BookListUIState,
    onBookClick: (Long) -> Unit,
    onAddBookClick: () -> Unit,
    viewModel: BookListViewModel,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {

        when (bookListUIState) {
            is BookListUIState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is BookListUIState.Empty -> {
                // 空列表状态
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = painterResource(id = R.mipmap.empty),
                        contentDescription = "空列表",
                    )


                    Text(
                        text = "赶紧整理",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black,
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable { onAddBookClick() })


                }
            }

            is BookListUIState.Error -> {
                // 添加错误状态UI
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    Text(text = bookListUIState.message)
                }
            }


            is BookListUIState.Success -> {
                //TODO: 分页加载
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxWidth(),
                    columns = GRID_COLUMNS,
                    contentPadding = GRID_PADDING,
                    state = rememberLazyGridState(),
                    verticalArrangement = Arrangement.spacedBy(GRID_SPACING),
                    horizontalArrangement = Arrangement.spacedBy(GRID_SPACING)
                ) {
                    items(bookListUIState.books.size) { index ->
                        val book = bookListUIState.books[index]
                        BookItem(
                            book = book, onItemClick = { onBookClick(book.id) })

                    }
                }
            }
        }

    }
}

@Composable
fun BookItem(book: BookEntity, onItemClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
            .padding(8.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(7f / 10f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Gray.copy(alpha = 100f))

        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(book.coverUrl).apply {
                    Log.d("BookListScreen", "正在加载封面URL: ${book.coverUrl}")
                }.crossfade(true).build(),
                contentDescription = null,
                error = painterResource(id = R.mipmap.default_cover),// 指定默认图片
                modifier = Modifier.fillMaxWidth()
//                .aspectRatio(7f / 10f)
//                .clip(RoundedCornerShape(8.dp))
//                .background(Color.Gray.copy(alpha = 100f))
//                .border(
//                    width = 1.dp,
//                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
//                    shape = RoundedCornerShape(8.dp)
//                )
                ,
                onSuccess = {
                    Log.i("BookListScreen", "封面加载成功: ${book.coverUrl}")
                },
                onError = { result ->
                    Log.e("BookListScreen", "封面加载失败: ${result.result.throwable}")
                })
            // 添加右上角黄色背景白色字体的“已读”标志
            if (book.readStatus == ReadStatus.FINISHED.ordinal) { // 假设3表示需要标红的状态
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .clip(
                            RoundedCornerShape(
                                topStart = 0.dp, topEnd = 8.dp, bottomStart = 8.dp, bottomEnd = 0.dp
                            )
                        )
                        .background(AccentOrange)
                    // .border(
                    //     width = 1.dp,
                    //     color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    //     shape = RoundedCornerShape(4.dp)
                    // )
                ) {
                    Text(
                        text = "已读",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontSize = 10.sp
                    )
                }
            }
        }
        Text(
            text = book.name.toString(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = book.author.toString(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 2.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

    }
}

@Composable
private fun PreOrderBookListContent() {
    //TODO: 预购书单内容实现
    Column(Modifier.fillMaxSize()) {
        Text("预购书单功能开发中", style = MaterialTheme.typography.titleMedium)
    }
}