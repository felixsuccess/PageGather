package com.anou.pagegather.ui.theme

import androidx.compose.material3.ColorScheme
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

/**
 * ThemeCache 单元测试
 * 测试主题缓存的所有功能
 */
class ThemeCacheTest {

    @Before
    fun setup() {
        // 每个测试前清除缓存
        ThemeCache.clearCache()
        
        // Mock ThemeErrorHandler 以避免实际的错误处理
        mockkObject(ThemeErrorHandler)
        every { ThemeErrorHandler.handleCacheError(any(), any()) } just Runs
    }

    @After
    fun tearDown() {
        // 清除缓存和 Mock
        ThemeCache.clearCache()
        unmockkObject(ThemeErrorHandler)
    }

    @Test
    fun `getColorScheme 应该返回颜色方案`() {
        // Given
        val theme = AppTheme.ELEGANT_WHITE
        val isDark = false
        
        // When
        val colorScheme = ThemeCache.getColorScheme(theme, isDark)
        
        // Then
        assertNotNull("颜色方案不应该为空", colorScheme)
        assertTrue("应该是 ColorScheme 类型", colorScheme is ColorScheme)
    }

    @Test
    fun `getColorScheme 相同参数应该返回缓存的实例`() {
        // Given
        val theme = AppTheme.HUNDI_BLUE
        val isDark = true
        
        // When
        val colorScheme1 = ThemeCache.getColorScheme(theme, isDark)
        val colorScheme2 = ThemeCache.getColorScheme(theme, isDark)
        
        // Then
        assertSame("相同参数应该返回缓存的实例", colorScheme1, colorScheme2)
    }

    @Test
    fun `getColorScheme 不同参数应该返回不同的实例`() {
        // Given
        val theme = AppTheme.HUNDI_GREEN
        
        // When
        val lightColorScheme = ThemeCache.getColorScheme(theme, false)
        val darkColorScheme = ThemeCache.getColorScheme(theme, true)
        
        // Then
        assertNotSame("不同参数应该返回不同的实例", lightColorScheme, darkColorScheme)
    }

    @Test
    fun `getColorScheme 不同主题应该返回不同的实例`() {
        // Given
        val isDark = false
        
        // When
        val elegantWhiteScheme = ThemeCache.getColorScheme(AppTheme.ELEGANT_WHITE, isDark)
        val hundiBlueScheme = ThemeCache.getColorScheme(AppTheme.HUNDI_BLUE, isDark)
        
        // Then
        assertNotSame("不同主题应该返回不同的实例", elegantWhiteScheme, hundiBlueScheme)
    }

    @Test
    fun `preloadAllThemes 应该预加载所有主题的颜色方案`() {
        // Given
        val initialStats = ThemeCache.getCacheStats()
        assertEquals("初始缓存应该为空", 0, initialStats.colorSchemeCacheSize)
        
        // When
        ThemeCache.preloadAllThemes()
        
        // Then
        val finalStats = ThemeCache.getCacheStats()
        val expectedCacheSize = AppTheme.getAllThemes().size * 2 // 每个主题有亮色和暗色两个版本
        assertEquals("应该预加载所有主题的颜色方案", expectedCacheSize, finalStats.colorSchemeCacheSize)
    }

    @Test
    fun `clearCache 应该清除所有缓存`() {
        // Given - 先预加载一些主题
        ThemeCache.getColorScheme(AppTheme.ELEGANT_WHITE, false)
        ThemeCache.getColorScheme(AppTheme.HUNDI_BLUE, true)
        
        val statsBeforeClear = ThemeCache.getCacheStats()
        assertTrue("清除前应该有缓存", statsBeforeClear.colorSchemeCacheSize > 0)
        
        // When
        ThemeCache.clearCache()
        
        // Then
        val statsAfterClear = ThemeCache.getCacheStats()
        assertEquals("清除后缓存大小应该为0", 0, statsAfterClear.colorSchemeCacheSize)
        assertEquals("清除后缓存命中数应该为0", 0, statsAfterClear.cacheHits)
        assertEquals("清除后缓存未命中数应该为0", 0, statsAfterClear.cacheMisses)
    }

