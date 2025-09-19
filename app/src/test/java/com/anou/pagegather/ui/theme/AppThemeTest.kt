package com.anou.pagegather.ui.theme

import androidx.compose.ui.graphics.Color
import org.junit.Test
import org.junit.Assert.*

/**
 * AppTheme 单元测试
 * 测试主题枚举的所有功能
 */
class AppThemeTest {

    @Test
    fun `所有主题应该有唯一的ID`() {
        // Given
        val themes = AppTheme.getAllThemes()
        
        // When
        val ids = themes.map { it.id }
        val uniqueIds = ids.toSet()
        
        // Then
        assertEquals("主题ID应该是唯一的", ids.size, uniqueIds.size)
    }

    @Test
    fun `所有主题应该有非空的显示名称`() {
        // Given
        val themes = AppTheme.getAllThemes()
        
        // When & Then
        themes.forEach { theme ->
            assertNotNull("主题 ${theme.id} 的显示名称不应该为空", theme.displayName)
            assertTrue("主题 ${theme.id} 的显示名称不应该为空字符串", theme.displayName.isNotBlank())
        }
    }

    @Test
    fun `所有主题应该有非空的描述`() {
        // Given
        val themes = AppTheme.getAllThemes()
        
        // When & Then
        themes.forEach { theme ->
            assertNotNull("主题 ${theme.id} 的描述不应该为空", theme.description)
            assertTrue("主题 ${theme.id} 的描述不应该为空字符串", theme.description.isNotBlank())
        }
    }

    @Test
    fun `所有主题应该有有效的主色调`() {
        // Given
        val themes = AppTheme.getAllThemes()
        
        // When & Then
        themes.forEach { theme ->
            assertNotNull("主题 ${theme.id} 的主色调不应该为空", theme.primaryColor)
            assertNotEquals("主题 ${theme.id} 的主色调不应该是透明色", Color.Transparent, theme.primaryColor)
        }
    }

    @Test
    fun `所有主题应该有非空的表情符号`() {
        // Given
        val themes = AppTheme.getAllThemes()
        
        // When & Then
        themes.forEach { theme ->
            assertNotNull("主题 ${theme.id} 的表情符号不应该为空", theme.emoji)
            assertTrue("主题 ${theme.id} 的表情符号不应该为空字符串", theme.emoji.isNotBlank())
        }
    }

    @Test
    fun `应该只有一个默认主题`() {
        // Given
        val themes = AppTheme.getAllThemes()
        
        // When
        val defaultThemes = themes.filter { it.isDefault }
        
        // Then
        assertEquals("应该只有一个默认主题", 1, defaultThemes.size)
        assertEquals("默认主题应该是典雅白", AppTheme.ELEGANT_WHITE, defaultThemes.first())
    }

    @Test
    fun `fromId 应该返回正确的主题`() {
        // Given
        val themes = AppTheme.getAllThemes()
        
        // When & Then
        themes.forEach { expectedTheme ->
            val actualTheme = AppTheme.fromId(expectedTheme.id)
            assertEquals("fromId 应该返回正确的主题", expectedTheme, actualTheme)
        }
    }

    @Test
    fun `fromId 使用无效ID时应该返回默认主题`() {
        // Given
        val invalidIds = listOf("invalid_id", "", "non_existent_theme", "null")
        
        // When & Then
        invalidIds.forEach { invalidId ->
            val result = AppTheme.fromId(invalidId)
            assertEquals("无效ID '$invalidId' 应该返回默认主题", AppTheme.getDefault(), result)
        }
    }

    @Test
    fun `getDefault 应该返回默认主题`() {
        // When
        val defaultTheme = AppTheme.getDefault()
        
        // Then
        assertNotNull("默认主题不应该为空", defaultTheme)
        assertTrue("默认主题应该标记为默认", defaultTheme.isDefault)
        assertEquals("默认主题应该是典雅白", AppTheme.ELEGANT_WHITE, defaultTheme)
    }

