package by.devnmisko.test.data.di

import android.content.Context
import androidx.room.Room
import by.devnmisko.test.data.local.AppDatabase
import by.devnmisko.test.data.local.DatabaseInitializer
import by.devnmisko.test.data.local.dao.BarcodeDao
import by.devnmisko.test.data.local.dao.PackDao
import by.devnmisko.test.data.local.dao.PackPriceDao
import by.devnmisko.test.data.local.dao.ProductDao
import by.devnmisko.test.data.local.dao.UnitDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "product_database"
        ).build()
    }

    @Provides
    @Singleton
    fun providePackDao(database: AppDatabase): PackDao {
        return database.packDao()
    }

    @Provides
    @Singleton
    fun provideUnitDao(database: AppDatabase): UnitDao {
        return database.unitDao()
    }

    @Provides
    @Singleton
    fun providePackPriceDao(database: AppDatabase): PackPriceDao {
        return database.packPriceDao()
    }

    @Provides
    @Singleton
    fun provideBarcodeDao(database: AppDatabase): BarcodeDao {
        return database.barcodeDao()
    }

    @Provides
    @Singleton
    fun provideProductDao(database: AppDatabase): ProductDao {
        return database.productDao()
    }

    @Provides
    @Singleton
    fun provideDatabaseInitializer(
        unitDao: UnitDao,
        packDao: PackDao,
        packPriceDao: PackPriceDao,
        barcodeDao: BarcodeDao
    ): DatabaseInitializer {
        return DatabaseInitializer(unitDao, packDao, packPriceDao, barcodeDao)
    }
}