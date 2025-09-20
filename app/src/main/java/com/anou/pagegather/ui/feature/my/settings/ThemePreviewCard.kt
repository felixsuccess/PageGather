package com.anou.pagegather.ui.feature.my.settings

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anou.pagegather.ui.theme.AppTheme
import com.anou.pagegather.ui.theme.getColorSchemeForTheme

/**
 * 主题预览卡片组件
 */
@Composable
fun ThemePreviewCard(
    theme: AppTheme,
    isSelected: Boolean,
    isDarkMode: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 获取主题的颜色方案用于预览
    val colorScheme = getColorSchemeForTheme(theme, isDarkMode)
    
    // 选中状态的动画颜色
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) colorScheme.primary else Color.Transparent,
        animationSpec = tween(durationMillis = 200),
        label = "border_color"
    )
    
    val cardElevation by animateColorAsState(
        targetValue = if (isSelected) colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent,
        animationSpec = tween(durationMillis = 200),
        label = "card_elevation"
    )
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() }
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // 主题颜色预览区域
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 左侧：主题信息
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    // 主题名称和表情符号
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = theme.emoji,
                            fontSize = 20.sp
                        )
                        Text(
                            text = theme.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            color = colorScheme.onSurface,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // 主题描述
                    Text(
                        text = theme.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = colorScheme.onSurface.copy(alpha = 0.7f),
                        maxLines = 2
                    )
                }
                
                // 右侧：颜色预览
                ThemeColorPreview(
                    colorScheme = colorScheme,
                    modifier = Modifier.size(60.dp)
                )
            }
            
            // 选中状态指示器
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(24.dp)
                        .background(
                            color = colorScheme.primary,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "已选中",
                        tint = colorScheme.onPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * 主题颜色预览组件
 * 显示主题的主要颜色
 */
@Composable
private fun ThemeColorPreview(
    colorScheme: ColorScheme,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
    ) {
        // 背景色
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
        )
        
        // 主色调预览
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            // 主色
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(colorScheme.primary)
            )
            
            // 次要色
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(colorScheme.secondary)
            )
        }
        
        // 表面色预览（小圆点）
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(16.dp)
                .background(
                    color = colorScheme.surface,
                    shape = RoundedCornerShape(8.dp)
                )
                .border(
                    width = 1.dp,
                    color = colorScheme.outline.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp)
                )
        )
    }
}

/**
 * 紧凑型主题预览卡片
 * 用于较小的空间或网格布局
 */
@Composable
fun CompactThemePreviewCard(
    theme: AppTheme,
    isSelected: Boolean,
    isDarkMode: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = getColorSchemeForTheme(theme, isDarkMode)
    
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) colorScheme.primary else colorScheme.outline.copy(alpha = 0.3f),
        animationSpec = tween(durationMillis = 200),
        label = "compact_border_color"
    )
    
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable { onClick() }
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // 颜色预览
            ThemeColorPreview(
                colorScheme = colorScheme,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            )
            
            // 主题表情符号
            Text(
                text = theme.emoji,
                fontSize = 16.sp,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(6.dp)
            )
            
            // 选中指示器
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(16.dp)
                        .background(
                            color = colorScheme.primary,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "已选中",
                        tint = colorScheme.onPrimary,
                        modifier = Modifier.size(10.dp)
                    )
                }
            }
            
            // 主题名称
            Text(
                text = theme.displayName,
                style = MaterialTheme.typography.labelSmall,
                color = colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(4.dp)
                    .fillMaxWidth(),
                maxLines = 1
            )
        }
    }
}