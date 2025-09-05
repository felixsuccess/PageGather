package com.anou.pagegather.data.local.entity

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class ReadStatus(val code: Int, val message: String) {
    WANT_TO_READ(0, "想读"),
    READING(1, "在读"),
    FINISHED(2, "读完"),
    ABANDONED(3, "弃读");

    @Composable
    fun getColor(): Color {
        return when (this) {
            WANT_TO_READ -> MaterialTheme.colorScheme.primary
            READING -> MaterialTheme.colorScheme.secondary
            FINISHED -> MaterialTheme.colorScheme.tertiary
            ABANDONED -> MaterialTheme.colorScheme.error
        }
    }
}