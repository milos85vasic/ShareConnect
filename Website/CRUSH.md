# CRUSH.md - ShareConnect Website

## Build & Development Commands

**Docker Development:**
- `./scripts/build.sh` - Build Docker image
- `./scripts/start.sh [port]` - Start website (default: 8080)
- `./scripts/stop.sh` - Stop website
- `./scripts/restart.sh` - Restart website
- `./scripts/deploy.sh` - Full deployment (stop, rebuild, start)
- `./scripts/logs.sh` - View container logs

**Local Development:**
- `python3 -m http.server 8080` - Serve from public/
- `python3 test_website.py` - Run comprehensive tests
- `python3 final_test.py` - Run final validation tests
- `python3 check_links.py` - Check for broken links

## Code Style Guidelines

**HTML:**
- Semantic HTML5 with proper accessibility attributes
- CSS classes use kebab-case: `nav-container`, `theme-toggle`
- External scripts in head, internal scripts before closing body
- Use Font Awesome 6 icons with proper ARIA labels

**CSS:**
- CSS variables for theming in `:root` selector
- Mobile-first responsive design with media queries
- BEM-like naming: `.component__element--modifier`
- Use flexbox/grid for layouts, avoid floats
- Dark theme via `[data-theme="dark"]` selector

**JavaScript:**
- ES6+ with class-based architecture
- Vanilla JS only (no frameworks)
- Event delegation for performance
- LocalStorage for theme persistence
- Smooth animations with CSS transitions

**Python (Testing):**
- Python 3 with shebang `#!/usr/bin/env python3`
- Descriptive function names with docstrings
- Modular test functions with clear assertions
- Console output with emoji indicators

**File Organization:**
- Main files in root: `index.html`, `styles.css`, `script.js`
- Connector pages: `*connect.html` naming pattern
- Static assets in `public/` and `assets/` directories
- Docker configs in `docker/` directory

**Testing Strategy:**
- File existence checks for all required pages
- CSS theme validation (blue color scheme)
- Consumer-focused messaging validation
- Mobile responsive design testing
- Internal link validation

**Error Handling:**
- Graceful fallbacks for missing features
- Console logging for debugging
- User-friendly error messages
- Health check endpoints for monitoring