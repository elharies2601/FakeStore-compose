package com.example.fakestore.domain

import com.example.fakestore.data.model.NetworkResult
import com.example.fakestore.data.model.product.ProductRes

interface ProductRepository {
    suspend fun getProducts(): NetworkResult<MutableList<ProductRes>>
    suspend fun getProductById(id: Int): NetworkResult<ProductRes>
    suspend fun getCategories(): NetworkResult<MutableList<String>>
    suspend fun getProductsByCategory(category: String): NetworkResult<MutableList<ProductRes>>
}