# 读书记录应用设计文档

## 概述

本设计文档基于需求文档，详细说明了读书记录应用的技术架构、数据模型、组件设计和实现方案。该应用使用 Android 原生开发，采用 Jetpack Compose + Material 3 构建现代化的用户界面。

## 技术架构

### 整体架构

采用 **MVVM + Clean Architecture** 分层架构：


## 数据同步设计

### 同步策略

数据同步功能支持本地数据库与云端存储之间的数据传输，实现多设备数据一致性。以下是详细设计：

#### 1. 同步模式

- **全量同步**：一次性同步所有数据（首次使用或数据严重不一致时）
- **增量同步**：仅同步上次同步后变更的数据（日常使用的主要模式）
- **定时同步**：根据用户设置的时间间隔自动执行增量同步
- **手动同步**：用户手动触发的同步操作

#### 2. 数据冲突解决

当本地数据与云端数据存在冲突时，采用以下策略：

- **时间戳优先**：以最后修改时间较新的数据为准
- **用户选择**：对于重要数据冲突，提示用户选择保留哪一方的数据
- **自动合并**：对于可合并的数据（如笔记、书摘），自动合并内容

#### 3. 同步状态管理

- **同步状态追踪**：记录每个数据项的同步状态（未同步、同步中、已同步、同步失败）
- **同步进度展示**：在UI中显示同步进度和状态信息
- **失败重试机制**：对于同步失败的数据，提供自动重试和手动重试选项

#### 4. 网络状态适应

- **在线模式**：网络连接正常时，实时或定时同步数据
- **离线模式**：网络断开时，记录本地变更，待网络恢复后批量同步
- **网络状态检测**：实时监测网络连接状态，自动切换同步策略

#### 5. 数据安全性

- **数据加密**：同步过程中对敏感数据进行加密传输
- **身份验证**：使用OAuth 2.0或其他安全认证机制确保只有授权用户可以访问数据
- **数据备份**：定期自动备份云端数据，防止数据丢失

### 实现架构

```
┌─────────────────────────────────────────────────────────────────┐
│                        数据同步模块                              │
├─────────────┬─────────────┬──────────────┬────────────────────┐
│ 同步策略管理 │ 冲突解决器  │ 同步状态追踪 │ 网络状态监测器     │
├─────────────┴─────────────┴──────────────┴────────────────────┤
│                        云存储服务接口                            │
└─────────────────────────────────────────────────────────────────┘
```

## 勋章系统实现细节

### 勋章展示界面

勋章系统采用网格布局展示所有勋章，每个勋章卡片包含以下信息：

- 勋章图标（彩色表示已解锁，灰色表示未解锁）
- 勋章名称
- 勋章描述
- 解锁进度（对于渐进式勋章）

### 解锁逻辑

勋章解锁采用事件触发机制：

1. **一次性勋章**：完成特定单次任务后解锁（如添加第一本书）
2. **累计勋章**：完成特定次数任务后解锁（如阅读10本书）
3. **时间勋章**：在特定时间段内完成任务解锁（如连续7天阅读）
4. **挑战勋章**：完成高难度任务后解锁（如一周内读完一本书）

### 通知机制

当用户解锁新勋章时，系统会：

- 显示Toast通知
- 发送系统通知（可在设置中关闭）
- 在应用内显示勋章解锁弹窗，展示勋章详情

## 应用设置模块设计

### 界面设计

应用设置界面采用分组列表布局，包含以下设置分组：

1. **常规设置**：主题切换、语言选择、字体大小调节
2. **阅读设置**：阅读模式、页面翻转动画、默认阅读进度单位
3. **同步设置**：同步频率、仅在WiFi下同步、自动备份
4. **通知设置**：阅读提醒、勋章解锁通知、目标完成提醒
5. **数据与隐私**：数据导出、清除缓存、隐私政策

### 实现细节

- 使用DataStore存储用户设置
- 主题切换支持即时生效，无需重启应用
- 字体大小调节支持预览功能
- 所有设置变更都即时保存
```
┌─────────────────────────────────────┐
│           Presentation Layer        │
│  (Compose UI + ViewModels)          │
├─────────────────────────────────────┤
│           Domain Layer              │
│  (Use Cases + Entities + Repository │
│   Interfaces)                       │
├─────────────────────────────────────┤
│             Data Layer              │
│  (Repository Impl + Data Sources)   │
└─────────────────────────────────────┘
```

### 核心技术栈

**UI 框架**

- **Jetpack Compose**: 现代化声明式 UI 框架
  - 用途：构建所有界面组件（书籍列表、详情页、笔记编辑器等）
  - 优势：响应式 UI、类型安全、与 Material 3 完美集成
- **Material Design 3**: Google 最新设计系统
  - 用途：提供一致的视觉设计语言和组件库
  - 功能：深色/浅色主题、动态颜色、现代化组件样式
- **Compose Navigation**: 页面导航管理
  - 用途：管理应用内页面跳转和参数传递
  - 功能：底部导航栏、页面间的数据传递、返回栈管理

**架构组件**

- **ViewModel**: 状态管理和业务逻辑
  - 用途：管理 UI 状态、处理用户交互、调用业务逻辑
  - 功能：配置变更时保持状态、生命周期感知
- **StateFlow**: UI 状态管理（热流）
  - 用途：ViewModel 中管理 UI 状态，配置变更时保持状态
  - 功能：始终保持最新状态、多订阅者共享、与 Compose 自动重组
