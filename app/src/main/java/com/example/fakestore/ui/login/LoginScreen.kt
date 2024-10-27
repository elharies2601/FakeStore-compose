package com.example.fakestore.ui.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.fakestore.data.model.UiState
import com.example.fakestore.data.model.login.LoginRes
import com.example.fakestore.navigation.FakeStoreRoute
import com.example.fakestore.util.navigateAndClean

@Composable
fun LoginScreen(
    navigator: NavHostController = rememberNavController(),
    viewModel: LoginViewModel = hiltViewModel()
) {
    val login by viewModel.login.collectAsStateWithLifecycle()
    val isLogin by viewModel.isLogin.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.checkIsLogin()
    }

    LaunchedEffect(key1 = isLogin) {
        if (isLogin) {
            navigator.navigateAndClean(FakeStoreRoute.Home.route)
        }
    }

    LoginContent(
        uiState = login,
        onClickLogin = { u, p -> viewModel.login(u, p) },
        onSuccessLogin = { navigator.navigateAndClean(FakeStoreRoute.Home.route) }
    )
}

@Composable
fun LoginContent(
    uiState: UiState<LoginRes> = UiState.Idle,
    onClickLogin: (String, String) -> Unit = { _, _ -> },
    onSuccessLogin: () -> Unit = {}
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        HeaderLogin(modifier = Modifier.fillMaxWidth()) {
            username = "johnd"
            password = "m38rmF\$"
        }
        Spacer(modifier = Modifier.height(32.dp))
        LoginForm(
            modifier = Modifier.fillMaxWidth(),
            uiState = uiState,
            username = username,
            password = password,
            onChangeUsername = { s -> username = s },
            onChangePassword = { p -> password = p }) { u, p ->
            onClickLogin(username, password)
        }
        Spacer(modifier = Modifier.height(16.dp))
        when (uiState) {
            is UiState.Success -> onSuccessLogin()
            is UiState.Failed -> {
                Text(
                    text = uiState.message,
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.error),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }

            else -> {}
        }
    }
}

@Composable
private fun LoginForm(
    modifier: Modifier = Modifier,
    uiState: UiState<LoginRes> = UiState.Idle,
    username: String = "",
    password: String = "",
    onChangeUsername: (String) -> Unit = {},
    onChangePassword: (String) -> Unit = {},
    onClickLogin: (String, String) -> Unit = { _, _ -> }
) {
    Column(
        verticalArrangement = Arrangement.Top,
        modifier = modifier.padding(16.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = username,
            onValueChange = onChangeUsername,
            label = { Text("Username") },
            leadingIcon = {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "user icon"
                )
            },
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = password,
            onValueChange = onChangePassword,
            label = { Text("Password") },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = "lock icon")
            },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button (modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
            enabled = username.isNotEmpty() && password.isNotEmpty() &&
                    uiState !is UiState.Loading,
            onClick = { onClickLogin(username, password) }) {
            if (uiState is UiState.Loading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.testTag("loading_indicator"))
            } else {
                Text("Login")
            }
        }
    }
}

@Composable
private fun HeaderLogin(
    modifier: Modifier = Modifier,
    tempClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp),
        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
        color = MaterialTheme.colorScheme.primary
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Welcome to FakeStore",
                modifier = modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 24.dp).clickable { tempClick() },
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Preview
@Composable
private fun PreviewLoginForm() {
    LoginForm()
}

@Preview
@Composable
private fun PreviewHeaderLogin() {
    HeaderLogin()
}

@Preview(name = "LoginScreen")
@Composable
private fun PreviewLoginScreen() {
    LoginContent()
}