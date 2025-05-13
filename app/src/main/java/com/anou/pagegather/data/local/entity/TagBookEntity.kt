package com.anou.pagegather.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tag_book",
    foreignKeys = [
        ForeignKey(
            entity = BookEntity::class,  
            parentColumns = ["id"],
            childColumns = ["book_id"],
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = TagEntity::class,  
            parentColumns = ["id"],
            childColumns = ["tag_id"],
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )
    ],
    indices = [
        Index(value = ["book_id"], name = "index_tag_book_book_id"),
        Index(value = ["tag_id"], name = "index_tag_book_tag_id")
    ]
)
data class TagBookEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "book_id")
    val bookId: Long,   

    @ColumnInfo(name = "tag_id")
    val tagId: Long,  

    @ColumnInfo(name = "created_date")
    val createdDate: Long,  

    @ColumnInfo(name = "updated_date")
    val updatedDate: Long,  

    @ColumnInfo(name = "last_sync_date")
    val lastSyncDate: Long,   

    @ColumnInfo(name = "is_deleted")
    val isDeleted: Int = 0  // 默认未删除（0表示未删除）
)