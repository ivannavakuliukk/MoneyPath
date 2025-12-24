package com.example.moneypath.di

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)  // час на підключення
    .readTimeout(30, TimeUnit.SECONDS)     // час очікування відповіді
    .writeTimeout(30, TimeUnit.SECONDS)    // час на відправку запиту
    .cache(null)
    .build()