package com.example.fakestore.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakestore.data.db.entity.CartEntity
import com.example.fakestore.domain.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(private val cartRepository: CartRepository) : ViewModel() {
    private val _uiState: MutableStateFlow<CartUiState> = MutableStateFlow(CartUiState.Loading)
    val uiState: StateFlow<CartUiState>
        get() = _uiState.asStateFlow()

    fun updateQuantity(itemId: Int, increment: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                cartRepository.updateCart(itemId, increment)
            } catch (e: Exception) {
                _uiState.update {
                    CartUiState.Error(e.message.toString())
                }
            }
        }
    }

    fun removeCart(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                cartRepository.deleteCart(id)
            } catch (e: Exception) {
                _uiState.update {
                    CartUiState.Error(e.message.toString())
                }
            }
        }
    }

    fun fetchCarts() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _uiState.update { CartUiState.Loading }
                cartRepository.getCarts()
                    .catch { e ->
                        _uiState.update { CartUiState.Error(e.message ?: "Unknown error occurred") }
                    }
                    .distinctUntilChanged()
                    .flowOn(Dispatchers.IO)
                    .collectLatest { items ->
                        if (items.isEmpty()) {
                            _uiState.update { CartUiState.Empty }
                        } else {
                            _uiState.update {
                                CartUiState.Success(
                                    items = items.toMutableList(),
                                    total = calculateTotal(items.toMutableList()),
                                    isCheckoutEnable = enableCheckout(items.toMutableList())
                                )
                            }
                        }
                    }
            } catch (e: Exception) {
                _uiState.update {
                    CartUiState.Error(e.message.toString())
                }
            }
        }
    }

    private fun enableCheckout(items: MutableList<CartEntity>): Boolean {
        return items.any { it.quantity > 0 }
    }

    private fun calculateTotal(items: MutableList<CartEntity>): Double {
        return items.sumOf { it.price * it.quantity.toDouble() }
    }
}

sealed interface CartUiState {
    data object Loading : CartUiState
    data object Empty : CartUiState
    data class Error(val message: String) : CartUiState
    data class Success(
        val items: MutableList<CartEntity>,
        val total: Double,
        val isCheckoutEnable: Boolean = false
    ) : CartUiState
}