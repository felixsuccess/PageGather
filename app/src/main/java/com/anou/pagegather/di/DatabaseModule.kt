package com.anou.pagegather.di

import android.content.Context
import androidx.room.Room
import com.anou.pagegather.data.local.dao.BookDao
import com.anou.pagegather.data.local.dao.NoteDao
import com.anou.pagegather.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

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
            //TODO： 仅在开发环境下执行
            .setQueryCallback({ sql, params ->
            // 添加 SQL 日志用于调试
            println("SQL: $sql\nParams: $params")
        }, Executors.newSingleThreadExecutor())
            .build()
    }

    @Provides
    fun provideBookDao(database: AppDatabase): BookDao {
        return database.bookDao()
    }

    @Provides
    fun provideNoteDao(database: AppDatabase): NoteDao {
        return database.noteDao()
    }
}