package com.anou.pagegather.ui.feature.bookshelf


import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.anou.pagegather.R
import com.anou.pagegather.data.local.entity.BookEntity
import com.anou.pagegather.data.local.entity.BookSource
import com.anou.pagegather.data.local.entity.BookType
import com.anou.pagegather.data.local.entity.ReadPositionUnit
import com.anou.pagegather.data.local.entity.ReadStatus
import com.anou.pagegather.ui.components.RateBar
import com.anou.pagegather.ui.theme.TextGray

private val PRICE_REGEX = Regex("^\\d+(\\.\\d{1,2})?$")
private val DATE_REGEX = Regex("^\\d{4}-\\d{2}-\\d{2}$")
private val ISBN_REGEX =
    Regex("^(?:ISBN(?:-1[03])?:?\\s*)?(?=[0-9X]{13}\$|[0-9X]{10}\$|(?=(?:[0-9]+[-\\s]){3})[-\\s0-9X]{13}\$)[0-9]{1,5}[-\\s]?[0-9]+[-\\s]?[0-9]+[-\\s]?[0-9X]\$")

// 定义新的出版日期正则表达式
private val PUB_DATE_REGEX = Regex("^$|^\\d{4}$|^\\d{4}-\\d{1,2}$|^\\d{4}-\\d{1,2}-\\d{1,2}$")

//定义新的购买日期正则表达式
private val PURCHASE_DATE_REGEX = Regex("^$|^\\d{4}$|^\\d{4}-\\d{1,2}$|^\\d{4}-\\d{1,2}-\\d{1,2}$")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookEditScreen(
    navController: NavController,
    bookId: String? = null,
    viewModel: BookEditViewModel = hiltViewModel(),
) {
    val book by viewModel.book.collectAsState()
    val formState = remember { mutableStateOf(BookFormState()) }

    LaunchedEffect(bookId) {
        Log.i("BookEditScreen", "" + bookId)
        try {
            bookId?.toLongOrNull()?.let { parsedId ->
                if (parsedId != 0L) {
                    viewModel.loadBook(parsedId)
                }
            }
        } catch (e: Exception) {
            Log.e("BookEditScreen", "加载书籍失败: ${e.message}")
        }
    }

    LaunchedEffect(book) {
        try {
            book?.let {
                formState.value = BookFormState(
                    name = it.name ?: "",
                    author = it.author ?: "",
                    isbn = it.isbn ?: "",
                    press = it.press ?: "",
                    purchasePrice = it.purchasePrice.toString(),
                    publishDate = it.publishDate ?: "",
                    summary = it.summary ?: "",
                    coverUrl = it.coverUrl ?: "",
                    authorIntro = it.authorIntro ?: "",
                    translator = it.translator ?: "",
                    readPosition = it.readPosition.toString(),
                    totalPosition = it.totalPosition.toString(),
                    totalPagination = it.totalPagination,
                    type = it.type,
                    positionUnit = it.positionUnit,
                    bookSourceId = it.bookSourceId,
                    purchaseDate = it.purchaseDate.toString(),
                    bookOrder = it.bookOrder,
                    rating = it.rating,
                    bookMarkModifiedTime = it.bookMarkModifiedTime.toString(),
                    readStatusId = it.readStatus,
                    readStatusChangedDate = it.readStatusChangedDate.toString(),
                    pinned = it.pinned,
                    pinOrder = it.pinOrder,
                    createdDate = it.createdDate.toString(),
                    updatedDate = it.updatedDate.toString(),
                    lastSyncDate = it.lastSyncDate.toString(),
                    isDeleted = it.isDeleted
                )
            }
        } catch (e: Exception) {
            Log.e("BookEditScreen", "更新表单状态失败: ${e.message}")
        }
    }

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
                AnimatedVisibility(
                    (scrollState.value > 64),
                    enter = slideInVertically(initialOffsetY = { it / 4 }),
                    exit = slideOutVertically(targetOffsetY = { it / 4 })
                ) {
                    Text(
                        text = if (bookId != null && bookId.toLongOrNull() != 0L) "编辑书籍" else "新建书籍",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            },
            actions = {
                val showToast = rememberToast()
                // 定义保存逻辑函数
                val saveBookLogic = {
                    val priceValue =
                        formState.value.purchasePrice.ifBlank { "0" }.toDoubleOrNull() ?: 0.0
                    when {
                        formState.value.name.isBlank() -> showToast("书名不能为空")
                        formState.value.purchasePrice.isNotBlank() && !formState.value.purchasePrice.matches(
                            PRICE_REGEX
                        ) -> showToast(
                            "请输入有效的数字格式（如12.34）"
                        )

                        formState.value.publishDate.isNotBlank() && !formState.value.publishDate.matches(
                            PUB_DATE_REGEX
                        ) -> showToast(
                            "出版日期格式不正确，支持格式：空、年、年月、年月日"
                        )

                        formState.value.isbn.isNotBlank() && !formState.value.isbn.matches(
                            ISBN_REGEX
                        ) -> showToast(
                            "ISBN格式无效"
                        )

                        else -> {
                            val newBook = createBookEntity(bookId, formState.value)
                            viewModel.saveBook(newBook) {
                                navController.popBackStack()
                            }
                        }
                    }
                }
                Text(
                    text = if (bookId != null && bookId.toLongOrNull() != 0L) "保存" else "添加",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
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
                Column(
                    Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val handleCoverClick = rememberCoverClick()
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(formState.value.coverUrl).crossfade(true).build(),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(5.dp)
                            .height(120.dp)
                            .aspectRatio(7f / 10f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { handleCoverClick() },
                        error = painterResource(id = R.mipmap.default_cover)
                    )
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
                    onValueChange = { formState.value = formState.value.copy(translator = it) },
                    label = "译者",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                )

                // 为 authorIntro 添加展开折叠状态
                var isAuthorIntroExpanded by remember { mutableStateOf(false) }

                CommonTextField(
                    value = formState.value.authorIntro,
                    onValueChange = { formState.value = formState.value.copy(authorIntro = it) },
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
                    onValueChange = { formState.value = formState.value.copy(publishDate = it) },
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
                    onValueChange = { formState.value = formState.value.copy(summary = it) },
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
                    onItemSelected = { formState.value = formState.value.copy(type = it.code) },
                    label = "书籍类型",
                    itemToString = { it.message }
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
                                formState.value =
                                    formState.value.copy(totalPagination = newPagination)
                            }, label = "总页数", keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number
                            ), modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp)
                        )
                    } else {


                        CommonTextField(
                            value = formState.value.readPosition.toString(), onValueChange = {
                                val newPagination = it.toDoubleOrNull() ?: 0
                                formState.value =
                                    formState.value.copy(readPosition = newPagination.toString())
                            }, label = "阅读进度", keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Decimal
                            ), modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp)
                        )
                    }

                }


                var bookSourceExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = bookSourceExpanded,
                    onExpandedChange = { bookSourceExpanded = !bookSourceExpanded },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                ) {
                    TextField(
                        readOnly = true,
                        value = BookSource.entries.firstOrNull { it.code == formState.value.bookSourceId }?.message
                            ?: "",
                        onValueChange = {},
                        label = { Text("来源") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = bookSourceExpanded
                            )
                        },
                        modifier = Modifier.menuAnchor(
                            type = MenuAnchorType.PrimaryNotEditable, enabled = true
                        ),
                    )
                    ExposedDropdownMenu(
                        expanded = bookSourceExpanded,
                        onDismissRequest = { bookSourceExpanded = false }) {
                        BookSource.entries.forEach { source ->
                            DropdownMenuItem(text = { Text(source.message) }, onClick = {
                                formState.value.bookSourceId = source.code
                                bookSourceExpanded = false
                            })
                        }
                    }
                }
                //TODO：  日历控件

                CommonTextField(
                    value = formState.value.purchaseDate,
                    onValueChange = { formState.value = formState.value.copy(purchaseDate = it) },
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
                        text = "评分", color = TextGray,
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


    //


}

