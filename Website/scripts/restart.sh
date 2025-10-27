#!/bin/bash
# ShareConnect Website - Restart Script

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Configuration
CONTAINER_NAME="shareconnect-website"

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}ShareConnect Website - Restart Script${NC}"
echo -e "${BLUE}========================================${NC}"
echo

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo -e "${RED}Error: Docker is not installed${NC}"
    exit 1
fi

# Check if container exists
if docker ps -a --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    echo -e "${YELLOW}Stopping and removing existing container...${NC}"
    docker stop "$CONTAINER_NAME" 2>/dev/null || true
    docker rm "$CONTAINER_NAME" 2>/dev/null || true
    
    echo -e "${YELLOW}Building latest image...${NC}"
    "$SCRIPT_DIR/build.sh"
    
    echo -e "${YELLOW}Starting fresh container with latest version...${NC}"
    "$SCRIPT_DIR/start.sh"

    # Wait for container to be healthy
    sleep 3

    if docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
        echo
        echo -e "${GREEN}✓ Website restarted successfully!${NC}"

        # Get the port
        PORT=$(docker port "$CONTAINER_NAME" 80 | cut -d: -f2)
        echo -e "${BLUE}URL:${NC} http://localhost:$PORT"
    else
        echo
        echo -e "${RED}✗ Failed to restart website${NC}"
        exit 1
    fi
else
    echo -e "${YELLOW}Container does not exist. Starting fresh...${NC}"
    "$SCRIPT_DIR/start.sh"
fi
