# Guía de Creación y Distribución de APK Firmado

Esta guía explica cómo crear un keystore, firmar tu aplicación Android y distribuirla.

## 📋 Requisitos Previos

- JDK 21 instalado (`java -version` debe mostrar versión 21)
- Android Studio o Gradle instalado
- Proyecto Android configurado

## 🔐 Paso 1: Crear el Keystore

### Opción A: Usando Comandos (Recomendado)

```bash
# Navega al directorio del proyecto
cd /ruta/a/tu/proyecto

# Crea el keystore con parámetros no interactivos
keytool -genkeypair -v \
  -keystore release.keystore \
  -alias tu-app-alias \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -storepass TU_PASSWORD_SEGURO \
  -keypass TU_PASSWORD_SEGURO \
  -dname "CN=Tu Nombre, OU=Unidad, O=Organización, L=Ciudad, ST=Estado, C=PAIS"
```

**Parámetros importantes:**
- `-validity 10000`: Válido por 10,000 días (~27 años)
- `-keysize 2048`: Tamaño de clave recomendado
- `-storepass` y `-keypass`: Contraseñas (mínimo 6 caracteres)

### Opción B: Usando Android Studio

1. Ve a **Build → Generate Signed APK/Bundle**
2. Selecciona **APK**
3. Haz clic en **Create new...** para crear un nuevo keystore
4. Llena los campos requeridos
5. El keystore se creará automáticamente

## ⚙️ Paso 2: Configurar el Proyecto

### Archivo keystore.properties

Crea el archivo `keystore.properties` en la raíz del proyecto:

```properties
storeFile=release.keystore
storePassword=TU_PASSWORD_SEGURO
keyAlias=tu-app-alias
keyPassword=TU_PASSWORD_SEGURO
```

### Configuración en build.gradle.kts

Ya está configurado automáticamente en tu proyecto:

```kotlin
// En build.gradle.kts (app level)
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(keystorePropertiesFile.inputStream())
}

android {
    signingConfigs {
        create("release") {
            if (keystorePropertiesFile.exists()) {
                storeFile = rootProject.file(keystoreProperties.getProperty("storeFile"))
                storePassword = keystoreProperties.getProperty("storePassword")
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            // ... otras configuraciones
        }
    }
}
```

## 🚀 Paso 3: Generar el APK de Release

### Opción A: Script Automatizado (Recomendado)

```bash
# Script completo que verifica todo y genera el APK
./build-release.sh

# Opciones disponibles:
./build-release.sh --help    # Ver ayuda
./build-release.sh --clean   # Limpiar builds anteriores y compilar
./build-release.sh --verify  # Solo verificar APK existente
```

### Opción B: Comandos Manuales

```bash
# Asegúrate de estar en el directorio del proyecto
cd /ruta/a/tu/proyecto

# Genera el APK de release
./gradlew assembleRelease
```

### Opción C: Comando con Java 21 (si hay problemas)

```bash
# Si tienes problemas con versiones de Java
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk
./gradlew assembleRelease --no-daemon
```

### Verificación del APK Generado

```bash
# Lista los APKs generados
find . -name "*.apk" -type f

# Verifica el tamaño del APK
ls -lh app/build/outputs/apk/release/app-release.apk
```

## 📦 Paso 4: Distribuir la Aplicación

### Opción A: GitHub Releases (Recomendado)

1. **Compila el APK:**
   ```bash
   ./gradlew assembleRelease
   ```

2. **Ve a GitHub:**
   - Ve a tu repositorio
   - Clic en "Releases" en la barra lateral
   - Clic en "Create a new release"

3. **Configura el Release:**
   - **Tag:** `v1.2.0` (incrementa la versión)
   - **Title:** `Versión 1.2.0`
   - **Description:** Describe los cambios
   - **Attach:** `app/build/outputs/apk/release/app-release.apk`

4. **Publica:** Clic en "Publish release"

### Opción B: Distribución Directa

1. **Transfiere el APK al dispositivo:**
   ```bash
   # Usando ADB (si tienes el dispositivo conectado)
   adb push app/build/outputs/apk/release/app-release.apk /sdcard/Download/

   # O transfiere manualmente vía USB/correo/etc
   ```

