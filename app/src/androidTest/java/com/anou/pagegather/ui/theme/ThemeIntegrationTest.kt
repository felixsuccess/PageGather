package com.anou.pagegather.ui.theme

import androidx.compose.runtime.collectAsState
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.anou.pagegather.ui.feature.my.settings.ThemeSelectionScreen
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class ThemeIntegrationTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    // Mock flows for testing
    private val currentThemeFlow = MutableStateFlow(AppTheme.getDefault())
    private val themeModeFlow = MutableStateFlow(ThemeMode.getDefault())
    private val isDarkModeFlow = MutableStateFlow(false)
    private val useDynamicColorFlow = MutableStateFlow(false)

    @Test
    fun themeIntegration_themeSwitch() {
        // Given
        composeTestRule.setContent {
            PageGatherTheme(
                theme = currentThemeFlow.collectAsState().value,
                darkTheme = isDarkModeFlow.collectAsState().value
            ) {
                ThemeSelectionScreen(
                    onNavigateBack = { },
                    onNavigateToCustomThemeCreation = { }
                )
            }
        }

        // When - 切换到蓝色主题
        composeTestRule
            .onNodeWithText(AppTheme.HUNDI_BLUE.displayName)
            .performClick()

        // Then - 验证主题已切换
        assert(currentThemeFlow.value == AppTheme.HUNDI_BLUE) {
            "主题应该切换到蓝色"
        }
        
        // 验证UI显示更新
        composeTestRule
            .onNodeWithText("当前主题：${AppTheme.HUNDI_BLUE.displayName}")
            .assertIsDisplayed()
    }

    @Test
    fun themeIntegration_darkModeToggle() {
        // Given
        composeTestRule.setContent {
            PageGatherTheme(
                theme = currentThemeFlow.collectAsState().value,
                darkTheme = isDarkModeFlow.collectAsState().value
            ) {
                ThemeSelectionScreen(
                    onNavigateBack = { },
                    onNavigateToCustomThemeCreation = { }
                )
            }
        }

        // When - 切换到暗色模式
        composeTestRule
            .onNodeWithText("暗色模式")
            .performClick()

        // Then - 验证暗色模式已启用
        assert(themeModeFlow.value == ThemeMode.DARK) {
            "主题模式应该切换到暗色"
        }
        
        assert(isDarkModeFlow.value) {
            "暗色模式应该被启用"
        }
        
        // 验证UI显示更新
        composeTestRule
            .onNodeWithText("当前模式：暗色")
            .assertIsDisplayed()
    }

    @Test
    fun themeIntegration_lightModeToggle() {
        // Given - 先设置为暗色模式
        themeModeFlow.value = ThemeMode.DARK
        isDarkModeFlow.value = true
        
        composeTestRule.setContent {
            PageGatherTheme(
                theme = currentThemeFlow.collectAsState().value,
                darkTheme = isDarkModeFlow.collectAsState().value
            ) {
                ThemeSelectionScreen(
                    onNavigateBack = { },
                    onNavigateToCustomThemeCreation = { }
                )
            }
        }

        // When - 切换到亮色模式
        composeTestRule
            .onNodeWithText("亮色模式")
            .performClick()

        // Then - 验证亮色模式已启用
        assert(themeModeFlow.value == ThemeMode.LIGHT) {
            "主题模式应该切换到亮色"
        }
        
        assert(!isDarkModeFlow.value) {
            "暗色模式应该被禁用"
        }
        
        // 验证UI显示更新
        composeTestRule
            .onNodeWithText("当前模式：亮色")
            .assertIsDisplayed()
    }

    @Test
    fun themeIntegration_systemModeToggle() {
        // Given
        composeTestRule.setContent {
            PageGatherTheme(
                theme = currentThemeFlow.collectAsState().value,
                darkTheme = isDarkModeFlow.collectAsState().value
            ) {
                ThemeSelectionScreen(
                    onNavigateBack = { },
                    onNavigateToCustomThemeCreation = { }
                )
            }
        }

        // When - 切换到跟随系统模式
        composeTestRule
            .onNodeWithText("跟随系统")
            .performClick()

        // Then - 验证系统模式已启用
        assert(themeModeFlow.value == ThemeMode.SYSTEM) {
            "主题模式应该切换到跟随系统"
        }
    }

    @Test
    fun themeIntegration_multipleThemeSwitches() {
        // Given
        composeTestRule.setContent {
            PageGatherTheme(
                theme = currentThemeFlow.collectAsState().value,
                darkTheme = isDarkModeFlow.collectAsState().value
            ) {
                ThemeSelectionScreen(
                    onNavigateBack = { },
                    onNavigateToCustomThemeCreation = { }
                )
            }
        }

        // When - 连续切换多个主题
        val themes = listOf(
            AppTheme.HUNDI_BLUE,
            AppTheme.HUNDI_GREEN,
            AppTheme.HUNDI_PURPLE,
            AppTheme.ELEGANT_WHITE
        )

        themes.forEach { theme ->
            composeTestRule
                .onNodeWithText(theme.displayName)
                .performClick()
            
            // 验证每次切换都成功
            assert(currentThemeFlow.value == theme) {
                "主题应该切换到 ${theme.displayName}"
            }
        }
    }

    @Test
    fun themeIntegration_themeAndModeCombo() {
        // Given
        composeTestRule.setContent {
            PageGatherTheme(
                theme = currentThemeFlow.collectAsState().value,
                darkTheme = isDarkModeFlow.collectAsState().value
            ) {
                ThemeSelectionScreen(
                    onNavigateBack = { },
                    onNavigateToCustomThemeCreation = { }
                )
            }
        }

        // When - 同时切换主题和模式
        // 1. 切换到绿色主题
        composeTestRule
            .onNodeWithText(AppTheme.HUNDI_GREEN.displayName)
            .performClick()
        
        // 2. 切换到暗色模式
        composeTestRule
            .onNodeWithText("暗色模式")
            .performClick()

        // Then - 验证两个设置都生效
        assert(currentThemeFlow.value == AppTheme.HUNDI_GREEN) {
            "主题应该是绿色"
        }
        
        assert(themeModeFlow.value == ThemeMode.DARK) {
            "模式应该是暗色"
        }
        
        assert(isDarkModeFlow.value) {
            "暗色模式应该被启用"
        }
    }

    @Test
    fun themeIntegration_persistenceSimulation() {
        // Given - 模拟应用重启后的状态恢复
        val savedTheme = AppTheme.HUNDI_ORANGE
        val savedMode = ThemeMode.DARK
        val savedIsDark = true
        
        // 模拟从存储中加载状态
        currentThemeFlow.value = savedTheme
        themeModeFlow.value = savedMode
        isDarkModeFlow.value = savedIsDark
        
        composeTestRule.setContent {
            PageGatherTheme(
                theme = currentThemeFlow.collectAsState().value,
                darkTheme = isDarkModeFlow.collectAsState().value
            ) {
                ThemeSelectionScreen(
                    onNavigateBack = { },
                    onNavigateToCustomThemeCreation = { }
                )
            }
        }

        // Then - 验证初始状态正确显示
        composeTestRule
            .onNodeWithText("当前主题：${savedTheme.displayName}")
            .assertIsDisplayed()
            
        composeTestRule
            .onNodeWithText("当前模式：暗色")
            .assertIsDisplayed()
    }

    @Test
    fun themeIntegration_dynamicColorToggle() {
        // Given
        composeTestRule.setContent {
            PageGatherTheme(
                theme = currentThemeFlow.collectAsState().value,
                darkTheme = isDarkModeFlow.collectAsState().value
            ) {
                ThemeSelectionScreen(
                    onNavigateBack = { },
                    onNavigateToCustomThemeCreation = { }
                )
            }
        }

        // When - 切换动态颜色开关
        composeTestRule
            .onNodeWithText("启用动态颜色")
            .assertIsDisplayed()

        // Note: 实际测试需要mock ViewModel来验证状态变化
    }

    @Test
    fun themeIntegration_customThemeNavigation() {
        // Given
        var navigateToCustomThemeCreationCalled = false
        
        composeTestRule.setContent {
            PageGatherTheme(
                theme = currentThemeFlow.collectAsState().value,
                darkTheme = isDarkModeFlow.collectAsState().value
            ) {
                ThemeSelectionScreen(
                    onNavigateBack = { },
                    onNavigateToCustomThemeCreation = { navigateToCustomThemeCreationCalled = true }
                )
            }
        }

        // When - 点击添加自定义主题按钮
        composeTestRule
            .onNodeWithContentDescription("添加自定义主题")
            .performClick()

        // Then - 验证导航回调被调用
        assert(navigateToCustomThemeCreationCalled) { 
            "应该调用自定义主题创建导航回调" 
        }
    }

    @Test
    fun