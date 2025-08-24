package com.anou.pagegather.ui.feature.management

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anou.pagegather.data.local.entity.BookGroupEntity
import com.anou.pagegather.data.repository.BookGroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * 分组管理界面状态
 */
data class GroupManagementUiState(
    val groups: List<BookGroupEntity> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isEditDialogVisible: Boolean = false,
    val editingGroup: BookGroupEntity? = null,
    val searchQuery: String = ""
)

/**
 * 分组管理ViewModel
 * 处理分组的CRUD操作和状态管理
 */
@HiltViewModel
class BookGroupManagementViewModel @Inject constructor(
    private val groupRepository: BookGroupRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GroupManagementUiState())
    val uiState: StateFlow<GroupManagementUiState> = _uiState

    private val _searchQuery = MutableStateFlow("")
    
    init {
        loadGroups()
    }

    /**
     * 加载所有分组
     */
    private fun loadGroups() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                combine(
                    groupRepository.getAllGroups(),
                    _searchQuery
                ) { groups, query ->
                    if (query.isBlank()) {
                        groups
                    } else {
                        groups.filter { group ->
                            group.name?.contains(query, ignoreCase = true) == true
                        }
                    }
                }.collect { filteredGroups ->
                    _uiState.value = _uiState.value.copy(
                        groups = filteredGroups,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Log.e("GroupManagementVM", "加载分组失败: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "加载分组失败: ${e.message}"
                )
            }
        }
    }

    /**
     * 搜索分组
     */
    fun searchGroups(query: String) {
        _searchQuery.value = query
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    /**
     * 显示添加分组对话框
     */
    fun showAddGroupDialog() {
        _uiState.value = _uiState.value.copy(
            isEditDialogVisible = true,
            editingGroup = null
        )
    }

    /**
     * 显示编辑分组对话框
     */
    fun showEditGroupDialog(group: BookGroupEntity) {
        _uiState.value = _uiState.value.copy(
            isEditDialogVisible = true,
            editingGroup = group
        )
    }

    /**
     * 隐藏编辑对话框
     */
    fun hideEditDialog() {
        _uiState.value = _uiState.value.copy(
            isEditDialogVisible = false,
            editingGroup = null
        )
    }

    /**
     * 保存分组（新增或更新）
     */
    fun saveGroup(name: String, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val trimmedName = name.trim()
                if (trimmedName.isBlank()) {
                    withContext(Dispatchers.Main) {
                        _uiState.value = _uiState.value.copy(errorMessage = "分组名称不能为空")
                    }
                    return@launch
                }

                val editingGroup = _uiState.value.editingGroup
                val excludeId = editingGroup?.id ?: -1L
                
                // 检查名称是否已存在
                val nameExists = groupRepository.isGroupNameExists(trimmedName, excludeId)
                if (nameExists) {
                    withContext(Dispatchers.Main) {
                        _uiState.value = _uiState.value.copy(errorMessage = "分组名称已存在")
                    }
                    return@launch
                }

                val currentTime = System.currentTimeMillis()
                
                if (editingGroup == null) {
                    // 新增分组 - 获取最大order值+1，使新分组排在最前面
                    val maxOrder = groupRepository.getMaxOrder() ?: 0
                    val newGroup = BookGroupEntity(
                        name = trimmedName,
                        groupOrder = maxOrder + 1,
                        createdDate = currentTime,
                        updatedDate = currentTime,
                        lastSyncDate = currentTime
                    )
                    groupRepository.insertGroup(newGroup)
                } else {
                    // 更新分组
                    val updatedGroup = editingGroup.copy(
                        name = trimmedName,
                        updatedDate = currentTime
                    )
                    groupRepository.updateGroup(updatedGroup)
                }

                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        isEditDialogVisible = false,
                        editingGroup = null,
                        errorMessage = null
                    )
                    onSuccess?.invoke()
                }
            } catch (e: Exception) {
                Log.e("GroupManagementVM", "保存分组失败: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "保存分组失败: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * 删除分组
     */
    fun deleteGroup(group: BookGroupEntity, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                groupRepository.deleteGroup(group.id)
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(errorMessage = null)
                    onSuccess?.invoke()
                }
            } catch (e: Exception) {
                Log.e("GroupManagementVM", "删除分组失败: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "删除分组失败: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * 移动分组位置（拖拽排序）
     */
    fun moveGroup(fromIndex: Int, toIndex: Int) {
        val currentGroups = _uiState.value.groups.toMutableList()
        if (fromIndex >= 0 && fromIndex < currentGroups.size && 
            toIndex >= 0 && toIndex < currentGroups.size && 
            fromIndex != toIndex) {
            
            // 更新本地状态
            val movedGroup = currentGroups.removeAt(fromIndex)
            val clampedToIndex = toIndex.coerceIn(0, currentGroups.size)
            currentGroups.add(clampedToIndex, movedGroup)
            
            _uiState.value = _uiState.value.copy(groups = currentGroups)
            
            // 延迟更新数据库，避免频繁操作
            updateGroupOrdersInDatabase(currentGroups)
        }
    }
    
    /**
     * 更新数据库中的分组顺序
     */
    private fun updateGroupOrdersInDatabase(groups: List<BookGroupEntity>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 重新分配 group_order 值，从最大值开始递减
                val maxOrder = groups.size * 10 // 给予足够的间隔
                val orderUpdates = groups.mapIndexed { index, group ->
                    group.id to (maxOrder - index * 10)
                }.toMap()
                
                groupRepository.updateGroupOrders(orderUpdates)
                
                Log.d("GroupManagementVM", "更新分组顺序成功: $orderUpdates")
            } catch (e: Exception) {
                Log.e("GroupManagementVM", "更新分组顺序失败: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "更新排序失败: ${e.message}"
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