package com.example.fakestore.network

import com.example.fakestore.data.datastore.UserDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(private val userDataStore: UserDataStore): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking(Dispatchers.IO) { userDataStore.authToken.first() }
        val request = chain.request().newBuilder()
            .apply {
                token?.let {
                    addHeader("Authorization", "Bearer $it")
                }
            }
            .build()
        return chain.proceed(request)
    }
}