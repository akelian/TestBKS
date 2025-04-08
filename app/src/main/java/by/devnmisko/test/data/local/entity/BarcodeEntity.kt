package by.devnmisko.test.data.local.entity
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
@Entity(
    tableName = "barcode",
    foreignKeys = [ForeignKey(
        entity = PackEntity::class,
        parentColumns = ["id"],
        childColumns = ["pack_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["pack_id"])]
)
data class BarcodeEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "pack_id") val packId: Int,
    @ColumnInfo(name = "barcode") val body: String
)