package com.anou.pagegather.ui.feature.notes

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anou.pagegather.data.local.entity.NoteEntity
import com.anou.pagegather.data.local.entity.TagEntity
import com.anou.pagegather.data.repository.NoteRepository
import com.anou.pagegather.data.repository.TagRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * 笔记编辑界面状态
 */
data class NoteEditUiState(
    val note: NoteEntity? = null,
    val availableTags: List<TagEntity> = emptyList(),
    val selectedTagIds: List<Long> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class NoteEditViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    private val tagRepository: TagRepository
) : ViewModel() {
    // 笔记状态流
    private val _note = MutableStateFlow<NoteEntity?>(null)
    val note: StateFlow<NoteEntity?> = _note.asStateFlow()
    
    private val _uiState = MutableStateFlow(NoteEditUiState())
    val uiState: StateFlow<NoteEditUiState> = _uiState
    
    init {
        loadAvailableTags()
    }

    /**
     * 加载可用标签（仅笔记标签）
     */
    private fun loadAvailableTags() {
        viewModelScope.launch {
            try {
                tagRepository.getNoteTags().collect { tags ->
                    _uiState.value = _uiState.value.copy(
                        availableTags = tags
                    )
                }
            } catch (e: Exception) {
                Log.e("NoteEditViewModel", "加载标签失败: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    errorMessage = "加载标签失败: ${e.message}"
                )
            }
        }
    }


    // 加载笔记
    fun loadNote(noteId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                val fetchedNote = noteRepository.getNoteById(noteId)
                
                // 加载笔记关联的标签
                val tagRefs = tagRepository.getTagsByNoteId(noteId)
                
                combine(
                    tagRefs,
                    tagRepository.getNoteTags()
                ) { tags, allTags ->
                    val selectedTagIds = tags.map { it.id }
                    Pair(selectedTagIds, allTags)
                }.collect { (selectedTagIds, allTags) ->
                    withContext(Dispatchers.Main) {
                        _note.value = fetchedNote
                        _uiState.value = _uiState.value.copy(
                            note = fetchedNote,
                            availableTags = allTags,
                            selectedTagIds = selectedTagIds,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("NoteEditViewModel", "加载笔记失败: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "加载笔记失败: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * 更新选中的标签（多选）
     */
    fun updateSelectedTags(tagIds: List<Long>) {
        _uiState.value = _uiState.value.copy(selectedTagIds = tagIds)
    }
    
    /**
     * 切换标签选中状态
     */
    fun toggleTagSelection(tagId: Long) {
        val currentSelectedIds = _uiState.value.selectedTagIds.toMutableList()
        if (currentSelectedIds.contains(tagId)) {
            currentSelectedIds.remove(tagId)
        } else {
            currentSelectedIds.add(tagId)
        }
        _uiState.value = _uiState.value.copy(selectedTagIds = currentSelectedIds)
    }

    // 保存笔记
    fun saveNote(note: NoteEntity, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.runInTransaction {
                try {
                    val noteId = if (note.id == 0L) {
                        val insertedId = noteRepository.insertNote(note)
                        Log.d("NoteEdit", "Inserted note with id: $insertedId")
                        insertedId
                    } else {
                        noteRepository.updateNote(note)
                        Log.d("NoteEdit", "Updated note with id: ${note.id}")
                        note.id
                    }
                    
                    // 保存标签关联（多选标签）
                    val selectedTagIds = _uiState.value.selectedTagIds
                    tagRepository.updateNoteTags(noteId, selectedTagIds)
                    Log.d("NoteEdit", "Updated note tags: $selectedTagIds")
                    
                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                } catch (e: Exception) {
                    Log.e("NoteEdit", "Save failed: ${e.stackTraceToString()}")
                    withContext(Dispatchers.Main) {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = "保存失败: ${e.message}"
                        )
                    }
                }
            }
        }
    }
    
    /**
     * 清除错误消息
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}