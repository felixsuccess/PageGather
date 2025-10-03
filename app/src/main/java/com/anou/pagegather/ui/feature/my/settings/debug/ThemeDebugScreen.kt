package com.anou.pagegather.ui.feature.my.settings.debug

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.anou.pagegather.domain.theme.ThemeManagerViewModel
import com.anou.pagegather.ui.theme.*

/**
 * 主题系统调试界面
 * 显示错误统计、系统健康状态、缓存信息等
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeDebugScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    themeManager: ThemeManagerViewModel = hiltViewModel()
) {
    var errorStats by remember { mutableStateOf<ThemeErrorStats?>(null) }
    var cacheStats by remember { mutableStateOf<CacheStats?>(null) }
    var systemHealth by remember { mutableStateOf<ThemeSystemHealth?>(null) }
    var accessibilityReports by remember { mutableStateOf<List<ThemeAccessibilityReport>>(emptyList()) }
    
    // 刷新数据
    fun refreshData() {
        // 这里需要通过 ThemeManager 获取统计信息
        // 由于架构限制，我们创建一个简化的展示
        cacheStats = ThemeCache.getCacheStats()
        errorStats = ThemeErrorHandler.getErrorStats()
        systemHealth = ThemeErrorHandler.checkSystemHealth()
        accessibilityReports = AccessibilityUtils.validateAllThemes()
    }
    
    LaunchedEffect(Unit) {
        refreshData()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("主题系统调试") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { refreshData() }) {
                        Icon(Icons.Default.Refresh, "刷新")
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 系统健康状态
            item {
                SystemHealthCard(systemHealth)
            }
            
            // 缓存统计
            item {
                CacheStatsCard(cacheStats)
            }
            
            // 错误统计
            item {
                ErrorStatsCard(errorStats)
            }
            
            // 可访问性报告
            item {
                AccessibilityReportCard(accessibilityReports)
            }
            
            // 操作按钮
            item {
                ActionButtonsCard(
                    onClearCache = { 
                        ThemeCache.clearCache()
                        refreshData()
                    },
                    onClearErrors = {
                        ThemeErrorHandler.clearErrorHistory()
                        refreshData()
                    },
                    onPreloadThemes = {
                        ThemeCache.preloadAllThemes()
                        refreshData()
                    }
                )
            }
        }
    }
}

@Composable
private fun SystemHealthCard(health: ThemeSystemHealth?) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = when (health) {
                        ThemeSystemHealth.EXCELLENT -> Icons.Default.CheckCircle
                        ThemeSystemHealth.GOOD -> Icons.Default.Check
                        ThemeSystemHealth.FAIR -> Icons.Default.Warning
                        ThemeSystemHealth.POOR -> Icons.Default.Error
                        null -> Icons.AutoMirrored.Filled.Help
                    },
                    contentDescription = null,
                    tint = when (health) {
                        ThemeSystemHealth.EXCELLENT -> MaterialTheme.colorScheme.primary
                        ThemeSystemHealth.GOOD -> MaterialTheme.colorScheme.primary
                        ThemeSystemHealth.FAIR -> MaterialTheme.colorScheme.tertiary
                        ThemeSystemHealth.POOR -> MaterialTheme.colorScheme.error
                        null -> MaterialTheme.colorScheme.onSurface
                    }
                )
                Text(
                    text = "系统健康状态",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = when (health) {
                    ThemeSystemHealth.EXCELLENT -> "优秀 - 系统运行完美"
                    ThemeSystemHealth.GOOD -> "良好 - 系统运行正常"
                    ThemeSystemHealth.FAIR -> "一般 - 系统有轻微问题"
                    ThemeSystemHealth.POOR -> "较差 - 系统需要关注"
                    null -> "未知 - 正在检测..."
                },
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun CacheStatsCard(stats: CacheStats?) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Storage, contentDescription = null)
                Text(
                    text = "缓存统计",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (stats != null) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    StatItem("颜色方案缓存", "${stats.colorSchemeCacheSize} 项")
                    StatItem("扩展颜色缓存", "${stats.extendedColorsCacheSize} 项")
                    StatItem("缓存命中", "${stats.cacheHits} 次")
                    StatItem("缓存未命中", "${stats.cacheMisses} 次")
                    StatItem("命中率", "${String.format("%.1f", stats.hitRate)}%")
                }
            } else {
                Text("正在加载缓存统计...")
            }
        }
    }
}

@Composable
private fun ErrorStatsCard(stats: ThemeErrorStats?) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.BugReport, contentDescription = null)
                Text(
                    text = "错误统计",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (stats != null) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    StatItem("总错误数", "${stats.totalErrors}")
                    StatItem("最近错误数", "${stats.recentErrors}")
                    
                    if (stats.errorsByType.isNotEmpty()) {
                        Text(
                            text = "错误类型分布:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        stats.errorsByType.forEach { (type, count) ->
                            StatItem("  ${type.name}", "$count 次")
                        }
                    }
                    
                    stats.lastError?.let { error ->
                        Text(
                            text = "最后错误: ${error.message}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            } else {
                Text("正在加载错误统计...")
            }
        }
    }
}

@Composable
private fun AccessibilityReportCard(reports: List<ThemeAccessibilityReport>) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Accessibility, contentDescription = null)
                Text(
                    text = "可访问性报告",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (reports.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    reports.forEach { report ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = report.getDisplayName(),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${String.format("%.0f", report.overallScore)}分",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Icon(
                                    imageVector = if (report.isCompliant) Icons.Default.Check else Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = if (report.isCompliant) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                Text("正在生成可访问性报告...")
            }
        }
    }
}

@Composable
private fun ActionButtonsCard(
    onClearCache: () -> Unit,
    onClearErrors: () -> Unit,
    onPreloadThemes: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "调试操作",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onClearCache,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.ClearAll, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("清除缓存")
                }
                
                OutlinedButton(
                    onClick = onClearErrors,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.DeleteSweep, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("清除错误历史")
                }
                
                OutlinedButton(
                    onClick = onPreloadThemes,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Download, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("预加载所有主题")
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}