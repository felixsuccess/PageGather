# 读书记录应用 UI 设计规范

## 概述

本文档定义了读书记录应用的界面设计规范，包括布局结构、视觉样式、交互规范等，基于 Material Design 3 设计系统。

## 设计系统

### 颜色系统 (Material 3)

#### 浅色主题
```
Primary: #6750A4          // 主色调 - 紫色
OnPrimary: #FFFFFF        // 主色调上的文字
PrimaryContainer: #EADDFF // 主色调容器
OnPrimaryContainer: #21005D // 主色调容器上的文字

Secondary: #625B71        // 次要色 - 灰紫色
OnSecondary: #FFFFFF      // 次要色上的文字
SecondaryContainer: #E8DEF8 // 次要色容器
OnSecondaryContainer: #1D192B // 次要色容器上的文字

Background: #FFFBFE       // 背景色
OnBackground: #1C1B1F     // 背景上的文字
Surface: #F7F2FA          // 表面色
OnSurface: #1C1B1F        // 表面上的文字

Error: #BA1A1A            // 错误色
OnError: #FFFFFF          // 错误色上的文字
```

#### 深色主题
```
Primary: #D0BCFF          // 主色调
OnPrimary: #381E72        // 主色调上的文字
PrimaryContainer: #4F378B // 主色调容器
OnPrimaryContainer: #EADDFF // 主色调容器上的文字

Secondary: #CCC2DC        // 次要色
OnSecondary: #332D41      // 次要色上的文字
SecondaryContainer: #4A4458 // 次要色容器
OnSecondaryContainer: #E8DEF8 // 次要色容器上的文字

Background: #1C1B1F       // 背景色
OnBackground: #E6E1E5     // 背景上的文字
Surface: #2B2930          // 表面色
OnSurface: #E6E1E5        // 表面上的文字

Error: #FFB4AB            // 错误色
OnError: #690005          // 错误色上的文字
```

### 字体系统

```
Display Large: 57sp, Regular, -0.25sp letter spacing
Display Medium: 45sp, Regular, 0sp letter spacing
Display Small: 36sp, Regular, 0sp letter spacing

Headline Large: 32sp, Regular, 0sp letter spacing
Headline Medium: 28sp, Regular, 0sp letter spacing
Headline Small: 24sp, Regular, 0sp letter spacing

Title Large: 22sp, Regular, 0sp letter spacing
Title Medium: 16sp, Medium, 0.15sp letter spacing
Title Small: 14sp, Medium, 0.1sp letter spacing

Body Large: 16sp, Regular, 0.5sp letter spacing
Body Medium: 14sp, Regular, 0.25sp letter spacing
Body Small: 12sp, Regular, 0.4sp letter spacing

Label Large: 14sp, Medium, 0.1sp letter spacing
Label Medium: 12sp, Medium, 0.5sp letter spacing
Label Small: 11sp, Medium, 0.5sp letter spacing
```

### 间距系统

```
Spacing Scale:
4dp   - 最小间距
8dp   - 小间距
12dp  - 中小间距
16dp  - 标准间距
20dp  - 中等间距
24dp  - 大间距
32dp  - 超大间距
48dp  - 特大间距

页面边距: 16dp
卡片内边距: 16dp
组件间距: 8dp/16dp/24dp
列表项高度: 72dp
按钮高度: 40dp
输入框高度: 56dp
```

### 图标规范

```
导航图标: 24dp
操作图标: 20dp
状态图标: 16dp
列表图标: 24dp
```

### 圆角规范

```
小圆角: 4dp
标准圆角: 8dp
大圆角: 16dp
超大圆角: 28dp
```

## 界面布局设计

### 1. 主界面 (HomeScreen)

```
┌─────────────────────────────────┐
│ 📚 读书记录      🔍 ⚙️        │ ← TopAppBar (64dp)
├─────────────────────────────────┤
│                                 │
│  📊 本月已读 3 本               │ ← 统计卡片区域
│  🎯 年度目标 12/50              │   (Card, 16dp margin)
│                                 │
├─────────────────────────────────┤
│ 📖 正在阅读                     │ ← 分组标题 (Title Medium)
│                                 │
│ [封面] 时间管理的艺术           │ ← 书籍卡片
│ 80x120 进度: 45% (120/267页)    │   (Card, 8dp spacing)
│                                 │
│ [封面] 深度工作                 │
│ 80x120 进度: 78% (第8/10章)     │
│                                 │
├─────────────────────────────────┤
│ 📝 最近笔记                     │ ← 分组标题
│                                 │
│ • 时间管理的四象限法则          │ ← 笔记列表项
│   2024-01-20 • 《时间管理》     │   (ListItem, 56dp height)
│ • 深度工作的三个层次            │
│   2024-01-19 • 《深度工作》     │
│                                 │
└─────────────────────────────────┘
│ 📚 书籍 📝 笔记 📊 统计 🏆 勋章 │ ← NavigationBar (80dp)
└─────────────────────────────────┘
```

