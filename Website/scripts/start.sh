#!/bin/bash
# ShareConnect Website - Start Script

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
CONTAINER_NAME="shareconnect-website"
IMAGE_NAME="shareconnect-website"
PORT="${1:-8080}"

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}ShareConnect Website - Start Script${NC}"
echo -e "${BLUE}========================================${NC}"
echo

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo -e "${RED}Error: Docker is not installed${NC}"
    exit 1
fi

# Check if container is already running
if docker ps -a --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    echo -e "${YELLOW}Container already exists${NC}"

    if docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
        echo -e "${GREEN}Container is already running${NC}"
        echo -e "URL: ${BLUE}http://localhost:$PORT${NC}"
        exit 0
    else
        echo -e "${YELLOW}Starting existing container...${NC}"
        docker start "$CONTAINER_NAME"
    fi
else
    # Check if image exists
    if ! docker images --format '{{.Repository}}' | grep -q "^${IMAGE_NAME}$"; then
        echo -e "${YELLOW}Image not found. Building...${NC}"
        "$SCRIPT_DIR/build.sh"
    fi

    echo -e "${YELLOW}Starting new container...${NC}"
    docker run -d \
        --name "$CONTAINER_NAME" \
        --restart unless-stopped \
        -p "$PORT:80" \
        "$IMAGE_NAME:latest"
fi

# Wait for container to be healthy
echo -e "${YELLOW}Waiting for container to be healthy...${NC}"
sleep 3

if docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    echo
    echo -e "${GREEN}✓ Website started successfully!${NC}"
    echo
    echo -e "${BLUE}Container:${NC} $CONTAINER_NAME"
    echo -e "${BLUE}URL:${NC} http://localhost:$PORT"
    echo
    echo -e "${YELLOW}Useful commands:${NC}"
    echo -e "  View logs:    ./scripts/logs.sh"
    echo -e "  Stop website: ./scripts/stop.sh"
    echo -e "  Restart:      ./scripts/restart.sh"
else
    echo
    echo -e "${RED}✗ Failed to start website${NC}"
    echo -e "${YELLOW}Check logs with: docker logs $CONTAINER_NAME${NC}"
    exit 1
fi
