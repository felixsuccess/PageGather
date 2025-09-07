package com.anou.pagegather.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// 创建DataStore实例
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

/**
 * 应用配置数据存储管理类
 * 使用DataStore存储用户偏好设置，如显示模式、筛选选项、排序设置等
 */
@Singleton
class DataStoreManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // 定义偏好设置的键
    companion object {
        // 显示模式相关
        val IS_GRID_MODE = booleanPreferencesKey("is_grid_mode")
        
        // 筛选选项相关
        val SELECTED_FILTER_CODE = stringPreferencesKey("selected_filter_code")
        
        // 排序相关
        val SORT_FIELD = intPreferencesKey("sort_field")
        val IS_ASCENDING = booleanPreferencesKey("is_ascending")
        
        // 默认值
        const val DEFAULT_IS_GRID_MODE = true
        const val DEFAULT_SELECTED_FILTER_CODE = "default"
        const val DEFAULT_SORT_FIELD = 0 // ADD_TIME
        const val DEFAULT_IS_ASCENDING = false
    }

    // 获取DataStore实例
    private val dataStore: DataStore<Preferences> = context.dataStore

    // 获取显示模式设置
    val isGridMode: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[IS_GRID_MODE] ?: DEFAULT_IS_GRID_MODE
    }

    // 获取筛选选项设置
    val selectedFilterCode: Flow<String> = dataStore.data.map { preferences ->
        preferences[SELECTED_FILTER_CODE] ?: DEFAULT_SELECTED_FILTER_CODE
    }

    // 获取排序字段设置
    val sortField: Flow<Int> = dataStore.data.map { preferences ->
        preferences[SORT_FIELD] ?: DEFAULT_SORT_FIELD
    }

    // 获取排序方向设置
    val isAscending: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[IS_ASCENDING] ?: DEFAULT_IS_ASCENDING
    }

    // 更新显示模式设置
    suspend fun updateGridMode(isGridMode: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_GRID_MODE] = isGridMode
        }
    }

    // 更新筛选选项设置
    suspend fun updateSelectedFilterCode(filterCode: String) {
        dataStore.edit { preferences ->
            preferences[SELECTED_FILTER_CODE] = filterCode
        }
    }

    // 更新排序字段设置
    suspend fun updateSortField(sortField: Int) {
        dataStore.edit { preferences ->
            preferences[SORT_FIELD] = sortField
        }
    }

    // 更新排序方向设置
    suspend fun updateIsAscending(isAscending: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_ASCENDING] = isAscending
        }
    }
}