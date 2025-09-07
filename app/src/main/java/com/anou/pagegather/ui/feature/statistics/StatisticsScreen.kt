package com.anou.pagegather.ui.feature.statistics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.anou.pagegather.ui.feature.statistics.components.ReadingOverviewCard
import com.anou.pagegather.ui.navigation.Routes

@Composable
fun StatisticsScreen(
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "统计页面",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.headlineSmall
        )
        
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
                    navController.navigate(Routes.ReadingRoutes.READING_RECORDS)
                }
            )
            
            StatisticsButton(
                text = "书籍阅读统计",
                onClick = {
                    navController.navigate(Routes.ReadingRoutes.BOOK_READING_STATISTICS)
                }
            )
            
            StatisticsButton(
                text = "阅读时间线",
                onClick = {
                    navController.navigate(Routes.DashboardRoutes.TIMELINE)
                }
            )
            
            StatisticsButton(
                text = "阅读图表",
                onClick = {
                    navController.navigate(Routes.DashboardRoutes.CHARTS)
                }
            )
        }
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