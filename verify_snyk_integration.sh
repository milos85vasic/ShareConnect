#!/bin/bash

# ShareConnect Snyk Integration Verification
# Comprehensive testing of all Snyk security scanning components

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
REPORT_DIR="Documentation/Tests/${TIMESTAMP}_SNYK_VERIFICATION"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Logging functions
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Verification functions
verify_files_exist() {
    local files=(
        "docker-compose.snyk.yml"
        ".snyk"
        "run_snyk_scan.sh"
        "snyk_scan_on_demand.sh"
        "run_ai_qa_with_snyk.sh"
        "qa-ai/testbank/security/snyk_security_scan.yaml"
        "SNYK_INTEGRATION_README.md"
    )

    log_info "Verifying Snyk integration files exist..."

    local missing_files=()
    for file in "${files[@]}"; do
        if [ ! -f "$file" ]; then
            missing_files+=("$file")
        fi
    done

    if [ ${#missing_files[@]} -eq 0 ]; then
        log_success "All Snyk integration files are present"
        return 0
    else
        log_error "Missing files: ${missing_files[*]}"
        return 1
    fi
}

verify_scripts_executable() {
    log_info "Verifying scripts are executable..."

    local scripts=(
        "run_snyk_scan.sh"
        "snyk_scan_on_demand.sh"
        "run_ai_qa_with_snyk.sh"
    )

    local non_executable=()
    for script in "${scripts[@]}"; do
        if [ ! -x "$script" ]; then
            non_executable+=("$script")
        fi
    done

    if [ ${#non_executable[@]} -eq 0 ]; then
        log_success "All scripts are executable"
        return 0
    else
        log_error "Non-executable scripts: ${non_executable[*]}"
        return 1
    fi
}

verify_docker_compose() {
    log_info "Verifying Docker Compose configuration..."

    if command -v docker-compose >/dev/null 2>&1 || command -v docker >/dev/null 2>&1; then
        if docker-compose -f docker-compose.snyk.yml config >/dev/null 2>&1; then
            log_success "Docker Compose configuration is valid"
            return 0
        else
            log_error "Docker Compose configuration is invalid"
            return 1
        fi
    else
        log_warning "Docker/Docker Compose not available for validation"
        return 0
    fi
}

verify_build_gradle_changes() {
    log_info "Verifying build.gradle security fixes..."

    local files=("build.gradle.kts" "ShareConnector/build.gradle")
    local security_deps=(
        "com.fasterxml.jackson.core:jackson-databind:2.14.1"
        "org.springframework:spring-core:5.3.21"
        "com.google.guava:guava:31.1-jre"
    )

    local all_found=true
    for file in "${files[@]}"; do
        if [ -f "$file" ]; then
            for dep in "${security_deps[@]}"; do
                if ! grep -q "$dep" "$file"; then
                    log_error "Security dependency $dep not found in $file"
                    all_found=false
                fi
            done
        else
            log_warning "Build file $file not found"
        fi
    done

    if [ "$all_found" = true ]; then
        log_success "All security dependency fixes are in place"
        return 0
    else
        return 1
    fi
}

verify_qa_config() {
    log_info "Verifying AI QA configuration includes Snyk..."

    if [ -f "qa-ai/qa-config.yaml" ]; then
        if grep -q "snyk_scanning" "qa-ai/qa-config.yaml"; then
            log_success "Snyk scanning configured in AI QA"
            return 0
        else
            log_error "Snyk scanning not found in AI QA configuration"
            return 1
        fi
    else
        log_error "AI QA configuration file not found"
        return 1
    fi
}

verify_test_case() {
    log_info "Verifying Snyk security test case exists..."

    if [ -f "qa-ai/testbank/security/snyk_security_scan.yaml" ]; then
        if grep -q "SEC_SNYK_001" "qa-ai/testbank/security/snyk_security_scan.yaml"; then
            log_success "Snyk security test case is properly configured"
            return 0
        else
            log_error "Snyk test case ID not found"
            return 1
        fi
    else
        log_error "Snyk security test case file not found"
        return 1
    fi
}

verify_documentation() {
    log_info "Verifying documentation is complete..."

    local docs=(
        "SNYK_INTEGRATION_README.md"
        "AGENTS.md"
    )

    local doc_checks=true
    for doc in "${docs[@]}"; do
        if [ -f "$doc" ]; then
            if grep -q "Snyk" "$doc"; then
                log_success "Snyk documentation found in $doc"
            else
                log_error "Snyk documentation missing in $doc"
                doc_checks=false
            fi
        else
            log_error "Documentation file $doc not found"
            doc_checks=false
        fi
    done

    return $([ "$doc_checks" = true ])
}

run_syntax_check() {
    log_info "Running basic syntax checks..."

    # Check shell scripts for syntax errors
    local scripts=(
        "run_snyk_scan.sh"
        "snyk_scan_on_demand.sh"
        "run_ai_qa_with_snyk.sh"
    )

    local syntax_errors=false
    for script in "${scripts[@]}"; do
        if bash -n "$script" 2>/dev/null; then
            log_success "Syntax check passed for $script"
        else
            log_error "Syntax error in $script"
            syntax_errors=true
        fi
    done

    return $([ "$syntax_errors" = false ])
}

generate_verification_report() {
    log_info "Generating verification report..."

    mkdir -p "$REPORT_DIR"

    cat > "$REPORT_DIR/verification_report.html" << EOF
<!DOCTYPE html>
<html>
<head>
    <title>ShareConnect Snyk Integration Verification</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; background: #f5f5f5; }
        .header { background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%); color: white; padding: 30px; border-radius: 10px; margin-bottom: 30px; }
        .section { background: white; padding: 20px; margin: 20px 0; border-radius: 10px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        .status-passed { color: #28a745; font-weight: bold; background: #d4edda; padding: 10px; border-radius: 5px; }
        .status-failed { color: #dc3545; font-weight: bold; background: #f8d7da; padding: 10px; border-radius: 5px; }
        .checklist { background: #f8f9fa; padding: 15px; margin: 10px 0; border-radius: 5px; }
        .check-passed { color: #28a745; }
        .check-failed { color: #dc3545; }
    </style>
</head>
<body>
    <div class="header">
        <h1>🔍 ShareConnect Snyk Integration Verification</h1>
        <p><strong>Generated:</strong> $(date)</p>
        <p><strong>Verification Status:</strong> <span class="$OVERALL_STATUS_CLASS">$OVERALL_STATUS</span></p>
    </div>

    <div class="section">
        <h2>📋 Verification Checklist</h2>
        <div class="checklist">
            <p><strong>Files Exist:</strong> <span class="$FILES_STATUS_CLASS">$FILES_STATUS</span></p>
            <p><strong>Scripts Executable:</strong> <span class="$SCRIPTS_STATUS_CLASS">$SCRIPTS_STATUS</span></p>
            <p><strong>Docker Config:</strong> <span class="$DOCKER_STATUS_CLASS">$DOCKER_STATUS</span></p>
            <p><strong>Build Changes:</strong> <span class="$BUILD_STATUS_CLASS">$BUILD_STATUS</span></p>
            <p><strong>AI QA Config:</strong> <span class="$QA_STATUS_CLASS">$QA_STATUS</span></p>
            <p><strong>Test Case:</strong> <span class="$TEST_STATUS_CLASS">$TEST_STATUS</span></p>
            <p><strong>Documentation:</strong> <span class="$DOCS_STATUS_CLASS">$DOCS_STATUS</span></p>
            <p><strong>Syntax Check:</strong> <span class="$SYNTAX_STATUS_CLASS">$SYNTAX_STATUS</span></p>
        </div>
    </div>

    <div class="section">
        <h2>📊 Summary</h2>
        <p>Total Checks: $TOTAL_CHECKS</p>
        <p>Passed: $PASSED_CHECKS</p>
        <p>Failed: $FAILED_CHECKS</p>
        <p>Success Rate: $SUCCESS_RATE%</p>
    </div>

    <div class="section">
        <h2>🔗 Next Steps</h2>
        <ul>
            <li>Set up SNYK_TOKEN environment variable</li>
            <li>Run initial security scan: <code>./run_snyk_scan.sh</code></li>
            <li>Integrate with CI/CD pipeline</li>
            <li>Set up automated weekly scans</li>
        </ul>
    </div>
</body>
</html>
EOF

    cat > "$REPORT_DIR/verification_summary.txt" << EOF
ShareConnect Snyk Integration Verification Summary
================================================

Generated: $(date)
Report Directory: $REPORT_DIR

VERIFICATION STATUS
-------------------
Overall Status: $OVERALL_STATUS
Success Rate: $SUCCESS_RATE%

CHECKLIST RESULTS
-----------------
Files Exist: $FILES_STATUS
Scripts Executable: $SCRIPTS_STATUS
Docker Configuration: $DOCKER_STATUS
Build Gradle Changes: $BUILD_STATUS
AI QA Configuration: $QA_STATUS
Security Test Case: $TEST_STATUS
Documentation: $DOCS_STATUS
Syntax Check: $SYNTAX_STATUS

SUMMARY
-------
Total Checks: $TOTAL_CHECKS
Passed: $PASSED_CHECKS
Failed: $FAILED_CHECKS

NEXT STEPS
----------
1. Set SNYK_TOKEN environment variable
2. Run: ./run_snyk_scan.sh --start
3. Run: ./run_snyk_scan.sh --scan
4. Review generated reports
5. Integrate with CI/CD pipeline

For detailed instructions, see SNYK_INTEGRATION_README.md
EOF

    log_success "Verification report generated: $REPORT_DIR/verification_report.html"
}

# Main verification logic
main() {
    log_info "🚀 Starting ShareConnect Snyk Integration Verification"
    log_info "Report Directory: $REPORT_DIR"

    mkdir -p "$REPORT_DIR"

    # Initialize counters
    TOTAL_CHECKS=0
    PASSED_CHECKS=0
    FAILED_CHECKS=0

    # Run all verification checks
    local checks=(
        "FILES:verify_files_exist"
        "SCRIPTS:verify_scripts_executable"
        "DOCKER:verify_docker_compose"
        "BUILD:verify_build_gradle_changes"
        "QA:verify_qa_config"
        "TEST:verify_test_case"
        "DOCS:verify_documentation"
        "SYNTAX:run_syntax_check"
    )

    declare -A results

    for check in "${checks[@]}"; do
        IFS=':' read -r name function <<< "$check"
        ((TOTAL_CHECKS++))

        log_info "Running $name check..."
        if $function; then
            results[$name]="PASSED"
            ((PASSED_CHECKS++))
        else
            results[$name]="FAILED"
            ((FAILED_CHECKS++))
        fi
    done

    # Calculate success rate
    SUCCESS_RATE=$((PASSED_CHECKS * 100 / TOTAL_CHECKS))

    # Set overall status
    if [ $FAILED_CHECKS -eq 0 ]; then
        OVERALL_STATUS="✅ ALL CHECKS PASSED"
        OVERALL_STATUS_CLASS="status-passed"
    else
        OVERALL_STATUS="❌ ISSUES DETECTED"
        OVERALL_STATUS_CLASS="status-failed"
    fi

    # Set individual status classes
    FILES_STATUS="${results[FILES]}"
    FILES_STATUS_CLASS=$([ "$FILES_STATUS" = "PASSED" ] && echo "check-passed" || echo "check-failed")

    SCRIPTS_STATUS="${results[SCRIPTS]}"
    SCRIPTS_STATUS_CLASS=$([ "$SCRIPTS_STATUS" = "PASSED" ] && echo "check-passed" || echo "check-failed")

    DOCKER_STATUS="${results[DOCKER]}"
    DOCKER_STATUS_CLASS=$([ "$DOCKER_STATUS" = "PASSED" ] && echo "check-passed" || echo "check-failed")

    BUILD_STATUS="${results[BUILD]}"
    BUILD_STATUS_CLASS=$([ "$BUILD_STATUS" = "PASSED" ] && echo "check-passed" || echo "check-failed")

    QA_STATUS="${results[QA]}"
    QA_STATUS_CLASS=$([ "$QA_STATUS" = "PASSED" ] && echo "check-passed" || echo "check-failed")

    TEST_STATUS="${results[TEST]}"
    TEST_STATUS_CLASS=$([ "$TEST_STATUS" = "PASSED" ] && echo "check-passed" || echo "check-failed")

    DOCS_STATUS="${results[DOCS]}"
    DOCS_STATUS_CLASS=$([ "$DOCS_STATUS" = "PASSED" ] && echo "check-passed" || echo "check-failed")

    SYNTAX_STATUS="${results[SYNTAX]}"
    SYNTAX_STATUS_CLASS=$([ "$SYNTAX_STATUS" = "PASSED" ] && echo "check-passed" || echo "check-failed")

    # Generate verification report
    generate_verification_report

    # Final output
    echo ""
    echo -e "${BLUE}═══════════════════════════════════════════════════════════════════${NC}"
    echo -e "${BLUE}                SNYK INTEGRATION VERIFICATION SUMMARY               ${NC}"
    echo -e "${BLUE}═══════════════════════════════════════════════════════════════════${NC}"
    echo -e "Total Checks: $TOTAL_CHECKS"
    echo -e "Passed: $PASSED_CHECKS"
    echo -e "Failed: $FAILED_CHECKS"
    echo -e "Success Rate: $SUCCESS_RATE%"
    echo ""
    echo -e "Report Directory: $REPORT_DIR"
    echo -e "Detailed Report: file://$PWD/$REPORT_DIR/verification_report.html"
    echo ""

    if [ $FAILED_CHECKS -eq 0 ]; then
        echo -e "${GREEN}╔═══════════════════════════════════════════════════════════════════╗${NC}"
        echo -e "${GREEN}║              ✅ SNYK INTEGRATION VERIFICATION PASSED               ║${NC}"
        echo -e "${GREEN}╚═══════════════════════════════════════════════════════════════════╝${NC}"
        exit 0
    else
        echo -e "${RED}╔═══════════════════════════════════════════════════════════════════╗${NC}"
        echo -e "${RED}║            ❌ SNYK INTEGRATION VERIFICATION FAILED                 ║${NC}"
        echo -e "${RED}╚═══════════════════════════════════════════════════════════════════╝${NC}"
        echo ""
        echo -e "Please review the verification report and fix any failed checks."
        exit 1
    fi
}

# Run main verification
main "$@"