package com.example.fakestore.data.model.login

import com.google.gson.annotations.SerializedName

data class LoginReq(
    @SerializedName("username")
    val username: String = "",
    @SerializedName("password")
    val password: String = ""
)
