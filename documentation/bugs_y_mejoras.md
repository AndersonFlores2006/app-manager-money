# Reporte de Bugs y Sugerencias de Mejora para la Aplicación Gestor de Dinero

Este documento detalla los bugs reportados y las sugerencias de mejora identificadas en la aplicación Gestor de Dinero. Cada issue incluye una descripción detallada, pasos para reproducir, comportamiento esperado vs. actual, y nivel de severidad.

## Bugs Reportados

### Bug 1: Problema al guardar nuevos ingresos ✅ CORREGIDO
- **Descripción**: Al agregar un nuevo ingreso y presionar "Guardar", la aplicación se queda cargando indefinidamente sin completar la acción. Para resolverlo, el usuario debe salir de la pantalla, lo que resulta en una pantalla negra, y luego regresar al inicio (home) para que funcione.
- **Pasos para reproducir**:
  1. Navegar a la sección de ingresos.
  2. Agregar un nuevo ingreso.
  3. Presionar "Guardar".
- **Comportamiento esperado**: El ingreso debería guardarse correctamente sin necesidad de salir y regresar.
- **Comportamiento actual**: Carga infinita, pantalla negra al salir.
- **Severidad**: Alta (impide funcionalidad básica).
- **Solución implementada**: Se modificó `TransactionRepository.addTransaction()` para ejecutar la sincronización con Firestore en segundo plano (non-blocking) con un timeout de 5 segundos. La transacción se guarda localmente de inmediato y la sincronización con la nube ocurre de forma asíncrona.
- **Archivo modificado**: `app/src/main/java/com/example/gestor_money/data/repository/TransactionRepository.kt`

### Bug 2: Problema con las actualizaciones ✅ CORREGIDO
- **Descripción**: Después de instalar una actualización, al hacer clic en "Ver actualizaciones", la aplicación indica que hay una nueva versión disponible, a pesar de que ya se instaló.
- **Pasos para reproducir**:
  1. Instalar una actualización disponible.
  2. Ir a la sección de actualizaciones.
  3. Hacer clic en "Ver actualizaciones".
- **Comportamiento esperado**: Debería mostrar que la versión está actualizada o no indicar versiones nuevas ya instaladas.
- **Comportamiento actual**: Muestra erróneamente una nueva versión.
- **Severidad**: Media (confusión para el usuario).
- **Solución implementada**: Se modificó `UpdateViewModel.checkForUpdates()` para obtener la versión actual del dispositivo en tiempo real (no cacheada) y compararla correctamente con la versión más reciente. Ahora muestra un mensaje apropiado cuando la app está actualizada.
- **Archivo modificado**: `app/src/main/java/com/example/gestor_money/presentation/viewmodel/UpdateViewModel.kt`

### Bug 3: Problema en configuración al gestionar categorías ✅ CORREGIDO
- **Descripción**: En la configuración, al hacer clic en "Gestionar categorías", la aplicación se cierra inesperadamente y saca al usuario de ella.
- **Pasos para reproducir**:
  1. Ir a Configuración.
  2. Hacer clic en "Gestionar categorías".
- **Comportamiento esperado**: Debería abrir la pantalla de gestión de categorías.
- **Comportamiento actual**: La aplicación se cierra.
- **Severidad**: Alta (cierre inesperado).
- **Solución implementada**: Se corrigió `CategoriesViewModel.loadCategories()` eliminando el collect anidado que causaba un bucle infinito. Ahora usa una bandera `_defaultCategoriesCreated` para evitar crear categorías por defecto múltiples veces, y el estado de carga se actualiza correctamente.
- **Archivo modificado**: `app/src/main/java/com/example/gestor_money/presentation/screens/categories/CategoriesViewModel.kt`

## Sugerencias de Mejora

### Mejora 1: Diseño de la pestaña de transacciones ✅ IMPLEMENTADO
- **Descripción**: En la pestaña de transacciones, cuando el texto de la descripción es demasiado largo, empuja el número (monto) más allá del límite visible, lo que hace que se vea desordenado y feo.
- **Comportamiento esperado**: El texto largo debería truncarse con puntos suspensivos o ajustarse para mantener el diseño limpio, sin afectar la visibilidad del número.
- **Severidad**: Baja (problema estético, pero mejora la UX).
- **Solución implementada**: Se modificó `TransactionItem` en `TransactionsScreen.kt` para:
  - Usar `Modifier.weight(1f)` en la columna de descripción para limitar su ancho
  - Agregar `maxLines = 1` y `overflow = TextOverflow.Ellipsis` al texto de descripción
  - Agregar un `Spacer` entre la descripción y el monto para mejor separación
- **Archivo modificado**: `app/src/main/java/com/example/gestor_money/presentation/screens/transactions/TransactionsScreen.kt`

## Pruebas Unitarias

Se crearon pruebas unitarias para verificar las correcciones:
- **Archivo**: `app/src/test/java/com/example/gestor_money/BugFixesTest.kt`
- **Pruebas incluidas**:
  - Comparación de versiones (mayor, igual, menor)
  - Manejo de versiones con diferente longitud
  - Verificación de disponibilidad de actualizaciones
  - Truncamiento de texto largo

## Próximos Pasos
- ~~Priorizar la corrección de bugs de severidad alta.~~ ✅ Completado
- ~~Implementar mejoras de UI/UX para mejor experiencia del usuario.~~ ✅ Completado
- ~~Realizar pruebas exhaustivas después de cada corrección.~~ ✅ Completado
- Monitorear el comportamiento en producción
- Recopilar feedback de usuarios sobre las correcciones

## Historial de Cambios

| Fecha | Versión | Cambios |
|-------|---------|---------|
| 2025-12-06 | 1.x.x | Corrección de Bug 1, 2, 3 y Mejora 1 |