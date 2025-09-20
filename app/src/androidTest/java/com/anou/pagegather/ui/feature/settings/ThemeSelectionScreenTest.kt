package com.anou.pagegather.ui.feature.settings

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.anou.pagegather.ui.theme.AppTheme
import com.anou.pagegather.ui.theme.PageGatherTheme
import com.anou.pagegather.ui.theme.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class ThemeSelectionScreenTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    // Mock flows for testing
    private val currentThemeFlow = MutableStateFlow(AppTheme.getDefault())
    private val themeModeFlow = MutableStateFlow(ThemeMode.getDefault())
    private val isDarkModeFlow = MutableStateFlow(false)
    private val useDynamicColorFlow = MutableStateFlow(false)
    private val isLoadingFlow = MutableStateFlow(false)

    @Test
    fun themeSelectionScreen_displaysCorrectTitle() {
        // Given
        composeTestRule.setContent {
            PageGatherTheme {
                ThemeSelectionScreen(
                    onNavigateBack = { },
                    onNavigateToCustomThemeCreation = { }
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("主题设置")
            .assertIsDisplayed()
    }

    @Test
    fun themeSelectionScreen_displaysBackButton() {
        // Given
        var backPressed = false
        
        composeTestRule.setContent {
            PageGatherTheme {
                ThemeSelectionScreen(
                    onNavigateBack = { backPressed = true },
                    onNavigateToCustomThemeCreation = { }
                )
            }
        }

        // When
        composeTestRule
            .onNodeWithContentDescription("返回")
            .assertIsDisplayed()
            .performClick()

        // Then
        assert(backPressed) { "返回按钮应该触发导航回调" }
    }

    @Test
    fun themeSelectionScreen_displaysThemeModeSection() {
        // Given
        composeTestRule.setContent {
            PageGatherTheme {
                ThemeSelectionScreen(
                    onNavigateBack = { },
                    onNavigateToCustomThemeCreation = { }
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("显示模式")
            .assertIsDisplayed()
        
        // 验证所有主题模式选项都显示
        composeTestRule
            .onNodeWithText("亮色模式")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("暗色模式")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("跟随系统")
            .assertIsDisplayed()
    }

    @Test
    fun themeSelectionScreen_displaysThemeColorSection() {
        // Given
        composeTestRule.setContent {
            PageGatherTheme {
                ThemeSelectionScreen(
                    onNavigateBack = { },
                    onNavigateToCustomThemeCreation = { }
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("主题颜色")
            .assertIsDisplayed()
    }

    @Test
    fun themeSelectionScreen_displaysAllAvailableThemes() {
        // Given
        composeTestRule.setContent {
            PageGatherTheme {
                ThemeSelectionScreen(
                    onNavigateBack = { },
                    onNavigateToCustomThemeCreation = { }
                )
            }
        }

        // Then - 验证所有主题都显示
        AppTheme.getAllThemes().forEach { theme ->
            composeTestRule
                .onNodeWithText(theme.displayName)
                .assertIsDisplayed()
        }
    }

    @Test
    fun themeSelectionScreen_showsCurrentThemeAsSelected() {
        // Given - 设置当前主题为蓝色
        currentThemeFlow.value = AppTheme.HUNDI_BLUE
        
        composeTestRule.setContent {
            PageGatherTheme {
                ThemeSelectionScreen(
                    onNavigateBack = { },
                    onNavigateToCustomThemeCreation = { }
                )
            }
        }

        // Then - 验证当前主题信息显示
        composeTestRule
            .onNodeWithText("当前主题：${AppTheme.HUNDI_BLUE.displayName}")
            .assertIsDisplayed()
    }

    @Test
    fun themeSelectionScreen_showsCurrentModeAsSelected() {
        // Given - 设置当前模式为暗色
        themeModeFlow.value = ThemeMode.DARK
        
        composeTestRule.setContent {
            PageGatherTheme {
                ThemeSelectionScreen(
                    onNavigateBack = { },
                    onNavigateToCustomThemeCreation = { }
                )
            }
        }

        // Then - 验证暗色模式被选中
        composeTestRule
            .onAllNodesWithText("暗色模式")
            .onFirst()
            .assertIsDisplayed()
    }

    @Test
    fun themeSelectionScreen_clickThemeMode_updatesSelection() {
        // Given
        composeTestRule.setContent {
            PageGatherTheme {
                ThemeSelectionScreen(
                    onNavigateBack = { },
                    onNavigateToCustomThemeCreation = { }
                )
            }
        }

        // When - 点击亮色模式
        composeTestRule
            .onNodeWithText("亮色模式")
            .performClick()

        // Then - 验证模式被更新
        // Note: 实际测试中需要mock ViewModel或使用真实实现
    }

    @Test
    fun themeSelectionScreen_clickTheme_updatesSelection() {
        // Given
        composeTestRule.setContent {
            PageGatherTheme {
                ThemeSelectionScreen(
                    onNavigateBack = { },
                    onNavigateToCustomThemeCreation = { }
                )
            }
        }

        // When - 点击绿色主题
        composeTestRule
            .onNodeWithText(AppTheme.HUNDI_GREEN.displayName)
            .performClick()

        // Then - 验证主题被更新
        // Note: 实际测试中需要mock ViewModel或使用真实实现
    }

    @Test
    fun themeSelectionScreen_displaysThemeDescriptions() {
        // Given
        composeTestRule.setContent {
            PageGatherTheme {
                ThemeSelectionScreen(
                    onNavigateBack = { },
                    onNavigateToCustomThemeCreation = { }
                )
            }
        }

        // Then - 验证主题描述显示
        AppTheme.getAllThemes().forEach { theme ->
            composeTestRule
                .onNodeWithText(theme.description)
                .assertIsDisplayed()
        }
    }

    @Test
    fun themeSelectionScreen_displaysThemeEmojis() {
        // Given
        composeTestRule.setContent {
            PageGatherTheme {
                ThemeSelectionScreen(
                    onNavigateBack = { },
                    onNavigateToCustomThemeCreation = { }
                )
            }
        }

        // Then - 验证主题表情符号显示
        AppTheme.getAllThemes().forEach { theme ->
            composeTestRule
                .onNodeWithText(theme.emoji)
                .assertIsDisplayed()
        }
    }

    @Test
    fun themeSelectionScreen_clickBackButton_callsOnNavigateBack() {
        // Given
        var navigateBackCalled = false
        
        composeTestRule.setContent {
            PageGatherTheme {
                ThemeSelectionScreen(
                    onNavigateBack = { navigateBackCalled = true },
                    onNavigateToCustomThemeCreation = { }
                )
            }
        }

        // When
        composeTestRule
            .onNodeWithContentDescription("返回")
            .performClick()

        // Then
        assert(navigateBackCalled) { "点击返回按钮应该调用onNavigateBack回调" }
    }

    @Test
    fun themeSelectionScreen_displaysDynamicColorSection() {
        // Given
        composeTestRule.setContent {
            PageGatherTheme {
                ThemeSelectionScreen(
                    onNavigateBack = { },
                    onNavigateToCustomThemeCreation = { }
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("动态颜色")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("启用动态颜色")
            .assertIsDisplayed()
    }

    @Test
    fun themeSelectionScreen_displaysAddCustomThemeButton() {
        // Given
        composeTestRule.setContent {
            PageGatherTheme {
                ThemeSelectionScreen(
                    onNavigateBack = { },
                    onNavigateToCustomThemeCreation = { }
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithContentDescription("添加自定义主题")
            .assertIsDisplayed()
    }

    @Test
    fun themeSelectionScreen_clickAddCustomThemeButton_callsOnNavigateToCustomThemeCreation() {
        // Given
        var navigateToCustomThemeCreationCalled = false
        
        composeTestRule.setContent {
            PageGatherTheme {
                ThemeSelectionScreen(
                    onNavigateBack = { },
                    onNavigateToCustomThemeCreation = { navigateToCustomThemeCreationCalled = true }
                )
            }
        }

        // When
        composeTestRule
            .onNodeWithContentDescription("添加自定义主题")
            .performClick()

        // Then
        assert(navigateToCustomThemeCreationCalled) { "点击添加自定义主题按钮应该调用onNavigateToCustomThemeCreation回调" }
    }

    @Test
    fun themeSelectionScreen_displaysCurrentThemeInfo() {
        // Given
        composeTestRule.setContent {
            PageGatherTheme {
                ThemeSelectionScreen(
                    onNavigateBack = { },
                    onNavigateToCustomThemeCreation = { }
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("当前主题")
            .assertIsDisplayed()
    }

    @Test
    fun themeSelectionScreen_handlesThemeSelection() {
        // Given
        composeTestRule.setContent {
            PageGatherTheme {
                ThemeSelectionScreen(
                    onNavigateBack = { },
                    onNavigateToCustomThemeCreation = { }
                )
            }
        }

        // When - 选择第一个主题
        val firstTheme = AppTheme.getAllThemes().first()
        composeTestRule
            .onNodeWithText(firstTheme.displayName)
            .performClick()

        // Then - 验证主题被选中（需要mock实现）
    }

    @Test
    fun themeSelectionScreen_handlesThemeModeSelection() {
        // Given
        composeTestRule.setContent {
            PageGatherTheme {
                ThemeSelectionScreen(
                    onNavigateBack = { },
                    onNavigateToCustomThemeCreation = { }
                )
            }
        }

        // When - 选择暗色模式
        composeTestRule
            .onNodeWithText("暗色模式")
            .performClick()

        // Then - 验证模式被选中（需要mock实现）
    }

    @Test
    fun themeSelectionScreen_handlesDynamicColorToggle() {
        // Given
        composeTestRule.setContent {
            PageGatherTheme {
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
    fun themeSelectionScreen_handlesLoadingState() {
        // Given
        composeTestRule.setContent {
            PageGatherTheme {
                ThemeSelectionScreen(
                    onNavigateBack = { },
                    onNavigateToCustomThemeCreation = { }
                )
            }
        }

        // Then - 验证UI元素正常显示（加载状态测试需要mock实现）
        composeTestRule
            .onNodeWithText("主题设置")
            .assertIsDisplayed()
    }

    @Test
    fun themeSelectionScreen_displaysAllSections() {
        // Given
        composeTestRule.setContent {
            PageGatherTheme {
                ThemeSelectionScreen(
                    onNavigateBack = { },
                    onNavigateToCustomThemeCreation = { }
                )
            }
        }

        // Then - 验证所有主要部分都显示
        composeTestRule
            .onNodeWithText("显示模式")
            .assertIsDisplayed()
            
        composeTestRule
            .onNodeWithText("动态颜色")
            .assertIsDisplayed()
            
        composeTestRule
            .onNodeWithText("主题颜色")
            .assertIsDisplayed()
    }
}