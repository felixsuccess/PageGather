package com.anou.pagegather.data.local.entity

/**
 * 书籍状态枚举
 * 用于标识书籍的物理状态
 */
enum class BookCondition(val code: Int, val message: String) {
    /** 全新 */
    NEW(0, "全新"),
    /** 良好 */
    GOOD(1, "良好"),
    /** 一般 */
    FAIR(2, "一般"),
    /** 破损 */
    POOR(3, "破损");

    companion object {
        fun fromCode(code: Int): BookCondition {
            return BookCondition.entries.find { it.code == code } ?: NEW
        }
    }
}