package com.anou.pagegather.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.anou.pagegather.data.local.entity.BookGroupRefEntity

/**
 * 书籍分组关联表数据访问对象
 * 管理书籍与分组之间的多对多关系
 */
@Dao
interface BookGroupRefDao {
    
    // ========== 查询操作 ==========
    
    /** 获取书籍的所有分组关联 */
    @Query("SELECT * FROM book_group_ref WHERE book_id = :bookId")
    fun getGroupRefsByBookId(bookId: Long): Flow<List<BookGroupRefEntity>>
    
    /** 获取分组的所有书籍关联 */
    @Query("SELECT * FROM book_group_ref WHERE group_id = :groupId")
    fun getBookRefsByGroupId(groupId: Long): Flow<List<BookGroupRefEntity>>
    
    /** 检查书籍是否在指定分组中 */
    @Query("SELECT COUNT(*) FROM book_group_ref WHERE book_id = :bookId AND group_id = :groupId")
    suspend fun isBookInGroup(bookId: Long, groupId: Long): Int
    
    /** 获取分组中的书籍数量 */
    @Query("SELECT COUNT(*) FROM book_group_ref WHERE group_id = :groupId")
    suspend fun getBookCountInGroup(groupId: Long): Int
    
    /** 获取书籍所属的分组数量 */
    @Query("SELECT COUNT(*) FROM book_group_ref WHERE book_id = :bookId")
    suspend fun getGroupCountForBook(bookId: Long): Int
    
    // ========== 增删改操作 ==========
    
    /** 添加书籍到分组 */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addBookToGroup(ref: BookGroupRefEntity): Long
    
    /** 批量添加书籍到分组 */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addBooksToGroup(refs: List<BookGroupRefEntity>): List<Long>
    
    /** 从分组中移除书籍 */
    @Query("DELETE FROM book_group_ref WHERE book_id = :bookId AND group_id = :groupId")
    suspend fun removeBookFromGroup(bookId: Long, groupId: Long)
    
    /** 移除书籍的所有分组关联 */
    @Query("DELETE FROM book_group_ref WHERE book_id = :bookId")
    suspend fun removeAllGroupsForBook(bookId: Long)
    
    /** 移除分组的所有书籍关联 */
    @Query("DELETE FROM book_group_ref WHERE group_id = :groupId")
    suspend fun removeAllBooksFromGroup(groupId: Long)
    
    /** 批量移除书籍的分组关联 */
    @Query("DELETE FROM book_group_ref WHERE book_id = :bookId AND group_id IN (:groupIds)")
    suspend fun removeBooksFromGroups(bookId: Long, groupIds: List<Long>)
    
    // ========== 批量操作 ==========
    
    /** 更新书籍的分组关联（先删除所有，再添加新的） */
    @Transaction
    suspend fun updateBookGroups(bookId: Long, groupIds: List<Long>) {
        removeAllGroupsForBook(bookId)
        if (groupIds.isNotEmpty()) {
            val refs = groupIds.map { groupId ->
                BookGroupRefEntity(bookId = bookId, groupId = groupId)
            }
            addBooksToGroup(refs)
        }
    }
    
    /** 更新分组的书籍关联（先删除所有，再添加新的） */
    @Transaction
    suspend fun updateGroupBooks(groupId: Long, bookIds: List<Long>) {
        removeAllBooksFromGroup(groupId)
        if (bookIds.isNotEmpty()) {
            val refs = bookIds.map { bookId ->
                BookGroupRefEntity(bookId = bookId, groupId = groupId)
            }
            addBooksToGroup(refs)
        }
    }
}