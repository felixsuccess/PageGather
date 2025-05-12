package com.anou.pagegather.ui.navigation


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.anou.pagegather.ui.theme.TextDark
import com.anou.pagegather.ui.theme.TextGray
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
    val selectedColor: Color = TextDark
    val unselectedColor: Color = TextGray
    val navColors=    NavigationBarItemDefaults.colors(
        indicatorColor = Color.Transparent,
        selectedIconColor = (selectedColor),
        selectedTextColor = (selectedColor),
        unselectedIconColor = (unselectedColor),
        unselectedTextColor = (unselectedColor)
    )
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == Routes.DashboardRoutes.STATISTICS,
                        onClick = { navController.navigate(Routes.DashboardRoutes.STATISTICS) },
                        icon = { Icon(Icons.Default.DateRange, contentDescription = "统计") },
                        label = { Text(text = "统计", fontSize = 13.sp, lineHeight = 13.sp) },
                        colors = navColors

                    )
                    NavigationBarItem(
                        selected = currentRoute == Routes.BookRoutes.BOOKS,
                        onClick = { navController.navigate(Routes.BookRoutes.BOOKS) },
                        icon = { Icon(Icons.Default.Menu, contentDescription = "书架") },
                        label = { Text(text = "书架", fontSize = 13.sp, lineHeight = 13.sp) },
                        colors = navColors
                    )

                    NavigationBarItem(
                        selected = currentRoute == Routes.NoteRoutes.NOTE_LIST,
                        onClick = { navController.navigate(Routes.NoteRoutes.NOTE_LIST) },
                        icon = { Icon(Icons.Default.Info, contentDescription = "随记") },
                        label = { Text(text = "随记", fontSize = 13.sp, lineHeight = 13.sp) },
                        colors = navColors
                    )

                    NavigationBarItem(
                        selected = currentRoute == Routes.ProfileRoutes.PROFILE,
                        onClick = { navController.navigate(Routes.ProfileRoutes.PROFILE) },
                        icon = { Icon(Icons.Default.Person, contentDescription = "我的") },
                        label = { Text(text = "我的", fontSize = 13.sp, lineHeight = 13.sp) },
                        colors = navColors
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