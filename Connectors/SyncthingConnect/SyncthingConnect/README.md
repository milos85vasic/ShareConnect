# SyncthingConnect

## Overview
SyncthingConnect is a ShareConnect connector for Syncthing services, providing seamless integration and synchronization capabilities.

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
val connection = SyncthingData(
    id = "main_server",
    name = "Main Syncthing Server",
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
