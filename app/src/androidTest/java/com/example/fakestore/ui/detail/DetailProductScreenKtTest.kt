package com.example.fakestore.ui.detail

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.fakestore.data.model.UiState
import com.example.fakestore.data.model.product.ProductRes
import com.example.fakestore.util.capitalize
import org.junit.Rule
import org.junit.Test

class DetailProductScreenKtTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockProduct = ProductRes(
        id = 1,
        title = "Test Product",
        price = 100.0,
        description = "Test Description",
        category = "test category",
        image = "https://test.com/image.jpg",
        rating = ProductRes.Rating(rate = 4.5, count = 100)
    )

    @Test
    fun detailProductContent_DisplaysCorrectInformation() {
        composeTestRule.setContent {
            DetailProductContent(
                uiStateDetail = UiState.Success(mockProduct),
                username = "testuser"
            )
        }

        // Verify product title is displayed
        composeTestRule
            .onNodeWithText(mockProduct.title)
            .assertExists()
            .assertIsDisplayed()

        // Verify product category is displayed
        composeTestRule
            .onNodeWithText(mockProduct.category.capitalize())
            .assertExists()
            .assertIsDisplayed()

        // Verify product description is displayed
        composeTestRule
            .onNodeWithText(mockProduct.description)
            .assertExists()
            .assertIsDisplayed()

        // Verify rating is displayed
        composeTestRule
            .onNodeWithText(mockProduct.rating.rate.toString())
            .assertExists()
            .assertIsDisplayed()

        // Verify sold count is displayed
        composeTestRule
            .onNodeWithText("${mockProduct.rating.count} Sold")
            .assertExists()
            .assertIsDisplayed()

        // Verify Add Cart button exists
        composeTestRule
            .onNodeWithText("Add Cart")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun detailProductContent_ShowsLoadingState() {
        composeTestRule.setContent {
            DetailProductContent(
                uiStateDetail = UiState.Loading,
                username = "testuser"
            )
        }

        // Verify that the content is not displayed in loading state
        composeTestRule
            .onNodeWithText("Description")
            .assertDoesNotExist()
    }

    @Test
    fun detailProductContent_AddToCartSuccess() {
        var cartAdded = false

        composeTestRule.setContent {
            DetailProductContent(
                uiStateDetail = UiState.Success(mockProduct),
                uiStateInserted = UiState.Idle,
                username = "testuser",
                onAddCart = { cartAdded = true }
            )
        }

        // Click add to cart button
        composeTestRule
            .onNodeWithText("Add Cart")
            .performClick()

        // Verify cart was added
        assert(cartAdded)
    }

    @Test
    fun detailProductContent_ShowsSnackbarOnSuccess() {
        composeTestRule.setContent {
            DetailProductContent(
                uiStateDetail = UiState.Success(mockProduct),
                uiStateInserted = UiState.Success(1),
                username = "testuser"
            )
        }

        // Verify success snackbar is shown
        composeTestRule
            .onNodeWithText("Success Add Cart")
            .assertExists()
    }

    @Test
    fun detailProductContent_ShowsSnackbarOnFailure() {
        val errorMessage = "Error adding to cart"

        composeTestRule.setContent {
            DetailProductContent(
                uiStateDetail = UiState.Success(mockProduct),
                uiStateInserted = UiState.Failed(errorMessage),
                username = "testuser"
            )
        }

        // Verify error snackbar is shown
        composeTestRule
            .onNodeWithText("Failed Add Cart: $errorMessage")
            .assertExists()
    }
}