# ShareConnect Website - Deployment Guide

Complete guide for deploying the ShareConnect website in various environments.

## Table of Contents

1. [Local Development](#local-development)
2. [Docker Deployment](#docker-deployment)
3. [Production Deployment](#production-deployment)
4. [Cloud Deployment](#cloud-deployment)
5. [CI/CD Integration](#cicd-integration)
6. [Monitoring & Maintenance](#monitoring--maintenance)

---

## Local Development

### Prerequisites

- **Option 1 (Docker)**: Docker 20.10+
- **Option 2 (Native)**: Any web server (Python, Node.js, PHP, etc.)

### Quick Start with Docker

```bash
cd Website
./scripts/deploy.sh
```

Access at: `http://localhost:8080`

### Quick Start without Docker

```bash
cd Website/public

# Python 3
python3 -m http.server 8080

# Node.js (install http-server globally first)
npm install -g http-server
http-server -p 8080

# PHP
php -S localhost:8080
```

---

## Docker Deployment

### Basic Docker Commands

#### Build Image

```bash
./scripts/build.sh
# OR specify a tag
./scripts/build.sh v1.0.0
```

#### Start Website

```bash
./scripts/start.sh
# OR specify a custom port
./scripts/start.sh 3000
```

#### View Logs

```bash
# Last 100 lines
./scripts/logs.sh

# Follow logs in real-time
./scripts/logs.sh follow
```

#### Restart Website

```bash
./scripts/restart.sh
```

#### Stop Website

```bash
./scripts/stop.sh
```

#### Full Deployment

```bash
# Stops existing container, rebuilds, and starts fresh
./scripts/deploy.sh
```

### Docker Compose Deployment

```bash
cd Website/docker
docker-compose up -d

# View logs
docker-compose logs -f

# Stop
docker-compose down

# Rebuild and restart
docker-compose up -d --build
```

### Custom Port with Docker Compose

Edit `docker/docker-compose.yml`:

```yaml
ports:
  - "3000:80"  # Change 3000 to your desired port
```

---

## Production Deployment

### Reverse Proxy Setup

For production, use a reverse proxy (Nginx or Caddy) to handle SSL/TLS.

#### Option 1: Caddy (Recommended for simplicity)

**Install Caddy:**

```bash
# Ubuntu/Debian
sudo apt install -y debian-keyring debian-archive-keyring apt-transport-https
curl -1sLf 'https://dl.cloudsmith.io/public/caddy/stable/gpg.key' | sudo gpg --dearmor -o /usr/share/keyrings/caddy-stable-archive-keyring.gpg
curl -1sLf 'https://dl.cloudsmith.io/public/caddy/stable/debian.deb.txt' | sudo tee /etc/apt/sources.list.d/caddy-stable.list
sudo apt update
sudo apt install caddy
```

**Configure Caddy:**

Create `/etc/caddy/Caddyfile`:

```caddy
shareconnect.yourdomain.com {
    reverse_proxy localhost:8080
    encode gzip

    # Security headers
    header {
        Strict-Transport-Security "max-age=31536000; includeSubDomains; preload"
        X-Content-Type-Options "nosniff"
        X-Frame-Options "SAMEORIGIN"
        X-XSS-Protection "1; mode=block"
        Referrer-Policy "strict-origin-when-cross-origin"
    }

    # Logging
    log {
        output file /var/log/caddy/shareconnect.log
    }
}
```

**Start Caddy:**

```bash
sudo systemctl enable caddy
sudo systemctl start caddy
sudo systemctl status caddy
```

Caddy automatically handles SSL certificates via Let's Encrypt!

#### Option 2: Nginx

**Install Nginx:**

```bash
sudo apt update
sudo apt install nginx certbot python3-certbot-nginx
```

**Configure Nginx:**

Create `/etc/nginx/sites-available/shareconnect`:

```nginx
upstream shareconnect_backend {
    server localhost:8080;
}

server {
    listen 80;
    server_name shareconnect.yourdomain.com;

    # Redirect HTTP to HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name shareconnect.yourdomain.com;

    # SSL configuration (managed by certbot)
    ssl_certificate /etc/letsencrypt/live/shareconnect.yourdomain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/shareconnect.yourdomain.com/privkey.pem;

    # SSL settings
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_prefer_server_ciphers on;
    ssl_ciphers 'ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256:ECDHE-ECDSA-AES256-GCM-SHA384:ECDHE-RSA-AES256-GCM-SHA384';

    # Security headers
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains; preload" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Referrer-Policy "strict-origin-when-cross-origin" always;

    # Logging
    access_log /var/log/nginx/shareconnect-access.log;
    error_log /var/log/nginx/shareconnect-error.log;

    # Proxy settings
    location / {
        proxy_pass http://shareconnect_backend;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;

        # Cache static assets
        proxy_cache_bypass $http_upgrade;
    }
}
```

**Enable site and obtain SSL:**

```bash
# Enable site
sudo ln -s /etc/nginx/sites-available/shareconnect /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx

# Obtain SSL certificate
sudo certbot --nginx -d shareconnect.yourdomain.com
```

### Deploy Docker Container

```bash
cd /home/milosvasic/Projects/ShareConnect/Website

# Deploy on port 8080 (accessed via reverse proxy)
./scripts/deploy.sh

# Set container to start on boot
docker update --restart=always shareconnect-website
```

### Firewall Configuration

```bash
# Allow HTTP and HTTPS
sudo ufw allow 'Nginx Full'
# OR for Caddy
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp

# Block direct access to Docker port (optional)
sudo ufw deny 8080/tcp
```

---

## Cloud Deployment

### AWS (EC2 + ALB)

#### 1. Launch EC2 Instance

```bash
# Ubuntu 22.04 LTS
# t2.micro or t3.micro for small traffic
# Security Group: Allow SSH (22), HTTP (80), HTTPS (443)
```

#### 2. Install Dependencies

```bash
ssh ubuntu@your-ec2-instance

# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker ubuntu

# Clone repository
git clone https://github.com/milos85vasic/ShareConnect.git
cd ShareConnect/Website
```

#### 3. Deploy

```bash
./scripts/deploy.sh
```

#### 4. Application Load Balancer (Optional)

- Create ALB with HTTPS listener
- Configure target group pointing to EC2 instance port 8080
- Attach SSL certificate from ACM

### Google Cloud Platform (Cloud Run)

#### 1. Prepare Dockerfile

Already provided at `docker/Dockerfile`

#### 2. Deploy to Cloud Run

```bash
# Authenticate
gcloud auth login

# Set project
gcloud config set project YOUR_PROJECT_ID

# Build and deploy
cd Website
gcloud run deploy shareconnect-website \
    --source . \
    --platform managed \
    --region us-central1 \
    --allow-unauthenticated
```

### DigitalOcean (Droplet + App Platform)

#### Option 1: Droplet (similar to EC2)

Follow AWS EC2 instructions above.

#### Option 2: App Platform

1. Push code to GitHub/GitLab
2. Create new App in DigitalOcean App Platform
3. Select repository and `Website/docker` directory
4. Configure build settings:
   - Dockerfile path: `Dockerfile`
   - HTTP port: 80
5. Deploy

### Heroku

**Note**: Heroku requires PORT environment variable

#### Modify Dockerfile

Add before CMD:

```dockerfile
ENV PORT=80
```

#### Deploy

```bash
# Install Heroku CLI
# Login
heroku login

# Create app
cd Website
heroku create shareconnect-website

# Set stack to container
heroku stack:set container

# Deploy
git init
git add .
git commit -m "Deploy website"
git push heroku main
```

---

## CI/CD Integration

### GitHub Actions

Create `.github/workflows/deploy-website.yml`:

```yaml
name: Deploy ShareConnect Website

on:
  push:
    branches: [main]
    paths:
      - 'Website/**'
  workflow_dispatch:

jobs:
  deploy:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout code
        uses: actions/checkout@v3

      - name: Set up Docker Buildx
        uses: docker/setup-buildx-action@v2

      - name: Log in to Docker Hub
        uses: docker/login-action@v2
        with:
          username: ${{ secrets.DOCKER_USERNAME }}
          password: ${{ secrets.DOCKER_PASSWORD }}

      - name: Build and push Docker image
        uses: docker/build-push-action@v4
        with:
          context: ./Website
          file: ./Website/docker/Dockerfile
          push: true
          tags: |
            yourusername/shareconnect-website:latest
            yourusername/shareconnect-website:${{ github.sha }}

      - name: Deploy to server
        uses: appleboy/ssh-action@master
        with:
          host: ${{ secrets.DEPLOY_HOST }}
          username: ${{ secrets.DEPLOY_USER }}
          key: ${{ secrets.DEPLOY_KEY }}
          script: |
            cd /opt/shareconnect/Website
            docker pull yourusername/shareconnect-website:latest
            ./scripts/deploy.sh
```

### GitLab CI/CD

Create `.gitlab-ci.yml`:

```yaml
stages:
  - build
  - deploy

variables:
  IMAGE_TAG: $CI_REGISTRY_IMAGE/website:$CI_COMMIT_SHA
  IMAGE_LATEST: $CI_REGISTRY_IMAGE/website:latest

build:
  stage: build
  image: docker:latest
  services:
    - docker:dind
  before_script:
    - docker login -u $CI_REGISTRY_USER -p $CI_REGISTRY_PASSWORD $CI_REGISTRY
  script:
    - cd Website
    - docker build -f docker/Dockerfile -t $IMAGE_TAG -t $IMAGE_LATEST .
    - docker push $IMAGE_TAG
    - docker push $IMAGE_LATEST
  only:
    - main

deploy:
  stage: deploy
  image: alpine:latest
  before_script:
    - apk add --no-cache openssh-client
    - eval $(ssh-agent -s)
    - echo "$SSH_PRIVATE_KEY" | ssh-add -
  script:
    - ssh $DEPLOY_USER@$DEPLOY_HOST "
        cd /opt/shareconnect/Website &&
        docker pull $IMAGE_LATEST &&
        ./scripts/deploy.sh
      "
  only:
    - main
  environment:
    name: production
```

---

## Monitoring & Maintenance

### Health Checks

#### Docker Health Check

```bash
# Check container health
docker inspect shareconnect-website --format='{{.State.Health.Status}}'

# View health check logs
docker inspect shareconnect-website --format='{{range .State.Health.Log}}{{.Output}}{{end}}'
```

#### HTTP Health Endpoint

```bash
curl http://localhost:8080/health
# Should return: healthy
```

### Monitoring with Prometheus

**docker-compose-monitoring.yml:**

```yaml
version: '3.8'

services:
  shareconnect-website:
    image: shareconnect-website:latest
    container_name: shareconnect-website
    restart: unless-stopped
    ports:
      - "8080:80"
    labels:
      - "prometheus-job=shareconnect-website"

  nginx-exporter:
    image: nginx/nginx-prometheus-exporter:latest
    command:
      - '-nginx.scrape-uri=http://shareconnect-website/nginx_status'
    ports:
      - "9113:9113"
    depends_on:
      - shareconnect-website

  prometheus:
    image: prom/prometheus:latest
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
      - prometheus_data:/prometheus
    ports:
      - "9090:9090"

  grafana:
    image: grafana/grafana:latest
    volumes:
      - grafana_data:/var/lib/grafana
    ports:
      - "3000:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin

volumes:
  prometheus_data:
  grafana_data:
```

### Log Management

#### View Logs

```bash
# Docker logs
./scripts/logs.sh

# Nginx access logs
docker exec shareconnect-website tail -f /var/log/nginx/access.log

# Nginx error logs
docker exec shareconnect-website tail -f /var/log/nginx/error.log
```

#### Log Rotation

Docker handles log rotation automatically, but you can configure it:

**/etc/docker/daemon.json:**

```json
{
  "log-driver": "json-file",
  "log-opts": {
    "max-size": "10m",
    "max-file": "3"
  }
}
```

Restart Docker after changes:

```bash
sudo systemctl restart docker
```

### Backups

#### Backup Docker Image

```bash
# Save image to tar
docker save shareconnect-website:latest | gzip > shareconnect-website-backup.tar.gz

# Restore image from tar
docker load < shareconnect-website-backup.tar.gz
```

#### Backup Website Files

```bash
# Backup public directory
tar -czf website-backup-$(date +%Y%m%d).tar.gz Website/public/

# Restore
tar -xzf website-backup-YYYYMMDD.tar.gz
```

### Updates

#### Update Website Content

```bash
# 1. Update files in Website/public/
# 2. Rebuild and deploy
cd Website
./scripts/deploy.sh
```

#### Update Dependencies

```bash
# Pull latest Nginx Alpine image
docker pull nginx:alpine

# Rebuild
./scripts/build.sh
./scripts/deploy.sh
```

### Performance Optimization

#### Enable Brotli Compression (Optional)

Modify Dockerfile to use nginx with Brotli:

```dockerfile
FROM fholzer/nginx-brotli:latest
# ... rest of Dockerfile
```

#### CDN Integration

For global distribution, use a CDN:

1. **Cloudflare**: Add site to Cloudflare DNS
2. **CloudFront**: Create distribution pointing to origin
3. **Fastly**: Configure service pointing to backend

---

## Troubleshooting

### Container Won't Start

```bash
# Check logs
docker logs shareconnect-website

# Verify port availability
sudo netstat -tulpn | grep 8080

# Try different port
./scripts/start.sh 3000
```

### Website Not Accessible

```bash
# Test from inside container
docker exec shareconnect-website curl http://localhost/health

# Check Nginx status
docker exec shareconnect-website nginx -t

# Restart container
./scripts/restart.sh
```

### Build Failures

```bash
# Clean Docker cache
docker builder prune -a

# Rebuild without cache
docker build --no-cache -f docker/Dockerfile -t shareconnect-website .
```

### High Memory Usage

```bash
# Check container resources
docker stats shareconnect-website

# Limit container resources
docker update --memory="256m" --memory-swap="512m" shareconnect-website
```

---

## Security Checklist

- [ ] SSL/TLS configured (Let's Encrypt or other)
- [ ] Security headers enabled (CSP, HSTS, etc.)
- [ ] Firewall configured (only 80, 443 accessible)
- [ ] Regular updates scheduled
- [ ] Logs monitored for suspicious activity
- [ ] Backups automated and tested
- [ ] Container resources limited
- [ ] Non-root user in container (already configured)
- [ ] No sensitive data in logs

---

## Performance Benchmarks

Expected performance metrics:

- **Page Load Time**: < 1s (with CDN < 500ms)
- **First Contentful Paint**: < 1s
- **Time to Interactive**: < 2s
- **Lighthouse Score**: 95+
- **Memory Usage**: < 50MB per container
- **CPU Usage**: < 5% under normal load
- **Concurrent Connections**: 1000+ (Nginx default)

---

## Support & Resources

- **Documentation**: `/Website/WEBSITE_README.md`
- **Main Project**: [ShareConnect Repository](https://github.com/milos85vasic/ShareConnect)
- **Issues**: [GitHub Issues](https://github.com/milos85vasic/ShareConnect/issues)
- **Wiki**: [Project Wiki](https://deepwiki.com/vasic-digital/ShareConnect)

---

**Last Updated**: 2025-10-25
**Version**: 1.0.0
