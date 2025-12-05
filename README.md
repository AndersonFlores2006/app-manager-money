# Gestor de Dinero

Una aplicación Android para gestionar tus finanzas personales de manera eficiente.

## Autor

Anderson Flores

## Características Principales

*   **Seguimiento de Transacciones:** Registra tus gastos e ingresos de forma detallada.
*   **Análisis Financiero:** Visualiza tus finanzas con gráficos interactivos (Vico Charts).
*   **Asistente IA:** Obtén consejos financieros personalizados y respuestas a tus preguntas gracias a Gemini AI.
*   **Gestión de Presupuestos:** (Funcionalidad a futuro)
*   **Sincronización de Datos:** (Funcionalidad a futuro)

## Tecnologías Utilizadas

*   **Lenguaje:** Kotlin
*   **Arquitectura:** MVVM (Model-View-ViewModel)
*   **UI Toolkit:** Jetpack Compose
*   **Inyección de Dependencias:** Hilt
*   **Base de Datos:** Room Persistence Library
*   **Navegación:** Jetpack Navigation Compose
*   **Servicios de IA:** Google Gemini API
*   **Manejo de Red:** Retrofit, OkHttp
*   **Procesamiento de Excel:** Apache POI

## Configuración y Ejecución

### Prerrequisitos

*   Android Studio (versión reciente)
*   Git
*   Java Development Kit (JDK) 11 o superior
*   Una clave API de Google Gemini (obtenida desde [Google AI Studio](https://aistudio.google.com/app/apikey))

### Configuración del Proyecto

1.  **Clona el repositorio:**
    ```bash
    git clone https://github.com/AndersonFlores2006/app-manager-money.git
    cd app-manager-money
    ```
2.  **Configura las variables de entorno:**
    *   Copia el archivo `.env.example` a `.env`:
        ```bash
        cp .env.example .env
        ```
    *   Abre el archivo `.env` y reemplaza `TU_CLAVE_API_DE_GEMINI` con tu clave API real de Google Gemini.

3.  **Sincroniza el proyecto con Gradle:**
    *   Abre el proyecto en Android Studio.
    *   Android Studio probablemente te pedirá que sincronices el proyecto. Si no, ve a `File > Sync Project with Gradle Files`.

4.  **Compila y ejecuta la aplicación:**
    *   Desde Android Studio, selecciona tu dispositivo o emulador y haz clic en el botón de "Run" (el ícono de play).

### Cómo funciona

La aplicación sigue una arquitectura MVVM con Jetpack Compose para la interfaz de usuario.

*   **`MainActivity.kt`**: Es el punto de entrada de la aplicación, configura el `Scaffold` y el `NavGraph`.
*   **`NavGraph.kt`**: Define la navegación entre las diferentes pantallas de la aplicación.
*   **`BottomNavigationBar.kt`**: Implementa la barra de navegación inferior para acceder a las secciones principales.
*   **Pantallas (Screens)**: Cada archivo en `presentation.screens` (ej. `HomeScreen.kt`, `ChatScreen.kt`) representa una pantalla de la UI. Utilizan `ViewModel`s para manejar la lógica de la UI y el estado.
*   **ViewModels**: Los `ViewModel`s (ej. `ChatViewModel.kt`) exponen el estado de la UI a través de `StateFlow` y manejan las interacciones del usuario.
*   **Data Layer**: Incluye `GeminiRepository.kt` para interactuar con la API de Gemini y Room para el almacenamiento local de transacciones.
*   **DI (Hilt)**: Hilt se utiliza para la inyección de dependencias, facilitando la gestión de las dependencias entre componentes.

## Contribuciones

Si deseas contribuir a este proyecto, por favor:
1.  Haz un fork del repositorio.
2.  Crea una nueva rama (`git checkout -b feature/tu-caracteristica`).
3.  Realiza tus cambios.
4.  Confirma tus cambios (`git commit -m 'feat: Agrega nueva caracteristica'`).
5.  Haz push a la rama (`git push origin feature/tu-caracteristica`).
6.  Abre un Pull Request.

## Licencia

Este proyecto está bajo la Licencia MIT.
