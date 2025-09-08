package com.anou.pagegather.ui.feature.bookshelf

import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.font.FontWeight
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
import com.anou.pagegather.data.local.entity.BookSourceEntity
import com.anou.pagegather.data.local.entity.BookType
import com.anou.pagegather.data.local.entity.NoteEntity
import com.anou.pagegather.data.local.entity.ReadPositionUnit
import com.anou.pagegather.data.local.entity.ReadStatus
import com.anou.pagegather.data.local.entity.ReadingRecordEntity
import com.anou.pagegather.data.model.BookReadingStatistics
import com.anou.pagegather.ui.theme.Accent
import com.anou.pagegather.utils.BlurTransformation
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 定义Tab枚举
enum class BookDetailTab(val title: String) {
    BASIC_INFO("基础信息"),
    READING_HISTORY("阅读历史"),
    EXCERPTS("书摘"),
    REVIEWS("书评"),
    RELATED_DATA("相关数据")
}

@SuppressLint("ContextCastToActivity")
@Composable
fun BookDetailScreen(
    navController: NavController,
    bookId: String? = null,
    viewModel: BookDetailViewModel = hiltViewModel(),
    onEditBookClick: (Long) -> Unit,
    onNavigateToNoteEdit: (Long) -> Unit,
    onNavigateToNewNote: () -> Unit,
    onBackClick: () -> Unit,
    onNavigateToTimer: ((Long) -> Unit)? = null
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
    val bookSource by viewModel.bookSource.collectAsState()

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

    var showMenu by remember { mutableStateOf(false) }
    // 当前选中的Tab
    var selectedTab by remember { mutableStateOf(BookDetailTab.BASIC_INFO) }

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
            book?.let { TopBackImgLayout(it, imageHeight, bookSource) }
            
            // Tab导航
            book?.let { bookEntity ->
                BookDetailTabs(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    book = bookEntity,
                    navController = navController,
                    onNavigateToNoteEdit = onNavigateToNoteEdit
                )
            }
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
                
                // 更多操作菜单
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "更多选项",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("阅读计时") },
                            onClick = {
                                showMenu = false
                                bookId?.toLongOrNull()?.let { parsedId ->
                                    if (parsedId != 0L) {
                                        onNavigateToTimer?.invoke(parsedId)
                                    }
                                }
                            }
                        )
                        
                        DropdownMenuItem(
                            text = { Text("添加笔记") },
                            onClick = {
                                showMenu = false
                                bookId?.toLongOrNull()?.let { parsedId ->
                                    if (parsedId != 0L) {
                                        onNavigateToNoteEdit(parsedId)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BookDetailTabs(
    selectedTab: BookDetailTab,
    onTabSelected: (BookDetailTab) -> Unit,
    book: BookEntity,
    navController: NavController,
    onNavigateToNoteEdit: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Tab导航栏
        TabRow(
            selectedTabIndex = selectedTab.ordinal,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        ) {
            BookDetailTab.entries.forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = { onTabSelected(tab) },
                    text = {
                        Text(
                            text = tab.title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (selectedTab == tab) FontWeight.Medium else FontWeight.Normal
                        )
                    }
                )
            }
        }
        
        // Tab内容区域 - 使用Box替代LazyColumn避免嵌套滚动问题
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()  // 使用fillMaxHeight替代weight
                .padding(horizontal = 16.dp)
        ) {
            // 根据选中的Tab显示对应的内容
            when (selectedTab) {
                BookDetailTab.BASIC_INFO -> BasicInfoTab(book)
                BookDetailTab.READING_HISTORY -> ReadingHistoryTabContent(book.id)
                BookDetailTab.EXCERPTS -> BookExcerptsTabContent(book.id, onNavigateToNoteEdit)
                BookDetailTab.REVIEWS -> BookReviewsTabContent(book.id, onNavigateToNoteEdit)
                BookDetailTab.RELATED_DATA -> RelatedDataTabContent(book.id, navController)
            }
        }
    }
}

