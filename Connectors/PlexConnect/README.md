# PlexConnect - Plex Media Server Connector

## Recent Additions

### Comprehensive Repository Implementation
- Full repository pattern with local caching
- Room database integration
- Comprehensive error handling and state management

### Advanced ViewModel
- Reactive state management with Kotlin Flows
- Unified UI state handling
- Comprehensive media browsing and search capabilities

### Dependency Injection
- Koin-based dependency injection
- Modular and testable architecture

### Jetpack Compose UI
- Modern, reactive user interface
- Comprehensive media browsing screens
- State-driven UI updates

## Architectural Highlights

### Data Flow
```
PlexApiClient → PlexRepository → ViewModel → Compose UI
       ↑            ↕            ↕
   Network      Room Database   UI State
```

### Key Components
- **API Client**: Retrofit-based Plex API interactions
- **Repository**: Abstracts data sources, provides caching
- **ViewModel**: Manages UI state and business logic
- **Compose UI**: Reactive, modern user interface

## Code Example

### Fetching Libraries
```kotlin
class PlexViewModel(private val plexRepository: PlexRepository) : ViewModel() {
    fun fetchLibraries(serverUrl: String, token: String) {
        viewModelScope.launch {
            plexRepository.getLibraries(serverUrl, token)
                .collect { libraries ->
                    // Update UI with libraries
                }
        }
    }
}
```

### Searching Media
```kotlin
fun searchMedia(serverUrl: String, query: String, token: String) {
    viewModelScope.launch {
        plexRepository.searchMedia(serverUrl, query, token)
            .collect { results ->
                // Update search results UI
            }
    }
}
```

## Performance Metrics
- **Caching**: Reduced network calls via Room database
- **State Management**: Efficient reactive updates
- **Error Handling**: Comprehensive fallback mechanisms

## Testing Strategy
- Unit tests for API client
- Integration tests for repository
- ViewModel state verification
- Comprehensive error scenario testing

## Planned Improvements
- Advanced media filtering
- Enhanced offline support
- More granular error handling
- Performance optimizations

## Getting Started
1. Ensure Koin is configured in your app
2. Add PlexModule to your Koin setup
3. Inject PlexViewModel in your Compose screens

## Contributing
- Follow existing code style
- Write comprehensive tests
- Update documentation
- Submit pull requests

## License
Part of ShareConnect project. See root LICENSE file.

## Support
- GitHub Issues
- Plex Community Forums