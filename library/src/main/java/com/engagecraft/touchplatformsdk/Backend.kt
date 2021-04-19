package com.engagecraft.touchplatformsdk

import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

internal class Backend {
    companion object {
        private const val URL = "https://api%s.touch.global/api/"
        private const val HEADER_ACCEPT = "Accept"
        private const val HEADER_API_KEY = "x-api-client"
        private const val API_KEY = "AN664b9cA2kwBWoNQJomX45nh"

        private var service : Interface? = null
        private var apiEnv : String? = null

        fun get() : Interface {
            if (service == null || apiEnv != TouchPlatformSDK.environment) {
                apiEnv = TouchPlatformSDK.environment
                val retrofit = Retrofit.Builder()
                    .baseUrl(Util.prepareUrl(URL))
                    .client(OkHttpClient.Builder().apply {
                        addInterceptor {
                            it.proceed(it.request().newBuilder().apply {
                                addHeader(HEADER_ACCEPT, "application/json")
                                addHeader(HEADER_API_KEY, API_KEY)
                            }.build())
                        }

                        if (BuildConfig.DEBUG) {
                            addInterceptor(HttpLoggingInterceptor().apply {
                                level = HttpLoggingInterceptor.Level.BODY
                            })
                        }
                    }.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                service = retrofit.create(Interface::class.java)
            }
            return service!!
        }
    }

    data class Response<T>(
        @SerializedName("data") var data: T?,
        @SerializedName("error") var error: Error?
    )

    data class Error(
        @SerializedName("type") var type: String,
        @SerializedName("message") var message: String
    )

    data class Availability(
        @SerializedName("available") var available: Boolean,
        @SerializedName("height") var height: Int
    )

    interface Interface {
        @GET("v1/widget/{widgetHash}/availability")
        suspend fun availability(
            @Path("widgetHash") widgetHash: String
        ) : Response<Availability>?
    }
}