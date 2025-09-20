package com.anou.pagegather.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
 * 全应用主题一致性测试
 * 验证所有页面都正确应用选定主题，确保主题在不同屏幕间的一致性
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ThemeConsistencyTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun themeConsistency_allThemes_basicComponents() {
        // Given & When & Then - 测试所有主题在基础组件上的一致性
        AppTheme.getAllThemes().forEach { theme ->
            listOf(false, true).forEach { isDark ->
                composeTestRule.setContent {
                    PageGatherTheme(
                        theme = theme,
                        darkTheme = isDark
                    ) {
                        BasicComponentsTestScreen()
                    }
                }

                // 验证基础组件都能正确渲染
                composeTestRule
                    .onNodeWithText("主题测试: ${theme.displayName}")
                    .assertIsDisplayed()

                composeTestRule
                    .onNodeWithText("测试按钮")
                    .assertIsDisplayed()

                composeTestRule
                    .onNodeWithText("测试卡片")
                    .assertIsDisplayed()

                composeTestRule
                    .onNodeWithText("测试文本字段")
                    .assertIsDisplayed()
            }
        }
    }

    @Test
    fun themeConsistency_materialComponents_allThemes() {
        // Given & When & Then - 测试所有 Material 组件在不同主题下的一致性
        AppTheme.getAllThemes().forEach { theme ->
            composeTestRule.setContent {
                PageGatherTheme(
                    theme = theme,
                    darkTheme = false
                ) {
                    MaterialComponentsTestScreen()
                }
            }

            // 验证各种 Material 组件
            composeTestRule
                .onNodeWithText("主要按钮")
                .assertIsDisplayed()

            composeTestRule
                .onNodeWithText("次要按钮")
                .assertIsDisplayed()

            composeTestRule
                .onNodeWithText("轮廓按钮")
                .assertIsDisplayed()

            composeTestRule
                .onNodeWithText("文本按钮")
                .assertIsDisplayed()

            composeTestRule
                .onNodeWithText("浮动操作按钮")
                .assertIsDisplayed()
        }
    }

    @Test
    fun themeConsistency_customComponents_allThemes() {
        // Given & When & Then - 测试自定义组件在不同主题下的一致性
        AppTheme.getAllThemes().forEach { theme ->
            composeTestRule.setContent {
                PageGatherTheme(
                    theme = theme,
                    darkTheme = false
                ) {
                    CustomComponentsTestScreen()
                }
            }

            // 验证自定义组件正确应用主题
            composeTestRule
                .onNodeWithText("自定义组件测试")
                .assertIsDisplayed()
        }
    }

    @Test
    fun themeConsistency_navigationComponents() {
        // Given & When & Then - 测试导航组件的主题一致性
        AppTheme.getAllThemes().forEach { theme ->
            composeTestRule.setContent {
                PageGatherTheme(
                    theme = theme,
                    darkTheme = false
                ) {
                    NavigationTestScreen()
                }
            }

            // 验证导航组件
            composeTestRule
                .onNodeWithText("导航测试")
                .assertIsDisplayed()

            composeTestRule
                .onNodeWithContentDescription("菜单")
                .assertIsDisplayed()

            composeTestRule
                .onNodeWithContentDescription("搜索")
                .assertIsDisplayed()
        }
    }

    @Test
    fun themeConsistency_darkModeTransition() {
        // Given
        val theme = AppTheme.HUNDI_BLUE
        var isDarkMode by mutableStateOf(false)

        composeTestRule.setContent {
            PageGatherTheme(
                theme = theme,
                darkTheme = isDarkMode
            ) {
                Column {
                    Text("模式切换测试")
                    Button(
                        onClick = { isDarkMode = !isDarkMode }
                    ) {
                        Text("切换模式")
                    }
                    Card {
                        Text("卡片内容", modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }

        // When - 切换到暗色模式
        composeTestRule
            .onNodeWithText("切换模式")
            .performClick()

        // Then - 验证组件在暗色模式下仍然正确显示
        composeTestRule
            .onNodeWithText("模式切换测试")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("卡片内容")
            .assertIsDisplayed()

        // When - 切换回亮色模式
        composeTestRule
            .onNodeWithText("切换模式")
            .performClick()

        // Then - 验证组件在亮色模式下仍然正确显示
        composeTestRule
            .onNodeWithText("模式切换测试")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("卡片内容")
            .assertIsDisplayed()
    }

    @Test
    fun themeConsistency_complexLayout() {
        // Given & When & Then - 测试复杂布局的主题一致性
        AppTheme.getAllThemes().forEach { theme ->
            composeTestRule.setContent {
                PageGatherTheme(
                    theme = theme,
                    darkTheme = false
                ) {
                    ComplexLayoutTestScreen()
                }
            }

            // 验证复杂布局中的各个元素
            composeTestRule
                .onNodeWithText("复杂布局测试")
                .assertIsDisplayed()

            composeTestRule
                .onNodeWithText("列表项 1")
                .assertIsDisplayed()

            composeTestRule
                .onNodeWithText("列表项 2")
                .assertIsDisplayed()
        }
    }

    @Test
    fun themeConsistency_stateChanges() {
        // Given
        val theme = AppTheme.HUNDI_GREEN
        
        composeTestRule.setContent {
            PageGatherTheme(
                theme = theme,
                darkTheme = false
            ) {
                StateChangeTestScreen()
            }
        }

        // When - 与有状态的组件交互
        composeTestRule
            .onNodeWithText("点击计数: 0")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("增加")
            .performClick()

        // Then - 验证状态变化后主题仍然一致
        composeTestRule
            .onNodeWithText("点击计数: 1")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("增加")
            .assertIsDisplayed()
    }

    @Test
    fun themeConsistency_scrollableContent() {
        // Given
        val theme = AppTheme.HUNDI_PURPLE
        
        composeTestRule.setContent {
            PageGatherTheme(
                theme = theme,
                darkTheme = false
            ) {
                ScrollableContentTestScreen()
            }
        }

        // When - 滚动内容
        composeTestRule
            .onNodeWithText("项目 1")
            .assertIsDisplayed()

        // 滚动到底部
        composeTestRule
            .onNodeWithText("项目 20")
            .performScrollTo()

        // Then - 验证滚动后的内容仍然正确应用主题
        composeTestRule
            .onNodeWithText("项目 20")
            .assertIsDisplayed()
    }

    @Test
    fun themeConsistency_errorStates() {
        // Given & When & Then - 测试错误状态下的主题一致性
        AppTheme.getAllThemes().forEach { theme ->
            composeTestRule.setContent {
                PageGatherTheme(
                    theme = theme,
                    darkTheme = false
                ) {
                    ErrorStateTestScreen()
                }
            }

            // 验证错误状态组件
            composeTestRule
                .onNodeWithText("错误状态测试")
                .assertIsDisplayed()

            composeTestRule
                .onNodeWithText("这是一个错误消息")
                .assertIsDisplayed()
        }
    }

    @Test
    fun themeConsistency_loadingStates() {
        // Given & When & Then - 测试加载状态下的主题一致性
        AppTheme.getAllThemes().forEach { theme ->
            composeTestRule.setContent {
                PageGatherTheme(
                    theme = theme,
                    darkTheme = false
                ) {
                    LoadingStateTestScreen()
                }
            }

            // 验证加载状态组件
            composeTestRule
                .onNodeWithText("加载状态测试")
                .assertIsDisplayed()

            // 验证进度指示器存在
            composeTestRule
                .onNode(hasTestTag("loading_indicator"))
                .assertIsDisplayed()
        }
    }

    @Test
    fun themeConsistency_multipleScreens() {
        // Given - 模拟多个屏幕的主题一致性
        val screens = listOf("屏幕1", "屏幕2", "屏幕3")
        var currentScreen by mutableStateOf(screens[0])
        
        composeTestRule.setContent {
            PageGatherTheme(
                theme = AppTheme.HUNDI_ORANGE,
                darkTheme = false
            ) {
                MultiScreenTestLayout(
                    currentScreen = currentScreen,
                    onScreenChange = { currentScreen = it },
                    screens = screens
                )
            }
        }

        // When & Then - 在不同屏幕间切换，验证主题一致性
        screens.forEach { screen ->
            composeTestRule
                .onNodeWithText(screen)
                .performClick()

            composeTestRule
                .onNodeWithText("当前屏幕: $screen")
                .assertIsDisplayed()
        }
    }
}

/**
 * 基础组件测试屏幕
 */
@Composable
private fun BasicComponentsTestScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "主题测试: 基础组件",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Button(onClick = { }) {
            Text("测试按钮")
        }
        
        Card {
            Text(
                text = "测试卡片",
                modifier = Modifier.padding(16.dp)
            )
        }
        
        OutlinedTextField(
            value = "",
            onValueChange = { },
            label = { Text("测试文本字段") }
        )
        
        Checkbox(
            checked = true,
            onCheckedChange = { }
        )
        
        Switch(
            checked = false,
            onCheckedChange = { }
        )
    }
}

/**
 * Material 组件测试屏幕
 */
@Composable
private fun MaterialComponentsTestScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(onClick = { }) {
            Text("主要按钮")
        }
        
        OutlinedButton(onClick = { }) {
            Text("轮廓按钮")
        }
        
        TextButton(onClick = { }) {
            Text("文本按钮")
        }
        
        ElevatedButton(onClick = { }) {
            Text("次要按钮")
        }
        
        FloatingActionButton(
            onClick = { }
        ) {
            Text("浮动操作按钮")
        }
        
        LinearProgressIndicator()
        
        Slider(
            value = 0.5f,
            onValueChange = { }
        )
    }
}