- **Flow**: 数据流处理（冷流）
  - 用途：数据库查询、网络请求、文件操作的响应式数据流
  - 功能：按需执行、操作符链式调用、背压处理
- **Hilt**: 依赖注入框架
  - 用途：管理对象依赖关系、简化测试
  - 功能：自动生成依赖注入代码、作用域管理

**数据存储**

- **Room Database**: 本地 SQLite 数据库
  - 用途：存储书籍信息、阅读记录、笔记、书摘等核心数据
  - 功能：类型安全的 SQL 查询、数据库迁移、事务支持
- **DataStore**: 用户设置和偏好存储
  - 用途：存储应用设置（主题、字体大小、通知偏好等）
  - 优势：替代 SharedPreferences、类型安全、协程支持
- **File System**: 图片和备份文件存储
  - 用途：存储书籍封面图片、用户拍摄的书摘照片、数据备份文件
  - 功能：文件管理、缓存清理、存储空间优化

**网络和数据**

- **Retrofit + OkHttp**: HTTP 客户端和网络层
  - 用途：调用在线图书数据库 API（如豆瓣、Google Books）获取 ISBN 书籍信息
  - 功能：类型安全的 API 调用、自动缓存、超时处理、请求拦截
- **Gson**: JSON 序列化/反序列化
  - 用途：解析 API 返回的 JSON 数据、数据备份导出
  - 功能：自动映射 JSON 到 Kotlin 对象

**图像处理**

- **Coil**: 图片加载和缓存
  - 用途：加载和显示书籍封面图片
  - 功能：内存缓存、磁盘缓存、占位符显示、图片变换
- **CameraX**: 相机功能（条形码扫描、拍照）
  - 用途：扫描书籍条形码获取 ISBN、拍摄书摘照片
  - 功能：相机预览、图片捕获、条形码检测
- **ML Kit**: 条形码识别和 OCR 文字识别
  - 用途：识别书籍条形码、从照片中提取文字内容
  - 功能：离线识别、多种条形码格式支持、文字识别准确率高

**异步处理**

- **Kotlin Coroutines**: 异步编程和并发处理
  - 用途：数据库操作、网络请求、文件 I/O 的异步处理
  - 功能：结构化并发、取消支持、异常处理

**权限管理**

- **动态权限管理**: 运行时权限请求
  - 用途：相机权限（扫描条形码）、存储权限（保存图片）
  - 功能：权限状态检查、用户友好的权限请求流程

**富文本和图表**

- **Markdown 编辑器库**: 富文本笔记编辑
  - 用途：支持 Markdown 语法的笔记编辑和预览
  - 功能：语法高亮、实时预览、图片插入
- **Compose 原生图表**: 统计数据可视化
  - 用途：阅读统计图表、进度趋势、热力图显示
  - 备选方案：MPAndroidChart（如需复杂图表）

**性能优化**

- **内存优化**: 图片缓存管理、大列表虚拟化
- **布局优化**: Compose 重组优化、状态提升
- **图片压缩**: 自动压缩用户上传的图片
- **数据库优化**: 索引优化、分页查询

**其他功能**

- **WorkManager**: 后台任务调度
  - 用途：定期数据备份、阅读提醒通知、统计数据计算
  - 功能：任务持久化、网络状态感知、电池优化
- **Notification**: 系统通知
  - 用途：阅读提醒、勋章解锁通知、目标完成提醒
  - 功能：通知渠道管理、自定义通知样式、用户偏好控制
- **数据同步**: 本地与云端数据同步
  - 用途：实现多设备数据同步和备份恢复
  - 功能：增量/全量同步、定时同步、冲突解决

**代码质量和测试**

- **代码规范**: 遵循 Android 官方代码规范和 Kotlin 编码约定
- **单元测试**: Repository、UseCase、ViewModel 的单元测试
- **UI 测试**: Compose UI 组件的集成测试
- **代码检查**: Lint 检查、静态代码分析

## 数据模型设计

### 核心实体

#### 1. Book（书籍）

```kotlin
@Entity(tableName = "books")
data class Book(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val author: String,
    val isbn: String? = null,
    val coverImagePath: String? = null,
    val coverImageUrl: String? = null,
    val publisher: String? = null,
    val publishDate: String? = null,
    val language: String? = null,
    val description: String? = null,
    val bookType: BookType, // PHYSICAL, EBOOK
    val status: BookStatus, // WANT_TO_READ, READING, FINISHED, ABANDONED
    val progressUnit: ProgressUnit, // PAGE, CHAPTER, PERCENTAGE
    val totalPages: Int? = null,
    val totalChapters: Int? = null,
    val currentProgress: Int = 0,
    val rating: Float? = null,
    val review: String? = null,
    val sourceId: String? = null, // 书籍来源ID
    val addedDate: Long = System.currentTimeMillis(),
    val startReadingDate: Long? = null,
    val finishedDate: Long? = null,
    val lastReadDate: Long? = null,
    val customSortOrder: Int = 0 // 用户自定义排序顺序
)

enum class BookType { PHYSICAL, EBOOK }
enum class BookStatus { WANT_TO_READ, READING, FINISHED, ABANDONED }
enum class ProgressUnit { PAGE, CHAPTER, PERCENTAGE }
```

#### 2. BookCollection（藏书管理）

