package com.anou.pagegather.ui.feature.bookshelf

import com.anou.pagegather.data.local.entity.BookEntity
import com.anou.pagegather.data.repository.BookRepository
import com.anou.pagegather.data.repository.BookGroupRepository
import com.anou.pagegather.data.repository.BookSourceRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class BookListViewModelTest {

    private val testDispatcher = TestCoroutineDispatcher()
    private lateinit var bookRepository: BookRepository
    private lateinit var groupRepository: BookGroupRepository
    private lateinit var sourceRepository: BookSourceRepository
    private lateinit var viewModel: BookListViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        bookRepository = mockk()
        groupRepository = mockk()
        sourceRepository = mockk()
        
        // 默认mock行为
        coEvery { bookRepository.getAllBooks() } returns flowOf(emptyList())
        coEvery { groupRepository.getAllGroups() } returns flowOf(emptyList())
        coEvery { sourceRepository.getAllEnabledSources() } returns flowOf(emptyList())
        
        viewModel = BookListViewModel(
            bookRepository = bookRepository,
            groupRepository = groupRepository,
            bookSourceRepository = sourceRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `getSourceBookCount returns correct count`() = testDispatcher.runBlockingTest {
        // 准备测试数据
        val sourceId = 1L
        val testBooks = listOf(
            BookEntity(id = 1, name = "Book 1", bookSourceId = sourceId.toInt()),
            BookEntity(id = 2, name = "Book 2", bookSourceId = sourceId.toInt()),
            BookEntity(id = 3, name = "Book 3", bookSourceId = sourceId.toInt())
        )
        
        // 设置mock行为
        coEvery { bookRepository.getBooksBySourceId(sourceId.toInt()) } returns flowOf(testBooks)
        
        // 收集流中的值
        val result = mutableListOf<Int>()
        viewModel.getSourceBookCount(sourceId).collect { count ->
            result.add(count)
        }
        
        // 验证结果
        assertEquals(1, result.size)
        assertEquals(3, result[0])
    }

    @Test
    fun `getSourceTopBooks returns limited number of books`() = testDispatcher.runBlockingTest {
        // 准备测试数据
        val sourceId = 1L
        val testBooks = listOf(
            BookEntity(id = 1, name = "Book 1", bookSourceId = sourceId.toInt()),
            BookEntity(id = 2, name = "Book 2", bookSourceId = sourceId.toInt()),
            BookEntity(id = 3, name = "Book 3", bookSourceId = sourceId.toInt()),
            BookEntity(id = 4, name = "Book 4", bookSourceId = sourceId.toInt()),
            BookEntity(id = 5, name = "Book 5", bookSourceId = sourceId.toInt())
        )
        
        // 设置mock行为
        coEvery { bookRepository.getBooksBySourceId(sourceId.toInt()) } returns flowOf(testBooks)
        
        // 收集流中的值 - 默认限制为9本
        val result1 = mutableListOf<List<BookEntity>>()
        viewModel.getSourceTopBooks(sourceId).collect { books ->
            result1.add(books)
        }
        
        // 收集流中的值 - 限制为2本
        val result2 = mutableListOf<List<BookEntity>>()
        viewModel.getSourceTopBooks(sourceId, 2).collect { books ->
            result2.add(books)
        }
        
        // 验证结果 - 默认限制
        assertEquals(1, result1.size)
        assertEquals(5, result1[0].size)  // 所有5本书都应该返回，因为小于默认限制9
        
        // 验证结果 - 限制为2
        assertEquals(1, result2.size)
        assertEquals(2, result2[0].size)  // 只应该返回前2本书
        assertEquals("Book 1", result2[0][0].name)
        assertEquals("Book 2", result2[0][1].name)
    }
}