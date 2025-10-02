# PageGather 原生图表组件库使用指南

## 📊 概述

PageGather 应用使用完全原生的图表组件库，基于 Jetpack Compose Canvas 实现，无第三方依赖。本指南详细介绍了所有图表组件的使用方法、参数配置和最佳实践。

## 🎯 设计理念

### 核心原则
- **原生实现**：基于 Compose Canvas，无第三方依赖
- **Material Design**：遵循 Material 3 设计规范
- **高度可定制**：丰富的参数配置选项
- **性能优化**：智能算法，流畅渲染
- **响应式设计**：自适应不同屏幕尺寸

### 技术优势
- ✅ 完全控制渲染过程
- ✅ 与应用主题完美集成
- ✅ 减少应用体积
- ✅ 提升性能表现
- ✅ 便于维护和扩展

---

## 📈 图表组件类型

### 1. 柱状图 (BarChart)

#### 基本用法
```kotlin
BarChart(
    data = listOf(
        ChartDataPoint(0f, 180f, "2020", "180小时"),
        ChartDataPoint(1f, 220f, "2021", "220小时"),
        ChartDataPoint(2f, 195f, "2022", "195小时")
    ),
    modifier = Modifier
        .fillMaxWidth()
        .height(250.dp)
)
```

#### 完整参数列表
```kotlin
BarChart(
    data: List<ChartDataPoint>,           // 数据点列表
    modifier: Modifier = Modifier,        // 修饰符
    title: String = "",                   // 图表标题
    barColor: Color = ChartDefaults.primaryColor(), // 柱子颜色
    showValues: Boolean = true,           // 是否显示数值标签
    showGrid: Boolean = true,             // 是否显示网格线
    smartLabels: Boolean = false,         // 智能标签（避免重叠）
    roundedCorners: Boolean = true,       // 圆角柱子
    cornerRadius: Float = 12f,            // 圆角半径
    yAxisOnRight: Boolean = false,        // Y轴标签位置
    labelSpacing: Float = 16f             // 标签间距
)
```

#### 特色功能

##### 🔄 智能标签系统
解决X轴标签重叠问题，自动选择显示关键标签：
```kotlin
BarChart(
    data = denseDailyData, // 31个数据点
    smartLabels = true,    // 启用智能标签
    showValues = false     // 密集数据不显示数值
)
```

##### 🎨 圆角柱状图
现代化的圆角设计，可配置圆角半径：
```kotlin
BarChart(
    data = chartData,
    roundedCorners = true,  // 启用圆角
    cornerRadius = 12f      // 圆角半径
)
```

**圆角计算逻辑：**
- 不超过柱子宽度的1/2
- 不超过柱子高度的1/2
- 最大不超过设定值
- 自适应柱子尺寸

##### 📍 Y轴标签位置选择
支持左侧或右侧显示Y轴标签：
```kotlin
BarChart(
    data = chartData,
    yAxisOnRight = true,   // Y轴标签在右侧
    labelSpacing = 18f     // 调整间距
)
```

#### 使用场景
- ✅ 年度数据对比
- ✅ 月度趋势分析
- ✅ 分类数据展示
- ✅ 时间序列数据

---

### 2. 折线图 (LineChart)

#### 基本用法
```kotlin
LineChart(
    data = listOf(
        ChartDataPoint(0f, 25f, "1月", "25小时"),
        ChartDataPoint(1f, 30f, "2月", "30小时"),
        ChartDataPoint(2f, 28f, "3月", "28小时")
    ),
    showArea = true,
    modifier = Modifier
        .fillMaxWidth()
        .height(250.dp)
)
```

#### 完整参数列表
```kotlin
LineChart(
    data: List<ChartDataPoint>,           // 数据点列表
    modifier: Modifier = Modifier,        // 修饰符
    title: String = "",                   // 图表标题
    lineColor: Color = ChartDefaults.primaryColor(), // 线条颜色
    showPoints: Boolean = true,           // 显示数据点
    showGrid: Boolean = true,             // 显示网格线
    showArea: Boolean = false,            // 面积填充
    smoothCurve: Boolean = false,         // 平滑曲线
    yAxisOnRight: Boolean = false,        // Y轴标签位置
    labelSpacing: Float = 16f             // 标签间距
)
```

