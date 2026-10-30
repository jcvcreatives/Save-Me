package com.example

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import kotlin.math.exp
import kotlin.random.Random
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlinx.coroutines.delay

// ============================================================================
// 1. COLOR PALETTE & THEME DEFINITIONS ("GRAPH PAPER DOODLE")
// ============================================================================

object DoodleColors {
    val PaperOffWhite = Color(0xFFFAF9F6)
    val GridLineLight = Color(0xFFDCE6F1)
    val GridLineMajor = Color(0xFFC5D6E8)
    val MarginRed = Color(0xFFF4B4B4)

    val InkBlack = Color(0xFF1E1E1E)
    val InkBlue = Color(0xFF1B4F72)
    val CorrectGreen = Color(0xFF2ECC71)
    val WrongRed = Color(0xFFE74C3C)
    val DoodleYellow = Color(0xFFFFD166)

    val HintMagnifierGreen = Color(0xFF4CAF50)
    val HintPotionBlue = Color(0xFF2196F3)
    val HintBulbOrange = Color(0xFFFF9800)

    val ChalkboardDark = Color(0xFF263238)
    val ChalkboardGrid = Color(0xFF37474F)
    val WaterBlue = Color(0xFF4FC3F7)
    val WaterDeep = Color(0xFF0288D1)
    val SharkGrey = Color(0xFF546E7A)

    val UfoSilver = Color(0xFFB0BEC5)
    val UfoDomeCyan = Color(0xFF80DEEA)
    val UfoBeamGreen = Color(0xFF76FF03)

    val LavaOrange = Color(0xFFFF5722)
    val LavaYellow = Color(0xFFFFEB3B)
    val CrateStone = Color(0xFFD7CCC8)
    val CrateStoneDark = Color(0xFFA1887F)

    val DinoGreen = Color(0xFF689F38)
    val DinoLightGreen = Color(0xFF9CCC65)

    val BalloonPalette = listOf(
        Color(0xFFFF595E), // Coral Red
        Color(0xFFFFCA3A), // Sunny Yellow
        Color(0xFF8AC926), // Lime Green
        Color(0xFF1982C4), // Ocean Blue
        Color(0xFF6A4C93), // Grape Purple
        Color(0xFFFF924C)  // Tangerine Orange
    )
}

enum class PaperTheme(
    val title: String,
    val bg: Color,
    val gridMinor: Color,
    val gridMajor: Color
) {
    CLASSIC("Classic Graph", Color(0xFFFAF9F6), Color(0xFFDCE6F1), Color(0xFFC4D7ED)),
    SUNNY("Sunny Pad", Color(0xFFFFFDF2), Color(0xFFEFE4B0), Color(0xFFE0CE7E)),
    MINT("Mint Sketch", Color(0xFFF4FBF7), Color(0xFFD0ECE0), Color(0xFFB3DFCB)),
    LAVENDER("Lavender Note", Color(0xFFF9F6FD), Color(0xFFE3D8F4), Color(0xFFCBB8EA))
}

// ============================================================================
// 2. DYNAMIC PERIL SCENARIOS & LEVEL MODELS
// ============================================================================

/**
 * Rotates automatically based on the level number (`level % 4`):
 * - Levels 1, 5, 9...  -> SHARK_BALLOONS
 * - Levels 2, 6, 10... -> UFO_ABDUCTION
 * - Levels 3, 7, 11... -> VOLCANO_BLOCKS
 * - Levels 4, 8, 12... -> DINO_STEALTH
 */
enum class PerilType(
    val displayName: String,
    val panicBubbleText: String,
    val gameOverTitle: String,
    val gameOverDescription: String,
    val extraLifeLabel: String,
    val hitEffectLabel: String
) {
    SHARK_BALLOONS(
        displayName = "Shark & Balloons",
        panicBubbleText = "Come on, you can do that!",
        gameOverTitle = "SPLASH! SHARK SNACK",
        gameOverDescription = "All 6 balloons popped! The shark caught up.",
        extraLifeLabel = "+1 Balloon attached!",
        hitEffectLabel = "POP!"
    ),
    UFO_ABDUCTION(
        displayName = "UFO Abduction",
        panicBubbleText = "Not the alien probe!",
        gameOverTitle = "ZAP! ABDUCTED!",
        gameOverDescription = "The UFO tractor beam pulled the stickman aboard!",
        extraLifeLabel = "+1 Gravity Anchor!",
        hitEffectLabel = "ZAP!"
    ),
    VOLCANO_BLOCKS(
        displayName = "Volcano Crates",
        panicBubbleText = "My shoes are melting!",
        gameOverTitle = "SIZZLE! MELTDOWN!",
        gameOverDescription = "All 6 stone crates crumbled into the bubbling lava!",
        extraLifeLabel = "+1 Stone Crate stacked!",
        hitEffectLabel = "CRUMBLE!"
    ),
    DINO_STEALTH(
        displayName = "Sleeping T-Rex",
        panicBubbleText = "Shhh! Don't wake Rex!",
        gameOverTitle = "ROOOAR! REX AWOKE!",
        gameOverDescription = "The Noise Meter maxed out and woke the giant T-Rex!",
        extraLifeLabel = "-1 Noise Bar hushed!",
        hitEffectLabel = "CLANG!"
    );

    companion object {
        fun forLevel(levelNumber: Int): PerilType {
            val normalized = if (levelNumber <= 0) 1 else levelNumber
            return when (normalized % 4) {
                1 -> SHARK_BALLOONS
                2 -> UFO_ABDUCTION
                3 -> VOLCANO_BLOCKS
                else -> DINO_STEALTH
            }
        }
    }
}

data class WordLevel(
    val number: Int,
    val word: String,
    val clue: String,
    val isAdventure: Boolean = false
) {
    val perilType: PerilType
        get() = PerilType.forLevel(if (number <= 0) 1 else number)

    /** Every 5th level is a Boss Level with 3 bomb letters on the keyboard! */
    val isBossLevel: Boolean
        get() = isAdventure || (number > 0 && number % 5 == 0)
}

enum class ScreenState {
    MAIN_MENU,
    GAMEPLAY,
    TWO_PLAYER_SETUP
}

enum class KeyStatus {
    DEFAULT,
    CORRECT,
    WRONG
}

enum class RoundOutcome {
    PLAYING,
    VICTORY,
    GAME_OVER
}

/**
 * Lightweight procedural sound engine that synthesizes 16-bit mono PCM WAV audio
 * on the fly and plays it through standard Android [MediaPlayer] APIs.
 */
enum class DoodleSoundEffect {
    POP,      // Balloon pop (sharp pitch drop + transient burst)
    SPLASH,   // Shark water splash (filtered noise + bubbly modulation)
    CRUNCH,   // Volcano stone crate crumble (multi-impulse rock crackle)
    ZAP,      // UFO tractor beam pulse
    CLANG,    // Sleeping T-Rex metallic noise clang
    CHIME,    // Correct letter / power-up chime
    VICTORY   // Level complete 4-note fanfare
}

object ProceduralSoundEngine {
    var soundEnabled: Boolean = true
    var lastPlayedEffect: DoodleSoundEffect? = null
        private set

    private var cacheDir: File? = null
    private val wavFiles = mutableMapOf<DoodleSoundEffect, File>()

    fun init(context: Context) {
        if (cacheDir != null) return
        cacheDir = File(context.cacheDir, "doodle_sfx").apply { mkdirs() }
    }

    fun play(effect: DoodleSoundEffect) {
        if (!soundEnabled) return
        lastPlayedEffect = effect
        val dir = cacheDir ?: return

        runCatching {
            val wavFile = wavFiles.getOrPut(effect) {
                val file = File(dir, "sfx_${effect.name.lowercase()}.wav")
                if (!file.exists() || file.length() == 0L) {
                    val pcmSamples = synthesizeSamples(effect)
                    val wavBytes = encodePcm16MonoWav(pcmSamples, SAMPLE_RATE)
                    FileOutputStream(file).use { it.write(wavBytes) }
                }
                file
            }

            val mediaPlayer = MediaPlayer()
            FileInputStream(wavFile).use { fis ->
                mediaPlayer.setDataSource(fis.fd)
            }
            mediaPlayer.setOnCompletionListener { mp ->
                runCatching {
                    mp.reset()
                    mp.release()
                }
            }
            mediaPlayer.setOnErrorListener { mp, _, _ ->
                runCatching {
                    mp.reset()
                    mp.release()
                }
                true
            }
            mediaPlayer.prepare()
            mediaPlayer.start()
        }
    }

    private const val SAMPLE_RATE = 22050

    private fun synthesizeSamples(effect: DoodleSoundEffect): ShortArray {
        val rng = Random(effect.ordinal * 997 + 42)
        return when (effect) {
            DoodleSoundEffect.POP -> {
                // 120ms sharp balloon pop: rapid frequency sweep 760Hz -> 110Hz + initial transient snap
                val count = (SAMPLE_RATE * 0.12).toInt()
                ShortArray(count) { i ->
                    val t = i.toDouble() / SAMPLE_RATE
                    val progress = i.toDouble() / count
                    val freq = 760.0 * exp(-progress * 2.1) + 90.0
                    val env = exp(-progress * 6.5)
                    val snap = if (i < SAMPLE_RATE * 0.012) (rng.nextDouble() * 2.0 - 1.0) * 0.7 else 0.0
                    val wave = (sin(2.0 * PI * freq * t) * 0.8 + snap) * env
                    (wave.coerceIn(-1.0, 1.0) * Short.MAX_VALUE * 0.85).toInt().toShort()
                }
            }

            DoodleSoundEffect.SPLASH -> {
                // 450ms shark water splash: band-modulated water spray noise + bubbly low tones
                val count = (SAMPLE_RATE * 0.45).toInt()
                var lpState = 0.0
                ShortArray(count) { i ->
                    val t = i.toDouble() / SAMPLE_RATE
                    val progress = i.toDouble() / count
                    val noise = rng.nextDouble() * 2.0 - 1.0
                    val cutoff = 0.35 - progress * 0.22
                    lpState += cutoff * (noise - lpState)
                    val bubbleFreq = 180.0 + 90.0 * sin(2.0 * PI * 19.0 * t)
                    val bubble = sin(2.0 * PI * bubbleFreq * t) * 0.35
                    val env = sin(progress * PI) * exp(-progress * 2.2)
                    val wave = (lpState * 0.85 + bubble) * env
                    (wave.coerceIn(-1.0, 1.0) * Short.MAX_VALUE * 0.85).toInt().toShort()
                }
            }

            DoodleSoundEffect.CRUNCH -> {
                // 320ms volcano stone crate crunch: granular multi-impulse rock fracture
                val count = (SAMPLE_RATE * 0.32).toInt()
                ShortArray(count) { i ->
                    val t = i.toDouble() / SAMPLE_RATE
                    val progress = i.toDouble() / count
                    val grainPulse = if ((i / 420) % 2 == 0) 1.0 else 0.28
                    val lowRumble = sin(2.0 * PI * 145.0 * t) + 0.5 * sin(2.0 * PI * 230.0 * t)
                    val grit = (rng.nextDouble() * 2.0 - 1.0) * 0.75
                    val env = exp(-progress * 3.8) * grainPulse
                    val wave = (lowRumble * 0.45 + grit * 0.55) * env
                    (wave.coerceIn(-1.0, 1.0) * Short.MAX_VALUE * 0.85).toInt().toShort()
                }
            }

            DoodleSoundEffect.ZAP -> {
                // 200ms UFO tractor beam FM zap
                val count = (SAMPLE_RATE * 0.20).toInt()
                ShortArray(count) { i ->
                    val t = i.toDouble() / SAMPLE_RATE
                    val progress = i.toDouble() / count
                    val carrier = 880.0 - progress * 520.0
                    val mod = sin(2.0 * PI * 45.0 * t) * 120.0
                    val env = exp(-progress * 3.5)
                    val wave = sin(2.0 * PI * (carrier + mod) * t) * env
                    (wave.coerceIn(-1.0, 1.0) * Short.MAX_VALUE * 0.75).toInt().toShort()
                }
            }

            DoodleSoundEffect.CLANG -> {
                // 280ms T-Rex stealth metallic clang
                val count = (SAMPLE_RATE * 0.28).toInt()
                ShortArray(count) { i ->
                    val t = i.toDouble() / SAMPLE_RATE
                    val progress = i.toDouble() / count
                    val partials = sin(2.0 * PI * 580.0 * t) * 0.45 +
                        sin(2.0 * PI * 935.0 * t) * 0.35 +
                        sin(2.0 * PI * 1410.0 * t) * 0.20
                    val env = exp(-progress * 5.2)
                    (partials * env * Short.MAX_VALUE * 0.8).toInt().toShort()
                }
            }

            DoodleSoundEffect.CHIME -> {
                // 160ms two-tone positive ink chime
                val count = (SAMPLE_RATE * 0.16).toInt()
                ShortArray(count) { i ->
                    val t = i.toDouble() / SAMPLE_RATE
                    val progress = i.toDouble() / count
                    val freq = if (progress < 0.45) 587.33 else 880.0
                    val env = exp(-progress * 4.0)
                    (sin(2.0 * PI * freq * t) * env * Short.MAX_VALUE * 0.65).toInt().toShort()
                }
            }

            DoodleSoundEffect.VICTORY -> {
                // 440ms 4-note major arpeggio (C5 - E5 - G5 - C6)
                val notes = doubleArrayOf(523.25, 659.25, 783.99, 1046.50)
                val count = (SAMPLE_RATE * 0.44).toInt()
                val noteLen = count / notes.size
                ShortArray(count) { i ->
                    val t = i.toDouble() / SAMPLE_RATE
                    val noteIdx = (i / noteLen).coerceIn(0, notes.lastIndex)
                    val localProg = (i % noteLen).toDouble() / noteLen
                    val env = exp(-localProg * 3.0)
                    val wave = sin(2.0 * PI * notes[noteIdx] * t) * env
                    (wave * Short.MAX_VALUE * 0.7).toInt().toShort()
                }
            }
        }
    }

    private fun encodePcm16MonoWav(samples: ShortArray, sampleRate: Int): ByteArray {
        val dataSize = samples.size * 2
        val totalSize = 36 + dataSize
        val out = ByteArrayOutputStream(44 + dataSize)

        fun writeAscii(s: String) = out.write(s.toByteArray(Charsets.US_ASCII))
        fun writeIntLE(v: Int) {
            out.write(v and 0xFF)
            out.write((v ushr 8) and 0xFF)
            out.write((v ushr 16) and 0xFF)
            out.write((v ushr 24) and 0xFF)
        }
        fun writeShortLE(v: Int) {
            out.write(v and 0xFF)
            out.write((v ushr 8) and 0xFF)
        }

        writeAscii("RIFF")
        writeIntLE(totalSize)
        writeAscii("WAVE")
        writeAscii("fmt ")
        writeIntLE(16) // PCM chunk size
        writeShortLE(1) // AudioFormat = 1 (PCM)
        writeShortLE(1) // NumChannels = 1 (Mono)
        writeIntLE(sampleRate)
        writeIntLE(sampleRate * 2) // ByteRate
        writeShortLE(2) // BlockAlign
        writeShortLE(16) // BitsPerSample = 16
        writeAscii("data")
        writeIntLE(dataSize)
        for (s in samples) {
            writeShortLE(s.toInt())
        }
        return out.toByteArray()
    }
}

