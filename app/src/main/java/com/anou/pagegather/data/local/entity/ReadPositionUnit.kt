package com.anou.pagegather.data.local.entity

enum class ReadPositionUnit(val code: Int, val message: String) {
    PAGE(0, "页码"),
    PERCENT(1, "百分比"),
    CHAPTER(2, "章节"),
}