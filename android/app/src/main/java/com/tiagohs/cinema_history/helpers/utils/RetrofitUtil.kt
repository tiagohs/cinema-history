package com.tiagohs.cinema_history.helpers.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
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
            .baseUrl("https://local/")
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
        val cacheSize = 10 * 1024 * 1024 // 30 MB
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
        httpClient.addInterceptor(getCacheInterceptor(context))

        return httpClient.build()
    }

    private fun getCacheInterceptor(context: Context?): Interceptor {
        return object: Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                var request = chain.request()

                /*
                *  Leveraging the advantage of using Kotlin,
                *  we initialize the request and change its header depending on whether
                *  the device is connected to Internet or not.
                */
                request = if (hasNetwork(context)!!)
                /*
                *  If there is Internet, get the cache that was stored 5 seconds ago.
                *  If the cache is older than 5 seconds, then discard it,
                *  and indicate an error in fetching the response.
                *  The 'max-age' attribute is responsible for this behavior.
                */
                    request.newBuilder().header("Cache-Control", "public, max-age=" + 10).build()
                else
                /*
                *  If there is no Internet, get the cache that was stored 7 days ago.
                *  If the cache is older than 7 days, then discard it,
                *  and indicate an error in fetching the response.
                *  The 'max-stale' attribute is responsible for this behavior.
                *  The 'only-if-cached' attribute indicates to not retrieve new data; fetch the cache only instead.
                */
                    request.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7).build()
                // End of if-else statement

                // Add the modified request to the chain.
                return chain.proceed(request)
            }
        }
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

    fun hasNetwork(context: Context?): Boolean? {
        var isConnected: Boolean? = false
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        if (activeNetwork != null && activeNetwork.isConnected)
            isConnected = true

        return isConnected
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