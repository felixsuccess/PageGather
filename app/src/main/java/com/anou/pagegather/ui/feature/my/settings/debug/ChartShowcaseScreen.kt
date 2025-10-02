package com.anou.pagegather.ui.feature.my.settings.debug

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.anou.pagegather.ui.components.charts.*
import com.anou.pagegather.ui.theme.PageGatherTheme

/**
 * 图表展示页面 - 开发者选项
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartShowcaseScreen(
    navController: NavController = rememberNavController()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "图表组件展示",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            
            item {
                Text(
                    text = "原生图表组件库演示",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "基于 Jetpack Compose Canvas 实现，无第三方依赖",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // 书籍来源分布饼图
            item {
                ChartCard(title = "图例模式（推荐：详细信息展示）") {
                    BookSourcePieChartDemo()
                }
            }

            // 书籍类型分布饼图
            item {
                ChartCard(title = "纯外部标签模式（推荐：简洁数据展示）") {
                    BookTypePieChartDemo()
                }
            }

            // 扇形间隙演示
            item {
                ChartCard(title = "扇形间隙效果演示（5度间隙）") {
                    SegmentSpacingPieChartDemo()
                }
            }

            // 底部图例演示
            item {
                ChartCard(title = "底部图例布局演示") {
                    BottomLegendPieChartDemo()
                }
            }

            // 使用场景对比
            item {
                ChartCard(title = "外部标签 vs 图例 - 使用场景对比") {
                    LabelVsLegendComparisonDemo()
                }
            }

            // 年度阅读时长分布柱状图
            item {
                ChartCard(title = "年度阅读时长分布（圆角柱状图）") {
                    YearlyReadingBarChartDemo()
                }
            }

            // 月度阅读时长折线图
            item {
                ChartCard(title = "月度阅读时长分布（面积折线图）") {
                    MonthlyReadingLineChartDemo()
                }
            }

            // 8月阅读时长分布柱状图
            item {
                ChartCard(title = "8月阅读时长分布（智能标签 + 右侧Y轴）") {
                    AugustReadingBarChartDemo()
                }
            }

            // 阅读趋势对比图
            item {
                ChartCard(title = "阅读趋势对比（高级折线图）") {
                    ReadingTrendComparisonDemo()
                }
            }

            // 半圆形进度条
            item {
                ChartCard(title = "半圆形进度条") {
                    SemicircularProgressDemo()
                }
            }
        }
    }
}

@Composable
private fun ChartCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            content()
        }
    }
}

// =================== 图表演示组件 ====================

@Composable
private fun BookSourcePieChartDemo() {
    val bookSourceData = listOf(
        PieChartSegment(35f, "购买", Color(0xFF6366F1)),
        PieChartSegment(22f, "图书馆", Color(0xFF8B5CF6)),
        PieChartSegment(18f, "朋友推荐", Color(0xFFEC4899)),
        PieChartSegment(12f, "电子书", Color(0xFFF59E0B)),
        PieChartSegment(8f, "二手书店", Color(0xFF10B981)),
        PieChartSegment(5f, "赠送", Color(0xFF06B6D4))
    )

    PieChart(
        segments = bookSourceData,
        title = "",
        showPercentages = true,
        showLegend = true,
        isDonut = true,
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    )
}

@Composable
private fun BookTypePieChartDemo() {
    val bookTypeData = listOf(
        PieChartSegment(28f, "文学小说", Color(0xFF10B981)),
        PieChartSegment(22f, "科技技术", Color(0xFF06B6D4)),
        PieChartSegment(18f, "历史传记", Color(0xFFEF4444)),
        PieChartSegment(12f, "哲学思想", Color(0xFF3B82F6)),
        PieChartSegment(8f, "心理学", Color(0xFF8B5CF6)),
        PieChartSegment(6f, "经济管理", Color(0xFFEC4899)),
        PieChartSegment(4f, "艺术设计", Color(0xFFF59E0B)),
        PieChartSegment(2f, "其他", Color(0xFF8B5A2B))
    )

    PieChart(
        segments = bookTypeData,
        title = "",
        showPercentages = true,
        showLegend = false, // 隐藏图例，展示纯外部标签效果
        isDonut = true,
        donutHoleRatio = 0.4f,
        segmentSpacing = 2f,
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp) // 增加高度确保外部标签不被裁剪
    )
}

@Composable
private fun SegmentSpacingPieChartDemo() {
    val spacingDemoData = listOf(
        PieChartSegment(30f, "Android", Color(0xFF3DDC84)),
        PieChartSegment(25f, "iOS", Color(0xFF007AFF)),
        PieChartSegment(20f, "Web", Color(0xFFFF6B35)),
        PieChartSegment(15f, "Desktop", Color(0xFF6366F1)),
        PieChartSegment(10f, "其他", Color(0xFF8B5CF6))
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PieChart(
            segments = spacingDemoData,
            title = "",
            showPercentages = true,
            showLegend = true,
            legendPosition = LegendPosition.Right,
            isDonut = false, // 使用实心饼图更好地展示间隙效果
            segmentSpacing = 5f, // 5度间隙
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp) // 增加高度避免标签遮挡
        )
        
        Text(
            text = "扇形间隙: 5度 | 优化的右侧图例",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = "标签距离增加到70像素，完全避免遮挡 | 图例使用卡片样式",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun LabelVsLegendComparisonDemo() {
    val comparisonData = listOf(
        PieChartSegment(35f, "移动应用开发", Color(0xFF3DDC84)),
        PieChartSegment(25f, "Web前端开发", Color(0xFF007AFF)),
        PieChartSegment(20f, "后端服务", Color(0xFFFF6B35)),
        PieChartSegment(12f, "数据分析", Color(0xFF6366F1)),
        PieChartSegment(8f, "其他", Color(0xFF8B5CF6))
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 纯外部标签模式
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "纯外部标签模式",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            PieChart(
                segments = comparisonData,
                title = "",
                showPercentages = true,
                showLegend = false, // 只显示外部标签
                isDonut = true,
                donutHoleRatio = 0.4f,
                segmentSpacing = 3f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            )
            
            Text(
                text = "✅ 简洁直观 ✅ 节省空间 ✅ 直接关联",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
        
        Divider()
        
        // 图例模式
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "图例模式",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
            
            PieChart(
                segments = comparisonData,
                title = "",
                showPercentages = false, // 不显示外部标签
                showLegend = true,
                legendPosition = LegendPosition.Right,
                isDonut = true,
                donutHoleRatio = 0.4f,
                segmentSpacing = 3f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            )
            
            Text(
                text = "✅ 完整信息 ✅ 长标签友好 ✅ 交互潜力",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun BottomLegendPieChartDemo() {
    val bottomLegendData = listOf(
        PieChartSegment(25f, "小说", Color(0xFF10B981)),
        PieChartSegment(20f, "技术", Color(0xFF06B6D4)),
        PieChartSegment(18f, "历史", Color(0xFFEF4444)),
        PieChartSegment(15f, "哲学", Color(0xFF3B82F6)),
        PieChartSegment(12f, "心理学", Color(0xFF8B5CF6)),
        PieChartSegment(10f, "经济", Color(0xFFEC4899))
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PieChart(
            segments = bottomLegendData,
            title = "",
            showPercentages = true,
            showLegend = true,
            legendPosition = LegendPosition.Bottom, // 底部图例
            isDonut = true,
            donutHoleRatio = 0.5f,
            segmentSpacing = 3f,
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp) // 大幅增加高度确保饼图和图例都能完整显示
        )
        
        Text(
            text = "底部图例布局 | 每行3个项目 | 完整显示",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = "饼图尺寸400dp，标签距离70像素，图例高度限制200dp",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun YearlyReadingBarChartDemo() {
    val yearlyReadingData = listOf(
        ChartDataPoint(0f, 180f, "2020", "180小时"),
        ChartDataPoint(1f, 220f, "2021", "220小时"),
        ChartDataPoint(2f, 195f, "2022", "195小时"),
        ChartDataPoint(3f, 280f, "2023", "280小时"),
        ChartDataPoint(4f, 320f, "2024", "320小时")
    )

    BarChart(
        data = yearlyReadingData,
        title = "",
        showValues = true,
        showGrid = true,
        smartLabels = false,
        roundedCorners = true,
        cornerRadius = 12f,
        yAxisOnRight = false,
        labelSpacing = 20f,
        barColor = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    )
}

@Composable
private fun MonthlyReadingLineChartDemo() {
    val monthlyReadingData = listOf(
        ChartDataPoint(0f, 25f, "1月", "25小时"),
        ChartDataPoint(1f, 30f, "2月", "30小时"),
        ChartDataPoint(2f, 28f, "3月", "28小时"),
        ChartDataPoint(3f, 35f, "4月", "35小时"),
        ChartDataPoint(4f, 22f, "5月", "22小时"),
        ChartDataPoint(5f, 40f, "6月", "40小时"),
        ChartDataPoint(6f, 38f, "7月", "38小时"),
        ChartDataPoint(7f, 32f, "8月", "32小时"),
        ChartDataPoint(8f, 28f, "9月", "28小时"),
        ChartDataPoint(9f, 35f, "10月", "35小时"),
        ChartDataPoint(10f, 27f, "11月", "27小时"),
        ChartDataPoint(11f, 30f, "12月", "30小时")
    )

    LineChart(
        data = monthlyReadingData,
        title = "",
        showPoints = true,
        showGrid = true,
        showArea = true,
        smoothCurve = true, // 启用平滑曲线
        yAxisOnRight = false,
        labelSpacing = 20f,
        lineColor = MaterialTheme.colorScheme.secondary,
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    )
}

@Composable
private fun AugustReadingBarChartDemo() {
    val augustReadingData = listOf(
        ChartDataPoint(0f, 45f, "1日", "45分钟"),
        ChartDataPoint(1f, 60f, "2日", "60分钟"),
        ChartDataPoint(2f, 30f, "3日", "30分钟"),
        ChartDataPoint(3f, 75f, "4日", "75分钟"),
        ChartDataPoint(4f, 90f, "5日", "90分钟"),
        ChartDataPoint(5f, 20f, "6日", "20分钟"),
        ChartDataPoint(6f, 0f, "7日", "0分钟"),
        ChartDataPoint(7f, 120f, "8日", "120分钟"),
        ChartDataPoint(8f, 85f, "9日", "85分钟"),
        ChartDataPoint(9f, 55f, "10日", "55分钟"),
        ChartDataPoint(10f, 40f, "11日", "40分钟"),
        ChartDataPoint(11f, 95f, "12日", "95分钟"),
        ChartDataPoint(12f, 70f, "13日", "70分钟"),
        ChartDataPoint(13f, 25f, "14日", "25分钟"),
        ChartDataPoint(14f, 110f, "15日", "110分钟"),
        ChartDataPoint(15f, 80f, "16日", "80分钟"),
        ChartDataPoint(16f, 35f, "17日", "35分钟"),
        ChartDataPoint(17f, 65f, "18日", "65分钟"),
        ChartDataPoint(18f, 100f, "19日", "100分钟"),
        ChartDataPoint(19f, 50f, "20日", "50分钟"),
        ChartDataPoint(20f, 15f, "21日", "15分钟"),
        ChartDataPoint(21f, 85f, "22日", "85分钟"),
        ChartDataPoint(22f, 75f, "23日", "75分钟"),
        ChartDataPoint(23f, 90f, "24日", "90分钟"),
        ChartDataPoint(24f, 60f, "25日", "60分钟"),
        ChartDataPoint(25f, 45f, "26日", "45分钟"),
        ChartDataPoint(26f, 105f, "27日", "105分钟"),
        ChartDataPoint(27f, 80f, "28日", "80分钟"),
        ChartDataPoint(28f, 55f, "29日", "55分钟"),
        ChartDataPoint(29f, 70f, "30日", "70分钟"),
        ChartDataPoint(30f, 95f, "31日", "95分钟")
    )

    Column {
        BarChart(
            data = augustReadingData,
            title = "",
            showValues = false,
            showGrid = true,
            smartLabels = true,
            roundedCorners = true,
            cornerRadius = 10f,
            yAxisOnRight = true,
            labelSpacing = 18f,
            barColor = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 统计信息
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatisticItem("总时长", "32小时", MaterialTheme.colorScheme.primary)
            StatisticItem("平均每日", "62分钟", MaterialTheme.colorScheme.secondary)
            StatisticItem("最长单日", "120分钟", MaterialTheme.colorScheme.tertiary)
        }
    }
}

@Composable
private fun ReadingTrendComparisonDemo() {
    val readingTrendSeries1 = listOf(
        ChartDataPoint(0f, 15f, "1月", "15本"),
        ChartDataPoint(1f, 18f, "2月", "18本"),
        ChartDataPoint(2f, 12f, "3月", "12本"),
        ChartDataPoint(3f, 22f, "4月", "22本"),
        ChartDataPoint(4f, 25f, "5月", "25本"),
        ChartDataPoint(5f, 20f, "6月", "20本")
    )

    val readingTrendSeries2 = listOf(
        ChartDataPoint(0f, 8f, "1月", "8本"),
        ChartDataPoint(1f, 12f, "2月", "12本"),
        ChartDataPoint(2f, 15f, "3月", "15本"),
        ChartDataPoint(3f, 10f, "4月", "10本"),
        ChartDataPoint(4f, 18f, "5月", "18本"),
        ChartDataPoint(5f, 22f, "6月", "22本")
    )

    val dataSeries = listOf(
        ChartDataSeries("已读完", readingTrendSeries1, Color(0xFF10B981)),
        ChartDataSeries("在读中", readingTrendSeries2, Color(0xFFEF4444))
    )

    AdvancedLineChart(
        dataSeries = dataSeries,
        title = "",
        showPoints = true,
        showGrid = true,
        showArea = true,
        yAxisOnRight = false, // Y轴标签在左侧
        labelSpacing = 24f, // 与其他图表组件保持一致的间距
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    )
}

@Composable
private fun SemicircularProgressDemo() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SemicircularProgressIndicator(
            progress = 0.65f,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
            strokeWidth = 16.dp
        )
        
        Text(
            text = "今日阅读进度: 65%",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
        
        Text(
            text = "目标: 30分钟 | 已完成: 19.5分钟",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun StatisticItem(
    label: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

// ==================== Jetpack Compose Previews ====================

@Preview(showBackground = true)
@Composable
private fun BookSourcePieChartPreview() {
    PageGatherTheme {
        Surface {
            ChartCard(title = "书籍来源分布饼图") {
                BookSourcePieChartDemo()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BookTypePieChartPreview() {
    PageGatherTheme {
        Surface {
            ChartCard(title = "书籍类型分布饼图") {
                BookTypePieChartDemo()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SegmentSpacingPieChartPreview() {
    PageGatherTheme {
        Surface {
            ChartCard(title = "扇形间隙效果演示") {
                SegmentSpacingPieChartDemo()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LabelVsLegendComparisonPreview() {
    PageGatherTheme {
        Surface {
            ChartCard(title = "外部标签 vs 图例对比") {
                LabelVsLegendComparisonDemo()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomLegendPieChartPreview() {
    PageGatherTheme {
        Surface {
            ChartCard(title = "底部图例布局演示") {
                BottomLegendPieChartDemo()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun YearlyReadingBarChartPreview() {
    PageGatherTheme {
        Surface {
            ChartCard(title = "年度阅读时长分布") {
                YearlyReadingBarChartDemo()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MonthlyReadingLineChartPreview() {
    PageGatherTheme {
        Surface {
            ChartCard(title = "月度阅读时长分布") {
                MonthlyReadingLineChartDemo()
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 400)
@Composable
private fun AugustReadingBarChartPreview() {
    PageGatherTheme {
        Surface {
            ChartCard(title = "8月阅读时长分布") {
                AugustReadingBarChartDemo()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReadingTrendComparisonPreview() {
    PageGatherTheme {
        Surface {
            ChartCard(title = "阅读趋势对比") {
                ReadingTrendComparisonDemo()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SemicircularProgressPreview() {
    PageGatherTheme {
        Surface {
            ChartCard(title = "半圆形进度条") {
                SemicircularProgressDemo()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChartShowcaseScreenPreview() {
    PageGatherTheme {
        ChartShowcaseScreen()
    }
}