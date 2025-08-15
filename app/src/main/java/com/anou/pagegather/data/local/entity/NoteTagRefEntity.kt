package com.anou.pagegather.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 笔记标签关联表
 * 表示笔记与标签之间的多对多关系
 */
@Entity(
    tableName = "note_tag_ref",
    foreignKeys = [
        ForeignKey(
            entity = NoteEntity::class,
            parentColumns = ["id"],
            childColumns = ["note_id"],
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
        Index(value = ["note_id"], name = "index_note_tag_ref_note_id"),
        Index(value = ["tag_id"], name = "index_note_tag_ref_tag_id")
    ]
)
data class NoteTagRefEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    /** 笔记ID */
    @ColumnInfo(name = "note_id")
    val noteId: Long,  

    /** 标签ID */
    @ColumnInfo(name = "tag_id")
    val tagId: Long,  

    /** 创建日期 */
    @ColumnInfo(name = "created_date")
    val createdDate: Long, 

    /** 更新日期 */
    @ColumnInfo(name = "updated_date")
    val updatedDate: Long,  

    /** 最后同步日期 */
    @ColumnInfo(name = "last_sync_date")
    val lastSyncDate: Long,  

    /** 是否已删除：0-否，1-是 */
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Int = 0
)