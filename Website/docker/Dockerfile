# ShareConnect Website Dockerfile
# Multi-stage build for optimized production image

# Production stage with Nginx
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

# Copy website files from root directory
COPY . /usr/share/nginx/html/

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
