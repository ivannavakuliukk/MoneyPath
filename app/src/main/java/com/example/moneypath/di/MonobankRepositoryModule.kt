package com.example.moneypath.di

import com.example.moneypath.data.local.MonobankLocalDataSource
import com.example.moneypath.data.remote.MonobankRemoteDataSource
import com.example.moneypath.data.repository.MonobankRepositoryImpl
import com.example.moneypath.domain.repository.MonobankRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideMonobankRepository(
        remote: MonobankRemoteDataSource,
        local: MonobankLocalDataSource
    ): MonobankRepository {
        return MonobankRepositoryImpl(remote, local)
    }
}
