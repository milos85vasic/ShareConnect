FROM adoptopenjdk/openjdk17:alpine
LABEL project="ShareConnect"
LABEL version="1.0.0"

# Placeholder Dockerfile for Snyk container scanning
RUN mkdir -p /app
WORKDIR /app

COPY . /app

# Placeholder build step
RUN echo "ShareConnect container build placeholder"

CMD ["sh", "-c", "echo 'ShareConnect container running'"]