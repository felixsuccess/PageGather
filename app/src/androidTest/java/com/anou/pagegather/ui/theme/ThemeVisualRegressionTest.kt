package com.anou.pagegather.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 主题视觉回归测试
 * 验证主题切换后所有UI元素正确更新颜色，确保视觉一致性
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ThemeVisualRegressionTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun visualRegression_themeSwitch_allComponentsUpdate() {
        // Given - 创建一个包含多种组件的测试界面
        var currentTheme by mutableStateOf(AppTheme.ELEGANT_WHITE)
        var isDarkMode by mutableStateOf(false)

        composeTestRule.setContent {
            PageGatherTheme(
                theme = currentTheme,
                darkTheme = isDarkMode
            ) {
                ComprehensiveUITestScreen(
                    onThemeChange = { theme -> currentTheme = theme },
                    onModeChange = { dark -> isDarkMode = dark }
                )
            }
        }

        // When & Then - 测试每个主题切换
        AppTheme.getAllThemes().forEach { theme ->
            // 切换主题
            composeTestRule
                .onNodeWithText(theme.displayName)
                .performClick()

            // 验证主题已切换
            assert(currentTheme == theme) {
                "主题应该切换到 ${theme.displayName}"
            }

            // 验证所有组件仍然可见和可交互
            verifyAllComponentsVisible()
            verifyAllComponentsInteractive()
        }
    }

    @Test
    fun visualRegression_darkModeSwitch_allComponentsUpdate() {
        // Given
        var isDarkMode by mutableStateOf(false)
        val theme = AppTheme.HUNDI_BLUE

        composeTestRule.setContent {
            PageGatherTheme(
                theme = theme,
                darkTheme = isDarkMode
            ) {
                ComprehensiveUITestScreen(
                    onThemeChange = { },
                    onModeChange = { dark -> isDarkMode = dark }
                )
            }
        }

        // When - 切换到暗色模式
        composeTestRule
            .onNodeWithText("切换暗色模式")
            .performClick()

        // Then - 验证所有组件在暗色模式下正确显示
        assert(isDarkMode) { "应该切换到暗色模式" }
        verifyAllComponentsVisible()

        // When - 切换回亮色模式
        composeTestRule
            .onNodeWithText("切换暗色模式")
            .performClick()

        // Then - 验证所有组件在亮色模式下正确显示
        assert(!isDarkMode) { "应该切换回亮色模式" }
        verifyAllComponentsVisible()
    }

    @Test
    fun visualRegression_rapidThemeSwitching() {
        // Given
        var currentTheme by mutableStateOf(AppTheme.ELEGANT_WHITE)

        composeTestRule.setContent {
            PageGatherTheme(
                theme = currentTheme,
                darkTheme = false
            ) {
                RapidSwitchTestScreen(
                    onThemeChange = { theme -> currentTheme = theme }
                )
            }
        }

        // When - 快速切换多个主题
        val themes = AppTheme.getAllThemes()
        repeat(3) { // 重复3轮
            themes.forEach { theme ->
                composeTestRule
                    .onNodeWithText(theme.displayName)
                    .performClick()

                // 验证每次切换后组件都正确显示
                composeTestRule
                    .onNodeWithText("当前主题: ${theme.displayName}")
                    .assertIsDisplayed()
            }
        }
    }

    @Test
    fun visualRegression_componentStates_allThemes() {
        // Given & When & Then - 测试不同状态下的组件在所有主题中的表现
        AppTheme.getAllThemes().forEach { theme ->
            composeTestRule.setContent {
                PageGatherTheme(
                    theme = theme,
                    darkTheme = false
                ) {
                    ComponentStatesTestScreen()
                }
            }

            // 验证不同状态的组件
            verifyComponentStates()
        }
    }

    @Test
    fun visualRegression_colorContrast_accessibility() {
        // Given & When & Then - 验证所有主题的颜色对比度符合可访问性要求
        AppTheme.getAllThemes().forEach { theme ->
            listOf(false, true).forEach { isDark ->
                composeTestRule.setContent {
                    PageGatherTheme(
                        theme = theme,
                        darkTheme = isDark
                    ) {
                        AccessibilityTestScreen()
                    }
                }

                // 验证文本在不同背景上都可读
                verifyTextReadability()
            }
        }
    }

    @Test
    fun visualRegression_animatedComponents() {
        // Given
        var currentTheme by mutableStateOf(AppTheme.ELEGANT_WHITE)

        composeTestRule.setContent {
            PageGatherTheme(
                theme = currentTheme,
                darkTheme = false
            ) {
                AnimatedComponentsTestScreen(
                    onThemeChange = { theme -> currentTheme = theme }
                )
            }
        }

        // When - 在动画过程中切换主题
        composeTestRule
            .onNodeWithText("开始动画")
            .performClick()

        // 切换主题
        composeTestRule
            .onNodeWithText(AppTheme.HUNDI_GREEN.displayName)
            .performClick()

        // Then - 验证动画组件在主题切换后仍然正常工作
        composeTestRule
            .onNodeWithText("动画组件")
            .assertIsDisplayed()
    }

    @Test
    fun visualRegression_complexLayouts() {
        // Given & When & Then - 测试复杂布局在主题切换后的视觉一致性
        AppTheme.getAllThemes().forEach { theme ->
            composeTestRule.setContent {
                PageGatherTheme(
                    theme = theme,
                    darkTheme = false
                ) {
                    ComplexLayoutTestScreen()
                }
            }

            // 验证复杂布局的各个部分
            verifyComplexLayoutComponents()
        }
    }

    @Test
    fun visualRegression_edgeCases() {
        // Given & When & Then - 测试边缘情况下的视觉表现
        AppTheme.getAllThemes().forEach { theme ->
            composeTestRule.setContent {
                PageGatherTheme(
                    theme = theme,
                    darkTheme = false
                ) {
                    EdgeCasesTestScreen()
                }
            }

            // 验证边缘情况
            verifyEdgeCases()
        }
    }

    /**
     * 验证所有组件可见
     */
    private fun verifyAllComponentsVisible() {
        // 基础组件
        composeTestRule.onNodeWithText("主要按钮").assertIsDisplayed()
        composeTestRule.onNodeWithText("次要按钮").assertIsDisplayed()
        composeTestRule.onNodeWithText("测试卡片").assertIsDisplayed()
        composeTestRule.onNodeWithText("测试文本").assertIsDisplayed()

        // 输入组件
        composeTestRule.onNodeWithTestTag("test_textfield").assertIsDisplayed()
        composeTestRule.onNodeWithTestTag("test_checkbox").assertIsDisplayed()
        composeTestRule.onNodeWithTestTag("test_switch").assertIsDisplayed()

        // 导航组件
        composeTestRule.onNodeWithTestTag("test_fab").assertIsDisplayed()
    }

    /**
     * 验证所有组件可交互
     */
    private fun verifyAllComponentsInteractive() {
        // 测试按钮点击
        composeTestRule.onNodeWithText("主要按钮").assertHasClickAction()
        composeTestRule.onNodeWithText("次要按钮").assertHasClickAction()

        // 测试输入组件
        composeTestRule.onNodeWithTestTag("test_checkbox").assertIsToggleable()
        composeTestRule.onNodeWithTestTag("test_switch").assertIsToggleable()
    }

    /**
     * 验证组件状态
     */
    private fun verifyComponentStates() {
        // 启用状态
        composeTestRule.onNodeWithText("启用按钮").assertIsDisplayed()
        composeTestRule.onNodeWithText("启用按钮").assertIsEnabled()

        // 禁用状态
        composeTestRule.onNodeWithText("禁用按钮").assertIsDisplayed()
        composeTestRule.onNodeWithText("禁用按钮").assertIsNotEnabled()

        // 选中状态
        composeTestRule.onNodeWithTestTag("checked_checkbox").assertIsOn()
        composeTestRule.onNodeWithTestTag("unchecked_checkbox").assertIsOff()
    }

    /**
     * 验证文本可读性
     */
    private fun verifyTextReadability() {
        // 验证主要文本
        composeTestRule.onNodeWithText("主要文本").assertIsDisplayed()
        composeTestRule.onNodeWithText("次要文本").assertIsDisplayed()
        composeTestRule.onNodeWithText("标题文本").assertIsDisplayed()
        composeTestRule.onNodeWithText("正文文本").assertIsDisplayed()
    }

    /**
     * 验证复杂布局组件
     */
    private fun verifyComplexLayoutComponents() {
        composeTestRule.onNodeWithText("复杂布局标题").assertIsDisplayed()
        composeTestRule.onNodeWithText("列表项 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("列表项 2").assertIsDisplayed()
        composeTestRule.onNodeWithTestTag("complex_card").assertIsDisplayed()
    }

    /**
     * 验证边缘情况
     */
    private fun verifyEdgeCases() {
        composeTestRule.onNodeWithText("长文本内容测试").assertIsDisplayed()
        composeTestRule.onNodeWithText("空状态").assertIsDisplayed()
        composeTestRule.onNodeWithTestTag("error_state").assertIsDisplayed()
    }
}

