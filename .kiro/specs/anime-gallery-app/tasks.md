# Implementation Plan: Anime Gallery App

## Overview

This implementation plan breaks down the Android anime gallery app into discrete, incremental coding tasks. Each task builds on previous work, starting with project setup and core architecture, then implementing features layer by layer (data → domain → presentation), and finally integrating everything together. The plan follows MVVM architecture with clean separation of concerns.

## Tasks

- [x] 1. Project setup and core architecture
  - Create new Android Studio project with Kotlin and Jetpack Compose
  - Configure build.gradle with all required dependencies (Retrofit, Room, Coil, Paging 3, Kotest)
  - Set up project structure with data, domain, and presentation packages
  - Configure dependency injection framework (Hilt/Koin)
  - Set up Material Design 3 theme with dark/light mode support
  - _Requirements: 17.1, 17.2, 17.3, 15.1, 11.1, 11.2_

- [ ] 2. Data layer - API integration
  - [x] 2.1 Create data models and DTOs
    - Define ImageDto, GelbooruResponse, TagSuggestionsResponse with kotlinx.serialization annotations
    - Define FavoriteImageEntity and DownloadedImageEntity for Room database
    - Implement mapper functions (ImageDto.toDomainModel(), toEntity(), etc.)
    - _Requirements: 14.3_
  
  - [x] 2.2 Write property test for JSON parsing round trip
    - **Property 32: JSON Parsing Round Trip**
    - **Validates: Requirements 14.3**
  
  - [x] 2.3 Write unit tests for mapper functions
    - Test ImageDto to ImageItem mapping
    - Test rating string to Rating enum conversion
    - Test tag string splitting
    - _Requirements: 14.3_
  
  - [-] 2.4 Create Gelbooru API service interface
    - Define GelbooruApiService with Retrofit annotations
    - Implement getImages() endpoint with query parameters
    - Implement getTagSuggestions() endpoint
    - Configure Retrofit with OkHttp client, JSON converter, and base URL
    - _Requirements: 14.1, 14.2_
  
  - [x] 2.5 Write property test for tag parameter encoding
    - **Property 31: Tag Parameter Encoding**
    - **Validates: Requirements 14.2**
  
  - [x] 2.6 Write property test for API endpoint usage
    - **Property 30: API Endpoint Usage**
    - **Validates: Requirements 14.1**

- [ ] 3. Data layer - local storage
  - [x] 3.1 Create Room database and DAOs
    - Define AppDatabase with FavoriteImageEntity and DownloadedImageEntity
    - Implement FavoriteImageDao with insert, delete, query, and exists operations
    - Implement DownloadedImageDao with insert and exists operations
    - Configure Room database builder with migrations strategy
    - _Requirements: 7.1, 7.3, 7.4, 8.1_
  
  - [x] 3.2 Write integration tests for database operations
    - Test favorite insert and retrieve
    - Test favorite delete
    - Test isFavorite query
    - Test downloaded image tracking
    - _Requirements: 7.1, 7.3, 7.4_
  
  - [ ] 3.3 Create preferences data store
    - Define PreferencesDataStore for NSFW filter and age confirmation settings
    - Implement read and write operations using DataStore
    - _Requirements: 9.4, 9.5, 10.4, 10.5_
  
  - [ ] 3.4 Write property test for NSFW filter persistence
    - **Property 20: NSFW Filter Persistence**
    - **Validates: Requirements 9.4, 9.5**

