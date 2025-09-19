package com.anou.pagegather.ui.components 


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import com.anou.pagegather.ui.theme.extendedColors 

@Composable
fun RateBar(
    rate: Int,
    onRateChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
    stars: Int = 5,
    starSize: Dp = 24.dp,
    activeColor: Color = Color.Yellow,
    inactiveColor: Color = Color.Gray,
) {
    Row(modifier = modifier) {
        for (i in 1..stars) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                modifier = Modifier
                    .size(starSize)
                    .clickable { onRateChanged(i) },
                tint = if (i <= rate) activeColor else inactiveColor
            )
        }
    }
}


@Composable
fun RateBar(
    rate: Float,
    onRateChanged: (Float) -> Unit,
    modifier: Modifier = Modifier,
    stars: Int = 5,
    starSize: Dp = 24.dp,
    activeColor: Color = MaterialTheme.extendedColors.warning,
    inactiveColor: Color = MaterialTheme.extendedColors.neutral300,
) {
    Row(modifier = modifier) {
        for (i in 1..stars) {
            val starValue = i.toFloat()
            Icon(
                imageVector = if (rate >= starValue - 0.5f && rate < starValue)
                    Icons.AutoMirrored.Filled.StarHalf
                else if (rate >= starValue)
                    Icons.Default.Star
                else
                    Icons.Default.StarOutline,
                contentDescription = null,
                modifier = Modifier
                    .size(starSize)
                    .clickable { onRateChanged(starValue) },
                tint = if (rate >= starValue - 0.5f) activeColor else inactiveColor

            )
        }
    }
}

