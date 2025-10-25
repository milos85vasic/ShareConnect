#!/bin/bash
# ShareConnect Website - Build Script

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
IMAGE_NAME="shareconnect-website"
IMAGE_TAG="${1:-latest}"
DOCKERFILE="$PROJECT_ROOT/docker/Dockerfile"

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}ShareConnect Website - Build Script${NC}"
echo -e "${BLUE}========================================${NC}"
echo

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo -e "${RED}Error: Docker is not installed${NC}"
    exit 1
fi

# Check if Dockerfile exists
if [ ! -f "$DOCKERFILE" ]; then
    echo -e "${RED}Error: Dockerfile not found at $DOCKERFILE${NC}"
    exit 1
fi

echo -e "${YELLOW}Building Docker image...${NC}"
echo -e "Image name: ${GREEN}$IMAGE_NAME:$IMAGE_TAG${NC}"
echo

# Build the Docker image
cd "$PROJECT_ROOT"

if docker build -f "$DOCKERFILE" -t "$IMAGE_NAME:$IMAGE_TAG" .; then
    echo
    echo -e "${GREEN}✓ Build successful!${NC}"
    echo

    # Show image info
    echo -e "${BLUE}Image details:${NC}"
    docker images "$IMAGE_NAME:$IMAGE_TAG" --format "table {{.Repository}}\t{{.Tag}}\t{{.Size}}\t{{.CreatedAt}}"
    echo

    # Tag as latest if not already
    if [ "$IMAGE_TAG" != "latest" ]; then
        docker tag "$IMAGE_NAME:$IMAGE_TAG" "$IMAGE_NAME:latest"
        echo -e "${GREEN}✓ Tagged as latest${NC}"
    fi

    echo
    echo -e "${GREEN}Build completed successfully!${NC}"
    echo -e "${YELLOW}Run './scripts/start.sh' to start the website${NC}"
else
    echo
    echo -e "${RED}✗ Build failed${NC}"
    exit 1
fi