- [ ] 4. Data layer - repositories
  - [x] 4.1 Implement ImagePagingSource
    - Create ImagePagingSource extending PagingSource
    - Implement load() method with API calls and error handling
    - Handle NSFW filtering in query construction
    - Implement getRefreshKey() for refresh logic
    - _Requirements: 2.1, 2.2, 9.1, 9.2_
  
  - [ ] 4.2 Write property test for pagination appends images
    - **Property 3: Pagination Appends Images**
    - **Validates: Requirements 2.2**
  
  - [x] 4.3 Implement ImageRepositoryImpl
    - Create ImageRepositoryImpl implementing ImageRepository interface
    - Implement getImages() using Pager with ImagePagingSource
    - Implement getImageDetail() with API call and error handling
    - Add image caching logic using Coil's disk cache
    - _Requirements: 2.1, 5.2, 12.1, 12.2_
  
  - [ ] 4.4 Write property test for image cache storage
    - **Property 25: Image Cache Storage**
    - **Validates: Requirements 12.1, 12.2**
  
  - [ ] 4.5 Implement FavoritesRepositoryImpl
    - Create FavoritesRepositoryImpl implementing FavoritesRepository interface
    - Implement addFavorite() with database insert
    - Implement removeFavorite() with database delete
    - Implement isFavorite() query
    - Implement getAllFavorites() returning Flow
    - _Requirements: 7.1, 7.3, 7.4_
  
  - [ ] 4.6 Write property test for favorite persistence round trip
    - **Property 12: Favorite Persistence Round Trip**
    - **Validates: Requirements 7.1, 7.3, 7.4**
  
  - [x] 4.7 Implement DownloadRepositoryImpl
    - Create DownloadRepositoryImpl implementing DownloadRepository interface
    - Implement downloadImage() with OkHttp download and progress tracking
    - Save images to device storage using MediaStore API
    - Implement isDownloaded() query
    - _Requirements: 8.1, 8.2_
  
  - [ ] 4.8 Write property test for image download to storage
    - **Property 15: Image Download to Storage**
    - **Validates: Requirements 8.1**
  
  - [ ] 4.9 Implement PreferencesRepositoryImpl
    - Create PreferencesRepositoryImpl implementing PreferencesRepository interface
    - Implement NSFW filter get/set operations
    - Implement age confirmation get/set operations
    - _Requirements: 9.4, 9.5, 10.4, 10.5_

- [ ] 5. Checkpoint - Data layer complete
  - Ensure all data layer tests pass
  - Verify API integration works with real Gelbooru API
  - Verify database operations work correctly
  - Ask the user if questions arise

- [ ] 6. Domain layer - use cases
  - [x] 6.1 Create GetImagesUseCase
    - Implement GetImagesUseCase with tags and NSFW filter parameters
    - Call ImageRepository.getImages() and return Flow<PagingData<ImageItem>>
    - _Requirements: 2.1, 3.1, 9.1, 9.2_
  
  - [ ] 6.2 Write property test for tag search query construction
    - **Property 5: Tag Search Query Construction**
    - **Validates: Requirements 3.1, 3.2, 3.3**
  
  - [ ] 6.3 Write property test for NSFW filter query construction
    - **Property 18: NSFW Filter Query Construction**
    - **Validates: Requirements 9.1, 9.2**
  
  - [x] 6.4 Create GetImageDetailUseCase
    - Implement GetImageDetailUseCase with imageId parameter
    - Call ImageRepository.getImageDetail() and return Result<ImageDetail>
    - _Requirements: 5.2, 5.3, 5.4, 5.5_
  
  - [x] 6.5 Create ToggleFavoriteUseCase
    - Implement ToggleFavoriteUseCase with image parameter
    - Check current favorite status and toggle (add or remove)
    - Return Result<Boolean> indicating new favorite status
    - _Requirements: 7.1, 7.4_
  
  - [x] 6.6 Create DownloadImageUseCase
    - Implement DownloadImageUseCase with imageUrl and imageId parameters
    - Call DownloadRepository.downloadImage() and return Flow<DownloadProgress>
    - _Requirements: 8.1, 8.2, 8.3, 8.4_
  
  - [ ] 6.7 Create GetTagSuggestionsUseCase
    - Implement GetTagSuggestionsUseCase with query parameter
    - Call TagRepository.getTagSuggestions() and return Result<List<String>>
    - _Requirements: 4.1, 4.3_
  
  - [x] 6.8 Create CheckFavoriteStatusUseCase
    - Implement CheckFavoriteStatusUseCase with imageId parameter
    - Call FavoritesRepository.isFavorite() and return Boolean
    - _Requirements: 7.2_

