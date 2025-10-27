# Contributing to ShareConnect

Thank you for your interest in contributing to ShareConnect! This document provides guidelines and information for contributors.

## 🚀 **Getting Started**

### **Prerequisites**
- **Android Studio**: Arctic Fox or later (2020.3.1+)
- **JDK 17**: Required for Android development
- **Git**: Version control system

### **Development Setup**

1. **Clone the repository**
   ```bash
   git clone https://github.com/milos85vasic/ShareConnect.git
   cd ShareConnect
   ```

2. **Initialize submodules**
   ```bash
   git submodule update --init --recursive
   ```

3. **Open in Android Studio**
   - Import the project as a Gradle project
   - Wait for Gradle sync to complete
   - Build and run any connector app

4. **Firebase Setup (Optional)**
   - Create Firebase projects for each connector
   - Download `google-services.json` files
   - Place them in respective app directories

## 📋 **Development Workflow**

### **1. Choose an Issue**
- Check [GitHub Issues](https://github.com/milos85vasic/ShareConnect/issues) for open tasks
- Look for issues labeled `good first issue` or `help wanted`
- Comment on the issue to indicate you're working on it

### **2. Create a Branch**
```bash
git checkout -b feature/your-feature-name
# or
git checkout -b fix/issue-number-description
```

### **3. Make Changes**
- Follow the existing code style and conventions
- Write tests for new functionality
- Update documentation as needed
- Ensure all tests pass

### **4. Commit Changes**
```bash
git add .
git commit -m "Brief description of changes

- Detailed explanation of what was changed
- Why the change was necessary
- Any breaking changes or migration notes"
```

### **5. Push and Create Pull Request**
```bash
git push origin your-branch-name
```
Then create a Pull Request on GitHub with a clear description.

## 🏗️ **Project Structure**

```
ShareConnect/
├── Connectors/           # Individual connector applications
│   ├── ShareConnector/   # Main app with sync capabilities
│   ├── qBitConnector/    # qBittorrent client
│   ├── TransmissionConnect/  # Transmission client
│   └── ...               # Other connectors
├── Toolkit/              # Shared libraries and modules
│   ├── Main/            # Core functionality
│   ├── Analytics/       # Analytics and tracking
│   ├── SecurityAccess/  # Authentication and security
│   └── Search/          # Unified search functionality
├── DesignSystem/        # UI components and theming
├── Asinka/              # Synchronization framework
├── Tests/               # Testing utilities
└── docs/                # Documentation
```

## 📝 **Code Style Guidelines**

### **Kotlin Style**
- Use `val` over `var` when possible
- Prefer immutable data structures
- Use meaningful variable and function names
- Follow Kotlin naming conventions (PascalCase for classes, camelCase for functions)

### **Android Specific**
- Use Material Design 3 components
- Follow Android architecture patterns (MVVM, Repository)
- Handle lifecycle properly
- Use coroutines for asynchronous operations

### **Imports**
- Group imports: standard library, third-party, local
- Remove unused imports
- Use wildcard imports sparingly

## 🧪 **Testing**

### **Unit Tests**
```bash
./gradlew :ConnectorName:testDebugUnitTest
```

### **Integration Tests**
```bash
./gradlew :ConnectorName:connectedDebugAndroidTest
```

### **AI QA Tests**
```bash
./run_ai_qa_tests.sh
```

### **All Tests**
```bash
./run_all_tests.sh
```

## 🔧 **Adding a New Connector**

1. **Create the connector structure**
   ```bash
   mkdir Connectors/NewConnector
   mkdir Connectors/NewConnector/NewConnectorApp
   ```

2. **Set up build.gradle**
   - Copy from an existing connector
   - Update package name and dependencies
   - Configure signing and Firebase

3. **Implement core functionality**
   - Create API client for the service
   - Implement data models
   - Add UI components
   - Integrate with sync framework

4. **Add to main build**
   - Update `settings.gradle` to include the new module
   - Add to CI/CD pipeline matrix

## 🚨 **Reporting Issues**

### **Bug Reports**
- Use the bug report template
- Include steps to reproduce
- Provide device information and Android version
- Attach logs and screenshots

### **Feature Requests**
- Check if the feature already exists
- Describe the use case clearly
- Explain why it's needed
- Consider implementation complexity

### **Security Issues**
- **DO NOT** create public issues for security vulnerabilities
- Email security@shareconnect.dev with details
- Allow time for investigation before public disclosure

## 📚 **Documentation**

### **Code Documentation**
- Use KDoc for public APIs
- Document complex logic and edge cases
- Keep comments up to date

### **User Documentation**
- Update README files for new features
- Provide usage examples
- Document configuration options

## 🤝 **Community Guidelines**

### **Be Respectful**
- Treat all contributors with respect
- Constructive criticism is welcome
- Disagreements should be handled professionally

### **Communication**
- Use clear and concise language
- Provide context for your suggestions
- Be patient with new contributors

### **Code Reviews**
- Review code objectively
- Suggest improvements constructively
- Acknowledge good work

## 📄 **License**

By contributing to ShareConnect, you agree that your contributions will be licensed under the same license as the project (see LICENSE file).

## 🙏 **Recognition**

Contributors will be recognized in:
- GitHub repository contributors list
- CHANGELOG.md for significant contributions
- Project documentation

Thank you for contributing to ShareConnect! 🎉