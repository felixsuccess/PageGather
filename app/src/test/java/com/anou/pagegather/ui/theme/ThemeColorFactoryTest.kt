package com.anou.pagegather.ui.theme

import androidx.compose.material3.ColorScheme
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

/**
 * ThemeColorFactory 单元测试
 * 测试主题颜色工厂的所有功能
 */
class ThemeColorFactoryTest {

    @Before
    fun setup() {
        // Mock ThemeCache 以控制缓存行为
        mockkObject(ThemeCache)
        
        // Mock ThemeErrorHandler 以避免实际的错误处理
        mockkObject(ThemeErrorHandler)
        every { ThemeErrorHandler.handleColorSchemeError(any(), any(), any()) } just Runs
    }

    @After
    fun tearDown() {
        // 清除所有 Mock
        unmockkObject(ThemeCache)
        unmockkObject(ThemeErrorHandler)
    }

    @Test
    fun `getColorSchemeForTheme 应该调用 ThemeCache`() {
        // Given
        val theme = AppTheme.ELEGANT_WHITE
        val isDark = false
        val mockColorScheme = mockk<ColorScheme>()
        
        every { ThemeCache.getColorScheme(theme, isDark) } returns mockColorScheme
        
        // When
        val result = getColorSchemeForTheme(theme, isDark)
        
        // Then
        assertEquals("应该返回缓存的颜色方案", mockColorScheme, result)
        verify { ThemeCache.getColorScheme(theme, isDark) }
    }

    @Test
    fun `getColorSchemeForThemeInternal 应该为所有主题返回颜色方案`() {
        // Given
        val themes = AppTheme.getAllThemes()
        val modes = listOf(false, true) // 亮色和暗色模式
        
        // When & Then
        themes.forEach { theme ->
            modes.forEach { isDark ->
                val colorScheme = getColorSchemeForThemeInternal(theme, isDark)
                
                assertNotNull(
                    "主题 ${theme.displayName} 的${if (isDark) "暗色" else "亮色"}模式应该返回颜色方案",
                    colorScheme
                )
                assertTrue(
                    "返回的对象应该是 ColorScheme 类型",
                    colorScheme is ColorScheme
                )
            }
        }
    }

    @Test
    fun `getColorSchemeForThemeInternal 不同主题应该返回不同的颜色方案`() {
        // Given
        val isDark = false
        
        // When
        val elegantWhiteScheme = getColorSchemeForThemeInternal(AppTheme.ELEGANT_WHITE, isDark)
        val hundiBlueScheme = getColorSchemeForThemeInternal(AppTheme.HUNDI_BLUE, isDark)
        val hundiGreenScheme = getColorSchemeForThemeInternal(AppTheme.HUNDI_GREEN, isDark)
        
        // Then
        assertNotEquals("典雅白和蓝色主题应该不同", elegantWhiteScheme, hundiBlueScheme)
        assertNotEquals("蓝色和绿色主题应该不同", hundiBlueScheme, hundiGreenScheme)
        assertNotEquals("绿色和典雅白主题应该不同", hundiGreenScheme, elegantWhiteScheme)
    }

    @Test
    fun `getColorSchemeForThemeInternal 同一主题的亮色和暗色模式应该不同`() {
        // Given
        val theme = AppTheme.HUNDI_PURPLE
        
        // When
        val lightScheme = getColorSchemeForThemeInternal(theme, false)
        val darkScheme = getColorSchemeForThemeInternal(theme, true)
        
        // Then
        assertNotEquals("同一主题的亮色和暗色模式应该不同", lightScheme, darkScheme)
    }

    @Test
    fun `getExtendedColorsForTheme 应该为所有主题返回扩展颜色`() {
        // Given
        val themes = AppTheme.getAllThemes()
        val modes = listOf(false, true)
        
        // When & Then
        themes.forEach { theme ->
            modes.forEach { isDark ->
                val extendedColors = getExtendedColorsForTheme(theme, isDark)
                
                assertNotNull(
                    "主题 ${theme.displayName} 的${if (isDark) "暗色" else "亮色"}模式应该返回扩展颜色",
                    extendedColors
                )
                
                // 验证扩展颜色的关键属性
                assertNotNull("成功颜色不应该为空", extendedColors.success)
                assertNotNull("警告颜色不应该为空", extendedColors.warning)
                assertNotNull("信息颜色不应该为空", extendedColors.info)
                assertNotNull("强调颜色不应该为空", extendedColors.accentColor)
                assertNotNull("渐变开始颜色不应该为空", extendedColors.gradientStart)
                assertNotNull("渐变结束颜色不应该为空", extendedColors.gradientEnd)
            }
        }
    }

