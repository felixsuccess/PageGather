package com.anou.pagegather.data.repository

import android.util.Log
import com.anou.pagegather.ui.theme.CustomTheme
import com.anou.pagegather.ui.theme.ExtendedColors
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.toArgb

/**
 * 自定义主题序列化器
 * 处理CustomTheme对象的序列化和反序列化
 */
object CustomThemeSerializer {
    private const val TAG = "CustomThemeSerializer"
    private val gson = GsonBuilder()
        .registerTypeAdapter(Color::class.java, ColorTypeAdapter())
        .registerTypeAdapter(ExtendedColors::class.java, ExtendedColorsTypeAdapter())
        .create()
    
    /**
     * 序列化CustomTheme对象为JSON字符串
     */
    fun serialize(theme: CustomTheme): String {
        return try {
            gson.toJson(theme)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to serialize CustomTheme", e)
            throw e
        }
    }
    
    /**
     * 反序列化JSON字符串为CustomTheme对象
     */
    fun deserialize(json: String): CustomTheme? {
        return try {
            gson.fromJson(json, CustomTheme::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to deserialize CustomTheme", e)
            null
        }
    }
}

/**
 * Color类型适配器
 */
class ColorTypeAdapter : JsonSerializer<Color>, JsonDeserializer<Color> {
    override fun serialize(src: Color?, typeOfSrc: java.lang.reflect.Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src?.toArgb() ?: 0)
    }
    
    override fun deserialize(json: JsonElement?, typeOfT: java.lang.reflect.Type?, context: JsonDeserializationContext?): Color {
        val argb = json?.asInt ?: 0
        return Color(argb)
    }
}

/**
 * ExtendedColors类型适配器
 */
class ExtendedColorsTypeAdapter : JsonSerializer<ExtendedColors>, JsonDeserializer<ExtendedColors> {
    override fun serialize(src: ExtendedColors?, typeOfSrc: java.lang.reflect.Type?, context: JsonSerializationContext?): JsonElement {
        val jsonObject = JsonObject()
        src?.let {
            jsonObject.addProperty("primaryContainer", it.primaryContainer.toArgb())
            jsonObject.addProperty("secondaryContainer", it.secondaryContainer.toArgb())
            jsonObject.addProperty("tertiaryContainer", it.tertiaryContainer.toArgb())
            jsonObject.addProperty("success", it.success.toArgb())
            jsonObject.addProperty("error", it.error.toArgb())
            jsonObject.addProperty("warning", it.warning.toArgb())
            jsonObject.addProperty("info", it.info.toArgb())
            jsonObject.addProperty("titleColor", it.titleColor.toArgb())
            jsonObject.addProperty("bodyColor", it.bodyColor.toArgb())
            jsonObject.addProperty("subtitleColor", it.subtitleColor.toArgb())
            jsonObject.addProperty("descriptionColor", it.descriptionColor.toArgb())
            jsonObject.addProperty("accentColor", it.accentColor.toArgb())
            jsonObject.addProperty("bookmarkColor", it.bookmarkColor.toArgb())
            jsonObject.addProperty("readingProgress", it.readingProgress.toArgb())
            jsonObject.addProperty("noteHighlight", it.noteHighlight.toArgb())
            jsonObject.addProperty("gradientStart", it.gradientStart.toArgb())
            jsonObject.addProperty("gradientEnd", it.gradientEnd.toArgb())
            jsonObject.addProperty("gradientSecondary", it.gradientSecondary.toArgb())
            jsonObject.addProperty("neutral100", it.neutral100.toArgb())
            jsonObject.addProperty("neutral200", it.neutral200.toArgb())
            jsonObject.addProperty("neutral300", it.neutral300.toArgb())
            jsonObject.addProperty("neutral500", it.neutral500.toArgb())
            jsonObject.addProperty("neutral700", it.neutral700.toArgb())
            jsonObject.addProperty("neutral900", it.neutral900.toArgb())
            jsonObject.addProperty("borderColor", it.borderColor.toArgb())
            jsonObject.addProperty("dividerColor", it.dividerColor.toArgb())
            jsonObject.addProperty("shadowColor", it.shadowColor.toArgb())
        }
        return jsonObject
    }
    
    override fun deserialize(json: JsonElement?, typeOfT: java.lang.reflect.Type?, context: JsonDeserializationContext?): ExtendedColors {
        val jsonObject = json?.asJsonObject ?: JsonObject()
        
        return ExtendedColors(
            primaryContainer = Color(jsonObject.get("primaryContainer")?.asInt ?: 0),
            secondaryContainer = Color(jsonObject.get("secondaryContainer")?.asInt ?: 0),
            tertiaryContainer = Color(jsonObject.get("tertiaryContainer")?.asInt ?: 0),
            success = Color(jsonObject.get("success")?.asInt ?: 0),
            error = Color(jsonObject.get("error")?.asInt ?: 0),
            warning = Color(jsonObject.get("warning")?.asInt ?: 0),
            info = Color(jsonObject.get("info")?.asInt ?: 0),
            titleColor = Color(jsonObject.get("titleColor")?.asInt ?: 0),
            bodyColor = Color(jsonObject.get("bodyColor")?.asInt ?: 0),
            subtitleColor = Color(jsonObject.get("subtitleColor")?.asInt ?: 0),
            descriptionColor = Color(jsonObject.get("descriptionColor")?.asInt ?: 0),
            accentColor = Color(jsonObject.get("accentColor")?.asInt ?: 0),
            bookmarkColor = Color(jsonObject.get("bookmarkColor")?.asInt ?: 0),
            readingProgress = Color(jsonObject.get("readingProgress")?.asInt ?: 0),
            noteHighlight = Color(jsonObject.get("noteHighlight")?.asInt ?: 0),
            gradientStart = Color(jsonObject.get("gradientStart")?.asInt ?: 0),
            gradientEnd = Color(jsonObject.get("gradientEnd")?.asInt ?: 0),
            gradientSecondary = Color(jsonObject.get("gradientSecondary")?.asInt ?: 0),
            neutral100 = Color(jsonObject.get("neutral100")?.asInt ?: 0),
            neutral200 = Color(jsonObject.get("neutral200")?.asInt ?: 0),
            neutral300 = Color(jsonObject.get("neutral300")?.asInt ?: 0),
            neutral500 = Color(jsonObject.get("neutral500")?.asInt ?: 0),
            neutral700 = Color(jsonObject.get("neutral700")?.asInt ?: 0),
            neutral900 = Color(jsonObject.get("neutral900")?.asInt ?: 0),
            borderColor = Color(jsonObject.get("borderColor")?.asInt ?: 0),
            dividerColor = Color(jsonObject.get("dividerColor")?.asInt ?: 0),
            shadowColor = Color(jsonObject.get("shadowColor")?.asInt ?: 0)
        )
    }
}