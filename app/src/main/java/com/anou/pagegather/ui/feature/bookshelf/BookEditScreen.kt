package com.anou.pagegather.ui.feature.bookshelf


import android.content.ActivityNotFoundException
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.anou.pagegather.R
import com.anou.pagegather.data.local.entity.BookEntity
import com.anou.pagegather.data.local.entity.BookType
import com.anou.pagegather.data.local.entity.ReadPositionUnit
import com.anou.pagegather.data.local.entity.ReadStatus
import com.anou.pagegather.ui.components.RateBar
import com.anou.pagegather.ui.feature.bookshelf.booksource.BookSourceSelector
import com.anou.pagegather.ui.feature.bookshelf.group.BookGroupSelector
import com.anou.pagegather.ui.navigation.Routes
import com.anou.pagegather.ui.theme.extendedColors
import com.anou.pagegather.utils.FileOperator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

private val PRICE_REGEX = Regex("^\\d+(\\.\\d{1,2})?$")
private val DATE_REGEX = Regex("^\\d{4}-\\d{2}-\\d{2}$")
private val ISBN_REGEX =
    Regex("^(?:ISBN(?:-1[03])?:?\\s*)?(?=[0-9X]{13}\$|[0-9X]{10}\$|(?=(?:[0-9]+[-\\s]){3})[-\\s0-9X]{13}\$)[0-9]{1,5}[-\\s]?[0-9]+[-\\s]?[0-9]+[-\\s]?[0-9X]\$")

// 定义新的出版日期正则表达式
private val PUB_DATE_REGEX = Regex("^$|^\\d{4}$|^\\d{4}-\\d{1,2}$|^\\d{4}-\\d{1,2}-\\d{1,2}$")

