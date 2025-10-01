package com.anou.pagegather.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object FileOperator {
    suspend fun saveBookCover(context: Context, uri: Uri): File {
        return withContext(Dispatchers.IO) {
            val storageDir = File(context.externalCacheDir, "bookCover")
            if (!storageDir.exists()) storageDir.mkdirs()

            val timestamp = System.currentTimeMillis()
            val outputFile = File(storageDir, "cover_${timestamp}.jpg")

            try {
                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(outputFile).use { output ->
                        input.copyTo(output)
                    }
                }
                outputFile
            } catch (e: Exception) {
                Log.e("FileOperator", "Save book cover failed", e)
                throw e
            }
        }
    }
    
    /**
     * 保存笔记附件
     * @param context 应用上下文
     * @param uri 附件URI
     * @return 保存后的文件
     */
    suspend fun saveNoteAttachment(context: Context, uri: Uri): File {
        return withContext(Dispatchers.IO) {
            val storageDir = File(context.externalCacheDir, "attachments")
            if (!storageDir.exists()) storageDir.mkdirs()

            // 获取文件扩展名
            val extension = getFileExtension(context.contentResolver, uri)
            val mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream"
            
            // 根据MIME类型确定文件扩展名
            val fileExtension = when {
                mimeType.startsWith("image/") -> {
                    when {
                        mimeType.contains("jpeg") || mimeType.contains("jpg") -> ".jpg"
                        mimeType.contains("png") -> ".png"
                        mimeType.contains("gif") -> ".gif"
                        else -> ".jpg"
                    }
                }
                mimeType.startsWith("audio/") -> {
                    when {
                        mimeType.contains("mpeg") || mimeType.contains("mp3") -> ".mp3"
                        mimeType.contains("wav") -> ".wav"
                        mimeType.contains("ogg") -> ".ogg"
                        else -> ".mp3"
                    }
                }
                mimeType.startsWith("video/") -> {
                    when {
                        mimeType.contains("mp4") -> ".mp4"
                        mimeType.contains("avi") -> ".avi"
                        mimeType.contains("mov") -> ".mov"
                        else -> ".mp4"
                    }
                }
                else -> extension ?: ".bin"
            }

            val timestamp = System.currentTimeMillis()
            val outputFile = File(storageDir, "note_${timestamp}${fileExtension}")

            try {
                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(outputFile).use { output ->
                        input.copyTo(output)
                    }
                }
                outputFile
            } catch (e: Exception) {
                Log.e("FileOperator", "Save note attachment failed", e)
                throw e
            }
        }
    }
    
    /**
     * 获取文件扩展名
     */
    private fun getFileExtension(contentResolver: ContentResolver, uri: Uri): String? {
        val mimeType = contentResolver.getType(uri)
        return when (mimeType) {
            "image/jpeg" -> ".jpg"
            "image/png" -> ".png"
            "image/gif" -> ".gif"
            "audio/mpeg" -> ".mp3"
            "audio/wav" -> ".wav"
            "audio/ogg" -> ".ogg"
            "video/mp4" -> ".mp4"
            "video/avi" -> ".avi"
            "video/quicktime" -> ".mov"
            else -> {
                // 尝试从URI获取扩展名
                val uriString = uri.toString()
                val lastDotIndex = uriString.lastIndexOf('.')
                if (lastDotIndex != -1) {
                    val ext = uriString.substring(lastDotIndex)
                    if (ext.length <= 5) { // 合理的扩展名长度
                        return ext
                    }
                }
                null
            }
        }
    }
}