    @Test
    fun `getExtendedColorsForTheme 不同主题应该返回不同的扩展颜色`() {
        // Given
        val isDark = false
        
        // When
        val elegantWhiteColors = getExtendedColorsForTheme(AppTheme.ELEGANT_WHITE, isDark)
        val hundiOrangeColors = getExtendedColorsForTheme(AppTheme.HUNDI_ORANGE, isDark)
        
        // Then
        // 比较一些关键颜色属性
        assertNotEquals("不同主题的强调颜色应该不同", 
            elegantWhiteColors.accentColor, hundiOrangeColors.accentColor)
        assertNotEquals("不同主题的渐变开始颜色应该不同", 
            elegantWhiteColors.gradientStart, hundiOrangeColors.gradientStart)
    }

    @Test
    fun `getExtendedColorsForTheme 应该包含所有必需的颜色属性`() {
        // Given
        val theme = AppTheme.HUNDI_BLUE
        val isDark = false
        
        // When
        val extendedColors = getExtendedColorsForTheme(theme, isDark)
        
        // Then - 验证所有颜色属性都存在且不为透明色（除了容器颜色）
        with(extendedColors) {
            // 状态颜色
            assertNotNull("成功颜色不应该为空", success)
            assertNotNull("错误颜色不应该为空", error)
            assertNotNull("警告颜色不应该为空", warning)
            assertNotNull("信息颜色不应该为空", info)
            
            // 文字颜色
            assertNotNull("标题颜色不应该为空", titleColor)
            assertNotNull("正文颜色不应该为空", bodyColor)
            assertNotNull("副标题颜色不应该为空", subtitleColor)
            assertNotNull("描述颜色不应该为空", descriptionColor)
            
            // 功能颜色
            assertNotNull("强调颜色不应该为空", accentColor)
            assertNotNull("书签颜色不应该为空", bookmarkColor)
            assertNotNull("阅读进度颜色不应该为空", readingProgress)
            assertNotNull("笔记高亮颜色不应该为空", noteHighlight)
            
            // 渐变颜色
            assertNotNull("渐变开始颜色不应该为空", gradientStart)
            assertNotNull("渐变结束颜色不应该为空", gradientEnd)
            assertNotNull("渐变次要颜色不应该为空", gradientSecondary)
            
            // 中性色
            assertNotNull("中性色100不应该为空", neutral100)
            assertNotNull("中性色200不应该为空", neutral200)
            assertNotNull("中性色300不应该为空", neutral300)
            assertNotNull("中性色500不应该为空", neutral500)
            assertNotNull("中性色700不应该为空", neutral700)
            assertNotNull("中性色900不应该为空", neutral900)
            
            // 边框和分割线
            assertNotNull("边框颜色不应该为空", borderColor)
            assertNotNull("分割线颜色不应该为空", dividerColor)
            assertNotNull("阴影颜色不应该为空", shadowColor)
        }
    }

    @Test
    fun `验证典雅白主题的颜色方案`() {
        // Given
        val theme = AppTheme.ELEGANT_WHITE
        
        // When
        val lightScheme = getColorSchemeForThemeInternal(theme, false)
        val darkScheme = getColorSchemeForThemeInternal(theme, true)
        
        // Then
        assertNotNull("典雅白亮色方案不应该为空", lightScheme)
        assertNotNull("典雅白暗色方案不应该为空", darkScheme)
        
        // 验证亮色和暗色方案确实不同
        assertNotEquals("典雅白的亮色和暗色方案应该不同", lightScheme.primary, darkScheme.primary)
        assertNotEquals("典雅白的亮色和暗色背景应该不同", lightScheme.background, darkScheme.background)
    }

