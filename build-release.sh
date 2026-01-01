#!/bin/bash

# Script para automatizar la creación y firma de APK de release
# Uso: ./build-release.sh [version]

set -e  # Salir si hay algún error

# Configuración
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
KEYSTORE_FILE="$PROJECT_DIR/release.keystore"
APK_OUTPUT_DIR="$PROJECT_DIR/app/build/outputs/apk/release"
APK_FILE="$APK_OUTPUT_DIR/app-release.apk"

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Función para imprimir mensajes coloreados
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

# Verificar requisitos
check_requirements() {
    print_info "Verificando requisitos..."

    # Verificar Java
    if ! command -v java &> /dev/null; then
        print_error "Java no está instalado"
        exit 1
    fi

    # Verificar que estamos usando Java 21
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$JAVA_VERSION" != "21" ]; then
        print_warning "Java versión $JAVA_VERSION detectada. Recomendado: Java 21"
        print_info "Configurando JAVA_HOME a Java 21..."
        export JAVA_HOME=/usr/lib/jvm/java-21-openjdk
    fi

    # Verificar Gradle
    if ! command -v ./gradlew &> /dev/null; then
        print_error "Gradle wrapper no encontrado"
        exit 1
    fi

    print_success "Requisitos verificados"
}

# Verificar keystore
check_keystore() {
    if [ ! -f "$KEYSTORE_FILE" ]; then
        print_error "Keystore no encontrado: $KEYSTORE_FILE"
        print_info "Ejecuta primero: keytool -genkeypair -v -keystore release.keystore -alias gestor-money -keyalg RSA -keysize 2048 -validity 10000"
        exit 1
    fi
    print_success "Keystore encontrado"
}

# Limpiar builds anteriores
clean_previous_builds() {
    print_info "Limpiando builds anteriores..."
    ./gradlew clean > /dev/null 2>&1
    print_success "Builds anteriores limpiados"
}

# Compilar APK de release
build_release_apk() {
    print_info "Compilando APK de release..."
    print_info "Esto puede tomar varios minutos..."

    if ./gradlew assembleRelease --no-daemon > build.log 2>&1; then
        print_success "APK compilado exitosamente"
    else
        print_error "Error durante la compilación"
        echo "Revisa build.log para más detalles"
        exit 1
    fi
}

# Firmar APK (siempre firmar manualmente para asegurar consistencia)
sign_apk() {
    print_info "Firmando APK manualmente..."

    # Verificar que el keystore existe
    if [ ! -f "$PROJECT_DIR/release.keystore" ]; then
        print_error "Keystore no encontrado: $PROJECT_DIR/release.keystore"
        exit 1
    fi

    export JAVA_HOME=/usr/lib/jvm/java-21-openjdk

    # Firmar con parámetros explícitos
    if jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 \
        -keystore "$PROJECT_DIR/release.keystore" \
        -storepass gestor2025! \
        -keypass gestor2025! \
        "$APK_FILE" gestor-money > /dev/null 2>&1; then

        # Verificar que la firma funcionó
        if unzip -l "$APK_FILE" | grep -q "META-INF/GESTOR-M\."; then
            print_success "APK firmado exitosamente"
        else
            print_error "La firma no se aplicó correctamente"
            exit 1
        fi
    else
        print_error "Error al ejecutar jarsigner"
        exit 1
    fi
}

# Verificar APK generado
verify_apk() {
    if [ ! -f "$APK_FILE" ]; then
        print_error "APK no encontrado en: $APK_FILE"
        exit 1
    fi

    APK_SIZE=$(du -h "$APK_FILE" | cut -f1)
    print_success "APK generado: $APK_FILE ($APK_SIZE)"

    # Firmar si es necesario
    sign_apk
}

# Mostrar información del APK
show_apk_info() {
    echo
    print_info "=== INFORMACIÓN DEL APK ==="
    echo "📱 Archivo: $APK_FILE"
    echo "📏 Tamaño: $(du -h "$APK_FILE" | cut -f1)"
    echo "📅 Fecha: $(date -r "$APK_FILE")"
    echo
    print_info "=== PRÓXIMOS PASOS ==="
    echo "1. Sube el APK a GitHub Releases:"
    echo "   - Ve a: https://github.com/AndersonFlores2006/app-manager-money/releases"
    echo "   - Crea un nuevo release"
    echo "   - Sube: $APK_FILE"
    echo
    echo "2. O instala directamente:"
    echo "   adb install -r $APK_FILE"
    echo
}

# Función principal
main() {
    echo
    echo "========================================"
    echo "🚀 GESTOR DE DINERO - BUILD RELEASE"
    echo "========================================"
    echo

    # Verificar parámetros
    if [ "$1" = "--help" ] || [ "$1" = "-h" ]; then
        echo "Uso: $0 [opciones]"
        echo
        echo "Opciones:"
        echo "  --help, -h    Mostrar esta ayuda"
        echo "  --clean       Limpiar builds anteriores"
        echo "  --verify      Solo verificar APK existente"
        echo
        echo "Ejemplos:"
        echo "  $0              # Build completo"
        echo "  $0 --clean      # Limpiar y build"
        echo "  $0 --verify     # Solo verificar"
        exit 0
    fi

    if [ "$1" = "--verify" ]; then
        verify_apk
        show_apk_info
        exit 0
    fi

    # Verificar requisitos
    check_requirements

    # Verificar keystore
    check_keystore

    # Limpiar si se solicita
    if [ "$1" = "--clean" ]; then
        clean_previous_builds
    fi

    # Build
    build_release_apk

    # Verificar y firmar si es necesario
    verify_apk

    # Mostrar información
    show_apk_info

    print_success "¡Build completado exitosamente! 🎉"
    echo
}

# Ejecutar función principal
main "$@"