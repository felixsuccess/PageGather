package com.anou.pagegather.ui.feature.my.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.anou.pagegather.ui.theme.AppTheme
import com.anou.pagegather.ui.theme.ThemeMode

/**
 * 主题选择屏幕
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSelectionScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCustomThemeCreation: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ThemeSelectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "主题设置",
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 主题模式选择部分
            item {
                ThemeModeSection(
                    currentMode = uiState.currentMode,
                    onModeSelected = viewModel::selectThemeMode,
                    isLoading = uiState.isLoading
                )
            }
            
            // 动态颜色开关部分
            item {
                DynamicColorSection(
                    isDynamicColorEnabled = uiState.useDynamicColor,
                    onDynamicColorChanged = viewModel::setDynamicColor,
                    isLoading = uiState.isLoading
                )
            }
            
            // 主题选择部分
            item {
                ThemeSelectionSection(
                    availableThemes = uiState.availableThemes,
                    currentTheme = uiState.currentTheme,
                    isDarkMode = uiState.isDarkMode,
                    onThemeSelected = viewModel::selectTheme,
                    onThemePreview = viewModel::previewTheme,
                    isLoading = uiState.isLoading,
                    onAddCustomTheme = onNavigateToCustomThemeCreation
                )
            }
        }
    }
}

/**
 * 主题模式选择部分
 */
@Composable
private fun ThemeModeSection(
    currentMode: ThemeMode,
    onModeSelected: (ThemeMode) -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 标题
        Text(
            text = "显示模式",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        // 模式选项
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ThemeMode.entries.forEach { mode ->
                ThemeModeOption(
                    mode = mode,
                    isSelected = currentMode == mode,
                    onClick = { onModeSelected(mode) },
                    enabled = !isLoading
                )
            }
        }
    }
}

/**
 * 动态颜色开关部分
 */
@Composable
private fun DynamicColorSection(
    isDynamicColorEnabled: Boolean,
    onDynamicColorChanged: (Boolean) -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 标题
        Text(
            text = "动态颜色",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        // 动态颜色开关
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "启用动态颜色",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Text(
                        text = "根据壁纸自动调整主题颜色（需要Android 12及以上版本）",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                Switch(
                    checked = isDynamicColorEnabled,
                    onCheckedChange = onDynamicColorChanged,
                    enabled = !isLoading
                )
            }
        }
    }
}

/**
 * 主题模式选项
 */
@Composable
private fun ThemeModeOption(
    mode: ThemeMode,
    isSelected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        onClick = onClick,
        enabled = enabled,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = if (isSelected) {
            CardDefaults.outlinedCardBorder().copy(
                width = 2.dp,
            )
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = mode.displayName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                
                Text(
                    text = when (mode) {
                        ThemeMode.LIGHT -> "始终使用亮色主题"
                        ThemeMode.DARK -> "始终使用暗色主题"
                        ThemeMode.SYSTEM -> "跟随系统设置自动切换"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    }
                )
            }
            
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                enabled = enabled
            )
        }
    }
}

/**
 * 主题选择部分
 */
@Composable
private fun ThemeSelectionSection(
    availableThemes: List<AppTheme>,
    currentTheme: AppTheme,
    isDarkMode: Boolean,
    onThemeSelected: (AppTheme) -> Unit,
    onThemePreview: (AppTheme) -> Unit,
    isLoading: Boolean,
    onAddCustomTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 标题和添加按钮
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "主题颜色",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            IconButton(
                onClick = onAddCustomTheme,
                enabled = !isLoading
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "添加自定义主题"
                )
            }
        }
        
        // 主题网格
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.height(300.dp) // 固定高度避免嵌套滚动问题
        ) {
            items(availableThemes) { theme ->
                CompactThemePreviewCard(
                    theme = theme,
                    isSelected = currentTheme == theme,
                    isDarkMode = isDarkMode,
                    onClick = { 
                        // 立即预览主题
                        onThemePreview(theme)
                        // 然后选择主题
                        onThemeSelected(theme)
                    }
                )
            }
        }
        
        // 当前选中主题的详细信息
        CurrentThemeInfo(
            theme = currentTheme,
            isDarkMode = isDarkMode
        )
    }
}

/**
 * 当前主题信息显示
 */
@Composable
private fun CurrentThemeInfo(
    theme: AppTheme,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
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
                    text = theme.emoji,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "当前主题：${theme.displayName}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Text(
                text = theme.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
            
            Text(
                text = "当前模式：${if (isDarkMode) "暗色" else "亮色"}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}

/**
 * 列表布局的主题选择屏幕（备用布局）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSelectionScreenList(
    onNavigateBack: () -> Unit,
    onNavigateToCustomThemeCreation: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ThemeSelectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("主题设置") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 主题模式选择
            item {
                ThemeModeSection(
                    currentMode = uiState.currentMode,
                    onModeSelected = viewModel::selectThemeMode,
                    isLoading = uiState.isLoading
                )
            }
            
            // 动态颜色开关
            item {
                DynamicColorSection(
                    isDynamicColorEnabled = uiState.useDynamicColor,
                    onDynamicColorChanged = viewModel::setDynamicColor,
                    isLoading = uiState.isLoading
                )
            }
            
            item {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = DividerDefaults.Thickness,
                    color = DividerDefaults.color
                )
            }
            
            // 主题列表
            items(uiState.availableThemes) { theme ->
                ThemePreviewCard(
                    theme = theme,
                    isSelected = uiState.currentTheme == theme,
                    isDarkMode = uiState.isDarkMode,
                    onClick = {
                        viewModel.previewTheme(theme)
                        viewModel.selectTheme(theme)
                    }
                )
            }
        }
    }
}