package com.example.fakestore.data.repository

import com.example.fakestore.data.datastore.UserDataStore
import com.example.fakestore.data.db.dao.CartDao
import com.example.fakestore.data.db.entity.CartEntity
import com.example.fakestore.domain.CartRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(private val cartDao: CartDao, private val dataStore: UserDataStore): CartRepository {

    private val username by lazy(LazyThreadSafetyMode.NONE) {
        runBlocking {
            dataStore.username.first() ?: ""
        }
    }

    override suspend fun getCarts(): Flow<MutableList<CartEntity>> {
        return withContext(Dispatchers.IO) {
            cartDao.getAllCartItems(username = username)
        }
    }

    override suspend fun insertCart(cartEntity: CartEntity) {
        cartDao.insertCartItem(cartEntity)
    }

    override suspend fun updateCart(cartEntity: CartEntity) {
        cartDao.updateCartItem(cartEntity)
    }

    override suspend fun deleteCart(cartEntity: CartEntity) {
        cartDao.deleteCartItem(cartEntity.id, username)
    }

    override suspend fun deleteAllCarts() {
        cartDao.clearCart(username)
    }

    override suspend fun isProductInCart(idProduct: Int): Boolean {
        return withContext(Dispatchers.IO) {
            cartDao.isProductInCart(idProduct, username)
        }
    }

    override suspend fun getCartItemByProductId(productId: Int): CartEntity? {
        return withContext(Dispatchers.IO) {
            cartDao.getCartItemByProductId(productId, username)
        }
    }

    override suspend fun updateCart(itemId: Int, increment: Boolean) {
        val item = cartDao.getCartItemById(itemId, username) ?: return
        val newQuantity = if (increment) item.quantity + 1 else (item.quantity - 1).coerceAtLeast(0)
        if (newQuantity >= 1) {
            updateCart(item.copy(quantity = newQuantity))
        } else {
            deleteCart(itemId)
        }
    }

    override suspend fun deleteCart(itemId: Int) {
        cartDao.getCartItemById(itemId, username)?.let { item ->
            cartDao.deleteCartItem(item)
        }
    }
}