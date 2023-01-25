package com.example.notetaking.api

import com.example.notetaking.utills.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor() : Interceptor {

    @Inject
    lateinit var tokenManager: TokenManager


    override fun intercept(chain: Interceptor.Chain): Response {
   var response = chain.request().newBuilder()

        var token= tokenManager.getToken()
        response.addHeader("Authorization","Bearer $token")

        return chain.proceed(response.build())

    }
}