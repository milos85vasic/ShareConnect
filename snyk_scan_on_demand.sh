#!/bin/bash

# ShareConnect On-Demand Snyk Security Scanning
# Quick vulnerability assessment for CI/CD and development workflows

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
REPORT_DIR="Documentation/Tests/${TIMESTAMP}_SNYK_ON_DEMAND"

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

# Function to run quick Snyk scan
run_quick_scan() {
    local severity="${1:-medium}"
    local fail_on_issues="${2:-false}"

    log_info "Running quick Snyk security scan (severity: $severity)..."

    # Create report directory
    mkdir -p "$REPORT_DIR"

    # Check if Snyk CLI is available locally
    if command -v snyk >/dev/null 2>&1; then
        log_info "Using local Snyk CLI"

        # Check for token
        if [ -z "$SNYK_TOKEN" ]; then
            log_warning "No SNYK_TOKEN set - running in freemium mode"
            FREEMIUM_MODE=true
        else
            FREEMIUM_MODE=false
        fi

        if [ "$FREEMIUM_MODE" = true ]; then
            # Freemium mode with local CLI
            log_info "Attempting basic scan in freemium mode..."
            snyk test --severity-threshold="$severity" --json > "$REPORT_DIR/snyk-results.json" 2> "$REPORT_DIR/snyk-errors.log" || {
                log_warning "Freemium scan failed - this is expected for private repos"
                echo '{"error": "Freemium mode - limited scanning available", "freemium": true}' > "$REPORT_DIR/snyk-results.json"
            }

            # Container scanning not available in freemium
            log_warning "Container scanning not available in freemium mode"
            echo '{"error": "Container scanning requires Snyk token", "freemium": true}' > "$REPORT_DIR/snyk-container-results.json"
        else
            # Full mode with token
            # Run dependency scan
            snyk test --all-projects --detection-depth=4 --severity-threshold="$severity" --json > "$REPORT_DIR/snyk-results.json" 2> "$REPORT_DIR/snyk-errors.log" || true

            # Run container scan if Dockerfile exists
            if [ -f "Dockerfile" ]; then
                snyk container test --file=Dockerfile --severity-threshold="$severity" --json > "$REPORT_DIR/snyk-container-results.json" 2> "$REPORT_DIR/snyk-container-errors.log" || true
            fi
        fi
    else
        log_info "Snyk CLI not found locally, using Docker"

        # Ensure Docker is available
        if ! docker info >/dev/null 2>&1; then
            log_error "Docker is not available and Snyk CLI is not installed locally"
            exit 1
        fi

        # Check for SNYK_TOKEN
        if [ -z "$SNYK_TOKEN" ]; then
            log_warning "SNYK_TOKEN not set - running in freemium mode with Docker"
            FREEMIUM_MODE=true
        else
            FREEMIUM_MODE=false
        fi

        if [ "$FREEMIUM_MODE" = true ]; then
            # Freemium mode with Docker
            log_info "Attempting basic scan in freemium mode..."
            docker run --rm \
                -v "$(pwd):/app" \
                -e SNYK_DISABLE_ANALYTICS=1 \
                snyk/snyk-cli:latest \
                test --severity-threshold="$severity" --json > "$REPORT_DIR/snyk-results.json" 2> "$REPORT_DIR/snyk-errors.log" || {
                log_warning "Freemium scan failed - this is expected for private repos"
                echo '{"error": "Freemium mode - limited scanning available", "freemium": true}' > "$REPORT_DIR/snyk-results.json"
            }

            # Container scanning not available in freemium
            log_warning "Container scanning not available in freemium mode"
            echo '{"error": "Container scanning requires Snyk token", "freemium": true}' > "$REPORT_DIR/snyk-container-results.json"
        else
            # Full mode with token
            # Run scan using Docker
            docker run --rm \
                -v "$(pwd):/app" \
                -e SNYK_TOKEN="$SNYK_TOKEN" \
                -e SNYK_DISABLE_ANALYTICS=1 \
                snyk/snyk-cli:latest \
                test --all-projects --detection-depth=4 --severity-threshold="$severity" --json > "$REPORT_DIR/snyk-results.json" 2> "$REPORT_DIR/snyk-errors.log" || true

            # Run container scan if Dockerfile exists
            if [ -f "Dockerfile" ]; then
                docker run --rm \
                    -v "$(pwd):/app" \
                    -e SNYK_TOKEN="$SNYK_TOKEN" \
                    -e SNYK_DISABLE_ANALYTICS=1 \
                    snyk/snyk-cli:latest \
                    container test --file=Dockerfile --severity-threshold="$severity" --json > "$REPORT_DIR/snyk-container-results.json" 2> "$REPORT_DIR/snyk-container-errors.log" || true
            fi
        fi
    fi

    # Analyze results
    if [ -f "$REPORT_DIR/snyk-results.json" ]; then
        # Check if this is freemium mode error response
        if jq -e '.freemium' "$REPORT_DIR/snyk-results.json" >/dev/null 2>&1; then
            log_warning "Freemium mode detected - limited scanning results"
            VULN_COUNT="N/A"
            CRITICAL_VULNS="N/A"
            HIGH_VULNS="N/A"

            # Generate freemium summary
            cat > "$REPORT_DIR/snyk_quick_summary.txt" << EOF
ShareConnect Quick Snyk Security Scan (Freemium Mode)
====================================================

Generated: $(date)
Severity Threshold: $severity

FREEMIUM LIMITATIONS:
- Limited scan frequency
- No private repository support
- Basic vulnerability detection only
- No advanced reporting features

Status: Freemium mode - upgrade to token for full functionality

To get full Snyk capabilities:
1. Sign up at https://snyk.io
2. Get API token from account settings
3. Set: export SNYK_TOKEN=your_token_here

Report Directory: $REPORT_DIR
EOF

            log_info "Freemium mode - basic scan completed"
            log_info "Upgrade to token for comprehensive security scanning"

        else
            VULN_COUNT=$(jq '.vulnerabilities | length' "$REPORT_DIR/snyk-results.json" 2>/dev/null || echo "0")
            CRITICAL_VULNS=$(jq '[.vulnerabilities[]? | select(.severity == "critical")] | length' "$REPORT_DIR/snyk-results.json" 2>/dev/null || echo "0")
            HIGH_VULNS=$(jq '[.vulnerabilities[]? | select(.severity == "high")] | length' "$REPORT_DIR/snyk-results.json" 2>/dev/null || echo "0")

            log_info "Scan completed:"
            log_info "- Total vulnerabilities: $VULN_COUNT"
            log_info "- Critical: $CRITICAL_VULNS"
            log_info "- High: $HIGH_VULNS"

            # Generate summary
            cat > "$REPORT_DIR/snyk_quick_summary.txt" << EOF
ShareConnect Quick Snyk Security Scan
====================================

Generated: $(date)
Severity Threshold: $severity

Results:
- Total Vulnerabilities: $VULN_COUNT
- Critical: $CRITICAL_VULNS
- High: $HIGH_VULNS

Report Directory: $REPORT_DIR
EOF

            # Check if we should fail on issues
            if [ "$fail_on_issues" = "true" ] && [ "$VULN_COUNT" -gt 0 ]; then
                log_error "Security vulnerabilities found! Failing build."
                echo "Review report: $REPORT_DIR/snyk_quick_summary.txt"
                exit 1
            fi

            if [ "$CRITICAL_VULNS" -gt 0 ] || [ "$HIGH_VULNS" -gt 0 ]; then
                log_warning "Critical or high severity vulnerabilities detected"
                log_warning "Review report: $REPORT_DIR/snyk_quick_summary.txt"
            else
                log_success "No critical or high severity vulnerabilities found"
            fi
        fi
    else
        log_error "Failed to generate scan results"
        exit 1
    fi
}

