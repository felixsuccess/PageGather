package com.anou.pagegather.ui.feature.bookshelf.group

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    onAddNewGroup: ((String) -> Unit)? = null,
    enabled: Boolean = true,
    label: String = "书籍分组",
    // 添加外部控制显示状态的参数
    showBottomSheet: Boolean = false,
    onBottomSheetVisibilityChange: ((Boolean) -> Unit)? = null,
    // 控制底部弹出框的最大高度，默认为屏幕高度的70%
    maxHeight: Float = 0.7f
) {
    availableGroups.find { it.id == selectedGroupId }
    BookGroupSelectorWithBottomSheet(
        availableGroups = availableGroups,
        selectedGroupId = selectedGroupId,
        onGroupSelectionChange = onGroupSelectionChange,
        modifier = modifier,
        onAddNewGroup = onAddNewGroup,
        onManageGroups = {
            // 导航到分组管理页面
            onAddNewGroup?.invoke("manage")
        },
        enabled = enabled,
        label = label,
        groupBookCounts = emptyMap(),
        // 传递外部控制状态
        showBottomSheet = showBottomSheet,
        onBottomSheetVisibilityChange = onBottomSheetVisibilityChange,
        // 传递最大高度参数
        maxHeight = maxHeight
    )

}

/**
 * 书籍分组选择器（底部弹出式）
 * 
 * 使用 ModalBottomSheet 实现的分组选择器，包含：
 * - 搜索框，支持关键字搜索分组
 * - 显示分组名称和书籍数量
 * - 管理按钮和添加按钮
 * - 底部取消按钮
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookGroupSelectorWithBottomSheet(
    availableGroups: List<BookGroupEntity>,
    selectedGroupId: Long?,
    onGroupSelectionChange: (Long?) -> Unit,
    modifier: Modifier = Modifier,
    onAddNewGroup: ((String) -> Unit)? = null,
    onManageGroups: (() -> Unit)? = null,
    enabled: Boolean = true,
    label: String = "书籍分组",
    // 每个分组的书籍数量，如果没有提供则不显示数量
    groupBookCounts: Map<Long, Int> = emptyMap(),
    // 添加外部控制显示状态的参数
    showBottomSheet: Boolean = false,
    onBottomSheetVisibilityChange: ((Boolean) -> Unit)? = null,
    // 控制底部弹出框的最大高度，默认为屏幕高度的70%
    maxHeight: Float = 0.7f
) {
    // 如果提供了外部控制，则使用外部状态；否则使用内部状态
    var showBottomSheetInternal by remember { mutableStateOf(false) }
    // 实际使用的状态
    val showBottomSheetState = if (onBottomSheetVisibilityChange != null) showBottomSheet else showBottomSheetInternal
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
        // 主选择区域
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable(enabled = enabled) { 
                    if (enabled) {
                        if (onBottomSheetVisibilityChange != null) {
                            onBottomSheetVisibilityChange(true)
                        } else {
                            showBottomSheetInternal = true
                        }
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
                        
                        // 显示书籍数量
                        groupBookCounts[selectedGroup.id]?.let { count ->
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "($count 本)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
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
            
            // 展开图标
            Icon(
                imageVector = Icons.Default.ExpandMore,
                contentDescription = "展开",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }
    }
    
    // 底部弹出选择器
    // 添加日志记录当前状态
    Log.d("BookGroupSelector", "显示状态: showBottomSheetState=$showBottomSheetState, 外部控制=${onBottomSheetVisibilityChange != null}")
    
    if (showBottomSheetState) {
        Log.d("BookGroupSelector", "打开底部弹出选择器")
        BookGroupSelectorBottomSheet(
            availableGroups = availableGroups,
            selectedGroupId = selectedGroupId,
            onGroupSelectionChange = onGroupSelectionChange,
            onDismissRequest = { 
                Log.d("BookGroupSelector", "关闭底部弹出选择器")
                if (onBottomSheetVisibilityChange != null) {
                    onBottomSheetVisibilityChange(false)
                } else {
                    showBottomSheetInternal = false
                }
            },
            onAddNewGroup = { 
                onAddNewGroup?.invoke("add")
                // 不再自动关闭模态框
            },
            onManageGroups = { 
                onManageGroups?.invoke()
                // 确保不关闭模态框
            },
            groupBookCounts = groupBookCounts,
            // 传递最大高度参数
            maxHeight = maxHeight
        )
    }
}

