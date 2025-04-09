package by.devnmisko.test.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.devnmisko.test.data.repository.FirebaseRepository
import by.devnmisko.test.model.OrderHistoryItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            combine(
                firebaseRepository.getOrderHistory(),
                firebaseRepository.fetchUserFullName()
            ) { orders, fullName ->
                if (orders.isNotEmpty() && !fullName.isNullOrBlank()){
                    _uiState.update {
                        it.copy(
                            orders = orders,
                            userName = fullName,
                            isLoading = false,
                            error = null
                        )
                    }
                }

            }.collect()
        }
    }


    fun logout() {
        viewModelScope.launch {
            firebaseRepository.signOut()
        }
    }

    data class ProfileUiState(
        val userName: String = "",
        val orders: List<OrderHistoryItem> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val expandedOrderIds: Set<String> = emptySet()
    )
}