val CAMPAIGN_LEVELS = listOf(
    WordLevel(1, "DIAMOND", "Guess a precious gemstone"),
    WordLevel(2, "CHINA", "What country was paper invented in?"),
    WordLevel(3, "PRANK", "April Fool's Day tradition"),
    WordLevel(4, "CAKE", "Sweet baked treat with birthday candles"),
    WordLevel(5, "GIRAFFE", "Boss Level: Tallest mammal with a long neck"),
    WordLevel(6, "COMPASS", "Navigation tool that always points north"),
    WordLevel(7, "VOLCANO", "Mountain that erupts with glowing lava"),
    WordLevel(8, "PENGUIN", "Tuxedo-wearing bird that cannot fly"),
    WordLevel(9, "RAINBOW", "Colorful arc seen in the sky after rain"),
    WordLevel(10, "PIRATE", "Boss Level: High-seas sailor seeking treasure"),
    WordLevel(11, "CACTUS", "Spiky desert plant that stores water"),
    WordLevel(12, "GUITAR", "Six-stringed musical instrument"),
    WordLevel(13, "OCTOPUS", "Clever sea creature with eight tentacles"),
    WordLevel(14, "POPCORN", "Buttery snack eaten at the movie theater"),
    WordLevel(15, "ASTRONAUT", "Boss Level: Explorer who travels into outer space"),
    WordLevel(16, "IGLOO", "Dome-shaped shelter built from snow blocks"),
    WordLevel(17, "TORNADO", "Violently rotating column of wind"),
    WordLevel(18, "PANCAKE", "Flat breakfast cake served with maple syrup"),
    WordLevel(19, "DINOSAUR", "Prehistoric reptile that ruled the Earth"),
    WordLevel(20, "TREASURE", "Boss Level: Chest overflowing with gold and jewels")
)

val ADVENTURE_LEVELS = listOf(
    WordLevel(1, "LABYRINTH", "Devil Boss: An intricate maze of winding passages", true),
    WordLevel(2, "CHAMELEON", "Devil Boss: Lizard famous for shifting its colors", true),
    WordLevel(3, "AVALANCHE", "Devil Boss: Sudden rapid rush of snow down a cliff", true),
    WordLevel(4, "QUICKSAND", "Devil Boss: Loose wet sand that traps the unwary", true),
    WordLevel(5, "JELLYFISH", "Devil Boss: Ocean drifter with stinging tentacles", true),
    WordLevel(6, "SAXOPHONE", "Devil Boss: Brass woodwind instrument iconic in jazz", true),
    WordLevel(7, "METEORITE", "Devil Boss: Space rock that survives falling to Earth", true),
    WordLevel(8, "KALEIDOSCOPE", "Devil Boss: Optical tube of mirrors and colorful glass", true)
)

// ============================================================================
// 3. GAME VIEWMODEL (PERILS, BOSS BOMBS, COINS, STREAKS, POWER-UPS)
// ============================================================================

class WordGameViewModel : ViewModel() {
    var currentScreen by mutableStateOf(ScreenState.MAIN_MENU)
        private set

    // Player economy & progression
    var coins by mutableIntStateOf(1231)
        private set
    var campaignLevelIndex by mutableIntStateOf(0)
        private set
    var adventureLevelIndex by mutableIntStateOf(0)
        private set

    // Victory / Progression stats
    var perfectCount by mutableIntStateOf(3)
        private set
    var currentStreak by mutableIntStateOf(2)
        private set
    var maxStreak by mutableIntStateOf(5)
        private set
    var chestProgress by mutableIntStateOf(1) // 1..4
        private set
    var themeProgress by mutableIntStateOf(1) // 1..10
        private set

    // Power-up inventory
    var freeShieldPotions by mutableIntStateOf(2)
        private set

    // Settings
    var soundEffectsEnabled by mutableStateOf(true)
        private set
    var useCursiveFont by mutableStateOf(false)
        private set
    var selectedPaperTheme by mutableStateOf(PaperTheme.CLASSIC)
        private set
    var showSettingsDialog by mutableStateOf(false)
    var showCoinShopDialog by mutableStateOf(false)

    // Active Round State
    var activeLevel by mutableStateOf(CAMPAIGN_LEVELS[0])
        private set
    var maxBalloons by mutableIntStateOf(6)
        private set
    var balloonsRemaining by mutableIntStateOf(6)
        private set
    var mistakesThisRound by mutableIntStateOf(0)
        private set
    var roundOutcome by mutableStateOf(RoundOutcome.PLAYING)
        private set

    val keyStatuses = mutableStateMapOf<Char, KeyStatus>()
    val revealedChars = mutableStateListOf<Char>()
    val bombLetters = mutableStateListOf<Char>()

    // Animation triggers for wrong guess / hazard hit / life loss
    var lastPoppedBalloonIndex by mutableIntStateOf(-1)
        private set
    var popAnimationTrigger by mutableIntStateOf(0)
        private set
    var lastDamageAmount by mutableIntStateOf(1)
        private set
    var lastHitWasBomb by mutableStateOf(false)
        private set
    var statusBannerMessage by mutableStateOf<String?>(null)
        private set

    val currentCampaignLevelNumber: Int
        get() = campaignLevelIndex + 1

    val activePerilType: PerilType
        get() = activeLevel.perilType

    fun startCampaignLevel() {
        val template = CAMPAIGN_LEVELS[campaignLevelIndex % CAMPAIGN_LEVELS.size]
        val level = template.copy(number = campaignLevelIndex + 1)
        loadLevel(level)
        currentScreen = ScreenState.GAMEPLAY
    }

    fun startAdventureLevel() {
        val template = ADVENTURE_LEVELS[adventureLevelIndex % ADVENTURE_LEVELS.size]
        val level = template.copy(number = adventureLevelIndex + 1, isAdventure = true)
        loadLevel(level)
        currentScreen = ScreenState.GAMEPLAY
    }

    fun openTwoPlayerSetup() {
        currentScreen = ScreenState.TWO_PLAYER_SETUP
    }

    fun startTwoPlayerGame(rawWord: String, rawClue: String, chosenPerilIndex: Int = 1) {
        val cleanedWord = rawWord.uppercase().filter { it in 'A'..'Z' }.take(12)
        val finalWord = cleanedWord.ifEmpty { "SHARK" }
        val finalClue = rawClue.trim().ifEmpty { "Two-Player Secret Word!" }
        loadLevel(
            WordLevel(
                number = chosenPerilIndex,
                word = finalWord,
                clue = finalClue,
                isAdventure = false
            )
        )
        currentScreen = ScreenState.GAMEPLAY
    }

    fun navigateToMainMenu() {
        currentScreen = ScreenState.MAIN_MENU
        roundOutcome = RoundOutcome.PLAYING
    }

    /** Allows jumping directly to a specific level number (great for testing perils & boss levels) */
    fun selectCampaignLevelNumber(levelNumber: Int) {
        campaignLevelIndex = (levelNumber - 1).coerceAtLeast(0)
        startCampaignLevel()
    }

    private fun loadLevel(level: WordLevel) {
        activeLevel = level
        maxBalloons = 6
        balloonsRemaining = 6
        mistakesThisRound = 0
        roundOutcome = RoundOutcome.PLAYING
        keyStatuses.clear()
        revealedChars.clear()
        bombLetters.clear()
        lastPoppedBalloonIndex = -1
        lastDamageAmount = 1
        lastHitWasBomb = false
        statusBannerMessage = null

        // If Boss Level (every 5th level or Adventure), mark 3 random wrong letters with a red bomb dot
        if (level.isBossLevel) {
            val targetSet = level.word.uppercase().toSet()
            val wrongPool = ('A'..'Z').filter { !targetSet.contains(it) }
            // Deterministic seeded selection per level word + number so tests & replays are consistent
            val step = 5
            val picked = mutableListOf<Char>()
            var idx = (level.number * 3) % wrongPool.size
            while (picked.size < 3 && picked.size < wrongPool.size) {
                val candidate = wrongPool[idx % wrongPool.size]
                if (!picked.contains(candidate)) {
                    picked.add(candidate)
                }
                idx += step
            }
            bombLetters.addAll(picked)
        }
    }

    fun onLetterTapped(letter: Char) {
        if (roundOutcome != RoundOutcome.PLAYING) return
        val upper = letter.uppercaseChar()
        if (keyStatuses[upper] != null && keyStatuses[upper] != KeyStatus.DEFAULT) return

        val targetWord = activeLevel.word.uppercase()
        if (targetWord.contains(upper)) {
            keyStatuses[upper] = KeyStatus.CORRECT
            if (!revealedChars.contains(upper)) {
                revealedChars.add(upper)
            }
            ProceduralSoundEngine.play(DoodleSoundEffect.CHIME)
            checkVictoryCondition()
        } else {
            keyStatuses[upper] = KeyStatus.WRONG
            mistakesThisRound++
            val isBomb = bombLetters.contains(upper)
            val damage = if (isBomb) 2 else 1
            lastDamageAmount = damage
            lastHitWasBomb = isBomb

            if (balloonsRemaining > 0) {
                lastPoppedBalloonIndex = (balloonsRemaining - 1).coerceAtLeast(0)
                balloonsRemaining = (balloonsRemaining - damage).coerceAtLeast(0)
                popAnimationTrigger++
            }
            if (isBomb) {
                statusBannerMessage = "💥 BOMB LETTER '$upper'! -2 ❤️"
            }
            if (balloonsRemaining <= 0) {
                currentStreak = 0
                roundOutcome = RoundOutcome.GAME_OVER
                // Play Splash on Shark Game Over, Crunch on Volcano, or hazard-specific sound
                val gameOverSound = when (activePerilType) {
                    PerilType.SHARK_BALLOONS -> DoodleSoundEffect.SPLASH
                    PerilType.VOLCANO_BLOCKS -> DoodleSoundEffect.CRUNCH
                    PerilType.UFO_ABDUCTION -> DoodleSoundEffect.ZAP
                    PerilType.DINO_STEALTH -> DoodleSoundEffect.CLANG
                }
                ProceduralSoundEngine.play(gameOverSound)
            } else {
                // Play procedural 'pop' for balloons, 'crunch' for volcano crates, etc.
                val hitSound = when (activePerilType) {
                    PerilType.SHARK_BALLOONS -> DoodleSoundEffect.POP
                    PerilType.VOLCANO_BLOCKS -> DoodleSoundEffect.CRUNCH
                    PerilType.UFO_ABDUCTION -> DoodleSoundEffect.ZAP
                    PerilType.DINO_STEALTH -> DoodleSoundEffect.CLANG
                }
                ProceduralSoundEngine.play(hitSound)
            }
        }
    }

    private fun checkVictoryCondition() {
        val targetChars = activeLevel.word.uppercase().filter { it in 'A'..'Z' }.toSet()
        if (revealedChars.containsAll(targetChars)) {
            if (mistakesThisRound == 0) {
                perfectCount++
            }
            currentStreak++
            if (currentStreak > maxStreak) {
                maxStreak = currentStreak
            }
            chestProgress = (chestProgress % 4) + 1
            themeProgress = (themeProgress % 10) + 1
            roundOutcome = RoundOutcome.VICTORY
            ProceduralSoundEngine.play(DoodleSoundEffect.VICTORY)
        }
    }

    // Power-Up 1: Green Magnifying Glass (Cost: 150 coins) -> Reveals 1 random unguessed letter
    fun useRevealLetterHint() {
        if (roundOutcome != RoundOutcome.PLAYING) return
        val cost = 150
        val targetChars = activeLevel.word.uppercase().filter { it in 'A'..'Z' }.toSet()
        val unrevealed = targetChars.filter { !revealedChars.contains(it) }
        if (unrevealed.isEmpty()) return

        if (coins < cost) {
            statusBannerMessage = "Need $cost coins! Tap + to get coins."
            return
        }
        coins -= cost
        val picked = unrevealed.random()
        keyStatuses[picked] = KeyStatus.CORRECT
        revealedChars.add(picked)
        statusBannerMessage = "Magnifier revealed '$picked'!"
        checkVictoryCondition()
    }

    // Power-Up 2: Blue Shield/Potion (Badge count or Cost: 200 coins) -> Adds 1 extra life back
    fun useExtraBalloonPotion() {
        if (roundOutcome != RoundOutcome.PLAYING) return
        if (balloonsRemaining >= 6) {
            statusBannerMessage = "Already at max 6 lives!"
            return
        }
        val cost = 200
        if (freeShieldPotions > 0) {
            freeShieldPotions--
            balloonsRemaining++
            statusBannerMessage = "${activePerilType.extraLifeLabel} (Free potion)"
        } else if (coins >= cost) {
            coins -= cost
            balloonsRemaining++
            statusBannerMessage = activePerilType.extraLifeLabel
        } else {
            statusBannerMessage = "Need $cost coins for a Life Potion!"
        }
    }

    // Power-Up 3: Orange Lightbulb (Cost: 125 coins) -> Eliminates 3 wrong letters from keyboard
    fun useEliminateWrongLettersHint() {
        if (roundOutcome != RoundOutcome.PLAYING) return
        val cost = 125
        val targetChars = activeLevel.word.uppercase().toSet()
        val candidateWrongs = ('A'..'Z').filter { ch ->
            !targetChars.contains(ch) && (keyStatuses[ch] == null || keyStatuses[ch] == KeyStatus.DEFAULT)
        }
        if (candidateWrongs.isEmpty()) {
            statusBannerMessage = "No wrong letters left to clear!"
            return
        }
        if (coins < cost) {
            statusBannerMessage = "Need $cost coins! Tap + to get coins."
            return
        }
        coins -= cost
        // Prioritize defusing bomb letters first if present!
        val prioritized = candidateWrongs.sortedByDescending { bombLetters.contains(it) }
        val toEliminate = prioritized.take(3)
        toEliminate.forEach { ch ->
            keyStatuses[ch] = KeyStatus.WRONG
        }
        statusBannerMessage = "Safely cleared ${toEliminate.size} wrong letters!"
    }

    fun proceedAfterVictory() {
        val reward = if (activeLevel.isBossLevel) 50 else 25
        val chestBonus = if (chestProgress == 4) 100 else 0
        coins += reward + chestBonus

        if (activeLevel.isAdventure) {
            adventureLevelIndex++
            startAdventureLevel()
        } else {
            campaignLevelIndex++
            startCampaignLevel()
        }
    }

    fun retryCurrentLevel() {
        loadLevel(activeLevel)
    }

    fun rescueWithCoins() {
        val rescueCost = 250
        if (coins >= rescueCost) {
            coins -= rescueCost
            balloonsRemaining = 3
            roundOutcome = RoundOutcome.PLAYING
            statusBannerMessage = "Rescued with 3 lives!"
        } else {
            statusBannerMessage = "Need $rescueCost coins! Tap + at top right."
        }
    }

    fun claimFreeCoinBonus(amount: Int = 150) {
        coins += amount
        statusBannerMessage = "+$amount Doodle Coins added!"
    }

    fun toggleSoundEffects() {
        soundEffectsEnabled = !soundEffectsEnabled
        ProceduralSoundEngine.soundEnabled = soundEffectsEnabled
        if (soundEffectsEnabled) {
            ProceduralSoundEngine.play(DoodleSoundEffect.POP)
        }
    }

    fun toggleFontStyle() {
        useCursiveFont = !useCursiveFont
    }

    fun selectTheme(theme: PaperTheme) {
        selectedPaperTheme = theme
    }

    fun clearBannerMessage() {
        statusBannerMessage = null
    }
}

// ============================================================================
// 4. MAIN ACTIVITY ENTRY POINT
// ============================================================================

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ProceduralSoundEngine.init(applicationContext)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                WordSharkDoodleApp()
            }
        }
    }
}

