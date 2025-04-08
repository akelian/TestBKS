package by.devnmisko.test.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import by.devnmisko.test.R
import by.devnmisko.test.ui.common.components.DefaultTextField
import by.devnmisko.test.ui.theme.TestBKSTheme
import by.devnmisko.test.ui.theme.circularProgressSize
import by.devnmisko.test.ui.theme.defaultPadding
import by.devnmisko.test.ui.theme.defaultPaddingHalf

@Composable
fun RegistrationContent(
    modifier: Modifier = Modifier,
    onButtonCLick: (String, String, String, String) -> Unit,
    isLoading: Boolean
) {
    var login = rememberSaveable { mutableStateOf("") }
    var password = rememberSaveable { mutableStateOf("") }
    var email = rememberSaveable { mutableStateOf("") }
    var fullname = rememberSaveable { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    DefaultTextField(
        text = login,
        label = { Text(stringResource(R.string.login)) },
        modifier = modifier.padding(bottom = defaultPadding),
    )
    DefaultTextField(
        text = password,
        label = { Text(stringResource(R.string.password)) },
        modifier = Modifier
            .padding(bottom = defaultPadding),
    )
    DefaultTextField(
        text = email,
        label = { Text(stringResource(R.string.email)) },
        modifier = Modifier
            .padding(bottom = defaultPadding),
    )
    DefaultTextField(
        text = fullname,
        label = { Text(stringResource(R.string.fullname)) },
        modifier = Modifier
            .padding(bottom = defaultPadding),
    )

    Button(
        modifier = modifier.height(48.dp),
        onClick = {
            onButtonCLick(login.value, password.value, email.value, fullname.value)
            keyboardController?.hide()
        }) {
        if (isLoading) {
            CircularProgressIndicator(
                color = androidx.compose.material3.MaterialTheme.colorScheme.surface,
                modifier = Modifier.size(circularProgressSize)
            )
        } else {
            Text(text = stringResource(R.string.register))
        }
    }
}

@Preview
@Composable
private fun RegistrationContentPreview() {
    TestBKSTheme {
        Column(
            modifier = Modifier.padding(defaultPadding),
            verticalArrangement = Arrangement.spacedBy(defaultPaddingHalf),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RegistrationContent(onButtonCLick = { _, _, _, _ -> }, isLoading = true)
        }
    }
}