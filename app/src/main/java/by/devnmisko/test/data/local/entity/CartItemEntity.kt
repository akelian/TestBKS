// CartItemEntity.kt
package by.devnmisko.test.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "pack_id") val packId: Int,
    @ColumnInfo(name = "quantity") val quantity: Int,
    @ColumnInfo(name = "product_type") val productType: Int,
    @ColumnInfo(name = "price_at_addition") val priceAtAddition: Int,
    @ColumnInfo(name = "bonus_at_addition") val bonusAtAddition: Int
)