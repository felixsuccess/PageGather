package com.anou.pagegather.data.model

/**
 * 书籍阅读统计信息数据类
 * 用于封装单本书籍的阅读统计数据
 */
data class BookReadingStatistics(
    /** 书籍ID */
    val bookId: Long,
    
    /** 书籍名称 */
    val bookName: String,
    
    /** 总阅读时长（毫秒） */
    val totalReadingTime: Long,
    
    /** 阅读记录数量 */
    val readingRecordCount: Int,
    
    /** 平均阅读时长（毫秒） */
    val averageReadingTime: Long,
    
    /** 最后一次阅读时间 */
    val lastReadingTime: Long?,
    
    /** 阅读进度（百分比） */
    val readingProgress: Double
)


