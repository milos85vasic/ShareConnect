#!/bin/bash

# SonarQube Code Quality Scanning for ShareConnect
# Comprehensive static analysis and code quality assessment

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
REPORT_DIR="Documentation/Tests/${TIMESTAMP}_SONARQUBE_SCAN"
SONARQUBE_URL="http://localhost:9001"
SONARQUBE_CONTAINER="shareconnect-sonarqube"
POSTGRES_CONTAINER="shareconnect-sonar-postgres"

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

# Function to check if container is healthy (non-blocking)
check_container_health() {
    local container_name=$1
    if docker ps --filter "name=$container_name" --filter "health=healthy" | grep -q "$container_name"; then
        return 0
    else
        return 1
    fi
}

# Function to start containers asynchronously
start_containers_async() {
    log_info "Starting SonarQube containers asynchronously..."

    # Create network if it doesn't exist
    docker network create sonarqube-network 2>/dev/null || true

    # Start PostgreSQL
    docker run -d --name shareconnect-sonar-postgres \
        --network sonarqube-network \
        -e POSTGRES_USER=sonar \
        -e POSTGRES_PASSWORD=sonar \
        -e POSTGRES_DB=sonarqube \
        -v sonar_postgres_data:/var/lib/postgresql/data \
        postgres:15-alpine >/dev/null 2>&1

    # Start SonarQube
    docker run -d --name shareconnect-sonarqube \
        --network sonarqube-network \
        -p 9001:9000 \
        -e SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true \
        -e SONAR_WEB_JAVAOPTS="-Xmx2g -Xms512m" \
        -e SONAR_CE_JAVAOPTS="-Xmx2g -Xms512m" \
        -e SONAR_SEARCH_JAVAOPTS="-Xmx1g -Xms512m" \
        -v sonarqube_data:/opt/sonarqube/data \
        -v sonarqube_extensions:/opt/sonarqube/extensions \
        -v sonarqube_logs:/opt/sonarqube/logs \
        -v sonarqube_temp:/opt/sonarqube/temp \
        sonarqube:10.6-community >/dev/null 2>&1

    log_info "Containers started. Use './run_sonarqube_scan.sh --status' to check readiness"
}

# Function to wait for SonarQube API
wait_for_sonarqube() {
    local max_attempts=30
    local attempt=1

    log_info "Waiting for SonarQube API to be available..."

    while [ $attempt -le $max_attempts ]; do
        if curl -s -f "$SONARQUBE_URL/api/system/status" | grep -q '"status":"UP"'; then
            log_success "SonarQube API is available"
            return 0
        fi

        log_info "Attempt $attempt/$max_attempts: SonarQube API not ready yet..."
        sleep 10
        ((attempt++))
    done

    log_error "SonarQube API failed to become available within $(($max_attempts * 10)) seconds"
    return 1
}

# Create report directory
mkdir -p "$REPORT_DIR"

# Parse command line arguments
ACTION="scan"

if [ $# -gt 0 ]; then
    case $1 in
        --start)
            ACTION="start"
            ;;
        --status)
            ACTION="status"
            ;;
        --scan)
            ACTION="scan"
            ;;
        --stop)
            ACTION="stop"
            ;;
        --help)
            echo "Usage: $0 [OPTION]"
            echo "Run SonarQube code quality analysis for ShareConnect"
            echo ""
            echo "Options:"
            echo "  --start    Start SonarQube containers asynchronously"
            echo "  --status   Check status of SonarQube containers"
            echo "  --scan     Run the code quality scan (default)"
            echo "  --stop     Stop and remove SonarQube containers"
            echo "  --help     Show this help message"
            exit 0
            ;;
        *)
            log_error "Unknown option: $1"
            echo "Use '$0 --help' for usage information"
            exit 1
            ;;
    esac
fi

# Check if Docker is running
if ! docker info >/dev/null 2>&1; then
    log_error "Docker is not running. Please start Docker and try again."
    exit 1
fi

