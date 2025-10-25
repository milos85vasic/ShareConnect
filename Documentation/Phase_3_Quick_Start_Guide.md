# Phase 3 Applications - Quick Start Guide

**Last Updated**: October 26, 2025

This guide helps you quickly build, test, and deploy all Phase 3 ShareConnect applications.

---

## 🚀 Quick Commands

### Build All Phase 3 Apps
```bash
cd /home/milosvasic/Projects/ShareConnect

# Build all Phase 3 apps (debug)
./gradlew :SeafileConnector:assembleDebug \
          :SyncthingConnector:assembleDebug \
          :MatrixConnector:assembleDebug \
          :PaperlessNGConnector:assembleDebug \
          :DuplicatiConnector:assembleDebug \
          :WireGuardConnector:assembleDebug \
          :MinecraftServerConnector:assembleDebug \
          :OnlyOfficeConnector:assembleDebug

# Build all Phase 3 apps (release)
./gradlew :SeafileConnector:assembleRelease \
          :SyncthingConnector:assembleRelease \
          :MatrixConnector:assembleRelease \
          :PaperlessNGConnector:assembleRelease \
          :DuplicatiConnector:assembleRelease \
          :WireGuardConnector:assembleRelease \
          :MinecraftServerConnector:assembleRelease \
          :OnlyOfficeConnector:assembleRelease
```

### Test All Phase 3 Apps
```bash
# Run all unit tests
./gradlew :SeafileConnector:test \
          :SyncthingConnector:test \
          :MatrixConnector:test \
          :PaperlessNGConnector:test \
          :DuplicatiConnector:test \
          :WireGuardConnector:test \
          :MinecraftServerConnector:test \
          :OnlyOfficeConnector:test

# Run all instrumentation tests (requires device/emulator)
./gradlew :SeafileConnector:connectedAndroidTest \
          :SyncthingConnector:connectedAndroidTest \
          :MatrixConnector:connectedAndroidTest \
          :PaperlessNGConnector:connectedAndroidTest \
          :DuplicatiConnector:connectedAndroidTest \
          :WireGuardConnector:connectedAndroidTest \
          :MinecraftServerConnector:connectedAndroidTest \
          :OnlyOfficeConnector:connectedAndroidTest
```

---

## 📱 Individual App Commands

### SeafileConnect
```bash
# Build
./gradlew :SeafileConnector:assembleDebug

# Test
./gradlew :SeafileConnector:test
./gradlew :SeafileConnector:connectedAndroidTest

# Install on device
./gradlew :SeafileConnector:installDebug

# APK Location
# build/outputs/apk/debug/SeafileConnector-debug.apk
```

### SyncthingConnect
```bash
./gradlew :SyncthingConnector:assembleDebug
./gradlew :SyncthingConnector:test
./gradlew :SyncthingConnector:installDebug
```

### MatrixConnect
```bash
./gradlew :MatrixConnector:assembleDebug
./gradlew :MatrixConnector:test
./gradlew :MatrixConnector:installDebug
```

### PaperlessNGConnect
```bash
./gradlew :PaperlessNGConnector:assembleDebug
./gradlew :PaperlessNGConnector:test
./gradlew :PaperlessNGConnector:installDebug
```

### DuplicatiConnect
```bash
./gradlew :DuplicatiConnector:assembleDebug
./gradlew :DuplicatiConnector:test
./gradlew :DuplicatiConnector:installDebug
```

### WireGuardConnect
```bash
./gradlew :WireGuardConnector:assembleDebug
./gradlew :WireGuardConnector:test
./gradlew :WireGuardConnector:installDebug
```

### MinecraftServerConnect
```bash
./gradlew :MinecraftServerConnector:assembleDebug
./gradlew :MinecraftServerConnector:test
./gradlew :MinecraftServerConnector:installDebug
```

### OnlyOfficeConnect
```bash
./gradlew :OnlyOfficeConnector:assembleDebug
./gradlew :OnlyOfficeConnector:test
./gradlew :OnlyOfficeConnector:installDebug
```

---

## 🧪 Testing Guide

### Unit Tests
Unit tests run on the JVM using Robolectric:

```bash
# Run specific test class
./gradlew :SeafileConnector:test --tests SeafileApiClientTest

# Run with coverage
./gradlew :SeafileConnector:testDebugUnitTest --info

# Generate test report
./gradlew :SeafileConnector:test
# Report: build/reports/tests/testDebugUnitTest/index.html
```

### Integration Tests
Integration tests run on Android devices/emulators:

```bash
# List connected devices
adb devices

# Run on specific device
ANDROID_SERIAL=emulator-5554 ./gradlew :SeafileConnector:connectedAndroidTest

# Run specific test
./gradlew :SeafileConnector:connectedAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=SeafileApiClientIntegrationTest
```

### Automation Tests
UI automation tests using Compose Test:

```bash
# Run all automation tests
./gradlew :SeafileConnector:connectedAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.package=com.shareconnect.seafileconnect

# View test results
# build/reports/androidTests/connected/index.html
```

---

## 🔧 Common Tasks

### Clean Build
```bash
# Clean specific app
./gradlew :SeafileConnector:clean

# Clean all Phase 3 apps
./gradlew :SeafileConnector:clean \
          :SyncthingConnector:clean \
          :MatrixConnector:clean \
          :PaperlessNGConnector:clean \
          :DuplicatiConnector:clean \
          :WireGuardConnector:clean \
          :MinecraftServerConnector:clean \
          :OnlyOfficeConnector:clean
```

