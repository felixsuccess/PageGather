package com.anou.pagegather.ui.theme

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import com.anou.pagegather.data.preferences.ThemePreferences
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import kotlinx.coroutines.Dispatchers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import com.anou.pagegather.domain.theme.ThemeManager

/**
 * ThemeManager 单元测试
 * 测试主题管理器的所有核心功能
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ThemeManagerTest {

    // Mock 对象
    private lateinit var mockContext: Context
    private lateinit var mockThemePreferences: ThemePreferences
    private lateinit var mockResources: Resources
    private lateinit var mockConfiguration: Configuration
    
    // 测试对象
    private lateinit var themeManager: ThemeManager
    
    // 测试协程调度器
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        // 设置测试协程调度器
        Dispatchers.setMain(testDispatcher)
        
        // 创建 Mock 对象
        mockContext = mockk()
        mockThemePreferences = mockk()
        mockResources = mockk()
        mockConfiguration = mockk()
        
        // 设置 Mock 行为
        every { mockContext.resources } returns mockResources
        every { mockResources.configuration } returns mockConfiguration
        
        // 默认设置为亮色模式
        every { mockConfiguration.uiMode and Configuration.UI_MODE_NIGHT_MASK } returns Configuration.UI_MODE_NIGHT_NO
        
        // 设置默认的 ThemePreferences 行为
        every { mockThemePreferences.getTheme() } returns flowOf(AppTheme.ELEGANT_WHITE)
        every { mockThemePreferences.getThemeMode() } returns flowOf(ThemeMode.SYSTEM)
        coEvery { mockThemePreferences.saveTheme(any()) } just Runs
        coEvery { mockThemePreferences.saveThemeMode(any()) } just Runs
        
        // 创建 ThemeManager 实例
        themeManager = ThemeManager(mockThemePreferences, mockContext)
    }

    @After
    fun tearDown() {
        // 重置主协程调度器
        Dispatchers.resetMain()
        
        // 清除所有 Mock
        clearAllMocks()
    }

    @Test
    fun `初始化时应该加载默认主题`() = runTest {
        // Given - setup() 中已经设置了默认行为
        
        // When - ThemeManager 已在 setup() 中初始化
        
        // Then
        assertEquals(AppTheme.ELEGANT_WHITE, themeManager.currentTheme.value)
        assertEquals(ThemeMode.SYSTEM, themeManager.themeMode.value)
        assertFalse(themeManager.isDarkMode.value) // 系统为亮色模式
    }

    @Test
    fun `setTheme 应该更新当前主题并保存到偏好设置`() = runTest {
        // Given
        val newTheme = AppTheme.HUNDI_BLUE
        
        // When
        themeManager.setTheme(newTheme)
        
        // Then
        assertEquals(newTheme, themeManager.currentTheme.value)
        coVerify { mockThemePreferences.saveTheme(newTheme) }
    }

    @Test
    fun `setTheme 保存失败时应该处理错误但不崩溃`() = runTest {
        // Given
        val newTheme = AppTheme.HUNDI_GREEN
        coEvery { mockThemePreferences.saveTheme(any()) } throws RuntimeException("Save failed")
        
        // When
        themeManager.setTheme(newTheme)
        
        // Then - 应该回退到默认主题
        assertEquals(AppTheme.ELEGANT_WHITE, themeManager.currentTheme.value)
    }

    @Test
    fun `setThemeMode 应该更新主题模式并保存到偏好设置`() = runTest {
        // Given
        val newMode = ThemeMode.DARK
        
        // When
        themeManager.setThemeMode(newMode)
        
        // Then
        assertEquals(newMode, themeManager.themeMode.value)
        assertTrue(themeManager.isDarkMode.value) // 暗色模式应该为 true
        coVerify { mockThemePreferences.saveThemeMode(newMode) }
    }

    @Test
    fun `setThemeMode 为 LIGHT 时应该设置 isDarkMode 为 false`() = runTest {
        // Given
        val lightMode = ThemeMode.LIGHT
        
        // When
        themeManager.setThemeMode(lightMode)
        
        // Then
        assertEquals(lightMode, themeManager.themeMode.value)
        assertFalse(themeManager.isDarkMode.value)
    }

    @Test
    fun `setThemeMode 为 SYSTEM 时应该跟随系统设置`() = runTest {
        // Given - 系统设置为暗色模式
        every { mockConfiguration.uiMode and Configuration.UI_MODE_NIGHT_MASK } returns Configuration.UI_MODE_NIGHT_YES
        val systemMode = ThemeMode.SYSTEM
        
        // When
        themeManager.setThemeMode(systemMode)
        
        // Then
        assertEquals(systemMode, themeManager.themeMode.value)
        assertTrue(themeManager.isDarkMode.value) // 应该跟随系统暗色模式
    }

    @Test
    fun `isSystemInDarkTheme 应该正确检测系统暗色模式`() {
        // Given - 设置系统为暗色模式
        every { mockConfiguration.uiMode and Configuration.UI_MODE_NIGHT_MASK } returns Configuration.UI_MODE_NIGHT_YES
        
        // When
        val result = themeManager.isSystemInDarkTheme()
        
        // Then
        assertTrue(result)
    }

    @Test
    fun `isSystemInDarkTheme 应该正确检测系统亮色模式`() {
        // Given - 设置系统为亮色模式
        every { mockConfiguration.uiMode and Configuration.UI_MODE_NIGHT_MASK } returns Configuration.UI_MODE_NIGHT_NO
        
        // When
        val result = themeManager.isSystemInDarkTheme()
        
        // Then
        assertFalse(result)
    }

    @Test
    fun `onConfigurationChanged 在 SYSTEM 模式下应该更新暗色模式状态`() = runTest {
        // Given - 设置为跟随系统模式
        themeManager.setThemeMode(ThemeMode.SYSTEM)
        
        // 初始为亮色模式
        every { mockConfiguration.uiMode and Configuration.UI_MODE_NIGHT_MASK } returns Configuration.UI_MODE_NIGHT_NO
        
        // When - 系统切换到暗色模式
        every { mockConfiguration.uiMode and Configuration.UI_MODE_NIGHT_MASK } returns Configuration.UI_MODE_NIGHT_YES
        themeManager.onConfigurationChanged()
        
        // Then
        assertTrue(themeManager.isDarkMode.value)
    }

    @Test
    fun `onConfigurationChanged 在非 SYSTEM 模式下不应该改变暗色模式状态`() = runTest {
        // Given - 设置为亮色模式
        themeManager.setThemeMode(ThemeMode.LIGHT)
        assertFalse(themeManager.isDarkMode.value)
        
        // When - 系统切换到暗色模式
        every { mockConfiguration.uiMode and Configuration.UI_MODE_NIGHT_MASK } returns Configuration.UI_MODE_NIGHT_YES
        themeManager.onConfigurationChanged()
        
        // Then - 应该保持亮色模式
        assertFalse(themeManager.isDarkMode.value)
    }

    @Test
    fun `getCacheStats 应该返回缓存统计信息`() {
        // When
        val stats = themeManager.getCacheStats()
        
        // Then
        assertNotNull(stats)
        assertTrue(stats.colorSchemeCacheSize >= 0)
        assertTrue(stats.cacheHits >= 0)
        assertTrue(stats.cacheMisses >= 0)
        assertTrue(stats.hitRate >= 0f)
    }

    @Test
    fun `getErrorStats 应该返回错误统计信息`() {
        // When
        val stats = themeManager.getErrorStats()
        
        // Then
        assertNotNull(stats)
        assertTrue(stats.totalErrors >= 0)
        assertTrue(stats.recentErrors >= 0)
    }

    @Test
    fun `getSystemHealth 应该返回系统健康状态`() {
        // When
        val health = themeManager.getSystemHealth()
        
        // Then
        assertNotNull(health)
        assertTrue(health in ThemeSystemHealth.values())
    }

    @Test
    fun `clearErrorHistory 应该清除错误历史`() {
        // When
        themeManager.clearErrorHistory()
        
        // Then - 不应该抛出异常
        val stats = themeManager.getErrorStats()
        assertNotNull(stats)
    }

    @Test
    fun `从偏好设置加载主题失败时应该使用默认主题`() = runTest {
        // Given - 模拟加载失败
        every { mockThemePreferences.getTheme() } returns flowOf(AppTheme.ELEGANT_WHITE)
            .also { 
                // 在第一次调用后抛出异常
                every { mockThemePreferences.getTheme() } throws RuntimeException("Load failed")
            }
        
        // When - 创建新的 ThemeManager 实例
        val newThemeManager = ThemeManager(mockThemePreferences, mockContext)
        
        // Then - 应该使用默认主题
        assertEquals(AppTheme.ELEGANT_WHITE, newThemeManager.currentTheme.value)
    }

    @Test
    fun `从偏好设置加载主题模式失败时应该使用默认模式`() = runTest {
        // Given - 模拟加载失败
        every { mockThemePreferences.getThemeMode() } returns flowOf(ThemeMode.SYSTEM)
            .also {
                // 在第一次调用后抛出异常
                every { mockThemePreferences.getThemeMode() } throws RuntimeException("Load failed")
            }
        
        // When - 创建新的 ThemeManager 实例
        val newThemeManager = ThemeManager(mockThemePreferences, mockContext)
        
        // Then - 应该使用默认模式
        assertEquals(ThemeMode.SYSTEM, newThemeManager.themeMode.value)
    }
}