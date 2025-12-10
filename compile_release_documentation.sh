#!/bin/bash

# Final Documentation Compilation Script

# Output Directory
OUTPUT_DIR="./release_documentation"

# Ensure output directory exists
mkdir -p "$OUTPUT_DIR"

# Documentation Files to Compile
DOCS=(
    "RELEASE_NOTES.md"
    "DEPLOYMENT_STRATEGY.md"
    "COMPLIANCE_CERTIFICATION.md"
    "IMPLEMENTATION_SUMMARY.md"
    "SECURITY_ANALYSIS.md"
    "PERFORMANCE_SECURITY_TESTING.md"
    "MATRIX_ENCRYPTION_GUIDE.md"
)

# Compilation Function
compile_documentation() {
    local output_file="$OUTPUT_DIR/ShareConnect_Release_Documentation_v1.0.0.md"
    
    # Start with a title page
    echo "# ShareConnect Release Documentation" > "$output_file"
    echo "## Version 1.0.0" >> "$output_file"
    echo "**Generated:** $(date)" >> "$output_file"
    echo "" >> "$output_file"
    
    # Compile each document
    for doc in "${DOCS[@]}"; do
        if [ -f "$doc" ]; then
            echo "## $(basename "$doc" .md)" >> "$output_file"
            echo "" >> "$output_file"
            cat "$doc" >> "$output_file"
            echo "" >> "$output_file"
            echo "---" >> "$output_file"
            echo "" >> "$output_file"
        fi
    done
    
    # Generate PDF (requires pandoc)
    pandoc "$output_file" -o "$OUTPUT_DIR/ShareConnect_Release_Documentation_v1.0.0.pdf"
}

# Run compilation
compile_documentation

# Verify compilation
if [ $? -eq 0 ]; then
    echo "✅ Documentation compilation successful"
    echo "📄 Documents compiled in $OUTPUT_DIR"
    exit 0
else
    echo "❌ Documentation compilation failed"
    exit 1
fi