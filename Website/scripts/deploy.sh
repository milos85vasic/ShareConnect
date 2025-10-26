#!/bin/bash
# ShareConnect Website - Deploy Script

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

# Configuration
CONTAINER_NAME="shareconnect-website"
IMAGE_NAME="shareconnect-website"
DEFAULT_PORT="${PORT:-8080}"

# Function to find first available port
find_available_port() {
    local port=$1
    while lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; do
        port=$((port + 1))
    done
    echo $port
}

# Find available port
PORT=$(find_available_port $DEFAULT_PORT)

# Get local IP address for network access
LOCAL_IP=$(hostname -I | awk '{print $1}')
if [ -z "$LOCAL_IP" ]; then
    LOCAL_IP="localhost"
fi

echo -e "${CYAN}========================================${NC}"
echo -e "${CYAN}ShareConnect Website - Deploy Script${NC}"
echo -e "${CYAN}========================================${NC}"
echo

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo -e "${RED}Error: Docker is not installed${NC}"
    exit 1
fi

# Step 1: Stop existing container
echo -e "${BLUE}[1/5]${NC} ${YELLOW}Stopping existing container...${NC}"
if docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    docker stop "$CONTAINER_NAME"
    echo -e "${GREEN}✓ Container stopped${NC}"
else
    echo -e "${YELLOW}No running container found${NC}"
fi

# Step 2: Remove existing container
echo
echo -e "${BLUE}[2/5]${NC} ${YELLOW}Removing existing container...${NC}"
if docker ps -a --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    docker rm "$CONTAINER_NAME"
    echo -e "${GREEN}✓ Container removed${NC}"
else
    echo -e "${YELLOW}No container to remove${NC}"
fi

# Step 3: Build new image
echo
echo -e "${BLUE}[3/5]${NC} ${YELLOW}Building new image...${NC}"
cd "$PROJECT_ROOT"

if docker build -f docker/Dockerfile -t "$IMAGE_NAME:latest" .; then
    echo -e "${GREEN}✓ Image built successfully${NC}"
else
    echo -e "${RED}✗ Build failed${NC}"
    exit 1
fi

# Step 4: Start new container
echo
echo -e "${BLUE}[4/5]${NC} ${YELLOW}Starting new container...${NC}"
docker run -d \
    --name "$CONTAINER_NAME" \
    --restart unless-stopped \
    -p "$PORT:80" \
    "$IMAGE_NAME:latest"

echo -e "${GREEN}✓ Container started${NC}"

# Step 5: Health check
echo
echo -e "${BLUE}[5/5]${NC} ${YELLOW}Performing health check...${NC}"
sleep 5

if docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    # Check if the website is accessible
    if curl -f http://localhost:$PORT/health > /dev/null 2>&1; then
        echo -e "${GREEN}✓ Health check passed${NC}"
    else
        echo -e "${YELLOW}⚠ Container is running but health check failed${NC}"
    fi
else
    echo -e "${RED}✗ Container is not running${NC}"
    echo -e "${YELLOW}Check logs with: docker logs $CONTAINER_NAME${NC}"
    exit 1
fi

# Clean up old images
echo
echo -e "${YELLOW}Cleaning up old images...${NC}"
docker image prune -f > /dev/null 2>&1
echo -e "${GREEN}✓ Cleanup complete${NC}"

# Summary
echo
echo -e "${CYAN}========================================${NC}"
echo -e "${GREEN}✓ Deployment completed successfully!${NC}"
echo -e "${CYAN}========================================${NC}"
echo
echo -e "${BLUE}Container:${NC} $CONTAINER_NAME"
echo -e "${BLUE}Port:${NC} $PORT"
echo -e "${BLUE}Local URL:${NC} http://localhost:$PORT"
echo -e "${BLUE}Network URL:${NC} http://$LOCAL_IP:$PORT"
echo -e "${BLUE}Status:${NC} $(docker ps --filter "name=$CONTAINER_NAME" --format "{{.Status}}")"
echo
echo -e "${YELLOW}Useful commands:${NC}"
echo -e "  View logs:  ./scripts/logs.sh"
echo -e "  Stop:       ./scripts/stop.sh"
echo -e "  Restart:    ./scripts/restart.sh"
echo
