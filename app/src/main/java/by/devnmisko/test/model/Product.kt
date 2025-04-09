package by.devnmisko.test.model

data class Product(
    val id: Int,
    val name: String,
    val unitName: String,
    val type: Int,
    val price: Int,
    val bonus: Int,
    val barcode: String?
)