case $ACTION in
    "start")
        start_containers_async
        exit 0
        ;;
    "status")
        log_info "Checking SonarQube container status..."

        if check_container_health "$POSTGRES_CONTAINER"; then
            log_success "PostgreSQL container is healthy"
        else
            log_warning "PostgreSQL container is not healthy"
        fi

        if check_container_health "$SONARQUBE_CONTAINER"; then
            log_success "SonarQube container is healthy"
        else
            log_warning "SonarQube container is not healthy"
        fi

        if curl -s -f "$SONARQUBE_URL/api/system/status" | grep -q '"status":"UP"'; then
            log_success "SonarQube API is available at $SONARQUBE_URL"
        else
            log_warning "SonarQube API is not available"
        fi
        exit 0
        ;;
    "stop")
        log_info "Stopping SonarQube containers..."
        docker stop "$SONARQUBE_CONTAINER" "$POSTGRES_CONTAINER" 2>/dev/null || true
        docker rm "$SONARQUBE_CONTAINER" "$POSTGRES_CONTAINER" 2>/dev/null || true
        docker network rm sonarqube-network 2>/dev/null || true
        log_success "SonarQube containers stopped and removed"
        exit 0
        ;;
    "scan")
        log_info "Starting SonarQube Code Quality Scan"
        log_info "Report Directory: $REPORT_DIR"
        log_info "Timestamp: $TIMESTAMP"

        # Check if containers are running
        if ! docker ps --filter "name=$POSTGRES_CONTAINER" | grep -q "$POSTGRES_CONTAINER" || ! docker ps --filter "name=$SONARQUBE_CONTAINER" | grep -q "$SONARQUBE_CONTAINER"; then
            log_error "SonarQube containers are not running. Start them first with: $0 --start"
            log_info "Then check status with: $0 --status"
            exit 1
        fi

        # Wait for SonarQube API (with timeout)
        if ! wait_for_sonarqube; then
            log_error "SonarQube is not ready. Please wait and try again."
            exit 1
        fi
        ;;
esac

# Generate authentication token if needed
SONAR_TOKEN_FILE="$HOME/.sonarqube_token"
if [ ! -f "$SONAR_TOKEN_FILE" ]; then
    log_info "Generating SonarQube authentication token..."

    # Default admin credentials for first login
    SONAR_ADMIN_TOKEN=$(curl -s -X POST \
        "$SONARQUBE_URL/api/authentication/login" \
        -d "login=admin&password=admin" | jq -r '.token // empty')

    if [ -z "$SONAR_ADMIN_TOKEN" ]; then
        # Try with default token if login fails
        SONAR_ADMIN_TOKEN="squ_1234567890abcdef1234567890abcdef12345678"
    fi

    # Generate user token
    USER_TOKEN=$(curl -s -X POST \
        "$SONARQUBE_URL/api/user_tokens/generate" \
        -H "Authorization: Bearer $SONAR_ADMIN_TOKEN" \
        -d "name=shareconnect-ci&login=admin" | jq -r '.token // empty')

    if [ -n "$USER_TOKEN" ]; then
        echo "$USER_TOKEN" > "$SONAR_TOKEN_FILE"
        log_success "SonarQube token generated and saved"
    else
        log_warning "Could not generate token, using anonymous access"
        USER_TOKEN=""
    fi
else
    USER_TOKEN=$(cat "$SONAR_TOKEN_FILE")
    log_info "Using existing SonarQube token"
fi

# Build the project to ensure all sources are compiled
log_info "Building project for analysis..."
./gradlew clean build -x test --parallel

# Run Detekt for Kotlin code analysis
log_info "Running Detekt analysis..."
./gradlew detekt --continue || true

# Run Lint for Android code analysis
log_info "Running Android Lint analysis..."
./gradlew lint --continue || true

# Run SonarQube analysis
log_info "Running SonarQube analysis..."

SONAR_CMD="sonar-scanner"
if ! command -v sonar-scanner >/dev/null 2>&1; then
    log_warning "sonar-scanner not found locally, trying Docker..."
    SONAR_CMD="docker run --rm -v $(pwd):/usr/src sonarsource/sonar-scanner-cli"
