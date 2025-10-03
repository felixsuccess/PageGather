package com.anou.pagegather.ui.feature.timer

import android.os.Parcelable
import androidx.navigation.NavController
import com.anou.pagegather.ui.navigation.Routes
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 计时器入口管理器
 * 
 * 职责：
 * 1. 统一管理所有计时器入口逻辑
 * 2. 简化导航参数传递
 * 3. 提供一致的用户体验
 * 4. 支持智能返回逻辑
 */
@Singleton
class TimerEntryManager @Inject constructor() {
    
    /**
     * 统一的计时器启动方法
     */
    fun startTimerFromEntry(
        context: TimerEntryContext,
        navController: NavController
    ) {
        val route = buildTimerRoute(context)
        navController.navigate(route)
    }
    
    /**
     * 构建计时器路由
     */
    private fun buildTimerRoute(context: TimerEntryContext): String {
        return "${Routes.TimeManagementRoutes.FORWARD_TIMER}" +
               "?entryContext=${context.encode()}"
    }
    
    /**
     * 解析入口上下文
     */
    fun parseEntryContext(encoded: String?): TimerEntryContext? {
        return try {
            encoded?.let { TimerEntryContext.decode(it) }
        } catch (e: Exception) {
            null
        }
    }
}

/**
 * 计时器入口上下文
 */
@Parcelize
@Serializable
data class TimerEntryContext(
    val bookId: Long? = null,
    val entrySource: TimerEntrySource,
    val sourceParams: Map<String, String> = emptyMap(),
    val returnRoute: String? = null,
    val userIntent: TimerUserIntent = TimerUserIntent.GENERAL_READING,
    val timestamp: Long = System.currentTimeMillis()
) : Parcelable {
    
    /**
     * 编码为字符串
     */
    fun encode(): String {
        // 使用简单的参数拼接，避免JSON序列化问题
        val encoded = buildString {
            append("bookId=${bookId ?: ""}")
            append("&source=${entrySource.name}")
            append("&intent=${userIntent.name}")
            if (returnRoute != null) {
                append("&return=${returnRoute}")
            }
            sourceParams.forEach { (key, value) ->
                append("&${key}=${value}")
            }
        }

        return encoded
    }
    
    companion object {
        /**
         * 从字符串解码
         */
        fun decode(encoded: String): TimerEntryContext {

            // 直接使用简单参数解析，避免JSON反序列化问题
            val result = parseSimpleParams(encoded)

            return result
        }
        
        /**
         * 解析简单参数格式
         */
        private fun parseSimpleParams(params: String): TimerEntryContext {
            if (params.isEmpty()) {
                return TimerEntryContext(
                    entrySource = TimerEntrySource.DIRECT,
                    userIntent = TimerUserIntent.GENERAL_READING
                )
            }
            
            val paramMap = try {
                params.split("&").associate { param ->
                    val parts = param.split("=", limit = 2)
                    if (parts.size == 2) {
                        parts[0] to parts[1]
                    } else {
                        parts[0] to ""
                    }
                }
            } catch (e: Exception) {
                emptyMap()
            }
            
            return TimerEntryContext(
                bookId = paramMap["bookId"]?.toLongOrNull(),
                entrySource = try {
                    TimerEntrySource.valueOf(paramMap["source"] ?: "DIRECT")
                } catch (e: Exception) {
                    TimerEntrySource.DIRECT
                },
                userIntent = try {
                    TimerUserIntent.valueOf(paramMap["intent"] ?: "GENERAL_READING")
                } catch (e: Exception) {
                    TimerUserIntent.GENERAL_READING
                },
                returnRoute = paramMap["return"],
                sourceParams = paramMap.filterKeys { 
                    it !in setOf("bookId", "source", "intent", "return") 
                }
            )
        }
        
        /**
         * 创建书架列表入口上下文
         */
        fun fromBookshelf(bookId: Long? = null): TimerEntryContext {
            return TimerEntryContext(
                bookId = bookId,
                entrySource = TimerEntrySource.BOOKSHELF_LIST,
                userIntent = TimerUserIntent.GENERAL_READING
            )
        }
        
        /**
         * 创建书籍详情入口上下文
         */
        fun fromBookDetail(bookId: Long): TimerEntryContext {
            return TimerEntryContext(
                bookId = bookId,
                entrySource = TimerEntrySource.BOOK_DETAIL,
                userIntent = TimerUserIntent.FOCUSED_READING
            )
        }
        
        /**
         * 创建分组详情入口上下文
         */
        fun fromGroupDetail(
            bookId: Long? = null,
            groupId: Long,
            groupName: String
        ): TimerEntryContext {
            return TimerEntryContext(
                bookId = bookId,
                entrySource = TimerEntrySource.GROUP_DETAIL,
                sourceParams = mapOf(
                    "groupId" to groupId.toString(),
                    "groupName" to groupName
                ),
                userIntent = TimerUserIntent.GENERAL_READING
            )
        }
        
        /**
         * 创建标签详情入口上下文
         */
        fun fromTagDetail(
            bookId: Long? = null,
            tagId: Long,
            tagName: String
        ): TimerEntryContext {
            return TimerEntryContext(
                bookId = bookId,
                entrySource = TimerEntrySource.TAG_DETAIL,
                sourceParams = mapOf(
                    "tagId" to tagId.toString(),
                    "tagName" to tagName
                ),
                userIntent = TimerUserIntent.GENERAL_READING
            )
        }
        
        /**
         * 创建快捷操作入口上下文
         */
        fun fromQuickAction(bookId: Long? = null): TimerEntryContext {
            return TimerEntryContext(
                bookId = bookId,
                entrySource = TimerEntrySource.QUICK_ACTION,
                userIntent = TimerUserIntent.QUICK_READING
            )
        }
    }
}

