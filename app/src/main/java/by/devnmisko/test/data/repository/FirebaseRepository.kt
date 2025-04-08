package by.devnmisko.test.data.repository

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
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

    fun signIn(
        email: String, password: String,
        onCompleteListener: OnCompleteListener<AuthResult?>
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(onCompleteListener)
    }

    fun signUp(
        login: String,
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
                    createUserDocument(login, fullname, user.uid, onSuccess, onFailure)
                }
            } else {
                // If sign in fails, display a message to the user.
                onFailure(task.exception?.message ?: "")
            }
        }
    }

    fun createUserDocument(
        login: String, fullname: String, userId: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        db.collection(USERS_COLLECTION).document(userId).set(
            hashMapOf(
                KEY_LOGIN to login,
                KEY_FULLNAME to fullname,
            )
        ).addOnSuccessListener {
            onSuccess()
        }
            .addOnFailureListener { e -> onFailure(e.message ?: "") }

    }

    companion object {
        const val USERS_COLLECTION = "Users"
        const val KEY_LOGIN = "login"
        const val KEY_FULLNAME = "fullname"
    }
}