```kotlin
@Entity(
    tableName = "book_collection",
    foreignKeys = [
        ForeignKey(
            entity = Book::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class BookCollection(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val bookId: String, // 关联的书籍ID
    val ownershipStatus: OwnershipStatus = OwnershipStatus.UNKNOWN, // 拥有状态
    val storageLocation: String? = null, // 存放位置
    val bookCondition: BookCondition? = null, // 书籍状态
    val wishlistPriority: Int? = null, // 心愿单优先级
    val wishlistNotes: String? = null, // 心愿单备注
    val purchaseDate: Long? = null, // 购买日期
    val purchasePrice: Float? = null, // 购买价格
    val createdDate: Long = System.currentTimeMillis(),
    val updatedDate: Long = System.currentTimeMillis()
)

enum class OwnershipStatus {
    UNKNOWN,        // 未设置
    OWNED,          // 已拥有
    WANT_TO_BUY,    // 想要购买
    LENT_OUT,       // 已借出
    BORROWED        // 已借入
}
enum class BookCondition { NEW, GOOD, FAIR, POOR } // 全新、良好、一般、破损
```

#### 3. BookCollectionDao

```kotlin
@Dao
interface BookCollectionDao {
    @Query("SELECT * FROM book_collection WHERE bookId = :bookId")
    suspend fun getByBookId(bookId: String): BookCollection?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(collection: BookCollection): Long

    @Update
    suspend fun update(collection: BookCollection)

    @Delete
    suspend fun delete(collection: BookCollection)

    @Query("SELECT * FROM book_collection WHERE ownershipStatus = :status")
    fun getByOwnershipStatus(status: OwnershipStatus): Flow<List<BookCollection>>

    @Query("SELECT * FROM book_collection WHERE wishlistPriority IS NOT NULL ORDER BY wishlistPriority ASC")
    fun getWishlist(): Flow<List<BookCollection>>
}
```

#### 4. BookGroup（书籍分组）

```kotlin
@Entity(tableName = "book_group")
data class BookGroup(
    @PrimaryKey val id: Long = 0,
    val name: String,
    val iconName: String? = null,
    val color: String? = null,
    val isDefault: Boolean = false,
    val sortOrder: Int = 0, // 排序顺序，数值越小越靠前
    val createdDate: Long = System.currentTimeMillis(),
    val updatedDate: Long = 0,
    val lastSyncDate: Long = 0,
    val isDeleted: Boolean = false
)
```

#### 3. BookSource（书籍来源）

```kotlin
@Entity(tableName = "book_source")
data class BookSource(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String, // 微信读书、亚马逊、实体书店、图书馆等
    val isDefault: Boolean = false,
    val sortOrder: Int = 0, // 排序顺序
    val createdDate: Long = System.currentTimeMillis()
)
```

#### 4. Tag（标签）

```kotlin
@Entity(tableName = "tags")
data class Tag(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val category: TagCategory, // BOOK, NOTE
    val color: String? = null,
    val sortOrder: Int = 0, // 排序顺序，数值越小越靠前
    val createdDate: Long = System.currentTimeMillis()
)

enum class TagCategory { BOOK, NOTE }
```

#### 5. BookGroupRef（书籍分组关联）

```kotlin
@Entity(
    tableName = "book_group_ref",
    foreignKeys = [
        ForeignKey(entity = Book::class, parentColumns = ["id"], childColumns = ["bookId"]),
        ForeignKey(entity = BookGroup::class, parentColumns = ["id"], childColumns = ["groupId"])
    ]
)
data class BookGroupRef(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookId: Long,
    val groupId: Long
)
```

#### 6. BookTagRef（书籍标签关联）

```kotlin
@Entity(
    tableName = "book_tag_ref",
    foreignKeys = [
        ForeignKey(entity = Book::class, parentColumns = ["id"], childColumns = ["bookId"]),
        ForeignKey(entity = Tag::class, parentColumns = ["id"], childColumns = ["tagId"])
    ]
)
data class BookTagRef(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookId: Long,
    val tagId: Long,
    val createdDate: Long = System.currentTimeMillis(),
    val updatedDate: Long = 0,
    val lastSyncDate: Long = 0,
    val isDeleted: Boolean = false
)
```

#### 7. ReadingRecord（阅读记录）

```kotlin
@Entity(
    tableName = "reading_records",
    foreignKeys = [
        ForeignKey(entity = Book::class, parentColumns = ["id"], childColumns = ["bookId"])
    ]
)
data class ReadingRecord(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val bookId: String,
    val startTime: Long,
    val endTime: Long? = null,
    val duration: Long = 0, // 毫秒
    val startProgress: Int,
    val endProgress: Int,
    val recordType: RecordType, // PRECISE, MANUAL
    val notes: String? = null,
    val date: String, // YYYY-MM-DD 格式
    val createdDate: Long = System.currentTimeMillis(),
    val modifiedDate: Long? = null
)

enum class RecordType { PRECISE, MANUAL }
```

#### 8. Note（笔记和书摘）

