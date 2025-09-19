package com.anou.pagegather.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.anou.pagegather.domain.theme.ThemeManager
import com.anou.pagegather.ui.feature.settings.ThemeSelectionScreen
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 主题系统集成测试
 * 测试主题切换的完整流程和视觉效果
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ThemeIntegrationTest {

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
            // 模拟暗色模式的自动切换
            when (firstArg<ThemeMode>()) {
                ThemeMode.LIGHT -> isDarkModeFlow.value = false
                ThemeMode.DARK -> isDarkModeFlow.value = true
                ThemeMode.SYSTEM -> {
                    // 保持当前系统设置
                }
            }
        }
    }

    @Test
    fun themeIntegration_completeThemeSwitchFlow() {
        // Given - 设置初始主题
        currentThemeFlow.value = AppTheme.ELEGANT_WHITE
        
        composeTestRule.setContent {
            PageGatherTheme(
                theme = currentThemeFlow.collectAsState().value,
                darkTheme = isDarkModeFlow.collectAsState().value
            ) {
                ThemeSelectionScreen(
                    onNavigateBack = { }
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
                    onNavigateBack = { }
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
                    onNavigateBack = { }
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
                    onNavigateBack = { }
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
                    onNavigateBack = { }
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
                    onNavigateBack = { }
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
        currentThemeFlow.value = AppTheme.HUNDI_PURPLE
        themeModeFlow.value = ThemeMode.DARK
        isDarkModeFlow.value = true
        
        composeTestRule.setContent {
            PageGatherTheme(
                theme = currentThemeFlow.collectAsState().value,
                darkTheme = isDarkModeFlow.collectAsState().value
            ) {
                ThemeSelectionScreen(
                    onNavigateBack = { }
                )
            }
        }

        // Then - 验证保存的设置被正确恢复
        composeTestRule
            .onNodeWithText("当前主题：${AppTheme.HUNDI_PURPLE.displayName}")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("当前模式：暗色")
            .assertIsDisplayed()
    }

    @Test
    fun themeIntegration_visualConsistency() {
        // Given - 创建一个包含多个UI元素的测试界面
        composeTestRule.setContent {
            PageGatherTheme(
                theme = currentThemeFlow.collectAsState().value,
                darkTheme = isDarkModeFlow.collectAsState().value
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // 测试各种UI组件在主题切换后的一致性
                    Card {
                        Text("测试卡片", modifier = Modifier.padding(16.dp))
                    }
                    
                    Button(onClick = { }) {
                        Text("测试按钮")
                    }
                    
                    OutlinedButton(onClick = { }) {
                        Text("测试轮廓按钮")
                    }
                    
                    ThemeSelectionScreen(
                        onNavigateBack = { }
                    )
                }
            }
        }

        // When - 切换主题
        composeTestRule
            .onNodeWithText(AppTheme.HUNDI_ORANGE.displayName)
            .performClick()

        // Then - 验证所有UI元素都存在且可见
        composeTestRule
            .onNodeWithText("测试卡片")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("测试按钮")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("测试轮廓按钮")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("当前主题：${AppTheme.HUNDI_ORANGE.displayName}")
            .assertIsDisplayed()
    }

    @Test
    fun themeIntegration_rapidSwitching() {
        // Given
        composeTestRule.setContent {
            PageGatherTheme(
                theme = currentThemeFlow.collectAsState().value,
                darkTheme = isDarkModeFlow.collectAsState().value
            ) {
                ThemeSelectionScreen(
                    onNavigateBack = { }
                )
            }
        }

        // When - 快速切换主题
        val themes = AppTheme.getAllThemes()
        repeat(3) { // 重复3轮快速切换
            themes.forEach { theme ->
                composeTestRule
                    .onNodeWithText(theme.displayName)
                    .performClick()
            }
        }

        // Then - 验证最终状态正确
        val finalTheme = themes.last()
        assert(currentThemeFlow.value == finalTheme) {
            "最终主题应该是 ${finalTheme.displayName}"
        }
    }

    @Test
    fun themeIntegration_errorRecovery() {
        // Given - 模拟主题切换过程中的错误
        var shouldThrowError = false
        
        coEvery { mockThemeManager.setTheme(any()) } answers {
            if (shouldThrowError) {
                throw RuntimeException("Theme switch failed")
            } else {
                currentThemeFlow.value = firstArg()
            }
        }
        
        composeTestRule.setContent {
            PageGatherTheme(
                theme = currentThemeFlow.collectAsState().value,
                darkTheme = isDarkModeFlow.collectAsState().value
            ) {
                ThemeSelectionScreen(
                    onNavigateBack = { }
                )
            }
        }

        // When - 先成功切换一次
        composeTestRule
            .onNodeWithText(AppTheme.HUNDI_BLUE.displayName)
            .performClick()
        
        assert(currentThemeFlow.value == AppTheme.HUNDI_BLUE)
        
        // 然后模拟错误
        shouldThrowError = true
        composeTestRule
            .onNodeWithText(AppTheme.HUNDI_GREEN.displayName)
            .performClick()
        
        // 恢复正常
        shouldThrowError = false
        composeTestRule
            .onNodeWithText(AppTheme.HUNDI_PURPLE.displayName)
            .performClick()

        // Then - 验证系统能够从错误中恢复
        assert(currentThemeFlow.value == AppTheme.HUNDI_PURPLE) {
            "系统应该能够从错误中恢复并正常切换主题"
        }
    }

    @Test
    fun themeIntegration_allThemesWork() {
        // Given & When & Then - 测试所有主题都能正常工作
        AppTheme.getAllThemes().forEach { theme ->
            composeTestRule.setContent {
                PageGatherTheme(
                    theme = theme,
                    darkTheme = false
                ) {
                    Column {
                        Text("测试主题: ${theme.displayName}")
                        Card {
                            Text("卡片内容", modifier = Modifier.padding(16.dp))
                        }
                        Button(onClick = { }) {
                            Text("按钮")
                        }
                    }
                }
            }

            // 验证主题能正常渲染
            composeTestRule
                .onNodeWithText("测试主题: ${theme.displayName}")
                .assertIsDisplayed()
            
            composeTestRule
                .onNodeWithText("卡片内容")
                .assertIsDisplayed()
            
            composeTestRule
                .onNodeWithText("按钮")
                .assertIsDisplayed()
        }
    }

    @Test
    fun themeIntegration_darkModeForAllThemes() {
        // Given & When & Then - 测试所有主题的暗色模式都能正常工作
        AppTheme.getAllThemes().forEach { theme ->
            // 测试亮色模式
            composeTestRule.setContent {
                PageGatherTheme(
                    theme = theme,
                    darkTheme = false
                ) {
                    Text("亮色模式: ${theme.displayName}")
                }
            }

            composeTestRule
                .onNodeWithText("亮色模式: ${theme.displayName}")
                .assertIsDisplayed()

            // 测试暗色模式
            composeTestRule.setContent {
                PageGatherTheme(
                    theme = theme,
                    darkTheme = true
                ) {
                    Text("暗色模式: ${theme.displayName}")
                }
            }

            composeTestRule
                .onNodeWithText("暗色模式: ${theme.displayName}")
                .assertIsDisplayed()
        }
    }
}