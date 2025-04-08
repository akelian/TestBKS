package by.devnmisko.test.ui.screens.products

import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import by.devnmisko.test.ui.theme.TestBKSTheme
import by.devnmisko.test.ui.tooling.UIPreviews

@Composable
fun Products(modifier: Modifier = Modifier) {
    Surface {
        Text("Products screen")
    }
}

@UIPreviews
@Composable
private fun ProductsPreview() {
    TestBKSTheme {
        Products()
    }
}