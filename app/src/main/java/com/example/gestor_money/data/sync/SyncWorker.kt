package com.example.gestor_money.data.sync

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * WorkManager worker for background synchronization
 * Runs periodically when device is connected to network
 */
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncManager: SyncManager,
    private val networkMonitor: NetworkMonitor
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val TAG = "SyncWorker"
        const val WORK_NAME = "sync_work"
        
        /**
         * Enqueue periodic sync work (runs every 2 hours when online)
         */
        fun enqueue(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
                2, TimeUnit.HOURS,
                30, TimeUnit.MINUTES // Flex interval
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    15, TimeUnit.MINUTES
                )
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                syncRequest
            )
            
            Log.d(TAG, "Periodic sync work enqueued")
        }

        /**
         * Trigger immediate one-time sync
         */
        fun triggerImmediateSync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueue(syncRequest)
            Log.d(TAG, "Immediate sync triggered")
        }
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            if (!networkMonitor.isCurrentlyConnected()) {
                Log.d(TAG, "No network, rescheduling sync")
                return@withContext Result.retry()
            }

            Log.d(TAG, "Starting background sync...")
            
            // First sync local changes to cloud
            val uploadResult = syncManager.syncToCloud()
            
            // Then fetch any new data from cloud
            val downloadResult = syncManager.syncFromCloud()

            when {
                uploadResult is SyncResult.Error || downloadResult is SyncResult.Error -> {
                    Log.e(TAG, "Sync failed, will retry")
                    Result.retry()
                }
                uploadResult is SyncResult.NoNetwork || downloadResult is SyncResult.NoNetwork -> {
                    Log.d(TAG, "No network during sync")
                    Result.retry()
                }
                else -> {
                    Log.d(TAG, "Sync completed successfully")
                    Result.success()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Sync worker failed", e)
            Result.retry()
        }
    }
}
