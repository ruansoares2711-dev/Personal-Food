@echo off
REM Script para rodar a aplicação PersonalFood localmente
REM Usar este script no PowerShell: .\run-local.ps1

echo.
echo ======================================
echo PersonalFood - Local Development
echo ======================================
echo.

REM Verificar se Docker está instalado
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Docker não encontrado! Instale o Docker primeiro.
    exit /b 1
)

echo ✅ Docker encontrado

REM Verificar se PostgreSQL container já existe
docker ps -a | find "personalfood-db" >nul
if %errorlevel% equ 0 (
    echo ℹ️  Container PostgreSQL já existe. Iniciando...
    docker start personalfood-db
) else (
    echo 🚀 Criando container PostgreSQL...
    docker run --name personalfood-db ^
        -e POSTGRES_USER=postgres ^
        -e POSTGRES_PASSWORD=password ^
        -e POSTGRES_DB=personal_food ^
        -p 5432:5432 ^
        -d postgres:15-alpine
)

echo ⏳ Aguardando PostgreSQL ficar pronto (20 segundos)...
timeout /t 20 /nobreak

echo.
echo 🚀 Iniciando Spring Boot com Maven Wrapper...
call mvnw.cmd spring-boot:run

echo.
echo ======================================
echo ✅ Aplicação rodando em http://localhost:8080
echo ======================================
echo.
