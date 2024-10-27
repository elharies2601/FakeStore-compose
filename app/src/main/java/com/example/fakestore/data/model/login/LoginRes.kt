package com.example.fakestore.data.model.login

import com.google.gson.annotations.SerializedName

data class LoginRes(
    @SerializedName("token")
    val token: String = ""
)
