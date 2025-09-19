package com.anou.pagegather.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.anou.pagegather.data.preferences.ThemePreferences
import com.anou.pagegather.domain.theme.ThemeManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 主题系统依赖注入模块
 */
@Module
@InstallIn(SingletonComponent::class)
object ThemeModule {
    
    // DataStore 扩展属性
    private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(
        name = "theme_preferences"
    )
    
    /**
     * 提供主题偏好 DataStore
     */
    @Provides
    @Singleton
    fun provideThemeDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return context.themeDataStore
    }
    
    /**
     * 提供主题偏好存储
     */
    @Provides
    @Singleton
    fun provideThemePreferences(
        dataStore: DataStore<Preferences>
    ): ThemePreferences {
        return ThemePreferences(dataStore)
    }
    
    /**
     * 提供主题管理器
     */
    @Provides
    @Singleton
    fun provideThemeManager(
        themePreferences: ThemePreferences,
        @ApplicationContext context: Context
    ): ThemeManager {
        return ThemeManager(themePreferences, context)
    }
}