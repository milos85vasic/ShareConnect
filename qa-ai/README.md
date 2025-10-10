# ShareConnect AI QA System

[![AI QA Tests](https://img.shields.io/badge/AI%20QA-Active-brightgreen?style=flat-square&logo=android)](qa-ai/)
[![Test Coverage](https://img.shields.io/badge/Coverage-Comprehensive-blue?style=flat-square)](testbank/)
[![AI Powered](https://img.shields.io/badge/AI-Claude%203.5-purple?style=flat-square&logo=anthropic)](https://www.anthropic.com/)

An advanced AI-powered quality assurance system for ShareConnect that uses Claude AI to intelligently test all application functionality, including edge cases and complex scenarios.

## Overview

The AI QA system is a comprehensive automated testing framework that combines traditional Android testing approaches (UIAutomator, Espresso) with AI-powered test interpretation, execution, and analysis. It provides:

- **Intelligent Test Execution**: AI interprets test cases and adapts to UI changes
- **Visual Validation**: Screenshot analysis using Claude's vision capabilities
- **Comprehensive Coverage**: All ShareConnect features, profiles, sync, and edge cases
- **Automated Failure Analysis**: Root cause identification and fix suggestions
- **Extensible Test Bank**: Easy-to-modify YAML/JSON test case definitions

## Key Features

### AI-Powered Testing
- **Test Interpretation**: Natural language test case understanding
- **Visual Verification**: Screenshot-based UI validation
- **Adaptive Execution**: Handles UI variations and dynamic content
- **Failure Diagnosis**: AI-powered root cause analysis
- **Fix Suggestions**: Automated fix recommendations

### Comprehensive Test Coverage
- **Profile Management**: All service types (MeTube, YT-DLP, Torrent clients, jDownloader)
- **Synchronization**: Multi-device sync scenarios with conflict resolution
- **UI Flows**: Onboarding, settings, navigation, sharing
- **Edge Cases**: Invalid inputs, boundary values, special characters, malformed data
- **Integration**: End-to-end user journeys
- **Performance**: App responsiveness, memory, battery metrics

### Test Data Generation
- **Profile Generator**: All profile type combinations
- **Edge Case Generator**: Comprehensive edge case scenarios
- **Sync Data Generator**: Multi-device sync test data
- **Mock Services**: Simulated server responses

### Reporting & Analysis
- **HTML Reports**: Visual test execution reports
- **JSON Reports**: Machine-readable results
- **Screenshot Gallery**: Step-by-step visual verification
- **Trend Analysis**: Historical test data analysis
- **Coverage Metrics**: Test coverage statistics

## Architecture

```
qa-ai/
├── src/main/kotlin/com/shareconnect/qa/ai/
│   ├── models/           # Data models for tests and results
│   ├── generators/       # Test data generators
│   ├── testbank/         # Test case bank management
│   ├── executor/         # AI-powered test execution
│   ├── emulator/         # Android emulator/device interaction
│   ├── analyzers/        # Result analysis and reporting
│   └── fixers/           # Automated fix suggestions
├── testbank/             # Test case definitions (YAML/JSON)
│   ├── profiles/         # Profile management tests
│   ├── sync/             # Synchronization tests
│   ├── ui/               # UI flow tests
│   ├── edge-cases/       # Edge case scenarios
│   ├── integration/      # Integration tests
│   └── suites/           # Test suites
├── testdata/             # Generated test data
├── reports/              # Test execution reports
├── screenshots/          # Captured screenshots
├── qa-config.yaml        # Configuration file
└── ARCHITECTURE.md       # Detailed architecture documentation
```

## Quick Start

### Prerequisites

1. **Android Emulator or Device**
   - API Level 28 (Android 9.0) or higher
   - USB debugging enabled
   - Device unlocked during test execution

2. **API Keys**
   - Anthropic Claude API key (for AI features)
   - Set environment variable: `export ANTHROPIC_API_KEY=your_key_here`

3. **Dependencies**
   - All dependencies are handled via Gradle

### Running AI QA Tests

#### Run All AI QA Tests
```bash
./run_ai_qa_tests.sh
```

#### Run Specific Test Suite
```bash
./run_ai_qa_tests.sh --suite smoke_test_suite
```

#### Run Specific Test Category
```bash
./run_ai_qa_tests.sh --category PROFILE_MANAGEMENT
```

#### Generate Test Data
```bash
./gradlew :qa-ai:generateTestData
```

### Viewing Results

After test execution, reports are generated in:
- **HTML Reports**: `qa-ai/reports/html/index.html`
- **JSON Results**: `qa-ai/reports/json/test_results.json`
- **Screenshots**: `qa-ai/screenshots/[test_id]/`

## Configuration

The AI QA system is configured via `qa-config.yaml`. Key configuration options:

### AI Settings
```yaml
ai:
  provider: "anthropic"              # AI provider (anthropic, openai, local)
  model: "claude-3-5-sonnet-20241022"  # Model to use
  api_key_env: "ANTHROPIC_API_KEY"   # Environment variable for API key
  max_tokens: 4096                   # Maximum tokens per request
  temperature: 0.7                   # Response creativity (0.0-1.0)
```

### Test Execution Settings
```yaml
test_execution:
  parallel_tests: 4                  # Number of parallel test executions
  retry_on_failure: 2                # Number of retries for failed tests
  screenshot_on_failure: true        # Capture screenshots on failure
  video_recording: true              # Record video of test execution
  max_test_duration_minutes: 30      # Maximum time per test case
```

### Emulator Settings
```yaml
emulator:
  api_level: 34                      # Android API level
  device: "pixel_6"                  # Device profile
  enable_network_simulation: true    # Simulate network conditions
  enable_video_recording: true       # Record test execution
```

## Test Case Bank

Test cases are defined in YAML format for easy modification and extension.

### Example Test Case

```yaml
id: "TC_PROF_001"
name: "Create MeTube Profile"
description: "Test creating a new MeTube server profile"
category: "PROFILE_MANAGEMENT"
priority: "HIGH"
tags:
  - "profile"
  - "metube"
  - "create"

preconditions:
  - "App is installed and running"
  - "User is on the main screen"

steps:
  - step_number: 1
    action: "tap"
    description: "Tap on the 'Add Profile' button"
    target: "buttonAddFirstProfile"
    expected_outcome: "Edit Profile screen opens"
    screenshot: true

  - step_number: 2
    action: "input"
    description: "Enter profile name"
    target: "editTextProfileName"
    input:
      text: "My MeTube Server"
    expected_outcome: "Profile name is entered"

  - step_number: 3
    action: "tap"
    description: "Tap Save button"
    target: "buttonSave"
    expected_outcome: "Profile is saved"
    screenshot: true

expected_results:
  - "Profile is created successfully"
  - "Profile appears in the profiles list"
```

### Adding New Test Cases

1. Create a new YAML file in the appropriate `testbank/` subdirectory
2. Follow the test case schema (see existing examples)
3. Run the test bank validator: `./gradlew :qa-ai:validateTestBank`
4. Execute your new test: `./run_ai_qa_tests.sh --test TC_YOUR_ID`

## Test Suites

Test suites group related test cases for organized execution.

### Available Test Suites

- **Smoke Test Suite** (`smoke_test_suite`): Critical functionality tests
- **Full Regression Suite** (`full_regression_suite`): Comprehensive test coverage
- **Profile Management Suite**: All profile-related tests
- **Sync Test Suite**: All synchronization tests
- **Edge Case Suite**: All edge case scenarios

### Running Test Suites

```bash
# Run smoke tests
./run_ai_qa_tests.sh --suite smoke_test_suite

# Run full regression
./run_ai_qa_tests.sh --suite full_regression_suite

# Run custom suite
./run_ai_qa_tests.sh --suite custom_suite_id
```

## AI Features

### Visual Validation

The AI QA system uses Claude's vision capabilities to analyze screenshots and verify UI states:

```kotlin
// AI automatically validates UI by analyzing screenshots
val result = aiClient.analyzeScreenshot(
    screenshotBase64 = screenshot,
    question = "Is the profile 'My MeTube Server' visible in the list?",
    context = "Profile creation test"
)
```

### Adaptive Test Execution

The AI can interpret ambiguous or complex test steps:

```kotlin
// AI interprets natural language actions
val step = TestStep(
    action = "verify_profile_appears",
    description = "Check that the newly created profile shows up correctly"
)
// AI determines the appropriate verification strategy
```

### Failure Analysis

When tests fail, AI provides detailed analysis:

```
STATUS: FAIL
ACTUAL_OUTCOME: Profile list is empty, expected profile not found
REASON: The profile creation appears to have failed - no confirmation dialog appeared
SUGGESTION: Check if the save button is properly enabled and accessible
```

## Advanced Usage

### Custom Test Data

Generate custom test data programmatically:

```kotlin
val profileGenerator = ProfileDataGenerator()
val profiles = profileGenerator.generateAllProfiles()

// Generate edge case scenarios
val edgeCaseGenerator = EdgeCaseGenerator()
val edgeCases = edgeCaseGenerator.generateAllEdgeCases()
```

### Programmatic Test Execution

Execute tests programmatically:

```kotlin
val testBank = TestBank("qa-ai/testbank")
val testCase = testBank.getTestCase("TC_PROF_001")

val executor = AITestExecutor(aiClient, emulatorBridge)
val result = executor.executeTestCase(testCase)

println("Test Status: ${result.status}")
println("Duration: ${result.durationMs}ms")
```

### Custom Analyzers

Create custom result analyzers:

```kotlin
class CustomAnalyzer : ResultAnalyzer {
    override fun analyze(results: List<TestResult>): AnalysisReport {
        // Your custom analysis logic
    }
}
```

## Troubleshooting

### Common Issues

**1. AI API Errors**
```
Error: API request failed: 401 Unauthorized
```
**Solution**: Verify your `ANTHROPIC_API_KEY` environment variable is set correctly.

**2. Emulator Connection Issues**
```
Error: No devices found
```
**Solution**:
- Ensure emulator is running: `adb devices`
- Check USB debugging is enabled
- Reconnect device: `adb reconnect`

**3. Test Timeouts**
```
Error: Test exceeded maximum duration
```
**Solution**: Increase `max_test_duration_minutes` in `qa-config.yaml`

**4. Screenshot Capture Failures**
```
Error: Failed to capture screenshot
```
**Solution**:
- Ensure device screen is unlocked
- Check storage permissions
- Verify UIAutomator access

### Debug Commands

```bash
# Check connected devices
adb devices -l

# View AI QA system logs
adb logcat | grep "AI-QA"

# Check test execution status
./gradlew :qa-ai:checkTestStatus

# Validate test bank
./gradlew :qa-ai:validateTestBank

# Generate test data
./gradlew :qa-ai:generateTestData
```

## Performance Optimization

### Parallel Test Execution

Run tests in parallel for faster execution:

```yaml
test_execution:
  parallel_tests: 8  # Adjust based on your hardware
```

### Test Filtering

Run only specific tests to save time:

```bash
# Run only high-priority tests
./run_ai_qa_tests.sh --priority HIGH

# Run only smoke tests
./run_ai_qa_tests.sh --tag smoke
```

### Caching

Enable test result caching:

```yaml
reporting:
  enable_caching: true
  cache_duration_hours: 24
```

## Integration with CI/CD

### GitHub Actions

```yaml
name: AI QA Tests
on: [push, pull_request]

jobs:
  ai-qa:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK
        uses: actions/setup-java@v2
        with:
          java-version: '17'
      - name: Run AI QA Tests
        env:
          ANTHROPIC_API_KEY: ${{ secrets.ANTHROPIC_API_KEY }}
        run: ./run_ai_qa_tests.sh --suite smoke_test_suite
      - name: Upload Reports
        uses: actions/upload-artifact@v2
        with:
          name: ai-qa-reports
          path: qa-ai/reports/
```

### Jenkins

```groovy
pipeline {
    agent any
    environment {
        ANTHROPIC_API_KEY = credentials('anthropic-api-key')
    }
    stages {
        stage('AI QA Tests') {
            steps {
                sh './run_ai_qa_tests.sh --suite full_regression_suite'
            }
        }
        stage('Publish Reports') {
            steps {
                publishHTML([
                    reportDir: 'qa-ai/reports/html',
                    reportFiles: 'index.html',
                    reportName: 'AI QA Report'
                ])
            }
        }
    }
}
```

## Best Practices

### Writing Test Cases

1. **Clear Descriptions**: Write descriptive test names and steps
2. **Proper Preconditions**: Always specify required preconditions
3. **Expected Outcomes**: Define clear, verifiable expected outcomes
4. **Screenshots**: Capture screenshots at key verification points
5. **Tags**: Use appropriate tags for easy filtering

### Test Data Management

1. **Version Control**: Keep test data in version control
2. **Data Isolation**: Use separate data for different test suites
3. **Cleanup**: Always clean up test data after execution
4. **Realistic Data**: Use realistic test data that mirrors production

### Maintenance

1. **Regular Updates**: Keep test cases updated with app changes
2. **Review Failures**: Investigate and fix failing tests promptly
3. **Coverage Monitoring**: Track test coverage and fill gaps
4. **Performance Monitoring**: Monitor test execution times

## Documentation

- **[ARCHITECTURE.md](ARCHITECTURE.md)**: Detailed system architecture
- **[USER_MANUAL.md](USER_MANUAL.md)**: Comprehensive user manual
- **[API_REFERENCE.md](API_REFERENCE.md)**: API documentation
- **[CONTRIBUTING.md](CONTRIBUTING.md)**: Contributing guidelines

## Support

For issues, questions, or contributions:
- Review existing documentation in `qa-ai/`
- Check the main [ShareConnect documentation](../Documentation/)
- Refer to test execution logs in `qa-ai/reports/`

## License

This AI QA system is part of the ShareConnect project and follows the same MIT License.

---

**Powered by Claude AI** | **ShareConnect AI QA System** | **Version 1.0.0**
