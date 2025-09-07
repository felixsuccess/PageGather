package com.anou.pagegather.ui.feature.bookshelf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anou.pagegather.data.local.entity.BookEntity
import com.anou.pagegather.data.local.entity.BookGroupEntity
import com.anou.pagegather.data.local.entity.BookSourceEntity
import com.anou.pagegather.data.repository.BookRepository
import com.anou.pagegather.data.repository.BookGroupRepository
import com.anou.pagegather.data.repository.BookSourceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * 书籍列表状态
 */
data class BookListState(
    val books: List<BookEntity> = emptyList(),
    val availableGroups: List<BookGroupEntity> = emptyList(),
    val availableSources: List<BookSourceEntity> = emptyList(),
    val availableTags: List<com.anou.pagegather.data.local.entity.TagEntity> = emptyList(),
    val selectedGroupId: Long? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMoreData: Boolean = true,
    val errorMessage: String? = null,
    // 批量操作相关状态
    val isBatchMode: Boolean = false,
    val selectedBooks: Set<Long> = emptySet(),
    // 排序相关状态
    val sortField: SortField = SortField.ADD_TIME,
    val isAscending: Boolean = false,
    // 显示模式状态
    val isGridMode: Boolean = true
) {
    /**
     * 排序字段枚举
     */
    enum class SortField {
        NAME,        // 书名
        AUTHOR,      // 作者
        ADD_TIME,    // 添加时间
        READ_STATUS  // 阅读状态
    }
}

