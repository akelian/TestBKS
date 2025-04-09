package by.devnmisko.test.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import by.devnmisko.test.R
import by.devnmisko.test.ui.common.components.DefaultTextField
import by.devnmisko.test.ui.common.components.TextFieldClearIcon
import by.devnmisko.test.ui.theme.TestBKSTheme
import by.devnmisko.test.ui.theme.defaultPadding
import by.devnmisko.test.ui.theme.defaultTextFieldWidth
import by.devnmisko.test.ui.tooling.UIPreviews

@Composable
fun LoginContent(
    modifier: Modifier = Modifier,
    onLoginCLick: (String, String) -> Unit,
    savedLogin: String? = null,
    isLoading: Boolean = false,
) {
    var login = rememberSaveable { mutableStateOf(savedLogin ?: "") }
    var password = rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    DefaultTextField(
        modifier = modifier,
        text = login,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onDone = { focusManager.moveFocus(FocusDirection.Down) },
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        label = { Text(stringResource(R.string.email)) }
    )

    TextField(
        modifier = modifier.width(defaultTextFieldWidth),
        value = password.value,
        visualTransformation = PasswordVisualTransformation(),
        onValueChange = {
            password.value = it
        },
        label = { Text(stringResource(R.string.password)) },
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        trailingIcon = { TextFieldClearIcon(text = password) },
        keyboardActions = KeyboardActions(
            onDone = {
                onLoginCLick(login.value, password.value)
                password.value = ""
            }
        ),
        singleLine = true
    )
    Button(
        modifier = modifier.height(48.dp),
        onClick = {
            onLoginCLick(login.value, password.value)
            password.value = ""
        }) {
        if (isLoading) {
            CircularProgressIndicator(
                color = androidx.compose.material3.MaterialTheme.colorScheme.surface,
                modifier = Modifier.size(32.dp)
            )
        } else {
            Text(text = stringResource(R.string.log_in))
        }
    }
}

@UIPreviews
@Composable
private fun LoginContentPreview() {
    TestBKSTheme {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LoginContent(
                modifier = Modifier.padding(defaultPadding),
                onLoginCLick = { login, password -> },
                savedLogin = null, isLoading = true
            )
        }
    }
}