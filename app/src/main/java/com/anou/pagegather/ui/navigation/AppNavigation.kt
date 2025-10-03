package com.anou.pagegather.ui.navigation

import android.net.Uri
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.anou.pagegather.ui.feature.bookshelf.BookDetailScreen
import com.anou.pagegather.ui.feature.bookshelf.BookEditScreen
import com.anou.pagegather.ui.feature.bookshelf.BookExcerptsScreen
import com.anou.pagegather.ui.feature.bookshelf.BookListViewModel
import com.anou.pagegather.ui.feature.bookshelf.BookReadingHistoryScreen
import com.anou.pagegather.ui.feature.bookshelf.BookRelatedDataScreen
import com.anou.pagegather.ui.feature.bookshelf.BookReviewsScreen
import com.anou.pagegather.ui.feature.bookshelf.BookShelfScreen
import com.anou.pagegather.ui.feature.bookshelf.booksource.BookShelfSourceDetailScreen
import com.anou.pagegather.ui.feature.bookshelf.group.BookShelfGroupDetailScreen
import com.anou.pagegather.ui.feature.bookshelf.rating.BookShelfRatingDetailScreen
import com.anou.pagegather.ui.feature.bookshelf.status.BookShelfStatusDetailScreen
import com.anou.pagegather.ui.feature.bookshelf.tag.BookShelfTagDetailScreen
import com.anou.pagegather.ui.feature.management.BookGroupManagementScreen
import com.anou.pagegather.ui.feature.management.BookSourceManagementScreen
import com.anou.pagegather.ui.feature.management.TagManagementScreen
import com.anou.pagegather.ui.feature.my.ProfileScreen
import com.anou.pagegather.ui.feature.my.settings.CustomThemeCreationScreen
import com.anou.pagegather.ui.feature.my.settings.ThemeSelectionScreen
import com.anou.pagegather.ui.feature.my.settings.debug.ThemeDebugScreen
import com.anou.pagegather.ui.feature.notes.NoteEditScreen
import com.anou.pagegather.ui.feature.notes.NoteViewScreen
import com.anou.pagegather.ui.feature.notes.NotesScreen
import com.anou.pagegather.ui.feature.quickactions.QuickActionsScreen
import com.anou.pagegather.ui.feature.quickactions.QuickNoteScreen
import com.anou.pagegather.ui.feature.quickactions.QuickReviewScreen
import com.anou.pagegather.ui.feature.reading.BookReadingStatisticsScreen
import com.anou.pagegather.ui.feature.reading.ManualRecordScreen
import com.anou.pagegather.ui.feature.reading.ReadingRecordsScreen
import com.anou.pagegather.ui.feature.reading.SaveRecordScreen
import com.anou.pagegather.ui.feature.statistics.StatisticsScreen
import com.anou.pagegather.ui.feature.timer.ReadingTimerScreen
import com.anou.pagegather.ui.feature.timer.GoalSettingScreen
import com.anou.pagegather.ui.feature.timer.PeriodicReminderScreen
import com.anou.pagegather.ui.feature.timer.ReadingPlanScreen

