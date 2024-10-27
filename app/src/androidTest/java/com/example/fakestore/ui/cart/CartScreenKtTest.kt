package com.example.fakestore.ui.cart

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.fakestore.data.db.entity.CartEntity
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class CartScreenKtTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun cartScreen_showLoadingState() {
        composeTestRule.setContent {
            CartContent(
                cartUiState = CartUiState.Loading
            )
        }

        // Verify loading items are displayed
        repeat(3) {
            composeTestRule.onAllNodesWithTag("loading_item_cart")
                .assertCountEquals(3)
        }
    }

    @Test
    fun cartScreen_showEmptyState() {
        composeTestRule.setContent {
            CartContent(
                cartUiState = CartUiState.Empty
            )
        }

        composeTestRule.onNodeWithText("Cart is empty")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun cartScreen_showErrorState() {
        val errorMessage = "Error loading cart"
        composeTestRule.setContent {
            CartContent(
                cartUiState = CartUiState.Error(errorMessage)
            )
        }

        composeTestRule.onNodeWithText(errorMessage)
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun cartScreen_showItemsAndTotal() {
        val cartItems = mutableListOf(
            CartEntity(
                id = 1,
                title = "Test Product",
                price = 99.99,
                imageUrl = "https://example.com/image.jpg",
                quantity = 2, productId = 7155, username = "Christopher McGee"
            )
        )

        composeTestRule.setContent {
            CartContent(
                cartUiState = CartUiState.Success(
                    items = cartItems,
                    total = 199.98
                )
            )
        }

        // Verify product details are displayed
        composeTestRule.onNodeWithText("Test Product")
            .assertExists()
            .assertIsDisplayed()

        // Verify quantity controls
        composeTestRule.onNodeWithContentDescription("Decrease")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Increase")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("2")
            .assertExists()
            .assertIsDisplayed()

        // Verify total and checkout button
        composeTestRule.onNodeWithText("Total")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Rp 199")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Checkout")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun cartScreen_QuantityButtonsClicked() {
        var decreaseClicked = false
        var increaseClicked = false

        val cartItems = mutableListOf(
            CartEntity(
                id = 1,
                title = "Test Product",
                price = 99.99,
                imageUrl = "https://example.com/image.jpg",
                quantity = 1, productId = 2142, username = "Alice Chang"
            )
        )

        composeTestRule.setContent {
            CartContent(
                cartUiState = CartUiState.Success(
                    items = cartItems,
                    total = 99.99
                ),
                onQuantityChange = { _, isIncrement ->
                    if (isIncrement) increaseClicked = true
                    else decreaseClicked = true
                }
            )
        }

        // Click decrease button
        composeTestRule.onNodeWithContentDescription("Decrease")
            .performClick()
        assert(decreaseClicked)

        // Click increase button
        composeTestRule.onNodeWithContentDescription("Increase")
            .performClick()
        assert(increaseClicked)
    }

    @Test
    fun cartScreen_checkoutButtonClicked() {
        var checkoutClicked = false

        composeTestRule.setContent {
            CartContent(
                cartUiState = CartUiState.Success(
                    items = mutableListOf(),
                    total = 0.0
                ),
                onCheckout = { checkoutClicked = true }
            )
        }

        composeTestRule.onNodeWithText("Checkout")
            .performClick()

        assert(checkoutClicked)
    }
}