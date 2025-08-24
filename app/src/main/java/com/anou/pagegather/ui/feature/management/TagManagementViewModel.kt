package com.anou.pagegather.ui.feature.management

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anou.pagegather.data.local.entity.TagEntity
import com.anou.pagegather.data.local.entity.TagType
import com.anou.pagegather.data.repository.TagRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * 标签管理界面状态
 */
data class TagManagementUiState(
    val tags: List<TagEntity> = emptyList(),
    val filteredTags: List<TagEntity> = emptyList(),
    val searchQuery: String = "",
    val selectedTagType: TagType? = TagType.BOOK, // 默认选中书籍标签
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isEditDialogVisible: Boolean = false,
    val editingTag: TagEntity? = null,
    val defaultTagType: TagType? = null // 新建标签时的默认类型
)

@HiltViewModel
class TagManagementViewModel @Inject constructor(
    private val tagRepository: TagRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(TagManagementUiState())
    val uiState: StateFlow<TagManagementUiState> = _uiState
    
    init {
        loadTags()
    }
    
    /**
     * 加载标签
     */
    private fun loadTags() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                combine(
                    tagRepository.getAllTags(),
                    _uiState
                ) { tags, state ->
                    val filteredTags = filterTags(tags, state.searchQuery, state.selectedTagType)
                    state.copy(
                        tags = tags,
                        filteredTags = filteredTags,
                        isLoading = false
                    )
                }.collect { newState ->
                    _uiState.value = newState
                }
            } catch (e: Exception) {
                Log.e("TagManagementViewModel", "加载标签失败: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "加载标签失败: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 筛选标签
     */
    private fun filterTags(
        tags: List<TagEntity>, 
        query: String, 
        tagType: TagType?
    ): List<TagEntity> {
        return tags.filter { tag ->
            val matchesQuery = if (query.isBlank()) {
                true
            } else {
                tag.name.contains(query, ignoreCase = true)
            }
            
            // 现在必须匹配指定的标签类型
            val matchesType = tagType?.let { type ->
                tag.tagType == type.code
            } ?: false // 如果tagType为null，不显示任何标签
            
            matchesQuery && matchesType
        }
    }
    
    /**
     * 搜索标签
     */
    fun searchTags(query: String) {
        val currentState = _uiState.value
        val filteredTags = filterTags(currentState.tags, query, currentState.selectedTagType)
        _uiState.value = currentState.copy(
            searchQuery = query,
            filteredTags = filteredTags
        )
    }
    
    /**
     * 按类型筛选标签
     */
    fun filterByType(tagType: TagType?) {
        val currentState = _uiState.value
        val filteredTags = filterTags(currentState.tags, currentState.searchQuery, tagType)
        _uiState.value = currentState.copy(
            selectedTagType = tagType,
            filteredTags = filteredTags
        )
    }
    
    /**
     * 显示添加标签对话框
     */
    fun showAddTagDialog() {
        val currentType = _uiState.value.selectedTagType ?: TagType.BOOK
        _uiState.value = _uiState.value.copy(
            isEditDialogVisible = true,
            editingTag = null,
            // 设置默认标签类型为当前选中的类型
            defaultTagType = currentType
        )
    }
    
    /**
     * 显示编辑标签对话框
     */
    fun showEditTagDialog(tag: TagEntity) {
        _uiState.value = _uiState.value.copy(
            isEditDialogVisible = true,
            editingTag = tag
        )
    }
    
    /**
     * 隐藏编辑对话框
     */
    fun hideEditDialog() {
        _uiState.value = _uiState.value.copy(
            isEditDialogVisible = false,
            editingTag = null,
            defaultTagType = null
        )
    }
    
    /**
     * 保存标签
     */
    fun saveTag(name: String, color: String?, type: TagType) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val trimmedName = name.trim()
                if (trimmedName.isBlank()) {
                    withContext(Dispatchers.Main) {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = "标签名称不能为空"
                        )
                    }
                    return@launch
                }
                
                val currentTag = _uiState.value.editingTag
                val excludeId = currentTag?.id ?: -1
                
                // 检查名称是否已存在
                val nameExists = tagRepository.isTagNameExists(trimmedName, type.code, excludeId)
                if (nameExists) {
                    withContext(Dispatchers.Main) {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = "标签名称已存在"
                        )
                    }
                    return@launch
                }
                
                val currentTime = System.currentTimeMillis()
                
                if (currentTag != null) {
                    // 更新现有标签
                    val updatedTag = currentTag.copy(
                        name = trimmedName,
                        color = color,
                        tagType = type.code,
                        updatedDate = currentTime
                    )
                    tagRepository.updateTag(updatedTag)
                    Log.d("TagManagement", "Updated tag: ${updatedTag.name}")
                } else {
                    // 创建新标签
                    // 获取该类型的最大排序值
                    val maxOrder = tagRepository.getMaxOrderByType(type.code) ?: 0
                    val newTag = TagEntity(
                        name = trimmedName,
                        color = color,
                        tagOrder = maxOrder + 1,
                        tagType = type.code,
                        createdDate = currentTime,
                        updatedDate = currentTime,
                        lastSyncDate = currentTime
                    )
                    val tagId = tagRepository.insertTag(newTag)
                    Log.d("TagManagement", "Created new tag with id: $tagId")
                }
                
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        isEditDialogVisible = false,
                        editingTag = null,
                        errorMessage = null,
                        defaultTagType = null
                    )
                }
            } catch (e: Exception) {
                Log.e("TagManagementViewModel", "保存标签失败: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "保存标签失败: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * 删除标签
     */
    fun deleteTag(tag: TagEntity, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                tagRepository.deleteTag(tag.id)
                Log.d("TagManagement", "Deleted tag: ${tag.name}")
                
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(errorMessage = null)
                    onSuccess?.invoke()
                }
            } catch (e: Exception) {
                Log.e("TagManagementViewModel", "删除标签失败: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "删除标签失败: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * 清除错误消息
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}