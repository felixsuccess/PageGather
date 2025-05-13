package com.anou.pagegather.ui.feature.bookshelf


import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.anou.pagegather.R
import com.anou.pagegather.data.local.entity.BookEntity
import com.anou.pagegather.data.local.entity.BookSource
import com.anou.pagegather.data.local.entity.BookType
import com.anou.pagegather.data.local.entity.ReadPositionUnit
import com.anou.pagegather.data.local.entity.ReadStatus
import com.anou.pagegather.ui.theme.AccentOrange
import com.anou.pagegather.utils.BlurTransformation
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@SuppressLint("ContextCastToActivity")

@Composable
fun BookDetailScreen(
    navController: NavController,
    bookId: String? = null,
    viewModel: BookDetailViewModel = hiltViewModel(),
    onEditBookClick: (Long) -> Unit,
    onNavigateToNoteEdit: (Long) -> Unit,
    onNavigateToNewNote: () -> Unit,
    onBackClick: () -> Unit

    ) {
    val context = LocalActivity.current
    val view = LocalView.current
    //val isLightTheme = MaterialTheme.colorScheme.background.luminance() > 0.5f
    val isLightTheme = !isSystemInDarkTheme()
    // 沉浸式状态栏设置
    LaunchedEffect(Unit) {

        context?.window?.let { window ->

            WindowCompat.setDecorFitsSystemWindows(window, false)
            window.statusBarColor = Color.Transparent.toArgb()
            WindowInsetsControllerCompat(window, view).apply {
                isAppearanceLightStatusBars = isLightTheme
            }

        }
        context?.window?.let {
            WindowInsetsControllerCompat(context.window, view).apply {
                isAppearanceLightStatusBars = isLightTheme
            }
            // 关键设置1：允许内容延伸到系统栏后面
            WindowCompat.setDecorFitsSystemWindows(context.window, false)

            // 关键设置2：设置状态栏透明
            context.window.statusBarColor = Color.Transparent.toArgb()

            // 关键设置3：调整状态栏图标颜色
            WindowInsetsControllerCompat(context.window, view).apply {
                // 根据背景亮度决定图标颜色
                isAppearanceLightStatusBars = isLightTheme

                // Android 11+ 隐藏状态栏阴影
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    systemBarsBehavior =
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            }
        }


    }


    val book by viewModel.book.collectAsState()

    // 简化 LaunchedEffect 中的逻辑
    LaunchedEffect(bookId) {
        bookId?.toLongOrNull()?.let { parsedId ->
            if (parsedId != 0L) {
                viewModel.loadBook(parsedId)
            }
        }
    }
    val scrollState = rememberScrollState()
    //（动态调整透明度）
    val imageHeight = 600.dp
    val imageHeightPx = with(LocalDensity.current) { imageHeight.toPx() }
    // 计算透明度 (0f - 1f)
    val appBarAlpha = remember(scrollState.value) {
        (scrollState.value / imageHeightPx).coerceIn(0f, 1f)
    }

//

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 主要内容区域（可滚动）
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)

        ) {

            // book?.let { TopImgLayout(it, imageHeight) }
            book?.let { TopBackImgLayout(it, imageHeight) }
        }

        // 固定在顶部的按钮组
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.surface.copy(alpha = appBarAlpha))
                .windowInsetsPadding(WindowInsets.statusBars) // 避开状态栏
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // 返回按钮

            IconButton(onClick = {
                onBackClick()
                // navController.popBackStack()
            }
                // , modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "返回",
                    tint = MaterialTheme.colorScheme.onSurface
                )

            }


            AnimatedVisibility(
                (scrollState.value > 120),
                // (appBarAlpha > 0.5f),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp), // 添加左右内边距
                enter = slideInVertically(initialOffsetY = { it / 4 }),
                exit = slideOutVertically(targetOffsetY = { it / 4 })
            ) {
                Text(
                    text = book?.name ?: "书籍详情",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // 操作按钮组
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                IconButton(
                    //  modifier = Modifier.padding(horizontal = 5.dp),
                    onClick = {
                        bookId?.toLongOrNull()?.let { parsedId ->
                            if (parsedId != 0L) {
                                onEditBookClick(parsedId)
                            }
                        }
                    }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "收藏",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = {
                   // onShareClick
                }

                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = "分享",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = {
                    //onMoreClick

                }) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "更多选项",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }


            }
        }
    }
}