```kotlin
@Entity(
    tableName = "note",
    foreignKeys = [
        ForeignKey(entity = Book::class, parentColumns = ["id"], childColumns = ["bookId"])
    ]
)
data class Note(
    @PrimaryKey val id: Long = 0,
    val bookId: Long?,
    val title: String? = null,
    val idea: String? = null,               // 个人想法/评论（支持 Markdown 图文混排）
    val quote: String? = null,              // 摘录原文（纯文本）

    val position: String? = null,

    val positionUnit: Int,

    val chapterName: String? = null,
    val createdDate: Long = System.currentTimeMillis(),
    val modifiedDate: Long? = null,
    val isDeleted: Boolean = false,
    val deletedDate: Long? = null,
    val attachmentCount: Int = 0
) {
    // 业务规则：必须填写至少一项内容
    fun isValid(): Boolean {
        return !idea.isNullOrBlank() || !quote.isNullOrBlank()
    }



    // 提取笔记中的内嵌图片路径（支持 Markdown 格式）
    fun getInlineImagePaths(): List<String> {
        if (idea.isNullOrBlank()) {
            return emptyList()
        }
        // 正则表达式提取 ![alt](path) 中的 path
        val imageRegex = """!\[.*?\]\((.*?)\)""".toRegex()
        return imageRegex.findAll(idea).map { it.groupValues[1] }.toList()
    }
}

```

#### 9. NoteAttachment（笔记附件）

```kotlin
@Entity(
    tableName = "note_attachments",
    foreignKeys = [
        ForeignKey(entity = Note::class, parentColumns = ["id"], childColumns = ["noteId"])
    ]
)
data class NoteAttachment(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val noteId: String,
    val fileName: String,           // 原始文件名
    val filePath: String,           // 存储路径
    val fileType: AttachmentType,   // 文件类型
    val fileSize: Long,             // 文件大小（字节）
    val mimeType: String,           // MIME 类型
    val isInlineImage: Boolean = false, // 是否为 Markdown 内嵌图片
    val sortOrder: Int = 0,         // 附件排序
    val createdDate: Long = System.currentTimeMillis()
)

enum class AttachmentType {
    IMAGE,      // 图片：jpg, png, gif, webp
    AUDIO,      // 音频：mp3, wav, m4a（语音笔记）
    VIDEO,      // 视频：mp4, mov（视频笔记）
    DOCUMENT,   // 文档：pdf, txt, doc
    OTHER       // 其他类型
}
```

#### 10. TagNoteEntity（笔记标签关联）

```kotlin
@Entity(
    tableName = "tag_note",
    primaryKeys = ["noteId", "tagId"],
    foreignKeys = [
        ForeignKey(entity = Note::class, parentColumns = ["id"], childColumns = ["noteId"]),
        ForeignKey(entity = Tag::class, parentColumns = ["id"], childColumns = ["tagId"])
    ]
)
data class TagNoteEntity(
    val noteId: String,
    val tagId: String
)
```

#### 11. LendingRecord（借阅记录）

```kotlin
@Entity(tableName = "lending_records")
data class LendingRecord(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val bookId: String,
    val type: LendingType, // LENT_OUT, BORROWED
    val contactName: String, // 对方姓名
    val contactInfo: String? = null, // 联系方式（可选）
    val lendDate: Long, // 借出/借入日期
    val expectedReturnDate: Long? = null, // 预期归还日期
    val actualReturnDate: Long? = null, // 实际归还日期
    val notes: String? = null, // 备注
    val isReturned: Boolean = false, // 是否已归还
    val createdDate: Long = System.currentTimeMillis()
)

enum class LendingType { LENT_OUT, BORROWED } // 借出、借入
```

#### 12. Achievement（勋章）

```kotlin
@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val iconName: String,
    val category: AchievementCategory,
    val level: AchievementLevel,
    val requirement: Int,
    val isUnlocked: Boolean = false,
    val unlockedDate: Long? = null,
    val progress: Int = 0
)

enum class AchievementCategory { READING_STREAK, BOOK_COUNT, READING_TIME, DIVERSITY, SPEED }
enum class AchievementLevel { BRONZE, SILVER, GOLD }
```

#### 12. ReadingGoal（阅读目标）

```kotlin
@Entity(tableName = "reading_goals")
data class ReadingGoal(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val type: GoalType,
    val targetValue: Int,
    val currentValue: Int = 0,
    val period: GoalPeriod,
    val startDate: Long,
    val endDate: Long,
    val isActive: Boolean = true,
    val createdDate: Long = System.currentTimeMillis()
)

enum class GoalType { BOOKS_COUNT, READING_TIME, PAGES_COUNT }
enum class GoalPeriod { WEEKLY, MONTHLY, YEARLY }
```

### 数据库设计

#### Room Database 配置

```kotlin
@Database(
    entities = [
        Book::class,
        BookGroupEntity::class,
        BookSource::class,
        Tag::class,
        BookGroupRefEntity::class,
        BookTagRefEntity::class,
        ReadingRecord::class,
        Note::class,
        NoteAttachment::class,
        NoteTagRefEntity::class,
        Achievement::class,
        ReadingGoal::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ReadingDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun bookGroupDao(): BookGroupDao
    abstract fun tagDao(): TagDao
    abstract fun bookSourceDao(): BookSourceDao
    abstract fun readingRecordDao(): ReadingRecordDao
    abstract fun noteAttachmentDao(): NoteAttachmentDao
    abstract fun noteDao(): NoteDao
    abstract fun achievementDao(): AchievementDao
    abstract fun readingGoalDao(): ReadingGoalDao
}
```

## 组件架构设计

### 1. Presentation Layer（表现层）

#### 主要 Composable 组件结构

```
MainActivity
├── ReadingTrackerApp
    ├── MainNavigation
        ├── HomeScreen
        ├── BooksScreen
        │   ├── BookListScreen
        │   ├── BookDetailScreen
        │   └── AddEditBookScreen
        ├── NotesScreen
        │   ├── NotesListScreen
        │   ├── NoteDetailScreen
        │   └── AddEditNoteScreen
        ├── StatisticsScreen
        │   ├── OverviewTab
        │   ├── TimelineTab
        │   └── ChartsTab
        ├── AchievementsScreen
        └── SettingsScreen
```

