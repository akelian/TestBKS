package by.devnmisko.test.ui.screens.profile

import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import by.devnmisko.test.ui.theme.TestBKSTheme
import by.devnmisko.test.ui.tooling.UIPreviews

@Composable
fun Profile(
    modifier: Modifier = Modifier,
    profileViewModel: ProfileViewModel = hiltViewModel()
    ) {
    Surface {
        Text("Profile screen")
    }
}

@UIPreviews
@Composable
private fun ProfilePreview() {
    TestBKSTheme {
        Profile()
    }
}