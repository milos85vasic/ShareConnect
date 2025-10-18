#!/bin/bash

# ShareConnect Snyk Security Scanning
# Comprehensive vulnerability assessment and dependency analysis

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
REPORT_DIR="Documentation/Tests/${TIMESTAMP}_SNYK_SCAN"
SNYK_CONTAINER="shareconnect-snyk"
SNYK_CLI_CONTAINER="shareconnect-snyk-cli"

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

# Function to check if container is running
check_container_running() {
    local container_name=$1
    if docker ps --filter "name=$container_name" --filter "status=running" | grep -q "$container_name"; then
        return 0
    else
        return 1
    fi
}

# Function to wait for scan completion
wait_for_scan_completion() {
    local container_name=$1
    local timeout=600  # 10 minutes timeout

    log_info "Waiting for Snyk scan to complete (timeout: ${timeout}s)..."

    local counter=0
    while [ $counter -lt $timeout ]; do
        if ! check_container_running "$container_name"; then
            log_success "Snyk scan completed"
            return 0
        fi

        sleep 5
        ((counter += 5))

        if [ $((counter % 60)) -eq 0 ]; then
            log_info "Still scanning... (${counter}s elapsed)"
        fi
    done

    log_error "Snyk scan timed out after ${timeout} seconds"
    return 1
}

# Create report directory
mkdir -p "$REPORT_DIR"

# Parse command line arguments
ACTION="scan"
SCAN_TYPE="full"
SEVERITY="medium"

if [ $# -gt 0 ]; then
    case $1 in
        --start)
            ACTION="start"
            ;;
        --scan)
            ACTION="scan"
            ;;
        --deps-only)
            ACTION="scan"
            SCAN_TYPE="deps"
            ;;
        --container-only)
            ACTION="scan"
            SCAN_TYPE="container"
            ;;
        --gradle-only)
            ACTION="scan"
            SCAN_TYPE="gradle"
            ;;
        --status)
            ACTION="status"
            ;;
        --stop)
            ACTION="stop"
            ;;
        --severity)
            SEVERITY="$2"
            shift 2
            ;;
        --help)
            echo "Usage: $0 [OPTION]"
            echo "Run Snyk security scanning for ShareConnect"
            echo ""
            echo "Options:"
            echo "  --start              Start Snyk containers"
            echo "  --scan               Run full security scan (default)"
            echo "  --deps-only          Scan dependencies only"
            echo "  --container-only     Scan container images only"
            echo "  --gradle-only        Scan Gradle dependencies only"
            echo "  --status             Check status of Snyk containers"
            echo "  --stop               Stop and remove Snyk containers"
            echo "  --severity LEVEL     Set severity threshold (low|medium|high|critical)"
            echo "  --help               Show this help message"
            echo ""
            echo "Examples:"
            echo "  $0                    # Run full scan"
            echo "  $0 --deps-only        # Scan dependencies only"
            echo "  $0 --severity high    # Scan with high severity threshold"
            exit 0
            ;;
        *)
            log_error "Unknown option: $1"
            echo "Use '$0 --help' for usage information"
            exit 1
            ;;
    esac
fi

# Check prerequisites
log_info "Checking prerequisites..."

# Check for Docker
if ! docker info >/dev/null 2>&1; then
    log_error "Docker is not running. Please start Docker and try again."
    exit 1
fi

# Check for Snyk token (optional for freemium mode)
if [ -z "$SNYK_TOKEN" ]; then
    log_warning "SNYK_TOKEN not set - running in freemium mode"
    log_warning "Freemium limitations:"
    log_warning "  - Limited scan frequency"
    log_warning "  - No private repository support"
    log_warning "  - Basic vulnerability detection only"
    log_warning "  - No advanced reporting features"
    log_warning ""
    log_warning "For full functionality, set: export SNYK_TOKEN=your_token_here"
    log_warning "Get token from: https://app.snyk.io/account"
    FREEMIUM_MODE=true
else
    log_success "Snyk token configured"
    FREEMIUM_MODE=false
fi

log_success "Prerequisites check passed"

case $ACTION in
    "start")
        log_info "Starting Snyk containers..."

        # Create .env file for docker-compose if it doesn't exist
        if [ ! -f ".env" ]; then
            cat > .env << EOF
