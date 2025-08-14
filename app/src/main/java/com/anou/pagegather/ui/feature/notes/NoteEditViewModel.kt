package com.anou.pagegather.ui.feature.notes

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anou.pagegather.data.local.entity.NoteEntity
import com.anou.pagegather.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NoteEditViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
) : ViewModel() {
    // 笔记状态流
    private val _note = MutableStateFlow<NoteEntity?>(null)
    val note: StateFlow<NoteEntity?> = _note.asStateFlow()


    // 加载笔记
    fun loadNote(noteId: Long) {
        viewModelScope.launch {
            try {
                val note = noteRepository.getNoteById(noteId)
                _note.value = note
            } catch (e: Exception) {
                Log.e("loadLoad", "Save failed: ${e.stackTraceToString()}")
            }

        }
    }

    // 保存笔记
    fun saveNote(note: NoteEntity, onSuccess: () -> Unit) {
        viewModelScope.launch {

            try {

                if (note.id == 0L) {
                    val insertedId = noteRepository.insertNote(note)
                    Log.d("saveNote", "Inserted note with id: $insertedId")
                } else {
                    noteRepository.updateNote(note)
                    Log.d("saveNote", "Updated note with id: ${note.id}")
                }
                withContext(Dispatchers.Main) {
                    onSuccess()
                }
            } catch (e: Exception) {
                Log.e("saveNote", "Save failed: ${e.stackTraceToString()}")
            }

        }
    }
}