/**
 * 综合UI测试屏幕
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ComprehensiveUITestScreen(
    onThemeChange: (AppTheme) -> Unit,
    onModeChange: (Boolean) -> Unit
) {
    var isDarkMode by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("综合UI测试") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
                modifier = Modifier.testTag("test_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 主题切换按钮
            item {
                Text("主题选择", style = MaterialTheme.typography.headlineSmall)
            }

            items(AppTheme.getAllThemes()) { theme ->
                Button(
                    onClick = { onThemeChange(theme) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(theme.displayName)
                }
            }

            // 模式切换
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("暗色模式")
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = {
                            isDarkMode = it
                            onModeChange(it)
                        }
                    )
                }
            }

            // 基础组件
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { }) {
                        Text("主要按钮")
                    }

                    OutlinedButton(onClick = { }) {
                        Text("次要按钮")
                    }

                    Card {
                        Text(
                            text = "测试卡片",
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    Text("测试文本")

                    OutlinedTextField(
                        value = "",
                        onValueChange = { },
                        label = { Text("测试输入") },
                        modifier = Modifier.testTag("test_textfield")
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = false,
                            onCheckedChange = { },
                            modifier = Modifier.testTag("test_checkbox")
                        )
                        Text("测试复选框")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(
                            checked = true,
                            onCheckedChange = { },
                            modifier = Modifier.testTag("test_switch")
                        )
                        Text("测试开关")
                    }
                }
            }
        }
    }
}

/**
 * 快速切换测试屏幕
 */
