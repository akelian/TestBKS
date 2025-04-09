package by.devnmisko.test.ui.screens.cart

import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import by.devnmisko.test.ui.theme.TestBKSTheme
import by.devnmisko.test.ui.tooling.UIPreviews

@Composable
fun Cart(modifier: Modifier = Modifier) {
    Surface {
        Text("Cart screen")
    }
}

@UIPreviews
@Composable
private fun CartPreview() {
    TestBKSTheme {
        Cart()
    }
}