#!/bin/bash

# Setup production signing for ShareConnect connectors
# This script creates the necessary keystore and configures signing

set -e

echo "🔐 Setting up Production Signing for ShareConnect"
echo "================================================"

KEYSTORE_DIR="signing"
KEYSTORE_FILE="$KEYSTORE_DIR/shareconnect_keystore.jks"
KEY_ALIAS="shareconnect_key"

# Create signing directory
mkdir -p "$KEYSTORE_DIR"

if [ ! -f "$KEYSTORE_FILE" ]; then
    echo "📝 Creating new keystore..."

    # Generate keystore
    keytool -genkeypair \
        -v \
        -keystore "$KEYSTORE_FILE" \
        -keyalg RSA \
        -keysize 2048 \
        -validity 10000 \
        -alias "$KEY_ALIAS" \
        -dname "CN=ShareConnect, OU=Development, O=ShareConnect, L=Internet, ST=Web, C=US" \
        -storepass "change_this_password" \
        -keypass "change_this_password"

    echo "✅ Keystore created: $KEYSTORE_FILE"
    echo ""
    echo "⚠️  IMPORTANT SECURITY STEPS:"
    echo "   1. Change the default passwords in signing.properties"
    echo "   2. Backup the keystore file securely"
    echo "   3. Never commit keystore or passwords to version control"
    echo "   4. Use environment variables for CI/CD pipelines"
    echo ""
    echo "🔧 Update signing.properties with your secure passwords"
else
    echo "✅ Keystore already exists: $KEYSTORE_FILE"
fi

echo ""
echo "📋 Next Steps:"
echo "   1. Update passwords in signing.properties"
echo "   2. Configure CI/CD with signing secrets"
echo "   3. Test signed builds: ./gradlew assembleRelease"
