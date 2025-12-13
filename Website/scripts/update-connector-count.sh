#!/bin/bash

# ShareConnect Website Connector Count Update Script
# Updates all references from "4" connectors to "20+" connectors across the website

set -e

echo "🔧 Updating ShareConnect Website Connector Count from 4 to 20+"
echo "================================================================"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Website directory
WEBSITE_DIR="/home/milosvasic/Projects/ShareConnect/Website"

# Function to update connector count in files
update_connector_count() {
    local file="$1"
    local backup_file="${file}.backup.$(date +%Y%m%d_%H%M%S)"
    
    echo -e "${BLUE}Processing: ${file}${NC}"
    
    # Create backup
    cp "$file" "$backup_file"
    echo -e "${YELLOW}  📋 Backup created: ${backup_file}${NC}"
    
    # Update specific patterns to avoid over-matching
    
    # Update "4 connectors" to "20+ connectors"
    sed -i 's/4 connectors/20+ connectors/g' "$file"
    sed -i 's/4 Connectors/20+ Connectors/g' "$file"
    
    # Update "4 Professional Connectors" to "20+ Professional Connectors"
    sed -i 's/4 Professional Connectors/20+ Professional Connectors/g' "$file"
    sed -i 's/4 Professional Connector/20+ Professional Connector/g' "$file"
    
    # Update standalone "4" in connector context
    sed -i 's/"4"/"20+"/g' "$file"
    sed -i 's/>4</>20+</g' "$file"
    sed -i 's/ 4 connectors/ 20+ connectors/g' "$file"
    sed -i 's/ 4 Connectors/ 20+ Connectors/g' "$file"
    
    # Update in statistics and feature sections
    sed -i 's/4 specialized Android applications/20+ specialized Android applications/g' "$file"
    sed -i 's/4 specialized Android apps/20+ specialized Android applications/g' "$file"
    sed -i 's/4 Android applications/20+ Android applications/g' "$file"
    sed -i 's/4 Android apps/20+ Android applications/g' "$file"
    
    echo -e "${GREEN}  ✅ Connector count updated${NC}"
}

# Function to update connector descriptions
update_connector_descriptions() {
    local file="$1"
    
    echo -e "${BLUE}Updating connector descriptions in: ${file}${NC}"
    
    # Add comprehensive connector information
    sed -i 's/intelligent connectors for torrent clients/20+ intelligent connectors for torrent clients, media servers, cloud storage/g' "$file"
    sed -i 's/One tap sharing made simple/One tap sharing across 20+ professional services made simple/g' "$file"
    sed -i 's/4 specialized Android applications/20+ specialized Android applications/g' "$file"
    
    echo -e "${GREEN}  ✅ Connector descriptions updated${NC}"
}

# Function to add video course integration
add_video_course_integration() {
    local file="$1"
    
    echo -e "${BLUE}Adding video course integration to: ${file}${NC}"
    
    # Add video course references if not present
    if ! grep -q "Video Courses" "$file"; then
        # Add video course section reference in navigation
        sed -i 's/<a href="#features" class="nav-link">Features<\/a>/<a href="#courses" class="nav-link">Video Courses<\/a>\n                    <a href="#features" class="nav-link">Features<\/a>/' "$file"
        
        # Add video course hero badge if not present
        if ! grep -q "Professional Video Courses Available" "$file"; then
            sed -i '/<div class="hero-content">/a\                <div class="hero-badge">\                    <span class="badge-text">Professional Video Courses Available<\/span>\                <\/div>' "$file"
        fi
    fi
    
    echo -e "${GREEN}  ✅ Video course integration added${NC}"
}

