package by.devnmisko.test.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import by.devnmisko.test.R
import by.devnmisko.test.ui.theme.TestBKSTheme
import by.devnmisko.test.ui.theme.defaultPadding
import by.devnmisko.test.ui.tooling.UIPreviews


@Composable
fun Auth(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current

    Auth(
        modifier = modifier,
        uiState = uiState,
        onLoginCLick = { login, password ->
            authViewModel.signIn(login, password)
            keyboardController?.hide()
        },
        onRegisterClick = { login, password, email, fullname ->
            authViewModel.signUp(login, password, email, fullname)
            keyboardController?.hide()
        },
        onBeginRegisterClick = {
            authViewModel.switchToRegister()
        }
    )
}

@Composable
fun Auth(
    modifier: Modifier = Modifier,
    uiState: AuthViewModel.UiState,
    onBeginRegisterClick: () -> Unit = {},
    onLoginCLick: (String, String) -> Unit,
    onRegisterClick: (String, String, String, String) -> Unit
) {
    Surface {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (uiState.screenState) {
                is AuthViewModel.ScreenState.Login -> {
                    LoginContent(
                        modifier = Modifier.padding(defaultPadding),
                        onLoginCLick = { login, password ->
                            onLoginCLick(login, password)
                        },
                        savedLogin = "",
                        isLoading = uiState.isLoading,
                    )
                    TextButton(onClick = {
                        onBeginRegisterClick()
                    }) {
                        Text(
                            text = stringResource(R.string.register),
                            fontSize = 14.sp,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.padding(0.dp),
                        )
                    }
                }

                is AuthViewModel.ScreenState.Registration -> {
                    RegistrationContent(
                        modifier = modifier.padding(defaultPadding),
                        onButtonCLick = { login, password, email, fullname ->
                            onRegisterClick(login, password, email, fullname)
                        },
                        isLoading = uiState.isLoading
                    )
                }
            }

        }
    }

}

@UIPreviews
@Composable
private fun AuthLoginPreview() {
    TestBKSTheme {
        Auth(
            uiState = AuthViewModel.UiState(
                screenState = AuthViewModel.ScreenState.Login(),
            ),
            onBeginRegisterClick = {},
            onRegisterClick = { _, _, _, _ -> },
            onLoginCLick = { _, _ -> })
    }
}

@UIPreviews
@Composable
private fun AuthRegisterPreview() {
    TestBKSTheme {
        Auth(
            uiState = AuthViewModel.UiState(
                screenState = AuthViewModel.ScreenState.Registration(),
            ),
            onBeginRegisterClick = {},
            onRegisterClick = { _, _, _, _ -> },
            onLoginCLick = { _, _ -> })
    }
}