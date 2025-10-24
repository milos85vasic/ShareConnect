# PlexConnect Signing Configuration

## Overview
PlexConnect uses standard Android signing configuration for release builds. This document explains how to set up signing for production deployment.

## Prerequisites

### Required Tools
- **Java Development Kit (JDK)**: For keytool command
- **Android Studio**: Optional, for GUI keystore management
- **Gradle**: For build automation

### Keystore Requirements
- **Algorithm**: RSA with 2048-bit key size
- **Validity**: Minimum 25 years (9125 days) for app store distribution
- **Password**: Strong passwords for both keystore and key

## Setting Up Production Signing

### Step 1: Create Production Keystore

```bash
# Navigate to project root
cd /path/to/ShareConnect

# Create signing directory
mkdir -p Connectors/PlexConnect/Signing

# Generate keystore
keytool -genkey -v -keystore Connectors/PlexConnect/Signing/release.jks \
  -alias plexconnect_release \
  -keyalg RSA \
  -keysize 2048 \
  -validity 9125 \
  -storepass your_strong_store_password \
  -keypass your_strong_key_password \
  -dname "CN=PlexConnect Release, OU=Development, O=ShareConnect, L=City, ST=State, C=US"
```

### Step 2: Configure Environment Variables

Create a `.env.properties` file in the project root (already in .gitignore):

```properties
# PlexConnect Release Signing
PLEXCONNECT_RELEASE_KEY_ALIAS=plexconnect_release
PLEXCONNECT_RELEASE_KEY_PASSWORD=your_strong_key_password
PLEXCONNECT_RELEASE_STORE_PASSWORD=your_strong_store_password
PLEXCONNECT_RELEASE_KEYSTORE_PATH=Connectors/PlexConnect/Signing/release.jks
```

### Step 3: Update Build Configuration

Add signing configuration to `Connectors/PlexConnect/PlexConnector/build.gradle`:

```gradle
android {
    signingConfigs {
        release {
            if (project.hasProperty('PLEXCONNECT_RELEASE_KEYSTORE_PATH')) {
                storeFile file(PLEXCONNECT_RELEASE_KEYSTORE_PATH)
                storePassword PLEXCONNECT_RELEASE_STORE_PASSWORD
                keyAlias PLEXCONNECT_RELEASE_KEY_ALIAS
                keyPassword PLEXCONNECT_RELEASE_KEY_PASSWORD
            }
        }
    }

    buildTypes {
        release {
            minifyEnabled true
            shrinkResources true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
            signingConfig signingConfigs.release
        }
    }
}
```

## Build Commands

### Debug Build (No Signing Required)
```bash
./gradlew :PlexConnector:assembleDebug
```

### Release Build (With Signing)
```bash
# Set environment variables or use .env.properties
./gradlew :PlexConnector:assembleRelease
```

### Verify APK Signature
```bash
# Check APK signature
jarsigner -verify -verbose -certs Connectors/PlexConnect/PlexConnector/build/outputs/apk/release/PlexConnector-release.apk

# Get signature info
keytool -printcert -jarfile Connectors/PlexConnect/PlexConnector/build/outputs/apk/release/PlexConnector-release.apk
```

## App Store Preparation

### Google Play Store Requirements

#### APK Requirements
- **Target SDK**: API 33 or higher (PlexConnect uses API 36)
- **Min SDK**: API 24 or higher (PlexConnect uses API 24)
- **Signing**: Release APK must be signed with production keystore
- **Size**: APK size under 150MB (PlexConnect is ~15MB)

#### Metadata Requirements
- **App Name**: PlexConnect
- **Package Name**: com.shareconnect.plexconnect
- **Category**: Media & Video
- **Content Rating**: Suitable for all audiences
- **Privacy Policy**: Required for app store submission

### Pre-Release Checklist

#### Technical Checklist
- [ ] Release APK builds successfully with production signing
- [ ] APK signature verification passes
- [ ] ProGuard obfuscation working correctly
- [ ] All tests pass on release build
- [ ] APK size within acceptable limits
- [ ] No debug code or logging in release build

#### Content Checklist
- [ ] App icon and screenshots prepared
- [ ] App description and changelog written
- [ ] Privacy policy created
- [ ] Content rating questionnaire completed
- [ ] Target audience and content warnings set

#### Testing Checklist
- [ ] Functionality tested on release APK
- [ ] Performance tested on target devices
- [ ] Compatibility tested across Android versions
- [ ] Security testing completed
- [ ] Accessibility testing performed

## Security Best Practices

### Keystore Security
- **Storage**: Store keystore in secure, backed-up location
- **Passwords**: Use strong, unique passwords
- **Access**: Limit access to signing credentials
- **Backup**: Create encrypted backup of keystore
- **Version Control**: Never commit keystore or passwords to VCS

### Password Management
```bash
# Example of secure password generation
openssl rand -base64 32  # Generate 32-character random password
```

### Environment Security
- Use environment variables or encrypted storage for passwords
- Implement proper access controls for build machines
- Use CI/CD secrets management for automated builds
- Regularly rotate signing passwords

## Troubleshooting

### Common Issues

#### Keystore Not Found
```
Error: Keystore file not found for signing config 'release'
```
**Solution**: Verify keystore path in environment variables or .env.properties

#### Invalid Password
```
Error: Failed to read key from keystore
```
**Solution**: Check keystore and key passwords in configuration

#### Certificate Issues
```
Error: Certificate expired or not yet valid
```
**Solution**: Ensure keystore validity period covers app store requirements

#### Build Configuration
```
Error: Cannot read property 'PLEXCONNECT_RELEASE_KEYSTORE_PATH'
```
**Solution**: Ensure .env.properties file exists and is properly formatted

### Debug Commands

#### Check Environment Variables
```bash
# List all signing-related environment variables
env | grep PLEXCONNECT
```

#### Verify Keystore
```bash
# Check keystore contents
keytool -list -v -keystore Connectors/PlexConnect/Signing/release.jks
```

#### Test Build Configuration
```bash
# Dry run of release build
./gradlew :PlexConnector:assembleRelease --dry-run
```

## Release Process

### Automated Release (CI/CD)
```yaml
# Example GitHub Actions workflow
- name: Build Release APK
  run: |
    echo "${{ secrets.PLEXCONNECT_KEYSTORE_BASE64 }}" | base64 -d > keystore.jks
    ./gradlew :PlexConnector:assembleRelease
  env:
    PLEXCONNECT_RELEASE_KEYSTORE_PATH: keystore.jks
    PLEXCONNECT_RELEASE_KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}
    PLEXCONNECT_RELEASE_STORE_PASSWORD: ${{ secrets.STORE_PASSWORD }}
```

### Manual Release
1. Update version numbers in build.gradle
2. Update changelog and release notes
3. Build release APK with signing
4. Verify APK signature and functionality
5. Upload to app store with metadata
6. Monitor release for issues

## Maintenance

### Keystore Rotation
- Plan keystore rotation before expiration
- Use Android App Bundle signing key upgrade
- Test key rotation process thoroughly
- Document rotation procedures

### Version Management
- Update versionCode for each release
- Maintain consistent versionName format
- Document breaking changes
- Keep changelog up to date

---

**Document Version:** 1.0.0  
**Last Updated:** 2025-10-24  
**Next Review:** 2026-04-24