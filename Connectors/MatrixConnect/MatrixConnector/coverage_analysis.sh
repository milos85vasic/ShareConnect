#!/bin/bash

# Comprehensive Test Coverage Analysis Script

echo "===== Matrix Encryption Manager - Test Coverage Analysis ====="

# Run tests with detailed coverage
./gradlew jacocoTestReport

# Generate detailed coverage report
echo -e "\n===== Detailed Coverage Report ====="
cat build/reports/jacoco/test/html/index.html | grep -A 10 "Total"

# Analyze specific module coverage
echo -e "\n===== Module Coverage Breakdown ====="
find build/reports/jacoco/test/html/ -name "*.html" | while read -r file; do
    module=$(basename "$file" .html)
    coverage=$(grep -oP '(?<=Total\s)[0-9.]+' "$file")
    echo "$module: $coverage%"
done

# Check for 100% coverage
total_coverage=$(grep -oP '(?<=Total\s)[0-9.]+' build/reports/jacoco/test/html/index.html)

if (( $(echo "$total_coverage >= 100" | bc -l) )); then
    echo -e "\n✅ Test Coverage: 100% \e[32m[PASSED]\e[0m"
    exit 0
else
    echo -e "\n❌ Test Coverage: ${total_coverage}% \e[31m[FAILED]\e[0m"
    exit 1
fi