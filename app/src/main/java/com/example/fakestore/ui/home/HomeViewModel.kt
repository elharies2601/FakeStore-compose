package com.example.fakestore.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakestore.data.datastore.UserDataStore
import com.example.fakestore.data.model.NetworkResult
import com.example.fakestore.data.model.UiState
import com.example.fakestore.data.model.product.ProductRes
import com.example.fakestore.data.model.user.UserRes
import com.example.fakestore.domain.CartRepository
import com.example.fakestore.domain.ProductRepository
import com.example.fakestore.domain.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ProductRepository,
    private val cartRepository: CartRepository,
    private val userRepository: UserRepository,
    private val userDataStore: UserDataStore
) : ViewModel() {
    private val _categories: MutableStateFlow<UiState<MutableList<String>>> =
        MutableStateFlow(UiState.Idle)
    val categories: StateFlow<UiState<MutableList<String>>>
        get() = _categories.asStateFlow()

    private val _products: MutableStateFlow<UiState<MutableList<ProductRes>>> =
        MutableStateFlow(UiState.Idle)
    val products: StateFlow<UiState<MutableList<ProductRes>>>
        get() = _products.asStateFlow()

    private val _categorySelected: MutableStateFlow<String> = MutableStateFlow("")
    val categorySelected: StateFlow<String>
        get() = _categorySelected.asStateFlow()

    private val _countCart: MutableStateFlow<Int> = MutableStateFlow(0)
    val countCart: StateFlow<Int>
        get() = _countCart.asStateFlow()

    private val _user: MutableStateFlow<UiState<UserRes>> = MutableStateFlow(UiState.Idle)
    val user: StateFlow<UiState<UserRes>>
        get() = _user.asStateFlow()

    fun doLogout() {
        viewModelScope.launch {
            userDataStore.clearToken()
        }
    }

    fun fetchUser() {
        _user.update { UiState.Loading }
        viewModelScope.launch {
            when (val res = userRepository.getProfileByUsername()) {
                is NetworkResult.Success -> {
                    _user.update { UiState.Success(res.data ?: UserRes()) }
                }
                is NetworkResult.Error -> {
                    _user.update { UiState.Failed(res.message) }
                }
                else -> {}
            }
        }
    }

    fun checkCart() {
        viewModelScope.launch {
            cartRepository.getCarts()
                .catch {
                    emit(mutableListOf())
                }
                .flowOn(Dispatchers.IO)
                .stateIn(viewModelScope)
                .collectLatest {
                    _countCart.value = it.size
                }
        }
    }

    fun fetchProducts(category: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _products.value = UiState.Loading
            val res = if (category.isEmpty()) {
                repository.getProducts()
            } else {
                repository.getProductsByCategory(category)
            }
            when (res) {
                is NetworkResult.Success -> {
                    _products.value = UiState.Success(res.data)
                }

                is NetworkResult.Error -> {
                    _products.value = UiState.Failed(res.message)
                }

                else -> {}
            }
        }
    }

    fun fetchCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            _categories.value = UiState.Loading
            when (val res = repository.getCategories()) {
                is NetworkResult.Success -> {
                    _categories.value = UiState.Success(res.data)
                }

                is NetworkResult.Error -> {
                    _categories.value = UiState.Failed(res.message)
                }

                else -> {}
            }
        }
    }

    fun toggleCategory(newCategorySelected: String) {
        _categorySelected.update {
            if (it == newCategorySelected) "" else newCategorySelected
        }
    }
}