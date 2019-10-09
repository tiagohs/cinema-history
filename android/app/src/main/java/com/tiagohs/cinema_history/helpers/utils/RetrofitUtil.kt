package com.tiagohs.cinema_history.helpers.utils

import com.google.gson.*
import com.tiagohs.cinema_history.helpers.deserializers.PageContentDeserializer
import com.tiagohs.cinema_history.models.contents.Content
import com.tiagohs.cinema_history.services.config.FakeInterceptor
import com.tiagohs.cinema_history.services.config.RxErrorHandlingCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

object RetrofitUtil {

    fun localBuild(): Retrofit {
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(getLoggingInterceptor())
        httpClient.addInterceptor(FakeInterceptor())

        return Retrofit.Builder()
            .baseUrl("http://local/")
            .addConverterFactory(GsonConverterFactory.create(gsonBuilder()))
            .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
            .client(httpClient.build())
            .build()
    }

    fun tmdbBuild(): Retrofit {
        val client = client()

        return Retrofit.Builder()
                .baseUrl("")
                .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
                .client(client)
                .build()
    }

    private fun client(): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
        httpClient.connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)

        httpClient.addInterceptor(getLoggingInterceptor())

        return httpClient.build()
    }

    private fun getLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        return logging
    }

    private fun gsonBuilder(): Gson {
        val customPolicy = FieldNamingStrategy {
            it.name.toLowerCase()
        }

        return GsonBuilder()
                .registerTypeAdapter(Content::class.java, PageContentDeserializer())
                .create()
    }
}