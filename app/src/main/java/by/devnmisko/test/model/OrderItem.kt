package by.devnmisko.test.model

data class OrderItem(
    val productId: Int,
    val name: String,
    val quantity: Int,
    val pricePerUnit: Int,
    val type: Int,
    val barcode: String?
)