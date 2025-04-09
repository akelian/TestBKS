package by.devnmisko.test.ui.common.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import by.devnmisko.test.R
import by.devnmisko.test.utils.BarcodeGenerator

@Composable
fun BarcodeImage(
    barcodeValue: String?,
    modifier: Modifier = Modifier,
    width: Int = 600,
    height: Int = 300
) {
    val bitmap = remember(barcodeValue) {
        barcodeValue?.let {
            BarcodeGenerator.generateBarcodeBitmap(it, width, height)
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = stringResource(R.string.barcode),
            modifier = modifier,
            contentScale = ContentScale.Fit
        )
    } else {
        Box(
            modifier = modifier
                .background(Color.LightGray)
                .fillMaxWidth()
                .height(height.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(R.string.failure_while_generating_barcode))
        }
    }
}