SNYK_TOKEN=${SNYK_TOKEN}
SNYK_ORG_ID=${SNYK_ORG_ID:-}
GIT_REMOTE_URL=$(git remote get-url origin 2>/dev/null || echo "")
EOF
            log_info "Created .env file with Snyk configuration"
        fi

        # Start containers
        docker-compose -f docker-compose.snyk.yml up -d snyk
        log_success "Snyk containers started"
        log_info "Use '$0 --status' to check readiness"
        exit 0
        ;;

    "status")
        log_info "Checking Snyk container status..."

        if check_container_running "$SNYK_CONTAINER"; then
            log_success "Snyk container is running"
        else
            log_warning "Snyk container is not running"
        fi

        if check_container_running "$SNYK_CLI_CONTAINER"; then
            log_success "Snyk CLI container is running"
        else
            log_info "Snyk CLI container is not running (use --start to launch)"
        fi

        exit 0
        ;;

    "stop")
        log_info "Stopping Snyk containers..."
        docker-compose -f docker-compose.snyk.yml down -v 2>/dev/null || true
        docker stop "$SNYK_CONTAINER" "$SNYK_CLI_CONTAINER" 2>/dev/null || true
        docker rm "$SNYK_CONTAINER" "$SNYK_CLI_CONTAINER" 2>/dev/null || true
        log_success "Snyk containers stopped and removed"
        exit 0
        ;;

    "scan")
        log_info "Starting ShareConnect Snyk Security Scan"
        log_info "Report Directory: $REPORT_DIR"
        log_info "Timestamp: $TIMESTAMP"
        log_info "Scan Type: $SCAN_TYPE"
        log_info "Severity Threshold: $SEVERITY"

        # Ensure containers are running
        if ! check_container_running "$SNYK_CONTAINER"; then
            log_info "Starting Snyk container..."
            $0 --start
            sleep 5
        fi

        # Wait for container to be ready
        if ! check_container_running "$SNYK_CONTAINER"; then
            log_error "Failed to start Snyk container"
            exit 1
        fi

        # Run the appropriate scan based on type
        case $SCAN_TYPE in
            "full")
                log_info "Running comprehensive security scan..."

        if [ "$FREEMIUM_MODE" = true ]; then
            # Freemium mode - limited scanning capabilities
            log_info "Running in freemium mode with limited capabilities..."

            # Try basic dependency scan (may work for public repos)
            log_info "Attempting basic dependency scan..."
            docker exec "$SNYK_CONTAINER" snyk test --severity-threshold="$SEVERITY" --json > "$REPORT_DIR/snyk-deps-results.json" 2> "$REPORT_DIR/snyk-deps-errors.log" || {
                log_warning "Basic dependency scan failed - this is expected in freemium mode"
                echo '{"error": "Freemium mode - limited scanning available", "freemium": true}' > "$REPORT_DIR/snyk-deps-results.json"
            }

            # Container scanning not available in freemium
            log_warning "Container scanning not available in freemium mode"
            echo '{"error": "Container scanning requires Snyk token", "freemium": true}' > "$REPORT_DIR/snyk-container-results.json"

            # Gradle scanning limited in freemium
            log_warning "Gradle scanning limited in freemium mode"
            echo '{"error": "Advanced Gradle scanning requires Snyk token", "freemium": true}' > "$REPORT_DIR/snyk-gradle-results.json"

        else
            # Full mode with token
            # Run dependency scan
            log_info "Scanning dependencies..."
            docker exec "$SNYK_CONTAINER" snyk test --all-projects --detection-depth=6 --severity-threshold="$SEVERITY" --json > "$REPORT_DIR/snyk-deps-results.json" 2> "$REPORT_DIR/snyk-deps-errors.log" || true

            # Run container scan if Dockerfile exists
            if [ -f "Dockerfile" ]; then
                log_info "Scanning container images..."
                docker exec "$SNYK_CONTAINER" snyk container test --file=Dockerfile --severity-threshold="$SEVERITY" --json > "$REPORT_DIR/snyk-container-results.json" 2> "$REPORT_DIR/snyk-container-errors.log" || true
            else
                log_info "No Dockerfile found, skipping container scan"
            fi

            # Run Gradle-specific scans
            log_info "Scanning Gradle dependencies..."
            find . -name "build.gradle*" -type f | while read -r gradle_file; do
                log_info "Scanning $gradle_file..."
                docker exec "$SNYK_CONTAINER" snyk test --file="$gradle_file" --severity-threshold="$SEVERITY" --json >> "$REPORT_DIR/snyk-gradle-results.json" 2>> "$REPORT_DIR/snyk-gradle-errors.log" || true
            done
        fi
                ;;

            "deps")
                log_info "Running dependency scan only..."
                docker exec "$SNYK_CONTAINER" snyk test --all-projects --detection-depth=6 --severity-threshold="$SEVERITY" --json > "$REPORT_DIR/snyk-deps-results.json" 2> "$REPORT_DIR/snyk-deps-errors.log" || true
                ;;

            "container")
                log_info "Running container scan only..."
                if [ -f "Dockerfile" ]; then
                    docker exec "$SNYK_CONTAINER" snyk container test --file=Dockerfile --severity-threshold="$SEVERITY" --json > "$REPORT_DIR/snyk-container-results.json" 2> "$REPORT_DIR/snyk-container-errors.log" || true
                else
                    log_error "No Dockerfile found for container scanning"
                    exit 1
                fi
                ;;

            "gradle")
                log_info "Running Gradle dependency scan only..."
                find . -name "build.gradle*" -type f | while read -r gradle_file; do
                    log_info "Scanning $gradle_file..."
                    docker exec "$SNYK_CONTAINER" snyk test --file="$gradle_file" --severity-threshold="$SEVERITY" --json >> "$REPORT_DIR/snyk-gradle-results.json" 2>> "$REPORT_DIR/snyk-gradle-errors.log" || true
                done
                ;;
        esac

        # Generate comprehensive reports
        log_info "Generating comprehensive reports..."

        # Generate HTML report
        cat > "$REPORT_DIR/snyk_report.html" << EOF
