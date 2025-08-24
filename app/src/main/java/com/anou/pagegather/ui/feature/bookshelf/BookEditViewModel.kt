package com.anou.pagegather.ui.feature.bookshelf

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anou.pagegather.data.local.entity.BookEntity
import com.anou.pagegather.data.local.entity.BookGroupEntity
import com.anou.pagegather.data.local.entity.BookSourceEntity
import com.anou.pagegather.data.local.entity.TagEntity
import com.anou.pagegather.data.repository.BookRepository
import com.anou.pagegather.data.repository.BookGroupRepository
import com.anou.pagegather.data.repository.BookSourceRepository
import com.anou.pagegather.data.repository.TagRepository
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
    val availableBookSources: List<BookSourceEntity> = emptyList(),
    val selectedBookSourceId: Long? = null,
    val availableTags: List<TagEntity> = emptyList(),
    val selectedTagIds: List<Long> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class BookEditViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val groupRepository: BookGroupRepository,
    private val bookSourceRepository: BookSourceRepository,
    private val tagRepository: TagRepository
) : ViewModel() {
    private val _book = MutableStateFlow<BookEntity?>(null)
    val book: StateFlow<BookEntity?> = _book
    
    private val _uiState = MutableStateFlow(BookEditUiState())
    val uiState: StateFlow<BookEditUiState> = _uiState
    
    init {
        loadAvailableGroups()
        loadAvailableBookSources()
        loadAvailableTags()
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
    
    /**
     * 加载可用书籍来源
     */
    private fun loadAvailableBookSources() {
        viewModelScope.launch {
            try {
                bookSourceRepository.getAllEnabledSources().collect { bookSources ->
                    _uiState.value = _uiState.value.copy(
                        availableBookSources = bookSources
                    )
                }
            } catch (e: Exception) {
                Log.e("BookEditViewModel", "加载书籍来源失败: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    errorMessage = "加载书籍来源失败: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 加载可用标签（仅书籍标签）
     */
    private fun loadAvailableTags() {
        viewModelScope.launch {
            try {
                tagRepository.getBookTags().collect { tags ->
                    _uiState.value = _uiState.value.copy(
                        availableTags = tags
                    )
                }
            } catch (e: Exception) {
                Log.e("BookEditViewModel", "加载标签失败: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    errorMessage = "加载标签失败: ${e.message}"
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
                // 加载书籍关联的标签
                val tagRefs = bookRepository.getTagsByBookId(bookId)
                
                combine(
                    groupRefs,
                    tagRefs,
                    groupRepository.getAllGroups()
                ) { refs, tags, allGroups ->
                    val selectedGroupId = refs.map { it.groupId }.firstOrNull()
                    val selectedTagIds = tags.map { it.id }
                    Triple(selectedGroupId, selectedTagIds, allGroups)
                }.collect { (selectedGroupId, selectedTagIds, groups) ->
                    withContext(Dispatchers.Main) {
                        _book.value = fetchedBook
                        _uiState.value = _uiState.value.copy(
                            book = fetchedBook,
                            availableGroups = groups,
                            selectedGroupId = selectedGroupId,
                            selectedTagIds = selectedTagIds,
                            selectedBookSourceId = fetchedBook?.bookSourceId?.toLong(),
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
     * 更新选中的书籍来源
     */
    fun updateSelectedBookSource(bookSourceId: Long?) {
        _uiState.value = _uiState.value.copy(selectedBookSourceId = bookSourceId)
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
                    
                    // 保存标签关联（多选标签）
                    val selectedTagIds = _uiState.value.selectedTagIds
                    bookRepository.updateBookTags(bookId, selectedTagIds)
                    Log.d("BookEdit", "Updated book tags: $selectedTagIds")
                    
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