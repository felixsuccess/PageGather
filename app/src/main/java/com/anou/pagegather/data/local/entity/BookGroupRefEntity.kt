package com.anou.pagegather.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 书籍分组关联表
 * 表示书籍与分组之间的多对多关系
 */
@Entity(
    tableName = "book_group_ref",
    foreignKeys = [
        ForeignKey(
            entity = BookEntity::class,  
            parentColumns = ["id"],
            childColumns = ["book_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = GroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["group_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["book_id"], name = "index_book_group_book_id"),
        Index(value = ["group_id"], name = "index_book_group_group_id")
    ]
)
data class BookGroupRefEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    /** 书籍ID */
    @ColumnInfo(name = "book_id")
    val bookId: Long,

    /** 分组ID */
    @ColumnInfo(name = "group_id")
    val groupId: Long
)