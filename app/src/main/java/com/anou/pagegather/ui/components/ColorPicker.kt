package com.anou.pagegather.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

/**
 * 真正的可视化颜色选择器组件
 * 用户可以通过点击和拖拽来选择任何颜色
 */
@Composable
fun ColorPicker(
    initialColor: Color = Color.Red,
    onColorChanged: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedColor by remember { mutableStateOf(initialColor) }
    
    Column(modifier = modifier) {
        // 颜色预览
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(selectedColor)
        ) {
            Text(
                text = "当前颜色: #${Integer.toHexString(selectedColor.toArgb()).substring(2).uppercase()}",
                color = if (selectedColor.red * 0.299 + selectedColor.green * 0.587 + selectedColor.blue * 0.114 > 0.5) Color.Black else Color.White,
                modifier = Modifier.padding(16.dp)
            )
        }
        
        // 可视化颜色选择区域
        Text(
            text = "选择颜色",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )
        
        VisualColorPicker(
            selectedColor = selectedColor,
            onColorSelected = { color ->
                selectedColor = color
                onColorChanged(color)
            }
        )
    }
}

/**
 * 可视化颜色选择区域
 */
@Composable
private fun VisualColorPicker(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectorPosition by remember { mutableStateOf(Offset(100f, 100f)) }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    selectorPosition = offset
                    val color = getColorAtPosition(offset, size.width.toFloat(), size.height.toFloat())
                    onColorSelected(color)
                }
            }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    selectorPosition = change.position
                    val color = getColorAtPosition(change.position, size.width.toFloat(), size.height.toFloat())
                    onColorSelected(color)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawColorSpectrum(size.width, size.height)
        }
        
        // 改进的选择指示器
        SelectorIndicator(selectorPosition)
    }
}

/**
 * 选择指示器组件
 */
@Composable
private fun SelectorIndicator(position: Offset) {
    Canvas(
        modifier = Modifier
            .size(30.dp)
            .offset {
               IntOffset(
                    (position.x - 15).toInt(),
                    (position.y - 15).toInt()
                )
            }
    ) {
        // 外圈 - 白色圆环
        drawCircle(
            color = Color.White,
            radius = size.minDimension / 2 - 2,
            style = Stroke(width = 4f)
        )
        
        // 内圈 - 黑色圆环
        drawCircle(
            color = Color.Black,
            radius = size.minDimension / 2 - 6,
            style = Stroke(width = 2f)
        )
        
        // 中心点 - 白色小圆点
        drawCircle(
            color = Color.White,
            radius = 2f
        )
    }
}

/**
 * 根据位置获取颜色
 */
private fun getColorAtPosition(offset: Offset, width: Float, height: Float): Color {
    // 简化的颜色选择算法
    // X轴控制色相 (0-360度)
    // Y轴控制饱和度和亮度
    val hue = (offset.x / width * 360).coerceIn(0f, 359f)
    val saturation = (offset.y / height).coerceIn(0f, 1f)
    val value = 1f - (offset.y / height).coerceIn(0f, 1f)
    
    // 使用HSV颜色模型生成颜色
    return Color.hsv(hue, saturation, value)
}

/**
 * 绘制颜色光谱
 */
private fun DrawScope.drawColorSpectrum(width: Float, height: Float) {
    // 创建一个简单的颜色光谱
    for (y in 0 until height.toInt() step 2) {
        val saturation = y.toFloat() / height
        val value = 1f - y.toFloat() / height
        
        for (x in 0 until width.toInt() step 2) {
            val hue = x.toFloat() / width * 360
            val color = Color.hsv(hue, saturation, value)
            
            drawRect(
                color = color,
                topLeft = androidx.compose.ui.geometry.Offset(x.toFloat(), y.toFloat()),
                size = androidx.compose.ui.geometry.Size(2f, 2f)
            )
        }
    }
}