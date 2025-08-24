package com.anou.pagegather.ui.feature.notes

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Grid3x3
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Voicemail
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.anou.pagegather.R
import com.anou.pagegather.data.local.entity.NoteEntity
import com.anou.pagegather.ui.theme.TextGray
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 随记页面
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    viewModel: NoteListViewModel = hiltViewModel(),
    onNavigateToNoteList: () -> Unit,
    onNavigateToNoteTags: () -> Unit,
    onNoteClick: (NoteEntity) -> Unit,
    onNavigateToNoteEdit: (Long) -> Unit,
) {

    val notes by viewModel.notes.collectAsState(initial = emptyList<NoteEntity>())

    val tabTitles = listOf(
        "书摘", "漫步"
    )
    var selectedTab by remember { mutableIntStateOf(0) }
    var isShowInput = remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            modifier = Modifier.fillMaxWidth(),
            title = {
                Column {
                    // 标题
                    Text(
                        text = "书摘漫步",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    // 选项卡
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.primary,
                        indicator = { tabPositions ->
                            TabRowDefaults.Indicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    ) {
                        tabTitles.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (selectedTab == index) FontWeight.Medium else FontWeight.Normal
                                    )
                                }
                            )
                        }
                    }
                }
            },
            actions = {
                IconButton(onClick = {
                    /* TODO: 添加搜索功能 */
                }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "搜索",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = {
                    onNavigateToNoteEdit(0)
                }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "添加笔记",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = {
                    /* TODO: 添加更多选项     如列表显示  排序 等 */
                }) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "更多选项",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            },

            )

        Box(
            Modifier
                .weight(1f)
                .navigationBarsPadding()
        ) {
            // Spacer(modifier = Modifier.height(it.calculateTopPadding()))

            when (selectedTab) {
                0 -> {
                    val snackbarHostState = remember { SnackbarHostState() }
                    Box(modifier = Modifier.fillMaxSize()) {
                        NoteListScreen(
                            //noteListUIState = uiState,
                            //  viewModel = viewModel,
                            notes = notes,
                            snackbarHostState = snackbarHostState,
                            onNavigateToNoteEdit = { },
                            onNavigateToNoteView = { },
                            onNavigateToNoteRoaming = { },
                        )
                        SnackbarHost(
                            hostState = snackbarHostState,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .navigationBarsPadding()
                        )
                    }
                }

                1 -> {
                    NoteShowContent()
                }
            }
        }

        if (isShowInput.value == true) {
            var text by remember { mutableStateOf("") }  // 修改这里，添加by关键字

            Box(
                contentAlignment = Alignment.BottomCenter,
                modifier = Modifier
                    .fillMaxSize()
                    // background( shape= RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))效果一致
                    .clickable() { //showRipple = false
                        isShowInput.value = false
                    })

            {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                            color = MaterialTheme.colorScheme.background
                        )
                        .padding(horizontal = 8.dp, vertical = 16.dp)

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
                        label = { Text("神评论") },
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

                        // 返回按钮

                        Button(onClick = {

                            viewModel.insertNote(
                                NoteEntity(
                                    bookId = null,
                                    chapterName = "",
                                    quote = text,
                                    idea = "",
                                    position = "0",
                                    positionUnit = 0,
                                    createdDate = System.currentTimeMillis(),
                                    updatedDate = System.currentTimeMillis(),
                                    lastSyncDate = System.currentTimeMillis(),
                                    isDeleted =false
                                )                            )

                            isShowInput.value = false
                        }

                        ) {
                            Text("保存")

                        }

                    }
                    Spacer(modifier = Modifier.imePadding()) // 添加底部内边距
                }
            }
        }

    }
}


