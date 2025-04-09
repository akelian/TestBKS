// CartRepository.kt
package by.devnmisko.test.data.repository

import by.devnmisko.test.data.local.dao.CartDao
import by.devnmisko.test.data.local.dao.ProductDao
import by.devnmisko.test.data.local.entity.CartItemEntity
import by.devnmisko.test.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepository @Inject constructor(
    private val cartDao: CartDao,
    private val productDao: ProductDao,
) {
    fun getCartItemsWithProducts(): Flow<List<Pair<CartItemEntity, Product>>> {
        return cartDao.getAllCartItems()
            .combine(productDao.getAllProducts()) { cartItems, products ->
                cartItems.mapNotNull { cartItem ->
                    products.find { it.id == cartItem.packId }?.let { product ->
                        cartItem to product
                    }
                }
            }
    }

    suspend fun addToCart(product: Product, quantity: Int) {
        val existingItem = cartDao.getCartItemByPackId(product.id)
        if (existingItem != null) {
            cartDao.update(existingItem.copy(quantity = existingItem.quantity + quantity))
        } else {
            cartDao.insert(
                CartItemEntity(
                    packId = product.id,
                    quantity = quantity,
                    productType = product.type,
                    priceAtAddition = product.price,
                    bonusAtAddition = product.bonus
                )
            )
        }
    }

    suspend fun removeFromCart(cartEntity: CartItemEntity) {
        cartDao.delete(cartEntity)
    }

    suspend fun updateQuantity(packId: Int, newQuantity: Int) {
        cartDao.getCartItemByPackId(packId)?.let {
            cartDao.update(it.copy(quantity = newQuantity))
        }
    }

    suspend fun clearCart() {
        cartDao.clearCart()
    }

    fun getTotalPrice(): Flow<Int> {
        return cartDao.getTotalPrice()
    }

}