package by.devnmisko.test.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pack_price",
    foreignKeys = [ForeignKey(
        entity = PackEntity::class,
        parentColumns = ["id"],
        childColumns = ["pack_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["pack_id"])]
)
data class PackPriceEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "pack_id") val packId: Int,
    @ColumnInfo(name = "price") val price: Int,
    @ColumnInfo(name = "bonus") val bonus: Int
)