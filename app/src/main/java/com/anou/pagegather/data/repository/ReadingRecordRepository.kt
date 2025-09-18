package com.anou.pagegather.data.repository

import androidx.room.withTransaction
import com.anou.pagegather.data.local.database.AppDatabase
import com.anou.pagegather.data.local.entity.ReadingRecordEntity
import com.anou.pagegather.data.local.entity.RecordType
import com.anou.pagegather.data.model.BookReadingStatisticsItemData
import com.anou.pagegather.ui.feature.statistics.TimeGranularity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
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
    fun getReadingRecordsByDateRange(
        startDate: String,
        endDate: String
    ): Flow<List<ReadingRecordEntity>> {
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

    /**
     * 获取指定日期范围内的阅读趋势数据
     * @param startDate 开始日期 (格式: yyyy-MM-dd)
     * @param endDate 结束日期 (格式: yyyy-MM-dd)
     * @return 日期到阅读时长的映射
     */
    suspend fun getReadingTrendDataByDateRange(
        startDate: String,
        endDate: String
    ): List<BookReadingStatisticsItemData> {
        val readingRecordsFlow = getReadingRecordsByDateRange(startDate, endDate)
        val readingRecords = readingRecordsFlow.firstOrNull() ?: emptyList()

        // 按日期分组并计算每天的总阅读时长
        val trendData = mutableMapOf<String, Long>()
        readingRecords.forEach { record ->
            val currentDate = record.date
            val currentDuration = trendData[currentDate] ?: 0L
            trendData[currentDate] = currentDuration + record.duration
        }

        val result = mutableListOf<BookReadingStatisticsItemData>()

        trendData.entries.forEachIndexed { index, (key, value) ->
            result.add(
                BookReadingStatisticsItemData(
                    label = key,
                    groupId = index, // 这里设置默认值，实际使用时可以根据需要设置
                    value = value.toFloat()
                )
            )
        }
        return result

    }

    /**
     * 基于时间粒度的阅读时长分布统计
     * @param startDate 开始日期 (格式: yyyy-MM-dd)
     * @param endDate 结束日期 (格式: yyyy-MM-dd)
     * @param granularity 时间粒度 (年/月/周)
     * @return 时间点到阅读时长的映射
     */
    suspend fun getReadingDurationDistributionByTimeGranularity(
        startDate: String,
        endDate: String,
        granularity: TimeGranularity
    ): List<BookReadingStatisticsItemData> {
        val readingRecordsFlow = getReadingRecordsByDateRange(startDate, endDate)
        val readingRecords = readingRecordsFlow.firstOrNull() ?: emptyList()

        val resultMap = when (granularity) {
            TimeGranularity.YEAR -> {
                // 按月份统计
                val monthlyData = mutableMapOf<String, Long>()
                readingRecords.forEach { record ->
                    try {
                        // 从日期中提取月份
                        val parts = record.date.split("-")
                        if (parts.size >= 3) {
                            val month = parts[1] // 获取月份
                            val currentDuration = monthlyData[month] ?: 0L
                            monthlyData[month] = currentDuration + record.duration
                        }
                    } catch (e: Exception) {
                        // 处理日期解析异常
                        e.printStackTrace()
                    }
                }
                // 确保1-12月都有数据（没有数据的月份为0）

                val result = mutableListOf<BookReadingStatisticsItemData>()
                for (i in 1..12) {
                    val month = String.format("%02d", i)
                    result.add(
                        BookReadingStatisticsItemData(
                            label = "${month}月",
                            groupId = i,
                            value = (monthlyData[month] ?: 0L).toFloat()
                        )
                    )
                }
                result
            }

            TimeGranularity.MONTH -> {
                // 按天统计
                val dailyData = mutableMapOf<String, Long>()
                readingRecords.forEach { record ->
                    try {
                        // 从日期中提取天
                        val parts = record.date.split("-")
                        if (parts.size >= 3) {
                            val day = parts[2] // 获取天
                            val currentDuration = dailyData[day]?: 0L
                            dailyData[day] = currentDuration + record.duration
                        }
                    } catch (e: Exception) {
                        // 处理日期解析异常
                        e.printStackTrace()
                    }
                }

                // 确保该月的所有天都有数据（没有数据的天为0）
                val result = mutableListOf<BookReadingStatisticsItemData>()
                try {
                    val parts = startDate.split("-")
                    if (parts.size >= 3) {
                        val year = parts[0].toInt()
                        val month = parts[1].toInt()
                        val daysInMonth = getDaysInMonth(year, month)
                        for (i in 1..daysInMonth) {
                            val dayKey = String.format("%02d", i)
                            result.add(
                                BookReadingStatisticsItemData(
                                    label = "${i}日",
                                    groupId = i,
                                    value = dailyData.getOrDefault(dayKey, 0L).toFloat()
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    // 处理日期解析异常
                    e.printStackTrace()
                }
                result
            }

            TimeGranularity.WEEK -> {
                // 按周内天统计
                val weeklyData = mutableMapOf<String, Long>()
                readingRecords.forEach { record ->
                    try {
                        // 从日期计算是周几（1-7，周一到周日）
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val date = dateFormat.parse(record.date)
                        val calendar = Calendar.getInstance()
                        calendar.time = date
                        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                        // 调整为周一为1，周日为7
                        val adjustedDayOfWeek =
                            if (dayOfWeek == Calendar.SUNDAY) 7 else dayOfWeek - 1
                        val currentDuration =
                            weeklyData.getOrDefault(adjustedDayOfWeek.toString(), 0L)
                        weeklyData[adjustedDayOfWeek.toString()] = currentDuration + record.duration
                    } catch (e: Exception) {
                        // 处理日期解析异常
                        e.printStackTrace()
                    }
                }
                // 确保1-7天都有数据（没有数据的天为0）
                val result = mutableListOf<BookReadingStatisticsItemData>()
                val weekdays = listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")
                for (i in 1..7) {
                    result.add(
                        BookReadingStatisticsItemData(
                            label = weekdays[i - 1],
                            groupId = i,
                            value = (weeklyData[i.toString()] ?: 0L).toFloat()
                        )
                    )
                }

                result
            }

            TimeGranularity.DAY -> {
                // 按小时统计
                val hourlyData = mutableMapOf<String, Long>()
                readingRecords.forEach { record ->
                    try {
                        // 从开始时间提取小时
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = record.startTime
                        val hour = calendar.get(Calendar.HOUR_OF_DAY)
                        val currentDuration = hourlyData[hour.toString()]?: 0L
                        hourlyData[hour.toString()] = currentDuration + record.duration
                    } catch (e: Exception) {
                        // 处理日期解析异常
                        e.printStackTrace()
                    }
                }
                // 确保0-23小时都有数据（没有数据的小时为0）
                val result = mutableListOf<BookReadingStatisticsItemData>()
                for (i in 0..23) {
                    result.add(
                        BookReadingStatisticsItemData(
                            label = "${i}时",
                            groupId = i,
                            value = (hourlyData[i.toString()] ?: 0L).toFloat()
                        )
                    )
                }
                result
            }
        }

        return resultMap
    }

    /**
     * 基于时间粒度的阅读趋势统计
     * @param startDate 开始日期 (格式: yyyy-MM-dd)
     * @param endDate 结束日期 (格式: yyyy-MM-dd)
     * @param granularity 时间粒度 (年/月/周/日)
     * @return 时间点到阅读时长的映射
     */
    suspend fun getReadingTrendDataByTimeGranularity(
        startDate: String,
        endDate: String,
        granularity: TimeGranularity
    ): List<BookReadingStatisticsItemData> {
        val readingRecordsFlow = getReadingRecordsByDateRange(startDate, endDate)
        val readingRecords = readingRecordsFlow.firstOrNull() ?: emptyList()

        val resultMap = when (granularity) {
            TimeGranularity.YEAR -> {
                // 按月份统计趋势
                val monthlyData = mutableMapOf<String, Long>()
                readingRecords.forEach { record ->
                    try {
                        // 从日期中提取月份
                        val parts = record.date.split("-")
                        if (parts.size >= 3) {
                            val month = parts[1] // 获取月份
                            val currentDuration = monthlyData[month] ?: 0L
                            monthlyData[month] = currentDuration + record.duration
                        }
                    } catch (e: Exception) {
                        // 处理日期解析异常
                        e.printStackTrace()
                    }
                }
                // 确保1-12月都有数据（没有数据的月份为0）
                val result = mutableListOf<BookReadingStatisticsItemData>()
                for (i in 1..12) {
                    val month = String.format("%02d", i)
                    result.add(
                        BookReadingStatisticsItemData(
                            label = "${month}月",
                            groupId = i,
                            value = (monthlyData[month] ?: 0L).toFloat()
                        )
                    )
                }
                result
            }

            TimeGranularity.MONTH -> {
                // 按天统计趋势
                val dailyData = mutableMapOf<String, Long>()
                readingRecords.forEach { record ->
                    try {
                        // 从日期中提取天
                        val parts = record.date.split("-")
                        if (parts.size >= 3) {
                            val day = parts[2] // 获取天
                            val currentDuration = dailyData.getOrDefault(day, 0L)
                            dailyData[day] = currentDuration + record.duration
                        }
                    } catch (e: Exception) {
                        // 处理日期解析异常
                        e.printStackTrace()
                    }
                }
                // 确保该月的所有天都有数据（没有数据的天为0）
                val result = mutableListOf<BookReadingStatisticsItemData>()
                try {
                    val parts = startDate.split("-")
                    if (parts.size >= 3) {
                        val year = parts[0].toInt()
                        val month = parts[1].toInt()
                        val daysInMonth = getDaysInMonth(year, month)
                        for (i in 1..daysInMonth) {
                            val dayKey = String.format("%02d", i)
                            result.add(
                                BookReadingStatisticsItemData(
                                    label = "${i}日",
                                    groupId = i,
                                    value = dailyData.getOrDefault(dayKey, 0L).toFloat()
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    // 处理日期解析异常
                    e.printStackTrace()
                }
                result
            }

            TimeGranularity.WEEK -> {
                // 按周内天统计趋势
                val weeklyData = mutableMapOf<String, Long>()
                readingRecords.forEach { record ->
                    try {
                        // 从日期计算是周几（1-7，周一到周日）
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val date = dateFormat.parse(record.date)
                        val calendar = Calendar.getInstance()
                        calendar.time = date
                        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                        // 调整为周一为1，周日为7
                        val adjustedDayOfWeek =
                            if (dayOfWeek == Calendar.SUNDAY) 7 else dayOfWeek - 1
                        val currentDuration =
                            weeklyData.getOrDefault(adjustedDayOfWeek.toString(), 0L)
                        weeklyData[adjustedDayOfWeek.toString()] = currentDuration + record.duration
                    } catch (e: Exception) {
                        // 处理日期解析异常
                        e.printStackTrace()
                    }
                }
                // 确保1-7天都有数据（没有数据的天为0）
                val result = mutableListOf<BookReadingStatisticsItemData>()
                val weekdays = listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")
                for (i in 1..7) {
                    result.add(
                        BookReadingStatisticsItemData(
                            label = weekdays[i - 1],
                            groupId = i,
                            value = weeklyData.getOrDefault(i.toString(), 0L).toFloat()
                        )
                    )
                }
                result
            }

            TimeGranularity.DAY -> {
                // 按小时统计趋势
                val hourlyData = mutableMapOf<String, Long>()
                readingRecords.forEach { record ->
                    try {
                        // 从开始时间提取小时
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = record.startTime
                        val hour = calendar.get(Calendar.HOUR_OF_DAY)
                        val currentDuration = hourlyData.getOrDefault(hour.toString(), 0L)
                        hourlyData[hour.toString()] = currentDuration + record.duration
                    } catch (e: Exception) {
                        // 处理日期解析异常
                        e.printStackTrace()
                    }
                }
                // 确保0-23小时都有数据（没有数据的小时为0）
                val result = mutableListOf<BookReadingStatisticsItemData>()
                for (i in 0..23) {
                    result.add(
                        BookReadingStatisticsItemData(
                            label = "${i}时",
                            groupId = i,
                            value = hourlyData.getOrDefault(i.toString(), 0L).toFloat()
                        )
                    )
                }
                result
            }
        }

        return resultMap
    }

    /**
     * 获取指定日期范围内的阅读习惯时间分布数据（按小时统计阅读次数）
     * @param startDate 开始日期 (格式: yyyy-MM-dd)
     * @param endDate 结束日期 (格式: yyyy-MM-dd)
     * @return 小时到阅读次数的映射
     */
    suspend fun getReadingHabitDataByDateRange(
        startDate: String,
        endDate: String
    ): List<BookReadingStatisticsItemData> {
        val readingRecordsFlow = getReadingRecordsByDateRange(startDate, endDate)
        val readingRecords = readingRecordsFlow.firstOrNull() ?: emptyList()

        // 按小时统计阅读次数
        val habitData = mutableMapOf<String, Int>()
        readingRecords.forEach { record ->
            try {
                // 从开始时间提取小时
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = record.startTime
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val currentCount = habitData.getOrDefault("${hour}时", 0)
                habitData["${hour}时"] = currentCount + 1
            } catch (e: Exception) {
                // 处理日期解析异常
                e.printStackTrace()
            }
        }

        // 确保0-23小时都有数据（没有数据的小时为0）
        val result = mutableListOf<BookReadingStatisticsItemData>()
        for (i in 0..23) {
            result.add(
                BookReadingStatisticsItemData(
                    label = "${i}时",
                    groupId = i,
                    value = habitData.getOrDefault(i.toString(), 0L).toFloat()
                )
            )
        }
        return result
    }

    /**
     * 获取指定年月的天数
     */
    private fun getDaysInMonth(year: Int, month: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, 1) // 月份从0开始
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    /**
     * 基于百分位数的分组：根据用户实际的阅读时长分布来动态划分区间
     * @param startDate 开始日期 (格式: yyyy-MM-dd)
     * @param endDate 结束日期 (格式: yyyy-MM-dd)
     * @return 时长区间到阅读次数的映射
     */
    suspend fun getReadingDurationDistributionByPercentiles(
        startDate: String,
        endDate: String
    ): Map<String, Int> {
        // 获取指定日期范围内的所有阅读时长数据
        val durations = readingRecordDao.getReadingDurationsByDateRange(startDate, endDate)

        if (durations.isEmpty()) {
            return emptyMap()
        }

        // 对时长数据进行排序
        val sortedDurations = durations.sorted()
        val size = sortedDurations.size

        // 计算百分位数
        val percentile25: Long = sortedDurations[(size * 0.25).toInt().coerceIn(0, size - 1)]
        val percentile50: Long = sortedDurations[(size * 0.50).toInt().coerceIn(0, size - 1)]
        val percentile75: Long = sortedDurations[(size * 0.75).toInt().coerceIn(0, size - 1)]

        // 根据百分位数分组
        val distribution = mutableMapOf<String, Int>()
        distribution["快速阅读(<${formatDuration(percentile25)})"] = 0
        distribution["中等时长(${formatDuration(percentile25)}-${formatDuration(percentile50)})"] =
            0
        distribution["较长阅读(${formatDuration(percentile50)}-${formatDuration(percentile75)})"] =
            0
        distribution["深度阅读(>${formatDuration(percentile75)})"] = 0

        // 统计各区间的数据
        durations.forEach { duration ->
            when {
                duration < percentile25 -> {
                    distribution["快速阅读(<${formatDuration(percentile25)})"] =
                        distribution["快速阅读(<${formatDuration(percentile25)})"]!! + 1
                }

                duration < percentile50 -> {
                    distribution["中等时长(${formatDuration(percentile25)}-${
                        formatDuration(
                            percentile50
                        )
                    })"] =
                        distribution["中等时长(${formatDuration(percentile25)}-${
                            formatDuration(
                                percentile50
                            )
                        })"]!! + 1
                }

                duration < percentile75 -> {
                    distribution["较长阅读(${formatDuration(percentile50)}-${
                        formatDuration(
                            percentile75
                        )
                    })"] =
                        distribution["较长阅读(${formatDuration(percentile50)}-${
                            formatDuration(
                                percentile75
                            )
                        })"]!! + 1
                }

                else -> {
                    distribution["深度阅读(>${formatDuration(percentile75)})"] =
                        distribution["深度阅读(>${formatDuration(percentile75)})"]!! + 1
                }
            }
        }

        return distribution
    }

    /**
     * 格式化持续时间显示（分钟）
     */
    private fun formatDuration(milliseconds: Long): String {
        val minutes = milliseconds / 60000
        return "${minutes}分钟"
    }

}