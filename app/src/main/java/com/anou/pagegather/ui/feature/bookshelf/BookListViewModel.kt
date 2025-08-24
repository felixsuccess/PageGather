package com.anou.pagegather.ui.feature.bookshelf

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
 * 书籍列表状态
 */
data class BookListState(
    val books: List<BookEntity> = emptyList(),
    val availableGroups: List<BookGroupEntity> = emptyList(),
    val selectedGroupId: Long? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMoreData: Boolean = true,
    val errorMessage: String? = null
)

@HiltViewModel
class BookListViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val groupRepository: BookGroupRepository
) : ViewModel() {
    private var _bookList = MutableStateFlow<List<BookEntity>>(emptyList())
    private val _isLoading = MutableStateFlow(true)
    private val _state = MutableStateFlow<BookListUIState>(BookListUIState.Loading)
    val state: StateFlow<BookListUIState> = _state
    
    private val _bookListState = MutableStateFlow(BookListState())
    val bookListState: StateFlow<BookListState> = _bookListState
    
    // 分页相关状态
    private var currentPage = 0
    private val pageSize = 20
    private var isLoadingMore = false
    private var hasMoreData = true
    
    init {
        viewModelScope.launch {
            // 加载分组列表
            loadAvailableGroups()
            // 加载书籍列表
            loadBooks()
        }
    }

    /**
     * 加载可用分组
     */
    private fun loadAvailableGroups() {
        viewModelScope.launch {
            try {
                groupRepository.getAllGroups().collect { groups ->
                    _bookListState.value = _bookListState.value.copy(
                        availableGroups = groups
                    )
                }
            } catch (e: Exception) {
                _bookListState.value = _bookListState.value.copy(
                    errorMessage = "加载分组失败: ${e.message}"
                )
            }
        }
    }
    
    private suspend fun loadBooks() {
        _state.value = BookListUIState.Loading
        _isLoading.value = true
        _bookListState.value = _bookListState.value.copy(isLoading = true)
        currentPage = 0
        hasMoreData = true
        
        try {
            val currentState = _bookListState.value
            val selectedGroupId = currentState.selectedGroupId
            val searchQuery = currentState.searchQuery
            
            // 根据是否有分组筛选来选择不同的加载方式
            val flow = when {
                selectedGroupId != null -> {
                    // 按分组筛选
                    combine(
                        groupRepository.getBooksByGroupId(selectedGroupId),
                        bookRepository.getAllBooks()
                    ) { groupRefs, allBooks ->
                        val bookIds = groupRefs.map { it.bookId }.toSet()
                        allBooks.filter { it.id in bookIds }
                    }
                }
                searchQuery.isNotBlank() -> {
                    // 搜索模式
                    bookRepository.searchBooks(searchQuery)
                }
                else -> {
                    // 正常分页加载
                    bookRepository.getBooksPaged(currentPage, pageSize)
                }
            }
            
            flow.collect { books ->
                _bookList.value = books
                hasMoreData = books.size >= pageSize && selectedGroupId == null && searchQuery.isBlank()
                
                _bookListState.value = _bookListState.value.copy(
                    books = books,
                    isLoading = false,
                    hasMoreData = hasMoreData
                )
                
                _state.value = when {
                    books.isEmpty() -> BookListUIState.Empty
                    else -> BookListUIState.Success(books, isLoadingMore = false)
                }
            }
        } catch (e: Exception) {
            _state.value = BookListUIState.Error("加载失败: ${e.localizedMessage}")
            _bookList.value = emptyList()
            _bookListState.value = _bookListState.value.copy(
                isLoading = false,
                errorMessage = "加载失败: ${e.message}"
            )
        } finally {
            _isLoading.value = false
        }
    }
    
    fun loadMoreBooks() {
        // 只有在正常模式下（无分组筛选和搜索）才支持分页加载
        val currentState = _bookListState.value
        if (isLoadingMore || !hasMoreData || currentState.selectedGroupId != null || currentState.searchQuery.isNotBlank()) {
            return
        }
        
        viewModelScope.launch {
            isLoadingMore = true
            val state = _state.value
            if (state is BookListUIState.Success) {
                _state.value = state.copy(isLoadingMore = true)
            }
            _bookListState.value = _bookListState.value.copy(isLoadingMore = true)
            
            try {
                currentPage++
                bookRepository.getBooksPaged(currentPage, pageSize).collect { newBooks ->
                    if (newBooks.isNotEmpty()) {
                        val allBooks = _bookList.value + newBooks
                        _bookList.value = allBooks
                        hasMoreData = newBooks.size >= pageSize
                        
                        _bookListState.value = _bookListState.value.copy(
                            books = allBooks,
                            isLoadingMore = false,
                            hasMoreData = hasMoreData
                        )
                        _state.value = BookListUIState.Success(allBooks, isLoadingMore = false)
                    } else {
                        hasMoreData = false
                        _bookListState.value = _bookListState.value.copy(
                            isLoadingMore = false,
                            hasMoreData = false
                        )
                        if (state is BookListUIState.Success) {
                            _state.value = state.copy(isLoadingMore = false)
                        }
                    }
                }
            } catch (e: Exception) {
                // 加载更多失败时，恢复到之前的状态
                currentPage--
                _bookListState.value = _bookListState.value.copy(
                    isLoadingMore = false,
                    errorMessage = "加载更多失败: ${e.message}"
                )
                if (state is BookListUIState.Success) {
                    _state.value = state.copy(isLoadingMore = false)
                }
            } finally {
                isLoadingMore = false
            }
        }
    }

    fun searchBooks(query: String) {
        _bookListState.value = _bookListState.value.copy(searchQuery = query)
        viewModelScope.launch {
            loadBooks()
        }
    }

    fun clearSearch() {
        _bookListState.value = _bookListState.value.copy(searchQuery = "")
        viewModelScope.launch {
            loadBooks()
        }
    }
    
    /**
     * 选择分组筛选
     */
    fun selectGroup(groupId: Long?) {
        _bookListState.value = _bookListState.value.copy(selectedGroupId = groupId)
        viewModelScope.launch {
            loadBooks()
        }
    }
    
    /**
     * 清除分组筛选
     */
    fun clearGroupFilter() {
        selectGroup(null)
    }
    
    /**
     * 清除错误消息
     */
    fun clearError() {
        _bookListState.value = _bookListState.value.copy(errorMessage = null)
    }
    
    /**
     * 删除书籍
     */
    fun deleteBook(book: BookEntity, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 使用完整删除方法，包含所有关联数据
                bookRepository.deleteCompleteBook(book.id)
                
                withContext(Dispatchers.Main) {
                    // 从当前列表中移除该书籍
                    val currentBooks = _bookList.value.toMutableList()
                    currentBooks.removeAll { it.id == book.id }
                    _bookList.value = currentBooks
                    
                    // 更新UI状态
                    _bookListState.value = _bookListState.value.copy(
                        books = currentBooks
                    )
                    
                    _state.value = when {
                        currentBooks.isEmpty() -> BookListUIState.Empty
                        else -> BookListUIState.Success(currentBooks, isLoadingMore = false)
                    }
                    
                    onSuccess?.invoke()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _bookListState.value = _bookListState.value.copy(
                        errorMessage = "删除失败: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * 批量删除书籍
     */
    fun deleteBooksInBatch(books: List<BookEntity>, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                books.forEach { book ->
                    bookRepository.deleteCompleteBook(book.id)
                }
                
                withContext(Dispatchers.Main) {
                    // 从当前列表中移除这些书籍
                    val bookIdsToDelete = books.map { it.id }.toSet()
                    val currentBooks = _bookList.value.toMutableList()
                    currentBooks.removeAll { it.id in bookIdsToDelete }
                    _bookList.value = currentBooks
                    
                    // 更新UI状态
                    _bookListState.value = _bookListState.value.copy(
                        books = currentBooks
                    )
                    
                    _state.value = when {
                        currentBooks.isEmpty() -> BookListUIState.Empty
                        else -> BookListUIState.Success(currentBooks, isLoadingMore = false)
                    }
                    
                    onSuccess?.invoke()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _bookListState.value = _bookListState.value.copy(
                        errorMessage = "批量删除失败: ${e.message}"
                    )
                }
            }
        }
    }
}