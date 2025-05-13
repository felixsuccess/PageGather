package com.anou.pagegather.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tag_note",
    foreignKeys = [
        ForeignKey(
            entity = TagEntity::class,  // 关联 tag 表
            parentColumns = ["id"],
            childColumns = ["tag_id"],
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = NoteEntity::class,   
            parentColumns = ["id"],
            childColumns = ["note_id"],
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )
    ],
    indices = [
        Index(value = ["tag_id"], name = "index_tag_note_tag_id"),
        Index(value = ["note_id"], name = "index_tag_note_note_id")
    ]
)
data class TagNoteEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "tag_id")
    val tagId: Long,  

    @ColumnInfo(name = "note_id")
    val noteId: Long,  

    @ColumnInfo(name = "created_date")
    val createdDate: Long, 

    @ColumnInfo(name = "updated_date")
    val updatedDate: Long,  

    @ColumnInfo(name = "last_sync_date")
    val lastSyncDate: Long,  

    @ColumnInfo(name = "is_deleted")
    val isDeleted: Int = 0  // 默认未删除（0表示未删除）
)