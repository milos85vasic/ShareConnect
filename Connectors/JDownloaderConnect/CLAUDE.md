# CLAUDE.md - JDownloaderConnect

This file provides guidance for working with the JDownloaderConnect Android application.

## Project Overview

**JDownloaderConnect** - An Android application that provides full integration with MyJDownloader service, allowing users to manage remote JDownloader instances with modern enterprise UI/UX.

**Package**: `com.shareconnect.jdownloaderconnect`
**Version**: 1.0.0
**Target**: Android API 24-36

## Build Commands

### Building
```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Clean build
./gradlew clean
```

APK location: `JDownloaderConnector/build/outputs/apk/debug/JDownloaderConnector-debug.apk`

### Testing
```bash
# Run all tests
./gradlew test connectedAndroidTest

# Unit tests only
./gradlew test

# Instrumentation tests
./gradlew connectedAndroidTest

# Single unit test
./gradlew test --tests "com.shareconnect.jdownloaderconnect.repository.JDownloaderRepositoryTest"

# Single instrumentation test
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.shareconnect.jdownloaderconnect.integration.JDownloaderIntegrationTest
```

## Architecture

### Multi-Layer Architecture

#### UI Layer (Jetpack Compose)
- **MainActivity**: Primary app entry point
- **Onboarding**: Account setup flow
- **ViewModels**: State management with LiveData
- **Theme**: Material Design 3 theming

#### Repository Layer
- **JDownloaderRepository**: Main data repository
- **ServerRepository**: Server profile management
- **Flow/LiveData**: Reactive data streams

#### Data Layer
- **Room Database**: Local storage with SQLCipher encryption
- **MyJDownloader API**: Remote API integration
- **Retrofit/OkHttp**: Network communication

#### Sync Layer
- **JDownloaderSyncManager**: Real-time sync with other apps
- **Asinka Integration**: Cross-app data synchronization

### Key Components

#### Data Models
- `JDownloaderAccount`: MyJDownloader account information
- `DownloadPackage`: Download package with metadata
- `DownloadLink`: Individual download links
- `LinkGrabberPackage`: Link grabber packages
- `LinkGrabberLink`: Link grabber links
- `ServerProfile`: Server configuration

#### Database
- **Encryption**: SQLCipher for all local data
- **Migrations**: Automatic Room migrations
- **Type Converters**: Custom type conversion for complex types

#### Networking
- **Retrofit**: Type-safe HTTP client
- **OkHttp**: Underlying HTTP engine with logging
- **Gson**: JSON serialization/deserialization

## Development Guidelines

### Code Style
- **Kotlin First**: Prefer Kotlin over Java
- **Coroutines**: Use coroutines for async operations
- **Flow/LiveData**: Reactive programming patterns
- **Immutable Data**: Prefer val over var, immutable collections

### Dependency Injection
- **Manual DI**: No external DI frameworks
- **AppContainer**: Central dependency container
- **ViewModel Factory**: Custom ViewModel providers

### Testing Strategy
- **Unit Tests**: Business logic and data operations
- **Integration Tests**: Database and API integration
- **Automation Tests**: UI testing with Compose
- **E2E Tests**: Complete user flows

## MyJDownloader API Integration

### Authentication Flow
1. User enters email/password in onboarding
2. App calls `/my/connect` endpoint
3. Receives session token and device ID
4. Stores encrypted credentials locally
5. Uses token for subsequent API calls

### Key Endpoints
- `/my/connect` - Account authentication
- `/downloads/queryPackages` - Get download packages
- `/downloads/queryLinks` - Get package links
- `/downloads/addLinks` - Add new downloads
- `/linkgrabberv2/addLinks` - Add to link grabber
- `/linkgrabberv2/queryLinks` - Get link grabber content

### Error Handling
- **Network Errors**: Retry with exponential backoff
- **Authentication Errors**: Re-authenticate automatically
- **API Limits**: Respect rate limiting
- **Offline Mode**: Graceful degradation

## Sync Integration

