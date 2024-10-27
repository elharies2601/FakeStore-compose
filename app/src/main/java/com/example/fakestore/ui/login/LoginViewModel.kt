package com.example.fakestore.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakestore.data.datastore.UserDataStore
import com.example.fakestore.data.model.UiState
import com.example.fakestore.data.model.login.LoginRes
import com.example.fakestore.domain.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userDataStore: UserDataStore
) : ViewModel() {

    private val _login: MutableStateFlow<UiState<LoginRes>> = MutableStateFlow(UiState.Idle)
    val login: StateFlow<UiState<LoginRes>>
        get() = _login.asStateFlow()

    private val _isLogin: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLogin: StateFlow<Boolean>
        get() = _isLogin.asStateFlow()

    fun checkIsLogin() {
        viewModelScope.launch {
            userDataStore.isLogin.flowOn(Dispatchers.IO).collectLatest {
                _isLogin.value = it ?: false
            }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _login.value = UiState.Loading
            try {
                val response = userRepository.doLogin(username, password)
                saveDataStore(response.token, username)
                _login.value = UiState.Success(response)
            } catch (e: Exception) {
                _login.value = UiState.Failed(e.message ?: "Login Failed")
            }
        }
    }

    private fun saveDataStore(token: String, username: String) {
        viewModelScope.launch {
            userDataStore.saveToken(token)
            userDataStore.saveUsername(username)
            userDataStore.setLogin(true)
        }
    }
}