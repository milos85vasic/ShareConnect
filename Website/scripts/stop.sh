#!/bin/bash
# ShareConnect Website - Stop Script

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
CONTAINER_NAME="shareconnect-website"

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}ShareConnect Website - Stop Script${NC}"
echo -e "${BLUE}========================================${NC}"
echo

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo -e "${RED}Error: Docker is not installed${NC}"
    exit 1
fi

# Check if container exists
if ! docker ps -a --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    echo -e "${YELLOW}Container does not exist${NC}"
    exit 0
fi

# Check if container is running
if docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    echo -e "${YELLOW}Stopping container...${NC}"
    docker stop "$CONTAINER_NAME"
    echo -e "${GREEN}✓ Container stopped${NC}"
else
    echo -e "${YELLOW}Container is already stopped${NC}"
fi

# Remove the container
docker rm "$CONTAINER_NAME"
echo -e "${GREEN}✓ Container removed${NC}"

echo
echo -e "${GREEN}Done!${NC}"
