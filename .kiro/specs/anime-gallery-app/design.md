# Design Document: Anime Gallery App

## Overview

The Anime Gallery App is a modern Android application built with Kotlin and Jetpack Compose that provides users with a seamless browsing experience for anime images from the Gelbooru API. The application follows MVVM architecture with clean separation of concerns across data, domain, and presentation layers.

The app features a responsive image grid with infinite scrolling, tag-based search with autocomplete, favorites management, NSFW content filtering, and offline caching. All UI components follow Material Design 3 guidelines and support both light and dark themes.

## Architecture

### High-Level Architecture

The application follows the MVVM (Model-View-ViewModel) pattern with a three-layer architecture:

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│  (Compose UI + ViewModels)              │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│           Domain Layer                  │
│  (Use Cases + Domain Models)            │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│            Data Layer                   │
│  (Repositories + Data Sources)          │
│  ┌──────────────┐  ┌─────────────────┐ │
│  │ Remote (API) │  │ Local (Room DB) │ │
│  └──────────────┘  └─────────────────┘ │
└─────────────────────────────────────────┘
```

### Layer Responsibilities

**Presentation Layer:**
- Jetpack Compose UI components
- ViewModels managing UI state
- Navigation between screens
- User input handling

**Domain Layer:**
- Business logic and use cases
- Domain models (pure Kotlin classes)
- Repository interfaces
- Transformation logic

**Data Layer:**
- Repository implementations
- Remote data source (Retrofit API client)
- Local data source (Room database)
- Data models and mappers

### Dependency Flow

Dependencies flow inward: Presentation → Domain ← Data. The domain layer has no dependencies on Android framework or external libraries, making it highly testable.

## Components and Interfaces

### 1. Presentation Layer Components

#### HomeScreen (Composable)
Main screen displaying the image grid with search functionality.

**State:**
```kotlin
data class HomeUiState(
    val images: LazyPagingItems<ImageItem>,
    val isLoading: Boolean,
    val error: String?,
    val searchQuery: String,
    val tagSuggestions: List<String>,
    val nsfwFilterEnabled: Boolean
)
```

**ViewModel:**
```kotlin
class HomeViewModel(
    private val getImagesUseCase: GetImagesUseCase,
    private val getTagSuggestionsUseCase: GetTagSuggestionsUseCase,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {
    
    val uiState: StateFlow<HomeUiState>
    
    fun onSearchQueryChanged(query: String)
    fun onTagSelected(tag: String)
    fun onNsfwFilterToggled()
    fun onRetry()
}
```

#### DetailScreen (Composable)
Screen showing full image details, tags, and actions.

**State:**
```kotlin
data class DetailUiState(
    val image: ImageDetail?,
    val isLoading: Boolean,
    val error: String?,
    val isFavorite: Boolean,
    val isDownloaded: Boolean,
    val downloadProgress: Float?
)
```

**ViewModel:**
```kotlin
class DetailViewModel(
    private val getImageDetailUseCase: GetImageDetailUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val downloadImageUseCase: DownloadImageUseCase,
    private val checkFavoriteStatusUseCase: CheckFavoriteStatusUseCase
) : ViewModel() {
    
    val uiState: StateFlow<DetailUiState>
    
    fun loadImageDetail(imageId: String)
    fun onFavoriteToggled()
    fun onDownloadClicked()
}
```

#### FavoritesScreen (Composable)
Screen displaying user's favorited images.

**State:**
```kotlin
data class FavoritesUiState(
    val favorites: List<ImageItem>,
    val isLoading: Boolean,
    val isEmpty: Boolean
)
```

#### FullscreenViewer (Composable)
Fullscreen image viewer with zoom and swipe navigation.

**State:**
```kotlin
data class FullscreenUiState(
    val currentImageUrl: String,
    val currentIndex: Int,
    val totalImages: Int,
    val showControls: Boolean,
    val zoomScale: Float
)
```

#### AgeGateScreen (Composable)
Age verification screen shown on first launch.

**Interface:**
```kotlin
@Composable
fun AgeGateScreen(
    onAgeConfirmed: () -> Unit,
    onAgeDeclined: () -> Unit
)
```

### 2. Domain Layer Components

#### Use Cases

**GetImagesUseCase:**
```kotlin
class GetImagesUseCase(
    private val imageRepository: ImageRepository
) {
    operator fun invoke(
        tags: List<String>,
        nsfwFilter: Boolean
    ): Flow<PagingData<ImageItem>>
}
```

**GetImageDetailUseCase:**
```kotlin
class GetImageDetailUseCase(
    private val imageRepository: ImageRepository
) {
    suspend operator fun invoke(imageId: String): Result<ImageDetail>
}
```

**ToggleFavoriteUseCase:**
```kotlin
class ToggleFavoriteUseCase(
    private val favoritesRepository: FavoritesRepository
) {
    suspend operator fun invoke(image: ImageItem): Result<Boolean>
}
```

**DownloadImageUseCase:**
```kotlin
class DownloadImageUseCase(
    private val downloadRepository: DownloadRepository
) {
    suspend operator fun invoke(imageUrl: String, imageId: String): Flow<DownloadProgress>
}
```

**GetTagSuggestionsUseCase:**
```kotlin
class GetTagSuggestionsUseCase(
    private val tagRepository: TagRepository
) {
    suspend operator fun invoke(query: String): Result<List<String>>
}
```

**CheckFavoriteStatusUseCase:**
```kotlin
class CheckFavoriteStatusUseCase(
    private val favoritesRepository: FavoritesRepository
) {
    suspend operator fun invoke(imageId: String): Boolean
}
```

#### Repository Interfaces

**ImageRepository:**
```kotlin
interface ImageRepository {
    fun getImages(
        tags: List<String>,
        nsfwFilter: Boolean
    ): Flow<PagingData<ImageItem>>
    
    suspend fun getImageDetail(imageId: String): Result<ImageDetail>
}
```

**FavoritesRepository:**
```kotlin
interface FavoritesRepository {
    suspend fun addFavorite(image: ImageItem): Result<Unit>
    suspend fun removeFavorite(imageId: String): Result<Unit>
    suspend fun isFavorite(imageId: String): Boolean
    fun getAllFavorites(): Flow<List<ImageItem>>
}
```

**DownloadRepository:**
```kotlin
interface DownloadRepository {
    suspend fun downloadImage(
        imageUrl: String,
        imageId: String
    ): Flow<DownloadProgress>
    
    suspend fun isDownloaded(imageId: String): Boolean
}
```

**TagRepository:**
```kotlin
interface TagRepository {
    suspend fun getTagSuggestions(query: String): Result<List<String>>
}
```

**PreferencesRepository:**
```kotlin
interface PreferencesRepository {
    suspend fun setNsfwFilterEnabled(enabled: Boolean)
    fun getNsfwFilterEnabled(): Flow<Boolean>
    suspend fun setAgeConfirmed(confirmed: Boolean)
    suspend fun isAgeConfirmed(): Boolean
}
```

### 3. Data Layer Components

#### Remote Data Source

**GelbooruApiService:**
```kotlin
interface GelbooruApiService {
    @GET("index.php")
    suspend fun getImages(
        @Query("page") page: String = "dapi",
        @Query("s") s: String = "post",
        @Query("q") q: String = "index",
        @Query("json") json: Int = 1,
        @Query("tags") tags: String,
        @Query("pid") pageId: Int,
        @Query("limit") limit: Int = 50
    ): GelbooruResponse
    
    @GET("index.php")
    suspend fun getTagSuggestions(
        @Query("page") page: String = "dapi",
        @Query("s") s: String = "tag",
        @Query("q") q: String = "index",
        @Query("json") json: Int = 1,
        @Query("name_pattern") pattern: String,
        @Query("limit") limit: Int = 10
    ): TagSuggestionsResponse
}
```

**ImagePagingSource:**
```kotlin
class ImagePagingSource(
    private val apiService: GelbooruApiService,
    private val tags: List<String>,
    private val nsfwFilter: Boolean
) : PagingSource<Int, ImageDto>() {
    
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ImageDto>
    
    override fun getRefreshKey(state: PagingState<Int, ImageDto>): Int?
}
```

#### Local Data Source

**AppDatabase:**
```kotlin
@Database(
    entities = [FavoriteImageEntity::class, DownloadedImageEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteImageDao(): FavoriteImageDao
    abstract fun downloadedImageDao(): DownloadedImageDao
}
```

**FavoriteImageDao:**
```kotlin
@Dao
interface FavoriteImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(image: FavoriteImageEntity)
    
    @Delete
    suspend fun deleteFavorite(image: FavoriteImageEntity)
    
    @Query("SELECT * FROM favorite_images ORDER BY timestamp DESC")
    fun getAllFavorites(): Flow<List<FavoriteImageEntity>>
    
    @Query("SELECT EXISTS(SELECT 1 FROM favorite_images WHERE id = :imageId)")
    suspend fun isFavorite(imageId: String): Boolean
    
    @Query("DELETE FROM favorite_images WHERE id = :imageId")
    suspend fun deleteFavoriteById(imageId: String)
}
```

**DownloadedImageDao:**
```kotlin
@Dao
interface DownloadedImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(image: DownloadedImageEntity)
    
    @Query("SELECT EXISTS(SELECT 1 FROM downloaded_images WHERE id = :imageId)")
    suspend fun isDownloaded(imageId: String): Boolean
    
    @Query("SELECT * FROM downloaded_images WHERE id = :imageId")
    suspend fun getDownloadedImage(imageId: String): DownloadedImageEntity?
}
```

#### Repository Implementations

**ImageRepositoryImpl:**
```kotlin
class ImageRepositoryImpl(
    private val apiService: GelbooruApiService,
    private val imageCache: ImageCache
) : ImageRepository {
    
    override fun getImages(
        tags: List<String>,
        nsfwFilter: Boolean
    ): Flow<PagingData<ImageItem>> {
        return Pager(
            config = PagingConfig(pageSize = 50, enablePlaceholders = false),
            pagingSourceFactory = {
                ImagePagingSource(apiService, tags, nsfwFilter)
            }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomainModel() }
        }
    }
    
    override suspend fun getImageDetail(imageId: String): Result<ImageDetail> {
        return try {
            val response = apiService.getImages(tags = "id:$imageId", pageId = 0, limit = 1)
            val imageDto = response.posts.firstOrNull()
                ?: return Result.failure(Exception("Image not found"))
            Result.success(imageDto.toDetailModel())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

**FavoritesRepositoryImpl:**
```kotlin
class FavoritesRepositoryImpl(
    private val favoriteImageDao: FavoriteImageDao
) : FavoritesRepository {
    
    override suspend fun addFavorite(image: ImageItem): Result<Unit> {
        return try {
            favoriteImageDao.insertFavorite(image.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun removeFavorite(imageId: String): Result<Unit> {
        return try {
            favoriteImageDao.deleteFavoriteById(imageId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun isFavorite(imageId: String): Boolean {
        return favoriteImageDao.isFavorite(imageId)
    }
    
    override fun getAllFavorites(): Flow<List<ImageItem>> {
        return favoriteImageDao.getAllFavorites().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
}
```

## Data Models

### Domain Models

**ImageItem:**
```kotlin
data class ImageItem(
    val id: String,
    val thumbnailUrl: String,
    val previewUrl: String,
    val fileUrl: String,
    val width: Int,
    val height: Int,
    val rating: Rating,
    val tags: List<String>
)

enum class Rating {
    SAFE, QUESTIONABLE, EXPLICIT
}
```

**ImageDetail:**
```kotlin
data class ImageDetail(
    val id: String,
    val fileUrl: String,
    val width: Int,
    val height: Int,
    val fileSize: Long,
    val rating: Rating,
    val tags: List<String>,
    val source: String?,
    val createdAt: String
)
```

**DownloadProgress:**
```kotlin
sealed class DownloadProgress {
    data class Progress(val percentage: Float) : DownloadProgress()
    data class Success(val filePath: String) : DownloadProgress()
    data class Error(val message: String) : DownloadProgress()
}
```

### Data Transfer Objects (DTOs)

**GelbooruResponse:**
```kotlin
@Serializable
data class GelbooruResponse(
    @SerialName("post")
    val posts: List<ImageDto>
)
```

**ImageDto:**
```kotlin
@Serializable
data class ImageDto(
    @SerialName("id")
    val id: String,
    @SerialName("preview_url")
    val previewUrl: String,
    @SerialName("sample_url")
    val sampleUrl: String,
    @SerialName("file_url")
    val fileUrl: String,
    @SerialName("width")
    val width: Int,
    @SerialName("height")
    val height: Int,
    @SerialName("rating")
    val rating: String,
    @SerialName("tags")
    val tags: String,
    @SerialName("source")
    val source: String?,
    @SerialName("created_at")
    val createdAt: String?
)
```

### Database Entities

**FavoriteImageEntity:**
```kotlin
@Entity(tableName = "favorite_images")
data class FavoriteImageEntity(
    @PrimaryKey
    val id: String,
    val thumbnailUrl: String,
    val previewUrl: String,
    val fileUrl: String,
    val width: Int,
    val height: Int,
    val rating: String,
    val tags: String,
    val timestamp: Long
)
```

**DownloadedImageEntity:**
```kotlin
@Entity(tableName = "downloaded_images")
data class DownloadedImageEntity(
    @PrimaryKey
    val id: String,
    val localFilePath: String,
    val timestamp: Long
)
```

### Mappers

**ImageDto to ImageItem:**
```kotlin
fun ImageDto.toDomainModel(): ImageItem {
    return ImageItem(
        id = id,
        thumbnailUrl = previewUrl,
        previewUrl = sampleUrl,
        fileUrl = fileUrl,
        width = width,
        height = height,
        rating = rating.toRating(),
        tags = tags.split(" ").filter { it.isNotBlank() }
    )
}

fun String.toRating(): Rating {
    return when (this.lowercase()) {
        "safe", "s" -> Rating.SAFE
        "questionable", "q" -> Rating.QUESTIONABLE
        "explicit", "e" -> Rating.EXPLICIT
        else -> Rating.SAFE
    }
}
```

## Correctness Properties


*A property is a characteristic or behavior that should hold true across all valid executions of a system—essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.*

### Property 1: Image Grid Aspect Ratio Consistency

*For any* set of images displayed in the grid, all thumbnails should maintain consistent aspect ratios regardless of the original image dimensions.

**Validates: Requirements 1.2**

### Property 2: Error Display with Retry

*For any* error that occurs during image loading, the app should display an error message and provide a retry option.

**Validates: Requirements 1.5**

### Property 3: Pagination Appends Images

*For any* existing image list and newly loaded page, the combined list should contain all images from both the original list and the new page in the correct order.

**Validates: Requirements 2.2**

### Property 4: Pagination Error Indicator

*For any* pagination error, an error indicator should be displayed at the bottom of the grid.

**Validates: Requirements 2.4**

### Property 5: Tag Search Query Construction

*For any* list of search tags, the API query should be constructed with those tags properly formatted and space-separated, and the results should replace the current grid.

**Validates: Requirements 3.1, 3.2, 3.3**

### Property 6: Tag Autocomplete Suggestions

*For any* partial tag input, the autocomplete system should display relevant tag suggestions based on the current incomplete tag.

**Validates: Requirements 4.1, 4.3**

### Property 7: Tag Selection Adds to Search

*For any* selected tag suggestion, the complete tag should be added to the search field.

**Validates: Requirements 4.2**

### Property 8: Image Tap Navigation

*For any* image tapped in the grid, the app should navigate to the detail screen for that specific image.

**Validates: Requirements 5.1**

### Property 9: Detail Screen Information Display

*For any* image detail screen, all required information should be displayed including the full resolution image, complete tag list, resolution dimensions, source, and rating.

**Validates: Requirements 5.2, 5.3, 5.4, 5.5**

### Property 10: Fullscreen Mode Activation

*For any* tap on the full image in the detail screen, the app should enter fullscreen mode.

**Validates: Requirements 6.1**

### Property 11: Fullscreen Controls Toggle

*For any* tap on the screen while in fullscreen mode, the UI controls visibility should toggle between shown and hidden.

**Validates: Requirements 6.4**

### Property 12: Favorite Persistence Round Trip

*For any* image, favoriting it should save it to the database, and it should be retrievable from the favorites list. Unfavoriting should remove it from the database.

**Validates: Requirements 7.1, 7.3, 7.4**

### Property 13: Favorite Icon State Sync

*For any* image that is favorited or unfavorited, the favorite icon should immediately reflect the current favorite status.

**Validates: Requirements 7.2**

### Property 14: Favorite Cache Loading

*For any* favorited image that exists in the cache, loading the favorites screen should retrieve the image from cache rather than making a network request.

**Validates: Requirements 7.5**

### Property 15: Image Download to Storage

*For any* download request, the full resolution image should be saved to device storage and be accessible after the download completes.

**Validates: Requirements 8.1**

### Property 16: Download Status Indication

*For any* download operation, the appropriate status should be displayed: progress indicator during download, success notification on completion, or error message with retry on failure.

**Validates: Requirements 8.2, 8.3, 8.4**

### Property 17: Downloaded Image Status Display

*For any* image that has been downloaded, the app should indicate the downloaded status when viewing that image.

**Validates: Requirements 8.5**

### Property 18: NSFW Filter Query Construction

*For any* NSFW filter setting (enabled or disabled), API queries should include only the appropriate content ratings: excluding explicit content when enabled, including all ratings when disabled.

**Validates: Requirements 9.1, 9.2**

### Property 19: Filter Change Triggers Refresh

*For any* change to the NSFW filter setting, the image grid should refresh with the new filter applied.

**Validates: Requirements 9.3**

### Property 20: NSFW Filter Persistence

*For any* NSFW filter setting, the setting should persist across app sessions and be loaded on app launch.

**Validates: Requirements 9.4, 9.5**

### Property 21: Age Confirmation Grants Access

*For any* age confirmation action, confirming should grant access to the app, while declining should close the app.

**Validates: Requirements 10.2**

### Property 22: Age Gate Single Display

*For any* app session after age confirmation is completed, the age gate should not be displayed on subsequent launches.

**Validates: Requirements 10.4, 10.5**

### Property 23: Theme Application

*For any* system theme setting (dark or light mode), the app should apply the corresponding theme colors to all UI components.

**Validates: Requirements 11.1, 11.2, 11.3**

### Property 24: Theme Contrast Requirements

*For any* theme (dark or light), all text and UI elements should have sufficient contrast ratios meeting accessibility standards (minimum 4.5:1 for normal text).

**Validates: Requirements 11.5**

### Property 25: Image Cache Storage

*For any* image that is loaded, it should be stored in the local cache and be retrievable from cache on subsequent requests.

**Validates: Requirements 12.1, 12.2**

### Property 26: LRU Cache Eviction

*For any* cache that exceeds storage limits, the least recently used images should be removed first to make space for new images.

**Validates: Requirements 12.3**

### Property 27: Manual Cache Clear

*For any* manual cache clear action, all cached images should be removed and the cache should be empty.

**Validates: Requirements 12.5**

### Property 28: Error Message Display

*For any* error type (network error, rate limit, image load failure, offline state), an appropriate user-friendly error message should be displayed.

**Validates: Requirements 13.1, 13.2, 13.3, 13.4**

### Property 29: Error Logging

*For any* error that occurs, error details should be logged for debugging purposes.

**Validates: Requirements 13.5**

### Property 30: API Endpoint Usage

*For any* API request, the Gelbooru JSON API endpoint should be used with properly formatted parameters.

**Validates: Requirements 14.1**

### Property 31: Tag Parameter Encoding

*For any* tags containing special characters, the tags should be properly URL-encoded in API requests.

**Validates: Requirements 14.2**

### Property 32: JSON Parsing Round Trip

*For any* valid Gelbooru API JSON response, parsing it into domain models and then serializing back should preserve the essential data fields.

**Validates: Requirements 14.3**

### Property 33: API Error Handling

*For any* API error response, the app should handle it gracefully without crashing and provide appropriate user feedback.

**Validates: Requirements 14.4**

### Property 34: API Rate Limiting

*For any* sequence of API requests, the rate limiting mechanism should ensure requests do not exceed the API's usage policies.

**Validates: Requirements 14.5**

### Property 35: Touch Target Minimum Size

*For any* interactive UI element, the touch target should meet the minimum size requirement of 48dp × 48dp for accessibility.

**Validates: Requirements 15.4**

### Property 36: Visible Image Load Priority

*For any* image loading scenario, images currently visible in the viewport should be loaded before off-screen images.

**Validates: Requirements 16.3**

### Property 37: Lazy Loading on Viewport Entry

*For any* image that enters the viewport, it should trigger loading if not already loaded or cached.

**Validates: Requirements 16.5**

## Error Handling

### Error Categories

The application handles errors across multiple layers:

**Network Errors:**
- Connection timeout
- No internet connection
- DNS resolution failure
- Server unavailable (5xx errors)

**API Errors:**
- Rate limiting (429 status)
- Invalid request (4xx errors)
- Malformed response
- Empty response

**Local Storage Errors:**
- Database write failure
- Insufficient storage space
- File system errors
- Cache corruption

**Image Loading Errors:**
- Invalid image format
- Corrupted image data
- Image too large
- Download interrupted

### Error Handling Strategy

**Network Layer:**
```kotlin
sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val exception: Exception, val message: String) : NetworkResult<Nothing>()
    object Loading : NetworkResult<Nothing>()
}

class NetworkErrorHandler {
    fun handleError(exception: Exception): String {
        return when (exception) {
            is IOException -> "Network connection error. Please check your internet."
            is HttpException -> when (exception.code()) {
                429 -> "Rate limit reached. Please try again in a few minutes."
                in 400..499 -> "Invalid request. Please try again."
                in 500..599 -> "Server error. Please try again later."
                else -> "An error occurred. Please try again."
            }
            else -> "An unexpected error occurred."
        }
    }
}
```

**Repository Layer:**
```kotlin
suspend fun <T> safeApiCall(
    apiCall: suspend () -> T
): Result<T> {
    return try {
        Result.success(apiCall())
    } catch (e: Exception) {
        Log.e("Repository", "API call failed", e)
        Result.failure(e)
    }
}
```

**ViewModel Layer:**
```kotlin
fun loadImages() {
    viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, error = null) }
        
        getImagesUseCase(tags, nsfwFilter)
            .catch { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = errorHandler.handleError(exception)
                    )
                }
            }
            .collect { pagingData ->
                _images.value = pagingData
                _uiState.update { it.copy(isLoading = false) }
            }
    }
}
```

**UI Layer:**
```kotlin
@Composable
fun ErrorView(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = "Error",
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = message, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}
```

### Retry Logic

**Exponential Backoff:**
```kotlin
class RetryPolicy {
    suspend fun <T> retryWithBackoff(
        maxRetries: Int = 3,
        initialDelay: Long = 1000,
        maxDelay: Long = 10000,
        factor: Double = 2.0,
        block: suspend () -> T
    ): T {
        var currentDelay = initialDelay
        repeat(maxRetries - 1) { attempt ->
            try {
                return block()
            } catch (e: Exception) {
                Log.w("RetryPolicy", "Attempt ${attempt + 1} failed", e)
            }
            delay(currentDelay)
            currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
        }
        return block() // Last attempt without catching
    }
}
```

### Rate Limiting

**Request Throttling:**
```kotlin
class RateLimiter(
    private val maxRequests: Int = 10,
    private val timeWindowMs: Long = 1000
) {
    private val requestTimestamps = mutableListOf<Long>()
    
    suspend fun <T> throttle(block: suspend () -> T): T {
        val now = System.currentTimeMillis()
        requestTimestamps.removeAll { it < now - timeWindowMs }
        
        if (requestTimestamps.size >= maxRequests) {
            val oldestRequest = requestTimestamps.first()
            val delayMs = timeWindowMs - (now - oldestRequest)
            delay(delayMs)
        }
        
        requestTimestamps.add(System.currentTimeMillis())
        return block()
    }
}
```

## Testing Strategy

### Overview

The testing strategy employs a dual approach combining unit tests for specific examples and edge cases with property-based tests for universal correctness properties. This comprehensive approach ensures both concrete functionality and general correctness across all inputs.

### Testing Layers

**Unit Tests:**
- Specific examples demonstrating correct behavior
- Edge cases (empty states, no results, boundary conditions)
- Error conditions and error handling
- Integration points between components
- ViewModel state transitions
- Repository data transformations
- Mapper functions

**Property-Based Tests:**
- Universal properties that hold for all inputs
- Data model invariants
- Round-trip properties (serialization, database operations)
- State consistency properties
- API contract validation
- Comprehensive input coverage through randomization

### Property-Based Testing Configuration

**Framework:** Kotest Property Testing
**Minimum Iterations:** 100 per property test
**Tag Format:** `Feature: anime-gallery-app, Property {number}: {property_text}`

Each correctness property defined in this document must be implemented as a single property-based test that validates the property across randomly generated inputs.

### Test Organization

```
app/
├── src/
│   ├── test/                    # Unit tests
│   │   ├── domain/
│   │   │   ├── usecase/
│   │   │   └── model/
│   │   ├── data/
│   │   │   ├── repository/
│   │   │   ├── mapper/
│   │   │   └── source/
│   │   └── presentation/
│   │       └── viewmodel/
│   │
│   └── androidTest/             # Instrumented tests
│       ├── ui/
│       ├── database/
│       └── integration/
```

### Unit Test Examples

**ViewModel Tests:**
```kotlin
class HomeViewModelTest {
    @Test
    fun `when search query changes, should update state and trigger search`() = runTest {
        // Given
        val viewModel = HomeViewModel(getImagesUseCase, getTagSuggestionsUseCase, preferencesRepository)
        
        // When
        viewModel.onSearchQueryChanged("anime")
        
        // Then
        assertEquals("anime", viewModel.uiState.value.searchQuery)
        verify(getImagesUseCase).invoke(listOf("anime"), any())
    }
    
    @Test
    fun `when NSFW filter toggled, should update preference and refresh grid`() = runTest {
        // Given
        val viewModel = HomeViewModel(getImagesUseCase, getTagSuggestionsUseCase, preferencesRepository)
        
        // When
        viewModel.onNsfwFilterToggled()
        
        // Then
        verify(preferencesRepository).setNsfwFilterEnabled(any())
        verify(getImagesUseCase).invoke(any(), any())
    }
}
```

**Repository Tests:**
```kotlin
class ImageRepositoryImplTest {
    @Test
    fun `getImageDetail returns success when API returns valid data`() = runTest {
        // Given
        val mockApi = mockk<GelbooruApiService>()
        val repository = ImageRepositoryImpl(mockApi, imageCache)
        coEvery { mockApi.getImages(any(), any(), any()) } returns validResponse
        
        // When
        val result = repository.getImageDetail("123")
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals("123", result.getOrNull()?.id)
    }
    
    @Test
    fun `getImageDetail returns failure when API throws exception`() = runTest {
        // Given
        val mockApi = mockk<GelbooruApiService>()
        val repository = ImageRepositoryImpl(mockApi, imageCache)
        coEvery { mockApi.getImages(any(), any(), any()) } throws IOException()
        
        // When
        val result = repository.getImageDetail("123")
        
        // Then
        assertTrue(result.isFailure)
    }
}
```

**Mapper Tests:**
```kotlin
class ImageMapperTest {
    @Test
    fun `ImageDto maps to ImageItem correctly`() {
        // Given
        val dto = ImageDto(
            id = "123",
            previewUrl = "https://example.com/preview.jpg",
            sampleUrl = "https://example.com/sample.jpg",
            fileUrl = "https://example.com/file.jpg",
            width = 1920,
            height = 1080,
            rating = "safe",
            tags = "anime girl school",
            source = "https://source.com",
            createdAt = "2024-01-01"
        )
        
        // When
        val item = dto.toDomainModel()
        
        // Then
        assertEquals("123", item.id)
        assertEquals(Rating.SAFE, item.rating)
        assertEquals(listOf("anime", "girl", "school"), item.tags)
    }
    
    @Test
    fun `rating string maps to Rating enum correctly`() {
        assertEquals(Rating.SAFE, "safe".toRating())
        assertEquals(Rating.SAFE, "s".toRating())
        assertEquals(Rating.QUESTIONABLE, "questionable".toRating())
        assertEquals(Rating.QUESTIONABLE, "q".toRating())
        assertEquals(Rating.EXPLICIT, "explicit".toRating())
        assertEquals(Rating.EXPLICIT, "e".toRating())
    }
}
```

### Property-Based Test Examples

**Property 3: Pagination Appends Images**
```kotlin
class PaginationPropertiesTest : StringSpec({
    "Property 3: Pagination appends images" {
        // Feature: anime-gallery-app, Property 3: Pagination appends images
        checkAll(100, Arb.list(Arb.imageItem(), 1..50), Arb.list(Arb.imageItem(), 1..50)) { existingList, newPage ->
            // Given existing list and new page
            val combined = existingList + newPage
            
            // Then combined list should contain all items
            combined.size shouldBe existingList.size + newPage.size
            combined.take(existingList.size) shouldBe existingList
            combined.drop(existingList.size) shouldBe newPage
        }
    }
})
```

**Property 5: Tag Search Query Construction**
```kotlin
class SearchPropertiesTest : StringSpec({
    "Property 5: Tag search query construction" {
        // Feature: anime-gallery-app, Property 5: Tag search query construction
        checkAll(100, Arb.list(Arb.string(1..20), 1..5)) { tags ->
            // Given a list of tags
            val query = tags.joinToString(" ")
            
            // Then query should be space-separated
            val reconstructed = query.split(" ")
            reconstructed shouldBe tags
            
            // And should be properly formatted for API
            query.contains("  ") shouldBe false // No double spaces
        }
    }
})
```

**Property 12: Favorite Persistence Round Trip**
```kotlin
class FavoritePropertiesTest : StringSpec({
    "Property 12: Favorite persistence round trip" {
        // Feature: anime-gallery-app, Property 12: Favorite persistence round trip
        checkAll(100, Arb.imageItem()) { image ->
            // Given a favorites repository
            val repository = FavoritesRepositoryImpl(favoriteDao)
            
            // When favoriting an image
            repository.addFavorite(image)
            val isFavorite = repository.isFavorite(image.id)
            
            // Then it should be in favorites
            isFavorite shouldBe true
            
            // When unfavoriting
            repository.removeFavorite(image.id)
            val isStillFavorite = repository.isFavorite(image.id)
            
            // Then it should not be in favorites
            isStillFavorite shouldBe false
        }
    }
})
```

**Property 20: NSFW Filter Persistence**
```kotlin
class PreferencesPropertiesTest : StringSpec({
    "Property 20: NSFW filter persistence" {
        // Feature: anime-gallery-app, Property 20: NSFW filter persistence
        checkAll(100, Arb.bool()) { filterEnabled ->
            // Given a preferences repository
            val repository = PreferencesRepositoryImpl(dataStore)
            
            // When setting NSFW filter
            repository.setNsfwFilterEnabled(filterEnabled)
            
            // Then it should persist and be retrievable
            val retrieved = repository.getNsfwFilterEnabled().first()
            retrieved shouldBe filterEnabled
        }
    }
})
```

**Property 32: JSON Parsing Round Trip**
```kotlin
class ApiPropertiesTest : StringSpec({
    "Property 32: JSON parsing round trip" {
        // Feature: anime-gallery-app, Property 32: JSON parsing round trip
        checkAll(100, Arb.imageDto()) { imageDto ->
            // Given a valid ImageDto
            val json = Json.encodeToString(imageDto)
            
            // When parsing back
            val parsed = Json.decodeFromString<ImageDto>(json)
            
            // Then essential fields should be preserved
            parsed.id shouldBe imageDto.id
            parsed.fileUrl shouldBe imageDto.fileUrl
            parsed.width shouldBe imageDto.width
            parsed.height shouldBe imageDto.height
            parsed.rating shouldBe imageDto.rating
            parsed.tags shouldBe imageDto.tags
        }
    }
})
```

### Custom Arbitraries for Property Tests

```kotlin
object ImageArbitraries {
    fun Arb.Companion.imageItem(): Arb<ImageItem> = arbitrary {
        ImageItem(
            id = Arb.string(1..10).bind(),
            thumbnailUrl = Arb.string(10..50).bind(),
            previewUrl = Arb.string(10..50).bind(),
            fileUrl = Arb.string(10..50).bind(),
            width = Arb.int(100..4000).bind(),
            height = Arb.int(100..4000).bind(),
            rating = Arb.enum<Rating>().bind(),
            tags = Arb.list(Arb.string(1..20), 0..10).bind()
        )
    }
    
    fun Arb.Companion.imageDto(): Arb<ImageDto> = arbitrary {
        ImageDto(
            id = Arb.string(1..10).bind(),
            previewUrl = Arb.string(10..50).bind(),
            sampleUrl = Arb.string(10..50).bind(),
            fileUrl = Arb.string(10..50).bind(),
            width = Arb.int(100..4000).bind(),
            height = Arb.int(100..4000).bind(),
            rating = Arb.element("safe", "questionable", "explicit", "s", "q", "e").bind(),
            tags = Arb.list(Arb.string(1..20), 0..10).bind().joinToString(" "),
            source = Arb.string(10..50).orNull().bind(),
            createdAt = Arb.string(10..20).orNull().bind()
        )
    }
}
```

### UI Testing

**Compose UI Tests:**
```kotlin
class HomeScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun imageGrid_displaysImages() {
        // Given
        val images = listOf(
            ImageItem("1", "url1", "url1", "url1", 100, 100, Rating.SAFE, emptyList()),
            ImageItem("2", "url2", "url2", "url2", 100, 100, Rating.SAFE, emptyList())
        )
        
        // When
        composeTestRule.setContent {
            HomeScreen(images = flowOf(PagingData.from(images)).collectAsLazyPagingItems())
        }
        
        // Then
        composeTestRule.onNodeWithTag("image_grid").assertExists()
        composeTestRule.onAllNodesWithTag("image_item").assertCountEquals(2)
    }
    
    @Test
    fun searchField_updatesQuery() {
        // When
        composeTestRule.setContent {
            HomeScreen()
        }
        
        // Then
        composeTestRule.onNodeWithTag("search_field").performTextInput("anime")
        composeTestRule.onNodeWithTag("search_field").assertTextContains("anime")
    }
}
```

### Integration Testing

**Database Integration:**
```kotlin
@RunWith(AndroidJUnit4::class)
class FavoriteDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var favoriteDao: FavoriteImageDao
    
    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        favoriteDao = database.favoriteImageDao()
    }
    
    @After
    fun teardown() {
        database.close()
    }
    
    @Test
    fun insertAndRetrieveFavorite() = runTest {
        // Given
        val favorite = FavoriteImageEntity(
            id = "123",
            thumbnailUrl = "url",
            previewUrl = "url",
            fileUrl = "url",
            width = 100,
            height = 100,
            rating = "safe",
            tags = "anime",
            timestamp = System.currentTimeMillis()
        )
        
        // When
        favoriteDao.insertFavorite(favorite)
        val favorites = favoriteDao.getAllFavorites().first()
        
        // Then
        assertEquals(1, favorites.size)
        assertEquals("123", favorites[0].id)
    }
}
```

### Test Coverage Goals

- **Unit Test Coverage:** Minimum 80% code coverage
- **Property Test Coverage:** All 37 correctness properties implemented
- **UI Test Coverage:** All major user flows covered
- **Integration Test Coverage:** All repository and database operations covered

### Continuous Integration

Tests should be run automatically on:
- Every pull request
- Every commit to main branch
- Nightly builds for extended property test runs (1000+ iterations)

Property-based tests with high iteration counts help discover edge cases that might not be caught with standard unit tests, providing confidence in the correctness of the implementation across a wide range of inputs.