@Composable
private fun TopBackImgLayout(
    book: BookEntity, imageHeight: Dp,
) {
    // ==================== 顶部复合图片区域 ====================

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(imageHeight)
    ) {

        // 1. 背景虚化图或渐变色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(book.coverUrl ?: "")
                .crossfade(true).build(),
                contentDescription = null,
                error = painterResource(id = R.mipmap.default_cover),// 指定默认图片
                contentScale = ContentScale.Crop, // 关键：裁剪或填充
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { alpha = 0.85f }
                    .blur(radius = 34.dp) // 高斯模糊效果
                    .drawWithContent {
                        drawContent()
                        //背景遮上半透明颜色，改善明亮色调的背景下，白色操作按钮的显示效果
                        drawRect(Color.Gray, alpha = 0.6f)
                    })
        } else {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(book.coverUrl ?: "")
                .transformations(
                    listOf(
                        BlurTransformation(
                            LocalContext.current, 18f, 5f
                        )
                    )
                )//radius must be in [0, 25].
                .crossfade(true).build(),
                contentDescription = null,
                error = painterResource(id = R.mipmap.default_cover),// 指定默认图片
                contentScale = ContentScale.Crop, // 关键：裁剪或填充
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { alpha = 0.85f }
                    .drawWithContent {
                        drawContent()
                        //背景遮上半透明颜色，改善明亮色调的背景下，白色操作按钮的显示效果
                        drawRect(Color.Gray, alpha = 0.6f)
                    })
        }
        // 2. 内容行（左侧文字 + 右侧缩略图）
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 32.dp) // 留出状态栏空间
                .padding(24.dp), verticalAlignment = Alignment.Top
        ) {
            // 左侧简介文字
            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = book.name ?: "未知",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,

                    )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = " ${(book.author ?: "")} ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Text(
                    text = ((ReadStatus.entries.firstOrNull { it.code == book.readStatus }?.message
                        ?: "")),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier
                        .padding(5.dp)
                        // .background(Color.Red)
                        .clip(
                            RoundedCornerShape(
                                topStart = 0.dp, topEnd = 8.dp, bottomStart = 8.dp, bottomEnd = 0.dp
                            )
                        )
                        .background(AccentOrange)
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                )
            }

            // 右侧完整缩略图
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(book.coverUrl ?: "")
                    .crossfade(true).build(),
                contentDescription = "缩略图",
                error = painterResource(id = R.mipmap.default_cover),// 指定默认图片
                contentScale = ContentScale.Fit, // 关键：裁剪或填充
                modifier = Modifier
                    .width(150.dp)
                    .align(Alignment.Top) // 确保顶部对齐
                    .fillMaxHeight()
                    .aspectRatio(7f / 10f)
                    .clip(RoundedCornerShape(12.dp))// 添加12dp圆角
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ),
            )

        }

        // 3. 底部渐变遮罩

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(80.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.3f),
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )

        )
    }
    // ==================== 下方文字区域 ====================
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight() // 占满剩余空间
            .background(MaterialTheme.colorScheme.surface)
            .padding(24.dp)
    ) {
        DisplayBookInfo(book)

    }


}

@Composable
private fun DisplayBookInfo(book: BookEntity) {

    Column(
        modifier = Modifier
            .background(Color.White)
            .padding(8.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = ((ReadStatus.entries.firstOrNull { it.code == book.readStatus }?.message
                    ?: "")),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                modifier = Modifier
                    .padding(5.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 0.dp, topEnd = 8.dp, bottomStart = 8.dp, bottomEnd = 0.dp
                        )
                    )
                    .background(AccentOrange)
                    .padding(horizontal = 8.dp, vertical = 5.dp)
            )
        }

        DetailItem(label = "书名", value = book.name ?: "")
        DetailItem(label = "作者", value = book.author ?: "")
        DetailItem(label = "译者", value = book.translator?.takeIf { it.isNotEmpty() } ?: "")
        DetailItem(label = "ISBN", value = book.isbn ?: "")


    }
    SectionTitle("基本信息")
    Card {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(8.dp)
        ) {
            DetailItem(label = "书名", value = book.name ?: "")

            DetailItem(label = "作者", value = book.author ?: "")
            DetailItem(label = "译者", value = book.translator?.takeIf { it.isNotEmpty() } ?: "")
            DetailItem(label = "ISBN", value = book.isbn ?: "")
            DetailItem(label = "出版社", value = book.press ?: "")
            DetailItem(label = "出版日期", value = book.publishDate ?: "")

            DetailItem(
                label = "购买日期", value = formatDate(book.purchaseDate, false)
            )
            DetailItem(label = "购买价格", value = book.purchasePrice.toString())
            DetailItem(label = "作者简介", value = book.authorIntro ?: "")
            DetailItem(label = "内容简介", value = book.summary ?: "")
        }
    }

    SectionTitle("阅读状态")
    Card {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(8.dp)
        ) {
            DetailItem(
                label = "书籍类型",
                value = ((BookType.entries.firstOrNull { it.code == book.type }?.message ?: ""))
            )
            if (book.type == BookType.PAPER_BOOK.code) {

                DetailItem(
                    label = "已读页数", value = "${book.readPosition.toInt()}"
                )
                DetailItem(
                    label = "总页数", value = book.totalPagination.toString()
                )
            } else {


                if (book.positionUnit == ReadPositionUnit.PAGE.code) {
                    DetailItem(
                        label = "已读页数", value = "${book.readPosition.toInt()}"
                    )
                    DetailItem(
                        label = "总页数", value = book.totalPagination.toString()
                    )
                } else {
                    DetailItem(
                        label = "阅读进度", value = "${book.readPosition.toDouble()}%"
                    )
                }

            }

            DetailItem(
                label = "状态",
                value = (ReadStatus.entries.firstOrNull { it.code == book.readStatus }?.message
                    ?: "")

            )
            DetailItem(label = "评分", value = "${book.rating} ★")
            DetailItem(
                label = "来源",
                value = (BookSource.entries.firstOrNull { it.code == book.bookSourceId }?.message
                    ?: "")

            )
        }


    }


    SectionTitle("扩展信息")
    Card {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(8.dp)
        ) {
            DetailItem(
                label = "添加时刻", value = formatDate(book.createdDate)
            )
            DetailItem(
                label = "最后更新", value = formatDate(book.updatedDate)
            )
//            DetailItem(
//                label = "目录",
//                value = book.catalog?.takeIf { it.isNotEmpty() } ?: "暂无目录")
        }

    }
    SectionTitle(" ")
}


@Composable
private fun SectionTitle(
    label: String, modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

    }
}


@Composable
private fun DetailItem(
    label: String, value: String, modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

private fun formatDate(timestamp: Long?, isFull: Boolean = true): String {
    if (timestamp == null) {
        return ""
    }
    if (timestamp.toLong() == 0L) {
        return ""
    }
    val fixValue = if (isFull) 1L else 1000L
    val date = Date(timestamp.toLong() * fixValue)
    return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
}


