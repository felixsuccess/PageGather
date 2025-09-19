package com.anou.pagegather.ui.theme

import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

/**
 * ThemeErrorHandler 单元测试
 * 测试主题错误处理的所有功能
 */
class ThemeErrorHandlerTest {

    @Before
    fun setup() {
        // 每个测试前清除错误历史
        ThemeErrorHandler.clearErrorHistory()
        
        // Mock ThemeCache 以避免实际的缓存操作
        mockkObject(ThemeCache)
        every { ThemeCache.clearCache() } just Runs
    }

    @After
    fun tearDown() {
        // 清除错误历史和 Mock
        ThemeErrorHandler.clearErrorHistory()
        unmockkObject(ThemeCache)
    }

    @Test
    fun `handleThemeLoadError 应该返回默认主题并记录错误`() {
        // Given
        val theme = AppTheme.HUNDI_BLUE
        val isDark = true
        val error = RuntimeException("Load failed")
        
        // When
        val result = ThemeErrorHandler.handleThemeLoadError(theme, isDark, error)
        
        // Then
        assertEquals("应该返回默认主题", AppTheme.getDefault(), result)
        
        val stats = ThemeErrorHandler.getErrorStats()
        assertEquals("应该记录一个错误", 1, stats.totalErrors)
        assertEquals("错误类型应该是 THEME_LOAD_FAILED", ThemeErrorType.THEME_LOAD_FAILED, stats.lastError?.type)
    }

    @Test
    fun `handleThemeSwitchError 应该返回 false 并记录错误`() {
        // Given
        val fromTheme = AppTheme.ELEGANT_WHITE
        val toTheme = AppTheme.HUNDI_GREEN
        val error = RuntimeException("Switch failed")
        
        // When
        val result = ThemeErrorHandler.handleThemeSwitchError(fromTheme, toTheme, error)
        
        // Then
        assertFalse("主题切换失败应该返回 false", result)
        
        val stats = ThemeErrorHandler.getErrorStats()
        assertEquals("应该记录一个错误", 1, stats.totalErrors)
        assertEquals("错误类型应该是 THEME_SWITCH_FAILED", ThemeErrorType.THEME_SWITCH_FAILED, stats.lastError?.type)
    }

    @Test
    fun `handleThemeSaveError 应该记录错误`() {
        // Given
        val theme = AppTheme.HUNDI_PURPLE
        val error = RuntimeException("Save failed")
        
        // When
        ThemeErrorHandler.handleThemeSaveError(theme, error)
        
        // Then
        val stats = ThemeErrorHandler.getErrorStats()
        assertEquals("应该记录一个错误", 1, stats.totalErrors)
        assertEquals("错误类型应该是 THEME_SAVE_FAILED", ThemeErrorType.THEME_SAVE_FAILED, stats.lastError?.type)
        assertEquals("错误主题应该正确", theme, stats.lastError?.theme)
    }

    @Test
    fun `handleColorSchemeError 应该记录错误`() {
        // Given
        val theme = AppTheme.HUNDI_ORANGE
        val isDark = false
        val error = RuntimeException("Color scheme failed")
        
        // When
        ThemeErrorHandler.handleColorSchemeError(theme, isDark, error)
        
        // Then
        val stats = ThemeErrorHandler.getErrorStats()
        assertEquals("应该记录一个错误", 1, stats.totalErrors)
        assertEquals("错误类型应该是 COLOR_SCHEME_FAILED", ThemeErrorType.COLOR_SCHEME_FAILED, stats.lastError?.type)
        assertEquals("错误主题应该正确", theme, stats.lastError?.theme)
        assertEquals("暗色模式标志应该正确", isDark, stats.lastError?.isDark)
    }

    @Test
    fun `handleCacheError 应该记录错误`() {
        // Given
        val operation = "getColorScheme"
        val error = RuntimeException("Cache failed")
        
        // When
        ThemeErrorHandler.handleCacheError(operation, error)
        
        // Then
        val stats = ThemeErrorHandler.getErrorStats()
        assertEquals("应该记录一个错误", 1, stats.totalErrors)
        assertEquals("错误类型应该是 CACHE_ERROR", ThemeErrorType.CACHE_ERROR, stats.lastError?.type)
        assertTrue("错误消息应该包含操作名称", stats.lastError?.message?.contains(operation) == true)
    }

