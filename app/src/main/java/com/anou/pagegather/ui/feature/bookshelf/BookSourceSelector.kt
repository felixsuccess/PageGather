package com.anou.pagegather.ui.feature.bookshelf

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Source
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.anou.pagegather.data.local.entity.BookSourceEntity

/**
 * 书籍来源选择器
 * 用于书籍编辑页面选择单个书籍来源
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookSourceSelector(
    availableBookSources: List<BookSourceEntity>,
    selectedBookSourceId: Long?,
    onBookSourceSelectionChange: (Long?) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String = "书籍来源"
) {
    var isExpanded by remember { mutableStateOf(false) }
    val selectedBookSource = availableBookSources.find { it.id == selectedBookSourceId }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // 主选择区域
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .clickable(enabled = enabled) { 
                        if (availableBookSources.isNotEmpty()) {
                            isExpanded = !isExpanded 
                        }
                    }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    if (selectedBookSource != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedBookSource.getDisplayName(),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    } else {
                        Text(
                            text = if (availableBookSources.isEmpty()) "暂无来源" else "点击选择来源",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }
                
                // 展开/收起图标
                if (availableBookSources.isNotEmpty()) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "收起" else "展开",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            // 书籍来源选择列表
            if (isExpanded) {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 280.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    // "无来源"选项
                    item {
                        BookSourceItem(
                            title = "无来源",
                            isSelected = selectedBookSourceId == null,
                            onSelectionChange = { onBookSourceSelectionChange(null) },
                            enabled = enabled,
                            isBuiltIn = false
                        )
                    }
                    
                    // 书籍来源列表
                    items(availableBookSources) { bookSource ->
                        BookSourceItem(
                            title = bookSource.getDisplayName(),
                            isSelected = bookSource.id == selectedBookSourceId,
                            onSelectionChange = { onBookSourceSelectionChange(bookSource.id) },
                            enabled = enabled,
                            isBuiltIn = bookSource.isBuiltIn
                        )
                    }
                }
            }
        }
    }
}

/**
 * 书籍来源条目
 */
@Composable
private fun BookSourceItem(
    modifier: Modifier = Modifier,
    title: String,
    isSelected: Boolean,
    onSelectionChange: () -> Unit,
    enabled: Boolean = true,
    isBuiltIn: Boolean = false
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        } else {
            Color.Transparent
        },
        animationSpec = tween(150), label = "backgroundColor"
    )
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .selectable(
                selected = isSelected,
                onClick = onSelectionChange,
                enabled = enabled,
                role = Role.RadioButton
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 选择状态指示器
        Box(
            modifier = Modifier
                .size(20.dp)
                .border(
                    width = 2.dp,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                    },
                    shape = CircleShape
                )
                .background(
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        Color.Transparent
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "已选择",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
            // 来源名称
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
    }
}