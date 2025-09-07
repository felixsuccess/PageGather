package com.anou.pagegather.ui.navigation

import android.net.Uri
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.anou.pagegather.ui.feature.bookshelf.BookDetailScreen
import com.anou.pagegather.ui.feature.bookshelf.BookEditScreen
import com.anou.pagegather.ui.feature.bookshelf.BookShelfScreen
import com.anou.pagegather.ui.feature.bookshelf.group.BookShelfGroupDetailScreen
import com.anou.pagegather.ui.feature.bookshelf.booksource.BookShelfSourceDetailScreen
import com.anou.pagegather.ui.feature.bookshelf.rating.BookShelfRatingDetailScreen
import com.anou.pagegather.ui.feature.bookshelf.status.BookShelfStatusDetailScreen
import com.anou.pagegather.ui.feature.bookshelf.tag.BookShelfTagDetailScreen
import com.anou.pagegather.ui.feature.management.BookGroupManagementScreen
import com.anou.pagegather.ui.feature.management.BookSourceManagementScreen
import com.anou.pagegather.ui.feature.management.TagManagementScreen
import com.anou.pagegather.ui.feature.my.ProfileScreen
import com.anou.pagegather.ui.feature.notes.NoteEditScreen
import com.anou.pagegather.ui.feature.notes.NoteViewScreen
import com.anou.pagegather.ui.feature.notes.NotesScreen
import com.anou.pagegather.ui.feature.timer.ForwardTimerScreen
import com.anou.pagegather.ui.feature.timer.ReverseTimerScreen
import com.anou.pagegather.ui.feature.quickactions.*
import com.anou.pagegather.ui.feature.timer.GoalSettingScreen
import com.anou.pagegather.ui.feature.timer.PeriodicReminderScreen
import com.anou.pagegather.ui.feature.timer.ReadingPlanScreen
import com.anou.pagegather.ui.feature.reading.SaveRecordScreen
import com.anou.pagegather.ui.feature.reading.RecordSource

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier, navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = Routes.DashboardRoutes.STATISTICS,
        modifier = modifier,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(350)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(350)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(350)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(350)
            )
        }) {


        // 我的页面
        composable(Routes.ProfileRoutes.PROFILE) {
            ProfileScreen(
                onNavigateToTagSettings = { navController.navigate(Routes.ProfileRoutes.TAG_SETTINGS) },
                onNavigateToGroupSettings = { navController.navigate(Routes.ProfileRoutes.BOOK_GROUP_SETTINGS) },
                onNavigateToBookSourceSettings = { navController.navigate(Routes.ProfileRoutes.BOOK_SOURCE_SETTINGS) }
            )
        }

        // 书架页面
        composable(Routes.BookRoutes.BOOKS) {
            BookShelfScreen(
                onToBookViewClick = { bookId ->
                    navController.navigate("${Routes.BookRoutes.BOOK_DETAIL}/$bookId")
                },
                onToBookAddClick = {
                    navController.navigate("${Routes.BookRoutes.BOOK_EDIT}/0")
                },
                onNavigateToBookEdit = { bookId ->
                    // 导航到书籍编辑页面
                    navController.navigate("${Routes.BookRoutes.BOOK_EDIT}/$bookId")
                },
                onNavigateToBookGroups = { navController.navigate(Routes.ProfileRoutes.BOOK_GROUP_SETTINGS) },
                onNavigateToTimer = { bookId ->
                    val bookIdParam = if (bookId > 0L) "?selectedBookId=$bookId" else ""
                    navController.navigate("${Routes.TimeManagementRoutes.FORWARD_TIMER}$bookIdParam")
                },
                onNavigateToQuickActions = { navController.navigate(Routes.QuickActionsRoutes.QUICK_ACTIONS) },
                onNavigateToNoteEdit = { noteId, bookId ->
                    // 导航到笔记编辑页面，传递笔记ID和书籍ID
                    navController.navigate("${Routes.NoteRoutes.NOTE_EDIT}/$noteId?book_id=$bookId")
                },

                onNavigateToGroupDetail = { groupId, groupName ->
                    navController.navigate(Routes.BookRoutes.bookGroupDetail(groupId, groupName))
                },
                onNavigateToSourceDetail = { sourceId, sourceName ->
                    navController.navigate(Routes.BookRoutes.bookSourceDetail(sourceId, sourceName))
                },
                onNavigateToTagDetail = { tagId, tagName, tagColor ->
                    navController.navigate(Routes.BookRoutes.bookTagDetail(tagId, tagName))
                },
                onNavigateToStatusDetail = { status, statusName ->
                    navController.navigate(Routes.BookRoutes.bookStatusDetail(status, statusName))
                },
                onNavigateToRatingDetail = { rating, ratingValue ->
                    navController.navigate(Routes.BookRoutes.bookRatingDetail(rating, ratingValue))
                }
            )

        }

        composable(
            route = "${Routes.BookRoutes.BOOK_EDIT}/{${Routes.BookRoutes.BOOK_ID}}",
            arguments = listOf(navArgument(Routes.BookRoutes.BOOK_ID) { type = NavType.StringType })
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString(Routes.BookRoutes.BOOK_ID)
            BookEditScreen(bookId = bookId, navController = navController)
        }

        // 添加支持回调参数的书籍编辑路由
        composable(
            route = "${Routes.BookRoutes.BOOK_EDIT}/{${Routes.BookRoutes.BOOK_ID}}?callback={callback}",
            arguments = listOf(
                navArgument(Routes.BookRoutes.BOOK_ID) { type = NavType.StringType },
                navArgument("callback") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString(Routes.BookRoutes.BOOK_ID)
            val callback = backStackEntry.arguments?.getString("callback")
            BookEditScreen(
                bookId = bookId, 
                navController = navController,
                callbackRoute = callback
            )
        }

        composable(
            route = "${Routes.BookRoutes.BOOK_DETAIL}/{${Routes.BookRoutes.BOOK_ID}}",
            arguments = listOf(navArgument(Routes.BookRoutes.BOOK_ID) { type = NavType.StringType })
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString(Routes.BookRoutes.BOOK_ID)

            BookDetailScreen(
                navController = navController,
                bookId = bookId,
                onEditBookClick = { bookId ->
                    navController.navigate("${Routes.BookRoutes.BOOK_EDIT}/$bookId")
                },
                onNavigateToNoteEdit = { noteId ->
                    navController.navigate("${Routes.NoteRoutes.NOTE_EDIT}/$noteId")
                },
                onNavigateToNewNote = {
                    navController.navigate(Routes.NoteRoutes.NOTE_EDIT)
                },
                onBackClick = {
                    navController.popBackStack()
                },
                onNavigateToTimer = { bookId ->
                    // 从书籍详情页导航到正向计时器，传递来源信息
                    navController.navigate("${Routes.TimeManagementRoutes.FORWARD_TIMER}?selectedBookId=$bookId&from=book_detail&bookId=$bookId")
                }
            )
        }


        // 统计子页面


        // 统计页面
        composable(Routes.DashboardRoutes.STATISTICS) {
            StatisticsScreen(
                onNavigateToTimeline = { navController.navigate(Routes.DashboardRoutes.TIMELINE) },
                onNavigateToCalendar = { navController.navigate(Routes.DashboardRoutes.CALENDAR) },
                onNavigateToCharts = { navController.navigate(Routes.DashboardRoutes.CHARTS) })
        }

        // 我的子页面
        composable(Routes.ProfileRoutes.TAG_SETTINGS) { 
            TagManagementScreen(navController = navController)
        }
        composable(Routes.ProfileRoutes.BOOK_GROUP_SETTINGS) { 
            BookGroupManagementScreen(navController = navController)
        }
        composable(Routes.ProfileRoutes.BOOK_SOURCE_SETTINGS) {
            BookSourceManagementScreen(navController = navController)
        }


        // 随记页面
        composable(Routes.NoteRoutes.NOTE_LIST) {
            NotesScreen(
                onNavigateToNoteList = { navController.navigate(Routes.NoteRoutes.NOTE_LIST) },
                onNavigateToNoteTags = { navController.navigate(Routes.ProfileRoutes.TAG_SETTINGS) },
                onNoteClick = { noteId ->
                    navController.navigate("${Routes.NoteRoutes.NOTE_VIEW}/$noteId")
                },
                onNavigateToNoteEdit = { noteId ->
                    navController.navigate("${Routes.NoteRoutes.NOTE_EDIT}/$noteId")
                },
            )
        }

        composable(
            route = "${Routes.NoteRoutes.NOTE_EDIT}/{${Routes.NoteRoutes.ARG_NOTE_ID}}?book_id={book_id}",
            arguments = listOf(
                navArgument(Routes.NoteRoutes.ARG_NOTE_ID) {
                    type = NavType.StringType
                },
                navArgument("book_id") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString(Routes.NoteRoutes.ARG_NOTE_ID)
            val bookId = backStackEntry.arguments?.getString("book_id")?.toLongOrNull()
            NoteEditScreen(noteId = noteId, bookId = bookId, navController = navController)
        }

        composable(
            route = "${Routes.NoteRoutes.NOTE_VIEW}/{${Routes.NoteRoutes.ARG_NOTE_ID}}",
            arguments = listOf(navArgument(Routes.NoteRoutes.ARG_NOTE_ID) {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString(Routes.NoteRoutes.ARG_NOTE_ID)
            NoteViewScreen(noteId = noteId)
        }

        composable(Routes.DashboardRoutes.TIMELINE) { TimelineScreen() }
        composable(Routes.DashboardRoutes.CALENDAR) { CalendarScreen() }
        composable(Routes.DashboardRoutes.CHARTS) { ChartsScreen() }

        // 时间管理相关页面
        composable(
            route = "${Routes.TimeManagementRoutes.FORWARD_TIMER}?selectedBookId={selectedBookId}&from={from}&bookId={bookId}&groupId={groupId}&groupName={groupName}&sourceId={sourceId}&sourceName={sourceName}&tagId={tagId}&tagName={tagName}&status={status}&statusName={statusName}&rating={rating}&ratingValue={ratingValue}",
            arguments = listOf(
                navArgument("selectedBookId") { 
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("from") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("bookId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("groupId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("groupName") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("sourceId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("sourceName") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("tagId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("tagName") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("status") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("statusName") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("rating") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("ratingValue") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val selectedBookId = backStackEntry.arguments?.getString("selectedBookId")?.toLongOrNull()
            val from = backStackEntry.arguments?.getString("from")
            val bookId = backStackEntry.arguments?.getString("bookId")
            val groupId = backStackEntry.arguments?.getString("groupId")
            val groupName = backStackEntry.arguments?.getString("groupName")
            val sourceId = backStackEntry.arguments?.getString("sourceId")
            val sourceName = backStackEntry.arguments?.getString("sourceName")
            val tagId = backStackEntry.arguments?.getString("tagId")
            val tagName = backStackEntry.arguments?.getString("tagName")
            val status = backStackEntry.arguments?.getString("status")
            val statusName = backStackEntry.arguments?.getString("statusName")
            val rating = backStackEntry.arguments?.getString("rating")
            val ratingValue = backStackEntry.arguments?.getString("ratingValue")
            
            ForwardTimerScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSaveRecord = { elapsedTime, startTime, bookId ->
                    val bookIdParam = bookId?.let { "&selectedBookId=$it" } ?: ""
                    val fromParam = from?.let { "&from=$it" } ?: ""
                    val fromBookIdParam = bookId?.let { "&bookId=$it" } ?: ""
                    val groupIdParam = groupId?.let { "&groupId=$it" } ?: ""
                    val groupNameParam = groupName?.let { "&groupName=$it" } ?: ""
                    val sourceIdParam = sourceId?.let { "&sourceId=$it" } ?: ""
                    val sourceNameParam = sourceName?.let { "&sourceName=$it" } ?: ""
                    val tagIdParam = tagId?.let { "&tagId=$it" } ?: ""
                    val tagNameParam = tagName?.let { "&tagName=$it" } ?: ""
                    val statusParam = status?.let { "&status=$it" } ?: ""
                    val statusNameParam = statusName?.let { "&statusName=$it" } ?: ""
                    val ratingParam = rating?.let { "&rating=$it" } ?: ""
                    val ratingValueParam = ratingValue?.let { "&ratingValue=$it" } ?: ""
                    println("AppNavigation: 导航到保存记录页面")
                    println("AppNavigation: elapsedTime = $elapsedTime")
                    println("AppNavigation: startTime = $startTime")
                    println("AppNavigation: bookId = $bookId")
                    navController.navigate("${Routes.ReadingRoutes.SAVE_RECORD}?source=TIMER&elapsedTime=$elapsedTime&startTime=$startTime$bookIdParam$fromParam$fromBookIdParam$groupIdParam$groupNameParam$sourceIdParam$sourceNameParam$tagIdParam$tagNameParam$statusParam$statusNameParam$ratingParam$ratingValueParam")
                },
                selectedBookId = selectedBookId
            ) 
        }
        
        composable(Routes.TimeManagementRoutes.REVERSE_TIMER) { 
            ReverseTimerScreen(
                onNavigateBack = { navController.popBackStack() }
            ) 
        }
        composable(Routes.TimeManagementRoutes.GOAL_SETTING) { GoalSettingScreen() }
        composable(Routes.TimeManagementRoutes.READING_PLAN) { ReadingPlanScreen() }
        composable(Routes.TimeManagementRoutes.PERIODIC_REMINDER) { PeriodicReminderScreen() }

        // 阅读记录相关页面
        composable(
            route = "${Routes.ReadingRoutes.SAVE_RECORD}?source={source}&elapsedTime={elapsedTime}&startTime={startTime}&selectedBookId={selectedBookId}&from={from}&bookId={bookId}&groupId={groupId}&groupName={groupName}&sourceId={sourceId}&sourceName={sourceName}&tagId={tagId}&tagName={tagName}&status={status}&statusName={statusName}&rating={rating}&ratingValue={ratingValue}",
            arguments = listOf(
                navArgument("source") { 
                    type = NavType.StringType
                    defaultValue = "MANUAL"
                },
                navArgument("elapsedTime") { 
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("startTime") { 
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("selectedBookId") { 
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("from") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("bookId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("groupId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("groupName") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("sourceId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("sourceName") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("tagId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("tagName") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("status") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("statusName") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("rating") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("ratingValue") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val sourceStr = backStackEntry.arguments?.getString("source") ?: "MANUAL"
            val source = try {
                RecordSource.valueOf(sourceStr)
            } catch (e: IllegalArgumentException) {
                RecordSource.MANUAL
            }
            val elapsedTime = backStackEntry.arguments?.getString("elapsedTime")?.toLongOrNull()
            val startTime = backStackEntry.arguments?.getString("startTime")?.toLongOrNull()
            val selectedBookId = backStackEntry.arguments?.getString("selectedBookId")?.toLongOrNull()
            val from = backStackEntry.arguments?.getString("from")
            val bookId = backStackEntry.arguments?.getString("bookId")
            val groupId = backStackEntry.arguments?.getString("groupId")
            val groupName = backStackEntry.arguments?.getString("groupName")
            val sourceId = backStackEntry.arguments?.getString("sourceId")
            val sourceName = backStackEntry.arguments?.getString("sourceName")
            val tagId = backStackEntry.arguments?.getString("tagId")
            val tagName = backStackEntry.arguments?.getString("tagName")
            val status = backStackEntry.arguments?.getString("status")
            val statusName = backStackEntry.arguments?.getString("statusName")
            val rating = backStackEntry.arguments?.getString("rating")
            val ratingValue = backStackEntry.arguments?.getString("ratingValue")
            
            SaveRecordScreen(
                source = source,
                onNavigateBack = { 
                    // 根据用户是否更改了书籍来决定返回路径
                    val returnBookId = when (from) {
                        "book_detail" -> bookId?.toLongOrNull()
                        else -> null
                    }
                    
                    if (returnBookId != null && returnBookId > 0) {
                        // 如果有返回书籍ID，返回到该书籍的详情页
                        navController.navigate("${Routes.BookRoutes.BOOK_DETAIL}/$returnBookId") {
                            // 清除导航堆栈中保存记录页面及以下的所有页面
                            popUpTo(Routes.ReadingRoutes.SAVE_RECORD) { inclusive = true }
                        }
                    } else {
                        // 根据来源决定返回路径
                        when (from) {
                            "book_detail" -> {
                                // 如果来自书籍详情页，返回到对应的书籍详情页
                                bookId?.let { 
                                    navController.navigate("${Routes.BookRoutes.BOOK_DETAIL}/$it") {
                                        // 清除导航堆栈中保存记录页面及以下的所有页面
                                        popUpTo(Routes.ReadingRoutes.SAVE_RECORD) { inclusive = true }
                                    }
                                } ?: navController.popBackStack()
                            }
                            "book_group_detail" -> {
                                // 如果来自分组详情页面，返回到对应的分组详情页面
                                groupId?.let {
                                    navController.navigate(Routes.BookRoutes.bookGroupDetail(groupId.toLong(), groupName ?: "")) {
                                        // 清除导航堆栈中保存记录页面及以下的所有页面
                                        popUpTo(Routes.ReadingRoutes.SAVE_RECORD) { inclusive = true }
                                    }
                                } ?: navController.popBackStack()
                            }
                            "book_source_detail" -> {
                                // 如果来自来源详情页面，返回到对应的来源详情页面
                                sourceId?.let {
                                    navController.navigate(Routes.BookRoutes.bookSourceDetail(sourceId.toLong(), sourceName ?: "")) {
                                        // 清除导航堆栈中保存记录页面及以下的所有页面
                                        popUpTo(Routes.ReadingRoutes.SAVE_RECORD) { inclusive = true }
                                    }
                                } ?: navController.popBackStack()
                            }
                            "book_tag_detail" -> {
                                // 如果来自标签详情页面，返回到对应的标签详情页面
                                tagId?.let {
                                    navController.navigate(Routes.BookRoutes.bookTagDetail(tagId.toLong(), tagName ?: "")) {
                                        // 清除导航堆栈中保存记录页面及以下的所有页面
                                        popUpTo(Routes.ReadingRoutes.SAVE_RECORD) { inclusive = true }
                                    }
                                } ?: navController.popBackStack()
                            }
                            "book_status_detail" -> {
                                // 如果来自状态详情页面，返回到对应的状态详情页面
                                status?.let {
                                    navController.navigate(Routes.BookRoutes.bookStatusDetail(status.toInt(), statusName ?: "")) {
                                        // 清除导航堆栈中保存记录页面及以下的所有页面
                                        popUpTo(Routes.ReadingRoutes.SAVE_RECORD) { inclusive = true }
                                    }
                                } ?: navController.popBackStack()
                            }
                            "book_rating_detail" -> {
                                // 如果来自评分详情页面，返回到对应的评分详情页面
                                rating?.let {
                                    navController.navigate(Routes.BookRoutes.bookRatingDetail(rating.toInt(), ratingValue ?: "")) {
                                        // 清除导航堆栈中保存记录页面及以下的所有页面
                                        popUpTo(Routes.ReadingRoutes.SAVE_RECORD) { inclusive = true }
                                    }
                                } ?: navController.popBackStack()
                            }
                            else -> {
                                // 默认返回逻辑
                                navController.popBackStack()
                            }
                        }
                    }
                    
                    // 移除对全局变量的重置
                },
                onNavigateToAddBook = {
                    // 构建回调URL，包含当前页面的所有参数
                    val currentRoute = "${Routes.ReadingRoutes.SAVE_RECORD}?source=$sourceStr" +
                        (elapsedTime?.let { "&elapsedTime=$it" } ?: "") +
                        (startTime?.let { "&startTime=$it" } ?: "") +
                        (selectedBookId?.let { "&selectedBookId=$it" } ?: "") +
                        (from?.let { "&from=$it" } ?: "") +
                        (bookId?.let { "&bookId=$it" } ?: "") +
                        (groupId?.let { "&groupId=$it" } ?: "") +
                        (groupName?.let { "&groupName=$it" } ?: "") +
                        (sourceId?.let { "&sourceId=$it" } ?: "") +
                        (sourceName?.let { "&sourceName=$it" } ?: "") +
                        (tagId?.let { "&tagId=$it" } ?: "") +
                        (tagName?.let { "&tagName=$it" } ?: "") +
                        (status?.let { "&status=$it" } ?: "") +
                        (statusName?.let { "&statusName=$it" } ?: "") +
                        (rating?.let { "&rating=$it" } ?: "") +
                        (ratingValue?.let { "&ratingValue=$it" } ?: "")
                    val encodedCallback = Uri.encode(currentRoute)
                    navController.navigate("${Routes.BookRoutes.BOOK_EDIT}/0?callback=$encodedCallback")
                },
                elapsedTime = elapsedTime,
                startTime = startTime,
                selectedBookId = selectedBookId
            )
        }

        // 快捷导航相关页面
        composable(Routes.QuickActionsRoutes.QUICK_ACTIONS) { QuickActionsScreen() }
        composable(Routes.QuickActionsRoutes.QUICK_NOTE) { QuickNoteScreen() }
        composable(Routes.QuickActionsRoutes.QUICK_REVIEW) { QuickReviewScreen() }
        
        // 分组详情页面
        composable(
            route = Routes.BookRoutes.BOOK_GROUP_DETAIL,
            arguments = listOf(
                navArgument("group_id") { type = NavType.StringType },
                navArgument("groupName") { 
                    type = NavType.StringType
                    nullable = true
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("group_id")?.toLongOrNull() ?: 0L
            val groupName = backStackEntry.arguments?.getString("groupName") ?: ""
            
            BookShelfGroupDetailScreen(
                groupId = groupId,
                groupName = groupName,
                onBackClick = { navController.popBackStack() },
                onBookClick = { bookId ->
                    navController.navigate("${Routes.BookRoutes.BOOK_DETAIL}/$bookId")
                },
                onNavigateToBookEdit = { bookId ->
                    // 导航到书籍编辑页面
                    navController.navigate("${Routes.BookRoutes.BOOK_EDIT}/$bookId")
                },
                onNavigateToTimer = { bookId ->
                    // 导航到正向计时器页面，传递书籍ID和来源信息作为参数
                    navController.navigate("${Routes.TimeManagementRoutes.FORWARD_TIMER}?selectedBookId=$bookId&from=book_group_detail&groupId=$groupId&groupName=$groupName")
                },
                onNavigateToNoteEdit = { bookId ->
                    // 导航到笔记编辑页面，传递书籍ID作为参数
                    navController.navigate("${Routes.NoteRoutes.NOTE_EDIT}/0?book_id=$bookId")
                }
            )
        }
        
        // 来源详情页面
        composable(
            route = Routes.BookRoutes.BOOK_SOURCE_DETAIL,
            arguments = listOf(
                navArgument("source_id") { type = NavType.StringType },
                navArgument("sourceName") { 
                    type = NavType.StringType
                    nullable = true
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val sourceId = backStackEntry.arguments?.getString("source_id")?.toLongOrNull() ?: 0L
            val sourceName = backStackEntry.arguments?.getString("sourceName") ?: ""
            
            BookShelfSourceDetailScreen(
                sourceId = sourceId,
                sourceName = sourceName,
                onBackClick = { navController.popBackStack() },
                onBookClick = { bookId ->
                    navController.navigate("${Routes.BookRoutes.BOOK_DETAIL}/$bookId")
                },
                onNavigateToBookEdit = { bookId ->
                    // 导航到书籍编辑页面
                    navController.navigate("${Routes.BookRoutes.BOOK_EDIT}/$bookId")
                },
                onNavigateToTimer = { bookId ->
                    // 导航到正向计时器页面，传递书籍ID作为参数
                    navController.navigate("${Routes.TimeManagementRoutes.FORWARD_TIMER}?selectedBookId=$bookId&from=book_source_detail")
                },
                onNavigateToNoteEdit = { bookId ->
                    // 导航到笔记编辑页面，传递书籍ID作为参数
                    navController.navigate("${Routes.NoteRoutes.NOTE_EDIT}/0?book_id=$bookId")
                }
            )
        }
        
        // 标签详情页面
        composable(
            route = Routes.BookRoutes.BOOK_TAG_DETAIL,
            arguments = listOf(
                navArgument("tag_id") { type = NavType.StringType },
                navArgument("tagName") { 
                    type = NavType.StringType
                    nullable = true
                    defaultValue = ""
                },
                navArgument("tagColor") { 
                    type = NavType.StringType
                    nullable = true
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val tagId = backStackEntry.arguments?.getString("tag_id")?.toLongOrNull() ?: 0L
            val tagName = backStackEntry.arguments?.getString("tagName") ?: ""
            val tagColor = backStackEntry.arguments?.getString("tagColor") ?: ""
            
            BookShelfTagDetailScreen(
                tagId = tagId,
                tagName = tagName,
                tagColor = tagColor,
                onBackClick = { navController.popBackStack() },
                onBookClick = { bookId ->
                    navController.navigate("${Routes.BookRoutes.BOOK_DETAIL}/$bookId")
                },
                onNavigateToBookEdit = { bookId ->
                    // 导航到书籍编辑页面
                    navController.navigate("${Routes.BookRoutes.BOOK_EDIT}/$bookId")
                },
                onNavigateToTimer = { bookId ->
                    // 导航到正向计时器页面，传递书籍ID作为参数
                    navController.navigate("${Routes.TimeManagementRoutes.FORWARD_TIMER}?selectedBookId=$bookId&from=book_tag_detail")
                },
                onNavigateToNoteEdit = { bookId ->
                    // 导航到笔记编辑页面，传递书籍ID作为参数
                    navController.navigate("${Routes.NoteRoutes.NOTE_EDIT}/0?book_id=$bookId")
                }
            )
        }
        
        // 状态详情页面
        composable(
            route = Routes.BookRoutes.BOOK_STATUS_DETAIL,
            arguments = listOf(
                navArgument("status") { type = NavType.StringType },
                navArgument("statusName") { 
                    type = NavType.StringType
                    nullable = true
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val status = backStackEntry.arguments?.getString("status")?.toIntOrNull() ?: 0
            val statusName = backStackEntry.arguments?.getString("statusName") ?: ""
            
            BookShelfStatusDetailScreen(
                status = status,
                statusName = statusName,
                onBackClick = { navController.popBackStack() },
                onBookClick = { bookId ->
                    navController.navigate("${Routes.BookRoutes.BOOK_DETAIL}/$bookId")
                },
                onNavigateToBookEdit = { bookId ->
                    // 导航到书籍编辑页面
                    navController.navigate("${Routes.BookRoutes.BOOK_EDIT}/$bookId")
                },
                onNavigateToTimer = { bookId ->
                    // 导航到正向计时器页面，传递书籍ID作为参数
                    navController.navigate("${Routes.TimeManagementRoutes.FORWARD_TIMER}?selectedBookId=$bookId&from=book_status_detail")
                },
                onNavigateToNoteEdit = { bookId ->
                    // 导航到笔记编辑页面，传递书籍ID作为参数
                    navController.navigate("${Routes.NoteRoutes.NOTE_EDIT}/0?book_id=$bookId")
                }
            )
        }
        
        // 评分详情页面
        composable(
            route = Routes.BookRoutes.BOOK_RATING_DETAIL,
            arguments = listOf(
                navArgument("rating") { type = NavType.StringType },
                navArgument("ratingValue") { 
                    type = NavType.StringType
                    nullable = true
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val rating = backStackEntry.arguments?.getString("rating")?.toIntOrNull() ?: 0
            val ratingValue = backStackEntry.arguments?.getString("ratingValue") ?: ""
            
            BookShelfRatingDetailScreen(
                rating = rating,
                ratingValue = ratingValue,
                onBackClick = { navController.popBackStack() },
                onBookClick = { bookId ->
                    navController.navigate("${Routes.BookRoutes.BOOK_DETAIL}/$bookId")
                },
                onNavigateToBookEdit = { bookId ->
                    // 导航到书籍编辑页面
                    navController.navigate("${Routes.BookRoutes.BOOK_EDIT}/$bookId")
                },
                onNavigateToTimer = { bookId ->
                    // 导航到正向计时器页面，传递书籍ID作为参数
                    navController.navigate("${Routes.TimeManagementRoutes.FORWARD_TIMER}?selectedBookId=$bookId&from=book_rating_detail")
                },
                onNavigateToNoteEdit = { bookId ->
                    // 导航到笔记编辑页面，传递书籍ID作为参数
                    navController.navigate("${Routes.NoteRoutes.NOTE_EDIT}/0?book_id=$bookId")
                }
            )
        }
    }
}
