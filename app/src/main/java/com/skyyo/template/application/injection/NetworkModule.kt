package com.skyyo.template.application.injection

import com.skyyo.template.BuildConfig
import com.skyyo.template.application.network.RetrofitAuthenticator
import com.skyyo.template.application.network.calls.AuthCalls
import com.skyyo.template.application.persistance.DataStoreManager
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(
        dataStoreManager: DataStoreManager,
        authenticator: RetrofitAuthenticator
    ): OkHttpClient {
        val headerInjector = Interceptor { chain ->
            return@Interceptor chain.proceed(
                chain.request()
                    .newBuilder()
                    .header(
                        "Authorization",
                        runBlocking { dataStoreManager.getAccessToken() ?: "" }
                    )
                    .build()
            )
        }
        return OkHttpClient.Builder().apply {
            addInterceptor(headerInjector)
            if (BuildConfig.DEBUG) {
                val loggingInterceptor = HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
                addInterceptor(loggingInterceptor)
            }
            authenticator(authenticator)
            connectTimeout(timeout = 10, TimeUnit.SECONDS)
            writeTimeout(timeout = 10, TimeUnit.SECONDS)
            readTimeout(timeout = 10, TimeUnit.SECONDS)
        }.build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: Lazy<OkHttpClient>): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .callFactory { request -> okHttpClient.get().newCall(request) }
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    @Singleton
    @Provides
    fun provideAuthCalls(retrofit: Retrofit): AuthCalls = retrofit.create(AuthCalls::class.java)
}
