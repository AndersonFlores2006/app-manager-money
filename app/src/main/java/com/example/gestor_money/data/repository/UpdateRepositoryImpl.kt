package com.example.gestor_money.data.repository

import android.content.Context
import android.os.Environment
import android.util.Log
import com.example.gestor_money.data.remote.GithubRelease
import com.example.gestor_money.data.remote.UpdateApi
import com.example.gestor_money.domain.repository.UpdateRepository
import okhttp3.OkHttpClient
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class UpdateRepositoryImpl @Inject constructor(
    private val updateApi: UpdateApi,
    private val okHttpClient: OkHttpClient,
    private val context: Context
) : UpdateRepository {

    override suspend fun getLatestRelease(): Result<GithubRelease> = runCatching {
        updateApi.getLatestRelease()
    }

    override suspend fun downloadAPK(
        url: String,
        onProgress: (Long, Long) -> Unit
    ): Result<String> = try {
        Log.d("UpdateRepository", "Starting APK download from: $url")

        val request = okhttp3.Request.Builder().url(url).build()
        val response = okHttpClient.newCall(request).execute()

        Log.d("UpdateRepository", "Response code: ${response.code}")

        if (!response.isSuccessful) {
            Log.e("UpdateRepository", "Download failed with code: ${response.code}, message: ${response.message}")
            throw Exception("Failed to download APK: ${response.code} - ${response.message}")
        }

        val body = response.body ?: throw Exception("Empty response body")
        val totalSize = body.contentLength()
        var downloadedSize = 0L

        Log.d("UpdateRepository", "Total file size: $totalSize bytes")

        val apkFile = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
            "app_update.apk"
        )

        Log.d("UpdateRepository", "Saving to: ${apkFile.absolutePath}")

        FileOutputStream(apkFile).use { output ->
            body.byteStream().use { input ->
                val buffer = ByteArray(8192)
                var bytesRead: Int
                while (input.read(buffer).also { bytesRead = it } != -1) {
                    output.write(buffer, 0, bytesRead)
                    downloadedSize += bytesRead
                    onProgress(downloadedSize, totalSize)
                }
            }
        }

        Log.d("UpdateRepository", "Download completed. File size: ${apkFile.length()} bytes")
        Result.success(apkFile.absolutePath)
    } catch (e: Exception) {
        Log.e("UpdateRepository", "Download failed with exception: ${e.javaClass.simpleName}: ${e.message}", e)
        Result.failure(e)
    }
}
