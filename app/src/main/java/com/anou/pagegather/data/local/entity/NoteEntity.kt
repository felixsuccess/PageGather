package com.anou.pagegather.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

/**
 * 笔记实体类
 * 支持 Markdown 格式
 */
@Entity(
    tableName = "note",
    foreignKeys = [
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["id"],
            childColumns = ["book_id"],
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )
    ]
)
data class NoteEntity(
    /** 主键ID，自动生成 */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    /** 关联的书籍ID */
    @ColumnInfo(name = "book_id", index = true)
    val bookId: Long?,

    /** 笔记标题（可选） */
    @ColumnInfo(name = "title")
    val title: String? = null,

   /** 摘录原文（纯文本） */
    @ColumnInfo(name = "quote")
    val quote: String? = null,

    /** 个人想法/评论（支持 Markdown 图文混排） */
    @ColumnInfo(name = "idea")
    val idea: String? = null,

/** 章节名称（可选） */
    @ColumnInfo(name = "chapter_name")
    val chapterName: String? = null,
  
    @ColumnInfo(name = "position")
    val position: String? = null,

    @ColumnInfo(name = "position_unit")
    val positionUnit: Int,
 

    /** 创建日期 */
    @ColumnInfo(name = "created_date")
    val createdDate: Long = System.currentTimeMillis(),

    /** 修改日期 */
    @ColumnInfo(name = "updated_date")
    val updatedDate: Long? = null,

    /** 最后同步日期 */
    @ColumnInfo(name = "last_sync_date")
    val lastSyncDate: Long = 0,

    /** 是否已删除 */
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean = false,

    /** 删除日期 */
    @ColumnInfo(name = "deleted_date")
    val deletedDate: Long? = null,

    /** 附件数量 */
    @ColumnInfo(name = "attachment_count")
    val attachmentCount: Int = 0
) {
    /**
     * 业务规则：必须填写至少一项内容
     */
    fun isValid(): Boolean {
        return !idea.isNullOrBlank() || !quote.isNullOrBlank()
    }
 

    /**
     * 提取笔记中的内嵌图片路径（支持 Markdown 格式）
     */
    fun getInlineImagePaths(): List<String> {
        if (idea.isNullOrBlank()) {
            return emptyList()
        }
        // 正则表达式提取 ![alt](path) 中的 path
        val imageRegex = """!\[.*?\]\((.*?)\)""".toRegex()
        return imageRegex.findAll(idea).map { it.groupValues[1] }.toList()
    }
 
}


/**
 * 笔记附件实体类
 * 支持多种附件类型和内嵌图片标记
 */
@Entity(
    tableName = "note_attachment",
    foreignKeys = [
        ForeignKey(
            entity = NoteEntity::class,
            parentColumns = ["id"],
            childColumns = ["note_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)

@Parcelize
data class NoteAttachmentEntity(
    /** 主键ID，自动生成 */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    /** 关联的笔记ID */
    @ColumnInfo(name = "note_id", index = true)
    val noteId: Long,

    /** 原始文件名 */
    @ColumnInfo(name = "file_name")
    val fileName: String,

    /** 存储路径 */
    @ColumnInfo(name = "file_path")
    val filePath: String,

    /** 文件类型 */
    @ColumnInfo(name = "file_type")
    val fileType: AttachmentType,

    /** 文件大小（字节） */
    @ColumnInfo(name = "file_size")
    val fileSize: Long,

    /** MIME 类型 */
    @ColumnInfo(name = "mime_type")
    val mimeType: String,

    /** 是否为 Markdown 内嵌图片 */
    @ColumnInfo(name = "is_inline_image")
    val isInlineImage: Boolean = false,

    /** 附件排序 */
    @ColumnInfo(name = "sort_order")
    val sortOrder: Int = 0,

    /** 描述信息 */
    @ColumnInfo(name = "description")
    val description: String = "",

    /** 创建日期 */
    @ColumnInfo(name = "created_date")
    val createdDate: Long = System.currentTimeMillis()
) : Parcelable {
    
    /**
     * 检查附件是否为空
     */
    fun isEmpty() = filePath.isEmpty() && fileName.isEmpty()
    
    /**
     * 获取文件扩展名
     */
    fun getFileExtension(): String {
        return fileName.substringAfterLast('.', "")
    }
    
    /**
     * 检查是否为图片类型
     */
    fun isImage(): Boolean {
        return fileType == AttachmentType.IMAGE
    }
    
    /**
     * 检查是否为音频类型
     */
    fun isAudio(): Boolean {
        return fileType == AttachmentType.AUDIO
    }
    
    /**
     * 检查是否为视频类型
     */
    fun isVideo(): Boolean {
        return fileType == AttachmentType.VIDEO
    }
}

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