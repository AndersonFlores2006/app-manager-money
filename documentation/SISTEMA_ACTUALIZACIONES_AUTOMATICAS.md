# Sistema de Actualizaciones Automáticas

Este documento explica cómo funciona el sistema de actualizaciones automáticas de tu app y cómo mantenerlo.

## ¿Cómo funciona?

### 1. **Verificación Automática (Cada 24 horas)**
- WorkManager verifica automáticamente cada 24 horas si hay una nueva versión en GitHub Releases
- Si encuentra una versión más reciente, envía una notificación al usuario
- La verificación se ejecuta en background sin afectar el rendimiento

### 2. **Verificación Manual**
- El usuario puede ir a Configuración → Actualizaciones → "Verificar actualizaciones"
- Se abre un diálogo mostrando la versión actual y la última disponible
- Si hay actualización, puede descargar e instalar con un click

### 3. **Instalación**
- El APK se descarga automáticamente desde GitHub Releases
- Se muestra una barra de progreso de descarga
- Una vez descargado, se abre el instalador del sistema
- El usuario solo debe confirmar la instalación

## Pasos para distribuir actualizaciones

### Paso 1: Compilar el APK
```bash
./gradlew assembleRelease
```

### Paso 2: Actualizar la versión
En `app/build.gradle.kts`:
```kotlin
defaultConfig {
    versionCode = 2  // Incrementar
    versionName = "1.1.0"  // Nueva versión
}
```

### Paso 3: Subir a GitHub Releases
1. Ve a tu repositorio en GitHub
2. Clic en "Releases" en la barra lateral
3. Clic en "Create a new release"
4. En "Choose a tag", escribe `v1.1.0` (coincide con versionName)
5. Sube el APK descargado
6. Escribe los cambios en la descripción
7. Clic en "Publish release"

**Ejemplo de changelog:**
```
## v1.1.0 - Sistema de actualizaciones automáticas

### Nuevas características
- ✨ Actualizaciones automáticas sin Play Store
- 📱 Notificaciones cuando hay nuevas versiones
- 📊 Interfaz mejorada de configuración

### Correcciones
- 🐛 Error al exportar PDF en dispositivos Android 13+
- ⚡ Mejor rendimiento en cálculos

### Cambios técnicos
- Migrado a WorkManager para verificaciones en background
- Uso de GitHub API para detectar nuevas versiones
```

## Estructura del código

### Archivos creados:

1. **UpdateApi.kt** - Interface para consultar GitHub API
2. **UpdateRepository.kt** - Contrato de actualizaciones
3. **UpdateRepositoryImpl.kt** - Implementación con descarga de APK
4. **UpdateViewModel.kt** - Lógica de la pantalla de actualizaciones
5. **UpdateDialog.kt** - UI mostrando versiones disponibles
6. **UpdateCheckWorker.kt** - Tarea periódica que verifica actualizaciones
7. **WorkManagerInitializer.kt** - Inicializa WorkManager al arrancar app
8. **UpdateModule.kt** - Inyección de dependencias

### Cambios en archivos existentes:

- **SettingsScreen.kt** - Agregado botón para verificar actualizaciones
- **AndroidManifest.xml** - Permisos y provider configuration
- **build.gradle.kts** - Versión actualizada a 1.1.0
- **file_paths.xml** - Acceso a carpeta de descargas

## Permisos utilizados

```xml
<uses-permission android:name="android.permission.INTERNET" /> <!-- Ya existía -->
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />
```

## Flujo de actualización

```
App inicia
    ↓
WorkManager verifica GitHub cada 24h
    ↓
¿Nueva versión disponible?
    ├─ Sí → Notifica al usuario
    └─ No → Continúa normal

Usuario va a Configuración
    ↓
Click en "Verificar actualizaciones"
    ↓
Se consulta GitHub API
    ↓
¿Nueva versión?
    ├─ Sí → Muestra diálogo con changelog y botón descargar
    │   ├─ Usuario hace click
    │   ├─ Descarga APK desde GitHub Releases
    │   ├─ Muestra progreso
    │   ├─ Abre instalador del sistema
    │   └─ Usuario confirma instalación
    │
    └─ No → Muestra mensaje "Ya estás actualizado"
```

## Ejemplo: Actualizar a versión 1.2.0

### 1. En Android Studio
```kotlin
// app/build.gradle.kts
versionCode = 3
versionName = "1.2.0"
```

### 2. Compilar
```bash
./gradlew assembleRelease
```

### 3. En GitHub
- Ve a Releases
- New Release
- Tag: `v1.2.0`
- Title: `v1.2.0`
- Upload `app-release.apk` desde `app/build/outputs/apk/release/`
- Description: Tu changelog
- Publish

### 4. Los usuarios verán automáticamente:
- En 24 horas: Notificación automática
- O manual: Configuración → Actualizaciones → Verificar

## Solucionar problemas

### La app no detecta nuevas versiones

1. **Verifica la etiqueta en GitHub**
   - El tag debe ser `v` + versionName
   - Ej: `v1.2.0` para versionName = "1.2.0"

2. **Verifica que el APK esté en Releases**
   - Descargalo manualmente desde GitHub para probar el enlace

3. **Compara versiones**
   - La app solo detecta versiones más nuevas
   - Si la versión es igual o menor, no notificará

### El usuario no puede instalar el APK

1. **Requiere permisos:**
   - Configuración → Aplicaciones → [App] → Permisos → Almacenamiento
   - Configuración → Aplicaciones → [App] → Permisos → Instalar aplicaciones

2. **En Android 12+:**
   - El usuario puede necesitar permitir instalaciones en Configuración

### La verificación automática no funciona

1. **Reinicia la app** para que WorkManager se active
2. **Verifica que el dispositivo tiene conexión a internet**
3. **WorkManager requiere batería** - En algunos phones desactiva tareas si batería es baja

## Próximas mejoras opcionales

- [ ] Descarga automática en background (sin confirmar)
- [ ] Actualizaciones delta (solo cambios, no app completa)
- [ ] Historial de versiones dentro de la app
- [ ] Rollback a versión anterior
- [ ] Beta testing (releases pre-release)

## Contacto & Soporte

Si los usuarios tienen problemas con actualizaciones:
1. Verifica que tengan internet
2. Pídeles que reinicien la app
3. Verifica que GitHub Releases esté accesible en su región
