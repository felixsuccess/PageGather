package com.anou.pagegather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.anou.pagegather.ui.navigation.MainScreen
import com.anou.pagegather.ui.navigation.Routes
import com.anou.pagegather.ui.theme.PageGatherTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PageGatherTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainContent(
                        modifier = Modifier.padding(innerPadding), route = null
                    )
                }
            }
        }
    }
}


@Composable
fun MainContent(
    modifier: Modifier = Modifier, route: String?
) {
    val navController = rememberNavController()
    var isSplash = remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(2000) // 延迟 2 秒
        isSplash.value = false
    }
    if (isSplash.value) {
        SplashScreen(onClick = { navController.navigate(Routes.MainRoutes.MAIN) })
    } else {
        MainScreen()
    }
}


@Composable
fun SplashScreen(onClick: () -> Unit) {
    Text("捕捉字里行间的光")

    Button(onClick = onClick) {

        Text("出发")
    }

}