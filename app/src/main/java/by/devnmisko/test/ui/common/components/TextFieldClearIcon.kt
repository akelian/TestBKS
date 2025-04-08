package by.devnmisko.test.ui.common.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import by.devnmisko.test.R
import by.devnmisko.test.ui.theme.defaultIconSize

@Composable
fun TextFieldClearIcon(modifier: Modifier = Modifier, text: MutableState<String>) {
    if (text.value.isNotEmpty()) {
        IconButton(
            onClick = { text.value = "" },
            modifier = modifier.size(defaultIconSize)
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = stringResource(R.string.clear_field)
            )
        }
    }
}

