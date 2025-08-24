package com.anou.pagegather.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.anou.pagegather.data.local.dao.BookDao
import com.anou.pagegather.data.local.dao.BookCollectionDao
import com.anou.pagegather.data.local.dao.BookGroupRefDao
import com.anou.pagegather.data.local.dao.BookSourceDao
import com.anou.pagegather.data.local.dao.BookGroupDao
import com.anou.pagegather.data.local.dao.NoteDao
import com.anou.pagegather.data.local.dao.NoteAttachmentDao
import com.anou.pagegather.data.local.dao.ReadingRecordDao
import com.anou.pagegather.data.local.dao.BookTagRefDao
import com.anou.pagegather.data.local.dao.TagDao
import com.anou.pagegather.data.local.entity.BookCollectionEntity
import com.anou.pagegather.data.local.entity.BookEntity
import com.anou.pagegather.data.local.entity.BookGroupRefEntity
import com.anou.pagegather.data.local.entity.BookSourceEntity
import com.anou.pagegather.data.local.entity.BookGroupEntity
import com.anou.pagegather.data.local.entity.NoteEntity
import com.anou.pagegather.data.local.entity.NoteAttachmentEntity
import com.anou.pagegather.data.local.entity.ReadingRecordEntity
import com.anou.pagegather.data.local.entity.BookTagRefEntity
import com.anou.pagegather.data.local.entity.TagEntity
import com.anou.pagegather.data.local.entity.NoteTagRefEntity

/**
 * 应用数据库
 * 使用 Room 数据库，包含书籍、笔记、阅读记录等核心数据表
 */
@Database(
    entities = [
        BookEntity::class,
        NoteEntity::class,
        NoteAttachmentEntity::class,
        TagEntity::class,
        BookTagRefEntity::class,
        NoteTagRefEntity::class,
        BookGroupEntity::class,
        BookGroupRefEntity::class,
        ReadingRecordEntity::class,
        BookCollectionEntity::class,
        BookSourceEntity::class
    ],
    version = 16,  // BookGroupEntity.name字段改为非空  
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    /** 书籍数据访问对象 */
    abstract fun bookDao(): BookDao
    
    /** 笔记数据访问对象 */
    abstract fun noteDao(): NoteDao
    
    /** 笔记附件数据访问对象 */
    abstract fun noteAttachmentDao(): NoteAttachmentDao
    
    /** 阅读记录数据访问对象 */
    abstract fun readingRecordDao(): ReadingRecordDao
    
    /** 书籍收藏数据访问对象 */
    abstract fun bookCollectionDao(): BookCollectionDao
    
    /** 书籍来源数据访问对象 */
    abstract fun bookSourceDao(): BookSourceDao
    
    /** 书籍分组数据访问对象 */
    abstract fun bookGroupDao(): BookGroupDao
    
    /** 标签数据访问对象 */
    abstract fun tagDao(): TagDao
    
    /** 书籍分组关联数据访问对象 */
    abstract fun bookGroupRefDao(): BookGroupRefDao
    
    /** 书籍标签关联数据访问对象 */
    abstract fun bookTagRefDao(): BookTagRefDao
}