# SecurityAccess

## Overview and Purpose

SecurityAccess is a comprehensive security module that provides authentication and access control for ShareConnect applications. It implements multiple authentication methods including PIN, password, biometric authentication (fingerprint, face recognition, iris scanning), and session management with configurable timeouts. The module ensures secure access to sensitive application features.

## Architecture and Components

SecurityAccess follows a repository pattern with state management:

### Core Components
- **SecurityAccessManager**: Main security coordinator and state manager
- **SecurityAccessRepository**: Data persistence and settings management
- **AccessMethod Implementations**: PIN, password, biometric authentication
- **SecurityUtils**: Cryptographic utilities and validation

### Authentication Methods
- **PinAccessMethod**: Numeric PIN authentication
- **PasswordAccessMethod**: Text password authentication
- **BiometricAccessMethod**: Device biometric authentication
- **CompositeAccessMethod**: Multi-factor authentication

## API Reference

### SecurityAccessManager
```kotlin
class SecurityAccessManager private constructor(context: Context) {
    val accessState: StateFlow<AccessState>
    val isLocked: StateFlow<Boolean>
    val failedAttempts: StateFlow<Int>
    val lockoutEndTime: StateFlow<LocalDateTime?>
    val currentSessionId: StateFlow<String?>

    suspend fun authenticate(method: AccessMethod, credentials: String? = null): AccessResult
    suspend fun grantAccess(sessionId: String)
    suspend fun revokeAccess()
    suspend fun checkAccess(): Boolean
    suspend fun extendSession()
    fun isSessionValid(): Boolean
    suspend fun resetFailedAttempts()
}
```

### SecurityAccessRepository
```kotlin
class SecurityAccessRepository private constructor(context: Context) {
    suspend fun getSecuritySettings(): SecuritySettings
    suspend fun updateSecuritySettings(settings: SecuritySettings)
    suspend fun saveAccessAttempt(attempt: AccessAttempt)
    suspend fun getAccessHistory(): List<AccessAttempt>
    suspend fun clearAccessHistory()
    suspend fun isLockedOut(): Boolean
    suspend fun getLockoutEndTime(): LocalDateTime?
}
```

## Key Classes and Their Responsibilities

### SecurityAccessManager
- **Responsibilities**:
  - Authentication flow orchestration
  - Session management and timeout handling
  - Failed attempt tracking and lockout enforcement
  - State synchronization across app components
  - Biometric prompt management

### SecurityAccessRepository
- **Responsibilities**:
  - Encrypted storage of security settings
  - Access attempt logging and history
  - Lockout state management
  - Data migration and integrity checks

### PinAccessMethod
- **Responsibilities**:
  - PIN validation and strength checking
  - Secure PIN storage and comparison
  - PIN change and reset functionality

### BiometricAccessMethod
- **Responsibilities**:
  - Biometric authentication integration
  - Device capability detection
  - Fallback handling for unsupported devices
  - Biometric prompt customization

## Data Models

### AccessState
```kotlin
enum class AccessState {
    IDLE,
    AUTHENTICATING,
    GRANTED,
    DENIED,
    LOCKED_OUT,
    SESSION_EXPIRED
}
```

### AccessMethod
```kotlin
enum class AccessMethod {
    PIN,
    PASSWORD,
    BIOMETRIC,
    COMPOSITE
}
```

### SecuritySettings
```kotlin
data class SecuritySettings(
    val enabledMethods: Set<AccessMethod> = setOf(AccessMethod.PIN),
    val sessionTimeoutMinutes: Int = 5,
    val maxFailedAttempts: Int = 5,
    val lockoutDurationMinutes: Int = 15,
    val requireBiometric: Boolean = false,
    val allowBiometricFallback: Boolean = true
)
```

### AccessAttempt
```kotlin
data class AccessAttempt(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val method: AccessMethod,
    val success: Boolean,
    val ipAddress: String? = null,
    val userAgent: String? = null
)
```

## Usage Examples

### Basic Authentication Setup
```kotlin
val securityManager = SecurityAccessManager.getInstance(context)

// Check if access is required
if (securityManager.checkAccess()) {
    // User has valid access
    proceedToMainScreen()
} else {
    // Show authentication UI
    showAuthenticationDialog()
}
```

### PIN Authentication
```kotlin
suspend fun authenticateWithPin(pin: String): Boolean {
    val result = securityManager.authenticate(AccessMethod.PIN, pin)
    return when (result) {
        is AccessResult.Success -> {
            securityManager.grantAccess(result.sessionId)
            true
        }
        is AccessResult.Failure -> {
            handleAuthenticationFailure(result.reason)
            false
        }
    }
}
```

### Biometric Authentication
```kotlin
suspend fun authenticateWithBiometric(): Boolean {
    return try {
        val result = securityManager.authenticate(AccessMethod.BIOMETRIC)
        when (result) {
            is AccessResult.Success -> true
            is AccessResult.Failure -> false
        }
    } catch (e: BiometricException) {
        // Handle biometric not available or failed
        fallbackToPinAuthentication()
        false
    }
}
```

### Session Management
```kotlin
// Check session validity
if (securityManager.isSessionValid()) {
    // Extend session on user activity
    lifecycleScope.launch {
        securityManager.extendSession()
    }
} else {
    // Session expired, re-authenticate
    showAuthenticationDialog()
}
```

### Security Settings Configuration
```kotlin
suspend fun updateSecuritySettings() {
    val newSettings = SecuritySettings(
        enabledMethods = setOf(AccessMethod.PIN, AccessMethod.BIOMETRIC),
        sessionTimeoutMinutes = 10,
        maxFailedAttempts = 3,
        lockoutDurationMinutes = 30
    )

    val repository = SecurityAccessRepository.getInstance(context)
    repository.updateSecuritySettings(newSettings)
}
```

## Dependencies

### Android Security
- `androidx.biometric:biometric:1.1.0` - Biometric authentication
- `androidx.security:security-crypto:1.1.0` - Cryptographic operations

### Coroutines and Flow
- `org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2` - Coroutine support
- `org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2` - Android coroutines

### Architecture Components
- `androidx.lifecycle:lifecycle-livedata-ktx:2.8.4` - LiveData support
- `androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4` - ViewModel support

### Database
- `androidx.room:room-runtime:2.6.1` - Database runtime
- `androidx.room:room-ktx:2.6.1` - Kotlin extensions

### Time Handling
- `org.jetbrains.kotlinx:kotlinx-datetime:0.6.1` - Date/time operations

---

*For more information, visit [https://shareconnect.org/docs/securityaccess](https://shareconnect.org/docs/securityaccess)*