**组件规范:**
- TopAppBar: 64dp 高度，Primary 背景色
- 统计卡片: Card 组件，16dp 圆角，8dp elevation
- 书籍卡片: 封面 80x120dp，16dp 内边距
- 底部导航: NavigationBar，80dp 高度

### 2. 书籍列表界面 (BooksScreen)

```
┌─────────────────────────────────┐
│ ← 书籍库        🔍 ➕ ⋮        │ ← TopAppBar
├─────────────────────────────────┤
│ 🔽 想读 (5)  在读 (2)  已读 (12)│ ← FilterChip 行
│                                 │   (8dp spacing)
├─────────────────────────────────┤
│                                 │
│ [封面] 《原子习惯》             │ ← 书籍列表项
│ 60x90  詹姆斯·克利尔            │   (ListItem, 88dp height)
│        想读 • 2024-01-15        │   (12dp 垂直间距)
│                                 │
│ [封面] 《深度工作》             │
│ 60x90  卡尔·纽波特              │
│        在读 • 78% • 第8章       │
│                                 │
│ [封面] 《时间管理》             │
│ 60x90  大卫·艾伦                │
│        已读 • ⭐⭐⭐⭐⭐        │
│                                 │
└─────────────────────────────────┘
```

**组件规范:**
- FilterChip: 32dp 高度，8dp 圆角
- 书籍列表项: 88dp 高度，封面 60x90dp
- 状态图标: 16dp，使用对应状态颜色
- 评分星星: 16dp，使用 Amber 颜色

### 3. 书籍详情页面 (BookDetailScreen)

```
┌─────────────────────────────────┐
│ ← 详情                    ⋮     │ ← TopAppBar
├─────────────────────────────────┤
│                                 │
│     [大封面图片]                │ ← 封面区域
│     120x180dp                   │   (居中显示)
│                                 │
│   《深度工作》                  │ ← 书籍信息
│   卡尔·纽波特                   │   (Title Large)
│   中信出版社 • 2017年           │   (Body Medium)
│                                 │
├─────────────────────────────────┤
│ 📖 在读  📝 笔记(3)  ✏️ 编辑   │ ← 操作按钮行
│                                 │   (Chip 组件)
├─────────────────────────────────┤
│                                 │
│ 📊 阅读进度                     │ ← 进度卡片
│ ████████░░ 78% (第8章/共10章)   │   (LinearProgressIndicator)
│                                 │
│ 🏷️ 标签: 效率 工作 自我提升     │ ← 标签区域
│                                 │   (AssistChip)
│ 📖 简介                         │ ← 简介区域
│ 在这个信息爆炸的时代...         │   (可展开/折叠)
│                                 │
│ 📈 阅读统计                     │ ← 统计卡片
│ 总时长: 5小时30分               │
│ 最近阅读: 2024-01-20            │
│                                 │
└─────────────────────────────────┘
```

**组件规范:**
- 封面图片: 120x180dp，8dp 圆角
- 操作按钮: Chip 组件，32dp 高度
- 进度条: LinearProgressIndicator，8dp 高度
- 标签: AssistChip，24dp 高度

### 4. 笔记编辑界面 (NoteEditScreen)

```
┌─────────────────────────────────┐
│ ← 笔记编辑           💾 ✓       │ ← TopAppBar
├─────────────────────────────────┤
│ 📖 《深度工作》第3章            │ ← 关联书籍信息
│                                 │   (Surface 背景)
├─────────────────────────────────┤
│                                 │
│ 标题: [深度工作的三个层次]      │ ← 标题输入框
│                                 │   (OutlinedTextField)
│ ┌─ 工具栏 ─────────────────────┐ │
│ │ B I U 📷 🔗 📝 👁️          │ │ ← Markdown 工具栏
│ └─────────────────────────────┘ │   (IconButton 行)
│                                 │
│ # 深度工作的三个层次            │ ← 内容编辑区域
│                                 │   (TextField, 多行)
│ 作者提到深度工作分为三个层次:   │
│                                 │
│ ![图片](image.jpg)              │
│                                 │
│ 1. **浅层工作** - 日常事务      │
│ 2. **深度工作** - 专注创造      │
│ 3. **超深度工作** - 心流状态    │
│                                 │
├─────────────────────────────────┤
│ 🏷️ 标签: [重要想法] [实践]     │ ← 标签选择区域
│ 📎 附件: audio_note.m4a         │ ← 附件列表
└─────────────────────────────────┘
```

