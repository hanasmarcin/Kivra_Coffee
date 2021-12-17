package com.hanasmarcin.kivracoffee.di.modules

import com.hanasmarcin.kivracoffee.BuildConfig.COFFEE_SERVICE_BASE_URL
import com.hanasmarcin.kivracoffee.model.service.CoffeeApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RestServiceModule {

    @Provides
    fun provideService(retrofit: Retrofit): CoffeeApiService = retrofit.create(CoffeeApiService::class.java)

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()
        return Retrofit.Builder()
            .baseUrl(COFFEE_SERVICE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
}