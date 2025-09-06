package com.anou.pagegather.ui.feature.bookshelf

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

// 书架页面
@Composable
fun BookShelfScreen(
    onNavigateToBookGroups: () -> Unit,
    onToBookViewClick: (Long) -> Unit,
    onToBookAddClick: () -> Unit,
    onNavigateToTimer: () -> Unit = {},
    onNavigateToQuickActions: () -> Unit = {},
    onNavigateToNoteEdit: ((Long, Long) -> Unit)? = null,  // 添加导航到笔记编辑页面的回调函数
    onNavigateToBookEdit: ((Long) -> Unit)? = null,  // 添加导航到书籍编辑页面的回调函数
    onNavigateToGroupDetail: (Long, String) -> Unit = { _, _ -> },  // 添加导航到分组详情的回调
    onNavigateToSourceDetail: (Long, String) -> Unit = { _, _ -> },  // 添加导航到来源详情的回调
    onNavigateToTagDetail: (Long, String, String?) -> Unit = { _, _, _ -> },  // 添加导航到标签详情的回调
    onNavigateToStatusDetail: (Int, String) -> Unit = { _, _ -> },  // 添加导航到状态详情的回调
    onNavigateToRatingDetail: (Int, String) -> Unit = { _, _ -> }  // 添加导航到评分详情的回调
) {
    Column {
        Text(
            text = "书架页面：管理和查看书籍的页面", 
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        BookListScreen(
            //viewModel: BookListViewModel = hiltViewModel(),
            onBookClick = onToBookViewClick,
            onAddBookClick = onToBookAddClick,
            onTimerClick = onNavigateToTimer,
            onQuickActionsClick = onNavigateToQuickActions,
            onNavigateToNoteEdit = onNavigateToNoteEdit,  // 传递导航到笔记编辑页面的回调函数
            onNavigateToBookEdit = onNavigateToBookEdit,  // 传递导航到书籍编辑页面的回调函数
            onNavigateToGroupDetail = onNavigateToGroupDetail,  // 传递分组导航回调
            onNavigateToSourceDetail = onNavigateToSourceDetail,  // 传递来源导航回调
            onNavigateToTagDetail = onNavigateToTagDetail,  // 传递标签导航回调
            onNavigateToStatusDetail = onNavigateToStatusDetail,  // 传递状态导航回调
            onNavigateToRatingDetail = onNavigateToRatingDetail  // 传递评分导航回调
        )
    }
}