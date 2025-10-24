# Website

## Overview and Purpose

Website is the public-facing web presence for the ShareConnect project, providing documentation, downloads, and real-time project health monitoring. It serves as the central hub for users to learn about ShareConnect applications, access documentation, and monitor project status through live dashboards.

## Architecture and Components

Website follows a static site architecture with dynamic dashboard integration:

### Core Components
- **Index Page**: Main landing page with project overview
- **Documentation**: Technical documentation and user manuals
- **Dashboard**: Live project health monitoring
- **Download Section**: Application distribution
- **Blog/News**: Project updates and announcements

### Technologies
- **HTML5/CSS3**: Modern web standards
- **JavaScript**: Interactive features and dashboard
- **Jekyll**: Static site generation (optional)
- **GitHub Pages**: Hosting platform

## File Structure

```
Website/
├── index.html          # Main landing page
├── script.js           # Interactive functionality
├── styles.css          # Site styling
├── _config.yml         # Jekyll configuration
├── README.md           # Website documentation
├── deploy.sh           # Deployment script
└── docs/               # Documentation pages
```

## Key Features

### Live Dashboard
- Real-time test results and coverage
- Security scan status
- Build status indicators
- Code quality metrics
- Performance benchmarks

### Documentation Hub
- User manuals for all applications
- Technical documentation
- API references
- Troubleshooting guides

### Download Portal
- APK distribution for all apps
- Release notes and changelogs
- System requirements
- Installation guides

## Usage Examples

### Accessing Live Dashboard
```javascript
// Dashboard data loading
async function loadDashboardData() {
    const response = await fetch('/api/dashboard');
    const data = await response.json();

    updateTestStatus(data.tests);
    updateSecurityStatus(data.security);
    updateBuildStatus(data.build);
}
```

### Deploying Updates
```bash
# Deploy website updates
./deploy.sh

# Or manually via GitHub Pages
git add .
git commit -m "Update website content"
git push origin main
```

## Dependencies

### Web Technologies
- **HTML5**: Semantic markup
- **CSS3**: Responsive design
- **JavaScript ES6+**: Interactive features
- **Fetch API**: Data loading

### Build Tools (Optional)
- **Jekyll**: Static site generation
- **Sass**: CSS preprocessing
- **Webpack**: Asset bundling

---

*For more information, visit [https://shareconnect.org](https://shareconnect.org)*