package com.anou.pagegather.ui.feature.my.settings

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anou.pagegather.data.repository.CustomThemeRepository
import com.anou.pagegather.ui.theme.CustomTheme
import com.anou.pagegather.ui.theme.ThemeCustomizationImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 自定义主题创建 ViewModel
 */
@HiltViewModel
class CustomThemeCreationViewModel @Inject constructor(
    private val customThemeRepository: CustomThemeRepository
) : ViewModel() {
    
    private val themeCustomization = ThemeCustomizationImpl()
    
    private val _uiState = MutableStateFlow(CustomThemeCreationUiState())
    val uiState = _uiState.asStateFlow()
    
    /**
     * 更新主题名称
     */
    fun onNameChanged(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
        updateSaveEnabled()
    }
    
    /**
     * 更新主色调
     */
    fun onPrimaryColorChanged(color: Color) {
        _uiState.value = _uiState.value.copy(primaryColor = color)
    }
    
    /**
     * 更新主题描述
     */
    fun onDescriptionChanged(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }
    
    /**
     * 更新表情符号
     */
    fun onEmojiChanged(emoji: String) {
        _uiState.value = _uiState.value.copy(emoji = emoji)
    }
    
    /**
     * 保存自定义主题
     */
    fun saveCustomTheme(): CustomTheme? {
        viewModelScope.launch {
            try {
                // 设置加载状态
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                // 验证输入
                if (!isInputValid()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "请填写主题名称"
                    )
                    return@launch
                }
                
                // 创建自定义主题
                val customTheme = themeCustomization.createCustomTheme(
                    name = _uiState.value.name,
                    primaryColor = _uiState.value.primaryColor,
                    description = _uiState.value.description,
                    emoji = _uiState.value.emoji
                )
                
                // 保存到仓库
                customThemeRepository.saveCustomTheme(customTheme)
                    .onSuccess {
                        // 保存成功
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            saveSuccess = true,
                            error = null,
                            createdTheme = customTheme
                        )
                    }
                    .onFailure { exception ->
                        // 保存失败
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = exception.message ?: "保存失败"
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "保存失败"
                )
            }
        }
        
        // 返回null，因为实际的主题创建是异步的
        return null
    }
    
    /**
     * 获取创建的主题（如果有的话）
     */
    fun getCreatedTheme(): CustomTheme? {
        return _uiState.value.createdTheme
    }
    
    /**
     * 检查输入是否有效
     */
    private fun isInputValid(): Boolean {
        val state = _uiState.value
        return state.name.isNotBlank() && state.primaryColor != Color.Unspecified
    }
    
    /**
     * 更新保存按钮启用状态
     */
    private fun updateSaveEnabled() {
        val state = _uiState.value
        _uiState.value = state.copy(
            isSaveEnabled = state.name.isNotBlank()
        )
    }
}

/**
 * 自定义主题创建界面状态
 */
data class CustomThemeCreationUiState(
    val name: String = "",
    val description: String = "",
    val emoji: String = "🎨",
    val primaryColor: Color = Color(0xFF6200EE),
    val isSaveEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val saveSuccess: Boolean = false,
    val createdTheme: CustomTheme? = null,
    val error: String? = null
)