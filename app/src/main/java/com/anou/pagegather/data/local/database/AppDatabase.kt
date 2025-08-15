package com.anou.pagegather.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.anou.pagegather.data.local.dao.BookDao
import com.anou.pagegather.data.local.dao.NoteDao
import com.anou.pagegather.data.local.dao.ReadingRecordDao
import com.anou.pagegather.data.local.entity.BookEntity
import com.anou.pagegather.data.local.entity.BookSourceEntity
import com.anou.pagegather.data.local.entity.GroupBookEntity
import com.anou.pagegather.data.local.entity.GroupEntity
import com.anou.pagegather.data.local.entity.NoteEntity
import com.anou.pagegather.data.local.entity.ReadingRecordEntity
import com.anou.pagegather.data.local.entity.TagBookEntity
import com.anou.pagegather.data.local.entity.TagEntity
import com.anou.pagegather.data.local.entity.TagNoteEntity

/**
 * 应用数据库
 * 使用 Room 数据库，包含书籍、笔记、阅读记录等核心数据表
 */
@Database(
    entities = [
        BookEntity::class,
        NoteEntity::class,
        TagEntity::class,
        TagBookEntity::class,
        TagNoteEntity::class,
        GroupEntity::class,
        GroupBookEntity::class,
        BookSourceEntity::class,
        ReadingRecordEntity::class // 任务4新增：阅读记录实体
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    /** 书籍数据访问对象 */
    abstract fun bookDao(): BookDao
    
    /** 笔记数据访问对象 */
    abstract fun noteDao(): NoteDao
    
    /** 阅读记录数据访问对象 - 任务4新增 */
    abstract fun readingRecordDao(): ReadingRecordDao
}