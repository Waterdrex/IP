# ==============================================================================
# Production Dockerfile for TCET Project Management System
# Supports Render, Railway, Fly.io, AWS App Runner, or any Docker/VPS host
# ==============================================================================
FROM eclipse-temurin:17-jdk-alpine AS runtime

WORKDIR /app

# Install bash and curl for healthchecks
RUN apk add --no-cache bash curl

# Copy source code, frontend assets, and initial data
COPY src/ ./src/
COPY public/ ./public/
COPY data/ ./data/

# Compile pure Java backend into bin directory (Zero external dependency required)
RUN mkdir -p bin && \
    javac -encoding UTF-8 -d bin \
    src/com/tcet/pms/model/*.java \
    src/com/tcet/pms/repository/*.java \
    src/com/tcet/pms/service/*.java \
    src/com/tcet/pms/util/*.java \
    src/com/tcet/pms/controller/*.java \
    src/com/tcet/pms/Main.java

# Default container listening port
ENV PORT=8080
EXPOSE 8080

# Health check to ensure service is responding
HEALTHCHECK --interval=30s --timeout=5s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:${PORT:-8080}/api/health || exit 1

# Launch application using dynamic cloud PORT binding
CMD ["sh", "-c", "java -cp bin com.tcet.pms.Main ${PORT:-8080}"]
