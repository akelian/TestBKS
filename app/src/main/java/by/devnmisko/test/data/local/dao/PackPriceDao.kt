package by.devnmisko.test.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import by.devnmisko.test.data.local.entity.PackPriceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PackPriceDao : BaseDao<PackPriceEntity> {

    @Query("SELECT * FROM pack_price WHERE pack_id = :packId")
    suspend fun getPriceByPackId(packId: Int): PackPriceEntity?

    @Query("SELECT * FROM pack_price")
    fun getAllPrices(): Flow<List<PackPriceEntity>>
}