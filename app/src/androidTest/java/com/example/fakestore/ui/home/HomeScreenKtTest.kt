package com.example.fakestore.ui.home

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.fakestore.data.model.UiState
import com.example.fakestore.data.model.product.ProductRes
import com.example.fakestore.data.model.user.UserRes
import com.example.fakestore.util.capitalize
import com.example.fakestore.util.toFormat
import org.junit.Rule
import org.junit.Test

class HomeScreenKtTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // Test data
    private val mockCategories = mutableListOf("electronics", "jewelry", "men's clothing")
    private val mockProducts = mutableListOf(
        ProductRes(
            id = 1,
            title = "Test Product 1",
            price = 100.0,
            description = "Test Description 1",
            category = "electronics",
            image = "https://test.com/image1.jpg",
            rating = ProductRes.Rating(rate = 4.5, count = 100)
        ),
        ProductRes(
            id = 2,
            title = "Test Product 2",
            price = 200.0,
            description = "Test Description 2",
            category = "jewelry",
            image = "https://test.com/image2.jpg",
            rating = ProductRes.Rating(rate = 4.0, count = 80)
        )
    )
    private val mockUser = UserRes(
        name = UserRes.Name(firstname = "John", lastname = "Doe"),
        phone = "123456789",
        address = UserRes.Address(
            street = "Test Street",
            city = "Test City",
            zipcode = "12345"
        )
    )

    @Test
    fun homeScreen_DisplaysAllComponents() {
        composeTestRule.setContent {
            HomeContent(
                uiStateCategories = UiState.Success(mockCategories),
                uiStateProducts = UiState.Success(mockProducts),
                uiStateUser = UiState.Success(mockUser),
                countCart = 2
            )
        }

        // Verify TopBar
        composeTestRule.onNodeWithText("Home").assertExists()

        // Verify Categories
        mockCategories.forEach { category ->
            composeTestRule.onNodeWithText(category).assertExists()
        }

        // Verify Products
        mockProducts.forEach { product ->
            composeTestRule.onNodeWithText(product.title).assertExists()
            composeTestRule.onNodeWithText(product.price.toFormat()).assertExists()
            composeTestRule.onNodeWithText(product.rating.rate.toString()).assertExists()
            composeTestRule.onNodeWithText("${product.rating.count} sold").assertExists()
        }
    }

    @Test
    fun homeScreen_ShowsLoadingState() {
        composeTestRule.setContent {
            HomeContent(
                uiStateCategories = UiState.Loading,
                uiStateProducts = UiState.Loading
            )
        }

        // Should show shimmer loading effects
        composeTestRule.onNodeWithTag("loading_category").assertExists()
        composeTestRule.onNodeWithTag("loading_products").assertExists()
    }

    @Test
    fun homeScreen_HandlesEmptyProducts() {
        composeTestRule.setContent {
            HomeContent(
                uiStateCategories = UiState.Success(mockCategories),
                uiStateProducts = UiState.Success(mutableListOf())
            )
        }

        composeTestRule.onNodeWithText("No Product Found").assertExists()
    }

    @Test
    fun homeScreen_ShowsProfileBottomSheet() {
        composeTestRule.setContent {
            HomeContent(
                uiStateCategories = UiState.Success(mockCategories),
                uiStateProducts = UiState.Success(mockProducts),
                uiStateUser = UiState.Success(mockUser)
            )
        }

        // Click profile icon
        composeTestRule.onNodeWithContentDescription("icon person").performClick()

        // Verify bottom sheet content
        composeTestRule.onNodeWithText("${mockUser.name.firstname} ${mockUser.name.lastname}".capitalize())
            .assertExists()
        composeTestRule.onNodeWithText(mockUser.phone).assertExists()
        composeTestRule.onNodeWithText("Logout").assertExists()
    }

    @Test
    fun homeScreen_HandlesLogout() {
        composeTestRule.setContent {
            HomeContent(uiStateUser = UiState.Success(mockUser))
        }

        // Open profile bottom sheet
        composeTestRule.onNodeWithTag("icon_person").performClick()

        // Click logout button
        composeTestRule.onNodeWithText("Logout").performClick()
    }

    @Test
    fun homeScreen_ShowsCartBadgeCount() {
        composeTestRule.setContent {
            HomeContent(
                uiStateCategories = UiState.Success(mockCategories),
                uiStateProducts = UiState.Success(mockProducts),
                countCart = 2
            )
        }

        // Verify cart badge shows correct count
        composeTestRule.onNodeWithText("2").assertExists()
    }
}