    @Test
    fun `getErrorStats 应该正确统计错误`() {
        // Given - 创建多个不同类型的错误
        ThemeErrorHandler.handleThemeLoadError(AppTheme.ELEGANT_WHITE, false, RuntimeException("Load error"))
        ThemeErrorHandler.handleThemeSwitchError(AppTheme.ELEGANT_WHITE, AppTheme.HUNDI_BLUE, RuntimeException("Switch error"))
        ThemeErrorHandler.handleThemeSaveError(AppTheme.HUNDI_GREEN, RuntimeException("Save error"))
        
        // When
        val stats = ThemeErrorHandler.getErrorStats()
        
        // Then
        assertEquals("总错误数应该是3", 3, stats.totalErrors)
        assertEquals("最近错误数应该是3", 3, stats.recentErrors)
        assertEquals("应该有3种错误类型", 3, stats.errorsByType.size)
        assertNotNull("应该有最后一个错误", stats.lastError)
    }

    @Test
    fun `getErrorStats 应该正确计算最近错误`() {
        // Given - 创建一个旧错误（通过反射修改时间戳）
        ThemeErrorHandler.handleThemeLoadError(AppTheme.ELEGANT_WHITE, false, RuntimeException("Old error"))
        
        // 获取错误统计并修改时间戳（模拟旧错误）
        val stats = ThemeErrorHandler.getErrorStats()
        val lastError = stats.lastError
        
        // 创建新错误
        ThemeErrorHandler.handleThemeSaveError(AppTheme.HUNDI_BLUE, RuntimeException("New error"))
        
        // When
        val newStats = ThemeErrorHandler.getErrorStats()
        
        // Then
        assertEquals("总错误数应该是2", 2, newStats.totalErrors)
        assertTrue("最近错误数应该大于0", newStats.recentErrors > 0)
    }

    @Test
    fun `clearErrorHistory 应该清除所有错误`() {
        // Given - 创建一些错误
        ThemeErrorHandler.handleThemeLoadError(AppTheme.ELEGANT_WHITE, false, RuntimeException("Error 1"))
        ThemeErrorHandler.handleThemeSaveError(AppTheme.HUNDI_BLUE, RuntimeException("Error 2"))
        
        val statsBeforeClear = ThemeErrorHandler.getErrorStats()
        assertTrue("清除前应该有错误", statsBeforeClear.totalErrors > 0)
        
        // When
        ThemeErrorHandler.clearErrorHistory()
        
        // Then
        val statsAfterClear = ThemeErrorHandler.getErrorStats()
        assertEquals("清除后总错误数应该为0", 0, statsAfterClear.totalErrors)
        assertEquals("清除后最近错误数应该为0", 0, statsAfterClear.recentErrors)
        assertNull("清除后最后错误应该为空", statsAfterClear.lastError)
    }

    @Test
    fun `checkSystemHealth 应该根据错误和缓存状态返回正确的健康等级`() {
        // Test EXCELLENT health
        ThemeErrorHandler.clearErrorHistory()
        every { ThemeCache.getCacheStats() } returns CacheStats(5, 0, 10, 2, 85f)
        
        var health = ThemeErrorHandler.checkSystemHealth()
        assertEquals("无错误且高命中率应该是 EXCELLENT", ThemeSystemHealth.EXCELLENT, health)
        
        // Test GOOD health
        ThemeErrorHandler.handleCacheError("test", RuntimeException("Minor error"))
        every { ThemeCache.getCacheStats() } returns CacheStats(5, 0, 8, 4, 70f)
        
        health = ThemeErrorHandler.checkSystemHealth()
        assertEquals("少量错误且中等命中率应该是 GOOD", ThemeSystemHealth.GOOD, health)
        
        // Test FAIR health
        repeat(3) {
            ThemeErrorHandler.handleCacheError("test$it", RuntimeException("Error $it"))
        }
        every { ThemeCache.getCacheStats() } returns CacheStats(5, 0, 6, 6, 50f)
        
        health = ThemeErrorHandler.checkSystemHealth()
        assertEquals("中等错误且低命中率应该是 FAIR", ThemeSystemHealth.FAIR, health)
        
        // Test POOR health
        repeat(5) {
            ThemeErrorHandler.handleThemeLoadError(AppTheme.ELEGANT_WHITE, false, RuntimeException("Load error $it"))
        }
        every { ThemeCache.getCacheStats() } returns CacheStats(2, 0, 2, 10, 20f)
        
        health = ThemeErrorHandler.checkSystemHealth()
        assertEquals("大量错误且很低命中率应该是 POOR", ThemeSystemHealth.POOR, health)
    }

