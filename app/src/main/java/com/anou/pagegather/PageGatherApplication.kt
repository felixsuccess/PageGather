package com.anou.pagegather

import android.app.Application
import com.anou.pagegather.data.repository.BookSourceRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class PageGatherApplication : Application() {
    
    @Inject
    lateinit var bookSourceRepository: BookSourceRepository
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    override fun onCreate() {
        super.onCreate()
        
        // 初始化内置书籍来源
        applicationScope.launch {
            try {
                bookSourceRepository.initializeBuiltInSourcesIfNeeded()
            } catch (e: Exception) {
                // 记录初始化失败，但不影响应用启动
                android.util.Log.e("PageGatherApp", "初始化书籍来源失败: ${e.message}", e)
            }
        }
    }
}