#!/bin/bash

# Lottie Animation Template Generator
# Creates JSON templates and implementation guides for Lottie animations

set -e

# Animation properties
DURATION="3000"
WIDTH="512"
HEIGHT="512"
FRAME_RATE="60"

# Application animation concepts
declare -A ANIMATION_CONCEPTS=(
    ["ShareConnector"]="Network connectivity coming to life"
    ["TransmissionConnect"]="Butterfly taking flight with mechanical elements"
    ["uTorrentConnect"]="µ symbol with dynamic download progress"
    ["qBitConnect"]="Q letter assembling with torrent pieces"
    ["JDownloaderConnect"]="Download manager in action with packages"
    ["PlexConnect"]="Media waves flowing with play symbol"
    ["HomeAssistantConnect"]="Smart home waking up with connected devices"
    ["PortainerConnect"]="Container stack building on Docker whale"
    ["JellyfinConnect"]="Jellyfish swimming with media symbols"
    ["NetdataConnect"]="Live dashboard metrics animating"
    ["WireGuardConnect"]="Secure connection establishing with shield"
    ["MatrixConnect"]="Chat platform coming online with message flow"
    ["GiteaConnect"]="Code repository syncing with branch movement"
    ["OnlyOfficeConnect"]="Document creation process with tools"
    ["MinecraftServerConnect"]="Game server starting up with block animation"
    ["SeafileConnect"]="File synchronization process with cloud"
    ["SyncthingConnect"]="Real-time file sync with folders"
    ["DuplicatiConnect"]="Backup process with data streams"
    ["PaperlessNGConnect"]="Document scanning process with OCR"
    ["MotrixConnect"]="Download manager interface with speed indicators"
)

echo "Generating Lottie Animation Templates..."
echo "========================================"
echo ""

# Create main Lottie directory structure
mkdir -p "Assets/Logos/Lottie"

