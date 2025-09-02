package com.anou.pagegather.data.repository

import androidx.room.withTransaction
import com.anou.pagegather.data.local.database.AppDatabase
import kotlinx.coroutines.flow.flowOf
import com.anou.pagegather.data.local.entity.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
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

    /**
     * 分页获取所有书籍
     * @param page 页码 (从0开始)
     * @param pageSize 每页大小
     */
    fun getBooksPaged(page: Int, pageSize: Int): Flow<List<BookEntity>> {
        return bookDao.getBooksPaged(page * pageSize, pageSize, 0, false) // sortType = 0 (updated_date), ascending = false
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

    /**
     * 模糊搜索书籍 - 支持多个关键词搜索
     */
    fun fuzzySearchBooks(query: String): Flow<List<BookEntity>> {
        // 将查询字符串按空格分割成多个关键词
        val keywords = query.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }
        
        // 如果没有关键词，返回空结果
        if (keywords.isEmpty()) {
            return flowOf(emptyList())
        }
        
        // 构建查询条件
        val conditions = keywords.joinToString(" AND ") { keyword ->
            "(name LIKE '%' || '$keyword' || '%' OR author LIKE '%' || '$keyword' || '%' OR summary LIKE '%' || '$keyword' || '%' OR isbn LIKE '%' || '$keyword' || '%')"
        }
        
        // 构建完整的SQL查询
        val sql = """
            SELECT * FROM book 
            WHERE is_deleted = 0 
            AND ($conditions)
            ORDER BY updated_date DESC
        """.trimIndent()
        
        // 使用标准的searchBooks方法进行搜索
        return bookDao.searchBooks(query)
    }

    fun getBooksByStatus(status: Int): Flow<List<BookEntity>> {
        return bookDao.getBooksByStatus(status)
    }

    /**
     * 根据排序类型获取书籍
     * @param sortType 排序类型: 0-更新日期(默认), 1-创建日期, 2-书名, 3-作者, 4-阅读进度
     * @param ascending 是否升序排列
     */
    fun getBooksSorted(sortType: Int, ascending: Boolean = false): Flow<List<BookEntity>> {
        return bookDao.getBooksSorted(sortType, ascending)
    }

    /**
     * 根据排序类型和状态获取书籍
     * @param status 阅读状态
     * @param sortType 排序类型
     * @param ascending 是否升序排列
     */
    fun getBooksByStatusSorted(status: Int, sortType: Int, ascending: Boolean = false): Flow<List<BookEntity>> {
        return bookDao.getBooksByStatusSorted(status, sortType, ascending)
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

    fun getAllSources(): Flow<List<BookSourceEntity>> {
        return bookSourceDao.getAllSources()
    }

    fun getEnabledSources(): Flow<List<BookSourceEntity>> {
        return bookSourceDao.getAllEnabledSources()
    }

    suspend fun insertSource(source: BookSourceEntity): Long {
        return bookSourceDao.insert(source)
    }

    suspend fun updateSource(source: BookSourceEntity) {
        bookSourceDao.update(source)
    }

    suspend fun deleteSource(id: Long) {
        bookSourceDao.deleteById(id)
    }

    suspend fun getSourceById(id: Long): BookSourceEntity? {
        return bookSourceDao.getSourceById(id)
    }

    fun searchSources(name: String): Flow<List<BookSourceEntity>> {
        return bookSourceDao.searchSourcesByName(name)
    }

    suspend fun isSourceNameExists(name: String, excludeId: Long = -1): Boolean {
        return bookSourceDao.isNameExists(name, excludeId) > 0
    }

    // ========== 书籍分组管理 ==========

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

    /**
     * 根据来源ID获取书籍
     */
    fun getBooksBySourceId(sourceId: Int): Flow<List<BookEntity>> {
        return bookDao.getBooksBySourceId(sourceId)
    }

    // ========== 书籍标签关联管理 ==========

    fun getTagsByBookId(bookId: Long): Flow<List<TagEntity>> {
        return tagDao.getTagsByBookId(bookId)
    }

    fun getBooksByTagId(tagId: Long): Flow<List<BookTagRefEntity>> {
        return bookTagRefDao.getBookRefsByTagId(tagId)
    }
    
    /**
     * 获取标签下的书籍
     */
    fun getBooksWithTag(tagId: Long): Flow<List<BookEntity>> {
        return combine(
            bookTagRefDao.getBookRefsByTagId(tagId),
            bookDao.getAllBooks()
        ) { refs, allBooks ->
            val bookIds = refs.map { it.bookId }.toSet()
            allBooks.filter { it.id in bookIds }
        }
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
    
    /**
     * 根据评分获取书籍
     */
    fun getBooksByRating(rating: Float): Flow<List<BookEntity>> {
        return bookDao.getBooksByRating(rating)
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