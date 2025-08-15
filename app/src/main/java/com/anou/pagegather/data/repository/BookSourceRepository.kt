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
    suspend fun getSourceById(id: Long): BookSourceEntity? {
        return bookSourceDao.getSourceById(id)
    }

    /** 获取用户自定义来源 */
    fun getCustomSources(): Flow<List<BookSourceEntity>> {
        return bookSourceDao.getCustomSources()
    }

    /** 搜索来源 */
    fun searchSources(name: String): Flow<List<BookSourceEntity>> {
        return bookSourceDao.searchSourcesByName(name)
    }

    // ========== 增删改操作 ==========

    /** 添加自定义来源 */
    suspend fun addCustomSource(name: String, iconName: String? = null): Long {
        // 检查名称是否已存在
        if (bookSourceDao.isNameExists(name) > 0) {
            throw IllegalArgumentException("来源名称已存在")
        }

        val source = BookSourceEntity(
            name = name,
            iconName = iconName,
            isBuiltIn = false,
            isEnabled = true,
            sortOrder = 999 // 自定义来源排在后面
        )

        return bookSourceDao.insert(source)
    }

    /** 更新来源 */
    suspend fun updateSource(source: BookSourceEntity) {
        // 检查名称是否与其他来源冲突
        if (bookSourceDao.isNameExists(source.name, source.id) > 0) {
            throw IllegalArgumentException("来源名称已存在")
        }

        val updatedSource = source.copy(updatedDate = System.currentTimeMillis())
        bookSourceDao.update(updatedSource)
    }

    /** 删除自定义来源 */
    suspend fun deleteCustomSource(id: Long) {
        val source = getSourceById(id)
        if (source?.isBuiltIn == true) {
            throw IllegalArgumentException("不能删除内置来源")
        }
        bookSourceDao.deleteById(id)
    }

    /** 启用/禁用来源 */
    suspend fun toggleSourceEnabled(id: Long) {
        val source = getSourceById(id) ?: return
        val newEnabled = !source.isEnabled
        bookSourceDao.updateEnabled(id, if (newEnabled) 1 else 0)
    }

    /** 更新排序 */
    suspend fun updateSortOrders(sourceIds: List<Long>) {
        bookSourceDao.updateSortOrders(sourceIds)
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
            BookSourceEntity(name = "实体书", iconName = "book", isBuiltIn = true, sortOrder = 1),
            BookSourceEntity(name = "微信读书", iconName = "weread", isBuiltIn = true, sortOrder = 2),
            BookSourceEntity(name = "Kindle", iconName = "kindle", isBuiltIn = true, sortOrder = 3),
            BookSourceEntity(name = "Apple Books", iconName = "apple", isBuiltIn = true, sortOrder = 4),
            BookSourceEntity(name = "豆瓣阅读", iconName = "douban", isBuiltIn = true, sortOrder = 5),
            BookSourceEntity(name = "京东读书", iconName = "jd", isBuiltIn = true, sortOrder = 6),
            BookSourceEntity(name = "掌阅", iconName = "zhangyue", isBuiltIn = true, sortOrder = 7),
            BookSourceEntity(name = "多看阅读", iconName = "duokan", isBuiltIn = true, sortOrder = 8),
            BookSourceEntity(name = "其他", iconName = "other", isBuiltIn = true, sortOrder = 99)
        )

        bookSourceDao.insertAll(builtInSources)
    }

    /** 强制重新初始化内置来源（开发/测试用） */
    suspend fun forceReinitializeBuiltInSources() {
        // 删除所有内置来源
        // 注意：这里需要添加相应的DAO方法
        initializeBuiltInSources()
    }
}