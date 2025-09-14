package com.anou.pagegather.ui.components.charts

import androidx.compose.ui.graphics.Color

data class ChartData(
    val value: Float,
    val label: String,
    val color: Color? = null
)