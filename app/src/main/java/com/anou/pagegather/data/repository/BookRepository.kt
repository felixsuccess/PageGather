package com.anou.pagegather.data.repository

import androidx.room.withTransaction
import com.anou.pagegather.data.local.database.AppDatabase
import com.anou.pagegather.data.local.entity.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 书籍仓库类
 * 提供书籍相关的数据操作，包括分类、标签、来源管理功能
 */
@Singleton
class BookRepository @Inject constructor(
    private val database: AppDatabase
) {
    private val bookDao = database.bookDao()
    private val bookCollectionDao = database.bookCollectionDao()
    private val bookSourceDao = database.bookSourceDao()
    private val bookGroupDao = database.bookGroupDao()
    private val tagDao = database.tagDao()
    private val bookGroupRefDao = database.bookGroupRefDao()
    private val bookTagRefDao = database.bookTagRefDao()

    // ========== 事务管理 ==========
    
    suspend fun <T> runInTransaction(block: suspend () -> T): T {
        return database.withTransaction {
            block()
        }
    }

    // ========== 书籍基础操作 ==========

    fun getAllBooks(): Flow<List<BookEntity>> {
        return bookDao.getAllBooks()
    }

    suspend fun insertBook(book: BookEntity): Long {
        return bookDao.insert(book)
    }

    suspend fun updateBook(book: BookEntity) {
        bookDao.update(book)
    }

    suspend fun deleteBook(book: BookEntity) {
        bookDao.delete(book)
    }

    suspend fun getBookById(id: Long): BookEntity? {
        return bookDao.getById(id)
    }

    fun searchBooks(query: String): Flow<List<BookEntity>> {
        return bookDao.searchBooks(query)
    }

    fun getBooksByStatus(status: Int): Flow<List<BookEntity>> {
        return bookDao.getBooksByStatus(status)
    }

    // ========== 书籍收藏管理 ==========

    suspend fun insertBookCollection(collection: BookCollectionEntity): Long {
        return bookCollectionDao.insert(collection)
    }

    suspend fun updateBookCollection(collection: BookCollectionEntity) {
        bookCollectionDao.update(collection)
    }

    suspend fun getBookCollectionByBookId(bookId: Long): BookCollectionEntity? {
        return bookCollectionDao.getByBookId(bookId)
    }

    fun getBooksByOwnershipStatus(status: Int): Flow<List<BookCollectionEntity>> {
        return bookCollectionDao.getByOwnershipStatus(status)
    }

    fun getWishlist(): Flow<List<BookCollectionEntity>> {
        return bookCollectionDao.getWishlist()
    }

    suspend fun updateOwnershipStatus(bookId: Long, status: Int) {
        bookCollectionDao.updateOwnershipStatus(bookId, status)
    }

    suspend fun updateStorageLocation(bookId: Long, location: String?) {
        bookCollectionDao.updateStorageLocation(bookId, location)
    }

    // ========== 书籍来源管理 ==========

    fun getAllBookSources(): Flow<List<BookSourceEntity>> {
        return bookSourceDao.getAllSources()
    }

    fun getEnabledBookSources(): Flow<List<BookSourceEntity>> {
        return bookSourceDao.getAllEnabledSources()
    }

    suspend fun insertBookSource(source: BookSourceEntity): Long {
        return bookSourceDao.insert(source)
    }

    suspend fun updateBookSource(source: BookSourceEntity) {
        bookSourceDao.update(source)
    }

    suspend fun deleteBookSource(id: Long) {
        bookSourceDao.deleteById(id)
    }

    suspend fun getBookSourceById(id: Long): BookSourceEntity? {
        return bookSourceDao.getSourceById(id)
    }

    // ========== 分组管理 ==========

    fun getAllGroups(): Flow<List<BookGroupEntity>> {
        return bookGroupDao.getAllGroups()
    }

    suspend fun insertGroup(group: BookGroupEntity): Long {
        return bookGroupDao.insertGroup(group)
    }

    suspend fun updateGroup(group: BookGroupEntity) {
        bookGroupDao.updateGroup(group)
    }

    suspend fun deleteGroup(id: Long) {
        bookGroupDao.deleteGroup(id)
    }

    suspend fun getGroupById(id: Long): BookGroupEntity? {
        return bookGroupDao.getGroupById(id)
    }

    fun searchGroups(name: String): Flow<List<BookGroupEntity>> {
        return bookGroupDao.searchGroupsByName(name)
    }

    suspend fun isGroupNameExists(name: String, excludeId: Long = -1): Boolean {
        return bookGroupDao.isGroupNameExists(name, excludeId) > 0
    }

    // ========== 标签管理 ==========

    fun getAllTags(): Flow<List<TagEntity>> {
        return tagDao.getAllTags()
    }

    fun getBookTags(): Flow<List<TagEntity>> {
        return tagDao.getBookTags()
    }

    fun getNoteTags(): Flow<List<TagEntity>> {
        return tagDao.getNoteTags()
    }

    suspend fun insertTag(tag: TagEntity): Long {
        return tagDao.insertTag(tag)
    }

    suspend fun updateTag(tag: TagEntity) {
        tagDao.updateTag(tag)
    }

    suspend fun deleteTag(id: Long) {
        tagDao.deleteTag(id)
    }

    suspend fun getTagById(id: Long): TagEntity? {
        return tagDao.getTagById(id)
    }

    fun searchTags(name: String): Flow<List<TagEntity>> {
        return tagDao.searchTagsByName(name)
    }

    suspend fun isTagNameExists(name: String, type: Int, excludeId: Long = -1): Boolean {
        return tagDao.isTagNameExists(name, type, excludeId) > 0
    }

    // ========== 书籍分组关联管理 ==========

    fun getGroupsByBookId(bookId: Long): Flow<List<BookGroupRefEntity>> {
        return bookGroupRefDao.getGroupRefsByBookId(bookId)
    }

    fun getBooksByGroupId(groupId: Long): Flow<List<BookGroupRefEntity>> {
        return bookGroupRefDao.getBookRefsByGroupId(groupId)
    }

    suspend fun addBookToGroup(bookId: Long, groupId: Long): Long {
        return bookGroupRefDao.addBookToGroup(
            BookGroupRefEntity(bookId = bookId, groupId = groupId)
        )
    }

    suspend fun removeBookFromGroup(bookId: Long, groupId: Long) {
        bookGroupRefDao.removeBookFromGroup(bookId, groupId)
    }

    suspend fun updateBookGroups(bookId: Long, groupIds: List<Long>) {
        bookGroupRefDao.updateBookGroups(bookId, groupIds)
    }

    suspend fun isBookInGroup(bookId: Long, groupId: Long): Boolean {
        return bookGroupRefDao.isBookInGroup(bookId, groupId) > 0
    }

    // ========== 书籍标签关联管理 ==========

    fun getTagsByBookId(bookId: Long): Flow<List<TagEntity>> {
        return tagDao.getTagsByBookId(bookId)
    }

    fun getBooksByTagId(tagId: Long): Flow<List<BookTagRefEntity>> {
        return bookTagRefDao.getBookRefsByTagId(tagId)
    }

    suspend fun addTagToBook(bookId: Long, tagId: Long): Long {
        val currentTime = System.currentTimeMillis()
        return bookTagRefDao.addTagToBook(
            BookTagRefEntity(
                bookId = bookId,
                tagId = tagId,
                createdDate = currentTime,
                updatedDate = currentTime,
                lastSyncDate = currentTime
            )
        )
    }

    suspend fun removeTagFromBook(bookId: Long, tagId: Long) {
        bookTagRefDao.removeTagFromBook(bookId, tagId)
    }

    suspend fun updateBookTags(bookId: Long, tagIds: List<Long>) {
        bookTagRefDao.updateBookTags(bookId, tagIds)
    }

    suspend fun isBookHasTag(bookId: Long, tagId: Long): Boolean {
        return bookTagRefDao.isBookHasTag(bookId, tagId) > 0
    }

    // ========== 复合操作 ==========

    /**
     * 创建完整的书籍记录（包含收藏信息、分组、标签）
     */
    suspend fun createCompleteBook(
        book: BookEntity,
        collection: BookCollectionEntity? = null,
        groupIds: List<Long> = emptyList(),
        tagIds: List<Long> = emptyList()
    ): Long = runInTransaction {
        // 插入书籍
        val bookId = insertBook(book)
        
        // 插入收藏信息
        collection?.let {
            insertBookCollection(it.copy(bookId = bookId))
        }
        
        // 添加分组关联
        if (groupIds.isNotEmpty()) {
            updateBookGroups(bookId, groupIds)
        }
        
        // 添加标签关联
        if (tagIds.isNotEmpty()) {
            updateBookTags(bookId, tagIds)
        }
        
        bookId
    }

    /**
     * 更新完整的书籍记录
     */
    suspend fun updateCompleteBook(
        book: BookEntity,
        collection: BookCollectionEntity? = null,
        groupIds: List<Long> = emptyList(),
        tagIds: List<Long> = emptyList()
    ) = runInTransaction {
        // 更新书籍
        updateBook(book)
        
        // 更新收藏信息
        collection?.let {
            val existing = getBookCollectionByBookId(book.id)
            if (existing != null) {
                updateBookCollection(it.copy(id = existing.id, bookId = book.id))
            } else {
                insertBookCollection(it.copy(bookId = book.id))
            }
        }
        
        // 更新分组关联
        updateBookGroups(book.id, groupIds)
        
        // 更新标签关联
        updateBookTags(book.id, tagIds)
    }

    /**
     * 删除完整的书籍记录（包含所有关联数据）
     */
    suspend fun deleteCompleteBook(bookId: Long) = runInTransaction {
        // 删除书籍（外键约束会自动删除相关的收藏信息和关联表数据）
        val book = getBookById(bookId)
        book?.let { deleteBook(it) }
    }
}