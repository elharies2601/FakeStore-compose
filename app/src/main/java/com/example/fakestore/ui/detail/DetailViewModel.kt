package com.example.fakestore.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakestore.data.datastore.UserDataStore
import com.example.fakestore.data.db.entity.CartEntity
import com.example.fakestore.data.model.NetworkResult
import com.example.fakestore.data.model.UiState
import com.example.fakestore.data.model.product.ProductRes
import com.example.fakestore.domain.CartRepository
import com.example.fakestore.domain.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val userDataStore: UserDataStore
) : ViewModel() {
    private val _detail: MutableStateFlow<UiState<ProductRes>> =
        MutableStateFlow(UiState.Idle)
    val detail: StateFlow<UiState<ProductRes>>
        get() = _detail.asStateFlow()

    private val _insertedCart: MutableStateFlow<UiState<Int>> = MutableStateFlow(UiState.Idle)
    val insertedCart: StateFlow<UiState<Int>>
        get() = _insertedCart.asStateFlow()

    val username by lazy {
        userDataStore.username
    }

    fun addToCart(cartProduct: CartEntity) {
        _insertedCart.value = UiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            delay(1000L)
            try {
                val isExistBefore = checkCartIsExist(cartProduct.productId)
                if (isExistBefore) {
                    val itemCart = getItemCart(cartProduct.productId)
                    itemCart?.let { item ->
                        val newQuantity = item.copy(quantity =  item.quantity.plus(cartProduct.quantity))
                        cartRepository.updateCart(newQuantity)
                    } ?: run {
                        cartRepository.insertCart(cartProduct)
                    }
                } else {
                    cartRepository.insertCart(cartProduct)
                }
                _insertedCart.value = UiState.Success(1)
            } catch (e: Exception) {
                _insertedCart.value = UiState.Failed(e.message.toString())
            }
        }
    }

    fun fetchDetailProduct(id: Int) {
        viewModelScope.launch {
            _detail.value = UiState.Loading
            when(val res = productRepository.getProductById(id)) {
                is NetworkResult.Success -> {
                    _detail.value = UiState.Success(res.data)
                }
                is NetworkResult.Error -> {
                    _detail.value = UiState.Failed(res.message)
                }
                else -> {}
            }
        }
    }

    private suspend fun checkCartIsExist(idProduct: Int): Boolean {
        return cartRepository.isProductInCart(idProduct)
    }

    private suspend fun getItemCart(idProduct: Int): CartEntity? {
        return cartRepository.getCartItemByProductId(idProduct)
    }
}