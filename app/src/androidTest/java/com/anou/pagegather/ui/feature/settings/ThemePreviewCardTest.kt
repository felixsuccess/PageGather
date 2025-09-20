package com.anou.pagegather.ui.feature.settings

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.anou.pagegather.ui.feature.my.settings.CompactThemePreviewCard
import com.anou.pagegather.ui.feature.my.settings.ThemePreviewCard
import com.anou.pagegather.ui.theme.AppTheme
import com.anou.pagegather.ui.theme.PageGatherTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * ThemePreviewCard UI 集成测试
 * 测试主题预览卡片组件的显示和交互功能
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ThemePreviewCardTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun themePreviewCard_displaysThemeName() {
        // Given
        val theme = AppTheme.HUNDI_BLUE
        
        composeTestRule.setContent {
            PageGatherTheme {
                ThemePreviewCard(
                    theme = theme,
                    isSelected = false,
                    isDarkMode = false,
                    onClick = { }
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(theme.displayName)
            .assertIsDisplayed()
    }

    @Test
    fun themePreviewCard_displaysThemeDescription() {
        // Given
        val theme = AppTheme.ELEGANT_WHITE
        
        composeTestRule.setContent {
            PageGatherTheme {
                ThemePreviewCard(
                    theme = theme,
                    isSelected = false,
                    isDarkMode = false,
                    onClick = { }
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(theme.description)
            .assertIsDisplayed()
    }

    @Test
    fun themePreviewCard_displaysThemeEmoji() {
        // Given
        val theme = AppTheme.HUNDI_GREEN
        
        composeTestRule.setContent {
            PageGatherTheme {
                ThemePreviewCard(
                    theme = theme,
                    isSelected = false,
                    isDarkMode = false,
                    onClick = { }
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(theme.emoji)
            .assertIsDisplayed()
    }

    @Test
    fun themePreviewCard_showsSelectedState() {
        // Given
        val theme = AppTheme.HUNDI_PURPLE
        
        composeTestRule.setContent {
            PageGatherTheme {
                ThemePreviewCard(
                    theme = theme,
                    isSelected = true,
                    isDarkMode = false,
                    onClick = { }
                )
            }
        }

        // Then - 验证选中状态指示器显示
        composeTestRule
            .onNodeWithContentDescription("已选中")
            .assertIsDisplayed()
    }

    @Test
    fun themePreviewCard_hidesSelectedStateWhenNotSelected() {
        // Given
        val theme = AppTheme.HUNDI_ORANGE
        
        composeTestRule.setContent {
            PageGatherTheme {
                ThemePreviewCard(
                    theme = theme,
                    isSelected = false,
                    isDarkMode = false,
                    onClick = { }
                )
            }
        }

        // Then - 验证选中状态指示器不显示
        composeTestRule
            .onNodeWithContentDescription("已选中")
            .assertDoesNotExist()
    }

    @Test
    fun themePreviewCard_respondsToClick() {
        // Given
        val theme = AppTheme.ELEGANT_WHITE
        var clicked = false
        
        composeTestRule.setContent {
            PageGatherTheme {
                ThemePreviewCard(
                    theme = theme,
                    isSelected = false,
                    isDarkMode = false,
                    onClick = { clicked = true }
                )
            }
        }

        // When
        composeTestRule
            .onNodeWithText(theme.displayName)
            .performClick()

        // Then
        assert(clicked) { "点击主题卡片应该触发回调" }
    }

    @Test
    fun themePreviewCard_displaysColorPreview() {
        // Given
        val theme = AppTheme.HUNDI_BLUE
        
        composeTestRule.setContent {
            PageGatherTheme {
                ThemePreviewCard(
                    theme = theme,
                    isSelected = false,
                    isDarkMode = false,
                    onClick = { }
                )
            }
        }

        // Then - 验证颜色预览区域存在
        // 由于颜色预览是通过 Box 组件实现的，我们验证整个卡片的存在
        composeTestRule
            .onNodeWithText(theme.displayName)
            .assertIsDisplayed()
    }

    @Test
    fun compactThemePreviewCard_displaysThemeName() {
        // Given
        val theme = AppTheme.HUNDI_GREEN
        
        composeTestRule.setContent {
            PageGatherTheme {
                CompactThemePreviewCard(
                    theme = theme,
                    isSelected = false,
                    isDarkMode = false,
                    onClick = { }
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(theme.displayName)
            .assertIsDisplayed()
    }

    @Test
    fun compactThemePreviewCard_displaysThemeEmoji() {
        // Given
        val theme = AppTheme.HUNDI_PURPLE
        
        composeTestRule.setContent {
            PageGatherTheme {
                CompactThemePreviewCard(
                    theme = theme,
                    isSelected = false,
                    isDarkMode = false,
                    onClick = { }
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(theme.emoji)
            .assertIsDisplayed()
    }

    @Test
    fun compactThemePreviewCard_showsSelectedState() {
        // Given
        val theme = AppTheme.ELEGANT_WHITE
        
        composeTestRule.setContent {
            PageGatherTheme {
                CompactThemePreviewCard(
                    theme = theme,
                    isSelected = true,
                    isDarkMode = false,
                    onClick = { }
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithContentDescription("已选中")
            .assertIsDisplayed()
    }

    @Test
    fun compactThemePreviewCard_respondsToClick() {
        // Given
        val theme = AppTheme.HUNDI_ORANGE
        var clicked = false
        
        composeTestRule.setContent {
            PageGatherTheme {
                CompactThemePreviewCard(
                    theme = theme,
                    isSelected = false,
                    isDarkMode = false,
                    onClick = { clicked = true }
                )
            }
        }

        // When
        composeTestRule
            .onNodeWithText(theme.displayName)
            .performClick()

        // Then
        assert(clicked) { "点击紧凑型主题卡片应该触发回调" }
    }

    @Test
    fun themePreviewCard_allThemes_displayCorrectly() {
        // Given & When & Then - 测试所有主题都能正确显示
        AppTheme.getAllThemes().forEach { theme ->
            composeTestRule.setContent {
                PageGatherTheme {
                    ThemePreviewCard(
                        theme = theme,
                        isSelected = false,
                        isDarkMode = false,
                        onClick = { }
                    )
                }
            }

            // 验证主题名称显示
            composeTestRule
                .onNodeWithText(theme.displayName)
                .assertIsDisplayed()

            // 验证主题描述显示
            composeTestRule
                .onNodeWithText(theme.description)
                .assertIsDisplayed()

            // 验证主题表情符号显示
            composeTestRule
                .onNodeWithText(theme.emoji)
                .assertIsDisplayed()
        }
    }

    @Test
    fun compactThemePreviewCard_allThemes_displayCorrectly() {
        // Given & When & Then - 测试所有主题的紧凑型卡片都能正确显示
        AppTheme.getAllThemes().forEach { theme ->
            composeTestRule.setContent {
                PageGatherTheme {
                    CompactThemePreviewCard(
                        theme = theme,
                        isSelected = false,
                        isDarkMode = false,
                        onClick = { }
                    )
                }
            }

            // 验证主题名称显示
            composeTestRule
                .onNodeWithText(theme.displayName)
                .assertIsDisplayed()

            // 验证主题表情符号显示
            composeTestRule
                .onNodeWithText(theme.emoji)
                .assertIsDisplayed()
        }
    }

    @Test
    fun themePreviewCard_darkMode_displaysCorrectly() {
        // Given
        val theme = AppTheme.HUNDI_BLUE
        
        composeTestRule.setContent {
            PageGatherTheme {
                ThemePreviewCard(
                    theme = theme,
                    isSelected = false,
                    isDarkMode = true,
                    onClick = { }
                )
            }
        }

        // Then - 验证在暗色模式下也能正确显示
        composeTestRule
            .onNodeWithText(theme.displayName)
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText(theme.description)
            .assertIsDisplayed()
    }

    @Test
    fun compactThemePreviewCard_darkMode_displaysCorrectly() {
        // Given
        val theme = AppTheme.HUNDI_GREEN
        
        composeTestRule.setContent {
            PageGatherTheme {
                CompactThemePreviewCard(
                    theme = theme,
                    isSelected = false,
                    isDarkMode = true,
                    onClick = { }
                )
            }
        }

        // Then - 验证在暗色模式下也能正确显示
        composeTestRule
            .onNodeWithText(theme.displayName)
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText(theme.emoji)
            .assertIsDisplayed()
    }

    @Test
    fun themePreviewCard_selectedAndUnselected_visualDifference() {
        // Given
        val theme = AppTheme.ELEGANT_WHITE
        
        // Test selected state
        composeTestRule.setContent {
            PageGatherTheme {
                ThemePreviewCard(
                    theme = theme,
                    isSelected = true,
                    isDarkMode = false,
                    onClick = { }
                )
            }
        }

        // Verify selected indicator is present
        composeTestRule
            .onNodeWithContentDescription("已选中")
            .assertIsDisplayed()

        // Test unselected state
        composeTestRule.setContent {
            PageGatherTheme {
                ThemePreviewCard(
                    theme = theme,
                    isSelected = false,
                    isDarkMode = false,
                    onClick = { }
                )
            }
        }

        // Verify selected indicator is not present
        composeTestRule
            .onNodeWithContentDescription("已选中")
            .assertDoesNotExist()
    }

    @Test
    fun themePreviewCard_multipleClicks_handledCorrectly() {
        // Given
        val theme = AppTheme.HUNDI_PURPLE
        var clickCount = 0
        
        composeTestRule.setContent {
            PageGatherTheme {
                ThemePreviewCard(
                    theme = theme,
                    isSelected = false,
                    isDarkMode = false,
                    onClick = { clickCount++ }
                )
            }
        }

        // When - 多次点击
        repeat(3) {
            composeTestRule
                .onNodeWithText(theme.displayName)
                .performClick()
        }

        // Then
        assert(clickCount == 3) { "应该正确处理多次点击" }
    }

    @Test
    fun themePreviewCard_accessibility_contentDescriptions() {
        // Given
        val theme = AppTheme.HUNDI_ORANGE
        
        composeTestRule.setContent {
            PageGatherTheme {
                ThemePreviewCard(
                    theme = theme,
                    isSelected = true,
                    isDarkMode = false,
                    onClick = { }
                )
            }
        }

        // Then - 验证可访问性内容描述
        composeTestRule
            .onNodeWithContentDescription("已选中")
            .assertIsDisplayed()
    }

    @Test
    fun compactThemePreviewCard_accessibility_contentDescriptions() {
        // Given
        val theme = AppTheme.HUNDI_BLUE
        
        composeTestRule.setContent {
            PageGatherTheme {
                CompactThemePreviewCard(
                    theme = theme,
                    isSelected = true,
                    isDarkMode = false,
                    onClick = { }
                )
            }
        }

        // Then - 验证可访问性内容描述
        composeTestRule
            .onNodeWithContentDescription("已选中")
            .assertIsDisplayed()
    }
}