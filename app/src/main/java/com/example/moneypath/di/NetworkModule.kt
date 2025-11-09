package com.example.moneypath.di

import com.example.moneypath.data.datasource.BackendService
import com.example.moneypath.data.datasource.MonobankService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @Named("MonobankRetrofit")
    fun provideMonobankRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.monobank.ua/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideMonobankService(@Named("MonobankRetrofit") retrofit: Retrofit): MonobankService {
        return retrofit.create(MonobankService::class.java)
    }

    @Provides
    @Singleton
    @Named("BackendRetrofit")
    fun provideBackendRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://ivannavakuliukkkk.pythonanywhere.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideBackendService(
        @Named("BackendRetrofit") retrofit: Retrofit
    ): BackendService {
        return retrofit.create(BackendService::class.java)
    }
}
