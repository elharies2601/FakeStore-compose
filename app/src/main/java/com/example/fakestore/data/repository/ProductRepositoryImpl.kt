package com.example.fakestore.data.repository

import com.example.fakestore.data.model.NetworkResult
import com.example.fakestore.data.model.product.ProductRes
import com.example.fakestore.data.service.ProductApi
import com.example.fakestore.domain.ProductRepository
import com.example.fakestore.util.safeCallApi
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(private val productApi: ProductApi): ProductRepository {
    override suspend fun getProducts(): NetworkResult<MutableList<ProductRes>> {
        return safeCallApi { productApi.getProducts() }
    }

    override suspend fun getProductById(id: Int): NetworkResult<ProductRes> {
        return safeCallApi { productApi.getProductById(id) }
    }

    override suspend fun getCategories(): NetworkResult<MutableList<String>> {
        return safeCallApi { productApi.getProductsCategories() }
    }

    override suspend fun getProductsByCategory(category: String): NetworkResult<MutableList<ProductRes>> {
        return safeCallApi { productApi.getProductsByCategory(category) }
    }
}