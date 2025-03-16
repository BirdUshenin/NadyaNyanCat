package org.birdushenin.nadyanyancat

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest

@RequiresApi(Build.VERSION_CODES.P)
@Composable
actual fun GifLoader(url: String) {
//    AsyncImage(
//        model = ImageRequest.Builder(LocalContext.current)
//            .data(R.drawable.nyancat)
//            .decoderFactory { result, options, _ ->
//                ImageDecoderDecoder(result.source, options)
//            }
//            .build(),
//        contentDescription = "GIF from resources"
//    )
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .crossfade(true)
            .build(),
        contentDescription = "GIF from URL"
    )
}