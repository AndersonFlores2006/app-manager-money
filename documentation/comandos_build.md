# Comandos para Construir y Enviar la App a tu Celular

## Preparación
Asegúrate de tener Java 21 configurado:
```
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk
export PATH=$JAVA_HOME/bin:$PATH
```

Verifica:
```
java -version
./gradlew --version
```

## Construcción
1. Limpiar el proyecto:
   ```
   ./gradlew clean
   ```

2. Construir el APK de debug:
   ```
   ./gradlew assembleDebug
   ```

3. Verificar que el APK se creó:
   ```
   ls -la app/build/outputs/apk/debug/app-debug.apk
   ```

## Instalación en el Celular
1. Conectar el celular (modo desarrollador habilitado):
   ```
   adb devices
   ```

2. Instalar el APK:
   ```
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

## Notas
- Si usas WiFi en lugar de USB, conecta con:
  ```
  adb tcpip 5555
  adb connect <IP_DEL_CELULAR>:5555
  ```
- Para ver logs de la app:
  ```
  adb logcat | grep -i "gestor_money\|export"