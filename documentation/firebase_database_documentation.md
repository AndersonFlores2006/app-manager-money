# Documentación de la Base de Datos de Firebase

## 1. Introducción
Este documento detalla la integración y configuración de Firebase en el proyecto "Gestor de Dinero", así como la estructura de la base de datos y las mejoras implementadas para optimizar su rendimiento y seguridad. Firebase se utiliza como la solución de backend principal para la gestión de datos de usuarios y transacciones.

## 2. Configuración de Firebase
La configuración de Firebase se realizó siguiendo los pasos oficiales para una aplicación de Android utilizando Kotlin DSL para Gradle.

*   **Archivo `google-services.json`**: Este archivo, descargado desde la Firebase Console, se ha colocado en el directorio `app/` del proyecto (`gestor-de-dinero/app/google-services.json`). Este archivo contiene las credenciales y la configuración específica de tu proyecto de Firebase.
*   **Configuración de Gradle (build.gradle.kts)**:
    *   **Archivo de nivel de raíz (`gestor-de-dinero/build.gradle.kts`)**: Se añadió el complemento de Google Services como una dependencia en la sección `plugins`:
        ```gestor-de-dinero/build.gradle.kts
        plugins {
            // ...
            id("com.google.gms.google-services") version "4.4.4" apply false
        }
        ```
    *   **Archivo de nivel de módulo (`gestor-de-dinero/app/build.gradle.kts`)**: El complemento de Google Services ya estaba aplicado mediante `alias(libs.plugins.google.services)`. Además, se importó la Firebase BoM (`platform(libs.firebase.bom)`) para gestionar las versiones de las dependencias de Firebase, y se incluyeron las bibliotecas para Firestore y autenticación:
        ```gestor-de-dinero/app/build.gradle.kts
        plugins {
            id("com.android.application")
            id("com.google.gms.google-services")
            // ...
        }

        dependencies {
            // ...
            implementation(platform(libs.firebase.bom))
            implementation(libs.firebase.firestore)
            implementation(libs.firebase.auth)
            // ...
        }
        ```
    *   La versión de `firebaseBom` es `33.6.0` y la de `googleServices` es `4.4.2`, según `gestor-de-dinero/gradle/libs.versions.toml`.

## 3. Estructura de la Base de Datos (Firestore)
El proyecto utiliza **Cloud Firestore** como su base de datos principal, un sistema de base de datos NoSQL basado en documentos. La estructura se organiza en colecciones y documentos, permitiendo una escalabilidad flexible y consultas potentes.

### Colecciones Principales:

*   **`users` (Usuarios)**:
    *   Representa a cada usuario registrado en la aplicación.
    *   **Documento**: `userId` (ID único de usuario proporcionado por Firebase Authentication).
    *   **Campos de Ejemplo**:
        *   `email`: Correo electrónico del usuario.
        *   `displayName`: Nombre visible del usuario.
        *   `createdAt`: Marca de tiempo de creación de la cuenta.
        *   `totalBalance`: Saldo total actual del usuario.
        *   `currency`: Moneda preferida del usuario.
        *   `profileImageUrl` (opcional): URL de la imagen de perfil del usuario.

*   **`transactions` (Transacciones)**:
    *   Contiene todas las transacciones financieras realizadas por los usuarios.
    *   **Documento**: `transactionId` (ID único generado automáticamente por Firestore).
    *   **Campos de Ejemplo**:
        *   `userId`: Referencia al ID del usuario que realizó la transacción.
        *   `amount`: Monto de la transacción.
        *   `type`: Tipo de transacción (`income`, `expense`).
        *   `category`: Categoría de la transacción (ej. `food`, `transport`, `salary`).
        *   `description` (opcional): Descripción detallada de la transacción.
        *   `date`: Fecha de la transacción.
        *   `createdAt`: Marca de tiempo de creación del registro.
        *   `updatedAt` (opcional): Marca de tiempo de la última actualización del registro.

## 4. Mejoras Implementadas

Las siguientes mejoras se han aplicado para optimizar el rendimiento, la seguridad y la experiencia del usuario con la base de datos de Firebase:

*   **Uso de la BoM de Firebase**: La implementación de la "Bill of Materials" (BoM) de Firebase asegura que todas las dependencias de Firebase utilizadas en el proyecto sean compatibles entre sí, reduciendo problemas de versiones y simplificando la gestión de dependencias.
*   **Consultas Optimizadas**: Se han implementado consultas específicas en Firestore para minimizar la lectura de documentos innecesarios. Esto incluye el uso de `where` clauses, `orderBy` y `limit` para obtener solo los datos relevantes para la UI actual del usuario. Por ejemplo, al mostrar el historial de transacciones, se pueden cargar las últimas N transacciones o filtrar por un rango de fechas.
*   **Manejo de Transacciones sin Conexión (Offline Capability)**: Firebase Firestore ofrece persistencia de datos offline de forma nativa. Esto significa que la aplicación puede seguir funcionando y almacenando datos incluso sin conexión a internet. Los cambios se sincronizarán automáticamente una vez que la conexión se restablezca, mejorando la robustez y la experiencia del usuario.
*   **Seguridad con Reglas de Seguridad de Firestore**: Se han definido reglas de seguridad en Firestore para controlar estrictamente el acceso a los datos. Estas reglas aseguran que los usuarios solo puedan leer y escribir sus propios datos, previniendo accesos no autorizados y manipulaciones maliciosas. Por ejemplo, una regla para la colección `transactions` podría ser: `allow read, write: if request.auth.uid == resource.data.userId;`.
*   **Modelos de Datos con Kotlin Data Classes**: Los datos recuperados de Firestore se mapean directamente a Kotlin Data Classes, lo que proporciona tipado fuerte, inmutabilidad y mejora la legibilidad y el mantenimiento del código. Esto reduce errores en tiempo de ejecución y facilita la manipulación de los datos en la aplicación.
*   **Uso de Listeners en Tiempo Real**: Para funcionalidades que requieren actualizaciones instantáneas (ej. ver el saldo actual o nuevas transacciones), se utilizan listeners en tiempo real de Firestore. Esto permite que la UI de la aplicación se actualice automáticamente cada vez que hay un cambio en los datos del backend, proporcionando una experiencia dinámica y reactiva al usuario.

## 5. Notas Adicionales
*   Se recomienda revisar y actualizar periódicamente las reglas de seguridad de Firestore a medida que la aplicación evoluciona y se añaden nuevas funcionalidades.
*   Considerar la implementación de Cloud Functions para Firebase para lógica de backend compleja, gatillos en la base de datos o integración con servicios externos.