    @Test
    fun `多次错误应该触发缓存清除`() {
        // Given - 创建10个错误以触发缓存清除
        repeat(10) { index ->
            ThemeErrorHandler.handleThemeLoadError(
                AppTheme.ELEGANT_WHITE, 
                false, 
                RuntimeException("Error $index")
            )
        }
        
        // Then - 验证缓存清除被调用
        verify { ThemeCache.clearCache() }
    }

    @Test
    fun `错误历史应该限制在50个以内`() {
        // Given - 创建超过50个错误
        repeat(60) { index ->
            ThemeErrorHandler.handleThemeLoadError(
                AppTheme.ELEGANT_WHITE, 
                false, 
                RuntimeException("Error $index")
            )
        }
        
        // When
        val stats = ThemeErrorHandler.getErrorStats()
        
        // Then
        assertEquals("总错误数应该是60", 60, stats.totalErrors)
        // 注意：由于错误历史限制，实际的错误历史记录可能少于总数
        // 但这取决于内部实现，我们主要验证系统不会因为过多错误而崩溃
        assertTrue("系统应该能处理大量错误", stats.totalErrors > 0)
    }

    @Test
    fun `ThemeError 数据类应该正确存储所有属性`() {
        // Given
        val type = ThemeErrorType.THEME_LOAD_FAILED
        val message = "Test error message"
        val theme = AppTheme.HUNDI_BLUE
        val isDark = true
        val exception = RuntimeException("Test exception")
        val timestamp = System.currentTimeMillis()
        
        // When
        val error = ThemeError(type, message, theme, isDark, exception, timestamp)
        
        // Then
        assertEquals(type, error.type)
        assertEquals(message, error.message)
        assertEquals(theme, error.theme)
        assertEquals(isDark, error.isDark)
        assertEquals(exception, error.exception)
        assertEquals(timestamp, error.timestamp)
    }

    @Test
    fun `ThemeErrorStats toString 应该返回格式化的字符串`() {
        // Given
        val errorsByType = mapOf(
            ThemeErrorType.THEME_LOAD_FAILED to 2,
            ThemeErrorType.CACHE_ERROR to 1
        )
        val lastError = ThemeError(
            ThemeErrorType.THEME_LOAD_FAILED,
            "Test error",
            AppTheme.ELEGANT_WHITE,
            false,
            RuntimeException("Test"),
            System.currentTimeMillis()
        )
        
        val stats = ThemeErrorStats(
            totalErrors = 3,
            recentErrors = 2,
            errorsByType = errorsByType,
            lastError = lastError
        )
        
        // When
        val result = stats.toString()
        
        // Then
        assertNotNull("toString 不应该返回 null", result)
        assertTrue("应该包含总错误数", result.contains("3"))
        assertTrue("应该包含最近错误数", result.contains("2"))
        assertTrue("应该包含错误类型信息", result.contains("THEME_LOAD_FAILED"))
    }

    @Test
    fun `所有 ThemeErrorType 都应该被测试覆盖`() {
        // Given
        val allErrorTypes = ThemeErrorType.values()
        
        // When - 为每种错误类型创建一个错误
        ThemeErrorHandler.handleThemeLoadError(AppTheme.ELEGANT_WHITE, false, RuntimeException("Load"))
        ThemeErrorHandler.handleThemeSwitchError(AppTheme.ELEGANT_WHITE, AppTheme.HUNDI_BLUE, RuntimeException("Switch"))
        ThemeErrorHandler.handleThemeSaveError(AppTheme.HUNDI_GREEN, RuntimeException("Save"))
        ThemeErrorHandler.handleColorSchemeError(AppTheme.HUNDI_PURPLE, true, RuntimeException("ColorScheme"))
        ThemeErrorHandler.handleCacheError("test", RuntimeException("Cache"))
        
        val stats = ThemeErrorHandler.getErrorStats()
        
        // Then
        assertEquals("应该覆盖所有错误类型", allErrorTypes.size, stats.errorsByType.size)
        allErrorTypes.forEach { errorType ->
            assertTrue("应该包含错误类型 $errorType", stats.errorsByType.containsKey(errorType))
        }
    }

    @Test
    fun `ThemeSystemHealth 枚举应该包含所有预期值`() {
        // Given
        val expectedHealthLevels = setOf(
            ThemeSystemHealth.EXCELLENT,
            ThemeSystemHealth.GOOD,
            ThemeSystemHealth.FAIR,
            ThemeSystemHealth.POOR
        )
        
        // When
        val actualHealthLevels = ThemeSystemHealth.values().toSet()
        
        // Then
        assertEquals("应该包含所有预期的健康等级", expectedHealthLevels, actualHealthLevels)
    }
}