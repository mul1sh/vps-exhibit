package com.verve.vps.helpers

import android.content.Context

import com.verve.vps.BuildConfig
import com.verve.vps.R
import com.verve.vps.helpers.AccessTokenHelper.handleAccessTokenExpiry
import com.verve.vps.helpers.AccessTokenHelper.saveAccessToken
import com.verve.vps.helpers.AccessTokenHelper.getAccessToken
import com.verve.vps.services.ApiService

import okhttp3.OkHttpClient
import okhttp3.Interceptor

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

import timber.log.Timber
import java.util.concurrent.TimeUnit

class RetrofitHelper(private val context: Context) {
    private val TAG_API_SERVICE  = RetrofitHelper::class.simpleName
    // init the api service
    internal fun getApiService(): ApiService {
            return Retrofit.Builder()
                .baseUrl(context.getString(R.string.api_baseUrl))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient())
                .build()
                .create(ApiService::class.java)
    }

    private fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Timber.tag(TAG_API_SERVICE).d(message)
            }
        })
        if (BuildConfig.DEBUG) {
            interceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            // TODO - change to HEADERS on production...
            interceptor.level = HttpLoggingInterceptor.Level.BODY
        }

        return interceptor
    }

    private fun provideHttpResponseInterceptor() = Interceptor { chain ->
            val request = chain.request()
            var response = chain.proceed(request)
            if (response.code() == 403) { // check if access token has expired or is invalid
                val accessToken = handleAccessTokenExpiry().access_token
                if (accessToken.isNotEmpty()) {
                    // save the token first
                    saveAccessToken(accessToken)
                    // retry the request again with the correct token
                    response = chain.proceed(request.newBuilder().header(
                        "Authorization", getAccessToken()
                    ).build())
                }
            }
            response
    }

    private fun okHttpClient() =
            OkHttpClient
                .Builder()
                .addInterceptor(provideHttpLoggingInterceptor())
                .addInterceptor(provideHttpResponseInterceptor())
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build()
}