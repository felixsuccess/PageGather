package com.anou.pagegather.ui.navigation

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
import com.anou.pagegather.ui.feature.management.BookGroupManagementScreen
import com.anou.pagegather.ui.feature.management.BookSourceManagementScreen
import com.anou.pagegather.ui.feature.management.TagManagementScreen
import com.anou.pagegather.ui.feature.my.ProfileScreen
import com.anou.pagegather.ui.feature.notes.NoteEditScreen
import com.anou.pagegather.ui.feature.notes.NoteViewScreen
import com.anou.pagegather.ui.feature.notes.NotesScreen
import com.anou.pagegather.ui.feature.timer.*
import com.anou.pagegather.ui.feature.quickactions.*

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
                onNavigateToBookGroups = { navController.navigate(Routes.ProfileRoutes.BOOK_GROUP_SETTINGS) },
                onNavigateToTimer = { navController.navigate(Routes.TimeManagementRoutes.FORWARD_TIMER) },
                onNavigateToQuickActions = { navController.navigate(Routes.QuickActionsRoutes.QUICK_ACTIONS) },
            )

        }

        composable(
            route = "${Routes.BookRoutes.BOOK_EDIT}/{${Routes.BookRoutes.BOOK_ID}}",
            arguments = listOf(navArgument(Routes.BookRoutes.BOOK_ID) { type = NavType.StringType })
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString(Routes.BookRoutes.BOOK_ID)
            BookEditScreen(bookId = bookId, navController = navController)
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
            route = "${Routes.NoteRoutes.NOTE_EDIT}/{${Routes.NoteRoutes.ARG_NOTE_ID}}",
            arguments = listOf(navArgument(Routes.NoteRoutes.ARG_NOTE_ID) {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString(Routes.NoteRoutes.ARG_NOTE_ID)
            NoteEditScreen(noteId = noteId, navController = navController)
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
        composable(Routes.TimeManagementRoutes.FORWARD_TIMER) { 
            ForwardTimerScreen(
                onNavigateBack = { navController.popBackStack() }
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

        // 快捷导航相关页面
        composable(Routes.QuickActionsRoutes.QUICK_ACTIONS) { QuickActionsScreen() }
        composable(Routes.QuickActionsRoutes.QUICK_NOTE) { QuickNoteScreen() }
        composable(Routes.QuickActionsRoutes.QUICK_REVIEW) { QuickReviewScreen() }
        composable(Routes.QuickActionsRoutes.QUICK_BOOKMARK) { QuickBookmarkScreen() }

    }
}