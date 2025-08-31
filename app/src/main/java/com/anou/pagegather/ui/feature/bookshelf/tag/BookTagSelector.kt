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
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.anou.pagegather.data.local.entity.TagEntity
import com.anou.pagegather.data.local.entity.TagType
import androidx.core.graphics.toColorInt

/**
 * 通用标签选择器
 * 提供多选标签功能，采用下拉式设计，支持书籍标签和笔记标签
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagSelector(
    availableTags: List<TagEntity>,
    selectedTagIds: List<Long>,
    onTagSelectionChange: (Long) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String = "标签",
    tagType: TagType? = null // 可选的标签类型过滤
) {
    // 根据标签类型过滤可用标签
    val filteredTags = if (tagType != null) {
        availableTags.filter { it.tagType == tagType.code }
    } else {
        availableTags
    }
    
    var isExpanded by remember { mutableStateOf(false) }
    val selectedTags = filteredTags.filter { selectedTagIds.contains(it.id) }
    
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
                        if (filteredTags.isNotEmpty()) {
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
                    
                    // 显示选中的标签状态
                    when {
                        selectedTags.isEmpty() -> {
                            Text(
                                text = if (filteredTags.isEmpty()) "暂无可用标签" else "点击选择标签",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                        else -> {
                            // 显示所有选中的标签
                            Column {
                                selectedTags.forEachIndexed { index, tag ->
                                    TagDisplayRow(
                                        tag = tag,
                                        showCount = false,
                                        modifier = if (index > 0) Modifier.padding(top = 4.dp) else Modifier
                                    )
                                }
                            }
                        }
                    }
                }
                
                // 展开/收起图标
                if (filteredTags.isNotEmpty()) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "收起" else "展开",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            // 标签选择列表
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
                    // 标签列表
                    items(filteredTags) { tag ->
                        TagItem(
                            tag = tag,
                            isSelected = selectedTagIds.contains(tag.id),
                            onSelectionChange = { onTagSelectionChange(tag.id) },
                            enabled = enabled
                        )
                    }
                }
            }
        }
    }
}

/**
 * 书籍标签选择器（向后兼容的包装器）
 */
@Composable
fun BookTagSelector(
    availableTags: List<TagEntity>,
    selectedTagIds: List<Long>,
    onTagSelectionChange: (Long) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String = "书籍标签"
) {
    TagSelector(
        availableTags = availableTags,
        selectedTagIds = selectedTagIds,
        onTagSelectionChange = onTagSelectionChange,
        modifier = modifier,
        enabled = enabled,
        label = label,
        tagType = TagType.BOOK
    )
}

/**
 * 笔记标签选择器
 */
@Composable
fun NoteTagSelector(
    availableTags: List<TagEntity>,
    selectedTagIds: List<Long>,
    onTagSelectionChange: (Long) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String = "笔记标签"
) {
    TagSelector(
        availableTags = availableTags,
        selectedTagIds = selectedTagIds,
        onTagSelectionChange = onTagSelectionChange,
        modifier = modifier,
        enabled = enabled,
        label = label,
        tagType = TagType.NOTE
    )
}

/**
 * 标签显示行（用于显示单个选中的标签）
 */
@Composable
private fun TagDisplayRow(
    tag: TagEntity,
    showCount: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        // 标签颜色指示器
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Label,
            contentDescription = null,
            tint = try {
                Color((tag.color ?: "#2196F3").toColorInt())
            } catch (e: Exception) {
                MaterialTheme.colorScheme.primary
            },
            modifier = Modifier.size(16.dp)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = tag.getDisplayName(),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * 标签条目（用于下拉列表中的选择项）
 */
@Composable
private fun TagItem(
    modifier: Modifier = Modifier,
    tag: TagEntity,
    isSelected: Boolean,
    onSelectionChange: () -> Unit,
    enabled: Boolean
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        } else {
            Color.Transparent
        },
        animationSpec = tween(durationMillis = 200),
        label = "background_color"
    )
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .selectable(
                selected = isSelected,
                onClick = onSelectionChange,
                enabled = enabled,
                role = Role.Checkbox
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 多选复选框
        Checkbox(
            checked = isSelected,
            onCheckedChange = null,
            enabled = enabled,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // 标签颜色指示器
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Label,
            contentDescription = null,
            tint = try {
                Color(android.graphics.Color.parseColor(tag.color ?: "#2196F3"))
            } catch (e: Exception) {
                MaterialTheme.colorScheme.primary
            },
            modifier = Modifier.size(18.dp)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // 标签名称
        Text(
            text = tag.getDisplayName(),
            style = MaterialTheme.typography.bodyLarge,
            color = if (enabled) {
                if (isSelected) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            },
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        
        // 选中指示器
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "已选择",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}