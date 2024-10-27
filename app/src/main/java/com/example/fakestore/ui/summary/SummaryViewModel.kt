package com.example.fakestore.ui.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakestore.data.db.entity.CartEntity
import com.example.fakestore.data.model.NetworkResult
import com.example.fakestore.data.model.UiState
import com.example.fakestore.data.model.user.UserRes
import com.example.fakestore.domain.CartRepository
import com.example.fakestore.domain.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
class SummaryViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _summaryUiState = MutableStateFlow<SummaryUiState>(SummaryUiState.Loading)
    val summaryUiState: StateFlow<SummaryUiState> = _summaryUiState.asStateFlow()

    private val _address: MutableStateFlow<UiState<UserRes.Address>> =
        MutableStateFlow(UiState.Idle)
    val address: StateFlow<UiState<UserRes.Address>>
        get() = _address.asStateFlow()

    private val _deletedCart: MutableStateFlow<UiState<Boolean>> =
        MutableStateFlow(UiState.Idle)
    val deletedCart: StateFlow<UiState<Boolean>>
        get() = _deletedCart.asStateFlow()

    fun clearCarts() {
        viewModelScope.launch(Dispatchers.IO) {
            _deletedCart.update { UiState.Loading }
            try {
                cartRepository.deleteAllCarts()
                delay(1500L)
                _deletedCart.update { UiState.Success(true) }
            } catch (e: Exception) {
                _deletedCart.update { UiState.Failed(e.message ?: "") }
            }
        }
    }

    fun fetchAddress() {
        _address.update { UiState.Loading }
        viewModelScope.launch {
            when (val res = userRepository.getProfileByUsername()) {
                is NetworkResult.Success -> {
                    _address.update { UiState.Success(res.data?.address ?: UserRes.Address()) }
                }

                is NetworkResult.Error -> {
                    _address.update { UiState.Failed(res.message) }
                }

                else -> {}
            }
        }
    }

    fun fetchSummary() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _summaryUiState.update { SummaryUiState.Loading }
                cartRepository.getCarts()
                    .catch { e ->
                        _summaryUiState.update { SummaryUiState.Error(e.message ?: "") }
                    }
                    .distinctUntilChanged()
                    .flowOn(Dispatchers.IO)
                    .collectLatest { items ->
                        _summaryUiState.update {
                            SummaryUiState.Success(
                                items = items,
                                total = calculateTotal(items)
                            )
                        }
                    }
            } catch (e: Exception) {
                _summaryUiState.update { SummaryUiState.Error(e.message ?: "") }
            }
        }
    }

    private fun calculateTotal(items: MutableList<CartEntity>): Double {
        return items.sumOf { it.price * it.quantity.toDouble() }
    }
}

sealed interface SummaryUiState {
    data object Loading : SummaryUiState
    data class Error(val message: String = "") : SummaryUiState
    data class Success(val items: MutableList<CartEntity>, val total: Double) : SummaryUiState
}