#### 特色功能

##### 🌊 平滑曲线
使用贝塞尔曲线创建平滑的线条和面积：
```kotlin
LineChart(
    data = monthlyData,
    smoothCurve = true,    // 启用平滑曲线
    showArea = true        // 面积填充也会平滑
)
```

##### 🎨 面积填充
渐变面积填充，增强视觉效果：
```kotlin
LineChart(
    data = trendData,
    showArea = true,       // 启用面积填充
    lineColor = MaterialTheme.colorScheme.secondary
)
```

#### 使用场景
- ✅ 趋势分析
- ✅ 时间序列数据
- ✅ 连续数据展示
- ✅ 变化趋势可视化

---

### 3. 高级折线图 (AdvancedLineChart)

#### 基本用法
```kotlin
AdvancedLineChart(
    dataSeries = listOf(
        ChartDataSeries("已读完", series1Data, Color(0xFF10B981)),
        ChartDataSeries("在读中", series2Data, Color(0xFFEF4444))
    ),
    showArea = true,
    modifier = Modifier
        .fillMaxWidth()
        .height(250.dp)
)
```

#### 完整参数列表
```kotlin
AdvancedLineChart(
    dataSeries: List<ChartDataSeries>,    // 多系列数据
    modifier: Modifier = Modifier,        // 修饰符
    title: String = "",                   // 图表标题
    showPoints: Boolean = true,           // 显示数据点
    showGrid: Boolean = true,             // 显示网格线
    showArea: Boolean = false,            // 面积填充
    yAxisOnRight: Boolean = false,        // Y轴标签位置
    labelSpacing: Float = 24f,            // 标签与图表的间距
    animationEnabled: Boolean = true      // 动画效果
)
```

#### 特色功能

##### 📊 多系列对比
支持多条线同时显示，自动生成图例：
```kotlin
val dataSeries = listOf(
    ChartDataSeries("系列1", data1, Color(0xFF10B981)),
    ChartDataSeries("系列2", data2, Color(0xFFEF4444)),
    ChartDataSeries("系列3", data3, Color(0xFF3B82F6))
)

AdvancedLineChart(
    dataSeries = dataSeries,
    showArea = true  // 每个系列都有渐变填充
)
```

##### 🎨 自动图例
多系列时自动显示图例：
- 颜色圆点标识
- 系列名称显示
- 水平排列布局

#### 使用场景
- ✅ 多维度数据对比
- ✅ 趋势对比分析
- ✅ 复杂数据关系展示
- ✅ 业务指标监控

---

### 4. 饼图 (PieChart)

#### 基本用法
```kotlin
PieChart(
    segments = listOf(
        PieChartSegment(35f, "购买", Color(0xFF6366F1)),
        PieChartSegment(22f, "图书馆", Color(0xFF8B5CF6)),
        PieChartSegment(18f, "朋友推荐", Color(0xFFEC4899))
    ),
    isDonut = true,
    modifier = Modifier
        .fillMaxWidth()
        .height(250.dp)
)
```

#### 完整参数列表
```kotlin
PieChart(
    segments: List<PieChartSegment>,      // 扇形数据
    modifier: Modifier = Modifier,        // 修饰符
    title: String = "",                   // 图表标题
    showPercentages: Boolean = true,      // 显示外部百分比标签（带指示线）
    showLegend: Boolean = true,           // 显示图例
    legendPosition: LegendPosition = LegendPosition.Right, // 图例位置
    isDonut: Boolean = false,             // 环形图模式
    donutHoleRatio: Float = 0.4f,        // 环形图内径比例（0.0-1.0）
    segmentSpacing: Float = 2f,           // 扇形间隙角度（度数）
    animationEnabled: Boolean = true      // 动画效果（预留参数）
)
```

#### 特色功能

