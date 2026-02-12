# Anime Gallery App

An open-source Android application for browsing anime images from the Gelbooru API. Built with Kotlin, Jetpack Compose, and MVVM architecture.

> ⚠️ **Content Warning**: This app displays anime artwork that may include NSFW content. Users must be 18+ to use this application.

## Features

- ✅ **Image Grid Display**: Browse anime images in a responsive 2-column grid with LazyVerticalGrid
- ✅ **Infinite Scrolling**: Seamless pagination using Paging 3 library
- ✅ **Tag-Based Search**: Search images using multiple space-separated tags
- ✅ **Tag Autocomplete**: Real-time tag suggestions while typing (debounced for performance)
- ✅ **Image Detail View**: View full resolution images with complete metadata
  - Image resolution and rating
  - Source URL
  - Clickable tags
  - Favorite and download actions
- ✅ **Fullscreen Viewer**: Immersive image viewing experience
  - Pinch-to-zoom (1x to 5x)
  - Pan when zoomed in
  - Double-tap to reset zoom
  - Tap to toggle controls
  - Zoom percentage indicator
- ✅ **Favorites System**: Save favorite images locally using Room database
  - Add/remove favorites with heart icon
  - Dedicated favorites screen
  - Persistent across app restarts
- ✅ **Image Downloads**: Download full resolution images to device storage
  - Progress indicator during download
  - Success/error notifications
  - MediaStore API for Android 10+
  - Legacy storage support for older versions
- ✅ **Dark Mode Support**: Automatic theme switching based on system settings
- ✅ **Offline Cache**: Efficient image caching with Coil
  - 512MB disk cache
  - 25% memory cache
  - LRU eviction policy
- ✅ **Shimmer Loading**: Beautiful loading placeholders
- ✅ **Error Handling**: User-friendly error messages with retry options
- ✅ **Rate Limiting**: Prevents API abuse (10 requests per second)
- ✅ **Firebase Authentication** (Optional): Sign in with email/password
  - Optional login (app works without account)
  - User profile screen
  - Cloud sync ready (favorites sync can be added)
  - Sign out functionality

## Screenshots

_Screenshots will be added here_

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material Design 3
- **Architecture**: MVVM (Model-View-ViewModel)
- **Dependency Injection**: Hilt
- **Networking**: Retrofit + OkHttp
- **Image Loading**: Coil
- **Pagination**: Paging 3
- **Local Storage**: Room Database
- **Authentication**: Firebase Auth (optional)
- **Async**: Kotlin Coroutines + Flow

## Project Structure

```
app/
├── data/                    # Data layer
│   ├── local/              # Room database, DAOs, entities
│   ├── remote/             # Retrofit API, DTOs, paging
│   ├── repository/         # Repository implementations
│   └── mapper/             # Data mappers
├── domain/                  # Domain layer
│   ├── model/              # Domain models
│   ├── repository/         # Repository interfaces
│   └── usecase/            # Use cases
├── presentation/            # Presentation layer
│   ├── auth/               # Login, register screens
│   ├── profile/            # Profile screen
│   ├── home/               # Home screen
│   ├── detail/             # Detail screen
│   ├── favorites/          # Favorites screen
│   ├── fullscreen/         # Fullscreen viewer
│   ├── navigation/         # Navigation setup
│   └── theme/              # Theme configuration
├── di/                      # Dependency injection modules
└── util/                    # Utility classes
```

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material Design 3
- **Architecture**: MVVM (Model-View-ViewModel)
- **Dependency Injection**: Hilt
- **Networking**: Retrofit + OkHttp
- **Image Loading**: Coil
- **Pagination**: Paging 3
- **Local Storage**: Room Database + DataStore
- **Testing**: Kotest (Property-Based Testing), JUnit, MockK

## Project Structure

```
app/
├── data/                    # Data layer
│   ├── local/              # Room database, DAOs, entities
│   ├── remote/             # Retrofit API, DTOs
│   └── repository/         # Repository implementations
├── domain/                  # Domain layer
│   ├── model/              # Domain models
│   ├── repository/         # Repository interfaces
│   └── usecase/            # Use cases
├── presentation/            # Presentation layer
│   ├── home/               # Home screen
│   ├── detail/             # Detail screen
│   ├── favorites/          # Favorites screen
│   ├── fullscreen/         # Fullscreen viewer
│   ├── agegate/            # Age verification
│   └── theme/              # Theme configuration
└── di/                      # Dependency injection modules
```

## Setup Instructions

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK 34
- Minimum SDK 24 (Android 7.0)

### Building the Project

1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle files
4. Run the app on an emulator or physical device

### Firebase Setup (Optional)

The app includes optional Firebase Authentication. To enable it:

1. Create a Firebase project at [Firebase Console](https://console.firebase.google.com/)
2. Add an Android app to your Firebase project
3. Download the `google-services.json` file
4. Replace `app/google-services.json` with your downloaded file
5. Enable Email/Password authentication in Firebase Console

For detailed instructions, see [FIREBASE_SETUP.md](FIREBASE_SETUP.md)

**Note**: The app works fully offline without Firebase. Authentication is optional for cloud sync features.

### API Configuration

The app uses the Gelbooru public API:
- Base URL: `https://gelbooru.com/`
- No API key required for basic usage
- Rate limiting is implemented to respect API policies

## Architecture

The app follows Clean Architecture principles with three distinct layers:

### Presentation Layer
- Jetpack Compose UI components
- ViewModels for state management
- Navigation using Navigation Compose

### Domain Layer
- Business logic encapsulated in use cases
- Domain models (pure Kotlin classes)
- Repository interfaces

### Data Layer
- Repository implementations
- Remote data source (Retrofit)
- Local data source (Room)
- Data mappers

## Testing

The project includes comprehensive testing:

- **Unit Tests**: Specific examples and edge cases
- **Property-Based Tests**: Universal correctness properties (100+ iterations)
- **Integration Tests**: End-to-end user flows
- **UI Tests**: Compose UI testing

Run tests:
```bash
./gradlew test           # Unit tests
./gradlew connectedTest  # Instrumented tests
```

## Building the Project

### Generate Release APK

```bash
./gradlew assembleRelease
```

The APK will be generated at: `app/build/outputs/apk/release/app-release.apk`

### Install on Device

```bash
adb install app/build/outputs/apk/release/app-release.apk
```

## Disclaimer

This is an open-source project for educational purposes. The app uses the public Gelbooru API and displays user-generated content. The developers are not responsible for the content displayed through this app.

- This app is NOT affiliated with Gelbooru
- Content is provided by third-party APIs
- Users must be 18+ to use this application
- This app is NOT published on Google Play Store

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is open-source and available under the MIT License.

## Acknowledgments

- Images provided by [Gelbooru API](https://gelbooru.com/)
- Built with [Jetpack Compose](https://developer.android.com/jetpack/compose)
- Material Design 3 by Google
- [Coil](https://coil-kt.github.io/coil/) for image loading
- [Retrofit](https://square.github.io/retrofit/) for networking
