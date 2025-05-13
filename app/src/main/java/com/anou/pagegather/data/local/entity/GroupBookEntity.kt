package com.anou.pagegather.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "group_book",
    foreignKeys = [
        ForeignKey(
            entity = GroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["group_id"],
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = BookEntity::class,  
            parentColumns = ["id"],
            childColumns = ["book_id"],
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )
    ],
    indices = [
        Index(value = ["group_id"], name = "index_group_book_group_id"),
        Index(value = ["book_id"], name = "index_group_book_book_id")
    ]
)
data class GroupBookEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "group_id")
    val groupId: Long,

    @ColumnInfo(name = "book_id")
    val bookId: Long,

    @ColumnInfo(name = "created_date")
    val createdDate: Long,

    @ColumnInfo(name = "updated_date")
    val updatedDate: Long,

    @ColumnInfo(name = "last_sync_date")
    val lastSyncDate: Long,

    @ColumnInfo(name = "is_deleted")
    val isDeleted: Int = 0
)