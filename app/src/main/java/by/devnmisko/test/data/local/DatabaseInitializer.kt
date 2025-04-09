package by.devnmisko.test.data.local

import by.devnmisko.test.data.local.dao.BarcodeDao
import by.devnmisko.test.data.local.dao.PackDao
import by.devnmisko.test.data.local.dao.PackPriceDao
import by.devnmisko.test.data.local.dao.UnitDao
import by.devnmisko.test.data.local.entity.BarcodeEntity
import by.devnmisko.test.data.local.entity.PackEntity
import by.devnmisko.test.data.local.entity.PackPriceEntity
import by.devnmisko.test.data.local.entity.UnitEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DatabaseInitializer @Inject constructor(
    private val unitDao: UnitDao,
    private val packDao: PackDao,
    private val packPriceDao: PackPriceDao,
    private val barcodeDao: BarcodeDao
) {
    suspend fun initialize() = withContext(Dispatchers.IO) {
        if (shouldInitializeUnits()) initializeUnits()
        if (shouldInitializePacks()) initializePacks()
        if (shouldInitializePackPrices()) initializePackPrices()
        if (shouldInitializeBarcodes()) initializeBarcodes()
    }

    private suspend fun shouldInitializeUnits(): Boolean {
        return unitDao.getAllUnits().first().isEmpty()
    }

    private suspend fun shouldInitializePacks(): Boolean {
        return packDao.getAllPacks().first().isEmpty()
    }

    private suspend fun shouldInitializePackPrices(): Boolean {
        return packPriceDao.getAllPrices().first().isEmpty()
    }

    private suspend fun shouldInitializeBarcodes(): Boolean {
        return barcodeDao.getBarcodesByPackId(1).isEmpty()
    }

    private suspend fun initializeUnits() {
        val units = listOf(
            UnitEntity(name = "шт"),
            UnitEntity(name = "г"),
            UnitEntity(name = "кг"),
            UnitEntity(name = "л")
        )
        unitDao.insert(units)
    }

    private suspend fun initializePacks() {
        val packs = listOf(
            PackEntity(unitId = 4, name = "Молоко", type = 0, quant = 1),
            PackEntity(unitId = 1, name = "Хлеб", type = 0, quant = 1),
            PackEntity(unitId = 3, name = "Сахар", type = 1, quant = 1000),
            PackEntity(unitId = 1, name = "Яйца", type = 0, quant = 1),
            PackEntity(unitId = 1, name = "Масло", type = 0, quant = 1),
            PackEntity(unitId = 3, name = "Свинина", type = 1, quant = 1000),
            PackEntity(unitId = 3, name = "Курица", type = 1, quant = 1000),
            PackEntity(unitId = 2, name = "Специи", type = 1, quant = 1000),
        )
        packDao.insert(packs)
    }

    private suspend fun initializePackPrices() {
        val prices = listOf(
            PackPriceEntity(packId = 1, price = 8000, bonus = 500),
            PackPriceEntity(packId = 2, price = 4500, bonus = 300),
            PackPriceEntity(packId = 3, price = 6000, bonus = 400),
            PackPriceEntity(packId = 4, price = 12000, bonus = 800),
            PackPriceEntity(packId = 5, price = 1000, bonus = 0),
            PackPriceEntity(packId = 6, price = 3000, bonus = 1200),
            PackPriceEntity(packId = 7, price = 500, bonus = 40),
        )
        packPriceDao.insert(prices)
    }

    private suspend fun initializeBarcodes() {
        val barcodes = listOf(
            BarcodeEntity(packId = 1, body = "123456789012"),
            BarcodeEntity(packId = 2, body = "987654321098"),
            BarcodeEntity(packId = 3, body = "456789123045"),
            BarcodeEntity(packId = 4, body = "789012345678")
        )
        barcodeDao.insert(barcodes)
    }
}