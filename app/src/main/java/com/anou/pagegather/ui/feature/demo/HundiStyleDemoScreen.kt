package com.anou.pagegather.ui.feature.demo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anou.pagegather.ui.components.*
import com.anou.pagegather.ui.theme.extendedColors

/**
 * Hundi 风格演示界面
 * 展示新的配色方案和组件设计
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HundiStyleDemoScreen(
    onNavigateBack: () -> Unit = {}
) {
    HundiGradientBackground {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 标题区域
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Hundi 风格演示",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.extendedColors.titleColor,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "温暖橙色主题 • 现代卡片设计 • 柔和渐变背景",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.extendedColors.subtitleColor
                    )
                }
            }

            // 统计卡片区域
            item {
                Text(
                    text = "统计概览",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.extendedColors.titleColor,
                    fontWeight = FontWeight.SemiBold
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(
                        listOf(
                            Triple("本月阅读", "12", "本书"),
                            Triple("阅读时长", "48", "小时"),
                            Triple("完成进度", "75", "%"),
                            Triple("笔记数量", "156", "条")
                        )
                    ) { (title, value, unit) ->
                        HundiStatCard(
                            title = title,
                            value = value,
                            subtitle = unit,
                            modifier = Modifier.width(140.dp),
                            icon = {
                                Icon(
                                    imageVector = when (title) {
                                        "本月阅读" -> Icons.Default.MenuBook
                                        "阅读时长" -> Icons.Default.Schedule
                                        "完成进度" -> Icons.Default.TrendingUp
                                        else -> Icons.Default.Note
                                    },
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        )
                    }
                }
            }

            // 按钮演示
            item {
                Text(
                    text = "按钮样式",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.extendedColors.titleColor,
                    fontWeight = FontWeight.SemiBold
                )
            }

            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HundiPrimaryButton(
                        text = "开始阅读",
                        onClick = { },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    HundiSecondaryButton(
                        text = "添加书籍",
                        onClick = { },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // 卡片演示
            item {
                Text(
                    text = "卡片设计",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.extendedColors.titleColor,
                    fontWeight = FontWeight.SemiBold
                )
            }

            item {
                HundiCard {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "当前阅读",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.extendedColors.titleColor,
                                    fontWeight = FontWeight.SemiBold
                                )
                                
                                Text(
                                    text = "《长安十二时辰》",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.extendedColors.subtitleColor
                                )
                            }
                            
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "阅读进度",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.extendedColors.descriptionColor
                                )
                                
                                Text(
                                    text = "65%",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            
                            HundiProgressIndicator(
                                progress = 0.65f,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // 标签演示
            item {
                Text(
                    text = "标签样式",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.extendedColors.titleColor,
                    fontWeight = FontWeight.SemiBold
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(
                        listOf("历史小说", "马伯庸", "已完成", "推荐", "经典")
                    ) { tag ->
                        HundiTag(
                            text = tag,
                            backgroundColor = when (tag) {
                                "已完成" -> MaterialTheme.extendedColors.success.copy(alpha = 0.2f)
                                "推荐" -> MaterialTheme.colorScheme.primaryContainer
                                else -> MaterialTheme.colorScheme.secondaryContainer
                            },
                            textColor = when (tag) {
                                "已完成" -> MaterialTheme.extendedColors.success
                                "推荐" -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.onSecondaryContainer
                            }
                        )
                    }
                }
            }

            // 颜色展示
            item {
                Text(
                    text = "配色方案",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.extendedColors.titleColor,
                    fontWeight = FontWeight.SemiBold
                )
            }

            item {
                HundiCard {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ColorSwatch("主色调", MaterialTheme.colorScheme.primary)
                        ColorSwatch("辅助色", MaterialTheme.colorScheme.secondary)
                        ColorSwatch("强调色", MaterialTheme.extendedColors.accentColor)
                        ColorSwatch("成功色", MaterialTheme.extendedColors.success)
                        ColorSwatch("警告色", MaterialTheme.extendedColors.warning)
                        ColorSwatch("错误色", MaterialTheme.extendedColors.error)
                    }
                }
            }

            // 底部间距
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun ColorSwatch(
    name: String,
    color: androidx.compose.ui.graphics.Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.extendedColors.bodyColor
        )
        
        Surface(
            modifier = Modifier.size(32.dp),
            shape = androidx.compose.foundation.shape.CircleShape,
            color = color
        ) {}
    }
}