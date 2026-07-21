package com.example.vietaicoach.di

import com.example.vietaicoach.data.ChatRepository
import com.example.vietaicoach.data.ChatRepositoryImpl
import com.example.vietaicoach.data.remote.ChatRemoteDataSource
import com.example.vietaicoach.data.remote.ChatRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Singleton
    @Binds
    abstract fun bindChatRepository(chatRepositoryImpl: ChatRepositoryImpl): ChatRepository

    @Singleton
    @Binds
    abstract fun bindChatRemoteDataSource(chatRemoteDataSourceImpl: ChatRemoteDataSourceImpl): ChatRemoteDataSource
}