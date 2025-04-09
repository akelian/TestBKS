package by.devnmisko.test.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.devnmisko.test.data.repository.FirebaseRepository
import by.devnmisko.test.model.OrderHistoryItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            loadOrderHistory()
            loadUserData()
        }
    }

    private fun loadOrderHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isHistoryLoading = true) }
            firebaseRepository.getOrderHistory().collect { orders ->
                _uiState.update {
                    it.copy(
                        orders = orders,
                        error = null,
                        isHistoryLoading = false
                    )
                }
            }
            withTimeout(10000) {
                _uiState.update { it.copy(isHistoryLoading = false) }
            }
        }
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            firebaseRepository.fetchUserFullName().collect { fullName ->
                    _uiState.update {
                        it.copy(
                            userName = fullName,
                            error = null,
                            isLoading = false
                        )
                    }
                withTimeout(10000) {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }

        }
    }

    fun logout() {
        viewModelScope.launch {
            firebaseRepository.signOut()
        }
    }

    data class ProfileUiState(
        val userName: String? = "",
        val orders: List<OrderHistoryItem> = emptyList(),
        val isLoading: Boolean = false,
        val isHistoryLoading: Boolean = false,
        val error: String? = null,
        val expandedOrderIds: Set<String> = emptySet()
    )
}