fi

# Prepare sonar-scanner command
if [ -n "$USER_TOKEN" ]; then
    SONAR_SCANNER_CMD="$SONAR_CMD \
        -Dsonar.host.url=$SONARQUBE_URL \
        -Dsonar.login=$USER_TOKEN \
        -Dsonar.projectVersion=$TIMESTAMP"
else
    SONAR_SCANNER_CMD="$SONAR_CMD \
        -Dsonar.host.url=$SONARQUBE_URL \
        -Dsonar.projectVersion=$TIMESTAMP"
fi

# Run the analysis
log_info "Executing: $SONAR_SCANNER_CMD"
eval "$SONAR_SCANNER_CMD"

# Wait for analysis to complete
log_info "Waiting for SonarQube analysis to complete..."
sleep 30

# Generate comprehensive reports
log_info "Generating comprehensive reports..."

# Get project status
PROJECT_STATUS=$(curl -s "$SONARQUBE_URL/api/qualitygates/project_status?projectKey=shareconnect" | jq -r '.projectStatus.status // "UNKNOWN"')

# Get analysis metrics
ANALYSIS_METRICS=$(curl -s "$SONARQUBE_URL/api/measures/component?component=shareconnect&metricKeys=ncloc,complexity,cognitive_complexity,duplicated_lines_density,duplicated_blocks,coverage,sqale_index,reliability_rating,security_rating,sqale_debt_ratio,alert_status" | jq '.')

# Get issues summary
ISSUES_SUMMARY=$(curl -s "$SONARQUBE_URL/api/issues/search?componentKeys=shareconnect&ps=500" | jq '.')

