#!/bin/bash

# Lottie Animation Integration Script
# Updates all ShareConnect applications with Lottie splash animations

set -e

# Color definitions
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Base project directory
PROJECT_ROOT="$(pwd)"

# List of all applications
APPS=(
    "ShareConnector"
    "Connectors/TransmissionConnect/TransmissionConnector"
    "Connectors/uTorrentConnect/uTorrentConnector"
    "Connectors/qBitConnect/qBitConnector"
    "Connectors/JDownloaderConnect/JDownloaderConnector"
    "Connectors/PlexConnect/PlexConnector"
    "Connectors/HomeAssistantConnect/HomeAssistantConnector"
    "Connectors/PortainerConnect/PortainerConnector"
    "Connectors/JellyfinConnect/JellyfinConnector"
    "Connectors/NetdataConnect/NetdataConnector"
    "Connectors/WireGuardConnect/WireGuardConnector"
    "Connectors/MatrixConnect/MatrixConnector"
    "Connectors/GiteaConnect/GiteaConnector"
    "Connectors/OnlyOfficeConnect/OnlyOfficeConnector"
    "Connectors/MinecraftServerConnect/MinecraftServerConnector"
    "Connectors/SeafileConnect/SeafileConnector"
    "Connectors/SyncthingConnect/SyncthingConnector"
    "Connectors/DuplicatiConnect/DuplicatiConnector"
    "Connectors/PaperlessNGConnect/PaperlessNGConnector"
    "Connectors/MotrixConnect/MotrixConnector"
)

echo -e "${BLUE}ShareConnect Lottie Animation Integration${NC}"
echo -e "${BLUE}==========================================${NC}"
echo ""

# Function to check if directory exists
check_app_exists() {
    local app_path="$1"
    if [ ! -d "$app_path" ]; then
        echo -e "${YELLOW}Warning: $app_path not found${NC}"
        return 1
    fi
    return 0
}

# Function to check if Lottie animation exists
check_lottie_exists() {
    local app_name="$1"
    local lottie_file="Assets/Logos/Lottie/$app_name/splash_animation.json"
    if [ ! -f "$lottie_file" ]; then
        echo -e "${YELLOW}Warning: Lottie animation not found: $lottie_file${NC}"
        return 1
    fi
    return 0
}

# Function to update build.gradle with Lottie dependency
update_build_gradle() {
    local app_path="$1"
    local build_file="$app_path/build.gradle"
    
    if [ ! -f "$build_file" ]; then
        echo -e "${YELLOW}Warning: build.gradle not found: $build_file${NC}"
        return 1
    fi
    
    # Check if Lottie dependency already exists
    if grep -q "com.airbnb.android:lottie" "$build_file"; then
        echo -e "${GREEN}✓ Lottie dependency already exists${NC}"
        return 0
    fi
    
    # Add Lottie dependency
    sed -i '/dependencies {/a\    implementation \"com.airbnb.android:lottie:6.1.0\"' "$build_file"
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Added Lottie dependency${NC}"
    else
        echo -e "${RED}✗ Failed to add Lottie dependency${NC}"
        return 1
    fi
}

# Function to copy Lottie animation to app resources
copy_lottie_animation() {
    local app_name="$1"
    local app_path="$2"
    local lottie_source="Assets/Logos/Lottie/$app_name/splash_animation.json"
    local lottie_dest="$app_path/src/main/res/raw/splash_animation.json"
    
    # Create raw directory if it doesn't exist
    mkdir -p "$app_path/src/main/res/raw"
    
    # Copy Lottie animation
    cp "$lottie_source" "$lottie_dest"
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Copied Lottie animation${NC}"
    else
        echo -e "${RED}✗ Failed to copy Lottie animation${NC}"
        return 1
    fi
}

# Function to create splash activity layout
create_splash_layout() {
    local app_path="$1"
    local layout_file="$app_path/src/main/res/layout/activity_splash.xml"
    local app_name=$(basename "$app_path")
    
    # Create layout directory if it doesn't exist
    mkdir -p "$app_path/src/main/res/layout"
    
    # Create splash activity layout
    cat > "$layout_file" << EOF
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
EOF
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Created splash activity layout${NC}"
    else
        echo -e "${RED}✗ Failed to create splash activity layout${NC}"
        return 1
    fi
}

