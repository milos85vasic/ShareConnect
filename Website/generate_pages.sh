#!/bin/bash

# ShareConnect Application Page Generator
# Generates individual product pages for all ShareConnect applications

# Application definitions
declare -A apps=(
    # Phase 1 - Core Applications (already created)
    # ["shareconnector"]="ShareConnector|Universal Media Sharing|Main Application"
    # ["qbitconnect"]="qBitConnect|qBittorrent Integration|Torrent Client"
    # ["transmissionconnect"]="TransmissionConnect|Efficient Torrenting|Lightweight Torrent Client"
    # ["utorrentconnect"]="uTorrentConnect|Enterprise Torrenting|Torrent Client"

    # Phase 2 - Cloud Services
    ["metubeconnect"]="MeTubeConnect|YouTube Downloads|Download Manager"
    ["ytdlpconnect"]="YT-DLPConnect|1800+ Sites Support|Download Manager"
    ["nextcloudconnect"]="NextcloudConnect|Self-Hosted Cloud|Cloud Storage"
    ["filebrowserconnect"]="FileBrowserConnect|Web File Manager|Cloud Storage"
    ["jellyfinconnect"]="JellyfinConnect|Open-Source Streaming|Media Server"
    ["embyconnect"]="EmbyConnect|Professional Media|Media Server"

    # Phase 3 - Specialized Services
    ["seafileconnect"]="SeafileConnect|Encrypted Cloud|Cloud Storage"
    ["syncthingconnect"]="SyncthingConnect|P2P Synchronization|File Sync"
    ["matrixconnect"]="MatrixConnect|End-to-End Messaging|Communication"
    ["paperlessngconnect"]="PaperlessNGConnect|Document Management|Productivity"
    ["duplicaticonnect"]="DuplicatiConnect|Backup Management|Backup"
    ["wireguardconnect"]="WireGuardConnect|VPN Management|Networking"
    ["minecraftserverconnect"]="MinecraftServerConnect|Game Server Management|Gaming"
    ["onlyofficeconnect"]="OnlyOfficeConnect|Collaborative Editing|Productivity"
)

