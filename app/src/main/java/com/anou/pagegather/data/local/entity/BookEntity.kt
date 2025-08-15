package com.anou.pagegather.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

/**
 * 书籍实体类
 * 包含书籍的基本信息、阅读状态和藏书管理相关字段
 */
@Entity(tableName = "book")
data class BookEntity(
    /** 主键ID，自动生成 */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long,
    
    // ========== 基本信息 ==========
    /** 书名 */
    @ColumnInfo(name = "name")
    val name: String? = "",
    /** 作者 */
    @ColumnInfo(name = "author")
    val author: String? = "",
    /** 封面图片URL */
    @ColumnInfo(name = "cover_url")
    val coverUrl: String? = null,
    /** 作者简介 */
    @ColumnInfo(name = "author_intro")
    val authorIntro: String? = "",
    /** 译者 */
    @ColumnInfo(name = "translator")
    val translator: String? = "",
    /** ISBN号码 */
    @ColumnInfo(name = "isbn")
    val isbn: String? = "",
    /** 出版日期 */
    @ColumnInfo(name = "publish_date")
    val publishDate: String? = "",
    /** 出版社 */
    @ColumnInfo(name = "press")
    val press: String? = "",
    /** 内容简介 */
    @ColumnInfo(name = "summary")
    val summary: String? = "",
    /** 语言 */
    @ColumnInfo(name = "language")
    val language: String? = null,
    
    // ========== 阅读进度 ==========
    /** 当前阅读进度 */
    @ColumnInfo(name = "read_position")
    val readPosition: Double = 0.0,
    /** 总进度（页数或章节数） */
    @ColumnInfo(name = "total_position")
    val totalPosition: Int = 0,
    /** 总页数 */
    @ColumnInfo(name = "total_pagination")
    val totalPagination: Int = 0,
    /** 总章节数 */
    @ColumnInfo(name = "total_chapters")
    val totalChapters: Int? = null,
    /** 书籍类型：0-实体书，1-电子书 */
    @ColumnInfo(name = "type")
    val type: Int = 0,
    /** 进度单位：0-页数，1-章节，2-百分比 */
    @ColumnInfo(name = "position_unit")
    val positionUnit: Int = 0,
    /** 阅读状态：0-未读，1-正在读，2-已完成，3-已放弃 */
    @ColumnInfo(name = "read_status")
    val readStatus: Int = 0,
    /** 阅读状态变更日期 */
    @ColumnInfo(name = "read_status_changed_date")
    val readStatusChangedDate: Long = 0,
    /** 开始阅读日期 */
    @ColumnInfo(name = "start_reading_date")
    val startReadingDate: Long? = null,
    /** 完成阅读日期 */
    @ColumnInfo(name = "finished_date")
    val finishedDate: Long? = null,
    /** 最后阅读日期 */
    @ColumnInfo(name = "last_read_date")
    val lastReadDate: Long? = null,
    
    // ========== 评价和备注 ==========
    /** 个人评分 */
    @ColumnInfo(name = "rating")
    val rating: Float = 0.0f,
    /** 个人评价/书评 */
    @ColumnInfo(name = "review")
    val review: String? = null, 
 
    
    // ========== 购买信息 ==========
    /** 书籍来源ID */
    @ColumnInfo(name = "source_id")
    val bookSourceId: Int = 0,
    /** 购买日期 */
    @ColumnInfo(name = "purchase_date")
    val purchaseDate: Long = 0,
    /** 购买价格 */
    @ColumnInfo(name = "purchase_price")
    val purchasePrice: Double = 0.0,
    
    // ========== 排序和显示 ==========
    /** 书籍排序 */
    @ColumnInfo(name = "book_order")
    val bookOrder: Int = 0,
    /** 用户自定义排序顺序 */
    @ColumnInfo(name = "custom_sort_order")
    val customSortOrder: Int = 0,
    /** 是否置顶：0-否，1-是 */
    @ColumnInfo(name = "pinned")
    val pinned: Int = 0,
    /** 置顶排序 */
    @ColumnInfo(name = "pin_order")
    val pinOrder: Int = 0,
    
    // ========== 系统字段 ==========
    /** 书签修改时间 */
    @ColumnInfo(name = "bookmark_modified_time")
    val bookMarkModifiedTime: Long = 0,
    /** 创建日期 */
    @ColumnInfo(name = "created_date")
    val createdDate: Long = 0,
    /** 更新日期 */
    @ColumnInfo(name = "updated_date")
    val updatedDate: Long = 0,
    /** 最后同步日期 */
    @ColumnInfo(name = "last_sync_date")
    val lastSyncDate: Long = 0,
    /** 是否已删除：0-否，1-是 */
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Int = 0
) {
    /**
     * 获取阅读进度百分比
     */
    fun getProgressPercentage(): Float {
        return if (totalPosition > 0) {
            (readPosition / totalPosition * 100).toFloat()
        } else {
            0f
        }
    }
    
    /**
     * 获取进度显示文本
     */
    fun getProgressText(): String {
        val unit = ReadPositionUnit.entries.find { it.code == positionUnit }
        return when (unit) {
            ReadPositionUnit.PAGE -> "${readPosition.toInt()}/${totalPosition}页"
            ReadPositionUnit.CHAPTER -> "${readPosition.toInt()}/${totalPosition}章"
            ReadPositionUnit.PERCENT -> "${getProgressPercentage().toInt()}%"
            else -> "${readPosition.toInt()}/${totalPosition}"
        }
    }
    
    /**
     * 判断是否正在阅读
     */
    fun isReading(): Boolean = readStatus == ReadStatus.READING.code
    
    /**
     * 判断是否已完成
     */
    fun isFinished(): Boolean = readStatus == ReadStatus.FINISHED.code
    
    // 藏书管理相关方法已移至 BookManagementEntity
    
    /**
     * 获取阅读状态显示文本
     */
    fun getReadStatusText(): String {
        return ReadStatus.entries.find { it.code == readStatus }?.message ?: "未知状态"
    }
}

