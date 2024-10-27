package com.example.fakestore.ui.summary

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.fakestore.data.db.entity.CartEntity
import com.example.fakestore.data.model.UiState
import com.example.fakestore.data.model.user.UserRes
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class SummaryScreenKtTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val sampleAddress = UserRes.Address(
        city = "New York",
        street = "Broadway",
        number = 123, geolocation = UserRes.Address.Geolocation(
            lat = "per",
            long = "vocibus"
        ), zipcode = "123"
    )

    private val sampleCart = CartEntity(
        id = 1,
        title = "Sample Product",
        price = 99.99,
        imageUrl = "https://example.com/image.jpg",
        quantity = 2, productId = 6840, username = "Terrell Garrett"
    )

    @Test
    fun summaryScreen_showLoadingStates() {
        composeTestRule.setContent {
            SummaryContent(
                summaryUiState = SummaryUiState.Loading,
                addressState = UiState.Loading
            )
        }

        // Verify loading states
        composeTestRule.onAllNodesWithTag("loading_address")
            .assertCountEquals(1)

        composeTestRule.onAllNodesWithTag("loading_item_summary")
            .assertCountEquals(2)
    }

    @Test
    fun summaryScreen_showAddressContent() {
        composeTestRule.setContent {
            SummaryContent(
                addressState = UiState.Success(sampleAddress)
            )
        }

        composeTestRule.onNodeWithText("Delivery Location")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("New York, Broadway, 123")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun summaryScreen_showErrorMessageAddress() {
        val errorMessage = "Failed to load address"

        composeTestRule.setContent {
            SummaryContent(
                addressState = UiState.Failed(errorMessage)
            )
        }

        composeTestRule.onNodeWithText(errorMessage)
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun summaryScreen_showSummaryProductDetails() {
        composeTestRule.setContent {
            SummaryContent(
                summaryUiState = SummaryUiState.Success(
                    items = mutableListOf(sampleCart),
                    total = 200.0
                )
            )
        }

        // Verify product details
        composeTestRule.onNodeWithText("Product Detail")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(sampleCart.title)
            .assertExists()
            .assertIsDisplayed()

        // Verify quantity and price
        composeTestRule.onNodeWithText("2 x Rp 99")
            .assertExists()
            .assertIsDisplayed()

        // Verify total
        composeTestRule.onNodeWithText("Total Purchase")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Rp 200")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun summaryScreen_showSummaryEmptyState() {
        composeTestRule.setContent {
            SummaryContent(
                summaryUiState = SummaryUiState.Success(
                    items = mutableListOf(),
                    total = 0.0
                )
            )
        }

        composeTestRule.onNodeWithText("No Items Found")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun summaryScreen_showSummaryErrorMessage() {
        val errorMessage = "Failed to load summary"

        composeTestRule.setContent {
            SummaryContent(
                summaryUiState = SummaryUiState.Error(errorMessage)
            )
        }

        composeTestRule.onNodeWithText(errorMessage)
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun summaryScreen_showLoadingInButton() {
        var checkoutClicked = false

        composeTestRule.setContent {
            SummaryContent(
                deletedState = UiState.Loading,
                onDeleted = { checkoutClicked = true }
            )
        }

        // Verify checkout button
        composeTestRule.onNodeWithTag("checkout_button")
            .assertExists()
            .assertIsDisplayed()
            .performClick()

        assert(checkoutClicked)

        // Verify loading indicator
        composeTestRule.onNodeWithTag("checkout_loading")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun summaryScreen_showSnackbarAndNavigate() {
        var navigateToHomeCalled = false

        composeTestRule.setContent {
            SummaryContent(
                deletedState = UiState.Success(true),
                onBackHome = { navigateToHomeCalled = true }
            )
        }

        // Verify snackbar
        composeTestRule.onNodeWithText("Checkout Success")
            .assertExists()
            .assertIsDisplayed()

        // Wait for navigation delay
        composeTestRule.mainClock.advanceTimeBy(600L)
    }
}