#!/bin/bash

# Simple markdown to HTML converter
# Converts basic markdown elements to HTML

convert_md_to_html() {
    local input_file="$1"
    local output_file="$2"

    echo "<!DOCTYPE html>
<html lang=\"en\">
<head>
    <meta charset=\"UTF-8\">
    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">
    <title>$(basename "$input_file" .md)</title>
    <style>
        body { font-family: Arial, sans-serif; line-height: 1.6; margin: 40px; }
        h1, h2, h3, h4, h5, h6 { color: #333; }
        code { background-color: #f4f4f4; padding: 2px 4px; border-radius: 4px; }
        pre { background-color: #f4f4f4; padding: 10px; border-radius: 4px; overflow-x: auto; }
        ul, ol { margin-left: 20px; }
        a { color: #0066cc; text-decoration: none; }
        a:hover { text-decoration: underline; }
    </style>
</head>
<body>" > "$output_file"

    sed -e 's/^# \(.*\)$/<h1>\1<\/h1>/' \
        -e 's/^## \(.*\)$/<h2>\1<\/h2>/' \
        -e 's/^### \(.*\)$/<h3>\1<\/h3>/' \
        -e 's/^#### \(.*\)$/<h4>\1<\/h4>/' \
        -e 's/^##### \(.*\)$/<h5>\1<\/h5>/' \
        -e 's/^###### \(.*\)$/<h6>\1<\/h6>/' \
        -e 's/^\(.*\)$/<p>\1<\/p>/' \
        -e 's/^\- \(.*\)$/<li>\1<\/li>/' \
        -e 's/^[0-9]\+\. \(.*\)$/<li>\1<\/li>/' \
        -e 's/\*\*\(.*\)\*\*/<strong>\1<\/strong>/g' \
        -e 's/\*\(.*\)\*/<em>\1<\/em>/g' \
        -e 's/`\(.*\)`/<code>\1<\/code>/g' \
        -e 's/\[.*\](.*)/<a href=\"\1\">\0<\/a>/g' \
        "$input_file" >> "$output_file"

    echo "</body></html>" >> "$output_file"
}

# Find all .md files in Documentation/ and convert
find Documentation/ -name "*.md" -type f | while read -r md_file; do
    html_file="${md_file%.md}.html"
    convert_md_to_html "$md_file" "$html_file"
    echo "Converted $md_file to $html_file"
done