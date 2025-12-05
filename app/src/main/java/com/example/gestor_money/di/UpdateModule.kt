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
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UpdateModule {

    @Singleton
    @Provides
    fun provideUpdateApi(okHttpClient: OkHttpClient): UpdateApi {
        return Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UpdateApi::class.java)
    }

    @Singleton
    @Provides
    fun provideUpdateRepository(
        updateApi: UpdateApi,
        okHttpClient: OkHttpClient,
        @ApplicationContext context: Context
    ): UpdateRepository {
        return UpdateRepositoryImpl(updateApi, okHttpClient, context)
    }
}
