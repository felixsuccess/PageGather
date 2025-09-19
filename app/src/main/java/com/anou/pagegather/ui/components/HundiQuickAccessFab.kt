package com.anou.pagegather.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anou.pagegather.ui.theme.extendedColors

/**
 * Hundi 风格快速访问浮动按钮
 * 可以在任何界面快速跳转到 Hundi 风格展示
 */
@Composable
fun HundiQuickAccessFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    expanded: Boolean = false
) {
    if (expanded) {
        ExtendedFloatingActionButton(
            onClick = onClick,
            modifier = modifier,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(
                imageVector = Icons.Default.Brush,
                contentDescription = "Hundi 风格展示"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Hundi 风格",
                fontWeight = FontWeight.Medium
            )
        }
    } else {
        FloatingActionButton(
            onClick = onClick,
            modifier = modifier,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(
                imageVector = Icons.Default.Brush,
                contentDescription = "Hundi 风格展示"
            )
        }
    }
}

/**
 * 带有提示的 Hundi 快速访问按钮
 */
@Composable
fun HundiQuickAccessCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    HundiCard(
        onClick = onClick,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Brush,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            
            Column {
                Text(
                    text = "查看 Hundi 风格",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                Text(
                    text = "体验全新的温暖橙色设计",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.extendedColors.subtitleColor
                )
            }
        }
    }
}