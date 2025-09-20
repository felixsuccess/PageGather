package com.anou.pagegather.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import kotlin.math.max
import kotlin.math.min
import com.anou.pagegather.ui.theme.getColorSchemeForTheme

/**
 * 可访问性工具类
 * 用于验证颜色对比度和 WCAG 标准合规性
 */
object AccessibilityUtils {
    
    /**
     * WCAG 对比度标准
     */
    object ContrastRatio {
        const val AA_NORMAL = 4.5f      // WCAG AA 标准 - 普通文本
        const val AA_LARGE = 3.0f       // WCAG AA 标准 - 大文本
        const val AAA_NORMAL = 7.0f     // WCAG AAA 标准 - 普通文本
        const val AAA_LARGE = 4.5f      // WCAG AAA 标准 - 大文本
    }
    
    /**
     * 计算两个颜色之间的对比度
     * @param foreground 前景色
     * @param background 背景色
     * @return 对比度值 (1.0 到 21.0)
     */
    fun calculateContrastRatio(foreground: Color, background: Color): Float {
        val foregroundLuminance = foreground.luminance()
        val backgroundLuminance = background.luminance()
        
        val lighter = max(foregroundLuminance, backgroundLuminance)
        val darker = min(foregroundLuminance, backgroundLuminance)
        
        return (lighter + 0.05f) / (darker + 0.05f)
    }
    
    /**
     * 检查颜色对比度是否符合 WCAG AA 标准
     */
    fun meetsWCAGAA(foreground: Color, background: Color, isLargeText: Boolean = false): Boolean {
        val ratio = calculateContrastRatio(foreground, background)
        val requiredRatio = if (isLargeText) ContrastRatio.AA_LARGE else ContrastRatio.AA_NORMAL
        return ratio >= requiredRatio
    }
    
    /**
     * 检查颜色对比度是否符合 WCAG AAA 标准
     */
    fun meetsWCAGAAA(foreground: Color, background: Color, isLargeText: Boolean = false): Boolean {
        val ratio = calculateContrastRatio(foreground, background)
        val requiredRatio = if (isLargeText) ContrastRatio.AAA_LARGE else ContrastRatio.AAA_NORMAL
        return ratio >= requiredRatio
    }
    
    /**
     * 验证主题的可访问性
     */
    fun validateThemeAccessibility(theme: AppTheme, isDark: Boolean): ThemeAccessibilityReport {
        val colorScheme = getColorSchemeForThemeNonComposable(theme, isDark)
        val issues = mutableListOf<AccessibilityIssue>()
        
        // 验证主要文本对比度
        val primaryTextRatio = calculateContrastRatio(
            colorScheme.onSurface, 
            colorScheme.surface
        )
        if (primaryTextRatio < ContrastRatio.AA_NORMAL) {
            issues.add(
                AccessibilityIssue(
                    type = "文本对比度",
                    description = "主要文本对比度不足",
                    currentRatio = primaryTextRatio,
                    requiredRatio = ContrastRatio.AA_NORMAL,
                    severity = AccessibilitySeverity.HIGH
                )
            )
        }
        
        // 验证次要文本对比度
        val secondaryTextRatio = calculateContrastRatio(
            colorScheme.onSurfaceVariant,
            colorScheme.surface
        )
        if (secondaryTextRatio < ContrastRatio.AA_NORMAL) {
            issues.add(
                AccessibilityIssue(
                    type = "次要文本对比度",
                    description = "次要文本对比度不足",
                    currentRatio = secondaryTextRatio,
                    requiredRatio = ContrastRatio.AA_NORMAL,
                    severity = AccessibilitySeverity.MEDIUM
                )
            )
        }
        
        // 验证主色按钮对比度
        val primaryButtonRatio = calculateContrastRatio(
            colorScheme.onPrimary,
            colorScheme.primary
        )
        if (primaryButtonRatio < ContrastRatio.AA_NORMAL) {
            issues.add(
                AccessibilityIssue(
                    type = "按钮对比度",
                    description = "主色按钮文本对比度不足",
                    currentRatio = primaryButtonRatio,
                    requiredRatio = ContrastRatio.AA_NORMAL,
                    severity = AccessibilitySeverity.HIGH
                )
            )
        }
        
        // 验证错误文本对比度
        val errorTextRatio = calculateContrastRatio(
            colorScheme.onError,
            colorScheme.error
        )
        if (errorTextRatio < ContrastRatio.AA_NORMAL) {
            issues.add(
                AccessibilityIssue(
                    type = "错误文本对比度",
                    description = "错误状态文本对比度不足",
                    currentRatio = errorTextRatio,
                    requiredRatio = ContrastRatio.AA_NORMAL,
                    severity = AccessibilitySeverity.HIGH
                )
            )
        }
        
        return ThemeAccessibilityReport(
            theme = theme,
            isDark = isDark,
            issues = issues,
            isCompliant = issues.isEmpty(),
            overallScore = calculateAccessibilityScore(issues)
        )
    }
    
    /**
     * 计算可访问性评分
     */
    private fun calculateAccessibilityScore(issues: List<AccessibilityIssue>): Float {
        if (issues.isEmpty()) return 100f
        
        val totalDeductions = issues.sumOf { issue ->
            when (issue.severity) {
                AccessibilitySeverity.HIGH -> 25
                AccessibilitySeverity.MEDIUM -> 15
                AccessibilitySeverity.LOW -> 5
            }.toInt()
        }
        
        return (100 - totalDeductions).coerceAtLeast(0).toFloat()
    }
    
    /**
     * 验证所有主题的可访问性
     */
    fun validateAllThemes(): List<ThemeAccessibilityReport> {
        val reports = mutableListOf<ThemeAccessibilityReport>()
        
        AppTheme.getAllThemes().forEach { theme ->
            // 验证亮色模式
            reports.add(validateThemeAccessibility(theme, false))
            // 验证暗色模式
            reports.add(validateThemeAccessibility(theme, true))
        }
        
        return reports
    }
}

/**
 * 可访问性问题数据类
 */
data class AccessibilityIssue(
    val type: String,
    val description: String,
    val currentRatio: Float,
    val requiredRatio: Float,
    val severity: AccessibilitySeverity
)

/**
 * 可访问性严重程度
 */
enum class AccessibilitySeverity {
    LOW,    // 轻微问题
    MEDIUM, // 中等问题
    HIGH    // 严重问题
}

/**
 * 主题可访问性报告
 */
data class ThemeAccessibilityReport(
    val theme: AppTheme,
    val isDark: Boolean,
    val issues: List<AccessibilityIssue>,
    val isCompliant: Boolean,
    val overallScore: Float
) {
    fun getDisplayName(): String {
        val modeText = if (isDark) "暗色" else "亮色"
        return "${theme.displayName} ($modeText)"
    }
    
    fun getComplianceStatus(): String {
        return when {
            overallScore >= 95f -> "优秀"
            overallScore >= 85f -> "良好"
            overallScore >= 70f -> "合格"
            else -> "需要改进"
        }
    }
}