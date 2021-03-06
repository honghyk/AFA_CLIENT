package com.example.graduation.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object Retrofit {
    private val baseUrl = "http://175.211.217.138:799"
    private var instance: Retrofit? = null

    private fun instance(): Retrofit {
        if(instance == null) {
            val interceptor = OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(HttpLoggingInterceptor().also {
                it.setLevel(HttpLoggingInterceptor.Level.BODY)
            }).build()
            instance = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(interceptor)
                .build()
        }
        return requireNotNull(instance)
    }

    fun getImageApi(): ImageApi = instance().create(ImageApi::class.java)
}