package com.anou.pagegather.ui.feature.demo

import androidx.compose.foundation.layout.*
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
 * Hundi 风格测试界面
 * 用于快速验证所有组件是否正常工作
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HundiTestScreen() {
    HundiGradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 标题
            Text(
                text = "Hundi 风格测试",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.extendedColors.titleColor,
                fontWeight = FontWeight.Bold
            )
            
            // 主要按钮
            HundiPrimaryButton(
                text = "主要按钮",
                onClick = { },
                modifier = Modifier.fillMaxWidth()
            )
            
            // 次要按钮
            HundiSecondaryButton(
                text = "次要按钮",
                onClick = { },
                modifier = Modifier.fillMaxWidth()
            )
            
            // 统计卡片
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HundiStatCard(
                    title = "测试数据",
                    value = "100",
                    subtitle = "单位",
                    modifier = Modifier.weight(1f),
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                )
                
                HundiStatCard(
                    title = "另一个",
                    value = "50",
                    subtitle = "项目",
                    modifier = Modifier.weight(1f),
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                )
            }
            
            // 进度条
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "进度示例",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                HundiProgressIndicator(
                    progress = 0.75f,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Text(
                    text = "75% 完成",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.extendedColors.subtitleColor
                )
            }
            
            // 标签
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HundiTag(text = "标签1")
                HundiTag(
                    text = "成功",
                    backgroundColor = MaterialTheme.extendedColors.success.copy(alpha = 0.2f),
                    textColor = MaterialTheme.extendedColors.success
                )
                HundiTag(
                    text = "警告",
                    backgroundColor = MaterialTheme.extendedColors.warning.copy(alpha = 0.2f),
                    textColor = MaterialTheme.extendedColors.warning
                )
            }
            
            // 卡片
            HundiCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "测试卡片",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Text(
                        text = "这是一个测试卡片，用于验证 Hundi 风格是否正常工作。",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.extendedColors.subtitleColor
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "状态：正常",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.extendedColors.success
                        )
                        
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.extendedColors.success
                        )
                    }
                }
            }
            
            // 评分组件测试
            var rating by remember { mutableFloatStateOf(3.5f) }
            
            HundiRatingCard(
                rating = rating,
                onRatingChanged = { rating = it },
                title = "评分测试",
                modifier = Modifier.fillMaxWidth()
            )
            
            // 只读评分显示
            HundiCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "评分显示",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    HundiRatingDisplay(
                        rating = 4.5f,
                        showText = true
                    )
                    
                    HundiRatingDisplay(
                        rating = 2.5f,
                        showText = true,
                        starSize = 16.dp
                    )
                }
            }
            
            // 颜色测试
            HundiCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "颜色测试",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Text(
                        text = "主色调文字",
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Text(
                        text = "标题颜色",
                        color = MaterialTheme.extendedColors.titleColor
                    )
                    
                    Text(
                        text = "副标题颜色",
                        color = MaterialTheme.extendedColors.subtitleColor
                    )
                    
                    Text(
                        text = "描述颜色",
                        color = MaterialTheme.extendedColors.descriptionColor
                    )
                }
            }
        }
    }
}