    @Test
    fun `getAllThemes 应该返回所有主题`() {
        // When
        val allThemes = AppTheme.getAllThemes()
        
        // Then
        assertEquals("应该返回所有5个主题", 5, allThemes.size)
        
        // 验证包含所有预期的主题
        val expectedThemes = setOf(
            AppTheme.ELEGANT_WHITE,
            AppTheme.HUNDI_ORANGE,
            AppTheme.HUNDI_GREEN,
            AppTheme.HUNDI_BLUE,
            AppTheme.HUNDI_PURPLE
        )
        
        assertEquals("应该包含所有预期的主题", expectedThemes, allThemes.toSet())
    }

    @Test
    fun `验证具体主题的属性`() {
        // 典雅白主题
        with(AppTheme.ELEGANT_WHITE) {
            assertEquals("elegant_white", id)
            assertEquals("典雅白", displayName)
            assertEquals("🤍", emoji)
            assertTrue("典雅白应该是默认主题", isDefault)
        }
        
        // Hundi 橙色主题
        with(AppTheme.HUNDI_ORANGE) {
            assertEquals("hundi_orange", id)
            assertEquals("Hundi 橙色", displayName)
            assertEquals("🧡", emoji)
            assertFalse("Hundi 橙色不应该是默认主题", isDefault)
        }
        
        // Hundi 绿色主题
        with(AppTheme.HUNDI_GREEN) {
            assertEquals("hundi_green", id)
            assertEquals("Hundi 绿色", displayName)
            assertEquals("💚", emoji)
            assertFalse("Hundi 绿色不应该是默认主题", isDefault)
        }
        
        // Hundi 蓝色主题
        with(AppTheme.HUNDI_BLUE) {
            assertEquals("hundi_blue", id)
            assertEquals("Hundi 蓝色", displayName)
            assertEquals("💙", emoji)
            assertFalse("Hundi 蓝色不应该是默认主题", isDefault)
        }
        
        // Hundi 紫色主题
        with(AppTheme.HUNDI_PURPLE) {
            assertEquals("hundi_purple", id)
            assertEquals("Hundi 紫色", displayName)
            assertEquals("💜", emoji)
            assertFalse("Hundi 紫色不应该是默认主题", isDefault)
        }
    }

    @Test
    fun `主题颜色应该符合预期`() {
        // 验证主题颜色不是默认的黑色或白色
        val themes = AppTheme.getAllThemes()
        
        themes.forEach { theme ->
            assertNotEquals("主题 ${theme.id} 的主色调不应该是黑色", Color.Black, theme.primaryColor)
            assertNotEquals("主题 ${theme.id} 的主色调不应该是白色", Color.White, theme.primaryColor)
            assertNotEquals("主题 ${theme.id} 的主色调不应该是透明色", Color.Transparent, theme.primaryColor)
        }
    }
}

/**
 * ThemeMode 单元测试
 * 测试主题模式枚举的所有功能
 */
class ThemeModeTest {

    @Test
    fun `所有主题模式应该有唯一的ID`() {
        // Given
        val modes = ThemeMode.values()
        
        // When
        val ids = modes.map { it.id }
        val uniqueIds = ids.toSet()
        
        // Then
        assertEquals("主题模式ID应该是唯一的", ids.size, uniqueIds.size)
    }

    @Test
    fun `所有主题模式应该有非空的显示名称`() {
        // Given
        val modes = ThemeMode.values()
        
        // When & Then
        modes.forEach { mode ->
            assertNotNull("主题模式 ${mode.id} 的显示名称不应该为空", mode.displayName)
            assertTrue("主题模式 ${mode.id} 的显示名称不应该为空字符串", mode.displayName.isNotBlank())
        }
    }

    @Test
    fun `fromId 应该返回正确的主题模式`() {
        // Given
        val modes = ThemeMode.values()
        
        // When & Then
        modes.forEach { expectedMode ->
            val actualMode = ThemeMode.fromId(expectedMode.id)
            assertEquals("fromId 应该返回正确的主题模式", expectedMode, actualMode)
        }
    }

