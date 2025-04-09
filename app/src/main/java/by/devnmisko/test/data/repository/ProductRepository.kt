package by.devnmisko.test.data.repository

import by.devnmisko.test.data.local.dao.ProductDao
import by.devnmisko.test.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val productDao: ProductDao
) {
    fun getAllProducts(): Flow<List<Product>> {
        return flow {
            productDao.getAllProducts().collect { products ->
                emit(products)
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getProductById(productId: Int): Product? {
        return productDao.getProductById(productId)
    }

    fun getProductsByType(type: Int): Flow<List<Product>> {
        return flow {
            productDao.getProductsByType(type).collect { products ->
                emit(products)
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getProductByBarcode(barcode: String): Product? {
        return productDao.getProductByBarcode(barcode)
    }
}