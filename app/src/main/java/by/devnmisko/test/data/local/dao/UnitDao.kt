package by.devnmisko.test.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import by.devnmisko.test.data.local.entity.UnitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UnitDao : BaseDao<UnitEntity> {

    @Query("SELECT * FROM unit")
    fun getAllUnits(): Flow<List<UnitEntity>>

    @Query("SELECT * FROM unit WHERE id = :id")
    suspend fun getUnitById(id: Int): UnitEntity?
}