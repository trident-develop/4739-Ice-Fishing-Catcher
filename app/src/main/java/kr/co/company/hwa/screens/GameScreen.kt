package kr.co.company.hwa.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kr.co.company.hwa.audio.SoundManager
import kr.co.company.hwa.model.MemoryLevelConfig
import kr.co.company.hwa.model.MemoryLevelData
import kr.co.company.hwa.storage.PrefsManager
import kr.co.company.hwa.ui.components.OceanButton
import kr.co.company.hwa.ui.components.OceanButtonFullWidth
import kr.co.company.hwa.ui.components.OceanButtonStyle
import kr.co.company.hwa.ui.components.UnderwaterBackground
import kr.co.company.hwa.ui.components.pressableWithCooldown
import kr.co.company.hwa.ui.theme.AquaGlow
import kr.co.company.hwa.ui.theme.CoralOrange
import kr.co.company.hwa.ui.theme.DeepOcean
import kr.co.company.hwa.ui.theme.FoamWhite
import kr.co.company.hwa.ui.theme.GameFontFamily
import kr.co.company.hwa.ui.theme.GoldFish
import kr.co.company.hwa.ui.theme.OceanBlue
import kr.co.company.hwa.ui.theme.SeaBlue
import kr.co.company.hwa.ui.theme.SeaGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ─── Domain models ───────────────────────────────────────────────────────────

enum class CardState { FACE_DOWN, FACE_UP, MATCHED }

data class MemoryCard(
    val id: Int,
    val pairId: Int,
    val face: String,
    val state: CardState = CardState.FACE_DOWN
)

enum class GameResult { WIN, LOSE }

// ─── Fish face set — 12 ocean creatures (one per possible pair) ───────────────

private val FISH_FACES = listOf(
    "\uD83D\uDC1F", // 🐟 fish
    "\uD83D\uDC20", // 🐠 tropical fish
    "\uD83D\uDC21", // 🐡 blowfish
    "\uD83E\uDD88", // 🦈 shark
    "\uD83D\uDC19", // 🐙 octopus
    "\uD83E\uDD91", // 🦑 squid
    "\uD83E\uDD90", // 🦐 shrimp
    "\uD83E\uDD80", // 🦀 crab
    "\uD83D\uDC2C", // 🐬 dolphin
    "\uD83D\uDC33", // 🐳 whale
    "\uD83D\uDC1A", // 🐚 shell
    "\uD83C\uDF0A"  // 🌊 wave
)

private fun buildCards(config: MemoryLevelConfig): List<MemoryCard> {
    return FISH_FACES
        .take(config.pairCount)
        .flatMapIndexed { pairId, face ->
            listOf(
                MemoryCard(id = pairId * 2,     pairId = pairId, face = face),
                MemoryCard(id = pairId * 2 + 1, pairId = pairId, face = face)
            )
        }
        .shuffled()
}

private fun formatTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return if (m > 0) "$m:${s.toString().padStart(2, '0')}" else "$s"
}

