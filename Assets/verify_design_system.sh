#!/bin/bash

# ShareConnect Design System Verification Script
# Verifies all design assets and documentation are properly created

echo "🔍 ShareConnect Design System Verification"
echo "=========================================="
echo ""

# Count files by type
echo "📊 File Statistics:"
echo "-------------------"

# Count SVG files
SVG_COUNT=$(find Assets/Logos -name "*.svg" 2>/dev/null | wc -l)
echo "SVG Templates: $SVG_COUNT"

# Count PNG files
PNG_COUNT=$(find Assets/Logos -name "*.png" 2>/dev/null | wc -l)
echo "PNG Placeholders: $PNG_COUNT"

# Count Lottie JSON files
LOTTIE_COUNT=$(find Assets/Logos/Lottie -name "*.json" 2>/dev/null | wc -l)
echo "Lottie Animations: $LOTTIE_COUNT"

# Count documentation files
DOC_COUNT=$(find Assets -name "*.md" -o -name "*.html" 2>/dev/null | wc -l)
echo "Documentation Files: $DOC_COUNT"

# Count script files
SCRIPT_COUNT=$(find Assets/Logos -name "*.sh" 2>/dev/null | wc -l)
echo "Automation Scripts: $SCRIPT_COUNT"

TOTAL_FILES=$((SVG_COUNT + PNG_COUNT + LOTTIE_COUNT + DOC_COUNT + SCRIPT_COUNT))
echo ""
echo "📈 Total Files Created: $TOTAL_FILES"
echo ""

# Verify application directories
echo "📱 Application Coverage:"
echo "------------------------"

APPS=(
    "ShareConnector" "TransmissionConnect" "uTorrentConnect" "qBitConnect" "JDownloaderConnect"
    "PlexConnect" "JellyfinConnect" "HomeAssistantConnect" "PortainerConnect" "NetdataConnect"
    "WireGuardConnect" "MatrixConnect" "GiteaConnect" "OnlyOfficeConnect" "MinecraftServerConnect"
    "SeafileConnect" "SyncthingConnect" "DuplicatiConnect" "PaperlessNGConnect" "MotrixConnect"
)

MISSING_APPS=0
for app in "${APPS[@]}"; do
    if [ -d "Assets/Logos/$app" ]; then
        echo "✅ $app"
    else
        echo "❌ $app (MISSING)"
        MISSING_APPS=$((MISSING_APPS + 1))
    fi
done

echo ""
echo "🔧 Script Verification:"
echo "-----------------------"

# Check if scripts are executable
SCRIPTS=(
    "Assets/Logos/generate_icons.sh"
    "Assets/Logos/create_advanced_templates.sh"
    "Assets/Logos/generate_lottie_templates.sh"
    "Assets/Logos/update_all_app_icons.sh"
    "Assets/Logos/update_lottie_animations.sh"
)

for script in "${SCRIPTS[@]}"; do
    if [ -f "$script" ]; then
        if [ -x "$script" ]; then
            echo "✅ $script (executable)"
        else
            echo "⚠️  $script (not executable)"
        fi
    else
        echo "❌ $script (MISSING)"
    fi
done

echo ""
echo "📚 Documentation Verification:"
echo "-------------------------------"

DOCS=(
    "Assets/DESIGN_REVIEW.md"
    "Assets/DESIGN_REVIEW.html"
    "Assets/DESIGN_REVIEW_SUMMARY.md"
    "Assets/Logos/README.md"
    "Assets/Logos/ICON_DESIGN_SPECIFICATION.md"
    "Assets/Logos/ICON_DESIGN_DETAILS.md"
    "Assets/Logos/IMPLEMENTATION_GUIDE.md"
    "Assets/Logos/VERIFICATION_SUMMARY.md"
    "Assets/Logos/LOTTIE_ANIMATION_SPECIFICATION.md"
)

for doc in "${DOCS[@]}"; do
    if [ -f "$doc" ]; then
        echo "✅ $doc"
    else
        echo "❌ $doc (MISSING)"
    fi
done

echo ""
echo "🎯 Verification Summary:"
echo "------------------------"

if [ $MISSING_APPS -eq 0 ]; then
    echo "✅ All 20 applications have icon directories"
else
    echo "❌ $MISSING_APPS applications missing directories"
fi

if [ $TOTAL_FILES -gt 100 ]; then
    echo "✅ Comprehensive file system created ($TOTAL_FILES files)"
else
    echo "⚠️  File count lower than expected ($TOTAL_FILES files)"
fi

echo ""
echo "🚀 Next Steps:"
echo "---------------"
echo "1. Open Assets/DESIGN_REVIEW.html for interactive review"
echo "2. Review Assets/DESIGN_REVIEW.md for detailed documentation"
echo "3. Use SVG templates to design final icons"
echo "4. Create Lottie animations using JSON templates"
echo "5. Run automation scripts to integrate into applications"
echo ""
echo "🎉 Design system ready for professional review!"