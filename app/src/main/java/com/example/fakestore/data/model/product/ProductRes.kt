package com.example.fakestore.data.model.product


import com.example.fakestore.data.db.entity.CartEntity
import com.google.gson.annotations.SerializedName

data class ProductRes(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("title")
    val title: String = "",
    @SerializedName("price")
    val price: Double = 0.0,
    @SerializedName("description")
    val description: String = "",
    @SerializedName("category")
    val category: String = "",
    @SerializedName("image")
    val image: String = "",
    @SerializedName("rating")
    val rating: Rating = Rating()
) {
    data class Rating(
        @SerializedName("rate")
        val rate: Double = 0.0,
        @SerializedName("count")
        val count: Int = 0
    )

    companion object {
        fun ProductRes.mapperToCart(username: String): CartEntity {
            return CartEntity(
                productId = this.id,
                title = this.title,
                price = this.price,
                imageUrl = this.image,
                quantity = 1,
                username = username
            )
        }
    }
}