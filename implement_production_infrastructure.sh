#!/bin/bash

# Phase 4: Production Infrastructure - APK Signing and Crash Reporting
# Set up production-ready signing configuration and crash reporting for all connectors

set -e

echo "🚀 Phase 4: Implementing Production Infrastructure"
echo "================================================="

# Create production signing configuration
cat > signing.properties << 'EOF'
# Production Signing Configuration for ShareConnect
# This file contains signing configuration for all connectors
# IMPORTANT: Keep this file secure and never commit to version control

# Store configuration
STORE_FILE=../signing/shareconnect_keystore.jks
STORE_PASSWORD=your_store_password_here
KEY_ALIAS=shareconnect_key
KEY_PASSWORD=your_key_password_here

# Signing metadata
SIGNING_VERSION=1.0.0
SIGNING_DATE=2025-10-26
EOF

# Create signing setup script
cat > setup_signing.sh << 'EOF'
#!/bin/bash

# Setup production signing for ShareConnect connectors
# This script creates the necessary keystore and configures signing

set -e

echo "🔐 Setting up Production Signing for ShareConnect"
echo "================================================"

KEYSTORE_DIR="signing"
KEYSTORE_FILE="$KEYSTORE_DIR/shareconnect_keystore.jks"
KEY_ALIAS="shareconnect_key"

# Create signing directory
mkdir -p "$KEYSTORE_DIR"

if [ ! -f "$KEYSTORE_FILE" ]; then
    echo "📝 Creating new keystore..."

    # Generate keystore
    keytool -genkeypair \
        -v \
        -keystore "$KEYSTORE_FILE" \
        -keyalg RSA \
        -keysize 2048 \
        -validity 10000 \
        -alias "$KEY_ALIAS" \
        -dname "CN=ShareConnect, OU=Development, O=ShareConnect, L=Internet, ST=Web, C=US" \
        -storepass "change_this_password" \
        -keypass "change_this_password"

    echo "✅ Keystore created: $KEYSTORE_FILE"
    echo ""
    echo "⚠️  IMPORTANT SECURITY STEPS:"
    echo "   1. Change the default passwords in signing.properties"
    echo "   2. Backup the keystore file securely"
    echo "   3. Never commit keystore or passwords to version control"
    echo "   4. Use environment variables for CI/CD pipelines"
    echo ""
    echo "🔧 Update signing.properties with your secure passwords"
else
    echo "✅ Keystore already exists: $KEYSTORE_FILE"
fi

echo ""
echo "📋 Next Steps:"
echo "   1. Update passwords in signing.properties"
echo "   2. Configure CI/CD with signing secrets"
echo "   3. Test signed builds: ./gradlew assembleRelease"
EOF

chmod +x setup_signing.sh

# Update root build.gradle.kts with signing configuration
cat >> build.gradle.kts << 'EOF'

// Production signing configuration
android {
    signingConfigs {
        create("release") {
            storeFile = file("signing/shareconnect_keystore.jks")
            storePassword = System.getenv("STORE_PASSWORD") ?: "change_this_password"
            keyAlias = System.getenv("KEY_ALIAS") ?: "shareconnect_key"
            keyPassword = System.getenv("KEY_PASSWORD") ?: "change_this_password"
        }
    }

    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}
EOF

# Create Firebase Crashlytics configuration
cat > firebase_crashlytics.gradle << 'EOF'
// Firebase Crashlytics Configuration
// Include this in connector build.gradle files for crash reporting

dependencies {
    // Firebase Crashlytics
    implementation 'com.google.firebase:firebase-crashlytics:19.1.0'
    implementation 'com.google.firebase:firebase-analytics:22.1.0'
}

android {
    buildFeatures {
        buildConfig = true
    }

    // Crashlytics configuration
    afterEvaluate {
        tasks.named("preBuild") {
            dependsOn("firebaseCrashlyticsMappingFileTask")
        }
    }
}

// Apply Firebase Crashlytics plugin
apply plugin: 'com.google.firebase.crashlytics'
EOF

