package com.anou.pagegather.di

import com.anou.pagegather.data.local.database.AppDatabase
import com.anou.pagegather.data.repository.BookGroupRepository
import com.anou.pagegather.data.repository.BookRepository
import com.anou.pagegather.data.repository.BookSourceRepository
import com.anou.pagegather.data.repository.NoteRepository
import com.anou.pagegather.data.repository.ReadingRecordRepository
import com.anou.pagegather.data.repository.TagRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 仓库依赖注入模块
 * 提供各种 Repository 的依赖注入
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    /**
     * 提供书籍仓库实例
     */
    @Provides
    @Singleton
    fun provideBookRepository(database: AppDatabase): BookRepository {
        return BookRepository(database)
    }

    /**
     * 提供书籍分组仓库实例
     */
    @Provides
    @Singleton
    fun provideBookGroupRepository(database: AppDatabase): BookGroupRepository {
        return BookGroupRepository(database)
    }

    /**
     * 提供书籍来源仓库实例
     */
    @Provides
    @Singleton
    fun provideBookSourceRepository(database: AppDatabase): BookSourceRepository {
        return BookSourceRepository(database)
    }

    /**
     * 提供标签仓库实例
     */
    @Provides
    @Singleton
    fun provideTagRepository(database: AppDatabase): TagRepository {
        return TagRepository(database)
    }

    /**
     * 提供笔记仓库实例
     */
    @Provides
    @Singleton
    fun provideNoteRepository(database: AppDatabase): NoteRepository {
        return NoteRepository(database)
    }

    /**
     * 提供阅读记录仓库实例
     */
    @Provides
    @Singleton
    fun provideReadingRecordRepository(database: AppDatabase): ReadingRecordRepository {
        return ReadingRecordRepository(database)
    }
}