# Parse command line arguments
SEVERITY="medium"
FAIL_ON_ISSUES="false"

while [[ $# -gt 0 ]]; do
    case $1 in
        --severity)
            SEVERITY="$2"
            shift 2
            ;;
        --fail-on-issues)
            FAIL_ON_ISSUES="true"
            shift
            ;;
        --help)
            echo "ShareConnect On-Demand Snyk Security Scanning"
            echo ""
            echo "Usage: $0 [OPTIONS]"
            echo ""
            echo "Options:"
            echo "  --severity LEVEL     Set severity threshold (low|medium|high|critical) [default: medium]"
            echo "  --fail-on-issues     Fail the script if vulnerabilities are found"
            echo "  --help               Show this help message"
            echo ""
            echo "Examples:"
            echo "  $0                           # Quick scan with medium severity"
            echo "  $0 --severity high           # Scan with high severity threshold"
            echo "  $0 --fail-on-issues          # Fail if any vulnerabilities found"
            exit 0
            ;;
        *)
            log_error "Unknown option: $1"
            echo "Use --help for usage information"
            exit 1
            ;;
    esac
done

# Validate severity level
case $SEVERITY in
    low|medium|high|critical)
        ;;
    *)
        log_error "Invalid severity level: $SEVERITY"
        echo "Valid options: low, medium, high, critical"
        exit 1
        ;;
esac

# Run the scan
log_info "Starting ShareConnect on-demand Snyk security scan..."
run_quick_scan "$SEVERITY" "$FAIL_ON_ISSUES"

log_success "On-demand Snyk scan completed"
log_info "Reports saved to: $REPORT_DIR"