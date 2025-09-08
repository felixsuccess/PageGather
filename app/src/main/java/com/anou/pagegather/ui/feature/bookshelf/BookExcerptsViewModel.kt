package com.anou.pagegather.ui.feature.bookshelf

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anou.pagegather.data.local.entity.NoteEntity
import com.anou.pagegather.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 书籍书摘ViewModel
 * 负责管理特定书籍的书摘数据
 */
@HiltViewModel
class BookExcerptsViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _excerpts = MutableStateFlow<List<NoteEntity>>(emptyList())
    val excerpts: StateFlow<List<NoteEntity>> = _excerpts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * 加载指定书籍的书摘
     * 书摘在系统中以NoteEntity形式存储，通过bookId关联
     */
    fun loadBookExcerpts(bookId: Long) {
        Log.d("BookExcerptsViewModel", "Loading excerpts for bookId: $bookId")
        viewModelScope.launch {
            noteRepository.getNotesByBookId(bookId)
                .onStart {
                    Log.d("BookExcerptsViewModel", "Starting to load excerpts for bookId: $bookId")
                    _isLoading.value = true
                    _error.value = null
                }
                .catch { e ->
                    Log.e("BookExcerptsViewModel", "Error loading excerpts for bookId: $bookId", e)
                    _error.value = e.message
                    _isLoading.value = false
                }
                .onEach { notes ->
                    Log.d("BookExcerptsViewModel", "Collected ${notes.size} excerpts for bookId: $bookId")
                    // 过滤出书摘类型的笔记（假设书摘有特定的类型标识）
                    val bookExcerpts = notes.filter { note ->
                        // 这里可以根据实际需求过滤书摘
                        // 例如：note.type == NoteType.EXCERPT.code
                        true
                    }
                    _excerpts.value = bookExcerpts
                    Log.d("BookExcerptsViewModel", "Updated excerpts state with ${bookExcerpts.size} items")
                    // 在每次收集到数据后，将加载状态设置为false
                    _isLoading.value = false
                }
                .collect() // 空收集，因为我们只关心onEach中的副作用
        }
    }

    /**
     * 删除书摘
     */
    fun deleteExcerpt(note: NoteEntity) {
        viewModelScope.launch {
            try {
                noteRepository.deleteNote(note.id)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}