# Generate HTML report
cat > "$REPORT_DIR/sonarqube_report.html" << EOF
<!DOCTYPE html>
<html>
<head>
    <title>ShareConnect SonarQube Analysis Report</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; background: #f5f5f5; }
        .header { background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%); color: white; padding: 30px; border-radius: 10px; margin-bottom: 30px; }
        .summary { background: white; padding: 30px; margin: 20px 0; border-radius: 10px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        .metric { background: #f8f9fa; padding: 15px; margin: 10px 0; border-radius: 5px; }
        .issues { background: white; padding: 20px; margin: 20px 0; border-radius: 10px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        .issue-item { background: #fff3cd; border-left: 4px solid #ffc107; padding: 10px; margin: 10px 0; }
        .status-passed { color: #28a745; font-weight: bold; }
        .status-failed { color: #dc3545; font-weight: bold; }
        .status-warning { color: #ffc107; font-weight: bold; }
        table { width: 100%; border-collapse: collapse; margin: 20px 0; background: white; border-radius: 5px; overflow: hidden; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        th, td { padding: 15px; text-align: left; border-bottom: 1px solid #ddd; }
        th { background-color: #4CAF50; color: white; font-weight: bold; }
        tr:nth-child(even) { background-color: #f8f9fa; }
        tr:hover { background-color: #e8f4f8; }
    </style>
</head>
<body>
    <div class="header">
        <h1>🔍 ShareConnect SonarQube Analysis Report</h1>
        <p><strong>Generated:</strong> $(date)</p>
        <p><strong>Project:</strong> ShareConnect</p>
        <p><strong>Quality Gate Status:</strong>
EOF

# Add quality gate status with appropriate styling
case "$PROJECT_STATUS" in
    "OK")
        echo "<span class=\"status-passed\">✅ PASSED</span>" >> "$REPORT_DIR/sonarqube_report.html"
        ;;
    "ERROR")
        echo "<span class=\"status-failed\">❌ FAILED</span>" >> "$REPORT_DIR/sonarqube_report.html"
        ;;
    "WARN")
        echo "<span class=\"status-warning\">⚠️ WARNING</span>" >> "$REPORT_DIR/sonarqube_report.html"
        ;;
    *)
        echo "<span>UNKNOWN</span>" >> "$REPORT_DIR/sonarqube_report.html"
        ;;
esac

cat >> "$REPORT_DIR/sonarqube_report.html" << EOF
        </p>
    </div>

    <div class="summary">
        <h2>📊 Analysis Summary</h2>
        <div class="metric">
            <h3>Quality Metrics</h3>
EOF

# Add metrics to HTML report
if [ -n "$ANALYSIS_METRICS" ]; then
    echo "$ANALYSIS_METRICS" | jq -r '.component.measures[]? | "<p><strong>\(.metric):</strong> \(.value)</p>"' >> "$REPORT_DIR/sonarqube_report.html"
fi

cat >> "$REPORT_DIR/sonarqube_report.html" << EOF
        </div>
    </div>

    <div class="issues">
        <h2>🚨 Issues Found</h2>
EOF

# Add issues to HTML report
if [ -n "$ISSUES_SUMMARY" ]; then
    ISSUE_COUNT=$(echo "$ISSUES_SUMMARY" | jq -r '.total // 0')
    echo "<p><strong>Total Issues:</strong> $ISSUE_COUNT</p>" >> "$REPORT_DIR/sonarqube_report.html"

    echo "$ISSUES_SUMMARY" | jq -r '.issues[]? | "<div class=\"issue-item\"><h4>\(.message)</h4><p><strong>Severity:</strong> \(.severity) | <strong>Type:</strong> \(.type) | <strong>File:</strong> \(.component)</p></div>"' >> "$REPORT_DIR/sonarqube_report.html"
fi

cat >> "$REPORT_DIR/sonarqube_report.html" << EOF
    </div>

    <div class="summary">
        <h2>🔗 Links</h2>
        <p><a href="$SONARQUBE_URL/dashboard?id=shareconnect" target="_blank">View Full Report in SonarQube</a></p>
        <p><a href="$SONARQUBE_URL/project/issues?id=shareconnect" target="_blank">View All Issues</a></p>
    </div>
</body>
</html>
EOF

# Generate JSON report
cat > "$REPORT_DIR/sonarqube_report.json" << EOF
{
    "report_type": "sonarqube_analysis",
    "generated_at": "$(date -Iseconds)",
    "project": "ShareConnect",
    "timestamp": "$TIMESTAMP",
    "quality_gate_status": "$PROJECT_STATUS",
    "analysis_metrics": $ANALYSIS_METRICS,
    "issues_summary": $ISSUES_SUMMARY,
    "sonarqube_url": "$SONARQUBE_URL",
    "project_key": "shareconnect"
}
EOF

# Generate text summary
cat > "$REPORT_DIR/sonarqube_summary.txt" << EOF
ShareConnect SonarQube Analysis Summary
=======================================

Generated: $(date)
Project: ShareConnect
Quality Gate Status: $PROJECT_STATUS

Analysis Metrics:
$(echo "$ANALYSIS_METRICS" | jq -r '.component.measures[]? | "\(.metric): \(.value)"' 2>/dev/null || echo "Metrics not available")

Issues Summary:
$(echo "$ISSUES_SUMMARY" | jq -r '"Total Issues: \(.total // 0)"' 2>/dev/null || echo "Issues data not available")

SonarQube URL: $SONARQUBE_URL/dashboard?id=shareconnect

Reports generated:
- HTML Report: sonarqube_report.html
- JSON Report: sonarqube_report.json
- Text Summary: sonarqube_summary.txt
EOF

log_success "SonarQube analysis completed"
log_info "Reports saved to: $REPORT_DIR"

# Check quality gate status
if [ "$PROJECT_STATUS" = "ERROR" ]; then
    log_error "Quality gate failed! Check the reports for details."
    exit 1
elif [ "$PROJECT_STATUS" = "WARN" ]; then
    log_warning "Quality gate passed with warnings."
else
    log_success "Quality gate passed successfully!"
fi

log_info "SonarQube containers are still running. Access at: $SONARQUBE_URL"
log_info "To check status: $0 --status"
log_info "To stop containers: $0 --stop"