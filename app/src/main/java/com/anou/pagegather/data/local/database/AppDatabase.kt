package com.anou.pagegather.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.anou.pagegather.data.local.dao.BookDao
import com.anou.pagegather.data.local.dao.NoteDao
import com.anou.pagegather.data.local.entity.BookEntity
import com.anou.pagegather.data.local.entity.BookSourceEntity
import com.anou.pagegather.data.local.entity.GroupBookEntity
import com.anou.pagegather.data.local.entity.GroupEntity
import com.anou.pagegather.data.local.entity.NoteEntity
import com.anou.pagegather.data.local.entity.TagBookEntity
import com.anou.pagegather.data.local.entity.TagEntity
import com.anou.pagegather.data.local.entity.TagNoteEntity

@Database(
    entities = [
        BookEntity::class,
        NoteEntity::class,
        TagEntity::class,
        TagBookEntity::class,
        TagNoteEntity::class,
        GroupEntity::class,
        GroupBookEntity::class,
        BookSourceEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun noteDao(): NoteDao
}