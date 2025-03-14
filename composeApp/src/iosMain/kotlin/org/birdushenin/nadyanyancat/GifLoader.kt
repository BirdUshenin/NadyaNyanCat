package org.birdushenin.nadyanyancat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.viewinterop.UIKitView
import cocoapods.SDWebImage.SDAnimatedImage
import cocoapods.SDWebImage.SDAnimatedImageView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSBundle
import platform.Foundation.NSURL

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun GifLoader() {
    val imageView = remember { SDAnimatedImageView() }

    LaunchedEffect(Unit) {
        val path = NSBundle.mainBundle.pathForResource("nyancat", "gif")
        val url = path?.let { NSURL.fileURLWithPath(it) }

        url?.let {
            val gifImage = SDAnimatedImage.imageNamed("nyancat.gif")
            gifImage?.let { imageView.setImage(it) }
        }
    }

    UIKitView(factory = { imageView })
}
