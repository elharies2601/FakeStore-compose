package com.example.fakestore.domain

import com.example.fakestore.data.model.NetworkResult
import com.example.fakestore.data.model.login.LoginRes
import com.example.fakestore.data.model.user.UserRes

interface UserRepository {
    suspend fun doLogin(username: String, password: String): LoginRes
    suspend fun getProfileById(id: Int): UserRes
    suspend fun getProfileByUsername(username: String): NetworkResult<UserRes?>
    suspend fun getProfileByUsername(): NetworkResult<UserRes?>
}