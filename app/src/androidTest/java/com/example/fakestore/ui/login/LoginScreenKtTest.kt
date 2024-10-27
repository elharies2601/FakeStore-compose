package com.example.fakestore.ui.login

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.fakestore.data.model.UiState
import com.example.fakestore.data.model.login.LoginRes
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenKtTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_initialState_showsEmptyFields() {
        // When
        composeTestRule.setContent {
            LoginContent()
        }

        // Then
        composeTestRule.onNodeWithText("Username").assertExists()
        composeTestRule.onNodeWithText("Password").assertExists()
        composeTestRule.onNodeWithText("Login").assertIsNotEnabled()
    }

    @Test
    fun loginScreen_whenFieldsFilled_loginButtonEnabled() {
        // When
        composeTestRule.setContent {
            LoginContent()
        }

        // Fill the fields
        composeTestRule.onNodeWithText("Username")
            .performTextInput("johnd")
        composeTestRule.onNodeWithText("Password")
            .performTextInput("m38rmF\$")

        // Then
        composeTestRule.onNodeWithText("Login").assertIsEnabled()
    }

    @Test
    fun loginScreen_whenLoginClicked_showsLoadingState() {
        val uiState = mutableStateOf<UiState<LoginRes>>(UiState.Idle)

        // When
        composeTestRule.setContent {
            LoginContent(
                uiState = uiState.value,
                onClickLogin = { _, _ ->
                    uiState.value = UiState.Loading
                }
            )
        }

        // Fill the fields first
        composeTestRule.onNodeWithText("Username")
            .performTextInput("johnd")
        composeTestRule.onNodeWithText("Password")
            .performTextInput("m38rmF\$")

        // Verify button is enabled
        composeTestRule.onNodeWithText("Login").assertIsEnabled()

        // Click login button
        composeTestRule.onNodeWithText("Login").performClick()

        // Then
        composeTestRule.onNodeWithTag("loading_indicator").assertExists()
    }

    @Test
    fun loginScreen_whenLoginFailed_showsErrorMessage() {
        val errorMessage = "Invalid credentials"

        // When
        composeTestRule.setContent {
            LoginContent(
                uiState = UiState.Failed(errorMessage)
            )
        }

        // Then
        composeTestRule.onNodeWithText(errorMessage).assertExists()
    }

    @Test
    fun loginScreen_whenLoginSuccess_callsOnSuccessLogin() {
        var onSuccessCalled = false

        // When
        composeTestRule.setContent {
            LoginContent(
                uiState = UiState.Success(LoginRes()),
                onSuccessLogin = {
                    onSuccessCalled = true
                }
            )
        }

        // Then
        assert(onSuccessCalled)
    }
}