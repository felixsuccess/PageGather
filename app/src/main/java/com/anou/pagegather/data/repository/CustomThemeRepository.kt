package com.anou.pagegather.data.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.anou.pagegather.data.preferences.ThemePreferences
import com.anou.pagegather.ui.theme.CustomTheme
import com.anou.pagegather.ui.theme.ThemeCustomizationImpl
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 自定义主题仓库
 * 负责自定义主题的持久化存储和管理
 */
@Singleton
class CustomThemeRepository @Inject constructor(
    private val themePreferences: ThemePreferences,
    private val dataStore: DataStore<Preferences>,
    @ApplicationContext private val context: Context
) {
    private val themeCustomization = ThemeCustomizationImpl()
    
    companion object {
        private const val TAG = "CustomThemeRepository"
    }
    
    /**
     * 保存自定义主题
     */
    suspend fun saveCustomTheme(theme: CustomTheme): Result<Unit> {
        return try {
            // 序列化主题对象
            val themeJson = CustomThemeSerializer.serialize(theme)
            val themeKey = stringPreferencesKey("custom_theme_${theme.id}")
            
            // 保存主题数据
            dataStore.edit { preferences ->
                preferences[themeKey] = themeJson
            }
            
            // 更新主题列表
            val currentThemes = getCustomThemeIds()
            val updatedThemes = currentThemes + theme.id
            themePreferences.saveCustomThemes(updatedThemes)
            
            Log.d(TAG, "Custom theme saved: ${theme.name}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save custom theme: ${theme.name}", e)
            Result.failure(e)
        }
    }
    
    /**
     * 获取自定义主题
     */
    fun getCustomTheme(themeId: String): Flow<CustomTheme?> {
        val themeKey = stringPreferencesKey("custom_theme_$themeId")
        
        return dataStore.data
            .map { preferences ->
                val themeJson = preferences[themeKey]
                if (themeJson != null) {
                    try {
                        CustomThemeSerializer.deserialize(themeJson)
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to deserialize custom theme: $themeId", e)
                        null
                    }
                } else {
                    null
                }
            }
            .catch { exception ->
                Log.e(TAG, "Failed to load custom theme: $themeId", exception)
                emit(null)
            }
    }
    
    /**
     * 获取所有自定义主题ID
     */
    suspend fun getCustomThemeIds(): Set<String> {
        return try {
            themePreferences.getCustomThemes()
                .first()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get custom theme IDs", e)
            emptySet()
        }
    }
    
    /**
     * 删除自定义主题
     */
    suspend fun deleteCustomTheme(themeId: String): Result<Unit> {
        return try {
            val themeKey = stringPreferencesKey("custom_theme_$themeId")
            
            // 删除主题数据
            dataStore.edit { preferences ->
                preferences.remove(themeKey)
            }
            
            // 更新主题列表
            val currentThemes = getCustomThemeIds()
            val updatedThemes = currentThemes - themeId
            themePreferences.saveCustomThemes(updatedThemes)
            
            Log.d(TAG, "Custom theme deleted: $themeId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete custom theme: $themeId", e)
            Result.failure(e)
        }
    }
    
    /**
     * 更新自定义主题
     */
    suspend fun updateCustomTheme(theme: CustomTheme): Result<Unit> {
        return try {
            // 序列化主题对象
            val themeJson = CustomThemeSerializer.serialize(theme)
            val themeKey = stringPreferencesKey("custom_theme_${theme.id}")
            
            // 更新主题数据
            dataStore.edit { preferences ->
                preferences[themeKey] = themeJson
            }
            
            Log.d(TAG, "Custom theme updated: ${theme.name}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update custom theme: ${theme.name}", e)
            Result.failure(e)
        }
    }
    
    /**
     * 检查主题ID是否存在
     */
    suspend fun isThemeExists(themeId: String): Boolean {
        return try {
            val themeKey = stringPreferencesKey("custom_theme_$themeId")
            dataStore.data.map { it.contains(themeKey) }
                .first()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to check if theme exists: $themeId", e)
            false
        }
    }
    
    /**
     * 获取所有自定义主题
     */
    fun getAllCustomThemes(): Flow<List<CustomTheme>> {
        return themePreferences.getCustomThemes()
            .map { themeIds ->
                themeIds.mapNotNull { themeId ->
                    getCustomTheme(themeId)
                        .first()
                }
            }
            .catch { exception ->
                Log.e(TAG, "Failed to load all custom themes", exception)
                emit(emptyList())
            }
    }
}