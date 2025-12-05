# Plan de Mejora: Implementación de Pantalla de Transacciones

Este documento describe los pasos necesarios para completar la funcionalidad de la pantalla de Transacciones en la aplicación Gestor de Dinero.

## Objetivo

Implementar la UI y la lógica para visualizar, agregar, editar y eliminar transacciones.

## Estado Actual

La pantalla de transacciones actualmente muestra un mensaje de "Transaction list coming soon..." y no tiene funcionalidad implementada.

## Plan de Acción

### Fase 1: Configuración Básica y Modelo de Datos

1.  **Definir el `TransactionItem` Composable:** Crear un componente reutilizable para mostrar una sola transacción en la lista. Deberá mostrar la descripción, monto, tipo (ingreso/gasto) y fecha.
2.  **Diseñar la Lista de Transacciones:** Implementar un `LazyColumn` en `TransactionsScreen.kt` para mostrar la lista de `TransactionItem`s.
3.  **Integrar con ViewModel:** Crear un `TransactionsViewModel` que obtenga la lista de transacciones desde la base de datos (Room) y la exponga a la UI.

### Fase 2: Funcionalidad de Agregar Transacción

1.  **Navegación a la Pantalla de Agregar:** Asegurarse de que el botón '+' (o similar) navegue a la pantalla `AddTransactionScreen` (que ya existe).
2.  **Implementar la UI de `AddTransactionScreen`:** Completar la UI para la entrada de monto, descripción, tipo de transacción y fecha.
3.  **ViewModel de `AddTransactionScreen`:** Implementar la lógica para guardar las nuevas transacciones en la base de datos.

### Fase 3: Funcionalidad de Editar y Eliminar

1.  **Implementar Edición:** Permitir al usuario hacer clic en una transacción de la lista para navegar a una pantalla de edición (posiblemente reutilizando `AddTransactionScreen` o una similar) y modificar los detalles.
2.  **Implementar Eliminación:** Añadir una opción (ej. botón de eliminar o swipe-to-delete) para eliminar transacciones de la lista y de la base de datos.

### Fase 4: Filtrado y Ordenación

1.  **Opciones de Filtrado:** Implementar UI para filtrar transacciones por tipo (ingreso/gasto), fecha, o categoría (si se añade).
2.  **Opciones de Ordenación:** Permitir ordenar la lista de transacciones por fecha, monto, etc.

### Fase 5: Pruebas y Refinamiento

1.  **Pruebas Unitarias y de Integración:** Escribir pruebas para la lógica del ViewModel, la base de datos y los `Composable`s principales.
2.  **Refinamiento de UI/UX:** Asegurar una experiencia de usuario fluida y visualmente agradable.

## Próximos Pasos Inmediatos

*   Crear el archivo `plan-mejora.md` en `documentation/`.
*   Empezar la Fase 1: Definir el `TransactionItem` Composable y la lista básica.
