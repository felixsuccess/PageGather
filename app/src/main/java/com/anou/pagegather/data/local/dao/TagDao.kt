package com.anou.pagegather.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.anou.pagegather.data.local.entity.TagEntity
import com.anou.pagegather.data.local.entity.TagType

/**
 * 标签数据访问对象
 * 提供标签的增删改查操作
 */
@Dao
interface TagDao {
    
    // ========== 查询操作 ==========
    
    /** 获取所有标签（按排序顺序） */
    @Query("SELECT * FROM tag WHERE is_deleted = 0 ORDER BY tag_order ASC")
    fun getAllTags(): Flow<List<TagEntity>>
    
    /** 根据类型获取标签 */
    @Query("SELECT * FROM tag WHERE tag_type = :type AND is_deleted = 0 ORDER BY tag_order ASC")
    fun getTagsByType(type: Int): Flow<List<TagEntity>>
    
    /** 获取书籍标签 */
    @Query("SELECT * FROM tag WHERE tag_type = 0 AND is_deleted = 0 ORDER BY tag_order ASC")
    fun getBookTags(): Flow<List<TagEntity>>
    
    /** 获取笔记标签 */
    @Query("SELECT * FROM tag WHERE tag_type = 1 AND is_deleted = 0 ORDER BY tag_order ASC")
    fun getNoteTags(): Flow<List<TagEntity>>
    
    /** 根据ID获取标签 */
    @Query("SELECT * FROM tag WHERE id = :id AND is_deleted = 0")
    suspend fun getTagById(id: Long): TagEntity?
    
    /** 根据名称搜索标签 */
    @Query("SELECT * FROM tag WHERE name LIKE '%' || :name || '%' AND is_deleted = 0 ORDER BY tag_order ASC")
    fun searchTagsByName(name: String): Flow<List<TagEntity>>
    
    /** 根据名称和类型搜索标签 */
    @Query("SELECT * FROM tag WHERE name LIKE '%' || :name || '%' AND tag_type = :type AND is_deleted = 0 ORDER BY tag_order ASC")
    fun searchTagsByNameAndType(name: String, type: Int): Flow<List<TagEntity>>
    
    /** 获取标签数量 */
    @Query("SELECT COUNT(*) FROM tag WHERE is_deleted = 0")
    suspend fun getTagCount(): Int
    
    /** 根据类型获取标签数量 */
    @Query("SELECT COUNT(*) FROM tag WHERE tag_type = :type AND is_deleted = 0")
    suspend fun getTagCountByType(type: Int): Int
    
    /** 检查标签名称是否存在 */
    @Query("SELECT COUNT(*) FROM tag WHERE name = :name AND tag_type = :type AND is_deleted = 0 AND id != :excludeId")
    suspend fun isTagNameExists(name: String, type: Int, excludeId: Long = -1): Int
    
    // ========== 增删改操作 ==========
    
    /** 插入标签 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: TagEntity): Long
    
    /** 插入多个标签 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTags(tags: List<TagEntity>): List<Long>
    
    /** 更新标签 */
    @Update
    suspend fun updateTag(tag: TagEntity)
    
    /** 删除标签（软删除） */
    @Query("UPDATE tag SET is_deleted = 1, updated_date = :updateTime WHERE id = :id")
    suspend fun deleteTag(id: Long, updateTime: Long = System.currentTimeMillis())
    
    /** 物理删除标签 */
    @Query("DELETE FROM tag WHERE id = :id")
    suspend fun deleteTagPermanently(id: Long)
    
    // ========== 排序操作 ==========
    
    /** 更新标签排序 */
    @Query("UPDATE tag SET tag_order = :order, updated_date = :updateTime WHERE id = :id")
    suspend fun updateTagOrder(id: Long, order: Int, updateTime: Long = System.currentTimeMillis())
    
    /** 根据类型获取最大排序值 */
    @Query("SELECT MAX(tag_order) FROM tag WHERE tag_type = :type AND is_deleted = 0")
    suspend fun getMaxOrderByType(type: Int): Int?
    
    // ========== 关联查询 ==========
    
    /** 获取书籍的标签 */
    @Query("""
        SELECT t.* FROM tag t 
        INNER JOIN book_tag_ref btr ON t.id = btr.tag_id 
        WHERE btr.book_id = :bookId AND t.is_deleted = 0 AND btr.is_deleted = 0
        ORDER BY t.tag_order ASC
    """)
    fun getTagsByBookId(bookId: Long): Flow<List<TagEntity>>
    
    /** 获取笔记的标签 */
    @Query("""
        SELECT t.* FROM tag t 
        INNER JOIN note_tag_ref ntr ON t.id = ntr.tag_id 
        WHERE ntr.note_id = :noteId AND t.is_deleted = 0 AND ntr.is_deleted = 0
        ORDER BY t.tag_order ASC
    """)
    fun getTagsByNoteId(noteId: Long): Flow<List<TagEntity>>
    
    // ========== 批量操作 ==========
    
    /** 批量更新排序 */
    @Transaction
    suspend fun updateTagOrders(tagOrders: Map<Long, Int>) {
        val updateTime = System.currentTimeMillis()
        tagOrders.forEach { (id, order) ->
            updateTagOrder(id, order, updateTime)
        }
    }
    
    /** 清理已删除的标签 */
    @Query("DELETE FROM tag WHERE is_deleted = 1 AND updated_date < :beforeTime")
    suspend fun cleanupDeletedTags(beforeTime: Long)
}