// 随记子页面：显示随记列表，包含空状态提示和笔记卡片列表
@Composable
fun NoteListScreen(
    notes: List<NoteEntity>,
    snackbarHostState: SnackbarHostState,
    onNavigateToNoteEdit: (String) -> Unit,
    onNavigateToNoteView: (String) -> Unit,
    onNavigateToNoteRoaming: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp, 15.dp, 5.dp, 5.dp)
            .background(MaterialTheme.colorScheme.surface),
    ) {


        if (notes.isEmpty()) {
            // 空列表状态
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.empty),
                    contentDescription = "空列表",
                )


                Text(
                    text = "有感悟的时间再来添加",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        LazyColumn(
            modifier = Modifier
            //.padding(padding)
        ) {
            items(notes.size) { index ->
                var note = notes[index]
                NoteCard(note = note, onNoteClick = {
                    //onNoteClick(note)
                    // onNavigateToNoteView();
                    Log.d("NOTES SCREEN", "onClick");

                    scope.launch {
                        snackbarHostState.showSnackbar("点击了随记")
                    }
                }, onLongClick = {
                    //TODO: 长按操作（待实现）：编辑笔记、删除笔记、管理标签、复制内容、分享笔记
                    // showToast("长按未实现 -》 编辑  删除 标签 复制  分享 等")
                    Log.d("NOTES SCREEN", "onLongClick");
                    scope.launch {
                        snackbarHostState.showSnackbar("长按随记-》未实现 ")
                    }
                    //ShowNoteContextMenu(note)
                }

                )
            }
        }

    }

}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NoteCard(
    note: NoteEntity,
    onNoteClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            // .clickable { onNoteClick() }
            .combinedClickable(
                onClick = {
                    onNoteClick()
                },
                onLongClick = {
                    onLongClick()
                },
            ),

        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = note.createdDate.toTime(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
            )
            Spacer(modifier = Modifier.height(4.dp))
            //TODO:临时
            var tags = listOf(
                "书摘", "感想", "神评论", "妙笔生花"
            ) //note.tags;
            if (tags.isNotEmpty()) {
                // 如果 有标签   才显示
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    tags.forEach { tag ->
                        Text(
                            text = "#$tag",
                            modifier = Modifier
//                                .background(
//                                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
//                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            //  style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
            }

            if (note.quote.toString().isNotEmpty()) {

                Text(
                    text = note.quote.toString(), maxLines = 4, overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))
            }
            if (note.idea.toString().isNotEmpty()) {
                Text(
                    text = note.idea.toString(), maxLines = 4, overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
//TODO: 图片位置
//            if (note.attachments.isNotEmpty() == true) {
//                note.attachments.forEach { tag ->
//                    Text(
//                        text = "附件$tag.fileName",
//                        modifier = Modifier
//                            .background(
//                                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
//                            )
//                            .padding(horizontal = 8.dp, vertical = 4.dp),
//                        style = MaterialTheme.typography.labelSmall,
//                        color = MaterialTheme.colorScheme.secondary
//                    )
//                }
//
//                Spacer(modifier = Modifier.height(4.dp))
//            }


        }
    }
}

private fun Long.toTime(): String {
//    val dateTime = Date(this)
//    val format = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ENGLISH)
//    return format.format(dateTime)
    return this.toTimeFormat("yyyy/MM/dd HH:mm:ss")
}

private fun Long.toTimeFormat(formatStr: String): String {
    val dateTime = Date(this)
    val format = SimpleDateFormat(formatStr, Locale.ENGLISH)
    return format.format(dateTime)
}

@Composable
private fun LazyItemScope.ShowNoteContextMenu(noteEntity: NoteEntity) {

    val expanded = remember { mutableStateOf(false) }

    DropdownMenu(
        expanded = expanded.value, onDismissRequest = { expanded.value = false }) {
        DropdownMenuItem(text = { Text("编辑") }, onClick = {
            expanded.value = false
            // navController.navigate("note_edit/${noteEntity.id}")
        })
        DropdownMenuItem(text = { Text("删除") }, onClick = {
            expanded.value = false
            // viewModel.deleteNote(note.id)
        })
    }

    // 显示上下文菜单
    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(
            onClick = { expanded.value = true }, modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(Icons.Default.MoreVert, contentDescription = "更多选项")
        }
    }

}




// 随记子页面 - 随记查看
@Composable
fun NoteViewScreen(noteId: String?) {
    Text(
        text = "随记查看：查看指定随记详情的页面",
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun NoteShowContent() {
    //书摘漫步
}
