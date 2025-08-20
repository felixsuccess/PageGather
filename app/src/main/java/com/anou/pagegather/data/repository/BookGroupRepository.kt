package com.anou.pagegather.data.repository

import com.anou.pagegather.data.local.database.AppDatabase
import com.anou.pagegather.data.local.entity.BookGroupEntity
import com.anou.pagegather.data.local.entity.BookGroupRefEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 书籍分组仓库类
 * 提供书籍分组相关的数据操作
 */
@Singleton
class BookGroupRepository @Inject constructor(
    private val database: AppDatabase
) {
    private val bookGroupDao = database.bookGroupDao()
    private val bookGroupRefDao = database.bookGroupRefDao()
    private val bookDao = database.bookDao()

    // ========== 分组基础操作 ==========

    /**
     * 获取所有分组
     */
    fun getAllGroups(): Flow<List<BookGroupEntity>> {
        return bookGroupDao.getAllGroups()
    }

    /**
     * 插入分组
     */
    suspend fun insertGroup(group: BookGroupEntity): Long {
        return bookGroupDao.insertGroup(group)
    }

    /**
     * 更新分组
     */
    suspend fun updateGroup(group: BookGroupEntity) {
        bookGroupDao.updateGroup(group)
    }

    /**
     * 删除分组
     */
    suspend fun deleteGroup(id: Long) {
        bookGroupDao.deleteGroup(id)
    }

    /**
     * 根据ID获取分组
     */
    suspend fun getGroupById(id: Long): BookGroupEntity? {
        return bookGroupDao.getGroupById(id)
    }

    /**
     * 搜索分组
     */
    fun searchGroups(name: String): Flow<List<BookGroupEntity>> {
        return bookGroupDao.searchGroupsByName(name)
    }

    /**
     * 检查分组名称是否存在
     */
    suspend fun isGroupNameExists(name: String, excludeId: Long = -1): Boolean {
        return bookGroupDao.isGroupNameExists(name, excludeId) > 0
    }

    // ========== 分组与书籍关联操作 ==========

    /**
     * 获取书籍所属的分组
     */
    fun getGroupsByBookId(bookId: Long): Flow<List<BookGroupRefEntity>> {
        return bookGroupRefDao.getGroupRefsByBookId(bookId)
    }

    /**
     * 获取分组中的书籍
     */
    fun getBooksByGroupId(groupId: Long): Flow<List<BookGroupRefEntity>> {
        return bookGroupRefDao.getBookRefsByGroupId(groupId)
    }

    /**
     * 添加书籍到分组
     */
    suspend fun addBookToGroup(bookId: Long, groupId: Long): Long {
        return bookGroupRefDao.addBookToGroup(
            BookGroupRefEntity(bookId = bookId, groupId = groupId)
        )
    }

    /**
     * 从分组中移除书籍
     */
    suspend fun removeBookFromGroup(bookId: Long, groupId: Long) {
        bookGroupRefDao.removeBookFromGroup(bookId, groupId)
    }

    /**
     * 更新书籍的分组
     */
    suspend fun updateBookGroups(bookId: Long, groupIds: List<Long>) {
        bookGroupRefDao.updateBookGroups(bookId, groupIds)
    }

    /**
     * 检查书籍是否在分组中
     */
    suspend fun isBookInGroup(bookId: Long, groupId: Long): Boolean {
        return bookGroupRefDao.isBookInGroup(bookId, groupId) > 0
    }
}