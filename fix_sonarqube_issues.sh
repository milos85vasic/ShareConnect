#!/bin/bash

# SonarQube Issues Auto-Fix Script for ShareConnect
# Automatically addresses common code quality issues found by SonarQube

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SONARQUBE_URL="http://localhost:9000"

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

log_info "Starting SonarQube issues auto-fix process..."

# Check if SonarQube is running
if ! curl -s -f "$SONARQUBE_URL/api/system/status" | grep -q '"status":"UP"'; then
    log_error "SonarQube is not running. Please start it first with: docker-compose -f docker-compose.sonarqube.yml up -d"
    exit 1
fi

# Get authentication token
SONAR_TOKEN_FILE="$HOME/.sonarqube_token"
if [ -f "$SONAR_TOKEN_FILE" ]; then
    SONAR_TOKEN=$(cat "$SONAR_TOKEN_FILE")
else
    log_warning "No SonarQube token found. Using anonymous access (limited functionality)"
    SONAR_TOKEN=""
fi

# Function to get issues from SonarQube
get_issues() {
    local rule_key=$1
    local ps=${2:-500}

    if [ -n "$SONAR_TOKEN" ]; then
        curl -s "$SONARQUBE_URL/api/issues/search?componentKeys=shareconnect&rules=$rule_key&ps=$ps" \
            -H "Authorization: Bearer $SONAR_TOKEN"
    else
        curl -s "$SONARQUBE_URL/api/issues/search?componentKeys=shareconnect&rules=$rule_key&ps=$ps"
    fi
}

# Function to fix an issue by updating the file
fix_issue() {
    local file_path=$1
    local line=$2
    local rule=$3
    local message=$4

    log_info "Fixing $rule in $file_path:$line - $message"

    case $rule in
        "kotlin:S100")
            # Function name should not contain underscores
            fix_function_name "$file_path" "$line"
            ;;
        "kotlin:S107")
            # Constructor has too many parameters
            fix_constructor_parameters "$file_path" "$line"
            ;;
        "kotlin:S108")
            # Nested blocks should be avoided
            fix_nested_blocks "$file_path" "$line"
            ;;
        "kotlin:S117")
            # Variable name should not contain underscores
            fix_variable_name "$file_path" "$line"
            ;;
        "kotlin:S1192")
            # Define a constant instead of duplicating this literal
            fix_magic_number "$file_path" "$line"
            ;;
        "kotlin:S1481")
            # Remove unused local variable
            fix_unused_variable "$file_path" "$line"
            ;;
        "kotlin:S1874")
            # Deprecated code should be removed
            fix_deprecated_code "$file_path" "$line"
            ;;
        "java:S100")
            # Method name should not contain underscores
            fix_java_method_name "$file_path" "$line"
            ;;
        "java:S106")
            # Standard outputs should not be used directly
            fix_java_print_statements "$file_path" "$line"
            ;;
        "java:S108")
            # Nested blocks should be avoided
            fix_java_nested_blocks "$file_path" "$line"
            ;;
        "java:S117")
            # Variable name should not contain underscores
            fix_java_variable_name "$file_path" "$line"
            ;;
        "java:S1192")
            # Define a constant instead of duplicating this literal
            fix_java_magic_number "$file_path" "$line"
            ;;
        "java:S1481")
            # Remove unused local variable
            fix_java_unused_variable "$file_path" "$line"
            ;;
        "java:S1874")
            # Deprecated code should be removed
            fix_java_deprecated_code "$file_path" "$line"
            ;;
        *)
            log_warning "No auto-fix available for rule: $rule"
            ;;
    esac
}

# Kotlin fixes
fix_function_name() {
    local file=$1
    local line=$2
    # This would require more complex parsing - for now just log
    log_warning "Manual fix needed: Function name contains underscores at $file:$line"
}

fix_constructor_parameters() {
    local file=$1
    local line=$2
    log_warning "Manual fix needed: Constructor has too many parameters at $file:$line"
}

