package com.anou.pagegather.ui.navigation


object Routes {
    // 主路由
    object MainRoutes {
        const val SPLASH = "splash"

        // 主页面路由
        const val MAIN = "main"
    }

    // 书架页面
    object BookRoutes {
        private const val PREFIX = "books"
        const val BOOK_ID = "book_id"
        const val BOOKS = PREFIX

        const val BOOK_LIST = "$PREFIX/list"
        const val BOOK_EDIT = "$PREFIX/edit/{$BOOK_ID}"
        const val BOOK_DETAIL = "$PREFIX/detail/{$BOOK_ID}"

        fun bookEdit(bookId: Long): String = "$PREFIX/edit/$bookId"
        fun bookDetail(bookId: Long): String = "$PREFIX/detail/$bookId"
    }

    // 随记页面
    object NoteRoutes {
        private const val PREFIX = "notes"
        const val ARG_NOTE_ID = "note_id"
        const val NOTE_LIST = "${PREFIX}/list"
        const val NOTE_EDIT = "${PREFIX}/edit/{$ARG_NOTE_ID}"
        const val NOTE_VIEW = "${PREFIX}/view/{$ARG_NOTE_ID}"
        // const val NOTE_ROAMING = "note_roaming"
        fun noteEdit(noteId: Long): String = "$PREFIX/edit/$noteId"
        fun noteDetail(noteId: Long): String = "$PREFIX/detail/$noteId"
    }


    // 统计页面
    object DashboardRoutes {
        const val STATISTICS = "statistics"
        const val TIMELINE = "timeline"
        const val CALENDAR = "calendar"
        const val CHARTS = "charts"
    }


    // 我的页面
    object ProfileRoutes {
        const val PROFILE = "profile"
        const val TAG_SETTINGS = "tag_settings"
        const val BOOK_GROUP_SETTINGS = "book_group_settings"
        const val BOOK_SOURCE_SETTINGS = "book_source_settings"
    }

    // 时间管理相关路由
    object TimeManagementRoutes {
        const val FORWARD_TIMER = "forward_timer"
        const val REVERSE_TIMER = "reverse_timer"
        const val GOAL_SETTING = "goal_setting"
        const val READING_PLAN = "reading_plan"
        const val PERIODIC_REMINDER = "periodic_reminder"
    }

    // 阅读记录相关路由
    object ReadingRoutes {
        const val SAVE_RECORD = "reading/save"
        const val RECORD_HISTORY = "reading/history"
        const val EDIT_RECORD = "reading/edit/{recordId}"
        const val RECORD_ANALYTICS = "reading/analytics"
        const val RECORD_TIMELINE = "reading/timeline"
    }

    // 快捷操作相关路由
    object QuickActionsRoutes {
        const val QUICK_ACTIONS = "quick_actions"
        const val QUICK_NOTE = "quick_note"
        const val QUICK_REVIEW = "quick_review"
        const val QUICK_BOOKMARK = "quick_bookmark"
    }

}