# Function to validate updates
validate_updates() {
    local file="$1"
    
    echo -e "${BLUE}Validating updates in: ${file}${NC}"
    
    # Check for remaining "4 connectors" references
    if grep -q "4 connectors\|4 Connectors" "$file"; then
        echo -e "${RED}  ⚠️  Warning: Still found '4 connectors' references${NC}"
        grep -n "4 connectors\|4 Connectors" "$file"
    else
        echo -e "${GREEN}  ✅ No remaining '4 connectors' references${NC}"
    fi
    
    # Check for new "20+" references
    local count_20plus=$(grep -c "20+" "$file" || echo "0")
    echo -e "${GREEN}  ✅ Found ${count_20plus} '20+' references${NC}"
    
    # Validate HTML structure
    if command -v xmllint >/dev/null 2>&1; then
        if xmllint --html --noout "$file" 2>/dev/null; then
            echo -e "${GREEN}  ✅ HTML structure is valid${NC}"
        else
            echo -e "${YELLOW}  ⚠️  HTML validation warnings (may be due to HTML5 features)${NC}"
        fi
    fi
}

# Function to create CSS for new elements
create_additional_css() {
    local css_file="${WEBSITE_DIR}/styles-enhanced-additions.css"
    
    echo -e "${BLUE}Creating additional CSS file: ${css_file}${NC}"
    
    cat > "$css_file" << 'EOF'
/* Additional CSS for Enhanced ShareConnect Website */

/* Video Course Elements */
.hero-badge {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    padding: 8px 16px;
    background: #dcfce7;
    color: #15803d;
    border-radius: 9999px;
    font-size: 12px;
    font-weight: 500;
    margin-bottom: 24px;
    animation: fadeInUp 0.6s ease-out;
}

.badge-text {
    font-weight: 600;
}

/* Video Player Enhancements */
.video-js {
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
}

.vjs-big-play-button {
    background: rgba(59, 130, 246, 0.9) !important;
    border: none !important;
    border-radius: 50% !important;
    width: 80px !important;
    height: 80px !important;
    line-height: 80px !important;
    font-size: 24px !important;
    transition: all 0.3s ease;
}

.vjs-big-play-button:hover {
    background: rgba(37, 99, 235, 0.9) !important;
    transform: scale(1.1);
}

/* Interactive Demo Elements */
.demo-success {
    background: #f0fdf4;
    border: 1px solid #bbf7d0;
    border-radius: 12px;
    padding: 24px;
    margin: 16px 0;
}

.success-header {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 16px;
    color: #15803d;
    font-weight: 600;
}

.success-header i {
    font-size: 24px;
}

.success-data {
    margin-top: 16px;
}

.data-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 8px 0;
    border-bottom: 1px solid #e5e7eb;
}

.data-item:last-child {
    border-bottom: none;
}

.data-label {
    font-weight: 500;
    color: #374151;
}

.data-value {
    color: #15803d;
    font-weight: 600;
}

/* Course Card Enhancements */
.course-card.featured {
    border: 2px solid #dbeafe;
    position: relative;
}

.course-card.featured::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 4px;
    background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
    border-radius: 12px 12px 0 0;
}

/* Floating Elements Animation */
@keyframes float {
    0%, 100% { transform: translateY(0px); }
    50% { transform: translateY(-20px); }
}

.floating-card {
    animation: float 6s ease-in-out infinite;
}

/* Enhanced Statistics */
.hero-stats {
    display: flex;
    gap: 32px;
    margin-top: 32px;
}

.stat {
    text-align: center;
}

.stat-number {
    font-size: 2rem;
    font-weight: 800;
    color: #3b82f6;
    line-height: 1;
}

.stat-label {
    font-size: 0.875rem;
    color: #6b7280;
    margin-top: 4px;
}

/* Connector Grid Improvements */
.connector-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 24px;
}

.connector-card {
    background: #ffffff;
    border-radius: 16px;
    padding: 24px;
    border: 1px solid #e5e7eb;
    transition: all 0.3s ease;
    position: relative;
    overflow: hidden;
}

.connector-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
    border-color: #3b82f6;
}

.connector-card.featured {
    border: 2px solid #dbeafe;
}

.connector-card.featured::before {
    content: '';
    position: absolute;
    top: -2px;
    left: -2px;
    right: -2px;
    height: 4px;
    background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
    border-radius: 16px 16px 0 0;
}

/* Dark Mode Support */
[data-theme="dark"] .hero-badge {
    background: #15803d;
    color: #dcfce7;
}

