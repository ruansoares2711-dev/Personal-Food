#!/usr/bin/env pwsh
# Script para rodar a aplicação PersonalFood localmente no PowerShell
# Uso: .\run-local.ps1

Write-Host ""
Write-Host "======================================" -ForegroundColor Cyan
Write-Host "PersonalFood - Local Development" -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan
Write-Host ""

# Verificar se Docker está instalado
try {
    docker --version | Out-Null
    Write-Host "✅ Docker encontrado" -ForegroundColor Green
} catch {
    Write-Host "❌ Docker não encontrado! Instale o Docker primeiro." -ForegroundColor Red
    exit 1
}

# Verificar se PostgreSQL container já existe
$containerExists = docker ps -a 2>/dev/null | Select-String "personalfood-db"

if ($containerExists) {
    Write-Host "ℹ️  Container PostgreSQL já existe. Iniciando..." -ForegroundColor Yellow
    docker start personalfood-db
} else {
    Write-Host "🚀 Criando container PostgreSQL..." -ForegroundColor Cyan
    docker run --name personalfood-db `
        -e POSTGRES_USER=postgres `
        -e POSTGRES_PASSWORD=password `
        -e POSTGRES_DB=personal_food `
        -p 5432:5432 `
        -d postgres:15-alpine
}

Write-Host "⏳ Aguardando PostgreSQL ficar pronto (20 segundos)..." -ForegroundColor Yellow
Start-Sleep -Seconds 20

Write-Host ""
Write-Host "🚀 Iniciando Spring Boot com Maven Wrapper..." -ForegroundColor Cyan
Write-Host ""

# Rodar a aplicação
& .\mvnw.cmd spring-boot:run

Write-Host ""
Write-Host "======================================" -ForegroundColor Green
Write-Host "✅ Aplicação rodando em http://localhost:8080" -ForegroundColor Green
Write-Host "======================================" -ForegroundColor Green
Write-Host ""