2. **Instala en el dispositivo:**
   - Ve a Configuración → Aplicaciones
   - Habilita "Instalar apps desconocidas" para tu fuente
   - Abre el APK desde el administrador de archivos

## 🔍 Paso 5: Verificar la Instalación

### Verificar Firma del APK

```bash
# Verifica que el APK esté firmado correctamente
jarsigner -verify -verbose app/build/outputs/apk/release/app-release.apk
```

### Información del Certificado

```bash
# Ver detalles del certificado
keytool -printcert -jarfile app/build/outputs/apk/release/app-release.apk
```

### Probar la App

1. Instala el APK en un dispositivo Android
2. Verifica que:
   - La app se instala sin errores
   - Las funcionalidades críticas funcionan
   - La versión se muestra correctamente en Configuración
   - Las actualizaciones automáticas funcionan

## 🛠️ Comandos Útiles para Mantenimiento

### Gestionar el Keystore

```bash
# Listar contenido del keystore
keytool -list -v -keystore release.keystore -storepass TU_PASSWORD

# Cambiar contraseña del keystore
keytool -storepasswd -keystore release.keystore

# Cambiar contraseña de una clave específica
keytool -keypasswd -alias tu-app-alias -keystore release.keystore

# Crear backup del keystore
cp release.keystore release.keystore.backup
```

### Limpiar Builds

```bash
# Limpiar builds anteriores
./gradlew clean

# Limpiar cache de Gradle
./gradlew cleanBuildCache
```

## 🚨 Consideraciones de Seguridad

### ❌ NUNCA hagas esto:
- **No subas el keystore a Git** (ya está en .gitignore)
- **No compartas las contraseñas**
- **No uses contraseñas débiles**

### ✅ Mejores Prácticas:
- **Guarda el keystore en lugar seguro** (disco duro externo, caja fuerte digital)
- **Haz backups regulares** del keystore
- **Usa contraseñas fuertes** (mínimo 8 caracteres, combinación de letras, números, símbolos)
- **Documenta tus procesos** pero no las credenciales

## 🔧 Solución de Problemas

### Error: "Keystore was tampered with, or password was incorrect"

```bash
# Verifica que las contraseñas en keystore.properties sean correctas
cat keystore.properties

# Si es necesario, recrea el keystore con nuevas contraseñas
```

### Error: "Java version mismatch"

```bash
# Fuerza el uso de Java 21
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk
./gradlew --version  # Debería mostrar Java 21
```

### Error: APK no está firmado ("jar is unsigned")

**Problema:** Gradle no firma automáticamente el APK aunque la configuración sea correcta.

**Solución:**
```bash
# Opción 1: Usar el script automatizado (recomendado)
./build-release.sh

# Opción 2: Firmar manualmente después del build
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk
jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 \
  -keystore release.keystore \
  -storepass TU_PASSWORD \
  app/build/outputs/apk/release/app-release.apk \
  TU_ALIAS
```

**Verificación:**
```bash
jarsigner -verify app/build/outputs/apk/release/app-release.apk
# Debe mostrar "jar verified."
```

### Error: "No signature of method"

```bash
# Limpia y reconstruye
./gradlew clean
./gradlew assembleRelease

# Ver logs detallados
./gradlew assembleRelease --info
```

### APK no se instala

1. Verifica que el APK esté firmado:
   ```bash
   jarsigner -verify app/build/outputs/apk/release/app-release.apk
   ```

2. Habilita instalación de apps desconocidas en el dispositivo

3. Verifica que el dispositivo tenga suficiente espacio

## 📋 Checklist de Distribución

- [ ] Keystore creado y respaldado
- [ ] Contraseñas configuradas en keystore.properties
- [ ] APK generado exitosamente
- [ ] APK verificado (firmado correctamente)
- [ ] Release creado en GitHub
- [ ] APK subido al release
- [ ] Notas de versión escritas
- [ ] Enlaces de descarga probados
- [ ] Usuarios notificados de la nueva versión

## 📞 Contacto y Soporte

Si tienes problemas con la firma o distribución:
1. Verifica los logs de Gradle: `./gradlew assembleRelease --info`
2. Revisa la configuración en build.gradle.kts
3. Asegúrate de que el keystore.properties existe y tiene las credenciales correctas

---

**Última actualización:** Diciembre 2025