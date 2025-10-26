# SyncthingConnect Lottie Animation Implementation

## Animation Details
- **Concept**: Real-time file sync with folders
- **Duration**: 3000ms
- **Resolution**: 512x512
- **Framerate**: 60fps

## Android Integration

### 1. Add Lottie Dependency
```gradle
implementation 'com.airbnb.android:lottie:6.1.0'
```

### 2. Add Animation to Resources
Copy `splash_animation.json` to:
```
app/src/main/res/raw/splash_animation.json
```

### 3. Create Splash Activity Layout
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout 
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/ic_launcher_background">

    <com.airbnb.lottie.LottieAnimationView
        android:id="@+id/animation_view"
        android:layout_width="200dp"
        android:layout_height="200dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:lottie_autoPlay="true"
        app:lottie_loop="false"
        app:lottie_rawRes="@raw/splash_animation" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

### 4. Implement Splash Activity
```kotlin
class SplashActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        
        val animationView = findViewById<LottieAnimationView>(R.id.animation_view)
        
        animationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                // Animation started
            }
            
            override fun onAnimationEnd(animation: Animator) {
                // Start main activity when animation completes
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }
            
            override fun onAnimationCancel(animation: Animator) {
                // Handle cancellation
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }
            
            override fun onAnimationRepeat(animation: Animator) {
                // Animation repeated
            }
        })
    }
}
```

### 5. Update AndroidManifest.xml
```xml
<activity
    android:name=".SplashActivity"
    android:theme="@style/Theme.AppCompat.Light.NoActionBar">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
```

## Animation Sequence
1. **0-500ms**: Background circle scales in
2. **500-1500ms**: SyncthingConnect icon fades in with application-specific animation
3. **1500-2500ms**: Idle animation with subtle effects
4. **2500-3000ms**: Clean transition (handled by activity change)

## Performance Notes
- Animation file size: <100KB
- Optimized for 60fps playback
- Minimal memory usage
- Compatible with Android 5.0+

## Testing Checklist
- [ ] Animation plays smoothly on target devices
- [ ] No performance issues or lag
- [ ] Proper timing and sequencing
- [ ] Correct colors and visual appearance
- [ ] Memory usage within acceptable limits
