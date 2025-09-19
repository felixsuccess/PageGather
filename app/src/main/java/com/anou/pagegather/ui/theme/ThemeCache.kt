package com.anou.pagegather.ui.theme

import androidx.compose.material3.ColorScheme
import java.util.concurrent.ConcurrentHashMap

/**
 * 主题缓存管理器
 * 提供颜色方案缓存机制以提升主题切换性能
 */
object ThemeCache {

    // 颜色方案缓存
    private val colorSchemeCache = ConcurrentHashMap<ThemeCacheKey, ColorScheme>()

    // 缓存统计
    private var cacheHits = 0
    private var cacheMisses = 0

    /**
     * 获取缓存的颜色方案，如果不存在则创建并缓存
     */
    fun getColorScheme(theme: AppTheme, isDark: Boolean): ColorScheme {
        val key = ThemeCacheKey(theme, isDark)

        return try {
            colorSchemeCache.getOrPut(key) {
                cacheMisses++
                createColorSchemeForTheme(theme, isDark)
            }.also {
                if (colorSchemeCache.containsKey(key)) {
                    cacheHits++
                }
            }
        } catch (e: Exception) {
            ThemeErrorHandler.handleCacheError("getColorScheme", e)
            // 回退到直接创建，不使用缓存
            createColorSchemeForTheme(theme, isDark)
        }
    }

    /**
     * 预加载所有主题的颜色方案
     */
    fun preloadAllThemes() {
        AppTheme.getAllThemes().forEach { theme ->
            // 预加载亮色和暗色版本的颜色方案
            getColorScheme(theme, false)
            getColorScheme(theme, true)
        }
    }

    /**
     * 清除缓存
     */
    fun clearCache() {
        colorSchemeCache.clear()
        cacheHits = 0
        cacheMisses = 0
    }

    /**
     * 获取缓存统计信息
     */
    fun getCacheStats(): CacheStats {
        val totalRequests = cacheHits + cacheMisses
        val hitRate = if (totalRequests > 0) {
            (cacheHits.toFloat() / totalRequests.toFloat()) * 100f
        } else {
            0f
        }

        return CacheStats(
            colorSchemeCacheSize = colorSchemeCache.size,
            extendedColorsCacheSize = 0, // 简化版本不缓存扩展颜色
            cacheHits = cacheHits,
            cacheMisses = cacheMisses,
            hitRate = hitRate
        )
    }

    /**
     * 创建颜色方案（内部方法）
     */
    private fun createColorSchemeForTheme(theme: AppTheme, isDark: Boolean): ColorScheme {
        return getColorSchemeForThemeInternal(theme, isDark)
    }
}

/**
 * 主题缓存键
 */
private data class ThemeCacheKey(
    val theme: AppTheme,
    val isDark: Boolean
)

/**
 * 缓存统计信息
 */
data class CacheStats(
    val colorSchemeCacheSize: Int,
    val extendedColorsCacheSize: Int,
    val cacheHits: Int,
    val cacheMisses: Int,
    val hitRate: Float
) {
    override fun toString(): String {
        return """
            缓存统计:
            - 颜色方案缓存大小: $colorSchemeCacheSize
            - 扩展颜色缓存大小: $extendedColorsCacheSize
            - 缓存命中: $cacheHits
            - 缓存未命中: $cacheMisses
            - 命中率: ${String.format("%.1f", hitRate)}%
        """.trimIndent()
    }
}