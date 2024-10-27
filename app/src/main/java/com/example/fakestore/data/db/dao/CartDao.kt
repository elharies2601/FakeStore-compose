package com.example.fakestore.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.fakestore.data.db.entity.CartEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Query("SELECT * FROM carts")
    @Transaction
    fun getAllCartItems(): Flow<MutableList<CartEntity>>

    @Query("SELECT * FROM carts WHERE username = :username")
    @Transaction
    fun getAllCartItems(username: String): Flow<MutableList<CartEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM carts WHERE productId = :productId LIMIT 1)")
    suspend fun isProductInCart(productId: Int): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM carts WHERE productId = :productId AND username = :username LIMIT 1)")
    suspend fun isProductInCart(productId: Int, username: String): Boolean

    @Query("SELECT * FROM carts WHERE productId = :productId LIMIT 1")
    suspend fun getCartItemByProductId(productId: Int): CartEntity?

    @Query("SELECT * FROM carts WHERE productId = :productId AND username = :username LIMIT 1")
    suspend fun getCartItemByProductId(productId: Int, username: String): CartEntity?

    @Query("SELECT * FROM carts WHERE id = :id AND username = :username LIMIT 1")
    suspend fun getCartItemById(id: Int, username: String): CartEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(cartItem: CartEntity)

    @Update
    suspend fun updateCartItem(cartItem: CartEntity)

    @Delete
    suspend fun deleteCartItem(cartItem: CartEntity)

    @Query("DELETE FROM carts WHERE id = :id AND username = :username")
    fun deleteCartItem(id: Int, username: String)

    @Query("DELETE FROM carts")
    fun clearCart()

    @Query("DELETE FROM carts WHERE username = :username")
    fun clearCart(username: String)
}