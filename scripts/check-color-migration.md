# 颜色迁移检查脚本

## 自动检查命令

在项目根目录运行以下命令来检查是否还有旧颜色引用：

### 1. 检查旧颜色导入
```bash
# Windows (PowerShell)
Select-String -Path "app\src\**\*.kt" -Pattern "import.*\.(TextDark|TextGray|TextWhite|Accent|BackgroundLight|SurfaceLight|BackgroundDark|SurfaceDark|StatusRed|StatusGreen)"

# Linux/Mac
grep -r "import.*\.\(TextDark\|TextGray\|TextWhite\|Accent\|BackgroundLight\|SurfaceLight\|BackgroundDark\|SurfaceDark\|StatusRed\|StatusGreen\)" app/src/
```

### 2. 检查旧颜色使用
```bash
# Windows (PowerShell)
Select-String -Path "app\src\**\*.kt" -Pattern "\b(TextDark|TextGray|TextWhite|Accent|BackgroundLight|SurfaceLight|BackgroundDark|SurfaceDark|StatusRed|StatusGreen)\b"

# Linux/Mac
grep -r "\b\(TextDark\|TextGray\|TextWhite\|Accent\|BackgroundLight\|SurfaceLight\|BackgroundDark\|SurfaceDark\|StatusRed\|StatusGreen\)\b" app/src/
```

### 3. 检查缺失的 extendedColors 导入
```bash
# Windows (PowerShell)
Select-String -Path "app\src\**\*.kt" -Pattern "MaterialTheme\.extendedColors" | Select-String -NotMatch "import.*extendedColors"

# Linux/Mac
grep -r "MaterialTheme\.extendedColors" app/src/ | grep -v "import.*extendedColors"
```

## 常见修复模式

### 导入语句修复
```kotlin
// ❌ 删除这些导入
import com.anou.pagegather.ui.theme.TextDark
import com.anou.pagegather.ui.theme.TextGray
import com.anou.pagegather.ui.theme.Accent

// ✅ 添加这个导入
import com.anou.pagegather.ui.theme.extendedColors
```

### 颜色使用修复
```kotlin
// ❌ 旧的颜色使用
color = TextDark
color = TextGray
color = Accent

// ✅ 新的颜色使用
color = MaterialTheme.extendedColors.titleColor
color = MaterialTheme.extendedColors.subtitleColor
color = MaterialTheme.extendedColors.accentColor
```

## 批量替换脚本

### PowerShell 批量替换脚本
```powershell
# 保存为 migrate-colors.ps1
$files = Get-ChildItem -Path "app\src" -Filter "*.kt" -Recurse

foreach ($file in $files) {
    $content = Get-Content $file.FullName -Raw
    
    # 替换导入语句
    $content = $content -replace "import com\.anou\.pagegather\.ui\.theme\.TextDark", ""
    $content = $content -replace "import com\.anou\.pagegather\.ui\.theme\.TextGray", ""
    $content = $content -replace "import com\.anou\.pagegather\.ui\.theme\.Accent", ""
    
    # 替换颜色使用
    $content = $content -replace "\bTextDark\b", "MaterialTheme.extendedColors.titleColor"
    $content = $content -replace "\bTextGray\b", "MaterialTheme.extendedColors.subtitleColor"
    $content = $content -replace "\bAccent\b", "MaterialTheme.extendedColors.accentColor"
    
    # 添加 extendedColors 导入（如果需要且不存在）
    if ($content -match "MaterialTheme\.extendedColors" -and $content -notmatch "import.*extendedColors") {
        $content = $content -replace "(import androidx\.compose\.material3\.\*)", "`$1`nimport com.anou.pagegather.ui.theme.extendedColors"
    }
    
    Set-Content $file.FullName $content
}
```

### Bash 批量替换脚本
```bash
#!/bin/bash
# 保存为 migrate-colors.sh

find app/src -name "*.kt" -type f | while read file; do
    # 替换导入语句
    sed -i 's/import com\.anou\.pagegather\.ui\.theme\.TextDark//g' "$file"
    sed -i 's/import com\.anou\.pagegather\.ui\.theme\.TextGray//g' "$file"
    sed -i 's/import com\.anou\.pagegather\.ui\.theme\.Accent//g' "$file"
    
    # 替换颜色使用
    sed -i 's/\bTextDark\b/MaterialTheme.extendedColors.titleColor/g' "$file"
    sed -i 's/\bTextGray\b/MaterialTheme.extendedColors.subtitleColor/g' "$file"
    sed -i 's/\bAccent\b/MaterialTheme.extendedColors.accentColor/g' "$file"
    
    # 添加 extendedColors 导入（如果需要）
    if grep -q "MaterialTheme\.extendedColors" "$file" && ! grep -q "import.*extendedColors" "$file"; then
        sed -i '/import androidx\.compose\.material3\.\*/a import com.anou.pagegather.ui.theme.extendedColors' "$file"
    fi
done
```

## 验证迁移完成

### 1. 编译检查
```bash
./gradlew build
```

### 2. 运行测试界面
在 MainActivity 中临时启用：
```kotlin
HundiTestScreen()  // 查看所有颜色是否正常显示
```

### 3. 视觉检查清单
- [ ] 所有文字颜色正常显示
- [ ] 按钮颜色符合 Hundi 风格
- [ ] 卡片背景和边框正确
- [ ] 状态颜色（成功、警告、错误）正确
- [ ] 深浅主题切换正常

## 常见问题解决

### 问题1：编译错误 "Unresolved reference"
**解决**：检查是否有遗漏的旧颜色引用，使用上面的检查命令

### 问题2：颜色显示不正确
**解决**：确保使用了 `PageGatherTheme` 包装，并且导入了 `extendedColors`

### 问题3：深浅主题不生效
**解决**：使用 `MaterialTheme.extendedColors` 而不是固定的 `Color` 值

## 迁移完成标志

当以下检查都通过时，说明迁移完成：

1. ✅ 编译无错误
2. ✅ 搜索无旧颜色引用
3. ✅ 所有界面颜色正常
4. ✅ 深浅主题切换正常
5. ✅ `HundiTestScreen` 显示正确