#!/bin/bash

# Script to update all sync modules to use centralized dependencies

SYNC_MODULES=("ThemeSync" "ProfileSync" "HistorySync" "RSSSync" "BookmarkSync" "PreferencesSync" "LanguageSync" "TorrentSharingSync")

for module in "${SYNC_MODULES[@]}"; do
    echo "Updating $module..."
    
    if [ -f "$module/build.gradle.kts" ]; then
        # Remove force configurations
        sed -i '/Force Kotlin version to avoid conflicts/,/force("org.jetbrains.kotlin:kotlin-stdlib-common:2.0.0")/d' "$module/build.gradle.kts"
        
        # Remove force configurations for KSP
        sed -i '/Force Kotlin version for KSP to avoid conflicts/,/force("org.jetbrains.kotlin:kotlin-stdlib-common:2.0.0")/d' "$module/build.gradle.kts"
        
        # Update dependencies section
        if grep -q "implementation(project(\":Dependencies\"))" "$module/build.gradle.kts"; then
            echo "  $module already updated"
        else
            # Replace dependencies section with simplified version
            sed -i '/^dependencies {/,/^}/c\
dependencies {\n    // Centralized dependencies\n    implementation(project(":Dependencies"))\n\n    // Room Database KSP compiler\n    ksp("androidx.room:room-compiler:2.6.1")\n\n    // Asinka for syncing\n    implementation(project(":Asinka:asinka"))\n}' "$module/build.gradle.kts"
            echo "  $module updated successfully"
        fi
    else
        echo "  $module/build.gradle.kts not found"
    fi
done

echo "Sync modules update complete!"