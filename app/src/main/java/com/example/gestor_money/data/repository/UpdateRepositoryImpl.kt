package com.example.gestor_money.data.repository

import android.content.Context
import android.os.Environment
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
    ): Result<String> = runCatching {
        val request = okhttp3.Request.Builder().url(url).build()
        val response = okHttpClient.newCall(request).execute()

        if (!response.isSuccessful) throw Exception("Failed to download APK")

        val body = response.body ?: throw Exception("Empty response body")
        val totalSize = body.contentLength()
        var downloadedSize = 0L

        val apkFile = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
            "app_update.apk"
        )

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

        apkFile.absolutePath
    }
}