@Composable
fun WordSharkDoodleApp(viewModel: WordGameViewModel = viewModel()) {
    val activeFontFamily = if (viewModel.useCursiveFont) {
        FontFamily.Cursive
    } else {
        FontFamily.SansSerif
    }

    LaunchedEffect(viewModel.statusBannerMessage) {
        if (viewModel.statusBannerMessage != null) {
            delay(2200)
            viewModel.clearBannerMessage()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Procedural Graph Paper Background Canvas
        GraphPaperBackground(
            theme = viewModel.selectedPaperTheme,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Main Foreground Content with Safe Drawing Insets
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            when (viewModel.currentScreen) {
                ScreenState.MAIN_MENU -> {
                    MainMenuScreen(
                        levelNumber = viewModel.currentCampaignLevelNumber,
                        coins = viewModel.coins,
                        fontFamily = activeFontFamily,
                        onPlayCampaign = { viewModel.startCampaignLevel() },
                        onPlayAdventure = { viewModel.startAdventureLevel() },
                        onPlayTwoPlayer = { viewModel.openTwoPlayerSetup() },
                        onSelectPerilLevel = { lvl -> viewModel.selectCampaignLevelNumber(lvl) },
                        onOpenSettings = { viewModel.showSettingsDialog = true },
                        onOpenCoinShop = { viewModel.showCoinShopDialog = true }
                    )
                }

                ScreenState.TWO_PLAYER_SETUP -> {
                    BackHandler { viewModel.navigateToMainMenu() }
                    TwoPlayerSetupScreen(
                        fontFamily = activeFontFamily,
                        onBack = { viewModel.navigateToMainMenu() },
                        onStartGame = { word, clue, perilIdx ->
                            viewModel.startTwoPlayerGame(word, clue, perilIdx)
                        }
                    )
                }

                ScreenState.GAMEPLAY -> {
                    BackHandler { viewModel.navigateToMainMenu() }
                    GameplayScreen(
                        viewModel = viewModel,
                        fontFamily = activeFontFamily
                    )
                }
            }

            // Floating Status Toast
            AnimatedVisibility(
                visible = viewModel.statusBannerMessage != null,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 66.dp)
            ) {
                SketchyContainer(
                    fillColor = DoodleColors.DoodleYellow,
                    cornerRadius = 20.dp,
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    Text(
                        text = viewModel.statusBannerMessage.orEmpty(),
                        fontFamily = activeFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = DoodleColors.InkBlack,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }

        // 3. Victory / Level Complete Full Overlay ("WELL DONE!")
        AnimatedVisibility(
            visible = viewModel.currentScreen == ScreenState.GAMEPLAY &&
                viewModel.roundOutcome == RoundOutcome.VICTORY,
            enter = fadeIn(tween(250)) + scaleIn(spring(dampingRatio = Spring.DampingRatioLowBouncy)),
            exit = fadeOut(tween(150))
        ) {
            VictoryOverlay(
                level = viewModel.activeLevel,
                perfectCount = viewModel.perfectCount,
                streak = viewModel.currentStreak,
                maxStreak = viewModel.maxStreak,
                chestProgress = viewModel.chestProgress,
                themeProgress = viewModel.themeProgress,
                fontFamily = activeFontFamily,
                onNextLevel = { viewModel.proceedAfterVictory() }
            )
        }

        // 4. Game Over Overlay (0 Lives Left)
        AnimatedVisibility(
            visible = viewModel.currentScreen == ScreenState.GAMEPLAY &&
                viewModel.roundOutcome == RoundOutcome.GAME_OVER,
            enter = fadeIn(tween(250)) + scaleIn(),
            exit = fadeOut(tween(150))
        ) {
            GameOverOverlay(
                level = viewModel.activeLevel,
                fontFamily = activeFontFamily,
                onRescue = { viewModel.rescueWithCoins() },
                onRetry = { viewModel.retryCurrentLevel() },
                onMainMenu = { viewModel.navigateToMainMenu() }
            )
        }

        // 5. Settings & Paper Theme Dialog
        if (viewModel.showSettingsDialog) {
            SettingsDoodleDialog(
                soundEnabled = viewModel.soundEffectsEnabled,
                useCursive = viewModel.useCursiveFont,
                selectedTheme = viewModel.selectedPaperTheme,
                fontFamily = activeFontFamily,
                onToggleSound = { viewModel.toggleSoundEffects() },
                onToggleFont = { viewModel.toggleFontStyle() },
                onSelectTheme = { viewModel.selectTheme(it) },
                onDismiss = { viewModel.showSettingsDialog = false }
            )
        }

        // 6. Free Coin / Ad Bonus Dialog
        if (viewModel.showCoinShopDialog) {
            CoinBonusDoodleDialog(
                coins = viewModel.coins,
                fontFamily = activeFontFamily,
                onClaimCoins = { amount ->
                    viewModel.claimFreeCoinBonus(amount)
                    viewModel.showCoinShopDialog = false
                },
                onDismiss = { viewModel.showCoinShopDialog = false }
            )
        }
    }
}

// ============================================================================
// 5. PROCEDURAL GRAPH PAPER BACKGROUND & SKETCHY UI CONTAINERS
// ============================================================================

@Composable
fun GraphPaperBackground(
    theme: PaperTheme,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.background(theme.bg)) {
        val step = 22.dp.toPx()
        val width = size.width
        val height = size.height

        var x = 0f
        var colIndex = 0
        while (x <= width) {
            val isMajor = colIndex % 5 == 0
            drawLine(
                color = if (isMajor) theme.gridMajor else theme.gridMinor,
                start = Offset(x, 0f),
                end = Offset(x, height),
                strokeWidth = if (isMajor) 1.5f else 1f
            )
            x += step
            colIndex++
        }

        var y = 0f
        var rowIndex = 0
        while (y <= height) {
            val isMajor = rowIndex % 5 == 0
            drawLine(
                color = if (isMajor) theme.gridMajor else theme.gridMinor,
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = if (isMajor) 1.5f else 1f
            )
            y += step
            rowIndex++
        }

        val marginX = step * 1.6f
        drawLine(
            color = DoodleColors.MarginRed.copy(alpha = 0.45f),
            start = Offset(marginX, 0f),
            end = Offset(marginX, height),
            strokeWidth = 2f
        )
    }
}

@Composable
fun SketchyContainer(
    modifier: Modifier = Modifier,
    fillColor: Color = Color.White,
    borderColor: Color = DoodleColors.InkBlack,
    cornerRadius: Dp = 18.dp,
    strokeWidth: Dp = 2.2.dp,
    shadowOffset: Dp = 3.dp,
    onClick: (() -> Unit)? = null,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable BoxScope.() -> Unit
) {
    val clickableModifier = if (onClick != null) {
        Modifier
            .clip(RoundedCornerShape(cornerRadius))
            .clickable(onClick = onClick)
    } else {
        Modifier
    }

    Box(
        contentAlignment = contentAlignment,
        modifier = modifier
            .then(clickableModifier)
            .padding(2.dp)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val r = cornerRadius.toPx()
            val sw = strokeWidth.toPx()
            val sh = shadowOffset.toPx()

            drawRoundRect(
                color = borderColor.copy(alpha = 0.18f),
                topLeft = Offset(sh, sh),
                size = Size(size.width - sh, size.height - sh),
                cornerRadius = CornerRadius(r, r),
                style = Fill
            )

            drawRoundRect(
                color = fillColor,
                topLeft = Offset(0f, 0f),
                size = Size(size.width - sh * 0.6f, size.height - sh * 0.6f),
                cornerRadius = CornerRadius(r, r),
                style = Fill
            )

            drawRoundRect(
                color = borderColor,
                topLeft = Offset(sw * 0.5f, sw * 0.5f),
                size = Size(size.width - sh * 0.6f - sw, size.height - sh * 0.6f - sw),
                cornerRadius = CornerRadius(r, r),
                style = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            drawRoundRect(
                color = borderColor.copy(alpha = 0.75f),
                topLeft = Offset(sw * 1.15f, sw * 0.2f),
                size = Size(size.width - sh * 0.6f - sw * 1.6f, size.height - sh * 0.6f - sw * 0.9f),
                cornerRadius = CornerRadius(r * 0.95f, r * 1.05f),
                style = Stroke(width = sw * 0.55f, cap = StrokeCap.Round)
            )
        }
        content()
    }
}

// ============================================================================
// 6. SCREEN 1: MAIN MENU SCREEN (WITH PERIL PREVIEW & LEVEL SELECTOR)
// ============================================================================

@Composable
fun MainMenuScreen(
    levelNumber: Int,
    coins: Int,
    fontFamily: FontFamily,
    onPlayCampaign: () -> Unit,
    onPlayAdventure: () -> Unit,
    onPlayTwoPlayer: () -> Unit,
    onSelectPerilLevel: (Int) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenCoinShop: () -> Unit
) {
    val upcomingPeril = PerilType.forLevel(levelNumber)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .widthIn(max = 600.dp)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SketchyIconButton(
                testTag = "settings_button",
                contentDescription = "Settings",
                onClick = onOpenSettings
            ) {
                DrawDoodleGearIcon()
            }

            CoinBalancePill(
                coins = coins,
                fontFamily = fontFamily,
                onClickAdd = onOpenCoinShop,
                testTag = "menu_coin_pill"
            )
        }

        // Center Hero: Animated Peril Scene + Peril Selector Chips + "W O R D" title
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                DynamicPerilSceneCanvas(
                    perilType = upcomingPeril,
                    livesRemaining = 6,
                    lastPoppedIndex = -1,
                    popTrigger = 0,
                    lastDamageAmount = 1,
                    isGameOver = false,
                    showSpeechBubble = false,
                    isMenuPreview = true,
                    fontFamily = fontFamily,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Quick Peril Preview / Level Jump Row (Lets user see or jump to any of the 4 perils + Boss Level 5)
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                val quickPickLevels = listOf(
                    1 to "1:Shark",
                    2 to "2:UFO",
                    3 to "3:Lava",
                    4 to "4:Dino",
                    5 to "5:Boss💣"
                )
                quickPickLevels.forEach { (lvl, label) ->
                    val isCurrent = levelNumber == lvl
                    SketchyContainer(
                        fillColor = if (isCurrent) DoodleColors.DoodleYellow else Color.White,
                        cornerRadius = 12.dp,
                        strokeWidth = 1.5.dp,
                        shadowOffset = 1.5.dp,
                        onClick = { onSelectPerilLevel(lvl) },
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("quick_level_$lvl")
                    ) {
                        Text(
                            text = label,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            color = DoodleColors.InkBlack,
                            modifier = Modifier.padding(horizontal = 7.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            DoodleWordTitleHeader(fontFamily = fontFamily)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Bottom Stacked Buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 360.dp)
                .padding(bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Level X (Yellow fill, primary action)
            SketchyContainer(
                fillColor = DoodleColors.DoodleYellow,
                cornerRadius = 28.dp,
                onClick = onPlayCampaign,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .testTag("play_level_button")
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (levelNumber % 5 == 0) "Level $levelNumber (BOSS 💣)" else "Level $levelNumber",
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp,
                        color = DoodleColors.InkBlack
                    )
                    Text(
                        text = upcomingPeril.displayName,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = DoodleColors.InkBlack.copy(alpha = 0.75f)
                    )
                }
            }

            // 2. Adventure (White fill with red devil/horn doodle icon on top-left corner)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                SketchyContainer(
                    fillColor = Color.White,
                    cornerRadius = 28.dp,
                    onClick = onPlayAdventure,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .align(Alignment.BottomCenter)
                        .testTag("adventure_button")
                ) {
                    Text(
                        text = "Adventure",
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 21.sp,
                        color = DoodleColors.InkBlack
                    )
                }

                Canvas(
                    modifier = Modifier
                        .size(38.dp)
                        .align(Alignment.TopStart)
                        .offset(x = 12.dp, y = (-2).dp)
                ) {
                    drawDevilHornsBadge()
                }
            }

            // 3. Two Player (White fill)
            SketchyContainer(
                fillColor = Color.White,
                cornerRadius = 28.dp,
                onClick = onPlayTwoPlayer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("two_player_button")
            ) {
                Text(
                    text = "Two Player",
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 21.sp,
                    color = DoodleColors.InkBlack
                )
            }
        }
    }
}

