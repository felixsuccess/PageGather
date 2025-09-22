package com.anou.pagegather.ui.feature.bookshelf.booksource

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.anou.pagegather.data.local.entity.BookSourceEntity

/**
 * 书籍来源选择底部弹出框
 * 
 * 功能：
 * 1. 搜索框，支持关键字搜索来源
 * 2. 显示来源名称和书籍数量
 * 3. 管理按钮和添加按钮
 * 4. 底部取消按钮
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookSourceSelectorBottomSheet(
    availableSources: List<BookSourceEntity>,
    selectedSourceId: Long?,
    onSourceSelectionChange: (Long?) -> Unit,
    onDismissRequest: () -> Unit,
    onAddNewSource: (String) -> Unit,
    onManageSources: () -> Unit,
    // 每个来源的书籍数量，如果没有提供则不显示数量
    sourceBookCounts: Map<Long, Int> = emptyMap(),
    sheetState: SheetState = rememberModalBottomSheetState(),
    // 添加最大高度参数，默认为屏幕高度的70%
    maxHeight: Float = 0.7f
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        // 获取屏幕配置
        val configuration = LocalConfiguration.current
        val maxHeightDp = (configuration.screenHeightDp * maxHeight).dp
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = maxHeightDp)
                .padding(bottom = 16.dp)
        ) {
            // 顶部标题栏和操作按钮
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "选择书籍来源",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Row {
                    // 管理按钮
                    IconButton(
                        onClick = {
                            onManageSources()
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "管理来源",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    // 添加按钮
                    IconButton(
                        onClick = {
                            onAddNewSource("")
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "添加来源",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            HorizontalDivider()
            
            // 搜索框
            var searchQuery by remember { mutableStateOf("") }
            val focusManager = LocalFocusManager.current
            
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("搜索来源") },
                leadingIcon = { 
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "搜索"
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { searchQuery = "" },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "清除"
                            )
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = { focusManager.clearFocus() }
                ),
                shape = RoundedCornerShape(8.dp)
            )
            
            // 来源列表
            val filteredSources = remember(searchQuery, availableSources) {
                if (searchQuery.isEmpty()) {
                    availableSources
                } else {
                    availableSources.filter { 
                        it.getDisplayName().contains(searchQuery, ignoreCase = true) 
                    }
                }
            }
            
            if (filteredSources.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "没有找到匹配的来源",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    // "无来源"选项
                    item {
                        SourceItem(
                            title = "无来源",
                            isSelected = selectedSourceId == null,
                            onSelectionChange = { 
                                onSourceSelectionChange(null)
                                onDismissRequest()
                            },
                            bookCount = null,
                            isBuiltIn = false
                        )
                    }
                    
                    // 来源列表
                    items(
                        items = filteredSources,
                        key = { it.id }
                    ) { source ->
                        SourceItem(
                            title = source.getDisplayName(),
                            isSelected = source.id == selectedSourceId,
                            onSelectionChange = { 
                                onSourceSelectionChange(source.id)
                                onDismissRequest()
                            },
                            bookCount = sourceBookCounts[source.id],
                            isBuiltIn = source.isBuiltIn
                        )
                    }
                }
            }
            
            // 底部取消按钮
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Button(
                    onClick = onDismissRequest,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("取消")
                }
            }
        }
    }
}

@Composable
private fun SourceItem(
    modifier: Modifier = Modifier,
    title: String,
    isSelected: Boolean,
    onSelectionChange: () -> Unit,
    bookCount: Int? = null,
    isBuiltIn: Boolean = false
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
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .clickable(
                onClick = onSelectionChange,
                role = Role.RadioButton
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 选择指示器
        RadioButton(
            selected = isSelected,
            onClick = null,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary,
                unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // 来源名称
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isSelected) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        
        // 内置标识
        if (isBuiltIn) {
            Text(
                text = "内置",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        // 书籍数量
        if (bookCount != null) {
            Text(
                text = "$bookCount 本",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
        
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