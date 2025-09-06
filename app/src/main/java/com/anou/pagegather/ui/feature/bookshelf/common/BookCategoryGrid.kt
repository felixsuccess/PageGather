package com.anou.pagegather.ui.feature.bookshelf.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * 通用书籍网格项件，用于显示书籍分组的网格
 * 
 * @param title 标题
 * @param bookCount 书籍数量
 * @param onClick 点击事件
 * @param modifier Modifier
 * @param emptyIcon 空状态时显示的图标
 * @param emptyIconTint 空状态时图标的颜色
 * @param emptyContent 空状态时显示的内容
 * @param subtitle 副标题（可选）
 * @param content 自定义内容（用于显示预览）
 */
@Composable
fun BookCategoryGrid(
    title: String,
    bookCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    emptyIcon: ImageVector? = null,
    emptyIconTint: Color = MaterialTheme.colorScheme.primary,
    emptyContent: @Composable (() -> Unit)? = null,
    subtitle: String? = null,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .clickable(onClick = onClick)
            .padding(8.dp), // 添加一些内边距
        horizontalAlignment = Alignment.Start 
    ) {
        // 预览内容
        content()
        
        Spacer(modifier = Modifier.height(6.dp))
        
        // 标题和书籍数量
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )

        // 副标题（如果提供）
        subtitle?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // 书籍数量
        Text(
            text = "$bookCount 本",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        )
    }
}