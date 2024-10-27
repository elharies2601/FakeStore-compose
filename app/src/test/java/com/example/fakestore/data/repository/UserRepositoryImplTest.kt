package com.example.fakestore.data.repository

import com.example.fakestore.data.datastore.UserDataStore
import com.example.fakestore.data.model.NetworkResult
import com.example.fakestore.data.model.login.LoginReq
import com.example.fakestore.data.model.login.LoginRes
import com.example.fakestore.data.model.user.UserRes
import com.example.fakestore.data.service.UserApi
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import java.io.IOException

class UserRepositoryImplTest {

    @MockK
    private lateinit var userApi: UserApi

    @MockK
    private lateinit var dataStore: UserDataStore

    private lateinit var userRepository: UserRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        userRepository = UserRepositoryImpl(userApi, dataStore)
    }

    @Test
    fun `doLogin returns login response when credentials are valid`() = runBlocking {
        // Given
        val username = "testUser"
        val password = "testPass"
        val loginReq = LoginReq(username, password)
        val expectedResponse = LoginRes(token = "test-token")

        coEvery { userApi.login(loginReq) } returns expectedResponse

        // When
        val result = userRepository.doLogin(username, password)

        // Then
        assertEquals(expectedResponse, result)
        coVerify(exactly = 1) { userApi.login(loginReq) }
    }

    @Test(expected = Exception::class)
    fun `doLogin throws exception when credentials are invalid`(): Unit = runBlocking {
        // Given
        val username = "testUser"
        val password = "wrongPass"
        val loginReq = LoginReq(username, password)

        coEvery { userApi.login(loginReq) } throws Exception("Invalid credentials")

        // When
        userRepository.doLogin(username, password)

        // Then
        // Exception is expected
    }

    @Test
    fun `getProfileById returns user data when id exists`() = runBlocking {
        // Given
        val userId = 1
        val expectedUser = UserRes(
            id = userId,
            username = "testUser",
            email = "test@example.com"
        )

        coEvery { userApi.getUserById(userId) } returns expectedUser

        // When
        val result = userRepository.getProfileById(userId)

        // Then
        assertEquals(expectedUser, result)
        coVerify(exactly = 1) { userApi.getUserById(userId) }
    }

    @Test
    fun `getProfileByUsername returns success with user when username exists`() = runBlocking {
        // Given
        val username = "testUser"
        val users = mutableListOf(
            UserRes(id = 1, username = "testUser", email = "test@example.com"),
            UserRes(id = 2, username = "otherUser", email = "other@example.com")
        )

        coEvery { userApi.getUsers() } returns users

        // When
        val result = userRepository.getProfileByUsername(username)

        // Then
        assertTrue(result is NetworkResult.Success)
        assertEquals(users[0], (result as NetworkResult.Success).data)
    }

    @Test
    fun `getProfileByUsername returns success with null when username doesn't exist`() = runBlocking {
        // Given
        val username = "nonExistentUser"
        val users = mutableListOf(
            UserRes(id = 1, username = "testUser", email = "test@example.com"),
            UserRes(id = 2, username = "otherUser", email = "other@example.com")
        )

        coEvery { userApi.getUsers() } returns users

        // When
        val result = userRepository.getProfileByUsername(username)

        // Then
        assertTrue(result is NetworkResult.Success)
        assertNull((result as NetworkResult.Success).data)
    }

    @Test
    fun `getProfileByUsername returns error when network error occurs`() = runBlocking {
        // Given
        val username = "testUser"
        coEvery { userApi.getUsers() } throws IOException()

        // When
        val result = userRepository.getProfileByUsername(username)

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals("Network error", (result as NetworkResult.Error).message)
    }

    @Test
    fun `getProfileByUsername with no parameter uses stored username`() = runBlocking {
        // Given
        val storedUsername = "storedUser"
        val users = mutableListOf(
            UserRes(id = 1, username = "storedUser", email = "stored@example.com"),
            UserRes(id = 2, username = "otherUser", email = "other@example.com")
        )

        coEvery { dataStore.username } returns flowOf(storedUsername)
        coEvery { userApi.getUsers() } returns users

        // When
        val result = userRepository.getProfileByUsername()

        // Then
        assertTrue(result is NetworkResult.Success)
        assertEquals(users[0], (result as NetworkResult.Success).data)
    }

    @Test
    fun `getProfileByUsername with no parameter handles network error`() = runBlocking {
        // Given
        coEvery { dataStore.username } returns flowOf("storedUser")
        coEvery { userApi.getUsers() } throws IOException()

        // When
        val result = userRepository.getProfileByUsername()

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals("Network error", (result as NetworkResult.Error).message)
    }
}