#!/bin/bash

# ShareConnect AI QA - Clean Emulator Setup Script
# This script creates a clean Android emulator environment for AI QA testing

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
EMULATOR_NAME="ShareConnect_QA_Emulator"
ANDROID_API_LEVEL=34
DEVICE_MODEL="pixel_6"
EMULATOR_PORT=5554
ADB_SERVER_PORT=5037
TIMEOUT=300

# App configurations for different test scenarios
declare -A APP_CONFIGS=(
    ["single_shareconnector"]="ShareConnector"
    ["single_transmission"]="TransmissionConnector"
    ["single_utorrent"]="uTorrentConnector"
    ["single_qbit"]="qBitConnector"
    ["dual_shareconnector_transmission"]="ShareConnector TransmissionConnector"
    ["dual_shareconnector_utorrent"]="ShareConnector uTorrentConnector"
    ["dual_shareconnector_qbit"]="ShareConnector qBitConnector"
    ["triple_shareconnector_transmission_utorrent"]="ShareConnector TransmissionConnector uTorrentConnector"
    ["all_apps"]="ShareConnector TransmissionConnector uTorrentConnector qBitConnector"
)

# Parse command line arguments
CLEAN_START=true
APP_SCENARIO="all_apps"
HEADLESS=false
GPU_MODE="swiftshader_indirect"

while [[ $# -gt 0 ]]; do
    case $1 in
        --no-clean)
            CLEAN_START=false
            shift
            ;;
        --scenario)
            APP_SCENARIO="$2"
            shift 2
            ;;
        --headless)
            HEADLESS=true
            shift
            ;;
        --gpu)
            GPU_MODE="$2"
            shift 2
            ;;
        --help)
            echo "ShareConnect AI QA - Clean Emulator Setup"
            echo ""
            echo "Usage: $0 [OPTIONS]"
            echo ""
            echo "Options:"
            echo "  --no-clean          Don't recreate emulator (reuse existing)"
            echo "  --scenario SCENARIO App installation scenario:"
            echo "                      single_shareconnector, single_transmission,"
            echo "                      single_utorrent, single_qbit,"
            echo "                      dual_*, triple_*, all_apps (default)"
            echo "  --headless          Run emulator in headless mode"
            echo "  --gpu MODE          GPU mode: swiftshader_indirect, host, off"
            echo "  --help              Show this help message"
            echo ""
            echo "Examples:"
            echo "  $0                                    # Clean start with all apps"
            echo "  $0 --no-clean --scenario single_shareconnector"
            echo "  $0 --headless --gpu host"
            exit 0
            ;;
        *)
            echo -e "${RED}Unknown option: $1${NC}"
            echo "Use --help for usage information"
            exit 1
            ;;
    esac
done

# Validate app scenario
if [[ ! ${APP_CONFIGS[$APP_SCENARIO]+_} ]]; then
    echo -e "${RED}Invalid app scenario: $APP_SCENARIO${NC}"
    echo "Valid scenarios: ${!APP_CONFIGS[*]}"
    exit 1
fi

echo -e "${BLUE}"
echo "╔══════════════════════════════════════════════════════════════╗"
echo "║         ShareConnect AI QA - Clean Emulator Setup           ║"
echo "╚══════════════════════════════════════════════════════════════╝"
echo -e "${NC}"

echo -e "${YELLOW}Configuration:${NC}"
echo -e "  Emulator Name: $EMULATOR_NAME"
echo -e "  API Level: $ANDROID_API_LEVEL"
echo -e "  Device: $DEVICE_MODEL"
echo -e "  App Scenario: $APP_SCENARIO"
echo -e "  Apps to install: ${APP_CONFIGS[$APP_SCENARIO]}"
echo -e "  Headless: $HEADLESS"
echo -e "  GPU Mode: $GPU_MODE"
echo ""

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Check prerequisites
echo -e "${YELLOW}[1/8] Checking prerequisites...${NC}"

# Check for required commands
for cmd in adb emulator avdmanager sdkmanager; do
    if ! command_exists "$cmd"; then
        echo -e "${RED}ERROR: $cmd command not found. Please install Android SDK.${NC}"
        exit 1
    fi
done

echo -e "${GREEN}✓ All prerequisites found${NC}"

# Kill any existing emulators and ADB server
echo -e "${YELLOW}[2/8] Cleaning up existing emulator processes...${NC}"
adb kill-server 2>/dev/null || true
pkill -f "emulator.*$EMULATOR_NAME" 2>/dev/null || true
pkill -f "qemu.*$EMULATOR_NAME" 2>/dev/null || true
sleep 2
echo -e "${GREEN}✓ Cleanup completed${NC}"

# Start ADB server
echo -e "${YELLOW}[3/8] Starting ADB server...${NC}"
timeout $TIMEOUT adb start-server
echo -e "${GREEN}✓ ADB server started${NC}"