@Composable
fun rememberCoverClick(): () -> Unit {
    val showToast = rememberToast()
    return remember {
        {
            showToast("点击封面功能待实现")
            // 其他封面点击逻辑
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

    TextField(
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
        textStyle = LocalTextStyle.current,
        visualTransformation = visualTransformation,
        keyboardActions = keyboardActions,
        singleLine = maxLines == 1,
        trailingIcon = trailingIcon,
        readOnly = readOnly,
        colors = TextFieldDefaults.colors(
            errorLabelColor = Color.Red,
            errorSupportingTextColor = Color.Red,
            errorTrailingIconColor = Color.Red
        )
//                colors = TextFieldDefaults.colors(
//                focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
//        unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
//        focusedContainerColor = Color.Transparent,
//        unfocusedContainerColor = Color.Transparent,
//        cursorColor = MaterialTheme.colorScheme.onPrimaryContainer,
//        errorCursorColor = MaterialTheme.colorScheme.onPrimaryContainer,
//        focusedIndicatorColor = MaterialTheme.colorScheme.onPrimaryContainer,
//        unfocusedIndicatorColor = MaterialTheme.colorScheme.onPrimaryContainer,
//        focusedPlaceholderColor = MaterialTheme.colorScheme.onPrimaryContainer,
//        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onPrimaryContainer,
//    ),
    )

    if (!isInputValid) {
        Text(
            text = errorMessage,
            color = Color.Red,
            style = TextStyle(fontSize = 12.sp),
            modifier = Modifier.padding(start = 16.dp)
        )
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
    var bookSourceId: Int = 0,
    var purchaseDate: String = "",
    var purchasePrice: String = "",
    var bookOrder: Int = 0,
    var rating: Float = 0.0f,
    var bookMarkModifiedTime: String = "",
    var readStatusId: Int = 0,
    var readStatusChangedDate: String = "",
    var pinned: Int = 0,
    var pinOrder: Int = 0,
    var createdDate: String = "",
    var updatedDate: String = "",
    var lastSyncDate: String = "",
    var isDeleted: Int = 0,
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
            bookSourceId = bookSourceId,
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
        bookSourceId = formState.bookSourceId,
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