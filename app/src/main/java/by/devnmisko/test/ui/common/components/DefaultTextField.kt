package by.devnmisko.test.ui.common.components

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import by.devnmisko.test.ui.theme.defaultTextFieldWidth

@Composable
fun DefaultTextField(
    modifier: Modifier = Modifier,
    text: MutableState<String>,
    label: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {

    TextField(
        modifier = modifier.width(defaultTextFieldWidth),
        value = text.value,
        onValueChange = { text.value = it },
        trailingIcon = { TextFieldClearIcon(text = text) },
        label = label,
        visualTransformation = visualTransformation,
        keyboardActions = keyboardActions,
        keyboardOptions = keyboardOptions,
        singleLine = true
    )
}