#!/bin/bash

# Test Coverage Analysis Script for MatrixConnector

echo "=== Matrix Encryption Manager Test Coverage Analysis ==="

# Run tests with coverage
./gradlew jacocoTestReport

# Display coverage summary
echo "\n=== Coverage Summary ==="
grep -A 10 "Total" build/reports/jacoco/test/html/index.html

# Check for 100% coverage
total_coverage=$(grep -oP '(?<=Total\s)[0-9.]+' build/reports/jacoco/test/html/index.html)

if (( $(echo "$total_coverage >= 100" | bc -l) )); then
    echo "✅ Test Coverage: 100%"
    exit 0
else
    echo "❌ Test Coverage: ${total_coverage}% (Needs improvement)"
    exit 1
fi