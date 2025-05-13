package com.anou.pagegather.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey

@Entity(
    tableName = "note",
    foreignKeys = [
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["id"],
            childColumns = ["book_id"],
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
//        ForeignKey(
//            entity = ChapterEntity::class,
//            parentColumns = ["id"],
//            childColumns = ["chapter_id"],
//            onDelete = ForeignKey.NO_ACTION,
//            onUpdate = ForeignKey.NO_ACTION
//        )
    ]
)
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "book_id" , index = true)
    val bookId: Long?,

    @ColumnInfo(name = "chapter_id")
    val chapterId: Long,

    @ColumnInfo(name = "content")
    val content: String? = null,

    @ColumnInfo(name = "idea")
    val idea: String? = null,

    @ColumnInfo(name = "position")
    val position: String? = null,

    @ColumnInfo(name = "position_unit")
    val positionUnit: Int,

    @ColumnInfo(name = "include_time")
    val includeTime: Long,

    @ColumnInfo(name = "created_date")
    val createdDate: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_date")
    val updatedDate: Long = 0,

    @ColumnInfo(name = "last_sync_date")
    val lastSyncDate: Long,

    @ColumnInfo(name = "is_deleted")
    val isDeleted: Int,

//    @ColumnInfo(name = "attachments")
//    var attachments: List<Attachment> = arrayListOf(),
)

//// 附件， 只支持图片
//@Serializable
//@Parcelize
//data class Attachment(
//    val type: Type = Type.IMAGE,
//    val path: String = "",
//    val description: String = "",
//    val fileName: String = "",
//) : Parcelable {
//    enum class Type { AUDIO, IMAGE }
//
//    fun isEmpty() = path.isEmpty() && description.isEmpty() && fileName.isEmpty()
//}
//
//data class NoteIdWithPath(
//    val noteId: Long,
//    val path: String,
//)

//TODO: 评论，待定
////评论
//@Serializable
//@Parcelize
//@Entity(
//    foreignKeys = [ForeignKey(
//        entity = NoteEntity::class,
//        parentColumns = arrayOf("note_id"),
//        childColumns = arrayOf("note_comment_id"),
//        onDelete = CASCADE
//    )]
//)
//data class Comment(
//    @PrimaryKey(autoGenerate = true) var id: Long = 0,
//    @ColumnInfo(name = "note_comment_id", index = true) val noteCommentId: Long,
//    @ColumnInfo(name = "content", index = true) var content: String = "",
//    @ColumnInfo(name = "create_time") var createTime: Long = System.currentTimeMillis(),
//    @ColumnInfo(name = "update_time") var updateTime: Long = System.currentTimeMillis(),
//) : Parcelable