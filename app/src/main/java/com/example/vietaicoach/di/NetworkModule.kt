package com.example.vietaicoach.di

import com.example.vietaicoach.data.remote.ChatService
import com.example.vietaicoach.data.remote.ChatService.Companion.BASE_URL
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * `ignoreUnknownKeys` so the client keeps parsing when the backend grows fields it does not
     * yet know about; `explicitNulls = false` so absent coaching annotations fall back to the
     * defaults on [com.example.vietaicoach.data.remote.model.ChatResponse].
     */
    @Singleton
    @Provides
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    @Singleton
    @Provides
    fun provideRetrofit(json: Json): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType())
            )
            .build()
    }

    @Singleton
    @Provides
    fun provideChatService(retrofit: Retrofit): ChatService {
        return retrofit.create<ChatService>()
    }
}