# ShareConnect Website

This is the official GitHub Pages website for the ShareConnect project, showcasing the enterprise-grade media sharing platform with comprehensive health monitoring and quality metrics.

## 🚀 Features

- **Enterprise Design**: Modern, professional UI with dark theme
- **Real-time Dashboard**: Live project health monitoring
- **Comprehensive Metrics**: Testing, security, and code quality badges
- **Application Ecosystem**: Overview of all 5 Android applications
- **Responsive Design**: Optimized for all devices
- **Interactive Elements**: Smooth animations and hover effects

## 📊 Dashboard Sections

### Health Monitoring
- **Testing Status**: 100% success rate across all test types
- **Security Status**: No critical vulnerabilities with Snyk integration
- **Code Quality**: A+ SonarQube grade with 95% coverage

### Quality Metrics
- **Testing & Quality**: Unit tests, automation, AI QA, code coverage
- **Security & Analysis**: Snyk security, SonarQube, technical debt
- **Build & Technology**: Build status, Android API, Kotlin/Java versions

### Application Ecosystem
- **ShareConnector**: Main application for universal media sharing
- **qBitConnect**: qBittorrent client integration
- **TransmissionConnect**: Transmission client integration
- **uTorrentConnect**: uTorrent client integration
- **JDownloaderConnect**: JDownloader integration

## 🛠️ Development

### Local Development

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/ShareConnect.git
   cd ShareConnect/Website
   ```

2. **Serve locally**
   ```bash
   # Using Python (if installed)
   python -m http.server 8000
   
   # Using Node.js (if installed)
   npx serve .
   
   # Using PHP (if installed)
   php -S localhost:8000
   ```

3. **Open in browser**
   ```
   http://localhost:8000
   ```

### GitHub Pages Deployment

This website is automatically deployed to GitHub Pages when changes are pushed to the main branch.

**Deployment URL:** `https://yourusername.github.io/ShareConnect`

## 📁 File Structure

```
Website/
├── index.html              # Main website page
├── styles.css              # Custom CSS styles
├── script.js               # JavaScript functionality
├── _config.yml             # GitHub Pages configuration
├── README.md               # This file
└── assets/                 # Static assets (optional)
    ├── images/             # Website images
    └── icons/              # Favicons and app icons
```

## 🎨 Design System

### Color Palette
- **Primary**: `#2196F3` (Blue)
- **Success**: `#4CAF50` (Green)
- **Warning**: `#FF9800` (Orange)
- **Background**: Dark gradient theme

### Typography
- **Primary Font**: Inter
- **Monospace**: JetBrains Mono
- **Icons**: Font Awesome 6

### Components
- **Health Cards**: Gradient backgrounds with metrics
- **Badge Items**: Interactive status indicators
- **Application Cards**: Hover effects and animations
- **Navigation**: Smooth scrolling and active states

## 🔧 Customization

### Adding New Metrics

1. **Update HTML** in `index.html`:
   ```html
   <div class="badge-item">
       <div class="badge-icon">
           <i class="fas fa-icon-name"></i>
       </div>
       <div class="badge-text">New Metric</div>
       <div class="badge-status status-success">Value</div>
   </div>
   ```

2. **Update CSS** in `styles.css` if needed

3. **Update JavaScript** in `script.js` for interactivity

### Modifying Colors

Update CSS variables in `:root` section of `styles.css`:

```css
:root {
    --primary: #your-color;
    --success: #your-color;
    /* ... other variables */
}
```

## 📱 Mobile Optimization

The website is fully responsive and optimized for:
- **Mobile phones** (320px+)
- **Tablets** (768px+)
- **Desktop** (1024px+)
- **Large screens** (1200px+)

## 🔍 SEO Optimization

- **Meta tags** for description and viewport
- **Structured data** for search engines
- **Open Graph** tags for social sharing
- **Semantic HTML** for accessibility

## 📈 Performance

- **Fast loading** with optimized assets
- **Smooth animations** with CSS transitions
- **Lazy loading** for better performance
- **Minimal JavaScript** for faster execution

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test responsiveness
5. Submit a pull request

## 📄 License

This website is part of the ShareConnect project and follows the same licensing terms.

## 📞 Support

For website-related issues:
- Check this README
- Review the code comments
- Open an issue on GitHub

---

**Last Updated**: 2025-10-21  
**Website Version**: 1.0.0  
**Built With**: HTML5, CSS3, JavaScript