@Composable
fun DoodleWordTitleHeader(fontFamily: FontFamily) {
    val letters = listOf('W', 'O', 'R', 'D')
    val underlineColors = listOf(
        Color(0xFFFF595E),
        Color(0xFF2ECC71),
        Color(0xFF2196F3),
        Color(0xFFFF9800)
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(18.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        letters.forEachIndexed { index, letter ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = letter.toString(),
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 40.sp,
                    color = DoodleColors.InkBlack
                )
                Canvas(
                    modifier = Modifier
                        .width(40.dp)
                        .height(9.dp)
                ) {
                    val path = Path().apply {
                        moveTo(2f, size.height * 0.55f)
                        quadraticTo(
                            size.width * 0.5f,
                            size.height * (if (index % 2 == 0) 0.2f else 0.9f),
                            size.width - 2f,
                            size.height * 0.5f
                        )
                    }
                    drawPath(
                        path = path,
                        color = underlineColors[index],
                        style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawPath(
                        path = path,
                        color = DoodleColors.InkBlack,
                        style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }
        }
    }
}

// ============================================================================
// 7. SCREEN 2: GAMEPLAY SCREEN (DYNAMIC PERILS + BOSS BOMB MODIFIER)
// ============================================================================

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GameplayScreen(
    viewModel: WordGameViewModel,
    fontFamily: FontFamily
) {
    val level = viewModel.activeLevel
    val perilType = viewModel.activePerilType
    val targetWord = level.word.uppercase()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .widthIn(max = 650.dp)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. Top Bar: Back arrow button, "Level X" + Peril/Boss subtitle, and Coin balance pill
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SketchyIconButton(
                testTag = "back_button",
                contentDescription = "Back to main menu",
                onClick = { viewModel.navigateToMainMenu() }
            ) {
                DrawDoodleBackArrow()
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val headerTitle = when {
                    level.isAdventure -> "Adventure ${level.number}"
                    else -> "Level ${level.number}"
                }
                Text(
                    text = headerTitle,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    color = DoodleColors.InkBlack,
                    modifier = Modifier.testTag("level_header_text")
                )
                Text(
                    text = if (level.isBossLevel) {
                        "${perilType.displayName} • 💣 BOSS (-2 ❤️)"
                    } else {
                        perilType.displayName
                    },
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp,
                    color = if (level.isBossLevel) DoodleColors.WrongRed else DoodleColors.InkBlue,
                    modifier = Modifier.testTag("peril_subtitle_text")
                )
            }

            CoinBalancePill(
                coins = viewModel.coins,
                fontFamily = fontFamily,
                onClickAdd = { viewModel.showCoinShopDialog = true },
                testTag = "game_coin_pill"
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 2. Upper Split Scene:
        // Left Half: Custom Compose Canvas rendering the active PerilType (Shark, UFO, Volcano, or Dino)
        // Right Half: The Clue/Category prompt + Boss warning badge centered vertically
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Half: Dynamic Peril Canvas
            Box(
                modifier = Modifier
                    .weight(0.53f)
                    .fillMaxHeight()
                    .padding(end = 6.dp)
            ) {
                DynamicPerilSceneCanvas(
                    perilType = perilType,
                    livesRemaining = viewModel.balloonsRemaining,
                    lastPoppedIndex = viewModel.lastPoppedBalloonIndex,
                    popTrigger = viewModel.popAnimationTrigger,
                    lastDamageAmount = viewModel.lastDamageAmount,
                    isGameOver = viewModel.roundOutcome == RoundOutcome.GAME_OVER,
                    showSpeechBubble = viewModel.balloonsRemaining in 1..2,
                    isMenuPreview = false,
                    fontFamily = fontFamily,
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("peril_scene_canvas")
                )
            }

            // Right Half: Clue / Category prompt + Lives + Boss Bomb indicator
            Box(
                modifier = Modifier
                    .weight(0.47f)
                    .fillMaxHeight()
                    .padding(start = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    // 6 Lives status dots
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        repeat(6) { idx ->
                            val alive = idx < viewModel.balloonsRemaining
                            Canvas(
                                modifier = Modifier
                                    .size(14.dp)
                                    .padding(horizontal = 1.5.dp)
                            ) {
                                drawCircle(
                                    color = if (alive) DoodleColors.BalloonPalette[idx] else Color.LightGray.copy(alpha = 0.4f),
                                    radius = size.minDimension * 0.45f
                                )
                                drawCircle(
                                    color = DoodleColors.InkBlack,
                                    radius = size.minDimension * 0.45f,
                                    style = Stroke(width = 1.3.dp.toPx())
                                )
                            }
                        }
                    }

                    if (level.isBossLevel) {
                        SketchyContainer(
                            fillColor = Color(0xFFFFEBEE),
                            borderColor = DoodleColors.WrongRed,
                            cornerRadius = 12.dp,
                            strokeWidth = 1.6.dp,
                            shadowOffset = 1.5.dp,
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                                .testTag("boss_warning_badge")
                        ) {
                            Text(
                                text = "💣 BOSS: Red Dot Keys = -2 ❤️",
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                color = DoodleColors.WrongRed,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Text(
                        text = level.clue,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        lineHeight = 25.sp,
                        color = DoodleColors.InkBlack,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.testTag("clue_text")
                    )
                }
            }
        }

        // 3. Middle Area (Word Slots)
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            targetWord.forEachIndexed { index, ch ->
                val isRevealed = viewModel.revealedChars.contains(ch)
                WordLetterSlot(
                    letter = ch,
                    isRevealed = isRevealed,
                    slotIndex = index,
                    fontFamily = fontFamily
                )
            }
        }

        // 4. Keyboard Area: Custom 4-row alphabetical grid (with red bomb dots on Boss Levels!)
        DoodleAlphabetKeyboard(
            keyStatuses = viewModel.keyStatuses,
            bombLetters = viewModel.bombLetters.toSet(),
            fontFamily = fontFamily,
            onKeyPress = { ch -> viewModel.onLetterTapped(ch) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 5. Bottom Power-Up Bar (3 Circular Sketch Buttons)
        BottomPowerUpBar(
            freePotions = viewModel.freeShieldPotions,
            fontFamily = fontFamily,
            onRevealLetter = { viewModel.useRevealLetterHint() },
            onExtraBalloon = { viewModel.useExtraBalloonPotion() },
            onEliminateThree = { viewModel.useEliminateWrongLettersHint() }
        )
    }
}

@Composable
fun WordLetterSlot(
    letter: Char,
    isRevealed: Boolean,
    slotIndex: Int,
    fontFamily: FontFamily
) {
    val scale by animateFloatAsState(
        targetValue = if (isRevealed) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "letterRevealScale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .width(34.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.height(36.dp)
        ) {
            if (isRevealed) {
                Text(
                    text = letter.toString(),
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 27.sp,
                    color = if (slotIndex % 2 == 0) DoodleColors.CorrectGreen else DoodleColors.InkBlue,
                    modifier = Modifier.scale(scale)
                )
            }
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        ) {
            val wobble = if (slotIndex % 2 == 0) 1.5f else -1.5f
            val path = Path().apply {
                moveTo(2f, size.height * 0.5f)
                quadraticTo(
                    size.width * 0.5f,
                    size.height * 0.5f + wobble,
                    size.width - 2f,
                    size.height * 0.5f
                )
            }
            drawPath(
                path = path,
                color = DoodleColors.InkBlack,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}

// ============================================================================
// 8. CUSTOM 4-ROW ALPHABETICAL KEYBOARD (WITH BOSS LEVEL BOMB DOTS)
// ============================================================================

@Composable
fun DoodleAlphabetKeyboard(
    keyStatuses: Map<Char, KeyStatus>,
    bombLetters: Set<Char>,
    fontFamily: FontFamily,
    onKeyPress: (Char) -> Unit
) {
    val rows = listOf(
        listOf('A', 'B', 'C', 'D', 'E', 'F', 'G'),
        listOf('H', 'I', 'J', 'K', 'L', 'M', 'N'),
        listOf('O', 'P', 'Q', 'R', 'S', 'T', 'U'),
        listOf('V', 'W', 'X', 'Y', 'Z')
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        rows.forEach { rowChars ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                rowChars.forEach { ch ->
                    val status = keyStatuses[ch] ?: KeyStatus.DEFAULT
                    val isBomb = bombLetters.contains(ch)
                    DoodleLetterKey(
                        letter = ch,
                        status = status,
                        hasBombDot = isBomb,
                        fontFamily = fontFamily,
                        onClick = { onKeyPress(ch) },
                        modifier = Modifier.weight(1f, fill = false)
                    )
                }
            }
        }
    }
}

@Composable
fun DoodleLetterKey(
    letter: Char,
    status: KeyStatus,
    hasBombDot: Boolean,
    fontFamily: FontFamily,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val markScale by animateFloatAsState(
        targetValue = if (status == KeyStatus.DEFAULT) 0f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "keyMarkScale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable(
                enabled = status == KeyStatus.DEFAULT,
                onClick = onClick
            )
            .testTag("key_$letter")
            .semantics {
                contentDescription = if (hasBombDot) "Letter $letter Bomb Key" else "Letter $letter"
            }
    ) {
        // Letter Text
        Text(
            text = letter.toString(),
            fontFamily = fontFamily,
            fontWeight = FontWeight.Black,
            fontSize = 25.sp,
            color = when (status) {
                KeyStatus.CORRECT -> DoodleColors.CorrectGreen
                KeyStatus.WRONG -> DoodleColors.InkBlack.copy(alpha = 0.55f)
                KeyStatus.DEFAULT -> DoodleColors.InkBlack
            }
        )

        // Boss Level: Small red bomb dot with a tiny sizzling fuse at the top-right corner of the key
        if (hasBombDot) {
            Canvas(
                modifier = Modifier
                    .size(14.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = (-3).dp, y = 3.dp)
                    .testTag("bomb_dot_$letter")
            ) {
                val r = size.minDimension * 0.36f
                val c = Offset(size.width * 0.45f, size.height * 0.58f)
                // Fuse line
                drawLine(
                    color = DoodleColors.InkBlack,
                    start = Offset(c.x + r * 0.5f, c.y - r * 0.5f),
                    end = Offset(size.width * 0.88f, size.height * 0.14f),
                    strokeWidth = 1.5.dp.toPx(),
                    cap = StrokeCap.Round
                )
                // Spark tip
                drawCircle(
                    color = DoodleColors.DoodleYellow,
                    radius = 2.2.dp.toPx(),
                    center = Offset(size.width * 0.88f, size.height * 0.14f)
                )
                // Red bomb dot
                drawCircle(
                    color = DoodleColors.WrongRed,
                    radius = r,
                    center = c
                )
                drawCircle(
                    color = DoodleColors.InkBlack,
                    radius = r,
                    center = c,
                    style = Stroke(width = 1.3.dp.toPx())
                )
            }
        }

        // Hand-drawn Green Circle or Red Cross overlay
        if (status != KeyStatus.DEFAULT) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
                    .scale(markScale)
            ) {
                when (status) {
                    KeyStatus.CORRECT -> drawHandDrawnGreenCircle()
                    KeyStatus.WRONG -> drawHandDrawnRedCross()
                    KeyStatus.DEFAULT -> Unit
                }
            }
        }
    }
}

private fun DrawScope.drawHandDrawnGreenCircle() {
    val w = size.width
    val h = size.height
    val path = Path().apply {
        moveTo(w * 0.18f, h * 0.25f)
        cubicTo(w * 0.45f, h * 0.02f, w * 0.92f, h * 0.12f, w * 0.92f, h * 0.52f)
        cubicTo(w * 0.92f, h * 0.90f, w * 0.45f, h * 0.98f, w * 0.14f, h * 0.78f)
        cubicTo(w * 0.01f, h * 0.60f, w * 0.05f, h * 0.20f, w * 0.28f, h * 0.16f)
    }
    drawPath(
        path = path,
        color = DoodleColors.CorrectGreen,
        style = Stroke(width = 3.2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
}

private fun DrawScope.drawHandDrawnRedCross() {
    val w = size.width
    val h = size.height
    val stroke1 = Path().apply {
        moveTo(w * 0.16f, h * 0.18f)
        quadraticTo(w * 0.52f, h * 0.48f, w * 0.84f, h * 0.84f)
    }
    val stroke2 = Path().apply {
        moveTo(w * 0.84f, h * 0.16f)
        quadraticTo(w * 0.48f, h * 0.52f, w * 0.16f, h * 0.84f)
    }
    drawPath(
        path = stroke1,
        color = DoodleColors.WrongRed,
        style = Stroke(width = 3.6.dp.toPx(), cap = StrokeCap.Round)
    )
    drawPath(
        path = stroke2,
        color = DoodleColors.WrongRed,
        style = Stroke(width = 3.6.dp.toPx(), cap = StrokeCap.Round)
    )
}

// ============================================================================
// 9. BOTTOM POWER-UP BAR (3 CIRCULAR SKETCH BUTTONS)
// ============================================================================

@Composable
fun BottomPowerUpBar(
    freePotions: Int,
    fontFamily: FontFamily,
    onRevealLetter: () -> Unit,
    onExtraBalloon: () -> Unit,
    onEliminateThree: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        PowerUpCircularButton(
            badgeText = "150",
            accentColor = DoodleColors.HintMagnifierGreen,
            fontFamily = fontFamily,
            testTag = "hint_reveal_button",
            contentDescription = "Reveal 1 letter for 150 coins",
            onClick = onRevealLetter
        ) {
            DrawDoodleMagnifierIcon()
        }

        PowerUpCircularButton(
            badgeText = if (freePotions > 0) "$freePotions" else "200",
            isCountBadge = freePotions > 0,
            accentColor = DoodleColors.HintPotionBlue,
            fontFamily = fontFamily,
            testTag = "hint_balloon_button",
            contentDescription = "Add 1 extra life",
            onClick = onExtraBalloon
        ) {
            DrawDoodlePotionShieldIcon()
        }

        PowerUpCircularButton(
            badgeText = "125",
            accentColor = DoodleColors.HintBulbOrange,
            fontFamily = fontFamily,
            testTag = "hint_eliminate_button",
            contentDescription = "Eliminate 3 wrong letters for 125 coins",
            onClick = onEliminateThree
        ) {
            DrawDoodleLightbulbIcon()
        }
    }
}

@Composable
fun PowerUpCircularButton(
    badgeText: String,
    accentColor: Color,
    fontFamily: FontFamily,
    testTag: String,
    contentDescription: String,
    isCountBadge: Boolean = false,
    onClick: () -> Unit,
    iconContent: @Composable () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(width = 86.dp, height = 72.dp)
            .testTag(testTag)
            .semantics { this.contentDescription = contentDescription }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(60.dp)
                .align(Alignment.TopCenter)
                .clip(CircleShape)
                .clickable(onClick = onClick)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val r = size.minDimension * 0.44f
                drawCircle(
                    color = DoodleColors.InkBlack.copy(alpha = 0.16f),
                    radius = r,
                    center = Offset(center.x + 2.dp.toPx(), center.y + 3.dp.toPx())
                )
                drawCircle(color = Color.White, radius = r)
                drawCircle(color = accentColor.copy(alpha = 0.18f), radius = r * 0.84f)
                drawCircle(
                    color = DoodleColors.InkBlack,
                    radius = r,
                    style = Stroke(width = 2.4.dp.toPx())
                )
                drawCircle(
                    color = DoodleColors.InkBlack.copy(alpha = 0.65f),
                    radius = r * 0.96f,
                    center = Offset(center.x + 1f, center.y - 1f),
                    style = Stroke(width = 1.1.dp.toPx())
                )
            }
            iconContent()
        }

        SketchyContainer(
            fillColor = if (isCountBadge) accentColor else DoodleColors.DoodleYellow,
            cornerRadius = 10.dp,
            strokeWidth = 1.6.dp,
            shadowOffset = 1.5.dp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .height(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                if (!isCountBadge) {
                    Canvas(modifier = Modifier.size(11.dp)) {
                        drawCircle(color = Color(0xFFF39C12))
                        drawCircle(
                            color = DoodleColors.InkBlack,
                            style = Stroke(width = 1.2.dp.toPx())
                        )
                    }
                    Spacer(modifier = Modifier.width(3.dp))
                }
                Text(
                    text = badgeText,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    color = if (isCountBadge) Color.White else DoodleColors.InkBlack
                )
            }
        }
    }
}

// ============================================================================
// 10. DYNAMIC PERIL SCENE CANVAS (ALL 4 HAZARDS PROCEDURALLY DRAWN)
// ============================================================================

@Composable
fun DynamicPerilSceneCanvas(
    perilType: PerilType,
    livesRemaining: Int,
    lastPoppedIndex: Int,
    popTrigger: Int,
    lastDamageAmount: Int,
    isGameOver: Boolean,
    showSpeechBubble: Boolean,
    isMenuPreview: Boolean,
    fontFamily: FontFamily,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "perilInfinite")

    val bobOffset by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bobOffset"
    )

    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase"
    )

    val patrolPhase by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "patrolPhase"
    )

    // Smooth 0f..1f hazard progress as lives drop from 6 -> 0
    val dangerProgress by animateFloatAsState(
        targetValue = when {
            isGameOver || livesRemaining <= 0 -> 1.0f
            else -> ((6 - livesRemaining).coerceIn(0, 6)) / 6.0f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "dangerProgress"
    )

    val popProgress = remember { Animatable(1f) }
    LaunchedEffect(popTrigger) {
        if (popTrigger > 0) {
            popProgress.snapTo(0f)
            popProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 950, easing = FastOutSlowInEasing)
            )
        }
    }

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            when (perilType) {
                PerilType.SHARK_BALLOONS -> drawSharkBalloonsPeril(
                    livesRemaining = livesRemaining,
                    lastPoppedIndex = lastPoppedIndex,
                    popTrigger = popTrigger,
                    popProgress = popProgress.value,
                    dangerProgress = dangerProgress,
                    bobOffset = bobOffset,
                    wavePhase = wavePhase,
                    sharkPatrol = patrolPhase,
                    isMenuPreview = isMenuPreview,
                    isGameOver = isGameOver
                )

                PerilType.UFO_ABDUCTION -> drawUfoAbductionPeril(
                    livesRemaining = livesRemaining,
                    popTrigger = popTrigger,
                    popProgress = popProgress.value,
                    dangerProgress = dangerProgress,
                    bobOffset = bobOffset,
                    wavePhase = wavePhase,
                    ufoSway = patrolPhase,
                    isGameOver = isGameOver
                )

                PerilType.VOLCANO_BLOCKS -> drawVolcanoBlocksPeril(
                    livesRemaining = livesRemaining,
                    popTrigger = popTrigger,
                    popProgress = popProgress.value,
                    bobOffset = bobOffset,
                    wavePhase = wavePhase,
                    isGameOver = isGameOver
                )

                PerilType.DINO_STEALTH -> drawDinoStealthPeril(
                    livesRemaining = livesRemaining,
                    popTrigger = popTrigger,
                    popProgress = popProgress.value,
                    bobOffset = bobOffset,
                    wavePhase = wavePhase,
                    isGameOver = isGameOver
                )
            }
        }

        // Floating "-1 ❤️" or "-2 ❤️ 💥" + Peril Hit Effect (POP! / ZAP! / CRUMBLE! / CLANG!)
        if (popTrigger > 0 && popProgress.value < 0.95f) {
            val p = popProgress.value
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (36 - (p * 42)).dp)
                    .scale(1f + (1f - p) * 0.25f)
            ) {
                Text(
                    text = if (lastDamageAmount >= 2) "-2 ❤️ 💥" else "-1 ❤️",
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 21.sp,
                    color = DoodleColors.WrongRed.copy(alpha = (1f - p).coerceIn(0f, 1f))
                )
                Text(
                    text = perilType.hitEffectLabel,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = DoodleColors.InkBlack.copy(alpha = (1f - p).coerceIn(0f, 1f))
                )
            }
        }

        // Contextual Hazard-Specific Speech Bubble when lives <= 2
        AnimatedVisibility(
            visible = showSpeechBubble && !isGameOver,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier
                .align(
                    if (perilType == PerilType.UFO_ABDUCTION) Alignment.BottomCenter else Alignment.TopCenter
                )
                .padding(vertical = 4.dp, horizontal = 4.dp)
                .testTag("panic_speech_bubble")
        ) {
            SketchySpeechBubble(
                text = perilType.panicBubbleText,
                fontFamily = fontFamily
            )
        }
    }
}

