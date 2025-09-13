package com.anou.pagegather.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.anou.pagegather.data.local.entity.NoteAttachmentEntity
import com.anou.pagegather.data.local.entity.AttachmentType

/**
 * 笔记附件数据访问对象
 * 提供笔记附件的增删改查操作
 */
@Dao
interface NoteAttachmentDao {
    
    // ========== 查询操作 ==========
    
    /** 根据笔记ID获取所有附件 */
    @Query("SELECT * FROM note_attachment WHERE note_id = :noteId ORDER BY sort_order ASC")
    fun getAttachmentsByNoteId(noteId: Long): Flow<List<NoteAttachmentEntity>>
    
    /** 根据ID获取附件 */
    @Query("SELECT * FROM note_attachment WHERE id = :id")
    suspend fun getAttachmentById(id: Long): NoteAttachmentEntity?
    
    /** 根据笔记ID获取内嵌图片附件 */
    @Query("SELECT * FROM note_attachment WHERE note_id = :noteId AND is_inline_image = 1 ORDER BY sort_order ASC")
    fun getInlineImagesByNoteId(noteId: Long): Flow<List<NoteAttachmentEntity>>
    
    /** 根据笔记ID获取非内嵌附件 */
    @Query("SELECT * FROM note_attachment WHERE note_id = :noteId AND is_inline_image = 0 ORDER BY sort_order ASC")
    fun getRegularAttachmentsByNoteId(noteId: Long): Flow<List<NoteAttachmentEntity>>
    
    /** 根据文件类型获取附件 */
    @Query("SELECT * FROM note_attachment WHERE note_id = :noteId AND file_type = :fileType ORDER BY sort_order ASC")
    fun getAttachmentsByType(noteId: Long, fileType: AttachmentType): Flow<List<NoteAttachmentEntity>>
    
    /** 获取附件数量 */
    @Query("SELECT COUNT(*) FROM note_attachment WHERE note_id = :noteId")
    suspend fun getAttachmentCount(noteId: Long): Int
    
    /** 获取内嵌图片数量 */
    @Query("SELECT COUNT(*) FROM note_attachment WHERE note_id = :noteId AND is_inline_image = 1")
    suspend fun getInlineImageCount(noteId: Long): Int
    
    /** 获取所有笔记附件 */
    @Query("SELECT * FROM note_attachment")
    fun getAllNoteAttachments(): Flow<List<NoteAttachmentEntity>>
    
    // ========== 增删改操作 ==========
    
    /** 插入附件 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttachment(attachment: NoteAttachmentEntity): Long
    
    /** 插入多个附件 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttachments(attachments: List<NoteAttachmentEntity>): List<Long>
    
    /** 更新附件 */
    @Update
    suspend fun updateAttachment(attachment: NoteAttachmentEntity)
    
    /** 删除附件 */
    @Delete
    suspend fun deleteAttachment(attachment: NoteAttachmentEntity)
    
    /** 根据ID删除附件 */
    @Query("DELETE FROM note_attachment WHERE id = :id")
    suspend fun deleteAttachmentById(id: Long)
    
    /** 删除笔记的所有附件 */
    @Query("DELETE FROM note_attachment WHERE note_id = :noteId")
    suspend fun deleteAttachmentsByNoteId(noteId: Long)
    
    // ========== 排序操作 ==========
    
    /** 更新附件排序 */
    @Query("UPDATE note_attachment SET sort_order = :order WHERE id = :id")
    suspend fun updateAttachmentOrder(id: Long, order: Int)
    
    /** 获取最大排序值 */
    @Query("SELECT MAX(sort_order) FROM note_attachment WHERE note_id = :noteId")
    suspend fun getMaxOrder(noteId: Long): Int?
    
    // ========== 批量操作 ==========
    
    /** 批量更新排序 */
    @Transaction
    suspend fun updateAttachmentOrders(attachmentOrders: Map<Long, Int>) {
        attachmentOrders.forEach { (id, order) ->
            updateAttachmentOrder(id, order)
        }
    }
    
    /** 清理孤立的附件（没有对应笔记的附件） */
    @Query("""
        DELETE FROM note_attachment 
        WHERE note_id NOT IN (SELECT id FROM note)
    """)
    suspend fun cleanupOrphanedAttachments()
}