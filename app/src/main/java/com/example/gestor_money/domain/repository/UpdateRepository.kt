package com.example.gestor_money.domain.repository

import com.example.gestor_money.data.remote.GithubRelease

interface UpdateRepository {
    suspend fun getLatestRelease(): Result<GithubRelease>
    suspend fun downloadAPK(url: String, onProgress: (Long, Long) -> Unit): Result<String>
}