// ─── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun GameScreen(
    levelNumber: Int,
    prefsManager: PrefsManager,
    soundManager: SoundManager,
    onBackToLevels: () -> Unit,
    onNextLevel: (Int) -> Unit,
    onRetry: () -> Unit
) {
    val config = remember(levelNumber) { MemoryLevelData.getLevel(levelNumber) }
    val scope = rememberCoroutineScope()

    // ── Game state ────────────────────────────────────────────────────────────
    var cards       by remember(levelNumber) { mutableStateOf(buildCards(config)) }
    var flippedIds  by remember(levelNumber) { mutableStateOf<List<Int>>(emptyList()) }
    var movesLeft   by remember(levelNumber) { mutableIntStateOf(config.movesLimit) }
    var matchedPairs by remember(levelNumber) { mutableIntStateOf(0) }
    var isLocked    by remember(levelNumber) { mutableStateOf(false) }
    var timeLeft    by remember(levelNumber) { mutableStateOf(config.timerSeconds) }
    var gameResult  by remember(levelNumber) { mutableStateOf<GameResult?>(null) }
    var score       by remember(levelNumber) { mutableIntStateOf(0) }

    // ── Lifecycle: music pause / resume ───────────────────────────────────────
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE  -> soundManager.pauseMusic()
                Lifecycle.Event.ON_RESUME -> soundManager.resumeMusic()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // ── Countdown timer (optional, only for levels with timerSeconds) ─────────
    LaunchedEffect(Unit) {
        if (config.timerSeconds == null) return@LaunchedEffect
        while ((timeLeft ?: 0) > 0 && gameResult == null) {
            delay(1000L)
            if (gameResult != null) return@LaunchedEffect
            timeLeft = maxOf(0, (timeLeft ?: 0) - 1)
        }
        if (gameResult == null) {
            gameResult = GameResult.LOSE
            soundManager.playLoseSound()
        }
    }

    BackHandler { onBackToLevels() }

    // ── Card tap handler ──────────────────────────────────────────────────────
    fun onCardTap(card: MemoryCard) {
        if (gameResult != null || isLocked) return
        if (card.state != CardState.FACE_DOWN) return
        if (flippedIds.size >= 2) return
        if (card.id in flippedIds) return

        // Reveal the tapped card
        cards = cards.map { if (it.id == card.id) it.copy(state = CardState.FACE_UP) else it }
        val newFlipped = flippedIds + card.id
        flippedIds = newFlipped

        if (newFlipped.size == 2) {
            movesLeft--
            val c1 = cards.first { it.id == newFlipped[0] }
            val c2 = cards.first { it.id == newFlipped[1] }

            if (c1.pairId == c2.pairId) {
                // ── Match ──
                cards = cards.map {
                    if (it.id in newFlipped) it.copy(state = CardState.MATCHED) else it
                }
                flippedIds = emptyList()
                val newMatched = matchedPairs + 1
                matchedPairs = newMatched
                score += 50 + config.level * 5

                when {
                    newMatched == config.pairCount -> {
                        // All pairs found — win
                        val bonus = movesLeft * 10 + (timeLeft ?: 0) * 2
                        val finalScore = score + bonus
                        score = finalScore
                        gameResult = GameResult.WIN
                        soundManager.playWinSound()
                        prefsManager.recordLevelCompletion(levelNumber, finalScore)
                        prefsManager.unlockNextLevel(levelNumber)
                        prefsManager.totalLevelsCompleted++
                    }
                    movesLeft <= 0 -> {
                        // Matched a pair but no moves left and game isn't won
                        gameResult = GameResult.LOSE
                        soundManager.playLoseSound()
                    }
                }
            } else {
                // ── Mismatch — lock, show briefly, then flip back ──
                isLocked = true
                scope.launch {
                    delay(900L)
                    cards = cards.map {
                        if (it.id in newFlipped) it.copy(state = CardState.FACE_DOWN) else it
                    }
                    flippedIds = emptyList()
                    isLocked = false
                    if (movesLeft <= 0 && gameResult == null) {
                        gameResult = GameResult.LOSE
                        soundManager.playLoseSound()
                    }
                }
            }
        }
    }

    // ── Emoji size scales down for more columns ───────────────────────────────
    val emojiFontSize: TextUnit = when (config.columns) {
        2    -> 42.sp
        3    -> 32.sp
        else -> 22.sp
    }

    // ── Layout ────────────────────────────────────────────────────────────────
    Box(modifier = Modifier.fillMaxSize()) {
        UnderwaterBackground(showFish = true)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 12.dp)
                .padding(top = 40.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            GameHud(
                levelNumber  = levelNumber,
                movesLeft    = movesLeft,
                matchedPairs = matchedPairs,
                totalPairs   = config.pairCount,
                timeLeft     = timeLeft
            )

            Spacer(Modifier.height(8.dp))

            // Card grid — each row and column gets equal space via weight(1f)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(config.rows) { row ->
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        repeat(config.columns) { col ->
                            val card = cards[row * config.columns + col]
                            MemoryCardItem(
                                card          = card,
                                modifier      = Modifier.weight(1f).fillMaxHeight(),
                                isLocked      = isLocked,
                                emojiFontSize = emojiFontSize,
                                onClick       = { onCardTap(card) }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                OceanButton(
                    text    = "Back",
                    onClick = onBackToLevels,
                    style   = OceanButtonStyle.Secondary,
                    padding = PaddingValues(horizontal = 32.dp, vertical = 10.dp)
                )
            }

            Spacer(Modifier.height(12.dp))
        }

        // ── Result overlay ────────────────────────────────────────────────────
        gameResult?.let { result ->
            ResultDialog(
                isWin         = result == GameResult.WIN,
                score         = score,
                isLastLevel   = levelNumber >= 30,
                onNextLevel   = { onNextLevel(levelNumber + 1) },
                onRetry       = onRetry,
                onBackToLevels = onBackToLevels
            )
        }
    }
}

// ─── HUD bar ─────────────────────────────────────────────────────────────────

@Composable
private fun GameHud(
    levelNumber: Int,
    movesLeft: Int,
    matchedPairs: Int,
    totalPairs: Int,
    timeLeft: Int?
) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(DeepOcean.copy(alpha = 0.82f))
            .border(1.dp, AquaGlow.copy(alpha = 0.25f), shape)
            .padding(horizontal = 8.dp, vertical = 8.dp)
            ,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        HudCell(label = "Level",  value = "$levelNumber", valueColor = GoldFish)
        HudDivider()
        HudCell(
            label      = "Pairs",
            value      = "$matchedPairs / $totalPairs",
            valueColor = AquaGlow
        )
        HudDivider()
        HudCell(
            label      = "Moves",
            value      = "$movesLeft",
            valueColor = if (movesLeft <= 3) CoralOrange else FoamWhite
        )
        if (timeLeft != null) {
            HudDivider()
            HudCell(
                label      = "Time",
                value      = formatTime(timeLeft),
                valueColor = if (timeLeft <= 10) CoralOrange else FoamWhite
            )
        }
    }
}

