package com.anou.pagegather.ui.feature.notes

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.anou.pagegather.data.local.entity.NoteEntity
import com.anou.pagegather.ui.feature.bookshelf.NoteTagSelector
import com.anou.pagegather.utils.FileOperator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditScreen(
    noteId: String? = null,
    bookId: Long? = null,  // 添加书籍ID参数
    viewModel: NoteEditViewModel = hiltViewModel(),
    navController: NavController,
) {
    val note by viewModel.note.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var text by remember { mutableStateOf("") }
    var idea by remember { mutableStateOf("") }
    
    var localCoverPath by remember { mutableStateOf("") }
    var photoImageUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(noteId) {
        noteId?.toLongOrNull()?.let { parsedId ->
            if (parsedId != 0L) {
                viewModel.loadNote(parsedId)
            }
        }
    }

    LaunchedEffect(note) {
        text = note?.quote ?: ""
        idea = note?.idea ?: ""
    }
    
    // 显示错误消息
    uiState.errorMessage?.let { errorMessage ->
        LaunchedEffect(errorMessage) {
            android.widget.Toast.makeText(context, errorMessage, android.widget.Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
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
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .clickable(onClick = {
                            val newNote = NoteEntity(
                                bookId = bookId,  // 使用传入的书籍ID
                                title  = "",
                                quote = text,
                                idea = idea,
                                chapterName = "",
                                position = "0",
                                positionUnit = 0,
                                createdDate = System.currentTimeMillis(),
                                updatedDate = System.currentTimeMillis(),
                                lastSyncDate = System.currentTimeMillis(),
                                isDeleted = false
                            )
                            viewModel.saveNote(newNote){
                                navController.popBackStack()
                            }

                        }


                        ))
            })
        },
        content = {


        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(it.calculateTopPadding()))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                        color = MaterialTheme.colorScheme.background
                    )
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        //  .focusRequester(focusRequester)
                        .fillMaxWidth()
                        .heightIn(max = 280.dp)
                        .clickable { },
                    value = text,
                    minLines = 5,
                    textStyle = MaterialTheme.typography.labelLarge,
                    onValueChange = { newText ->
                        text = newText  // 直接赋值新值
                    },
                    label = { Text("摘录") },
                    placeholder =   { Text("摘录") },
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    modifier = Modifier
                        //  .focusRequester(focusRequester)
                        .fillMaxWidth()
                        .heightIn(max = 280.dp)
                        .clickable { },
                    value = idea,
                    minLines = 5,
                    textStyle = MaterialTheme.typography.labelLarge,
                    onValueChange = { newText ->
                        idea = newText  // 直接赋值新值
                    },
                    label = { Text("心得感悟") },
                    placeholder =   { Text("心得感悟") },
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                // 标签选择器
                NoteTagSelector(
                    availableTags = uiState.availableTags,
                    selectedTagIds = uiState.selectedTagIds,
                    onTagSelectionChange = viewModel::toggleTagSelection,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colorScheme.background)
                        .imePadding(),

                    //.padding(8.dp)
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {


                    // 操作按钮组
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        IconButton(onClick = {
                            //TODO: 选 Tag 逻辑
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Grid3x3,
                                contentDescription = "选Tag",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(onClick = {
                            //TODO: 选图 逻辑
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Image,
                                contentDescription = "选图",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }


                        IconButton(onClick = {
                            //TODO: 选音频 逻辑
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

    })

}

// 新增统一处理函数
// 图片处理结果回调
private sealed class ImageResult {
    data class Success(val filePath: String) : ImageResult()
    data class Error(val message: String, val exception: Exception?) : ImageResult()
}

// 统一处理图片结果
private fun handleImageResult(context: Context, result: ImageResult) {
    when (result) {
        is ImageResult.Success -> {
            Toast.makeText(context, "封面保存成功", Toast.LENGTH_SHORT).show()
        }

        is ImageResult.Error -> {
            val errorMsg = result.message + (result.exception?.localizedMessage ?: "")
            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
            Log.e("BookEditScreen", errorMsg, result.exception)
        }
    }
}

// 保存图片到本地
private suspend fun saveImageToLocal(context: Context, uri: Uri): ImageResult {
    return try {
        val outputFile = FileOperator.saveBookCover(context, uri)
        if (outputFile.exists()) {
            ImageResult.Success(outputFile.absolutePath)
        } else {
            ImageResult.Error("封面保存失败: 文件未创建", null)
        }
    } catch (e: Exception) {
        ImageResult.Error("封面保存失败: ", e)
    }
}
