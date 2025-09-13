package com.anou.pagegather.data.repository

import com.anou.pagegather.data.local.database.AppDatabase
import com.anou.pagegather.data.local.entity.BookTagRefEntity
import com.anou.pagegather.data.local.entity.NoteTagRefEntity
import com.anou.pagegather.data.local.entity.TagEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 标签仓库类
 * 提供标签相关的数据操作
 */
@Singleton
class TagRepository @Inject constructor(
    private val database: AppDatabase
) {
    private val tagDao = database.tagDao()
    private val bookTagRefDao = database.bookTagRefDao()
    private val noteTagRefDao = database.noteTagRefDao()

    // ========== 标签基础操作 ==========

    /**
     * 获取所有标签
     */
    fun getAllTags(): Flow<List<TagEntity>> {
        return tagDao.getAllTags()
    }

    /**
     * 获取书籍标签
     */
    fun getBookTags(): Flow<List<TagEntity>> {
        return tagDao.getBookTags()
    }

    /**
     * 获取笔记标签
     */
    fun getNoteTags(): Flow<List<TagEntity>> {
        return tagDao.getNoteTags()
    }

    /**
     * 插入标签
     */
    suspend fun insertTag(tag: TagEntity): Long {
        return tagDao.insertTag(tag)
    }

    /**
     * 更新标签
     */
    suspend fun updateTag(tag: TagEntity) {
        tagDao.updateTag(tag)
    }

    /**
     * 删除标签
     */
    suspend fun deleteTag(id: Long) {
        tagDao.deleteTag(id)
    }

    /**
     * 根据ID获取标签
     */
    suspend fun getTagById(id: Long): TagEntity? {
        return tagDao.getTagById(id)
    }

    /**
     * 搜索标签
     */
    fun searchTags(name: String): Flow<List<TagEntity>> {
        return tagDao.searchTagsByName(name)
    }

    /**
     * 检查标签名称是否存在
     */
    suspend fun isTagNameExists(name: String, type: Int, excludeId: Long = -1): Boolean {
        return tagDao.isTagNameExists(name, type, excludeId) > 0
    }

    /**
     * 获取指定类型的最大排序值
     */
    suspend fun getMaxOrderByType(type: Int): Int? {
        return tagDao.getMaxOrderByType(type)
    }

    /**
     * 获取所有笔记标签关联
     */
    fun getAllNoteTagRefs(): Flow<List<NoteTagRefEntity>> {
        return noteTagRefDao.getAllNoteTagRefs()
    }

    // ========== 标签与书籍关联操作 ==========

    /**
     * 获取书籍的标签
     */
    fun getTagsByBookId(bookId: Long): Flow<List<TagEntity>> {
        return tagDao.getTagsByBookId(bookId)
    }

    /**
     * 获取标签下的书籍
     */
    fun getBooksByTagId(tagId: Long): Flow<List<BookTagRefEntity>> {
        return bookTagRefDao.getBookRefsByTagId(tagId)
    }

    /**
     * 添加标签到书籍
     */
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

    /**
     * 从书籍中移除标签
     */
    suspend fun removeTagFromBook(bookId: Long, tagId: Long) {
        bookTagRefDao.removeTagFromBook(bookId, tagId)
    }

    /**
     * 更新书籍的标签
     */
    suspend fun updateBookTags(bookId: Long, tagIds: List<Long>) {
        bookTagRefDao.updateBookTags(bookId, tagIds)
    }

    /**
     * 检查书籍是否有标签
     */
    suspend fun isBookHasTag(bookId: Long, tagId: Long): Boolean {
        return bookTagRefDao.isBookHasTag(bookId, tagId) > 0
    }
    
    // ========== 标签与笔记关联操作 ==========

    /**
     * 获取笔记的标签
     */
    fun getTagsByNoteId(noteId: Long): Flow<List<TagEntity>> {
        return tagDao.getTagsByNoteId(noteId)
    }

    /**
     * 获取标签下的笔记
     */
    fun getNotesByTagId(tagId: Long): Flow<List<NoteTagRefEntity>> {
        return noteTagRefDao.getNoteRefsByTagId(tagId)
    }

    /**
     * 添加标签到笔记
     */
    suspend fun addTagToNote(noteId: Long, tagId: Long): Long {
        val currentTime = System.currentTimeMillis()
        return noteTagRefDao.addTagToNote(
            NoteTagRefEntity(
                noteId = noteId,
                tagId = tagId,
                createdDate = currentTime,
                updatedDate = currentTime,
                lastSyncDate = currentTime
            )
        )
    }

    /**
     * 从笔记中移除标签
     */
    suspend fun removeTagFromNote(noteId: Long, tagId: Long) {
        noteTagRefDao.removeTagFromNote(noteId, tagId)
    }

    /**
     * 更新笔记的标签
     */
    suspend fun updateNoteTags(noteId: Long, tagIds: List<Long>) {
        noteTagRefDao.updateNoteTags(noteId, tagIds)
    }

    /**
     * 检查笔记是否有标签
     */
    suspend fun isNoteHasTag(noteId: Long, tagId: Long): Boolean {
        return noteTagRefDao.isNoteHasTag(noteId, tagId) > 0
    }
}