**组件规范:**
- 标题输入: OutlinedTextField，56dp 高度
- 工具栏: 48dp 高度，IconButton 40dp
- 内容编辑: TextField，最小 200dp 高度
- 标签 Chip: InputChip，32dp 高度

### 5. 统计界面 (StatisticsScreen)

```
┌─────────────────────────────────┐
│ 📊 阅读统计      📅 🔄         │ ← TopAppBar
├─────────────────────────────────┤
│                                 │
│ 📈 本月概览                     │ ← 概览卡片
│ ┌─────────────────────────────┐ │
│ │ 📚 已读: 3本  ⏱️ 时长: 15h  │ │ (Card, 统计网格)
│ │ 📄 页数: 890  🎯 目标: 60%  │ │
│ └─────────────────────────────┘ │
│                                 │
│ 📊 阅读趋势                     │ ← 图表卡片
│ ┌─ 图表区域 ──────────────────┐ │
│ │     ▄                       │ │ (Chart 组件)
│ │   ▄ █ ▄                     │ │ 200dp 高度
│ │ ▄ █ █ █ ▄                   │ │
│ │ 1 2 3 4 5 ... 本周          │ │
│ └─────────────────────────────┘ │
│                                 │
│ 🏷️ 分类统计                    │ ← 分类统计卡片
│ • 技术类: 5本 (42%)             │ (进度条 + 文字)
│ • 文学类: 3本 (25%)             │
│ • 自我提升: 4本 (33%)           │
│                                 │
└─────────────────────────────────┘
```

**组件规范:**
- 统计卡片: Card，16dp 圆角，8dp elevation
- 图表区域: 200dp 高度，自定义绘制
- 进度条: LinearProgressIndicator，4dp 高度
- 统计项: ListItem，48dp 高度

## 交互规范

### 手势操作
- **点击**: 基本选择和导航
- **长按**: 显示上下文菜单 (500ms)
- **左滑**: 删除操作 (列表项)
- **右滑**: 标记操作 (列表项)
- **拖拽**: 排序操作 (长按后拖拽)

### 动画规范
- **页面转场**: 300ms，标准缓动
- **组件动画**: 200ms，快速缓动
- **加载动画**: 循环，1000ms 周期
- **状态变化**: 150ms，线性缓动

### 反馈规范
- **触觉反馈**: 按钮点击、长按、错误操作
- **视觉反馈**: 涟漪效果、状态变化、加载指示
- **音频反馈**: 成功操作、错误提示 (可选)

## 状态设计

### 加载状态
- **骨架屏**: 内容加载时显示结构占位
- **进度指示器**: 长时间操作显示进度
- **刷新指示器**: 下拉刷新动画

### 空状态
- **空列表**: 插图 + 引导文字 + 操作按钮
- **搜索无结果**: 搜索图标 + 提示文字
- **网络错误**: 错误图标 + 重试按钮

### 错误状态
- **网络错误**: 统一错误页面
- **权限错误**: 权限说明 + 设置引导
- **操作失败**: Snackbar 提示

## 响应式设计

### 屏幕适配
- **小屏幕** (< 600dp): 单列布局
- **中等屏幕** (600-840dp): 双列布局
- **大屏幕** (> 840dp): 三列布局

### 横屏适配
- **导航**: 侧边导航栏
- **内容**: 双栏布局 (列表 + 详情)
- **输入**: 优化键盘遮挡

## 无障碍设计

### 内容描述
- 所有图像提供 contentDescription
- 装饰性图像标记为 decorative
- 复杂图表提供文字描述

### 导航支持
- 支持 TalkBack 屏幕阅读器
- 合理的焦点顺序
- 键盘导航支持

### 对比度
- 文字对比度 ≥ 4.5:1
- 大文字对比度 ≥ 3:1
- 图标对比度 ≥ 3:1

## 组件库

### 自定义组件
- **BookCard**: 书籍卡片组件
- **ReadingProgressIndicator**: 阅读进度指示器
- **NoteEditor**: Markdown 笔记编辑器
- **StatisticsChart**: 统计图表组件
- **TagSelector**: 标签选择器

### 组件状态
- **Default**: 默认状态
- **Hover**: 悬停状态 (桌面端)
- **Pressed**: 按下状态
- **Focused**: 焦点状态
- **Disabled**: 禁用状态

这个设计规范文档为开发团队和设计师提供了完整的界面设计指导，确保应用的视觉一致性和用户体验质量。