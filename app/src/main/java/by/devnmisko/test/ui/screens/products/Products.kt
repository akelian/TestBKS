package by.devnmisko.test.ui.screens.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import by.devnmisko.test.R
import by.devnmisko.test.model.Product
import by.devnmisko.test.ui.screens.products.ProductsViewModel.DialogState
import by.devnmisko.test.ui.theme.TestBKSTheme
import by.devnmisko.test.ui.theme.circularProgressSize
import by.devnmisko.test.ui.theme.defaultPadding
import by.devnmisko.test.ui.theme.defaultPaddingDouble
import by.devnmisko.test.ui.theme.defaultPaddingHalf
import by.devnmisko.test.ui.tooling.UIPreviews
import by.devnmisko.test.utils.formatPrice
import by.devnmisko.test.utils.formatUnitName


@Composable
fun Products(
    modifier: Modifier = Modifier,
    viewModel: ProductsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Surface {
        Products(
            modifier = modifier,
            uiState = uiState,
            onProductSelected = { viewModel.showQuantityDialog(it) },
            onDismissDialog = { viewModel.hideDialog() },
            onAddToCart = { product, quantity ->
                viewModel.onAddToCart(product, quantity)
            })
    }
}

@Composable
fun Products(
    modifier: Modifier = Modifier,
    uiState: ProductsViewModel.UiState,
    onProductSelected: (Product) -> Unit,
    onDismissDialog: () -> Unit = {},
    onAddToCart: (product: Product, quantity: Int) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(horizontal = defaultPaddingDouble),
            text = stringResource(R.string.products),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
        if (uiState.isLoading) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.size(circularProgressSize)
            )
        } else {
            when (uiState.productListState) {
                ProductsViewModel.ProductListState.Empty,
                is ProductsViewModel.ProductListState.Error -> {
                    val message =
                        if (uiState.productListState is ProductsViewModel.ProductListState.Empty) {
                            R.string.empty_list
                        } else {
                            R.string.something_went_wrong
                        }
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            modifier = Modifier.padding(horizontal = defaultPaddingDouble),
                            text = stringResource(message),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                is ProductsViewModel.ProductListState.ProductList -> {
                    val list = uiState.productListState.products
                    LazyColumn(
                        modifier = Modifier.fillMaxHeight(),
                        contentPadding = PaddingValues(defaultPaddingDouble),
                        verticalArrangement = Arrangement.spacedBy(defaultPadding)
                    ) {
                        items(items = list) { product ->
                            ProductItem(
                                product = product,
                                onClick = { onProductSelected(it) }
                            )
                        }
                    }
                }
            }
            if (uiState.dialogState is DialogState.QuantityDialog) {
                ProductQuantityDialog(
                    product = uiState.dialogState.product,
                    onDismiss = { onDismissDialog() },
                    onAddToCart = { quantity ->
                        onAddToCart(uiState.dialogState.product, quantity)
                    }
                )
            }
        }
    }
}

@Composable
fun ProductItem(
    product: Product,
    onClick: (Product) -> Unit
) {
    Card(
        onClick = { onClick(product) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(defaultPaddingDouble)
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(defaultPadding))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = stringResource(
                            R.string.price_for_template,
                            formatUnitName(product.type, product.unitName)
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = stringResource(
                            R.string.normal_price_template,
                            formatPrice(product.price)
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    Text(
                        text = if (product.bonus != 0) {
                            stringResource(
                                R.string.price_with_bonus_template,
                                formatPrice(product.price - product.bonus)
                            )
                        } else {
                            stringResource(R.string.there_is_no_bonus)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}


@Composable
fun ProductQuantityDialog(
    product: Product,
    onDismiss: () -> Unit,
    onAddToCart: (Int) -> Unit
) {
    var quantity by remember { mutableIntStateOf(if (product.type == 0) 1 else 100) }
    var errorMessageStringRes by remember { mutableStateOf<Int?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(defaultPaddingDouble)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(defaultPaddingDouble))

                Text(
                    text = stringResource(R.string.enter_amount_in_unit_template, product.unitName),
                    style = MaterialTheme.typography.bodyMedium
                )

                OutlinedTextField(
                    value = if (quantity < 1) {
                        ""
                    } else quantity.toString(),
                    onValueChange = {
                        val newValue = it.toIntOrNull() ?: 0
                        quantity = newValue

                        errorMessageStringRes = if (newValue <= 0) {
                            R.string.amount_less_than_zero_message
                        } else if (product.type == 1 && newValue > 10000) {
                            R.string.maximum_weigth_message
                        } else {
                            null
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Decimal,
                    ),
                    isError = errorMessageStringRes != null,
                    modifier = Modifier.fillMaxWidth()
                )

                errorMessageStringRes?.let {
                    Text(
                        text = stringResource(it),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = defaultPaddingHalf)
                    )
                }

                Spacer(modifier = Modifier.height(defaultPaddingDouble))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text(stringResource(R.string.cancel))
                    }

                    Spacer(modifier = Modifier.width(defaultPadding))

                    Button(
                        onClick = {
                            if (errorMessageStringRes == null) {
                                onAddToCart(quantity)
                                onDismiss()
                            }
                        },
                        enabled = errorMessageStringRes == null
                    ) {
                        Text(stringResource(R.string.add_to_cart))
                    }
                }
            }
        }
    }
}

@UIPreviews
@Composable
private fun ProductsEmptyListPreview() {
    TestBKSTheme {
        Products(
            uiState = ProductsViewModel.UiState(),
            onProductSelected = {},
            onDismissDialog = {},
            onAddToCart = { _, _ -> }
        )
    }
}

@UIPreviews
@Composable
private fun ProductsErrorPreview() {
    TestBKSTheme {
        Products(
            uiState = ProductsViewModel.UiState(
                productListState = ProductsViewModel.ProductListState.Error(
                    "Something went wrong"
                )
            ),
            onProductSelected = {},
            onDismissDialog = {},
            onAddToCart = { _, _ -> }
        )
    }
}

@UIPreviews
@Composable
private fun ProductsListPreview() {
    TestBKSTheme {
        Products(
            uiState = ProductsViewModel.UiState(
                productListState = ProductsViewModel.ProductListState.ProductList(
                    listOf(
                        Product(1, "Яблоко", "кг", 1, 100, 10, "123456789"),
                        Product(2, "Молоко", "л", 1, 100, 10, "123456789"),
                        Product(3, "Хлеб", "шт", 0, 100, 10, "123456789"),
                    )
                )
            ),
            onProductSelected = {},
            onDismissDialog = {},
            onAddToCart = { _, _ -> }
        )
    }
}

@UIPreviews
@Composable
private fun ProductsDialogPreview() {
    TestBKSTheme {
        Products(
            uiState = ProductsViewModel.UiState(
                dialogState = DialogState.QuantityDialog(
                    Product(1, "Яблоко", "кг", 1, 100, 10, "123456789")
                )
            ),
            onProductSelected = {},
            onDismissDialog = {},
            onAddToCart = { _, _ -> }
        )
    }
}