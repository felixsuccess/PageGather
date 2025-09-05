package com.anou.pagegather.ui.feature.bookshelf.filter


import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anou.pagegather.ui.feature.bookshelf.booklist.useLetterPlaceholderForGrid

@Composable
fun BookFilterTabs(
    selectedFilter: FilterOption,
    onFilterSelected: (FilterOption) -> Unit,
    isGridMode: Boolean,
    onToggleDisplayMode: () -> Unit,
    onEnterBatchMode: () -> Unit
) {
    // 添加设置菜单状态
    var showSettingsMenu by remember { mutableStateOf(false) }
    // 添加排序选项状态
    var showSortOptions by remember { mutableStateOf(false) }

    Column {
        // 使用水平滚动的Row来容纳分类选项和设置按钮
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 使用水平滚动的Row来容纳分类选项
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 分类选项按钮 - 使用基础的Box和Text实现
                filterOptions.forEach { option ->
                    val isSelected = selectedFilter.code == option.code
                    // 添加选中状态的动画效果
                    val backgroundColor by animateColorAsState(
                        targetValue = if (isSelected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.surface,
                        animationSpec = tween(durationMillis = 200)
                    )
                    val textColor by animateColorAsState(
                        targetValue = if (isSelected)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        animationSpec = tween(durationMillis = 200)
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(backgroundColor)
                            .clickable(
                                enabled = !isSelected // 已选中的选项卡不可点击
                            ) {
                                onFilterSelected(option)
                            }
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = option.title,
                            style = MaterialTheme.typography.labelLarge,
                            color = textColor
                        )
                    }
                }
            }

            // 设置按钮 - 固定在右侧
            IconButton(
                onClick = { showSettingsMenu = true }
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "选项设置"
                )

                // 设置菜单
                DropdownMenu(
                    expanded = showSettingsMenu,
                    onDismissRequest = { showSettingsMenu = false }
                ) {
                    // 添加排序选项菜单项
                    DropdownMenuItem(
                        text = { Text("排序选项") },
                        onClick = {
                            showSettingsMenu = false
                            showSortOptions = true
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Sort,
                                contentDescription = null
                            )
                        }
                    )

                    // 添加批量操作菜单项
                    DropdownMenuItem(
                        text = { Text("批量操作") },
                        onClick = {
                            showSettingsMenu = false
                            onEnterBatchMode()
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.CheckBox,
                                contentDescription = null
                            )
                        }
                    )

                    // 添加显示模式切换菜单项
                    DropdownMenuItem(
                        text = {
                            Text(
                                if (isGridMode) "切换到列表模式" else "切换到网格模式"
                            )
                        },
                        onClick = {
                            showSettingsMenu = false
                            onToggleDisplayMode()
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (isGridMode) Icons.AutoMirrored.Filled.ViewList else Icons.Default.GridView,
                                contentDescription = null
                            )
                        }
                    )
                    
                    // 添加网格封面显示方式切换菜单项（在两种模式下都显示）
                    DropdownMenuItem(
                        text = { 
                            Text(
                                if (useLetterPlaceholderForGrid) "使用封面图片" else "使用首字母占位符"
                            ) 
                        },
                        onClick = {
                            showSettingsMenu = false
                            useLetterPlaceholderForGrid = !useLetterPlaceholderForGrid
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (useLetterPlaceholderForGrid) Icons.Default.GridView else Icons.Default.GridView,
                                contentDescription = null
                            )
                        }
                    )
                }
            }
        }
    }

    // 排序选项对话框
    if (showSortOptions) {
        SortOptionsDialog(
            onDismiss = { showSortOptions = false }
        )
    }
}

/**
 * 排序选项对话框
 */
@Composable
private fun SortOptionsDialog(
    onDismiss: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "排序选项",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 排序字段选项
                Text(
                    text = "排序字段",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // 按书名排序
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = false, // TODO: 从ViewModel获取当前排序状态
                        onClick = { /* TODO: 设置按书名排序 */ }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "书名",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // 按作者排序
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = false, // TODO: 从ViewModel获取当前排序状态
                        onClick = { /* TODO: 设置按作者排序 */ }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "作者",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // 按添加时间排序
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = true, // TODO: 从ViewModel获取当前排序状态
                        onClick = { /* TODO: 设置按添加时间排序 */ }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "添加时间",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // 按阅读状态排序
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = false, // TODO: 从ViewModel获取当前排序状态
                        onClick = { /* TODO: 设置按阅读状态排序 */ }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "阅读状态",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // 排序方向选项
                Text(
                    text = "排序方向",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // 升序
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = true, // TODO: 从ViewModel获取当前排序方向
                        onClick = { /* TODO: 设置升序 */ }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "升序",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // 降序
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = false, // TODO: 从ViewModel获取当前排序方向
                        onClick = { /* TODO: 设置降序 */ }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "降序",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        confirmButton = {
            androidx.compose.material3.TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("取消")
            }
        },
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 6.dp
    )
}