# Create crash reporting utility
cat > Toolkit/CrashReporting/src/main/kotlin/com/shareconnect/crashreporting/CrashReporting.kt << 'EOF'
package com.shareconnect.crashreporting

import android.app.Application
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics

/**
 * Crash reporting utility for ShareConnect applications
 * Provides unified crash reporting across all connectors
 */
object CrashReporting {

    private const val TAG = "CrashReporting"
    private var isInitialized = false

    /**
     * Initialize crash reporting for the application
     * Call this in Application.onCreate()
     */
    fun initialize(application: Application, appName: String, appVersion: String) {
        if (isInitialized) {
            Log.w(TAG, "Crash reporting already initialized")
            return
        }

        try {
            // Configure Firebase Crashlytics
            val crashlytics = FirebaseCrashlytics.getInstance()

            // Set custom keys for better crash analysis
            crashlytics.setCustomKey("app_name", appName)
            crashlytics.setCustomKey("app_version", appVersion)
            crashlytics.setCustomKey("android_version", android.os.Build.VERSION.RELEASE)
            crashlytics.setCustomKey("device_model", android.os.Build.MODEL)

            // Enable crash reporting (disabled in debug builds)
            crashlytics.setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)

            isInitialized = true
            Log.i(TAG, "Crash reporting initialized for $appName v$appVersion")

        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize crash reporting", e)
        }
    }

    /**
     * Log non-fatal exceptions
     */
    fun logException(exception: Throwable) {
        if (!isInitialized) {
            Log.w(TAG, "Crash reporting not initialized, logging to console", exception)
            return
        }

        try {
            FirebaseCrashlytics.getInstance().recordException(exception)
            Log.d(TAG, "Logged exception to Crashlytics", exception)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to log exception", e)
        }
    }

    /**
     * Log custom events
     */
    fun logEvent(eventName: String, parameters: Map<String, String> = emptyMap()) {
        if (!isInitialized) {
            Log.d(TAG, "Event: $eventName, Params: $parameters")
            return
        }

        try {
            val crashlytics = FirebaseCrashlytics.getInstance()
            crashlytics.log("Event: $eventName")

            // Set custom keys for the event
            parameters.forEach { (key, value) ->
                crashlytics.setCustomKey("event_$key", value)
            }

        } catch (e: Exception) {
            Log.e(TAG, "Failed to log event", e)
        }
    }

    /**
     * Set user identifier for crash reports
     * IMPORTANT: Never use personally identifiable information
     */
    fun setUserId(userId: String) {
        if (!isInitialized) return

        try {
            FirebaseCrashlytics.getInstance().setUserId(userId)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set user ID", e)
        }
    }

    /**
     * Set custom key-value pairs for crash context
     */
    fun setCustomKey(key: String, value: String) {
        if (!isInitialized) return

        try {
            FirebaseCrashlytics.getInstance().setCustomKey(key, value)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set custom key", e)
        }
    }

    /**
     * Force a crash for testing (DEBUG builds only)
     */
    fun testCrash() {
        if (BuildConfig.DEBUG) {
            throw RuntimeException("Test crash from CrashReporting")
        }
    }
}
EOF

# Create privacy-preserving analytics utility
cat > Toolkit/Analytics/src/main/kotlin/com/shareconnect/analytics/ShareConnectAnalytics.kt << 'EOF'
package com.shareconnect.analytics

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import java.security.MessageDigest

/**
 * Privacy-preserving analytics for ShareConnect applications
 * Only collects anonymous, aggregated usage statistics
 */
object ShareConnectAnalytics {

    private const val TAG = "ShareConnectAnalytics"
    private var isInitialized = false
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    // Privacy-preserving event names (no user-identifiable data)
    private const val EVENT_APP_LAUNCH = "app_launch"
    private const val EVENT_FEATURE_USED = "feature_used"
    private const val EVENT_SYNC_COMPLETED = "sync_completed"
    private const val EVENT_API_CALLED = "api_called"
    private const val EVENT_ERROR_OCCURRED = "error_occurred"