@Composable
fun SketchySpeechBubble(
    text: String,
    fontFamily: FontFamily
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        SketchyContainer(
            fillColor = Color.White,
            cornerRadius = 14.dp,
            strokeWidth = 1.8.dp,
            shadowOffset = 2.dp
        ) {
            Text(
                text = text,
                fontFamily = fontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp,
                lineHeight = 14.sp,
                color = DoodleColors.InkBlack,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
            )
        }
    }
}

// ----------------------------------------------------------------------------
// PERIL 1: SHARK_BALLOONS (Levels 1, 5, 9...)
// ----------------------------------------------------------------------------
private fun DrawScope.drawSharkBalloonsPeril(
    livesRemaining: Int,
    lastPoppedIndex: Int,
    popTrigger: Int,
    popProgress: Float,
    dangerProgress: Float,
    bobOffset: Float,
    wavePhase: Float,
    sharkPatrol: Float,
    isMenuPreview: Boolean,
    isGameOver: Boolean
) {
    val w = size.width
    val h = size.height
    val centerX = w * 0.5f
    val waterY = h * 0.80f

    val topSafeY = h * 0.34f
    val dangerLowY = h * 0.62f
    val splashedY = h * 0.82f

    val baseHarnessY = if (livesRemaining <= 0 || isGameOver) {
        splashedY
    } else {
        topSafeY + (dangerLowY - topSafeY) * (dangerProgress * 1.12f)
    }
    val harnessY = baseHarnessY + bobOffset

    val balloonOffsets = listOf(
        Offset(-w * 0.24f, -h * 0.20f),
        Offset(-w * 0.14f, -h * 0.27f),
        Offset(-w * 0.04f, -h * 0.30f),
        Offset(w * 0.06f, -h * 0.29f),
        Offset(w * 0.16f, -h * 0.25f),
        Offset(w * 0.24f, -h * 0.18f)
    )

    val handPoint = Offset(centerX, harnessY)
    for (i in 0 until 6) {
        val bCenter = Offset(
            x = centerX + balloonOffsets[i].x + sin(wavePhase + i) * 3.dp.toPx(),
            y = harnessY + balloonOffsets[i].y + cos(wavePhase + i) * 2.dp.toPx()
        )
        val balloonRadiusX = (w * 0.085f).coerceIn(13.dp.toPx(), 24.dp.toPx())
        val balloonRadiusY = balloonRadiusX * 1.18f

        if (i < livesRemaining) {
            val stringPath = Path().apply {
                moveTo(handPoint.x, handPoint.y)
                quadraticTo(
                    (handPoint.x + bCenter.x) * 0.5f,
                    (handPoint.y + bCenter.y) * 0.5f + 8.dp.toPx(),
                    bCenter.x,
                    bCenter.y + balloonRadiusY
                )
            }
            drawPath(
                path = stringPath,
                color = DoodleColors.InkBlack,
                style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
            )
            drawDoodleBalloon(
                center = bCenter,
                rx = balloonRadiusX,
                ry = balloonRadiusY,
                color = DoodleColors.BalloonPalette[i]
            )
        } else if (i == lastPoppedIndex && popTrigger > 0 && popProgress < 0.95f) {
            drawPoppingBalloonBurst(
                center = bCenter,
                progress = popProgress,
                color = DoodleColors.BalloonPalette[i]
            )
        }
    }

    drawDoodleStickman(
        handAnchor = handPoint,
        scale = (w / 420f).coerceIn(0.68f, 1.12f),
        isPanicking = livesRemaining in 1..2,
        isSplashed = livesRemaining <= 0 || isGameOver,
        wavePhase = wavePhase,
        handsUp = true
    )

    val isJawsOpen = (!isMenuPreview && livesRemaining <= 2)
    if (isJawsOpen) {
        drawOpenSharkJaws(
            centerX = centerX + sharkPatrol * (w * 0.04f),
            waterY = waterY,
            scale = (w / 380f).coerceIn(0.75f, 1.2f)
        )
    } else {
        val sharkX = centerX + sharkPatrol * (w * 0.24f)
        val movingRight = cos(wavePhase) >= 0f
        drawCirclingSharkFin(
            x = sharkX,
            waterY = waterY,
            facingRight = sharkPatrol < 0f || movingRight,
            scale = (w / 380f).coerceIn(0.75f, 1.2f)
        )
    }

    drawDoodleWaterWaves(waterY = waterY, wavePhase = wavePhase)
}

// ----------------------------------------------------------------------------
// PERIL 2: UFO_ABDUCTION (Levels 2, 6, 10...)
// Stickman on ground pulled upward step-by-step into UFO tractor beam at top
// ----------------------------------------------------------------------------
private fun DrawScope.drawUfoAbductionPeril(
    livesRemaining: Int,
    popTrigger: Int,
    popProgress: Float,
    dangerProgress: Float,
    bobOffset: Float,
    wavePhase: Float,
    ufoSway: Float,
    isGameOver: Boolean
) {
    val w = size.width
    val h = size.height
    val centerX = w * 0.5f + ufoSway * (w * 0.04f)
    val ufoY = h * 0.17f
    val groundY = h * 0.88f

    // 1. Tractor Beam Cone from UFO down to ground
    val beamTopHalfW = w * 0.14f
    val beamBottomHalfW = w * 0.38f
    val beamPath = Path().apply {
        moveTo(centerX - beamTopHalfW, ufoY + 10.dp.toPx())
        lineTo(centerX + beamTopHalfW, ufoY + 10.dp.toPx())
        lineTo(centerX + beamBottomHalfW, groundY)
        lineTo(centerX - beamBottomHalfW, groundY)
        close()
    }
    val beamAlpha = if (popTrigger > 0 && popProgress < 0.5f) 0.40f else 0.22f
    drawPath(
        path = beamPath,
        color = DoodleColors.UfoBeamGreen.copy(alpha = beamAlpha)
    )
    // Dashed sketch edges on beam
    drawLine(
        color = DoodleColors.CorrectGreen.copy(alpha = 0.7f),
        start = Offset(centerX - beamTopHalfW, ufoY + 10.dp.toPx()),
        end = Offset(centerX - beamBottomHalfW, groundY),
        strokeWidth = 2.dp.toPx()
    )
    drawLine(
        color = DoodleColors.CorrectGreen.copy(alpha = 0.7f),
        start = Offset(centerX + beamTopHalfW, ufoY + 10.dp.toPx()),
        end = Offset(centerX + beamBottomHalfW, groundY),
        strokeWidth = 2.dp.toPx()
    )

    // 6 Tractor Beam Altitude Rings (representing the 6 steps to abduction)
    for (stepIdx in 0 until 6) {
        val frac = (stepIdx + 1) / 7f
        val ringY = groundY - (groundY - ufoY - 24.dp.toPx()) * frac
        val ringRx = beamTopHalfW + (beamBottomHalfW - beamTopHalfW) * (1f - frac) * 0.75f
        val isAbductedStep = stepIdx >= livesRemaining
        drawOval(
            color = if (isAbductedStep) DoodleColors.WrongRed.copy(alpha = 0.65f)
            else DoodleColors.CorrectGreen.copy(alpha = 0.55f),
            topLeft = Offset(centerX - ringRx, ringY - 5.dp.toPx()),
            size = Size(ringRx * 2f, 10.dp.toPx()),
            style = Stroke(width = if (isAbductedStep) 2.4.dp.toPx() else 1.5.dp.toPx())
        )
    }

    // 2. Sketched Ground Grass Line at bottom
    drawLine(
        color = DoodleColors.CorrectGreen,
        start = Offset(w * 0.08f, groundY),
        end = Offset(w * 0.92f, groundY),
        strokeWidth = 4.dp.toPx(),
        cap = StrokeCap.Round
    )
    drawLine(
        color = DoodleColors.InkBlack,
        start = Offset(w * 0.08f, groundY),
        end = Offset(w * 0.92f, groundY),
        strokeWidth = 2.dp.toPx(),
        cap = StrokeCap.Round
    )

    // 3. Stickman position: starts on ground (dangerProgress = 0) and rises into UFO hatch (dangerProgress = 1)
    val startHandY = groundY - 76.dp.toPx()
    val abductedHandY = ufoY + 12.dp.toPx()
    val currentHandY = startHandY + (abductedHandY - startHandY) * dangerProgress + bobOffset

    if (!isGameOver && livesRemaining > 0) {
        drawDoodleStickman(
            handAnchor = Offset(centerX, currentHandY),
            scale = ((w / 420f) * (1f - dangerProgress * 0.25f)).coerceIn(0.55f, 1.1f),
            isPanicking = livesRemaining <= 2,
            isSplashed = false,
            wavePhase = wavePhase,
            handsUp = dangerProgress > 0.1f
        )
    }

    // 4. Draw Doodle UFO Flying Saucer at top
    val saucerRx = (w * 0.28f).coerceAtMost(64.dp.toPx())
    val saucerRy = 14.dp.toPx()
    val domeRx = saucerRx * 0.58f
    val domeRy = 18.dp.toPx()

    // Glass dome
    drawOval(
        color = DoodleColors.UfoDomeCyan,
        topLeft = Offset(centerX - domeRx, ufoY - domeRy * 1.3f),
        size = Size(domeRx * 2f, domeRy * 2f)
    )
    drawOval(
        color = DoodleColors.InkBlack,
        topLeft = Offset(centerX - domeRx, ufoY - domeRy * 1.3f),
        size = Size(domeRx * 2f, domeRy * 2f),
        style = Stroke(width = 2.2.dp.toPx())
    )
    // Little green alien silhouette inside dome
    drawCircle(
        color = DoodleColors.CorrectGreen,
        radius = 6.dp.toPx(),
        center = Offset(centerX, ufoY - domeRy * 0.55f)
    )
    drawCircle(
        color = DoodleColors.InkBlack,
        radius = 6.dp.toPx(),
        center = Offset(centerX, ufoY - domeRy * 0.55f),
        style = Stroke(width = 1.5.dp.toPx())
    )

    // Metallic Saucer Disc
    drawOval(
        color = DoodleColors.UfoSilver,
        topLeft = Offset(centerX - saucerRx, ufoY - saucerRy),
        size = Size(saucerRx * 2f, saucerRy * 2f)
    )
    drawOval(
        color = DoodleColors.InkBlack,
        topLeft = Offset(centerX - saucerRx, ufoY - saucerRy),
        size = Size(saucerRx * 2f, saucerRy * 2f),
        style = Stroke(width = 2.4.dp.toPx())
    )

    // Glowing UFO lights along saucer rim
    for (i in -2..2) {
        val lx = centerX + i * (saucerRx * 0.34f)
        drawCircle(
            color = if ((i + (wavePhase * 2).toInt()) % 2 == 0) DoodleColors.DoodleYellow else DoodleColors.WrongRed,
            radius = 3.5.dp.toPx(),
            center = Offset(lx, ufoY)
        )
        drawCircle(
            color = DoodleColors.InkBlack,
            radius = 3.5.dp.toPx(),
            center = Offset(lx, ufoY),
            style = Stroke(width = 1.2.dp.toPx())
        )
    }
}

// ----------------------------------------------------------------------------
// PERIL 3: VOLCANO_BLOCKS (Levels 3, 7, 11...)
// Stickman standing on a stack of 6 sketched stone crates over bubbling lava
// ----------------------------------------------------------------------------
private fun DrawScope.drawVolcanoBlocksPeril(
    livesRemaining: Int,
    popTrigger: Int,
    popProgress: Float,
    bobOffset: Float,
    wavePhase: Float,
    isGameOver: Boolean
) {
    val w = size.width
    val h = size.height
    val centerX = w * 0.5f
    val lavaY = h * 0.82f

    val crateW = (w * 0.34f).coerceIn(46.dp.toPx(), 78.dp.toPx())
    val crateH = ((h * 0.44f) / 6f).coerceIn(14.dp.toPx(), 24.dp.toPx())

    // 1. Draw Stack of Remaining Stone Crates above lavaY
    val cratesCount = livesRemaining.coerceIn(0, 6)
    for (i in 0 until cratesCount) {
        val topY = lavaY - (i + 1) * crateH
        val wobbleX = if (livesRemaining <= 2) sin(wavePhase * 3f + i) * 2.5.dp.toPx() else 0f
        val leftX = centerX - crateW * 0.5f + wobbleX

        // Stone crate fill
        drawRoundRect(
            color = if (i % 2 == 0) DoodleColors.CrateStone else DoodleColors.CrateStoneDark,
            topLeft = Offset(leftX, topY),
            size = Size(crateW, crateH - 1.5.dp.toPx()),
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
        )
        // Sketchy black outline
        drawRoundRect(
            color = DoodleColors.InkBlack,
            topLeft = Offset(leftX, topY),
            size = Size(crateW, crateH - 1.5.dp.toPx()),
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
            style = Stroke(width = 2.dp.toPx())
        )
        // Diagonal stone brace / crack detail
        drawLine(
            color = DoodleColors.InkBlack.copy(alpha = 0.45f),
            start = Offset(leftX + 6.dp.toPx(), topY + 3.dp.toPx()),
            end = Offset(leftX + crateW - 6.dp.toPx(), topY + crateH - 5.dp.toPx()),
            strokeWidth = 1.4.dp.toPx(),
            cap = StrokeCap.Round
        )
    }

    // Crumbling rock debris when a crate breaks
    if (popTrigger > 0 && popProgress < 0.9f) {
        val crumbleY = lavaY - cratesCount * crateH
        for (r in 0 until 6) {
            val angle = (r * PI / 3.0).toFloat()
            val rx = centerX + cos(angle) * (16.dp.toPx() + popProgress * 28.dp.toPx())
            val ry = crumbleY + sin(angle) * 12.dp.toPx() + popProgress * 26.dp.toPx()
            drawCircle(
                color = DoodleColors.CrateStoneDark.copy(alpha = (1f - popProgress).coerceIn(0f, 1f)),
                radius = 4.dp.toPx(),
                center = Offset(rx, ry)
            )
        }
    }

    // 2. Draw Stickman standing on top of the highest crate
    val platformTopY = lavaY - cratesCount * crateH
    val stickmanScale = (w / 440f).coerceIn(0.65f, 1.05f)
    val feetHeight = 72.dp.toPx() * stickmanScale
    val handY = if (isGameOver || livesRemaining <= 0) {
        lavaY - 18.dp.toPx()
    } else {
        platformTopY - feetHeight + bobOffset * 0.4f
    }

    drawDoodleStickman(
        handAnchor = Offset(centerX, handY),
        scale = stickmanScale,
        isPanicking = livesRemaining in 1..2,
        isSplashed = livesRemaining <= 0 || isGameOver,
        wavePhase = wavePhase,
        handsUp = false
    )

    // 3. Draw Bubbling Red/Orange Lava at bottom
    val lavaPath = Path().apply {
        moveTo(0f, h)
        lineTo(0f, lavaY)
        var x = 0f
        val step = 16.dp.toPx()
        while (x <= w + step) {
            val ly = lavaY + sin(wavePhase * 1.4f + x * 0.05f) * 5.dp.toPx()
            lineTo(x, ly)
            x += step
        }
        lineTo(w, h)
        close()
    }
    drawPath(path = lavaPath, color = DoodleColors.LavaOrange.copy(alpha = 0.85f))

    // Lava bubbles
    for (b in 1..4) {
        val bx = w * (0.18f * b) + sin(wavePhase + b) * 6.dp.toPx()
        val by = lavaY + 8.dp.toPx() + cos(wavePhase * 2f + b) * 4.dp.toPx()
        drawCircle(color = DoodleColors.LavaYellow, radius = 5.dp.toPx(), center = Offset(bx, by))
        drawCircle(
            color = DoodleColors.InkBlack,
            radius = 5.dp.toPx(),
            center = Offset(bx, by),
            style = Stroke(width = 1.4.dp.toPx())
        )
    }

    // Bold lava crust line
    drawLine(
        color = DoodleColors.InkBlack,
        start = Offset(0f, lavaY),
        end = Offset(w, lavaY),
        strokeWidth = 2.4.dp.toPx()
    )
}

