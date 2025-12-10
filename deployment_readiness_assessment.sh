#!/bin/bash

# Deployment Readiness Assessment Script

# Color Codes
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Deployment Readiness Checklist
echo -e "${YELLOW}===== Deployment Readiness Assessment =====${NC}"

# Dependency Compatibility Check
echo -e "\n${YELLOW}1. Dependency Compatibility Check${NC}"
./gradlew dependencies --refresh-dependencies
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Dependencies Resolved Successfully${NC}"
else
    echo -e "${RED}❌ Dependency Resolution Failed${NC}"
    exit 1
fi

# Build Verification
echo -e "\n${YELLOW}2. Build Verification${NC}"
./gradlew clean build
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Project Builds Successfully${NC}"
else
    echo -e "${RED}❌ Build Process Failed${NC}"
    exit 1
fi

# Comprehensive Test Suite
echo -e "\n${YELLOW}3. Comprehensive Test Suite${NC}"
./gradlew test integrationTest
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ All Tests Passed${NC}"
else
    echo -e "${RED}❌ Test Suite Failures Detected${NC}"
    exit 1
fi

# Security Vulnerability Scan
echo -e "\n${YELLOW}4. Security Vulnerability Scan${NC}"
./run_snyk_scan.sh
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ No Critical Security Vulnerabilities${NC}"
else
    echo -e "${RED}❌ Security Vulnerabilities Detected${NC}"
    exit 1
fi

# Performance Benchmark
echo -e "\n${YELLOW}5. Performance Benchmark${NC}"
./run_performance_tests.sh
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Performance Benchmarks Passed${NC}"
else
    echo -e "${RED}❌ Performance Benchmark Failures${NC}"
    exit 1
fi

# Compatibility Check
echo -e "\n${YELLOW}6. Cross-Module Compatibility${NC}"
./verify_module_compatibility.sh
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Module Compatibility Verified${NC}"
else
    echo -e "${RED}❌ Module Compatibility Issues${NC}"
    exit 1
fi

# Final Readiness Assessment
echo -e "\n${GREEN}===== DEPLOYMENT READINESS ASSESSMENT COMPLETE =====${NC}"
echo -e "${GREEN}Project is ready for deployment!${NC}"

exit 0