#### ViewModel 设计

```kotlin
// 主要 ViewModels
class BooksViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val bookGroupRepository: BookGroupRepository
) : ViewModel()

class ReadingRecordViewModel @Inject constructor(
    private val readingRecordRepository: ReadingRecordRepository
) : ViewModel()

class NotesViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : ViewModel()

class StatisticsViewModel @Inject constructor(
    private val statisticsRepository: StatisticsRepository
) : ViewModel()
```

### 2. Domain Layer（领域层）

#### Use Cases 设计

```kotlin
// 书籍管理用例
class AddBookUseCase @Inject constructor(
    private val bookRepository: BookRepository
)

class UpdateBookProgressUseCase @Inject constructor(
    private val bookRepository: BookRepository,
    private val readingRecordRepository: ReadingRecordRepository
)

class SearchBooksUseCase @Inject constructor(
    private val bookRepository: BookRepository
)

// 排序管理用例
class UpdateBookSortOrderUseCase @Inject constructor(
    private val bookRepository: BookRepository
)

class UpdateBookGroupSortOrderUseCase @Inject constructor(
    private val bookGroupRepository: BookGroupRepository
)

class UpdateTagSortOrderUseCase @Inject constructor(
    private val tagRepository: TagRepository
)

// 阅读记录用例
class StartReadingRecordUseCase @Inject constructor(
    private val readingRecordRepository: ReadingRecordRepository
)

class EndReadingRecordUseCase @Inject constructor(
    private val readingRecordRepository: ReadingRecordRepository
)

// 统计分析用例
class GetReadingStatisticsUseCase @Inject constructor(
    private val statisticsRepository: StatisticsRepository
)

class GenerateTimelineUseCase @Inject constructor(
    private val readingRecordRepository: ReadingRecordRepository,
    private val noteRepository: NoteRepository
)
```

### 3. Data Layer（数据层）

#### Repository 实现

```kotlin
@Singleton
class BookRepositoryImpl @Inject constructor(
    private val bookDao: BookDao,
    private val isbnApiService: IsbnApiService
) : BookRepository {

    override suspend fun addBook(book: Book): Result<String>
    override suspend fun updateBook(book: Book): Result<Unit>
    override suspend fun deleteBook(bookId: String): Result<Unit>
    override suspend fun getBookById(bookId: String): Result<Book?>
    override suspend fun getAllBooks(): Flow<List<Book>>
    override suspend fun getAllBooksSorted(sortBy: BookSortType): Flow<List<Book>>
    override suspend fun searchBooks(query: String): Flow<List<Book>>
    override suspend fun getBooksByStatus(status: BookStatus): Flow<List<Book>>
    override suspend fun getBooksByGroup(groupId: Long): Flow<List<Book>>
    override suspend fun searchBookByIsbn(isbn: String): Result<BookInfo?>
    override suspend fun updateBookSortOrder(bookId: String, newOrder: Int): Result<Unit>
}

enum class BookSortType {
    TITLE_ASC,           // 标题升序
    TITLE_DESC,          // 标题降序
    AUTHOR_ASC,          // 作者升序
    AUTHOR_DESC,         // 作者降序
    ADDED_DATE_ASC,      // 添加时间升序
    ADDED_DATE_DESC,     // 添加时间降序
    READING_PROGRESS_ASC, // 阅读进度升序
    READING_PROGRESS_DESC, // 阅读进度降序
    CUSTOM_ORDER         // 自定义排序
}
```

## 界面设计方案

### Material 3 主题配置

#### 颜色系统

```kotlin
// Light Theme Colors
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6750A4),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFEADDFF),
    onPrimaryContainer = Color(0xFF21005D),
    secondary = Color(0xFF625B71),
    onSecondary = Color(0xFFFFFFFF),
    // ... 其他颜色定义
)

// Dark Theme Colors
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFD0BCFF),
    onPrimary = Color(0xFF381E72),
    primaryContainer = Color(0xFF4F378B),
    onPrimaryContainer = Color(0xFFEADDFF),
    secondary = Color(0xFFCCC2DC),
    onSecondary = Color(0xFF332D41),
    // ... 其他颜色定义
)
```

#### 字体系统

```kotlin
val ReadingTrackerTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp,
    ),
    // ... 其他字体样式定义
)
```

### 核心界面组件

#### 1. 书籍卡片组件

```kotlin
@Composable
fun BookCard(
    book: Book,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 书籍封面
            BookCoverImage(
                imageUrl = book.coverImageUrl,
                imagePath = book.coverImagePath,
                contentDescription = book.title,
                modifier = Modifier.size(80.dp, 120.dp)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // 书名和作者
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // 进度条
                if (book.status == BookStatus.READING) {
                    ReadingProgressIndicator(
                        progress = book.getProgressPercentage(),
                        progressText = book.getProgressText()
                    )
                }

                // 状态标签
                BookStatusChip(status = book.status)
            }
        }
    }
}
```

#### 2. 阅读进度指示器

```kotlin
@Composable
fun ReadingProgressIndicator(
    progress: Float,
    progressText: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "阅读进度",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = progressText,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
```

#### 3. 计时器组件

