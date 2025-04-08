package by.devnmisko.test.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import by.devnmisko.test.data.local.entity.BarcodeEntity

@Dao
interface BarcodeDao : BaseDao<BarcodeEntity>{
    
    @Query("SELECT * FROM barcode WHERE pack_id = :packId")
    suspend fun getBarcodesByPackId(packId: Int): List<BarcodeEntity>
    
    @Query("SELECT * FROM barcode WHERE barcode = :barcode")
    suspend fun getPackByBarcode(barcode: String): BarcodeEntity?
}