// ----------------------------------------------------------------------------
// PERIL 4: DINO_STEALTH (Levels 4, 8, 12...)
// Stickman tiptoeing next to a giant sleeping T-Rex head + 6-bar Noise Meter
// ----------------------------------------------------------------------------
private fun DrawScope.drawDinoStealthPeril(
    livesRemaining: Int,
    popTrigger: Int,
    popProgress: Float,
    bobOffset: Float,
    wavePhase: Float,
    isGameOver: Boolean
) {
    val w = size.width
    val h = size.height
    val groundY = h * 0.84f
    val noiseLevel = (6 - livesRemaining).coerceIn(0, 6)

    // 1. Draw 6-Bar "Noise Meter" at top-left of canvas
    val meterStartX = w * 0.08f
    val meterTopY = h * 0.12f
    val barW = (w * 0.11f).coerceIn(14.dp.toPx(), 24.dp.toPx())
    val barGap = 4.dp.toPx()

    for (i in 0 until 6) {
        val barH = (10 + i * 3).dp.toPx()
        val bx = meterStartX + i * (barW + barGap)
        val by = meterTopY + (28.dp.toPx() - barH)
        val isActiveNoise = i < noiseLevel
        val barColor = when {
            !isActiveNoise -> Color.White
            i < 2 -> DoodleColors.DoodleYellow
            i < 4 -> DoodleColors.HintBulbOrange
            else -> DoodleColors.WrongRed
        }
        drawRoundRect(
            color = barColor,
            topLeft = Offset(bx, by),
            size = Size(barW, barH),
            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
        )
        drawRoundRect(
            color = DoodleColors.InkBlack,
            topLeft = Offset(bx, by),
            size = Size(barW, barH),
            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx()),
            style = Stroke(width = 1.6.dp.toPx())
        )
    }

    // 2. Ground line
    drawLine(
        color = DoodleColors.InkBlack,
        start = Offset(w * 0.05f, groundY),
        end = Offset(w * 0.95f, groundY),
        strokeWidth = 2.6.dp.toPx(),
        cap = StrokeCap.Round
    )

    // 3. Giant Sleeping / Waking T-Rex Head on the right half of the scene
    val dinoX = w * 0.62f
    val dinoY = groundY - 46.dp.toPx()
    val jawOpenAngle = when {
        isGameOver || livesRemaining <= 0 -> 26.dp.toPx()
        livesRemaining <= 2 -> 16.dp.toPx()
        livesRemaining <= 4 -> 5.dp.toPx()
        else -> 0f
    }

    // T-Rex Upper Snout / Head
    val upperHeadW = w * 0.34f
    val upperHeadH = 38.dp.toPx()
    drawRoundRect(
        color = DoodleColors.DinoGreen,
        topLeft = Offset(dinoX - upperHeadW * 0.4f, dinoY - upperHeadH - jawOpenAngle * 0.5f),
        size = Size(upperHeadW, upperHeadH),
        cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx())
    )
    drawRoundRect(
        color = DoodleColors.InkBlack,
        topLeft = Offset(dinoX - upperHeadW * 0.4f, dinoY - upperHeadH - jawOpenAngle * 0.5f),
        size = Size(upperHeadW, upperHeadH),
        cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx()),
        style = Stroke(width = 2.4.dp.toPx())
    )

    // T-Rex Lower Jaw
    val lowerJawH = 18.dp.toPx()
    drawRoundRect(
        color = DoodleColors.DinoLightGreen,
        topLeft = Offset(dinoX - upperHeadW * 0.35f, dinoY + jawOpenAngle * 0.4f),
        size = Size(upperHeadW * 0.9f, lowerJawH),
        cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
    )
    drawRoundRect(
        color = DoodleColors.InkBlack,
        topLeft = Offset(dinoX - upperHeadW * 0.35f, dinoY + jawOpenAngle * 0.4f),
        size = Size(upperHeadW * 0.9f, lowerJawH),
        cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
        style = Stroke(width = 2.2.dp.toPx())
    )

    // Sharp T-Rex Teeth when mouth opens
    if (jawOpenAngle > 2f) {
        val startTx = dinoX - upperHeadW * 0.32f
        val toothStep = (upperHeadW * 0.7f) / 4f
        for (t in 0 until 4) {
            val tx = startTx + t * toothStep
            val ty = dinoY - jawOpenAngle * 0.5f
            val toothPath = Path().apply {
                moveTo(tx, ty)
                lineTo(tx + toothStep * 0.4f, ty + 8.dp.toPx())
                lineTo(tx + toothStep * 0.8f, ty)
                close()
            }
            drawPath(toothPath, color = Color.White)
            drawPath(toothPath, color = DoodleColors.InkBlack, style = Stroke(width = 1.4.dp.toPx()))
        }
    }

    // T-Rex Eye (Closed sleeping arc when lives >= 5, half-open at 3..4, wide glowing eye at <= 2)
    val eyeCenter = Offset(dinoX + upperHeadW * 0.15f, dinoY - upperHeadH * 0.55f - jawOpenAngle * 0.5f)
    if (livesRemaining >= 5) {
        // Sleeping closed eyelid + Zzz bubbles
        drawLine(
            color = DoodleColors.InkBlack,
            start = Offset(eyeCenter.x - 7.dp.toPx(), eyeCenter.y),
            end = Offset(eyeCenter.x + 7.dp.toPx(), eyeCenter.y),
            strokeWidth = 2.6.dp.toPx(),
            cap = StrokeCap.Round
        )
        // Floating Zzz circles
        drawCircle(
            color = DoodleColors.InkBlue.copy(alpha = 0.5f),
            radius = 4.dp.toPx(),
            center = Offset(eyeCenter.x + 12.dp.toPx(), eyeCenter.y - 16.dp.toPx() + bobOffset)
        )
        drawCircle(
            color = DoodleColors.InkBlue.copy(alpha = 0.7f),
            radius = 6.dp.toPx(),
            center = Offset(eyeCenter.x + 22.dp.toPx(), eyeCenter.y - 28.dp.toPx() + bobOffset)
        )
    } else {
        val eyeR = if (livesRemaining <= 2) 8.dp.toPx() else 5.5.dp.toPx()
        drawCircle(
            color = if (livesRemaining <= 2) DoodleColors.DoodleYellow else Color.White,
            radius = eyeR,
            center = eyeCenter
        )
        drawCircle(
            color = DoodleColors.InkBlack,
            radius = eyeR,
            center = eyeCenter,
            style = Stroke(width = 2.dp.toPx())
        )
        drawCircle(
            color = DoodleColors.WrongRed,
            radius = eyeR * 0.45f,
            center = Offset(eyeCenter.x - 2.dp.toPx(), eyeCenter.y)
        )
    }

    // 4. Stickman Tiptoeing on the left side
    val stickmanX = w * 0.25f
    val stickmanScale = (w / 420f).coerceIn(0.68f, 1.05f)
    val stickmanHandY = groundY - 72.dp.toPx() * stickmanScale + bobOffset * 0.5f

    drawDoodleStickman(
        handAnchor = Offset(stickmanX, stickmanHandY),
        scale = stickmanScale,
        isPanicking = livesRemaining <= 2,
        isSplashed = isGameOver || livesRemaining <= 0,
        wavePhase = wavePhase,
        handsUp = false
    )

    // 5. Comic "CLANG!" sound burst waves on wrong guess
    if (popTrigger > 0 && popProgress < 0.9f) {
        val clangCenter = Offset(stickmanX + 18.dp.toPx(), groundY - 16.dp.toPx())
        for (ring in 1..2) {
            drawCircle(
                color = DoodleColors.WrongRed.copy(alpha = (1f - popProgress).coerceIn(0f, 1f)),
                radius = (ring * 12.dp.toPx()) + popProgress * 22.dp.toPx(),
                center = clangCenter,
                style = Stroke(width = 2.2.dp.toPx())
            )
        }
    }
}

// ----------------------------------------------------------------------------
// SHARED DOODLE STICKMAN, BALLOONS & SHARK HELPERS
// ----------------------------------------------------------------------------

private fun DrawScope.drawDoodleBalloon(
    center: Offset,
    rx: Float,
    ry: Float,
    color: Color
) {
    drawOval(
        color = color,
        topLeft = Offset(center.x - rx, center.y - ry),
        size = Size(rx * 2f, ry * 2f)
    )
    drawOval(
        color = Color.White.copy(alpha = 0.55f),
        topLeft = Offset(center.x - rx * 0.58f, center.y - ry * 0.62f),
        size = Size(rx * 0.42f, ry * 0.52f)
    )
    val knotPath = Path().apply {
        moveTo(center.x, center.y + ry)
        lineTo(center.x - 4.dp.toPx(), center.y + ry + 5.dp.toPx())
        lineTo(center.x + 4.dp.toPx(), center.y + ry + 5.dp.toPx())
        close()
    }
    drawPath(path = knotPath, color = color)
    drawPath(
        path = knotPath,
        color = DoodleColors.InkBlack,
        style = Stroke(width = 1.5.dp.toPx(), join = StrokeJoin.Round)
    )
    drawOval(
        color = DoodleColors.InkBlack,
        topLeft = Offset(center.x - rx, center.y - ry),
        size = Size(rx * 2f, ry * 2f),
        style = Stroke(width = 2.2.dp.toPx())
    )
}