# Create AVD if clean start or if it doesn't exist
if [[ "$CLEAN_START" == true ]] || ! avdmanager list avd | grep -q "$EMULATOR_NAME"; then
    echo -e "${YELLOW}[4/8] Creating clean Android Virtual Device...${NC}"

    # Remove existing AVD if it exists
    avdmanager delete avd -n "$EMULATOR_NAME" 2>/dev/null || true

    # Create new AVD
    echo "y" | avdmanager create avd -n "$EMULATOR_NAME" -k "system-images;android-$ANDROID_API_LEVEL;google_apis;x86_64" --device "$DEVICE_MODEL"

    if [[ $? -ne 0 ]]; then
        echo -e "${RED}ERROR: Failed to create AVD${NC}"
        exit 1
    fi

    echo -e "${GREEN}✓ AVD created successfully${NC}"
else
    echo -e "${GREEN}✓ Using existing AVD${NC}"
fi

# Start emulator
echo -e "${YELLOW}[5/8] Starting Android emulator...${NC}"

EMULATOR_ARGS="-avd $EMULATOR_NAME -port $EMULATOR_PORT -gpu $GPU_MODE"

if [[ "$HEADLESS" == true ]]; then
    EMULATOR_ARGS="$EMULATOR_ARGS -no-window -no-audio"
fi

# Start emulator in background
emulator $EMULATOR_ARGS &
EMULATOR_PID=$!

# Wait for emulator to boot
echo -e "${YELLOW}Waiting for emulator to boot (this may take several minutes)...${NC}"

BOOT_TIMEOUT=300
BOOT_START_TIME=$(date +%s)

while true; do
    if adb devices | grep -q "emulator-$EMULATOR_PORT"; then
        # Wait for device to be ready
        if timeout 30 adb -s "emulator-$EMULATOR_PORT" wait-for-device shell 'while [[ -z $(getprop sys.boot_completed) ]]; do sleep 1; done;'; then
            echo -e "${GREEN}✓ Emulator booted successfully${NC}"
            break
        fi
    fi

    CURRENT_TIME=$(date +%s)
    ELAPSED=$((CURRENT_TIME - BOOT_START_TIME))

    if [[ $ELAPSED -gt $BOOT_TIMEOUT ]]; then
        echo -e "${RED}ERROR: Emulator failed to boot within $BOOT_TIMEOUT seconds${NC}"
        kill $EMULATOR_PID 2>/dev/null || true
        exit 1
    fi

    echo -e "${YELLOW}Still waiting... (${ELAPSED}s elapsed)${NC}"
    sleep 10
done

# Set up device for testing
echo -e "${YELLOW}[6/8] Setting up device for testing...${NC}"

# Disable animations for faster testing
adb -s "emulator-$EMULATOR_PORT" shell settings put global window_animation_scale 0.0
adb -s "emulator-$EMULATOR_PORT" shell settings put global transition_animation_scale 0.0
adb -s "emulator-$EMULATOR_PORT" shell settings put global animator_duration_scale 0.0

# Disable system notifications during tests
adb -s "emulator-$EMULATOR_PORT" shell settings put global heads_up_notifications_enabled 0

# Set screen timeout to never
adb -s "emulator-$EMULATOR_PORT" shell settings put system screen_off_timeout 2147483647

# Wake up screen and unlock
adb -s "emulator-$EMULATOR_PORT" shell input keyevent 26  # Power button
adb -s "emulator-$EMULATOR_PORT" shell input keyevent 82  # Unlock

echo -e "${GREEN}✓ Device setup completed${NC}"

# Install required apps
echo -e "${YELLOW}[7/8] Installing apps for scenario: $APP_SCENARIO${NC}"

APPS_TO_INSTALL=(${APP_CONFIGS[$APP_SCENARIO]})

for app in "${APPS_TO_INSTALL[@]}"; do
    APK_PATH=""
    case $app in
        "ShareConnector")
            APK_PATH="ShareConnector/build/outputs/apk/debug/ShareConnector-debug.apk"
            ;;
        "TransmissionConnector")
            APK_PATH="TransmissionConnect/build/outputs/apk/debug/TransmissionConnect-debug.apk"
            ;;
        "uTorrentConnector")
            APK_PATH="uTorrentConnect/build/outputs/apk/debug/uTorrentConnect-debug.apk"
            ;;
        "qBitConnector")
            APK_PATH="qBitConnect/build/outputs/apk/debug/qBitConnect-debug.apk"
            ;;
    esac

    if [[ -f "$APK_PATH" ]]; then
        echo -e "${YELLOW}Installing $app...${NC}"
        if timeout 120 adb -s "emulator-$EMULATOR_PORT" install -r -g "$APK_PATH"; then
            echo -e "${GREEN}✓ $app installed successfully${NC}"
        else
            echo -e "${RED}✗ Failed to install $app${NC}"
            exit 1
        fi
    else
        echo -e "${RED}✗ APK not found: $APK_PATH${NC}"
        echo -e "${YELLOW}Building APK first...${NC}"

        # Try to build the APK
        case $app in
            "ShareConnector")
                ./gradlew :ShareConnector:assembleDebug
                ;;
            "TransmissionConnector")
                ./gradlew :TransmissionConnect:assembleDebug
                ;;
            "uTorrentConnector")
                ./gradlew :uTorrentConnect:assembleDebug
                ;;
            "qBitConnector")
                ./gradlew :qBitConnect:assembleDebug
                ;;
        esac

        if [[ -f "$APK_PATH" ]]; then
            echo -e "${YELLOW}Retrying installation of $app...${NC}"
            if timeout 120 adb -s "emulator-$EMULATOR_PORT" install -r -g "$APK_PATH"; then
                echo -e "${GREEN}✓ $app installed successfully${NC}"
            else
                echo -e "${RED}✗ Failed to install $app after build${NC}"
                exit 1
            fi
        else
            echo -e "${RED}✗ Failed to build APK for $app${NC}"
            exit 1
        fi
    fi
