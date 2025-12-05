package com.example.gestor_money.data.remote

import retrofit2.http.GET

data class GithubRelease(
    val tag_name: String,
    val assets: List<Asset>,
    val body: String?
)

data class Asset(
    val name: String,
    val browser_download_url: String,
    val size: Long
)

interface UpdateApi {
    @GET("repos/AndersonFlores2006/app-manager-money/releases/latest")
    suspend fun getLatestRelease(): GithubRelease
}
