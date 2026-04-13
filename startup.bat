@echo off
REM ==============================================================================
REM PersonalFood - Startup Script for Windows
REM ==============================================================================

setlocal enabledelayedexpansion

echo.
echo 🚀 PersonalFood - Startup Script
echo ==================================
echo.

REM Check if .env exists
if not exist ".env" (
    echo ⚠️  .env file not found! Creating from template...
    (
        echo # PostgreSQL Configuration
        echo SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/personal_food
        echo SPRING_DATASOURCE_USERNAME=postgres
        echo SPRING_DATASOURCE_PASSWORD=password
        echo.
        echo # Mercado Pago
        echo MP_TOKEN=TEST-xxx
        echo.
        echo # Email
        echo EMAIL_PASSWORD=xxx
        echo.
        echo # Google Gemini
        echo GEMINI_API_KEY=xxx
        echo.
        echo # Google OAuth2
        echo GOOGLE_CLIENT_ID=xxx
        echo GOOGLE_CLIENT_SECRET=xxx
    ) > .env
    echo ✅ .env created. Please update it with your actual values!
    echo.
)

REM Check if Docker is installed
docker --version > nul 2>&1
if errorlevel 1 (
    echo ❌ Docker is not installed. Please install Docker Desktop for Windows.
    pause
    exit /b 1
)

REM Check if PostgreSQL container exists
docker ps -a --format "{{.Names}}" 2>nul | find "personalfood-db" > nul
if errorlevel 1 (
    echo Creating PostgreSQL container...
    docker run --name personalfood-db ^
        -e POSTGRES_USER=postgres ^
        -e POSTGRES_PASSWORD=password ^
        -e POSTGRES_DB=personal_food ^
        -p 5432:5432 ^
        -d postgres:15-alpine
    if errorlevel 1 (
        echo ❌ Failed to create PostgreSQL container
        pause
        exit /b 1
    )
    echo ✅ PostgreSQL created and started
    timeout /t 5 /nobreak
) else (
    echo ⚠️  PostgreSQL container already exists
    docker ps --format "{{.Names}}" 2>nul | find "personalfood-db" > nul
    if errorlevel 1 (
        echo Starting existing PostgreSQL container...
        docker start personalfood-db > nul
        if errorlevel 1 (
            echo ❌ Failed to start PostgreSQL
            pause
            exit /b 1
        )
        echo ✅ PostgreSQL started
        timeout /t 3 /nobreak
    ) else (
        echo ✅ PostgreSQL is running
    )
)

REM Test database connection
echo Testing database connection...
docker exec personalfood-db pg_isready -U postgres > nul 2>&1
if errorlevel 1 (
    echo ❌ Cannot connect to database
    pause
    exit /b 1
)
echo ✅ Database is ready

REM Build application
echo.
echo Building application...
call mvnw.cmd clean package -DskipTests -q
if errorlevel 1 (
    echo ❌ Build failed
    pause
    exit /b 1
)
echo ✅ Application built successfully

REM Start Spring Boot
echo.
echo Starting Spring Boot application...
java -jar .\target\PersonalFood-0.0.1-SNAPSHOT.jar

REM Note: Script will run the Spring Boot app in foreground
REM Use Ctrl+C to stop
REM To stop PostgreSQL: docker stop personalfood-db

pause
