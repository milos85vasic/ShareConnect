#!/bin/bash
# ShareConnect Website - Logs Script

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
CONTAINER_NAME="shareconnect-website"
FOLLOW="${1:-false}"

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}ShareConnect Website - Logs${NC}"
echo -e "${BLUE}========================================${NC}"
echo

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo -e "${RED}Error: Docker is not installed${NC}"
    exit 1
fi

# Check if container exists
if ! docker ps -a --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    echo -e "${RED}Error: Container does not exist${NC}"
    exit 1
fi

# Show logs
if [ "$FOLLOW" = "follow" ] || [ "$FOLLOW" = "-f" ]; then
    echo -e "${YELLOW}Following logs (Ctrl+C to exit)...${NC}"
    echo
    docker logs -f "$CONTAINER_NAME"
else
    echo -e "${YELLOW}Last 100 lines of logs:${NC}"
    echo
    docker logs --tail 100 "$CONTAINER_NAME"
    echo
    echo -e "${BLUE}Tip: Use './scripts/logs.sh follow' to follow logs in real-time${NC}"
fi
