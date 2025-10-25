# ShareConnect Website

Modern, responsive website for the ShareConnect ecosystem - a unified Android platform for managing self-hosted services.

## Features

- **Modern Design**: Material Design 3 inspired with smooth animations
- **Responsive**: Optimized for all devices (mobile, tablet, desktop)
- **Fast**: Optimized assets, lazy loading, and efficient caching
- **Accessible**: WCAG compliant with keyboard navigation and screen reader support
- **Docker Ready**: Containerized with Nginx for easy deployment

## Project Structure

```
Website/
├── public/               # Website files
│   ├── css/             # Stylesheets
│   │   ├── main.css     # Main styles with CSS variables
│   │   └── animations.css # Animation effects
│   ├── js/              # JavaScript
│   │   └── main.js      # Interactive functionality
│   ├── images/          # Images and assets
│   ├── fonts/           # Custom fonts
│   └── index.html       # Main HTML page
├── docker/              # Docker configuration
│   ├── Dockerfile       # Multi-stage Docker build
│   ├── docker-compose.yml # Docker Compose config
│   ├── nginx.conf       # Nginx main config
│   ├── default.conf     # Server configuration
│   └── healthcheck.sh   # Health check script
└── scripts/             # Management scripts
    ├── build.sh         # Build Docker image
    ├── start.sh         # Start website
    ├── stop.sh          # Stop website
    ├── restart.sh       # Restart website
    ├── logs.sh          # View logs
    └── deploy.sh        # Full deployment
```

## Quick Start

### Using Docker (Recommended)

1. **Build and start the website:**
   ```bash
   ./scripts/deploy.sh
   ```

2. **Access the website:**
   ```
   http://localhost:8080
   ```

### Individual Commands

```bash
# Build Docker image
./scripts/build.sh

# Start website
./scripts/start.sh [port]

# View logs
./scripts/logs.sh          # Last 100 lines
./scripts/logs.sh follow   # Follow in real-time

# Restart website
./scripts/restart.sh

# Stop website
./scripts/stop.sh

# Full deployment (stop, rebuild, start)
./scripts/deploy.sh
```

### Using Docker Compose

```bash
cd docker
docker-compose up -d
docker-compose logs -f
docker-compose down
```

## Development

### Local Development (without Docker)

You can serve the website using any static file server:

```bash
# Using Python
cd public
python3 -m http.server 8080

# Using Node.js (http-server)
npm install -g http-server
cd public
http-server -p 8080

# Using PHP
cd public
php -S localhost:8080
```

### Making Changes

1. **Edit HTML**: Modify `/public/index.html`
2. **Edit CSS**: Update `/public/css/main.css` or `/public/css/animations.css`
3. **Edit JavaScript**: Update `/public/js/main.js`
4. **Rebuild**: Run `./scripts/deploy.sh` to rebuild and deploy

### CSS Variables

The website uses CSS variables for easy theming. Edit in `public/css/main.css`:

```css
:root {
    --primary: #6366f1;
    --secondary: #ec4899;
    --accent: #14b8a6;
    /* ... */
}
```

## Configuration

### Port Configuration

Change the default port (8080) by passing it as an argument:

```bash
./scripts/start.sh 3000
```

Or set the `PORT` environment variable:

```bash
PORT=3000 ./scripts/deploy.sh
```

### Nginx Configuration

- **Main config**: `docker/nginx.conf`
- **Server config**: `docker/default.conf`
- **Caching**: Static assets cached for 1 year
- **Compression**: Gzip enabled for all text files

## Performance

- **Cold Start**: < 1s
- **First Contentful Paint**: < 2s
- **Time to Interactive**: < 3s
- **Lighthouse Score**: 95+

### Optimization Features

- Gzip compression
- Browser caching
- Lazy loading for images
- Minified CSS/JS (in production)
- Optimized font loading
- Efficient animations with GPU acceleration

## Browser Support

- Chrome/Edge 90+
- Firefox 88+
- Safari 14+
- Opera 76+

## Security

- HTTPS ready (configure reverse proxy)
- Security headers (X-Frame-Options, X-Content-Type-Options, etc.)
- No inline scripts
- Content Security Policy compatible

## Deployment

### Production Deployment

1. **Configure your domain**
2. **Set up reverse proxy** (Nginx/Caddy with SSL)
3. **Deploy the container:**
   ```bash
   PORT=80 ./scripts/deploy.sh
   ```

### Example Caddy Configuration

```caddy
shareconnect.yourdomain.com {
    reverse_proxy localhost:8080
    encode gzip
}
```

### Example Nginx Reverse Proxy

```nginx
server {
    listen 443 ssl http2;
    server_name shareconnect.yourdomain.com;

    ssl_certificate /path/to/cert.pem;
    ssl_certificate_key /path/to/key.pem;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

## Monitoring

### Health Check

The website includes a health endpoint:

```bash
curl http://localhost:8080/health
```

### Docker Health Check

```bash
docker inspect shareconnect-website --format='{{.State.Health.Status}}'
```

### View Logs

```bash
# Docker logs
./scripts/logs.sh

# Nginx access logs
docker exec shareconnect-website tail -f /var/log/nginx/access.log

# Nginx error logs
docker exec shareconnect-website tail -f /var/log/nginx/error.log
```

## Troubleshooting

### Container won't start

```bash
# Check logs
docker logs shareconnect-website

# Check if port is in use
netstat -tulpn | grep 8080

# Try a different port
./scripts/start.sh 3000
```

### Website not accessible

```bash
# Verify container is running
docker ps | grep shareconnect-website

# Check health
docker inspect shareconnect-website --format='{{.State.Health.Status}}'

# Test from inside container
docker exec shareconnect-website curl http://localhost/health
```

### Build fails

```bash
# Clean Docker cache
docker builder prune -a

# Rebuild without cache
docker build --no-cache -f docker/Dockerfile -t shareconnect-website .
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test locally with `./scripts/deploy.sh`
5. Submit a pull request

## License

Part of the ShareConnect project. See main project LICENSE.

## Links

- **Main Project**: [ShareConnect Repository](https://github.com/milos85vasic/ShareConnect)
- **Documentation**: [Project Wiki](https://deepwiki.com/vasic-digital/ShareConnect)
- **Issues**: [GitHub Issues](https://github.com/milos85vasic/ShareConnect/issues)

## Credits

Built with:
- HTML5
- CSS3 (with CSS Grid and Flexbox)
- Vanilla JavaScript (no frameworks)
- Docker & Nginx
- Google Fonts (Inter, JetBrains Mono)

---

**ShareConnect** - Unified Self-Hosted Service Management