##### 🍩 环形图模式
现代化的环形设计：
```kotlin
PieChart(
    segments = pieData,
    isDonut = true,        // 环形图
    showPercentages = true,
    showLegend = true
)
```

##### 📋 自动图例
智能图例布局：
- 颜色方块标识
- 标签和百分比显示
- 响应式布局

##### 🔄 扇形间隙功能
在扇形之间创建视觉分隔，提高图表可读性：
```kotlin
PieChart(
    segments = pieData,
    segmentSpacing = 5f,   // 5度间隙
    isDonut = false,       // 实心饼图更好地展示间隙效果
    showPercentages = true
)
```

**间隙功能特点：**
- 可配置间隙角度（建议0-10度）
- 自动调整扇形大小以适应间隙
- 外部标签位置自动适配间隙
- 保持数据比例的准确性

##### 🏷️ 外部标签系统（已优化）
智能的外部标签显示：
```kotlin
PieChart(
    segments = pieData,
    showPercentages = true,  // 启用外部标签
    showLegend = false       // 可选择只显示外部标签
)
```

**外部标签优化特点：**
- 标签显示在饼图外围，距离增加到50像素，完全避免遮挡
- 通过**加粗指示线**（3像素宽）连接到对应扇形
- 指示线终点添加**小圆点**，增强视觉连接
- 移除白色背景，使用**文字阴影**提高可读性
- 只有角度大于3度的扇形才显示标签（避免重叠）
- 自动适配扇形间隙位置

##### 📍 图例位置选择
支持右侧和底部两种图例布局：
```kotlin
// 右侧图例（默认，适合项目较少的情况）
PieChart(
    segments = pieData,
    legendPosition = LegendPosition.Right
)

// 底部图例（适合项目较多的情况）
PieChart(
    segments = pieData,
    legendPosition = LegendPosition.Bottom
)
```

**图例位置对比：**
- **右侧图例**：传统布局，适合3-6个项目，节省垂直空间
- **底部图例**：网格布局，适合6个以上项目，更好的空间利用
- **底部图例**：每行最多2个项目，自动换行，避免滚动问题

#### 使用场景
- ✅ 占比分析
- ✅ 分类数据展示
- ✅ 构成比例可视化
- ✅ 市场份额分析

---

### 5. 半圆形进度条 (SemicircularProgressIndicator)

#### 基本用法
```kotlin
SemicircularProgressIndicator(
    progress = 0.65f,
    modifier = Modifier
        .fillMaxWidth()
        .height(120.dp),
    color = MaterialTheme.colorScheme.primary
)
```

#### 完整参数列表
```kotlin
SemicircularProgressIndicator(
    progress: Float,                      // 进度值 (0.0-1.0)
    modifier: Modifier = Modifier,        // 修饰符
    color: Color = ChartDefaults.primaryColor(), // 进度条颜色
    trackColor: Color = Color.Gray.copy(alpha = 0.3f), // 轨道颜色
    strokeWidth: Dp = 12.dp,             // 线条宽度
    animationEnabled: Boolean = true      // 动画效果
)
```

#### 使用场景
- ✅ 进度展示
- ✅ 完成度指示
- ✅ 目标达成率
- ✅ 仪表盘设计

---

## 🎨 布局优化系统

### 动态Padding计算

我们的图表组件使用智能的padding系统，根据标签位置和内容动态调整空间分配：

```kotlin
// 基础设计原理
val basePadding = 20f                    // 基础边距
val yAxisLabelSpace = 50f + labelSpacing // Y轴标签所需空间

val leftPadding = if (yAxisOnRight) basePadding else yAxisLabelSpace
val rightPadding = if (yAxisOnRight) yAxisLabelSpace else basePadding
```

#### 空间分配逻辑

##### Y轴标签在左侧时
```
|<--yAxisLabelSpace-->|<--图表区域-->|<--basePadding-->|
|     Y轴标签区域      |    图表内容   |   基础边距     |
|      (66像素)       |             |   (20像素)     |
```

##### Y轴标签在右侧时
```
|<--basePadding-->|<--图表区域-->|<--yAxisLabelSpace-->|
|   基础边距     |    图表内容   |     Y轴标签区域      |
|   (20像素)     |             |      (66像素)       |
```

