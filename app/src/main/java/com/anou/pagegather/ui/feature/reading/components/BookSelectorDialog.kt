package com.anou.pagegather.ui.feature.reading.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anou.pagegather.data.local.entity.BookEntity

@Composable
fun BookSelectorDialog(
    books: List<BookEntity>,
    selectedBook: BookEntity?,
    onBookSelect: (BookEntity) -> Unit,
    onDismiss: () -> Unit,
    onNavigateToAddBook: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    
    // 过滤书籍列表
    val filteredBooks = remember(books, searchQuery) {
        if (searchQuery.isBlank()) {
            books
        } else {
            books.filter { 
                it.name?.contains(searchQuery, ignoreCase = true) == true ||
                it.author?.contains(searchQuery, ignoreCase = true) == true
            }
        }
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "选择书籍",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 搜索框和添加按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("搜索书籍") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "搜索"
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    Button(
                        onClick = {
                            onDismiss()
                            onNavigateToAddBook()
                        },
                        modifier = Modifier.height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "添加书籍",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("添加")
                    }
                }
                
                if (filteredBooks.isEmpty() && searchQuery.isBlank()) {
                    // 如果没有书籍
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "暂无书籍",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "请先添加书籍",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else if (filteredBooks.isEmpty() && searchQuery.isNotBlank()) {
                    // 搜索无结果
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "未找到匹配的书籍",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    // 书籍列表
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .heightIn(max = 300.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(filteredBooks) { book ->
                            val isSelected = selectedBook?.id == book.id
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = isSelected,
                                        onClick = { onBookSelect(book) }
                                    ),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) {
                                        MaterialTheme.colorScheme.primaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.surface
                                    }
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = book.name ?: "",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) {
                                                    MaterialTheme.colorScheme.primary
                                                } else {
                                                    MaterialTheme.colorScheme.onSurface
                                                }
                                            )
                                            book.author?.let { author ->
                                                Text(
                                                    text = author,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }

                                        }
                                        
                                        // 选中标记
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "已选中",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (filteredBooks.isEmpty() && searchQuery.isBlank()) {
                Button(
                    onClick = { 
                        onDismiss()
                        onNavigateToAddBook()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("添加书籍")
                }
            }
        },
        dismissButton = {
            if (filteredBooks.isNotEmpty() || searchQuery.isNotBlank()) {
                TextButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("取消")
                }
            }
        },
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 6.dp
    )
}