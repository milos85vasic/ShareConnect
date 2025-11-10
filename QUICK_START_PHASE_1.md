# Quick Start Guide - Phase 1: Fix Broken Tests

**Start NOW**: This guide gets you executing Phase 1 immediately.
**Time to First Action**: 5 minutes
**Phase 1 Duration**: 10 days (2 weeks)

---

## Prerequisites Check (2 minutes)

Run these commands to verify your environment:

```bash
# Check Android Studio
which android-studio || echo "Install Android Studio"

# Check Java version
java -version
# Required: Java 17

# Check Gradle
./gradlew --version
# Required: Gradle 8.14.3

# Check Git
git --version

# Check current branch
git branch --show-current
# Should be: main (or your working branch)

# Verify project builds
./gradlew clean build --dry-run
```

**✅ All checks passed? Continue below.**

---

## Step 1: Create Feature Branch (1 minute)

```bash
# Create and checkout feature branch
git checkout -b fix/phase-1-broken-tests

# Verify branch
git branch --show-current
# Output: fix/phase-1-broken-tests
```

---

## Step 2: Locate Broken Test Files (1 minute)

```bash
# List all files with @Ignore annotations
grep -r "@Ignore" --include="*Test*.kt" Connectors/ ShareConnector/

# Expected output:
# Connectors/qBitConnect/qBitConnector/src/test/.../QBittorrentApiClientTest.kt:53:@Ignore("Temporarily disabled for release build")
# ... (8 more files)
```

**Save this list - you'll fix each one.**

---

## Step 3: Begin Task 1.1.1 - Remove First @Ignore (NOW!)

### File to Edit
`Connectors/qBitConnect/qBitConnector/src/test/kotlin/com/shareconnect/qbitconnect/data/api/QBittorrentApiClientTest.kt`

### Action
Open in Android Studio:
```bash
# Open in Android Studio
open -a "Android Studio" Connectors/qBitConnect/qBitConnector/src/test/kotlin/com/shareconnect/qbitconnect/data/api/QBittorrentApiClientTest.kt

# Or use your preferred editor
code Connectors/qBitConnect/qBitConnector/src/test/kotlin/com/shareconnect/qbitconnect/data/api/QBittorrentApiClientTest.kt
```

### Find and Remove
**Line 53:**
```kotlin
@Ignore("Temporarily disabled for release build")  // ← DELETE THIS LINE
@Test
fun testSomething() {
    // ...
}
```

**After:**
```kotlin
@Test
fun testSomething() {
    // ...
}
```

### Save the File
`Ctrl+S` or `Cmd+S`

---

## Step 4: Run Tests to See Failures (5 minutes)

```bash
# Run qBitConnector tests
./gradlew :qBitConnector:test --info 2>&1 | tee qbit_test_failures.log

# This WILL fail - that's expected!
# We need to see the failure messages.
```

### While Tests Run
Open `qbit_test_failures.log` in editor:
```bash
tail -f qbit_test_failures.log
```

### What to Look For
- **Build errors**: Missing dependencies, compilation issues
- **Test failures**: Assertion errors, API mismatches
- **Configuration issues**: MockWebServer, Robolectric problems

---

## Step 5: Analyze First Failure (10 minutes)

Example failure:
```
com.shareconnect.qbitconnect.data.api.QBittorrentApiClientTest > testLogin FAILED
    Expected: 200
    Actual: 404
    at QBittorrentApiClientTest.kt:68
```

### Understand the Failure
1. **Read the test method** (line 68 in example)
2. **Check what it expects** (HTTP 200 in example)
3. **See what it got** (HTTP 404 in example)
4. **Identify the root cause**:
   - API changed?
   - Mock response wrong?
   - URL incorrect?

### Document in Tracking Sheet
Create file: `qBitConnect_Failures.md`
```markdown
# qBitConnect Test Failures

## Test 1: testLogin
- **File**: QBittorrentApiClientTest.kt
- **Line**: 68
- **Error**: Expected HTTP 200, got 404
- **Root Cause**: Mock response URL mismatch
- **Fix**: Update MockWebServer endpoint from `/login` to `/api/v2/auth/login`
- **Status**: 🔴 To Fix
```

