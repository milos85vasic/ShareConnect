#!/usr/bin/env python3
"""
Script to update all ShareConnect connector pages with consumer-focused messaging and blue theme
"""

import os
import re

# Consumer-focused messaging templates
CONSUMER_MESSAGES = {
    # Hero section templates
    "hero_title": {
        "pattern": r'<h1 class="hero-title">\s*([^<]+)\s*<span class="gradient-text">([^<]+)</span>\s*</h1>',
        "replacement": '<h1 class="hero-title">\n                    \1\n                    <span class="gradient-text">\2</span>\n                </h1>'
    },
    "hero_subtitle": {
        "pattern": r'<p class="hero-subtitle">\s*([^<]+)\s*</p>',
        "replacement": '<p class="hero-subtitle">\n                    \1\n                </p>'
    },
    # Feature section templates
    "feature_section": {
        "pattern": r'<h2>([^<]+)</h2>\s*<p>([^<]+)</p>',
        "replacement": '<h2>\1</h2>\n                <p>\2</p>'
    }
}

# Consumer-focused content updates by connector type
CONNECTOR_UPDATES = {
    "torrent": {
        "hero_subtitle": "Send torrent links directly to your torrent client. Start downloads instantly with one tap - no more copying URLs or switching apps.",
        "feature_1": "Instant torrent downloads from any app",
        "feature_2": "Full queue management and organization",
        "feature_3": "Works with your existing torrent client"
    },
    "media": {
        "hero_subtitle": "Add content to your media server instantly. Share streaming links and build your media collection with one tap.",
        "feature_1": "Add content to your streaming library instantly",
        "feature_2": "Automatic metadata and organization",
        "feature_3": "Works with your media server setup"
    },
    "download": {
        "hero_subtitle": "Download from 1800+ sites automatically. Premium account support, batch downloads, and smart file organization.",
        "feature_1": "Download from 1800+ sites with one tap",
        "feature_2": "Premium account and CAPTCHA support",
        "feature_3": "Batch downloads and automatic organization"
    },
    "cloud": {
        "hero_subtitle": "Save files directly to your cloud storage. Encrypted sync, automatic backup, and access from anywhere.",
        "feature_1": "Save files to cloud storage instantly",
        "feature_2": "Encrypted sync and automatic backup",
        "feature_3": "Access your files from anywhere"
    },
    "specialized": {
        "hero_subtitle": "Connect to specialized tools for messaging, file sync, documents, and more. One tap access to all your services.",
        "feature_1": "Connect to messaging and sync tools",
        "feature_2": "Document management and organization",
        "feature_3": "One tap access to all your services"
    }
}

def update_connector_file(filepath, connector_type):
    """Update a single connector file with consumer-focused messaging"""
    
    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # Update hero subtitle
        updates = CONNECTOR_UPDATES.get(connector_type, CONNECTOR_UPDATES["specialized"])
        
        # Update hero subtitle
        old_subtitle_pattern = r'<p class="hero-subtitle">[^<]*</p>'
        new_subtitle = f'<p class="hero-subtitle">\n                    {updates["hero_subtitle"]}\n                </p>'
        content = re.sub(old_subtitle_pattern, new_subtitle, content)
        
        # Update GitHub repository link
        content = content.replace('https://github.com/yourusername/ShareConnect', 'https://github.com/shareconnect')
        
        # Update logo path to use transparent version
        content = content.replace('assets/logo.jpeg', 'assets/logo_transparent.png')
        
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
        
        print(f"✓ Updated: {os.path.basename(filepath)}")
        return True
        
    except Exception as e:
        print(f"✗ Failed to update {filepath}: {e}")
        return False

def main():
    """Main function to update all connector files"""
    
    # Define connector files and their types
    connectors = {
        "qbitconnect.html": "torrent",
        "transmissionconnect.html": "torrent",
        "utorrentconnect.html": "torrent",
        "plexconnect.html": "media",
        "jellyfinconnect.html": "media",
        "embyconnect.html": "media",
        "jdownloaderconnect.html": "download",
        "ytdlpconnect.html": "download",
        "metubeconnect.html": "download",
        "nextcloudconnect.html": "cloud",
        "seafileconnect.html": "cloud",
        "filebrowserconnect.html": "cloud",
        "syncthingconnect.html": "specialized",
        "matrixconnect.html": "specialized",
        "paperlessngconnect.html": "specialized",
        "duplicaticonnect.html": "specialized",
        "wireguardconnect.html": "specialized",
        "minecraftserverconnect.html": "specialized",
        "onlyofficeconnect.html": "specialized",
        "shareconnector.html": "specialized"
    }
    
    updated_count = 0
    total_count = len(connectors)
    
    print(f"Updating {total_count} connector files...")
    
    for filename, connector_type in connectors.items():
        filepath = os.path.join(".", filename)
        if os.path.exists(filepath):
            if update_connector_file(filepath, connector_type):
                updated_count += 1
        else:
            print(f"✗ File not found: {filename}")
    
    print(f"\n✅ Successfully updated {updated_count}/{total_count} connector files")

if __name__ == "__main__":
    main()