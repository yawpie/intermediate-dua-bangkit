package com.dicoding.intermediate_satu.data.module

import android.app.Application
import android.content.Context
import com.dicoding.intermediate_satu.data.preferences.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideSessionManager(application: Application): SessionManager {
        return SessionManager(application.applicationContext)
    }
}