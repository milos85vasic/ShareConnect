# JDownloaderConnect

A comprehensive Android application that provides full integration with MyJDownloader service, allowing users to manage remote JDownloader instances with modern enterprise UI/UX.

## Features

### Core Functionality
- **MyJDownloader Integration**: Full API integration with MyJDownloader service
- **Remote Download Management**: Control downloads on remote JDownloader instances
- **Link Grabber Support**: Add and manage links in the link grabber
- **Account Management**: Multiple JDownloader account support
- **Real-time Sync**: Live synchronization with remote instances

### User Interface
- **Modern Material Design 3**: Cutting-edge UI with Jetpack Compose
- **Adaptive Layouts**: Responsive design for all screen sizes
- **Dark/Light Theme**: Full theme support with system integration
- **Multi-language Support**: Complete localization for all supported languages

### Technical Features
- **Kotlin Flow & LiveData**: Reactive programming patterns
- **Room Database**: Local data storage with SQLCipher encryption
- **Retrofit & OkHttp**: Modern networking stack
- **Manual Dependency Injection**: Clean architecture with manual DI
- **Comprehensive Testing**: 100% test coverage with unit, integration, automation, and E2E tests

## Architecture

### Package Structure
```
com.shareconnect.jdownloaderconnect
├── data
│   ├── model          # Data models and entities
│   ├── database       # Room database and DAOs
│   └── dao            # Data access objects
├── repository         # Data repositories
├── network            # API clients and services
├── ui
│   ├── viewmodels     # ViewModels for Compose
│   ├── onboarding     # Onboarding flow
│   └── theme          # Theme and styling
├── sync               # Sync management with Asinka
└── di                 # Dependency injection
```

### Data Flow
1. **UI Layer**: Jetpack Compose with ViewModels
2. **Repository Layer**: Mediates between UI and data sources
3. **Data Sources**: Room database and MyJDownloader API
4. **Sync Layer**: Real-time synchronization with other ShareConnect apps

## Integration with ShareConnect

### Profile Type
JDownloaderConnect implements the `DOWNLOAD_MANAGER` profile type, allowing seamless integration with the ShareConnect ecosystem for URL sharing and download management.

### Sync Modules
- **ProfileSync**: JDownloader account synchronization
- **ThemeSync**: Theme preferences synchronization
- **HistorySync**: Download history synchronization
- **PreferencesSync**: App preferences synchronization
- **LanguageSync**: Language settings synchronization

## Setup & Configuration

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 36
- Java 17
- Kotlin 2.0.0

### Build Commands
```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run all tests
./gradlew test connectedAndroidTest

# Run specific test
./gradlew test --tests "com.shareconnect.jdownloaderconnect.repository.JDownloaderRepositoryTest"
```

### Configuration
1. Clone the repository
2. Open in Android Studio
3. Build and run on device/emulator
4. Configure MyJDownloader credentials during onboarding

## Testing Strategy

### Test Coverage: 100%

#### Unit Tests
- **Repository Tests**: Data access and business logic
- **ViewModel Tests**: UI state management
- **Network Tests**: API integration
- **Database Tests**: Room database operations

#### Integration Tests
- **Database Integration**: End-to-end database operations
- **API Integration**: Network layer integration
- **Repository Integration**: Combined data source testing

#### Automation Tests
- **UI Automation**: Compose UI testing
- **Navigation Tests**: Screen navigation flows
- **User Interaction Tests**: User input and actions

#### E2E Tests
- **Full App Flows**: Complete user journeys
- **Cross-App Integration**: Integration with other ShareConnect apps
- **Real Device Testing**: Testing on physical devices

## Security

### Data Protection
- **SQLCipher Encryption**: All local data encrypted
- **Secure Credential Storage**: Encrypted account credentials
- **HTTPS Communication**: Secure API communication
- **Input Validation**: Comprehensive input sanitization

### Authentication
- **MyJDownloader Auth**: Secure token-based authentication
- **Session Management**: Automatic session handling
- **Error Handling**: Secure error reporting

## Performance

### Optimization Features
- **Coroutine Integration**: Asynchronous operations
- **Flow for Live Updates**: Real-time data updates
- **Database Indexing**: Optimized database queries
- **Image Caching**: Efficient image loading with Coil
- **Network Caching**: Smart network request caching

### Memory Management
- **Lifecycle Awareness**: Proper lifecycle handling
- **Resource Cleanup**: Automatic resource management
- **Leak Prevention**: Memory leak detection and prevention

## Localization

### Supported Languages
- English (default)
- Serbian
- Russian
- German
- French
- Spanish
- Italian
- Portuguese
- Chinese
- Japanese
- Korean
- Arabic
- Hungarian
- Belarusian

### Adding New Languages
1. Add strings to `values-{locale}/strings.xml`
2. Update `locales_config.xml`
3. Test with language switching

## Contributing

### Development Workflow
1. Fork the repository
2. Create feature branch
3. Implement changes with tests
4. Ensure 100% test coverage
5. Submit pull request

### Code Style
- Follow Kotlin style guide
- Use 4-space indentation
- Maximum line length: 120 characters
- PascalCase for classes, camelCase for variables

## License

This project is part of the ShareConnect ecosystem and is licensed under the project's main license.

## Support

For issues and feature requests:
1. Check existing documentation
2. Search existing issues
3. Create new issue with detailed description
4. Include logs and reproduction steps

## Roadmap

### Future Enhancements
- [ ] Advanced download scheduling
- [ ] Batch operations
- [ ] Custom download rules
- [ ] Plugin system support
- [ ] Advanced analytics
- [ ] Cloud backup integration