for app_name in "${!ANIMATION_CONCEPTS[@]}"; do
    concept="${ANIMATION_CONCEPTS[$app_name]}"
    app_lower=$(echo "$app_name" | tr '[:upper:]' '[:lower:]')
    
    echo "Creating Lottie templates for: $app_name"
    
    # Create Lottie JSON template
    cat > "Assets/Logos/Lottie/$app_name/splash_animation.json" << EOF
{
  "v": "5.7.4",
  "fr": $FRAME_RATE,
  "ip": 0,
  "op": $((DURATION * FRAME_RATE / 1000)),
  "w": $WIDTH,
  "h": $HEIGHT,
  "nm": "${app_name} Splash Animation",
  "ddd": 0,
  "assets": [],
  "layers": [
    {
      "ddd": 0,
      "ind": 1,
      "ty": 4,
      "nm": "Background Circle",
      "td": 1,
      "sr": 1,
      "ks": {
        "o": {
          "a": 0,
          "k": 100,
          "ix": 11
        },
        "r": {
          "a": 0,
          "k": 0,
          "ix": 10
        },
        "p": {
          "a": 0,
          "k": [256, 256, 0],
          "ix": 2
        },
        "a": {
          "a": 0,
          "k": [0, 0, 0],
          "ix": 1
        },
        "s": {
          "a": 1,
          "k": [
            {
              "i": {"x": [0.833, 0.833, 0.833], "y": [0.833, 0.833, 0.833]},
              "o": {"x": [0.167, 0.167, 0.167], "y": [0.167, 0.167, 0.167]},
              "t": 0,
              "s": [0, 0, 100]
            },
            {
              "i": {"x": [0.833, 0.833, 0.833], "y": [0.833, 0.833, 0.833]},
              "o": {"x": [0.167, 0.167, 0.167]}, "y": [0.167, 0.167, 0.167]},
              "t": 15,
              "s": [100, 100, 100]
            }
          ],
          "ix": 6
        }
      },
      "ao": 0,
      "shapes": [
        {
          "ty": "el",
          "p": {
            "a": 0,
            "k": [0, 0],
            "ix": 3
          },
          "s": {
            "a": 0,
            "k": [180, 180],
            "ix": 2
          }
        },
        {
          "ty": "fl",
          "c": {
            "a": 0,
            "k": [0.3098, 0.7647, 0.9686, 1],
            "ix": 4
          },
          "o": {
            "a": 0,
            "k": 90,
            "ix": 5
          },
          "r": 1
        }
      ]
    },
    {
      "ddd": 0,
      "ind": 2,
      "ty": 4,
      "nm": "${app_name} Icon",
      "td": 1,
      "sr": 1,
      "ks": {
        "o": {
          "a": 1,
          "k": [
            {
              "i": {"x": [0.833], "y": [0.833]},
              "o": {"x": [0.167], "y": [0.167]},
              "t": 0,
              "s": [0]
            },
            {
              "i": {"x": [0.833], "y": [0.833]},
              "o": {"x": [0.167], "y": [0.167]},
              "t": 10,
              "s": [100]
            }
          ],
          "ix": 11
        },
        "r": {
          "a": 0,
          "k": 0,
          "ix": 10
        },
        "p": {
          "a": 0,
          "k": [256, 256, 0],
          "ix": 2
        },
        "a": {
          "a": 0,
          "k": [0, 0, 0],
          "ix": 1
        },
        "s": {
          "a": 0,
          "k": [80, 80, 100],
          "ix": 6
        }
      },
      "ao": 0,
      "shapes": [
        {
          "ty": "gr",
          "it": [
            {
              "ty": "rc",
              "p": {
                "a": 0,
                "k": [0, 0],
                "ix": 3
              },
              "s": {
                "a": 0,
                "k": [40, 40],
                "ix": 2
              },
              "r": {
                "a": 0,
                "k": 0,
                "ix": 4
              }
            },
            {
              "ty": "st",
              "c": {
                "a": 0,
                "k": [1, 1, 1, 1],
                "ix": 3
              },
              "o": {
                "a": 0,
                "k": 100,
                "ix": 4
              },
              "w": {
                "a": 0,
                "k": 8,
                "ix": 5
              }
            },
            {
              "ty": "tr",
              "p": {
                "a": 0,
                "k": [0, 0],
                "ix": 2
              },
              "a": {
                "a": 0,
                "k": [0, 0],
                "ix": 1
              },
              "s": {
                "a": 0,
                "k": [100, 100],
                "ix": 3
              },
              "r": {
                "a": 0,
                "k": 0,
                "ix": 6
              },
              "o": {
                "a": 0,
                "k": 100,
                "ix": 7
              }
            }
          ]
        }
      ]
    }
  ],
  "markers": [],
  "meta": {
    "a": "ShareConnect Lottie Animations",
    "d": "${app_name} Splash Animation - ${concept}",
    "tc": ""
  }
}
EOF
    
    echo "✓ Created: Assets/Logos/Lottie/$app_name/splash_animation.json"
    
    # Create Android implementation guide
    cat > "Assets/Logos/Lottie/$app_name/implementation_guide.md" << EOF
# ${app_name} Lottie Animation Implementation

## Animation Details
- **Concept**: ${concept}
- **Duration**: ${DURATION}ms
- **Resolution**: ${WIDTH}x${HEIGHT}
- **Framerate**: ${FRAME_RATE}fps

## Android Integration

### 1. Add Lottie Dependency
\`\`\`gradle
implementation 'com.airbnb.android:lottie:6.1.0'
\`\`\`

### 2. Add Animation to Resources
Copy \`splash_animation.json\` to:
\`\`\`
app/src/main/res/raw/splash_animation.json
\`\`\`

### 3. Create Splash Activity Layout
\`\`\`xml
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
\`\`\`

### 4. Implement Splash Activity
\`\`\`kotlin
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
\`\`\`

### 5. Update AndroidManifest.xml
\`\`\`xml
<activity
    android:name=".SplashActivity"
    android:theme="@style/Theme.AppCompat.Light.NoActionBar">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
\`\`\`

## Animation Sequence
1. **0-500ms**: Background circle scales in
2. **500-1500ms**: ${app_name} icon fades in with application-specific animation
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
EOF
    
    echo "✓ Created: Assets/Logos/Lottie/$app_name/implementation_guide.md"
    
    echo ""
    
done

echo "Lottie animation template generation complete!"
echo ""
echo "Generated for each application:"
echo "- splash_animation.json (Lottie animation file)"
echo "- implementation_guide.md (Android integration guide)"
echo ""
echo "Next steps:"
echo "1. Design detailed animations in After Effects"
echo "2. Export using Bodymovin extension"
echo "3. Replace template JSON files with final animations"
echo "4. Test on Android devices"
echo ""
echo "Tools required:"
echo "- Adobe After Effects (animation design)"
echo "- Bodymovin extension (Lottie export)"
echo "- LottieFiles (preview and optimization)"