### 标签间距优化

#### 问题解决
- **原问题**：Y轴标签与图表距离太近，影响可读性
- **解决方案**：添加可配置的 `labelSpacing` 参数
- **效果**：标签与图表保持合适的视觉间距

#### 实现细节
```kotlin
// Y轴标签位置计算
val labelX = if (yAxisOnRight) {
    leftPadding + chartWidth + labelSpacing  // 右侧：图表边缘 + 间距
} else {
    basePadding  // 左侧：从边缘开始，预留足够空间
}

// X轴标签位置计算
val labelY = topPadding + chartHeight + labelSpacing
```

---

## 🔧 高级配置

### 智能标签系统

当数据点过多时，传统的标签显示会导致重叠。我们的智能标签系统能够：

#### 功能特点
- 自动检测标签重叠
- 智能选择显示关键标签
- 保持数据可读性
- 适应不同数据密度

#### 使用示例
```kotlin
// 密集数据（31个数据点）
BarChart(
    data = augustDailyData,  // 31天的数据
    smartLabels = true,      // 启用智能标签
    showValues = false,      // 不显示数值（避免拥挤）
    labelSpacing = 18f       // 适当增加间距
)
```

#### 算法逻辑
```kotlin
// 智能标签显示间隔计算
val displayInterval = (dataSize / maxDisplayLabels).coerceAtLeast(1)

// 只显示关键位置的标签
if (index % displayInterval == 0) {
    // 显示此标签
}
```

### 圆角系统

#### 自适应圆角计算
```kotlin
val actualCornerRadius = minOf(
    cornerRadius,        // 用户设定值
    barWidth / 2,       // 不超过宽度一半
    barHeight / 2       // 不超过高度一半
)
```

#### 设计考虑
- **小柱子**：自动减小圆角，避免过度圆润
- **大柱子**：保持设定圆角，维持美观
- **零高度**：跳过圆角处理，优化性能

### 平滑曲线算法

#### 贝塞尔曲线实现
```kotlin
// 控制点计算
val controlPoint1X = previousPoint.x + (currentPoint.x - previousPoint.x) * 0.5f
val controlPoint1Y = previousPoint.y
val controlPoint2X = currentPoint.x - (currentPoint.x - previousPoint.x) * 0.5f
val controlPoint2Y = currentPoint.y

// 绘制平滑曲线
cubicTo(
    controlPoint1X, controlPoint1Y,
    controlPoint2X, controlPoint2Y,
    currentPoint.x, currentPoint.y
)
```

#### 一致性保证
- 线条和面积使用相同算法
- 视觉效果完全一致
- 避免视觉不协调

---

## 📱 实际应用示例

### 统计页面集成

#### 年度阅读时长分布
```kotlin
BarChart(
    data = yearlyReadingData,
    title = "年度阅读时长分布",
    showValues = true,
    showGrid = true,
    smartLabels = false,      // 年度数据点少，不需要智能标签
    roundedCorners = true,    // 启用圆角柱子
    cornerRadius = 12f,       // 设置圆角半径
    yAxisOnRight = false,     // Y轴标签在左侧
    labelSpacing = 20f,       // 增加标签间距
    barColor = MaterialTheme.colorScheme.primary
)
```

#### 月度阅读趋势
```kotlin
LineChart(
    data = monthlyReadingData,
    title = "月度阅读时长分布（2024年）",
    showPoints = true,
    showGrid = true,
    showArea = true,
    smoothCurve = true,       // 启用平滑曲线
    yAxisOnRight = false,     // Y轴标签在左侧
    labelSpacing = 20f,       // 增加标签间距
    lineColor = MaterialTheme.colorScheme.secondary
)
```

