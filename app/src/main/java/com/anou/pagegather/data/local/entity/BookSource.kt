package com.anou.pagegather.data.local.entity

/**
 * 书籍来源枚举
 * 
 * @deprecated 此枚举已弃用，请使用 BookSourceEntity 进行来源管理。
 * BookSourceEntity 提供了更灵活的内置+自定义来源管理功能，支持动态添加、编辑、排序等操作。
 * 
 * 迁移指南：
 * - 使用 BookSourceRepository.getAllEnabledSources() 获取可用来源
 * - 使用 BookSourceRepository.addCustomSource() 添加自定义来源
 * - 使用 BookSourceEntity.id 作为书籍的来源引用
 */
@Deprecated(
    message = "使用 BookSourceEntity 替代，支持动态管理内置和自定义来源",
    replaceWith = ReplaceWith("BookSourceEntity"),
    level = DeprecationLevel.WARNING
)
enum class BookSource(val code: Int, val message: String) {
    UNKNOWN(0, "未知"),
    APPLE_BOOKS(1, "Apple Books"),
    WEREAD(2, "微信读书"),
    NETEASE_SNAIL(3, "网易蜗牛"),
    DOUBAN_READ_APP(4, "豆瓣阅读(App)"),
    DOUBAN_READ(5, "豆瓣阅读"),
    READ(6, "阅读"),
    DIMO_BOOK(7, "滴墨书摘"),
    LIFE_WEEK(8, "三联生活周刊"),
    JD_READ(9, "京东读书"),
    KINDLE_READER(10, "Kindle阅读器"),
    KINDLE_APP(11, "Kindle App"),
    TOMATO_NOVEL(12, "番茄小说"),
    MOON_READER(13, "静读天下"),
    DUO_KAN(14, "多看阅读"),
    ZHANG_YUE(15, "掌阅"),
    ZHANG_YUE_SELECT(16, "掌阅精选"),
    BOOX_READER(17, "文石阅读器"),
    DANG_DANG_CLOUD(18, "当当云阅读"),
    KO_READER(19, "KOReader");

}





 