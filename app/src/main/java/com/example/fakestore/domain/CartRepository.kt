package com.example.fakestore.domain

import com.example.fakestore.data.db.entity.CartEntity
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    suspend fun getCarts(): Flow<MutableList<CartEntity>>
    suspend fun insertCart(cartEntity: CartEntity)
    suspend fun updateCart(cartEntity: CartEntity)
    suspend fun deleteCart(cartEntity: CartEntity)
    suspend fun updateCart(itemId: Int, increment: Boolean)
    suspend fun deleteCart(itemId: Int)
    suspend fun isProductInCart(idProduct: Int): Boolean
    suspend fun getCartItemByProductId(productId: Int): CartEntity?
    suspend fun deleteAllCarts()
}