- [ ] 7. Presentation layer - ViewModels
  - [x] 7.1 Create HomeViewModel
    - Define HomeUiState data class with images, loading, error, searchQuery, tagSuggestions, nsfwFilterEnabled
    - Implement StateFlow<HomeUiState> for UI state management
    - Implement onSearchQueryChanged() to update query and trigger search
    - Implement onTagSelected() to add tag to search
    - Implement onNsfwFilterToggled() to toggle filter and refresh
    - Implement onRetry() for error recovery
    - Inject GetImagesUseCase, GetTagSuggestionsUseCase, PreferencesRepository
    - _Requirements: 1.1, 1.4, 1.5, 3.1, 3.2, 3.3, 4.1, 9.3_
  
  - [ ] 7.2 Write unit tests for HomeViewModel
    - Test search query changes trigger API calls
    - Test NSFW filter toggle updates preference and refreshes
    - Test tag suggestions update on input
    - Test error state and retry logic
    - _Requirements: 3.1, 9.3_
  
  - [ ] 7.3 Write property test for filter change triggers refresh
    - **Property 19: Filter Change Triggers Refresh**
    - **Validates: Requirements 9.3**
  
  - [x] 7.4 Create DetailViewModel
    - Define DetailUiState data class with image, loading, error, isFavorite, isDownloaded, downloadProgress
    - Implement StateFlow<DetailUiState> for UI state management
    - Implement loadImageDetail() to fetch image details
    - Implement onFavoriteToggled() to toggle favorite status
    - Implement onDownloadClicked() to start download
    - Inject GetImageDetailUseCase, ToggleFavoriteUseCase, DownloadImageUseCase, CheckFavoriteStatusUseCase
    - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5, 7.1, 7.2, 8.1, 8.2_
  
  - [ ] 7.5 Write unit tests for DetailViewModel
    - Test image detail loading
    - Test favorite toggle updates state
    - Test download progress tracking
    - Test error handling
    - _Requirements: 5.2, 7.1, 8.1_
  
  - [ ] 7.6 Write property test for favorite icon state sync
    - **Property 13: Favorite Icon State Sync**
    - **Validates: Requirements 7.2**
  
  - [ ] 7.7 Write property test for download status indication
    - **Property 16: Download Status Indication**
    - **Validates: Requirements 8.2, 8.3, 8.4**
  
  - [x] 7.8 Create FavoritesViewModel
    - Define FavoritesUiState data class with favorites, loading, isEmpty
    - Implement StateFlow<FavoritesUiState> for UI state management
    - Load favorites from FavoritesRepository on init
    - Inject FavoritesRepository
    - _Requirements: 7.3_
  
  - [ ] 7.9 Write unit tests for FavoritesViewModel
    - Test favorites loading from repository
    - Test empty state handling
    - _Requirements: 7.3_

- [ ] 8. Checkpoint - Domain and ViewModels complete
  - Ensure all use case tests pass
  - Ensure all ViewModel tests pass
  - Verify state management works correctly
  - Ask the user if questions arise

