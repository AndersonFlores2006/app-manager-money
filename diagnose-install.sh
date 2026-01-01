#!/bin/bash

# Script de diagnóstico para problemas de instalación de APK
# Uso: ./diagnose-install.sh

set -e

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APK_FILE="$PROJECT_DIR/app/build/outputs/apk/release/app-release.apk"

# Colores
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

print_header() {
    echo -e "\n${BLUE}=== $1 ===${NC}"
}

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Verificar APK
check_apk() {
    print_header "VERIFICANDO APK"
    if [ ! -f "$APK_FILE" ]; then
        print_error "APK no encontrado: $APK_FILE"
        exit 1
    fi

    APK_SIZE=$(du -h "$APK_FILE" | cut -f1)
    print_success "APK encontrado: $APK_SIZE"

    # Verificar firma
    print_info "Verificando firma..."
    if unzip -l "$APK_FILE" | grep -q "META-INF/.*\.\(SF\|RSA\|DSA\)$"; then
        print_success "APK firmado correctamente"
    else
        print_error "APK NO ESTÁ FIRMADO"
        exit 1
    fi
}

# Verificar dispositivo
check_device() {
    print_header "VERIFICANDO DISPOSITIVO"

    # Verificar ADB
    if ! command -v adb &> /dev/null; then
        print_error "ADB no está instalado o no está en PATH"
        exit 1
    fi

    # Verificar conexión
    DEVICES=$(adb devices | grep -v "List" | grep -v "^$" | wc -l)
    if [ "$DEVICES" -eq 0 ]; then
        print_error "No hay dispositivos conectados"
        print_info "Asegúrate de:"
        echo "  1. Conectar el dispositivo por USB"
        echo "  2. Habilitar 'Depuración USB' en Ajustes > Opciones de desarrollador"
        echo "  3. Aceptar el diálogo de autorización en el dispositivo"
        exit 1
    fi

    print_success "Dispositivo conectado"

    # Información del dispositivo
    MODEL=$(adb shell getprop ro.product.model 2>/dev/null | tr -d '\r')
    ANDROID_VER=$(adb shell getprop ro.build.version.release 2>/dev/null | tr -d '\r')

    print_info "Modelo: $MODEL"
    print_info "Android: $ANDROID_VER"
}

# Verificar app instalada
check_installed_app() {
    print_header "VERIFICANDO APP INSTALADA"

    PACKAGE_EXISTS=$(adb shell pm list packages | grep -c "com.example.gestor_money" || true)

    if [ "$PACKAGE_EXISTS" -gt 0 ]; then
        print_warning "La app ya está instalada"

        # Obtener información de la app instalada
        print_info "Información de la app instalada:"
        adb shell dumpsys package com.example.gestor_money | grep -E "(versionName|versionCode|signatures)" | head -10

        print_info "Opciones:"
        echo "  1. Desinstalar la app actual: adb uninstall com.example.gestor_money"
        echo "  2. Forzar instalación: adb install -r -d $APK_FILE"
    else
        print_success "La app no está instalada"
    fi
}

# Intentar instalación
try_install() {
    print_header "INTENTANDO INSTALACIÓN"

    print_info "Limpiando logs anteriores..."
    adb logcat -c 2>/dev/null || true

    print_info "Intentando instalar APK..."

    # Instalar y capturar salida
    INSTALL_OUTPUT=$(adb install -r "$APK_FILE" 2>&1)
    INSTALL_EXIT_CODE=$?

    echo "$INSTALL_OUTPUT"

    if [ $INSTALL_EXIT_CODE -eq 0 ]; then
        print_success "¡Instalación exitosa!"
        return 0
    else
        print_error "Instalación fallida"

        # Mostrar logs relevantes
        print_header "LOGS DEL SISTEMA"
        adb logcat -d -s "PackageManager" "InstallPackage" 2>/dev/null | tail -20 || true

        print_header "POSIBLES SOLUCIONES"

        if echo "$INSTALL_OUTPUT" | grep -qi "conflict"; then
            print_error "CONFLICTO DE PAQUETE detectado"
            echo "  Solución: Desinstalar la app anterior"
            echo "  Comando: adb uninstall com.example.gestor_money"
        fi

        if echo "$INSTALL_OUTPUT" | grep -qi "signature"; then
            print_error "PROBLEMA DE FIRMA detectado"
            echo "  Posibles causas:"
            echo "  - La app instalada tiene diferente firma"
            echo "  - El APK no está correctamente firmado"
        fi

        if echo "$INSTALL_OUTPUT" | grep -qi "permission"; then
            print_error "PROBLEMA DE PERMISOS detectado"
            echo "  Solución: Habilitar instalación de apps desconocidas"
        fi

        return 1
    fi
}

# Función principal
main() {
    echo "🔍 DIAGNÓSTICO DE INSTALACIÓN DE APK"
    echo "===================================="

    check_apk
    check_device
    check_installed_app

    echo
    read -p "¿Quieres intentar instalar el APK ahora? (y/N): " -n 1 -r
    echo

    if [[ $REPLY =~ ^[Yy]$ ]]; then
        try_install
    else
        print_info "Instalación cancelada"
        print_info "Comandos útiles:"
        echo "  - Desinstalar app: adb uninstall com.example.gestor_money"
        echo "  - Instalar APK: adb install -r app/build/outputs/apk/release/app-release.apk"
        echo "  - Ver logs: adb logcat -s PackageManager"
    fi
}

main "$@"