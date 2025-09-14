package com.anou.pagegather.data.repository

import androidx.room.withTransaction
import com.anou.pagegather.data.local.database.AppDatabase
import com.anou.pagegather.data.local.entity.ReadingRecordEntity
import com.anou.pagegather.data.local.entity.RecordType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 阅读记录仓储类
 * 负责管理阅读记录的数据操作，包括CRUD操作、统计查询和会话管理
 */
@Singleton
class ReadingRecordRepository @Inject constructor(
    private val database: AppDatabase
) {
    private val readingRecordDao = database.readingRecordDao()

    /**
     * 在事务中执行数据库操作
     */
    suspend fun <T> runInTransaction(block: suspend () -> T): T {
        return database.withTransaction {
            block()
        }
    }

    // ========== 基础 CRUD 操作 ==========

    /** 获取所有阅读记录 */
    fun getAllReadingRecords(): Flow<List<ReadingRecordEntity>> {
        return readingRecordDao.getAllReadingRecords()
    }

    /** 根据书籍ID获取阅读记录 */
    fun getReadingRecordsByBookId(bookId: Long): Flow<List<ReadingRecordEntity>> {
        return readingRecordDao.getReadingRecordsByBookId(bookId)
    }

    /** 根据记录ID获取单个阅读记录 */
    suspend fun getReadingRecordById(id: Long): ReadingRecordEntity? {
        return readingRecordDao.getReadingRecordById(id)
    }

    /** 根据日期获取阅读记录 */
    fun getReadingRecordsByDate(date: String): Flow<List<ReadingRecordEntity>> {
        return readingRecordDao.getReadingRecordsByDate(date)
    }

    /** 根据日期范围获取阅读记录 */
    fun getReadingRecordsByDateRange(startDate: String, endDate: String): Flow<List<ReadingRecordEntity>> {
        return readingRecordDao.getReadingRecordsByDateRange(startDate, endDate)
    }

    /** 插入新的阅读记录 */
    suspend fun insertReadingRecord(readingRecord: ReadingRecordEntity): Long {
        return readingRecordDao.insert(readingRecord)
    }

    /** 更新阅读记录 */
    suspend fun updateReadingRecord(readingRecord: ReadingRecordEntity) {
        readingRecordDao.update(readingRecord)
    }

    /** 删除阅读记录 */
    suspend fun deleteReadingRecord(readingRecord: ReadingRecordEntity) {
        readingRecordDao.delete(readingRecord)
    }

    /** 根据ID删除阅读记录 */
    suspend fun deleteReadingRecordById(id: Long) {
        readingRecordDao.deleteById(id)
    }

    /** 删除指定书籍的所有阅读记录 */
    suspend fun deleteReadingRecordsByBookId(bookId: Long) {
        readingRecordDao.deleteByBookId(bookId)
    }

    // ========== 统计查询 ==========

    /** 获取指定书籍的总阅读时长（毫秒） */
    suspend fun getTotalReadingTimeByBookId(bookId: Long): Long {
        return readingRecordDao.getTotalReadingTimeByBookId(bookId) ?: 0L
    }

    /** 获取指定日期的总阅读时长（毫秒） */
    suspend fun getTotalReadingTimeByDate(date: String): Long {
        return readingRecordDao.getTotalReadingTimeByDate(date) ?: 0L
    }

    /** 获取指定日期范围的总阅读时长（毫秒） */
    suspend fun getTotalReadingTimeByDateRange(startDate: String, endDate: String): Long {
        return readingRecordDao.getTotalReadingTimeByDateRange(startDate, endDate) ?: 0L
    }

    /** 获取指定日期的阅读会话数量 */
    suspend fun getReadingSessionCountByDate(date: String): Int {
        return readingRecordDao.getReadingSessionCountByDate(date)
    }

    /** 获取总阅读时长 */
    suspend fun getTotalReadingTime(): Long {
        return readingRecordDao.getTotalReadingTime() ?: 0L
    }
    
    /** 获取指定日期范围内的阅读天数 */
    suspend fun getReadingDaysCount(startDate: String, endDate: String): Int {
        return readingRecordDao.getReadingDaysCount(startDate, endDate)
    }
    
    /** 获取今天的总阅读时长 */
    suspend fun getTodayTotalReadingTime(): Long {
        return readingRecordDao.getTodayTotalReadingTime() ?: 0L
    }
    
    /** 获取本周的总阅读时长 */
    suspend fun getThisWeekTotalReadingTime(): Long {
        return readingRecordDao.getThisWeekTotalReadingTime() ?: 0L
    }
    
    /** 获取本月的总阅读时长 */
    suspend fun getThisMonthTotalReadingTime(): Long {
        return readingRecordDao.getThisMonthTotalReadingTime() ?: 0L
    }
    
    /** 获取阅读中的书籍数量 */
    suspend fun getReadingBooksCount(): Int {
        return readingRecordDao.getReadingBooksCount()
    }
    
    /** 获取指定书籍的阅读记录数量 */
    suspend fun getReadingRecordCountByBookId(bookId: Long): Int {
        return readingRecordDao.getReadingRecordCountByBookId(bookId)
    }
    
    /** 获取指定书籍的平均阅读时长 */
    suspend fun getAverageReadingTimeByBookId(bookId: Long): Long {
        return readingRecordDao.getAverageReadingTimeByBookId(bookId) ?: 0L
    }
    
    /** 获取指定书籍的最后一次阅读时间 */
    suspend fun getLastReadingTimeByBookId(bookId: Long): Long? {
        return readingRecordDao.getLastReadingTimeByBookId(bookId)
    }
    
    // ========== 阅读会话管理 ==========

    /** 获取当前正在进行的阅读记录 */
    suspend fun getActiveReadingRecord(): ReadingRecordEntity? {
        return readingRecordDao.getActiveReadingRecord()
    }

    /**
     * 开始阅读会话
     * @param bookId 书籍ID
     * @param startProgress 开始阅读进度
     * @param notes 备注信息
     * @return 新创建的阅读记录ID
     */
    suspend fun startReadingSession(
        bookId: Long,
        startProgress: Double,
        notes: String? = null
    ): Long {
        val currentTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.format(Date(currentTime))
        
        val readingRecord = ReadingRecordEntity(
            bookId = bookId,
            startTime = currentTime,
            startProgress = startProgress,
            endProgress = startProgress,
            recordType = RecordType.PRECISE.ordinal,
            notes = notes,
            date = date
        )
        
        return insertReadingRecord(readingRecord)
    }

    /**
     * 结束阅读会话
     * @param recordId 阅读记录ID
     * @param endProgress 结束阅读进度
     * @param notes 备注信息
     */
    suspend fun endReadingSession(
        recordId: Long,
        endProgress: Double,
        notes: String? = null
    ) {
        val record = getReadingRecordById(recordId) ?: return
        val endTime = System.currentTimeMillis()
        val duration = endTime - record.startTime
        
        val updatedRecord = record.copy(
            endTime = endTime,
            duration = duration,
            endProgress = endProgress,
            notes = notes ?: record.notes,
            modifiedDate = endTime
        )
        
        updateReadingRecord(updatedRecord)
    }

    /**
     * 添加手动阅读记录
     * @param bookId 书籍ID
     * @param startProgress 开始进度
     * @param endProgress 结束进度
     * @param duration 阅读时长（毫秒）
     * @param date 阅读日期（YYYY-MM-DD格式）
     * @param notes 备注信息
     * @return 新创建的阅读记录ID
     */
    suspend fun addManualReadingRecord(
        bookId: Long,
        startProgress: Double,
        endProgress: Double,
        duration: Long,
        date: String,
        notes: String? = null
    ): Long {
        val currentTime = System.currentTimeMillis()
        
        val readingRecord = ReadingRecordEntity(
            bookId = bookId,
            startTime = currentTime - duration,
            endTime = currentTime,
            duration = duration,
            startProgress = startProgress,
            endProgress = endProgress,
            recordType = RecordType.MANUAL.ordinal,
            notes = notes,
            date = date
        )
        
        return insertReadingRecord(readingRecord)
    }
    
    /**
     * 获取指定日期范围内有阅读记录的书籍ID列表
     * @param startDate 开始日期 (格式: yyyy-MM-dd)
     * @param endDate 结束日期 (格式: yyyy-MM-dd)
     * @return 书籍ID列表
     */
    suspend fun getBookIdsByDateRange(startDate: String, endDate: String): List<Long> {
        val readingRecordsFlow = getReadingRecordsByDateRange(startDate, endDate)
        val readingRecords = readingRecordsFlow.firstOrNull() ?: emptyList()
        return readingRecords.map { record -> record.bookId }.distinct()
    }
}