# Function to create splash activity Kotlin file
create_splash_activity() {
    local app_path="$1"
    local app_name=$(basename "$app_path")
    local package_name="com.shareconnect.${app_name,,}"
    local activity_file="$app_path/src/main/kotlin/${package_name//.//}/SplashActivity.kt"
    
    # Create package directory structure
    mkdir -p "$(dirname "$activity_file")"
    
    # Create splash activity
    cat > "$activity_file" << EOF
package $package_name

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView

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
EOF
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Created splash activity${NC}"
    else
        echo -e "${RED}✗ Failed to create splash activity${NC}"
        return 1
    fi
}

# Function to update AndroidManifest.xml
update_android_manifest() {
    local app_path="$1"
    local manifest_file="$app_path/src/main/AndroidManifest.xml"
    local package_name="com.shareconnect.$(basename "$app_path" | tr '[:upper:]' '[:lower:]')"
    
    if [ ! -f "$manifest_file" ]; then
        echo -e "${YELLOW}Warning: AndroidManifest.xml not found: $manifest_file${NC}"
        return 1
    fi
    
    # Check if SplashActivity already exists
    if grep -q "SplashActivity" "$manifest_file"; then
        echo -e "${GREEN}✓ SplashActivity already in manifest${NC}"
        return 0
    fi
    
    # Add SplashActivity to manifest
    # This is a simplified approach - actual implementation may need more sophisticated XML parsing
    sed -i "/<application/a\\    <activity android:name=\".SplashActivity\" android:theme=\"@style/Theme.AppCompat.Light.NoActionBar\">\\n        <intent-filter>\\n            <action android:name=\"android.intent.action.MAIN\" />\\n            <category android:name=\"android.intent.category.LAUNCHER\" />\\n        </intent-filter>\\n    </activity>" "$manifest_file"
    
    # Also update MainActivity to remove LAUNCHER intent filter
    sed -i '/android:name=".*MainActivity"/,/</intent-filter>/ { /category.*LAUNCHER/d }' "$manifest_file"
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Updated AndroidManifest.xml${NC}"
    else
        echo -e "${RED}✗ Failed to update AndroidManifest.xml${NC}"
        echo -e "${YELLOW}Manual update required for: $manifest_file${NC}"
        return 1
    fi
}

# Main integration process
for app_info in "${APPS[@]}"; do
    app_path="$PROJECT_ROOT/$app_info"
    app_name=$(basename "$app_info")
    
    echo -e "${BLUE}Processing: $app_name${NC}"
    echo -e "${BLUE}Path: $app_path${NC}"
    
    # Check if app exists
    if ! check_app_exists "$app_path"; then
        continue
    fi
    
    # Check if Lottie animation exists
    if ! check_lottie_exists "$app_name"; then
        continue
    fi
    
    # Update build.gradle
    update_build_gradle "$app_path"
    
    # Copy Lottie animation
    copy_lottie_animation "$app_name" "$app_path"
    
    # Create splash layout
    create_splash_layout "$app_path"
    
    # Create splash activity
    create_splash_activity "$app_path"
    
    # Update AndroidManifest.xml
    update_android_manifest "$app_path"
    
    echo -e "${GREEN}✓ Completed $app_name${NC}"
    echo ""
    
done

echo -e "${GREEN}Lottie animation integration completed!${NC}"
echo ""
echo -e "${YELLOW}Next steps:${NC}"
echo "1. Build each application to verify integration"
echo "2. Test splash animations on Android devices"
echo "3. Verify animation timing and performance"
echo "4. Customize animations as needed"
echo ""
echo -e "${YELLOW}Manual verification required:${NC}"
echo "- Check AndroidManifest.xml for proper activity configuration"
echo "- Verify MainActivity no longer has LAUNCHER intent filter"
echo "- Test on multiple Android versions and devices"