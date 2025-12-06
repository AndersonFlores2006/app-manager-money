package com.example.gestor_money.di

import android.content.Context
import com.example.gestor_money.data.remote.UpdateApi
import com.example.gestor_money.data.repository.UpdateRepositoryImpl
import com.example.gestor_money.domain.repository.UpdateRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UpdateModule {

    @Singleton
    @Provides
    @Named("github")
    fun provideGithubOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val userAgentInterceptor = Interceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header("User-Agent", "GestorMoney-Android-App")
                .header("Accept", "application/vnd.github.v3+json")
                .build()
            chain.proceed(request)
        }

        return OkHttpClient.Builder()
            .addInterceptor(userAgentInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun provideUpdateApi(@Named("github") githubOkHttpClient: OkHttpClient): UpdateApi {
        return Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .client(githubOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UpdateApi::class.java)
    }

    @Singleton
    @Provides
    fun provideUpdateRepository(
        updateApi: UpdateApi,
        @Named("github") githubOkHttpClient: OkHttpClient,
        @ApplicationContext context: Context
    ): UpdateRepository {
        return UpdateRepositoryImpl(updateApi, githubOkHttpClient, context)
    }
}
