

# PageGather

拾卷（PageGather） - 划下的线，终将成为思想的脉络.

## 项目概述

拾卷（PageGather）是一款基于 Jetpack Compose 开的个人读书记录和管理应用，帮助用户跟踪阅读进度、记录读书笔记、分析阅读习惯。

## 📱 功能特性

### 核心功能
- **书籍管理**: 添加、编辑、删除书籍，支持 ISBN 扫描自动获取书籍信息
- **阅读记录**: 精确记录阅读时间和进度，支持多种进度跟踪模式
- **笔记系统**: 支持 Markdown 格式的富文本笔记，可添加图片附件
- **分类标签**: 灵活的书籍分类和标签管理系统

### 高级功能
- **个人藏书**: 管理书籍拥有状态、存放位置、借阅记录和购书心愿单
- **阅读计时**: 正向/反向计时器，后台计时和通知提醒
- **统计分析**: 阅读趋势图表、时间分布、阅读目标跟踪
- **勋章系统**: 基于阅读成就的激励机制
- **数据同步**: 本地数据备份和恢复功能

## 🛠️ 技术栈

- **开发语言**: Kotlin
- **UI 框架**: Jetpack Compose
- **设计系统**: Material 3
- **架构模式**: MVVM + Clean Architecture
- **依赖注入**: Hilt
- **数据库**: Room
- **网络请求**: Retrofit
- **图片加载**: Coil
- **相机功能**: CameraX
- **条码扫描**: ML Kit 
- **状态管理**: Compose ViewModel 单向数据流
- **数据库**: Room
- **本地存储**: DataStore 类型安全存储 
- **异步处理**: Kotlin Coroutines + Flow
- **导航**: Compose Navigation 
- **权限管理**: 动态权限请求
- **测试**: Unit Test + Instrumented Test
- **代码规范**: 遵循 Android 官方代码规范

## 兼容性

- 最低支持 Android 6.0 (API 23)
- 支持深色/浅色主题

## 准则
- 克制，保持简单，专注记录

## 🏗️ 项目架构

```
app/
├── presentation/          # UI 层
│   ├── ui/               # Compose UI 组件
│   ├── viewmodel/        # ViewModel 类
│   └── navigation/       # 导航配置
├── domain/               # 业务逻辑层
│   ├── model/           # 数据模型
│   ├── repository/      # Repository 接口
│   └── usecase/         # Use Case 类
└── data/                # 数据层
    ├── local/           # 本地数据源 (Room)
    ├── remote/          # 远程数据源 (API)
    └── repository/      # Repository 实现
```

## 📋 开发计划

### 🚀 P0 - 基础可用版本
- [x] 项目架构搭建
- [ ] 数据层实现
- [ ] 业务逻辑层实现
- [ ] 基础 UI 界面

### 🎯 P1 - 完整功能版本
- [ ] 笔记管理界面
- [ ] 阅读计时功能
- [ ] ISBN 扫描功能

### 🌟 P2 - 增强体验版本
- [ ] 统计分析功能
- [ ] 应用设置和个性化

### 🎁 P3 - 质量保证版本
- [ ] 单元测试和 UI 测试
- [ ] 性能优化

## 🚀 快速开始

### 环境要求
- Android Studio Hedgehog | 2023.1.1 或更高版本
- Android SDK API 24+ (Android 7.0)
- Kotlin 1.9.0+

### 安装步骤
1. 克隆项目到本地
```bash
git clone [项目地址]
cd reading-tracker-app
```

2. 使用 Android Studio 打开项目

3. 同步项目依赖
```bash
./gradlew build
```

4. 运行应用
```bash
./gradlew installDebug
```

## 📖 使用指南

### 添加书籍
1. 点击主界面的 "+" 按钮
2. 手动输入书籍信息或使用 ISBN 扫描
3. 选择分类和标签
4. 保存书籍信息

### 记录阅读
1. 在书籍列表中选择要阅读的书籍
2. 点击 "开始阅读" 启动计时器
3. 阅读完成后更新进度
4. 添加阅读笔记（可选）

### 查看统计
1. 进入统计页面
2. 查看阅读时长、书籍数量等数据
3. 分析阅读趋势和习惯

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

### 开发规范
- 遵循 Kotlin 编码规范
- 使用 Clean Architecture 架构模式
- 编写单元测试和 UI 测试
- 提交前运行代码检查

### 提交流程
1. Fork 项目
2. 创建功能分支
3. 提交代码变更
4. 创建 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 📞 联系方式

如有问题或建议，请通过以下方式联系：
- 提交 GitHub Issue
- 发送邮件至 [邮箱地址]

---

**注意**: 本项目仍在开发中，功能可能不完整或存在 Bug。欢迎反馈和建议！