@Composable
private fun RapidSwitchTestScreen(
    onThemeChange: (AppTheme) -> Unit
) {
    var currentTheme by remember { mutableStateOf(AppTheme.ELEGANT_WHITE) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "当前主题: ${currentTheme.displayName}",
            style = MaterialTheme.typography.headlineMedium
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(AppTheme.getAllThemes()) { theme ->
                Button(
                    onClick = {
                        currentTheme = theme
                        onThemeChange(theme)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(theme.displayName)
                }
            }
        }
    }
}

/**
 * 组件状态测试屏幕
 */
@Composable
private fun ComponentStatesTestScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("组件状态测试", style = MaterialTheme.typography.headlineMedium)

        // 启用/禁用状态
        Button(onClick = { }) {
            Text("启用按钮")
        }

        Button(
            onClick = { },
            enabled = false
        ) {
            Text("禁用按钮")
        }

        // 选中状态
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = true,
                onCheckedChange = { },
                modifier = Modifier.testTag("checked_checkbox")
            )
            Text("选中复选框")
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = false,
                onCheckedChange = { },
                modifier = Modifier.testTag("unchecked_checkbox")
            )
            Text("未选中复选框")
        }

        // 加载状态
        CircularProgressIndicator()

        // 错误状态
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Text(
                text = "错误状态",
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

/**
 * 可访问性测试屏幕
 */
@Composable
private fun AccessibilityTestScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "标题文本",
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = "主要文本",
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = "次要文本",
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = "正文文本",
            style = MaterialTheme.typography.bodySmall
        )

        // 不同背景上的文本
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = "主色背景上的文本",
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(16.dp)
            )
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text(
                text = "次要色背景上的文本",
                color = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

/**
 * 动画组件测试屏幕
 */
@Composable
private fun AnimatedComponentsTestScreen(
    onThemeChange: (AppTheme) -> Unit
) {
    var isAnimating by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("动画组件", style = MaterialTheme.typography.headlineMedium)

        Button(
            onClick = { isAnimating = !isAnimating }
        ) {
            Text("开始动画")
        }

        // 主题切换按钮
        AppTheme.getAllThemes().take(3).forEach { theme ->
            Button(
                onClick = { onThemeChange(theme) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(theme.displayName)
            }
        }

        if (isAnimating) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * 复杂布局测试屏幕
 */
@Composable
private fun ComplexLayoutTestScreen() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "复杂布局标题",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn {
            items(3) { index ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .testTag("complex_card")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "列表项 ${index + 1}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "描述文本",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 边缘情况测试屏幕
 */
@Composable
private fun EdgeCasesTestScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 长文本
        Text(
            text = "长文本内容测试：这是一个很长很长很长很长很长很长很长很长很长很长的文本内容，用于测试文本在不同主题下的显示效果。",
            style = MaterialTheme.typography.bodyMedium
        )

        // 空状态
        Card {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
                Text("空状态")
            }
        }

        // 错误状态
        Card(
            modifier = Modifier.testTag("error_state"),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "错误状态显示",
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}