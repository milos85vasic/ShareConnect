#!/bin/bash

# ShareConnect Website Deployment Script
# This script prepares and deploys the website to GitHub Pages

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
WEBSITE_DIR="Website"
DEPLOY_BRANCH="gh-pages"
MAIN_BRANCH="main"

# Function to log messages
log() {
    echo -e "${BLUE}[$(date +'%H:%M:%S')]${NC} $1"
}

# Function to log errors
error() {
    echo -e "${RED}[$(date +'%H:%M:%S')] ERROR:${NC} $1" >&2
}

# Function to log warnings
warning() {
    echo -e "${YELLOW}[$(date +'%H:%M:%S')] WARNING:${NC} $1"
}

# Function to check prerequisites
check_prerequisites() {
    log "Checking prerequisites..."
    
    # Check if we're in a git repository
    if ! git rev-parse --git-dir > /dev/null 2>&1; then
        error "Not in a git repository"
        return 1
    fi
    
    # Check if website directory exists
    if [ ! -d "$WEBSITE_DIR" ]; then
        error "Website directory '$WEBSITE_DIR' not found"
        return 1
    fi
    
    log "✓ All prerequisites met"
    return 0
}

# Function to build website
build_website() {
    log "Building website..."
    
    # Create build directory
    BUILD_DIR="build"
    rm -rf "$BUILD_DIR"
    mkdir -p "$BUILD_DIR"
    
    # Copy website files
    cp -r "$WEBSITE_DIR"/* "$BUILD_DIR/"
    
    # Validate HTML
    if command -v tidy >/dev/null 2>&1; then
        log "Validating HTML..."
        tidy -q -errors "$BUILD_DIR/index.html" 2>/dev/null || warning "HTML validation completed with warnings"
    else
        warning "HTML tidy not found, skipping validation"
    fi
    
    log "✓ Website built successfully"
}

# Function to deploy to GitHub Pages
deploy_to_gh_pages() {
    log "Deploying to GitHub Pages..."
    
    # Check if we're on the correct branch
    CURRENT_BRANCH=$(git branch --show-current)
    if [ "$CURRENT_BRANCH" != "$MAIN_BRANCH" ]; then
        warning "Not on $MAIN_BRANCH branch (currently on $CURRENT_BRANCH)"
        read -p "Continue anyway? (y/N) " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            log "Deployment cancelled"
            exit 0
        fi
    fi
    
    # Create or switch to gh-pages branch
    if git show-ref --verify --quiet "refs/heads/$DEPLOY_BRANCH"; then
        git checkout "$DEPLOY_BRANCH"
    else
        git checkout --orphan "$DEPLOY_BRANCH"
    fi
    
    # Remove existing files except .git
    find . -maxdepth 1 ! -name '.git' ! -name '.' ! -name '..' -exec rm -rf {} +
    
    # Copy built website
    cp -r build/* .
    
    # Add and commit
    git add .
    git commit -m "Deploy website $(date +'%Y-%m-%d %H:%M:%S')"
    
    # Push to GitHub
    git push origin "$DEPLOY_BRANCH" --force
    
    # Switch back to main branch
    git checkout "$MAIN_BRANCH"
    
    log "✓ Website deployed to GitHub Pages"
    log "URL: https://$(git config --get remote.origin.url | sed 's/.*github.com\/\([^/]*\/.*\)\.git/\1/')"
}

# Function to test website locally
test_website() {
    log "Testing website locally..."
    
    # Check if website loads correctly
    if command -v python3 >/dev/null 2>&1; then
        cd "build" && python3 -m http.server 8000 >/dev/null 2>&1 &
        SERVER_PID=$!
        sleep 2
        
        if curl -s "http://localhost:8000" >/dev/null 2>&1; then
            log "✓ Website loads correctly"
        else
            error "Website failed to load locally"
        fi
        
        kill $SERVER_PID 2>/dev/null
        cd ..
    else
        warning "Python not found, skipping local test"
    fi
}

# Function to show deployment status
show_status() {
    log "Deployment Status"
    echo "================"
    echo "Website Directory: $WEBSITE_DIR"
    echo "Build Directory: build"
    echo "Deploy Branch: $DEPLOY_BRANCH"
    echo "Main Branch: $MAIN_BRANCH"
    echo ""
}

# Main deployment function
main() {
    local skip_test=false
    local force_deploy=false
    
    # Parse command line arguments
    while [[ $# -gt 0 ]]; do
        case $1 in
            --skip-test)
                skip_test=true
                shift
                ;;
            --force)
                force_deploy=true
                shift
                ;;
            --help)
                echo "ShareConnect Website Deployment Script"
                echo ""
                echo "Usage: $0 [OPTIONS]"
                echo ""
                echo "Options:"
                echo "  --skip-test    Skip local testing"
                echo "  --force        Force deployment without confirmation"
                echo "  --help         Show this help message"
                echo ""
                exit 0
                ;;
            *)
                error "Unknown option: $1"
                echo "Use --help for usage information"
                exit 1
                ;;
        esac
    done
    
    # Show banner
    echo -e "${BLUE}"
    echo "╔═══════════════════════════════════════════════════╗"
    echo "║         ShareConnect Website Deployment          ║"
    echo "╚═══════════════════════════════════════════════════╝"
    echo -e "${NC}"
    
    # Check prerequisites
    if ! check_prerequisites; then
        exit 1
    fi
    
    # Show status
    show_status
    
    # Confirm deployment
    if [ "$force_deploy" = false ]; then
        read -p "Proceed with deployment? (y/N) " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            log "Deployment cancelled"
            exit 0
        fi
    fi
    
    # Build website
    build_website
    
    # Test website
    if [ "$skip_test" = false ]; then
        test_website
    fi
    
    # Deploy to GitHub Pages
    deploy_to_gh_pages
    
    # Cleanup
    rm -rf build
    
    echo ""
    echo -e "${GREEN}╔═══════════════════════════════════════════════════╗${NC}"
    echo -e "${GREEN}║              DEPLOYMENT SUCCESSFUL!               ║${NC}"
    echo -e "${GREEN}╚═══════════════════════════════════════════════════╝${NC}"
    echo ""
    echo "Your website is now live on GitHub Pages!"
    echo "It may take a few minutes for changes to propagate."
    echo ""
}

# Run main function with all arguments
main "$@"