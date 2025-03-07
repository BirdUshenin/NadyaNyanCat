package org.birdushenin.nadyanyancat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            DisplayScreenSize()
        }
    }
}

@Composable
fun DisplayScreenSize() {
    val screenWidth = getScreenWidth()
    val screenHeight = getScreenHeight()
    Text(text = "Screen Size: ${screenWidth.value} x ${screenHeight.value}")
}
