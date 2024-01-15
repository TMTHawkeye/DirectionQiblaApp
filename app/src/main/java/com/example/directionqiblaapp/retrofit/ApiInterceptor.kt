package com.example.directionqiblaapp.retrofit

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class ApiInterceptor(context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
//        requestBuilder.let {
//        }
        return chain.proceed(requestBuilder.build())
    }
}