/**
 * 自定义组件测试屏幕
 */
@Composable
private fun CustomComponentsTestScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "自定义组件测试",
            style = MaterialTheme.typography.headlineMedium
        )
        
        // 这里可以添加自定义组件的测试
        // 例如：HundiBookCard, HundiRateBar 等
        Card {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("自定义卡片组件")
                Text(
                    text = "这是一个自定义组件的示例",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

/**
 * 导航组件测试屏幕
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NavigationTestScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("导航测试") },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "搜索"
                        )
                    }
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "菜单"
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("首页") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                    label = { Text("收藏") }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text("导航内容区域")
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
            text = "复杂布局测试",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )
        
        LazyColumn {
            items(5) { index ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                        Column {
                            Text(
                                text = "列表项 ${index + 1}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "这是列表项的描述",
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
 * 状态变化测试屏幕
 */
@Composable
private fun StateChangeTestScreen() {
    var count by remember { mutableIntStateOf(0) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "点击计数: $count",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Button(
            onClick = { count++ }
        ) {
            Text("增加")
        }
        
        Button(
            onClick = { count-- }
        ) {
            Text("减少")
        }
        
        LinearProgressIndicator(
            progress = { (count % 10) / 10f }
        )
    }
}

/**
 * 可滚动内容测试屏幕
 */
@Composable
private fun ScrollableContentTestScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(20) { index ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "项目 ${index + 1}",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

/**
 * 错误状态测试屏幕
 */
@Composable
private fun ErrorStateTestScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "错误状态测试",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
                Text(
                    text = "这是一个错误消息",
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

/**
 * 加载状态测试屏幕
 */
@Composable
private fun LoadingStateTestScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "加载状态测试",
            style = MaterialTheme.typography.headlineMedium
        )
        
        CircularProgressIndicator(
            modifier = Modifier.testTag("loading_indicator")
        )
        
        Text("正在加载...")
    }
}

/**
 * 多屏幕测试布局
 */
@Composable
private fun MultiScreenTestLayout(
    currentScreen: String,
    onScreenChange: (String) -> Unit,
    screens: List<String>
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 屏幕切换按钮
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            screens.forEach { screen ->
                Button(
                    onClick = { onScreenChange(screen) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(screen)
                }
            }
        }
        
        // 当前屏幕内容
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "当前屏幕: $currentScreen",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}