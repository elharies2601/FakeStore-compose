package com.example.fakestore.data.repository

import com.example.fakestore.data.datastore.UserDataStore
import com.example.fakestore.data.model.NetworkResult
import com.example.fakestore.data.model.login.LoginReq
import com.example.fakestore.data.model.login.LoginRes
import com.example.fakestore.data.model.user.UserRes
import com.example.fakestore.data.service.UserApi
import com.example.fakestore.domain.UserRepository
import com.example.fakestore.util.safeCallApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(private val userApi: UserApi, private val dataStore: UserDataStore): UserRepository {

    private val username by lazy(LazyThreadSafetyMode.NONE) {
        runBlocking {
            dataStore.username.first() ?: ""
        }
    }

    override suspend fun doLogin(username: String, password: String): LoginRes {
        return userApi.login(LoginReq(username, password))
    }

    override suspend fun getProfileById(id: Int): UserRes {
        return userApi.getUserById(id)
    }

    override suspend fun getProfileByUsername(username: String): NetworkResult<UserRes?> {
        return safeCallApi {
            userApi.getUsers().find {
                it.username == username
            }
        }
    }

    override suspend fun getProfileByUsername(): NetworkResult<UserRes?> {
        return getProfileByUsername(username)
    }
}