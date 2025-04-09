package by.devnmisko.test.model

import by.devnmisko.test.data.repository.FirebaseRepository.Companion.KEY_BARCODE
import by.devnmisko.test.data.repository.FirebaseRepository.Companion.KEY_DATE
import by.devnmisko.test.data.repository.FirebaseRepository.Companion.KEY_ITEMS
import by.devnmisko.test.data.repository.FirebaseRepository.Companion.KEY_NAME
import by.devnmisko.test.data.repository.FirebaseRepository.Companion.KEY_PRICE_PER_UNIT
import by.devnmisko.test.data.repository.FirebaseRepository.Companion.KEY_PRODUCT_ID
import by.devnmisko.test.data.repository.FirebaseRepository.Companion.KEY_QUANTITY
import by.devnmisko.test.data.repository.FirebaseRepository.Companion.KEY_TOTAL_PRICE
import by.devnmisko.test.data.repository.FirebaseRepository.Companion.KEY_TYPE
import by.devnmisko.test.data.repository.FirebaseRepository.Companion.KEY_USER_ID
import com.google.firebase.firestore.DocumentSnapshot
import java.time.LocalDateTime
import java.time.ZoneId

fun Map<String, Any>.toOrderItem() = OrderItem(
    productId = (this[KEY_PRODUCT_ID] as? Long)?.toInt() ?: 0,
    name = this[KEY_NAME] as? String ?: "",
    quantity = (this[KEY_QUANTITY] as? Long)?.toInt() ?: 0,
    pricePerUnit = (this[KEY_PRICE_PER_UNIT] as? Long)?.toInt()
        ?: 0,
    type = (this[KEY_TYPE] as? Long)?.toInt() ?: 0,
    barcode = this[KEY_BARCODE] as? String
)

fun DocumentSnapshot.toOrderHistoryItem() = OrderHistoryItem(
    id = id,
    userId = get(KEY_USER_ID).toString(),
    date = getTimestamp(KEY_DATE)?.toDate()?.toInstant()
        ?.atZone(ZoneId.systemDefault())?.toLocalDateTime()
        ?: LocalDateTime.now(),
    items = (get(KEY_ITEMS) as List<Map<String, Any>>?
        ?: emptyList()).map { itemMap ->
        itemMap.toOrderItem()
    },
    totalPrice = (getLong(KEY_TOTAL_PRICE) ?: 0L).toInt()
)