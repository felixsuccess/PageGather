package com.anou.pagegather.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "group" 
)
data class GroupEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
 

    @ColumnInfo(name = "name")
    val name: String?,

    @ColumnInfo(name = "group_order")
    val groupOrder: Int,

    @ColumnInfo(name = "pinned")
    val pinned: Int = 0,

    @ColumnInfo(name = "pin_order")
    val pinOrder: Int = 0,

    @ColumnInfo(name = "created_date")
    val createdDate: Long,

    @ColumnInfo(name = "updated_date")
    val updatedDate: Long,

    @ColumnInfo(name = "last_sync_date")
    val lastSyncDate: Long,

    @ColumnInfo(name = "is_deleted")
    val isDeleted: Int = 0
)