@Composable
private fun ReadingHistoryTabContent(bookId: Long) {
    val viewModel: BookReadingHistoryViewModel = hiltViewModel()
    val readingRecords by viewModel.readingRecords.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    // 加载阅读记录
    LaunchedEffect(bookId) {
        viewModel.loadReadingRecords(bookId)
    }
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "阅读历史",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                Text(
                    text = "加载失败: ${error}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
            readingRecords.isEmpty() -> {
                Text(
                    text = "暂无阅读记录",
                    modifier = Modifier.padding(16.dp)
                )
            }
            else -> {
                // 直接显示阅读记录项，而不是嵌套LazyColumn
                readingRecords.forEach { record ->
                    ReadingRecordItem(
                        record = record,
                        onDeleteClick = { viewModel.deleteReadingRecord(record) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun BookExcerptsTabContent(bookId: Long, onNavigateToNoteEdit: (Long) -> Unit) {
    val viewModel: BookExcerptsViewModel = hiltViewModel()
    val excerpts by viewModel.excerpts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    // 加载书摘
    LaunchedEffect(bookId) {
        viewModel.loadBookExcerpts(bookId)
    }
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "书摘",
                style = MaterialTheme.typography.headlineSmall
            )
            
            IconButton(
                onClick = { 
                    // 导航到添加新书摘页面
                    onNavigateToNoteEdit(bookId)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "添加书摘"
                )
            }
        }
        
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                Text(
                    text = "加载失败: ${error}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
            excerpts.isEmpty() -> {
                Text(
                    text = "暂无书摘",
                    modifier = Modifier.padding(16.dp)
                )
            }
            else -> {
                // 直接显示书摘项，而不是嵌套LazyColumn
                excerpts.forEach { excerpt ->
                    ExcerptItem(
                        excerpt = excerpt,
                        onDeleteClick = { viewModel.deleteExcerpt(excerpt) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun BookReviewsTabContent(bookId: Long, onNavigateToNoteEdit: (Long) -> Unit) {
    val viewModel: BookReviewsViewModel = hiltViewModel()
    val reviews by viewModel.reviews.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    // 加载书评
    LaunchedEffect(bookId) {
        viewModel.loadBookReviews(bookId)
    }
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "书评",
                style = MaterialTheme.typography.headlineSmall
            )
            
            IconButton(
                onClick = { 
                    // 导航到添加新书评页面
                    onNavigateToNoteEdit(bookId)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "添加书评"
                )
            }
        }
        
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                Text(
                    text = "加载失败: ${error}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
            reviews.isEmpty() -> {
                Text(
                    text = "暂无书评",
                    modifier = Modifier.padding(16.dp)
                )
            }
            else -> {
                // 直接显示书评项，而不是嵌套LazyColumn
                reviews.forEach { review ->
                    ReviewItem(
                        review = review,
                        onDeleteClick = { viewModel.deleteReview(review) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun RelatedDataTabContent(bookId: Long, navController: NavController) {
    val viewModel: BookRelatedDataViewModel = hiltViewModel()
    val bookStatistics by viewModel.bookStatistics.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    // 加载书籍统计数据
    LaunchedEffect(bookId) {
        viewModel.loadBookStatistics(bookId)
    }
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "相关数据",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                Text(
                    text = "加载失败: ${error}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
            bookStatistics != null -> {
                RelatedDataContent(bookStatistics!!, navController, bookId)
            }
            else -> {
                Text(
                    text = "暂无数据",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun BasicInfoTab(book: BookEntity) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // 基本信息卡片
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                SectionTitle("基本信息")
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
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 内容简介卡片
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                SectionTitle("内容简介")
                Text(
                    text = book.summary?.takeIf { it.isNotEmpty() } ?: "暂无简介",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 作者简介卡片
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                SectionTitle("作者简介")
                Text(
                    text = book.authorIntro?.takeIf { it.isNotEmpty() } ?: "暂无作者简介",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 阅读状态卡片
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                SectionTitle("阅读状态")
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
            }
        }
    }
}

@Composable
private fun ReadingRecordItem(
    record: ReadingRecordEntity,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 顶部行：时间信息和删除按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatDateTime(record.startTime),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "删除记录"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 阅读时长
            Text(
                text = "阅读时长: ${formatDuration(record.duration)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            
            // 进度信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "开始进度: ${String.format("%.1f", record.startProgress)}%",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "结束进度: ${String.format("%.1f", record.endProgress)}%",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            // 备注信息
            record.notes?.let { notes ->
                if (notes.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "备注: $notes",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ExcerptItem(
    excerpt: NoteEntity,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 顶部行：时间信息和删除按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatDateTime(excerpt.createdDate),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "删除书摘"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 书摘内容
            Text(
                text = excerpt.quote?.takeIf { it.isNotEmpty() } ?: "无内容",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            
            // 位置信息
//            excerpt.pageNumber?.let { pageNumber ->
//                if (pageNumber > 0) {
//                    Text(
//                        text = "位置: 第${pageNumber}页",
//                        style = MaterialTheme.typography.bodySmall,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant,
//                        modifier = Modifier.padding(vertical = 4.dp)
//                    )
//                }
//            }
        }
    }
}

@Composable
private fun ReviewItem(
    review: NoteEntity,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 顶部行：时间信息和删除按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatDateTime(review.createdDate),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "删除书评"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 书评内容
            Text(
                text = review.idea?.takeIf { it.isNotEmpty() } ?: "无内容",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            
//            // 评分信息
//            review.rating?.let { rating ->
//                if (rating > 0) {
//                    Text(
//                        text = "评分: ${rating} ★",
//                        style = MaterialTheme.typography.bodySmall,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant,
//                        modifier = Modifier.padding(vertical = 4.dp)
//                    )
//                }
//            }
        }
    }
}

@Composable
private fun RelatedDataContent(
    bookStatistics: BookReadingStatistics,
    navController: NavController,
    bookId: Long
) {
    // 统计卡片
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "阅读统计",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // 显示真实的统计数据
            Text(
                text = "总阅读时长: ${formatDuration(bookStatistics.totalReadingTime)}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            
            Text(
                text = "阅读次数: ${bookStatistics.readingRecordCount}次",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            
            Text(
                text = "平均每次阅读: ${formatDuration(bookStatistics.averageReadingTime)}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            
            bookStatistics.lastReadingTime?.let { lastTime ->
                Text(
                    text = "最后阅读: ${formatDateTime(lastTime)}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
            
            Text(
                text = "当前进度: ${String.format("%.1f", bookStatistics.readingProgress)}%",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
    
    Spacer(modifier = Modifier.height(16.dp))
    
    // 图表卡片
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "阅读进度趋势",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // 这里可以显示阅读进度的图表
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text(
                    text = "阅读进度图表占位符",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
    
    Spacer(modifier = Modifier.height(16.dp))
    
    // 操作按钮
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // 查看详细统计
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable { 
                        // 导航到书籍阅读统计页面
                        navController.navigate("reading/book_statistics/$bookId")
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "详细统计",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        // 查看图表分析
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable { 
                        // 导航到图表分析页面
                        navController.navigate("statistics/charts/$bookId")
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "图表分析",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun TopBackImgLayout(
    book: BookEntity, imageHeight: Dp, bookSource: BookSourceEntity?
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
                        .background(Accent)
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

private fun formatDateTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

private fun formatDuration(milliseconds: Long): String {
    val seconds = milliseconds / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    
    return when {
        hours > 0 -> "${hours}小时${minutes % 60}分钟"
        minutes > 0 -> "${minutes}分钟${seconds % 60}秒"
        else -> "${seconds}秒"
    }
}