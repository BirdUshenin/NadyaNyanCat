package org.birdushenin.nadyanyancat

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import nadyanyancat.composeapp.generated.resources.Res
import nadyanyancat.composeapp.generated.resources.background
import nadyanyancat.composeapp.generated.resources.down
import nadyanyancat.composeapp.generated.resources.up
import org.birdushenin.nadyanyancat.data.Pipe
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    val birdY = remember { mutableStateOf(300f) }
    val isGameOver = remember { mutableStateOf(false) }

    val pipes = remember {
        mutableStateListOf(
            Pipe(1000f, 500f),
            Pipe(1500f, 600f)
        )
    }
    val score = remember { mutableStateOf(0) }
    MaterialTheme {
        BackgroundImage()
        Box(modifier = Modifier.fillMaxSize()) {
            FlappyBirdGame(birdY = birdY, pipes = pipes, score = score, isGameOver = isGameOver)
            RestartButton(isGameOver = isGameOver, birdY = birdY, pipes = pipes, score = score)
        }
    }
}

@Composable
fun FlappyBirdGame(
    birdY: MutableState<Float>,
    pipes: MutableList<Pipe>,
    score: MutableState<Int>,
    isGameOver: MutableState<Boolean>
) {
    val textMeasurer = rememberTextMeasurer()
    val text = "Score: ${score.value}"
    val textLayoutResult = textMeasurer.measure(text)

    val density = LocalDensity.current
    val screenWidth = with(density) { getScreenWidth().toPx() }

    val birdSpeed = remember { mutableStateOf(0f) }
    val pipeSpeed = remember { mutableStateOf(5f) }

    // Гравитация
    val gravity = 0.5f
    val jumpStrength = -15f

    val holeHeight = remember { mutableStateOf(400f) }

    val upPipePainter = imageResource(Res.drawable.up)
    val downPipeImage = imageResource(Res.drawable.down)

    LaunchedEffect(isGameOver.value) {
        if (!isGameOver.value) {
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

                pipes.forEach { pipe ->
                    if (pipe.xPosition in 50f..100f) {
                        score.value += 1
                    }
                }

                checkCollision(birdY.value, pipes, holeHeight = holeHeight.value, isGameOver)

                delay(16L)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().pointerInput(Unit) {
        detectTapGestures(
            onTap = {
                if (!isGameOver.value) {
                    birdSpeed.value = jumpStrength
                }
            }
        )
    }) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(Color.Red, radius = 30f, center = Offset(100f, birdY.value))
            pipes.forEach { pipe ->
                drawImage(
                    image = downPipeImage,
                    srcSize = IntSize(upPipePainter.width, upPipePainter.height),
                    dstSize = IntSize(50, pipe.height.toInt()),
                    dstOffset = IntOffset(pipe.xPosition.toInt(), 0),
                    alpha = 1f
                )
                drawImage(
                    image = upPipePainter,
                    srcSize = IntSize(downPipeImage.width, downPipeImage.height),
                    dstSize = IntSize(50, (size.height - pipe.height - holeHeight.value).toInt()),
                    dstOffset = IntOffset(pipe.xPosition.toInt(), (pipe.height + holeHeight.value).toInt()),
                    alpha = 1f
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

            if (isGameOver.value) {
                drawText(
                    textLayoutResult = textMeasurer.measure("Game Over"),
                    brush = SolidColor(Color.Red),
                    topLeft = Offset(200f, 300f),
                    alpha = 1f,
                    shadow = Shadow(color = Color.Gray, offset = Offset(5f, 5f), blurRadius = 2f)
                )
            }
        }
    }
}

@Composable
fun RestartButton(
    isGameOver: MutableState<Boolean>,
    birdY: MutableState<Float>,
    pipes: MutableList<Pipe>,
    score: MutableState<Int>
) {
    if (isGameOver.value) {
        Button(
            onClick = {
                isGameOver.value = false
                birdY.value = 300f
                pipes.clear()
                pipes.addAll(
                    listOf(
                        Pipe(1000f, 500f),
                        Pipe(1500f, 600f)
                    )
                )
                score.value = 0
            },
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(text = "Restart")
        }
    }
}

fun checkCollision(birdY: Float, pipes: List<Pipe>, holeHeight: Float, isGameOver: MutableState<Boolean>) {
    pipes.forEach { pipe ->
        if (pipe.xPosition in 50f..100f) {
            if (birdY - 30f < pipe.height || birdY + 30f > (pipe.height + holeHeight)) {
                isGameOver.value = true
            }
        }
    }
}

@Composable
fun BackgroundImage() {
    Image(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer(
                scaleX = 3f,
                scaleY = 3f
            ),
        painter = painterResource(Res.drawable.background),
        contentDescription = "My Image"
    )
}