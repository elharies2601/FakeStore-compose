package com.example.fakestore.data.repository

import com.example.fakestore.data.datastore.UserDataStore
import com.example.fakestore.data.db.dao.CartDao
import com.example.fakestore.data.db.entity.CartEntity
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CartRepositoryImplTest {

    @MockK
    private lateinit var cartDao: CartDao

    @MockK
    private lateinit var dataStore: UserDataStore

    private lateinit var cartRepository: CartRepositoryImpl

    private val testUsername = "testUser"
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        Dispatchers.setMain(testDispatcher)
        coEvery { dataStore.username } returns flowOf(testUsername)
        cartRepository = CartRepositoryImpl(cartDao, dataStore)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getCarts returns flow of cart items for current user`() = runBlocking {
        // Given
        val cartItems = mutableListOf(
            CartEntity(id = 1, productId = 1, username = testUsername, quantity = 2, title = "title",
                price = 20.0,
                imageUrl = "imageUrl"),
            CartEntity(
                id = 2, productId = 2, username = testUsername, quantity = 1,
                title = "title",
                price = 20.0,
                imageUrl = "imageUrl"
            )
        )
        coEvery { cartDao.getAllCartItems(testUsername) } returns flowOf(cartItems)

        // When
        val result = cartRepository.getCarts().first()

        // Then
        assertEquals(cartItems, result)
        coVerify { cartDao.getAllCartItems(testUsername) }
    }

    @Test
    fun `insertCart calls dao to insert cart item`() = runBlocking {
        // Given
        val cartItem = CartEntity(id = 1, productId = 1, username = testUsername, quantity = 1,
            title = "dolores",
            price = 2.3,
            imageUrl = "https://search.yahoo.com/search?p=scripserit"
        )

        // When
        cartRepository.insertCart(cartItem)

        // Then
        coVerify { cartDao.insertCartItem(cartItem) }
    }

    @Test
    fun `updateCart calls dao to update cart item`() = runBlocking {
        // Given
        val cartItem = CartEntity(id = 1, productId = 1, username = testUsername, quantity = 2,
            title = "graeco",
            price = 6.7,
            imageUrl = "http://www.bing.com/search?q=sonet"
        )

        // When
        cartRepository.updateCart(cartItem)

        // Then
        coVerify { cartDao.updateCartItem(cartItem) }
    }

    @Test
    fun `deleteCart by entity calls dao to delete cart item`() = runBlocking {
        // Given
        val cartItem = CartEntity(id = 1, productId = 1, username = testUsername, quantity = 1,
            title = "vivamus",
            price = 10.11,
            imageUrl = "https://search.yahoo.com/search?p=tota"
        )

        // When
        cartRepository.deleteCart(cartItem)

        // Then
        coVerify { cartDao.deleteCartItem(cartItem.id, testUsername) }
    }

    @Test
    fun `deleteAllCarts calls dao to clear all cart items for current user`() = runBlocking {
        // When
        cartRepository.deleteAllCarts()

        // Then
        coVerify { cartDao.clearCart(testUsername) }
    }

    @Test
    fun `isProductInCart returns true when product exists in cart`() = runBlocking {
        // Given
        val productId = 1
        coEvery { cartDao.isProductInCart(productId, testUsername) } returns true

        // When
        val result = cartRepository.isProductInCart(productId)

        // Then
        assertTrue(result)
        coVerify { cartDao.isProductInCart(productId, testUsername) }
    }

    @Test
    fun `getCartItemByProductId returns cart item when it exists`() = runBlocking {
        // Given
        val productId = 1
        val cartItem = CartEntity(id = 1, productId = productId, username = testUsername, quantity = 1,
            title = "causae",
            price = 14.15,
            imageUrl = "https://www.google.com/#q=affert"
        )
        coEvery { cartDao.getCartItemByProductId(productId, testUsername) } returns cartItem

        // When
        val result = cartRepository.getCartItemByProductId(productId)

        // Then
        assertEquals(cartItem, result)
        coVerify { cartDao.getCartItemByProductId(productId, testUsername) }
    }

    @Test
    fun `updateCart with increment increases quantity`() = runBlocking {
        // Given
        val itemId = 1
        val initialItem = CartEntity(id = itemId, productId = 1, username = testUsername, quantity = 1,
            title = "netus",
            price = 18.19,
            imageUrl = "https://search.yahoo.com/search?p=interdum"
        )
        val expectedUpdatedItem = initialItem.copy(quantity = 2)

        coEvery { cartDao.getCartItemById(itemId, testUsername) } returns initialItem

        // When
        cartRepository.updateCart(itemId, increment = true)

        // Then
        coVerify { cartDao.updateCartItem(expectedUpdatedItem) }
    }

    @Test
    fun `updateCart with decrement decreases quantity`() = runBlocking {
        // Given
        val itemId = 1
        val initialItem = CartEntity(id = itemId, productId = 1, username = testUsername, quantity = 2,
            title = "doctus",
            price = 22.23,
            imageUrl = "https://search.yahoo.com/search?p=indoctum"
        )
        val expectedUpdatedItem = initialItem.copy(quantity = 1)

        coEvery { cartDao.getCartItemById(itemId, testUsername) } returns initialItem

        // When
        cartRepository.updateCart(itemId, increment = false)

        // Then
        coVerify { cartDao.updateCartItem(expectedUpdatedItem) }
    }

    @Test
    fun `updateCart with decrement deletes item when quantity becomes zero`() = runBlocking {
        // Given
        val itemId = 1
        val initialItem = CartEntity(id = itemId, productId = 1, username = testUsername, quantity = 1,
            title = "nam",
            price = 26.27,
            imageUrl = "https://duckduckgo.com/?q=intellegat"
        )

        coEvery { cartDao.getCartItemById(itemId, testUsername) } returns initialItem

        // When
        cartRepository.updateCart(itemId, increment = false)

        // Then
        coVerify { cartDao.deleteCartItem(initialItem) }
    }

    @Test
    fun `deleteCart by id deletes existing cart item`() = runBlocking {
        // Given
        val itemId = 1
        val cartItem = CartEntity(id = itemId, productId = 1, username = testUsername, quantity = 1,
            title = "mutat",
            price = 30.31,
            imageUrl = "https://www.google.com/#q=at"
        )
        coEvery { cartDao.getCartItemById(itemId, testUsername) } returns cartItem

        // When
        cartRepository.deleteCart(itemId)

        // Then
        coVerify { cartDao.deleteCartItem(cartItem) }
    }

    @Test
    fun `deleteCart by id does nothing when item doesn't exist`() = runBlocking {
        // Given
        val itemId = 1
        coEvery { cartDao.getCartItemById(itemId, testUsername) } returns null

        // When
        cartRepository.deleteCart(itemId)

        // Then
        coVerify(exactly = 0) { cartDao.deleteCartItem(any<CartEntity>()) }
    }

    @Test
    fun `repository uses username from datastore`() = runBlocking {
        // Given
        val differentUsername = "differentUser"
        coEvery { dataStore.username } returns flowOf(differentUsername)
        val newRepository = CartRepositoryImpl(cartDao, dataStore)

        // When
        newRepository.deleteAllCarts()

        // Then
        coVerify { cartDao.clearCart(differentUsername) }
    }
}