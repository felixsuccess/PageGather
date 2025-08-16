package com.anou.pagegather.ui.feature.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anou.pagegather.data.local.entity.NoteEntity
import com.anou.pagegather.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteListViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : ViewModel() {

    val notes: StateFlow<List<NoteEntity>> = noteRepository.getAllNotes()
        .map { notes ->
            notes.filter { !it.isDeleted }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun getNotesByBookId(bookId: Long): StateFlow<List<NoteEntity>> {
        return noteRepository.getNotesByBookId(bookId)
            .map { notes ->
                notes.filter { !it.isDeleted }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

      fun insertNote(note: NoteEntity) {
          viewModelScope.launch(Dispatchers.IO) {
              noteRepository.insertNote(note)
          }
    }

    suspend fun updateNote(note: NoteEntity) {
        noteRepository.updateNote(note)
    }

    suspend fun deleteNote(noteId: Long) {
        noteRepository.deleteNote(noteId)
    }

    suspend fun deleteNotesByBookId(bookId: Long) {
        noteRepository.deleteNotesByBookId(bookId)
    }
}