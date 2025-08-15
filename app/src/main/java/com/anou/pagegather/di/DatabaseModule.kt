package com.anou.pagegather.di

import android.content.Context
import androidx.room.Room
import com.anou.pagegather.data.local.dao.BookDao
import com.anou.pagegather.data.local.dao.NoteDao
import com.anou.pagegather.data.local.dao.ReadingRecordDao
import com.anou.pagegather.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.Executors
import javax.inject.Singleton

/**
 * 数据库依赖注入模块
 * 提供数据库实例和各种 DAO 的依赖注入
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * 提供应用数据库实例
     */
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "read_time_db"
        )
            // TODO: 仅在开发环境下执行 SQL 日志
            .setQueryCallback({ sql, params ->
                // 添加 SQL 日志用于调试
                println("SQL: $sql\nParams: $params")
            }, Executors.newSingleThreadExecutor())
            // TODO: 生产环境需要正确的数据库迁移策略
            .fallbackToDestructiveMigration()
            .build()
    }

    /** 提供书籍数据访问对象 */
    @Provides
    fun provideBookDao(database: AppDatabase): BookDao {
        return database.bookDao()
    }

    /** 提供笔记数据访问对象 */
    @Provides
    fun provideNoteDao(database: AppDatabase): NoteDao {
        return database.noteDao()
    }

    /** 提供阅读记录数据访问对象 - 任务4新增 */
    @Provides
    fun provideReadingRecordDao(database: AppDatabase): ReadingRecordDao {
        return database.readingRecordDao()
    }
}