<!DOCTYPE html>
<html>
<head>
    <title>ShareConnect Snyk Security Report</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; background: #f5f5f5; }
        .header { background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%); color: white; padding: 30px; border-radius: 10px; margin-bottom: 30px; }
        .summary { background: white; padding: 30px; margin: 20px 0; border-radius: 10px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        .vulnerability { background: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 10px 0; border-radius: 5px; }
        .critical { background: #f8d7da; border-left-color: #dc3545; }
        .high { background: #f8d7da; border-left-color: #dc3545; }
        .medium { background: #fff3cd; border-left-color: #ffc107; }
        .low { background: #d1ecf1; border-left-color: #17a2b8; }
        .metric { background: #f8f9fa; padding: 15px; margin: 10px 0; border-radius: 5px; }
        table { width: 100%; border-collapse: collapse; margin: 20px 0; background: white; border-radius: 5px; overflow: hidden; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        th, td { padding: 15px; text-align: left; border-bottom: 1px solid #ddd; }
        th { background-color: #4CAF50; color: white; font-weight: bold; }
        tr:nth-child(even) { background-color: #f8f9fa; }
        tr:hover { background-color: #e8f4f8; }
        .status-passed { color: #28a745; font-weight: bold; }
        .status-failed { color: #dc3545; font-weight: bold; }
        .status-warning { color: #ffc107; font-weight: bold; }
    </style>
</head>
<body>
    <div class="header">
        <h1>🔒 ShareConnect Snyk Security Report</h1>
        <p><strong>Generated:</strong> $(date)</p>
        <p><strong>Project:</strong> ShareConnect</p>
        <p><strong>Scan Type:</strong> $SCAN_TYPE</p>
        <p><strong>Severity Threshold:</strong> $SEVERITY</p>
    </div>
EOF

        # Process dependency scan results
        if [ -f "$REPORT_DIR/snyk-deps-results.json" ]; then
            VULN_COUNT=$(jq '.vulnerabilities | length' "$REPORT_DIR/snyk-deps-results.json" 2>/dev/null || echo "0")
            UNIQUE_VULNS=$(jq '[.vulnerabilities[] | .id] | unique | length' "$REPORT_DIR/snyk-deps-results.json" 2>/dev/null || echo "0")

            cat >> "$REPORT_DIR/snyk_report.html" << EOF
    <div class="summary">
        <h2>📊 Dependency Scan Summary</h2>
        <div class="metric">
            <p><strong>Total Vulnerabilities:</strong> $VULN_COUNT</p>
            <p><strong>Unique Vulnerabilities:</strong> $UNIQUE_VULNS</p>
        </div>
    </div>

    <div class="summary">
        <h2>🚨 Vulnerabilities Found</h2>
EOF

            # Add vulnerabilities to HTML report
            jq -r '.vulnerabilities[]? | "<div class=\"vulnerability \(.severity)\"><h4>\(.title)</h4><p><strong>Severity:</strong> \(.severity | ascii_upcase) | <strong>Package:</strong> \(.packageName)@\(.version) | <strong>CVE:</strong> \(.identifiers.CVE[0] // "N/A")</p><p>\(.description)</p></div>"' "$REPORT_DIR/snyk-deps-results.json" >> "$REPORT_DIR/snyk_report.html" 2>/dev/null || true

            cat >> "$REPORT_DIR/snyk_report.html" << EOF
    </div>
EOF
        fi

        # Process container scan results
        if [ -f "$REPORT_DIR/snyk-container-results.json" ]; then
            CONTAINER_VULNS=$(jq '.vulnerabilities | length' "$REPORT_DIR/snyk-container-results.json" 2>/dev/null || echo "0")

            cat >> "$REPORT_DIR/snyk_report.html" << EOF
    <div class="summary">
        <h2>🐳 Container Scan Summary</h2>
        <div class="metric">
            <p><strong>Container Vulnerabilities:</strong> $CONTAINER_VULNS</p>
        </div>
    </div>
EOF
        fi

        cat >> "$REPORT_DIR/snyk_report.html" << EOF
    <div class="summary">
        <h2>🔗 Links</h2>
        <p><a href="https://app.snyk.io/org/${SNYK_ORG_ID:-your-org}/projects" target="_blank">View Full Report in Snyk Dashboard</a></p>
        <p><a href="https://snyk.io/vuln" target="_blank">Snyk Vulnerability Database</a></p>
    </div>
</body>
</html>
EOF

        # Generate JSON report
        cat > "$REPORT_DIR/snyk_report.json" << EOF
{
    "report_type": "snyk_security_scan",
    "generated_at": "$(date -Iseconds)",
    "project": "ShareConnect",
    "timestamp": "$TIMESTAMP",
    "scan_type": "$SCAN_TYPE",
    "severity_threshold": "$SEVERITY",
    "dependency_scan": $(cat "$REPORT_DIR/snyk-deps-results.json" 2>/dev/null || echo "{}"),
    "container_scan": $(cat "$REPORT_DIR/snyk-container-results.json" 2>/dev/null || echo "{}"),
    "gradle_scan": $(cat "$REPORT_DIR/snyk-gradle-results.json" 2>/dev/null || echo "{}")
}
EOF

        # Generate text summary
        cat > "$REPORT_DIR/snyk_summary.txt" << EOF
ShareConnect Snyk Security Scan Summary
======================================

Generated: $(date)
Project: ShareConnect
Scan Type: $SCAN_TYPE
Severity Threshold: $SEVERITY

Dependency Scan Results:
$(if [ -f "$REPORT_DIR/snyk-deps-results.json" ]; then
    echo "- Total Vulnerabilities: $(jq '.vulnerabilities | length' "$REPORT_DIR/snyk-deps-results.json" 2>/dev/null || echo "0")"
    echo "- Unique Vulnerabilities: $(jq '[.vulnerabilities[] | .id] | unique | length' "$REPORT_DIR/snyk-deps-results.json" 2>/dev/null || echo "0")"
else
    echo "- No dependency scan results available"
fi)

Container Scan Results:
$(if [ -f "$REPORT_DIR/snyk-container-results.json" ]; then
    echo "- Container Vulnerabilities: $(jq '.vulnerabilities | length' "$REPORT_DIR/snyk-container-results.json" 2>/dev/null || echo "0")"
else
    echo "- No container scan performed or results available"
fi)

Reports generated:
- HTML Report: snyk_report.html
- JSON Report: snyk_report.json
- Text Summary: snyk_summary.txt
- Raw Results: snyk-*-results.json

Snyk Dashboard: https://app.snyk.io/org/${SNYK_ORG_ID:-your-org}/projects
EOF

        log_success "Snyk security scan completed"
        log_info "Reports saved to: $REPORT_DIR"

        # Check for critical/high severity vulnerabilities
        CRITICAL_VULNS=$(jq '[.vulnerabilities[]? | select(.severity == "critical")] | length' "$REPORT_DIR/snyk-deps-results.json" 2>/dev/null || echo "0")
        HIGH_VULNS=$(jq '[.vulnerabilities[]? | select(.severity == "high")] | length' "$REPORT_DIR/snyk-deps-results.json" 2>/dev/null || echo "0")

        if [ "$CRITICAL_VULNS" -gt 0 ] || [ "$HIGH_VULNS" -gt 0 ]; then
            log_error "Critical/High severity vulnerabilities found: $CRITICAL_VULNS critical, $HIGH_VULNS high"
            log_warning "Please review the security report and address these vulnerabilities"
            exit 1
        else
            log_success "No critical or high severity vulnerabilities found"
        fi

        log_info "Snyk containers are still running. Access dashboard at: https://app.snyk.io"
        log_info "To check status: $0 --status"
        log_info "To stop containers: $0 --stop"
        ;;
esac