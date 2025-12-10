# PlexConnect - Plex Media Server Connector

## Recent Additions

### Advanced Media Search
- Comprehensive search engine with multi-source querying
- Intelligent result ranking and scoring
- Fuzzy matching and relevance calculation
- Support for advanced filtering
- Local and remote search integration

### Machine Learning Recommendations
- Personalized media recommendations
- Watch history-based suggestion engine
- TensorFlow Lite model integration
- Configurable recommendation options
- Potential for federated learning

## Architectural Highlights

### Data Flow
```
PlexApiClient → PlexRepository → ViewModel → Compose UI
       ↑            ↕            ↕
   Network      Room Database   UI State
   Advanced     ML Recommendations
   Search
```

### Key Components
- **API Client**: Retrofit-based Plex API interactions
- **Repository**: Abstracts data sources, provides caching
- **ViewModel**: Manages UI state and business logic
- **Compose UI**: Reactive, modern user interface
- **Search Engine**: Multi-source intelligent search
- **Recommendation Engine**: ML-powered personalized suggestions

## Code Examples

### Advanced Search
```kotlin
fun performAdvancedSearch(serverUrl: String, query: String, token: String) {
    val searchOptions = SearchOptions(
        query = query,
        mediaTypes = listOf(MediaType.MOVIE, MediaType.TV_SHOW),
        yearRange = IntRange(2010, 2023)
    )

    searchEngine.search(serverUrl, token, searchOptions)
        .collect { searchResult ->
            // Handle search results with ranking and relevance
            val rankedItems = searchResult.items
        }
}
```

### Machine Learning Recommendations
```kotlin
fun getPersonalizedRecommendations(userId: String) {
    recommendationEngine
        .generateRecommendations(userId) 
        .collect { recommendedItems ->
            // Display personalized recommendations
        }
}
```

## Performance Metrics
- **Search**: Multi-source querying with intelligent ranking
- **Recommendations**: Personalized ML-driven suggestions
- **Caching**: Reduced network calls via Room database
- **State Management**: Efficient reactive updates
- **Error Handling**: Comprehensive fallback mechanisms

## Key Technologies
- Kotlin Coroutines
- Jetpack Compose
- Room Database
- Retrofit
- TensorFlow Lite
- Koin Dependency Injection

## Latest Enhancements

### Advanced Media Search
- Multi-source search across local and remote databases
- Intelligent result ranking
- Fuzzy matching and relevance scoring
- Advanced filtering capabilities

### Machine Learning Recommendations
- Personalized media suggestions
- Watch history analysis
- TensorFlow Lite model integration
- Configurable recommendation parameters

### Advanced Media Filtering
- Comprehensive `PlexMediaFilter`
- Media type and year range selection
- Sorting and watch status filtering
- Preset and custom filter configurations

### Enhanced Offline Support
- Offline-first data retrieval
- Intelligent caching
- Background synchronization
- Seamless network fallback

### Comprehensive Error Handling
- Detailed error classification
- Network and authentication error management
- Server compatibility validation
- Exponential backoff retry mechanism

## Getting Started
1. Configure Koin in your app
2. Add PlexModule to Koin setup
3. Inject PlexViewModel in Compose screens
4. Initialize search and recommendation engines

## Contributing
- Follow existing code style
- Write comprehensive tests
- Update documentation
- Submit pull requests

## Future Roadmap
- Enhanced ML model training
- More advanced recommendation algorithms
- Expanded search capabilities
- Cross-platform sync improvements

## License
Part of ShareConnect project. See root LICENSE file.

## Support
- GitHub Issues
- Plex Community Forums