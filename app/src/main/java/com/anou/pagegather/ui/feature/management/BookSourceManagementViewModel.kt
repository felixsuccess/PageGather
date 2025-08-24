package com.anou.pagegather.ui.feature.management

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anou.pagegather.data.local.entity.BookSourceEntity
import com.anou.pagegather.data.repository.BookSourceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * 书籍来源管理UI状态
 */
data class BookSourceManagementUiState(
    val bookSources: List<BookSourceEntity> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isEditDialogVisible: Boolean = false,
    val editingBookSource: BookSourceEntity? = null
)

/**
 * 书籍来源管理ViewModel
 */
@HiltViewModel
class BookSourceManagementViewModel @Inject constructor(
    private val bookSourceRepository: BookSourceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookSourceManagementUiState())
    val uiState: StateFlow<BookSourceManagementUiState> = _uiState

    private val _searchQuery = MutableStateFlow("")
    
    init {
        loadBookSources()
    }

    /**
     * 加载所有书籍来源
     */
    private fun loadBookSources() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                combine(
                    bookSourceRepository.getAllSources(),
                    _searchQuery
                ) { bookSources, query ->
                    if (query.isBlank()) {
                        bookSources
                    } else {
                        bookSources.filter { bookSource ->
                            bookSource.name.contains(query, ignoreCase = true)
                        }
                    }
                }.collect { filteredBookSources ->
                    _uiState.value = _uiState.value.copy(
                        bookSources = filteredBookSources,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Log.e("BookSourceManagementVM", "加载书籍来源失败: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "加载书籍来源失败: ${e.message}"
                )
            }
        }
    }

    /**
     * 搜索书籍来源
     */
    fun searchBookSources(query: String) {
        _searchQuery.value = query
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    /**
     * 显示添加书籍来源对话框
     */
    fun showAddBookSourceDialog() {
        _uiState.value = _uiState.value.copy(
            isEditDialogVisible = true,
            editingBookSource = null
        )
    }

    /**
     * 显示编辑书籍来源对话框
     */
    fun showEditBookSourceDialog(bookSource: BookSourceEntity) {
        _uiState.value = _uiState.value.copy(
            isEditDialogVisible = true,
            editingBookSource = bookSource
        )
    }

    /**
     * 隐藏编辑对话框
     */
    fun hideEditDialog() {
        _uiState.value = _uiState.value.copy(
            isEditDialogVisible = false,
            editingBookSource = null
        )
    }

    /**
     * 保存书籍来源（新增或更新）
     */
    fun saveBookSource(name: String, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val trimmedName = name.trim()
                if (trimmedName.isBlank()) {
                    withContext(Dispatchers.Main) {
                        _uiState.value = _uiState.value.copy(errorMessage = "来源名称不能为空")
                    }
                    return@launch
                }

                val editingBookSource = _uiState.value.editingBookSource
                
                if (editingBookSource == null) {
                    // 新增自定义书籍来源
                    bookSourceRepository.addCustomBookSource(trimmedName)
                } else {
                    // 更新书籍来源（仅允许更新自定义来源）
                    if (editingBookSource.isBuiltIn) {
                        withContext(Dispatchers.Main) {
                            _uiState.value = _uiState.value.copy(errorMessage = "不能修改内置来源")
                        }
                        return@launch
                    }
                    
                    val updatedBookSource = editingBookSource.copy(
                        name = trimmedName,
                        updatedDate = System.currentTimeMillis()
                    )
                    bookSourceRepository.updateBookSource(updatedBookSource)
                }

                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        isEditDialogVisible = false,
                        editingBookSource = null,
                        errorMessage = null
                    )
                    onSuccess?.invoke()
                }
            } catch (e: Exception) {
                Log.e("BookSourceManagementVM", "保存书籍来源失败: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "保存失败: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * 删除书籍来源（仅允许删除自定义来源）
     */
    fun deleteBookSource(bookSource: BookSourceEntity, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (bookSource.isBuiltIn) {
                    withContext(Dispatchers.Main) {
                        _uiState.value = _uiState.value.copy(errorMessage = "不能删除内置来源")
                    }
                    return@launch
                }
                
                bookSourceRepository.deleteCustomBookSource(bookSource.id)
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(errorMessage = null)
                    onSuccess?.invoke()
                }
            } catch (e: Exception) {
                Log.e("BookSourceManagementVM", "删除书籍来源失败: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "删除失败: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * 切换书籍来源启用状态
     */
    fun toggleBookSourceEnabled(bookSource: BookSourceEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                bookSourceRepository.toggleBookSourceEnabled(bookSource.id)
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(errorMessage = null)
                }
            } catch (e: Exception) {
                Log.e("BookSourceManagementVM", "切换启用状态失败: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "操作失败: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * 更新书籍来源排序
     */
    fun moveBookSource(fromIndex: Int, toIndex: Int) {
        val currentBookSources = _uiState.value.bookSources.toMutableList()
        if (fromIndex >= 0 && fromIndex < currentBookSources.size && 
            toIndex >= 0 && toIndex < currentBookSources.size && 
            fromIndex != toIndex) {
            
            // 更新本地状态
            val movedBookSource = currentBookSources.removeAt(fromIndex)
            val clampedToIndex = toIndex.coerceIn(0, currentBookSources.size)
            currentBookSources.add(clampedToIndex, movedBookSource)
            
            _uiState.value = _uiState.value.copy(bookSources = currentBookSources)
            
            // 更新数据库排序
            updateBookSourceOrdersInDatabase(currentBookSources)
        }
    }
    
    /**
     * 更新数据库中的书籍来源排序
     */
    private fun updateBookSourceOrdersInDatabase(bookSources: List<BookSourceEntity>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val sourceIds = bookSources.map { it.id }
                bookSourceRepository.updateSortOrders(sourceIds)
                
                Log.d("BookSourceManagementVM", "更新书籍来源排序成功")
            } catch (e: Exception) {
                Log.e("BookSourceManagementVM", "更新书籍来源排序失败: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "更新排序失败: ${e.message}"
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