    /**
     * Initialize analytics for the application
     * Call this in Application.onCreate()
     */
    fun initialize(application: Application, appName: String) {
        if (isInitialized) {
            Log.w(TAG, "Analytics already initialized")
            return
        }

        try {
            firebaseAnalytics = FirebaseAnalytics.getInstance(application)

            // Disable collection of advertising ID
            firebaseAnalytics.setAnalyticsCollectionEnabled(true)

            // Set default event parameters (anonymous)
            val defaultParams = Bundle().apply {
                putString("app_name", appName)
                putString("platform", "android")
                putString("version_category", getVersionCategory())
            }

            firebaseAnalytics.setDefaultEventParameters(defaultParams)

            isInitialized = true
            Log.i(TAG, "Analytics initialized for $appName")

            // Log initial app launch
            logAppLaunch()

        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize analytics", e)
        }
    }

    /**
     * Log app launch event
     */
    fun logAppLaunch() {
        logEvent(EVENT_APP_LAUNCH)
    }

    /**
     * Log feature usage (anonymized)
     */
    fun logFeatureUsed(featureName: String) {
        val params = Bundle().apply {
            putString("feature_category", anonymizeString(featureName))
        }
        logEvent(EVENT_FEATURE_USED, params)
    }

    /**
     * Log sync completion
     */
    fun logSyncCompleted(syncType: String, itemCount: Int) {
        val params = Bundle().apply {
            putString("sync_type", anonymizeString(syncType))
            putInt("item_count_category", categorizeCount(itemCount))
        }
        logEvent(EVENT_SYNC_COMPLETED, params)
    }

    /**
     * Log API call (anonymized)
     */
    fun logApiCall(endpoint: String, success: Boolean) {
        val params = Bundle().apply {
            putString("endpoint_category", anonymizeString(endpoint))
            putBoolean("success", success)
        }
        logEvent(EVENT_API_CALLED, params)
    }

    /**
     * Log error occurrence (anonymized)
     */
    fun logError(errorType: String) {
        val params = Bundle().apply {
            putString("error_category", anonymizeString(errorType))
        }
        logEvent(EVENT_ERROR_OCCURRED, params)
    }

    /**
     * Internal event logging
     */
    private fun logEvent(eventName: String, params: Bundle? = null) {
        if (!isInitialized) {
            Log.d(TAG, "Event: $eventName, Params: $params")
            return
        }

        try {
            firebaseAnalytics.logEvent(eventName, params)
            Log.d(TAG, "Logged event: $eventName")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to log event", e)
        }
    }

    /**
     * Anonymize strings using SHA-256 hash
     * This ensures no personally identifiable information is collected
     */
    private fun anonymizeString(input: String): String {
        return try {
            val bytes = input.toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            digest.fold("") { str, it -> str + "%02x".format(it) }.substring(0, 16)
        } catch (e: Exception) {
            "unknown"
        }
    }

    /**
     * Categorize counts to avoid collecting exact metrics
     */
    private fun categorizeCount(count: Int): Int {
        return when {
            count == 0 -> 0
            count <= 5 -> 1
            count <= 20 -> 2
            count <= 100 -> 3
            count <= 1000 -> 4
            else -> 5
        }
    }

    /**
     * Get version category for analytics
     */
    private fun getVersionCategory(): String {
        return try {
            val versionCode = android.os.Build.VERSION.SDK_INT
            when {
                versionCode >= 35 -> "android_15_plus"
                versionCode >= 34 -> "android_14"
                versionCode >= 33 -> "android_13"
                versionCode >= 31 -> "android_12"
                versionCode >= 29 -> "android_10_11"
                else -> "android_9_minus"
            }
        } catch (e: Exception) {
            "unknown"
        }
    }

    /**
     * Enable/disable analytics collection
     */
    fun setAnalyticsEnabled(enabled: Boolean) {
        if (!isInitialized) return

        try {
            firebaseAnalytics.setAnalyticsCollectionEnabled(enabled)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set analytics enabled", e)
        }
    }