- [ ] 9. Presentation layer - UI components
  - [x] 9.1 Create HomeScreen composable
    - Implement LazyVerticalGrid for image display
    - Implement search field with tag input
    - Implement NSFW filter toggle
    - Display loading indicators during image loading
    - Display error view with retry button on errors
    - Display empty state when no images
    - Handle image tap navigation to detail screen
    - Collect LazyPagingItems from ViewModel
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 3.1, 5.1, 9.3_
  
  - [ ] 9.2 Write property test for image tap navigation
    - **Property 8: Image Tap Navigation**
    - **Validates: Requirements 5.1**
  
  - [ ] 9.3 Write property test for error display with retry
    - **Property 2: Error Display with Retry**
    - **Validates: Requirements 1.5**
  
  - [ ] 9.3 Create ImageGridItem composable
    - Display image thumbnail using Coil AsyncImage
    - Handle loading and error states for individual images
    - Apply consistent aspect ratio
    - Add click handler for navigation
    - _Requirements: 1.2, 5.1_
  
  - [ ] 9.4 Write property test for image grid aspect ratio consistency
    - **Property 1: Image Grid Aspect Ratio Consistency**
    - **Validates: Requirements 1.2**
  
  - [ ] 9.5 Create SearchBar composable with tag autocomplete
    - Implement TextField for tag input
    - Display tag suggestions dropdown
    - Handle tag selection from suggestions
    - Handle multiple tags with space separation
    - _Requirements: 3.1, 3.2, 4.1, 4.2, 4.3_
  
  - [ ] 9.6 Write property test for tag autocomplete suggestions
    - **Property 6: Tag Autocomplete Suggestions**
    - **Validates: Requirements 4.1, 4.3**
  
  - [ ] 9.7 Write property test for tag selection adds to search
    - **Property 7: Tag Selection Adds to Search**
    - **Validates: Requirements 4.2**
  
  - [x] 9.8 Create DetailScreen composable
    - Display full resolution image using Coil AsyncImage
    - Display image metadata (resolution, rating, source)
    - Display tags as chips
    - Implement favorite button with state indicator
    - Implement download button with progress indicator
    - Handle fullscreen image tap
    - _Requirements: 5.2, 5.3, 5.4, 5.5, 6.1, 7.1, 7.2, 8.1, 8.2_
  
  - [ ] 9.9 Write property test for detail screen information display
    - **Property 9: Detail Screen Information Display**
    - **Validates: Requirements 5.2, 5.3, 5.4, 5.5**
  
  - [ ] 9.10 Write property test for fullscreen mode activation
    - **Property 10: Fullscreen Mode Activation**
    - **Validates: Requirements 6.1**
  
  - [ ] 9.11 Create FullscreenViewer composable
    - Implement fullscreen image display with zoom support
    - Implement horizontal pager for swipe navigation
    - Implement tap to toggle controls visibility
    - Add back navigation to detail screen
    - _Requirements: 6.1, 6.2, 6.3, 6.4, 6.5_
  
  - [ ] 9.12 Write property test for fullscreen controls toggle
    - **Property 11: Fullscreen Controls Toggle**
    - **Validates: Requirements 6.4**
  
  - [x] 9.13 Create FavoritesScreen composable
    - Display favorites in LazyVerticalGrid
    - Handle empty favorites state
    - Handle image tap navigation to detail screen
    - Load images from cache when available
    - _Requirements: 7.3, 7.5_
  
  - [ ] 9.14 Write property test for favorite cache loading
    - **Property 14: Favorite Cache Loading**
    - **Validates: Requirements 7.5**
  
  - [ ] 9.15 Create AgeGateScreen composable
    - Display age confirmation message
    - Implement "I am 18+" confirmation button
    - Implement "Exit" decline button
    - Handle confirmation to save preference and proceed
    - Handle decline to close app
    - _Requirements: 10.1, 10.2, 10.3_
  
  - [ ] 9.16 Write unit test for age gate first launch
    - Test age gate displays on first launch
    - Test confirmation grants access
    - Test decline closes app
    - _Requirements: 10.1, 10.2, 10.3_
  
  - [ ] 9.17 Write property test for age gate single display
    - **Property 22: Age Gate Single Display**
    - **Validates: Requirements 10.4, 10.5**

