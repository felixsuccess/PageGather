package com.anou.pagegather.data.local.dao


import androidx.room.*
import com.anou.pagegather.data.local.entity.BookEntity
import com.anou.pagegather.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM note WHERE is_deleted = 0")
    fun getAllNotes(): Flow<List<NoteEntity>>


    @Query("SELECT * FROM note WHERE id = :id")
    fun getById(id: Long): NoteEntity?

    @Query("SELECT * FROM note WHERE book_id = :bookId AND is_deleted = 0")
    fun getNotesByBookId(bookId: Long): Flow<List<NoteEntity>>

    @Insert
    suspend fun insert(note: NoteEntity)

    @Update
    suspend fun update(note: NoteEntity)

    @Query("UPDATE note SET is_deleted = 1 WHERE id = :noteId")
    suspend fun delete(noteId: Long)

    @Query("UPDATE note SET is_deleted = 1 WHERE book_id = :bookId")
    suspend fun deleteByBookId(bookId: Long)
}