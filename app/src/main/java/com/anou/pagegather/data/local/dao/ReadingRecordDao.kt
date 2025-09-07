package com.anou.pagegather.data.local.dao

import androidx.room.*
import com.anou.pagegather.data.local.entity.ReadingRecordEntity
import kotlinx.coroutines.flow.Flow

/**
 * 阅读记录数据访问对象
 * 提供阅读记录的数据库操作方法
 */
@Dao
interface ReadingRecordDao {
    
    // ========== 查询操作 ==========
    
    /** 获取所有阅读记录，按开始时间倒序排列 */
    @Query("SELECT * FROM reading_record ORDER BY start_time DESC")
    fun getAllReadingRecords(): Flow<List<ReadingRecordEntity>>
    
    /** 根据书籍ID获取阅读记录 */
    @Query("SELECT * FROM reading_record WHERE book_id = :bookId ORDER BY start_time DESC")
    fun getReadingRecordsByBookId(bookId: Long): Flow<List<ReadingRecordEntity>>
    
    /** 根据记录ID获取单个阅读记录 */
    @Query("SELECT * FROM reading_record WHERE id = :id")
    suspend fun getReadingRecordById(id: Long): ReadingRecordEntity?
    
    /** 根据日期获取阅读记录 */
    @Query("SELECT * FROM reading_record WHERE date = :date ORDER BY start_time DESC")
    fun getReadingRecordsByDate(date: String): Flow<List<ReadingRecordEntity>>
    
    /** 根据日期范围获取阅读记录 */
    @Query("SELECT * FROM reading_record WHERE date BETWEEN :startDate AND :endDate ORDER BY start_time DESC")
    fun getReadingRecordsByDateRange(startDate: String, endDate: String): Flow<List<ReadingRecordEntity>>
    
    // ========== 统计查询 ==========
    
    /** 获取指定书籍的总阅读时长 */
    @Query("SELECT SUM(duration) FROM reading_record WHERE book_id = :bookId")
    suspend fun getTotalReadingTimeByBookId(bookId: Long): Long?
    
    /** 获取指定日期的总阅读时长 */
    @Query("SELECT SUM(duration) FROM reading_record WHERE date = :date")
    suspend fun getTotalReadingTimeByDate(date: String): Long?
    
    /** 获取指定日期范围的总阅读时长 */
    @Query("SELECT SUM(duration) FROM reading_record WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getTotalReadingTimeByDateRange(startDate: String, endDate: String): Long?
    
    /** 获取指定日期的阅读会话数量 */
    @Query("SELECT COUNT(*) FROM reading_record WHERE date = :date")
    suspend fun getReadingSessionCountByDate(date: String): Int
    
    /** 获取指定日期范围内的阅读天数 */
    @Query("SELECT COUNT(DISTINCT date) FROM reading_record WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getReadingDaysCount(startDate: String, endDate: String): Int
    
    /** 获取指定书籍的阅读记录数量 */
    @Query("SELECT COUNT(*) FROM reading_record WHERE book_id = :bookId")
    suspend fun getReadingRecordCountByBookId(bookId: Long): Int
    
    /** 获取指定书籍的平均阅读时长 */
    @Query("SELECT AVG(duration) FROM reading_record WHERE book_id = :bookId")
    suspend fun getAverageReadingTimeByBookId(bookId: Long): Long?
    
    /** 获取指定书籍的最后一次阅读时间 */
    @Query("SELECT MAX(start_time) FROM reading_record WHERE book_id = :bookId")
    suspend fun getLastReadingTimeByBookId(bookId: Long): Long?
    
    /** 获取今天的总阅读时长 */
    @Query("SELECT SUM(duration) FROM reading_record WHERE date = date('now')")
    suspend fun getTodayTotalReadingTime(): Long?
    
    /** 获取本周的总阅读时长 */
    @Query("SELECT SUM(duration) FROM reading_record WHERE date >= date('now', '-7 days')")
    suspend fun getThisWeekTotalReadingTime(): Long?
    
    /** 获取本月的总阅读时长 */
    @Query("SELECT SUM(duration) FROM reading_record WHERE date >= date('now', '-30 days')")
    suspend fun getThisMonthTotalReadingTime(): Long?
    
    /** 获取阅读中的书籍数量 */
    @Query("SELECT COUNT(*) FROM book WHERE read_status = 1")
    suspend fun getReadingBooksCount(): Int
    
    // ========== 特殊查询 ==========
    
    /** 获取当前正在进行的阅读记录（end_time为null） */
    @Query("SELECT * FROM reading_record WHERE end_time IS NULL ORDER BY start_time DESC LIMIT 1")
    suspend fun getActiveReadingRecord(): ReadingRecordEntity?
    
    // ========== 增删改操作 ==========
    
    /** 插入新的阅读记录 */
    @Insert
    suspend fun insert(readingRecord: ReadingRecordEntity): Long
    
    /** 更新阅读记录 */
    @Update
    suspend fun update(readingRecord: ReadingRecordEntity)
    
    /** 删除阅读记录 */
    @Delete
    suspend fun delete(readingRecord: ReadingRecordEntity)
    
    /** 根据ID删除阅读记录 */
    @Query("DELETE FROM reading_record WHERE id = :id")
    suspend fun deleteById(id: Long)
    
    /** 删除指定书籍的所有阅读记录 */
    @Query("DELETE FROM reading_record WHERE book_id = :bookId")
    suspend fun deleteByBookId(bookId: Long)
}