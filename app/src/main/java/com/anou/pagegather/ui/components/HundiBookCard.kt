package com.anou.pagegather.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.anou.pagegather.data.local.entity.BookEntity
import com.anou.pagegather.data.local.entity.ReadStatus
import com.anou.pagegather.ui.theme.extendedColors

/**
 * Hundi 风格的书籍卡片
 */
@Composable
fun HundiBookCard(
    book: BookEntity,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    isSelected: Boolean = false
) {
    HundiCard(
        onClick = onClick,
        modifier = modifier.then(
            if (isSelected) {
                Modifier.background(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(16.dp)
                )
            } else Modifier
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 书籍封面和基本信息
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                // 书籍封面
                BookCoverImage(
                    coverUrl = book.coverUrl,
                    coverPath = null, // BookEntity 没有 coverImagePath 字段
                    title = book.name ?: "",
                    modifier = Modifier.size(60.dp, 80.dp)
                )
                
                // 书籍信息
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // 书名
                    Text(
                        text = book.name ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.extendedColors.titleColor,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    // 作者
                    Text(
                        text = book.author ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.extendedColors.subtitleColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    // 状态标签
                    val readStatus = ReadStatus.entries.find { it.code == book.readStatus } ?: ReadStatus.WANT_TO_READ
                    HundiTag(
                        text = readStatus.message,
                        backgroundColor = when (readStatus) {
                            ReadStatus.WANT_TO_READ -> 
                                MaterialTheme.extendedColors.info.copy(alpha = 0.2f)
                            ReadStatus.READING -> 
                                MaterialTheme.colorScheme.primaryContainer
                            ReadStatus.FINISHED -> 
                                MaterialTheme.extendedColors.success.copy(alpha = 0.2f)
                            ReadStatus.ABANDONED -> 
                                MaterialTheme.extendedColors.neutral300
                        },
                        textColor = when (readStatus) {
                            ReadStatus.WANT_TO_READ -> 
                                MaterialTheme.extendedColors.info
                            ReadStatus.READING -> 
                                MaterialTheme.colorScheme.primary
                            ReadStatus.FINISHED -> 
                                MaterialTheme.extendedColors.success
                            ReadStatus.ABANDONED -> 
                                MaterialTheme.extendedColors.neutral700
                        }
                    )
                }
                
                // 评分显示
                if (book.rating > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = MaterialTheme.extendedColors.warning,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = book.rating.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.extendedColors.subtitleColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            // 阅读进度（仅在读状态显示）
            if (book.readStatus == ReadStatus.READING.code) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "阅读进度",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.extendedColors.descriptionColor
                        )
                        
                        Text(
                            text = "${book.getProgressPercentage().toInt()}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    HundiProgressIndicator(
                        progress = book.getProgressPercentage() / 100f,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            // 最后阅读时间（如果有）
            book.lastReadDate?.let { lastRead ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.extendedColors.descriptionColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "最后阅读: ${formatDate(lastRead)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.extendedColors.descriptionColor
                    )
                }
            }
        }
    }
}

/**
 * Hundi 风格的书籍网格卡片（更紧凑的布局）
 */
@Composable
fun HundiBookGridCard(
    book: BookEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false
) {
    HundiCard(
        onClick = onClick,
        modifier = modifier.then(
            if (isSelected) {
                Modifier.background(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(16.dp)
                )
            } else Modifier
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 书籍封面
            BookCoverImage(
                coverUrl = book.coverUrl,
                coverPath = null,
                title = book.name ?: "",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.7f)
            )
            
            // 书名
            Text(
                text = book.name ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.extendedColors.titleColor,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            // 作者
            Text(
                text = book.author ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.extendedColors.subtitleColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            // 状态和进度
            val readStatus = ReadStatus.entries.find { it.code == book.readStatus } ?: ReadStatus.WANT_TO_READ
            if (readStatus == ReadStatus.READING) {
                HundiProgressIndicator(
                    progress = book.getProgressPercentage() / 100f,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                HundiTag(
                    text = readStatus.message,
                    backgroundColor = when (readStatus) {
                        ReadStatus.WANT_TO_READ -> 
                            MaterialTheme.extendedColors.info.copy(alpha = 0.2f)
                        ReadStatus.FINISHED -> 
                            MaterialTheme.extendedColors.success.copy(alpha = 0.2f)
                        ReadStatus.ABANDONED -> 
                            MaterialTheme.extendedColors.neutral300
                        else -> Color.Transparent
                    },
                    textColor = when (readStatus) {
                        ReadStatus.WANT_TO_READ -> 
                            MaterialTheme.extendedColors.info
                        ReadStatus.FINISHED -> 
                            MaterialTheme.extendedColors.success
                        ReadStatus.ABANDONED -> 
                            MaterialTheme.extendedColors.neutral700
                        else -> Color.Transparent
                    }
                )
            }
        }
    }
}

/**
 * 书籍封面图片组件
 */
@Composable
private fun BookCoverImage(
    coverUrl: String?,
    coverPath: String?,
    title: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.extendedColors.neutral200),
        contentAlignment = Alignment.Center
    ) {
        when {
            !coverUrl.isNullOrBlank() -> {
                AsyncImage(
                    model = coverUrl,
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            !coverPath.isNullOrBlank() -> {
                AsyncImage(
                    model = coverPath,
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            else -> {
                // 默认封面
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = MaterialTheme.extendedColors.neutral500,
                        modifier = Modifier.size(24.dp)
                    )
                    if (title.isNotEmpty()) {
                        Text(
                            text = title.take(2),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.extendedColors.neutral700,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * 格式化日期显示
 */
private fun formatDate(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val days = diff / (24 * 60 * 60 * 1000)
    
    return when {
        days == 0L -> "今天"
        days == 1L -> "昨天"
        days < 7 -> "${days}天前"
        days < 30 -> "${days / 7}周前"
        else -> "${days / 30}月前"
    }
}