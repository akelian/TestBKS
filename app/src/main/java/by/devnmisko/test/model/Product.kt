package by.devnmisko.test.model

data class Product(
    val id: Int,
    val name: String,
    val unitName: String,
    val type: Int, // 0 - штучный, 1 - весовой
    val price: Int, // в копейках
    val bonus: Int, // в копейках
    val barcode: String?
)