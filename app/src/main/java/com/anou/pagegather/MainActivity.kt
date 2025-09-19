package com.anou.pagegather

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.anou.pagegather.ui.navigation.MainScreen
import com.anou.pagegather.ui.theme.PageGatherTheme
import com.anou.pagegather.ui.theme.extendedColors
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    private lateinit var themeManagerViewModel: com.anou.pagegather.domain.theme.ThemeManagerViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainActivityContent { viewModel ->
                themeManagerViewModel = viewModel
            }
        }
    }
    
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // 通知主题管理器配置已更改
        if (::themeManagerViewModel.isInitialized) {
            themeManagerViewModel.onConfigurationChanged()
        }
    }
}

@Composable
fun MainActivityContent(
    onViewModelReady: (com.anou.pagegather.domain.theme.ThemeManagerViewModel) -> Unit = {}
) {
    val themeManager = hiltViewModel<com.anou.pagegather.domain.theme.ThemeManagerViewModel>()
    val currentTheme by themeManager.currentTheme.collectAsState()
    val themeMode by themeManager.themeMode.collectAsState()
    val isDarkMode by themeManager.isDarkMode.collectAsState()
    
    // 通知 Activity ViewModel 已准备好
    LaunchedEffect(themeManager) {
        onViewModelReady(themeManager)
    }
    
    // 监听系统配置变化
    val systemInDarkTheme = isSystemInDarkTheme()
    LaunchedEffect(systemInDarkTheme, themeMode) {
        // 当系统暗色模式变化且用户选择跟随系统时，通知主题管理器
        if (themeMode == com.anou.pagegather.ui.theme.ThemeMode.SYSTEM) {
            themeManager.onConfigurationChanged()
        }
    }
    
    PageGatherTheme(
        theme = currentTheme,
        darkTheme = isDarkMode
    ) {
        SetupImmersiveNavigation(isDarkMode)
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            MainContent(
                modifier = Modifier.padding(innerPadding), 
                route = null
            )
        }
    }
}


@Composable
fun MainContent(
    modifier: Modifier = Modifier, route: String?,
) {
    val isSplash = remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(2000) // 延迟 2 秒
        isSplash.value = false
    }
    if (isSplash.value) {
        SplashScreen()
    } else {
        MainScreen()
    }
}


@Composable
fun SplashScreen() {
    val textToDisplay = stringResource(R.string.app_slogan)
    val displayedTextLength = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        displayedTextLength.animateTo(
            targetValue = textToDisplay.length.toFloat(),
            animationSpec = tween(
                durationMillis = 1500,
                easing = LinearEasing
            )
        )
    }

    val displayedText = textToDisplay.take(displayedTextLength.value.toInt())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column {
            displayedText.forEach { char ->
                Text(
                    char.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.extendedColors.titleColor
                )
            }
        }
    }

}


@SuppressLint("InlinedApi")
@Composable
fun SetupImmersiveNavigation(isDarkMode: Boolean = false) {
    val view = LocalView.current
    val isLightTheme = !isDarkMode // 使用主题管理器的暗色模式状态
    val window = (view.context as? ComponentActivity)?.window

    SideEffect {
        window?.let {
            WindowCompat.setDecorFitsSystemWindows(it, false)
            val controller = WindowInsetsControllerCompat(it, view)

            // 兼容 Android 6.0 及以上设置状态栏图标颜色
            // 确保状态栏在暗色模式下具有足够的对比度
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                controller.isAppearanceLightStatusBars = isLightTheme
            }

            // 兼容 Android 8.0 及以上设置导航栏图标颜色
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                controller.isAppearanceLightNavigationBars = isLightTheme
            }

            // 设置状态栏和导航栏透明
            it.statusBarColor = Color.Transparent.toArgb()
            it.navigationBarColor = Color.Transparent.toArgb()

            // Android 11+ 隐藏状态栏和导航栏阴影
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                controller.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }
}