done

# Grant necessary permissions
echo -e "${YELLOW}Granting permissions...${NC}"
for app in "${APPS_TO_INSTALL[@]}"; do
    PACKAGE_NAME=""
    case $app in
        "ShareConnector")
            PACKAGE_NAME="com.shareconnect"
            ;;
        "TransmissionConnector")
            PACKAGE_NAME="com.transmissionconnect"
            ;;
        "uTorrentConnector")
            PACKAGE_NAME="com.utorrentconnect"
            ;;
        "qBitConnector")
            PACKAGE_NAME="com.qbitconnect"
            ;;
    esac

    if [[ -n "$PACKAGE_NAME" ]]; then
        adb -s "emulator-$EMULATOR_PORT" shell pm grant "$PACKAGE_NAME" android.permission.READ_EXTERNAL_STORAGE 2>/dev/null || true
        adb -s "emulator-$EMULATOR_PORT" shell pm grant "$PACKAGE_NAME" android.permission.WRITE_EXTERNAL_STORAGE 2>/dev/null || true
        adb -s "emulator-$EMULATOR_PORT" shell pm grant "$PACKAGE_NAME" android.permission.INTERNET 2>/dev/null || true
        adb -s "emulator-$EMULATOR_PORT" shell pm grant "$PACKAGE_NAME" android.permission.ACCESS_NETWORK_STATE 2>/dev/null || true
    fi
done

echo -e "${GREEN}✓ App installation completed${NC}"

# Final setup and verification
echo -e "${YELLOW}[8/8] Final setup and verification...${NC}"

# Clear app data to ensure clean state
for app in "${APPS_TO_INSTALL[@]}"; do
    PACKAGE_NAME=""
    case $app in
        "ShareConnector")
            PACKAGE_NAME="com.shareconnect"
            ;;
        "TransmissionConnector")
            PACKAGE_NAME="com.transmissionconnect"
            ;;
        "uTorrentConnector")
            PACKAGE_NAME="com.utorrentconnect"
            ;;
        "qBitConnector")
            PACKAGE_NAME="com.qbitconnect"
            ;;
    esac

    if [[ -n "$PACKAGE_NAME" ]]; then
        adb -s "emulator-$EMULATOR_PORT" shell pm clear "$PACKAGE_NAME" 2>/dev/null || true
    fi
done

# Verify all apps are installed
MISSING_APPS=()
for app in "${APPS_TO_INSTALL[@]}"; do
    PACKAGE_NAME=""
    case $app in
        "ShareConnector")
            PACKAGE_NAME="com.shareconnect"
            ;;
        "TransmissionConnector")
            PACKAGE_NAME="com.transmissionconnect"
            ;;
        "uTorrentConnector")
            PACKAGE_NAME="com.utorrentconnect"
            ;;
        "qBitConnector")
            PACKAGE_NAME="com.qbitconnect"
            ;;
    esac

    if [[ -n "$PACKAGE_NAME" ]]; then
        if ! adb -s "emulator-$EMULATOR_PORT" shell pm list packages | grep -q "$PACKAGE_NAME"; then
            MISSING_APPS+=("$app")
        fi
    fi
done

if [[ ${#MISSING_APPS[@]} -gt 0 ]]; then
    echo -e "${RED}ERROR: Some apps failed to install: ${MISSING_APPS[*]}${NC}"
    exit 1
fi

echo -e "${GREEN}✓ All apps verified successfully${NC}"

# Print summary
echo ""
echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}                    EMULATOR SETUP COMPLETE                    ${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo ""
echo -e "Emulator Device: emulator-$EMULATOR_PORT"
echo -e "Apps Installed: ${APP_CONFIGS[$APP_SCENARIO]}"
echo -e "Emulator PID: $EMULATOR_PID"
echo ""
echo -e "${GREEN}Ready for AI QA testing!${NC}"
echo ""
echo -e "To run tests:"
echo -e "  export ANDROID_SERIAL=emulator-$EMULATOR_PORT"
echo -e "  ./run_ai_qa_tests.sh --suite full_regression_suite"
echo ""
echo -e "To stop emulator:"
echo -e "  kill $EMULATOR_PID"
echo ""

# Export environment variables for calling scripts
export ANDROID_SERIAL="emulator-$EMULATOR_PORT"
export EMULATOR_PID="$EMULATOR_PID"
export APP_SCENARIO="$APP_SCENARIO"