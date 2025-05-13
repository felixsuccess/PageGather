package com.anou.pagegather.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tag"
)
data class TagEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
 
    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "color")
    val color: Int,

    @ColumnInfo(name = "tag_order")
    val tagOrder: Int,

/**
* 类型：0 书籍标签 ；1 书摘标签；
*/
    @ColumnInfo(name = "type")
    val type: Int,

    @ColumnInfo(name = "created_date")
    val createdDate: Long,

    @ColumnInfo(name = "updated_date")
    val updatedDate: Long,

    @ColumnInfo(name = "last_sync_date")
    val lastSyncDate: Long,

    @ColumnInfo(name = "is_deleted")
    val isDeleted: Int = 0  // 默认未删除
)