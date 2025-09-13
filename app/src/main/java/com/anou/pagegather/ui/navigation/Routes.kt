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
        const val BOOK_READING_HISTORY = "$PREFIX/reading_history/{$BOOK_ID}"
        const val BOOK_EXCERPTS = "$PREFIX/excerpts/{$BOOK_ID}"
        const val BOOK_REVIEWS = "$PREFIX/reviews/{$BOOK_ID}"
        const val BOOK_RELATED_DATA = "$PREFIX/related_data/{$BOOK_ID}"
        const val BOOK_GROUP_DETAIL = "$PREFIX/group/{group_id}?groupName={groupName}"  // 更新分组详情路由，添加groupName参数
        const val BOOK_SOURCE_DETAIL = "$PREFIX/source/{source_id}?sourceName={sourceName}"  // 来源详情路由
        const val BOOK_TAG_DETAIL = "$PREFIX/tag/{tag_id}?tagName={tagName}"  // 标签详情路由
        const val BOOK_STATUS_DETAIL = "$PREFIX/status/{status}?statusName={statusName}"  // 状态详情路由
        const val BOOK_RATING_DETAIL = "$PREFIX/rating/{rating}?ratingValue={ratingValue}"  // 评分详情路由

        fun bookEdit(bookId: Long): String = "$PREFIX/edit/$bookId"
        fun bookDetail(bookId: Long): String = "$PREFIX/detail/$bookId"
        fun bookReadingHistory(bookId: Long): String = "$PREFIX/reading_history/$bookId"
        fun bookExcerpts(bookId: Long): String = "$PREFIX/excerpts/$bookId"
        fun bookReviews(bookId: Long): String = "$PREFIX/reviews/$bookId"
        fun bookRelatedData(bookId: Long): String = "$PREFIX/related_data/$bookId"
        fun bookGroupDetail(groupId: Long, groupName: String = ""): String = "$PREFIX/group/$groupId?groupName=$groupName"  // 更新分组详情路由函数，添加groupName参数
        fun bookSourceDetail(sourceId: Long, sourceName: String = ""): String = "$PREFIX/source/$sourceId?sourceName=$sourceName"  // 来源详情路由函数
        fun bookTagDetail(tagId: Long, tagName: String = ""): String = "$PREFIX/tag/$tagId?tagName=$tagName"  // 标签详情路由函数
        fun bookStatusDetail(status: Int, statusName: String = ""): String = "$PREFIX/status/$status?statusName=$statusName"  // 状态详情路由函数
        fun bookRatingDetail(rating: Int, ratingValue: String = ""): String = "$PREFIX/rating/$rating?ratingValue=$ratingValue"  // 评分详情路由函数
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
        const val DATA_MANAGEMENT = "data_management"
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
        const val READING_RECORDS = "reading_records"
        const val BOOK_READING_STATISTICS = "book_reading_statistics"  // 添加书籍阅读统计路由
        const val LONGEST_READING_BOOKS = "longest_reading_books"  // 添加阅读最久书籍路由
        const val PREFERRED_BOOK_TYPES = "preferred_book_types"  // 添加偏好阅读类型路由
        const val READING_HABIT_DISTRIBUTION = "reading_habit_distribution"  // 添加阅读习惯时间分布路由
        const val BOOK_TYPE_DISTRIBUTION = "book_type_distribution"  // 添加书籍类型分布路由
        const val PREFERRED_AUTHORS_PUBLISHERS = "preferred_authors_publishers"  // 添加偏好作者和版权方路由
    }

    // 快捷操作相关路由
    object QuickActionsRoutes {
        const val QUICK_ACTIONS = "quick_actions"
        const val QUICK_NOTE = "quick_note"
        const val QUICK_REVIEW = "quick_review"
        const val QUICK_BOOKMARK = "quick_bookmark"
    }

}