---

## Step 6: Fix First Test (15-30 minutes)

### Common Fix Patterns

#### Pattern 1: Update Mock Response
```kotlin
// OLD
mockWebServer.enqueue(MockResponse()
    .setResponseCode(200)
    .setBody("{\"status\":\"ok\"}"))

// NEW (API changed)
mockWebServer.enqueue(MockResponse()
    .setResponseCode(200)
    .setBody("{\"success\":true}"))  // ← Updated field name
```

#### Pattern 2: Update API Endpoint
```kotlin
// OLD
val url = "$baseUrl/login"

// NEW
val url = "$baseUrl/api/v2/auth/login"  // ← Added /api/v2/
```

#### Pattern 3: Fix Robolectric Config
```kotlin
// Add to test class
@Config(sdk = [28])
```

### Test Your Fix
```bash
# Run just this one test
./gradlew :qBitConnector:test --tests QBittorrentApiClientTest.testLogin

# Did it pass?
# YES → Continue to next test
# NO → Debug further
```

---

## Step 7: Repeat for Remaining Tests

### Process
1. Remove @Ignore annotation
2. Run test → Observe failure
3. Analyze root cause
4. Implement fix
5. Verify test passes
6. Commit changes
7. Move to next test

### Git Workflow
```bash
# After fixing each test (or group of related tests)
git add .
git commit -m "fix: qBitConnect QBittorrentApiClientTest.testLogin

- Updated mock response format
- Fixed API endpoint path
- Test now passing"

# Push regularly
git push origin fix/phase-1-broken-tests
```

---

## Phase 1 Daily Checklist

### Morning (30 minutes)
- [ ] Review yesterday's progress
- [ ] Update `RESTORATION_PROGRESS_TRACKER.md`
- [ ] Plan today's 3 goals
- [ ] Check for blockers

### During Day (6-8 hours)
- [ ] Fix tests according to plan
- [ ] Run tests frequently
- [ ] Document failures and fixes
- [ ] Commit working changes

### Evening (30 minutes)
- [ ] Run full test suite for module
- [ ] Update progress tracker
- [ ] Document blockers
- [ ] Plan tomorrow

---

## Helpful Commands Reference

### Building
```bash
# Clean build
./gradlew clean

# Build specific module
./gradlew :qBitConnector:build

# Build all
./gradlew build
```

### Testing
```bash
# Run all tests for module
./gradlew :qBitConnector:test

# Run specific test class
./gradlew :qBitConnector:test --tests QBittorrentApiClientTest

# Run specific test method
./gradlew :qBitConnector:test --tests QBittorrentApiClientTest.testLogin

# Run with full output
./gradlew :qBitConnector:test --info

# Run instrumentation tests
./gradlew :qBitConnector:connectedAndroidTest

# Run automation tests
./run_automation_tests.sh qBitConnect
```

### Coverage
```bash
# Generate coverage report
./gradlew :qBitConnector:jacocoTestReport

# View report
open qBitConnector/build/reports/jacoco/test/html/index.html
```

### Debugging
```bash
# Run tests in debug mode
./gradlew :qBitConnector:test --debug-jvm

# Check test output
cat qBitConnector/build/reports/tests/test/index.html

# View logcat during instrumentation tests
adb logcat | grep "qBitConnect"
```

---

## When You Get Stuck

### Issue: Tests still failing after fix
**Action**:
1. Read full stack trace carefully
2. Check for secondary errors
3. Search codebase for similar patterns
4. Review git history for recent changes
5. Ask for help (document what you tried)

### Issue: Build fails
**Action**:
1. Clean: `./gradlew clean`
2. Invalidate caches (Android Studio: File → Invalidate Caches)
3. Restart Android Studio
4. Check `build.gradle` for conflicts
5. Verify all dependencies resolved

