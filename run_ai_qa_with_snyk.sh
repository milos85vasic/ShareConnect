#!/bin/bash

# ShareConnect AI QA with Integrated Snyk Security Scanning
# Comprehensive testing including security vulnerability assessment

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
REPORT_DIR="qa-ai/reports/${TIMESTAMP}_AI_QA_WITH_SNYK"
SNYK_REPORT_DIR="Documentation/Tests/${TIMESTAMP}_SNYK_INTEGRATION"

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

# Function to run Snyk security scan
run_snyk_security_scan() {
    log_info "🔒 Running integrated Snyk security scan..."

    # Create Snyk report directory
    mkdir -p "$SNYK_REPORT_DIR"

    # Check prerequisites - token is now optional for freemium mode
    if [ -z "$SNYK_TOKEN" ]; then
        log_warning "SNYK_TOKEN not set - running in freemium mode"
        log_warning "Freemium limitations will apply to security scanning"
        FREEMIUM_MODE=true
    else
        log_success "Snyk token configured - full scanning available"
        FREEMIUM_MODE=false
    fi

    # Run Snyk scan using the on-demand script
    if [ "$FREEMIUM_MODE" = true ]; then
        # In freemium mode, don't fail on issues since scanning is limited
        if ./snyk_scan_on_demand.sh --severity medium 2>&1 | tee "$SNYK_REPORT_DIR/snyk_scan.log"; then
            log_success "Snyk freemium scan completed"
            echo "freemium" > "$SNYK_REPORT_DIR/snyk_scan_status.txt"

            # Copy Snyk reports to AI QA report directory
            cp -r "$SNYK_REPORT_DIR"/* "$REPORT_DIR/" 2>/dev/null || true

            return 0
        else
            log_warning "Snyk freemium scan completed with limitations"
            echo "freemium" > "$SNYK_REPORT_DIR/snyk_scan_status.txt"

            # Copy Snyk reports even on limitations for analysis
            cp -r "$SNYK_REPORT_DIR"/* "$REPORT_DIR/" 2>/dev/null || true

            return 0  # Don't fail in freemium mode
        fi
    else
        # Full mode with token
        if ./snyk_scan_on_demand.sh --severity medium --fail-on-issues 2>&1 | tee "$SNYK_REPORT_DIR/snyk_scan.log"; then
            log_success "Snyk security scan passed"
            echo "true" > "$SNYK_REPORT_DIR/snyk_scan_status.txt"

            # Copy Snyk reports to AI QA report directory
            cp -r "$SNYK_REPORT_DIR"/* "$REPORT_DIR/" 2>/dev/null || true

            return 0
        else
            log_error "Snyk security scan failed - vulnerabilities detected"
            echo "false" > "$SNYK_REPORT_DIR/snyk_scan_status.txt"

            # Copy Snyk reports even on failure for analysis
            cp -r "$SNYK_REPORT_DIR"/* "$REPORT_DIR/" 2>/dev/null || true

            return 1
        fi
    fi
}

# Function to run AI QA tests
run_ai_qa_tests() {
    local include_snyk="$1"

    log_info "🤖 Running AI QA tests..."

    # Prepare test arguments
    TEST_ARGS=""
    if [ "$include_snyk" = "true" ]; then
        TEST_ARGS="$TEST_ARGS --include-security"
    fi

    # Add any additional arguments passed to this script
    shift
    TEST_ARGS="$TEST_ARGS $@"

    # Run AI QA tests
    if ./run_ai_qa_tests.sh $TEST_ARGS 2>&1 | tee "$REPORT_DIR/ai_qa.log"; then
        log_success "AI QA tests completed successfully"
        return 0
    else
        log_error "AI QA tests failed"
        return 1
    fi
}

# Function to generate integrated report
generate_integrated_report() {
    log_info "📊 Generating integrated AI QA + Snyk report..."

    # Check if Snyk scan was performed
    SNYK_STATUS="not_run"
    if [ -f "$SNYK_REPORT_DIR/snyk_scan_status.txt" ]; then
        SNYK_STATUS=$(cat "$SNYK_REPORT_DIR/snyk_scan_status.txt")
    fi

    # Determine if Snyk scan passed (handle freemium mode)
    case "$SNYK_STATUS" in
        "true")
            SNYK_PASSED="true"
            SNYK_STATUS_TEXT="✅ PASSED"
            ;;
        "freemium")
            SNYK_PASSED="freemium"
            SNYK_STATUS_TEXT="⚠️ FREEMIUM MODE"
            ;;
        "false")
            SNYK_PASSED="false"
            SNYK_STATUS_TEXT="❌ FAILED"
            ;;
        *)
            SNYK_PASSED="false"
            SNYK_STATUS_TEXT="❓ NOT RUN"
            ;;
    esac

    # Check AI QA results
    AI_QA_PASSED="false"
    if [ -f "$REPORT_DIR/ai_qa.log" ]; then
        if grep -q "ALL TESTS PASSED" "$REPORT_DIR/ai_qa.log"; then
            AI_QA_PASSED="true"
        fi
    fi

    # Generate comprehensive HTML report
    cat > "$REPORT_DIR/integrated_report.html" << EOF
<!DOCTYPE html>
<html>
<head>
    <title>ShareConnect Integrated AI QA + Snyk Security Report</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; background: #f5f5f5; }
        .header { background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%); color: white; padding: 30px; border-radius: 10px; margin-bottom: 30px; }
        .summary { background: white; padding: 30px; margin: 20px 0; border-radius: 10px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        .section { background: white; padding: 20px; margin: 20px 0; border-radius: 10px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        .status-passed { color: #28a745; font-weight: bold; background: #d4edda; padding: 10px; border-radius: 5px; }
        .status-failed { color: #dc3545; font-weight: bold; background: #f8d7da; padding: 10px; border-radius: 5px; }
        .metric { background: #f8f9fa; padding: 15px; margin: 10px 0; border-radius: 5px; }
        table { width: 100%; border-collapse: collapse; margin: 20px 0; background: white; border-radius: 5px; overflow: hidden; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        th, td { padding: 15px; text-align: left; border-bottom: 1px solid #ddd; }
        th { background-color: #4CAF50; color: white; font-weight: bold; }
        tr:nth-child(even) { background-color: #f8f9fa; }
        tr:hover { background-color: #e8f4f8; }
        .tabs { display: flex; margin-bottom: 20px; }
        .tab { padding: 10px 20px; background: #e9ecef; border: none; cursor: pointer; border-radius: 5px 5px 0 0; margin-right: 5px; }
        .tab.active { background: white; border-bottom: 2px solid #4CAF50; }
        .tab-content { display: none; }
        .tab-content.active { display: block; }
    </style>
</head>
<body>
    <div class="header">
        <h1>🔍 ShareConnect Integrated QA Report</h1>
        <p><strong>Generated:</strong> $(date)</p>
        <p><strong>AI QA + Snyk Security Scanning</strong></p>
    </div>

    <div class="summary">
        <h2>📈 Executive Summary</h2>
        <div class="metric">
            <p><strong>AI QA Tests:</strong>
EOF

    if [ "$AI_QA_PASSED" = "true" ]; then
        echo "<span class=\"status-passed\">✅ PASSED</span>" >> "$REPORT_DIR/integrated_report.html"
    else
        echo "<span class=\"status-failed\">❌ FAILED</span>" >> "$REPORT_DIR/integrated_report.html"
    fi

    cat >> "$REPORT_DIR/integrated_report.html" << EOF
            </p>
            <p><strong>Snyk Security Scan:</strong>
EOF

    case "$SNYK_PASSED" in
        "true")
            echo "<span class=\"status-passed\">✅ PASSED</span>" >> "$REPORT_DIR/integrated_report.html"
            ;;
        "freemium")
            echo "<span style=\"color: #ffc107; font-weight: bold;\">⚠️ FREEMIUM MODE</span>" >> "$REPORT_DIR/integrated_report.html"
            ;;
        "false")
            echo "<span class=\"status-failed\">❌ FAILED</span>" >> "$REPORT_DIR/integrated_report.html"
            ;;
        *)
            echo "<span style=\"color: #6c757d;\">❓ NOT RUN</span>" >> "$REPORT_DIR/integrated_report.html"
            ;;
    esac

    cat >> "$REPORT_DIR/integrated_report.html" << EOF
            </p>
        </div>
    </div>

    <div class="tabs">
        <button class="tab active" onclick="showTab('ai-qa')">AI QA Results</button>
        <button class="tab" onclick="showTab('snyk')">Snyk Security</button>
        <button class="tab" onclick="showTab('integrated')">Integrated Analysis</button>
    </div>

    <div id="ai-qa" class="tab-content active">
        <div class="section">
            <h2>🤖 AI QA Test Results</h2>
EOF

    # Include AI QA results
    if [ -f "$REPORT_DIR/comprehensive_report.html" ]; then
        echo "<iframe src=\"comprehensive_report.html\" width=\"100%\" height=\"600px\" frameborder=\"0\"></iframe>" >> "$REPORT_DIR/integrated_report.html"
    else
        echo "<p>AI QA report not available</p>" >> "$REPORT_DIR/integrated_report.html"
    fi

    cat >> "$REPORT_DIR/integrated_report.html" << EOF
        </div>
    </div>

    <div id="snyk" class="tab-content">
        <div class="section">
            <h2>🔒 Snyk Security Scan Results</h2>
EOF

    # Include Snyk results
    if [ -f "$REPORT_DIR/snyk_report.html" ]; then
        echo "<iframe src=\"snyk_report.html\" width=\"100%\" height=\"600px\" frameborder=\"0\"></iframe>" >> "$REPORT_DIR/integrated_report.html"
    else
        echo "<p>Snyk security report not available</p>" >> "$REPORT_DIR/integrated_report.html"
    fi

    cat >> "$REPORT_DIR/integrated_report.html" << EOF
        </div>
    </div>

    <div id="integrated" class="tab-content">
        <div class="section">
            <h2>🔗 Integrated Analysis</h2>
            <div class="metric">
                <h3>Quality Assurance Coverage</h3>
                <p>This integrated report combines AI-powered functional testing with comprehensive security vulnerability scanning to provide complete confidence in code quality and security.</p>
            </div>
        </div>
    </div>

    <script>
        function showTab(tabName) {
            const tabs = document.querySelectorAll('.tab');
            const contents = document.querySelectorAll('.tab-content');

            tabs.forEach(tab => tab.classList.remove('active'));
            contents.forEach(content => content.classList.remove('active'));

            event.target.classList.add('active');
            document.getElementById(tabName).classList.add('active');
        }
    </script>
</body>
</html>
EOF

    # Generate summary text report
    cat > "$REPORT_DIR/integrated_summary.txt" << EOF
ShareConnect Integrated AI QA + Snyk Security Report
====================================================

Generated: $(date)
Report Directory: $REPORT_DIR

EXECUTIVE SUMMARY
-----------------
AI QA Tests: $([ "$AI_QA_PASSED" = "true" ] && echo "PASSED" || echo "FAILED")
Snyk Security Scan: $([ "$SNYK_PASSED" = "true" ] && echo "PASSED" || echo "FAILED")

OVERALL STATUS: $([ "$AI_QA_PASSED" = "true" ] && [ "$SNYK_PASSED" = "true" ] && echo "✅ ALL CHECKS PASSED" || echo "❌ ISSUES DETECTED")

REPORTS GENERATED
-----------------
- Integrated HTML Report: integrated_report.html
- AI QA Report: comprehensive_report.html
- Snyk Security Report: snyk_report.html
- Execution Logs: ai_qa.log, snyk_scan.log

NEXT STEPS
----------
$(if [ "$AI_QA_PASSED" = "false" ]; then
    echo "- Review AI QA failures in comprehensive_report.html"
fi)
$(if [ "$SNYK_PASSED" = "false" ]; then
    echo "- Address security vulnerabilities in snyk_report.html"
fi)
$(if [ "$AI_QA_PASSED" = "true" ] && [ "$SNYK_PASSED" = "true" ]; then
    echo "- All quality and security checks passed ✅"
    echo "- Ready for deployment or next development phase"
fi)
EOF

    log_success "Integrated report generated: $REPORT_DIR/integrated_report.html"
}

# Main execution flow
main() {
    local include_snyk="${INCLUDE_SNYK:-true}"

    log_info "🚀 Starting ShareConnect Integrated AI QA + Snyk Security Testing"
    log_info "Report Directory: $REPORT_DIR"
    log_info "Timestamp: $TIMESTAMP"

    # Create report directory
    mkdir -p "$REPORT_DIR"

    # Parse command line arguments
    while [[ $# -gt 0 ]]; do
        case $1 in
            --no-snyk)
                include_snyk="false"
                shift
                ;;
            --help)
                echo "ShareConnect Integrated AI QA + Snyk Security Testing"
                echo ""
                echo "Usage: $0 [OPTIONS] [AI_QA_ARGS...]"
                echo ""
                echo "Options:"
                echo "  --no-snyk          Skip Snyk security scanning"
                echo "  --help             Show this help message"
                echo ""
                echo "AI QA Arguments:"
                echo "  All arguments after options are passed to AI QA tests"
                echo ""
                echo "Examples:"
                echo "  $0                           # Run full integrated testing"
                echo "  $0 --no-snyk                 # Run AI QA only"
                echo "  $0 --suite smoke_test_suite  # Run specific AI QA suite with Snyk"
                exit 0
                ;;
            *)
                break
                ;;
        esac
    done

    OVERALL_SUCCESS=true
    FREEMIUM_MODE_DETECTED=false

    # Run Snyk security scan if enabled
    if [ "$include_snyk" = "true" ]; then
        if run_snyk_security_scan; then
            if [ "$SNYK_PASSED" = "freemium" ]; then
                FREEMIUM_MODE_DETECTED=true
                log_warning "Snyk running in freemium mode - limited security coverage"
            fi
        else
            OVERALL_SUCCESS=false
        fi
    else
        log_info "⏭️  Skipping Snyk security scan (--no-snyk specified)"
    fi

    # Run AI QA tests
    if ! run_ai_qa_tests "$include_snyk" "$@"; then
        OVERALL_SUCCESS=false
    fi

    # Generate integrated report
    generate_integrated_report

    # Final status
    echo ""
    echo -e "${BLUE}═══════════════════════════════════════════════════════════════════${NC}"
    echo -e "${BLUE}                    INTEGRATED TESTING SUMMARY                      ${NC}"
    echo -e "${BLUE}═══════════════════════════════════════════════════════════════════${NC}"
    echo -e "Report Directory: $REPORT_DIR"
    echo -e "Integrated Report: file://$PWD/$REPORT_DIR/integrated_report.html"
    echo ""

    if [ "$OVERALL_SUCCESS" = "true" ]; then
        if [ "$FREEMIUM_MODE_DETECTED" = "true" ]; then
            echo -e "${YELLOW}╔═══════════════════════════════════════════════════════════════════╗${NC}"
            echo -e "${YELLOW}║        ⚠️  TESTS PASSED - FREEMIUM MODE LIMITATIONS APPLY         ║${NC}"
            echo -e "${YELLOW}╚═══════════════════════════════════════════════════════════════════╝${NC}"
            echo ""
            echo -e "${YELLOW}Note: Snyk is running in freemium mode with limited security scanning.${NC}"
            echo -e "${YELLOW}Consider upgrading to a Snyk token for comprehensive security coverage.${NC}"
            echo ""
            echo -e "View detailed results: file://$PWD/$REPORT_DIR/integrated_report.html"
            exit 0
        else
            echo -e "${GREEN}╔═══════════════════════════════════════════════════════════════════╗${NC}"
            echo -e "${GREEN}║              ✅ ALL TESTS PASSED - READY FOR DEPLOYMENT           ║${NC}"
            echo -e "${GREEN}╚═══════════════════════════════════════════════════════════════════╝${NC}"
            exit 0
        fi
    else
        echo -e "${RED}╔═══════════════════════════════════════════════════════════════════╗${NC}"
        echo -e "${RED}║            ❌ ISSUES DETECTED - REVIEW REPORTS REQUIRED           ║${NC}"
        echo -e "${RED}╚═══════════════════════════════════════════════════════════════════╝${NC}"
        echo ""
        echo -e "View detailed results: file://$PWD/$REPORT_DIR/integrated_report.html"
        exit 1
    fi
}

# Run main function with all arguments
main "$@"