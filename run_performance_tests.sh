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
