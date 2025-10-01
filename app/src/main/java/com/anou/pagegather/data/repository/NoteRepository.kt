package com.anou.pagegather.data.repository

import androidx.room.withTransaction
import com.anou.pagegather.data.local.database.AppDatabase
import com.anou.pagegather.data.local.entity.NoteEntity
import com.anou.pagegather.data.local.entity.NoteAttachmentEntity
import com.anou.pagegather.data.local.entity.AttachmentType

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 笔记仓库类
 * 提供笔记和附件相关的数据操作，支持 Markdown 和附件管理
 */
@Singleton
class NoteRepository @Inject constructor(
    private val database: AppDatabase
) {
    private val noteDao = database.noteDao()
    private val noteAttachmentDao = database.noteAttachmentDao()

    // ========== 事务管理 ==========
    
    suspend fun <T> runInTransaction(block: suspend () -> T): T {
        return database.withTransaction {
            block()
        }
    }

    // ========== 笔记基础操作 ==========

    /** 获取所有笔记 */
    fun getAllNotes(): Flow<List<NoteEntity>> {
        return noteDao.getAllNotes()
    }

    /** 根据ID获取笔记 */
    suspend fun getNoteById(id: Long): NoteEntity? {
        return noteDao.getById(id)
    }

    /** 根据书籍ID获取笔记 */
    fun getNotesByBookId(bookId: Long): Flow<List<NoteEntity>> {
        return noteDao.getNotesByBookId(bookId)
    }

    /** 插入笔记 */
    suspend fun insertNote(note: NoteEntity): Long {
        return noteDao.insert(note)
    }

    /** 更新笔记 */
    suspend fun updateNote(note: NoteEntity) {
        noteDao.update(note)
    }

    /** 删除笔记 */
    suspend fun deleteNote(noteId: Long) {
        noteDao.deleteNote(noteId)
    }

    /** 删除书籍的所有笔记 */
    suspend fun deleteNotesByBookId(bookId: Long) {
        noteDao.deleteNotesByBookId(bookId)
    }

    // ========== 笔记搜索和筛选 ==========

    /** 搜索笔记（标题和内容） */
    suspend fun searchNotes(query: String): Flow<List<NoteEntity>> {
        return noteDao.searchNotes("%$query%")
    }



    /** 获取包含个人想法的笔记 */
    suspend fun getNotesWithIdea(): Flow<List<NoteEntity>> {
        return noteDao.getNotesWithPersonalContent()
    }

    /** 获取包含原文摘录的笔记 */
    suspend fun getNotesWithQuote(): Flow<List<NoteEntity>> {
        return noteDao.getNotesWithOriginalText()
    }

    // ========== 附件管理 ==========

    /** 根据笔记ID获取所有附件 */
    fun getAttachmentsByNoteId(noteId: Long): Flow<List<NoteAttachmentEntity>> {
        return noteAttachmentDao.getAttachmentsByNoteId(noteId)
    }

    /** 根据ID获取附件 */
    suspend fun getAttachmentById(id: Long): NoteAttachmentEntity? {
        return noteAttachmentDao.getAttachmentById(id)
    }

    /** 获取内嵌图片附件 */
    fun getInlineImagesByNoteId(noteId: Long): Flow<List<NoteAttachmentEntity>> {
        return noteAttachmentDao.getInlineImagesByNoteId(noteId)
    }

    /** 获取非内嵌附件 */
    fun getRegularAttachmentsByNoteId(noteId: Long): Flow<List<NoteAttachmentEntity>> {
        return noteAttachmentDao.getRegularAttachmentsByNoteId(noteId)
    }

    /** 根据文件类型获取附件 */
    fun getAttachmentsByType(noteId: Long, fileType: AttachmentType): Flow<List<NoteAttachmentEntity>> {
        return noteAttachmentDao.getAttachmentsByType(noteId, fileType)
    }

    /** 插入附件 */
    suspend fun insertAttachment(attachment: NoteAttachmentEntity): Long {
        return noteAttachmentDao.insertAttachment(attachment)
    }

    /** 插入多个附件 */
    suspend fun insertAttachments(attachments: List<NoteAttachmentEntity>): List<Long> {
        return noteAttachmentDao.insertAttachments(attachments)
    }

    /** 更新附件 */
    suspend fun updateAttachment(attachment: NoteAttachmentEntity) {
        noteAttachmentDao.updateAttachment(attachment)
    }

    /** 删除附件 */
    suspend fun deleteAttachment(attachment: NoteAttachmentEntity) {
        noteAttachmentDao.deleteAttachment(attachment)
    }

    /** 根据ID删除附件 */
    suspend fun deleteAttachmentById(id: Long) {
        noteAttachmentDao.deleteAttachmentById(id)
    }

    /** 删除笔记的所有附件 */
    suspend fun deleteAttachmentsByNoteId(noteId: Long) {
        noteAttachmentDao.deleteAttachmentsByNoteId(noteId)
    }

    /**
     * 获取所有笔记附件
     */
    fun getAllNoteAttachments(): Flow<List<NoteAttachmentEntity>> {
        return noteAttachmentDao.getAllNoteAttachments()
    }

    // ========== 复合操作 ==========

    /** 创建笔记并添加附件 */
    suspend fun createNoteWithAttachments(
        note: NoteEntity,
        attachments: List<NoteAttachmentEntity>
    ): Long {
        return runInTransaction {
            val noteId = insertNote(note)
            if (attachments.isNotEmpty()) {
                val attachmentsWithNoteId = attachments.map { it.copy(noteId = noteId) }
                noteAttachmentDao.insertAttachments(attachmentsWithNoteId)
            }
            noteId
        }
    }

    /** 更新笔记并同步附件数量 */
    suspend fun updateNoteWithAttachmentCount(note: NoteEntity) {
        runInTransaction {
            val attachmentCount = noteAttachmentDao.getAttachmentCount(note.id)
            val updatedNote = note.copy(attachmentCount = attachmentCount)
            updateNote(updatedNote)
        }
    }

    /** 删除笔记及其所有附件 */
    suspend fun deleteNoteWithAttachments(noteId: Long) {
        runInTransaction {
            deleteAttachmentsByNoteId(noteId)
            deleteNote(noteId)
        }
    }

    // ========== 统计和分析 ==========

    /** 获取笔记总数 */
    suspend fun getNoteCount(): Int {
        return noteDao.getNoteCount()
    }

    /** 获取书籍的笔记数量 */
    suspend fun getNoteCountByBookId(bookId: Long): Int {
        return noteDao.getNoteCountByBookId(bookId)
    }

    /** 获取附件总数 */
    suspend fun getAttachmentCount(noteId: Long): Int {
        return noteAttachmentDao.getAttachmentCount(noteId)
    }

    /** 清理孤立的附件 */
    suspend fun cleanupOrphanedAttachments() {
        noteAttachmentDao.cleanupOrphanedAttachments()
    }
}