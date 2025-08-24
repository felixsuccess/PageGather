package com.anou.pagegather.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

/**
 * 书籍来源实体类
 * 支持预定义来源和用户自定义来源的管理
 */
@Entity(tableName = "book_source")
data class BookSourceEntity(
    /** 主键ID，自动生成 */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    
    /** 来源名称 */
    @ColumnInfo(name = "name")
    val name: String,
    
    /** 来源图标名称（可选） */
    @ColumnInfo(name = "icon_name")
    val iconName: String? = null,
    
    /** 是否为内置来源 */
    @ColumnInfo(name = "is_builtin")
    val isBuiltIn: Boolean = false,
    
    /** 是否启用 */
    @ColumnInfo(name = "is_enabled")
    val isEnabled: Boolean = true,
    
    /** 
     * 排序顺序
     * 新建来源使用最大sort_order值+1，确保新来源显示在最前面
     */
    @ColumnInfo(name = "sort_order")
    val sortOrder: Int = 0,
    
    /** 创建日期 */
    @ColumnInfo(name = "created_date")
    val createdDate: Long = System.currentTimeMillis(),
    
    /** 更新日期 */
    @ColumnInfo(name = "updated_date")
    val updatedDate: Long = System.currentTimeMillis()
) {
    /**
     * 判断是否为内置来源
     */

    
    
    
    /**
     * 获取显示名称
     */
    fun getDisplayName(): String = name.ifBlank { "未知来源" }
}