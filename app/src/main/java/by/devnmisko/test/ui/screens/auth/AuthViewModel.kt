package by.devnmisko.test.ui.screens.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.devnmisko.test.R
import by.devnmisko.test.data.repository.FirebaseRepository
import by.devnmisko.test.ui.common.SnackbarController
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firebaseRepository: FirebaseRepository,
    private val snackbarController: SnackbarController
) : ViewModel() {

    private val _isUserLoggedIn = MutableStateFlow<Boolean>(false)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn.asStateFlow()

    private val _uiState = MutableStateFlow<UiState>(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        observeUserLoginStatus()
    }

    fun signIn(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            snackbarController.showSnackbar(context.getString(R.string.enter_login_and_password))
            return
        }
        viewModelScope.launch {
            setLoading(true)
            firebaseRepository.signIn(email, password) { task ->
                if (!task.isSuccessful) {
                    snackbarController.showSnackbar(task.exception?.message.toString())
                }
                viewModelScope.launch {
                    setLoading(false)
                }
            }
        }
    }

    fun signUp(password: String, email: String, fullname: String) {
        firebaseRepository.signUp(password, email, fullname, onSuccess = {
            switchToLogin()
        }, onFailure = {
            snackbarController.showSnackbar(it)
        })
    }

    private fun observeUserLoginStatus() {
        viewModelScope.launch {
            firebaseRepository.observeUserLoginStatus { firebaseUser ->
                setUserLogged(firebaseUser != null)
            }
        }
    }

    internal fun setUserLogged(isLogged: Boolean) {
        viewModelScope.launch {
            _isUserLoggedIn.emit(isLogged)
        }
    }

    fun setLoading(isLoading: Boolean) {
        viewModelScope.launch {
            _uiState.emit(uiState.value.copy(isLoading = isLoading))
        }
    }

    fun switchToLogin() {
        viewModelScope.launch {
            _uiState.emit(uiState.value.copy(screenState = ScreenState.Login()))
        }
    }

    fun switchToRegister() {
        viewModelScope.launch {
            _uiState.emit(uiState.value.copy(screenState = ScreenState.Registration()))
        }
    }

    sealed interface ScreenState {
        data class Login(val isLoading: Boolean = false) : ScreenState
        data class Registration(val isLoading: Boolean = false) : ScreenState
    }

    data class UiState(
        val screenState: ScreenState = ScreenState.Login(),
        val isLoading: Boolean = false
    )
}

