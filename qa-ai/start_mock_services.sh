#!/bin/bash

# ShareConnect AI QA - Mock Services Startup Script
# This script starts all mock services for external dependencies

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
MOCK_SERVICES_JAR="$SCRIPT_DIR/build/libs/mock-services.jar"
MOCK_SERVICES_PID_FILE="$SCRIPT_DIR/mock_services.pid"
LOG_FILE="$SCRIPT_DIR/mock_services.log"

# Service ports (matching qa-config.yaml)
declare -A SERVICE_PORTS=(
    ["metube"]=8081
    ["ytdl"]=8082
    ["qbittorrent"]=8083
    ["transmission"]=9091
    ["utorrent"]=8080
    ["jdownloader"]=3129
)

echo -e "${BLUE}"
echo "╔══════════════════════════════════════════════════════════════╗"
echo "║         ShareConnect AI QA - Mock Services Startup          ║"
echo "╚══════════════════════════════════════════════════════════════╝"
echo -e "${NC}"

# Parse command line arguments
SERVICES_TO_START="all"
STOP_SERVICES=false

while [[ $# -gt 0 ]]; do
    case $1 in
        --services)
            SERVICES_TO_START="$2"
            shift 2
            ;;
        --stop)
            STOP_SERVICES=true
            shift
            ;;
        --help)
            echo "ShareConnect AI QA - Mock Services Startup"
            echo ""
            echo "Usage: $0 [OPTIONS]"
            echo ""
            echo "Options:"
            echo "  --services LIST   Comma-separated list of services to start (default: all)"
            echo "                    Available: metube,ytdl,qbittorrent,transmission,utorrent,jdownloader"
            echo "  --stop            Stop running mock services"
            echo "  --help            Show this help message"
            echo ""
            echo "Examples:"
            echo "  $0                          # Start all services"
            echo "  $0 --services metube,ytdl  # Start only MeTube and YT-DLP"
            echo "  $0 --stop                   # Stop all services"
            exit 0
            ;;
        *)
            echo -e "${RED}Unknown option: $1${NC}"
            echo "Use --help for usage information"
            exit 1
            ;;
    esac
done

# Function to check if port is in use
is_port_in_use() {
    local port=$1
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
        return 0
    else
        return 1
    fi
}

# Function to wait for service to be ready
wait_for_service() {
    local service=$1
    local port=$2
    local max_attempts=30
    local attempt=1

    echo -e "${YELLOW}Waiting for $service to be ready on port $port...${NC}"

    while [[ $attempt -le $max_attempts ]]; do
        if curl -s "http://localhost:$port/health" >/dev/null 2>&1; then
            echo -e "${GREEN}✓ $service is ready${NC}"
            return 0
        fi

        echo -e "${YELLOW}Attempt $attempt/$max_attempts...${NC}"
        sleep 2
        ((attempt++))
    done

    echo -e "${RED}✗ $service failed to start within $(($max_attempts * 2)) seconds${NC}"
    return 1
}

# Stop services if requested
if [[ "$STOP_SERVICES" == true ]]; then
    echo -e "${YELLOW}Stopping mock services...${NC}"

    if [[ -f "$MOCK_SERVICES_PID_FILE" ]]; then
        MOCK_PID=$(cat "$MOCK_SERVICES_PID_FILE")
        if kill -0 "$MOCK_PID" 2>/dev/null; then
            echo -e "${YELLOW}Stopping mock services process (PID: $MOCK_PID)...${NC}"
            kill "$MOCK_PID" 2>/dev/null || true
            sleep 2

            # Force kill if still running
            if kill -0 "$MOCK_PID" 2>/dev/null; then
                kill -9 "$MOCK_PID" 2>/dev/null || true
            fi
        fi
        rm -f "$MOCK_SERVICES_PID_FILE"
    fi

    # Kill any remaining processes on service ports
    for service in "${!SERVICE_PORTS[@]}"; do
        port=${SERVICE_PORTS[$service]}
        if is_port_in_use "$port"; then
            echo -e "${YELLOW}Killing process on port $port...${NC}"
            lsof -ti :$port | xargs kill -9 2>/dev/null || true
        fi
    done

    echo -e "${GREEN}✓ Mock services stopped${NC}"
    exit 0
fi

# Check if services are already running
echo -e "${YELLOW}[1/4] Checking for existing services...${NC}"

