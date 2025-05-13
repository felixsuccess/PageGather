package com.anou.pagegather.data.local.database

import androidx.room.TypeConverter
import com.anou.pagegather.data.local.entity.BookType

class BookTypeConverter {
    @TypeConverter
    fun fromBookType(bookType: BookType): Int = bookType.code

    @TypeConverter
    fun toBookType(value: Int): BookType = BookType.fromValue(value)
}