- [ ] 10. Presentation layer - error handling and states
  - [ ] 10.1 Create ErrorView composable
    - Display error icon and message
    - Display retry button
    - Handle different error types (network, rate limit, offline)
    - _Requirements: 13.1, 13.2, 13.3, 13.4_
  
  - [ ] 10.2 Write property test for error message display
    - **Property 28: Error Message Display**
    - **Validates: Requirements 13.1, 13.2, 13.3, 13.4**
  
  - [ ] 10.3 Create LoadingView composable
    - Display circular progress indicator
    - Display loading message
    - _Requirements: 1.4_
  
  - [ ] 10.4 Create EmptyStateView composable
    - Display empty state icon and message
    - Handle different empty states (no images, no results, no favorites)
    - _Requirements: 1.3, 3.4_
  
  - [x] 10.5 Implement NetworkErrorHandler
    - Create error handling utility for network errors
    - Map exceptions to user-friendly messages
    - Handle rate limiting with retry timing
    - _Requirements: 13.1, 13.2_
  
  - [ ] 10.6 Implement error logging
    - Add logging for all error cases
    - Include error details for debugging
    - _Requirements: 13.5_
  
  - [ ] 10.7 Write property test for error logging
    - **Property 29: Error Logging**
    - **Validates: Requirements 13.5**

- [ ] 11. Checkpoint - UI components complete
  - Ensure all UI components render correctly
  - Test navigation between screens
  - Test error states and loading states
  - Ask the user if questions arise

- [ ] 12. Navigation and app structure
  - [x] 12.1 Set up Navigation Compose
    - Define navigation routes for all screens
    - Implement NavHost with route definitions
    - Set up navigation arguments for detail screen
    - Handle back navigation
    - _Requirements: 5.1, 6.5_
  
  - [x] 12.2 Create MainActivity
    - Set up Compose theme with Material Design 3
    - Implement dark/light theme switching based on system settings
    - Set up navigation container
    - Handle age gate on first launch
    - _Requirements: 10.1, 11.1, 11.2, 11.3_
  
  - [ ] 12.3 Write property test for theme application
    - **Property 23: Theme Application**
    - **Validates: Requirements 11.1, 11.2, 11.3**
  
  - [ ] 12.4 Create bottom navigation bar
    - Add navigation items for Home, Favorites
    - Handle navigation between main screens
    - Highlight current screen
    - _Requirements: 7.3_

- [ ] 13. Advanced features - caching and performance
  - [ ] 13.1 Configure Coil image loading
    - Set up Coil ImageLoader with disk cache
    - Configure cache size limits
    - Implement LRU eviction policy
    - Add memory cache configuration
    - _Requirements: 12.1, 12.2, 12.3, 16.1_
  
  - [ ] 13.2 Write property test for LRU cache eviction
    - **Property 26: LRU Cache Eviction**
    - **Validates: Requirements 12.3**
  
  - [ ] 13.3 Implement manual cache clearing
    - Add settings option to clear image cache
    - Implement cache clear functionality
    - Show confirmation dialog
    - _Requirements: 12.5_
  
  - [ ] 13.4 Write property test for manual cache clear
    - **Property 27: Manual Cache Clear**
    - **Validates: Requirements 12.5**
  
  - [ ] 13.5 Implement image load prioritization
    - Configure Coil to prioritize visible images
    - Implement lazy loading for off-screen images
    - _Requirements: 16.3, 16.5_
  
  - [ ] 13.6 Write property test for visible image load priority
    - **Property 36: Visible Image Load Priority**
    - **Validates: Requirements 16.3**
  
  - [ ] 13.7 Write property test for lazy loading on viewport entry
    - **Property 37: Lazy Loading on Viewport Entry**
    - **Validates: Requirements 16.5**

