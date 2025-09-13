package com.anou.pagegather.data.local.dao

import androidx.room.*
import com.anou.pagegather.data.local.entity.BookCollectionEntity
import kotlinx.coroutines.flow.Flow

/**
 * 书籍收藏数据访问对象
 * 提供书籍收藏信息的数据库操作方法
 */
@Dao
interface BookCollectionDao {
    
    // ========== 查询操作 ==========
    
    /** 根据书籍ID获取收藏信息 */
    @Query("SELECT * FROM book_collection WHERE book_id = :bookId")
    suspend fun getByBookId(bookId: Long): BookCollectionEntity?
    
    /** 根据书籍ID获取收藏信息（Flow） */
    @Query("SELECT * FROM book_collection WHERE book_id = :bookId")
    fun getByBookIdFlow(bookId: Long): Flow<BookCollectionEntity?>
    
    /** 根据存放位置搜索 */
    @Query("SELECT * FROM book_collection WHERE storage_location LIKE '%' || :location || '%'")
    fun getByStorageLocation(location: String): Flow<List<BookCollectionEntity>>
    
    /** 获取所有收藏信息 */
    @Query("SELECT * FROM book_collection")
    fun getAllCollections(): Flow<List<BookCollectionEntity>>
    
    /** 根据拥有状态获取收藏信息 */
    @Query("SELECT * FROM book_collection WHERE ownership_status = :status")
    fun getByOwnershipStatus(status: Int): Flow<List<BookCollectionEntity>>
    
    /** 获取心愿单（按优先级排序） */
    @Query("""
        SELECT * FROM book_collection 
        WHERE ownership_status = 2 
        ORDER BY wishlist_priority ASC, created_date DESC
    """)
    fun getWishlist(): Flow<List<BookCollectionEntity>>
    
    /** 获取已拥有的书籍收藏信息 */
    @Query("SELECT * FROM book_collection WHERE ownership_status = 1")
    fun getOwnedBooks(): Flow<List<BookCollectionEntity>>
    
    // ========== 增删改操作 ==========
    
    /** 插入收藏信息 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(collection: BookCollectionEntity): Long
    
    /** 更新收藏信息 */
    @Update
    suspend fun update(collection: BookCollectionEntity)
    
    /** 删除收藏信息 */
    @Delete
    suspend fun delete(collection: BookCollectionEntity)
    
    /** 根据书籍ID删除收藏信息 */
    @Query("DELETE FROM book_collection WHERE book_id = :bookId")
    suspend fun deleteByBookId(bookId: Long)
    
    // ========== 便利操作 ==========
    
    /** 更新拥有状态 */
    @Query("UPDATE book_collection SET ownership_status = :status, updated_date = :updateTime WHERE book_id = :bookId")
    suspend fun updateOwnershipStatus(bookId: Long, status: Int, updateTime: Long = System.currentTimeMillis())
    
    /** 更新存放位置 */
    @Query("UPDATE book_collection SET storage_location = :location, updated_date = :updateTime WHERE book_id = :bookId")
    suspend fun updateStorageLocation(bookId: Long, location: String?, updateTime: Long = System.currentTimeMillis())
    
    /** 更新心愿单优先级 */
    @Query("UPDATE book_collection SET wishlist_priority = :priority, updated_date = :updateTime WHERE book_id = :bookId")
    suspend fun updateWishlistPriority(bookId: Long, priority: Int?, updateTime: Long = System.currentTimeMillis())
    
    /** 批量更新心愿单优先级 */
    @Query("UPDATE book_collection SET wishlist_priority = :priority WHERE book_id IN (:bookIds)")
    suspend fun batchUpdateWishlistPriority(bookIds: List<Long>, priority: Int)
}