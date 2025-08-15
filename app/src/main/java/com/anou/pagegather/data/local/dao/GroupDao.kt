package com.anou.pagegather.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.anou.pagegather.data.local.entity.GroupEntity

/**
 * 分组数据访问对象
 * 提供分组的增删改查操作
 */
@Dao
interface GroupDao {
    
    // ========== 查询操作 ==========
    
    /** 获取所有分组（按排序顺序） */
    @Query("SELECT * FROM `group` WHERE is_deleted = :notDeleted ORDER BY group_order ASC")
    fun getAllGroups(notDeleted: Boolean = false): Flow<List<GroupEntity>>
    
    /** 根据ID获取分组 */
    @Query("SELECT * FROM `group` WHERE id = :id AND is_deleted = :notDeleted")
    suspend fun getGroupById(id: Long, notDeleted: Boolean = false): GroupEntity?
    
    /** 根据名称搜索分组 */
    @Query("SELECT * FROM `group` WHERE name LIKE '%' || :name || '%' AND is_deleted = :notDeleted ORDER BY group_order ASC")
    fun searchGroupsByName(name: String, notDeleted: Boolean = false): Flow<List<GroupEntity>>
    
    /** 获取分组数量 */
    @Query("SELECT COUNT(*) FROM `group` WHERE is_deleted = :notDeleted")
    suspend fun getGroupCount(notDeleted: Boolean = false): Int
    
    /** 检查分组名称是否存在 */
    @Query("SELECT COUNT(*) FROM `group` WHERE name = :name AND is_deleted = :notDeleted AND id != :excludeId")
    suspend fun isGroupNameExists(name: String, excludeId: Long = -1, notDeleted: Boolean = false): Int
    
    // ========== 增删改操作 ==========
    
    /** 插入分组 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: GroupEntity): Long
    
    /** 插入多个分组 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroups(groups: List<GroupEntity>): List<Long>
    
    /** 更新分组 */
    @Update
    suspend fun updateGroup(group: GroupEntity)
    
    /** 删除分组（软删除） */
    @Query("UPDATE `group` SET is_deleted = :deleted, updated_date = :updateTime WHERE id = :id")
    suspend fun deleteGroup(id: Long, deleted: Boolean = true, updateTime: Long = System.currentTimeMillis())
    
    /** 物理删除分组 */
    @Query("DELETE FROM `group` WHERE id = :id")
    suspend fun deleteGroupPermanently(id: Long)
    
    // ========== 排序操作 ==========
    
    /** 更新分组排序 */
    @Query("UPDATE `group` SET group_order = :order, updated_date = :updateTime WHERE id = :id")
    suspend fun updateGroupOrder(id: Long, order: Int, updateTime: Long = System.currentTimeMillis())
    
    /** 获取最大排序值 */
    @Query("SELECT MAX(group_order) FROM `group` WHERE is_deleted = :notDeleted")
    suspend fun getMaxOrder(notDeleted: Boolean = false): Int?
    
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
    @Query("DELETE FROM `group` WHERE is_deleted = :deleted AND updated_date < :beforeTime")
    suspend fun cleanupDeletedGroups(beforeTime: Long, deleted: Boolean = true)
}