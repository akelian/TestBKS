package by.devnmisko.test.model

import java.time.LocalDateTime

data class OrderHistoryItem(
    val id: String,
    val userId: String,
    val date: LocalDateTime,
    val items: List<OrderItem>,
    val totalPrice: Int
)
