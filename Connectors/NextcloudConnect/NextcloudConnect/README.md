# NextcloudConnect

## Overview
NextcloudConnect is a ShareConnect connector for Nextcloud services, providing seamless integration and synchronization capabilities.

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
val connection = NextcloudData(
    id = "main_server",
    name = "Main Nextcloud Server",
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
