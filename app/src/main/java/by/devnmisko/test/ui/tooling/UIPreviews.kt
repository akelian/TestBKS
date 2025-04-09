package by.devnmisko.test.ui.tooling

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES, device = "id:pixel_7", showSystemUi = true)
@Preview(name = "default", device = "id:pixel_7", showSystemUi = true)
annotation class UIPreviews
