package com.anou.pagegather.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.anou.pagegather.data.local.entity.BookGroupEntity

/**
 * 书籍分组数据访问对象
 * 
 * 提供书籍分组的增删改查操作，支持基于 group_order 的统一排序：
 * - 所有查询操作按 group_order DESC 排序（数字越大越靠前）
 * - 新建分组使用 getMaxOrder() + 1 分配排序值，确保新分组在最前面
 * - 支持手动拖拽调整分组顺序
 */
@Dao
interface BookGroupDao {
    
    // ========== 查询操作 ==========
    
    /** 获取所有分组（按group_order降序，最新在上面） */
    @Query("SELECT * FROM book_group WHERE is_deleted = 0 ORDER BY group_order DESC, created_date DESC")
    fun getAllGroups(): Flow<List<BookGroupEntity>>
    
    /** 根据ID获取分组 */
    @Query("SELECT * FROM book_group WHERE id = :id AND is_deleted = 0")
    suspend fun getGroupById(id: Long): BookGroupEntity?
    
    /** 根据名称搜索分组 */
    @Query("SELECT * FROM book_group WHERE name LIKE '%' || :name || '%' AND is_deleted = 0 ORDER BY group_order DESC, created_date DESC")
    fun searchGroupsByName(name: String): Flow<List<BookGroupEntity>>
    
    /** 获取分组数量 */
    @Query("SELECT COUNT(*) FROM book_group WHERE is_deleted = 0")
    suspend fun getGroupCount(): Int
    
    /** 检查分组名称是否存在 */
    @Query("SELECT COUNT(*) FROM book_group WHERE name = :name AND is_deleted = 0 AND id != :excludeId")
    suspend fun isGroupNameExists(name: String, excludeId: Long = -1): Int
    
    // ========== 增删改操作 ==========
    
    /** 插入分组 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: BookGroupEntity): Long
    
    /** 插入多个分组 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroups(groups: List<BookGroupEntity>): List<Long>
    
    /** 更新分组 */
    @Update
    suspend fun updateGroup(group: BookGroupEntity)
    
    /** 删除分组（软删除） */
    @Query("UPDATE book_group SET is_deleted = 1, updated_date = :updateTime WHERE id = :id")
    suspend fun deleteGroup(id: Long, updateTime: Long = System.currentTimeMillis())
    
    /** 物理删除分组 */
    @Query("DELETE FROM book_group WHERE id = :id")
    suspend fun deleteGroupPermanently(id: Long)
    
    // ========== 排序操作 ==========
    
    /** 更新分组排序 */
    @Query("UPDATE book_group SET group_order = :order, updated_date = :updateTime WHERE id = :id")
    suspend fun updateGroupOrder(id: Long, order: Int, updateTime: Long = System.currentTimeMillis())
    
    /** 
     * 获取最大排序值
     * 用于新建分组时分配排序值（maxOrder + 1），确保新分组排在最前面
 */
    @Query("SELECT MAX(group_order) FROM book_group WHERE is_deleted = 0")
    suspend fun getMaxOrder(): Int?
    
    // ========== 批量操作 ==========
    
    /** 批量更新排序 */
    @Transaction
    suspend fun updateGroupOrders(groupOrders: Map<Long, Int>) {
        val updateTime = System.currentTimeMillis()
        groupOrders.forEach { (id, order) ->
            updateGroupOrder(id, order, updateTime)
        }
    }
    
    /** 清理已删除的分组 */
    @Query("DELETE FROM book_group WHERE is_deleted = 1 AND updated_date < :beforeTime")
    suspend fun cleanupDeletedGroups(beforeTime: Long)
}