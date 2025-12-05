package com.example.gestor_money.di

import com.example.gestor_money.data.remote.FirebaseDataSource
import com.example.gestor_money.data.remote.RemoteDataSource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dependency injection module for Firebase and sync-related dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object SyncModule {

    @Provides
    @Singleton
    fun provideRemoteDataSource(
        firestore: FirebaseFirestore
    ): RemoteDataSource {
        return FirebaseDataSource(firestore)
    }
}
