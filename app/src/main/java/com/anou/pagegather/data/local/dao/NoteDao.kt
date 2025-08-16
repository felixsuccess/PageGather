package com.anou.pagegather.data.local.dao

import androidx.room.*
import com.anou.pagegather.data.local.entity.NoteEntity

import kotlinx.coroutines.flow.Flow

/**
 * 笔记数据访问对象
 * 提供笔记的增删改查操作，支持新的字段结构
 */
@Dao
interface NoteDao {
    
    // ========== 基础查询操作 ==========
    
    /** 获取所有未删除的笔记 */
    @Query("SELECT * FROM note WHERE is_deleted = 0 ORDER BY created_date DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    /** 根据ID获取笔记 */
    @Query("SELECT * FROM note WHERE id = :id AND is_deleted = 0")
    suspend fun getById(id: Long): NoteEntity?

    /** 根据书籍ID获取笔记 */
    @Query("SELECT * FROM note WHERE book_id = :bookId AND is_deleted = 0 ORDER BY created_date DESC")
    fun getNotesByBookId(bookId: Long): Flow<List<NoteEntity>>

    // ========== 搜索和筛选 ==========

    /** 搜索笔记（标题、个人想法、原文摘录） */
    @Query("""
        SELECT * FROM note 
        WHERE is_deleted = 0 
        AND (title LIKE :query 
             OR idea LIKE :query 
             OR quote LIKE :query)
        ORDER BY created_date DESC
    """)
    fun searchNotes(query: String): Flow<List<NoteEntity>>



    /** 获取包含个人想法的笔记 */
    @Query("SELECT * FROM note WHERE idea IS NOT NULL AND idea != '' AND is_deleted = 0 ORDER BY created_date DESC")
    fun getNotesWithPersonalContent(): Flow<List<NoteEntity>>

    /** 获取包含原文摘录的笔记 */
    @Query("SELECT * FROM note WHERE quote IS NOT NULL AND quote != '' AND is_deleted = 0 ORDER BY created_date DESC")
    fun getNotesWithOriginalText(): Flow<List<NoteEntity>>

    /** 获取混合类型笔记（既有个人想法又有原文摘录） */
    @Query("""
        SELECT * FROM note 
        WHERE idea IS NOT NULL AND idea != '' 
        AND quote IS NOT NULL AND quote != ''
        AND is_deleted = 0 
        ORDER BY created_date DESC
    """)
    fun getMixedNotes(): Flow<List<NoteEntity>>

    // ========== 统计查询 ==========

    /** 获取笔记总数 */
    @Query("SELECT COUNT(*) FROM note WHERE is_deleted = 0")
    suspend fun getNoteCount(): Int

    /** 获取书籍的笔记数量 */
    @Query("SELECT COUNT(*) FROM note WHERE book_id = :bookId AND is_deleted = 0")
    suspend fun getNoteCountByBookId(bookId: Long): Int

    /** 获取包含附件的笔记数量 */
    @Query("SELECT COUNT(*) FROM note WHERE attachment_count > 0 AND is_deleted = 0")
    suspend fun getNotesWithAttachmentsCount(): Int

    // ========== 增删改操作 ==========

    /** 插入笔记 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: NoteEntity): Long

    /** 插入多个笔记 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: List<NoteEntity>): List<Long>

    /** 更新笔记 */
    @Update
    suspend fun update(note: NoteEntity)

    /** 软删除笔记 */
    @Query("UPDATE note SET is_deleted = 1, deleted_date = :deleteTime WHERE id = :noteId")
    suspend fun deleteNote(noteId: Long, deleteTime: Long = System.currentTimeMillis())

    /** 软删除书籍的所有笔记 */
    @Query("UPDATE note SET is_deleted = 1, deleted_date = :deleteTime WHERE book_id = :bookId")
    suspend fun deleteNotesByBookId(bookId: Long, deleteTime: Long = System.currentTimeMillis())

    /** 物理删除笔记 */
    @Query("DELETE FROM note WHERE id = :noteId")
    suspend fun deleteNotePermanently(noteId: Long)

    /** 恢复已删除的笔记 */
    @Query("UPDATE note SET is_deleted = 0, deleted_date = NULL WHERE id = :noteId")
    suspend fun restoreNote(noteId: Long)

    // ========== 批量操作 ==========

    /** 更新笔记的附件数量 */
    @Query("UPDATE note SET attachment_count = :count WHERE id = :noteId")
    suspend fun updateAttachmentCount(noteId: Long, count: Int)

    /** 清理已删除的笔记 */
    @Query("DELETE FROM note WHERE is_deleted = 1 AND deleted_date < :beforeTime")
    suspend fun cleanupDeletedNotes(beforeTime: Long)

    /** 获取已删除的笔记 */
    @Query("SELECT * FROM note WHERE is_deleted = 1 ORDER BY deleted_date DESC")
    fun getDeletedNotes(): Flow<List<NoteEntity>>
}