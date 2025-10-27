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
        # Check if Docker has this port allocated
        if docker ps -a --format '{{.Ports}}' | grep -q "0.0.0.0:$port->"; then
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
        # Get the current port mapping
        CURRENT_PORT=$(docker port "$CONTAINER_NAME" 80 | cut -d: -f2)
        if [ "$CURRENT_PORT" = "$PORT" ]; then
            echo -e "${GREEN}Container is already running on port $PORT${NC}"
            echo -e "Port: ${BLUE}$PORT${NC}"
            echo -e "Local URL: ${BLUE}http://localhost:$PORT${NC}"
            echo -e "Network URL: ${BLUE}http://$LOCAL_IP:$PORT${NC}"
            exit 0
        else
            echo -e "${YELLOW}Container is running on different port ($CURRENT_PORT), removing and recreating...${NC}"
            docker stop "$CONTAINER_NAME"
            docker rm "$CONTAINER_NAME"
        fi
    else
        echo -e "${YELLOW}Removing stopped container to recreate with new port...${NC}"
        docker rm "$CONTAINER_NAME"
    fi
 else
     # Always build latest image to ensure up-to-date codebase
     echo -e "${YELLOW}Building latest image...${NC}"
     "$SCRIPT_DIR/build.sh"

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
    echo -e "${BLUE}Port:${NC} $PORT"
    echo -e "${BLUE}Local URL:${NC} http://localhost:$PORT"
    echo -e "${BLUE}Network URL:${NC} http://$LOCAL_IP:$PORT"
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
