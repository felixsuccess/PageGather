# 主题系统测试运行指南

本文档提供了运行主题系统所有测试的详细指南。

## 📋 测试概览

我们为主题系统创建了完整的测试套件，包括：

### 单元测试 (Unit Tests)
- `ThemeManagerTest.kt` - 主题管理器核心功能测试
- `ThemePreferencesTest.kt` - 主题偏好存储测试
- `AppThemeTest.kt` - 主题枚举功能测试
- `ThemeCacheTest.kt` - 主题缓存功能测试
- `ThemeErrorHandlerTest.kt` - 错误处理功能测试
- `ThemeColorFactoryTest.kt` - 主题颜色工厂测试
- `DataStoreManagerTest.kt` - 数据存储管理器测试

### UI集成测试 (UI Integration Tests)
- `ThemeSelectionScreenTest.kt` - 主题选择界面测试
- `ThemePreviewCardTest.kt` - 主题预览卡片测试
- `ThemeIntegrationTest.kt` - 主题切换完整流程测试

### 一致性测试 (Consistency Tests)
- `ThemeConsistencyTest.kt` - 全应用主题一致性测试
- `ThemeVisualRegressionTest.kt` - 主题视觉回归测试

## 🚀 运行测试

### 1. 运行所有单元测试

```bash
# Windows (CMD)
gradlew test

# Windows (PowerShell)
.\gradlew test

# 运行特定模块的单元测试
.\gradlew :app:test
```

### 2. 运行所有UI测试

```bash
# 确保有连接的设备或运行的模拟器
.\gradlew connectedAndroidTest

# 运行特定的UI测试类
.\gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.anou.pagegather.ui.theme.ThemeConsistencyTest
```

### 3. 运行特定的测试套件

#### 主题管理器测试
```bash
.\gradlew test --tests "*ThemeManagerTest*"
```

#### 主题UI测试
```bash
.\gradlew connectedAndroidTest --tests "*ThemeSelectionScreenTest*"
```

#### 主题一致性测试
```bash
.\gradlew connectedAndroidTest --tests "*ThemeConsistencyTest*"
```

### 4. 运行所有主题相关测试

```bash
# 单元测试
.\gradlew test --tests "*Theme*"

# UI测试
.\gradlew connectedAndroidTest --tests "*Theme*"
```

## 📊 测试覆盖率

### 生成测试覆盖率报告

```bash
# 生成单元测试覆盖率报告
.\gradlew testDebugUnitTestCoverage

# 生成完整测试覆盖率报告（包括UI测试）
.\gradlew createDebugCoverageReport
```

覆盖率报告将生成在：
- 单元测试：`app/build/reports/coverage/test/debug/`
- 完整报告：`app/build/reports/coverage/debug/`

## 🔍 测试验证清单

运行测试后，请验证以下功能：

### ✅ 核心功能测试
- [ ] 主题切换功能正常
- [ ] 主题模式切换（亮色/暗色/跟随系统）正常
- [ ] 主题偏好持久化存储正常
- [ ] 主题缓存机制正常
- [ ] 错误处理和回退机制正常

### ✅ UI功能测试
- [ ] 主题选择界面显示正常
- [ ] 主题预览卡片交互正常
- [ ] 主题切换视觉反馈正常
- [ ] 所有主题都能正确预览

### ✅ 一致性测试
- [ ] 所有页面正确应用选定主题
- [ ] 主题在不同屏幕间保持一致
- [ ] 主题切换后所有UI元素正确更新
- [ ] 暗色模式在所有主题下正常工作

## 🐛 常见问题排查

### 编译错误修复

如果遇到编译错误，请参考 `scripts/fix-test-issues.md` 获取详细的修复指南。

常见编译错误：
- `Unresolved reference 'Dispatchers'` - 缺少协程导入
- `Cannot access DataStore extension` - DataStore 扩展属性访问问题
- `MutablePreferences type issues` - 类型导入问题

### 测试失败排查

1. **单元测试失败**
   ```bash
   # 首先检查编译错误
   .\gradlew compileDebugUnitTestKotlin
   
   # 查看详细测试报告
   .\gradlew test --info
   
   # 查看测试报告文件
   # app/build/reports/tests/testDebugUnitTest/index.html
   ```

2. **UI测试失败**
   ```bash
   # 确保设备/模拟器正常运行
   adb devices
   
   # 查看UI测试报告
   .\gradlew connectedAndroidTest --info
   
   # 查看测试报告文件
   # app/build/reports/androidTests/connected/index.html
   ```

3. **依赖问题**
   ```bash
   # 清理并重新构建
   .\gradlew clean build
   
   # 重新同步依赖
   .\gradlew --refresh-dependencies
   ```

### 性能测试

```bash
# 运行性能基准测试（如果配置了）
.\gradlew connectedBenchmarkAndroidTest
```

## 📈 测试指标

### 预期测试覆盖率目标
- 单元测试覆盖率：≥ 90%
- UI测试覆盖率：≥ 80%
- 集成测试覆盖率：≥ 85%

### 测试执行时间
- 单元测试：< 2分钟
- UI测试：< 10分钟
- 完整测试套件：< 15分钟

## 🔧 测试环境配置

### 必需的依赖
确保 `build.gradle.kts` 包含以下测试依赖：

```kotlin
dependencies {
    // 单元测试
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    
    // UI测试
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("io.mockk:mockk-android:1.13.8")
    
    // Hilt测试
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.48")
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:2.48")
}
```

### 测试配置
在 `app/build.gradle.kts` 中配置：

```kotlin
android {
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}
```

## 📝 测试报告

测试完成后，可以在以下位置查看详细报告：

1. **单元测试报告**：`app/build/reports/tests/testDebugUnitTest/index.html`
2. **UI测试报告**：`app/build/reports/androidTests/connected/index.html`
3. **覆盖率报告**：`app/build/reports/coverage/debug/index.html`

## 🎯 持续集成

### GitHub Actions 配置示例

```yaml
name: Theme System Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
        
    - name: Run Unit Tests
      run: ./gradlew test
      
    - name: Run UI Tests
      uses: reactivecircus/android-emulator-runner@v2
      with:
        api-level: 29
        script: ./gradlew connectedAndroidTest
```

## 📞 支持

如果在运行测试时遇到问题，请：

1. 检查上述常见问题排查部分
2. 查看测试报告中的详细错误信息
3. 确保所有依赖都正确配置
4. 验证测试环境设置

---

**注意**：首次运行UI测试时，可能需要较长时间来下载和设置测试环境。建议在稳定的网络环境下进行。