package com.anou.pagegather.ui.feature.reading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anou.pagegather.data.local.entity.BookEntity
import com.anou.pagegather.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 书籍选择器的 ViewModel
 */
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class BookSelectorViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _books = MutableStateFlow<List<BookEntity>>(emptyList())
    val books: StateFlow<List<BookEntity>> = _books.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        // 设置搜索流，带防抖动
        viewModelScope.launch {
            _searchQuery
                .debounce(300) // 300ms 防抖动
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    _isLoading.value = true
                    if (query.isBlank()) {
                        // 如果搜索为空，获取最近的书籍
                        bookRepository.getBooksPaged(0, 20)
                    } else {
                        // 使用模糊搜索
                        bookRepository.fuzzySearchBooks(query)
                    }
                }
                .catch { e ->
                    _error.value = "搜索失败: ${e.message}"
                    _books.value = emptyList()
                }
                .collect { bookList ->
                    _books.value = bookList
                    _isLoading.value = false
                }
        }
    }

    /**
     * 更新搜索查询
     */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    /**
     * 清除错误信息
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * 刷新书籍列表
     */
    fun refresh() {
        updateSearchQuery(_searchQuery.value)
    }
}