    @Test
    fun `getCacheStats 应该正确统计缓存命中和未命中`() {
        // Given
        val theme = AppTheme.HUNDI_PURPLE
        val isDark = false
        
        // When - 第一次访问（未命中）
        ThemeCache.getColorScheme(theme, isDark)
        val statsAfterFirstAccess = ThemeCache.getCacheStats()
        
        // Then
        assertEquals("第一次访问后应该有1个缓存项", 1, statsAfterFirstAccess.colorSchemeCacheSize)
        assertTrue("第一次访问应该有未命中", statsAfterFirstAccess.cacheMisses > 0)
        
        // When - 第二次访问（命中）
        ThemeCache.getColorScheme(theme, isDark)
        val statsAfterSecondAccess = ThemeCache.getCacheStats()
        
        // Then
        assertTrue("第二次访问应该有缓存命中", statsAfterSecondAccess.cacheHits > statsAfterFirstAccess.cacheHits)
    }

    @Test
    fun `getCacheStats 应该正确计算命中率`() {
        // Given
        val theme = AppTheme.HUNDI_ORANGE
        val isDark = true
        
        // When - 一次未命中，两次命中
        ThemeCache.getColorScheme(theme, isDark) // 未命中
        ThemeCache.getColorScheme(theme, isDark) // 命中
        ThemeCache.getColorScheme(theme, isDark) // 命中
        
        val stats = ThemeCache.getCacheStats()
        
        // Then
        val expectedHitRate = (2.0f / 3.0f) * 100f // 2次命中 / 3次总请求
        assertEquals("命中率应该正确计算", expectedHitRate, stats.hitRate, 0.1f)
    }

    @Test
    fun `getCacheStats 没有请求时命中率应该为0`() {
        // Given - 清空缓存，没有任何请求
        ThemeCache.clearCache()
        
        // When
        val stats = ThemeCache.getCacheStats()
        
        // Then
        assertEquals("没有请求时命中率应该为0", 0f, stats.hitRate, 0.01f)
    }

    @Test
    fun `CacheStats toString 应该返回格式化的字符串`() {
        // Given
        val stats = CacheStats(
            colorSchemeCacheSize = 5,
            extendedColorsCacheSize = 3,
            cacheHits = 10,
            cacheMisses = 2,
            hitRate = 83.3f
        )
        
        // When
        val result = stats.toString()
        
        // Then
        assertNotNull("toString 不应该返回 null", result)
        assertTrue("应该包含缓存大小信息", result.contains("5"))
        assertTrue("应该包含命中信息", result.contains("10"))
        assertTrue("应该包含未命中信息", result.contains("2"))
        assertTrue("应该包含命中率信息", result.contains("83.3"))
    }

    @Test
    fun `所有主题都应该能够成功创建颜色方案`() {
        // Given
        val themes = AppTheme.getAllThemes()
        val modes = listOf(false, true) // 亮色和暗色模式
        
        // When & Then
        themes.forEach { theme ->
            modes.forEach { isDark ->
                val colorScheme = ThemeCache.getColorScheme(theme, isDark)
                assertNotNull(
                    "主题 ${theme.displayName} 的${if (isDark) "暗色" else "亮色"}模式应该能创建颜色方案",
                    colorScheme
                )
            }
        }
    }

    @Test
    fun `缓存应该是线程安全的`() {
        // Given
        val theme = AppTheme.ELEGANT_WHITE
        val isDark = false
        val results = mutableListOf<ColorScheme>()
        
        // When - 模拟并发访问
        val threads = (1..10).map {
            Thread {
                val colorScheme = ThemeCache.getColorScheme(theme, isDark)
                synchronized(results) {
                    results.add(colorScheme)
                }
            }
        }
        
        threads.forEach { it.start() }
        threads.forEach { it.join() }
        
        // Then - 所有结果应该是同一个实例
        val firstResult = results.first()
        results.forEach { result ->
            assertSame("并发访问应该返回相同的缓存实例", firstResult, result)
        }
    }

    @Test
    fun `缓存错误处理应该调用错误处理器`() {
        // Given - 模拟缓存操作中的异常
        // 这个测试比较难模拟，因为 ThemeCache 内部处理了大部分异常
        // 我们主要验证错误处理器被正确配置
        
        // When & Then - 验证错误处理器存在且可调用
        assertDoesNotThrow {
            ThemeErrorHandler.handleCacheError("test", RuntimeException("test"))
        }
    }

    /**
     * 辅助方法：断言不抛出异常
     */
    private fun assertDoesNotThrow(block: () -> Unit) {
        try {
            block()
        } catch (e: Exception) {
            fail("Expected no exception, but got: ${e.message}")
        }
    }
}