//定义新的购买日期正则表达式
private val PURCHASE_DATE_REGEX = Regex("^$|^\\d{4}$|^\\d{4}-\\d{1,2}$|^\\d{4}-\\d{1,2}-\\d{1,2}$")

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookEditScreen(
    navController: NavController,
    bookId: String? = null,
    callbackRoute: String? = null, // 添加回调路由参数
    viewModel: BookEditViewModel = hiltViewModel(),
) {
    val book by viewModel.book.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val formState = remember { mutableStateOf(BookFormState()) }
    val backStackEntry by navController.currentBackStackEntryAsState()
    var isFromManage by rememberSaveable { mutableStateOf(false) }
    var localCoverPath by remember { mutableStateOf("") }
    var photoImageUri by remember { mutableStateOf<Uri?>(null) }
    var showGroupSelector by rememberSaveable { mutableStateOf(false) }

    fun updateFormState(book: BookEntity): BookFormState {
        return BookFormState(
            name = book.name ?: "",
            author = book.author ?: "",
            isbn = book.isbn ?: "",
            press = book.press ?: "",
            purchasePrice = book.purchasePrice.toString(),
            publishDate = book.publishDate ?: "",
            summary = book.summary ?: "",
            coverUrl = book.coverUrl ?: "",
            authorIntro = book.authorIntro ?: "",
            translator = book.translator ?: "",
            readPosition = book.readPosition.toString(),
            totalPosition = book.totalPosition.toString(),
            totalPagination = book.totalPagination,
            type = book.type,
            positionUnit = book.positionUnit,
            bookSourceId = book.bookSourceId.toLong(),
            purchaseDate = book.purchaseDate.toString(),
            bookOrder = book.bookOrder,
            rating = book.rating,
            bookMarkModifiedTime = book.bookMarkModifiedTime.toString(),
            readStatusId = book.readStatus,
            readStatusChangedDate = book.readStatusChangedDate.toString(),
            pinned = book.pinned,
            pinOrder = book.pinOrder,
            createdDate = book.createdDate.toString(),
            updatedDate = book.updatedDate.toString(),
            lastSyncDate = book.lastSyncDate.toString(),
            isDeleted = book.isDeleted
        )
    }

    LaunchedEffect(bookId) {
        Log.i("BookEditScreen", "" + bookId)
        try {
            bookId?.toLongOrNull()?.let { parsedId ->
                if (parsedId != 0L) {
                    viewModel.loadBook(parsedId)
                }
            }
        } catch (e: Exception) {
            Log.e("BookEditScreen", "加载书籍失败: ${e.message}", e)
        }
    }

    LaunchedEffect(book) {
        try {
            book?.let {
                formState.value = updateFormState(it)
                localCoverPath = it.coverUrl ?: ""
            }
        } catch (e: Exception) {
            Log.e("BookEditScreen", "更新表单状态失败: ${e.message}", e)
        }
    }
    
    // 监听导航返回事件，处理从分组管理页面返回的情况
    LaunchedEffect(backStackEntry) {
        Log.d("BookEditScreen", "导航状态变化: ${backStackEntry?.destination?.route}, isFromManage=$isFromManage")
        if (isFromManage) {
            // 如果是从分组管理页面返回，自动打开分组选择器
            showGroupSelector = true
            // 重置标记
            isFromManage = false
            Log.d("BookEditScreen", "从分组管理返回，打开分组选择器: showGroupSelector=$showGroupSelector")
        }
    }

    // 获取 context
    val context = LocalContext.current

    // 显示错误消息
    uiState.errorMessage?.let { errorMessage ->
        LaunchedEffect(errorMessage) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }
    var showModal by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    var showPickNetImgModal by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val pickImageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            coroutineScope.launch {
                val result = withContext(Dispatchers.IO) {
                    saveImageToLocal(context, it)
                }
                when (result) {
                    is ImageResult.Success -> {
                        formState.value = formState.value.copy(coverUrl = result.filePath)
                        localCoverPath = result.filePath
                    }
                    is ImageResult.Error -> {
                        handleImageResult(context, result)
                    }
                }
            }
        }
    }

    val takePhoto =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                photoImageUri?.let { uri ->
                    coroutineScope.launch {
                        val result = withContext(Dispatchers.IO) {
                            saveImageToLocal(context, uri)
                        }
                        when (result) {
                            is ImageResult.Success -> {
                                formState.value = formState.value.copy(coverUrl = result.filePath)
                                localCoverPath = result.filePath
                            }

                            else -> {}
                        }
                        withContext(Dispatchers.Main) {
                            handleImageResult(context, result)
                        }
                    }
                }
            }
        }

    // 在线图片输入对话框状态
    var netImageUrl by remember { mutableStateOf("") }


    val scrollState = rememberScrollState()


    Scaffold(containerColor = MaterialTheme.colorScheme.background, topBar = {

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
                    text = if (bookId != null && bookId.toLongOrNull() != 0L) "编辑书籍" else "新建书籍",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            actions = {
                val showToast = rememberToast()
                // 定义保存逻辑函数
                val saveBookLogic = {

                    fun validateForm(formState: BookFormState): Boolean {
                        return when {
                            formState.name.isBlank() -> {
                                showToast("书名不能为空")
                                false
                            }

                            formState.purchasePrice.isNotBlank() && !formState.purchasePrice.matches(
                                PRICE_REGEX
                            ) -> {
                                showToast("请输入有效的数字格式（如12.34）")
                                false
                            }

                            formState.publishDate.isNotBlank() && !formState.publishDate.matches(
                                PUB_DATE_REGEX
                            ) -> {
                                showToast("出版日期格式不正确，支持格式：空、年、年月、年月日")
                                false
                            }

                            formState.isbn.isNotBlank() && !formState.isbn.matches(ISBN_REGEX) -> {
                                showToast("ISBN格式无效")
                                false
                            }

                            else -> true
                        }
                    }

                    if (validateForm(formState.value)) {
                        val selectedBookSourceId = uiState.selectedBookSourceId
                        val newBook = createBookEntity(bookId, formState.value).copy(
                            bookSourceId = selectedBookSourceId?.toInt() ?: 0
                        )
                        viewModel.saveBook(newBook) { actualBookId ->
                            // 根据是否有回调路由决定返回方式
                            if (callbackRoute != null) {
                                // 解码回调路由
                                val decodedCallback = Uri.decode(callbackRoute)
                                // 构建带有新书籍ID的回调URL，使用实际的书籍ID
                                val separator = if (decodedCallback.contains("?")) "&" else "?"
                                val callbackWithBookId = "${decodedCallback}${separator}selectedBookId=${actualBookId}"

                                navController.navigate(callbackWithBookId) {
                                    // 清除当前的书籍编辑页面
                                    popUpTo(navController.currentDestination?.route ?: "") { inclusive = true }
                                    // 保持在栈顶，不要添加新的实例
                                    launchSingleTop = true
                                }
                            } else {
                                // 否则使用默认的返回方式
                                navController.popBackStack()
                            }
                        }
                    }

                }

                Icon(
                    imageVector =   Icons.Default.Save  ,
                    contentDescription = if (bookId != null && bookId.toLongOrNull() != 0L) "保存" else "添加",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .clickable(onClick = saveBookLogic)
                )
            })
    }, content = {
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(it.calculateTopPadding()))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .verticalScroll(scrollState)
                    .padding(10.dp)
            ) {
                // 封面选择区域
                androidx.compose.material3.Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    elevation = androidx.compose.material3.CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth() .background(   MaterialTheme.colorScheme.primaryContainer)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "书籍封面",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Box(
                            modifier = Modifier
                                .size(width = 120.dp, height = 160.dp)
                                .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                .clickable { showModal = true },
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current).data(localCoverPath)
                                    .crossfade(true).build(),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                error = painterResource(id = R.mipmap.default_cover),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )

                            // 添加点击提示
                            if (localCoverPath.isEmpty()) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    androidx.compose.material3.Icon(
                                        imageVector = androidx.compose.material.icons.Icons.Default.Add,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Text(
                                        text = "点击添加封面",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // 更新封面URL到表单状态
                LaunchedEffect(localCoverPath) {
                    formState.value = formState.value.copy(coverUrl = localCoverPath)
                }


                CommonTextField(
                    value = formState.value.name,
                    onValueChange = { formState.value = formState.value.copy(name = it) },
                    label = "书名",
                    placeholder = "书名",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    isError = formState.value.name.isBlank(),
                    validator = { it.isNotBlank() },
                    errorMessage = "书名不能为空"
                )


                CommonTextField(
                    value = formState.value.author,
                    onValueChange = { formState.value = formState.value.copy(author = it) },
                    label = "作者",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                )


                CommonTextField(
                    value = formState.value.translator,
                    // 修改为使用 copy 方法
                    onValueChange = {
                        formState.value = formState.value.copy(translator = it)
                    }, label = "译者", modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                )

                // 为 authorIntro 添加展开折叠状态
                var isAuthorIntroExpanded by remember { mutableStateOf(false) }

                CommonTextField(
                    value = formState.value.authorIntro,
                    onValueChange = {
                        formState.value = formState.value.copy(authorIntro = it)
                    },
                    label = "作者简介",
                    modifier = Modifier
                        .height(if (isAuthorIntroExpanded) 200.dp else 100.dp)
                        .fillMaxWidth()
                        .padding(5.dp),
                    maxLines = if (isAuthorIntroExpanded) Int.MAX_VALUE else 3,
                    trailingIcon = {
                        Text(
                            text = if (isAuthorIntroExpanded) "收起" else "展开",
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable {
                                isAuthorIntroExpanded = !isAuthorIntroExpanded
                            })
                    })


                CommonTextField(
                    value = formState.value.isbn,
                    // 修改为使用 copy 方法
                    onValueChange = { formState.value = formState.value.copy(isbn = it) },
                    label = "ISBN",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                )

                CommonTextField(
                    value = formState.value.press,
                    // 修改为使用 copy 方法
                    onValueChange = { formState.value = formState.value.copy(press = it) },
                    label = "出版社",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                )




                CommonTextField(
                    value = formState.value.publishDate,
                    onValueChange = {
                        formState.value = formState.value.copy(publishDate = it)
                    },
                    label = "出版日期",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    validator = { it.isEmpty() || it.matches(PUB_DATE_REGEX) },
                    errorMessage = "出版日期格式不正确，支持格式：空、年、年月、年月日"
                )
                CommonTextField(
                    value = formState.value.purchasePrice,
                    onValueChange = { newValue ->
                        formState.value =
                            formState.value.copy(purchasePrice = newValue.filter { it.isDigit() || it == '.' })
                    },
                    label = "购买价格",
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    validator = { it.isEmpty() || it.matches(Regex("^\\d+(\\.\\d{1,2})?$")) },
                    errorMessage = "请输入有效的数字格式（如12.34）"
                )

                var isSummaryExpanded by remember { mutableStateOf(false) }
                CommonTextField(
                    value = formState.value.summary,
                    onValueChange = {
                        formState.value = formState.value.copy(summary = it)
                    },
                    label = "内容简介",
                    modifier = Modifier
                        .height(if (isSummaryExpanded) 200.dp else 100.dp) // 根据
                        .fillMaxWidth()
                        .padding(5.dp),
                    maxLines = if (isSummaryExpanded) Int.MAX_VALUE else 5, // 根据展开状态调整最大行数
                    placeholder = "内容简介",
                    trailingIcon = {
                        Text(
                            text = if (isSummaryExpanded) "收起" else "展开",
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable {
                                isSummaryExpanded = !isSummaryExpanded
                            })
                    })
                CommonExposedDropdown(
                    items = BookType.entries,
                    selectedItem = BookType.entries.firstOrNull { it.code == formState.value.type }
                        ?: BookType.PAPER_BOOK,
                    onItemSelected = {
                        formState.value = formState.value.copy(type = it.code)
                    },
                    label = "书籍类型",
                    itemToString = { it.message })

                // 书籍分组选择器
                var showAddGroupDialog by remember { mutableStateOf(false) }
                var newGroupName by remember { mutableStateOf("") }
                // 分组选择器状态由回调显式控制



                //
                BookGroupSelector(
                    availableGroups = uiState.availableGroups,
                    selectedGroupId = uiState.selectedGroupId,
                    onGroupSelectionChange = viewModel::updateSelectedGroup,
                    onAddNewGroup = {
                        // 根据按钮类型执行不同操作
                        if (it == "add") {
                            // 显示添加分组对话框
                            showAddGroupDialog = true
                        } else {
                            // 设置标记，表示从编辑页面导航到分组管理
                            isFromManage = true
                            Log.d("BookEditScreen", "导航到分组管理页面，设置 isFromManage = true")
                            navController.navigate(Routes.ProfileRoutes.BOOK_GROUP_SETTINGS) {
                                launchSingleTop = true
                                // 不使用 popUpTo，确保返回栈保留书籍编辑页面
                            }
                        }
                    },
                    modifier = Modifier.padding(vertical = 8.dp),
                    // 传递外部控制状态
                    showBottomSheet = showGroupSelector,
                    onBottomSheetVisibilityChange = { isVisible ->
                        Log.d("BookEditScreen", "分组选择器可见性变化: $isVisible")
                        showGroupSelector = isVisible
                    }
                )

                // 添加分组对话框
                if (showAddGroupDialog) {
                    AlertDialog(
                        onDismissRequest = { showAddGroupDialog = false },
                        title = { Text("添加新分组") },
                        text = {
                            Column {
                                if (uiState.errorMessage != null) {
                                    Text(
                                        text = uiState.errorMessage ?: "",
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }
                                OutlinedTextField(
                                    value = newGroupName,
                                    onValueChange = { newGroupName = it },
                                    label = { Text("分组名称") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    viewModel.addNewGroup(newGroupName) {
                                        // 成功添加分组后关闭对话框并清空输入
                                        showAddGroupDialog = false
                                        newGroupName = ""
                                    }
                                }
                            ) {
                                Text("确定")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showAddGroupDialog = false }) {
                                Text("取消")
                            }
                        }
                    )
                }

                // 书籍来源选择器
                BookSourceSelector(
                    availableBookSources = uiState.availableBookSources,
                    selectedBookSourceId = uiState.selectedBookSourceId,
                    onBookSourceSelectionChange = viewModel::updateSelectedBookSource,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // 标签选择器
                BookTagSelector(
                    availableTags = uiState.availableTags,
                    selectedTagIds = uiState.selectedTagIds,
                    onTagSelectionChange = viewModel::toggleTagSelection,
                    modifier = Modifier.padding(vertical = 8.dp)
                )


                if (formState.value.type == BookType.PAPER_BOOK.code) {

                    CommonTextField(
                        value = formState.value.readPosition.toString(), onValueChange = {
                            val newPagination = it.toIntOrNull() ?: 0
                            formState.value =
                                formState.value.copy(readPosition = newPagination.toString())
                        }, label = "已读页数", keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ), modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                    )

                    CommonTextField(
                        value = formState.value.totalPagination.toString(), onValueChange = {
                            val newPagination = it.toIntOrNull() ?: 0
                            formState.value = formState.value.copy(totalPagination = newPagination)
                        }, label = "总页数", keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ), modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                    )
                } else {
                    CommonExposedDropdown(
                        items = ReadPositionUnit.entries,
                        selectedItem = ReadPositionUnit.entries.firstOrNull { it.code == formState.value.positionUnit }
                            ?: ReadPositionUnit.PAGE,
                        onItemSelected = {
                            formState.value = formState.value.copy(positionUnit = it.code)
                        },
                        label = "位置单位",
                        itemToString = { it.message })



                    if (formState.value.positionUnit == ReadPositionUnit.PAGE.code) {
                        CommonTextField(
                            value = formState.value.readPosition.toString(),
                            onValueChange = {
                                val newPagination = it.toIntOrNull() ?: 0
                                formState.value =
                                    formState.value.copy(readPosition = newPagination.toString())
                            },
                            label = "已读页数",
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp)
                        )

                        CommonTextField(
                            value = formState.value.totalPagination.toString(),
                            onValueChange = {
                                val newPagination = it.toIntOrNull() ?: 0
                                formState.value =
                                    formState.value.copy(totalPagination = newPagination)
                            },
                            label = "总页数",
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp)
                        )
                    } else {


                        CommonTextField(
                            value = formState.value.readPosition.toString(),
                            onValueChange = {
                                val newPagination = it.toDoubleOrNull() ?: 0
                                formState.value =
                                    formState.value.copy(readPosition = newPagination.toString())
                            },
                            label = "阅读进度",
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Decimal
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp)
                        )
                    }

                }

                //TODO：  日历控件

                CommonTextField(
                    value = formState.value.purchaseDate,
                    onValueChange = {
                        formState.value = formState.value.copy(purchaseDate = it)
                    },
                    label = "购买日期",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    validator = { it.isEmpty() || it.matches(PURCHASE_DATE_REGEX) },
                    errorMessage = "购买日期格式不正确，支持格式：空、年、年月、年月日"
                )

                //TODO:
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "评分",
                        color = MaterialTheme.extendedColors.subtitleColor,
                    )
                    RateBar(
                        rate = formState.value.rating,
                        onRateChanged = {
                            formState.value = formState.value.copy(rating = it)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),


                        )
                }

                //TODO:
                var readStatusExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = readStatusExpanded,
                    onExpandedChange = { readStatusExpanded = !readStatusExpanded }) {
                    TextField(
                        readOnly = true,
                        value = ReadStatus.entries.firstOrNull { it.code == formState.value.readStatusId }?.message
                            ?: "",
                        onValueChange = {},
                        label = { Text("阅读状态") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = readStatusExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                            .menuAnchor(
                                type = MenuAnchorType.PrimaryNotEditable, enabled = true
                            ),
                    )
                    ExposedDropdownMenu(
                        expanded = readStatusExpanded,
                        onDismissRequest = { readStatusExpanded = false }) {
                        ReadStatus.entries.forEach { status ->
                            DropdownMenuItem(text = { Text(status.message) }, onClick = {
                                formState.value.readStatusId = status.code
                                readStatusExpanded = false
                            })
                        }
                    }
                }
            }


        }
    })


    // 封面选择底部模态
    if (showModal) {
        ModalBottomSheet(
            onDismissRequest = { showModal = false },
            sheetState = sheetState
        ) {
            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                Text(
                    text = "选择封面来源",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                HorizontalDivider()

                // 在线URL输入
//                DropdownMenuItem(
//                    text = { Text("在线URL") },
//                    leadingIcon = { Icon(Icons.Default.Link, contentDescription = null) },
//                    onClick = {
//                        showModal = false
//                        showPickNetImgModal = true
//                    }
//                )
                //拍照
                DropdownMenuItem(
                    text = { Text("拍照") },
                    leadingIcon = { Icon(Icons.Default.Link, contentDescription = null) },
                    onClick = {
                        showModal = false
                        try {
                            val imagesFolder = File(context.cacheDir, "capture_picture")
                            if (!imagesFolder.exists()) {
                                imagesFolder.mkdirs()
                            }
                            val file = File.createTempFile("capture_picture_", ".jpg", imagesFolder)
                            val uri = FileProvider.getUriForFile(
                                context, context.packageName + ".provider", file
                            )
                            photoImageUri = uri
                            takePhoto.launch(uri)
                        } catch (e: ActivityNotFoundException) {
                            //Toast.makeText(e.localizedMessage ?: "Unable to take picture.").show();
                        }
                    })
                // 本地图片选择
                DropdownMenuItem(
                    text = { Text("本地图片") },
                    leadingIcon = { Icon(Icons.Default.Image, contentDescription = null) },
                    onClick = {
                        showModal = false
                        pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    })
                HorizontalDivider()
                Text(
                    text = "取消",
                    modifier = Modifier
                        .clickable { showModal = false }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }

    // 在线图片输入对话框
    if (showPickNetImgModal) {
        Dialog(onDismissRequest = { showPickNetImgModal = false }) {
            Box(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp)
                    )
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.background
                        )
                        .padding(16.dp)

                ) {
                    Text(
                        "输入在线图片URL",
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth(),
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    CommonTextField(
                        value = netImageUrl,
                        onValueChange = { netImageUrl = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        label = "在线图片地址",
                        placeholder = "在线图片地址",
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(color = MaterialTheme.colorScheme.background)
                            .align(Alignment.End), horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "取消",
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .clickable {
                                    showPickNetImgModal = false
                                    netImageUrl = ""
                                },
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "确定",
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .clickable {
                                    localCoverPath = netImageUrl
                                    showPickNetImgModal = false
                                    netImageUrl = ""
                                },
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> CommonExposedDropdown(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    label: String,
    itemToString: (T) -> String,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            CommonTextField(
                modifier = Modifier.menuAnchor(
                    type = MenuAnchorType.PrimaryNotEditable, enabled = true
                ),
                readOnly = true,
                value = itemToString(selectedItem),
                onValueChange = {},
                label = label,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded
                    )
                })
            ExposedDropdownMenu(
                expanded = expanded, onDismissRequest = { expanded = false }) {
                items.forEach { item ->
                    DropdownMenuItem(text = { Text(itemToString(item)) }, onClick = {
                        onItemSelected(item)
                        expanded = false
                    })
                }
            }
        }
    }
}

@Composable
fun CommonTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isError: Boolean = false,
    maxLines: Int = 1,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    validator: (String) -> Boolean = { true },
    errorMessage: String = "",
    readOnly: Boolean = false,
    trailingIcon: @Composable() (() -> Unit)? = null,
) {
    var isInputValid by remember { mutableStateOf(true) }

    Column {
        androidx.compose.material3.OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                isInputValid = validator(newValue)
                onValueChange(newValue)
            },
            label = { Text(label) },
            isError = !isInputValid || isError,
            modifier = modifier,
            keyboardOptions = keyboardOptions,
            maxLines = maxLines,
            placeholder = if (placeholder != null) {
                { Text(placeholder) }
            } else null,
            visualTransformation = visualTransformation,
            keyboardActions = keyboardActions,
            singleLine = maxLines == 1,
            trailingIcon = trailingIcon,
            readOnly = readOnly,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
        )

        if (!isInputValid || (isError && errorMessage.isNotEmpty())) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
private fun ItemEditTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier,
    textStyle: TextStyle = LocalTextStyle.current,
    placeholderText: String? = null,
    textAlign: TextAlign = TextAlign.End,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label, style = textStyle, color = MaterialTheme.colorScheme.onSurfaceVariant
        )


        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier,
            textStyle = textStyle.merge(
                textAlign = textAlign,
            ),
            placeholder = {
                Text(
                    placeholderText.toString(), style = textStyle.merge(
                        textAlign = textAlign,
                    )
                )
            },// placeholder ,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.onPrimaryContainer,
                errorCursorColor = MaterialTheme.colorScheme.onPrimaryContainer,
                focusedIndicatorColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onPrimaryContainer,
                focusedPlaceholderColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
        )
    }
}

