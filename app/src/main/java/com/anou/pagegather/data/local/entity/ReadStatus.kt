package com.anou.pagegather.data.local.entity

enum class ReadStatus(val code: Int, val message: String) {
    WANT_TO_READ(0, "想读"),
    READING(1, "在读"),
    FINISHED(2, "读完"),
    ABANDONED(3, "弃读")
}


