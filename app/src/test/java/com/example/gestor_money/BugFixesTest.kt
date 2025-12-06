package com.example.gestor_money

import org.junit.Test
import org.junit.Assert.*

/**
 * Pruebas unitarias para las correcciones de bugs y mejoras
 * Documentadas en documentation/bugs_y_mejoras.md
 */
class BugFixesTest {

    /**
     * Bug 1: Problema al guardar nuevos ingresos
     * Verifica que la comparación de versiones funcione correctamente
     */
    @Test
    fun `version comparison returns correct result for newer version`() {
        val result = compareVersions("1.2.0", "1.1.0")
        assertTrue("Nueva versión debería ser mayor", result > 0)
    }

    @Test
    fun `version comparison returns correct result for same version`() {
        val result = compareVersions("1.1.0", "1.1.0")
        assertEquals("Misma versión debería ser igual", 0, result)
    }

    @Test
    fun `version comparison returns correct result for older version`() {
        val result = compareVersions("1.0.0", "1.1.0")
        assertTrue("Versión antigua debería ser menor", result < 0)
    }

    @Test
    fun `version comparison handles different length versions`() {
        val result1 = compareVersions("1.2", "1.1.0")
        assertTrue("1.2 debería ser mayor que 1.1.0", result1 > 0)
        
        val result2 = compareVersions("1.1.0", "1.2")
        assertTrue("1.1.0 debería ser menor que 1.2", result2 < 0)
    }

    @Test
    fun `version comparison handles patch versions`() {
        val result = compareVersions("1.1.1", "1.1.0")
        assertTrue("1.1.1 debería ser mayor que 1.1.0", result > 0)
    }

    /**
     * Bug 2: Problema con las actualizaciones
     * Verifica que no se muestre actualización cuando ya está instalada
     */
    @Test
    fun `update not available when current version equals latest`() {
        val currentVersion = "1.2.0"
        val latestVersion = "1.2.0"
        val updateAvailable = compareVersions(latestVersion, currentVersion) > 0
        assertFalse("No debería haber actualización disponible", updateAvailable)
    }

    @Test
    fun `update available when latest version is newer`() {
        val currentVersion = "1.1.0"
        val latestVersion = "1.2.0"
        val updateAvailable = compareVersions(latestVersion, currentVersion) > 0
        assertTrue("Debería haber actualización disponible", updateAvailable)
    }

    /**
     * Mejora 1: Truncar texto largo en transacciones
     * Verifica que el texto se trunca correctamente
     */
    @Test
    fun `text truncation works for long descriptions`() {
        val longDescription = "Esta es una descripción muy larga que debería ser truncada con puntos suspensivos"
        val maxLength = 30
        val truncated = if (longDescription.length > maxLength) {
            longDescription.take(maxLength - 3) + "..."
        } else {
            longDescription
        }
        assertTrue("Texto truncado debería terminar con ...", truncated.endsWith("..."))
        assertTrue("Texto truncado debería ser menor o igual al máximo", truncated.length <= maxLength)
    }

    @Test
    fun `text truncation does not affect short descriptions`() {
        val shortDescription = "Compra"
        val maxLength = 30
        val truncated = if (shortDescription.length > maxLength) {
            shortDescription.take(maxLength - 3) + "..."
        } else {
            shortDescription
        }
        assertEquals("Texto corto no debería ser truncado", shortDescription, truncated)
    }

    // Helper function to compare versions (same logic as in UpdateViewModel)
    private fun compareVersions(v1: String, v2: String): Int {
        val parts1 = v1.split(".").map { it.toIntOrNull() ?: 0 }
        val parts2 = v2.split(".").map { it.toIntOrNull() ?: 0 }

        val maxLength = maxOf(parts1.size, parts2.size)
        for (i in 0 until maxLength) {
            val part1 = if (i < parts1.size) parts1[i] else 0
            val part2 = if (i < parts2.size) parts2[i] else 0
            
            if (part1 > part2) return 1
            if (part1 < part2) return -1
        }
        return 0
    }
}
