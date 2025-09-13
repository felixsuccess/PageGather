package com.anou.pagegather.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.anou.pagegather.data.local.entity.BookTagRefEntity

/**
 * 书籍标签关联表数据访问对象
 * 管理书籍与标签之间的多对多关系
 */
@Dao
interface BookTagRefDao {
    
    // ========== 查询操作 ==========
    
    /** 获取书籍的所有标签关联 */
    @Query("SELECT * FROM book_tag_ref WHERE book_id = :bookId AND is_deleted = 0")
    fun getTagRefsByBookId(bookId: Long): Flow<List<BookTagRefEntity>>
    
    /** 获取标签的所有书籍关联 */
    @Query("SELECT * FROM book_tag_ref WHERE tag_id = :tagId AND is_deleted = 0")
    fun getBookRefsByTagId(tagId: Long): Flow<List<BookTagRefEntity>>
    
    /** 检查书籍是否有指定标签 */
    @Query("SELECT COUNT(*) FROM book_tag_ref WHERE book_id = :bookId AND tag_id = :tagId AND is_deleted = 0")
    suspend fun isBookHasTag(bookId: Long, tagId: Long): Int
    
    /** 获取标签下的书籍数量 */
    @Query("SELECT COUNT(*) FROM book_tag_ref WHERE tag_id = :tagId AND is_deleted = 0")
    suspend fun getBookCountByTag(tagId: Long): Int
    
    /** 获取书籍的标签数量 */
    @Query("SELECT COUNT(*) FROM book_tag_ref WHERE book_id = :bookId AND is_deleted = 0")
    suspend fun getTagCountForBook(bookId: Long): Int
    
    /** 获取所有书籍标签关联 */
    @Query("SELECT * FROM book_tag_ref")
    fun getAllTagRefs(): Flow<List<BookTagRefEntity>>
    
    // ========== 增删改操作 ==========
    
    /** 为书籍添加标签 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTagToBook(ref: BookTagRefEntity): Long
    
    /** 批量为书籍添加标签 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTagsToBook(refs: List<BookTagRefEntity>): List<Long>
    
    /** 从书籍移除标签（软删除） */
    @Query("UPDATE book_tag_ref SET is_deleted = 1, updated_date = :updateTime WHERE book_id = :bookId AND tag_id = :tagId")
    suspend fun removeTagFromBook(bookId: Long, tagId: Long, updateTime: Long = System.currentTimeMillis())
    
    /** 移除书籍的所有标签（软删除） */
    @Query("UPDATE book_tag_ref SET is_deleted = 1, updated_date = :updateTime WHERE book_id = :bookId")
    suspend fun removeAllTagsFromBook(bookId: Long, updateTime: Long = System.currentTimeMillis())
    
    /** 移除标签的所有书籍关联（软删除） */
    @Query("UPDATE book_tag_ref SET is_deleted = 1, updated_date = :updateTime WHERE tag_id = :tagId")
    suspend fun removeAllBooksFromTag(tagId: Long, updateTime: Long = System.currentTimeMillis())
    
    /** 物理删除书籍标签关联 */
    @Query("DELETE FROM book_tag_ref WHERE book_id = :bookId AND tag_id = :tagId")
    suspend fun deleteTagFromBookPermanently(bookId: Long, tagId: Long)
    
    /** 物理删除书籍的所有标签关联 */
    @Query("DELETE FROM book_tag_ref WHERE book_id = :bookId")
    suspend fun deleteAllTagsFromBookPermanently(bookId: Long)
    
    // ========== 批量操作 ==========
    
    /** 更新书籍的标签关联（先软删除所有，再添加新的） */
    @Transaction
    suspend fun updateBookTags(bookId: Long, tagIds: List<Long>) {
        val currentTime = System.currentTimeMillis()
        removeAllTagsFromBook(bookId, currentTime)
        if (tagIds.isNotEmpty()) {
            val refs = tagIds.map { tagId ->
                BookTagRefEntity(
                    bookId = bookId,
                    tagId = tagId,
                    createdDate = currentTime,
                    updatedDate = currentTime,
                    lastSyncDate = currentTime,
                    isDeleted = false
                )
            }
            addTagsToBook(refs)
        }
    }
    
    /** 更新标签的书籍关联（先软删除所有，再添加新的） */
    @Transaction
    suspend fun updateTagBooks(tagId: Long, bookIds: List<Long>) {
        val currentTime = System.currentTimeMillis()
        removeAllBooksFromTag(tagId, currentTime)
        if (bookIds.isNotEmpty()) {
            val refs = bookIds.map { bookId ->
                BookTagRefEntity(
                    bookId = bookId,
                    tagId = tagId,
                    createdDate = currentTime,
                    updatedDate = currentTime,
                    lastSyncDate = currentTime,
                    isDeleted = false
                )
            }
            addTagsToBook(refs)
        }
    }
    
    /** 恢复已删除的关联 */
    @Query("UPDATE book_tag_ref SET is_deleted = 0, updated_date = :updateTime WHERE book_id = :bookId AND tag_id = :tagId")
    suspend fun restoreBookTagRef(bookId: Long, tagId: Long, updateTime: Long = System.currentTimeMillis())
    
    /** 清理已删除的关联 */
    @Query("DELETE FROM book_tag_ref WHERE is_deleted = 1 AND updated_date < :beforeTime")
    suspend fun cleanupDeletedRefs(beforeTime: Long)
}