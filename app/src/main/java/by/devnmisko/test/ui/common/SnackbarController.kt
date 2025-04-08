package by.devnmisko.test.ui.common

import androidx.annotation.StringRes
import androidx.compose.material3.SnackbarDuration
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SnackbarController @Inject constructor() {

    private val _snackbars = MutableStateFlow<Set<SnackbarState>>(emptySet())
    val snackbars get() = _snackbars.asStateFlow()

    private val _onDismissEvents = MutableSharedFlow<SnackbarState>()
    val onDismissEvents get() = _onDismissEvents.asSharedFlow()

    fun showSnackbar(message: String) {
        _snackbars.update { it.plus(SnackbarState(message)) }
    }

    fun showSnackbar(snackbarState: SnackbarState) {
        _snackbars.update { it.plus(snackbarState) }
    }

    fun dismissSnackbar(snackbarState: SnackbarState) {
        _onDismissEvents.tryEmit(snackbarState)
        if (snackbars.value.isNotEmpty()) {
            _snackbars.update { snackbar ->
                snackbar.minus(snackbarState)
            }
        }
    }
}

data class SnackbarState(
    val messageRes: String,
    @StringRes
    val actionLabelRes: Int? = null,
    val withDismissAction: Boolean = false,
    val duration: SnackbarDuration = SnackbarDuration.Short
)