package com.anou.pagegather.data.local.entity

enum class BookType(val code: Int, val message: String) {
    PAPER_BOOK(0,"纸质书"),
    E_BOOK(1,"电子书");

    companion object {
        fun fromValue(value: Int): BookType {
            return BookType.entries.firstOrNull { it.code == value } ?: PAPER_BOOK
        }
    }
}
