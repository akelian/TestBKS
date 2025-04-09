package by.devnmisko.test.ui.screens.profile

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import by.devnmisko.test.R
import by.devnmisko.test.model.OrderHistoryItem
import by.devnmisko.test.model.OrderItem
import by.devnmisko.test.ui.common.components.BarcodeImage
import by.devnmisko.test.ui.theme.TestBKSTheme
import by.devnmisko.test.ui.theme.defaultPadding
import by.devnmisko.test.ui.theme.defaultPaddingDouble
import by.devnmisko.test.ui.tooling.UIPreviews
import by.devnmisko.test.utils.formatPrice
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Composable
fun Profile(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(lifecycle) {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            Log.d("Lifecycle", "Lifecycle is started")
            viewModel.refresh()
        }
    }

    Profile(
        modifier = modifier,
        uiState = uiState,
        onLogoutClick = {
            viewModel.logout()
        }
    )

}

@Composable
fun Profile(
    modifier: Modifier = Modifier,
    uiState: ProfileViewModel.ProfileUiState,
    onLogoutClick: () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                modifier = Modifier.padding(horizontal = defaultPaddingDouble),
                text = stringResource(R.string.profile),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(defaultPaddingDouble))
            UserInfoSection(
                userName = uiState.userName,
                isLoading = uiState.isLoading
            )

            OrderHistorySection(
                orders = uiState.orders,
                isLoading = uiState.isHistoryLoading,
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { onLogoutClick() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = defaultPaddingDouble),
                enabled = !uiState.isLoading
            ) {
                Text(stringResource(R.string.logout))
            }
        }
    }
}

@Composable
private fun UserInfoSection(
    userName: String?,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = defaultPaddingDouble,
                vertical = defaultPadding
            )
    ) {
        Column(
            modifier = Modifier.padding(defaultPaddingDouble)
        ) {
            Text(
                stringResource(R.string.personal_data),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(defaultPaddingDouble))

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Text(
                    "ФИО: ${userName ?: "Ошибка при загрузке профиля"}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
private fun OrderHistorySection(
    orders: List<OrderHistoryItem>,
    isLoading: Boolean,
) {
    var expandedOrderIds by remember { mutableStateOf(emptySet<String>()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = defaultPaddingDouble,
                vertical = defaultPadding
            )
    ) {
        Column(
            modifier = Modifier.padding(defaultPaddingDouble)
        ) {
            Text(
                "История заказов",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(defaultPadding))

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (orders.isEmpty()) {
                Text(
                    "У вас пока нет заказов",
                    modifier = Modifier.padding(vertical = defaultPaddingDouble)
                )
            } else {
                orders.forEach { order ->
                    OrderItem(
                        order = order,
                        isExpanded = expandedOrderIds.contains(order.id),
                        onExpandToggle = {
                            expandedOrderIds = if (expandedOrderIds.contains(order.id)) {
                                expandedOrderIds - order.id
                            } else {
                                expandedOrderIds + order.id
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderItem(
    order: OrderHistoryItem,
    isExpanded: Boolean,
    onExpandToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = defaultPadding),
        onClick = onExpandToggle
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .padding(defaultPaddingDouble)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Заказ #${order.id.takeLast(4)}",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        order.date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Text(
                    formatPrice(order.totalPrice),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier.padding(top = defaultPadding)
                ) {
                    Spacer(modifier = Modifier.padding(vertical = defaultPadding))

                    order.items.forEach { item ->
                        OrderProductItem(item = item)
                        Spacer(modifier = Modifier.height(defaultPadding))
                    }
                }
            }

            Icon(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .rotate(if (isExpanded) 180f else 0f),
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = if (isExpanded) "Свернуть" else "Развернуть"
            )
        }
    }
}

@Composable
private fun OrderProductItem(item: OrderItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(item.name, fontWeight = FontWeight.Medium)
            Text("${item.quantity} ${if (item.type == 0) "шт" else "г"}")
            item.barcode?.let {
                BarcodeImage(
                    barcodeValue = it,
                )
            } ?: Text(stringResource(R.string.barcode_not_found))
        }
        Text(
            formatPrice(
                 if (item.type == 0) {
                     item.pricePerUnit * item.quantity
                } else {
                     (item.pricePerUnit * (item.quantity.toDouble() /1000)).toInt()
                }
            )
        )
    }
}

@UIPreviews
@Composable
private fun ProfileEmptyOrdersPreview() {
    TestBKSTheme {
        Profile(
            uiState = ProfileViewModel.ProfileUiState()
        ) {}
    }
}

@UIPreviews
@Composable
private fun ProfilePreview() {
    TestBKSTheme {
        Profile(
            uiState = ProfileViewModel.ProfileUiState(
                orders =
                    listOf(
                        OrderHistoryItem(
                            id = "1",
                            userId = "1",
                            date = LocalDateTime.now(),
                            items = listOf(
                                OrderItem(
                                    productId = 1,
                                    name = "Продукт 1",
                                    quantity = 2,
                                    pricePerUnit = 100,
                                    type = 0,
                                    barcode = "1234567890123"
                                ),
                                OrderItem(
                                    productId = 2,
                                    name = "Продукт 2",
                                    quantity = 4,
                                    pricePerUnit = 100,
                                    type = 1,
                                    barcode = "1234567890123"
                                ),
                            ),
                            totalPrice = 50000
                        )
                    )
            )
        ) {}
    }
}