    /**
     * Reset analytics data (for privacy compliance)
     */
    fun resetAnalyticsData() {
        if (!isInitialized) return

        try {
            firebaseAnalytics.resetAnalyticsData()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to reset analytics data", e)
        }
    }
}
EOF

# Create Google Play Store deployment script
cat > deploy_play_store.sh << 'EOF'
#!/bin/bash

# Deploy ShareConnect connectors to Google Play Store
# This script handles the complete deployment process

set -e

echo "🎯 ShareConnect Google Play Store Deployment"
echo "==========================================="

# Configuration
PACKAGE_NAME=${1:-"com.shareconnect.all"}
TRACK=${2:-"internal"}  # internal, alpha, beta, production
APK_DIR="Connectors"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check prerequisites
check_prerequisites() {
    echo "🔍 Checking prerequisites..."

    if ! command -v fastlane &> /dev/null; then
        echo -e "${RED}❌ Fastlane not installed. Install with: brew install fastlane${NC}"
        exit 1
    fi

    if [ -z "$GOOGLE_PLAY_JSON_KEY" ]; then
        echo -e "${YELLOW}⚠️  GOOGLE_PLAY_JSON_KEY not set${NC}"
        echo "   Set this environment variable with your Google Play service account JSON"
        exit 1
    fi

    echo -e "${GREEN}✅ Prerequisites check passed${NC}"
}

# Build all release APKs
build_release_apks() {
    echo "🏗️  Building release APKs..."

    ./build_phase3_apps.sh release

    if [ $? -ne 0 ]; then
        echo -e "${RED}❌ Build failed${NC}"
        exit 1
    fi

    echo -e "${GREEN}✅ All APKs built successfully${NC}"
}

# Deploy to Play Store
deploy_to_play_store() {
    local connector=$1
    local apk_path="$APK_DIR/${connector}/${connector}Connector/build/outputs/apk/release/${connector}Connector-release.apk"

    if [ ! -f "$apk_path" ]; then
        echo -e "${YELLOW}⚠️  APK not found: $apk_path${NC}"
        return 1
    fi

    echo "📤 Deploying $connector to $TRACK track..."

    # Create temporary Fastfile
    cat > Fastfile << FASTFILE_EOF
lane :deploy_$connector do
  upload_to_play_store(
    package_name: '${connector,,}connect',
    apk: '$apk_path',
    track: '$TRACK',
    skip_upload_metadata: true,
    skip_upload_images: true,
    skip_upload_screenshots: true
  )
end
FASTFILE_EOF

    # Run fastlane
    if fastlane "deploy_$connector"; then
        echo -e "${GREEN}✅ $connector deployed successfully${NC}"
        return 0
    else
        echo -e "${RED}❌ $connector deployment failed${NC}"
        return 1
    fi
}

