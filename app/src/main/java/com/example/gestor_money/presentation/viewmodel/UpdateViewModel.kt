package com.example.gestor_money.presentation.viewmodel

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestor_money.BuildConfig
import com.example.gestor_money.domain.repository.UpdateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

data class UpdateUiState(
    val isChecking: Boolean = false,
    val updateAvailable: Boolean = false,
    val currentVersion: String = "",
    val latestVersion: String = "",
    val latestVersionCode: String = "",
    val isDownloading: Boolean = false,
    val downloadProgress: Int = 0,
    val changeLog: String = "",
    val apkDownloadUrl: String = "",
    val error: String? = null
)

@HiltViewModel
class UpdateViewModel @Inject constructor(
    private val updateRepository: UpdateRepository,
    private val context: Context
) : ViewModel() {

    private val currentVersion = getCurrentVersion()
    private val _uiState = MutableStateFlow(UpdateUiState(currentVersion = currentVersion))
    val uiState: StateFlow<UpdateUiState> = _uiState.asStateFlow()

    private fun getCurrentVersion(): String {
        return try {
            val packageInfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "1.0.0"
        } catch (e: PackageManager.NameNotFoundException) {
            "1.0.0"
        }
    }

    fun checkForUpdates() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isChecking = true, error = null)
            
            // Obtener la versión actual del dispositivo (no la cacheada)
            val actualCurrentVersion = getCurrentVersion()
            
            updateRepository.getLatestRelease().onSuccess { release ->
                val latestVersion = release.tag_name.removePrefix("v")

                // Comparar con la versión actual real del dispositivo
                val versionComparison = compareVersions(latestVersion, actualCurrentVersion)
                val updateAvailable = versionComparison > 0
                
                Log.d("UpdateViewModel", "Current version: $actualCurrentVersion, Latest version: $latestVersion, Update available: $updateAvailable")
                
                // Buscar el APK en los assets
                val apkAsset = release.assets.find { it.name.endsWith(".apk") }
                val apkUrl = apkAsset?.browser_download_url ?: ""

                // Only show update as available if there's an APK to download AND version is newer
                val updateAvailableWithApk = updateAvailable && apkUrl.isNotEmpty()

                _uiState.value = _uiState.value.copy(
                    isChecking = false,
                    updateAvailable = updateAvailableWithApk,
                    currentVersion = actualCurrentVersion,
                    latestVersion = latestVersion,
                    changeLog = if (updateAvailableWithApk) {
                        release.body ?: "Sin cambios reportados"
                    } else {
                        "Tu aplicación está actualizada (v$actualCurrentVersion)"
                    },
                    apkDownloadUrl = apkUrl
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isChecking = false,
                    error = error.message ?: "Error al verificar actualizaciones"
                )
            }
        }
    }

    fun downloadAndInstallUpdate(context: Context) {
        viewModelScope.launch {
            val apkUrl = _uiState.value.apkDownloadUrl
            Log.d("UpdateViewModel", "Starting download from URL: $apkUrl")

            if (apkUrl.isEmpty()) {
                Log.e("UpdateViewModel", "APK URL is empty")
                _uiState.value = _uiState.value.copy(
                    error = "URL del APK no disponible"
                )
                return@launch
            }

            _uiState.value = _uiState.value.copy(isDownloading = true)

            try {
                val result = withContext(Dispatchers.IO) {
                    updateRepository.downloadAPK(apkUrl) { downloaded, total ->
                        val progress = ((downloaded * 100) / total).toInt()
                        // Update progress on main thread
                        launch(Dispatchers.Main) {
                            _uiState.value = _uiState.value.copy(downloadProgress = progress)
                        }
                    }
                }

                result.onSuccess { apkPath ->
                    Log.d("UpdateViewModel", "Download successful, installing APK from: $apkPath")
                    installAPK(context, apkPath)
                    _uiState.value = _uiState.value.copy(
                        isDownloading = false,
                        updateAvailable = false, // Reset update status after download
                        apkDownloadUrl = ""
                    )
                }.onFailure { error ->
                    Log.e("UpdateViewModel", "Download failed: ${error.message}")
                    _uiState.value = _uiState.value.copy(
                        isDownloading = false,
                        error = error.message ?: "Error al descargar actualización"
                    )
                }
            } catch (e: Exception) {
                Log.e("UpdateViewModel", "Exception during download: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    isDownloading = false,
                    error = e.message ?: "Error desconocido"
                )
            }
        }
    }

    private fun installAPK(context: Context, apkPath: String) {
        val apkFile = File(apkPath)
        Log.d("UpdateViewModel", "Installing APK from file: ${apkFile.absolutePath}, exists: ${apkFile.exists()}, size: ${apkFile.length()}")

        val apkUri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context, "${context.packageName}.provider", apkFile)
        } else {
            Uri.fromFile(apkFile)
        }

        Log.d("UpdateViewModel", "APK URI: $apkUri")

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        try {
            context.startActivity(intent)
            Log.d("UpdateViewModel", "Installation intent started successfully")
        } catch (e: Exception) {
            Log.e("UpdateViewModel", "Failed to start installation intent: ${e.message}")
            _uiState.value = _uiState.value.copy(
                error = "Error al iniciar instalación: ${e.message}"
            )
        }
    }

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

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    /**
     * Limpia el estado de actualizaciones y fuerza una nueva verificación
     * Útil después de instalar una actualización
     */
    fun resetUpdateState() {
        val currentVersion = getCurrentVersion()
        _uiState.value = UpdateUiState(currentVersion = currentVersion)
        Log.d("UpdateViewModel", "Update state reset, current version: $currentVersion")
    }
}
