package com.anou.pagegather.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * 阅读记录实体类
 * 用于记录用户的阅读会话信息，包括阅读时间、进度等
 */
@Entity(
    tableName = "reading_record",
    foreignKeys = [
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["id"],
            childColumns = ["book_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class ReadingRecordEntity(
    /** 主键ID，自动生成 */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    /** 关联的书籍ID */
    @ColumnInfo(name = "book_id", index = true)
    val bookId: Long,

    /** 开始阅读时间（时间戳） */
    @ColumnInfo(name = "start_time")
    val startTime: Long,

    /** 结束阅读时间（时间戳），null表示正在阅读中 */
    @ColumnInfo(name = "end_time")
    val endTime: Long? = null,

    /** 阅读时长（毫秒） */
    @ColumnInfo(name = "duration")
    val duration: Long = 0,

    /** 开始阅读时的进度 */
    @ColumnInfo(name = "start_progress")
    val startProgress: Double,

    /** 结束阅读时的进度 */
    @ColumnInfo(name = "end_progress")
    val endProgress: Double,

    /** 记录类型：0-精确记录（计时器），1-手动记录 */
    @ColumnInfo(name = "record_type")
    val recordType: Int,

    /** 阅读笔记或备注 */
    @ColumnInfo(name = "notes")
    val notes: String? = null,

    /** 阅读日期（YYYY-MM-DD格式） */
    @ColumnInfo(name = "date")
    val date: String,

    /** 记录创建时间 */
    @ColumnInfo(name = "created_date")
    val createdDate: Long = System.currentTimeMillis(),

    /** 记录修改时间 */
    @ColumnInfo(name = "modified_date")
    val modifiedDate: Long? = null
)

/**
 * 阅读记录类型枚举
 */
enum class RecordType {
    /** 精确记录（通过计时器自动记录） */
    PRECISE,
    /** 手动记录（用户手动输入） */
    MANUAL
}