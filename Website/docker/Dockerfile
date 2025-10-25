# ShareConnect Website Dockerfile
# Multi-stage build for optimized production image

# Stage 1: Build stage (if needed for processing assets)
FROM node:18-alpine AS builder

WORKDIR /app

# Copy package files if you need to process assets
# COPY package*.json ./
# RUN npm ci --only=production

# Copy website files
COPY public/ ./public/

# Stage 2: Production stage with Nginx
FROM nginx:alpine

# Install additional tools
RUN apk add --no-cache \
    curl \
    bash \
    && rm -rf /var/cache/apk/*

# Remove default Nginx config
RUN rm -rf /usr/share/nginx/html/*

# Copy custom Nginx configuration
COPY docker/nginx.conf /etc/nginx/nginx.conf
COPY docker/default.conf /etc/nginx/conf.d/default.conf

# Copy website files from builder stage
COPY --from=builder /app/public /usr/share/nginx/html

# Add health check script
COPY docker/healthcheck.sh /healthcheck.sh
RUN chmod +x /healthcheck.sh

# Set permissions
RUN chown -R nginx:nginx /usr/share/nginx/html && \
    chmod -R 755 /usr/share/nginx/html

# Add labels for metadata
LABEL maintainer="ShareConnect Team"
LABEL description="ShareConnect Website - Unified Self-Hosted Service Management"
LABEL version="1.0.0"

# Expose port
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD /healthcheck.sh

# Start Nginx
CMD ["nginx", "-g", "daemon off;"]
