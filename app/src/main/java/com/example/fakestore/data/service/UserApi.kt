package com.example.fakestore.data.service

import com.example.fakestore.data.model.login.LoginReq
import com.example.fakestore.data.model.login.LoginRes
import com.example.fakestore.data.model.user.UserRes
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginReq): LoginRes

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Int): UserRes

    @GET("users")
    suspend fun getUsers(): MutableList<UserRes>
}