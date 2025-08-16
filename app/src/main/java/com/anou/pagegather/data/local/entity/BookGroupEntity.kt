package com.anou.pagegather.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 书籍分组实体类
 * 用于书籍分组管理，保持简单实用
 */
@Entity(
    tableName = "book_group" 
)
data class BookGroupEntity(
    /** 主键ID，自动生成 */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    /** 分组名称 */
    @ColumnInfo(name = "name")
    val name: String?,

    /** 分组排序 */
    @ColumnInfo(name = "group_order")
    val groupOrder: Int,

    /** 是否置顶 */
    @ColumnInfo(name = "pinned")
    val pinned: Int = 0,

    /** 置顶排序 */
    @ColumnInfo(name = "pin_order")
    val pinOrder: Int = 0,

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
    fun getDisplayName(): String = name ?: "未命名分组"
}