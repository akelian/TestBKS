package by.devnmisko.test.data.repository

import by.devnmisko.test.model.OrderHistoryItem
import by.devnmisko.test.model.OrderItem
import by.devnmisko.test.model.toOrderHistoryItem
import by.devnmisko.test.model.toOrderItem
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
) {

    fun observeUserLoginStatus(onUserDataChanged: (FirebaseUser?) -> Unit) {
        auth.addAuthStateListener {
            onUserDataChanged(it.currentUser)
        }
    }

    fun signOut() {
        auth.signOut()
    }

    fun signIn(
        email: String, password: String,
        onCompleteListener: OnCompleteListener<AuthResult?>
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(onCompleteListener)
    }

    fun signUp(
        password: String,
        email: String,
        fullname: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                val user = task.result.user
                user?.let {
                    createUserDocument(
                        fullname = fullname,
                        userId = user.uid,
                        email = email,
                        onSuccess = onSuccess,
                        onFailure = onFailure
                    )
                }
            } else {
                // If sign in fails, display a message to the user.
                onFailure(task.exception?.message ?: "")
            }
        }
    }

    private fun createUserDocument(
        fullname: String, userId: String, email: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        db.collection(USERS_COLLECTION).document(userId).set(
            hashMapOf(
                KEY_FULLNAME to fullname,
                KEY_EMAIL to email,
            )
        )
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "")
            }

    }

    fun getOrderHistory(): Flow<List<OrderHistoryItem>> = callbackFlow {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            trySend(emptyList()).isSuccess
            awaitClose { }
            return@callbackFlow
        }

        val listener = db.collection(ORDERS_COLLECTION)
            .whereEqualTo(KEY_USER_ID, userId)
            .orderBy(KEY_DATE, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val orders = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toOrderHistoryItem()
                    } catch (_: Exception) {
                        null
                    }
                } ?: emptyList()

                trySend(orders).isSuccess
            }

        awaitClose { listener.remove() }
    }.flowOn(Dispatchers.IO)

    fun fetchUserFullName(): Flow<String?> = callbackFlow<String?> {
        val listener = db.collection(USERS_COLLECTION)
            .document(auth.currentUser?.uid ?: "")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val document = snapshot?.data
                if (document != null) {
                    val fullName: String = document[KEY_FULLNAME].toString()
                    trySend(fullName).isSuccess
                } else {
                    close()
                }
            }
        awaitClose { listener.remove() }
    }.flowOn(Dispatchers.IO)

    fun placeOrder(
        items: List<OrderItem>,
        totalPrice: Int,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val timestamp = com.google.firebase.Timestamp.now()

        val orderData = hashMapOf(
            KEY_USER_ID to auth.currentUser?.uid,
            KEY_DATE to timestamp,
            KEY_ITEMS to items.map { item ->
                hashMapOf(
                    KEY_PRODUCT_ID to item.productId,
                    KEY_NAME to item.name,
                    KEY_QUANTITY to item.quantity,
                    KEY_PRICE_PER_UNIT to item.pricePerUnit,
                    KEY_TYPE to item.type,
                    KEY_BARCODE to item.barcode
                )
            },
            KEY_TOTAL_PRICE to totalPrice
        )

        db.collection(ORDERS_COLLECTION)
            .document().set(orderData)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Неизвестная ошибка при создании заказа")
            }
    }

    companion object {
        const val USERS_COLLECTION = "Users"
        const val ORDERS_COLLECTION = "orders"
        const val KEY_FULLNAME = "fullname"
        const val KEY_EMAIL = "email"
        const val KEY_DATE = "date"
        const val KEY_USER_ID = "userId"
        const val KEY_ITEMS = "items"
        const val KEY_TOTAL_PRICE = "totalPrice"
        const val KEY_NAME = "name"
        const val KEY_PRODUCT_ID = "productId"
        const val KEY_QUANTITY = "quantity"
        const val KEY_PRICE_PER_UNIT = "pricePerUnit"
        const val KEY_BARCODE = "barcode"
        const val KEY_TYPE = "type"
    }
}