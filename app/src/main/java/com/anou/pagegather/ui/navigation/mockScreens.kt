package com.anou.pagegather.ui.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow


// 统计子页面 - 时间线
@Composable
fun TimelineScreen() {
    Text(text = "时间线：展示时间相关统计的页面", overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.primary)
}

// 统计子页面 - 日历
@Composable
fun CalendarScreen() {
    Text(text = "日历：展示日历相关统计的页面", overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.primary)
}

// 统计子页面 - 图表
@Composable
fun ChartsScreen() {
    Text(text = "图表：展示各种统计图表的页面", overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.primary)
}

// 搜索相关 - 搜索结果
@Composable
fun SearchResultsScreen(
    query: String?,
    onNavigateToBookView: (String) -> Unit,
    onNavigateToNoteView: (String) -> Unit,
) {
    Text(text = "搜索结果：展示搜索结果的页面", overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.primary)
}


// 勋章系统页面
@Composable
fun MedalSystemScreen() {
    Text(text = "勋章系统：展示和管理勋章的页面", overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.primary)
}