```kotlin
@Composable
fun ReadingTimer(
    isRunning: Boolean,
    elapsedTime: Long,
    targetTime: Long? = null,
    onStartPause: () -> Unit,
    onStop: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 时间显示
            Text(
                text = formatTime(elapsedTime),
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // 目标时间进度（如果设置了反向计时）
            targetTime?.let { target ->
                val progress = (elapsedTime.toFloat() / target).coerceAtMost(1f)
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // 控制按钮
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onStartPause,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isRunning) "暂停" else "开始"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isRunning) "暂停" else "开始")
                }

                OutlinedButton(
                    onClick = onStop,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "停止"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("停止")
                }
            }
        }
    }
}
```

### 导航设计

#### Navigation Graph

```kotlin
@Composable
fun ReadingTrackerNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable("home") {
            HomeScreen(
                onNavigateToBooks = { navController.navigate("books") },
                onNavigateToNotes = { navController.navigate("notes") },
                onNavigateToStatistics = { navController.navigate("statistics") }
            )
        }

        composable("books") {
            BooksScreen(
                onNavigateToBookDetail = { bookId ->
                    navController.navigate("book_detail/$bookId")
                },
                onNavigateToAddBook = {
                    navController.navigate("add_book")
                }
            )
        }

        composable(
            "book_detail/{bookId}",
            arguments = listOf(navArgument("bookId") { type = NavType.StringType })
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId") ?: return@composable
            BookDetailScreen(
                bookId = bookId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEditBook = { navController.navigate("edit_book/$bookId") },
                onNavigateToNotes = { navController.navigate("notes/$bookId") }
            )
        }

        // ... 其他路由定义
    }
}
```

## 错误处理策略

### 统一错误处理

```kotlin
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

// 扩展函数
inline fun <T> Result<T>.onSuccess(action: (value: T) -> Unit): Result<T> {
    if (this is Result.Success) action(data)
    return this
}

inline fun <T> Result<T>.onError(action: (exception: Throwable) -> Unit): Result<T> {
    if (this is Result.Error) action(exception)
    return this
}
```

### 网络错误处理

```kotlin
class NetworkErrorHandler {
    fun handleError(throwable: Throwable): String {
        return when (throwable) {
            is UnknownHostException -> "网络连接失败，请检查网络设置"
            is SocketTimeoutException -> "网络请求超时，请稍后重试"
            is HttpException -> {
                when (throwable.code()) {
                    404 -> "未找到相关信息"
                    500 -> "服务器错误，请稍后重试"
                    else -> "网络请求失败：${throwable.message()}"
                }
            }
            else -> "未知错误：${throwable.message}"
        }
    }
}
```

## 测试策略

### 单元测试

```kotlin
// Repository 测试示例
@RunWith(MockitoJUnitRunner::class)
class BookRepositoryImplTest {

    @Mock
    private lateinit var bookDao: BookDao

    @Mock
    private lateinit var isbnApiService: IsbnApiService

    private lateinit var repository: BookRepositoryImpl

    @Before
    fun setup() {
        repository = BookRepositoryImpl(bookDao, isbnApiService)
    }

    @Test
    fun `addBook should return success when book is added successfully`() = runTest {
        // Given
        val book = createTestBook()
        whenever(bookDao.insertBook(book)).thenReturn(Unit)

        // When
        val result = repository.addBook(book)

        // Then
        assertTrue(result is Result.Success)
        verify(bookDao).insertBook(book)
    }
}
```

### UI 测试

```kotlin
@RunWith(AndroidJUnit4::class)
class BookListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun bookListScreen_displaysBooks() {
        // Given
        val testBooks = listOf(createTestBook())

        // When
        composeTestRule.setContent {
            BookListScreen(
                books = testBooks,
                onBookClick = {},
                onAddBookClick = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithText(testBooks.first().title)
            .assertIsDisplayed()
    }
}
```

## 系统配置和规范

### 错误处理策略

#### 统一错误码定义

```kotlin
enum class AppErrorCode(val code: Int, val message: String) {
    // 网络相关错误
    NETWORK_UNAVAILABLE(1001, "网络连接不可用"),
    NETWORK_TIMEOUT(1002, "网络请求超时"),
    SERVER_ERROR(1003, "服务器错误"),

    // 数据库相关错误
    DATABASE_ERROR(2001, "数据库操作失败"),
    DATA_CORRUPTION(2002, "数据损坏"),
    STORAGE_FULL(2003, "存储空间不足"),

    // 文件相关错误
    FILE_NOT_FOUND(3001, "文件不存在"),
    FILE_TOO_LARGE(3002, "文件过大"),
    FILE_FORMAT_INVALID(3003, "文件格式不支持"),

    // 权限相关错误
    PERMISSION_DENIED(4001, "权限被拒绝"),
    CAMERA_UNAVAILABLE(4002, "相机不可用"),

    // 业务逻辑错误
    INVALID_ISBN(5001, "无效的ISBN格式"),
    BOOK_ALREADY_EXISTS(5002, "书籍已存在"),
    INVALID_PROGRESS(5003, "无效的阅读进度")
}
```

#### 错误处理工具类

