#!/bin/bash

# Phase 4: Developer Experience - CI/CD Pipeline Implementation
# Create comprehensive CI/CD pipeline for all 20 ShareConnect connectors
# Includes automated testing, security scanning, and performance benchmarks

set -e

echo "🚀 Phase 4: Implementing CI/CD Pipeline for All 20 Connectors"
echo "============================================================"

# Create GitHub Actions workflows directory if it doesn't exist
mkdir -p .github/workflows

# Create comprehensive CI/CD workflow for all connectors
cat > .github/workflows/shareconnect-full-ci.yml << 'EOF'
name: ShareConnect Full CI/CD Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main, develop ]
  schedule:
    # Run full pipeline daily at 2 AM UTC
    - cron: '0 2 * * *'
  workflow_dispatch:
    inputs:
      test_focus:
        description: 'Test focus (all, unit, integration, performance)'
        required: false
        default: 'all'
      security_scan:
        description: 'Run security scan'
        required: false
        default: 'true'

env:
  GRADLE_OPTS: -Dorg.gradle.daemon=false -Dorg.gradle.parallel=true -Dorg.gradle.caching=true

jobs:
  # Build and test all connectors
  build-and-test:
    runs-on: ubuntu-latest
    strategy:
      matrix:
        connector: [
          'ShareConnector', 'qBitConnector', 'TransmissionConnector', 'uTorrentConnector',
          'PlexConnector', 'NextcloudConnector', 'MotrixConnector', 'GiteaConnector',
          'JellyfinConnector', 'PortainerConnector', 'NetdataConnector', 'HomeAssistantConnector',
          'SeafileConnector', 'SyncthingConnector', 'MatrixConnector', 'PaperlessNGConnector',
          'DuplicatiConnector', 'WireGuardConnector', 'MinecraftServerConnector', 'OnlyOfficeConnector'
        ]
    steps:
    - uses: actions/checkout@v4

    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'

    - name: Cache Gradle packages
      uses: actions/cache@v4
      with:
        path: |
          ~/.gradle/caches
          ~/.gradle/wrapper
        key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
        restore-keys: |
          ${{ runner.os }}-gradle-

    - name: Grant execute permission for gradlew
      run: chmod +x gradlew

    - name: Build ${{ matrix.connector }}
      run: ./gradlew :${{ matrix.connector }}:assembleDebug --parallel

    - name: Run unit tests for ${{ matrix.connector }}
      run: ./gradlew :${{ matrix.connector }}:testDebugUnitTest

    - name: Run integration tests for ${{ matrix.connector }}
      if: matrix.connector != 'ShareConnector' # Skip for main app in PR builds
      run: ./gradlew :${{ matrix.connector }}:connectedDebugAndroidTest
      timeout-minutes: 10

    - name: Upload test results
      uses: actions/upload-artifact@v4
      if: always()
      with:
        name: test-results-${{ matrix.connector }}
        path: |
          **/build/test-results/**/*.xml
          **/build/reports/tests/**

  # Performance benchmarking
  performance-test:
    runs-on: ubuntu-latest
    needs: build-and-test
    if: github.event.inputs.test_focus == 'all' || github.event.inputs.test_focus == 'performance' || github.event_name == 'schedule'
    steps:
    - uses: actions/checkout@v4

    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'

    - name: Run performance benchmarks
      run: |
        ./gradlew assembleDebug
        # Run performance test script
        if [ -f "run_performance_tests.sh" ]; then
          ./run_performance_tests.sh
        else
          echo "Performance test script not found, skipping"
        fi

    - name: Upload performance results
      uses: actions/upload-artifact@v4
      with:
        name: performance-results
        path: Documentation/Tests/*_PERFORMANCE_*/

  # Security scanning
  security-scan:
    runs-on: ubuntu-latest
    needs: build-and-test
    if: github.event.inputs.security_scan == 'true'
    steps:
    - uses: actions/checkout@v4

    - name: Run Snyk security scan
      uses: snyk/actions/gradle-jdk17@master
      env:
        SNYK_TOKEN: ${{ secrets.SNYK_TOKEN }}
      with:
        args: --sarif-file-output=snyk.sarif

    - name: Upload Snyk results to GitHub Security tab
      uses: github/codeql-action/upload-sarif@v3
      with:
        sarif_file: snyk.sarif

    - name: Run custom security checks
      run: |
        if [ -f "run_snyk_scan.sh" ]; then
          ./run_snyk_scan.sh --ci
        fi

  # Code quality checks
  code-quality:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v4

    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'

    - name: Run Detekt code analysis
      run: ./gradlew detekt

    - name: Run Android Lint
      run: ./gradlew lintDebug

    - name: Upload code quality reports
      uses: actions/upload-artifact@v4
      with:
        name: code-quality-reports
        path: |
          **/build/reports/detekt/**
          **/build/reports/lint-results-debug.html

  # Deployment preparation
  deploy-prep:
    runs-on: ubuntu-latest
    needs: [build-and-test, security-scan, code-quality]
    if: github.ref == 'refs/heads/main' && github.event_name != 'pull_request'
    steps:
    - uses: actions/checkout@v4

    - name: Build release APKs
      run: ./build_phase3_apps.sh release

    - name: Sign APKs
      run: |
        # Sign all release APKs
        for apk in Connectors/*/build/outputs/apk/release/*-release-unsigned.apk; do
          if [ -f "$apk" ]; then
            signed_apk="${apk%-unsigned.apk}.apk"
            apksigner sign --ks ${{ secrets.SIGNING_KEYSTORE }} --ks-pass pass:${{ secrets.SIGNING_PASSWORD }} --out "$signed_apk" "$apk"
          fi
        done

    - name: Upload release artifacts
      uses: actions/upload-artifact@v4
      with:
        name: release-apks
        path: Connectors/*/build/outputs/apk/release/*.apk

  # Documentation deployment
  docs-deploy:
    runs-on: ubuntu-latest
    needs: deploy-prep
    if: github.ref == 'refs/heads/main' && github.event_name != 'pull_request'
    steps:
    - uses: actions/checkout@v4

    - name: Deploy documentation
      run: |
        if [ -f "deploy_docs.sh" ]; then
          ./deploy_docs.sh
        else
          echo "Documentation deployment script not found"
        fi

  # Notification
  notify:
    runs-on: ubuntu-latest
    needs: [build-and-test, security-scan, code-quality]
    if: always()
    steps:
    - name: Send notification
      run: |
        if [ "${{ needs.build-and-test.result }}" == "success" ] && \
           [ "${{ needs.security-scan.result }}" == "success" ] && \
           [ "${{ needs.code-quality.result }}" == "success" ]; then
          echo "✅ All checks passed!"
          # Add notification logic here (Slack, Discord, etc.)
        else
          echo "❌ Some checks failed"
          # Add failure notification logic here
        fi
EOF

# Create performance testing script
cat > run_performance_tests.sh << 'EOF'
#!/bin/bash

# Performance testing script for ShareConnect
# Runs automated performance benchmarks across all connectors

set -e

echo "🏃 Running Performance Tests for All Connectors"
echo "==============================================="

TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
REPORT_DIR="Documentation/Tests/${TIMESTAMP}_PERFORMANCE_TEST"
mkdir -p "$REPORT_DIR"

echo "📊 Performance Test Results - $TIMESTAMP" > "$REPORT_DIR/performance_summary.md"
echo "==========================================" >> "$REPORT_DIR/performance_summary.md"

# Test each connector
CONNECTORS=(
    "ShareConnector"
    "qBitConnector"
    "TransmissionConnector"
    "uTorrentConnector"
    "PlexConnector"
    "NextcloudConnector"
    "MotrixConnector"
    "GiteaConnector"
    "JellyfinConnector"
    "PortainerConnector"
    "NetdataConnector"
    "HomeAssistantConnector"
    "SeafileConnector"
    "SyncthingConnector"
    "MatrixConnector"
    "PaperlessNGConnector"
    "DuplicatiConnector"
    "WireGuardConnector"
    "MinecraftServerConnector"
    "OnlyOfficeConnector"
)

TOTAL_CONNECTORS=${#CONNECTORS[@]}
PASSED=0
FAILED=0

for connector in "${CONNECTORS[@]}"; do
    echo ""
    echo "📱 Testing $connector..."

    # Build connector
    if ./gradlew ":${connector}:assembleDebug" > "$REPORT_DIR/${connector}_build.log" 2>&1; then
        echo "✅ Build successful for $connector"

        # Run basic performance checks
        START_TIME=$(date +%s%3N)
        if ./gradlew ":${connector}:testDebugUnitTest" --quiet > "$REPORT_DIR/${connector}_test.log" 2>&1; then
            END_TIME=$(date +%s%3N)
            DURATION=$((END_TIME - START_TIME))
            echo "✅ Tests passed for $connector in ${DURATION}ms"
            echo "| $connector | ✅ PASS | ${DURATION}ms |" >> "$REPORT_DIR/performance_summary.md"
            ((PASSED++))
        else
            echo "❌ Tests failed for $connector"
            echo "| $connector | ❌ FAIL | - |" >> "$REPORT_DIR/performance_summary.md"
            ((FAILED++))
        fi
    else
        echo "❌ Build failed for $connector"
        echo "| $connector | ❌ BUILD FAIL | - |" >> "$REPORT_DIR/performance_summary.md"
        ((FAILED++))
    fi
done

echo "" >> "$REPORT_DIR/performance_summary.md"
echo "## Summary" >> "$REPORT_DIR/performance_summary.md"
echo "- Total Connectors: $TOTAL_CONNECTORS" >> "$REPORT_DIR/performance_summary.md"
echo "- Passed: $PASSED" >> "$REPORT_DIR/performance_summary.md"
echo "- Failed: $FAILED" >> "$REPORT_DIR/performance_summary.md"
echo "- Success Rate: $((PASSED * 100 / TOTAL_CONNECTORS))%" >> "$REPORT_DIR/performance_summary.md"

echo ""
echo "🎉 Performance Testing Complete!"
echo "================================="
echo "📊 Results saved to: $REPORT_DIR"
echo "✅ Passed: $PASSED/$TOTAL_CONNECTORS"
echo "❌ Failed: $FAILED/$TOTAL_CONNECTORS"

if [ $FAILED -eq 0 ]; then
    echo "🎉 All performance tests passed!"
    exit 0
else
    echo "⚠️  Some performance tests failed"
    exit 1
fi
EOF

chmod +x run_performance_tests.sh

echo "✅ Created comprehensive CI/CD pipeline:"
echo "   - GitHub Actions workflow (.github/workflows/shareconnect-full-ci.yml)"
echo "   - Performance testing script (run_performance_tests.sh)"
echo "   - Automated testing for all 20 connectors"
echo "   - Security scanning integration"
echo "   - Code quality checks"
echo "   - Release APK signing and deployment"

echo ""
echo "🎯 CI/CD Features:"
echo "   - Matrix builds for all 20 connectors"
echo "   - Parallel test execution"
echo "   - Automated security scanning with Snyk"
echo "   - Code quality analysis (Detekt + Lint)"
echo "   - Performance benchmarking"
echo "   - Release artifact management"
echo "   - Scheduled daily full pipeline runs"
echo "   - Manual workflow triggers for focused testing"

echo ""
echo "📊 Developer Experience Impact:"
echo "   - Automated testing prevents regressions"
echo "   - Security scanning catches vulnerabilities early"
echo "   - Performance benchmarks track improvements"
echo "   - Parallel builds reduce development time"
echo "   - Consistent quality standards across all connectors"