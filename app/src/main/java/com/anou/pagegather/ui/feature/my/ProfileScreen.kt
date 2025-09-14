package com.anou.pagegather.ui.feature.my

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.anou.pagegather.R
import com.anou.pagegather.ui.feature.my.components.SettingsItem
import com.anou.pagegather.ui.feature.my.components.SettingsSection
import com.anou.pagegather.ui.feature.my.components.UserProfileCard
import com.anou.pagegather.ui.theme.PageGatherTheme

/**
 * 我的页面 - 用户设置和管理功能入口
 */
@Composable
fun ProfileScreen(
    onNavigateToTagSettings: () -> Unit,
    onNavigateToGroupSettings: () -> Unit,
    onNavigateToBookSourceSettings: () -> Unit,
    onNavigateToReadingRecords: () -> Unit ,
    onNavigateToReadingSSRecords:() -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                                )
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {


                        Spacer(modifier = Modifier.height(16.dp))

                        // 用户信息
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 24.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = stringResource(R.string.app_slogan),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }

        item {
            // 用户信息卡片
            UserProfileCard()
        }

        item {
            // 数据管理分组
            SettingsSection(
                title = "数据管理",
                items = listOf(

                    SettingsItem(
                        icon = Icons.AutoMirrored.Filled.Label,
                        title = "标签管理",
                        subtitle = "管理书籍和笔记标签",
                        onClick = onNavigateToTagSettings
                    ),
                    SettingsItem(
                        icon = Icons.Default.Folder,
                        title = "书籍分组",
                        subtitle = "管理书籍分组",
                        onClick = onNavigateToGroupSettings
                    ),
                    SettingsItem(
                        icon = Icons.Default.Source,
                        title = "书籍来源",
                        subtitle = "管理书籍信息来源",
                        onClick = onNavigateToBookSourceSettings
                    )
                   
                )
            )
        }

        item {
            // 阅读管理分组
            SettingsSection(
                title = "阅读管理",
                items = listOf(
                    SettingsItem(
                        icon = Icons.Default.Timer,
                        title = "阅读计时",
                        subtitle = "阅读时间记录和计时器",
                        onClick = { /* TODO: 实现阅读计时导航 */ }
                    ),
                    SettingsItem(
                        icon = Icons.AutoMirrored.Filled.Assignment,
                        title = "阅读计划",
                        subtitle = "制定和管理阅读计划",
                        onClick = { /* TODO: 实现阅读计划导航 */ }
                    ),
                    SettingsItem(
                        icon = Icons.Default.BookmarkBorder,
                        title = "收藏夹",
                        subtitle = "管理收藏的书籍和笔记",
                        onClick = { /* TODO: 实现收藏夹导航 */ }
                    )
                )
            )
        }

        item {
            // 统计分析分组
            SettingsSection(
                title = "统计分析",
                items = listOf(
                    SettingsItem(
                        icon = Icons.Default.BarChart,
                        title = "阅读记录",
                        subtitle = "查看阅读记录",
                        onClick = onNavigateToReadingRecords
                    ),
                    SettingsItem(
                        icon = Icons.Default.StackedLineChart,
                        title = "阅读统计",
                        subtitle = "查看阅读统计",
                        onClick = onNavigateToReadingSSRecords
                    ),
                    SettingsItem(
                        icon = Icons.Default.EmojiEvents,
                        title = "成就勋章",
                        subtitle = "查看获得的阅读成就",
                        onClick = { /* TODO: 实现成就页面导航 */ }
                    )
                )
            )
        }

        item {
            // 系统设置分组
            SettingsSection(
                title = "系统设置",
                items = listOf(
                    SettingsItem(
                        icon = Icons.Default.Palette,
                        title = "主题设置",
                        subtitle = "深色/浅色主题切换",
                        onClick = { /* TODO: 实现主题设置 */ }
                    ),
                    SettingsItem(
                        icon = Icons.Default.Backup,
                        title = "数据备份",
                        subtitle = "备份和恢复应用数据",
                        onClick = { /* TODO: 实现数据备份 */ }
                    ),
                    SettingsItem(
                        icon = Icons.Default.Info,
                        title = "关于应用",
                        subtitle = "应用版本和开发信息",
                        onClick = { /* TODO: 实现关于页面 */ }
                    )
                )
            )
        }
    }
}