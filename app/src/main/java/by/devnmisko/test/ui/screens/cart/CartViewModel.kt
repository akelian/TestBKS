package by.devnmisko.test.ui.screens.cart

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.devnmisko.test.R
import by.devnmisko.test.data.local.entity.CartItemEntity
import by.devnmisko.test.data.repository.CartRepository
import by.devnmisko.test.data.repository.FirebaseRepository
import by.devnmisko.test.model.OrderItem
import by.devnmisko.test.model.Product
import by.devnmisko.test.ui.common.SnackbarController
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
    private val cartRepository: CartRepository,
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    @Inject
    lateinit var snackbarController: SnackbarController

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState = _uiState.asStateFlow()

    private var cachedCartItems: List<Pair<CartItemEntity, Product>> = emptyList()

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
                    cachedCartItems = cartItems
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
        if (cachedCartItems.isEmpty()) return
        setCheckoutLoading(true)
        val cartItems = cachedCartItems.map { it.first }
        val products = cachedCartItems.map { it.second }
        val orderItems = cartItems.map { cartItem ->
            val product = products.find { it.id == cartItem.packId } ?: return@map null
            OrderItem(
                productId = product.id,
                name = product.name,
                quantity = cartItem.quantity,
                pricePerUnit = cartItem.priceAtAddition - cartItem.bonusAtAddition,
                type = product.type,
                barcode = product.barcode
            )
        }.filterNotNull()

        val totalPrice = cartItems.sumOf {
            if (it.productType == 0){
                (it.priceAtAddition - it.bonusAtAddition) * it.quantity
            } else {
                ((it.priceAtAddition - it.bonusAtAddition) * (it.quantity.toDouble()/1000)).toInt()
            }
        }

        firebaseRepository.placeOrder(
            items = orderItems,
            totalPrice = totalPrice,
            onSuccess = {
                viewModelScope.launch {
                    val price = formatPrice(_uiState.value.totalPrice)
                    cartRepository.clearCart()
                    _uiState.update {
                        it.copy(
                            isCheckoutSuccess = true,
                            checkoutMessage = context.getString(
                                R.string.order_has_been_placed_template,
                                price
                            )
                        )
                    }
                }
                setCheckoutLoading(false)
            },
            onFailure = { _ ->
                snackbarController.showSnackbar(
                    context.getString(R.string.checkout_error_message)
                )
                setCheckoutLoading(false)
            }
        )
    }

    fun dismissCheckoutMessage() {
        _uiState.update {
            it.copy(
                isCheckoutSuccess = false,
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

    private fun setCheckoutLoading(loading: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isCheckoutLoading = loading) }
        }
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
    )
}