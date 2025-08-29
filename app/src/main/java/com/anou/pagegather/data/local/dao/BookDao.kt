
package com.anou.pagegather.data.local.dao


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import com.anou.pagegather.data.local.entity.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM book WHERE is_deleted = 0 ORDER BY updated_date DESC")
    fun getAllBooks(): Flow<List<BookEntity>>

    /**
     * 根据排序类型获取书籍
     * @param sortType 排序类型: 0-更新日期(默认), 1-创建日期, 2-书名, 3-作者, 4-阅读进度
     * @param ascending 是否升序排列
     */
    @Query("""
        SELECT * FROM book 
        WHERE is_deleted = 0 
        ORDER BY 
            CASE WHEN :sortType = 0 AND :ascending = 0 THEN updated_date END DESC,
            CASE WHEN :sortType = 0 AND :ascending = 1 THEN updated_date END ASC,
            CASE WHEN :sortType = 1 AND :ascending = 0 THEN created_date END DESC,
            CASE WHEN :sortType = 1 AND :ascending = 1 THEN created_date END ASC,
            CASE WHEN :sortType = 2 AND :ascending = 0 THEN name END COLLATE NOCASE DESC,
            CASE WHEN :sortType = 2 AND :ascending = 1 THEN name END COLLATE NOCASE ASC,
            CASE WHEN :sortType = 3 AND :ascending = 0 THEN author END COLLATE NOCASE DESC,
            CASE WHEN :sortType = 3 AND :ascending = 1 THEN author END COLLATE NOCASE ASC,
            CASE WHEN :sortType = 4 AND :ascending = 0 THEN read_position END DESC,
            CASE WHEN :sortType = 4 AND :ascending = 1 THEN read_position END ASC
    """)
    fun getBooksSorted(sortType: Int, ascending: Boolean = false): Flow<List<BookEntity>>

    /**
     * 根据排序类型和状态获取书籍
     */
    @Query("""
        SELECT * FROM book 
        WHERE read_status = :status AND is_deleted = 0 
        ORDER BY 
            CASE WHEN :sortType = 0 AND :ascending = 0 THEN updated_date END DESC,
            CASE WHEN :sortType = 0 AND :ascending = 1 THEN updated_date END ASC,
            CASE WHEN :sortType = 1 AND :ascending = 0 THEN created_date END DESC,
            CASE WHEN :sortType = 1 AND :ascending = 1 THEN created_date END ASC,
            CASE WHEN :sortType = 2 AND :ascending = 0 THEN name END COLLATE NOCASE DESC,
            CASE WHEN :sortType = 2 AND :ascending = 1 THEN name END COLLATE NOCASE ASC,
            CASE WHEN :sortType = 3 AND :ascending = 0 THEN author END COLLATE NOCASE DESC,
            CASE WHEN :sortType = 3 AND :ascending = 1 THEN author END COLLATE NOCASE ASC,
            CASE WHEN :sortType = 4 AND :ascending = 0 THEN read_position END DESC,
            CASE WHEN :sortType = 4 AND :ascending = 1 THEN read_position END ASC
    """)
    fun getBooksByStatusSorted(status: Int, sortType: Int, ascending: Boolean = false): Flow<List<BookEntity>>

    @Insert
    suspend fun insert(bookEntity: BookEntity): Long

    @Query("DELETE FROM book WHERE id = :bookId")
    suspend fun delete(bookId: Long): Int


    @Update
    fun update(bookEntity: BookEntity)

    @Delete
    fun delete(bookEntity: BookEntity)

    @Query("SELECT * FROM book WHERE id = :id")
    fun getById(id: Long): BookEntity?

    /** 根据关键词搜索书籍 */
    @Query("""
        SELECT * FROM book 
        WHERE is_deleted = 0 
        AND (name LIKE '%' || :query || '%' 
             OR author LIKE '%' || :query || '%' 
             OR summary LIKE '%' || :query || '%'
             OR isbn LIKE '%' || :query || '%')
        ORDER BY updated_date DESC
    """)
    fun searchBooks(query: String): Flow<List<BookEntity>>

    /** 模糊搜索书籍 */
    @Query("""
        SELECT * FROM book 
        WHERE is_deleted = 0 
        AND (name LIKE '%' || :query || '%' 
             OR author LIKE '%' || :query || '%' 
             OR summary LIKE '%' || :query || '%'
             OR isbn LIKE '%' || :query || '%')
        ORDER BY updated_date DESC
    """)
    fun fuzzySearchBooks(query: String): Flow<List<BookEntity>>

    /** 根据阅读状态获取书籍 */
    @Query("SELECT * FROM book WHERE read_status = :status AND is_deleted = 0 ORDER BY updated_date DESC")
    fun getBooksByStatus(status: Int): Flow<List<BookEntity>>

    /** 分页获取书籍 */
    @Query("""
        SELECT * FROM book 
        WHERE is_deleted = 0 
        ORDER BY 
            CASE WHEN :sortType = 0 AND :ascending = 0 THEN updated_date END DESC,
            CASE WHEN :sortType = 0 AND :ascending = 1 THEN updated_date END ASC,
            CASE WHEN :sortType = 1 AND :ascending = 0 THEN created_date END DESC,
            CASE WHEN :sortType = 1 AND :ascending = 1 THEN created_date END ASC,
            CASE WHEN :sortType = 2 AND :ascending = 0 THEN name END COLLATE NOCASE DESC,
            CASE WHEN :sortType = 2 AND :ascending = 1 THEN name END COLLATE NOCASE ASC,
            CASE WHEN :sortType = 3 AND :ascending = 0 THEN author END COLLATE NOCASE DESC,
            CASE WHEN :sortType = 3 AND :ascending = 1 THEN author END COLLATE NOCASE ASC,
            CASE WHEN :sortType = 4 AND :ascending = 0 THEN read_position END DESC,
            CASE WHEN :sortType = 4 AND :ascending = 1 THEN read_position END ASC
        LIMIT :pageSize OFFSET :offset
    """)
    fun getBooksPaged(offset: Int, pageSize: Int, sortType: Int, ascending: Boolean = false): Flow<List<BookEntity>>

    /** 分页获取特定状态的书籍 */
    @Query("""
        SELECT * FROM book 
        WHERE read_status = :status AND is_deleted = 0 
        ORDER BY 
            CASE WHEN :sortType = 0 AND :ascending = 0 THEN updated_date END DESC,
            CASE WHEN :sortType = 0 AND :ascending = 1 THEN updated_date END ASC,
            CASE WHEN :sortType = 1 AND :ascending = 0 THEN created_date END DESC,
            CASE WHEN :sortType = 1 AND :ascending = 1 THEN created_date END ASC,
            CASE WHEN :sortType = 2 AND :ascending = 0 THEN name END COLLATE NOCASE DESC,
            CASE WHEN :sortType = 2 AND :ascending = 1 THEN name END COLLATE NOCASE ASC,
            CASE WHEN :sortType = 3 AND :ascending =  0 THEN author END COLLATE NOCASE DESC,
            CASE WHEN :sortType = 3 AND :ascending = 1 THEN author END COLLATE NOCASE ASC,
            CASE WHEN :sortType = 4 AND :ascending = 0 THEN read_position END DESC,
            CASE WHEN :sortType = 4 AND :ascending = 1 THEN read_position END ASC
        LIMIT :pageSize OFFSET :offset
    """)
    fun getBooksByStatusPaged(status: Int, offset: Int, pageSize: Int, sortType: Int, ascending: Boolean = false): Flow<List<BookEntity>>
}