package com.anou.pagegather.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * 书籍收藏实体类
 * 用于管理书籍的拥有状态、存放位置、心愿单等收藏信息
 */
@Entity(
    tableName = "book_collection",
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
data class BookCollectionEntity(
    /** 主键ID，自动生成 */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    /** 关联的书籍ID */
    @ColumnInfo(name = "book_id", index = true)
    val bookId: Long,

    /** 拥有状态：0-未设置，1-已拥有，2-想要购买，3-已借出，4-已借入 */
    @ColumnInfo(name = "ownership_status")
    val ownershipStatus: Int = 0,

    /** 存放位置（如：书房书架第2层、卧室床头柜等） */
    @ColumnInfo(name = "storage_location")
    val storageLocation: String? = null,

    /** 书籍状态：0-全新，1-良好，2-一般，3-破损 */
    @ColumnInfo(name = "book_condition")
    val bookCondition: Int? = null,

    /** 心愿单优先级（1-5，数字越小优先级越高） */
    @ColumnInfo(name = "wishlist_priority")
    val wishlistPriority: Int? = null,

    /** 心愿单备注 */
    @ColumnInfo(name = "wishlist_notes")
    val wishlistNotes: String? = null,

    /** 创建日期 */
    @ColumnInfo(name = "created_date")
    val createdDate: Long = System.currentTimeMillis(),

    /** 更新日期 */
    @ColumnInfo(name = "updated_date")
    val updatedDate: Long = System.currentTimeMillis()
) {
    /**
     * 判断是否已拥有
     */
    fun isOwned(): Boolean = ownershipStatus == OwnershipStatus.OWNED.code
    
    /**
     * 判断是否在心愿单中
     */
    fun isInWishlist(): Boolean = ownershipStatus == OwnershipStatus.WANT_TO_BUY.code
    
    /**
     * 判断是否已借出
     */
    fun isLentOut(): Boolean = ownershipStatus == OwnershipStatus.LENT_OUT.code
    
    /**
     * 判断是否已借入
     */
    fun isBorrowed(): Boolean = ownershipStatus == OwnershipStatus.BORROWED.code
    
    /**
     * 获取拥有状态显示文本
     */
    fun getOwnershipStatusText(): String {
        return OwnershipStatus.fromCode(ownershipStatus).message
    }
    
    /**
     * 获取书籍状态显示文本
     */
    fun getBookConditionText(): String? {
        return bookCondition?.let { condition ->
            BookCondition.fromCode(condition).message
        }
    }
}