package com.anou.pagegather.ui.feature.statistics

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.anou.pagegather.ui.feature.statistics.components.ReadingOverviewCard
import com.anou.pagegather.ui.navigation.Routes

// 定义统计页面的Tab枚举
enum class StatisticsTab(val title: String) {
    OVERVIEW("概览"),
    TIMELINE("时间线"),
    CHARTS("图表"),
    RECORDS("记录"),
    BOOK_STATS("书籍统计")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    navController: NavController,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableStateOf(StatisticsTab.OVERVIEW) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Tab导航栏
        TabRow(
            selectedTabIndex = selectedTab.ordinal,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        ) {
            StatisticsTab.values().forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = { selectedTab = tab },
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
        
        // Tab内容区域
        when (selectedTab) {
            StatisticsTab.OVERVIEW -> {
                StatisticsOverviewTab(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }
            StatisticsTab.TIMELINE -> {
                StatisticsTimelineTab(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }
            StatisticsTab.CHARTS -> {
                StatisticsChartsTab(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }
            StatisticsTab.RECORDS -> {
                StatisticsRecordsTab(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    navController = navController
                )
            }
            StatisticsTab.BOOK_STATS -> {
                StatisticsBookStatsTab(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    navController = navController
                )
            }
        }
    }
}

@Composable
private fun StatisticsOverviewTab(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 阅读概览卡片
        ReadingOverviewCard()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 功能按钮区域
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatisticsButton(
                text = "查看阅读记录",
                onClick = {
                    // 导航逻辑将在Tab外部处理
                }
            )
            
            StatisticsButton(
                text = "书籍阅读统计",
                onClick = {
                    // 导航逻辑将在Tab外部处理
                }
            )
        }
    }
}

@Composable
private fun StatisticsTimelineTab(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "时间线：展示时间相关统计的页面",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun StatisticsChartsTab(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "图表：展示各种统计图表的页面",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun StatisticsRecordsTab(modifier: Modifier = Modifier, navController: NavController) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "阅读记录列表",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )
        
        StatisticsButton(
            text = "查看所有阅读记录",
            onClick = {
                navController.navigate(Routes.ReadingRoutes.READING_RECORDS)
            }
        )
    }
}

@Composable
private fun StatisticsBookStatsTab(modifier: Modifier = Modifier, navController: NavController) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "书籍阅读统计",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )
        
        StatisticsButton(
            text = "查看书籍阅读统计",
            onClick = {
                navController.navigate(Routes.ReadingRoutes.BOOK_READING_STATISTICS)
            }
        )
    }
}

@Composable
private fun StatisticsButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(text = text)
    }
}