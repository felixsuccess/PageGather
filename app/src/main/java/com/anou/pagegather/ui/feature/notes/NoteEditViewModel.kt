package com.anou.pagegather.ui.feature.notes

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anou.pagegather.data.local.entity.BookEntity
import com.anou.pagegather.data.local.entity.NoteEntity
import com.anou.pagegather.data.local.entity.NoteAttachmentEntity
import com.anou.pagegather.data.local.entity.TagEntity
import com.anou.pagegather.data.local.entity.AttachmentType
import com.anou.pagegather.data.repository.BookRepository
import com.anou.pagegather.data.repository.NoteRepository
import com.anou.pagegather.data.repository.TagRepository
import com.anou.pagegather.utils.FileOperator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

/**
 * 笔记编辑界面状态
 */
data class NoteEditUiState(
    val note: NoteEntity? = null,
    val availableTags: List<TagEntity> = emptyList(),
    val selectedTagIds: List<Long> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val attachments: List<NoteAttachmentEntity> = emptyList(),
    val isSaving: Boolean = false,
    val quote: String = "", // 添加摘录状态
    val idea: String = "",   // 添加心得状态
    val availableBooks: List<BookEntity> = emptyList(), // 添加可用书籍列表
    val selectedBook: BookEntity? = null // 添加选中的书籍
)

