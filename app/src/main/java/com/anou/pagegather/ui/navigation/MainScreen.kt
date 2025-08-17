package com.anou.pagegather.ui.navigation


import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

// 主页面，包含底部导航
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    // 定义需要显示底部导航的路由
    val bottomBarRoutes = listOf(
        Routes.ProfileRoutes.PROFILE,
        Routes.NoteRoutes.NOTE_LIST,
        Routes.BookRoutes.BOOKS,
        Routes.DashboardRoutes.STATISTICS
    )


    val showBottomBar = currentRoute in bottomBarRoutes
    val navColors = NavigationBarItemDefaults.colors(
        indicatorColor =Color.Transparent,// MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
        selectedIconColor = MaterialTheme.colorScheme.primary,
        selectedTextColor = MaterialTheme.colorScheme.primary,
        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface),
                    containerColor = MaterialTheme.colorScheme.surface,
                ) {
                    NavigationBarItem(
                        selected = currentRoute == Routes.DashboardRoutes.STATISTICS,
                        onClick = { if (currentRoute != Routes.DashboardRoutes.STATISTICS) navController.navigate(Routes.DashboardRoutes.STATISTICS) },
                        icon = {
                            val interactionSource = remember { MutableInteractionSource() }
                            val isPressed by interactionSource.collectIsPressedAsState()
                            val scale = animateFloatAsState(
                                targetValue = if (currentRoute == Routes.DashboardRoutes.STATISTICS) 1.15f else if (isPressed) 0.9f else 1f,
                                animationSpec = tween(durationMillis = 200)
                            )
                            Icon(
                                Icons.Default.DateRange,
                                contentDescription = "统计",
                                modifier = Modifier.scale(scale.value),
                            )
                        },
                        label = { Text(text = "统计", fontSize = 13.sp, lineHeight = 13.sp) },
                        colors = navColors,
                        interactionSource = remember { MutableInteractionSource() },
                    )
                    NavigationBarItem(
                        selected = currentRoute == Routes.BookRoutes.BOOKS,
                        onClick = { if (currentRoute != Routes.BookRoutes.BOOKS) navController.navigate(Routes.BookRoutes.BOOKS) },
                        icon = {
                            val interactionSource = remember { MutableInteractionSource() }
                            val isPressed by interactionSource.collectIsPressedAsState()
                            val scale = animateFloatAsState(
                                targetValue = if (currentRoute == Routes.BookRoutes.BOOKS) 1.15f else if (isPressed) 0.9f else 1f,
                                animationSpec = tween(durationMillis = 200)
                            )
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "书架",
                                modifier = Modifier.scale(scale.value),
                            )
                        },
                        label = { Text(text = "书架", fontSize = 13.sp, lineHeight = 13.sp) },
                        colors = navColors,
                        interactionSource = remember { MutableInteractionSource() },

                        )
                    NavigationBarItem(
                        selected = currentRoute == Routes.NoteRoutes.NOTE_LIST,
                        onClick = { if (currentRoute != Routes.NoteRoutes.NOTE_LIST) navController.navigate(Routes.NoteRoutes.NOTE_LIST) },
                        icon = {
                            val interactionSource = remember { MutableInteractionSource() }
                            val isPressed by interactionSource.collectIsPressedAsState()
                            val scale = animateFloatAsState(
                                targetValue = if (currentRoute == Routes.NoteRoutes.NOTE_LIST) 1.15f else if (isPressed) 0.9f else 1f,
                                animationSpec = tween(durationMillis = 200)
                            )
                            Icon(
                                Icons.Default.Info,
                                contentDescription = "随记",
                                modifier = Modifier.scale(scale.value),
                            )
                        },
                        label = { Text(text = "随记", fontSize = 13.sp, lineHeight = 13.sp) },
                        colors = navColors,
                        interactionSource = remember { MutableInteractionSource() },

                        )
                    NavigationBarItem(
                        selected = currentRoute == Routes.ProfileRoutes.PROFILE,
                        onClick = { if (currentRoute != Routes.ProfileRoutes.PROFILE) navController.navigate(Routes.ProfileRoutes.PROFILE) },
                        icon = {
                            val interactionSource = remember { MutableInteractionSource() }
                            val isPressed by interactionSource.collectIsPressedAsState()
                            val scale = animateFloatAsState(
                                targetValue = if (currentRoute == Routes.ProfileRoutes.PROFILE) 1.15f else if (isPressed) 0.9f else 1f,
                                animationSpec = tween(durationMillis = 200)
                            )
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "我的",
                                modifier = Modifier.scale(scale.value),
                            )
                        },
                        label = { Text(text = "我的", fontSize = 13.sp, lineHeight = 13.sp) },
                        colors = navColors,
                        interactionSource = remember { MutableInteractionSource() },

                        )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
        ) {
            AppNavigation(navController = navController)
        }
    }
}