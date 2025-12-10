# PlexConnect

## Overview
PlexConnect is a ShareConnect connector for Plex services, providing seamless integration and synchronization capabilities.

## Features
- Server connection management
- Sync capabilities
- Security access integration
- Material Design 3 UI

## Configuration
Add configuration in your app's settings or through the connection dialog.

## Usage
```kotlin
// Example connection
val connection = PlexData(
    id = "main_server",
    name = "Main Plex Server",
    host = "example.com",
    port = 8080
)
```

## Dependencies
- Kotlin Coroutines
- Jetpack Compose
- Retrofit
- Room Database

## License
MIT License
