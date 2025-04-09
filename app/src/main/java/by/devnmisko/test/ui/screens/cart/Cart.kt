package by.devnmisko.test.ui.screens.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import by.devnmisko.test.R
import by.devnmisko.test.data.local.entity.CartItemEntity
import by.devnmisko.test.model.Product
import by.devnmisko.test.ui.screens.cart.CartViewModel.CartState
import by.devnmisko.test.ui.screens.cart.CartViewModel.CartUiState
import by.devnmisko.test.ui.theme.TestBKSTheme
import by.devnmisko.test.ui.theme.circularProgressSize
import by.devnmisko.test.ui.theme.defaultPadding
import by.devnmisko.test.ui.theme.defaultPaddingDouble
import by.devnmisko.test.ui.tooling.UIPreviews
import by.devnmisko.test.utils.formatPrice
import by.devnmisko.test.utils.formatUnitName

@Composable
fun Cart(
    modifier: Modifier = Modifier,
    viewModel: CartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier.padding(horizontal = defaultPaddingDouble),
            text = stringResource(R.string.cart),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(defaultPaddingDouble))
        Cart(
            uiState = uiState,
            onQuantityChange = viewModel::updateQuantity,
            onRemoveItem = viewModel::removeFromCart,
            onCheckout = viewModel::checkout,
            onDismissRequest = viewModel::dismissCheckoutMessage,
            onConfirm = viewModel::dismissCheckoutMessage
        )
    }

}

@Composable
fun Cart(
    modifier: Modifier = Modifier,
    uiState: CartUiState,
    onQuantityChange: (Int, Int) -> Unit,
    onRemoveItem: (CartItemEntity) -> Unit,
    onCheckout: () -> Unit,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
) {
    when (val cartState = uiState.cartState) {
        is CartState.Empty -> {
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    text = stringResource(R.string.cart_is_empty)
                )
            }
        }

        is CartState.CartItems -> {
            CartWithItemsState(
                modifier = modifier,
                items = cartState.items,
                totalPrice = uiState.totalPrice,
                isLoading = uiState.isLoading,
                isCheckoutLoading = uiState.isCheckoutLoading,
                onQuantityChange = onQuantityChange,
                onRemoveItem = onRemoveItem,
                onCheckout = onCheckout
            )
        }
    }

    if (uiState.isCheckoutSuccess) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(stringResource(R.string.success)) },
            text = {
                Text(uiState.checkoutMessage ?: stringResource(R.string.order_has_been_placed))
            },
            confirmButton = {
                Button(onClick = onConfirm) {
                    Text(stringResource(android.R.string.ok))
                }
            }
        )
    }
}

@Composable
private fun CartWithItemsState(
    modifier: Modifier,
    items: List<Pair<CartItemEntity, Product>>,
    totalPrice: Int,
    isLoading: Boolean,
    isCheckoutLoading: Boolean,
    onQuantityChange: (Int, Int) -> Unit,
    onRemoveItem: (CartItemEntity) -> Unit,
    onCheckout: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(defaultPaddingDouble)
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(circularProgressSize))
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(defaultPadding)
        ) {
            items(items) { (cartItem, product) ->
                CartItem(
                    product = product,
                    quantity = cartItem.quantity,
                    onQuantityChange = { newQuantity ->
                        onQuantityChange(product.id, newQuantity)
                    },
                    onRemove = { onRemoveItem(cartItem) }
                )
            }
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = defaultPadding),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                stringResource(R.string.total),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                formatPrice(totalPrice),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Button(
            onClick = onCheckout,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isCheckoutLoading
        ) {
            if (isCheckoutLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text(stringResource(R.string.place_order))
            }
        }
    }
}

@Composable
fun CartItem(
    product: Product,
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Card {
        Column(modifier = Modifier.padding(defaultPaddingDouble)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.remove))
                }
            }

            Spacer(modifier = Modifier.height(defaultPadding))

            Text(
                stringResource(
                    R.string.price_template,
                    formatPrice(product.price - product.bonus),
                    formatUnitName(product.type, product.unitName)
                )
            )

            Spacer(modifier = Modifier.height(defaultPadding))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.amount))

                QuantitySelector(
                    quantity = quantity,
                    onQuantityChange = onQuantityChange,
                    isWeighted = product.type == 1
                )
            }
        }
    }
}

@Composable
fun QuantitySelector(
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    isWeighted: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(defaultPadding)
    ) {
        IconButton(
            onClick = {
                val newValue = if (isWeighted) maxOf(100, quantity - 100)
                else maxOf(1, quantity - 1)
                onQuantityChange(newValue)
            },
            enabled = quantity > (if (isWeighted) 100 else 1)
        ) {
            Icon(Icons.Default.Clear, contentDescription = stringResource(R.string.decrease))
        }

        Text(
            text = if (isWeighted) stringResource(
                R.string.unit_template,
                quantity / 1000f
            ) else quantity.toString(),
            style = MaterialTheme.typography.bodyMedium
        )

        IconButton(
            onClick = {
                val newValue = if (isWeighted) minOf(10000, quantity + 100)
                else quantity + 1
                onQuantityChange(newValue)
            },
            enabled = if (isWeighted) quantity < 10000 else true
        ) {
            Icon(Icons.Default.Add, contentDescription = stringResource(R.string.increase))
        }
    }
}

@UIPreviews
@Composable
private fun EmptyCartPreview() {
    TestBKSTheme {
        Cart(
            uiState = CartUiState(),
            onQuantityChange = { _, _ -> },
            onRemoveItem = {},
            onCheckout = {},
            onDismissRequest = {},
            onConfirm = {}
        )
    }
}

@UIPreviews
@Composable
private fun CartPreview() {
    TestBKSTheme {
        Cart(
            uiState = CartUiState(
                cartState = CartState.CartItems(
                    listOf(
                        CartItemEntity(1, 1, 2, 0, 100, 10) to Product(
                            1,
                            "Колбаса",
                            "шт",
                            0,
                            100,
                            10,
                            null
                        ),
                        CartItemEntity(2, 2, 1, 0, 1000, 10) to Product(
                            2,
                            "Молоко",
                            "л",
                            1,
                            100,
                            10,
                            null
                        ),
                        CartItemEntity(3, 3, 3, 1, 1000, 10) to Product(
                            3,
                            "Масло",
                            "шт",
                            1,
                            100,
                            10,
                            null
                        ),
                        CartItemEntity(4, 4, 1, 1, 1000, 10) to Product(
                            4,
                            "Яблоко",
                            "кг",
                            1,
                            100,
                            10,
                            null
                        ),
                    )
                )
            ),
            onQuantityChange = { _, _ -> },
            onRemoveItem = {},
            onCheckout = {},
            onDismissRequest = {},
            onConfirm = {}
        )
    }
}