    @Test
    fun `fromId 使用无效ID时应该返回默认模式`() {
        // Given
        val invalidIds = listOf("invalid_id", "", "non_existent_mode", "null")
        
        // When & Then
        invalidIds.forEach { invalidId ->
            val result = ThemeMode.fromId(invalidId)
            assertEquals("无效ID '$invalidId' 应该返回默认模式", ThemeMode.getDefault(), result)
        }
    }

    @Test
    fun `getDefault 应该返回 SYSTEM 模式`() {
        // When
        val defaultMode = ThemeMode.getDefault()
        
        // Then
        assertEquals("默认模式应该是 SYSTEM", ThemeMode.SYSTEM, defaultMode)
    }

    @Test
    fun `验证具体主题模式的属性`() {
        // 亮色模式
        with(ThemeMode.LIGHT) {
            assertEquals("light", id)
            assertEquals("亮色模式", displayName)
        }
        
        // 暗色模式
        with(ThemeMode.DARK) {
            assertEquals("dark", id)
            assertEquals("暗色模式", displayName)
        }
        
        // 跟随系统模式
        with(ThemeMode.SYSTEM) {
            assertEquals("system", id)
            assertEquals("跟随系统", displayName)
        }
    }
}

/**
 * ThemeConfig 数据类测试
 */
class ThemeConfigTest {

    @Test
    fun `ThemeConfig 应该正确存储所有属性`() {
        // Given
        val theme = AppTheme.HUNDI_BLUE
        val mode = ThemeMode.DARK
        val isDarkMode = true
        
        // When
        val config = ThemeConfig(theme, mode, isDarkMode)
        
        // Then
        assertEquals(theme, config.theme)
        assertEquals(mode, config.mode)
        assertEquals(isDarkMode, config.isDarkMode)
    }

    @Test
    fun `ThemeConfig 相等性测试`() {
        // Given
        val config1 = ThemeConfig(AppTheme.ELEGANT_WHITE, ThemeMode.LIGHT, false)
        val config2 = ThemeConfig(AppTheme.ELEGANT_WHITE, ThemeMode.LIGHT, false)
        val config3 = ThemeConfig(AppTheme.HUNDI_BLUE, ThemeMode.LIGHT, false)
        
        // When & Then
        assertEquals("相同属性的 ThemeConfig 应该相等", config1, config2)
        assertNotEquals("不同属性的 ThemeConfig 不应该相等", config1, config3)
    }
}

/**
 * ThemeSelectionUiState 数据类测试
 */
class ThemeSelectionUiStateTest {

    @Test
    fun `ThemeSelectionUiState 默认值应该正确`() {
        // When
        val state = ThemeSelectionUiState()
        
        // Then
        assertEquals("默认可用主题应该是所有主题", AppTheme.getAllThemes(), state.availableThemes)
        assertEquals("默认当前主题应该是默认主题", AppTheme.getDefault(), state.currentTheme)
        assertEquals("默认当前模式应该是默认模式", ThemeMode.getDefault(), state.currentMode)
        assertFalse("默认不应该是暗色模式", state.isDarkMode)
        assertFalse("默认不应该是加载状态", state.isLoading)
    }

    @Test
    fun `ThemeSelectionUiState 自定义值应该正确设置`() {
        // Given
        val customThemes = listOf(AppTheme.HUNDI_BLUE, AppTheme.HUNDI_GREEN)
        val currentTheme = AppTheme.HUNDI_PURPLE
        val currentMode = ThemeMode.DARK
        val isDarkMode = true
        val isLoading = true
        
        // When
        val state = ThemeSelectionUiState(
            availableThemes = customThemes,
            currentTheme = currentTheme,
            currentMode = currentMode,
            isDarkMode = isDarkMode,
            isLoading = isLoading
        )
        
        // Then
        assertEquals(customThemes, state.availableThemes)
        assertEquals(currentTheme, state.currentTheme)
        assertEquals(currentMode, state.currentMode)
        assertEquals(isDarkMode, state.isDarkMode)
        assertEquals(isLoading, state.isLoading)
    }
}