// CartDao.kt
package by.devnmisko.test.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import by.devnmisko.test.data.local.entity.CartItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao : BaseDao<CartItemEntity> {

    @Query("SELECT * FROM cart_items")
    fun getAllCartItems(): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items WHERE pack_id = :packId")
    suspend fun getCartItemByPackId(packId: Int): CartItemEntity?

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    @Query(
        """
    SELECT SUM(
        (price_at_addition - bonus_at_addition) * 
        CASE 
            WHEN product_type = 1 THEN quantity / 1000.0 
            ELSE quantity 
        END
    ) FROM cart_items
"""
    )
    fun getTotalPrice(): Flow<Int>

    @Query("SELECT COUNT(*) FROM cart_items")
    fun getCartItemsCount(): Flow<Int>
}