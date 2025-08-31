package com.anou.pagegather.ui.feature.bookshelf.group

// 定义分组信息数据类
data class GroupInfo(
    val id: Long,              // 分组唯一标识
    val name: String,          // 分组名称
    val remark: String? = null // 分组备注（可选）
)