#!/bin/bash

# MIT License Header for source files
LICENSE_HEADER_KT_JAVA="/*
 * Copyright (c) 2025 MeTube Share
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the \"Software\"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
"

LICENSE_HEADER_XML="<!--
    Copyright (c) 2025 MeTube Share

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the \"Software\"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
-->
"

# Function to add license header to a file if it doesn't already have one
add_license_to_file() {
    local file="$1"
    local header="$2"

    # Check if file already has a license header
    if head -20 "$file" | grep -q "Copyright.*MeTube Share\|Licensed under"; then
        echo "Skipping $file - already has license header"
        return
    fi

    # Create temporary file
    local temp_file="${file}.tmp"

    # Add header and original content
    echo "$header" > "$temp_file"
    echo "" >> "$temp_file"
    cat "$file" >> "$temp_file"

    # Replace original file
    mv "$temp_file" "$file"
    echo "Added license header to $file"
}

# Find all relevant source files
echo "Adding MIT license headers to source files..."

# Kotlin files
find . -name "*.kt" -type f | while read -r file; do
    # Skip build directories and generated files
    if [[ "$file" == *"/build/"* ]] || [[ "$file" == *"/.gradle/"* ]] || [[ "$file" == *"/.idea/"* ]]; then
        continue
    fi
    add_license_to_file "$file" "$LICENSE_HEADER_KT_JAVA"
done

# Java files
find . -name "*.java" -type f | while read -r file; do
    # Skip build directories and generated files
    if [[ "$file" == *"/build/"* ]] || [[ "$file" == *"/.gradle/"* ]] || [[ "$file" == *"/.idea/"* ]]; then
        continue
    fi
    add_license_to_file "$file" "$LICENSE_HEADER_KT_JAVA"
done

# XML files (only layout and values files, not manifests which have different structure)
find . -name "*.xml" -type f | while read -r file; do
    # Skip build directories, manifests, and generated files
    if [[ "$file" == *"/build/"* ]] || [[ "$file" == *"/.gradle/"* ]] || [[ "$file" == *"/AndroidManifest.xml" ]] || [[ "$file" == *"/.idea/"* ]]; then
        continue
    fi
    add_license_to_file "$file" "$LICENSE_HEADER_XML"
done

echo "License header addition complete!"