#### 8月每日阅读分布
```kotlin
BarChart(
    data = augustReadingData, // 31个数据点
    title = "8月阅读时长分布（按日）",
    showValues = false,       // 数据点太多，不显示具体数值
    showGrid = true,
    smartLabels = true,       // 启用智能标签显示，避免重叠
    roundedCorners = true,    // 启用圆角柱子
    cornerRadius = 10f,       // 设置圆角半径
    yAxisOnRight = true,      // Y轴标签在右侧（尝试不同布局）
    labelSpacing = 18f,       // 增加标签间距
    barColor = MaterialTheme.colorScheme.tertiary
)
```

### 开发者选项展示

在 **我的 → 开发者选项 → 图表展示** 中，可以查看所有图表组件的完整演示，包括：

- 书籍来源分布饼图（环形图 + 右侧图例）
- 书籍类型分布饼图（外部标签 + 无图例）
- 扇形间隙效果演示（5度间隙 + 优化指示线）
- 底部图例布局演示（网格布局 + 更好空间利用）
- 年度阅读时长分布（圆角柱状图）
- 月度阅读时长分布（面积折线图）
- 8月阅读时长分布（智能标签柱状图）
- 阅读趋势对比（高级折线图）
- 半圆形进度条

每个图表都包含完整的 Jetpack Compose Preview，便于开发和调试。

---

## 🎯 最佳实践

### 参数配置建议

#### 标准配置（推荐）
```kotlin
// 柱状图标准配置
BarChart(
    data = chartData,
    roundedCorners = true,    // 现代化圆角
    cornerRadius = 12f,       // 适中的圆角
    yAxisOnRight = false,     // 传统左侧Y轴
    labelSpacing = 16f,       // 标准间距
    smartLabels = false       // 少量数据不需要
)

// 折线图标准配置
LineChart(
    data = chartData,
    showArea = true,          // 面积填充增强视觉
    smoothCurve = true,       // 平滑曲线更美观
    yAxisOnRight = false,     // 传统左侧Y轴
    labelSpacing = 16f        // 标准间距
)
```

#### 密集数据配置
```kotlin
BarChart(
    data = denseData,         // 大量数据点
    smartLabels = true,       // 必须启用智能标签
    showValues = false,       // 不显示数值避免拥挤
    roundedCorners = true,    // 圆角让密集柱子更柔和
    cornerRadius = 8f,        // 较小圆角适合密集布局
    labelSpacing = 12f        // 较小间距节省空间
)
```

#### 紧凑布局配置
```kotlin
LineChart(
    data = chartData,
    showPoints = false,       // 不显示数据点
    showArea = false,         // 不使用面积填充
    smoothCurve = false,      // 使用直线连接
    labelSpacing = 12f        // 较小间距
)
```

### 颜色选择建议

#### 使用主题颜色
```kotlin
// 推荐：使用Material主题颜色
barColor = MaterialTheme.colorScheme.primary
lineColor = MaterialTheme.colorScheme.secondary

// 或使用图表默认颜色
barColor = ChartDefaults.primaryColor()
```

#### 多系列颜色搭配
```kotlin
val colors = listOf(
    Color(0xFF10B981),  // 绿色
    Color(0xFFEF4444),  // 红色
    Color(0xFF3B82F6),  // 蓝色
    Color(0xFFF59E0B),  // 橙色
    Color(0xFF8B5CF6)   // 紫色
)
```

### 性能优化建议

#### 数据量控制
- **柱状图**：建议不超过50个数据点
- **折线图**：建议不超过100个数据点
- **饼图**：建议不超过10个扇形

#### 动画使用
```kotlin
// 大数据量时关闭动画
AdvancedLineChart(
    dataSeries = largeDataSeries,
    animationEnabled = false  // 提升性能
)
```

---

## 🔍 故障排除

### 常见问题

#### 1. 标签重叠
**问题**：X轴标签相互重叠，难以阅读
**解决**：
```kotlin
BarChart(
    data = denseData,
    smartLabels = true,    // 启用智能标签
    labelSpacing = 20f     // 增加间距
)
```

#### 2. 图表显示不完整
**问题**：图表边缘被裁剪
**解决**：
```kotlin
BarChart(
    data = chartData,
    modifier = Modifier
        .fillMaxWidth()
        .height(300.dp)    // 增加高度
        .padding(16.dp)    // 添加外边距
)
```

