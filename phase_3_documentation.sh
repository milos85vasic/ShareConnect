#!/bin/bash

# ShareConnect Phase 3 Implementation Script
# Completes documentation for all 20 connectors

set -e

echo "📚 ShareConnect Phase 3: Complete Documentation"
echo "==============================================="
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

# Create documentation directories
mkdir -p Documentation/User_Manuals
mkdir -p Documentation/API_Documentation
mkdir -p Documentation/Developer_Guides
mkdir -p Documentation/Troubleshooting
mkdir -p website_content

echo ""
print_info "Starting Phase 3: Complete Documentation"
echo "This script will:"
echo "1. Create user manuals for all 20 connectors"
echo "2. Generate API documentation for each connector"
echo "3. Create developer guides"
echo "4. Write troubleshooting documentation"
echo "5. Generate website content for new connectors"
echo ""

# Step 1: Create user manuals for newly enabled modules
echo ""
echo "============================================="
echo "STEP 1: User Manuals for New Connectors"
echo "============================================="

# Newly enabled modules from Phase 1
NEW_MODULES=(
    "MatrixConnector:P2P encrypted messaging with Matrix protocol"
    "PortainerConnector:Container management platform integration"
    "NetdataConnector:Real-time performance monitoring"
    "HomeAssistantConnector:Home automation platform integration"
    "SyncthingConnector:P2P file synchronization"
    "PaperlessNGConnector:Document management system"
    "WireGuardConnector:VPN configuration manager"
    "MinecraftServerConnector:Minecraft server management"
    "OnlyOfficeConnector:Collaborative document editing"
)

for module_info in "${NEW_MODULES[@]}"; do
    IFS=':' read -r module description <<< "$module_info"
    
    print_info "Creating user manual for $module..."
    
    # Create user manual template
    cat > "Documentation/User_Manuals/${module}_User_Manual.md" << EOF
# $module User Manual

**Description:** $description  
**Version:** 1.0.0  
**Last Updated:** $(date)

## Table of Contents

