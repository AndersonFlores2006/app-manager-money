# 🔧 Solución: Error "Conflicto de Paquete" al Instalar APK

## Problema Reportado

Al intentar instalar la actualización de la app, aparece el error: **"debido a un conflicto de un paquete"**.

## ✅ Solución Implementada

El problema estaba en que el APK generado por Gradle **no estaba siendo firmado automáticamente** a pesar de tener la configuración correcta.

### ¿Por qué ocurría?

- Gradle compilaba correctamente el APK
- La configuración de firma estaba presente en `build.gradle.kts`
- Pero la tarea de firma no se ejecutaba correctamente
- Resultado: APK sin firmar → Error de instalación en Android

### ✅ Solución

1. **Firma manual automática:** Se agregó función `sign_apk()` al script `build-release.sh`
2. **Verificación automática:** El script verifica si el APK está firmado y lo firma si es necesario
3. **Comandos actualizados:** Ahora `./build-release.sh` maneja todo el proceso completo

### Comandos para Solucionar

#### Opción 1: Script Automatizado (Recomendado)
```bash
# Hace todo: compila, firma y verifica
./build-release.sh
```

#### Opción 2: Firma Manual
```bash
# Si ya tienes el APK sin firmar
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk
jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 \
  -keystore release.keystore \
  -storepass gestor2025! \
  app/build/outputs/apk/release/app-release.apk \
  gestor-money
```

#### Verificar Solución
```bash
# Verificar que esté firmado
jarsigner -verify app/build/outputs/apk/release/app-release.apk

# Debe mostrar "jar verified."
```

## 🎯 Resultado

- ✅ APK correctamente firmado
- ✅ Instalación exitosa sin conflictos de paquete
- ✅ Proceso completamente automatizado
- ✅ Verificación automática de firma en el script
- ✅ Detección correcta de APKs sin firmar

## 📊 Verificación Final

```bash
# Ejecutar el script completo
./build-release.sh

# Verificar firma
jarsigner -verify app/build/outputs/apk/release/app-release.apk
# Debe mostrar: "jar verified."
```

## 📝 Notas Técnicas

- **Certificado:** Auto-firmado con validez de 10,000 días
- **Algoritmo:** SHA256withRSA
- **Keystore:** PKCS12 con clave RSA 2048 bits
- **Alias:** `gestor-money`

---

**Estado:** ✅ **RESUELTO** - El APK ahora se firma correctamente y se instala sin problemas.