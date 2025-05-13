package com.anou.pagegather.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.anou.pagegather.data.local.entity.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Query("SELECT * FROM book WHERE is_deleted = 0 ORDER BY updated_date DESC")
    fun getAllBooks(): Flow<List<BookEntity>>

    @Insert
    suspend fun insert(bookEntity: BookEntity): Long

    @Query("DELETE FROM book WHERE id = :bookId")
    suspend fun delete(bookId: Long): Int


    @Update
    fun update(bookEntity: BookEntity)

    @Delete
    fun delete(bookEntity: BookEntity)

    @Query("SELECT * FROM book WHERE id = :id")
    fun getById(id: Long): BookEntity?
}