- [ ] 14. Advanced features - API rate limiting and retry
  - [x] 14.1 Implement RateLimiter
    - Create rate limiting mechanism for API requests
    - Track request timestamps
    - Throttle requests to respect API limits
    - _Requirements: 14.5_
  
  - [ ] 14.2 Write property test for API rate limiting
    - **Property 34: API Rate Limiting**
    - **Validates: Requirements 14.5**
  
  - [ ] 14.3 Implement RetryPolicy with exponential backoff
    - Create retry mechanism for failed requests
    - Implement exponential backoff strategy
    - Configure max retries and delays
    - _Requirements: 13.1, 13.2_
  
  - [ ] 14.4 Implement API error handling
    - Handle different HTTP error codes
    - Map API errors to user-friendly messages
    - Handle malformed responses gracefully
    - _Requirements: 14.4_
  
  - [ ] 14.5 Write property test for API error handling
    - **Property 33: API Error Handling**
    - **Validates: Requirements 14.4**

- [ ] 15. Accessibility and polish
  - [ ] 15.1 Implement accessibility features
    - Add content descriptions to all images and icons
    - Ensure minimum touch target sizes (48dp × 48dp)
    - Add semantic labels for screen readers
    - Test with TalkBack
    - _Requirements: 15.4_
  
  - [ ] 15.2 Write property test for touch target minimum size
    - **Property 35: Touch Target Minimum Size**
    - **Validates: Requirements 15.4**
  
  - [ ] 15.3 Implement theme contrast validation
    - Ensure text contrast ratios meet WCAG standards (4.5:1 minimum)
    - Test both light and dark themes
    - Adjust colors if needed
    - _Requirements: 11.5_
  
  - [ ] 15.4 Write property test for theme contrast requirements
    - **Property 24: Theme Contrast Requirements**
    - **Validates: Requirements 11.5**
  
  - [ ] 15.5 Add loading animations and transitions
    - Implement smooth screen transitions
    - Add shimmer effect for loading images
    - Add fade-in animations for loaded images
    - _Requirements: 15.5_
  
  - [ ] 15.6 Implement pagination loading indicators
    - Add loading indicator at bottom during pagination
    - Add error indicator for pagination failures
    - _Requirements: 2.1, 2.4_
  
  - [ ] 15.7 Write property test for pagination error indicator
    - **Property 4: Pagination Error Indicator**
    - **Validates: Requirements 2.4**

- [ ] 16. Final integration and testing
  - [ ] 16.1 Wire all components together
    - Ensure dependency injection is properly configured
    - Verify all ViewModels receive correct dependencies
    - Test end-to-end flows (search, favorite, download)
    - _Requirements: 17.3_
  
  - [ ] 16.2 Write integration tests for main user flows
    - Test search to detail to favorite flow
    - Test download flow
    - Test NSFW filter flow
    - _Requirements: 3.1, 7.1, 8.1, 9.3_
  
  - [ ] 16.3 Create app icon and splash screen
    - Design app icon following Material Design guidelines
    - Implement splash screen with age gate check
    - _Requirements: 10.1_
  
  - [ ] 16.4 Add ProGuard rules for release build
    - Configure ProGuard for code obfuscation
    - Add keep rules for data models and API interfaces
    - Test release build
    - _Requirements: 17.5_
  
  - [ ] 16.5 Create README with setup instructions
    - Document project structure
    - Add setup and build instructions
    - Document API configuration
    - Add screenshots
    - _Requirements: 17.1, 17.2_

- [ ] 17. Final checkpoint - Complete app
  - Run all tests (unit, property, integration, UI)
  - Verify all features work correctly
  - Test on multiple devices and Android versions
  - Verify Play Store compliance (age gate, content warnings)
  - Ask the user if questions arise

## Notes

- Each task references specific requirements for traceability
- Checkpoints ensure incremental validation at major milestones
- Property tests validate universal correctness properties with minimum 100 iterations
- Unit tests validate specific examples and edge cases
- The implementation follows MVVM architecture with clean separation of concerns
- All property tests should be tagged with format: `Feature: anime-gallery-app, Property {number}: {property_text}`
