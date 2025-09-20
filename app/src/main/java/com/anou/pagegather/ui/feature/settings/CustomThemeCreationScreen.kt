package com.anou.pagegather.ui.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Colorize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import android.graphics.Color as AndroidColor
import androidx.hilt.navigation.compose.hiltViewModel
import com.anou.pagegather.ui.components.ColorPicker
import com.anou.pagegather.ui.theme.CustomTheme

/**
 * 自定义主题创建屏幕
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomThemeCreationScreen(
    onNavigateBack: () -> Unit,
    onThemeCreated: (CustomTheme) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CustomThemeCreationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // 监听保存成功的状态变化
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess && uiState.createdTheme != null) {
            // 调用 onThemeCreated 回调通知上层组件主题已创建
            onThemeCreated(uiState.createdTheme!!)
            // 导航返回上一页
            onNavigateBack()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("创建自定义主题") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { 
                            viewModel.saveCustomTheme()
                        },
                        enabled = uiState.isSaveEnabled && !uiState.isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "保存"
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        CustomThemeCreationContent(
            uiState = uiState,
            onNameChanged = viewModel::onNameChanged,
            onPrimaryColorChanged = viewModel::onPrimaryColorChanged,
            onDescriptionChanged = viewModel::onDescriptionChanged,
            onEmojiChanged = viewModel::onEmojiChanged,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

@Composable
private fun CustomThemeCreationContent(
    uiState: CustomThemeCreationUiState,
    onNameChanged: (String) -> Unit,
    onPrimaryColorChanged: (Color) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onEmojiChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 主题基本信息
        ThemeBasicInfoSection(
            name = uiState.name,
            description = uiState.description,
            emoji = uiState.emoji,
            onNameChanged = onNameChanged,
            onDescriptionChanged = onDescriptionChanged,
            onEmojiChanged = onEmojiChanged
        )
        
        // 颜色选择
        ColorSelectionSection(
            primaryColor = uiState.primaryColor,
            onPrimaryColorChanged = onPrimaryColorChanged
        )
        
        // 预览
        ThemePreviewSection(
            name = uiState.name,
            emoji = uiState.emoji,
            primaryColor = uiState.primaryColor
        )
    }
}

@Composable
private fun ThemeBasicInfoSection(
    name: String,
    description: String,
    emoji: String,
    onNameChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onEmojiChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "基本信息",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            OutlinedTextField(
                value = name,
                onValueChange = onNameChanged,
                label = { Text("主题名称") },
                placeholder = { Text("输入主题名称") },
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChanged,
                label = { Text("主题描述") },
                placeholder = { Text("输入主题描述") },
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = emoji,
                onValueChange = onEmojiChanged,
                label = { Text("表情符号") },
                placeholder = { Text("选择表情符号") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ColorSelectionSection(
    primaryColor: Color,
    onPrimaryColorChanged: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "颜色选择",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Colorize,
                    contentDescription = null,
                    tint = primaryColor
                )
                
                Text(
                    text = "主色调",
                    style = MaterialTheme.typography.bodyLarge
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                // 简化的颜色选择器（实际项目中可能需要更复杂的实现）
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = primaryColor,
                            shape = MaterialTheme.shapes.medium
                        )
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = MaterialTheme.shapes.medium
                        )
                )
            }
            
            // 预定义颜色选项
            PredefinedColorOptions(
                selectedColor = primaryColor,
                onColorSelected = onPrimaryColorChanged
            )
            
            // 添加自定义颜色选择按钮
            CustomColorPicker(
                selectedColor = primaryColor,
                onColorSelected = onPrimaryColorChanged
            )
        }
    }
}

@Composable
private fun PredefinedColorOptions(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    val predefinedColors = listOf(
        Color(0xFF6200EE), // 紫色
        Color(0xFF03DAC6), // 青色
        Color(0xFFBB86FC), // 淡紫色
        Color(0xFF018786), // 深青色
        Color(0xFF3700B3), // 深紫色
        Color(0xFF03FFCC), // 青色
        Color(0xFFFF6B35), // 橙色
        Color(0xFF4CAF50), // 绿色
        Color(0xFF2196F3), // 蓝色
        Color(0xFFFF9800)  // 琥珀色
    )
    
    // 使用固定高度的网格而不是LazyVerticalGrid来避免嵌套滚动问题
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        predefinedColors.forEach { color ->
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = color,
                        shape = MaterialTheme.shapes.medium
                    )
                    .border(
                        width = if (color == selectedColor) 3.dp else 1.dp,
                        color = if (color == selectedColor) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outline
                        },
                        shape = MaterialTheme.shapes.medium
                    )
                    .clickable { onColorSelected(color) }
            )
        }
    }
}

@Composable
private fun CustomColorPicker(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    var showColorPicker by remember { mutableStateOf(false) }
    
    Column(modifier = modifier) {
        Button(
            onClick = { showColorPicker = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("选择自定义颜色")
        }
        
        if (showColorPicker) {
            // 使用我们新创建的颜色选择器
            ColorPicker(
                initialColor = selectedColor,
                onColorChanged = onColorSelected
            )
        }
    }
}

@Composable
private fun ThemePreviewSection(
    name: String,
    emoji: String,
    primaryColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "主题预览",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = primaryColor.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = emoji,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = name.ifEmpty { "未命名主题" },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Text(
                        text = "这是主题预览效果",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
