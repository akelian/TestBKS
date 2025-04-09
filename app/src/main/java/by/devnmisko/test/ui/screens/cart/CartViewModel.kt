package by.devnmisko.test.ui.screens.cart

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.devnmisko.test.R
import by.devnmisko.test.data.local.entity.CartItemEntity
import by.devnmisko.test.data.repository.CartRepository
import by.devnmisko.test.model.Product
import by.devnmisko.test.utils.formatPrice
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadCartItems()
    }

    private fun loadCartItems() {
        viewModelScope.launch {
            isLoading(true)
            combine(
                cartRepository.getCartItemsWithProducts(),
                cartRepository.getTotalPrice()
            ) { cartItems, totalPrice ->
                if (cartItems.isEmpty()) {
                    setCartEmpty()
                } else {
                    setCartItems(cartItems, totalPrice)
                }
                isLoading(false)
            }.flowOn(Dispatchers.IO).collect()
        }
    }

    fun updateQuantity(packId: Int, newQuantity: Int) {
        viewModelScope.launch {
            cartRepository.updateQuantity(packId, newQuantity)
        }
    }

    fun removeFromCart(cartItemEntity: CartItemEntity) {
        viewModelScope.launch {
            cartRepository.removeFromCart(cartItemEntity)
        }
    }

    fun checkout() {
        viewModelScope.launch {
            try {
                val totalPrice = formatPrice(_uiState.value.totalPrice).toString()
                _uiState.update { it.copy(isCheckoutLoading = true) }
                cartRepository.clearCart()
                _uiState.update {
                    it.copy(
                        isCheckoutSuccess = true,
                        checkoutMessage = context.getString(
                            R.string.order_has_been_placed_template,
                            totalPrice
                        )
                    )
                }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(checkoutError = context.getString(R.string.checkout_error_message))
                }
            } finally {
                _uiState.update { it.copy(isCheckoutLoading = false) }
            }
        }
    }

    fun dismissCheckoutMessage() {
        _uiState.update {
            it.copy(
                isCheckoutSuccess = false,
                checkoutError = null
            )
        }
    }

    private fun isLoading(loading: Boolean) {
        _uiState.update { it.copy(isLoading = loading) }
    }

    private fun setCartEmpty() {
        _uiState.update { it.copy(cartState = CartState.Empty) }
    }

    private fun setCartItems(items: List<Pair<CartItemEntity, Product>>, totalPrice: Int) {
        _uiState.update { it.copy(cartState = CartState.CartItems(items), totalPrice = totalPrice) }
    }

    sealed interface CartState {
        object Empty : CartState
        data class CartItems(val items: List<Pair<CartItemEntity, Product>>) : CartState
    }

    data class CartUiState(
        val cartState: CartState = CartState.Empty,
        val totalPrice: Int = 0,
        val isLoading: Boolean = false,
        val isCheckoutLoading: Boolean = false,
        val isCheckoutSuccess: Boolean = false,
        val checkoutMessage: String? = null,
        val checkoutError: String? = null
    )
}