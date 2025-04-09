package by.devnmisko.test.ui.screens.products

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.devnmisko.test.R
import by.devnmisko.test.data.local.DatabaseInitializer
import by.devnmisko.test.data.repository.ProductRepository
import by.devnmisko.test.model.Product
import by.devnmisko.test.ui.common.SnackbarController
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    databaseInitializer: DatabaseInitializer,
    @ApplicationContext private val context : Context
) : ViewModel() {

    @Inject
    lateinit var snackbarController: SnackbarController

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState())
    val uiState get() = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            databaseInitializer.initialize()
            loadProducts()
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            isLoading(true)
            productRepository.getAllProducts().flowOn(Dispatchers.IO).collect { products ->
                if (products.isEmpty()) {
                    setProductListIsEmpty()
                } else {
                    setProductList(products)
                }
                isLoading(false)
            }
        }
    }

    fun isLoading(isLoading: Boolean) {
        viewModelScope.launch {
            _uiState.emit(_uiState.value.copy(isLoading = isLoading))
        }
    }

    fun showQuantityDialog(product: Product) {
        viewModelScope.launch {
            _uiState.emit(_uiState.value.copy(dialogState = DialogState.QuantityDialog(product)))
        }

    }

    fun hideDialog() {
        viewModelScope.launch {
            _uiState.emit(_uiState.value.copy(dialogState = DialogState.Hidden))
        }
    }

    fun setProductListIsEmpty() {
        viewModelScope.launch {
            _uiState.emit(_uiState.value.copy(productListState = ProductListState.Empty))
        }
    }

    fun setProductList(list: List<Product>) {
        viewModelScope.launch {
            _uiState.emit(_uiState.value.copy(productListState = ProductListState.ProductList(list)))
        }
    }

    fun onAddToCart(product: Product, quantity: Int) {
        snackbarController.showSnackbar(
            context.getString(
                R.string.you_added_product_message,
                product.name,
                quantity,
                product.unitName
            ))
    }


    sealed interface ProductListState {
        object Empty : ProductListState
        data class Error(val message: String) : ProductListState
        data class ProductList(val products: List<Product>) : ProductListState
    }

    sealed interface DialogState {
        object Hidden : DialogState
        data class QuantityDialog(val product: Product) : DialogState
    }

    data class UiState(
        val productListState: ProductListState = ProductListState.Empty,
        val dialogState: DialogState = DialogState.Hidden,
        val isLoading: Boolean = false
    )
}