### Issue: Don't understand test
**Action**:
1. Read test method completely
2. Trace what it's testing
3. Check related production code
4. Look for similar tests
5. Add debug logging to understand flow

### Issue: Too many failures
**Action**:
1. Focus on ONE test at a time
2. Fix completely before moving on
3. Don't try to fix everything at once
4. Break large tasks into smaller ones

---

## Progress Milestones

### Day 1 Complete When:
- [ ] All 6 qBitConnect @Ignore annotations removed
- [ ] First 2-3 tests fixed and passing
- [ ] Failure analysis document created
- [ ] Tomorrow's plan documented

### Day 5 Complete When:
- [ ] All qBitConnect tests passing (25+ tests)
- [ ] Integration tests passing
- [ ] Automation tests passing
- [ ] Coverage ≥ 90%
- [ ] Ready for PlexConnect tests

### Day 10 Complete When:
- [ ] All broken tests fixed across all modules
- [ ] 100% test pass rate
- [ ] Coverage ≥ 90% all modules
- [ ] Phase 1 completion report created
- [ ] Ready for Phase 2

---

## Quick Wins (Do These First)

Some tests may be trivially easy to fix:

### Quick Win 1: Simple Annotation Removal
If test passes immediately after removing @Ignore:
```kotlin
// Before
@Ignore("Temporarily disabled")
@Test
fun testSomething() { ... }

// After - just remove annotation
@Test
fun testSomething() { ... }

// Run test
./gradlew :Module:test --tests ClassName.testSomething
// ✅ Passes!
```

### Quick Win 2: Update Test Expectations
If just the expected value changed:
```kotlin
// Before
assertEquals(5, result.size)

// After (if API now returns 6 items)
assertEquals(6, result.size)
```

### Quick Win 3: Update Mock Data
If API response format changed:
```kotlin
// Before
"""{"count":5}"""

// After
"""{"total":5}"""  // Field renamed
```

**Do the easy ones first to build momentum!**

---

## Success Metrics Tracker

Track these daily:

| Metric | Day 1 | Day 2 | Day 3 | Day 4 | Day 5 | Target |
|--------|-------|-------|-------|-------|-------|--------|
| Tests Fixed | | | | | | 25+ |
| Tests Passing (%) | | | | | | 100% |
| Modules Complete | | | | | | 1 |
| Coverage (%) | | | | | | ≥90% |
| Hours Worked | | | | | | 20 |

---

## Emergency Contact

**If completely blocked**:
1. Document the blocker in detail
2. Try workaround approaches
3. Mark in progress tracker
4. Continue with other tests
5. Plan to revisit with fresh perspective

**Blocker Template**:
```markdown
## Blocker: [Brief Description]

**Test**: [ClassName.testMethod]
**Error**: [Exact error message]
**What I tried**:
1. [Attempt 1]
2. [Attempt 2]
3. [Attempt 3]

**Stack trace**:
```
[paste here]
```

**Additional context**:
[Any relevant info]

**Workaround**:
[What you'll do instead]
```

---

## Ready to Start?

### Final Pre-Flight Check
- [ ] Feature branch created
- [ ] Android Studio open
- [ ] `RESTORATION_PROGRESS_TRACKER.md` open for updates
- [ ] Terminal ready with project directory
- [ ] Coffee/tea ready ☕
- [ ] Focused and ready to fix tests! 💪

### 3... 2... 1... GO!

```bash
# Start the timer
date

# Begin with Task 1.1.1
echo "Starting Phase 1: Fix Broken Tests"
echo "Task 1.1.1: Remove @Ignore from QBittorrentApiClientTest.kt"

# Open the file
open -a "Android Studio" Connectors/qBitConnect/qBitConnector/src/test/kotlin/com/shareconnect/qbitconnect/data/api/QBittorrentApiClientTest.kt

# You've got this! 🚀
```

---

**Remember**:
- One test at a time
- Document everything
- Commit frequently
- Ask for help when stuck
- Celebrate small wins!

**Let's make ShareConnect 100% complete! 🎯**
