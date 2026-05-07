#!/bin/bash

# ==============================================================================
# PersonalFood - Startup Script for Linux/Mac
# ==============================================================================

set -e

echo "🚀 PersonalFood - Startup Script"
echo "=================================="

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if .env exists
if [ ! -f ".env" ]; then
    echo -e "${YELLOW}⚠️  .env file not found! Creating from template...${NC}"
    cat > .env << 'EOF'
# PostgreSQL Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/personal_food
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=password

# Mercado Pago
MP_TOKEN=TEST-xxx

# Email
EMAIL_PASSWORD=xxx

# Google Gemini
GEMINI_API_KEY=xxx

# Google OAuth2
GOOGLE_CLIENT_ID=xxx
GOOGLE_CLIENT_SECRET=xxx
EOF
    echo -e "${GREEN}✅ .env created. Please update it with your actual values!${NC}"
fi

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo -e "${RED}❌ Docker is not installed. Please install Docker first.${NC}"
    exit 1
fi

# Check if Docker daemon is running
if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}❌ Docker daemon is not running. Please start Docker.${NC}"
    exit 1
fi

# Check if PostgreSQL container exists
if docker ps -a --format '{{.Names}}' | grep -q "^personalfood-db$"; then
    echo -e "${YELLOW}⚠️  PostgreSQL container already exists${NC}"
    if docker ps --format '{{.Names}}' | grep -q "^personalfood-db$"; then
        echo -e "${GREEN}✅ PostgreSQL is running${NC}"
    else
        echo "Starting existing PostgreSQL container..."
        docker start personalfood-db
        echo -e "${GREEN}✅ PostgreSQL started${NC}"
    fi
else
    echo "Creating PostgreSQL container..."
    docker run --name personalfood-db \
        -e POSTGRES_USER=postgres \
        -e POSTGRES_PASSWORD=password \
        -e POSTGRES_DB=personal_food \
        -p 5432:5432 \
        -v personalfood_pgdata:/var/lib/postgresql/data \
        -d postgres:15-alpine
    echo -e "${GREEN}✅ PostgreSQL created and started${NC}"
    sleep 5
fi

# Check database connection
echo "Testing database connection..."
if docker exec personalfood-db pg_isready -U postgres > /dev/null 2>&1; then
    echo -e "${GREEN}✅ Database is ready${NC}"
else
    echo -e "${RED}❌ Cannot connect to database${NC}"
    exit 1
fi

# Build application
echo ""
echo "Building application..."
./mvnw clean package -DskipTests -q
echo -e "${GREEN}✅ Application built successfully${NC}"

# Start Spring Boot
echo ""
echo "Starting Spring Boot application..."
java -jar ./target/PersonalFood-0.0.1-SNAPSHOT.jar

# Note: Script will run the Spring Boot app in foreground
# Use Ctrl+C to stop
# To stop PostgreSQL: docker stop personalfood-db
