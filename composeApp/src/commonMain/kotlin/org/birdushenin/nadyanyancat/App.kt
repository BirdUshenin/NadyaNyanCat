package org.birdushenin.nadyanyancat

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
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
import nadyanyancat.composeapp.generated.resources.*
import nadyanyancat.composeapp.generated.resources.Res
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
    val highScore = remember { mutableStateOf(0) }

    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            when (gameState.value) {
                GameState.SPLASH -> SplashScreen { gameState.value = GameState.MENU }
                GameState.MENU -> MainMenuScreen { gameState.value = GameState.GAME }
                GameState.GAME -> GameScreen(
                    onExit = { gameState.value = GameState.MENU },
                    highScore = highScore
                )
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
    val storage = RecordStorage(provideSettings())
    val record = storage.getRecord()

    Box(
        modifier = Modifier
            .background(Color(0xFFEAE89C))
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                modifier = Modifier
                    .height(224.dp)
                    .width(142.dp)
                    .clip(RoundedCornerShape(42.dp))
                    .clickable {
                        onStartClick()
                    },
                painter = painterResource(Res.drawable.cat),
                contentDescription = "Start Button"
            )
            Text(
                text = "Надя Прыг 2",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Рекорд: $record",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFA90024)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                modifier = Modifier
                    .clip(RoundedCornerShape(42.dp))
                    .clickable {
                        onStartClick()
                    },
                painter = painterResource(Res.drawable.start),
                contentDescription = "Start Button"
            )
        }
    }
}

@Composable
fun GameScreen(
    onExit: () -> Unit,
    highScore: MutableState<Int>
) {
    val birdY = remember { mutableStateOf(300f) }
    val isGameOver = remember { mutableStateOf(false) }
    val pipes = remember { mutableStateListOf(Pipe(1000f, 500f), Pipe(1500f, 600f)) }
    val score = remember { mutableStateOf(0) }

    val holeHeight = remember { mutableStateOf(400f) }
    val pipeSpeed = remember { mutableStateOf(5f) }

    val storage = RecordStorage(provideSettings())
    val record = storage.getRecord()

    val textRecord = if (record > score.value) {
        "До рекорда: ${record - score.value}"
    } else {
        "Новый рекорд: ${score.value}"
    }

    var clickMoon by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {

        BackgroundImage(gameOver = isGameOver.value)

        FlappyBirdGame(birdY, pipes, score, isGameOver, holeHeight, pipeSpeed)

        if (isGameOver.value) {
            if (score.value > highScore.value) {
                highScore.value = score.value
                storage.saveRecord(highScore.value)
            }
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Игра окончена!",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFA90024)
                )
                Text(
                    text = textRecord,
                    modifier = Modifier.padding(vertical = 16.dp),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Row (
                    modifier = Modifier
                        .clickable { onExit() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        modifier = Modifier
                            .size(82.dp)
                            .clip(RoundedCornerShape(42.dp))
                            .clickable {
                                onExit()
                            },
                        painter = painterResource(Res.drawable.menu),
                        contentDescription = "Menu Button"
                    )
                }
                RestartButton(
                    isGameOver, birdY, pipes, score
                ) {
                    clickMoon = false
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Column {
                if (isGameOver.value) {
                    if (!clickMoon) {
                        Image(
                            modifier = Modifier
                                .size(82.dp)
                                .padding(end = 16.dp)
                                .clickable {
                                    clickMoon = true
                                },
                            painter = painterResource(Res.drawable.moon),
                            contentDescription = "Moon"
                        )
                    }
                    if (clickMoon) {
                        Image(
                            modifier = Modifier
                                .size(82.dp)
                                .padding(end = 16.dp)
                                .clickable {
                                    clickMoon = true
                                },
                            painter = painterResource(Res.drawable.cat),
                            contentDescription = "cat"
                        )
                        Text(
                            text = "Мяу мяу мяу",
                            color = Color.White,
                            fontSize = 18.sp,
                        )
                        Text(
                            text = "Люблю Надю ❤\uFE0F",
                            color = Color.White,
                            fontSize = 18.sp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FlappyBirdGame(
    birdY: MutableState<Float>,
    pipes: MutableList<Pipe>,
    score: MutableState<Int>,
    isGameOver: MutableState<Boolean>,
    holeHeight: MutableState<Float>,
    pipeSpeed: MutableState<Float>
) {
    val textMeasurer = rememberTextMeasurer()
    val text = "Очки: ${score.value}"
    val textLayoutResult = textMeasurer.measure(text)

    val density = LocalDensity.current
    val screenWidth = with(density) { getScreenWidth().toPx() }

    val birdSpeed = remember { mutableStateOf(0f) }

    // Гравитация
    var gravity = 0.5f
    var jumpStrength = -15f

    val upPipePainter = imageResource(Res.drawable.up)
    val downPipeImage = imageResource(Res.drawable.down)

    val birdImage = imageResource(Res.drawable.nadya)

    var time by remember { mutableStateOf(0) }
    Timer(
        timeCount = { newTime -> time = newTime },
        resetTimer = isGameOver.value
    )

    when (time) {
        in 0..30 -> {  // 0-30 секунд
            holeHeight.value = 400f
            pipeSpeed.value = 5f
            gravity = 0.5f
            jumpStrength = -15f
        }
        in 30..60 -> {  // 30-60 секунд
            holeHeight.value = 370f
            pipeSpeed.value = 5f
            gravity = 0.5f
            jumpStrength = -15f
        }
        in 60..120 -> {  // 60-120 секунд
            holeHeight.value = 350f
            pipeSpeed.value = 7f
            gravity = 0.9f
            jumpStrength = -20f
        }
        in 120..Int.MAX_VALUE -> {  // 60-120 секунд
            holeHeight.value = 345f
            pipeSpeed.value = 8f
            gravity = 1.2f
            jumpStrength = -20f
        }
        else -> {
            holeHeight.value = 345f
            pipeSpeed.value = 8f
            gravity = 1.2f
            jumpStrength = -20f
        }
    }

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
                        val newHeight = (0..1000).random().toFloat()
                        pipes[index] = Pipe(xPosition = screenWidth, height = newHeight)
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
        }
    }
}

@Composable
fun Timer(
    timeCount: (Int) -> Unit,
    resetTimer: Boolean
) {
    var time by remember { mutableStateOf(0) }

    LaunchedEffect(resetTimer) {
        if (resetTimer) {
            time = 0
        } else {
            while (true) {
                delay(1000L)
                time++
                timeCount(time)
            }
        }
    }
}

@Composable
fun RestartButton(
    isGameOver: MutableState<Boolean>,
    birdY: MutableState<Float>,
    pipes: MutableList<Pipe>,
    score: MutableState<Int>,
    click: () -> Unit
) {
    if (isGameOver.value) {
        Image(
            modifier = Modifier
                .size(92.dp)
                .padding(top = 16.dp)
                .clip(RoundedCornerShape(22.dp))
                .clickable {
                    click()
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
            painter = painterResource(Res.drawable.restart),
            contentDescription = "Start Button"
        )
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
fun BackgroundImage(
    gameOver: Boolean
) {
    val tint = if (gameOver) {
        ColorFilter.tint(Color.Black)
    } else {
        null
    }

    Image(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer(
                scaleX = 3f,
                scaleY = 3f
            ),
        colorFilter = tint,
        painter = painterResource(Res.drawable.background),
        contentDescription = "My Image"
    )
}