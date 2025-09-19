package com.anou.pagegather.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anou.pagegather.ui.theme.extendedColors

/**
 * Hundi 风格的评分条组件（整数评分）
 */
@Composable
fun HundiRateBar(
    rating: Int,
    onRatingChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
    maxRating: Int = 5,
    starSize: Dp = 24.dp,
    activeColor: Color = MaterialTheme.extendedColors.warning,
    inactiveColor: Color = MaterialTheme.extendedColors.neutral300,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..maxRating) {
            Icon(
                imageVector = if (i <= rating) Icons.Default.Star else Icons.Default.StarOutline,
                contentDescription = "评分 $i 星",
                modifier = Modifier
                    .size(starSize)
                    .then(
                        if (enabled) {
                            Modifier.clickable { onRatingChanged(i) }
                        } else {
                            Modifier
                        }
                    ),
                tint = if (i <= rating) activeColor else inactiveColor
            )
        }
    }
}

/**
 * Hundi 风格的评分条组件（浮点数评分，支持半星）
 */
@Composable
fun HundiRateBar(
    rating: Float,
    onRatingChanged: (Float) -> Unit,
    modifier: Modifier = Modifier,
    maxRating: Int = 5,
    starSize: Dp = 24.dp,
    activeColor: Color = MaterialTheme.extendedColors.warning,
    inactiveColor: Color = MaterialTheme.extendedColors.neutral300,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..maxRating) {
            val starValue = i.toFloat()
            val icon = when {
                rating >= starValue -> Icons.Default.Star
                rating >= starValue - 0.5f -> Icons.AutoMirrored.Filled.StarHalf
                else -> Icons.Default.StarOutline
            }
            
            Icon(
                imageVector = icon,
                contentDescription = "评分 $i 星",
                modifier = Modifier
                    .size(starSize)
                    .then(
                        if (enabled) {
                            Modifier.clickable { 
                                // 点击逻辑：如果当前是满星，点击变成半星；如果是半星或空星，点击变成满星
                                val newRating = if (rating >= starValue) {
                                    starValue - 0.5f
                                } else {
                                    starValue
                                }
                                onRatingChanged(newRating.coerceAtLeast(0f))
                            }
                        } else {
                            Modifier
                        }
                    ),
                tint = if (rating >= starValue - 0.5f) activeColor else inactiveColor
            )
        }
    }
}

/**
 * Hundi 风格的评分显示组件（只读）
 */
@Composable
fun HundiRatingDisplay(
    rating: Float,
    modifier: Modifier = Modifier,
    maxRating: Int = 5,
    starSize: Dp = 20.dp,
    activeColor: Color = MaterialTheme.extendedColors.warning,
    inactiveColor: Color = MaterialTheme.extendedColors.neutral300,
    showText: Boolean = true
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 星星显示
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            for (i in 1..maxRating) {
                val starValue = i.toFloat()
                val icon = when {
                    rating >= starValue -> Icons.Default.Star
                    rating >= starValue - 0.5f -> Icons.AutoMirrored.Filled.StarHalf
                    else -> Icons.Default.StarOutline
                }
                
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(starSize),
                    tint = if (rating >= starValue - 0.5f) activeColor else inactiveColor
                )
            }
        }
        
        // 评分文字
        if (showText && rating > 0) {
            Text(
                text = String.format("%.1f", rating),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.extendedColors.subtitleColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Hundi 风格的评分卡片
 */
@Composable
fun HundiRatingCard(
    rating: Float,
    onRatingChanged: (Float) -> Unit,
    modifier: Modifier = Modifier,
    title: String = "我的评分",
    enabled: Boolean = true
) {
    HundiCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.extendedColors.titleColor,
                fontWeight = FontWeight.SemiBold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HundiRateBar(
                    rating = rating,
                    onRatingChanged = onRatingChanged,
                    enabled = enabled,
                    starSize = 28.dp
                )
                
                if (rating > 0) {
                    HundiTag(
                        text = String.format("%.1f 分", rating),
                        backgroundColor = MaterialTheme.extendedColors.warning.copy(alpha = 0.2f),
                        textColor = MaterialTheme.extendedColors.warning
                    )
                }
            }
            
            if (enabled) {
                Text(
                    text = "点击星星进行评分",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.extendedColors.descriptionColor
                )
            }
        }
    }
}