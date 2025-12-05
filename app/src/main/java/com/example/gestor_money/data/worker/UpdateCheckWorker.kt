package com.example.gestor_money.data.worker

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.gestor_money.MainActivity
import com.example.gestor_money.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UpdateCheckWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                // Aquí iría la lógica para verificar actualizaciones
                // Por ahora es un placeholder
                Log.d("UpdateCheckWorker", "Verificando actualizaciones...")
                Result.success()
            } catch (e: Exception) {
                Log.e("UpdateCheckWorker", "Error al verificar actualizaciones", e)
                Result.retry()
            }
        }
    }

    private fun showUpdateNotification(latestVersion: String) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        try {
            val notification = NotificationCompat.Builder(applicationContext, "updates")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Nueva actualización disponible")
                .setContentText("Versión $latestVersion")
                .setAutoCancel(true)
                .build()

            val notificationManager = androidx.core.app.NotificationManagerCompat.from(applicationContext)
            notificationManager.notify(UPDATE_NOTIFICATION_ID, notification)
        } catch (e: Exception) {
            // Handle permission error for Android 13+
            Log.e("UpdateCheckWorker", "Error al mostrar notificación", e)
        }
    }

    companion object {
        private const val UPDATE_NOTIFICATION_ID = 100
    }
}
