# Plan de Mejoras para Gestor de Dinero

Este documento detalla los planes para mejorar las funcionalidades de la aplicación Gestor de Dinero.

## 1. Mejoras en la pantalla de Transacciones

**Estado Actual:**
La funcionalidad de transacciones aún no está completamente terminada. Se necesita implementar la interfaz de usuario y la lógica para la visualización y edición de transacciones.

**Plan de Acción:**
1.  **Diseño de la UI:** Crear una pantalla dedicada para mostrar la lista de transacciones, con opciones de filtrado y ordenación.
2.  **Detalle de Transacción:** Implementar una vista detallada para cada transacción, permitiendo la edición y eliminación.
3.  **Validaciones:** Asegurar que los datos de las transacciones se ingresen y almacenen correctamente.
4.  **Integración con ViewModel:** Conectar la UI de transacciones con el ViewModel correspondiente para gestionar el estado y las operaciones.

**Próximos Pasos:**
*   Diseñar los componentes de UI para la lista y el detalle de transacciones.
*   Implementar la lógica para la creación, visualización, edición y eliminación de transacciones.

## 2. Corrección de la funcionalidad de Exportar a Excel

**Estado Actual:**
La funcionalidad de exportar transacciones a Excel está fallando y causa un crash en la aplicación.

**Plan de Acción:**
1.  **Análisis de Errores:** Revisar los logs de crash de la aplicación para identificar la causa raíz del problema. Es probable que esté relacionado con el manejo de la biblioteca Apache POI o con la forma en que se generan los datos para el archivo Excel.
2.  **Revisión del Código:** Analizar el código responsable de la exportación a Excel. Verificar: 
    *   La correcta inicialización de la biblioteca Apache POI.
    *   La correcta obtención y formateo de los datos de las transacciones.
    *   El manejo de archivos y permisos (si aplica).
    *   La correcta creación y guardado del archivo Excel.
3.  **Pruebas Unitarias y de Integración:** Escribir pruebas para la lógica de exportación para asegurar su correcto funcionamiento.
4.  **Implementación de Corrección:** Aplicar las correcciones necesarias basadas en el análisis.

**Próximos Pasos:**
*   Investigar los logs de crash para obtener detalles del error.
*   Revisar el código de la función de exportación.
*   Implementar la solución y realizar pruebas exhaustivas.

## 3. Mostrar el Modelo de IA en las Respuestas

**Objetivo:**
Indicar claramente al usuario qué modelo de IA está respondiendo a sus consultas.

**Implementación:**
Se realizará una pequeña modificación en la UI de la pantalla de chat para que, junto con la respuesta de la IA, se muestre un texto indicando el modelo utilizado (ej. "Respuesta de Gemini 2.0 Flash").

**Estado:**
Se procederá a implementar esta mejora.