@Composable
fun rememberToast(): (String) -> Unit {
    val context = LocalContext.current
    return remember {
        { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}


data class BookFormState(
    var name: String = "",
    var author: String = "",
    var isbn: String = "",
    var press: String = "",
    var publishDate: String = "",
    var summary: String = "",
    var coverUrl: String = "",
    var authorIntro: String = "",
    var translator: String = "",
    var readPosition: String = "",
    var totalPosition: String = "",
    var totalPagination: Int = 0,
    var type: Int = 0,
    var positionUnit: Int = 0,
    var bookSourceId: Long = 0,
    var purchaseDate: String = "",
    var purchasePrice: String = "",
    var bookOrder: Int = 0,
    var rating: Float = 0.0f,
    var bookMarkModifiedTime: String = "",
    var readStatusId: Int = 0,
    var readStatusChangedDate: String = "",
    var pinned: Boolean = false,
    var pinOrder: Int = 0,
    var createdDate: String = "",
    var updatedDate: String = "",
    var lastSyncDate: String = "",
    var isDeleted: Boolean = false,
) {
    fun toBookEntity(bookId: Long?): BookEntity {
        return BookEntity(
            id = bookId ?: 0L,
            name = name,
            author = author,
            authorIntro = authorIntro,
            translator = translator,
            isbn = isbn,
            publishDate = publishDate,
            press = press,
            summary = summary,
            readPosition = readPosition.toDoubleOrNull() ?: 0.0,
            totalPosition = totalPosition.toIntOrNull() ?: 0,
            totalPagination = totalPagination,
            type = type,
            positionUnit = positionUnit,
            bookSourceId = bookSourceId.toInt(),
            purchaseDate = purchaseDate.toLongOrNull() ?: 0,
            purchasePrice = purchasePrice.toDoubleOrNull() ?: 0.0,
            bookOrder = bookOrder,
            rating = rating,
            bookMarkModifiedTime = bookMarkModifiedTime.toLongOrNull() ?: 0,
            readStatus = readStatusId,
            readStatusChangedDate = readStatusChangedDate.toLongOrNull() ?: 0,
            pinned = pinned,
            pinOrder = pinOrder,
            createdDate = createdDate.toLongOrNull() ?: 0,
            updatedDate = updatedDate.toLongOrNull() ?: 0,
            lastSyncDate = lastSyncDate.toLongOrNull() ?: 0,
            isDeleted = isDeleted,
            coverUrl = coverUrl
        )
    }
}

fun createBookEntity(bookId: String?, formState: BookFormState): BookEntity {
    val priceValue = formState.purchasePrice.ifBlank { "0" }.toDoubleOrNull() ?: 0.0
    val parsedBookId = bookId?.toLongOrNull()?.takeIf { it != 0L } ?: 0L
    val currentTime = System.currentTimeMillis()

    return BookEntity(
        id = parsedBookId,
        name = formState.name,
        author = formState.author,
        authorIntro = formState.authorIntro,
        translator = formState.translator,
        isbn = formState.isbn,
        publishDate = formState.publishDate,
        press = formState.press,
        summary = formState.summary,
        readPosition = formState.readPosition.toDoubleOrNull() ?: 0.0,
        totalPosition = formState.totalPosition.toIntOrNull() ?: 0,
        totalPagination = formState.totalPagination,
        type = formState.type,
        positionUnit = formState.positionUnit,
        bookSourceId = formState.bookSourceId.toInt(),
        purchaseDate = formState.purchaseDate.toLongOrNull() ?: 0,
        purchasePrice = priceValue,
        bookOrder = formState.bookOrder,
        rating = formState.rating,
        bookMarkModifiedTime = formState.bookMarkModifiedTime.toLongOrNull() ?: 0,
        readStatus = formState.readStatusId,
        readStatusChangedDate = if (parsedBookId != 0L) currentTime else formState.readStatusChangedDate.toLongOrNull()
            ?: 0,
        pinned = formState.pinned,
        pinOrder = formState.pinOrder,
        createdDate = if (parsedBookId == 0L) currentTime else formState.createdDate.toLongOrNull()
            ?: currentTime,
        updatedDate = if (parsedBookId != 0L) currentTime else formState.updatedDate.toLongOrNull()
            ?: 0,
        lastSyncDate = formState.lastSyncDate.toLongOrNull() ?: 0,
        isDeleted = formState.isDeleted,
        coverUrl = formState.coverUrl
    )
}













