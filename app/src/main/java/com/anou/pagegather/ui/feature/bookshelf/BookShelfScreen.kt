package com.anou.pagegather.ui.feature.bookshelf

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

// 书架页面
@Composable
fun BookShelfScreen(
    onNavigateToBookGroups: () -> Unit,
    onToBookViewClick: (Long) -> Unit,
    onToBookAddClick: () -> Unit,
    onNavigateToTimer: () -> Unit = {},
    onNavigateToQuickActions: () -> Unit = {},
) {
    Text(
        text = "书架页面：管理和查看书籍的页面", style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.primary
    )

    BookListScreen(
        //viewModel: BookListViewModel = hiltViewModel(),
        onBookClick = onToBookViewClick,
        onAddBookClick = onToBookAddClick,
        onTimerClick = onNavigateToTimer,
        onQuickActionsClick = onNavigateToQuickActions
    )
}

