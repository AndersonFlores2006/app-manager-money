package com.example.gestor_money.presentation.screens.updates

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gestor_money.presentation.viewmodel.UpdateViewModel

@Composable
fun UpdateDialog(
    onDismiss: () -> Unit,
    viewModel: UpdateViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.checkForUpdates()
    }

    when {
        uiState.isChecking -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text("Verificando actualizaciones...") },
                text = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                },
                confirmButton = {}
            )
        }

        uiState.updateAvailable -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text("¡Actualización disponible!") },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Nueva versión: ${uiState.latestVersion}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "Actual: ${uiState.currentVersion}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        
                        if (uiState.changeLog.isNotEmpty()) {
                            Text(
                                "Cambios:",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                uiState.changeLog,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 5
                            )
                        }

                        if (uiState.isDownloading) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    progress = { uiState.downloadProgress / 100f },
                                    modifier = Modifier.size(48.dp)
                                )
                                Text("${uiState.downloadProgress}%", textAlign = TextAlign.Center)
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.downloadAndInstallUpdate(context) },
                        enabled = !uiState.isDownloading
                    ) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Descargar e instalar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismiss) {
                        Text("Luego")
                    }
                }
            )
        }

        uiState.error != null -> {
            AlertDialog(
                onDismissRequest = {
                    viewModel.clearError()
                    onDismiss()
                },
                title = { Text("Error") },
                text = { Text(uiState.error ?: "Error desconocido") },
                confirmButton = {
                    Button(onClick = {
                        viewModel.clearError()
                        onDismiss()
                    }) {
                        Text("Aceptar")
                    }
                }
            )
        }

        else -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text("Tu app está actualizada") },
                text = { Text("Usas la versión ${uiState.currentVersion}") },
                confirmButton = {
                    Button(onClick = onDismiss) {
                        Text("Aceptar")
                    }
                }
            )
        }
    }
}
