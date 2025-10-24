# Onboarding

## Overview and Purpose

Onboarding is a module that provides a unified first-time user experience across all ShareConnect applications. It handles initial setup, permission requests, and configuration wizards to ensure users can quickly get started with the apps. The module provides a consistent onboarding flow while allowing app-specific customization.

## Architecture and Components

Onboarding follows a fragment-based architecture with a state machine approach:

### Core Components
- **ShareConnectOnboardingActivity**: Main onboarding coordinator
- **OnboardingFragment**: Base fragment for onboarding steps
- **OnboardingViewModel**: State management and business logic
- **OnboardingRepository**: Onboarding progress persistence

### UI Components
- **WelcomeFragment**: App introduction and branding
- **PermissionFragment**: System permission requests
- **ConfigurationFragment**: Initial app settings
- **CompletionFragment**: Setup completion and next steps

## API Reference

### ShareConnectOnboardingActivity
```kotlin
class ShareConnectOnboardingActivity : AppCompatActivity() {
    fun startOnboardingFlow()
    fun skipOnboarding()
    fun completeOnboarding()
    fun getCurrentStep(): OnboardingStep
    fun goToNextStep()
    fun goToPreviousStep()
}
```

### OnboardingViewModel
```kotlin
class OnboardingViewModel(
    private val repository: OnboardingRepository
) : ViewModel() {
    val currentStep: StateFlow<OnboardingStep>
    val isCompleted: StateFlow<Boolean>

    fun completeStep(step: OnboardingStep)
    fun skipOnboarding()
    fun resetOnboarding()
}
```

## Key Classes and Their Responsibilities

### ShareConnectOnboardingActivity
- **Responsibilities**:
  - Onboarding flow orchestration
  - Fragment navigation management
  - State persistence across configuration changes
  - Completion handling and app transition
  - Back navigation and flow interruption handling

### OnboardingViewModel
- **Responsibilities**:
  - Onboarding state management
  - Step completion tracking
  - Data validation and persistence
  - Business logic for onboarding flow
  - Error handling and recovery

### OnboardingRepository
- **Responsibilities**:
  - Onboarding progress persistence
  - SharedPreferences integration
  - Data migration and cleanup
  - Completion state management

## Data Models

### OnboardingStep
```kotlin
enum class OnboardingStep {
    WELCOME,
    PERMISSIONS,
    CONFIGURATION,
    COMPLETION,
    SKIPPED
}
```

### OnboardingState
```kotlin
data class OnboardingState(
    val currentStep: OnboardingStep,
    val completedSteps: Set<OnboardingStep>,
    val isCompleted: Boolean,
    val startTime: Long,
    val completionTime: Long?
)
```

## Usage Examples

### Starting Onboarding
```kotlin
// Check if onboarding is needed
val prefs = getSharedPreferences("onboarding_prefs", MODE_PRIVATE)
val onboardingCompleted = prefs.getBoolean("onboarding_completed", false)

if (!onboardingCompleted) {
    val intent = Intent(this, ShareConnectOnboardingActivity::class.java)
    startActivity(intent)
    finish()
}
```

### Customizing Onboarding Flow
```kotlin
class CustomOnboardingActivity : ShareConnectOnboardingActivity() {
    override fun getOnboardingSteps(): List<OnboardingStep> {
        return listOf(
            OnboardingStep.WELCOME,
            OnboardingStep.CUSTOM_PERMISSION_STEP,
            OnboardingStep.CONFIGURATION,
            OnboardingStep.COMPLETION
        )
    }

    override fun createFragmentForStep(step: OnboardingStep): Fragment {
        return when (step) {
            OnboardingStep.CUSTOM_PERMISSION_STEP -> CustomPermissionFragment()
            else -> super.createFragmentForStep(step)
        }
    }
}
```

## Dependencies

### Android Architecture Components
- `androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4` - ViewModel support
- `androidx.lifecycle:lifecycle-livedata-ktx:2.8.4` - LiveData support
- `androidx.fragment:fragment-ktx:1.6.2` - Fragment extensions

### UI Components
- `com.google.android.material:material:1.11.0` - Material Design components
- `androidx.viewpager2:viewpager2:1.0.0` - ViewPager2 for step navigation

### Dependency Injection
- `androidx.activity:activity-ktx:1.8.2` - Activity extensions

---

*For more information, visit [https://shareconnect.org/docs/onboarding](https://shareconnect.org/docs/onboarding)*