#!/bin/bash
# ShareConnect Website - Development Server Script
# Always serves the latest files directly from the source directory

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

# Configuration
DEFAULT_PORT="${1:-8080}"

# Function to find first available port
find_available_port() {
    local port=$1
    while true; do
        # Check if port is in use by any process
        if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
            port=$((port + 1))
            continue
        fi
        # Port appears available
        echo $port
        break
    done
}

# Find available port
PORT=$(find_available_port $DEFAULT_PORT)

# Get local IP address for network access
LOCAL_IP=$(hostname -I | awk '{print $1}')
if [ -z "$LOCAL_IP" ]; then
    LOCAL_IP="localhost"
fi

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}ShareConnect Website - Development Server${NC}"
echo -e "${BLUE}========================================${NC}"
echo

# Check if Python is available
if ! command -v python3 &> /dev/null; then
    echo -e "${RED}Error: Python 3 is not installed${NC}"
    exit 1
fi

echo -e "${YELLOW}Starting development server...${NC}"
echo -e "${BLUE}Serving from:${NC} $PROJECT_ROOT"
echo -e "${BLUE}Port:${NC} $PORT"
echo -e "${BLUE}Local URL:${NC} http://localhost:$PORT"
echo -e "${BLUE}Network URL:${NC} http://$LOCAL_IP:$PORT"
echo
echo -e "${YELLOW}Press Ctrl+C to stop the server${NC}"
echo

# Start the development server
cd "$PROJECT_ROOT"
python3 -m http.server "$PORT"