package com.example.fakestore.data.model.user


import com.google.gson.annotations.SerializedName

data class UserRes(
    @SerializedName("address")
    val address: Address = Address(),
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("email")
    val email: String = "",
    @SerializedName("username")
    val username: String = "",
    @SerializedName("password")
    val password: String = "",
    @SerializedName("name")
    val name: Name = Name(),
    @SerializedName("phone")
    val phone: String = "",
    @SerializedName("__v")
    val v: Int = 0
) {
    data class Address(
        @SerializedName("geolocation")
        val geolocation: Geolocation = Geolocation(),
        @SerializedName("city")
        val city: String = "",
        @SerializedName("street")
        val street: String = "",
        @SerializedName("number")
        val number: Int = 0,
        @SerializedName("zipcode")
        val zipcode: String = ""
    ) {
        data class Geolocation(
            @SerializedName("lat")
            val lat: String = "",
            @SerializedName("long")
            val long: String = ""
        )
    }

    data class Name(
        @SerializedName("firstname")
        val firstname: String = "",
        @SerializedName("lastname")
        val lastname: String = ""
    )
}