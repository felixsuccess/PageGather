package com.anou.pagegather.data.local.entity

/**
 * 标签类型枚举
 * 用于区分书籍标签和笔记标签
 */
enum class TagType(val code: Int, val message: String) {
    /** 书籍标签 */
    BOOK(0, "书籍标签"),
    /** 笔记标签 */
    NOTE(1, "笔记标签");

    companion object {
        fun fromCode(code: Int): TagType {
            return TagType.entries.find { it.code == code } ?: BOOK
        }
    }
}