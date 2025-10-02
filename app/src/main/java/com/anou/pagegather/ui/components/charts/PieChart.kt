package com.anou.pagegather.ui.components.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import kotlin.math.*

/**
 * 图例位置枚举
 */
enum class LegendPosition {
    Right,   // 右侧（默认）
    Bottom   // 底部
}

/**
 * 原生饼图组件
 * 
 * @param segments 饼图数据段列表，每个段包含标签、数值和颜色
 * @param modifier 修饰符，用于控制组件的布局和样式
 * @param title 图表标题，为空时不显示标题
 * @param showPercentages 是否显示外部百分比标签（带指示线）
 *                       注意：标签显示在饼图外围，通过指示线连接到对应扇形
 *                       只有角度大于5度的扇形才会显示标签，避免重叠
 * @param showLegend 是否显示图例，包含颜色标识、标签名称、数值和百分比
 * @param legendPosition 图例位置，Right（右侧）或Bottom（底部）
 * @param isDonut 是否为环形图模式，true时中心为空心
 * @param donutHoleRatio 环形图内径比例（0.0-1.0），仅在isDonut=true时生效
 *                      0.0表示无空心，1.0表示完全空心（不可见）
 * @param segmentSpacing 扇形之间的间隙角度（度数），用于在扇形之间创建视觉分隔
 *                      建议值：0-10度，过大会影响视觉效果
 * @param animationEnabled 是否启用动画效果（预留参数，当前版本暂未实现动画）
 */
