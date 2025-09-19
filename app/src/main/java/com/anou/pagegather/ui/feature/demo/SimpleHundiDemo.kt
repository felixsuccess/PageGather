package com.anou.pagegather.ui.feature.demo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anou.pagegather.ui.components.*
import com.anou.pagegather.ui.theme.extendedColors

/**
 * 简化的 Hundi 风格演示
 * 展示基础组件和设计风格
 */
@Composable
fun SimpleHundiDemo() {
    HundiGradientBackground {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 标题
            item {
                Column(
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

            // 按钮演示
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "按钮样式",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    
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

            // 统计卡片演示
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "统计卡片",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        HundiStatCard(
                            title = "本月阅读",
                            value = "12",
                            subtitle = "本书",
                            modifier = Modifier.weight(1f),
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        )
                        
                        HundiStatCard(
                            title = "阅读时长",
                            value = "48",
                            subtitle = "小时",
                            modifier = Modifier.weight(1f),
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        )
                    }
                }
            }

            // 卡片演示
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "卡片设计",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    HundiCard {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "当前阅读",
                                        style = MaterialTheme.typography.titleMedium,
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
            }

            // 标签演示
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "标签样式",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        HundiTag(
                            text = "历史小说",
                            backgroundColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                        
                        HundiTag(
                            text = "已完成",
                            backgroundColor = MaterialTheme.extendedColors.success.copy(alpha = 0.2f),
                            textColor = MaterialTheme.extendedColors.success
                        )
                        
                        HundiTag(
                            text = "推荐",
                            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                            textColor = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // 颜色展示
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "配色方案",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    
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
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
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