@HiltViewModel
class NoteEditViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    private val tagRepository: TagRepository,
    private val bookRepository: BookRepository
) : ViewModel() {
    // 笔记状态流
    private val _note = MutableStateFlow<NoteEntity?>(null)
    val note: StateFlow<NoteEntity?> = _note.asStateFlow()
    
    private val _uiState = MutableStateFlow(NoteEditUiState())
    val uiState: StateFlow<NoteEditUiState> = _uiState
    
    init {
        loadAvailableTags()
        loadAvailableBooks()
    }

    /**
     * 加载可用标签（仅笔记标签）
     */
    private fun loadAvailableTags() {
        viewModelScope.launch {
            try {
                tagRepository.getNoteTags().collect { tags ->
                    _uiState.value = _uiState.value.copy(
                        availableTags = tags
                    )
                }
            } catch (e: Exception) {
                Log.e("NoteEditViewModel", "加载标签失败: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    errorMessage = "加载标签失败: ${e.message}"
                )
            }
        }
    }

    /**
     * 加载可用书籍列表
     */
    private fun loadAvailableBooks() {
        viewModelScope.launch {
            try {
                bookRepository.getAllBooks().collect { books ->
                    _uiState.value = _uiState.value.copy(
                        availableBooks = books
                    )
                }
            } catch (e: Exception) {
                Log.e("NoteEditViewModel", "加载书籍列表失败: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    errorMessage = "加载书籍列表失败: ${e.message}"
                )
            }
        }
    }

    /**
     * 选择书籍
     */
    fun selectBook(book: BookEntity?) {
        _uiState.value = _uiState.value.copy(selectedBook = book)
    }

    // 加载笔记
    fun loadNote(noteId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                val fetchedNote = noteRepository.getNoteById(noteId)
                
                // 加载笔记关联的标签
                val tagRefs = tagRepository.getTagsByNoteId(noteId)
                
                // 加载笔记附件
                val attachments = noteRepository.getAttachmentsByNoteId(noteId)
                
                // 加载关联的书籍
                val book = fetchedNote?.bookId?.let { bookId ->
                    bookRepository.getBookById(bookId)
                }
                
                combine(
                    tagRefs,
                    tagRepository.getNoteTags(),
                    attachments
                ) { tags, allTags, noteAttachments ->
                    val selectedTagIds = tags.map { it.id }
                    Triple(selectedTagIds, allTags, noteAttachments)
                }.collect { (selectedTagIds, allTags, noteAttachments) ->
                    withContext(Dispatchers.Main) {
                        _note.value = fetchedNote
                        _uiState.value = _uiState.value.copy(
                            note = fetchedNote,
                            availableTags = allTags,
                            selectedTagIds = selectedTagIds,
                            attachments = noteAttachments,
                            quote = fetchedNote?.quote ?: "",
                            idea = fetchedNote?.idea ?: "",
                            selectedBook = book,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("NoteEditViewModel", "加载笔记失败: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "加载笔记失败: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * 更新摘录
     */
    fun updateQuote(quote: String) {
        _uiState.value = _uiState.value.copy(quote = quote)
    }

    /**
     * 更新心得
     */
    fun updateIdea(idea: String) {
        _uiState.value = _uiState.value.copy(idea = idea)
    }

    /**
     * 更新选中的标签（多选）
     */
    fun updateSelectedTags(tagIds: List<Long>) {
        _uiState.value = _uiState.value.copy(selectedTagIds = tagIds)
    }
    
    /**
     * 切换标签选中状态
     */
    fun toggleTagSelection(tagId: Long) {
        val currentSelectedIds = _uiState.value.selectedTagIds.toMutableList()
        if (currentSelectedIds.contains(tagId)) {
            currentSelectedIds.remove(tagId)
        } else {
            currentSelectedIds.add(tagId)
        }
        _uiState.value = _uiState.value.copy(selectedTagIds = currentSelectedIds)
    }

    /**
     * 添加附件
     */
    fun addAttachment(attachment: NoteAttachmentEntity) {
        val currentAttachments = _uiState.value.attachments.toMutableList()
        currentAttachments.add(attachment)
        _uiState.value = _uiState.value.copy(attachments = currentAttachments)
    }

    /**
     * 删除附件
     */
    fun removeAttachment(attachmentId: Long) {
        val currentAttachments = _uiState.value.attachments.filter { it.id != attachmentId }
        _uiState.value = _uiState.value.copy(attachments = currentAttachments)
    }

    /**
     * 处理选择的图片
     */
    fun processSelectedImage(context: Context, uri: Uri, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val outputFile = FileOperator.saveNoteAttachment(context, uri)
                if (outputFile.exists()) {
                    withContext(Dispatchers.Main) {
                        // 根据文件扩展名确定附件类型
                        val fileType = when {
                            outputFile.extension.lowercase() in listOf("jpg", "jpeg", "png", "gif") -> AttachmentType.IMAGE
                            outputFile.extension.lowercase() in listOf("mp3", "wav", "ogg") -> AttachmentType.AUDIO
                            outputFile.extension.lowercase() in listOf("mp4", "avi", "mov") -> AttachmentType.VIDEO
                            else -> AttachmentType.OTHER
                        }
                        
                        // 根据文件类型设置MIME类型
                        val mimeType = when (fileType) {
                            AttachmentType.IMAGE -> "image/${outputFile.extension.lowercase()}"
                            AttachmentType.AUDIO -> when (outputFile.extension.lowercase()) {
                                "mp3" -> "audio/mpeg"
                                "wav" -> "audio/wav"
                                "ogg" -> "audio/ogg"
                                else -> "audio/mpeg"
                            }
                            AttachmentType.VIDEO -> when (outputFile.extension.lowercase()) {
                                "mp4" -> "video/mp4"
                                "avi" -> "video/avi"
                                "mov" -> "video/quicktime"
                                else -> "video/mp4"
                            }
                            else -> "application/octet-stream"
                        }
                        
                        val attachment = NoteAttachmentEntity(
                            id = 0L, // 新附件
                            noteId = 0L, // 将在保存时设置
                            fileName = outputFile.name,
                            filePath = outputFile.absolutePath,
                            fileType = fileType,
                            fileSize = outputFile.length(),
                            mimeType = mimeType,
                            isInlineImage = false,
                            sortOrder = 0,
                            description = "",
                            createdDate = System.currentTimeMillis()
                        )
                        addAttachment(attachment)
                        onResult(true, "附件添加成功")
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onResult(false, "附件保存失败: 文件未创建")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResult(false, "附件保存失败: ${e.message}")
                }
                Log.e("NoteEditViewModel", "Save attachment failed", e)
            }
        }
    }

    /**
     * 处理选择的音频
     */
    fun processSelectedAudio(context: Context, uri: Uri, onResult: (Boolean, String) -> Unit) {
        // 音频处理与图片处理使用相同的逻辑，因为现在都通过saveNoteAttachment处理
        processSelectedImage(context, uri, onResult)
    }

    /**
     * 验证笔记内容
     */
    fun validateNote(quote: String?, idea: String?): String? {
        // 检查是否至少填写了摘录或心得之一
        if (quote.isNullOrBlank() && idea.isNullOrBlank()) {
            return "摘录和心得至少填写一项"
        }
        
        // 检查摘录长度
        if (!quote.isNullOrBlank() && quote.length > 5000) {
            return "摘录长度不能超过5000个字符"
        }
        
        // 检查心得长度
        if (!idea.isNullOrBlank() && idea.length > 10000) {
            return "心得长度不能超过10000个字符"
        }
        
        return null // 验证通过
    }

    // 保存笔记
    fun saveNote(onSuccess: () -> Unit) {
        val currentState = _uiState.value
        val noteToSave = currentState.note?.copy(
            bookId = currentState.selectedBook?.id,
            quote = currentState.quote.ifBlank { null },
            idea = currentState.idea.ifBlank { null },
            updatedDate = System.currentTimeMillis(),
            lastSyncDate = System.currentTimeMillis(),
            attachmentCount = currentState.attachments.size
        ) ?: NoteEntity(
            bookId = currentState.selectedBook?.id,
            quote = currentState.quote.ifBlank { null },
            idea = currentState.idea.ifBlank { null },
            chapterName = "",
            position = "0",
            positionUnit = 0,
            createdDate = System.currentTimeMillis(),
            updatedDate = System.currentTimeMillis(),
            lastSyncDate = System.currentTimeMillis(),
            isDeleted = false,
            attachmentCount = currentState.attachments.size
        )
        
        // 先进行验证
        val validationError = validateNote(currentState.quote, currentState.idea)
        if (validationError != null) {
            _uiState.value = _uiState.value.copy(
                errorMessage = validationError
            )
            return
        }
        
        viewModelScope.launch(Dispatchers.IO) {
            // 设置保存状态
            withContext(Dispatchers.Main) {
                _uiState.value = _uiState.value.copy(isSaving = true)
            }
            
            try {
                noteRepository.runInTransaction {
                    val noteId = if (noteToSave.id == 0L) {
                        val insertedId = noteRepository.insertNote(noteToSave)
                        Log.d("NoteEdit", "Inserted note with id: $insertedId")
                        insertedId
                    } else {
                        noteRepository.updateNote(noteToSave)
                        Log.d("NoteEdit", "Updated note with id: ${noteToSave.id}")
                        noteToSave.id
                    }
                    
                    // 保存标签关联（多选标签）
                    val selectedTagIds = currentState.selectedTagIds
                    tagRepository.updateNoteTags(noteId, selectedTagIds)
                    Log.d("NoteEdit", "Updated note tags: $selectedTagIds")
                    
                    // 保存附件
                    val attachments = currentState.attachments
                    if (attachments.isNotEmpty()) {
                        // 为新附件设置正确的noteId
                        val attachmentsWithNoteId = attachments.map { attachment ->
                            if (attachment.id == 0L) {
                                attachment.copy(noteId = noteId)
                            } else {
                                attachment
                            }
                        }
                        
                        // 插入新附件
                        val newAttachments = attachmentsWithNoteId.filter { it.id == 0L }
                        if (newAttachments.isNotEmpty()) {
                            noteRepository.insertAttachments(newAttachments)
                        }
                        
                        // 更新现有附件
                        val existingAttachments = attachmentsWithNoteId.filter { it.id != 0L }
                        existingAttachments.forEach { attachment ->
                            noteRepository.updateAttachment(attachment)
                        }
                    }
                }
                
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(isSaving = false)
                    onSuccess()
                }
            } catch (e: Exception) {
                Log.e("NoteEdit", "Save failed: ${e.stackTraceToString()}")
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        errorMessage = "保存失败: ${e.message}"
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
    
    /**
     * 设置保存状态
     */
    fun setSavingState(isSaving: Boolean) {
        _uiState.value = _uiState.value.copy(isSaving = isSaving)
    }
}