# Main deployment process
main() {
    check_prerequisites
    build_release_apks

    echo ""
    echo "🚀 Starting deployment to $TRACK track..."
    echo ""

    local success_count=0
    local total_count=0

    # Deploy each connector
    for connector_dir in "$APK_DIR"/*/; do
        if [ -d "$connector_dir" ]; then
            connector_name=$(basename "$connector_dir")
            # Remove "Connect" suffix for package name
            app_name="${connector_name%Connect}"

            ((total_count++))
            if deploy_to_play_store "$app_name"; then
                ((success_count++))
            fi
        fi
    done

    echo ""
    echo "📊 Deployment Summary:"
    echo "   Total connectors: $total_count"
    echo "   Successfully deployed: $success_count"
    echo "   Failed: $((total_count - success_count))"

    if [ $success_count -eq $total_count ]; then
        echo -e "${GREEN}🎉 All connectors deployed successfully!${NC}"
        echo ""
        echo "📱 Next steps:"
        echo "   1. Check Google Play Console for review status"
        echo "   2. Monitor crash reports and user feedback"
        echo "   3. Prepare release notes for users"
    else
        echo -e "${RED}⚠️  Some deployments failed. Check logs above.${NC}"
        exit 1
    fi
}

# Run main function
main "$@"
EOF

chmod +x deploy_play_store.sh

# Create F-Droid deployment script
cat > deploy_fdroid.sh << 'EOF'
#!/bin/bash

# Deploy ShareConnect connectors to F-Droid
# F-Droid requires manual submission and review

set -e

echo "📱 ShareConnect F-Droid Deployment"
echo "================================="

# Configuration
REPO_URL="https://gitlab.com/fdroid/fdroiddata"
APK_DIR="Connectors"

echo "📋 F-Droid Deployment Checklist:"
echo ""
echo "1. 🔍 Verify all apps meet F-Droid inclusion criteria:"
echo "   ✅ Open source license (GPL-3.0)"
echo "   ✅ No anti-features (no tracking, no ads)"
echo "   ✅ No proprietary dependencies"
echo "   ✅ Proper metadata and descriptions"
echo ""
echo "2. 📝 Prepare F-Droid metadata files:"
echo ""

# Generate metadata for each connector
for connector_dir in "$APK_DIR"/*/; do
    if [ -d "$connector_dir" ]; then
        connector_name=$(basename "$connector_dir")
        app_name="${connector_name%Connect}"
        package_name="com.shareconnect.${app_name,,}connect"

        echo "   📄 $package_name:"
        echo "      - Create: metadata/$package_name.yml"
        echo "      - Include app description, screenshots, changelog"
        echo ""
    fi
done

echo "3. 🚀 Submit to F-Droid:"
echo "   📋 Steps:"
echo "      1. Fork $REPO_URL"
echo "      2. Add metadata files for each connector"
echo "      3. Upload APKs to appropriate directories"
echo "      4. Create merge request"
echo "      5. Wait for F-Droid review and inclusion"
echo ""
echo "4. ⏱️ Timeline:"
echo "   - Initial review: 1-2 weeks"
echo "   - Inclusion in repository: 1-4 weeks after approval"
echo "   - User availability: 1-2 days after inclusion"
echo ""
echo "📞 Resources:"
echo "   - F-Droid inclusion guide: https://f-droid.org/en/docs/Inclusion_Policy/"
echo "   - Metadata format: https://f-droid.org/en/docs/Build_Metadata_Reference/"
echo "   - Example metadata: https://gitlab.com/fdroid/fdroiddata/-/tree/master/metadata"
echo ""
echo "🎯 F-Droid Benefits:"
echo "   ✅ Free and open source app repository"
echo "   ✅ No Google account required"
echo "   ✅ Automatic updates"
echo "   ✅ Privacy-focused user base"
EOF

chmod +x deploy_fdroid.sh

echo "✅ Created comprehensive production infrastructure:"
echo ""
echo "🔐 Signing Configuration:"
echo "   - signing.properties (secure configuration)"
echo "   - setup_signing.sh (keystore creation script)"
echo "   - Updated build.gradle.kts with signing config"
echo ""
echo "📊 Crash Reporting & Analytics:"
echo "   - Toolkit/CrashReporting/ (Firebase Crashlytics integration)"
echo "   - Toolkit/Analytics/ (privacy-preserving analytics)"
echo "   - firebase_crashlytics.gradle (build configuration)"
echo ""
echo "🚀 Deployment Scripts:"
echo "   - deploy_play_store.sh (Google Play Store deployment)"
echo "   - deploy_fdroid.sh (F-Droid deployment guide)"
echo ""
echo "🛡️ Security Features:"
echo "   - Secure keystore management"
echo "   - Environment variable configuration"
echo "   - Privacy-preserving analytics (no PII)"
echo "   - Comprehensive crash reporting"
echo ""
echo "📱 Distribution Channels:"
echo "   - Google Play Store (automated deployment)"
echo "   - F-Droid (manual submission guide)"
echo "   - GitHub Releases (direct APK downloads)"
echo ""
echo "🔧 Next Steps:"
echo "   1. Run ./setup_signing.sh to create keystore"
echo "   2. Configure Firebase projects for each connector"
echo "   3. Set up CI/CD secrets for automated deployment"
echo "   4. Test signed builds: ./gradlew assembleRelease"