package com.anou.pagegather.ui.feature.bookshelf.group

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.anou.pagegather.data.local.entity.BookGroupEntity

/**
 * 书籍分组选择器
 * 用于书籍编辑页面选择单个分组
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookGroupSelector(
    availableGroups: List<BookGroupEntity>,
    selectedGroupId: Long?,
    onGroupSelectionChange: (Long?) -> Unit,
    modifier: Modifier = Modifier,
    onAddNewGroup: (() -> Unit)? = null,
    enabled: Boolean = true,
    label: String = "书籍分组"
) {
    var isExpanded by remember { mutableStateOf(false) }
    val selectedGroup = availableGroups.find { it.id == selectedGroupId }
    
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
                        if (availableGroups.isNotEmpty()) {
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
                    
                    if (selectedGroup != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 分组标识圆点
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        MaterialTheme.colorScheme.primary,
                                        CircleShape
                                    )
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Text(
                                text = selectedGroup.getDisplayName(),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    } else {
                        Text(
                            text = if (availableGroups.isEmpty()) "暂无分组" else "点击选择分组",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            fontStyle = FontStyle.Italic
                        )
                    }
                }
                
                // 展开/收起图标
                if (availableGroups.isNotEmpty()) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "收起" else "展开",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            // 分组选择列表
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
                    // "无分组"选项
                    item {
                        GroupItem(
                            title = "无分组",
                            isSelected = selectedGroupId == null,
                            onSelectionChange = { onGroupSelectionChange(null) },
                            enabled = enabled
                        )
                    }
                    
                    // 分组列表
                    items(availableGroups) { group ->
                        GroupItem(
                            title = group.getDisplayName(),
                            isSelected = group.id == selectedGroupId,
                            onSelectionChange = { onGroupSelectionChange(group.id) },
                            enabled = enabled
                        )
                    }
                    
                    // 添加新分组按钮
                    if (onAddNewGroup != null) {
                        item {
                            AddNewGroupButton(
                                onClick = {
                                    onAddNewGroup()
                                    isExpanded = false
                                },
                                enabled = enabled
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 分组条目
 */
@Composable
private fun GroupItem(
    modifier: Modifier = Modifier,
    title: String,
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
                role = Role.RadioButton
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 选择指示器
        RadioButton(
            selected = isSelected,
            onClick = null,
            enabled = enabled,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary,
                unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // 分组名称
        Text(
            text = title,
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

/**
 * 添加新分组按钮
 */
@Composable
private fun AddNewGroupButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "添加新分组",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = "添加新分组",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
        )
    }
}