package com.anou.pagegather.data.local.entity


import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "book")
data class BookEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "name")
    val name: String? = "",
    @ColumnInfo(name = "author")
    val author: String? = "",
    @ColumnInfo(name = "cover_url")
    val coverUrl: String? = null,
    @ColumnInfo(name = "author_intro")
    val authorIntro: String? = "",
    @ColumnInfo(name = "translator")
    val translator: String? = "",
    @ColumnInfo(name = "isbn")
    val isbn: String? = "",
    @ColumnInfo(name = "publish_date")
    val publishDate: String? = "",
    @ColumnInfo(name = "press")
    val press: String? = "",
    @ColumnInfo(name = "summary")
    val summary: String? = "",
    @ColumnInfo(name = "read_position")
    val readPosition: Double = 0.0,
    @ColumnInfo(name = "total_position")
    val totalPosition: Int = 0,
    @ColumnInfo(name = "total_pagination")
    val totalPagination: Int = 0,
    @ColumnInfo(name = "type")
    val type: Int = 0,
    @ColumnInfo(name = "position_unit")
    val positionUnit: Int = 0,
    @ColumnInfo(name = "source_id")
    val bookSourceId: Int = 0,
    @ColumnInfo(name = "purchase_date")
    val purchaseDate: Long = 0,
    @ColumnInfo(name = "purchase_price")
    val purchasePrice: Double = 0.0,
    @ColumnInfo(name = "book_order")
    val bookOrder: Int = 0,
    @ColumnInfo(name = "rating")
    val rating: Float = 0.0f,
    @ColumnInfo(name = "bookmark_modified_time")
    val bookMarkModifiedTime: Long = 0,
    @ColumnInfo(name = "read_status")
    val readStatus: Int = 0,
    @ColumnInfo(name = "read_status_changed_date")
    val readStatusChangedDate: Long = 0,
    @ColumnInfo(name = "pinned")
    val pinned: Int = 0,
    @ColumnInfo(name = "pin_order")
    val pinOrder: Int = 0,
    @ColumnInfo(name = "created_date")
    val createdDate: Long = 0,
    @ColumnInfo(name = "updated_date")
    val updatedDate: Long = 0,
    @ColumnInfo(name = "last_sync_date")
    val lastSyncDate: Long = 0,
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Int = 0
)