@Composable
private fun HudCell(label: String, value: String, valueColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text       = label,
            fontSize   = 10.sp,
            color      = FoamWhite.copy(alpha = 0.6f),
            textAlign  = TextAlign.Center
        )
        Text(
            text       = value,
            fontFamily = GameFontFamily,
            fontSize   = 20.sp,
            color      = valueColor,
            textAlign  = TextAlign.Center
        )
    }
}

@Composable
private fun HudDivider() {
    Box(
        modifier = Modifier
            .height(32.dp)
            .padding(horizontal = 4.dp)
            .background(AquaGlow.copy(alpha = 0.2f))
            .padding(horizontal = 1.dp)
    )
}

// ─── Memory card ─────────────────────────────────────────────────────────────

@Composable
private fun MemoryCardItem(
    card: MemoryCard,
    modifier: Modifier = Modifier,
    isLocked: Boolean,
    emojiFontSize: TextUnit,
    onClick: () -> Unit
) {
    val revealed = card.state != CardState.FACE_DOWN
    val enabled  = card.state == CardState.FACE_DOWN && !isLocked

    val rotation by animateFloatAsState(
        targetValue  = if (revealed) 180f else 0f,
        animationSpec = tween(durationMillis = 380, easing = FastOutSlowInEasing),
        label        = "flip_${card.id}"
    )

    Box(
        modifier = modifier.pressableWithCooldown(
            cooldownMillis = 50L,
            enabled        = enabled,
            onClick        = onClick
        )
    ) {
        // Card back — visible while rotation ≤ 90°
        CardBack(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationY       = rotation
                    cameraDistance  = 12f * density
                    alpha           = if (rotation <= 90f) 1f else 0f
                }
        )
        // Card front — visible once rotation > 90°
        CardFront(
            face          = card.face,
            isMatched     = card.state == CardState.MATCHED,
            emojiFontSize = emojiFontSize,
            modifier      = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationY       = rotation - 180f
                    cameraDistance  = 12f * density
                    alpha           = if (rotation > 90f) 1f else 0f
                }
        )
    }
}

@Composable
private fun CardBack(modifier: Modifier = Modifier) {
    val shape    = RoundedCornerShape(8.dp)
    val gradient = Brush.verticalGradient(listOf(OceanBlue, DeepOcean))
    Box(
        modifier = modifier
            .clip(shape)
            .background(gradient)
            .border(1.5.dp, AquaGlow.copy(alpha = 0.45f), shape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text     = "\uD83C\uDF0A", // 🌊
            fontSize = 22.sp
        )
    }
}

@Composable
private fun CardFront(
    face: String,
    isMatched: Boolean,
    emojiFontSize: TextUnit,
    modifier: Modifier = Modifier
) {
    val shape       = RoundedCornerShape(8.dp)
    val borderColor = if (isMatched) GoldFish.copy(alpha = 0.8f) else AquaGlow.copy(alpha = 0.6f)
    val gradient    = if (isMatched) {
        Brush.verticalGradient(listOf(SeaGreen, SeaGreen.copy(alpha = 0.65f)))
    } else {
        Brush.verticalGradient(listOf(SeaBlue, OceanBlue))
    }
    Box(
        modifier = modifier
            .clip(shape)
            .background(gradient)
            .border(1.5.dp, borderColor, shape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text     = face,
            fontSize = emojiFontSize
        )
    }
}

// ─── Result dialog ────────────────────────────────────────────────────────────

@Composable
private fun ResultDialog(
    isWin: Boolean,
    score: Int,
    isLastLevel: Boolean,
    onNextLevel: () -> Unit,
    onRetry: () -> Unit,
    onBackToLevels: () -> Unit
) {
    val shape       = RoundedCornerShape(20.dp)
    val accentColor = if (isWin) GoldFish else CoralOrange
    val gradient    = Brush.verticalGradient(listOf(OceanBlue, DeepOcean))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) { detectTapGestures { /* consume */ } }
            .background(Color.Black.copy(alpha = 0.65f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.82f)
                .clip(shape)
                .background(gradient)
                .border(2.dp, accentColor.copy(alpha = 0.8f), shape)
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text       = if (isWin) "Level Complete!" else "Level Failed",
                fontFamily = GameFontFamily,
                fontSize   = 28.sp,
                color      = accentColor,
                textAlign  = TextAlign.Center
            )

            if (isWin) {
                Text(
                    text       = "Score: $score",
                    fontFamily = GameFontFamily,
                    fontSize   = 22.sp,
                    color      = AquaGlow
                )
            } else {
                Text(
                    text      = "Better luck next time!",
                    fontSize  = 15.sp,
                    color     = FoamWhite.copy(alpha = 0.75f),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(4.dp))

            if (isWin && !isLastLevel) {
                OceanButtonFullWidth(
                    text  = "Next Level",
                    onClick = onNextLevel,
                    style = OceanButtonStyle.Accent
                )
            }
            OceanButtonFullWidth(
                text  = "Replay",
                onClick = onRetry,
                style = OceanButtonStyle.Primary
            )
            OceanButtonFullWidth(
                text  = "Levels",
                onClick = onBackToLevels,
                style = OceanButtonStyle.Secondary
            )
        }
    }
}
