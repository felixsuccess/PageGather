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
    onNavigateToGroupDetail: (Long, String) -> Unit = { _, _ -> },  // 添加导航到分组详情的回调
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
            onNavigateToGroupDetail = onNavigateToGroupDetail  // 传递导航回调
        )
    }
}