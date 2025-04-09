package by.devnmisko.test.data.di

import by.devnmisko.test.data.local.dao.CartDao
import by.devnmisko.test.data.local.dao.ProductDao
import by.devnmisko.test.data.repository.CartRepository
import by.devnmisko.test.data.repository.FirebaseRepository
import by.devnmisko.test.data.repository.ProductRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideFirebaseRepository(
        firebaseAuth: FirebaseAuth,
        firebaseFirestore: FirebaseFirestore
    ) = FirebaseRepository(firebaseAuth, firebaseFirestore)

    @Provides
    fun provideProductRepository(
        productDao: ProductDao
    ) = ProductRepository(productDao)

    @Provides
    fun provideCartRepository(
        cartDao: CartDao,
        productDao: ProductDao
    ) = CartRepository(cartDao, productDao)


}