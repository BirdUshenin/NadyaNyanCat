package org.birdushenin.nadyanyancat

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import nadyanyancat.composeapp.generated.resources.Res
import nadyanyancat.composeapp.generated.resources.bc2
import nadyanyancat.composeapp.generated.resources.down
import nadyanyancat.composeapp.generated.resources.nadya
import nadyanyancat.composeapp.generated.resources.up
import org.birdushenin.nadyanyancat.data.Pipe
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    val gameState = remember { mutableStateOf(GameState.SPLASH) }

    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            when (gameState.value) {
                GameState.SPLASH -> SplashScreen { gameState.value = GameState.MENU }
                GameState.MENU -> MainMenuScreen { gameState.value = GameState.GAME }
                GameState.GAME -> GameScreen { gameState.value = GameState.MENU }
            }
        }
    }
}

enum class GameState {
    SPLASH, MENU, GAME
}

@Composable
fun SplashScreen(onTimeout: () -> Unit) {

    LaunchedEffect(Unit) {
        delay(3000)
        onTimeout()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDFFFD)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        GifLoader(
            url = "https://i.pinimg.com/originals/98/81/5f/98815f30af15d94ab3dd1af44ef8e6a9.gif"
        )
        Text(
            text = "Meow Production",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun MainMenuScreen(onStartClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(Color(0xFFEAE89C))
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Flappy Bird",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onStartClick) {
                Text(text = "Старт")
            }
        }
    }
}

@Composable
fun GameScreen(onExit: () -> Unit) {
    val birdY = remember { mutableStateOf(300f) }
    val isGameOver = remember { mutableStateOf(false) }
    val pipes = remember { mutableStateListOf(Pipe(1000f, 500f), Pipe(1500f, 600f)) }
    val score = remember { mutableStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        FlappyBirdGame(birdY, pipes, score, isGameOver)

        if (isGameOver.value) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Game Over", fontSize = 24.sp, color = Color.Red)
                Button(onClick = onExit) {
                    Text("В меню")
                }
                RestartButton(isGameOver, birdY, pipes, score)
            }
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

    val birdImage = imageResource(Res.drawable.nadya)

    LaunchedEffect(isGameOver.value) {
        if (!isGameOver.value) {
            while (true) {
                birdSpeed.value += gravity
                birdY.value += birdSpeed.value

                pipes.forEach {
                    it.xPosition -= pipeSpeed.value
                }

                pipes.forEachIndexed { index, pipe ->
                    if (pipe.xPosition < -50) {
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

            drawImage(
                image = birdImage,
                srcSize = IntSize(birdImage.width, birdImage.height),
                dstSize = IntSize(60, 80),
                dstOffset = IntOffset(70, birdY.value.toInt()),
                alpha = 1f
            )

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
            if (birdY - 5f < pipe.height || birdY + 30f > (pipe.height + holeHeight)) {
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
                scaleX = 2f,
                scaleY = 2f
            ),
        painter = painterResource(Res.drawable.bc2),
        contentDescription = "My Image"
    )
}