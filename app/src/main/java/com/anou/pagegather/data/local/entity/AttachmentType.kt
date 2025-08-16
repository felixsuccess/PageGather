package com.anou.pagegather.data.local.entity

/**
 * 附件类型枚举
 * 用于区分笔记附件的文件类型
 */
enum class AttachmentType {
    /** 图片：jpg, png, gif, webp */
    IMAGE,
    
    /** 音频：mp3, wav, m4a（语音笔记） */
    AUDIO,
    
    /** 视频：mp4, mov（视频笔记） */
    VIDEO,
    
    /** 文档：pdf, txt, doc */
    DOCUMENT,
    
    /** 其他类型 */
    OTHER
}