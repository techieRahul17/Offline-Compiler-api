FROM eclipse-temurin:17-jdk-jammy

# Install languages
RUN apt-get update && apt-get install -y \
    gcc \
    g++ \
    python3 \
    nodejs \
    npm \
    && rm -rf /var/lib/apt/lists/*

# Create non-root user (CRITICAL)
RUN useradd -m sandboxuser
USER sandboxuser

WORKDIR /sandbox
