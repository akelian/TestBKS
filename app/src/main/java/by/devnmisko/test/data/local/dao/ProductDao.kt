package by.devnmisko.test.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import by.devnmisko.test.model.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Query("""
        SELECT 
            p.id as id,
            p.name as name,
            u.name as unitName,
            p.type as type,
            pp.price as price,
            pp.bonus as bonus,
            b.barcode as barcode
        FROM pack p
        INNER JOIN unit u ON p.unit_id = u.id
        INNER JOIN pack_price pp ON p.id = pp.pack_id
        LEFT JOIN barcode b ON p.id = b.pack_id
    """)
    fun getAllProducts(): Flow<List<Product>>

    @Query("""
        SELECT 
            p.id as id,
            p.name as name,
            u.name as unitName,
            p.type as type,
            pp.price as price,
            pp.bonus as bonus,
            b.barcode as barcode
        FROM pack p
        INNER JOIN unit u ON p.unit_id = u.id
        INNER JOIN pack_price pp ON p.id = pp.pack_id
        LEFT JOIN barcode b ON p.id = b.pack_id
        WHERE p.id = :productId
    """)
    suspend fun getProductById(productId: Int): Product?

    @Query("""
        SELECT 
            p.id as id,
            p.name as name,
            u.name as unitName,
            p.type as type,
            pp.price as price,
            pp.bonus as bonus,
            b.barcode as barcode
        FROM pack p
        INNER JOIN unit u ON p.unit_id = u.id
        INNER JOIN pack_price pp ON p.id = pp.pack_id
        LEFT JOIN barcode b ON p.id = b.pack_id
        WHERE p.type = :type
    """)
    fun getProductsByType(type: Int): Flow<List<Product>>

    @Query("""
        SELECT 
            p.id as id,
            p.name as name,
            u.name as unitName,
            p.type as type,
            pp.price as price,
            pp.bonus as bonus,
            b.barcode as barcode
        FROM pack p
        INNER JOIN unit u ON p.unit_id = u.id
        INNER JOIN pack_price pp ON p.id = pp.pack_id
        LEFT JOIN barcode b ON p.id = b.pack_id
        WHERE b.barcode = :barcode
    """)
    suspend fun getProductByBarcode(barcode: String): Product?
}