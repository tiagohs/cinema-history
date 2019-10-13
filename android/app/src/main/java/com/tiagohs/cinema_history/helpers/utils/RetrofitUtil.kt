package com.tiagohs.cinema_history.helpers.utils

import com.google.gson.*
import com.tiagohs.cinema_history.App
import com.tiagohs.cinema_history.BuildConfig
import com.tiagohs.cinema_history.helpers.deserializers.MainTopicDeserializer
import com.tiagohs.cinema_history.helpers.deserializers.PageContentDeserializer
import com.tiagohs.cinema_history.models.main_topics.MainTopic
import com.tiagohs.cinema_history.models.contents.Content
import com.tiagohs.cinema_history.services.config.FakeInterceptor
import com.tiagohs.cinema_history.services.config.RxErrorHandlingCallAdapterFactory
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import java.io.IOException


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
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder()))
                .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
                .client(client)
                .build()
    }

    private fun client(): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
        val cacheSize = 30 * 1024 * 1024 // 30 MB
        val context = App.appContext

        if (context != null) {
            val cache = Cache(context.cacheDir, cacheSize.toLong())
            httpClient.cache(cache)
        }

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

                val builder = original.newBuilder()
                val newUrl = original.url.toString() + "&api_key=" + BuildConfig.THEMOVIEDB_API_KEY

                return chain.proceed(builder.url(newUrl).build())
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