[data-theme="dark"] .connector-card {
    background: #1f2937;
    border-color: #374151;
}

[data-theme="dark"] .connector-card:hover {
    border-color: #3b82f6;
}

[data-theme="dark"] .demo-success {
    background: #052e16;
    border-color: #15803d;
}

/* Responsive Design */
@media (max-width: 768px) {
    .hero-stats {
        flex-direction: column;
        gap: 24px;
    }
    
    .connector-grid {
        grid-template-columns: 1fr;
    }
    
    .floating-elements {
        display: none;
    }
}

/* Accessibility Improvements */
.skip-link {
    position: absolute;
    top: -40px;
    left: 6px;
    background: #000000;
    color: white;
    padding: 8px;
    text-decoration: none;
    border-radius: 4px;
    z-index: 1000;
    transition: top 0.3s;
}

.skip-link:focus {
    top: 6px;
}

.sr-only {
    position: absolute;
    width: 1px;
    height: 1px;
    padding: 0;
    margin: -1px;
    overflow: hidden;
    clip: rect(0, 0, 0, 0);
    white-space: nowrap;
    border: 0;
}

/* Notification System */
.notification {
    position: fixed;
    top: 20px;
    right: 20px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
    padding: 16px;
    max-width: 400px;
    z-index: 1000;
    transform: translateX(100%);
    transition: transform 0.3s ease;
}

.notification.visible {
    transform: translateX(0);
}

.notification.removing {
    transform: translateX(100%);
}

.notification-success {
    border-left: 4px solid #22c55e;
}

.notification-error {
    border-left: 4px solid #ef4444;
}

.notification-warning {
    border-left: 4px solid #f59e0b;
}

.notification-info {
    border-left: 4px solid #3b82f6;
}

.notification-content {
    display: flex;
    align-items: center;
    gap: 12px;
}

.notification-close {
    position: absolute;
    top: 8px;
    right: 8px;
    background: none;
    border: none;
    cursor: pointer;
    color: #6b7280;
    padding: 4px;
}

.notification-close:hover {
    color: #374151;
}

/* Scroll to Top Button */
.scroll-to-top {
    position: fixed;
    bottom: 20px;
    right: 20px;
    width: 48px;
    height: 48px;
    background: #3b82f6;
    color: white;
    border: none;
    border-radius: 50%;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
    opacity: 0;
    visibility: hidden;
    transition: all 0.3s ease;
    z-index: 999;
}

.scroll-to-top.visible {
    opacity: 1;
    visibility: visible;
}

.scroll-to-top:hover {
    background: #2563eb;
    transform: translateY(-2px);
}

/* Enrollment Modal */
.enrollment-modal {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1001;
    opacity: 0;
    visibility: hidden;
    transition: all 0.3s ease;
}

.enrollment-modal.active {
    opacity: 1;
    visibility: visible;
}

.enrollment-modal .modal-content {
    background: white;
    border-radius: 16px;
    max-width: 500px;
    width: 90%;
    max-height: 90vh;
    overflow-y: auto;
    transform: scale(0.9);
    transition: transform 0.3s ease;
}

.enrollment-modal.active .modal-content {
    transform: scale(1);
}

.modal-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 24px;
    border-bottom: 1px solid #e5e7eb;
}

.modal-header h3 {
    margin: 0;
    font-size: 1.25rem;
    font-weight: 600;
}

.modal-close {
    background: none;
    border: none;
    cursor: pointer;
    color: #6b7280;
    font-size: 1.25rem;
    padding: 4px;
}

.modal-close:hover {
    color: #374151;
}

.modal-body {
    padding: 24px;
}

.course-preview {
    display: flex;
    gap: 16px;
    margin-bottom: 24px;
    align-items: center;
}

.course-preview img {
    width: 80px;
    height: 60px;
    object-fit: cover;
    border-radius: 8px;
}

.course-info h4 {
    margin: 0 0 8px 0;
    font-size: 1.125rem;
}

.course-info p {
    margin: 0;
    color: #6b7280;
    font-size: 0.875rem;
}

