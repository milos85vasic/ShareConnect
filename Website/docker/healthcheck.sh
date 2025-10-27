#!/bin/bash
# ShareConnect Website - Health Check Script

# Check if nginx is running
if ! pgrep -x "nginx" > /dev/null; then
    echo "Nginx is not running"
    exit 1
fi

# Check if the website is accessible
if ! curl -f http://localhost/ > /dev/null 2>&1; then
    echo "Website health check failed"
    exit 1
fi

echo "Health check passed"
exit 0