/**
 * 计时器入口来源
 */
@Serializable
enum class TimerEntrySource {
    BOOKSHELF_LIST,      // 书架列表
    BOOK_DETAIL,         // 书籍详情
    GROUP_DETAIL,        // 分组详情
    SOURCE_DETAIL,       // 来源详情
    TAG_DETAIL,          // 标签详情
    STATUS_DETAIL,       // 状态详情
    RATING_DETAIL,       // 评分详情
    QUICK_ACTION,        // 快捷操作
    NOTIFICATION,        // 通知
    WIDGET,              // 桌面小部件
    SEARCH_RESULT,       // 搜索结果
    STATISTICS,          // 统计页面
    DIRECT               // 直接打开
}

/**
 * 用户意图
 */
@Serializable
enum class TimerUserIntent {
    GENERAL_READING,     // 一般阅读
    FOCUSED_READING,     // 专注阅读
    QUICK_READING,       // 快速阅读
    REVIEW_READING,      // 复习阅读
    RESEARCH_READING     // 研究阅读
}

/**
 * 计时器导航扩展函数
 */
fun NavController.navigateToTimer(
    bookId: Long? = null,
    entrySource: TimerEntrySource,
    sourceParams: Map<String, String> = emptyMap(),
    userIntent: TimerUserIntent = TimerUserIntent.GENERAL_READING
) {
    val context = TimerEntryContext(
        bookId = bookId,
        entrySource = entrySource,
        sourceParams = sourceParams,
        returnRoute = currentBackStackEntry?.destination?.route,
        userIntent = userIntent
    )
    
    navigate("${Routes.TimeManagementRoutes.FORWARD_TIMER}?entryContext=${context.encode()}")
}

/**
 * 便捷的导航方法
 */
fun NavController.navigateToTimerFromBookshelf(bookId: Long? = null) {
    navigateToTimer(
        bookId = bookId,
        entrySource = TimerEntrySource.BOOKSHELF_LIST,
        userIntent = TimerUserIntent.GENERAL_READING
    )
}

fun NavController.navigateToTimerFromBookDetail(bookId: Long) {
    navigateToTimer(
        bookId = bookId,
        entrySource = TimerEntrySource.BOOK_DETAIL,
        userIntent = TimerUserIntent.FOCUSED_READING
    )
}

fun NavController.navigateToTimerFromGroupDetail(
    bookId: Long? = null,
    groupId: Long,
    groupName: String
) {
    navigateToTimer(
        bookId = bookId,
        entrySource = TimerEntrySource.GROUP_DETAIL,
        sourceParams = mapOf(
            "groupId" to groupId.toString(),
            "groupName" to groupName
        ),
        userIntent = TimerUserIntent.GENERAL_READING
    )
}

fun NavController.navigateToTimerFromQuickAction(bookId: Long? = null) {
    navigateToTimer(
        bookId = bookId,
        entrySource = TimerEntrySource.QUICK_ACTION,
        userIntent = TimerUserIntent.QUICK_READING
    )
}