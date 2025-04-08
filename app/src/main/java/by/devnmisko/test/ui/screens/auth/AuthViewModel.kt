package by.devnmisko.test.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.devnmisko.test.ui.common.SnackbarController
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val snackbarController: SnackbarController
) : ViewModel() {

    private val _isUserLoggedIn = MutableStateFlow<Boolean>(false)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn.asStateFlow()

    internal val isLoading = MutableStateFlow(false)

    init {
        checkUserLoginStatus()
    }

    fun signIn(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            snackbarController.showSnackbar("Please enter email and password")
            return
        }
        viewModelScope.launch {
            isLoading.emit(true)
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        setUserLogged()
                    } else {
                        snackbarController.showSnackbar(task.exception?.message.toString())
                    }
                    viewModelScope.launch {
                        delay(1000)
                        isLoading.emit(false)
                    }
                }
        }
    }

    private fun checkUserLoginStatus() {
        viewModelScope.launch {
            val currentUser = firebaseAuth.currentUser
            _isUserLoggedIn.emit(currentUser != null)
        }
    }

    internal fun setUserLogged() {
        viewModelScope.launch {
            _isUserLoggedIn.emit(true)
        }
    }


}

