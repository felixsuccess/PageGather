package com.anou.pagegather.data.repository


import com.anou.pagegather.data.local.database.AppDatabase
import com.anou.pagegather.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NoteRepository @Inject constructor (
    private val database: AppDatabase) {
        private val noteDao = database.noteDao()
        
    val notes: Flow<List<NoteEntity>> = noteDao.getAllNotes()

    fun getAllNotes(): Flow<List<NoteEntity>> {
        return noteDao.getAllNotes()
    }

    fun getNotesByBookId(bookId: Long): Flow<List<NoteEntity>> {
        return noteDao.getNotesByBookId(bookId)
    }

    suspend fun insertNote(note: NoteEntity) {
        noteDao.insert(note)
    }

    suspend fun updateNote(note: NoteEntity) {
        noteDao.update(note)
    }

    suspend fun deleteNote(noteId: Long) {
        noteDao.delete(noteId)
    }

    suspend fun deleteNotesByBookId(bookId: Long) {
        noteDao.delete(bookId)
    }

}