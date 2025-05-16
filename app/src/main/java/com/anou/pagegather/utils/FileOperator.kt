package com.anou.pagegather.utils

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
            //val storageDir = File(context.filesDir, "bookCover")
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
}
