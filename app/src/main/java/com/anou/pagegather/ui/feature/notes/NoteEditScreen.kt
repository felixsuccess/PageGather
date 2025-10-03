package com.anou.pagegather.ui.feature.notes

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Grid3x3
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Voicemail
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.anou.pagegather.data.local.entity.NoteAttachmentEntity
import com.anou.pagegather.ui.feature.bookshelf.tag.NoteTagSelector
import com.anou.pagegather.ui.feature.reading.components.BookSelectorDialog
import com.anou.pagegather.ui.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditScreen(
    noteId: String? = null,
    bookId: Long? = null,  // 添加书籍ID参数
    viewModel: NoteEditViewModel = hiltViewModel(),
    navController: NavController,
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    // 附件预览状态
    var previewAttachment by remember { mutableStateOf<NoteAttachmentEntity?>(null) }
    
    // 书籍选择器状态
    var showBookSelector by remember { mutableStateOf(false) }
    
    // 图片选择器
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let { 
                // 处理选择的图片
                viewModel.processSelectedImage(context, it) { success, message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    )
    
    // 音频录制器
    val audioRecorderLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let { 
                // 处理选择的音频
                viewModel.processSelectedAudio(context, it) { success, message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    )

    LaunchedEffect(noteId) {
        noteId?.toLongOrNull()?.let { parsedId ->
            if (parsedId != 0L) {
                viewModel.loadNote(parsedId)
            }
        }
        // 如果提供了bookId参数，直接设置为选中的书籍
        bookId?.let { id ->
            viewModel.loadNote(id)
        }
    }
    
    // 显示错误消息
    uiState.errorMessage?.let { errorMessage ->
        LaunchedEffect(errorMessage) {
            android.widget.Toast.makeText(context, errorMessage, android.widget.Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }
    
    // 附件预览对话框
    previewAttachment?.let { attachment ->
        AttachmentPreviewDialog(
            attachment = attachment,
            onDismiss = { previewAttachment = null },
            onDelete = {
                viewModel.removeAttachment(attachment.id)
                previewAttachment = null
            }
        )
    }
    
    // 书籍选择器对话框
    if (showBookSelector) {
        BookSelectorDialog(
            selectedBook = uiState.selectedBook,
            onBookSelect = { book ->
                viewModel.selectBook(book)
                showBookSelector = false
            },
            onDismiss = { showBookSelector = false },
            onNavigateToAddBook = {
                // 导航到添加书籍页面
                navController.navigate("${Routes.BookRoutes.BOOK_EDIT}/0")
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {

        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            modifier = Modifier.fillMaxWidth(),
            navigationIcon = {
                IconButton(
                    onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "返回",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            title = {
                Text(
                    text = if (noteId != null && noteId.toLongOrNull() != 0L) "编辑书摘" else "新建书摘",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(end = 16.dp)
                )
            },
            actions = {

                Text(
                    text = if (noteId != null && noteId.toLongOrNull() != 0L) "保存" else "添加",
                    style = MaterialTheme.typography.titleLarge,
                    color = if (uiState.isSaving) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) 
                           else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .clickable(enabled = !uiState.isSaving, onClick = {
                            viewModel.saveNote {
                                navController.popBackStack()
                            }
                        }
                    )
                )
            })
        },
        content = {
            Column(
                modifier = Modifier
                    .navigationBarsPadding()
                    .fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(it.calculateTopPadding()))
                // 添加垂直滚动功能
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                            color = MaterialTheme.colorScheme.background
                        )
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()) // 使用垂直滚动
                ) {
                    // 将书籍选择器移动到最上方
                    BookSelector(
                        selectedBook = uiState.selectedBook,
                        onBookSelect = { book ->
                            if (book == null) {
                                // 显示书籍选择器对话框
                                showBookSelector = true
                            } else {
                                // 直接选择书籍
                                viewModel.selectBook(book)
                            }
                        },
                        availableBooks = uiState.availableBooks,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                   
                    
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 280.dp)
                            .clickable { },
                        value = uiState.quote,
                        minLines = 5,
                        textStyle = MaterialTheme.typography.labelLarge,
                        onValueChange = viewModel::updateQuote,
                        label = { Text("摘录") },
                        placeholder =   { Text("摘录") },
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 280.dp)
                            .clickable { },
                        value = uiState.idea,
                        minLines = 5,
                        textStyle = MaterialTheme.typography.labelLarge,
                        onValueChange = viewModel::updateIdea,
                        label = { Text("心得感悟") },
                        placeholder =   { Text("心得感悟") },
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // 标签部分标题
                 
                    // 标签选择器
                    NoteTagSelector(
                        availableTags = uiState.availableTags,
                        selectedTagIds = uiState.selectedTagIds,
                        onTagSelectionChange = viewModel::toggleTagSelection,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // 底部操作按钮组 - 保留在底部但调整样式
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = MaterialTheme.colorScheme.background)
                            .imePadding()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "快速操作",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        // 操作按钮组
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(onClick = {
                                // 选择图片
                                imagePickerLauncher.launch("image/*")
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Image,
                                    contentDescription = "选图",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            IconButton(onClick = {
                                // 选择音频
                                audioRecorderLauncher.launch("audio/*")
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Voicemail,
                                    contentDescription = "音频",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            //TODO: OCR

                            IconButton(onClick = {
                                //TODO: 音转文 逻辑
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Mic,
                                    contentDescription = "音转文",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.imePadding()) // 添加底部内边距
                }
            }
        }
    )

}