@Composable
fun PieChart(
    segments: List<PieChartSegment>,
    modifier: Modifier = Modifier,
    title: String = "",
    showPercentages: Boolean = true,
    showLegend: Boolean = true,
    legendPosition: LegendPosition = LegendPosition.Right, // 图例位置
    isDonut: Boolean = false,
    donutHoleRatio: Float = 0.4f,
    segmentSpacing: Float = 2f, // 扇形之间的间隙（默认2度）
    animationEnabled: Boolean = true // 动画效果（预留参数）
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    
    Column(modifier = modifier) {
        if (title.isNotEmpty()) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        if (segments.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "暂无数据",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            return@Column
        }
        
        if (showLegend) {
            when (legendPosition) {
                LegendPosition.Right -> {
                    // 右侧图例布局
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // 饼图
                        Canvas(
                            modifier = Modifier
                                .size(320.dp) // 进一步增加尺寸为外部标签留出充足空间
                                .weight(1f)
                        ) {
                            drawPieChart(
                                segments = segments,
                                showPercentages = showPercentages,
                                isDonut = isDonut,
                                donutHoleRatio = donutHoleRatio,
                                segmentSpacing = segmentSpacing,
                                textMeasurer = textMeasurer,
                                baseTextStyle = TextStyle(
                                    fontSize = with(density) { 12.dp.toSp() },
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        
                        // 右侧图例
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(segments) { segment ->
                                LegendItem(
                                    color = segment.color,
                                    label = segment.label,
                                    value = segment.value,
                                    percentage = segment.value / segments.sumOf { it.value.toDouble() }.toFloat() * 100
                                )
                            }
                        }
                    }
                }
                LegendPosition.Bottom -> {
                    // 底部图例布局
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 饼图
                        Canvas(
                            modifier = Modifier.size(400.dp) // 底部图例时需要更大尺寸避免标签遮挡
                        ) {
                            drawPieChart(
                                segments = segments,
                                showPercentages = showPercentages,
                                isDonut = isDonut,
                                donutHoleRatio = donutHoleRatio,
                                segmentSpacing = segmentSpacing,
                                textMeasurer = textMeasurer,
                                baseTextStyle = TextStyle(
                                    fontSize = with(density) { 12.dp.toSp() },
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // 底部图例 - 使用网格布局
                        BottomLegend(segments = segments)
                    }
                }
            }
        } else {
            // 无图例时饼图居中显示
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier.size(400.dp) // 无图例时需要足够大避免标签遮挡
                ) {
                    drawPieChart(
                        segments = segments,
                        showPercentages = showPercentages,
                        isDonut = isDonut,
                        donutHoleRatio = donutHoleRatio,
                        segmentSpacing = segmentSpacing,
                        textMeasurer = textMeasurer,
                        baseTextStyle = TextStyle(
                            fontSize = with(density) { 12.dp.toSp() },
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String,
    value: Float,
    percentage: Float
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 4.dp), // 简单的padding，无背景
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            modifier = Modifier.size(14.dp),
            shape = CircleShape,
            color = color
        ) {}
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
            Text(
                text = "${value.toInt()} (${String.format("%.1f", percentage)}%)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun BottomLegend(segments: List<PieChartSegment>) {
    val total = segments.sumOf { it.value.toDouble() }.toFloat()
    
    // 使用LazyVerticalGrid布局，更好的空间利用
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 200.dp), // 限制最大高度，避免占用过多空间
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 将segments按每行3个分组
        val chunkedSegments = segments.chunked(3)
        items(chunkedSegments) { rowSegments ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                rowSegments.forEach { segment ->
                    BottomLegendItem(
                        color = segment.color,
                        label = segment.label,
                        value = segment.value,
                        percentage = segment.value / total * 100,
                        modifier = Modifier.weight(1f)
                    )
                }
                // 填充空白位置
                repeat(3 - rowSegments.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun BottomLegendItem(
    color: Color,
    label: String,
    value: Float,
    percentage: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Surface(
                modifier = Modifier.size(8.dp),
                shape = CircleShape,
                color = color
            ) {}
            
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
        }
        
        Text(
            text = "${String.format("%.1f", percentage)}%",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun DrawScope.drawPieChart(
    segments: List<PieChartSegment>,
    showPercentages: Boolean,
    isDonut: Boolean,
    donutHoleRatio: Float,
    segmentSpacing: Float,
    textMeasurer: TextMeasurer,
    baseTextStyle: TextStyle
) {
    val total = segments.sumOf { it.value.toDouble() }.toFloat()
    if (total <= 0f) return
    
    val center = Offset(size.width / 2, size.height / 2)
    val radius = min(size.width, size.height) / 2 * 0.4f // 大幅减小半径为外部标签留出充足空间
    val innerRadius = if (isDonut) radius * donutHoleRatio else 0f
    
    // 计算总的间隙角度
    val totalSpacing = segmentSpacing * segments.size
    // 可用于绘制扇形的总角度
    val availableAngle = 360f - totalSpacing
    
    var startAngle = -90f // 从顶部开始
    
    segments.forEach { segment ->
        // 根据可用角度计算扇形角度
        val sweepAngle = (segment.value / total) * availableAngle
        
        // 绘制扇形
        if (isDonut) {
            drawArc(
                color = segment.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = radius - innerRadius)
            )
        } else {
            drawArc(
                color = segment.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2)
            )
        }
        
        // 移动到下一个扇形的起始位置（包含间隙）
        startAngle += sweepAngle + segmentSpacing
    }
    
    // 绘制外部标签和指示线
    if (showPercentages) {
        drawExternalLabels(
            segments = segments,
            total = total,
            center = center,
            radius = radius,
            segmentSpacing = segmentSpacing,
            textMeasurer = textMeasurer,
            textStyle = baseTextStyle
        )
    }
}

/**
 * 绘制外部标签和指示线
 * 注意：标签确实显示在饼图外围，通过指示线连接到扇形中心
 */
private fun DrawScope.drawExternalLabels(
    segments: List<PieChartSegment>,
    total: Float,
    center: Offset,
    radius: Float,
    segmentSpacing: Float,
    textMeasurer: TextMeasurer,
    textStyle: TextStyle
) {
    // 进一步增加标签距离，完全避免遮挡饼图
    val labelRadius = radius + 70f // 标签距离圆心的距离（增加到70f）
    val lineStartRadius = radius + 10f // 指示线起点距离圆心的距离
    val lineEndRadius = radius + 60f // 指示线终点距离圆心的距离
    
    // 计算总的间隙角度和可用角度
    val totalSpacing = segmentSpacing * segments.size
    val availableAngle = 360f - totalSpacing
    
    var startAngle = -90f
    
    segments.forEach { segment ->
        // 根据可用角度计算扇形角度（与绘制扇形时保持一致）
        val sweepAngle = (segment.value / total) * availableAngle
        
        // 只为足够大的扇形绘制标签
        if (sweepAngle > 3f) { // 考虑间隙后降低阈值
            val percentage = (segment.value / total * 100)
            val labelText = "${String.format("%.1f", percentage)}%"
            
            // 标签指向扇形的中心角度
            val labelAngle = Math.toRadians((startAngle + sweepAngle / 2).toDouble())
            
            // 计算指示线的起点和终点
            val lineStartX = center.x + cos(labelAngle).toFloat() * lineStartRadius
            val lineStartY = center.y + sin(labelAngle).toFloat() * lineStartRadius
            val lineEndX = center.x + cos(labelAngle).toFloat() * lineEndRadius
            val lineEndY = center.y + sin(labelAngle).toFloat() * lineEndRadius
            
            // 计算标签位置（确保在饼图外围）
            val labelX = center.x + cos(labelAngle).toFloat() * labelRadius
            val labelY = center.y + sin(labelAngle).toFloat() * labelRadius
            
            // 绘制更明显的指示线
            drawLine(
                color = segment.color,
                start = Offset(lineStartX, lineStartY),
                end = Offset(lineEndX, lineEndY),
                strokeWidth = 3f // 增加线条宽度
            )
            
            // 在指示线终点绘制小圆点，增强视觉连接
            drawCircle(
                color = segment.color,
                radius = 3f,
                center = Offset(lineEndX, lineEndY)
            )
            
            // 直接绘制标签文字，不使用背景
            // 根据位置选择合适的文字颜色
            val textColor = if (labelAngle > Math.PI / 2 && labelAngle < 3 * Math.PI / 2) {
                // 左侧区域使用深色文字
                Color.Black
            } else {
                // 右侧区域使用深色文字
                Color.Black
            }
            
            val labelTextStyle = textStyle.copy(color = textColor)
            val finalTextResult = textMeasurer.measure(labelText, labelTextStyle)
            
            // 添加文字阴影效果，提高可读性
            // 先绘制阴影
            val shadowTextStyle = textStyle.copy(color = Color.White.copy(alpha = 0.8f))
            val shadowTextResult = textMeasurer.measure(labelText, shadowTextStyle)
            drawText(
                textLayoutResult = shadowTextResult,
                topLeft = Offset(
                    labelX - shadowTextResult.size.width / 2 + 1f,
                    labelY - shadowTextResult.size.height / 2 + 1f
                )
            )
            
            // 再绘制正文
            drawText(
                textLayoutResult = finalTextResult,
                topLeft = Offset(
                    labelX - finalTextResult.size.width / 2,
                    labelY - finalTextResult.size.height / 2
                )
            )
        }
        
        // 移动到下一个扇形的起始位置（包含间隙，与绘制扇形时保持一致）
        startAngle += sweepAngle + segmentSpacing
    }
}

/**
 * 根据背景颜色选择合适的文字颜色（黑色或白色）
 */
private fun getContrastColor(backgroundColor: Color): Color {
    // 计算颜色的相对亮度
    val luminance = 0.299 * backgroundColor.red + 0.587 * backgroundColor.green + 0.114 * backgroundColor.blue
    
    // 如果背景较暗，使用白色文字；如果背景较亮，使用黑色文字
    return if (luminance > 0.5f) Color.Black else Color.White
}