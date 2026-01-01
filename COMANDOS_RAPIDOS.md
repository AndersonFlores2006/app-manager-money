# 🚀 Comandos Rápidos para Distribución

## Crear APK Firmado (3 comandos)

```bash
# 1. Crear keystore (solo primera vez)
keytool -genkeypair -v -keystore release.keystore -alias gestor-money -keyalg RSA -keysize 2048 -validity 10000 -storepass gestor2025! -keypass gestor2025! -dname "CN=Anderson Flores, OU=Mobile, O=Personal, L=Lima, ST=Lima, C=PE"

# 2. Generar APK
./gradlew assembleRelease

# 3. Verificar APK generado
ls -lh app/build/outputs/apk/release/app-release.apk
```

## Script Automatizado (1 comando)

```bash
# Hace TODO automáticamente: keystore, build, firma, verificación
./build-release.sh
```

## Subir a GitHub Releases

```bash
# El APK estará listo en:
# app/build/outputs/apk/release/app-release.apk
#
# Sube este archivo a:
# https://github.com/AndersonFlores2006/app-manager-money/releases
```

## Verificar Instalación

```bash
# Verificar firma (debe mostrar "jar verified.")
jarsigner -verify app/build/outputs/apk/release/app-release.apk

# Instalar en dispositivo conectado (sin conflicto de paquete)
adb install -r app/build/outputs/apk/release/app-release.apk
```

## Solución Rápida de Problemas

```bash
# Si hay problemas con Java
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk

# Limpiar y reconstruir
./gradlew clean && ./gradlew assembleRelease

# Ver logs detallados
./gradlew assembleRelease --info
```

---

📖 **Documentación completa:** [GUIA_CREAR_FIRMAR_APK.md](documentation/GUIA_CREAR_FIRMAR_APK.md)