```kotlin
class ErrorHandler {
    companion object {
        fun handleError(throwable: Throwable): AppError {
            return when (throwable) {
                is UnknownHostException -> AppError(
                    AppErrorCode.NETWORK_UNAVAILABLE,
                    "请检查网络连接"
                )
                is SocketTimeoutException -> AppError(
                    AppErrorCode.NETWORK_TIMEOUT,
                    "网络请求超时，请稍后重试"
                )
                is HttpException -> handleHttpError(throwable)
                is SQLiteException -> AppError(
                    AppErrorCode.DATABASE_ERROR,
                    "数据保存失败，请重试"
                )
                is SecurityException -> AppError(
                    AppErrorCode.PERMISSION_DENIED,
                    "需要相应权限才能使用此功能"
                )
                else -> AppError(
                    AppErrorCode.DATABASE_ERROR,
                    "操作失败：${throwable.message}"
                )
            }
        }

        private fun handleHttpError(exception: HttpException): AppError {
            return when (exception.code()) {
                404 -> AppError(AppErrorCode.SERVER_ERROR, "未找到相关信息")
                500 -> AppError(AppErrorCode.SERVER_ERROR, "服务器错误，请稍后重试")
                else -> AppError(AppErrorCode.SERVER_ERROR, "网络请求失败")
            }
        }
    }
}

data class AppError(
    val code: AppErrorCode,
    val userMessage: String,
    val technicalMessage: String? = null
)
```

### 文件存储规范

#### 存储路径管理

```kotlin
class FilePathManager(private val context: Context) {

    companion object {
        // 主要目录
        private const val APP_DIR = "ReadingTracker"
        private const val BOOK_COVERS_DIR = "book_covers"
        private const val NOTE_IMAGES_DIR = "note_images"
        private const val NOTE_ATTACHMENTS_DIR = "note_attachments"
        private const val BACKUP_DIR = "backups"
        private const val TEMP_DIR = "temp"

        // 文件大小限制
        const val MAX_IMAGE_SIZE = 10 * 1024 * 1024 // 10MB
        const val MAX_ATTACHMENT_SIZE = 50 * 1024 * 1024 // 50MB
        const val MAX_BACKUP_SIZE = 100 * 1024 * 1024 // 100MB
    }

    // 获取应用根目录
    fun getAppDirectory(): File {
        val appDir = File(context.getExternalFilesDir(null), APP_DIR)
        if (!appDir.exists()) appDir.mkdirs()
        return appDir
    }

    // 获取书籍封面存储路径
    fun getBookCoverPath(bookId: String, fileName: String): String {
        val coverDir = File(getAppDirectory(), BOOK_COVERS_DIR)
        if (!coverDir.exists()) coverDir.mkdirs()
        return File(coverDir, "${bookId}_$fileName").absolutePath
    }

    // 获取笔记图片存储路径
    fun getNoteImagePath(noteId: String, fileName: String): String {
        val imageDir = File(getAppDirectory(), "$NOTE_IMAGES_DIR/$noteId")
        if (!imageDir.exists()) imageDir.mkdirs()
        return File(imageDir, fileName).absolutePath
    }

    // 获取笔记附件存储路径
    fun getNoteAttachmentPath(noteId: String, fileName: String): String {
        val attachmentDir = File(getAppDirectory(), "$NOTE_ATTACHMENTS_DIR/$noteId")
        if (!attachmentDir.exists()) attachmentDir.mkdirs()
        return File(attachmentDir, fileName).absolutePath
    }

    // 获取备份文件路径
    fun getBackupPath(fileName: String): String {
        val backupDir = File(getAppDirectory(), BACKUP_DIR)
        if (!backupDir.exists()) backupDir.mkdirs()
        return File(backupDir, fileName).absolutePath
    }

    // 清理临时文件
    fun cleanTempFiles() {
        val tempDir = File(getAppDirectory(), TEMP_DIR)
        if (tempDir.exists()) {
            tempDir.listFiles()?.forEach { file ->
                if (System.currentTimeMillis() - file.lastModified() > 24 * 60 * 60 * 1000) {
                    file.delete()
                }
            }
        }
    }

    // 获取存储空间使用情况
    fun getStorageUsage(): StorageInfo {
        val appDir = getAppDirectory()
        val totalSize = calculateDirectorySize(appDir)
        val availableSpace = appDir.freeSpace

        return StorageInfo(
            totalUsed = totalSize,
            availableSpace = availableSpace,
            bookCoversSize = calculateDirectorySize(File(appDir, BOOK_COVERS_DIR)),
            noteImagesSize = calculateDirectorySize(File(appDir, NOTE_IMAGES_DIR)),
            attachmentsSize = calculateDirectorySize(File(appDir, NOTE_ATTACHMENTS_DIR)),
            backupsSize = calculateDirectorySize(File(appDir, BACKUP_DIR))
        )
    }

    private fun calculateDirectorySize(directory: File): Long {
        if (!directory.exists()) return 0L
        return directory.walkTopDown().filter { it.isFile }.map { it.length() }.sum()
    }
}

data class StorageInfo(
    val totalUsed: Long,
    val availableSpace: Long,
    val bookCoversSize: Long,
    val noteImagesSize: Long,
    val attachmentsSize: Long,
    val backupsSize: Long
)
```

### 权限管理

#### 权限管理器

