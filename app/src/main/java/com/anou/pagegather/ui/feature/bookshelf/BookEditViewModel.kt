package com.anou.pagegather.ui.feature.bookshelf

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anou.pagegather.data.local.entity.BookEntity
import com.anou.pagegather.data.local.entity.BookGroupEntity
import com.anou.pagegather.data.repository.BookRepository
import com.anou.pagegather.data.repository.BookGroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * 书籍编辑界面状态
 */
data class BookEditUiState(
    val book: BookEntity? = null,
    val availableGroups: List<BookGroupEntity> = emptyList(),
    val selectedGroupId: Long? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class BookEditViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val groupRepository: BookGroupRepository
) : ViewModel() {
    private val _book = MutableStateFlow<BookEntity?>(null)
    val book: StateFlow<BookEntity?> = _book
    
    private val _uiState = MutableStateFlow(BookEditUiState())
    val uiState: StateFlow<BookEditUiState> = _uiState
    
    init {
        loadAvailableGroups()
    }

    /**
     * 加载可用分组
     */
    private fun loadAvailableGroups() {
        viewModelScope.launch {
            try {
                groupRepository.getAllGroups().collect { groups ->
                    _uiState.value = _uiState.value.copy(
                        availableGroups = groups
                    )
                }
            } catch (e: Exception) {
                Log.e("BookEditViewModel", "加载分组失败: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    errorMessage = "加载分组失败: ${e.message}"
                )
            }
        }
    }
    
    fun loadBook(bookId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                val fetchedBook = bookRepository.getBookById(bookId)
                
                // 加载书籍关联的分组
                val groupRefs = bookRepository.getGroupsByBookId(bookId)
                
                combine(
                    groupRefs,
                    groupRepository.getAllGroups()
                ) { refs, allGroups ->
                    val selectedId = refs.map { it.groupId }.firstOrNull()
                    Triple(fetchedBook, allGroups, selectedId)
                }.collect { (book, groups, selectedId) ->
                    withContext(Dispatchers.Main) {
                        _book.value = book
                        _uiState.value = _uiState.value.copy(
                            book = book,
                            availableGroups = groups,
                            selectedGroupId = selectedId,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("BookEditViewModel", "加载书籍失败: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "加载书籍失败: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * 更新选中的分组（单选）
     */
    fun updateSelectedGroup(groupId: Long?) {
        _uiState.value = _uiState.value.copy(selectedGroupId = groupId)
    }
    
    /**
     * 保存书籍及其分组关联
     */
    fun saveBook(book: BookEntity, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            bookRepository.runInTransaction {
                try {
                    val bookId = if (book.id == 0L) {
                        val insertedId = bookRepository.insertBook(book)
                        Log.d("BookEdit", "Inserted book with id: $insertedId")
                        insertedId
                    } else {
                        bookRepository.updateBook(book)
                        Log.d("BookEdit", "Updated book with id: ${book.id}")
                        book.id
                    }
                    
                    // 保存分组关联（使用单选分组）
                    val selectedGroupId = _uiState.value.selectedGroupId
                    val groupIds = selectedGroupId?.let { listOf(it) } ?: emptyList()
                    bookRepository.updateBookGroups(bookId, groupIds)
                    Log.d("BookEdit", "Updated book group: $selectedGroupId")
                    
                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                } catch (e: Exception) {
                    Log.e("BookEdit", "Save failed: ${e.stackTraceToString()}")
                    withContext(Dispatchers.Main) {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = "保存失败: ${e.message}"
                        )
                    }
                }
            }
        }
    }

    fun deleteBook(book: BookEntity) = viewModelScope.launch {
        bookRepository.deleteBook(book)
    }
    
    /**
     * 添加新分组
     */
    fun addNewGroup(name: String, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val trimmedName = name.trim()
                if (trimmedName.isBlank()) {
                    withContext(Dispatchers.Main) {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = "分组名称不能为空"
                        )
                    }
                    return@launch
                }
                
                // 检查名称是否已存在
                val nameExists = groupRepository.isGroupNameExists(trimmedName)
                if (nameExists) {
                    withContext(Dispatchers.Main) {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = "分组名称已存在"
                        )
                    }
                    return@launch
                }
                
                val currentTime = System.currentTimeMillis()
                val newGroup = BookGroupEntity(
                    name = trimmedName,
                    groupOrder = 0,
                    createdDate = currentTime,
                    updatedDate = currentTime,
                    lastSyncDate = currentTime
                )
                
                val groupId = groupRepository.insertGroup(newGroup)
                Log.d("BookEdit", "Created new group with id: $groupId")
                
                // 自动选中新创建的分组
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        selectedGroupId = groupId,
                        errorMessage = null
                    )
                    onSuccess?.invoke()
                }
            } catch (e: Exception) {
                Log.e("BookEditViewModel", "创建分组失败: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "创建分组失败: ${e.message}"
                    )
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