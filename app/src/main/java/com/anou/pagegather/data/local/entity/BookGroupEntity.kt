package com.anou.pagegather.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 书籍分组实体类
 * 
 * 用于书籍分组管理，支持自动和手动排序功能：
 * - 新建分组自动分配 group_order 值（最大值+1）
 * - 支持用户手动拖拽调整分组顺序
 * - 统一使用 group_order 字段进行排序显示
 */
@Entity(
    tableName = "book_group" 
)
data class BookGroupEntity(
    /** 主键ID，自动生成 */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    /** 分组名称（不能为空） */
    @ColumnInfo(name = "name")
    val name: String,

    /** 
     * 分组排序
     * 新建分组使用最大order值+1，支持手动和自动排序
     * 数值越小排序越靠前
     */
    @ColumnInfo(name = "group_order")
    val groupOrder: Int,

    /** 创建日期 */
    @ColumnInfo(name = "created_date")
    val createdDate: Long,

    /** 更新日期 */
    @ColumnInfo(name = "updated_date")
    val updatedDate: Long,

    /** 最后同步日期 */
    @ColumnInfo(name = "last_sync_date")
    val lastSyncDate: Long,

    /** 是否已删除 */
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Int = 0
) {
    /**
     * 获取显示名称
     */
    fun getDisplayName(): String = name.ifBlank { "未命名分组" }
}