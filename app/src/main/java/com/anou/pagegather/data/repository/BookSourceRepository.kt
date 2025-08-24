package com.anou.pagegather.data.repository

import com.anou.pagegather.data.local.database.AppDatabase
import com.anou.pagegather.data.local.entity.BookSourceEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 书籍来源仓储类
 * 负责管理书籍来源的数据操作和业务逻辑
 */
@Singleton
class BookSourceRepository @Inject constructor(
    private val database: AppDatabase
) {
    private val bookSourceDao = database.bookSourceDao()

    // ========== 查询操作 ==========

    /** 获取所有启用的来源 */
    fun getAllEnabledSources(): Flow<List<BookSourceEntity>> {
        return bookSourceDao.getAllEnabledSources()
    }

    /** 获取所有来源（管理界面用） */
    fun getAllSources(): Flow<List<BookSourceEntity>> {
        return bookSourceDao.getAllSources()
    }

    /** 根据ID获取来源 */
    suspend fun getBookSourceById(id: Long): BookSourceEntity? {
    return bookSourceDao.getSourceById(id)
}

    // ========== 增删改操作 ==========

    /** 添加自定义来源 */
    suspend fun addCustomBookSource(name: String): Long {
    // 检查名称是否已存在
    if (bookSourceDao.isNameExists(name) > 0) {
        throw IllegalArgumentException("来源名称已存在")
    }

    // 获取最大排序值+1，确保新来源显示在最前面
    val maxSortOrder = getMaxSortOrder() ?: 0
    val bookSource = BookSourceEntity(
        name = name,
        isBuiltIn = false,
        isEnabled = true,
        sortOrder = maxSortOrder + 1
    )

    return bookSourceDao.insert(bookSource)
}

    /** 更新来源 */
    suspend fun updateBookSource(source: BookSourceEntity) {
    // 检查名称是否已存在（排除当前ID）
    if (bookSourceDao.isNameExists(source.name, source.id) > 0) {
        throw IllegalArgumentException("来源名称已存在")
    }

    val updatedSource = source.copy(updatedDate = System.currentTimeMillis())
    bookSourceDao.update(updatedSource)
}

    /** 删除自定义来源 */
    suspend fun deleteCustomBookSource(id: Long) {
    val bookSource = getBookSourceById(id)
    if (bookSource?.isBuiltIn == true) {
        throw IllegalArgumentException("不能删除内置来源")
    }
    bookSourceDao.deleteById(id)
}

    /** 启用/禁用来源 */
    suspend fun toggleBookSourceEnabled(id: Long) {
    val bookSource = getBookSourceById(id) ?: return
    val newEnabled = !bookSource.isEnabled
    bookSourceDao.updateEnabled(id, if (newEnabled) 1 else 0)
}

    /** 更新排序 */
    suspend fun updateSortOrders(sourceIds: List<Long>) {
        bookSourceDao.updateSortOrders(sourceIds)
    }

    /** 获取最大排序值 */
    suspend fun getMaxSortOrder(): Int? {
        return bookSourceDao.getMaxSortOrder()
    }

    // ========== 初始化操作 ==========

    /** 检查并初始化预定义来源 */
    suspend fun initializeBuiltInSourcesIfNeeded() {
        // 检查是否已经初始化过内置来源
        val builtInSources = bookSourceDao.getBuiltInSourcesSync()
        if (builtInSources.isEmpty()) {
            initializeBuiltInSources()
        }
    }

    /** 初始化预定义来源 */
    private suspend fun initializeBuiltInSources() {
        val builtInSources = listOf(
            BookSourceEntity(name = "未知", iconName = "default", isBuiltIn = true, sortOrder = 1),
            BookSourceEntity(name = "实体书", iconName = "book", isBuiltIn = true, sortOrder = 11),
            BookSourceEntity(name = "微信读书", iconName = "weread", isBuiltIn = true, sortOrder = 12),
            BookSourceEntity(name = "Kindle", iconName = "kindle", isBuiltIn = true, sortOrder = 13),
            BookSourceEntity(name = "Apple Books", iconName = "apple", isBuiltIn = true, sortOrder = 14),
            BookSourceEntity(name = "网易蜗牛", iconName = "NETEASESNAIL", isBuiltIn = true, sortOrder =15),
            BookSourceEntity(name = "京东读书", iconName = "jd", isBuiltIn = true, sortOrder = 16),
            BookSourceEntity(name = "掌阅", iconName = "zhangyue", isBuiltIn = true, sortOrder = 17),
            BookSourceEntity(name = "多看阅读", iconName = "duokan", isBuiltIn = true, sortOrder = 18),
            BookSourceEntity(name = "豆瓣阅读", iconName = "douban", isBuiltIn = true, sortOrder = 19),
            BookSourceEntity(name = "其他", iconName = "other", isBuiltIn = true, sortOrder = 99)
        )

        bookSourceDao.insertAll(builtInSources)
    }
}