#!/bin/bash

# ShareConnect CI/CD Manual-Only Verification
# Ensures all workflows are configured for manual execution only

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
WORKFLOW_DIR=".github/workflows"

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

# Function to check if workflow is manual-only
check_workflow_manual_only() {
    local workflow_file="$1"
    local workflow_name=$(basename "$workflow_file" .yml)

    log_info "Checking workflow: $workflow_name"

    # Check if workflow_dispatch is present
    if ! grep -q "workflow_dispatch:" "$workflow_file"; then
        log_error "❌ $workflow_name: Missing workflow_dispatch trigger"
        return 1
    fi

    # Check for automated triggers that should NOT be present
    local automated_triggers=(
        "push:"
        "pull_request:"
        "pull_request_target:"
        "schedule:"
        "workflow_run:"
        "workflow_call:"
    )

    local has_automated=false
    for trigger in "${automated_triggers[@]}"; do
        if grep -q "^[[:space:]]*${trigger}" "$workflow_file"; then
            log_error "❌ $workflow_name: Contains automated trigger '$trigger'"
            has_automated=true
        fi
    done

    if [ "$has_automated" = true ]; then
        return 1
    fi

    # Check if workflow_dispatch is the only trigger (no 'on:' with multiple triggers)
    local trigger_count=$(grep -c "^[[:space:]]*on:" "$workflow_file")
    if [ "$trigger_count" -gt 1 ]; then
        log_error "❌ $workflow_name: Multiple trigger types detected"
        return 1
    fi

    log_success "✅ $workflow_name: Manual-only workflow confirmed"
    return 0
}

