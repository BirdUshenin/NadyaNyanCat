package org.birdushenin.nadyanyancat

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextDecoration
import kotlinx.coroutines.delay
import org.birdushenin.nadyanyancat.data.Pipe
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    val birdY = remember { mutableStateOf(300f) }

    val pipes = remember {
        mutableStateListOf(
            Pipe(1000f, 500f),
            Pipe(1500f, 600f)
        )
    }
    val score = remember { mutableStateOf("0") }

    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            FlappyBirdGame(birdY = birdY, pipes = pipes, score = score.value)
        }
    }
}

@Composable
fun FlappyBirdGame(birdY: MutableState<Float>, pipes: MutableList<Pipe>, score: String) {
    val textMeasurer = rememberTextMeasurer()
    val text = "Score: $score"
    val textLayoutResult = textMeasurer.measure(text)

    val density = LocalDensity.current
    val screenWidth = with(density) { getScreenWidth().toPx() }

    val birdSpeed = remember { mutableStateOf(0f) }
    val pipeSpeed = remember { mutableStateOf(5f) }

    // Гравитация
    val gravity = 0.5f
    val jumpStrength = -15f

    LaunchedEffect(Unit) {
        while (true) {
            birdSpeed.value += gravity
            birdY.value += birdSpeed.value

            pipes.forEach {
                it.xPosition -= pipeSpeed.value
            }

            pipes.forEachIndexed { index, pipe ->
                if (pipe.xPosition < 0) {
                    pipes[index] = pipe.copy(xPosition = screenWidth)
                }
            }

            delay(16L)
        }
    }

    Box(modifier = Modifier.fillMaxSize().pointerInput(Unit) {
        detectTapGestures(
            onTap = {
                birdSpeed.value = jumpStrength
            }
        )
    }) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(Color.Red, radius = 30f, center = Offset(100f, birdY.value))

            pipes.forEach { pipe ->
                drawRect(
                    color = Color.Green,
                    topLeft = Offset(pipe.xPosition, 0f),
                    size = Size(50f, pipe.height)
                )
                drawRect(
                    color = Color.Green,
                    topLeft = Offset(pipe.xPosition, pipe.height + 150f),
                    size = Size(50f, size.height - pipe.height - 150f)
                )
            }

            drawText(
                textLayoutResult = textLayoutResult,
                brush = SolidColor(Color.White),
                topLeft = Offset(50f, 50f),
                alpha = 0.8f,
                shadow = Shadow(color = Color.Gray, offset = Offset(5f, 5f), blurRadius = 2f),
                textDecoration = TextDecoration.Underline
            )
        }
    }
}