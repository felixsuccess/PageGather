package com.anou.pagegather.ui.feature.bookshelf.booklist

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.ui.unit.dp

// 常量定义
val GRID_COLUMNS = GridCells.Fixed(3)
val GRID_PADDING = PaddingValues(vertical = 12.dp, horizontal = 12.dp)
val GRID_SPACING = 12.dp

// 网格模式封面显示方式配置
// false: 使用默认图片加载方式（AsyncImage加载实际封面图片）
// true: 使用首字母占位符方式（显示书名首字母）
var useLetterPlaceholderForGrid: Boolean = true