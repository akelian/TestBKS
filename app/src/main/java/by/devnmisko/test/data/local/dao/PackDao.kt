package by.devnmisko.test.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import by.devnmisko.test.data.local.entity.PackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PackDao : BaseDao<PackEntity> {

    @Query("SELECT * FROM pack")
    fun getAllPacks(): Flow<List<PackEntity>>

    @Query("SELECT * FROM pack WHERE id = :id")
    suspend fun getPackById(id: Int): PackEntity?

    @Query("SELECT * FROM pack WHERE type = :type")
    fun getPacksByType(type: Int): Flow<List<PackEntity>>
}