#### 3. 颜色不协调
**问题**：图表颜色与应用主题不匹配
**解决**：
```kotlin
BarChart(
    data = chartData,
    barColor = MaterialTheme.colorScheme.primary  // 使用主题颜色
)
```

#### 4. 性能问题
**问题**：大数据量时渲染卡顿
**解决**：
```kotlin
BarChart(
    data = largeData,
    showValues = false,      // 关闭数值显示
    smartLabels = true,      // 减少标签数量
    animationEnabled = false // 关闭动画
)
```

### 调试技巧

#### 1. 使用Preview
```kotlin
@Preview(showBackground = true)
@Composable
private fun ChartPreview() {
    PageGatherTheme {
        BarChart(
            data = sampleData,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        )
    }
}
```

#### 2. 数据验证
```kotlin
// 确保数据格式正确
val chartData = listOf(
    ChartDataPoint(0f, 100f, "标签", "100单位"),  // x, y, label, value
    // ...
)
```

#### 3. 布局检查
```kotlin
// 使用边框检查布局
BarChart(
    data = chartData,
    modifier = Modifier
        .fillMaxWidth()
        .height(250.dp)
        .border(1.dp, Color.Red)  // 临时边框
)
```

---

## 📚 API 参考

### 数据结构

#### ChartDataPoint
```kotlin
data class ChartDataPoint(
    val x: Float,        // X轴数值
    val y: Float,        // Y轴数值
    val label: String,   // 显示标签
    val value: String    // 数值标签
)
```

#### ChartDataSeries
```kotlin
data class ChartDataSeries(
    val name: String,                    // 系列名称
    val data: List<ChartDataPoint>,      // 数据点列表
    val color: Color                     // 系列颜色
)
```

#### PieChartSegment
```kotlin
data class PieChartSegment(
    val value: Float,    // 数值
    val label: String,   // 标签
    val color: Color     // 颜色
)
```

### 工具类

#### ChartDefaults
```kotlin
object ChartDefaults {
    fun primaryColor(): Color           // 主要颜色
    fun onSurfaceColor(): Color        // 文字颜色
    fun surfaceColor(): Color          // 背景颜色
}
```

#### ChartUtils
```kotlin
object ChartUtils {
    fun formatValue(value: Float): String              // 格式化数值
    fun calculateOptimalLabelCount(dataSize: Int): Int // 计算最佳标签数量
}
```

---

## 🚀 未来规划

### 计划新增功能 
- [ ] 雷达图组件
- [ ] 热力图组件
- [ ] 更多动画效果
- [ ] 交互功能（点击、缩放）
- [ ] 数据导出功能

### 性能优化计划
- [ ] 虚拟化渲染（大数据量）
- [ ] 渲染缓存机制
- [ ] 异步数据处理
- [ ] 内存使用优化

---

## 📞 支持与反馈

如果在使用过程中遇到问题或有改进建议，请：

1. 查看本文档的故障排除部分
2. 检查开发者选项中的图表展示页面
3. 参考代码中的 Preview 示例
4. 提交 Issue 或 Pull Request

---

## 📄 更新日志

### v1.2.0 (当前版本)
- ✅ 完整的图表组件库
- ✅ 智能标签系统
- ✅ 圆角柱状图
- ✅ 平滑曲线支持
- ✅ 布局优化系统
- ✅ 开发者展示页面
- ✅ 完整的文档和示例
- 🆕 饼图扇形间隙功能
- 🆕 外部标签系统优化（解决遮挡问题）
- 🆕 图例位置选择（右侧/底部）
- 🆕 指示线视觉增强（加粗+圆点）
- 🆕 底部图例网格布局
- 🆕 高级折线图标签间距同步优化
- 🆕 详细参数注释说明

### v1.1.0
- ✅ 饼图扇形间隙功能
- ✅ 外部标签系统
- ✅ 详细参数注释

### v1.0.0
- ✅ 基础图表组件实现
- ✅ Material Design 集成
- ✅ 响应式布局支持

---

*本文档持续更新中，最后更新时间：2025年10月*