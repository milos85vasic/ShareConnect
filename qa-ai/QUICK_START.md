# ShareConnect AI QA - Quick Start Guide

Get started with the AI-powered QA system in 5 minutes!

## Prerequisites

1. **Android Emulator or Physical Device**
   - Android 9.0 (API 28) or higher
   - USB debugging enabled
   - Device unlocked

2. **Claude API Key**
   ```bash
   export ANTHROPIC_API_KEY=sk-ant-xxxxxxxxxxxxx
   ```
   Get your API key from: https://console.anthropic.com/

3. **Build Tools**
   - JDK 17
   - Android SDK
   - Gradle (included via wrapper)

## Installation

The AI QA module is already integrated into the ShareConnect project. No additional installation needed!

## First Run

### Step 1: Set Your API Key

```bash
# Add to your ~/.bashrc or ~/.zshrc
export ANTHROPIC_API_KEY=your_api_key_here

# Or set for current session only
export ANTHROPIC_API_KEY=your_api_key_here
```

### Step 2: Start an Emulator or Connect Device

```bash
# Check connected devices
adb devices

# You should see:
# List of devices attached
# emulator-5554   device
```

### Step 3: Run Smoke Tests

```bash
# Run critical path tests (takes ~5 minutes)
./run_ai_qa_tests.sh --suite smoke_test_suite
```

### Step 4: View Results

Open the HTML report in your browser:
```bash
# The script will print the report location
# Example: file:///path/to/qa-ai/reports/20251010_123456/html/index.html
```

## What Just Happened?

The AI QA system just:
1. ✓ Generated test data for all profile types
2. ✓ Installed the ShareConnect app on your device
3. ✓ Executed 5+ critical test cases using AI
4. ✓ Captured screenshots at each step
5. ✓ Analyzed results with Claude AI
6. ✓ Generated a beautiful HTML report

## Next Steps

### Run More Tests

```bash
# Run all profile management tests
./run_ai_qa_tests.sh --category PROFILE_MANAGEMENT

# Run all synchronization tests
./run_ai_qa_tests.sh --category SYNC_FUNCTIONALITY

# Run full regression suite (takes ~30 minutes)
./run_ai_qa_tests.sh --suite full_regression_suite
```

### Create Your First Test Case

1. Create a new file: `qa-ai/testbank/profiles/my_first_test.yaml`

```yaml
id: "TC_MY_001"
name: "My First AI Test"
description: "Testing something awesome"
category: "PROFILE_MANAGEMENT"
priority: "MEDIUM"
tags: ["custom", "learning"]

preconditions:
  - "App is installed"

steps:
  - step_number: 1
    action: "tap"
    description: "Tap settings button"
    target: "buttonSettings"
    expected_outcome: "Settings screen opens"
    screenshot: true

expected_results:
  - "Settings screen is displayed"
```

2. Run your test:
```bash
./run_ai_qa_tests.sh --test TC_MY_001
```

### Customize Configuration

Edit `qa-ai/qa-config.yaml` to customize:
- AI model and settings
- Number of parallel tests
- Screenshot and video settings
- Emulator configuration

### Explore Test Data

```bash
# Generate test data
./gradlew :qa-ai:generateTestData

# View generated data
ls qa-ai/testdata/
```

## Common Commands

```bash
# Run all tests
./run_ai_qa_tests.sh

# Run smoke tests only
./run_ai_qa_tests.sh --suite smoke_test_suite

# Run high-priority tests
./run_ai_qa_tests.sh --priority HIGH

# Run tests with specific tag
./run_ai_qa_tests.sh --tag smoke

# Generate test data
./gradlew :qa-ai:generateTestData

# Validate test bank
./gradlew :qa-ai:validateTestBank

# Clean reports
rm -rf qa-ai/reports/*
```

## Troubleshooting

### "No API key" Error
```bash
# Make sure API key is set
echo $ANTHROPIC_API_KEY

# If empty, set it:
export ANTHROPIC_API_KEY=your_key_here
```

### "No devices found" Error
```bash
# Check devices
adb devices

# If empty, start emulator or connect device
```

### Tests Fail on First Run
- **Normal!** The app might need to be in a specific state
- Check the HTML report for details
- Tests will automatically retry on failure

### Build Errors
```bash
# Clean and rebuild
./gradlew clean :qa-ai:assembleDebug
```

## Getting Help

- **Full Documentation**: `/qa-ai/README.md`
- **Architecture Guide**: `/qa-ai/ARCHITECTURE.md`
- **Test Examples**: `/qa-ai/testbank/`
- **AI QA System Docs**: `/Documentation/QA/AI_QA_SYSTEM.md`

## Example Output

```
╔═══════════════════════════════════════════════════╗
║      ShareConnect AI QA Test Runner v1.0.0       ║
║         Powered by Claude AI (Anthropic)         ║
╚═══════════════════════════════════════════════════╝

[1/7] Checking prerequisites...
✓ API key configured
✓ Found 1 device(s)

[2/7] Preparing test environment...
✓ Report directory: qa-ai/reports/20251010_123456
✓ Screenshot directory: qa-ai/screenshots/20251010_123456

[3/7] Building AI QA module...
✓ Build successful

[4/7] Generating test data...
✓ Test data generated

[5/7] Installing test application...
✓ Application installed

[6/7] Executing AI-powered tests...
This may take several minutes. Please wait...

Running: TC_UI_001 - Onboarding Flow... ✓ PASSED (12.3s)
Running: TC_PROF_001 - Create MeTube Profile... ✓ PASSED (8.7s)
Running: TC_PROF_002 - Create qBittorrent Profile... ✓ PASSED (10.2s)
Running: TC_PROF_003 - Set Default Profile... ✓ PASSED (6.5s)
Running: TC_SYNC_001 - Basic Theme Sync... ✓ PASSED (15.8s)

[7/7] Generating reports...
✓ Reports generated

═══════════════════════════════════════════════════
                  TEST SUMMARY
═══════════════════════════════════════════════════
Duration: 53s
Report Directory: qa-ai/reports/20251010_123456
Screenshot Directory: qa-ai/screenshots/20251010_123456

╔═══════════════════════════════════════════════════╗
║              ALL TESTS PASSED ✓                   ║
╚═══════════════════════════════════════════════════╝

View HTML report: file:///path/to/qa-ai/reports/20251010_123456/html/index.html
```

## Pro Tips

1. **Start Small**: Begin with smoke tests, then expand
2. **Use Tags**: Tag tests for easy filtering (`--tag smoke`)
3. **Parallel Execution**: Increase `parallel_tests` in config for speed
4. **Watch Screenshots**: Screenshots tell the whole story
5. **Let AI Help**: The AI can adapt to minor UI changes automatically

---

🎉 **You're ready to start AI-powered testing!**

Happy testing!