```kotlin
class PermissionManager(private val activity: Activity) {

    companion object {
        // 所需权限
        val CAMERA_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA
        )

        val STORAGE_PERMISSIONS = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        // 权限请求码
        const val REQUEST_CAMERA_PERMISSION = 1001
        const val REQUEST_STORAGE_PERMISSION = 1002
    }

    // 检查相机权限
    fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    // 检查存储权限
    fun hasStoragePermission(): Boolean {
        return STORAGE_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    // 请求相机权限
    fun requestCameraPermission() {
        if (shouldShowCameraPermissionRationale()) {
            showPermissionRationale(
                title = "需要相机权限",
                message = "扫描书籍条形码需要使用相机功能",
                onPositive = {
                    ActivityCompat.requestPermissions(
                        activity,
                        CAMERA_PERMISSIONS,
                        REQUEST_CAMERA_PERMISSION
                    )
                }
            )
        } else {
            ActivityCompat.requestPermissions(
                activity,
                CAMERA_PERMISSIONS,
                REQUEST_CAMERA_PERMISSION
            )
        }
    }

    // 请求存储权限
    fun requestStoragePermission() {
        if (shouldShowStoragePermissionRationale()) {
            showPermissionRationale(
                title = "需要存储权限",
                message = "保存书籍封面和笔记图片需要访问存储空间",
                onPositive = {
                    ActivityCompat.requestPermissions(
                        activity,
                        STORAGE_PERMISSIONS,
                        REQUEST_STORAGE_PERMISSION
                    )
                }
            )
        } else {
            ActivityCompat.requestPermissions(
                activity,
                STORAGE_PERMISSIONS,
                REQUEST_STORAGE_PERMISSION
            )
        }
    }

    // 处理权限请求结果
    fun handlePermissionResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        onGranted: () -> Unit,
        onDenied: () -> Unit
    ) {
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onGranted()
                } else {
                    onDenied()
                    if (!shouldShowCameraPermissionRationale()) {
                        showGoToSettingsDialog("相机权限")
                    }
                }
            }
            REQUEST_STORAGE_PERMISSION -> {
                if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    onGranted()
                } else {
                    onDenied()
                    if (!shouldShowStoragePermissionRationale()) {
                        showGoToSettingsDialog("存储权限")
                    }
                }
            }
        }
    }

    private fun shouldShowCameraPermissionRationale(): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            Manifest.permission.CAMERA
        )
    }

    private fun shouldShowStoragePermissionRationale(): Boolean {
        return STORAGE_PERMISSIONS.any { permission ->
            ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
        }
    }

    private fun showPermissionRationale(
        title: String,
        message: String,
        onPositive: () -> Unit
    ) {
        AlertDialog.Builder(activity)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("确定") { _, _ -> onPositive() }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showGoToSettingsDialog(permissionName: String) {
        AlertDialog.Builder(activity)
            .setTitle("权限被永久拒绝")
            .setMessage("${permissionName}被永久拒绝，请在设置中手动开启")
            .setPositiveButton("去设置") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", activity.packageName, null)
                }
                activity.startActivity(intent)
            }
            .setNegativeButton("取消", null)
            .show()
    }
}
```

这三个核心配置确保了应用的：

- **错误处理**：用户友好的错误提示和统一的错误管理
- **文件管理**：有序的文件存储和空间管理
- **权限管理**：流畅的权限请求流程和用户引导

这个设计文档涵盖了应用的核心架构、数据模型、组件设计和系统配置。接下来我们可以基于这个设计创建详细的实现任务列表。

## BookCollectionEntity 设计补充

基于任务调整，将藏书管理功能独立为单独的实体：

### BookCollectionEntity（藏书管理）

```kotlin
@Entity(
    tableName = "book_collection",
    foreignKeys = [
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class BookCollectionEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val bookId: Long, // 关联的书籍ID
    val ownershipStatus: OwnershipStatus = OwnershipStatus.UNKNOWN, // 拥有状态
    val storageLocation: String? = null, // 存放位置
    val bookCondition: BookCondition? = null, // 书籍状态
    val wishlistPriority: Int? = null, // 心愿单优先级
    val wishlistNotes: String? = null, // 心愿单备注
    val purchaseDate: Long? = null, // 购买日期
    val purchasePrice: Float? = null, // 购买价格
    val createdDate: Long = System.currentTimeMillis(),
    val updatedDate: Long = System.currentTimeMillis()
)

enum class OwnershipStatus {
    UNKNOWN,        // 未设置
    OWNED,          // 已拥有
    WANT_TO_BUY,    // 想要购买
    LENT_OUT,       // 已借出
    BORROWED        // 已借入
}

enum class BookCondition {
    NEW,    // 全新
    GOOD,   // 良好
    FAIR,   // 一般
    POOR    // 破损
}
```

### BookCollectionDao

```kotlin
@Dao
interface BookCollectionDao {
    @Query("SELECT * FROM book_collection WHERE bookId = :bookId")
    suspend fun getByBookId(bookId: Long): BookCollectionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(collection: BookCollectionEntity): Long

    @Update
    suspend fun update(collection: BookCollectionEntity)

    @Delete
    suspend fun delete(collection: BookCollectionEntity)

    @Query("SELECT * FROM book_collection WHERE ownershipStatus = :status")
    fun getByOwnershipStatus(status: OwnershipStatus): Flow<List<BookCollectionEntity>>

    @Query("SELECT * FROM book_collection WHERE wishlistPriority IS NOT NULL ORDER BY wishlistPriority ASC")
    fun getWishlist(): Flow<List<BookCollectionEntity>>
}
```

这样设计的优势：

1. **关注点分离**：书籍基本信息与藏书管理信息分离
2. **可选性**：不是所有书籍都需要藏书管理信息
3. **扩展性**：便于后续添加更多藏书管理功能
4. **数据完整性**：通过外键约束保证数据一致性
