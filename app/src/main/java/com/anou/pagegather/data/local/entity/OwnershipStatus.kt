package com.anou.pagegather.data.local.entity

/**
 * 拥有状态枚举
 * 用于标识书籍的拥有情况
 */
enum class OwnershipStatus(val code: Int, val message: String) {
    /** 未设置 */
    UNKNOWN(0, "未设置"),
    /** 已拥有 */
    OWNED(1, "已拥有"),
    /** 想要购买 */
    WANT_TO_BUY(2, "想要购买"),
    /** 已借出 */
    LENT_OUT(3, "已借出"),
    /** 已借入 */
    BORROWED(4, "已借入");

    companion object {
        fun fromCode(code: Int): OwnershipStatus {
            return OwnershipStatus.entries.find { it.code == code } ?: UNKNOWN
        }
    }
}