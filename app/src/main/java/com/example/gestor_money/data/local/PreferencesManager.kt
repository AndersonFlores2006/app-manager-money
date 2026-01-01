package com.example.gestor_money.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "gestor_money_prefs"
        private const val KEY_RECENT_UPDATE_INSTALLED = "recent_update_installed"
        private const val KEY_LAST_UPDATE_CHECK = "last_update_check"
        private const val KEY_CACHED_VERSION = "cached_version"
    }

    // Flag para indicar si se instaló una actualización recientemente
    var recentUpdateInstalled: Boolean
        get() = prefs.getBoolean(KEY_RECENT_UPDATE_INSTALLED, false)
        set(value) = prefs.edit().putBoolean(KEY_RECENT_UPDATE_INSTALLED, value).apply()

    // Timestamp de la última verificación de actualizaciones
    var lastUpdateCheck: Long
        get() = prefs.getLong(KEY_LAST_UPDATE_CHECK, 0)
        set(value) = prefs.edit().putLong(KEY_LAST_UPDATE_CHECK, value).apply()

    // Versión cacheada (para comparación después de instalación)
    var cachedVersion: String
        get() = prefs.getString(KEY_CACHED_VERSION, "1.0.0") ?: "1.0.0"
        set(value) = prefs.edit().putString(KEY_CACHED_VERSION, value).apply()

    // Limpiar flags después de verificar que la versión es correcta
    fun clearUpdateFlags() {
        prefs.edit()
            .putBoolean(KEY_RECENT_UPDATE_INSTALLED, false)
            .putString(KEY_CACHED_VERSION, "")
            .apply()
    }

    // Marcar que se instaló una actualización
    fun markUpdateInstalled(version: String) {
        prefs.edit()
            .putBoolean(KEY_RECENT_UPDATE_INSTALLED, true)
            .putString(KEY_CACHED_VERSION, version)
            .apply()
    }
}