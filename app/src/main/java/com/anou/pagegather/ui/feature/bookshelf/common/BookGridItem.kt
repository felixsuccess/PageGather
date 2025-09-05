package com.anou.pagegather.ui.feature.bookshelf.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.anou.pagegather.data.local.entity.BookEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun BookGridItem(
    book: BookEntity,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.Companion.Start // 左对齐，与微信读书一致
    ) {
        // 书籍封面 - 微信读书风格
        Box(
            modifier = Modifier.Companion
                .fillMaxWidth()
                .aspectRatio(0.72f) // 标准书籍比例
                .clip(RoundedCornerShape(1.dp)) // 微信读书封面几乎没有圆角
        ) {
            // 书籍封面
            if (book.coverUrl.isNullOrEmpty()) {
                // 没有封面时显示默认占位符
                Box(
                    modifier = Modifier.Companion
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Companion.Center
                ) {
                    Text(
                        text = book.name?.take(1) ?: "书",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Companion.Bold
                    )
                }
            } else {
                AsyncImage(
                    model = book.coverUrl,
                    contentDescription = null,
                    modifier = Modifier.Companion.fillMaxSize(),
                    contentScale = ContentScale.Companion.Crop
                )
            }

            // 阅读状态标签 - 微信读书在右上角显示标签
            when (book.readStatus) {
                2 -> { // 已读完
                    // 右上角的读完标签
                    Box(
                        modifier = Modifier.Companion
                            .align(Alignment.Companion.TopEnd)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(topEnd = 1.dp, bottomStart = 8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "读完",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Companion.Bold
                        )
                    }
                }

                0 -> { // 想读
                    // 右上角的想读标签
                    Box(
                        modifier = Modifier.Companion
                            .align(Alignment.Companion.TopEnd)
                            .background(
                                color = MaterialTheme.colorScheme.tertiary,
                                shape = RoundedCornerShape(topEnd = 1.dp, bottomStart = 8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "想读",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onTertiary,
                            fontWeight = FontWeight.Companion.Bold
                        )
                    }
                }
            }

            // 如果有进度，显示进度条
            if (book.totalPosition > 0 && book.readPosition > 0) {
                val progress = (book.readPosition.toFloat() / book.totalPosition.toFloat())
                Box(
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                        .height(3.dp)
                        .align(Alignment.Companion.BottomCenter)
                ) {
                    Box(
                        modifier = Modifier.Companion
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
                    )
                    Box(
                        modifier = Modifier.Companion
                            .fillMaxWidth(progress)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.Companion.height(6.dp))

        // 书籍标题 - 微信读书样式
        Text(
            text = book.name ?: "",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1, // 与微信读书一致，只显示一行标题
            overflow = TextOverflow.Companion.Ellipsis,
            modifier = Modifier.Companion.fillMaxWidth()
        )

        // 更新日期 - 微信读书显示日期而非作者
        val displayDate = if (book.updatedDate > 0) {
            val date = SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault())
                .format(Date(book.updatedDate))
            date
        } else {
            // 模拟日期，参考图片中的格式
            "${2024 + (book.id % 2).toInt()}年${1 + (book.id % 12).toInt()}月${1 + (book.id % 28).toInt()}日"
        }

        Text(
            text = displayDate,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Companion.Ellipsis,
            modifier = Modifier.Companion.fillMaxWidth()
        )
    }
}