package com.anou.pagegather.data.local.dao

import androidx.room.*
import com.anou.pagegather.data.local.entity.BookSourceEntity
import kotlinx.coroutines.flow.Flow

/**
 * 书籍来源数据访问对象
 * 提供书籍来源的数据库操作方法
 */
@Dao
interface BookSourceDao {
    
    // ========== 查询操作 ==========
    
    /** 获取所有启用的来源（按排序顺序） */
    @Query("SELECT * FROM book_source WHERE is_enabled = 1 ORDER BY sort_order ASC, name ASC")
    fun getAllEnabledSources(): Flow<List<BookSourceEntity>>
    
    /** 获取所有来源（包括禁用的） */
    @Query("SELECT * FROM book_source ORDER BY is_builtin DESC, sort_order ASC, name ASC")
    fun getAllSources(): Flow<List<BookSourceEntity>>
    
    /** 根据ID获取来源 */
    @Query("SELECT * FROM book_source WHERE id = :id")
    suspend fun getSourceById(id: Long): BookSourceEntity?
    
    /** 获取内置来源（同步方法，用于初始化检查） */
    @Query("SELECT * FROM book_source WHERE is_builtin = :trueValue LIMIT 1")
    suspend fun getBuiltInSourcesSync(trueValue: Boolean = true): List<BookSourceEntity>
    
    // ========== 增删改操作 ==========
    
    /** 插入来源 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(source: BookSourceEntity): Long
    
    /** 批量插入来源 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sources: List<BookSourceEntity>)
    
    /** 更新来源 */
    @Update
    suspend fun update(source: BookSourceEntity)
    
    /** 删除来源 */
    @Delete
    suspend fun delete(source: BookSourceEntity)
    
    /** 根据ID删除来源 */
    @Query("DELETE FROM book_source WHERE id = :id")
    suspend fun deleteById(id: Long)
    
    // ========== 便利操作 ==========
    
    /** 启用/禁用来源 */
    @Query("UPDATE book_source SET is_enabled = :enabled, updated_date = :updateTime WHERE id = :id")
    suspend fun updateEnabled(id: Long, enabled: Int, updateTime: Long = System.currentTimeMillis())
    
    /** 获取最大排序值 */
    @Query("SELECT MAX(sort_order) FROM book_source")
    suspend fun getMaxSortOrder(): Int?
    
    /** 更新排序顺序 */
    @Query("UPDATE book_source SET sort_order = :sortOrder, updated_date = :updateTime WHERE id = :id")
    suspend fun updateSortOrder(id: Long, sortOrder: Int, updateTime: Long = System.currentTimeMillis())
    
    /** 批量更新排序 */
    @Transaction
    suspend fun updateSortOrders(sourceIds: List<Long>) {
        sourceIds.forEachIndexed { index, id ->
            updateSortOrder(id, index)
        }
    }
    
    /** 检查来源名称是否已存在 */
    @Query("SELECT COUNT(*) FROM book_source WHERE name = :name AND id != :excludeId")
    suspend fun isNameExists(name: String, excludeId: Long = -1): Int
}