import com.anou.pagegather.ui.feature.timer.navigateToTimerFromBookDetail
import com.anou.pagegather.ui.feature.timer.navigateToTimerFromBookshelf
import com.anou.pagegather.ui.feature.timer.navigateToTimerFromGroupDetail

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
                onNavigateToBookSourceSettings = { navController.navigate(Routes.ProfileRoutes.BOOK_SOURCE_SETTINGS) },
                onNavigateToReadingRecords = { navController.navigate(Routes.ReadingRoutes.READING_RECORDS) },
                onNavigateToReadingSSRecords = { navController.navigate(Routes.ReadingRoutes.BOOK_READING_STATISTICS) },
                onNavigateToThemeSettings = { navController.navigate(Routes.ProfileRoutes.THEME_SETTINGS) },
                onNavigateToThemeDebug = { navController.navigate(Routes.ProfileRoutes.THEME_DEBUG) },
                onNavigateToHundiStyleDemo = { navController.navigate(Routes.ProfileRoutes.HUNDI_STYLE_DEMO) },
                onNavigateToChartShowcase = { navController.navigate(Routes.ProfileRoutes.CHART_SHOWCASE) }
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
                    navController.navigateToTimerFromBookshelf(bookId)
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
                    navController.navigateToTimerFromBookDetail(bookId)
                }
            )
        }


        // 统计页面
        composable(Routes.DashboardRoutes.STATISTICS) {
            StatisticsScreen(
                navController = navController
            )
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

        // 主题设置页面
        composable(Routes.ProfileRoutes.THEME_SETTINGS) {
            ThemeSelectionScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCustomThemeCreation = { navController.navigate(Routes.ProfileRoutes.CUSTOM_THEME_CREATION) }
            )
        }

        // 自定义主题创建页面
        composable(Routes.ProfileRoutes.CUSTOM_THEME_CREATION) {
            CustomThemeCreationScreen(
                onNavigateBack = { navController.popBackStack() },
                onThemeCreated = { customTheme ->
                    // 主题创建成功后返回主题设置页面
                    navController.popBackStack()
                }
            )
        }

        // 主题调试页面
        composable(Routes.ProfileRoutes.THEME_DEBUG) {
            ThemeDebugScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Hundi 风格展示页面
        composable(Routes.ProfileRoutes.HUNDI_STYLE_DEMO) {
            com.anou.pagegather.ui.feature.demo.HundiStyleShowcaseScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 图表展示页面
        composable(Routes.ProfileRoutes.CHART_SHOWCASE) {
            com.anou.pagegather.ui.feature.my.settings.debug.ChartShowcaseScreen(
                navController = navController
            )
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

        // 阅读记录列表页面
        composable(Routes.ReadingRoutes.READING_RECORDS) {
            ReadingRecordsScreen(
                onBackClick = { navController.popBackStack() },
                navController = navController  // 传递NavController
            )
        }

        // 书籍阅读统计页面
        composable(Routes.ReadingRoutes.BOOK_READING_STATISTICS) {
            BookReadingStatisticsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }


        // 统一计时器页面
        composable(
            route = "${Routes.TimeManagementRoutes.READING_TIMER}?entryContext={entryContext}",
            arguments = listOf(
                navArgument("entryContext") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val entryContextString = backStackEntry.arguments?.getString("entryContext")
            
            // 解析入口上下文
            val entryContext = if (!entryContextString.isNullOrEmpty()) {
                try {
                    com.anou.pagegather.ui.feature.timer.TimerEntryContext.decode(entryContextString)
                } catch (e: Exception) {
                    com.anou.pagegather.ui.feature.timer.TimerEntryContext(
                        entrySource = com.anou.pagegather.ui.feature.timer.TimerEntrySource.DIRECT,
                        userIntent = com.anou.pagegather.ui.feature.timer.TimerUserIntent.GENERAL_READING
                    )
                }
            } else {
                com.anou.pagegather.ui.feature.timer.TimerEntryContext(
                    entrySource = com.anou.pagegather.ui.feature.timer.TimerEntrySource.DIRECT,
                    userIntent = com.anou.pagegather.ui.feature.timer.TimerUserIntent.GENERAL_READING
                )
            }

            ReadingTimerScreen(
                entryContext = entryContext,
                onNavigateBack = { 
                    navController.popBackStack()
                },
                onTimerComplete = { result ->
                    if (result.success && result.duration > 0) {
                        navController.navigate("${Routes.ReadingRoutes.SAVE_RECORD}?duration=${result.duration}&bookId=${result.bookId ?: ""}")
                    } else {
                        navController.popBackStack()
                    }
                }
            )
        }

        composable(Routes.TimeManagementRoutes.GOAL_SETTING) { 
            GoalSettingScreen() 
        }
        composable(Routes.TimeManagementRoutes.READING_PLAN) { 
            ReadingPlanScreen() 
        }
        composable(Routes.TimeManagementRoutes.PERIODIC_REMINDER) { 
            PeriodicReminderScreen() 
        }

        // 阅读记录保存 - 恢复完整逻辑
        composable(
            route = "${Routes.ReadingRoutes.SAVE_RECORD}?duration={duration}&bookId={bookId}",
            arguments = listOf(
                navArgument("duration") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = "0"
                },
                navArgument("bookId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val duration = backStackEntry.arguments?.getString("duration")?.toLongOrNull() ?: 0L
            val bookId = backStackEntry.arguments?.getString("bookId")?.toLongOrNull()
            
            // 保存记录页面
            SaveRecordScreen(
                duration = duration,
                bookId = bookId,
                onNavigateBack = { navController.popBackStack() },
                onSaveComplete = { 
                    // 保存完成后返回到合适的页面
                    navController.popBackStack()
                },
                onNavigateToBookEdit = {
                    // 导航到添加新书籍页面
                    navController.navigate("${Routes.BookRoutes.BOOK_EDIT}/0")
                },
                onReturnToTimer = {
                    // 返回继续计时（不保存当前记录）
                    navController.popBackStack()
                },
                onStartNewTimer = { selectedBookId ->
                    // 保存并开始新计时：导航回计时器页面
                    navController.navigateToTimerFromBookshelf(selectedBookId)
                }
            )
        }

        // 添加手动记录页面
        composable(
            route = "${Routes.ReadingRoutes.MANUAL_RECORD}?selectedBookId={selectedBookId}",
            arguments = listOf(
                navArgument("selectedBookId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val selectedBookId =
                backStackEntry.arguments?.getString("selectedBookId")?.toLongOrNull()

            ManualRecordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToAddBook = {
                    // 构建回调URL，包含当前页面的所有参数
                    val currentRoute = "${Routes.ReadingRoutes.MANUAL_RECORD}" +
                            (selectedBookId?.let { "?selectedBookId=$it" } ?: "")
                    val encodedCallback = Uri.encode(currentRoute)
                    navController.navigate("${Routes.BookRoutes.BOOK_EDIT}/0?callback=$encodedCallback")
                },
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
            // 获取ViewModel实例，确保所有页面共享同一个实例
            val viewModel: BookListViewModel = hiltViewModel()

            BookShelfGroupDetailScreen(
                groupId = groupId,
                groupName = groupName,
                // 移除 isGridMode 参数，让组件直接从 ViewModel 获取
                onBackClick = { navController.popBackStack() },
                onBookClick = { bookId ->
                    navController.navigate("${Routes.BookRoutes.BOOK_DETAIL}/$bookId")
                },
                viewModel = viewModel, // 传递ViewModel实例
                onNavigateToBookEdit = { bookId ->
                    // 导航到书籍编辑页面
                    navController.navigate("${Routes.BookRoutes.BOOK_EDIT}/$bookId")
                },
                onNavigateToTimer = { bookId ->
                    navController.navigateToTimerFromGroupDetail(
                        bookId = bookId,
                        groupId = groupId,
                        groupName = groupName
                    )
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
            // 获取ViewModel实例，确保所有页面共享同一个实例
            val viewModel: BookListViewModel = hiltViewModel()

            BookShelfSourceDetailScreen(
                sourceId = sourceId,
                sourceName = sourceName,
                // 移除 isGridMode 参数，让组件直接从 ViewModel 获取
                onBackClick = { navController.popBackStack() },
                onBookClick = { bookId ->
                    navController.navigate("${Routes.BookRoutes.BOOK_DETAIL}/$bookId")
                },
                viewModel = viewModel, // 传递ViewModel实例
                onNavigateToBookEdit = { bookId ->
                    // 导航到书籍编辑页面
                    navController.navigate("${Routes.BookRoutes.BOOK_EDIT}/$bookId")
                },
                onNavigateToTimer = { bookId ->
                    navController.navigateToTimerFromBookshelf(bookId)
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
            // 获取ViewModel实例，确保所有页面共享同一个实例
            val viewModel: BookListViewModel = hiltViewModel()

            BookShelfTagDetailScreen(
                tagId = tagId,
                tagName = tagName,
                tagColor = tagColor,
                // 移除 isGridMode 参数，让组件直接从 ViewModel 获取
                onBackClick = { navController.popBackStack() },
                onBookClick = { bookId ->
                    navController.navigate("${Routes.BookRoutes.BOOK_DETAIL}/$bookId")
                },
                viewModel = viewModel, // 传递ViewModel实例
                onNavigateToBookEdit = { bookId ->
                    // 导航到书籍编辑页面
                    navController.navigate("${Routes.BookRoutes.BOOK_EDIT}/$bookId")
                },
                onNavigateToTimer = { bookId ->
                    navController.navigateToTimerFromBookshelf(bookId)
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
            // 获取ViewModel实例，确保所有页面共享同一个实例
            val viewModel: BookListViewModel = hiltViewModel()

            BookShelfStatusDetailScreen(
                status = status,
                statusName = statusName,
                // 移除 isGridMode 参数，让组件直接从 ViewModel 获取
                onBackClick = { navController.popBackStack() },
                onBookClick = { bookId ->
                    navController.navigate("${Routes.BookRoutes.BOOK_DETAIL}/$bookId")
                },
                viewModel = viewModel, // 传递ViewModel实例
                onNavigateToBookEdit = { bookId ->
                    // 导航到书籍编辑页面
                    navController.navigate("${Routes.BookRoutes.BOOK_EDIT}/$bookId")
                },
                onNavigateToTimer = { bookId ->
                    navController.navigateToTimerFromBookshelf(bookId)
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
            // 获取ViewModel实例，确保所有页面共享同一个实例
            val viewModel: BookListViewModel = hiltViewModel()

            BookShelfRatingDetailScreen(
                rating = rating,
                ratingValue = ratingValue,
                // 移除 isGridMode 参数，让组件直接从 ViewModel 获取
                onBackClick = { navController.popBackStack() },
                onBookClick = { bookId ->
                    navController.navigate("${Routes.BookRoutes.BOOK_DETAIL}/$bookId")
                },
                viewModel = viewModel, // 传递ViewModel实例
                onNavigateToBookEdit = { bookId ->
                    // 导航到书籍编辑页面
                    navController.navigate("${Routes.BookRoutes.BOOK_EDIT}/$bookId")
                },
                onNavigateToTimer = { bookId ->
                    navController.navigateToTimerFromBookshelf(bookId)
                },
                onNavigateToNoteEdit = { bookId ->
                    // 导航到笔记编辑页面，传递书籍ID作为参数
                    navController.navigate("${Routes.NoteRoutes.NOTE_EDIT}/0?book_id=$bookId")
                }
            )
        }

        // 添加书籍详情页的Tab页面路由
        composable(
            route = Routes.BookRoutes.BOOK_READING_HISTORY,
            arguments = listOf(navArgument(Routes.BookRoutes.BOOK_ID) { type = NavType.LongType })
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getLong(Routes.BookRoutes.BOOK_ID) ?: 0L
            BookReadingHistoryScreen(
                navController = navController,
                bookId = bookId,
                onNavigateToNoteEdit = { noteId ->
                    navController.navigate("${Routes.NoteRoutes.NOTE_EDIT}/$noteId")
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Routes.BookRoutes.BOOK_EXCERPTS,
            arguments = listOf(navArgument(Routes.BookRoutes.BOOK_ID) { type = NavType.LongType })
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getLong(Routes.BookRoutes.BOOK_ID) ?: 0L
            BookExcerptsScreen(
                navController = navController,
                bookId = bookId,
                onNavigateToNoteEdit = { noteId ->
                    navController.navigate("${Routes.NoteRoutes.NOTE_EDIT}/$noteId")
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Routes.BookRoutes.BOOK_REVIEWS,
            arguments = listOf(navArgument(Routes.BookRoutes.BOOK_ID) { type = NavType.LongType })
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getLong(Routes.BookRoutes.BOOK_ID) ?: 0L
            BookReviewsScreen(
                navController = navController,
                bookId = bookId,
                onNavigateToNoteEdit = { noteId ->
                    navController.navigate("${Routes.NoteRoutes.NOTE_EDIT}/$noteId")
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Routes.BookRoutes.BOOK_RELATED_DATA,
            arguments = listOf(navArgument(Routes.BookRoutes.BOOK_ID) { type = NavType.LongType })
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getLong(Routes.BookRoutes.BOOK_ID) ?: 0L
            BookRelatedDataScreen(
                navController = navController,
                bookId = bookId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

    
        
        // 目标设置
        composable(Routes.TimeManagementRoutes.GOAL_SETTING) {
            GoalSettingScreen()
        }
        
        // 阅读计划
        composable(Routes.TimeManagementRoutes.READING_PLAN) {
            ReadingPlanScreen()
        }
        
        // 定期提醒
        composable(Routes.TimeManagementRoutes.PERIODIC_REMINDER) {
            PeriodicReminderScreen()
        }


    }

}