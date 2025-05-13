package com.anou.pagegather.data.repository


import androidx.room.withTransaction
import com.anou.pagegather.data.local.database.AppDatabase
import com.anou.pagegather.data.local.entity.BookEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BookRepository @Inject constructor(
    private val database: AppDatabase
) {
    private val bookDao = database.bookDao()

    suspend fun <T> runInTransaction(block: suspend () -> T): T {
        return database.withTransaction {
            block()
        }
    }

    val books: Flow<List<BookEntity>> = bookDao.getAllBooks()

    fun getAllBooks(): Flow<List<BookEntity>> {
        return bookDao.getAllBooks()
    }


    suspend fun insert(book: BookEntity): Long {
        return bookDao.insert(book)
    }

    suspend fun update(book: BookEntity) {
        bookDao.update(book)
    }
    suspend fun delete(book: BookEntity) {
        bookDao.delete(book)
    }

    suspend fun getBookById(id: Long): BookEntity? {
        return bookDao.getById(id)
    }




}