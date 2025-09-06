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
                onNavigateToTimer = { navController.navigate(Routes.TimeManagementRoutes.FORWARD_TIMER) },
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
            route = "${Routes.TimeManagementRoutes.FORWARD_TIMER}?newlyAddedBookId={newlyAddedBookId}",
            arguments = listOf(
                navArgument("newlyAddedBookId") { 
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val newlyAddedBookId = backStackEntry.arguments?.getString("newlyAddedBookId")?.toLongOrNull()
            
            ForwardTimerScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSaveRecord = { elapsedTime, startTime ->
                    navController.navigate("${Routes.ReadingRoutes.SAVE_RECORD}?source=TIMER&elapsedTime=$elapsedTime&startTime=$startTime")
                },
                newlyAddedBookId = newlyAddedBookId
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
            route = "${Routes.ReadingRoutes.SAVE_RECORD}?source={source}&elapsedTime={elapsedTime}&startTime={startTime}&preSelectedBookId={preSelectedBookId}&newlyAddedBookId={newlyAddedBookId}",
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
                navArgument("preSelectedBookId") { 
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("newlyAddedBookId") { 
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
            val preSelectedBookId = backStackEntry.arguments?.getString("preSelectedBookId")?.toLongOrNull()
            val newlyAddedBookId = backStackEntry.arguments?.getString("newlyAddedBookId")?.toLongOrNull()
            
            SaveRecordScreen(
                source = source,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddBook = {
                    // 构建回调URL，包含当前页面的所有参数
                    val currentRoute = "${Routes.ReadingRoutes.SAVE_RECORD}?source=$sourceStr" +
                        (elapsedTime?.let { "&elapsedTime=$it" } ?: "") +
                        (startTime?.let { "&startTime=$it" } ?: "") +
                        (preSelectedBookId?.let { "&preSelectedBookId=$it" } ?: "")
                    val encodedCallback = Uri.encode(currentRoute)
                    navController.navigate("${Routes.BookRoutes.BOOK_EDIT}/0?callback=$encodedCallback")
                },
                elapsedTime = elapsedTime,
                startTime = startTime,
                preSelectedBookId = preSelectedBookId,
                newlyAddedBookId = newlyAddedBookId
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
                }
            )
        }
    }
}