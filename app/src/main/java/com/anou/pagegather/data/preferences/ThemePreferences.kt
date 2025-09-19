package com.anou.pagegather.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.anou.pagegather.ui.theme.AppTheme
import com.anou.pagegather.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 主题偏好存储类
 * 使用 DataStore 持久化存储用户的主题选择
 */
@Singleton
class ThemePreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val THEME_KEY = stringPreferencesKey("selected_theme")
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
    }
    
    /**
     * 保存选定的主题
     */
    suspend fun saveTheme(theme: AppTheme) {
        try {
            dataStore.edit { preferences ->
                preferences[THEME_KEY] = theme.id
            }
        } catch (e: Exception) {
            // 记录错误但不抛出异常，保持应用稳定性
            android.util.Log.e("ThemePreferences", "Failed to save theme: ${theme.id}", e)
        }
    }
    
    /**
     * 保存主题模式（亮色/暗色/跟随系统）
     */
    suspend fun saveThemeMode(mode: ThemeMode) {
        try {
            dataStore.edit { preferences ->
                preferences[THEME_MODE_KEY] = mode.id
            }
        } catch (e: Exception) {
            android.util.Log.e("ThemePreferences", "Failed to save theme mode: ${mode.id}", e)
        }
    }
    
    /**
     * 获取保存的主题
     * 如果没有保存的主题或发生错误，返回默认主题
     */
    fun getTheme(): Flow<AppTheme> = dataStore.data
        .map { preferences ->
            val themeId = preferences[THEME_KEY] ?: AppTheme.getDefault().id
            AppTheme.fromId(themeId)
        }
        .catch { exception ->
            android.util.Log.e("ThemePreferences", "Failed to load theme", exception)
            emit(AppTheme.getDefault())
        }
    
    /**
     * 获取保存的主题模式
     * 如果没有保存的模式或发生错误，返回默认模式（跟随系统）
     */
    fun getThemeMode(): Flow<ThemeMode> = dataStore.data
        .map { preferences ->
            val modeName = preferences[THEME_MODE_KEY] ?: ThemeMode.getDefault().id
            ThemeMode.fromId(modeName)
        }
        .catch { exception ->
            android.util.Log.e("ThemePreferences", "Failed to load theme mode", exception)
            emit(ThemeMode.getDefault())
        }
    
    /**
     * 清除所有主题偏好设置
     * 用于重置或测试
     */
    suspend fun clearThemePreferences() {
        try {
            dataStore.edit { preferences ->
                preferences.remove(THEME_KEY)
                preferences.remove(THEME_MODE_KEY)
            }
        } catch (e: Exception) {
            android.util.Log.e("ThemePreferences", "Failed to clear theme preferences", e)
        }
    }
}