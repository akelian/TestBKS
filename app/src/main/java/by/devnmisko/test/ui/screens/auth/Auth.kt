package by.devnmisko.test.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import by.devnmisko.test.ui.theme.TestBKSTheme
import by.devnmisko.test.ui.theme.defaultPadding
import by.devnmisko.test.ui.tooling.UIPreviews

@Composable
fun Auth(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = hiltViewModel(),
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Surface {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val isLoading by authViewModel.isLoading.collectAsStateWithLifecycle()
            LoginContent(
                modifier = Modifier.padding(defaultPadding),
                onLoginCLick = { login, password ->
                    authViewModel.signIn(login, password)
                    keyboardController?.hide()
                },
                savedLogin = "",
                isLoading = isLoading,
            )
        }
    }

}

@UIPreviews
@Composable
private fun AuthPreview() {
    TestBKSTheme {
        Auth()
    }
}