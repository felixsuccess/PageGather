package com.anou.pagegather.data.local.entity

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 标签实体类
 * 用于书籍和笔记的标签管理
 */
@Entity(
    tableName = "tag"
)
data class TagEntity(
    /** 主键ID，自动生成 */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
 
    /** 标签名称 */
    @ColumnInfo(name = "name")
    val name: String,

    /** 标签颜色（十六进制颜色值，如：#FF5722） */
    @ColumnInfo(name = "color")
    val color: String? = null,

    /** 标签排序 */
    @ColumnInfo(name = "tag_order")
    val tagOrder: Int,

    /** 标签类型：0-书籍标签，1-笔记标签 */
    @ColumnInfo(name = "tag_type")
    val tagType: Int,

    /** 创建日期 */
    @ColumnInfo(name = "created_date")
    val createdDate: Long,

    /** 更新日期 */
    @ColumnInfo(name = "updated_date")
    val updatedDate: Long,

    /** 最后同步日期 */
    @ColumnInfo(name = "last_sync_date")
    val lastSyncDate: Long,

    /** 是否已删除 */
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean = false
) {
    /**
     * 判断是否为书籍标签
     */
    fun isBookTag(): Boolean = tagType == TagType.BOOK.code
    
    /**
     * 判断是否为笔记标签
     */
    fun isNoteTag(): Boolean = tagType == TagType.NOTE.code
    

    
    /**
     * 获取标签类型显示文本
     */
    fun getTypeText(): String {
        return TagType.fromCode(tagType).message
    }
    
    /**
     * 获取颜色值（带默认值）
     */
    fun getColorValue(): String {
        return color ?: "#2196F3" // 默认蓝色
    }
    
    /**
     * 获取显示名称
     */
    fun getDisplayName(): String = name.ifBlank { "未知标签" }
    
    /**
     * 获取标签颜色
     */
    @Composable
    fun getColor(): Color {
        return try {
            Color(android.graphics.Color.parseColor(getColorValue()))
        } catch (e: Exception) {
            MaterialTheme.colorScheme.primary
        }
    }
}
