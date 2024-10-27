package com.example.fakestore.data.repository

import com.example.fakestore.data.model.NetworkResult
import com.example.fakestore.data.model.product.ProductRes
import com.example.fakestore.data.service.ProductApi
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class ProductRepositoryImplTest {

    @MockK
    lateinit var api: ProductApi

    private lateinit var repository: ProductRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        repository = ProductRepositoryImpl(api)
    }

    @Test
    fun `getProducts returns success when API call is successful`() = runBlocking {
        // Given
        val mockProducts = mutableListOf(
            ProductRes(id = 1, title = "Product 1", price = 100.0),
            ProductRes(id = 2, title = "Product 2", price = 200.0)
        )
        coEvery { api.getProducts() } returns mockProducts

        // When
        val result = repository.getProducts()

        // Then
        assertTrue(result is NetworkResult.Success)
        assertEquals(mockProducts, (result as NetworkResult.Success).data)
    }

    @Test
    fun `getProducts returns error when network error occurs`() = runBlocking {
        // Given
        coEvery { api.getProducts() } throws IOException()

        // When
        val result = repository.getProducts()

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals("Network error", (result as NetworkResult.Error).message)
    }

    @Test
    fun `getProductById returns success when API call is successful`() = runBlocking {
        // Given
        val mockProduct = ProductRes(id = 1, title = "Product 1", price = 100.0)
        coEvery { api.getProductById(1) } returns mockProduct

        // When
        val result = repository.getProductById(1)

        // Then
        assertTrue(result is NetworkResult.Success)
        assertEquals(mockProduct, (result as NetworkResult.Success).data)
    }

    @Test
    fun `getProductById returns error when product not found`() = runBlocking {
        // Given
        val mockResponse = Response.error<ProductRes>(
            404,
            "".toResponseBody(null)
        )
        coEvery { api.getProductById(1) } throws HttpException(mockResponse)

        // When
        val result = repository.getProductById(1)

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals("Resource not found", (result as NetworkResult.Error).message)
    }

    @Test
    fun `getCategories returns success when API call is successful`() = runBlocking {
        // Given
        val mockCategories = mutableListOf("Electronics", "Clothing", "Books")
        coEvery { api.getProductsCategories() } returns mockCategories

        // When
        val result = repository.getCategories()

        // Then
        assertTrue(result is NetworkResult.Success)
        assertEquals(mockCategories, (result as NetworkResult.Success).data)
    }

    @Test
    fun `getProductsByCategory returns success when API call is successful`() = runBlocking {
        // Given
        val category = "Electronics"
        val mockProducts = mutableListOf(
            ProductRes(id = 1, title = "Product 1", price = 100.0),
            ProductRes(id = 2, title = "Product 2", price = 200.0)
        )
        coEvery { api.getProductsByCategory(category) } returns mockProducts

        // When
        val result = repository.getProductsByCategory(category)

        // Then
        assertTrue(result is NetworkResult.Success)
        assertEquals(mockProducts, (result as NetworkResult.Success).data)
    }

    @Test
    fun `test different HTTP error codes`() = runBlocking {
        // 401 Unauthorized
        val unauthorizedResponse = Response.error<ProductRes>(
            401,
            "".toResponseBody(null)
        )
        coEvery { api.getProducts() } throws HttpException(unauthorizedResponse)
        var result = repository.getProducts()
        assertTrue(result is NetworkResult.Error)
        assertEquals("Authentication failed", (result as NetworkResult.Error).message)

        // 403 Forbidden
        val forbiddenResponse = Response.error<ProductRes>(
            403,
            "".toResponseBody(null)
        )
        coEvery { api.getProducts() } throws HttpException(forbiddenResponse)
        result = repository.getProducts()
        assertTrue(result is NetworkResult.Error)
        assertEquals("Access denied", (result as NetworkResult.Error).message)

        // Other HTTP error
        val otherErrorResponse = Response.error<ProductRes>(
            500,
            "".toResponseBody(null)
        )
        coEvery { api.getProducts() } throws HttpException(otherErrorResponse)
        result = repository.getProducts()
        assertTrue(result is NetworkResult.Error)
        assertEquals("An error occurred", (result as NetworkResult.Error).message)
    }
}