### Port Configuration
- **Base Port**: 8970
- **Port Calculation**: `basePort + Math.abs(appId.hashCode() % 100)`
- **Conflict Resolution**: Automatic port finding

### Sync Objects
- **Profiles**: JDownloader account synchronization
- **Themes**: UI theme preferences
- **History**: Download history
- **Preferences**: App settings
- **Languages**: Language preferences

## Security Considerations

### Data Protection
- **SQLCipher**: All local data encrypted
- **Secure Storage**: Encrypted credential storage
- **HTTPS**: All network communication encrypted
- **Input Validation**: Comprehensive input sanitization

### Authentication Security
- **Token Management**: Secure token storage and rotation
- **Session Timeout**: Automatic session management
- **Credential Encryption**: Passwords encrypted at rest

## Performance Optimization

### Database Optimization
- **Indexing**: Proper database indexing
- **Batch Operations**: Bulk database operations
- **Query Optimization**: Efficient Room queries

### Network Optimization
- **Caching**: Smart response caching
- **Compression**: Gzip compression for large responses
- **Connection Pooling**: Efficient connection reuse

### UI Performance
- **Lazy Loading**: Compose lazy column for lists
- **Image Caching**: Coil with memory and disk cache
- **State Management**: Efficient state updates

## Common Patterns

### Repository Pattern
```kotlin
class JDownloaderRepository {
    suspend fun connect(email: String, password: String)
    fun getActiveAccount(): Flow<JDownloaderAccount?>
    suspend fun addDownloadPackage(package: DownloadPackage, links: List<DownloadLink>)
}
```

### ViewModel Pattern
```kotlin
class MainViewModel : ViewModel() {
    val activeAccount: Flow<JDownloaderAccount?> = repository.getActiveAccount()
    
    fun connectAccount(email: String, password: String) {
        viewModelScope.launch {
            repository.connect(email, password)
        }
    }
}
```

### Sync Manager Pattern
```kotlin
class JDownloaderSyncManager : AsinkaClient {
    override fun startSync() {
        // Start synchronization
    }
    
    override suspend fun onObjectReceived(syncableObject: SyncableObject) {
        // Handle incoming sync objects
    }
}
```

## Troubleshooting

### Common Issues

#### Build Issues
- **Kotlin Version**: Ensure Kotlin 2.0.0
- **Room KSP**: Use KSP 2.0.0-1.0.21
- **Dependency Conflicts**: Check force resolutions in build.gradle

#### Runtime Issues
- **Port Conflicts**: Sync manager port allocation
- **Database Encryption**: SQLCipher initialization
- **Network Timeouts**: Configure appropriate timeouts

#### Testing Issues
- **Mocking**: Use MockK for Kotlin mocking
- **Coroutine Testing**: Use runTest for coroutine tests
- **Compose Testing**: Use createComposeRule for UI tests

### Debugging

#### Logging
- **Network**: OkHttp logging interceptor
- **Database**: Room database callbacks
- **Sync**: Asinka sync manager logs

#### Testing
- **Unit Tests**: Run with `./gradlew test`
- **Integration Tests**: Run with `./gradlew connectedAndroidTest`
- **Coverage**: Generate reports with `./gradlew jacocoTestReport`

## Version Compatibility

### Dependencies
- **Kotlin**: 2.0.0
- **Android Gradle Plugin**: 8.9.1
- **Compile SDK**: 36
- **Min SDK**: 24
- **Target SDK**: 36

### Java Version
- **Source Compatibility**: Java 17
- **Target Compatibility**: Java 17

## Additional Resources

### Documentation
- [MyJDownloader API Documentation](https://my.jdownloader.org/developers/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Room Database](https://developer.android.com/training/data-storage/room)
- [Retrofit](https://square.github.io/retrofit/)

### Testing Resources
- [MockK](https://mockk.io/)
- [Compose Testing](https://developer.android.com/jetpack/compose/testing)
- [Espresso](https://developer.android.com/training/testing/espresso)

### Security Resources
- [SQLCipher](https://www.zetetic.net/sqlcipher/)
- [Android Security](https://developer.android.com/topic/security)