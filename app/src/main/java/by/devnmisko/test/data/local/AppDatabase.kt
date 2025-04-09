package by.devnmisko.test.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import by.devnmisko.test.data.local.dao.BarcodeDao
import by.devnmisko.test.data.local.dao.PackDao
import by.devnmisko.test.data.local.dao.PackPriceDao
import by.devnmisko.test.data.local.dao.ProductDao
import by.devnmisko.test.data.local.dao.UnitDao
import by.devnmisko.test.data.local.entity.BarcodeEntity
import by.devnmisko.test.data.local.entity.PackEntity
import by.devnmisko.test.data.local.entity.PackPriceEntity
import by.devnmisko.test.data.local.entity.UnitEntity

@Database(
    entities = [
        UnitEntity::class,
        PackEntity::class,
        PackPriceEntity::class,
        BarcodeEntity::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun unitDao(): UnitDao
    abstract fun packDao(): PackDao
    abstract fun packPriceDao(): PackPriceDao
    abstract fun barcodeDao(): BarcodeDao
    abstract fun productDao(): ProductDao
}