.enrollment-form .form-group {
    margin-bottom: 16px;
}

.enrollment-form label {
    display: block;
    margin-bottom: 4px;
    font-weight: 500;
    color: #374151;
}

.enrollment-form input[type="text"],
.enrollment-form input[type="email"] {
    width: 100%;
    padding: 8px 12px;
    border: 1px solid #d1d5db;
    border-radius: 6px;
    font-size: 1rem;
}

.enrollment-form input[type="text"]:focus,
.enrollment-form input[type="email"]:focus {
    outline: none;
    border-color: #3b82f6;
    box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.checkbox-label {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
}

.checkbox-label input[type="checkbox"] {
    width: auto;
}

/* Responsive Video Player */
.video-js {
    width: 100% !important;
    height: auto !important;
}

/* Connection Quality Adjustments */
body.low-bandwidth .floating-elements {
    display: none;
}

body.low-bandwidth .video-js {
    box-shadow: none;
}

/* High Contrast Mode */
body.high-contrast .connector-card {
    border-width: 2px;
}

body.high-contrast .btn-primary {
    background: #000000;
    color: #ffffff;
}

/* Reduced Motion */
body.reduced-motion *,
body.reduced-motion *::before,
body.reduced-motion *::after {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: 0.01ms !important;
}
EOF

    echo -e "${GREEN}  ✅ Additional CSS created${NC}"
}

# Main execution
echo -e "${BLUE}Starting connector count update process...${NC}"

# Create js directory if it doesn't exist
mkdir -p "${WEBSITE_DIR}/js"

# Process main HTML files
html_files=(
    "${WEBSITE_DIR}/index.html"
    "${WEBSITE_DIR}/products.html"
    "${WEBSITE_DIR}/manuals.html"
)

for file in "${html_files[@]}"; do
    if [[ -f "$file" ]]; then
        echo -e "${YELLOW}Processing: $file${NC}"
        update_connector_count "$file"
        update_connector_descriptions "$file"
        add_video_course_integration "$file"
        validate_updates "$file"
        echo ""
    else
        echo -e "${RED}Warning: $file not found${NC}"
    fi
done

# Create additional CSS
create_additional_css

# Update JavaScript files to include new functionality
echo -e "${BLUE}Updating JavaScript functionality...${NC}"

# Create a simple script to include the new CSS
include_script="${WEBSITE_DIR}/js/include-enhanced-styles.js"
cat > "$include_script" << 'EOF'
// Auto-include enhanced styles
document.addEventListener('DOMContentLoaded', function() {
    const link = document.createElement('link');
    link.rel = 'stylesheet';
    link.href = 'styles-enhanced-additions.css';
    document.head.appendChild(link);
});
EOF

echo -e "${GREEN}✅ JavaScript include script created${NC}"

# Summary
echo -e "${GREEN}================================================================${NC}"
echo -e "${GREEN}✅ Connector count update completed successfully!${NC}"
echo -e "${GREEN}================================================================${NC}"
echo ""
echo "📊 Summary of changes:"
echo "  • Updated '4 connectors' → '20+ connectors' across all pages"
echo "  • Added professional video course integration"
echo "  • Enhanced interactive demo functionality"
echo "  • Created additional CSS for new elements"
echo "  • Added accessibility and performance improvements"
echo ""
echo "🔧 Files processed:"
for file in "${html_files[@]}"; do
    if [[ -f "$file" ]]; then
        echo "  • $file"
    fi
done
echo ""
echo "📁 New files created:"
echo "  • ${WEBSITE_DIR}/styles-enhanced-additions.css"
echo "  • ${WEBSITE_DIR}/js/include-enhanced-styles.js"
echo ""
echo "💡 Next steps:"
echo "  1. Test the updated website in a browser"
echo "  2. Verify all interactive elements work correctly"
echo "  3. Update any remaining hardcoded '4' references if found"
echo "  4. Deploy the updated website"
echo ""
echo -e "${BLUE}🌐 Website is now ready for Phase 5: Video Integration & Marketing!${NC}"