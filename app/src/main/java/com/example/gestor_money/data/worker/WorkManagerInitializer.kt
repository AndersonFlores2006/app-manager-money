package com.example.gestor_money.data.worker

import android.content.Context
import androidx.startup.Initializer
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class WorkManagerInitializer : Initializer<WorkManager> {
    override fun create(context: Context): WorkManager {
        // WorkManager ya está inicializado automáticamente
        val workManager = WorkManager.getInstance(context)

        // Programa verificación de actualizaciones cada 24 horas
        val updateCheckWork = PeriodicWorkRequestBuilder<UpdateCheckWorker>(
            24, TimeUnit.HOURS
        ).build()

        workManager.enqueueUniquePeriodicWork(
            "update_check",
            ExistingPeriodicWorkPolicy.KEEP,
            updateCheckWork
        )

        return workManager
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf()
    }
}
