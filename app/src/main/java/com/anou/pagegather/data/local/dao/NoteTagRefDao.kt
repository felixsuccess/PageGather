package com.anou.pagegather.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.anou.pagegather.data.local.entity.NoteTagRefEntity

/**
 * 笔记标签关联表数据访问对象
 * 管理笔记与标签之间的多对多关系
 */
@Dao
interface NoteTagRefDao {
    
    // ========== 查询操作 ==========
    
    /** 获取笔记的所有标签关联 */
    @Query("SELECT * FROM note_tag_ref WHERE note_id = :noteId AND is_deleted = false")
    fun getTagRefsByNoteId(noteId: Long): Flow<List<NoteTagRefEntity>>
    
    /** 获取标签的所有笔记关联 */
    @Query("SELECT * FROM note_tag_ref WHERE tag_id = :tagId AND is_deleted = false")
    fun getNoteRefsByTagId(tagId: Long): Flow<List<NoteTagRefEntity>>
    
    /** 检查笔记是否有指定标签 */
    @Query("SELECT COUNT(*) FROM note_tag_ref WHERE note_id = :noteId AND tag_id = :tagId AND is_deleted = false")
    suspend fun isNoteHasTag(noteId: Long, tagId: Long): Int
    
    /** 获取标签下的笔记数量 */
    @Query("SELECT COUNT(*) FROM note_tag_ref WHERE tag_id = :tagId AND is_deleted = false")
    suspend fun getNoteCountByTag(tagId: Long): Int
    
    /** 获取笔记的标签数量 */
    @Query("SELECT COUNT(*) FROM note_tag_ref WHERE note_id = :noteId AND is_deleted = false")
    suspend fun getTagCountForNote(noteId: Long): Int
    
    // ========== 增删改操作 ==========
    
    /** 为笔记添加标签 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTagToNote(ref: NoteTagRefEntity): Long
    
    /** 批量为笔记添加标签 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTagsToNote(refs: List<NoteTagRefEntity>): List<Long>
    
    /** 从笔记移除标签（软删除） */
    @Query("UPDATE note_tag_ref SET is_deleted = true, updated_date = :updateTime WHERE note_id = :noteId AND tag_id = :tagId")
    suspend fun removeTagFromNote(noteId: Long, tagId: Long, updateTime: Long = System.currentTimeMillis())
    
    /** 移除笔记的所有标签（软删除） */
    @Query("UPDATE note_tag_ref SET is_deleted = true, updated_date = :updateTime WHERE note_id = :noteId")
    suspend fun removeAllTagsFromNote(noteId: Long, updateTime: Long = System.currentTimeMillis())
    
    /** 移除标签的所有笔记关联（软删除） */
    @Query("UPDATE note_tag_ref SET is_deleted = true, updated_date = :updateTime WHERE tag_id = :tagId")
    suspend fun removeAllNotesFromTag(tagId: Long, updateTime: Long = System.currentTimeMillis())
    
    /** 物理删除笔记标签关联 */
    @Query("DELETE FROM note_tag_ref WHERE note_id = :noteId AND tag_id = :tagId")
    suspend fun deleteTagFromNotePermanently(noteId: Long, tagId: Long)
    
    /** 物理删除笔记的所有标签关联 */
    @Query("DELETE FROM note_tag_ref WHERE note_id = :noteId")
    suspend fun deleteAllTagsFromNotePermanently(noteId: Long)
    
    // ========== 批量操作 ==========
    
    /** 更新笔记的标签关联（先软删除所有，再添加新的） */
    @Transaction
    suspend fun updateNoteTags(noteId: Long, tagIds: List<Long>) {
        val currentTime = System.currentTimeMillis()
        removeAllTagsFromNote(noteId, currentTime)
        if (tagIds.isNotEmpty()) {
            val refs = tagIds.map { tagId ->
                NoteTagRefEntity(
                    noteId = noteId,
                    tagId = tagId,
                    createdDate = currentTime,
                    updatedDate = currentTime,
                    lastSyncDate = currentTime,
                    isDeleted = false
                )
            }
            addTagsToNote(refs)
        }
    }
    
    /** 更新标签的笔记关联（先软删除所有，再添加新的） */
    @Transaction
    suspend fun updateTagNotes(tagId: Long, noteIds: List<Long>) {
        val currentTime = System.currentTimeMillis()
        removeAllNotesFromTag(tagId, currentTime)
        if (noteIds.isNotEmpty()) {
            val refs = noteIds.map { noteId ->
                NoteTagRefEntity(
                    noteId = noteId,
                    tagId = tagId,
                    createdDate = currentTime,
                    updatedDate = currentTime,
                    lastSyncDate = currentTime,
                    isDeleted = false
                )
            }
            addTagsToNote(refs)
        }
    }
    
    /** 恢复已删除的关联 */
    @Query("UPDATE note_tag_ref SET is_deleted = 0, updated_date = :updateTime WHERE note_id = :noteId AND tag_id = :tagId")
    suspend fun restoreNoteTagRef(noteId: Long, tagId: Long, updateTime: Long = System.currentTimeMillis())
    
    /** 清理已删除的关联 */
    @Query("DELETE FROM note_tag_ref WHERE is_deleted = 1 AND updated_date < :beforeTime")
    suspend fun cleanupDeletedRefs(beforeTime: Long)
}