### View Dependencies
```bash
# Check dependencies for an app
./gradlew :SeafileConnector:dependencies

# Check for dependency conflicts
./gradlew :SeafileConnector:dependencyInsight --dependency retrofit
```

### Generate APKs
```bash
# Generate all APKs
./gradlew assembleDebug

# APKs will be in:
# Connectors/*/build/outputs/apk/debug/*.apk
```

---

## 📦 APK Locations

After building, APKs are located at:

```
Connectors/SeafileConnect/SeafileConnector/build/outputs/apk/debug/
Connectors/SyncthingConnect/SyncthingConnector/build/outputs/apk/debug/
Connectors/MatrixConnect/MatrixConnector/build/outputs/apk/debug/
Connectors/PaperlessNGConnect/PaperlessNGConnector/build/outputs/apk/debug/
Connectors/DuplicatiConnect/DuplicatiConnector/build/outputs/apk/debug/
Connectors/WireGuardConnect/WireGuardConnector/build/outputs/apk/debug/
Connectors/MinecraftServerConnect/MinecraftServerConnector/build/outputs/apk/debug/
Connectors/OnlyOfficeConnect/OnlyOfficeConnector/build/outputs/apk/debug/
```

---

## 🐛 Troubleshooting

### Build Failures

**Problem**: Gradle sync fails
```bash
# Solution: Clean and rebuild
./gradlew clean
./gradlew build --refresh-dependencies
```

**Problem**: Duplicate class errors
```bash
# Solution: Check for version conflicts
./gradlew :SeafileConnector:dependencies | grep -i "duplicate\|conflict"
```

**Problem**: Out of memory
```bash
# Solution: Increase heap size
export GRADLE_OPTS="-Xmx4096m -XX:MaxPermSize=512m"
./gradlew assembleDebug
```

### Test Failures

**Problem**: Tests fail on CI
```bash
# Solution: Run with more logging
./gradlew test --info --stacktrace
```

**Problem**: Integration tests fail
```bash
# Solution: Check device/emulator is running
adb devices

# Ensure emulator is ready
adb shell getprop sys.boot_completed
# Should return: 1
```

### Runtime Issues

**Problem**: App crashes on startup
```bash
# Check logs
adb logcat | grep -i "shareconnect\|crash\|exception"

# Clear app data
adb shell pm clear com.shareconnect.seafileconnect
```

**Problem**: Sync managers not starting
```bash
# Check for port conflicts
adb logcat | grep -i "bind\|port\|asinka"
```

---

## 🔐 Security Configuration

### Debug Keystore
Apps use debug keystore for development:
```
~/.android/debug.keystore
```

### Release Signing
For release builds, configure in `env.properties`:
```properties
SHARECONNECT_CLOUD_KEYSTORE_PATH=Signing/cloud.jks
SHARECONNECT_CLOUD_KEY_ALIAS=shareconnect
SHARECONNECT_CLOUD_KEY_PASSWORD=***
SHARECONNECT_CLOUD_STORE_PASSWORD=***
```

---

## 📊 Performance Testing

### Build Performance
```bash
# Profile build
./gradlew :SeafileConnector:assembleDebug --profile

# Report: build/reports/profile/profile-*.html
```

### App Performance
```bash
# Monitor memory usage
adb shell dumpsys meminfo com.shareconnect.seafileconnect

# CPU profiling
adb shell am profile start com.shareconnect.seafileconnect /sdcard/profile.trace
# Use app...
adb shell am profile stop com.shareconnect.seafileconnect
adb pull /sdcard/profile.trace
```

---

## 🚀 Deployment

### Firebase App Distribution
```bash
# Deploy to Firebase (if configured)
./gradlew :SeafileConnector:assembleDebug \
          :SeafileConnector:appDistributionUploadDebug
```

### Google Play Store
```bash
# Generate signed release bundle
./gradlew :SeafileConnector:bundleRelease

# Bundle location:
# build/outputs/bundle/release/SeafileConnector-release.aab
```

---

## 📝 Development Workflow

### Typical Development Cycle
```bash
# 1. Make changes to code
# 2. Run unit tests
./gradlew :SeafileConnector:test

# 3. Build debug APK
./gradlew :SeafileConnector:assembleDebug

# 4. Install on device
./gradlew :SeafileConnector:installDebug

# 5. Test manually or run automation tests
./gradlew :SeafileConnector:connectedAndroidTest

# 6. Check logs
adb logcat | grep SeafileConnect
```

---

## 🔍 Code Quality

### Lint Checks
```bash
# Run lint
./gradlew :SeafileConnector:lint

# Report: build/reports/lint-results-debug.html
```

### Code Coverage
```bash
# Generate coverage report
./gradlew :SeafileConnector:createDebugCoverageReport

# Report: build/reports/coverage/debug/index.html
```

---

## 📚 Additional Resources

- **Technical Documentation**: `Connectors/*/Documentation/*.md`
- **Build Scripts**: `build.gradle` files
- **Test Reports**: `build/reports/`
- **Project Documentation**: `Documentation/`

---

## ✅ Quick Verification

Run this to verify everything works:

```bash
# 1. Clean build
./gradlew clean

# 2. Build all apps
./gradlew assembleDebug

# 3. Run all tests
./gradlew test

# If all succeeds, you're ready to go! ✅
```

---

**Last Updated**: October 26, 2025  
**Status**: ✅ All Phase 3 apps ready for development