fix_nested_blocks() {
    local file=$1
    local line=$2
    log_warning "Manual fix needed: Nested blocks at $file:$line"
}

fix_variable_name() {
    local file=$1
    local line=$2
    log_warning "Manual fix needed: Variable name contains underscores at $file:$line"
}

fix_magic_number() {
    local file=$1
    local line=$2
    log_warning "Manual fix needed: Magic number at $file:$line"
}

fix_unused_variable() {
    local file=$1
    local line=$2

    # Read the specific line
    local content=$(sed -n "${line}p" "$file")

    # Try to identify and remove unused variable declaration
    if echo "$content" | grep -q "val.*=" || echo "$content" | grep -q "var.*="; then
        # Remove the line if it's a simple variable declaration
        sed -i "${line}d" "$file"
        log_success "Removed unused variable declaration at $file:$line"
    else
        log_warning "Manual fix needed: Unused variable at $file:$line"
    fi
}

fix_deprecated_code() {
    local file=$1
    local line=$2
    log_warning "Manual fix needed: Deprecated code at $file:$line"
}

# Java fixes
fix_java_method_name() {
    local file=$1
    local line=$2
    log_warning "Manual fix needed: Java method name contains underscores at $file:$line"
}

fix_java_print_statements() {
    local file=$1
    local line=$2
    log_warning "Manual fix needed: Java print statement at $file:$line"
}

fix_java_nested_blocks() {
    local file=$1
    local line=$2
    log_warning "Manual fix needed: Java nested blocks at $file:$line"
}

fix_java_variable_name() {
    local file=$1
    local line=$2
    log_warning "Manual fix needed: Java variable name contains underscores at $file:$line"
}

fix_java_magic_number() {
    local file=$1
    local line=$2
    log_warning "Manual fix needed: Java magic number at $file:$line"
}

fix_java_unused_variable() {
    local file=$1
    local line=$2

    # Read the specific line
    local content=$(sed -n "${line}p" "$file")

    # Try to identify and remove unused variable declaration
    if echo "$content" | grep -q "String.*=" || echo "$content" | grep -q "int.*=" || echo "$content" | grep -q "boolean.*="; then
        # Remove the line if it's a simple variable declaration
        sed -i "${line}d" "$file"
        log_success "Removed unused Java variable declaration at $file:$line"
    else
        log_warning "Manual fix needed: Unused Java variable at $file:$line"
    fi
}

fix_java_deprecated_code() {
    local file=$1
    local line=$2
    log_warning "Manual fix needed: Deprecated Java code at $file:$line"
}

# Get all issues from SonarQube
log_info "Fetching issues from SonarQube..."
ISSUES_JSON=$(get_issues "" 1000)

if [ -z "$ISSUES_JSON" ] || echo "$ISSUES_JSON" | jq -e '.issues' >/dev/null 2>&1; then
    ISSUE_COUNT=$(echo "$ISSUES_JSON" | jq -r '.total // 0')

    if [ "$ISSUE_COUNT" -gt 0 ]; then
        log_info "Found $ISSUE_COUNT issues to address"

        # Process each issue
        echo "$ISSUES_JSON" | jq -r '.issues[] | "\(.component)|\(.line)|\(.rule)|\(.message)"' | while IFS='|' read -r component line rule message; do
            # Convert component path to file path
            file_path="${component#shareconnect:}"

            if [ -f "$file_path" ]; then
                fix_issue "$file_path" "$line" "$rule" "$message"
            else
                log_warning "File not found: $file_path"
            fi
        done

        log_success "Completed auto-fix attempts for SonarQube issues"
        log_info "Some issues may require manual intervention"
    else
        log_success "No issues found - code is clean!"
    fi
else
    log_error "Failed to fetch issues from SonarQube"
    exit 1
fi

# Run tests to verify fixes
log_info "Running tests to verify fixes..."
if ./run_unit_tests.sh >/dev/null 2>&1; then
    log_success "Unit tests pass after fixes"
else
    log_warning "Unit tests failed after fixes - manual review needed"
fi

log_info "SonarQube issues auto-fix process completed"