@HiltViewModel
class BookListViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val groupRepository: BookGroupRepository,
    private val bookSourceRepository: BookSourceRepository
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
            // 加载来源列表
            loadAvailableSources()
            // 加载标签列表
            loadAvailableTags()
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
    
    /**
     * 加载可用来源
     */
    private fun loadAvailableSources() {
        viewModelScope.launch {
            try {
                bookSourceRepository.getAllEnabledSources().collect { sources ->
                    _bookListState.value = _bookListState.value.copy(
                        availableSources = sources
                    )
                }
            } catch (e: Exception) {
                _bookListState.value = _bookListState.value.copy(
                    errorMessage = "加载来源失败: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 加载可用标签
     */
    private fun loadAvailableTags() {
        viewModelScope.launch {
            try {
                bookRepository.getBookTags().collect { tags ->
                    _bookListState.value = _bookListState.value.copy(
                        availableTags = tags
                    )
                }
            } catch (e: Exception) {
                _bookListState.value = _bookListState.value.copy(
                    errorMessage = "加载标签失败: ${e.message}"
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
            val sortField = currentState.sortField
            val isAscending = currentState.isAscending
            
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
                    // 正常分页加载，根据排序字段和方向加载
                    val sortType = when (sortField) {
                        BookListState.SortField.NAME -> 2 // 书名
                        BookListState.SortField.AUTHOR -> 3 // 作者
                        BookListState.SortField.ADD_TIME -> 0 // 添加时间(默认)
                        BookListState.SortField.READ_STATUS -> 4 // 阅读进度
                    }
                    bookRepository.getBooksSorted(sortType, isAscending)
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
     * 清除错误消息
     */
    fun clearError() {
        _bookListState.value = _bookListState.value.copy(errorMessage = null)
    }
    
    /**
     * 获取分组中的书籍
     */
    fun getBooksByGroupId(groupId: Long): Flow<List<BookEntity>> {
        return combine(
            groupRepository.getBooksByGroupId(groupId),
            bookRepository.getAllBooks()
        ) { groupRefs, allBooks ->
            val bookIds = groupRefs.map { it.bookId }.toSet()
            allBooks.filter { it.id in bookIds }
        }
    }
    
    /**
     * 获取分组中的书籍数量
     */
    fun getGroupBookCount(groupId: Long): Flow<Int> {
        return groupRepository.getBooksByGroupId(groupId).map { books -> books.size }
    }
    
    /**
     * 获取来源中的书籍
     */
    fun getBooksBySourceId(sourceId: Long): Flow<List<BookEntity>> {
        return bookRepository.getBooksBySourceId(sourceId.toInt())
    }
    
    /**
     * 获取来源中的前9本书籍
     */
    fun getSourceTopBooks(sourceId: Long, limit: Int = 9): Flow<List<BookEntity>> {
        return bookRepository.getBooksBySourceId(sourceId.toInt()).map { books -> 
            books.take(limit)
        }
    }

    /**
     * 获取来源中的书籍数量
     */
    fun getSourceBookCount(sourceId: Long): Flow<Int> {
        return bookRepository.getBooksBySourceId(sourceId.toInt()).map { books -> books.size }
    }
    
    /**
     * 获取指定状态下的书籍
     */
    fun getBooksByStatus(status: Int): Flow<List<BookEntity>> {
        return bookRepository.getBooksByStatus(status)
    }
    
    /**
     * 获取指定状态下的书籍数量
     */
    fun getStatusBookCount(status: com.anou.pagegather.data.local.entity.ReadStatus): Flow<Int> {
        return bookRepository.getAllBooks().map { books -> 
            books.count { it.readStatus == status.code }
        }
    }
    
    /**
     * 获取指定标签下的书籍数量
     */
    fun getTagBookCount(tagId: Long): Flow<Int> {
        return bookRepository.getBooksWithTag(tagId).map { books -> books.size }
    }
    
    /**
     * 获取指定标签下的书籍列表
     */
    fun getBooksWithTag(tagId: Long): Flow<List<BookEntity>> {
        return bookRepository.getBooksWithTag(tagId)
    }
    
    /**
     * 获取指定评分下的书籍数量
     */
    fun getRatingBookCount(rating: Int): Flow<Int> {
        return bookRepository.getBooksByRating(rating.toFloat()).map { books -> books.size }
    }
    
    /**
     * 获取指定评分下的书籍列表
     */
    fun getBooksByRating(rating: Float): Flow<List<BookEntity>> {
        return bookRepository.getBooksByRating(rating)
    }
    
    /**
     * 获取未分组的书籍列表
     */
    fun getUngroupedBooks(): Flow<List<BookEntity>> {
        return bookRepository.getUngroupedBooks()
    }
    
    /**
     * 获取未分组的书籍数量
     */
    fun getUngroupedBookCount(): Flow<Int> {
        return bookRepository.getUngroupedBooks().map { books -> books.size }
    }
    
    /**
     * 获取未设置标签的书籍列表
     */
    fun getUntaggedBooks(): Flow<List<BookEntity>> {
        return bookRepository.getUntaggedBooks()
    }
    
    /**
     * 获取未设置标签的书籍数量
     */
    fun getUntaggedBookCount(): Flow<Int> {
        return bookRepository.getUntaggedBooks().map { books -> books.size }
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
    
    /**
     * 切换批量操作模式
     */
    fun toggleBatchMode() {
        val currentState = _bookListState.value
        _bookListState.value = currentState.copy(
            isBatchMode = !currentState.isBatchMode,
            selectedBooks = if (currentState.isBatchMode) emptySet() else currentState.selectedBooks
        )
    }
    
    /**
     * 选择/取消选择单个书籍
     */
    fun toggleBookSelection(bookId: Long) {
        val currentState = _bookListState.value
        if (!currentState.isBatchMode) return
        
        val newSelectedBooks = if (bookId in currentState.selectedBooks) {
            currentState.selectedBooks - bookId
        } else {
            currentState.selectedBooks + bookId
        }
        
        _bookListState.value = currentState.copy(
            selectedBooks = newSelectedBooks
        )
    }
    
    /**
     * 全选当前列表中的所有书籍
     */
    fun selectAllBooks() {
        val currentState = _bookListState.value
        if (!currentState.isBatchMode) return
        
        val allBookIds = currentState.books.map { it.id }.toSet()
        _bookListState.value = currentState.copy(
            selectedBooks = allBookIds
        )
    }
    
    /**
     * 清空选择
     */
    fun clearBookSelection() {
        val currentState = _bookListState.value
        if (!currentState.isBatchMode) return
        
        _bookListState.value = currentState.copy(
            selectedBooks = emptySet()
        )
    }
    
    /**
     * 设置排序字段
     */
    fun setSortField(sortField: BookListState.SortField) {
        val currentState = _bookListState.value
        if (currentState.sortField != sortField) {
            _bookListState.value = currentState.copy(
                sortField = sortField
            )
            // 重新加载书籍列表以应用新的排序
            viewModelScope.launch {
                loadBooks()
            }
        }
    }
    
    /**
     * 设置排序方向
     */
    fun setSortDirection(isAscending: Boolean) {
        val currentState = _bookListState.value
        if (currentState.isAscending != isAscending) {
            _bookListState.value = currentState.copy(
                isAscending = isAscending
            )
            // 重新加载书籍列表以应用新的排序方向
            viewModelScope.launch {
                loadBooks()
            }
        }
    }
    
    /**
     * 切换显示模式
     */
    fun toggleDisplayMode() {
        val currentState = _bookListState.value
        _bookListState.value = currentState.copy(
            isGridMode = !currentState.isGridMode
        )
    }
    
    /**
     * 标记书籍为已完成
     */
    fun markBookAsFinished(bookId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val book = bookRepository.getBookById(bookId)
                book?.let {
                    val updatedBook = it.copy(readStatus = 2) // 2 表示已完成
                    bookRepository.updateBook(updatedBook)
                    
                    // 更新UI状态
                    withContext(Dispatchers.Main) {
                        val currentBooks = _bookList.value.toMutableList()
                        val index = currentBooks.indexOfFirst { b -> b.id == bookId }
                        if (index != -1) {
                        currentBooks[index] = updatedBook
                            _bookList.value = currentBooks
                            _bookListState.value = _bookListState.value.copy(books = currentBooks)
                            _state.value = BookListUIState.Success(currentBooks, isLoadingMore = false)
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _bookListState.value = _bookListState.value.copy(
                        errorMessage = "标记为已完成失败: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * 切换书籍置顶状态
     */
    fun toggleBookPin(bookId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val book = bookRepository.getBookById(bookId)
                book?.let {
                    val updatedBook = it.copy(pinned = !it.pinned)
                    bookRepository.updateBook(updatedBook)
                    
                    // 更新UI状态
                    withContext(Dispatchers.Main) {
                        val currentBooks = _bookList.value.toMutableList()
                        val index = currentBooks.indexOfFirst { b -> b.id == bookId }
                        if (index != -1) {
                        currentBooks[index] = updatedBook
                            _bookList.value = currentBooks
                            _bookListState.value = _bookListState.value.copy(books = currentBooks)
                            _state.value = BookListUIState.Success(currentBooks, isLoadingMore = false)
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _bookListState.value = _bookListState.value.copy(
                        errorMessage = "置顶操作失败: ${e.message}"
                    )
                }
            }
        }
    }
    
}