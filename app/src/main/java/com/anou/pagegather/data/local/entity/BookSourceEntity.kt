package com.anou.pagegather.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "book_source")
data class BookSourceEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "name")
    val name: String = "",
    @ColumnInfo(name = "is_builtin")
    val isBuiltIn: Int = 0  
)