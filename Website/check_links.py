#!/usr/bin/env python3
"""
Script to check for broken links in the ShareConnect website
"""

import os
import re
from urllib.parse import urljoin, urlparse

# Track all found links
all_links = set()
broken_links = []

# Files to check
HTML_FILES = [
    "index.html",
    "products.html", 
    "manuals.html",
    "qbitconnect.html",
    "transmissionconnect.html",
    "plexconnect.html",
    "jellyfinconnect.html",
    "embyconnect.html",
    "jdownloaderconnect.html",
    "ytdlpconnect.html",
    "metubeconnect.html",
    "nextcloudconnect.html",
    "seafileconnect.html",
    "filebrowserconnect.html",
    "syncthingconnect.html",
    "matrixconnect.html",
    "paperlessngconnect.html",
    "duplicaticonnect.html",
    "wireguardconnect.html",
    "minecraftserverconnect.html",
    "onlyofficeconnect.html",
    "shareconnector.html"
]

def extract_links(html_content, base_file):
    """Extract all links from HTML content"""
    links = []
    
    # Find all href attributes
    href_pattern = r'href="([^"]+)"'
    matches = re.findall(href_pattern, html_content)
    
    for link in matches:
        # Skip external links and anchors
        if link.startswith('http') or link.startswith('#') or link.startswith('mailto:'):
            continue
        links.append((link, base_file))
    
    return links

def check_link_exists(link_path):
    """Check if a local file exists"""
    if link_path.startswith('/'):
        link_path = link_path[1:]
    
    # Check if file exists
    if os.path.exists(link_path):
        return True
    
    # Check if it's a directory that should have index.html
    if os.path.isdir(link_path):
        return os.path.exists(os.path.join(link_path, 'index.html'))
    
    return False

def main():
    """Main function to check all links"""
    
    print("🔍 Checking for broken links...\n")
    
    # Collect all links
    for html_file in HTML_FILES:
        if not os.path.exists(html_file):
            print(f"⚠️  File not found: {html_file}")
            continue
            
        with open(html_file, 'r', encoding='utf-8') as f:
            content = f.read()
        
        links = extract_links(content, html_file)
        all_links.update(links)
    
    # Check each link
    for link, source_file in all_links:
        if not check_link_exists(link):
            broken_links.append((link, source_file))
            print(f"❌ Broken link: {link} (in {source_file})")
        else:
            print(f"✅ Valid link: {link} (in {source_file})")
    
    # Summary
    print(f"\n📊 Link Check Summary:")
    print(f"   Total links checked: {len(all_links)}")
    print(f"   Broken links found: {len(broken_links)}")
    
    if broken_links:
        print(f"\n🚨 Broken links need fixing:")
        for link, source in broken_links:
            print(f"   - {link} (in {source})")
    else:
        print("\n🎉 All links are valid!")

if __name__ == "__main__":
    main()