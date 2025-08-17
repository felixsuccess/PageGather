package com.anou.pagegather.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anou.pagegather.ui.navigation.MainScreen

// 颜色主题预览组件
@Preview(showBackground = true)
@Composable
fun ColorSchemePreview() {
    PageGatherTheme {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // 主色调展示
            ColorBlock(
                color = MaterialTheme.colorScheme.primary,
                text = "Primary (#00B489)",
                textColor = MaterialTheme.colorScheme.onPrimary
            )

            // 辅助色展示
            ColorBlock(
                color = MaterialTheme.colorScheme.secondary,
                text = "Secondary (#4A5568)",
                textColor = MaterialTheme.colorScheme.onSecondary
            )

            // 强调色展示
            ColorBlock(
                color = MaterialTheme.colorScheme.tertiary,
                text = "Accent (#FF7D00)",
                textColor = MaterialTheme.colorScheme.onTertiary
            )

            // 背景色展示
            ColorBlock(
                color = MaterialTheme.colorScheme.background,
                text = "Background",
                textColor = MaterialTheme.colorScheme.onBackground
            )

            // 表面色展示
            ColorBlock(
                color = MaterialTheme.colorScheme.surface,
                text = "Surface",
                textColor = MaterialTheme.colorScheme.onSurface
            )

            // 扩展颜色展示
            ColorBlock(
                color = MaterialTheme.extendedColors.primaryContainer,
                text = "Primary Container",
                textColor = MaterialTheme.colorScheme.onPrimary
            )

            ColorBlock(
                color = MaterialTheme.extendedColors.success,
                text = "Success (#38A169)",
                textColor = Color.White
            )

            ColorBlock(
                color = MaterialTheme.extendedColors.error,
                text = "Error (#E53E3E)",
                textColor = Color.White
            )

            // 功能按钮
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {}) {
                Text("使用主色调的按钮")
            }
        }
    }
}

// 深色模式预览
@Preview(showBackground = true, uiMode = 0x20)
@Composable
fun DarkColorSchemePreview() {
    ColorSchemePreview()
}

// 主屏幕预览
@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    PageGatherTheme {
        MainScreen()
    }
}

// 颜色块组件
@Composable
private fun ColorBlock(
    color: Color,
    text: String,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color)
            .padding(16.dp)
    ) {
        Text(
            text = text,
            color = textColor
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
}