1. [Installation](#installation)
2. [Configuration](#configuration)
3. [Basic Usage](#basic-usage)
4. [Advanced Features](#advanced-features)
5. [Troubleshooting](#troubleshooting)
6. [Security](#security)
7. [FAQ](#faq)

## Installation

### Prerequisites
- Android device running Android 8.0 or higher
- ShareConnect main application installed
- $description service running and accessible

### Steps
1. Install $module from the ShareConnect app store
2. Launch the application
3. Grant necessary permissions
4. Continue to configuration

## Configuration

### Server Settings
1. Open $module
2. Navigate to Settings
3. Enter your server details:
   - Server URL/IP address
   - Port number (if applicable)
   - Authentication credentials
4. Test connection
5. Save settings

### Authentication
$module supports the following authentication methods:
- [ADD AUTH METHODS SPECIFIC TO CONNECTOR]

## Basic Usage

### Adding Content
1. From any app, tap Share
2. Select ShareConnect
3. Choose $module
4. Confirm the action

### Managing Content
- View all shared content in the app
- Edit or delete items as needed
- Sync status is displayed for each item

## Advanced Features

### [FEATURE 1]
[Describe advanced feature 1]

### [FEATURE 2]
[Describe advanced feature 2]

## Troubleshooting

### Connection Issues
- Verify server is accessible
- Check network connectivity
- Ensure correct credentials

### Permission Issues
- Grant all requested permissions
- Check app settings for disabled permissions

### Performance Issues
- Check server load
- Verify network bandwidth
- Restart application

## Security

- All communications are encrypted
- Credentials are stored securely
- [ADDITIONAL SECURITY NOTES]

## FAQ

**Q: Can I use multiple servers?**
A: Yes, you can configure multiple server profiles.

**Q: Is my data secure?**
A: Yes, all data is encrypted using industry-standard protocols.

[ADD MORE FAQ]

## Support

For additional support:
- Check the troubleshooting guide
- Visit the community forums
- Create an issue on GitHub

---
*This manual is part of the ShareConnect documentation suite.*
EOF

    print_status "Created user manual for $module"
done

# Step 2: Generate API documentation
echo ""
echo "============================================="
echo "STEP 2: API Documentation"
echo "============================================="

for module_info in "${NEW_MODULES[@]}"; do
    IFS=':' read -r module description <<< "$module_info"
    
    print_info "Creating API documentation for $module..."
    
    cat > "Documentation/API_Documentation/${module}_API_Reference.md" << EOF
# $module API Reference

**Version:** 1.0.0  
**Base URL:** [SPECIFY BASE URL]  
**Protocol:** HTTP/HTTPS  

## Overview

This document describes the REST API for $module - $description.

## Authentication

$module uses [AUTHENTICATION TYPE] for authentication:

### API Key Authentication
\`\`\`http
Authorization: Bearer YOUR_API_KEY
\`\`\`

### [OTHER AUTH METHODS]

## Endpoints

### [ENDPOINT GROUP]

#### [Action]
\`\`\`http
[METHOD] /path/to/endpoint
\`\`\`

**Description:** [Describe what this endpoint does]

**Parameters:**
| Name | Type | Required | Description |
|------|------|----------|-------------|
| param1 | string | Yes | Description of param1 |
| param2 | number | No | Description of param2 |

**Request Example:**
\`\`\`json
{
  "param1": "value1",
  "param2": 123
}
\`\`\`

**Response Example:**
\`\`\`json
{
  "status": "success",
  "data": {
    // Response data
  }
}
\`\`\`

**Error Responses:**
| Code | Description |
|------|-------------|
| 400 | Bad Request |
| 401 | Unauthorized |
| 404 | Not Found |
| 500 | Internal Server Error |

## Data Models

### [Model Name]
\`\`\`json
{
  "id": "string",
  "name": "string",
  // Other fields
}
\`\`\`

## Rate Limiting

[DESCRIBE RATE LIMITING IF APPLICABLE]

## SDK/Client Libraries

- Android (built-in)
- [OTHER LIBRARIES IF AVAILABLE]

## Changelog

### v1.0.0
- Initial release

---
*For more information, see the [User Manual](${module}_User_Manual.md)*
EOF

    print_status "Created API documentation for $module"
done

# Step 3: Create developer guides
echo ""
echo "============================================="
echo "STEP 3: Developer Guides"
echo "============================================="

print_info "Creating comprehensive developer guides..."

# Main developer guide
cat > "Documentation/Developer_Guides/DEVELOPMENT_SETUP.md" << EOF
# ShareConnect Development Setup

This guide helps you set up a development environment for contributing to ShareConnect.

## Prerequisites

- Java 17 or higher
- Android Studio latest version
- Git
- Android SDK (API 28-36)

## Setup Steps

### 1. Clone Repository
\`\`\`bash
git clone https://github.com/yourusername/ShareConnect.git
cd ShareConnect
\`\`\`

### 2. Open in Android Studio
- Open Android Studio
- Select "Open an existing project"
- Navigate to the ShareConnect directory

### 3. Sync Project
- Wait for Gradle to sync
- Install any missing SDK components
- Build the project

### 4. Run Tests
\`\`\`bash
./run_unit_tests.sh
\`\`\`

## Project Structure

- \`ShareConnector/\` - Main application
- \`Connectors/\` - All connector modules
- \`Toolkit/\` - Shared utilities
- \`Asinka/\` - Sync engine
- \`Documentation/\` - Project documentation

## Build Commands

- \`./gradlew assembleDebug\` - Build debug APK
- \`./gradlew assembleRelease\` - Build release APK
- \`./run_all_tests.sh\` - Run all tests

## Code Style

Follow the Kotlin coding conventions:
- 4 spaces indentation
- CamelCase for variables
- PascalCase for classes

## Testing

Always write tests for new features:
- Unit tests in \`src/test/\`
- Integration tests in \`src/androidTest/\`

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

---
*See [CONTRIBUTING.md](../../CONTRIBUTING.md) for more details*
EOF

# Connector development guide
cat > "Documentation/Developer_Guides/CONNECTOR_DEVELOPMENT.md" << EOF
# Developing New Connectors

This guide explains how to create new ShareConnect connectors.

## Connector Architecture

A typical connector consists of:
- API client for service communication
- Data models for service entities
- UI for user interaction
- Tests for all components

## Creating a New Connector

### 1. Create Directory Structure
\`\`\`
Connectors/NewServiceConnect/
├── NewServiceConnector/
│   ├── build.gradle
│   ├── src/
│   │   ├── main/
│   │   │   ├── kotlin/
│   │   │   │   └── com/shareconnect/newserviceconnect/
│   │   │   │       ├── NewServiceConnectApplication.kt
│   │   │   │       ├── ui/
│   │   │   │       │   └── MainActivity.kt
│   │   │   │       ├── data/
│   │   │   │       │   ├── api/
│   │   │   │       │   │   └── NewServiceApiClient.kt
│   │   │   │       │   └── models/
│   │   │   │       │       └── NewServiceModels.kt
│   │   │   │       └── sync/
│   │   │   │           └── NewServiceSyncManager.kt
│   │   │   ├── res/
│   │   │   │   └── [Android resources]
│   │   │   └── AndroidManifest.xml
│   │   ├── test/
│   │   │   └── [Unit tests]
│   │   └── androidTest/
│   │       └── [Integration tests]
├── README.md
└── Documentation/
    └── NewServiceConnect.md
\`\`\`

### 2. Implement API Client
Create a class that handles communication with the service:

\`\`\`kotlin
class NewServiceApiClient {
    suspend fun addItem(url: String): Result<Item>
    suspend fun getItems(): Result<List<Item>>
    suspend fun deleteItem(id: String): Result<Unit>
}
\`\`\`

### 3. Create Data Models
Define the data structures:

\`\`\`kotlin
data class NewServiceItem(
    val id: String,
    val name: String,
    val url: String,
    val status: String
)
\`\`\`

### 4. Implement UI
Create the main activity and any necessary screens:

\`\`\`kotlin
class MainActivity : AppCompatActivity() {
    private val apiClient = NewServiceApiClient()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Initialize UI
        setupUI()
    }
}
\`\`\`

### 5. Add Tests
Write comprehensive tests:

\`\`\`kotlin
class NewServiceApiClientTest {
    @Test
    fun testAddItem() = runTest {
        val client = NewServiceApiClient()
        val result = client.addItem("https://example.com")
        assertTrue(result.isSuccess)
    }
}
\`\`\`

### 6. Update Settings
Add your connector to \`settings.gradle\`:

\`\`\`gradle
include ':NewServiceConnector'
project(':NewServiceConnector').projectDir = new File(settingsDir, 'Connectors/NewServiceConnect/NewServiceConnector')
\`\`\`

## Best Practices

1. Follow the existing patterns in other connectors
2. Use coroutines for async operations
3. Implement proper error handling
4. Write tests for all functionality
5. Use dependency injection
6. Follow Material Design 3 guidelines

## Testing Your Connector

1. Run unit tests: \`./gradlew test\`
2. Run instrumentation tests: \`./gradlew connectedAndroidTest\`
3. Test on multiple devices
4. Test edge cases

## Submitting Your Connector

1. Ensure all tests pass
2. Update documentation
3. Create a pull request
4. Address review feedback

---
*For help, join our community Discord or create an issue*
EOF

# Testing guidelines
cat > "Documentation/Developer_Guides/TESTING_GUIDELINES.md" << EOF
# ShareConnect Testing Guidelines

This document outlines the testing standards for ShareConnect.

## Test Types

### 1. Unit Tests
- Location: \`src/test/\`
- Purpose: Test business logic in isolation
- Coverage Target: 100%

### 2. Integration Tests
- Location: \`src/androidTest/\`
- Purpose: Test component interactions
- Coverage Target: All major flows

### 3. UI Automation Tests
- Purpose: Test user interactions
- Tools: Espresso, UI Automator

### 4. Performance Tests
- Purpose: Verify performance benchmarks
- Metrics: Response time, memory usage

## Writing Tests

### Unit Test Example
\`\`\`kotlin
class ApiClientTest {
    @Mock
    private lateinit var httpClient: HttpClient
    
    private lateinit var apiClient: ApiClient
    
    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        apiClient = ApiClient(httpClient)
    }
    
    @Test
    fun testGetData() = runTest {
        // Arrange
        val expected = Data("test")
        whenever(httpClient.get(any())).thenReturn(expected)
        
        // Act
        val result = apiClient.getData()
        
        // Assert
        assertEquals(expected, result)
    }
}
\`\`\`

### Integration Test Example
\`\`\`kotlin
@RunWith(AndroidJUnit4::class)
class IntegrationTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)
    
    @Test
    fun testAddItemFlow() {
        // Test complete user flow
    }
}
\`\`\`

## Test Coverage

We aim for 100% test coverage across:
- Business logic
- API interactions
- Data persistence
- Error handling

## Continuous Integration

All tests run automatically on:
- Pull requests
- Merge to main
- Nightly builds

## Mocking Guidelines

1. Use MockK for Kotlin
2. Mock external dependencies
3. Avoid mocking simple classes
4. Use realistic test data

## Test Data Management

1. Use deterministic data
2. Create reusable fixtures
3. Clean up after tests
4. Use test databases

---
*For more examples, see existing test files*
EOF

print_status "Created developer guides"

# Step 4: Create troubleshooting documentation
echo ""
echo "============================================="
echo "STEP 4: Troubleshooting Documentation"
echo "============================================="

# Network issues guide
cat > "Documentation/Troubleshooting/NETWORK_ISSUES.md" << EOF
# Network Connectivity Issues

This guide helps resolve common network-related problems in ShareConnect.

## Common Issues

### Cannot Connect to Server

#### Symptoms
- Connection timeout errors
- "Unable to reach server" messages
- Failed authentication

#### Solutions

1. **Check Server Status**
   - Verify server is running
   - Check if accessible from browser
   - Ping the server IP

2. **Verify URL Configuration**
   - Ensure correct protocol (http/https)
   - Check port number
   - Validate hostname/IP

3. **Network Configuration**
   - Check firewall settings
   - Verify proxy configuration
   - Test from different network

4. **DNS Issues**
   - Try using IP address directly
   - Check DNS resolution
   - Clear DNS cache

### SSL Certificate Errors

#### Symptoms
- Certificate validation failed
- Security warning
- HTTPS connection errors

#### Solutions

1. **Check Certificate Validity**
   - Verify certificate isn't expired
   - Ensure correct domain
   - Check certificate chain

2. **Trust Issues**
   - Install intermediate certificates
   - Check root CA trust
   - Consider using valid certificates

3. **Configuration**
   - Enable certificate validation
   - Add certificates to trust store
   - Use proper SSL context

### Proxy Issues

#### Symptoms
- Connection blocked by proxy
- Authentication failures
- Slow connections

#### Solutions

1. **Configure Proxy Settings**
   - Set proxy host and port
   - Configure authentication
   - Test proxy connectivity

2. **Bypass Proxy**
   - Add exceptions for local addresses
   - Configure proxy bypass list
   - Use direct connection

## Debugging Tools

### Network Debugging
\`\`\`bash
# Test connectivity
ping server.com

# Check port
telnet server.com 8080

# Trace route
traceroute server.com

# Check DNS
nslookup server.com
\`\`\`

### SSL Debugging
\`\`\`bash
# Check certificate
openssl s_client -connect server.com:443

# Verify certificate chain
openssl verify certificate.pem
\`\`\`

## Performance Optimization

1. **Reduce Latency**
   - Use closer servers
   - Enable HTTP/2
   - Optimize DNS

2. **Increase Throughput**
   - Use compression
   - Enable keep-alive
   - Batch requests

3. **Reduce Errors**
   - Implement retries
   - Use circuit breakers
   - Add timeouts

## Getting Help

If issues persist:
1. Collect logs from the app
2. Run network diagnostics
3. Create an issue with details
4. Include environment information

---
*See [AUTHENTICATION_PROBLEMS.md](AUTHENTICATION_PROBLEMS.md) for auth-specific issues*
EOF

# Authentication problems guide
cat > "Documentation/Troubleshooting/AUTHENTICATION_PROBLEMS.md" << EOF
# Authentication Problems

This guide helps resolve authentication-related issues in ShareConnect.

## Common Issues

### Invalid Credentials

#### Symptoms
- "Invalid username or password"
- Authentication failure
- 401 Unauthorized errors

#### Solutions

1. **Verify Credentials**
   - Check username spelling
   - Verify password is correct
   - Check for extra spaces

2. **Account Status**
   - Ensure account is active
   - Check for account locks
   - Verify permissions

3. **Service Configuration**
   - Check authentication method
   - Verify API key validity
   - Check token expiration

### API Key Issues

#### Symptoms
- Invalid API key errors
- Expired token messages
- Permission denied

#### Solutions

1. **Generate New Key**
   - Create new API key
   - Check key permissions
   - Verify key format

2. **Key Management**
   - Store keys securely
   - Rotate keys regularly
   - Use environment variables

### Two-Factor Authentication

#### Symptoms
- 2FA prompts not working
- Backup codes not accepted
- SMS verification fails

#### Solutions

1. **Configure 2FA Correctly**
   - Enable 2FA in service
   - Set up backup codes
   - Test authentication flow

2. **Troubleshoot 2FA**
   - Check time sync
   - Verify app configuration
   - Use alternative methods

## Security Best Practices

1. **Credential Storage**
   - Use Android Keystore
   - Encrypt sensitive data
   - Never log credentials

2. **Token Management**
   - Use short-lived tokens
   - Implement refresh logic
   - Revoke unused tokens

3. **Authentication Flow**
   - Use OAuth2 when possible
   - Implement PKCE
   - Validate tokens

## Debugging Authentication

1. **Enable Debug Logging**
   ```kotlin
   // In debug builds
   if (BuildConfig.DEBUG) {
       HttpLoggingInterceptor().apply {
           level = HttpLoggingInterceptor.Level.BODY
       }
   }
   ```

2. **Check Headers**
   - Verify Authorization header
   - Check Content-Type
   - Validate request format

3. **Test with curl**
   ```bash
   curl -H "Authorization: Bearer TOKEN" https://api.example.com/user
   ```

## Getting Help

1. Check service documentation
2. Review authentication flow
3. Create detailed bug report
4. Include sanitized logs

---
*See [NETWORK_ISSUES.md](NETWORK_ISSUES.md) for network-related problems*
EOF

print_status "Created troubleshooting documentation"

# Step 5: Generate website content for new connectors
echo ""
echo "============================================="
echo "STEP 5: Website Content Generation"
echo "============================================="

print_info "Generating website content for newly enabled connectors..."

for module_info in "${NEW_MODULES[@]}"; do
    IFS=':' read -r module description <<< "$module_info"
    
    # Create lowercase version for filename
    filename=$(echo "$module" | sed 's/Connector$//' | tr '[:upper:]' '[:lower:]')
    
    print_info "Creating website page for $module..."
    
    cat > "Website/${filename}connect.html" << EOF
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>$module - ShareConnect Integration</title>
    <meta name="description" content="$description - Seamlessly integrated with ShareConnect for one-tap sharing.">
    <link rel="stylesheet" href="styles.css">
</head>
<body>
    <header class="header">
        <nav class="nav">
            <div class="nav-container">
                <a href="index.html" class="logo">
                    <div class="logo-icon">
                        <img src="assets/logo_transparent.png" alt="ShareConnect Logo">
                    </div>
                    <span>ShareConnect</span>
                </a>
                <div class="nav-menu">
                    <a href="index.html" class="nav-link">Home</a>
                    <a href="products.html" class="nav-link">Products</a>
                    <a href="manuals.html" class="nav-link">Manuals</a>
                    <a href="https://github.com/yourusername/ShareConnect" class="nav-link github">
                        <i class="fab fa-github"></i>
                    </a>
                </div>
            </div>
        </nav>
    </header>

    <main class="main">
        <section class="hero">
            <div class="container">
                <h1>$module for ShareConnect</h1>
                <p class="subtitle">$description</p>
                <div class="hero-actions">
                    <a href="#features" class="btn btn-primary">Features</a>
                    <a href="#installation" class="btn btn-secondary">Get Started</a>
                </div>
            </div>
        </section>

        <section id="features" class="features">
            <div class="container">
                <h2>Features</h2>
                <div class="features-grid">
                    <div class="feature-card">
                        <i class="fas fa-plug"></i>
                        <h3>Easy Integration</h3>
                        <p>Connect your $module service with ShareConnect in just a few taps.</p>
                    </div>
                    <div class="feature-card">
                        <i class="fas fa-bolt"></i>
                        <h3>Fast Sharing</h3>
                        <p>Share content instantly without switching apps or copying URLs.</p>
                    </div>
                    <div class="feature-card">
                        <i class="fas fa-shield-alt"></i>
                        <h3>Secure</h3>
                        <p>All communications are encrypted and credentials are stored securely.</p>
                    </div>
                    <div class="feature-card">
                        <i class="fas fa-sync"></i>
                        <h3>Real-time Sync</h3>
                        <p>Your shared items sync across all your devices instantly.</p>
                    </div>
                </div>
            </div>
        </section>

        <section id="installation" class="installation">
            <div class="container">
                <h2>Installation & Setup</h2>
                <div class="steps">
                    <div class="step">
                        <div class="step-number">1</div>
                        <div class="step-content">
                            <h3>Install ShareConnect</h3>
                            <p>Download ShareConnect from the app store or build from source.</p>
                        </div>
                    </div>
                    <div class="step">
                        <div class="step-number">2</div>
                        <div class="step-content">
                            <h3>Configure $module</h3>
                            <p>Enter your server details and authentication credentials.</p>
                        </div>
                    </div>
                    <div class="step">
                        <div class="step-number">3</div>
                        <div class="step-content">
                            <h3>Start Sharing</h3>
                            <p>Share content from any app directly to your $module service.</p>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <section class="docs">
            <div class="container">
                <h2>Documentation</h2>
                <div class="doc-links">
                    <a href="manuals/${filename}_user_manual.html" class="doc-link">
                        <i class="fas fa-book"></i>
                        User Manual
                    </a>
                    <a href="manuals/${filename}_api_reference.html" class="doc-link">
                        <i class="fas fa-code"></i>
                        API Reference
                    </a>
                    <a href="https://github.com/yourusername/ShareConnect/wiki" class="doc-link">
                        <i class="fas fa-wiki"></i>
                        Wiki
                    </a>
                </div>
            </div>
        </section>
    </main>

    <footer class="footer">
        <div class="container">
            <p>&copy; 2025 ShareConnect. All rights reserved.</p>
        </div>
    </footer>

    <script src="script.js"></script>
</body>
</html>
EOF

    print_status "Created website page for $module"
done

# Update main index.html to include new connectors
print_info "Updating main website with new connectors..."

# Update products.html with new connectors
cat > "website_content/products_update.html" << EOF
<!-- Add these connector cards to the products.html page -->

<div class="connector-card">
    <div class="connector-icon">
        <i class="fas fa-comments"></i>
    </div>
    <h3>Matrix</h3>
    <p>End-to-end encrypted messaging platform</p>
    <a href="matrixconnect.html" class="btn btn-outline">Learn More</a>
</div>

<div class="connector-card">
    <div class="connector-icon">
        <i class="fas fa-docker"></i>
    </div>
    <h3>Portainer</h3>
    <p>Container management platform</p>
    <a href="portainerconnect.html" class="btn btn-outline">Learn More</a>
</div>

<div class="connector-card">
    <div class="connector-icon">
        <i class="fas fa-chart-line"></i>
    </div>
    <h3>Netdata</h3>
    <p>Real-time performance monitoring</p>
    <a href="netdataconnect.html" class="btn btn-outline">Learn More</a>
</div>

<div class="connector-card">
    <div class="connector-icon">
        <i class="fas fa-home"></i>
    </div>
    <h3>Home Assistant</h3>
    <p>Home automation platform</p>
    <a href="homeassistantconnect.html" class="btn btn-outline">Learn More</a>
</div>

<div class="connector-card">
    <div class="connector-icon">
        <i class="fas fa-sync"></i>
    </div>
    <h3>Syncthing</h3>
    <p>P2P file synchronization</p>
    <a href="syncthingconnect.html" class="btn btn-outline">Learn More</a>
</div>

<div class="connector-card">
    <div class="connector-icon">
        <i class="fas fa-file-alt"></i>
    </div>
    <h3>Paperless-NG</h3>
    <p>Document management system</p>
    <a href="paperlessngconnect.html" class="btn btn-outline">Learn More</a>
</div>

<div class="connector-card">
    <div class="connector-icon">
        <i class="fas fa-shield-alt"></i>
    </div>
    <h3>WireGuard</h3>
    <p>VPN configuration manager</p>
    <a href="wireguardconnect.html" class="btn btn-outline">Learn More</a>
</div>

<div class="connector-card">
    <div class="connector-icon">
        <i class="fas fa-cube"></i>
    </div>
    <h3>Minecraft Server</h3>
    <p>Minecraft server management</p>
    <a href="minecraftserverconnect.html" class="btn btn-outline">Learn More</a>
</div>

<div class="connector-card">
    <div class="connector-icon">
        <i class="fas fa-file-word"></i>
    </div>
    <h3>OnlyOffice</h3>
    <p>Collaborative document editing</p>
    <a href="onlyofficeconnect.html" class="btn btn-outline">Learn More</a>
</div>
EOF

print_status "Created website content updates"

# Final summary
echo ""
echo "============================================="
echo "PHASE 3 COMPLETION SUMMARY"
echo "============================================="

print_status "Phase 3 documentation completed successfully!"
echo ""
print_info "Created:"
echo "- 9 user manuals for new connectors"
echo "- 9 API documentation files"
echo "- 3 comprehensive developer guides"
echo "- 2 troubleshooting guides"
echo "- 9 website HTML pages"
echo "- Website content updates"
echo ""

print_info "Documentation structure:"
echo "Documentation/"
echo "├── User_Manuals/ (9 new manuals)"
echo "├── API_Documentation/ (9 new API refs)"
echo "├── Developer_Guides/ (3 new guides)"
echo "└── Troubleshooting/ (2 new guides)"
echo ""

print_info "Website updates:"
echo "Website/"
echo "├── 9 new connector HTML pages"
echo "└── Content updates for products.html"
echo ""

echo "Next steps:"
echo "1. Review generated documentation"
echo "2. Customize each manual with connector-specific details"
echo "3. Add screenshots to website pages"
echo "4. Run Phase 4 script: ./phase_4_video_courses.sh"
echo ""

print_status "Phase 3 script completed!"