# Function to create application page
create_app_page() {
    local app_key=$1
    local app_name=$2
    local app_tagline=$3
    local app_category=$4
    local filename="${app_key}.html"

    cat > "$filename" << EOF
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${app_name} - ${app_tagline}</title>
    <meta name="description" content="${app_name} provides seamless integration with ${app_tagline}. Part of the ShareConnect ecosystem for intelligent media management.">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="styles.css">
</head>
<body>
    <!-- Theme Toggle -->
    <button id="theme-toggle" class="theme-toggle" aria-label="Toggle theme">
        <i class="fas fa-moon"></i>
        <i class="fas fa-sun"></i>
    </button>

    <!-- Mobile Menu Overlay -->
    <div class="mobile-menu-overlay"></div>

    <!-- Header -->
    <header class="header">
        <nav class="nav">
            <div class="nav-container">
                <a href="index.html" class="logo">
                     <div class="logo-icon">
                          <img src="assets/logo_transparent.png" alt="ShareConnect Logo" class="logo-image">
                      </div>
                    <span>ShareConnect</span>
                </a>

                <div class="nav-menu">
                    <a href="index.html" class="nav-link">Home</a>
                    <a href="#features" class="nav-link">Features</a>
                    <a href="#integration" class="nav-link">Integration</a>
                    <a href="products.html" class="nav-link">Products</a>
                    <a href="manuals.html" class="nav-link">Manuals</a>
                    <a href="https://github.com/yourusername/ShareConnect" class="nav-link github">
                        <i class="fab fa-github"></i>
                        GitHub
                    </a>
                </div>

                <!-- Mobile Menu -->
                <div class="nav-menu mobile">
                    <a href="index.html" class="nav-link">Home</a>
                    <a href="#features" class="nav-link">Features</a>
                    <a href="#integration" class="nav-link">Integration</a>
                    <a href="products.html" class="nav-link">Products</a>
                    <a href="manuals.html" class="nav-link">Manuals</a>
                    <a href="https://github.com/yourusername/ShareConnect" class="nav-link github">
                        <i class="fab fa-github"></i>
                        GitHub
                    </a>
                </div>

                <button class="mobile-menu-toggle" aria-label="Toggle menu">
                    <i class="fas fa-bars"></i>
                </button>
            </div>
        </nav>
    </header>

    <!-- Hero Section -->
    <section class="hero">
        <div class="hero-container">
            <div class="hero-content">
                <div class="hero-badge">
                    <span class="badge-text">${app_category}</span>
                </div>
                <h1 class="hero-title">
                    ${app_name}
                    <span class="gradient-text">Professional Integration</span>
                </h1>
                <p class="hero-subtitle">
                    Complete integration with ${app_tagline}. Part of the ShareConnect ecosystem, ${app_name} brings professional-grade features with seamless connectivity to your media workflow.
                </p>
                <div class="hero-features">
                    <div class="hero-feature">
                        <i class="fas fa-plug"></i>
                        <span>Easy Integration</span>
                    </div>
                    <div class="hero-feature">
                        <i class="fas fa-shield-alt"></i>
                        <span>Secure & Reliable</span>
                    </div>
                    <div class="hero-feature">
                        <i class="fas fa-sync"></i>
                        <span>Real-time Sync</span>
                    </div>
                </div>
                <div class="hero-actions">
                    <a href="https://github.com/yourusername/ShareConnect" class="btn btn-primary btn-large">
                        <i class="fab fa-github"></i>
                        Download ${app_name}
                    </a>
                    <a href="manuals.html" class="btn btn-secondary btn-large">
                        <i class="fas fa-book"></i>
                        Setup Guide
                    </a>
                </div>
                <div class="hero-stats">
                    <div class="stat">
                        <div class="stat-number">100%</div>
                        <div class="stat-label">Open Source</div>
                    </div>
                    <div class="stat">
                        <div class="stat-number">Production</div>
                        <div class="stat-label">Ready</div>
                    </div>
                    <div class="stat">
                        <div class="stat-number">Free</div>
                        <div class="stat-label">& Open</div>
                    </div>
                </div>
            </div>
            <div class="hero-visual">
                <div class="hero-illustration">
                    <img src="assets/hero-illustration.svg" alt="${app_name} Hero Illustration" class="hero-image">
                </div>
                <div class="floating-elements">
                    <div class="floating-card card-1">
                        <i class="fas fa-cogs"></i>
                        <span>Professional Features</span>
                    </div>
                    <div class="floating-card card-2">
                        <i class="fas fa-share-alt"></i>
                        <span>ShareConnect Integration</span>
                    </div>
                    <div class="floating-card card-3">
                        <i class="fas fa-mobile-alt"></i>
                        <span>Android Optimized</span>
                    </div>
                    <div class="floating-card card-4">
                        <i class="fas fa-lock"></i>
                        <span>Secure & Private</span>
                    </div>
                </div>
            </div>
        </div>
        <div class="hero-bg-pattern"></div>
        <div class="hero-bg-shapes">
            <div class="shape shape-1"></div>
            <div class="shape shape-2"></div>
            <div class="shape shape-3"></div>
        </div>
    </section>

    <!-- Features Section -->
    <section id="features" class="features">
        <div class="container">
            <div class="section-header">
                <h2>Why Choose ${app_name}?</h2>
                <p>Professional integration with enterprise-grade features</p>
            </div>

            <div class="features-grid">
                <div class="feature-card">
                    <div class="feature-icon">
                        <i class="fas fa-plug"></i>
                    </div>
                    <h3>Seamless Integration</h3>
                    <p>Deep integration with ${app_tagline} providing access to all features through the ShareConnect ecosystem.</p>
                </div>

                <div class="feature-card">
                    <div class="feature-icon">
                        <i class="fas fa-shield-alt"></i>
                    </div>
                    <h3>Secure Connection</h3>
                    <p>Encrypted communication and secure credential storage. Your data stays private and protected.</p>
                </div>

                <div class="feature-card">
                    <div class="feature-icon">
                        <i class="fas fa-sync"></i>
                    </div>
                    <h3>Real-time Synchronization</h3>
                    <p>Stay connected with real-time sync across all your ShareConnect applications and services.</p>
                </div>

                <div class="feature-card">
                    <div class="feature-icon">
                        <i class="fas fa-mobile-alt"></i>
                    </div>
                    <h3>Android Optimized</h3>
                    <p>Built specifically for Android with modern UI/UX following Material Design 3 principles.</p>
                </div>

                <div class="feature-card">
                    <div class="feature-icon">
                        <i class="fas fa-brain"></i>
                    </div>
                    <h3>Smart Automation</h3>
                    <p>Intelligent automation features that work seamlessly with ShareConnector for automated workflows.</p>
                </div>

                <div class="feature-card">
                    <div class="feature-icon">
                        <i class="fas fa-code"></i>
                    </div>
                    <h3>Open Source</h3>
                    <p>Completely open source with transparent development. Contribute to the project and help improve it.</p>
                </div>
            </div>
        </div>
    </section>

    <!-- Integration Section -->
    <section id="integration" class="connectors">
        <div class="container">
            <div class="section-header">
                <h2>Seamless Integration</h2>
                <p>${app_name} integrates perfectly with the ShareConnect ecosystem</p>
            </div>

            <div class="connectors-grid">
                <div class="connector-category">
                    <div class="category-header">
                        <div class="category-icon">
                            <i class="fas fa-share-alt"></i>
                        </div>
                        <h3>ShareConnector</h3>
                    </div>
                    <p>Universal sharing from any app</p>
                    <div class="connector-list">
                        <span class="connector-tag">Smart Routing</span>
                        <span class="connector-tag">Auto Integration</span>
                        <span class="connector-tag">One-Tap Setup</span>
                    </div>
                </div>

                <div class="connector-category">
                    <div class="category-header">
                        <div class="category-icon">
                            <i class="fas fa-cogs"></i>
                        </div>
                        <h3>Ecosystem Sync</h3>
                    </div>
                    <p>Real-time sync with all apps</p>
                    <div class="connector-list">
                        <span class="connector-tag">Live Updates</span>
                        <span class="connector-tag">Cross-App Sync</span>
                        <span class="connector-tag">Unified Settings</span>
                    </div>
                </div>

                <div class="connector-category">
                    <div class="category-header">
                        <div class="category-icon">
                            <i class="fas fa-mobile-alt"></i>
                        </div>
                        <h3>Android Integration</h3>
                    </div>
                    <p>Native Android experience</p>
                    <div class="connector-list">
                        <span class="connector-tag">Material Design</span>
                        <span class="connector-tag">System Integration</span>
                        <span class="connector-tag">Battery Optimized</span>
                    </div>
                </div>

                <div class="connector-category featured">
                    <div class="category-visual">
                        <div class="mascot-placeholder">
                            <i class="fas fa-rocket"></i>
                            <p>${app_name}</p>
                        </div>
                    </div>
                    <div class="category-content">
                        <h3>Professional Integration</h3>
                        <p>Experience enterprise-grade ${app_tagline} integration with the power and simplicity of ShareConnect.</p>
                        <a href="products.html" class="btn btn-outline">Explore Ecosystem</a>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- CTA Section -->
    <section class="cta">
        <div class="container">
            <div class="cta-content">
                <h2>Ready to Get Started?</h2>
                <p>Download ${app_name} and experience professional ${app_tagline} integration with ShareConnect.</p>
                <div class="cta-actions">
                    <a href="https://github.com/yourusername/ShareConnect" class="btn btn-primary btn-large">
                        <i class="fab fa-github"></i>
                        Download ${app_name}
                    </a>
                    <a href="manuals.html" class="btn btn-outline btn-large">
                        <i class="fas fa-book"></i>
                        Setup Guide
                    </a>
                </div>
            </div>
        </div>
    </section>

    <!-- Footer -->
    <footer class="footer">
        <div class="container">
            <div class="footer-content">
                <div class="footer-section">
                    <div class="footer-logo">
                          <div class="logo-icon">
                              <img src="assets/logo_transparent.png" alt="ShareConnect Logo" class="logo-image">
                          </div>
                        <span>ShareConnect</span>
                    </div>
                    <p>Revolutionizing media sharing with intelligent connectors for all your favorite services.</p>
                    <div class="social-links">
                        <a href="#" aria-label="GitHub"><i class="fab fa-github"></i></a>
                        <a href="#" aria-label="Twitter"><i class="fab fa-twitter"></i></a>
                        <a href="#" aria-label="Discord"><i class="fab fa-discord"></i></a>
                    </div>
                </div>

                <div class="footer-section">
                    <h4>Product</h4>
                    <ul>
                        <li><a href="#features">Features</a></li>
                        <li><a href="#integration">Integration</a></li>
                        <li><a href="products.html">All Products</a></li>
                        <li><a href="manuals.html">User Manuals</a></li>
                    </ul>
                </div>

                <div class="footer-section">
                    <h4>Resources</h4>
                    <ul>
                        <li><a href="https://github.com/yourusername/ShareConnect">GitHub</a></li>
                        <li><a href="index.html">Home</a></li>
                        <li><a href="#">Privacy</a></li>
                        <li><a href="#">Terms</a></li>
                    </ul>
                </div>

                <div class="footer-section">
                    <h4>Community</h4>
                    <ul>
                        <li><a href="#">Discord Server</a></li>
                        <li><a href="#">Contribute</a></li>
                        <li><a href="#">Bug Reports</a></li>
                        <li><a href="#">Feature Requests</a></li>
                    </ul>
                </div>
            </div>

            <div class="footer-bottom">
                <p>&copy; 2025 ShareConnect. Made with <i class="fas fa-heart"></i> for media enthusiasts.</p>
            </div>
        </div>
    </footer>

    <!-- Toast Notifications -->
    <div id="toast-container" class="toast-container"></div>

    <!-- Modal/Dialog System -->
    <div id="modal-overlay" class="modal-overlay"></div>
    <div id="modal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h3 id="modal-title">Modal Title</h3>
                <button class="modal-close" aria-label="Close modal">
                    <i class="fas fa-times"></i>
                </button>
            </div>
            <div id="modal-body" class="modal-body">
                Modal content goes here.
            </div>
        </div>
    </div>

    <script src="script.js"></script>
</body>
</html>
EOF

    echo "Created $filename for $app_name"
}

# Generate pages for all applications
for app_key in "${!apps[@]}"; do
    IFS='|' read -r app_name app_tagline app_category <<< "${apps[$app_key]}"
    create_app_page "$app_key" "$app_name" "$app_tagline" "$app_category"
done

echo "All application pages generated successfully!"
echo "Generated pages:"
for app_key in "${!apps[@]}"; do
    echo "  - ${app_key}.html"
done</content>
</xai:function_call">Now let me run this script to generate all the remaining application pages. 

<xai:function_call name="bash">
<parameter name="command">cd Website && chmod +x generate_pages.sh && ./generate_pages.sh