private fun DrawScope.drawPoppingBalloonBurst(
    center: Offset,
    progress: Float,
    color: Color
) {
    val rays = 8
    val innerR = 8.dp.toPx() + progress * 24.dp.toPx()
    val outerR = 18.dp.toPx() + progress * 38.dp.toPx()
    for (i in 0 until rays) {
        val angle = (i * 2.0 * PI / rays).toFloat()
        val start = Offset(center.x + cos(angle) * innerR, center.y + sin(angle) * innerR)
        val end = Offset(center.x + cos(angle) * outerR, center.y + sin(angle) * outerR)
        drawLine(
            color = color.copy(alpha = (1f - progress).coerceIn(0f, 1f)),
            start = start,
            end = end,
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawDoodleStickman(
    handAnchor: Offset,
    scale: Float,
    isPanicking: Boolean,
    isSplashed: Boolean,
    wavePhase: Float,
    handsUp: Boolean = true
) {
    val strokeW = (3.2.dp.toPx() * scale).coerceAtLeast(2.5.dp.toPx())
    val headRadius = 13.dp.toPx() * scale

    val shoulderY = handAnchor.y + 22.dp.toPx() * scale
    val headCenter = Offset(handAnchor.x, shoulderY - headRadius - 4.dp.toPx() * scale)
    val hipY = shoulderY + 28.dp.toPx() * scale

    if (handsUp) {
        drawLine(
            color = DoodleColors.InkBlack,
            start = Offset(handAnchor.x, shoulderY),
            end = Offset(handAnchor.x - 7.dp.toPx() * scale, handAnchor.y + 8.dp.toPx() * scale),
            strokeWidth = strokeW,
            cap = StrokeCap.Round
        )
        drawLine(
            color = DoodleColors.InkBlack,
            start = Offset(handAnchor.x - 7.dp.toPx() * scale, handAnchor.y + 8.dp.toPx() * scale),
            end = handAnchor,
            strokeWidth = strokeW,
            cap = StrokeCap.Round
        )
    } else {
        val armSwing = sin(wavePhase * 2f) * 6.dp.toPx() * scale
        drawLine(
            color = DoodleColors.InkBlack,
            start = Offset(handAnchor.x, shoulderY),
            end = Offset(handAnchor.x - 16.dp.toPx() * scale, shoulderY + 12.dp.toPx() * scale + armSwing),
            strokeWidth = strokeW,
            cap = StrokeCap.Round
        )
    }

    if (isPanicking) {
        val waveArmY = shoulderY - 10.dp.toPx() * scale + sin(wavePhase * 3f) * 8.dp.toPx()
        drawLine(
            color = DoodleColors.InkBlack,
            start = Offset(handAnchor.x, shoulderY),
            end = Offset(handAnchor.x + 22.dp.toPx() * scale, waveArmY),
            strokeWidth = strokeW,
            cap = StrokeCap.Round
        )
    } else if (handsUp) {
        drawLine(
            color = DoodleColors.InkBlack,
            start = Offset(handAnchor.x, shoulderY),
            end = Offset(handAnchor.x + 7.dp.toPx() * scale, handAnchor.y + 8.dp.toPx() * scale),
            strokeWidth = strokeW,
            cap = StrokeCap.Round
        )
        drawLine(
            color = DoodleColors.InkBlack,
            start = Offset(handAnchor.x + 7.dp.toPx() * scale, handAnchor.y + 8.dp.toPx() * scale),
            end = handAnchor,
            strokeWidth = strokeW,
            cap = StrokeCap.Round
        )
    } else {
        val armSwing = cos(wavePhase * 2f) * 6.dp.toPx() * scale
        drawLine(
            color = DoodleColors.InkBlack,
            start = Offset(handAnchor.x, shoulderY),
            end = Offset(handAnchor.x + 16.dp.toPx() * scale, shoulderY + 12.dp.toPx() * scale + armSwing),
            strokeWidth = strokeW,
            cap = StrokeCap.Round
        )
    }

    // Torso
    drawLine(
        color = DoodleColors.InkBlack,
        start = Offset(handAnchor.x, shoulderY),
        end = Offset(handAnchor.x, hipY),
        strokeWidth = strokeW,
        cap = StrokeCap.Round
    )

    // Legs
    val kick = if (isPanicking) sin(wavePhase * 4f) * 7.dp.toPx() else sin(wavePhase) * 3.dp.toPx()
    drawLine(
        color = DoodleColors.InkBlack,
        start = Offset(handAnchor.x, hipY),
        end = Offset(handAnchor.x - 13.dp.toPx() * scale, hipY + 22.dp.toPx() * scale + kick),
        strokeWidth = strokeW,
        cap = StrokeCap.Round
    )
    drawLine(
        color = DoodleColors.InkBlack,
        start = Offset(handAnchor.x, hipY),
        end = Offset(handAnchor.x + 13.dp.toPx() * scale, hipY + 22.dp.toPx() * scale - kick),
        strokeWidth = strokeW,
        cap = StrokeCap.Round
    )

    // Head
    drawCircle(color = Color.White, radius = headRadius, center = headCenter)
    drawCircle(
        color = DoodleColors.InkBlack,
        radius = headRadius,
        center = headCenter,
        style = Stroke(width = strokeW * 0.85f)
    )

    val eyeOffsetX = headRadius * 0.34f
    val eyeY = headCenter.y - headRadius * 0.15f
    drawCircle(
        color = DoodleColors.InkBlack,
        radius = 2.dp.toPx() * scale,
        center = Offset(headCenter.x - eyeOffsetX, eyeY)
    )
    drawCircle(
        color = DoodleColors.InkBlack,
        radius = 2.dp.toPx() * scale,
        center = Offset(headCenter.x + eyeOffsetX, eyeY)
    )

    if (isPanicking || isSplashed) {
        drawCircle(
            color = DoodleColors.InkBlack,
            radius = 3.2.dp.toPx() * scale,
            center = Offset(headCenter.x, headCenter.y + headRadius * 0.35f),
            style = Stroke(width = 1.8.dp.toPx())
        )
        drawCircle(
            color = DoodleColors.WaterBlue,
            radius = 2.5.dp.toPx() * scale,
            center = Offset(headCenter.x + headRadius + 5.dp.toPx(), headCenter.y - 4.dp.toPx())
        )
    } else {
        val smilePath = Path().apply {
            moveTo(headCenter.x - headRadius * 0.35f, headCenter.y + headRadius * 0.22f)
            quadraticTo(
                headCenter.x,
                headCenter.y + headRadius * 0.55f,
                headCenter.x + headRadius * 0.35f,
                headCenter.y + headRadius * 0.22f
            )
        }
        drawPath(
            path = smilePath,
            color = DoodleColors.InkBlack,
            style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

private fun DrawScope.drawCirclingSharkFin(
    x: Float,
    waterY: Float,
    facingRight: Boolean,
    scale: Float
) {
    val finH = 34.dp.toPx() * scale
    val finW = 28.dp.toPx() * scale
    val dir = if (facingRight) 1f else -1f

    val finPath = Path().apply {
        moveTo(x - finW * 0.5f * dir, waterY + 4.dp.toPx())
        quadraticTo(
            x - finW * 0.1f * dir,
            waterY - finH * 0.7f,
            x + finW * 0.55f * dir,
            waterY - finH
        )
        quadraticTo(
            x + finW * 0.25f * dir,
            waterY - finH * 0.35f,
            x + finW * 0.5f * dir,
            waterY + 4.dp.toPx()
        )
        close()
    }

    drawPath(path = finPath, color = DoodleColors.SharkGrey)
    drawPath(
        path = finPath,
        color = DoodleColors.InkBlack,
        style = Stroke(width = 2.4.dp.toPx(), join = StrokeJoin.Round)
    )
}

private fun DrawScope.drawOpenSharkJaws(
    centerX: Float,
    waterY: Float,
    scale: Float
) {
    val jawW = 46.dp.toPx() * scale
    val jawH = 44.dp.toPx() * scale

    val outerPath = Path().apply {
        moveTo(centerX - jawW, waterY + 8.dp.toPx())
        quadraticTo(centerX - jawW * 0.7f, waterY - jawH, centerX, waterY - jawH * 1.08f)
        quadraticTo(centerX + jawW * 0.7f, waterY - jawH, centerX + jawW, waterY + 8.dp.toPx())
        close()
    }
    drawPath(path = outerPath, color = DoodleColors.SharkGrey)
    drawPath(
        path = outerPath,
        color = DoodleColors.InkBlack,
        style = Stroke(width = 2.6.dp.toPx(), join = StrokeJoin.Round)
    )

    val mouthPath = Path().apply {
        moveTo(centerX - jawW * 0.68f, waterY + 4.dp.toPx())
        quadraticTo(centerX, waterY - jawH * 0.85f, centerX + jawW * 0.68f, waterY + 4.dp.toPx())
        close()
    }
    drawPath(path = mouthPath, color = DoodleColors.WrongRed)
    drawPath(
        path = mouthPath,
        color = DoodleColors.InkBlack,
        style = Stroke(width = 2.dp.toPx(), join = StrokeJoin.Round)
    )

    val teethPath = Path().apply {
        val leftX = centerX - jawW * 0.52f
        val rightX = centerX + jawW * 0.52f
        val step = (rightX - leftX) / 5f
        var tx = leftX
        moveTo(tx, waterY - jawH * 0.15f)
        for (i in 0 until 5) {
            lineTo(tx + step * 0.5f, waterY - jawH * 0.35f)
            lineTo(tx + step, waterY - jawH * 0.15f)
            tx += step
        }
    }
    drawPath(path = teethPath, color = Color.White)
    drawPath(
        path = teethPath,
        color = DoodleColors.InkBlack,
        style = Stroke(width = 1.6.dp.toPx(), join = StrokeJoin.Round)
    )
}

private fun DrawScope.drawDoodleWaterWaves(
    waterY: Float,
    wavePhase: Float
) {
    val w = size.width
    val h = size.height

    val wavePath = Path().apply {
        moveTo(0f, h)
        lineTo(0f, waterY)
        var x = 0f
        val step = 18.dp.toPx()
        while (x <= w + step) {
            val wy = waterY + sin(wavePhase + x * 0.045f) * 5.dp.toPx()
            lineTo(x, wy)
            x += step
        }
        lineTo(w, h)
        close()
    }
    drawPath(path = wavePath, color = DoodleColors.WaterBlue.copy(alpha = 0.35f))

    val crestPath = Path().apply {
        moveTo(0f, waterY)
        var x = 0f
        val step = 16.dp.toPx()
        while (x <= w + step) {
            val wy = waterY + sin(wavePhase + x * 0.045f) * 5.dp.toPx()
            lineTo(x, wy)
            x += step
        }
    }
    drawPath(
        path = crestPath,
        color = DoodleColors.WaterDeep,
        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
    )
    drawPath(
        path = crestPath,
        color = DoodleColors.InkBlack,
        style = Stroke(width = 1.4.dp.toPx(), cap = StrokeCap.Round)
    )
}

// ============================================================================
// 11. SCREEN 3: VICTORY / LEVEL COMPLETE OVERLAY ("WELL DONE!")
// ============================================================================

@Composable
fun VictoryOverlay(
    level: WordLevel,
    perfectCount: Int,
    streak: Int,
    maxStreak: Int,
    chestProgress: Int,
    themeProgress: Int,
    fontFamily: FontFamily,
    onNextLevel: () -> Unit
) {
    val rewardCoins = if (level.isBossLevel) 50 else 25

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 20.dp, vertical = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 460.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(DoodleColors.PaperOffWhite)
        ) {
            // Top dark chalkboard header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(215.dp)
                    .background(DoodleColors.ChalkboardDark)
                    .padding(16.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val step = 24.dp.toPx()
                    var x = 0f
                    while (x <= size.width) {
                        drawLine(
                            color = DoodleColors.ChalkboardGrid,
                            start = Offset(x, 0f),
                            end = Offset(x, size.height),
                            strokeWidth = 1f
                        )
                        x += step
                    }
                    var y = 0f
                    while (y <= size.height) {
                        drawLine(
                            color = DoodleColors.ChalkboardGrid,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1f
                        )
                        y += step
                    }
                }

                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Canvas(
                        modifier = Modifier
                            .weight(0.42f)
                            .fillMaxHeight()
                    ) {
                        drawCelebratingStickmanOnChalkboard()
                    }

                    Column(
                        modifier = Modifier
                            .weight(0.58f)
                            .padding(start = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = level.word.uppercase(),
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Black,
                            fontSize = 27.sp,
                            letterSpacing = 3.sp,
                            color = DoodleColors.DoodleYellow,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = level.clue,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            lineHeight = 19.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Bottom white graph-paper card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "WELL DONE!",
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Black,
                        fontSize = 32.sp,
                        color = DoodleColors.CorrectGreen,
                        modifier = Modifier.testTag("well_done_header")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        VictoryStatChip("Perfect: $perfectCount", fontFamily)
                        VictoryStatChip("Streak: $streak", fontFamily)
                        VictoryStatChip("Max Streak: $maxStreak", fontFamily)
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    DoodleProgressRow(
                        label = "Chest",
                        current = chestProgress,
                        total = 4,
                        barColor = DoodleColors.HintBulbOrange,
                        fontFamily = fontFamily
                    ) {
                        DrawDoodleChestIcon()
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    DoodleProgressRow(
                        label = "Theme",
                        current = themeProgress,
                        total = 10,
                        barColor = DoodleColors.HintPotionBlue,
                        fontFamily = fontFamily
                    ) {
                        DrawDoodlePaletteIcon()
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    SketchyContainer(
                        fillColor = DoodleColors.DoodleYellow,
                        cornerRadius = 28.dp,
                        onClick = onNextLevel,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .testTag("next_level_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "NEXT  (+$rewardCoins Coins)",
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.Black,
                                fontSize = 21.sp,
                                color = DoodleColors.InkBlack
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Canvas(modifier = Modifier.size(26.dp)) {
                                drawDoodleRightArrow()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VictoryStatChip(
    text: String,
    fontFamily: FontFamily
) {
    SketchyContainer(
        fillColor = Color.White,
        cornerRadius = 12.dp,
        strokeWidth = 1.5.dp,
        shadowOffset = 2.dp
    ) {
        Text(
            text = text,
            fontFamily = fontFamily,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 13.sp,
            color = DoodleColors.InkBlack,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun DoodleProgressRow(
    label: String,
    current: Int,
    total: Int,
    barColor: Color,
    fontFamily: FontFamily,
    iconContent: @Composable () -> Unit
) {
    val fraction = (current.toFloat() / total.toFloat()).coerceIn(0f, 1f)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontFamily = fontFamily,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 16.sp,
            color = DoodleColors.InkBlack,
            modifier = Modifier.width(64.dp)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .height(26.dp)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val r = 12.dp.toPx()
                drawRoundRect(
                    color = Color.White,
                    cornerRadius = CornerRadius(r, r)
                )
                drawRoundRect(
                    color = barColor,
                    size = Size(size.width * fraction, size.height),
                    cornerRadius = CornerRadius(r, r)
                )
                drawRoundRect(
                    color = DoodleColors.InkBlack,
                    cornerRadius = CornerRadius(r, r),
                    style = Stroke(width = 2.dp.toPx())
                )
            }
            Text(
                text = "$current/$total",
                fontFamily = fontFamily,
                fontWeight = FontWeight.Black,
                fontSize = 13.sp,
                color = DoodleColors.InkBlack
            )
        }

        Box(
            modifier = Modifier.size(36.dp),
            contentAlignment = Alignment.Center
        ) {
            iconContent()
        }
    }
}

private fun DrawScope.drawCelebratingStickmanOnChalkboard() {
    val cx = size.width * 0.5f
    val cy = size.height * 0.52f
    val headR = 16.dp.toPx()

    val confettiColors = listOf(
        DoodleColors.DoodleYellow,
        DoodleColors.CorrectGreen,
        DoodleColors.WaterBlue,
        Color(0xFFFF595E)
    )
    confettiColors.forEachIndexed { idx, c ->
        val angle = (idx * PI / 2.0 + 0.4).toFloat()
        val px = cx + cos(angle) * 44.dp.toPx()
        val py = cy - 12.dp.toPx() + sin(angle) * 44.dp.toPx()
        drawCircle(color = c, radius = 4.dp.toPx(), center = Offset(px, py))
    }

    val chalk = Color.White
    val sw = 3.2.dp.toPx()
    val headCenter = Offset(cx, cy - 28.dp.toPx())

    drawCircle(color = chalk, radius = headR, center = headCenter, style = Stroke(width = sw))
    drawCircle(color = chalk, radius = 2.dp.toPx(), center = Offset(headCenter.x - 5.dp.toPx(), headCenter.y - 2.dp.toPx()))
    drawCircle(color = chalk, radius = 2.dp.toPx(), center = Offset(headCenter.x + 5.dp.toPx(), headCenter.y - 2.dp.toPx()))

    val neckY = headCenter.y + headR
    val hipY = neckY + 34.dp.toPx()
    drawLine(chalk, Offset(cx, neckY), Offset(cx, hipY), strokeWidth = sw, cap = StrokeCap.Round)
    drawLine(chalk, Offset(cx, neckY + 8.dp.toPx()), Offset(cx - 24.dp.toPx(), neckY - 14.dp.toPx()), strokeWidth = sw, cap = StrokeCap.Round)
    drawLine(chalk, Offset(cx, neckY + 8.dp.toPx()), Offset(cx + 24.dp.toPx(), neckY - 14.dp.toPx()), strokeWidth = sw, cap = StrokeCap.Round)
    drawLine(chalk, Offset(cx, hipY), Offset(cx - 18.dp.toPx(), hipY + 26.dp.toPx()), strokeWidth = sw, cap = StrokeCap.Round)
    drawLine(chalk, Offset(cx, hipY), Offset(cx + 18.dp.toPx(), hipY + 26.dp.toPx()), strokeWidth = sw, cap = StrokeCap.Round)
}

// ============================================================================
// 12. GAME OVER OVERLAY & TWO-PLAYER PASS-AND-PLAY SETUP
// ============================================================================

@Composable
fun GameOverOverlay(
    level: WordLevel,
    fontFamily: FontFamily,
    onRescue: () -> Unit,
    onRetry: () -> Unit,
    onMainMenu: () -> Unit
) {
    val peril = level.perilType

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.58f))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        SketchyContainer(
            fillColor = DoodleColors.PaperOffWhite,
            cornerRadius = 26.dp,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 420.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = peril.gameOverTitle,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 26.sp,
                    color = DoodleColors.WrongRed,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("game_over_title")
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = peril.gameOverDescription,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = DoodleColors.InkBlack,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Clue: ${level.clue}",
                    fontFamily = fontFamily,
                    fontStyle = FontStyle.Italic,
                    fontSize = 15.sp,
                    color = DoodleColors.InkBlue,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                SketchyContainer(
                    fillColor = DoodleColors.DoodleYellow,
                    cornerRadius = 24.dp,
                    onClick = onRescue,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("rescue_button")
                ) {
                    Text(
                        text = "Rescue (+3 Lives • 250 Coins)",
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = DoodleColors.InkBlack
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                SketchyContainer(
                    fillColor = Color.White,
                    cornerRadius = 24.dp,
                    onClick = onRetry,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("retry_button")
                ) {
                    Text(
                        text = "Try Again",
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = DoodleColors.InkBlack
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Back to Main Menu",
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = DoodleColors.InkBlack.copy(alpha = 0.7f),
                    modifier = Modifier
                        .clickable(onClick = onMainMenu)
                        .padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun TwoPlayerSetupScreen(
    fontFamily: FontFamily,
    onBack: () -> Unit,
    onStartGame: (word: String, clue: String, perilNumber: Int) -> Unit
) {
    var secretWord by remember { mutableStateOf("") }
    var secretClue by remember { mutableStateOf("") }
    var selectedPerilNum by remember { mutableIntStateOf(1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .widthIn(max = 500.dp)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SketchyIconButton(
                testTag = "two_player_back",
                contentDescription = "Back",
                onClick = onBack
            ) {
                DrawDoodleBackArrow()
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Two Player Duel",
                fontFamily = fontFamily,
                fontWeight = FontWeight.Black,
                fontSize = 26.sp,
                color = DoodleColors.InkBlack
            )
        }

        SketchyContainer(
            fillColor = Color.White,
            cornerRadius = 24.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Player 1: Enter a secret word, clue & hazard for Player 2!",
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = DoodleColors.InkBlack,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = secretWord,
                    onValueChange = { input ->
                        secretWord = input.filter { it.isLetter() }.uppercase().take(12)
                    },
                    label = { Text("Secret Word (A-Z)", fontFamily = fontFamily) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DoodleColors.InkBlack,
                        unfocusedBorderColor = DoodleColors.InkBlack,
                        focusedTextColor = DoodleColors.InkBlack,
                        unfocusedTextColor = DoodleColors.InkBlack
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("two_player_word_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = secretClue,
                    onValueChange = { secretClue = it.take(60) },
                    label = { Text("Clue / Category Prompt", fontFamily = fontFamily) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DoodleColors.InkBlack,
                        unfocusedBorderColor = DoodleColors.InkBlack,
                        focusedTextColor = DoodleColors.InkBlack,
                        unfocusedTextColor = DoodleColors.InkBlack
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("two_player_clue_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Pick Hazard:",
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = DoodleColors.InkBlack
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    PerilType.entries.forEachIndexed { idx, p ->
                        val lvlNum = idx + 1
                        val selected = selectedPerilNum == lvlNum
                        SketchyContainer(
                            fillColor = if (selected) DoodleColors.DoodleYellow else DoodleColors.PaperOffWhite,
                            cornerRadius = 12.dp,
                            strokeWidth = 1.5.dp,
                            onClick = { selectedPerilNum = lvlNum },
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text(
                                text = p.displayName.substringBefore(" "),
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = DoodleColors.InkBlack,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                SketchyContainer(
                    fillColor = DoodleColors.DoodleYellow,
                    cornerRadius = 24.dp,
                    onClick = {
                        onStartGame(
                            secretWord.ifBlank { "SHARK" },
                            secretClue.ifBlank { "Ocean predator with sharp teeth" },
                            selectedPerilNum
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("start_two_player_duel_button")
                ) {
                    Text(
                        text = "Start Duel!",
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = DoodleColors.InkBlack
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

// ============================================================================
// 13. SETTINGS & COIN BONUS DIALOGS
// ============================================================================

@Composable
fun SettingsDoodleDialog(
    soundEnabled: Boolean,
    useCursive: Boolean,
    selectedTheme: PaperTheme,
    fontFamily: FontFamily,
    onToggleSound: () -> Unit,
    onToggleFont: () -> Unit,
    onSelectTheme: (PaperTheme) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        SketchyContainer(
            fillColor = DoodleColors.PaperOffWhite,
            cornerRadius = 24.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Notebook Settings",
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    color = DoodleColors.InkBlack
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Sound Effects Toggle
                SketchyContainer(
                    fillColor = if (soundEnabled) DoodleColors.DoodleYellow else Color.White,
                    cornerRadius = 18.dp,
                    onClick = onToggleSound,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("toggle_sound_button")
                ) {
                    Text(
                        text = if (soundEnabled) "🔊 Sound Effects: ON" else "🔇 Sound Effects: OFF",
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = DoodleColors.InkBlack
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Quick MediaPlayer Procedural Sound Audition Buttons (Pop, Splash, Crunch)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SketchyContainer(
                        fillColor = Color.White,
                        cornerRadius = 12.dp,
                        onClick = { ProceduralSoundEngine.play(DoodleSoundEffect.POP) },
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .testTag("test_pop_sound")
                    ) {
                        Text("🎈 Pop", fontFamily = fontFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    SketchyContainer(
                        fillColor = Color.White,
                        cornerRadius = 12.dp,
                        onClick = { ProceduralSoundEngine.play(DoodleSoundEffect.SPLASH) },
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .testTag("test_splash_sound")
                    ) {
                        Text("🌊 Splash", fontFamily = fontFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    SketchyContainer(
                        fillColor = Color.White,
                        cornerRadius = 12.dp,
                        onClick = { ProceduralSoundEngine.play(DoodleSoundEffect.CRUNCH) },
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .testTag("test_crunch_sound")
                    ) {
                        Text("🪨 Crunch", fontFamily = fontFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                SketchyContainer(
                    fillColor = Color.White,
                    cornerRadius = 18.dp,
                    onClick = onToggleFont,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    Text(
                        text = if (useCursive) "Font: Cursive Script (Tap to switch)" else "Font: Comic Bold Sans (Tap to switch)",
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = DoodleColors.InkBlack
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Graph Paper Theme:",
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    color = DoodleColors.InkBlack
                )

                Spacer(modifier = Modifier.height(6.dp))

                PaperTheme.entries.forEach { theme ->
                    val isSelected = theme == selectedTheme
                    SketchyContainer(
                        fillColor = if (isSelected) DoodleColors.DoodleYellow else theme.bg,
                        cornerRadius = 16.dp,
                        onClick = { onSelectTheme(theme) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .padding(vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isSelected) "✓ ${theme.title}" else theme.title,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = DoodleColors.InkBlack
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                SketchyContainer(
                    fillColor = DoodleColors.DoodleYellow,
                    cornerRadius = 20.dp,
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    Text(
                        text = "Close",
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = DoodleColors.InkBlack
                    )
                }
            }
        }
    }
}

@Composable
fun CoinBonusDoodleDialog(
    coins: Int,
    fontFamily: FontFamily,
    onClaimCoins: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        SketchyContainer(
            fillColor = DoodleColors.PaperOffWhite,
            cornerRadius = 24.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Doodle Coin Bank",
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    color = DoodleColors.InkBlack
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Current Balance: $coins Coins",
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = DoodleColors.InkBlue
                )

                Spacer(modifier = Modifier.height(16.dp))

                SketchyContainer(
                    fillColor = DoodleColors.DoodleYellow,
                    cornerRadius = 20.dp,
                    onClick = { onClaimCoins(150) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("claim_free_coins_button")
                ) {
                    Text(
                        text = "Claim Free Bonus (+150 Coins)",
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp,
                        color = DoodleColors.InkBlack
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                SketchyContainer(
                    fillColor = Color.White,
                    cornerRadius = 20.dp,
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    Text(
                        text = "Maybe Later",
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = DoodleColors.InkBlack
                    )
                }
            }
        }
    }
}

// ============================================================================
// 14. REUSABLE DOODLE HEADER PILLS & PROCEDURAL CANVAS ICONS
// ============================================================================

@Composable
fun SketchyIconButton(
    testTag: String,
    contentDescription: String,
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    SketchyContainer(
        fillColor = Color.White,
        cornerRadius = 16.dp,
        onClick = onClick,
        modifier = Modifier
            .size(48.dp)
            .testTag(testTag)
            .semantics { this.contentDescription = contentDescription }
    ) {
        icon()
    }
}

@Composable
fun CoinBalancePill(
    coins: Int,
    fontFamily: FontFamily,
    onClickAdd: () -> Unit,
    testTag: String
) {
    SketchyContainer(
        fillColor = Color.White,
        cornerRadius = 22.dp,
        onClick = onClickAdd,
        modifier = Modifier
            .height(48.dp)
            .testTag(testTag)
            .semantics { contentDescription = "Coins $coins, tap to add coins" }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Canvas(modifier = Modifier.size(22.dp)) {
                drawDoodleStarCoin()
            }

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = coins.toString(),
                fontFamily = fontFamily,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                color = DoodleColors.InkBlack
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(24.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(color = DoodleColors.DoodleYellow)
                    drawCircle(
                        color = DoodleColors.InkBlack,
                        style = Stroke(width = 1.8.dp.toPx())
                    )
                    val m = size.width * 0.28f
                    drawLine(
                        color = DoodleColors.InkBlack,
                        start = Offset(size.width * 0.5f, m),
                        end = Offset(size.width * 0.5f, size.height - m),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = DoodleColors.InkBlack,
                        start = Offset(m, size.height * 0.5f),
                        end = Offset(size.width - m, size.height * 0.5f),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }
        }
    }
}

@Composable
private fun DrawDoodleGearIcon() {
    Canvas(modifier = Modifier.size(26.dp)) {
        val c = center
        val outerR = size.minDimension * 0.42f
        val innerR = size.minDimension * 0.22f
        for (i in 0 until 8) {
            val angle = (i * PI / 4.0).toFloat()
            drawLine(
                color = DoodleColors.InkBlack,
                start = Offset(c.x + cos(angle) * innerR, c.y + sin(angle) * innerR),
                end = Offset(c.x + cos(angle) * outerR, c.y + sin(angle) * outerR),
                strokeWidth = 3.5.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
        drawCircle(color = DoodleColors.DoodleYellow, radius = size.minDimension * 0.30f)
        drawCircle(
            color = DoodleColors.InkBlack,
            radius = size.minDimension * 0.30f,
            style = Stroke(width = 2.dp.toPx())
        )
        drawCircle(color = Color.White, radius = size.minDimension * 0.12f)
    }
}

@Composable
private fun DrawDoodleBackArrow() {
    Canvas(modifier = Modifier.size(24.dp)) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.82f, h * 0.5f)
            lineTo(w * 0.20f, h * 0.5f)
            moveTo(w * 0.48f, h * 0.22f)
            lineTo(w * 0.18f, h * 0.5f)
            lineTo(w * 0.48f, h * 0.78f)
        }
        drawPath(
            path = path,
            color = DoodleColors.InkBlack,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

private fun DrawScope.drawDoodleRightArrow() {
    val w = size.width
    val h = size.height
    val path = Path().apply {
        moveTo(w * 0.15f, h * 0.5f)
        lineTo(w * 0.82f, h * 0.5f)
        moveTo(w * 0.55f, h * 0.22f)
        lineTo(w * 0.85f, h * 0.5f)
        lineTo(w * 0.55f, h * 0.78f)
    }
    drawPath(
        path = path,
        color = DoodleColors.InkBlack,
        style = Stroke(width = 3.2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
}

private fun DrawScope.drawDoodleStarCoin() {
    val r = size.minDimension * 0.46f
    drawCircle(color = DoodleColors.DoodleYellow, radius = r)
    drawCircle(color = DoodleColors.InkBlack, radius = r, style = Stroke(width = 1.8.dp.toPx()))

    val starPath = Path()
    val outer = r * 0.62f
    val inner = r * 0.28f
    for (i in 0 until 10) {
        val rad = if (i % 2 == 0) outer else inner
        val angle = (i * PI / 5.0 - PI / 2.0).toFloat()
        val px = center.x + cos(angle) * rad
        val py = center.y + sin(angle) * rad
        if (i == 0) starPath.moveTo(px, py) else starPath.lineTo(px, py)
    }
    starPath.close()
    drawPath(starPath, color = Color(0xFFE67E22))
    drawPath(starPath, color = DoodleColors.InkBlack, style = Stroke(width = 1.dp.toPx()))
}

private fun DrawScope.drawDevilHornsBadge() {
    val w = size.width
    val h = size.height
    val cx = w * 0.5f
    val cy = h * 0.58f
    val r = w * 0.32f

    val leftHorn = Path().apply {
        moveTo(cx - r * 0.8f, cy - r * 0.2f)
        quadraticTo(cx - r * 1.35f, cy - r * 1.25f, cx - r * 0.35f, cy - r * 1.15f)
        close()
    }
    val rightHorn = Path().apply {
        moveTo(cx + r * 0.8f, cy - r * 0.2f)
        quadraticTo(cx + r * 1.35f, cy - r * 1.25f, cx + r * 0.35f, cy - r * 1.15f)
        close()
    }
    drawPath(leftHorn, color = DoodleColors.WrongRed)
    drawPath(leftHorn, color = DoodleColors.InkBlack, style = Stroke(width = 1.6.dp.toPx()))
    drawPath(rightHorn, color = DoodleColors.WrongRed)
    drawPath(rightHorn, color = DoodleColors.InkBlack, style = Stroke(width = 1.6.dp.toPx()))

    drawCircle(color = DoodleColors.WrongRed, radius = r, center = Offset(cx, cy))
    drawCircle(color = DoodleColors.InkBlack, radius = r, center = Offset(cx, cy), style = Stroke(width = 1.8.dp.toPx()))

    drawLine(
        color = DoodleColors.InkBlack,
        start = Offset(cx - r * 0.5f, cy - r * 0.25f),
        end = Offset(cx - r * 0.15f, cy - r * 0.05f),
        strokeWidth = 1.8.dp.toPx(),
        cap = StrokeCap.Round
    )
    drawLine(
        color = DoodleColors.InkBlack,
        start = Offset(cx + r * 0.5f, cy - r * 0.25f),
        end = Offset(cx + r * 0.15f, cy - r * 0.05f),
        strokeWidth = 1.8.dp.toPx(),
        cap = StrokeCap.Round
    )
}

@Composable
private fun DrawDoodleMagnifierIcon() {
    Canvas(modifier = Modifier.size(30.dp)) {
        val lensCenter = Offset(size.width * 0.42f, size.height * 0.42f)
        val lensR = size.minDimension * 0.28f

        drawLine(
            color = DoodleColors.HintMagnifierGreen,
            start = Offset(lensCenter.x + lensR * 0.7f, lensCenter.y + lensR * 0.7f),
            end = Offset(size.width * 0.84f, size.height * 0.84f),
            strokeWidth = 6.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = DoodleColors.InkBlack,
            start = Offset(lensCenter.x + lensR * 0.7f, lensCenter.y + lensR * 0.7f),
            end = Offset(size.width * 0.84f, size.height * 0.84f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )

        drawCircle(
            color = DoodleColors.HintMagnifierGreen.copy(alpha = 0.35f),
            radius = lensR,
            center = lensCenter
        )
        drawCircle(
            color = DoodleColors.InkBlack,
            radius = lensR,
            center = lensCenter,
            style = Stroke(width = 2.4.dp.toPx())
        )
    }
}

@Composable
private fun DrawDoodlePotionShieldIcon() {
    Canvas(modifier = Modifier.size(30.dp)) {
        val w = size.width
        val h = size.height
        val shieldPath = Path().apply {
            moveTo(w * 0.5f, h * 0.12f)
            lineTo(w * 0.84f, h * 0.26f)
            quadraticTo(w * 0.82f, h * 0.68f, w * 0.5f, h * 0.88f)
            quadraticTo(w * 0.18f, h * 0.68f, w * 0.16f, h * 0.26f)
            close()
        }
        drawPath(shieldPath, color = DoodleColors.HintPotionBlue)
        drawPath(
            shieldPath,
            color = DoodleColors.InkBlack,
            style = Stroke(width = 2.2.dp.toPx(), join = StrokeJoin.Round)
        )
        drawLine(
            color = Color.White,
            start = Offset(w * 0.5f, h * 0.34f),
            end = Offset(w * 0.5f, h * 0.64f),
            strokeWidth = 2.8.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color.White,
            start = Offset(w * 0.35f, h * 0.49f),
            end = Offset(w * 0.65f, h * 0.49f),
            strokeWidth = 2.8.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun DrawDoodleLightbulbIcon() {
    Canvas(modifier = Modifier.size(30.dp)) {
        val w = size.width
        val h = size.height
        val bulbCenter = Offset(w * 0.5f, h * 0.40f)
        val r = size.minDimension * 0.27f

        drawCircle(color = DoodleColors.HintBulbOrange, radius = r, center = bulbCenter)
        drawCircle(
            color = DoodleColors.InkBlack,
            radius = r,
            center = bulbCenter,
            style = Stroke(width = 2.2.dp.toPx())
        )

        drawRoundRect(
            color = Color.LightGray,
            topLeft = Offset(w * 0.38f, h * 0.64f),
            size = Size(w * 0.24f, h * 0.18f),
            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
        )
        drawRoundRect(
            color = DoodleColors.InkBlack,
            topLeft = Offset(w * 0.38f, h * 0.64f),
            size = Size(w * 0.24f, h * 0.18f),
            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx()),
            style = Stroke(width = 1.8.dp.toPx())
        )
    }
}

@Composable
private fun DrawDoodleChestIcon() {
    Canvas(modifier = Modifier.size(30.dp)) {
        val w = size.width
        val h = size.height
        drawRoundRect(
            color = Color(0xFFD35400),
            topLeft = Offset(w * 0.12f, h * 0.24f),
            size = Size(w * 0.76f, h * 0.56f),
            cornerRadius = CornerRadius(5.dp.toPx(), 5.dp.toPx())
        )
        drawRoundRect(
            color = DoodleColors.InkBlack,
            topLeft = Offset(w * 0.12f, h * 0.24f),
            size = Size(w * 0.76f, h * 0.56f),
            cornerRadius = CornerRadius(5.dp.toPx(), 5.dp.toPx()),
            style = Stroke(width = 2.dp.toPx())
        )
        drawLine(
            color = DoodleColors.InkBlack,
            start = Offset(w * 0.12f, h * 0.50f),
            end = Offset(w * 0.88f, h * 0.50f),
            strokeWidth = 2.dp.toPx()
        )
        drawCircle(
            color = DoodleColors.DoodleYellow,
            radius = 4.dp.toPx(),
            center = Offset(w * 0.5f, h * 0.50f)
        )
    }
}

@Composable
private fun DrawDoodlePaletteIcon() {
    Canvas(modifier = Modifier.size(30.dp)) {
        val r = size.minDimension * 0.42f
        drawCircle(color = DoodleColors.DoodleYellow, radius = r)
        drawCircle(color = DoodleColors.InkBlack, radius = r, style = Stroke(width = 2.dp.toPx()))
        drawCircle(color = DoodleColors.WrongRed, radius = 3.dp.toPx(), center = Offset(center.x - 5.dp.toPx(), center.y - 4.dp.toPx()))
        drawCircle(color = DoodleColors.CorrectGreen, radius = 3.dp.toPx(), center = Offset(center.x + 5.dp.toPx(), center.y - 4.dp.toPx()))
        drawCircle(color = DoodleColors.HintPotionBlue, radius = 3.dp.toPx(), center = Offset(center.x, center.y + 5.dp.toPx()))
    }
}