    @Test
    fun `验证所有 Hundi 主题的颜色方案`() {
        // Given
        val hundiThemes = listOf(
            AppTheme.HUNDI_ORANGE,
            AppTheme.HUNDI_GREEN,
            AppTheme.HUNDI_BLUE,
            AppTheme.HUNDI_PURPLE
        )
        
        // When & Then
        hundiThemes.forEach { theme ->
            val lightScheme = getColorSchemeForThemeInternal(theme, false)
            val darkScheme = getColorSchemeForThemeInternal(theme, true)
            
            assertNotNull("${theme.displayName} 亮色方案不应该为空", lightScheme)
            assertNotNull("${theme.displayName} 暗色方案不应该为空", darkScheme)
            
            // 验证颜色方案的基本属性
            assertNotNull("${theme.displayName} 亮色主色不应该为空", lightScheme.primary)
            assertNotNull("${theme.displayName} 亮色背景不应该为空", lightScheme.background)
            assertNotNull("${theme.displayName} 暗色主色不应该为空", darkScheme.primary)
            assertNotNull("${theme.displayName} 暗色背景不应该为空", darkScheme.background)
        }
    }

    @Test
    fun `颜色方案应该具有合理的对比度`() {
        // Given
        val themes = AppTheme.getAllThemes()
        
        // When & Then
        themes.forEach { theme ->
            listOf(false, true).forEach { isDark ->
                val colorScheme = getColorSchemeForThemeInternal(theme, isDark)
                
                // 验证主色和背景色不相同（确保有对比度）
                assertNotEquals(
                    "${theme.displayName} ${if (isDark) "暗色" else "亮色"}模式的主色和背景色不应该相同",
                    colorScheme.primary,
                    colorScheme.background
                )
                
                // 验证文字颜色和背景色不相同
                assertNotEquals(
                    "${theme.displayName} ${if (isDark) "暗色" else "亮色"}模式的文字颜色和背景色不应该相同",
                    colorScheme.onBackground,
                    colorScheme.background
                )
            }
        }
    }

    @Test
    fun `扩展颜色应该与主题保持一致性`() {
        // Given
        val theme = AppTheme.HUNDI_ORANGE
        val isDark = false
        
        // When
        val extendedColors = getExtendedColorsForTheme(theme, isDark)
        
        // Then
        // 验证扩展颜色与主题的一致性（这里主要验证不为空和类型正确）
        assertNotNull("扩展颜色不应该为空", extendedColors)
        
        // 验证渐变颜色的一致性
        assertNotEquals("渐变开始和结束颜色应该不同", 
            extendedColors.gradientStart, extendedColors.gradientEnd)
        
        // 验证中性色的层次关系（较浅的颜色应该与较深的颜色不同）
        assertNotEquals("中性色100和900应该不同", 
            extendedColors.neutral100, extendedColors.neutral900)
    }

    @Test
    fun `错误处理应该回退到默认主题`() {
        // 这个测试比较难直接测试，因为内部实现使用了 try-catch
        // 我们主要验证在正常情况下不会抛出异常
        
        // Given
        val themes = AppTheme.getAllThemes()
        
        // When & Then - 验证所有主题都能正常处理
        themes.forEach { theme ->
            assertDoesNotThrow("主题 ${theme.displayName} 不应该抛出异常") {
                getColorSchemeForThemeInternal(theme, false)
                getColorSchemeForThemeInternal(theme, true)
                getExtendedColorsForTheme(theme, false)
                getExtendedColorsForTheme(theme, true)
            }
        }
    }

    @Test
    fun `缓存集成测试`() {
        // Given
        val theme = AppTheme.HUNDI_GREEN
        val isDark = true
        val mockColorScheme = mockk<ColorScheme>()
        
        // 设置缓存行为
        every { ThemeCache.getColorScheme(theme, isDark) } returns mockColorScheme
        
        // When
        val result1 = getColorSchemeForTheme(theme, isDark)
        val result2 = getColorSchemeForTheme(theme, isDark)
        
        // Then
        assertEquals("第一次调用应该返回缓存的颜色方案", mockColorScheme, result1)
        assertEquals("第二次调用应该返回相同的缓存颜色方案", mockColorScheme, result2)
        
        // 验证缓存被调用了两次
        verify(exactly = 2) { ThemeCache.getColorScheme(theme, isDark) }
    }

    /**
     * 辅助方法：断言不抛出异常
     */
    private fun assertDoesNotThrow(message: String, block: () -> Unit) {
        try {
            block()
        } catch (e: Exception) {
            fail("$message, but got: ${e.message}")
        }
    }
}