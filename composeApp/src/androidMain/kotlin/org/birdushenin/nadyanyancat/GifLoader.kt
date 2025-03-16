package org.birdushenin.nadyanyancat

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.request.ImageRequest

@RequiresApi(Build.VERSION_CODES.P)
@Composable
actual fun GifLoader(url: String) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .crossfade(true)
            .decoderFactory(GifDecoder.Factory())
            .build(),
        contentDescription = "GIF"
    )
}