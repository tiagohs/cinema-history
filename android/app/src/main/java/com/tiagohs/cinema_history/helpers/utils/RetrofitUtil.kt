package com.tiagohs.cinema_history.helpers.utils

import com.google.gson.*
import com.tiagohs.cinema_history.BuildConfig
import com.tiagohs.cinema_history.helpers.deserializers.MainTopicDeserializer
import com.tiagohs.cinema_history.helpers.deserializers.PageContentDeserializer
import com.tiagohs.cinema_history.models.MainTopic
import com.tiagohs.cinema_history.models.contents.Content
import com.tiagohs.cinema_history.services.config.FakeInterceptor
import com.tiagohs.cinema_history.services.config.RxErrorHandlingCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
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
                .baseUrl("https://api.themoviedb.org/4/")
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
        httpClient.addInterceptor(getTMDBIntercaptor())

        return httpClient.build()
    }

    private fun getLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        return logging
    }

    private fun getTMDBIntercaptor(): Interceptor {
        return object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val original = chain.request()
                var newRequest = original

                val builder = original.newBuilder()
                val newUrl = original.url.toString() + "&api_key=" + BuildConfig.THEMOVIEDB_API_KEY
                newRequest = builder.url(newUrl).build()

                var response = chain.proceed(newRequest)

                var retryCount = 0
                while (!response.isSuccessful && retryCount < 3) {
                    retryCount++
                    response = chain.proceed(newRequest)
                }

                return response
            }

        }
    }

    private fun gsonBuilder(): Gson {
        val customPolicy = FieldNamingStrategy {
            it.name.toLowerCase()
        }

        return GsonBuilder()
                .registerTypeAdapter(Content::class.java, PageContentDeserializer())
                .registerTypeAdapter(MainTopic::class.java, MainTopicDeserializer())
                .create()
    }
}