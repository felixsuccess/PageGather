package com.anou.pagegather.ui.theme

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.anou.pagegather.data.preferences.ThemePreferences
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import kotlinx.coroutines.Dispatchers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

/**
 * ThemePreferences 单元测试
 * 测试主题偏好存储的核心逻辑
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ThemePreferencesTest {

    // Mock 对象
    private lateinit var mockDataStore: DataStore<Preferences>
    private lateinit var mockPreferences: Preferences
    
    // 测试对象
    private lateinit var themePreferences: ThemePreferences
    
    // 测试协程调度器
    private val testDispatcher = UnconfinedTestDispatcher()

    // 偏好设置键
    private val themeKey = stringPreferencesKey("selected_theme")
    private val themeModeKey = stringPreferencesKey("theme_mode")

    @Before
    fun setup() {
        // 设置测试协程调度器
        Dispatchers.setMain(testDispatcher)
        
        // 创建 Mock 对象
        mockDataStore = mockk()
        mockPreferences = mockk()
        
        // 创建 ThemePreferences 实例
        themePreferences = ThemePreferences(mockDataStore)
    }

    @After
    fun tearDown() {
        // 重置主协程调度器
        Dispatchers.resetMain()
        
        // 清除所有 Mock
        clearAllMocks()
    }

    @Test
    fun `getTheme 应该返回保存的主题`() = runTest {
        // Given
        val savedThemeId = AppTheme.HUNDI_PURPLE.id
        every { mockPreferences[themeKey] } returns savedThemeId
        every { mockDataStore.data } returns flowOf(mockPreferences)
        
        // When
        val result = themePreferences.getTheme().first()
        
        // Then
        assertEquals(AppTheme.HUNDI_PURPLE, result)
    }

    @Test
    fun `getTheme 没有保存的主题时应该返回默认主题`() = runTest {
        // Given - 没有保存的主题
        every { mockPreferences[themeKey] } returns null
        every { mockDataStore.data } returns flowOf(mockPreferences)
        
        // When
        val result = themePreferences.getTheme().first()
        
        // Then
        assertEquals(AppTheme.getDefault(), result)
    }

    @Test
    fun `getTheme 保存的主题ID无效时应该返回默认主题`() = runTest {
        // Given - 无效的主题ID
        every { mockPreferences[themeKey] } returns "invalid_theme_id"
        every { mockDataStore.data } returns flowOf(mockPreferences)
        
        // When
        val result = themePreferences.getTheme().first()
        
        // Then
        assertEquals(AppTheme.getDefault(), result)
    }

    @Test
    fun `getTheme 读取失败时应该返回默认主题`() = runTest {
        // Given - 模拟读取失败
        every { mockDataStore.data } throws RuntimeException("Read failed")
        
        // When
        val result = themePreferences.getTheme().first()
        
        // Then
        assertEquals(AppTheme.getDefault(), result)
    }

    @Test
    fun `getThemeMode 应该返回保存的主题模式`() = runTest {
        // Given
        val savedModeId = ThemeMode.DARK.id
        every { mockPreferences[themeModeKey] } returns savedModeId
        every { mockDataStore.data } returns flowOf(mockPreferences)
        
        // When
        val result = themePreferences.getThemeMode().first()
        
        // Then
        assertEquals(ThemeMode.DARK, result)
    }

    @Test
    fun `getThemeMode 没有保存的模式时应该返回默认模式`() = runTest {
        // Given - 没有保存的模式
        every { mockPreferences[themeModeKey] } returns null
        every { mockDataStore.data } returns flowOf(mockPreferences)
        
        // When
        val result = themePreferences.getThemeMode().first()
        
        // Then
        assertEquals(ThemeMode.getDefault(), result)
    }

    @Test
    fun `getThemeMode 保存的模式ID无效时应该返回默认模式`() = runTest {
        // Given - 无效的模式ID
        every { mockPreferences[themeModeKey] } returns "invalid_mode_id"
        every { mockDataStore.data } returns flowOf(mockPreferences)
        
        // When
        val result = themePreferences.getThemeMode().first()
        
        // Then
        assertEquals(ThemeMode.getDefault(), result)
    }

    @Test
    fun `getThemeMode 读取失败时应该返回默认模式`() = runTest {
        // Given - 模拟读取失败
        every { mockDataStore.data } throws RuntimeException("Read failed")
        
        // When
        val result = themePreferences.getThemeMode().first()
        
        // Then
        assertEquals(ThemeMode.getDefault(), result)
    }

    @Test
    fun `验证偏好设置键的正确性`() {
        // Given & When & Then
        assertEquals("selected_theme", themeKey.name)
        assertEquals("theme_mode", themeModeKey.name)
    }

    @Test
    fun `所有主题ID都应该能正确映射`() = runTest {
        // Given
        val allThemes = AppTheme.getAllThemes()
        
        // When & Then - 测试每个主题ID都能正确映射
        allThemes.forEach { theme ->
            every { mockPreferences[themeKey] } returns theme.id
            every { mockDataStore.data } returns flowOf(mockPreferences)
            
            val result = themePreferences.getTheme().first()
            assertEquals("主题 ${theme.displayName} 应该能正确映射", theme, result)
        }
    }

    @Test
    fun `所有主题模式ID都应该能正确映射`() = runTest {
        // Given
        val allModes = ThemeMode.values()
        
        // When & Then - 测试每个模式ID都能正确映射
        allModes.forEach { mode ->
            every { mockPreferences[themeModeKey] } returns mode.id
            every { mockDataStore.data } returns flowOf(mockPreferences)
            
            val result = themePreferences.getThemeMode().first()
            assertEquals("主题模式 ${mode.displayName} 应该能正确映射", mode, result)
        }
    }

    /**
     * 注意：由于 DataStore 的 edit 操作涉及复杂的类型系统，
     * 保存操作的测试在这里简化处理。完整的保存测试应该在 androidTest 中进行。
     */
}