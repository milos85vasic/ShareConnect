# Phase 3 Applications - README

**Status**: ✅ Production Ready  
**Version**: 1.0.0  
**Last Updated**: October 26, 2025

---

## 📱 Applications Overview

Phase 3 of ShareConnect includes **8 specialized applications** for advanced cloud services, encryption, and server management.

### Quick Links
- [Build Guide](#building)
- [Testing Guide](#testing)
- [Deployment Guide](#deployment)
- [Troubleshooting](#troubleshooting)

---

## 🗂️ Applications

### 1. SeafileConnect
**Encrypted Cloud Storage Integration**

- **Package**: `com.shareconnect.seafileconnect`
- **Purpose**: Connect to Seafile servers with AES-256 encryption
- **Key Features**:
  - Encrypted library support
  - File upload/download
  - Directory management
  - Full-text search
  - PBKDF2 key derivation

📚 [Documentation](SeafileConnect/Documentation/SeafileConnect.md) | [User Manual](SeafileConnect/Documentation/SeafileConnect_User_Manual.md)

---

### 2. SyncthingConnect
**P2P File Synchronization**

- **Package**: `com.shareconnect.syncthingconnect`
- **Purpose**: Decentralized file sync via Syncthing protocol
- **Key Features**:
  - P2P synchronization
  - Real-time updates (SSE)
  - Folder/device management
  - Sync statistics
  - Auto-discovery

📚 [Documentation](SyncthingConnect/Documentation/SyncthingConnect.md)

---

### 3. MatrixConnect
**End-to-End Encrypted Messaging**

- **Package**: `com.shareconnect.matrixconnect`
- **Purpose**: Matrix protocol messaging with E2EE
- **Key Features**:
  - Olm/Megolm encryption
  - Device key management
  - Room synchronization
  - Multi-device support
  - Forward secrecy

📚 [Documentation](MatrixConnect/Documentation/MatrixConnect.md)

---

### 4. PaperlessNGConnect
**Document Management System**

- **Package**: `com.shareconnect.paperlessngconnect`
- **Purpose**: Paperless-ngx document management
- **Key Features**:
  - Document upload/download
  - PDF rendering
  - OCR metadata
  - Tag management
  - Full-text search

📚 [Documentation](PaperlessNGConnect/Documentation/PaperlessNGConnect.md)

---

### 5. DuplicatiConnect
**Backup Management**

- **Package**: `com.shareconnect.duplicaticonnect`
- **Purpose**: Duplicati backup system control
- **Key Features**:
  - Backup job management
  - Restore operations
  - Progress monitoring
  - Storage backends
  - Incremental backups

📚 [Documentation](DuplicatiConnect/Documentation/DuplicatiConnect.md)

---

### 6. WireGuardConnect
**VPN Configuration Manager**

- **Package**: `com.shareconnect.wireguardconnect`
- **Purpose**: WireGuard VPN configuration and management
- **Key Features**:
  - Config file parsing
  - QR code generation/scanning
  - Tunnel management
  - Multiple profiles
  - Key management

📚 [Documentation](WireGuardConnect/Documentation/WireGuardConnect.md)

---

### 7. MinecraftServerConnect
**Minecraft Server Management**

- **Package**: `com.shareconnect.minecraftserverconnect`
- **Purpose**: Minecraft server control via RCON
- **Key Features**:
  - RCON protocol
  - Command execution
  - Server ping/status
  - Player management
  - Custom commands

📚 [Documentation](MinecraftServerConnect/Documentation/MinecraftServerConnect.md)

---

### 8. OnlyOfficeConnect
**Document Editing Platform**

- **Package**: `com.shareconnect.onlyofficeconnect`
- **Purpose**: OnlyOffice collaborative editing
- **Key Features**:
  - WebView editing
  - Collaborative editing
  - Document conversion
  - Real-time sync
  - Multiple formats

📚 [Documentation](OnlyOfficeConnect/Documentation/OnlyOfficeConnect.md)

---

## 🏗️ Building

### Quick Build (All Apps)
```bash
cd /home/milosvasic/Projects/ShareConnect
./build_phase3_apps.sh debug
```

### Individual App Build
```bash
# Debug build
./gradlew :SeafileConnector:assembleDebug

# Release build
./gradlew :SeafileConnector:assembleRelease
```

### APK Locations
```
Connectors/[AppName]/[AppName]Connector/build/outputs/apk/debug/
Connectors/[AppName]/[AppName]Connector/build/outputs/apk/release/
```

---

## 🧪 Testing

### Run All Tests
```bash
./test_phase3_apps.sh all
```

### Unit Tests Only
```bash
./test_phase3_apps.sh unit
```

### Integration Tests (requires device)
```bash
./test_phase3_apps.sh integration
```

### Test Summary
| Application | Unit | Integration | Automation | Total |
|-------------|------|-------------|------------|-------|
| SeafileConnect | 29 | 20 | 11 | 60 |
| SyncthingConnect | 38 | 25 | 10 | 73 |
| MatrixConnect | 58 | 55 | 11 | 124 |
| PaperlessNGConnect | 26 | 21 | 8 | 55 |
| DuplicatiConnect | 24 | 19 | 8 | 51 |
| WireGuardConnect | 33 | 17 | 7 | 57 |
| MinecraftServerConnect | 33 | 17 | 8 | 58 |
| OnlyOfficeConnect | 18 | 20 | 7 | 45 |
| **TOTAL** | **279** | **194** | **70** | **523** |

---

## 📲 Installation

### Install All Apps
```bash
./install_phase3_apps.sh
```

### Install Single App
```bash
./gradlew :SeafileConnector:installDebug
```

---

## 🔧 Configuration

All apps share common configuration:

### Asinka Sync Modules (8 per app)
- ThemeSync (port 8890)
- ProfileSync (port 8900)
- HistorySync (port 8910)
- RSSSync (port 8920)
- BookmarkSync (port 8930)
- PreferencesSync (port 8940)
- LanguageSync (port 8950)
- TorrentSharingSync (port 8960)

### Security
- SQLCipher database encryption
- PIN/biometric authentication (SecurityAccess)
- HTTPS-only connections
- Secure credential storage

---

## 📚 Documentation

### Technical Documentation
Each app has comprehensive technical documentation in:
```
Connectors/[AppName]/Documentation/[AppName].md
```

### User Manuals
Where applicable:
```
Connectors/[AppName]/Documentation/[AppName]_User_Manual.md
```

### Project Documentation
- [Phase 3 Complete Summary](../Documentation/Phase_3_Implementation_Complete_Summary.md)
- [Quick Start Guide](../Documentation/Phase_3_Quick_Start_Guide.md)
- [Verification Report](../Documentation/Phase_3_Verification_Report.md)

---

## 🚀 Deployment

### Debug Builds
- Signed with debug keystore
- ProGuard disabled
- Logging enabled

### Release Builds
- Signed with release keystore (configure in `env.properties`)
- ProGuard enabled
- Optimized for production

### Firebase App Distribution
```bash
./gradlew :SeafileConnector:assembleDebug \
          :SeafileConnector:appDistributionUploadDebug
```

---

## 🐛 Troubleshooting

### Common Issues

**Build fails with duplicate class errors**
```bash
./gradlew --stop
./gradlew clean
./gradlew build --refresh-dependencies
```

**Tests fail on CI**
```bash
./gradlew test --info --stacktrace
```

**App crashes on startup**
```bash
adb logcat | grep -E "shareconnect|AndroidRuntime"
```

**Sync managers not starting**
- Check for port conflicts in logs
- Verify all 8 sync managers are initialized
- Check Asinka service is running

---

## 🔍 Code Structure

Each app follows the same structure:

```
[AppName]Connector/
├── build.gradle                   # Dependencies and build config
├── proguard-rules.pro            # ProGuard rules
├── src/
│   ├── main/
│   │   ├── AndroidManifest.xml
│   │   ├── kotlin/com/shareconnect/[appname]/
│   │   │   ├── [AppName]Application.kt
│   │   │   ├── ui/MainActivity.kt
│   │   │   └── data/
│   │   │       ├── api/          # API clients
│   │   │       └── models/       # Data models
│   │   └── res/
│   │       ├── values/strings.xml
│   │       └── xml/network_security_config.xml
│   ├── test/                      # Unit tests
│   └── androidTest/               # Integration tests
└── Documentation/
    └── [AppName].md              # Technical docs
```

---

## 💻 Development

### Adding New Features
1. Implement in appropriate layer (data/UI)
2. Add unit tests
3. Add integration tests
4. Update documentation
5. Test manually

### Code Style
- Kotlin official style guide
- MVVM architecture pattern
- Result-based error handling
- Coroutines for async operations

---

## 📊 Metrics

### Code Metrics
- **Total Lines**: ~15,000
- **Source Files**: 80+
- **Data Models**: 50+
- **API Endpoints**: 90+

### Test Coverage
- **Average Coverage**: 85%+
- **Total Tests**: 523
- **Test Code**: ~8,000 lines

---

## ✅ Quality Checklist

Before release, ensure:

- [ ] All tests passing
- [ ] No lint warnings
- [ ] ProGuard rules tested
- [ ] Release build successful
- [ ] App installs correctly
- [ ] Sync managers working
- [ ] SecurityAccess functional
- [ ] Documentation updated

---

## 🤝 Contributing

When contributing to Phase 3 apps:

1. Follow existing code structure
2. Maintain test coverage (min 80%)
3. Update documentation
4. Add tests for new features
5. Follow Kotlin best practices

---

## 📞 Support

- **Documentation**: See `Documentation/` folder
- **Issue Tracking**: GitHub Issues
- **Wiki**: https://deepwiki.com/vasic-digital/ShareConnect

---

## 📄 License

See LICENSE file in project root.

---

**Phase 3 Complete**: ✅  
**All 8 Applications**: Production Ready  
**Total Tests**: 523  
**Documentation**: Comprehensive

*Built with ❤️ as part of the ShareConnect ecosystem*
