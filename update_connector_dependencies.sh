#!/bin/bash

# Script to update all connector modules to use centralized Dependencies module

# List of connector modules to update
CONNECTORS=(
    "Connectors/JellyfinConnect/JellyfinConnector"
    "Connectors/SeafileConnect/SeafileConnector" 
    "Connectors/PlexConnect/PlexConnector"
    "Connectors/NextcloudConnect/NextcloudConnector"
    "Connectors/MotrixConnect/MotrixConnector"
    "Connectors/GiteaConnect/GiteaConnector"
    "Connectors/DuplicatiConnect/DuplicatiConnector"
)

for connector in "${CONNECTORS[@]}"; do
    build_file="$connector/build.gradle"
    
    if [ -f "$build_file" ]; then
        echo "Updating $build_file..."
        
        # Create a backup
        cp "$build_file" "$build_file.backup"
        
        # Check if Dependencies module is already included
        if grep -q "implementation(project(\":Dependencies\"))" "$build_file"; then
            echo "  Dependencies module already included in $connector"
        else
            # Add Dependencies module at the beginning of dependencies block
            sed -i '/^dependencies {/a\    // Centralized dependencies\n    implementation(project(":Dependencies"))' "$build_file"
            echo "  Added Dependencies module to $connector"
        fi
        
        # Remove duplicate dependencies that are now in Dependencies module
        # Remove specific dependencies that are covered by Dependencies module
        sed -i '/implementation.*"androidx.core:core-ktx:/d' "$build_file"
        sed -i '/implementation.*"androidx.appcompat:appcompat:/d' "$build_file"
        sed -i '/implementation.*"androidx.lifecycle:lifecycle-runtime-ktx:/d' "$build_file"
        sed -i '/implementation.*"androidx.lifecycle:lifecycle-viewmodel-compose:/d' "$build_file"
        sed -i '/implementation.*"org.jetbrains.kotlinx:kotlinx-coroutines-android:/d' "$build_file"
        sed -i '/implementation.*"org.jetbrains.kotlinx:kotlinx-coroutines-core:/d' "$build_file"
        sed -i '/implementation.*"com.squareup.retrofit2:retrofit:/d' "$build_file"
        sed -i '/implementation.*"com.squareup.retrofit2:converter-gson:/d' "$build_file"
        sed -i '/implementation.*"com.squareup.okhttp3:okhttp:/d' "$build_file"
        sed -i '/implementation.*"com.squareup.okhttp3:logging-interceptor:/d' "$build_file"
        sed -i '/implementation.*"com.google.code.gson:gson:/d' "$build_file"
        sed -i '/testImplementation.*"junit:junit:/d' "$build_file"
        sed -i '/testImplementation.*"org.jetbrains.kotlinx:kotlinx-coroutines-test:/d' "$build_file"
        sed -i '/testImplementation.*"androidx.arch.core:core-testing:/d' "$build_file"
        sed -i '/androidTestImplementation.*"androidx.test.ext:junit:/d' "$build_file"
        sed -i '/androidTestImplementation.*"androidx.test.espresso:espresso-core:/d' "$build_file"
        
        echo "  Removed duplicate dependencies from $connector"
    else
        echo "Build file not found: $build_file"
    fi
done

echo "Connector dependency updates completed!"