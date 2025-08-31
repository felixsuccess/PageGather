package com.anou.pagegather.ui.feature.bookshelf.filter

// 定义筛选选项数据类
data class FilterOption(
    val code: String,           // 唯一标识
    val title: String,         // 显示名称
    val remark: String? = null  // 备注/描述（可选）
)

// 常量定义筛选选项
val filterOptions = listOf(
    FilterOption("default", "默认", "显示所有书籍"),
    FilterOption("group", "分组", "按书籍分组筛选"),
    FilterOption("tag", "标签", "按标签筛选书籍"),
    FilterOption("status", "状态", "按阅读状态筛选"),
    FilterOption("source", "来源", "按书籍来源筛选"),
    FilterOption("rating", "评分", "按评分筛选书籍"),
    FilterOption("name", "名称", "按书籍名称排序")
)
