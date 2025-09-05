package com.anou.pagegather.ui.feature.bookshelf.common

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.anou.pagegather.R
import com.anou.pagegather.data.local.entity.BookEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 微信读书样式的来源书籍列表项
 */
@Composable
fun BookListItem(
    book: BookEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(0.dp), // 方形卡片，没有圆角
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), // 无阴影
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top
            ) {
                // 左侧书籍封面  
                Box(
                    modifier = Modifier
                        .width(65.dp)
                        .aspectRatio(0.72f) // 与微信读书封面比例一致
                        .clip( RoundedCornerShape(1.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                ) {
                  
                    
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current).data(book.coverUrl)
                            .apply {
                                Log.d("BookListScreen", "正在加载封面URL: ${book.coverUrl}")
                            }.crossfade(true).build(),
                        contentDescription = null,
                        error = painterResource(id = R.mipmap.default_cover),
                        modifier = Modifier.fillMaxSize(),
                        // 修改contentScale以保持图片的原始宽高比
                        contentScale = ContentScale.Fit,
                        onSuccess = {
                            Log.i("BookListScreen", "封面加载成功: ${book.coverUrl}")
                        },
                        onError = { result ->
                            Log.e("BookListScreen", "封面加载失败: ${result.result.throwable}")
                        }
                    )

                    // 如果有进度，显示进度条
                    if (book.totalPosition > 0 && book.readPosition > 0) {
                        val progress = (book.readPosition.toFloat() / book.totalPosition.toFloat())
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .align(Alignment.BottomCenter)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight()
                                    .background(
                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                            alpha = 0.2f
                                        )
                                    )
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(progress)
                                    .fillMaxHeight()
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // 右侧书籍信息
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // 书籍标题
                    Text(
                        text = book.name ?: "",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // 作者信息和出版社
                    val authorAndPublisher = buildString {
                        if (book.author?.isNotBlank() == true) {
                            append(book.author)
                            if (book.press?.isNotBlank() == true) {
                                append(" / ")
                            }
                        }

                        if (book.press?.isNotBlank() == true) {
                            append(book.press)
                        }

                        // 添加假想的出版时间，因为参考图片中有展示
                        // 实际应该使用书籍的真实出版时间
                        val publishYear = 2020 + (book.id % 5).toInt() // 仅作演示用
                        val publishMonth =
                            (1 + (book.id % 12).toInt()).toString().padStart(2, '0') // 仅作演示用

                        if (isNotEmpty()) {
                            append(" / $publishYear-$publishMonth")
                        }
                    }

                    Text(
                        text = authorAndPublisher,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 阅读状态标签
                    val (statusText, statusColor) = when (book.readStatus) {
                        2 -> Pair("读完", MaterialTheme.colorScheme.primary) // ReadStatus.FINISHED
                        1 -> Pair(
                            "阅读中",
                            MaterialTheme.colorScheme.secondary
                        ) // ReadStatus.READING
                        else -> Pair(
                            "想读",
                            MaterialTheme.colorScheme.tertiary
                        ) // ReadStatus.UNREAD
                    }

                    // 阅读进度信息
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (book.readStatus > 0) {
                            Box(
                                modifier = Modifier
                                    .border(
                                        width = 1.dp,
                                        color = statusColor,
                                        shape = androidx.compose.foundation.shape.RoundedCornerShape(
                                            4.dp
                                        )
                                    )
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = statusText,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = statusColor
                                )
                            }

                            // 阅读时间
                            if (book.totalPosition > 0 && book.readPosition > 0 && book.readStatus != 2) {
                                Spacer(modifier = Modifier.width(8.dp))

                                val timeText = if (book.id % 2 == 0L) {
                                    // 模拟页数信息
                                    "${book.readPosition.toInt()} 页"
                                } else {
                                    // 模拟时间信息
                                    "${(1 + book.id % 3)} 小时 ${(book.id % 60).toInt()} 分钟"
                                }

                                Text(
                                    text = timeText,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // 完成百分比
                            if (book.totalPosition > 0 && book.readPosition > 0) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${((book.readPosition.toFloat() / book.totalPosition.toFloat()) * 100).toInt()}%",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // 日期信息，单独放在底部右侧
            val displayDate = if (book.updatedDate > 0) {
                val date = SimpleDateFormat("yyyy 年 MM 月 dd 日", Locale.getDefault())
                    .format(Date(book.updatedDate))
                date
            } else {
                // 模拟日期，参考图片中的格式
                "2024 年 ${6 + (book.id % 6).toInt()} 月 ${1 + (book.id % 28).toInt()} 日"
            }

            Text(
                text = displayDate,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                textAlign = TextAlign.End
            )
        }
    }
}

