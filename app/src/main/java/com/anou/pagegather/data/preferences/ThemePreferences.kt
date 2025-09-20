package com.anou.pagegather.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
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
        private val DYNAMIC_COLOR_KEY = booleanPreferencesKey("dynamic_color")
        private val CUSTOM_THEMES_KEY = stringSetPreferencesKey("custom_themes")
        private val SELECTED_CUSTOM_THEME_KEY = stringPreferencesKey("selected_custom_theme")
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
     * 保存动态颜色偏好设置
     */
    suspend fun saveDynamicColor(enabled: Boolean) {
        try {
            dataStore.edit { preferences ->
                preferences[DYNAMIC_COLOR_KEY] = enabled
            }
        } catch (e: Exception) {
            android.util.Log.e("ThemePreferences", "Failed to save dynamic color preference: $enabled", e)
        }
    }
    
    /**
     * 保存自定义主题ID列表
     */
    suspend fun saveCustomThemes(themeIds: Set<String>) {
        try {
            dataStore.edit { preferences ->
                preferences[CUSTOM_THEMES_KEY] = themeIds
            }
        } catch (e: Exception) {
            android.util.Log.e("ThemePreferences", "Failed to save custom themes", e)
        }
    }
    
    /**
     * 保存选定的自定义主题ID
     */
    suspend fun saveSelectedCustomTheme(themeId: String?) {
        try {
            dataStore.edit { preferences ->
                if (themeId != null) {
                    preferences[SELECTED_CUSTOM_THEME_KEY] = themeId
                } else {
                    preferences.remove(SELECTED_CUSTOM_THEME_KEY)
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("ThemePreferences", "Failed to save selected custom theme", e)
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
     * 获取动态颜色偏好设置
     * 如果没有保存的设置或发生错误，返回默认值（false）
     */
    fun getDynamicColor(): Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[DYNAMIC_COLOR_KEY] ?: false
        }
        .catch { exception ->
            android.util.Log.e("ThemePreferences", "Failed to load dynamic color preference", exception)
            emit(false)
        }
    
    /**
     * 获取自定义主题ID列表
     */
    fun getCustomThemes(): Flow<Set<String>> = dataStore.data
        .map { preferences ->
            preferences[CUSTOM_THEMES_KEY] ?: emptySet()
        }
        .catch { exception ->
            android.util.Log.e("ThemePreferences", "Failed to load custom themes", exception)
            emit(emptySet())
        }
    
    /**
     * 获取选定的自定义主题ID
     */
    fun getSelectedCustomTheme(): Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[SELECTED_CUSTOM_THEME_KEY]
        }
        .catch { exception ->
            android.util.Log.e("ThemePreferences", "Failed to load selected custom theme", exception)
            emit(null)
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
                preferences.remove(DYNAMIC_COLOR_KEY)
                preferences.remove(CUSTOM_THEMES_KEY)
                preferences.remove(SELECTED_CUSTOM_THEME_KEY)
            }
        } catch (e: Exception) {
            android.util.Log.e("ThemePreferences", "Failed to clear theme preferences", e)
        }
    }
}