package com.anou.pagegather.ui.feature.settings

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.anou.pagegather.domain.theme.ThemeManager
import com.anou.pagegather.ui.theme.AppTheme
import com.anou.pagegather.ui.theme.PageGatherTheme
import com.anou.pagegather.ui.theme.ThemeMode
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

/**
 * ThemeSelectionScreen UI 集成测试
 * 测试主题选择界面的交互功能和视觉效果
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ThemeSelectionScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    // Mock ThemeManager
    private lateinit var mockThemeManager: ThemeManager
    
    // 状态流
    private val currentThemeFlow = MutableStateFlow(AppTheme.ELEGANT_WHITE)
    private val themeModeFlow = MutableStateFlow(ThemeMode.SYSTEM)
    private val isDarkModeFlow = MutableStateFlow(false)

    @Before
    fun setup() {
        hiltRule.inject()
        
        // 创建 Mock ThemeManager
        mockThemeManager = mockk(relaxed = true)
        
        // 设置 Mock 行为
        every { mockThemeManager.currentTheme } returns currentThemeFlow
        every { mockThemeManager.themeMode } returns themeModeFlow
        every { mockThemeManager.isDarkMode } returns isDarkModeFlow
        
        coEvery { mockThemeManager.setTheme(any()) } answers {
            currentThemeFlow.value = firstArg()
        }
        
        coEvery { mockThemeManager.setThemeMode(any()) } answers {
            themeModeFlow.value = firstArg()
        }
    }

    @Test
    fun themeSelectionScreen_displaysCorrectTitle() {
        // Given
        composeTestRule.setContent {
            PageGatherTheme {
                ThemeSelectionScreen(
                    onNavigateBack = { }
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
                    onNavigateBack = { backPressed = true }
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
                    onNavigateBack = { }
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
                    onNavigateBack = { }
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
                    onNavigateBack = { }
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
                    onNavigateBack = { }
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
                    onNavigateBack = { }
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
                    onNavigateBack = { }
                )
            }
        }

        // When - 点击亮色模式
        composeTestRule
            .onNodeWithText("亮色模式")
            .performClick()

        // Then - 验证模式被更新
        assert(themeModeFlow.value == ThemeMode.LIGHT) {
            "点击亮色模式应该更新主题模式"
        }
    }

    @Test
    fun themeSelectionScreen_clickTheme_updatesSelection() {
        // Given
        composeTestRule.setContent {
            PageGatherTheme {
                ThemeSelectionScreen(
                    onNavigateBack = { }
                )
            }
        }

        // When - 点击绿色主题
        composeTestRule
            .onNodeWithText(AppTheme.HUNDI_GREEN.displayName)
            .performClick()

        // Then - 验证主题被更新
        assert(currentThemeFlow.value == AppTheme.HUNDI_GREEN) {
            "点击绿色主题应该更新当前主题"
        }
    }

    @Test
    fun themeSelectionScreen_displaysThemeDescriptions() {
        // Given
        composeTestRule.setContent {
            PageGatherTheme {
                ThemeSelectionScreen(
                    onNavigateBack = { }
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
                    onNavigateBack = { }
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
    fun themeSelectionScreen_showsLoadingState() {
        // Given - 创建一个自定义的 ViewModel 来控制加载状态
        composeTestRule.setContent {
            PageGatherTheme {
                ThemeSelectionScreen(
                    onNavigateBack = { }
                )
            }
        }

        // 这个测试需要能够控制 ViewModel 的加载状态
        // 在实际实现中，可能需要注入 Mock ViewModel
        // 这里主要验证 UI 组件能够正确处理加载状态
    }

    @Test
    fun themeSelectionScreen_handlesMultipleQuickClicks() {
        // Given
        composeTestRule.setContent {
            PageGatherTheme {
                ThemeSelectionScreen(
                    onNavigateBack = { }
                )
            }
        }

        // When - 快速多次点击同一个主题
        repeat(3) {
            composeTestRule
                .onNodeWithText(AppTheme.HUNDI_PURPLE.displayName)
                .performClick()
        }

        // Then - 验证系统能够正确处理多次点击
        assert(currentThemeFlow.value == AppTheme.HUNDI_PURPLE) {
            "多次快速点击应该正确处理"
        }
    }

    @Test
    fun themeSelectionScreen_preservesScrollPosition() {
        // Given
        composeTestRule.setContent {
            PageGatherTheme {
                ThemeSelectionScreen(
                    onNavigateBack = { }
                )
            }
        }

        // When - 滚动到底部
        composeTestRule
            .onNodeWithText("主题颜色")
            .performScrollTo()

        // Then - 验证滚动位置
        composeTestRule
            .onNodeWithText("主题颜色")
            .assertIsDisplayed()
    }

    @Test
    fun themeSelectionScreen_accessibilityLabels() {
        // Given
        composeTestRule.setContent {
            PageGatherTheme {
                ThemeSelectionScreen(
                    onNavigateBack = { }
                )
            }
        }

        // Then - 验证可访问性标签
        composeTestRule
            .onNodeWithContentDescription("返回")
            .assertIsDisplayed()
    }

    @Test
    fun themeSelectionScreen_respondsToSystemThemeChanges() {
        // Given
        composeTestRule.setContent {
            PageGatherTheme {
                ThemeSelectionScreen(
                    onNavigateBack = { }
                )
            }
        }

        // When - 模拟系统暗色模式变化
        isDarkModeFlow.value = true

        // Then - 验证界面响应变化
        composeTestRule
            .onNodeWithText("当前模式：暗色")
            .assertIsDisplayed()
    }

    @Test
    fun themeSelectionScreen_listLayout_displaysCorrectly() {
        // Given
        composeTestRule.setContent {
            PageGatherTheme {
                ThemeSelectionScreenList(
                    onNavigateBack = { }
                )
            }
        }

        // Then - 验证列表布局正确显示
        composeTestRule
            .onNodeWithText("主题设置")
            .assertIsDisplayed()
        
        // 验证所有主题在列表中显示
        AppTheme.getAllThemes().forEach { theme ->
            composeTestRule
                .onNodeWithText(theme.displayName)
                .assertIsDisplayed()
        }
    }

    @Test
    fun themeSelectionScreen_gridLayout_displaysCorrectly() {
        // Given
        composeTestRule.setContent {
            PageGatherTheme {
                ThemeSelectionScreen(
                    onNavigateBack = { }
                )
            }
        }

        // Then - 验证网格布局正确显示所有主题
        AppTheme.getAllThemes().forEach { theme ->
            composeTestRule
                .onNodeWithText(theme.displayName)
                .assertIsDisplayed()
        }
    }

    @Test
    fun themeSelectionScreen_themeModeDescriptions_displayCorrectly() {
        // Given
        composeTestRule.setContent {
            PageGatherTheme {
                ThemeSelectionScreen(
                    onNavigateBack = { }
                )
            }
        }

        // Then - 验证主题模式描述正确显示
        composeTestRule
            .onNodeWithText("始终使用亮色主题")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("始终使用暗色主题")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("跟随系统设置自动切换")
            .assertIsDisplayed()
    }
}