RUNNING_SERVICES=()
for service in "${!SERVICE_PORTS[@]}"; do
    port=${SERVICE_PORTS[$service]}
    if is_port_in_use "$port"; then
        RUNNING_SERVICES+=("$service:$port")
    fi
done

if [[ ${#RUNNING_SERVICES[@]} -gt 0 ]]; then
    echo -e "${RED}ERROR: Some services are already running: ${RUNNING_SERVICES[*]}${NC}"
    echo -e "${YELLOW}Stop them first with: $0 --stop${NC}"
    exit 1
fi

echo -e "${GREEN}✓ No conflicting services found${NC}"

# Build mock services if needed
echo -e "${YELLOW}[2/4] Building mock services...${NC}"

cd "$PROJECT_ROOT"

if [[ ! -f "$MOCK_SERVICES_JAR" ]] || [[ "$SCRIPT_DIR/src" -nt "$MOCK_SERVICES_JAR" ]]; then
    echo -e "${YELLOW}Building mock services JAR...${NC}"
    ./gradlew :qa-ai:shadowJar

    if [[ ! -f "$MOCK_SERVICES_JAR" ]]; then
        echo -e "${RED}ERROR: Failed to build mock services JAR${NC}"
        exit 1
    fi
else
    echo -e "${GREEN}✓ Mock services JAR is up to date${NC}"
fi

# Determine which services to start
if [[ "$SERVICES_TO_START" == "all" ]]; then
    SERVICES_LIST=("${!SERVICE_PORTS[@]}")
else
    IFS=',' read -ra SERVICES_LIST <<< "$SERVICES_TO_START"
fi

echo -e "${YELLOW}[3/4] Starting mock services: ${SERVICES_LIST[*]}${NC}"

# Start mock services
java -cp "$MOCK_SERVICES_JAR" com.shareconnect.qa.ai.mocks.MockServiceManager "${SERVICES_LIST[@]}" > "$LOG_FILE" 2>&1 &
MOCK_PID=$!

echo $MOCK_PID > "$MOCK_SERVICES_PID_FILE"

# Wait a moment for services to start
sleep 3

# Verify services are running
echo -e "${YELLOW}[4/4] Verifying services...${NC}"

FAILED_SERVICES=()
SUCCESS_SERVICES=()

for service in "${SERVICES_LIST[@]}"; do
    port=${SERVICE_PORTS[$service]}

    if is_port_in_use "$port"; then
        if wait_for_service "$service" "$port"; then
            SUCCESS_SERVICES+=("$service:$port")
        else
            FAILED_SERVICES+=("$service:$port")
        fi
    else
        FAILED_SERVICES+=("$service:$port")
    fi
done

# Print results
echo ""
echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}                 MOCK SERVICES STATUS                         ${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"

if [[ ${#SUCCESS_SERVICES[@]} -gt 0 ]]; then
    echo -e "${GREEN}Successfully started services:${NC}"
    for service in "${SUCCESS_SERVICES[@]}"; do
        echo -e "${GREEN}  ✓ $service${NC}"
    done
fi

if [[ ${#FAILED_SERVICES[@]} -gt 0 ]]; then
    echo -e "${RED}Failed to start services:${NC}"
    for service in "${FAILED_SERVICES[@]}"; do
        echo -e "${RED}  ✗ $service${NC}"
    done
    echo ""
    echo -e "${RED}Check log file: $LOG_FILE${NC}"
    exit 1
fi

echo ""
echo -e "Mock Services PID: $MOCK_PID"
echo -e "Log File: $LOG_FILE"
echo ""
echo -e "${GREEN}All mock services are running and ready for testing!${NC}"
echo ""

# Export service URLs for use by tests
for service in "${SERVICES_LIST[@]}"; do
    port=${SERVICE_PORTS[$service]}
    export MOCK_${service^^}_URL="http://localhost:$port"
done

# Keep script running to maintain services (optional)
if [[ "${KEEP_SERVICES_RUNNING:-false}" == "true" ]]; then
    echo -e "${YELLOW}Keeping services running... Press Ctrl+C to stop${NC}"
    trap "echo -e '\n${YELLOW}Stopping services...${NC}'; kill $MOCK_PID 2>/dev/null || true; rm -f '$MOCK_SERVICES_PID_FILE'; exit 0" INT
    wait $MOCK_PID
fi