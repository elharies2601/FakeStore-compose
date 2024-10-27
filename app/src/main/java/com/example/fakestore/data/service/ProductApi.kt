package com.example.fakestore.data.service

import com.example.fakestore.data.model.product.ProductRes
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductApi {
    @GET("products")
    suspend fun getProducts(): MutableList<ProductRes>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Int): ProductRes

    @GET("products/categories")
    suspend fun getProductsCategories(): MutableList<String>

    @GET("products/category/{category}")
    suspend fun getProductsByCategory(@Path("category") category: String): MutableList<ProductRes>

}