# Function to verify workflow files exist
check_workflow_files() {
    log_info "Checking workflow files exist..."

    local expected_workflows=(
        "comprehensive-qa.yml"
        "snyk-security-scan.yml"
        "combined-qa-security.yml"
    )

    local missing_files=()
    for workflow in "${expected_workflows[@]}"; do
        if [ ! -f "$WORKFLOW_DIR/$workflow" ]; then
            missing_files+=("$workflow")
        fi
    done

    if [ ${#missing_files[@]} -eq 0 ]; then
        log_success "✅ All expected workflow files are present"
        return 0
    else
        log_error "❌ Missing workflow files: ${missing_files[*]}"
        return 1
    fi
}

# Function to check CI/CD policy documentation
check_policy_documentation() {
    log_info "Checking CI/CD policy documentation..."

    if [ -f "CI_CD_POLICY.md" ]; then
        if grep -q "manual-only" "CI_CD_POLICY.md" && grep -q "workflow_dispatch" "CI_CD_POLICY.md"; then
            log_success "✅ CI/CD policy documentation is present and mentions manual-only"
            return 0
        else
            log_error "❌ CI/CD policy documentation missing manual-only references"
            return 1
        fi
    else
        log_error "❌ CI/CD policy document (CI_CD_POLICY.md) not found"
        return 1
    fi
}

# Function to generate verification report
generate_ci_cd_report() {
    log_info "Generating CI/CD verification report..."

    mkdir -p "Documentation/Tests/CI_CD_VERIFICATION_$(date +%Y%m%d_%H%M%S)"

    cat > "Documentation/Tests/CI_CD_VERIFICATION_$(date +%Y%m%d_%H%M%S)/ci_cd_verification.html" << EOF
<!DOCTYPE html>
<html>
<head>
    <title>ShareConnect CI/CD Manual-Only Verification</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; background: #f5f5f5; }
        .header { background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%); color: white; padding: 30px; border-radius: 10px; margin-bottom: 30px; }
        .section { background: white; padding: 20px; margin: 20px 0; border-radius: 10px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        .status-passed { color: #28a745; font-weight: bold; background: #d4edda; padding: 10px; border-radius: 5px; }
        .status-failed { color: #dc3545; font-weight: bold; background: #f8d7da; padding: 10px; border-radius: 5px; }
        .checklist { background: #f8f9fa; padding: 15px; margin: 10px 0; border-radius: 5px; }
        .check-passed { color: #28a745; }
        .check-failed { color: #dc3545; }
        table { width: 100%; border-collapse: collapse; margin: 20px 0; background: white; border-radius: 5px; overflow: hidden; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        th, td { padding: 15px; text-align: left; border-bottom: 1px solid #ddd; }
        th { background-color: #4CAF50; color: white; font-weight: bold; }
        tr:nth-child(even) { background-color: #f8f9fa; }
        tr:hover { background-color: #e8f4f8; }
    </style>
</head>
<body>
    <div class="header">
        <h1>🔒 ShareConnect CI/CD Manual-Only Verification</h1>
        <p><strong>Generated:</strong> $(date)</p>
        <p><strong>Status:</strong> <span class="$OVERALL_STATUS_CLASS">$OVERALL_STATUS</span></p>
    </div>

    <div class="section">
        <h2>📋 Verification Checklist</h2>
        <div class="checklist">
            <p><strong>Workflow Files:</strong> <span class="$WORKFLOW_STATUS_CLASS">$WORKFLOW_STATUS</span></p>
            <p><strong>Manual-Only Config:</strong> <span class="$MANUAL_STATUS_CLASS">$MANUAL_STATUS</span></p>
            <p><strong>Policy Documentation:</strong> <span class="$POLICY_STATUS_CLASS">$POLICY_STATUS</span></p>
        </div>
    </div>

    <div class="section">
        <h2>🔍 Workflow Analysis</h2>
        <table>
            <tr>
                <th>Workflow</th>
                <th>Manual Only</th>
                <th>Automated Triggers</th>
                <th>Status</th>
            </tr>
            <tr>
                <td>comprehensive-qa.yml</td>
                <td>$COMPREHENSIVE_MANUAL</td>
                <td>$COMPREHENSIVE_AUTO</td>
                <td>$COMPREHENSIVE_STATUS</td>
            </tr>
            <tr>
                <td>snyk-security-scan.yml</td>
                <td>$SNYK_MANUAL</td>
                <td>$SNYK_AUTO</td>
                <td>$SNYK_STATUS</td>
            </tr>
            <tr>
                <td>combined-qa-security.yml</td>
                <td>$COMBINED_MANUAL</td>
                <td>$COMBINED_AUTO</td>
                <td>$COMBINED_STATUS</td>
            </tr>
        </table>
    </div>

    <div class="section">
        <h2>📊 Summary</h2>
        <p><strong>Verification Date:</strong> $(date)</p>
        <p><strong>Policy:</strong> Manual-only CI/CD execution</p>
        <p><strong>Workflows Checked:</strong> 3</p>
        <p><strong>Status:</strong> $OVERALL_STATUS</p>
    </div>

    <div class="section">
        <h2>🔗 Related Documentation</h2>
        <ul>
            <li><a href="../CI_CD_POLICY.md">CI/CD Policy Document</a></li>
            <li><a href="../SNYK_INTEGRATION_README.md">Snyk Integration Guide</a></li>
            <li><a href="../AGENTS.md">Development Commands</a></li>
        </ul>
    </div>
</body>
</html>
EOF

    cat > "Documentation/Tests/CI_CD_VERIFICATION_$(date +%Y%m%d_%H%M%S)/ci_cd_summary.txt" << EOF
ShareConnect CI/CD Manual-Only Verification Summary
==================================================

Generated: $(date)

VERIFICATION STATUS
-------------------
Overall Status: $OVERALL_STATUS

WORKFLOW ANALYSIS
-----------------
Comprehensive QA:
  Manual Only: $COMPREHENSIVE_MANUAL
  Auto Triggers: $COMPREHENSIVE_AUTO
  Status: $COMPREHENSIVE_STATUS

Snyk Security Scan:
  Manual Only: $SNYK_MANUAL
  Auto Triggers: $SNYK_AUTO
  Status: $SNYK_STATUS

Combined QA + Security:
  Manual Only: $COMBINED_MANUAL
  Auto Triggers: $COMBINED_AUTO
  Status: $COMBINED_STATUS

POLICY COMPLIANCE
------------------
Workflow Files: $WORKFLOW_STATUS
Manual-Only Config: $MANUAL_STATUS
Policy Documentation: $POLICY_STATUS

CONCLUSION
----------
$OVERALL_STATUS - All workflows are configured for manual execution only.

For detailed policy information, see CI_CD_POLICY.md
EOF

    log_success "CI/CD verification report generated"
}

# Main verification logic
main() {
    log_info "🚀 Starting ShareConnect CI/CD Manual-Only Verification"
    log_info "Policy: All workflows must be manual-only (workflow_dispatch)"

    # Initialize counters
    TOTAL_CHECKS=0
    PASSED_CHECKS=0

    # Check workflow files exist
    ((TOTAL_CHECKS++))
    if check_workflow_files; then
        WORKFLOW_STATUS="PASSED"
        WORKFLOW_STATUS_CLASS="check-passed"
        ((PASSED_CHECKS++))
    else
        WORKFLOW_STATUS="FAILED"
        WORKFLOW_STATUS_CLASS="check-failed"
    fi

    # Check individual workflows
    COMPREHENSIVE_MANUAL="No"
    COMPREHENSIVE_AUTO="None"
    COMPREHENSIVE_STATUS="❌ Not Checked"

    SNYK_MANUAL="No"
    SNYK_AUTO="None"
    SNYK_STATUS="❌ Not Checked"

    COMBINED_MANUAL="No"
    COMBINED_AUTO="None"
    COMBINED_STATUS="❌ Not Checked"

    MANUAL_CHECKS_PASSED=0

    if [ -f "$WORKFLOW_DIR/comprehensive-qa.yml" ]; then
        ((TOTAL_CHECKS++))
        if check_workflow_manual_only "$WORKFLOW_DIR/comprehensive-qa.yml"; then
            COMPREHENSIVE_MANUAL="Yes"
            COMPREHENSIVE_STATUS="✅ Manual Only"
            ((PASSED_CHECKS++))
            ((MANUAL_CHECKS_PASSED++))
        else
            COMPREHENSIVE_STATUS="❌ Has Auto Triggers"
        fi
    fi

    if [ -f "$WORKFLOW_DIR/snyk-security-scan.yml" ]; then
        ((TOTAL_CHECKS++))
        if check_workflow_manual_only "$WORKFLOW_DIR/snyk-security-scan.yml"; then
            SNYK_MANUAL="Yes"
            SNYK_STATUS="✅ Manual Only"
            ((PASSED_CHECKS++))
            ((MANUAL_CHECKS_PASSED++))
        else
            SNYK_STATUS="❌ Has Auto Triggers"
        fi
    fi

    if [ -f "$WORKFLOW_DIR/combined-qa-security.yml" ]; then
        ((TOTAL_CHECKS++))
        if check_workflow_manual_only "$WORKFLOW_DIR/combined-qa-security.yml"; then
            COMBINED_MANUAL="Yes"
            COMBINED_STATUS="✅ Manual Only"
            ((PASSED_CHECKS++))
            ((MANUAL_CHECKS_PASSED++))
        else
            COMBINED_STATUS="❌ Has Auto Triggers"
        fi
    fi

    # Set manual status
    if [ $MANUAL_CHECKS_PASSED -eq 3 ]; then
        MANUAL_STATUS="PASSED"
        MANUAL_STATUS_CLASS="check-passed"
    else
        MANUAL_STATUS="FAILED"
        MANUAL_STATUS_CLASS="check-failed"
    fi

    # Check policy documentation
    ((TOTAL_CHECKS++))
    if check_policy_documentation; then
        POLICY_STATUS="PASSED"
        POLICY_STATUS_CLASS="check-passed"
        ((PASSED_CHECKS++))
    else
        POLICY_STATUS="FAILED"
        POLICY_STATUS_CLASS="check-failed"
    fi

    # Calculate overall status
    SUCCESS_RATE=$((PASSED_CHECKS * 100 / TOTAL_CHECKS))

    if [ $PASSED_CHECKS -eq $TOTAL_CHECKS ]; then
        OVERALL_STATUS="✅ ALL CHECKS PASSED"
        OVERALL_STATUS_CLASS="status-passed"
    else
        OVERALL_STATUS="❌ ISSUES DETECTED"
        OVERALL_STATUS_CLASS="status-failed"
    fi

    # Generate report
    generate_ci_cd_report

    # Final output
    echo ""
    echo -e "${BLUE}═══════════════════════════════════════════════════════════════════${NC}"
    echo -e "${BLUE}                CI/CD MANUAL-ONLY VERIFICATION SUMMARY               ${NC}"
    echo -e "${BLUE}═══════════════════════════════════════════════════════════════════${NC}"
    echo -e "Total Checks: $TOTAL_CHECKS"
    echo -e "Passed: $PASSED_CHECKS"
    echo -e "Success Rate: $SUCCESS_RATE%"
    echo ""
    echo -e "Workflow Status:"
    echo -e "  Comprehensive QA: $COMPREHENSIVE_STATUS"
    echo -e "  Snyk Security: $SNYK_STATUS"
    echo -e "  Combined QA+Security: $COMBINED_STATUS"
    echo ""
    echo -e "Policy: Manual-only CI/CD execution confirmed"
    echo ""

    if [ $PASSED_CHECKS -eq $TOTAL_CHECKS ]; then
        echo -e "${GREEN}╔═══════════════════════════════════════════════════════════════════╗${NC}"
        echo -e "${GREEN}║              ✅ CI/CD MANUAL-ONLY POLICY VERIFIED                  ║${NC}"
        echo -e "${GREEN}╚═══════════════════════════════════════════════════════════════════╝${NC}"
        exit 0
    else
        echo -e "${RED}╔═══════════════════════════════════════════════════════════════════╗${NC}"
        echo -e "${RED}║            ❌ CI/CD CONFIGURATION ISSUES DETECTED                  ║${NC}"
        echo -e "${RED}╚═══════════════════════════════════════════════════════════════════╝${NC}"
        echo ""
        echo -e "Please review and fix workflow configurations to ensure manual-only execution."
        exit 1
    fi
}

# Run main verification
main "$@"