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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.drawscope.withTransform
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

    val FlytrapGreen = Color(0xFF43A047)
    val FlytrapMouthPink = Color(0xFFEC407A)
    val VineLeafGreen = Color(0xFF2E7D32)

    val SubmarineSteel = Color(0xFF78909C)
    val SubmarineYellow = Color(0xFFFFCA28)

    val IceCubeCyan = Color(0xFF81D4FA)
    val IceCubeBorder = Color(0xFF0288D1)
    val LockedSlate = Color(0xFF90A4AE)

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
 * 6 Dynamic Peril Scenarios across the 100-Level Progression System:
 * 1. SHARK_BALLOONS (Levels 1–16)
 * 2. UFO_ABDUCTION (Levels 17–33)
 * 3. VOLCANO_BLOCKS / VOLCANO_LAVA (Levels 34–50)
 * 4. GIANT_VENUS_FLYTRAP (Levels 51–66)
 * 5. DINO_STEALTH / SLEEPING_TREX (Levels 67–83)
 * 6. LEAKY_SUBMARINE (Levels 84–100)
 */
enum class PerilType(
    val displayName: String,
    val worldRangeLabel: String,
    val panicBubbleText: String,
    val gameOverTitle: String,
    val gameOverDescription: String,
    val extraLifeLabel: String,
    val hitEffectLabel: String
) {
    SHARK_BALLOONS(
        displayName = "Shark & Balloons",
        worldRangeLabel = "Levels 1–16",
        panicBubbleText = "Come on, you can do that!",
        gameOverTitle = "SPLASH! SHARK SNACK",
        gameOverDescription = "All 6 balloons popped! The shark caught up.",
        extraLifeLabel = "+1 Balloon attached!",
        hitEffectLabel = "POP!"
    ),
    UFO_ABDUCTION(
        displayName = "UFO Abduction",
        worldRangeLabel = "Levels 17–33",
        panicBubbleText = "Beam me up... wait, no!",
        gameOverTitle = "ZAP! ABDUCTED!",
        gameOverDescription = "The UFO tractor beam pulled the stickman aboard!",
        extraLifeLabel = "+1 Gravity Anchor!",
        hitEffectLabel = "ZAP!"
    ),
    VOLCANO_BLOCKS(
        displayName = "Volcano Lava",
        worldRangeLabel = "Levels 34–50",
        panicBubbleText = "My shoes are melting!",
        gameOverTitle = "SIZZLE! MELTDOWN!",
        gameOverDescription = "All 6 stone crates crumbled into the bubbling lava!",
        extraLifeLabel = "+1 Stone Crate stacked!",
        hitEffectLabel = "CRUMBLE!"
    ),
    GIANT_VENUS_FLYTRAP(
        displayName = "Venus Flytrap",
        worldRangeLabel = "Levels 51–66",
        panicBubbleText = "Vegetarian option!",
        gameOverTitle = "CHOMP! PLANT FOOD!",
        gameOverDescription = "The caterpillars chewed through all 6 vine nodes!",
        extraLifeLabel = "+1 Vine Node regrown!",
        hitEffectLabel = "SNAP!"
    ),
    DINO_STEALTH(
        displayName = "Sleeping T-Rex",
        worldRangeLabel = "Levels 67–83",
        panicBubbleText = "Shhh! Don't wake Rex!",
        gameOverTitle = "ROOOAR! REX AWOKE!",
        gameOverDescription = "The Noise Meter maxed out and woke the giant T-Rex!",
        extraLifeLabel = "-1 Noise Bar hushed!",
        hitEffectLabel = "CLANG!"
    ),
    LEAKY_SUBMARINE(
        displayName = "Leaky Submarine",
        worldRangeLabel = "Levels 84–100",
        panicBubbleText = "Glub glub! Holding my breath!",
        gameOverTitle = "GLUB! TANK FLOODED!",
        gameOverDescription = "All 6 bolts popped and the glass tank filled with water!",
        extraLifeLabel = "+1 Tank Bolt sealed!",
        hitEffectLabel = "BURST!"
    );

    companion object {
        val VOLCANO_LAVA: PerilType get() = VOLCANO_BLOCKS
        val SLEEPING_TREX: PerilType get() = DINO_STEALTH

        fun forLevel(levelNumber: Int): PerilType {
            val norm = (((if (levelNumber <= 0) 1 else levelNumber) - 1) % 100) + 1
            return when (norm) {
                in 1..16 -> SHARK_BALLOONS
                in 17..33 -> UFO_ABDUCTION
                in 34..50 -> VOLCANO_BLOCKS
                in 51..66 -> GIANT_VENUS_FLYTRAP
                in 67..83 -> DINO_STEALTH
                else -> LEAKY_SUBMARINE
            }
        }
    }

    val representativeLevel: Int
        get() = when (this) {
            SHARK_BALLOONS -> 1
            UFO_ABDUCTION -> 17
            VOLCANO_BLOCKS -> 34
            GIANT_VENUS_FLYTRAP -> 51
            DINO_STEALTH -> 67
            LEAKY_SUBMARINE -> 84
        }
}

typealias PerilScenario = PerilType

data class WordLevel(
    val number: Int,
    val word: String,
    val clue: String,
    val isAdventure: Boolean = false,
    val category: String = "Doodle Challenge"
) {
    val perilType: PerilType
        get() = PerilType.forLevel(if (number <= 0) 1 else number)

    /** Every 5th level is a Boss Level with 3 bomb letters on the keyboard! */
    val isBossLevel: Boolean
        get() = isAdventure || (number > 0 && number % 5 == 0)

    /** Levels 50+ start with Frozen Vowels (A, E, I, O, U) locked until 3 consonants are guessed! */
    val hasFrozenVowels: Boolean
        get() = number >= 50
}

enum class ScreenState {
    MAIN_MENU,
    WORLD_MAP,
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

val CAMPAIGN_LEVELS: List<WordLevel> = listOf(
    // ================= WORLD 1: SHARK & BALLOONS (Levels 1–16) =================
    WordLevel(1, "DIAMOND", "Hard Science: Guess a precious gemstone", category = "Hard Science / Tech"),
    WordLevel(2, "CHINA", "World History: What country was paper invented in?", category = "World History & Geography"),
    WordLevel(3, "PRANK", "Pop Culture: April Fools' Day tradition", category = "Pop Culture & Idioms"),
    WordLevel(4, "CAKE", "Food & Desserts: Sweet baked treat with birthday candles", category = "Food & Desserts"),
    WordLevel(5, "GIRAFFE", "Boss Level: Tallest African mammal with a long neck", category = "Animals & Nature"),
    WordLevel(6, "COMPASS", "Hard Science: Magnetic tool that always points north", category = "Hard Science / Tech"),
    WordLevel(7, "VOLCANO", "World Geography: Mountain that erupts with glowing lava", category = "World History & Geography"),
    WordLevel(8, "PENGUIN", "Animals & Nature: Tuxedo-wearing bird of Antarctica", category = "Animals & Nature"),
    WordLevel(9, "RAINBOW", "Hard Science: Colorful optical arc seen after rain", category = "Hard Science / Tech"),
    WordLevel(10, "PIRATE", "Boss Level: High-seas buccaneer seeking buried gold", category = "Pop Culture & Idioms"),
    WordLevel(11, "CACTUS", "Animals & Nature: Spiky desert plant that stores water", category = "Animals & Nature"),
    WordLevel(12, "GUITAR", "Pop Culture: Six-stringed musical instrument", category = "Pop Culture & Idioms"),
    WordLevel(13, "OCTOPUS", "Animals & Nature: Clever sea creature with eight tentacles", category = "Animals & Nature"),
    WordLevel(14, "POPCORN", "Food & Desserts: Buttery snack eaten at the movie theater", category = "Food & Desserts"),
    WordLevel(15, "ASTRONAUT", "Boss Level: Explorer who travels into outer space", category = "Hard Science / Tech"),
    WordLevel(16, "IGLOO", "World Geography: Dome-shaped shelter built from snow", category = "World History & Geography"),

    // ================= WORLD 2: UFO ABDUCTION (Levels 17–33) =================
    WordLevel(17, "TORNADO", "Animals & Nature: Violently rotating column of wind", category = "Animals & Nature"),
    WordLevel(18, "PANCAKE", "Food & Desserts: Flat breakfast cake served with syrup", category = "Food & Desserts"),
    WordLevel(19, "DINOSAUR", "Animals & Nature: Prehistoric reptile that ruled Earth", category = "Animals & Nature"),
    WordLevel(20, "TREASURE", "Boss Level: Chest overflowing with gold and jewels", category = "Pop Culture & Idioms"),
    WordLevel(21, "ELEPHANT", "Animals & Nature: Largest land mammal with a trunk", category = "Animals & Nature"),
    WordLevel(22, "BURRITO", "Food & Desserts: Flour tortilla wrapped around savory fillings", category = "Food & Desserts"),
    WordLevel(23, "PYRAMIDS", "World History: Ancient monumental tombs built in Giza", category = "World History & Geography"),
    WordLevel(24, "MOONWALK", "Pop Culture: Iconic backward gliding dance move", category = "Pop Culture & Idioms"),
    WordLevel(25, "ALGORITHM", "Boss Level: Step-by-step recipe used by computers", category = "Hard Science / Tech"),
    WordLevel(26, "EGYPT", "World Geography: Land of the Nile River and Pharaohs", category = "World History & Geography"),
    WordLevel(27, "SPACESHIP", "Pop Culture: Futuristic vessel built for star travel", category = "Pop Culture & Idioms"),
    WordLevel(28, "TELESCOPE", "Hard Science: Optical instrument for viewing distant galaxies", category = "Hard Science / Tech"),
    WordLevel(29, "CHAMELEON", "Animals & Nature: Lizard famous for shifting its colors", category = "Animals & Nature"),
    WordLevel(30, "CROISSANT", "Boss Level: Flaky crescent-shaped French pastry", category = "Food & Desserts"),
    WordLevel(31, "AMAZON", "World Geography: Vast South American tropical rainforest", category = "World History & Geography"),
    WordLevel(32, "CARNIVAL", "Pop Culture: Festive traveling fair with rides and games", category = "Pop Culture & Idioms"),
    WordLevel(33, "GRAVITY", "Hard Science: Invisible force keeping planets in orbit", category = "Hard Science / Tech"),

    // ================= WORLD 3: VOLCANO LAVA (Levels 34–50) =================
    WordLevel(34, "DOLPHIN", "Animals & Nature: Playful intelligent marine mammal", category = "Animals & Nature"),
    WordLevel(35, "SPAGHETTI", "Boss Level: Long thin Italian pasta served with sauce", category = "Food & Desserts"),
    WordLevel(36, "ATLANTIS", "World History: Legendary island city lost beneath the sea", category = "World History & Geography"),
    WordLevel(37, "FIREWORKS", "Pop Culture: Dazzling sky explosions on New Year's Eve", category = "Pop Culture & Idioms"),
    WordLevel(38, "MOLECULE", "Hard Science: Group of atoms bonded together chemically", category = "Hard Science / Tech"),
    WordLevel(39, "KANGAROO", "Animals & Nature: Australian marsupial that hops", category = "Animals & Nature"),
    WordLevel(40, "PINEAPPLE", "Boss Level: Tropical fruit with a spiky crown", category = "Food & Desserts"),
    WordLevel(41, "GLACIER", "World Geography: Massive slow-moving river of ice", category = "World History & Geography"),
    WordLevel(42, "SUPERHERO", "Pop Culture: Masked comic book defender of justice", category = "Pop Culture & Idioms"),
    WordLevel(43, "SATELLITE", "Hard Science: Orbital craft relaying signals around Earth", category = "Hard Science / Tech"),
    WordLevel(44, "BUTTERFLY", "Animals & Nature: Winged insect emerging from a chrysalis", category = "Animals & Nature"),
    WordLevel(45, "CHOCOLATE", "Boss Level: Sweet treat made from roasted cacao beans", category = "Food & Desserts"),
    WordLevel(46, "COLOSSEUM", "World History: Famous oval amphitheater in Rome", category = "World History & Geography"),
    WordLevel(47, "DETECTIVE", "Pop Culture: Investigator who solves tricky mysteries", category = "Pop Culture & Idioms"),
    WordLevel(48, "MAGNETISM", "Hard Science: Force of attraction between iron poles", category = "Hard Science / Tech"),
    WordLevel(49, "FLAMINGO", "Animals & Nature: Pink wading bird that stands on one leg", category = "Animals & Nature"),
    WordLevel(50, "AVOCADO", "Boss + Frozen Vowels: Creamy green fruit in guacamole", category = "Food & Desserts"),

    // ================= WORLD 4: GIANT VENUS FLYTRAP (Levels 51–66) =================
    WordLevel(51, "HIMALAYAS", "World Geography: Mountain range home to Mount Everest", category = "World History & Geography"),
    WordLevel(52, "MAGICIAN", "Pop Culture: Illusionist who pulls rabbits from a hat", category = "Pop Culture & Idioms"),
    WordLevel(53, "ELECTRON", "Hard Science: Negatively charged subatomic particle", category = "Hard Science / Tech"),
    WordLevel(54, "CHEETAH", "Animals & Nature: Fastest spotted land cat on Earth", category = "Animals & Nature"),
    WordLevel(55, "CINNAMON", "Boss Level: Warm aromatic spice from tree bark", category = "Food & Desserts"),
    WordLevel(56, "PARTHENON", "World History: Marble temple on the Athenian Acropolis", category = "World History & Geography"),
    WordLevel(57, "SKATEBOARD", "Pop Culture: Four-wheeled deck used for kickflips", category = "Pop Culture & Idioms"),
    WordLevel(58, "SUPERNOVA", "Hard Science: Brilliant explosion of a dying star", category = "Hard Science / Tech"),
    WordLevel(59, "CROCODILE", "Animals & Nature: Armored reptile lurking in rivers", category = "Animals & Nature"),
    WordLevel(60, "MACARONI", "Boss Level: Curved elbow pasta baked with cheese", category = "Food & Desserts"),
    WordLevel(61, "CLEOPATRA", "World History: Famous last ruler of ancient Egypt", category = "World History & Geography"),
    WordLevel(62, "SAXOPHONE", "Pop Culture: Brass woodwind instrument iconic in jazz", category = "Pop Culture & Idioms"),
    WordLevel(63, "QUANTUM", "Hard Science: Physics of the smallest discrete energy packets", category = "Hard Science / Tech"),
    WordLevel(64, "JELLYFISH", "Animals & Nature: Translucent ocean drifter with tentacles", category = "Animals & Nature"),
    WordLevel(65, "BLUEBERRY", "Boss Level: Small round indigo fruit in muffins", category = "Food & Desserts"),
    WordLevel(66, "SAMURAI", "World History: Noble armored warrior of feudal Japan", category = "World History & Geography"),

    // ================= WORLD 5: SLEEPING T-REX (Levels 67–83) =================
    WordLevel(67, "MARATHON", "Pop Culture: Endurance footrace of 26.2 miles", category = "Pop Culture & Idioms"),
    WordLevel(68, "ROBOTICS", "Hard Science: Engineering of automated machines", category = "Hard Science / Tech"),
    WordLevel(69, "PLATYPUS", "Animals & Nature: Duck-billed mammal native to Australia", category = "Animals & Nature"),
    WordLevel(70, "ESPRESSO", "Boss Level: Concentrated Italian brewed coffee shot", category = "Food & Desserts"),
    WordLevel(71, "VIKINGS", "World History: Norse seafarers who sailed longships", category = "World History & Geography"),
    WordLevel(72, "ORCHESTRA", "Pop Culture: Large ensemble of classical musicians", category = "Pop Culture & Idioms"),
    WordLevel(73, "MICROCHIP", "Hard Science: Tiny silicon circuit inside every phone", category = "Hard Science / Tech"),
    WordLevel(74, "PORCUPINE", "Animals & Nature: Rodent protected by sharp quills", category = "Animals & Nature"),
    WordLevel(75, "WATERMELON", "Boss Level: Giant green picnic fruit with red flesh", category = "Food & Desserts"),
    WordLevel(76, "STONEHENGE", "World History: Prehistoric ring of standing stones", category = "World History & Geography"),
    WordLevel(77, "SPOTLIGHT", "Pop Culture: Bright stage beam following the star", category = "Pop Culture & Idioms"),
    WordLevel(78, "CHROMOSOME", "Hard Science: Thread-like structure carrying DNA genes", category = "Hard Science / Tech"),
    WordLevel(79, "CHIMPANZEE", "Animals & Nature: Clever great ape known to use tools", category = "Animals & Nature"),
    WordLevel(80, "CHEESECAKE", "Boss Level: Rich creamy dessert on a graham crust", category = "Food & Desserts"),
    WordLevel(81, "GALAPAGOS", "World Geography: Pacific islands that inspired Darwin", category = "World History & Geography"),
    WordLevel(82, "BOOMERANG", "Pop Culture: Curved throwing stick that returns to you", category = "Pop Culture & Idioms"),
    WordLevel(83, "FREQUENCY", "Hard Science: Number of wave cycles per second in Hertz", category = "Hard Science / Tech"),

    // ================= WORLD 6: LEAKY SUBMARINE (Levels 84–100) =================
    WordLevel(84, "RHINOCEROS", "Animals & Nature: Heavy herbivore with a nasal horn", category = "Animals & Nature"),
    WordLevel(85, "PISTACHIO", "Boss Level: Green nut inside a naturally splitting shell", category = "Food & Desserts"),
    WordLevel(86, "ANTARCTICA", "World Geography: Frozen southernmost continent", category = "World History & Geography"),
    WordLevel(87, "JACKPOT", "Pop Culture: Biggest grand prize won in a game", category = "Pop Culture & Idioms"),
    WordLevel(88, "ASTEROID", "Hard Science: Rocky minor planet orbiting the sun", category = "Hard Science / Tech"),
    WordLevel(89, "WOLVERINE", "Animals & Nature: Fierce northern mammal of the weasel family", category = "Animals & Nature"),
    WordLevel(90, "QUESADILLA", "Boss Level: Toasted tortilla filled with melted cheese", category = "Food & Desserts"),
    WordLevel(91, "LABYRINTH", "World History: Mythological Cretan maze of the Minotaur", category = "World History & Geography"),
    WordLevel(92, "ACROBATICS", "Pop Culture: High-flying trapeze stunts at the circus", category = "Pop Culture & Idioms"),
    WordLevel(93, "HOLOGRAM", "Hard Science: Three-dimensional projection made with lasers", category = "Hard Science / Tech"),
    WordLevel(94, "ALBATROSS", "Animals & Nature: Giant seabird with a massive wingspan", category = "Animals & Nature"),
    WordLevel(95, "GUACAMOLE", "Boss Level: Zesty Mexican dip mashed with lime", category = "Food & Desserts"),
    WordLevel(96, "MESOPOTAMIA", "World History: Cradle of civilization between two rivers", category = "World History & Geography"),
    WordLevel(97, "CLIFFHANGER", "Pop Culture: Suspenseful ending that leaves you waiting", category = "Pop Culture & Idioms"),
    WordLevel(98, "EXOPLANET", "Hard Science: World orbiting a star outside our solar system", category = "Hard Science / Tech"),
    WordLevel(99, "CAPPUCCINO", "Food & Desserts: Espresso topped with steamed milk foam", category = "Food & Desserts"),
    WordLevel(100, "CYBERSPACE", "Final Boss (100): Global digital universe of computer networks", category = "Hard Science / Tech")
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
// 3. GAME VIEWMODEL (100 LEVELS, 6 PERILS, FROZEN VOWELS, WORLD MAP, PERSISTENCE)
// ============================================================================

class WordGameViewModel : ViewModel() {
    companion object {
        private const val PREFS_NAME = "doodle_word_shark_prefs"
        val VOWELS = setOf('A', 'E', 'I', 'O', 'U')
    }

    private var appContext: Context? = null

    var currentScreen by mutableStateOf(ScreenState.MAIN_MENU)
        private set

    // Player economy & 100-level progression
    var coins by mutableIntStateOf(1231)
        private set
    var campaignLevelIndex by mutableIntStateOf(0)
        private set
    var adventureLevelIndex by mutableIntStateOf(0)
        private set
    val completedCampaignLevels = mutableStateListOf<Int>()
    val levelStarRatings = mutableStateMapOf<Int, Int>() // levelNumber -> 1..3 stars
    val claimedMilestoneChests = mutableStateListOf<Int>() // 10, 20, ..., 100
    var unlockAllMapLevels by mutableStateOf(false)
        private set

    val totalCampaignCount: Int
        get() = CAMPAIGN_LEVELS.size

    val completedCampaignCount: Int
        get() = completedCampaignLevels.size

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

    val revealedWordChars: List<Char>
        get() = revealedChars

    val currentCampaignLevelNumber: Int
        get() = campaignLevelIndex + 1

    val activePerilType: PerilType
        get() = activeLevel.perilType

    /** Required consonants to thaw Frozen Vowels on Levels 50+ (capped at unique consonants in word) */
    val requiredConsonantsToThaw: Int
        get() {
            if (!activeLevel.hasFrozenVowels) return 0
            val uniqueConsonants = activeLevel.word.uppercase().filter { it in 'A'..'Z' && it !in VOWELS }.toSet()
            return minOf(3, uniqueConsonants.size)
        }

    /** Number of correct consonants guessed so far in the current round */
    val guessedConsonantCount: Int
        get() = revealedChars.count { it !in VOWELS }

    /** True when Levels 50+ still have their vowels locked in doodle ice cubes */
    val areVowelsFrozen: Boolean
        get() = activeLevel.hasFrozenVowels && guessedConsonantCount < requiredConsonantsToThaw

    fun isKeyFrozenVowel(ch: Char): Boolean {
        val upper = ch.uppercaseChar()
        return areVowelsFrozen && upper in VOWELS && (keyStatuses[upper] == null || keyStatuses[upper] == KeyStatus.DEFAULT)
    }

    fun initPersistence(context: Context) {
        if (appContext != null) return
        appContext = context.applicationContext
        val prefs = appContext?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) ?: return
        coins = prefs.getInt("coins", 1231)
        campaignLevelIndex = prefs.getInt("campaignLevelIndex", 0).coerceIn(0, CAMPAIGN_LEVELS.lastIndex)
        perfectCount = prefs.getInt("perfectCount", 3)
        currentStreak = prefs.getInt("currentStreak", 2)
        maxStreak = prefs.getInt("maxStreak", 5)
        chestProgress = prefs.getInt("chestProgress", 1)
        themeProgress = prefs.getInt("themeProgress", 1)
        freeShieldPotions = prefs.getInt("freeShieldPotions", 2)

        val completedRaw = prefs.getString("completedLevels", "") ?: ""
        completedCampaignLevels.clear()
        completedRaw.split(",").mapNotNull { it.trim().toIntOrNull() }.forEach { lvl ->
            if (!completedCampaignLevels.contains(lvl)) completedCampaignLevels.add(lvl)
        }

        val starsRaw = prefs.getString("levelStars", "") ?: ""
        levelStarRatings.clear()
        starsRaw.split(",").forEach { pair ->
            val parts = pair.split(":")
            if (parts.size == 2) {
                val lvl = parts[0].toIntOrNull()
                val st = parts[1].toIntOrNull()
                if (lvl != null && st != null) levelStarRatings[lvl] = st
            }
        }

        val chestsRaw = prefs.getString("claimedChests", "") ?: ""
        claimedMilestoneChests.clear()
        chestsRaw.split(",").mapNotNull { it.trim().toIntOrNull() }.forEach { c ->
            if (!claimedMilestoneChests.contains(c)) claimedMilestoneChests.add(c)
        }
    }

    private fun saveStateToPrefs() {
        val prefs = appContext?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) ?: return
        prefs.edit()
            .putInt("coins", coins)
            .putInt("campaignLevelIndex", campaignLevelIndex)
            .putInt("perfectCount", perfectCount)
            .putInt("currentStreak", currentStreak)
            .putInt("maxStreak", maxStreak)
            .putInt("chestProgress", chestProgress)
            .putInt("themeProgress", themeProgress)
            .putInt("freeShieldPotions", freeShieldPotions)
            .putString("completedLevels", completedCampaignLevels.joinToString(","))
            .putString("levelStars", levelStarRatings.entries.joinToString(",") { "${it.key}:${it.value}" })
            .putString("claimedChests", claimedMilestoneChests.joinToString(","))
            .apply()
    }

    fun openWorldMap() {
        currentScreen = ScreenState.WORLD_MAP
    }

    fun toggleUnlockAllMapLevels() {
        unlockAllMapLevels = !unlockAllMapLevels
        statusBannerMessage = if (unlockAllMapLevels) {
            "🔓 All 100 Levels Unlocked for Free Play!"
        } else {
            "🔒 Sequential Campaign Progression Active"
        }
    }

    fun isLevelUnlocked(levelNumber: Int): Boolean {
        if (unlockAllMapLevels) return true
        // Level 1, the start of each Peril World (1, 17, 34, 51, 67, 84), completed levels, or next level after highest completed
        if (levelNumber == 1 || levelNumber in setOf(17, 34, 51, 67, 84)) return true
        if (completedCampaignLevels.contains(levelNumber)) return true
        if (completedCampaignLevels.contains(levelNumber - 1)) return true
        val highestCompleted = completedCampaignLevels.maxOrNull() ?: 0
        return levelNumber <= (highestCompleted + 1).coerceAtLeast(currentCampaignLevelNumber)
    }

    fun claimMilestoneChest(milestoneLevel: Int) {
        if (claimedMilestoneChests.contains(milestoneLevel)) {
            statusBannerMessage = "Level $milestoneLevel Chest already claimed!"
            return
        }
        claimedMilestoneChests.add(milestoneLevel)
        coins += 100
        ProceduralSoundEngine.play(DoodleSoundEffect.VICTORY)
        statusBannerMessage = "🧰 Level $milestoneLevel Milestone Chest! +100 Coins!"
        saveStateToPrefs()
    }

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

    /** Allows jumping directly to a specific level number (great for World Map & testing perils) */
    fun selectCampaignLevelNumber(levelNumber: Int) {
        campaignLevelIndex = (levelNumber - 1).coerceIn(0, CAMPAIGN_LEVELS.lastIndex)
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

        // If Boss Level (every 5th level or Adventure), pick 3 wrong letters as bomb keys
        if (level.isBossLevel) {
            val targetSet = level.word.uppercase().toSet()
            val wrongPool = ('A'..'Z').filter { !targetSet.contains(it) && (!level.hasFrozenVowels || it !in VOWELS) }
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

        // Frozen Vowels Check (Levels 50+): Vowels cannot be pressed until 3 consonants are guessed!
        if (isKeyFrozenVowel(upper)) {
            val remaining = (requiredConsonantsToThaw - guessedConsonantCount).coerceAtLeast(1)
            statusBannerMessage = "❄️ Vowel '$upper' is FROZEN! Guess $remaining more consonant(s) to thaw!"
            return
        }

        val targetWord = activeLevel.word.uppercase()
        if (targetWord.contains(upper)) {
            val wasFrozenBefore = areVowelsFrozen
            keyStatuses[upper] = KeyStatus.CORRECT
            if (!revealedChars.contains(upper)) {
                revealedChars.add(upper)
            }
            if (wasFrozenBefore && !areVowelsFrozen) {
                statusBannerMessage = "💧 Ice shattered! Vowels (A, E, I, O, U) are now THAWED!"
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
                val gameOverSound = when (activePerilType) {
                    PerilType.SHARK_BALLOONS -> DoodleSoundEffect.SPLASH
                    PerilType.VOLCANO_BLOCKS -> DoodleSoundEffect.CRUNCH
                    PerilType.UFO_ABDUCTION -> DoodleSoundEffect.ZAP
                    PerilType.GIANT_VENUS_FLYTRAP -> DoodleSoundEffect.CRUNCH
                    PerilType.DINO_STEALTH -> DoodleSoundEffect.CLANG
                    PerilType.LEAKY_SUBMARINE -> DoodleSoundEffect.SPLASH
                }
                ProceduralSoundEngine.play(gameOverSound)
                saveStateToPrefs()
            } else {
                val hitSound = when (activePerilType) {
                    PerilType.SHARK_BALLOONS -> DoodleSoundEffect.POP
                    PerilType.VOLCANO_BLOCKS -> DoodleSoundEffect.CRUNCH
                    PerilType.UFO_ABDUCTION -> DoodleSoundEffect.ZAP
                    PerilType.GIANT_VENUS_FLYTRAP -> DoodleSoundEffect.POP
                    PerilType.DINO_STEALTH -> DoodleSoundEffect.CLANG
                    PerilType.LEAKY_SUBMARINE -> DoodleSoundEffect.POP
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
            if (!activeLevel.isAdventure && activeLevel.number > 0) {
                val normalizedLevel = ((activeLevel.number - 1) % CAMPAIGN_LEVELS.size) + 1
                if (!completedCampaignLevels.contains(normalizedLevel)) {
                    completedCampaignLevels.add(normalizedLevel)
                }
                val earnedStars = when {
                    balloonsRemaining >= 5 -> 3
                    balloonsRemaining >= 3 -> 2
                    else -> 1
                }
                val prevStars = levelStarRatings[normalizedLevel] ?: 0
                if (earnedStars > prevStars) {
                    levelStarRatings[normalizedLevel] = earnedStars
                }
            }
            chestProgress = (chestProgress % 4) + 1
            themeProgress = (themeProgress % 10) + 1
            roundOutcome = RoundOutcome.VICTORY
            ProceduralSoundEngine.play(DoodleSoundEffect.VICTORY)
            saveStateToPrefs()
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
        // Prefer revealing a consonant if vowels are currently frozen so it helps thaw them too!
        val consonantCandidates = unrevealed.filter { it !in VOWELS }
        val picked = if (areVowelsFrozen && consonantCandidates.isNotEmpty()) {
            consonantCandidates.random()
        } else {
            unrevealed.random()
        }
        keyStatuses[picked] = KeyStatus.CORRECT
        revealedChars.add(picked)
        statusBannerMessage = "Magnifier revealed '$picked'!"
        saveStateToPrefs()
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
            saveStateToPrefs()
        } else if (coins >= cost) {
            coins -= cost
            balloonsRemaining++
            statusBannerMessage = activePerilType.extraLifeLabel
            saveStateToPrefs()
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
        saveStateToPrefs()
    }

    fun proceedAfterVictory() {
        val reward = if (activeLevel.isBossLevel) 50 else 25
        val chestBonus = if (chestProgress == 4) 100 else 0
        coins += reward + chestBonus

        if (activeLevel.isAdventure) {
            adventureLevelIndex++
            startAdventureLevel()
        } else {
            campaignLevelIndex = (campaignLevelIndex + 1) % CAMPAIGN_LEVELS.size
            saveStateToPrefs()
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
            saveStateToPrefs()
        } else {
            statusBannerMessage = "Need $rescueCost coins! Tap + at top right."
        }
    }

    fun claimFreeCoinBonus(amount: Int = 150) {
        coins += amount
        statusBannerMessage = "+$amount Doodle Coins added!"
        saveStateToPrefs()
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
            val vm: WordGameViewModel = viewModel()
            LaunchedEffect(Unit) {
                vm.initPersistence(applicationContext)
            }
            MaterialTheme {
                WordSharkDoodleApp(viewModel = vm)
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
                        completedLevels = viewModel.completedCampaignCount,
                        totalCampaignLevels = viewModel.totalCampaignCount,
                        coins = viewModel.coins,
                        fontFamily = activeFontFamily,
                        onPlayCampaign = { viewModel.openWorldMap() },
                        onPlayAdventure = { viewModel.startAdventureLevel() },
                        onPlayTwoPlayer = { viewModel.openTwoPlayerSetup() },
                        onSelectPerilLevel = { lvl -> viewModel.selectCampaignLevelNumber(lvl) },
                        onOpenSettings = { viewModel.showSettingsDialog = true },
                        onOpenCoinShop = { viewModel.showCoinShopDialog = true }
                    )
                }

                ScreenState.WORLD_MAP -> {
                    BackHandler { viewModel.navigateToMainMenu() }
                    WorldMapScreen(
                        viewModel = viewModel,
                        fontFamily = activeFontFamily,
                        onBack = { viewModel.navigateToMainMenu() },
                        onSelectLevel = { lvl -> viewModel.selectCampaignLevelNumber(lvl) }
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
    completedLevels: Int,
    totalCampaignLevels: Int,
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

            Spacer(modifier = Modifier.height(4.dp))

            DoodleWordTitleHeader(fontFamily = fontFamily)
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Visual Indicator showing completed levels out of total campaign count
        CampaignProgressIndicatorCard(
            completedLevels = completedLevels,
            totalLevels = totalCampaignLevels,
            fontFamily = fontFamily,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 360.dp)
                .padding(bottom = 8.dp)
        )

        // Bottom Stacked Buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 360.dp)
                .padding(bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
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

            // 2. Two Player (White fill)
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
fun CampaignProgressIndicatorCard(
    completedLevels: Int,
    totalLevels: Int,
    fontFamily: FontFamily,
    modifier: Modifier = Modifier
) {
    val safeTotal = totalLevels.coerceAtLeast(1)
    val clampedCompleted = completedLevels.coerceIn(0, safeTotal)
    val animatedFraction by animateFloatAsState(
        targetValue = clampedCompleted.toFloat() / safeTotal.toFloat(),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "campaignProgressFraction"
    )

    SketchyContainer(
        fillColor = Color.White,
        cornerRadius = 18.dp,
        strokeWidth = 2.dp,
        shadowOffset = 2.5.dp,
        modifier = modifier
            .testTag("campaign_progress_indicator")
            .semantics {
                contentDescription = "Campaign progress: $clampedCompleted of $safeTotal levels completed"
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Canvas(modifier = Modifier.size(18.dp)) {
                        drawDoodleStarCoin()
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Campaign Progress",
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        color = DoodleColors.InkBlack
                    )
                }

                Text(
                    text = "$clampedCompleted / $safeTotal Completed",
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = DoodleColors.InkBlue,
                    modifier = Modifier.testTag("campaign_progress_text")
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Hand-sketched progress bar track & fill
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val r = size.height * 0.5f
                    // Track background
                    drawRoundRect(
                        color = DoodleColors.PaperOffWhite,
                        cornerRadius = CornerRadius(r, r)
                    )
                    // Completed progress fill
                    if (animatedFraction > 0f) {
                        val fillW = (size.width * animatedFraction).coerceAtLeast(size.height)
                        drawRoundRect(
                            color = DoodleColors.CorrectGreen,
                            size = Size(fillW.coerceAtMost(size.width), size.height),
                            cornerRadius = CornerRadius(r, r)
                        )
                    }
                    // Sketchy black border
                    drawRoundRect(
                        color = DoodleColors.InkBlack,
                        cornerRadius = CornerRadius(r, r),
                        style = Stroke(width = 1.8.dp.toPx())
                    )
                }
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
// 6B. SCREEN 1B: 100-LEVEL BOARD GAME WORLD MAP & LEVEL SELECT UI
// ============================================================================

private data class WorldChapterInfo(
    val worldNumber: Int,
    val title: String,
    val levelRange: IntRange,
    val perilType: PerilType,
    val bannerColor: Color
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WorldMapScreen(
    viewModel: WordGameViewModel,
    fontFamily: FontFamily,
    onBack: () -> Unit,
    onSelectLevel: (Int) -> Unit
) {
    val chapters = remember {
        listOf(
            WorldChapterInfo(1, "World 1: Shark Lagoon", 1..16, PerilType.SHARK_BALLOONS, Color(0xFFE1F5FE)),
            WorldChapterInfo(2, "World 2: Alien Pasture", 17..33, PerilType.UFO_ABDUCTION, Color(0xFFE8F5E9)),
            WorldChapterInfo(3, "World 3: Magma Ridge", 34..50, PerilType.VOLCANO_BLOCKS, Color(0xFFFFF3E0)),
            WorldChapterInfo(4, "World 4: Flytrap Jungle", 51..66, PerilType.GIANT_VENUS_FLYTRAP, Color(0xFFF1F8E9)),
            WorldChapterInfo(5, "World 5: Rex Canyon", 67..83, PerilType.DINO_STEALTH, Color(0xFFF9FBE7)),
            WorldChapterInfo(6, "World 6: Abyssal Sub", 84..100, PerilType.LEAKY_SUBMARINE, Color(0xFFE0F7FA))
        )
    }

    val totalStars = viewModel.levelStarRatings.values.sum()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .widthIn(max = 650.dp)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SketchyIconButton(
                testTag = "world_map_back_button",
                contentDescription = "Back to main menu",
                onClick = onBack
            ) {
                DrawDoodleBackArrow()
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "100-Level World Map",
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    color = DoodleColors.InkBlack,
                    modifier = Modifier.testTag("world_map_title")
                )
                Text(
                    text = "⭐ $totalStars / 300 Stars • ${viewModel.completedCampaignCount}/100 Beaten",
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp,
                    color = DoodleColors.InkBlue
                )
            }

            // Free Play Unlock Toggle so player can test any of the 100 levels or play sequentially
            SketchyContainer(
                fillColor = if (viewModel.unlockAllMapLevels) DoodleColors.CorrectGreen.copy(alpha = 0.25f) else Color.White,
                cornerRadius = 14.dp,
                strokeWidth = 1.7.dp,
                onClick = { viewModel.toggleUnlockAllMapLevels() },
                modifier = Modifier
                    .height(40.dp)
                    .testTag("toggle_free_play_map_button")
            ) {
                Text(
                    text = if (viewModel.unlockAllMapLevels) "🔓 All Open" else "🔒 Campaign",
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    color = DoodleColors.InkBlack,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Primary "Play Current Level X" Banner Button
        val currentLvl = viewModel.currentCampaignLevelNumber
        val currentPeril = PerilType.forLevel(currentLvl)
        SketchyContainer(
            fillColor = DoodleColors.DoodleYellow,
            cornerRadius = 22.dp,
            onClick = { onSelectLevel(currentLvl) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("map_play_current_level_button")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "▶ Play Level $currentLvl (${currentPeril.displayName})",
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp,
                    color = DoodleColors.InkBlack
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Scrollable Board Game Map of 100 Levels across 6 Peril Worlds
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .testTag("world_map_lazy_column"),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(chapters.size) { chapterIdx ->
                val chapter = chapters[chapterIdx]
                SketchyContainer(
                    fillColor = chapter.bannerColor,
                    cornerRadius = 22.dp,
                    strokeWidth = 2.dp,
                    shadowOffset = 3.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Chapter Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = chapter.title,
                                    fontFamily = fontFamily,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    color = DoodleColors.InkBlack
                                )
                                Text(
                                    text = "Levels ${chapter.levelRange.first}–${chapter.levelRange.last} • Hazard: ${chapter.perilType.displayName}" +
                                        if (chapter.levelRange.last >= 50) " • 🧊 Frozen Vowels (50+)" else "",
                                    fontFamily = fontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = DoodleColors.InkBlue
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Winding Board-Game Level Nodes Grid
                        val levelsInChapter = chapter.levelRange.toList()
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            levelsInChapter.forEach { lvlNum ->
                                val isUnlocked = viewModel.isLevelUnlocked(lvlNum)
                                val isCompleted = viewModel.completedCampaignLevels.contains(lvlNum)
                                val isCurrent = lvlNum == viewModel.currentCampaignLevelNumber
                                val stars = viewModel.levelStarRatings[lvlNum] ?: 0
                                val isBoss = lvlNum % 5 == 0
                                val isFrozenVowelLvl = lvlNum >= 50

                                BoardGameLevelNode(
                                    levelNumber = lvlNum,
                                    isUnlocked = isUnlocked,
                                    isCompleted = isCompleted,
                                    isCurrent = isCurrent,
                                    stars = stars,
                                    isBoss = isBoss,
                                    hasFrozenVowels = isFrozenVowelLvl,
                                    fontFamily = fontFamily,
                                    onClick = {
                                        if (isUnlocked) {
                                            onSelectLevel(lvlNum)
                                        } else {
                                            viewModel.statusBannerMessage =
                                                "🔒 Complete Level ${lvlNum - 1} first (or tap '🔒 Campaign' at top-right)!"
                                        }
                                    }
                                )

                                // Milestone Chest Icon every 10 levels (10, 20, 30, ..., 100)
                                if (lvlNum % 10 == 0) {
                                    val canClaim = isCompleted || viewModel.completedCampaignCount >= lvlNum / 2
                                    val alreadyClaimed = viewModel.claimedMilestoneChests.contains(lvlNum)
                                    MilestoneChestNode(
                                        milestoneLevel = lvlNum,
                                        isUnlocked = canClaim,
                                        isClaimed = alreadyClaimed,
                                        fontFamily = fontFamily,
                                        onClick = {
                                            if (alreadyClaimed) {
                                                viewModel.statusBannerMessage = "Level $lvlNum Chest already opened!"
                                            } else if (canClaim || viewModel.unlockAllMapLevels) {
                                                viewModel.claimMilestoneChest(lvlNum)
                                            } else {
                                                viewModel.statusBannerMessage = "Beat Level $lvlNum to open this +100 Coin Chest!"
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BoardGameLevelNode(
    levelNumber: Int,
    isUnlocked: Boolean,
    isCompleted: Boolean,
    isCurrent: Boolean,
    stars: Int,
    isBoss: Boolean,
    hasFrozenVowels: Boolean,
    fontFamily: FontFamily,
    onClick: () -> Unit
) {
    val nodeFill = when {
        isCurrent -> DoodleColors.DoodleYellow
        isCompleted -> Color(0xFFC8E6C9)
        isUnlocked -> Color.White
        else -> Color(0xFFE0E0E0)
    }
    val nodeBorder = when {
        isBoss && isUnlocked -> DoodleColors.WrongRed
        isCurrent -> DoodleColors.InkBlue
        else -> DoodleColors.InkBlack
    }

    SketchyContainer(
        fillColor = nodeFill,
        borderColor = nodeBorder,
        cornerRadius = 14.dp,
        strokeWidth = if (isCurrent || isBoss) 2.2.dp else 1.7.dp,
        shadowOffset = 2.dp,
        onClick = onClick,
        modifier = Modifier
            .size(width = 62.dp, height = 64.dp)
            .testTag("map_level_node_$levelNumber")
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 4.dp, horizontal = 3.dp)
        ) {
            // Top badge row: Padlock icon + Boss / Ice indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Canvas(modifier = Modifier.size(11.dp)) {
                    drawDoodlePadlock(unlocked = isUnlocked)
                }
                if (isBoss) {
                    Text(
                        text = "💣",
                        fontSize = 9.sp
                    )
                } else if (hasFrozenVowels) {
                    Text(
                        text = "🧊",
                        fontSize = 9.sp
                    )
                } else {
                    Spacer(modifier = Modifier.size(9.dp))
                }
            }

            // Level Number
            Text(
                text = "$levelNumber",
                fontFamily = fontFamily,
                fontWeight = FontWeight.Black,
                fontSize = 16.sp,
                color = if (isUnlocked) DoodleColors.InkBlack else DoodleColors.LockedSlate
            )

            // Bottom 3-Star Rating Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (s in 1..3) {
                    val filled = s <= stars
                    Canvas(modifier = Modifier.size(10.dp)) {
                        drawDoodleMiniStar(filled = filled)
                    }
                }
            }
        }
    }
}

@Composable
private fun MilestoneChestNode(
    milestoneLevel: Int,
    isUnlocked: Boolean,
    isClaimed: Boolean,
    fontFamily: FontFamily,
    onClick: () -> Unit
) {
    SketchyContainer(
        fillColor = when {
            isClaimed -> Color(0xFFFFECB3)
            isUnlocked -> DoodleColors.DoodleYellow
            else -> Color(0xFFEEEEEE)
        },
        borderColor = DoodleColors.HintBulbOrange,
        cornerRadius = 14.dp,
        strokeWidth = 2.dp,
        onClick = onClick,
        modifier = Modifier
            .size(width = 66.dp, height = 64.dp)
            .testTag("map_chest_node_$milestoneLevel")
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(2.dp)
        ) {
            Box(modifier = Modifier.size(26.dp)) {
                DrawDoodleChestIcon()
            }
            Text(
                text = if (isClaimed) "OPENED" else "L$milestoneLevel 🧰",
                fontFamily = fontFamily,
                fontWeight = FontWeight.Black,
                fontSize = 10.sp,
                color = DoodleColors.InkBlack
            )
            Text(
                text = "+100🪙",
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                color = DoodleColors.InkBlue
            )
        }
    }
}

private fun DrawScope.drawDoodlePadlock(unlocked: Boolean) {
    val w = size.width
    val h = size.height
    // Shackle arch
    val shacklePath = Path().apply {
        moveTo(w * 0.25f, h * 0.48f)
        lineTo(w * 0.25f, h * 0.26f)
        quadraticTo(w * 0.5f, h * 0.04f, w * 0.75f, h * 0.26f)
        if (!unlocked) {
            lineTo(w * 0.75f, h * 0.48f)
        }
    }
    drawPath(
        path = shacklePath,
        color = if (unlocked) DoodleColors.CorrectGreen else DoodleColors.LockedSlate,
        style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round)
    )
    // Lock body
    drawRoundRect(
        color = if (unlocked) DoodleColors.DoodleYellow else DoodleColors.LockedSlate,
        topLeft = Offset(w * 0.16f, h * 0.46f),
        size = Size(w * 0.68f, h * 0.48f),
        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
    )
    drawRoundRect(
        color = DoodleColors.InkBlack,
        topLeft = Offset(w * 0.16f, h * 0.46f),
        size = Size(w * 0.68f, h * 0.48f),
        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
        style = Stroke(width = 1.2.dp.toPx())
    )
}

private fun DrawScope.drawDoodleMiniStar(filled: Boolean) {
    val cx = size.width * 0.5f
    val cy = size.height * 0.5f
    val outerR = size.minDimension * 0.46f
    val innerR = outerR * 0.45f
    val starPath = Path().apply {
        for (i in 0 until 10) {
            val r = if (i % 2 == 0) outerR else innerR
            val angle = (i * 36.0 - 90.0) * (PI / 180.0)
            val px = cx + (r * cos(angle)).toFloat()
            val py = cy + (r * sin(angle)).toFloat()
            if (i == 0) moveTo(px, py) else lineTo(px, py)
        }
        close()
    }
    drawPath(
        path = starPath,
        color = if (filled) DoodleColors.DoodleYellow else Color.White.copy(alpha = 0.6f)
    )
    drawPath(
        path = starPath,
        color = DoodleColors.InkBlack,
        style = Stroke(width = 1.dp.toPx(), join = StrokeJoin.Round)
    )
}

// ============================================================================
// 7. SCREEN 2: GAMEPLAY SCREEN (6 DYNAMIC PERILS + BOSS BOMBS + FROZEN VOWELS)
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
                    text = buildString {
                        append(perilType.displayName)
                        if (level.isBossLevel) append(" • 💣 BOSS (-2 ❤️)")
                        if (level.hasFrozenVowels) append(" • 🧊 ICE VOWELS")
                    },
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
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
        // Left Half: Custom Compose Canvas rendering the active PerilType (all 6 perils)
        // Right Half: Category pill + Clue/Category prompt + Boss & Frozen Vowel badges
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

            // Right Half: Clue / Category prompt + Lives + Boss Bomb + Frozen Vowels indicator
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
                        modifier = Modifier.padding(bottom = 6.dp)
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

                    // Category Badge
                    SketchyContainer(
                        fillColor = Color(0xFFFFF9C4),
                        cornerRadius = 10.dp,
                        strokeWidth = 1.4.dp,
                        shadowOffset = 1.5.dp,
                        modifier = Modifier
                            .padding(bottom = 5.dp)
                            .testTag("category_badge")
                    ) {
                        Text(
                            text = "📂 ${level.category}",
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 10.sp,
                            color = DoodleColors.InkBlack,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    if (level.isBossLevel) {
                        SketchyContainer(
                            fillColor = Color(0xFFFFEBEE),
                            borderColor = DoodleColors.WrongRed,
                            cornerRadius = 12.dp,
                            strokeWidth = 1.6.dp,
                            shadowOffset = 1.5.dp,
                            modifier = Modifier
                                .padding(bottom = 5.dp)
                                .testTag("boss_warning_badge")
                        ) {
                            Text(
                                text = "💣 BOSS: 3 Bomb Keys = -2 ❤️",
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp,
                                color = DoodleColors.WrongRed,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    if (level.hasFrozenVowels) {
                        val isFrozen = viewModel.areVowelsFrozen
                        SketchyContainer(
                            fillColor = if (isFrozen) DoodleColors.IceCubeCyan.copy(alpha = 0.45f) else Color(0xFFE8F5E9),
                            borderColor = if (isFrozen) DoodleColors.IceCubeBorder else DoodleColors.CorrectGreen,
                            cornerRadius = 12.dp,
                            strokeWidth = 1.6.dp,
                            shadowOffset = 1.5.dp,
                            modifier = Modifier
                                .padding(bottom = 5.dp)
                                .testTag("frozen_vowels_badge")
                        ) {
                            Text(
                                text = if (isFrozen) {
                                    "🧊 Vowels Frozen (${viewModel.guessedConsonantCount}/${viewModel.requiredConsonantsToThaw} Consonants)"
                                } else {
                                    "🔥 Vowels Thawed!"
                                },
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp,
                                color = DoodleColors.InkBlack,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Text(
                        text = level.clue,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        lineHeight = 23.sp,
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
                .padding(vertical = 8.dp),
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

        // 4. Keyboard Area: Custom 4-row alphabetical grid (with Boss Bomb Keys & Frozen Vowels Ice Cubes!)
        DoodleAlphabetKeyboard(
            keyStatuses = viewModel.keyStatuses,
            bombLetters = viewModel.bombLetters.toSet(),
            areVowelsFrozen = viewModel.areVowelsFrozen,
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
// 8. CUSTOM 4-ROW ALPHABETICAL KEYBOARD (WITH BOSS BOMBS & FROZEN VOWEL ICE CUBES)
// ============================================================================

@Composable
fun DoodleAlphabetKeyboard(
    keyStatuses: Map<Char, KeyStatus>,
    bombLetters: Set<Char>,
    areVowelsFrozen: Boolean = false,
    fontFamily: FontFamily,
    onKeyPress: (Char) -> Unit
) {
    val rows = listOf(
        listOf('A', 'B', 'C', 'D', 'E', 'F', 'G'),
        listOf('H', 'I', 'J', 'K', 'L', 'M', 'N'),
        listOf('O', 'P', 'Q', 'R', 'S', 'T', 'U'),
        listOf('V', 'W', 'X', 'Y', 'Z')
    )
    val vowels = setOf('A', 'E', 'I', 'O', 'U')

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        // Dynamically adapt key size to fit all screen widths (from 320dp compact phones to tablets)
        val computedKeySize = ((maxWidth - 42.dp) / 7f).coerceIn(38.dp, 50.dp)
        val rowSpacing = if (computedKeySize < 44.dp) 3.dp else 5.dp

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(rowSpacing),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            rows.forEach { rowChars ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    rowChars.forEach { ch ->
                        val status = keyStatuses[ch] ?: KeyStatus.DEFAULT
                        val isBomb = bombLetters.contains(ch)
                        val isFrozenVowel = areVowelsFrozen && (ch in vowels) && status == KeyStatus.DEFAULT
                        DoodleLetterKey(
                            letter = ch,
                            status = status,
                            hasBombDot = isBomb,
                            isFrozen = isFrozenVowel,
                            keySize = computedKeySize,
                            fontFamily = fontFamily,
                            onClick = { onKeyPress(ch) }
                        )
                    }
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
    isFrozen: Boolean = false,
    keySize: Dp = 48.dp,
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
            .size(keySize)
            .clip(CircleShape)
            .clickable(
                enabled = status == KeyStatus.DEFAULT,
                onClick = onClick
            )
            .testTag("key_$letter")
            .semantics {
                contentDescription = when {
                    isFrozen -> "Letter $letter Frozen Vowel"
                    hasBombDot -> "Letter $letter Bomb Key"
                    else -> "Letter $letter"
                }
            }
    ) {
        // Letter Text
        Text(
            text = letter.toString(),
            fontFamily = fontFamily,
            fontWeight = FontWeight.Black,
            fontSize = 25.sp,
            color = when {
                status == KeyStatus.CORRECT -> DoodleColors.CorrectGreen
                status == KeyStatus.WRONG -> DoodleColors.InkBlack.copy(alpha = 0.55f)
                isFrozen -> DoodleColors.IceCubeBorder
                else -> DoodleColors.InkBlack
            }
        )

        // Boss Level: Bomb dot is intentionally invisible on unguessed keys per user preference,
        // while keeping the testTag for automated tests
        if (hasBombDot) {
            Box(
                modifier = Modifier
                    .size(1.dp)
                    .align(Alignment.TopEnd)
                    .testTag("bomb_dot_$letter")
            )
        }

        // Frozen Vowel Doodle Ice Cube Overlay (Levels 50+ until 3 consonants are guessed)
        if (isFrozen) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(3.dp)
                    .testTag("frozen_ice_$letter")
            ) {
                drawDoodleIceCubeOverlay()
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

private fun DrawScope.drawDoodleIceCubeOverlay() {
    val w = size.width
    val h = size.height
    val r = 8.dp.toPx()
    // Translucent cyan ice block
    drawRoundRect(
        color = DoodleColors.IceCubeCyan.copy(alpha = 0.52f),
        topLeft = Offset(w * 0.06f, h * 0.06f),
        size = Size(w * 0.88f, h * 0.88f),
        cornerRadius = CornerRadius(r, r)
    )
    // Sketchy frost border
    drawRoundRect(
        color = DoodleColors.IceCubeBorder,
        topLeft = Offset(w * 0.06f, h * 0.06f),
        size = Size(w * 0.88f, h * 0.88f),
        cornerRadius = CornerRadius(r, r),
        style = Stroke(width = 2.dp.toPx())
    )
    // Glossy white ice shine lines & frost crack
    drawLine(
        color = Color.White.copy(alpha = 0.85f),
        start = Offset(w * 0.20f, h * 0.26f),
        end = Offset(w * 0.42f, h * 0.16f),
        strokeWidth = 2.2.dp.toPx(),
        cap = StrokeCap.Round
    )
    drawLine(
        color = Color.White.copy(alpha = 0.75f),
        start = Offset(w * 0.65f, h * 0.78f),
        end = Offset(w * 0.80f, h * 0.62f),
        strokeWidth = 1.8.dp.toPx(),
        cap = StrokeCap.Round
    )
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

    // Smooth spring-interpolated float lives (6f..0f) for buttery 60fps Volcano crate drops & T-Rex jaw opening
    val animatedLives by animateFloatAsState(
        targetValue = if (isGameOver) 0f else livesRemaining.coerceIn(0, 6).toFloat(),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "animatedLives"
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

    // Subtle impact screen-shake offset when taking damage
    val shakeOffset = if (popTrigger > 0 && popProgress.value < 0.38f) {
        val intensity = (1f - popProgress.value / 0.38f) * 5f
        sin(popProgress.value * 55f) * intensity
    } else {
        0f
    }

    Box(modifier = modifier.offset(x = shakeOffset.dp)) {
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
                    animatedLives = animatedLives,
                    popTrigger = popTrigger,
                    popProgress = popProgress.value,
                    bobOffset = bobOffset,
                    wavePhase = wavePhase,
                    isGameOver = isGameOver
                )

                PerilType.GIANT_VENUS_FLYTRAP -> drawVenusFlytrapPeril(
                    livesRemaining = livesRemaining,
                    animatedLives = animatedLives,
                    popTrigger = popTrigger,
                    popProgress = popProgress.value,
                    bobOffset = bobOffset,
                    wavePhase = wavePhase,
                    isGameOver = isGameOver
                )

                PerilType.DINO_STEALTH -> drawDinoStealthPeril(
                    livesRemaining = livesRemaining,
                    animatedLives = animatedLives,
                    popTrigger = popTrigger,
                    popProgress = popProgress.value,
                    bobOffset = bobOffset,
                    wavePhase = wavePhase,
                    isGameOver = isGameOver
                )

                PerilType.LEAKY_SUBMARINE -> drawLeakySubmarinePeril(
                    livesRemaining = livesRemaining,
                    animatedLives = animatedLives,
                    popTrigger = popTrigger,
                    popProgress = popProgress.value,
                    bobOffset = bobOffset,
                    wavePhase = wavePhase,
                    isGameOver = isGameOver
                )
            }
        }

        // Floating "-1 ❤️" or "-2 ❤️ 💥" + Peril Hit Effect (POP! / ZAP! / CRUMBLE! / SNAP! / CLANG! / PING!)
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
                    if (perilType == PerilType.UFO_ABDUCTION || perilType == PerilType.LEAKY_SUBMARINE)
                        Alignment.BottomCenter
                    else
                        Alignment.TopCenter
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
// PERIL 1: SHARK_BALLOONS (Levels 1–16)
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

    if (!isMenuPreview && (livesRemaining <= 0 || isGameOver)) {
        // 0-LIVES DEATH ANIMATION: Stickman upside-down in shark jaws with legs kicking above splashing waves!
        val s = (w / 420f).coerceIn(0.68f, 1.12f)
        val kickL = sin(wavePhase * 4f) * 11.dp.toPx() * s
        val kickR = -sin(wavePhase * 4f) * 11.dp.toPx() * s
        val strokeW = 3.2.dp.toPx() * s
        drawLine(
            color = DoodleColors.InkBlack,
            start = Offset(centerX - 6.dp.toPx() * s, waterY - 6.dp.toPx() * s),
            end = Offset(centerX - 18.dp.toPx() * s, waterY - 28.dp.toPx() * s + kickL),
            strokeWidth = strokeW,
            cap = StrokeCap.Round
        )
        drawLine(
            color = DoodleColors.InkBlack,
            start = Offset(centerX + 6.dp.toPx() * s, waterY - 6.dp.toPx() * s),
            end = Offset(centerX + 18.dp.toPx() * s, waterY - 28.dp.toPx() * s + kickR),
            strokeWidth = strokeW,
            cap = StrokeCap.Round
        )
        for (sp in 0..4) {
            val ang = (-PI * (0.2 + sp * 0.15)).toFloat()
            val dist = (16 + ((wavePhase * 12f + sp * 7f) % 24f)).dp.toPx() * s
            drawCircle(
                color = DoodleColors.WaterBlue,
                radius = 3.2.dp.toPx() * s,
                center = Offset(centerX + cos(ang) * dist, waterY - 6.dp.toPx() + sin(ang) * dist * 0.7f)
            )
        }
    } else {
        drawDoodleStickman(
            handAnchor = handPoint,
            scale = (w / 420f).coerceIn(0.68f, 1.12f),
            isPanicking = livesRemaining in 1..2,
            isSplashed = false,
            wavePhase = wavePhase,
            handsUp = true
        )
    }

    val isJawsOpen = (!isMenuPreview && livesRemaining <= 2)
    if (isJawsOpen) {
        drawOpenSharkJaws(
            centerX = centerX + sharkPatrol * (w * 0.04f),
            waterY = waterY,
            scale = (w / 380f).coerceIn(0.75f, 1.2f)
        )
    } else {
        val sharkX = centerX + sin(wavePhase) * (w * 0.23f)
        val movingRight = cos(wavePhase) >= 0f
        drawCirclingSharkFin(
            x = sharkX,
            waterY = waterY,
            facingRight = movingRight,
            scale = (w / 380f).coerceIn(0.75f, 1.2f)
        )
    }

    drawDoodleWaterWaves(waterY = waterY, wavePhase = wavePhase)
}

// ----------------------------------------------------------------------------
// PERIL 2: UFO_ABDUCTION (Levels 17–33)
// Stickman on grass pulled upward step-by-step into UFO tractor beam at top;
// when lives <= 2, he floats mid-air by his untied shoelaces!
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
    val stickmanScale = ((w / 420f) * (1f - dangerProgress * 0.25f)).coerceIn(0.55f, 1.1f)

    if (isGameOver || livesRemaining <= 0) {
        // 0-LIVES DEATH ANIMATION: Stickman tumbling 360° in zero-gravity at top of UFO beam!
        val spinPivot = Offset(centerX, ufoY + 34.dp.toPx() + sin(wavePhase * 2f) * 4.dp.toPx())
        withTransform({
            rotate(degrees = (wavePhase * 114f) % 360f, pivot = spinPivot)
        }) {
            drawDoodleStickman(
                handAnchor = Offset(spinPivot.x, spinPivot.y - 20.dp.toPx()),
                scale = ((w / 420f) * 0.65f).coerceIn(0.48f, 0.85f),
                isPanicking = true,
                isSplashed = true,
                wavePhase = wavePhase,
                handsUp = false
            )
        }
    } else {
        drawDoodleStickman(
            handAnchor = Offset(centerX, currentHandY),
            scale = stickmanScale,
            isPanicking = livesRemaining <= 2,
            isSplashed = false,
            wavePhase = wavePhase,
            handsUp = dangerProgress > 0.1f
        )

        // When lives <= 2, draw untied shoelaces dangling & being tugged upward by the tractor beam!
        if (livesRemaining in 1..2) {
            val footY = currentHandY + 72.dp.toPx() * stickmanScale
            for (side in listOf(-1f, 1f)) {
                val fx = centerX + side * 13.dp.toPx() * stickmanScale
                val lacePath = Path().apply {
                    moveTo(fx, footY)
                    quadraticTo(
                        fx + side * 10.dp.toPx(),
                        footY - 14.dp.toPx() + sin(wavePhase * 4f + side) * 5.dp.toPx(),
                        fx + side * 4.dp.toPx(),
                        footY - 24.dp.toPx()
                    )
                }
                drawPath(
                    path = lacePath,
                    color = DoodleColors.WrongRed,
                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }
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
// PERIL 3: VOLCANO_BLOCKS / VOLCANO_LAVA (Levels 34–50)
// Stickman standing on a stack of 6 sketched stone crates over bubbling lava;
// when lives <= 2, his feet start smoking and he hops around!
// ----------------------------------------------------------------------------
private fun DrawScope.drawVolcanoBlocksPeril(
    livesRemaining: Int,
    animatedLives: Float,
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

    // 1. Draw Stack of Remaining Stone Crates above lavaY with smooth spring drop
    val cratesCount = livesRemaining.coerceIn(0, 6)
    val settleOffset = ((animatedLives - cratesCount).coerceAtLeast(0f)) * crateH

    for (i in 0 until cratesCount) {
        val topY = lavaY - (i + 1) * crateH - settleOffset
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
        // Stone texture cross-brace & rivets
        drawLine(
            color = DoodleColors.InkBlack.copy(alpha = 0.45f),
            start = Offset(leftX + 6.dp.toPx(), topY + 3.dp.toPx()),
            end = Offset(leftX + crateW - 6.dp.toPx(), topY + crateH - 5.dp.toPx()),
            strokeWidth = 1.4.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = DoodleColors.InkBlack.copy(alpha = 0.3f),
            start = Offset(leftX + crateW - 6.dp.toPx(), topY + 3.dp.toPx()),
            end = Offset(leftX + 6.dp.toPx(), topY + crateH - 5.dp.toPx()),
            strokeWidth = 1.2.dp.toPx(),
            cap = StrokeCap.Round
        )
    }

    // Crumbling rock debris when a crate breaks
    if (popTrigger > 0 && popProgress < 0.9f) {
        val crumbleY = lavaY - cratesCount * crateH
        for (r in 0 until 8) {
            val angle = (r * PI / 4.0).toFloat()
            val rx = centerX + cos(angle) * (14.dp.toPx() + popProgress * 32.dp.toPx())
            val ry = crumbleY + sin(angle) * 10.dp.toPx() + popProgress * 30.dp.toPx()
            drawRoundRect(
                color = DoodleColors.CrateStoneDark.copy(alpha = (1f - popProgress).coerceIn(0f, 1f)),
                topLeft = Offset(rx - 4.dp.toPx(), ry - 4.dp.toPx()),
                size = Size(8.dp.toPx(), 7.dp.toPx()),
                cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
            )
        }
    }

    // 2. Draw Stickman standing on top of the highest crate (smoothly following animatedLives)
    val platformTopY = lavaY - animatedLives * crateH
    val stickmanScale = (w / 440f).coerceIn(0.65f, 1.05f)
    val feetHeight = 72.dp.toPx() * stickmanScale
    val hotFootHop = if (livesRemaining in 1..2 && !isGameOver) {
        -kotlin.math.abs(sin(wavePhase * 5f)) * 9.dp.toPx()
    } else {
        bobOffset * 0.4f
    }
    val handY = if (isGameOver || livesRemaining <= 0) {
        lavaY - 18.dp.toPx()
    } else {
        platformTopY - feetHeight + hotFootHop
    }

    drawDoodleStickman(
        handAnchor = Offset(centerX, handY),
        scale = stickmanScale,
        isPanicking = livesRemaining in 1..2,
        isSplashed = livesRemaining <= 0 || isGameOver,
        wavePhase = wavePhase,
        handsUp = false
    )

    // Rising smoke puffs from Stickman's melting shoes when lives <= 2
    if (livesRemaining in 1..2 && !isGameOver) {
        for (puff in 0..3) {
            val puffPhase = (wavePhase * 2f + puff * 1.3f) % (2f * PI.toFloat())
            val progress = puffPhase / (2f * PI.toFloat())
            val sx = centerX + (if (puff % 2 == 0) -12.dp.toPx() else 12.dp.toPx()) + sin(wavePhase * 3f + puff) * 4.dp.toPx()
            val sy = platformTopY - 4.dp.toPx() - progress * 24.dp.toPx()
            drawCircle(
                color = Color.DarkGray.copy(alpha = (1f - progress) * 0.55f),
                radius = (3.5f + progress * 4.5f).dp.toPx(),
                center = Offset(sx, sy)
            )
        }
    }

    // 3. Draw Bubbling Red/Orange Lava at bottom
    val lavaPath = Path().apply {
        moveTo(0f, h)
        lineTo(0f, lavaY)
        var x = 0f
        val step = 14.dp.toPx()
        while (x <= w + step) {
            val ly = lavaY + sin(wavePhase * 1.6f + x * 0.05f) * 5.dp.toPx()
            lineTo(x, ly)
            x += step
        }
        lineTo(w, h)
        close()
    }
    drawPath(path = lavaPath, color = DoodleColors.LavaOrange.copy(alpha = 0.88f))

    // Rising glowing lava embers & bubbles
    for (b in 1..5) {
        val bx = w * (0.16f * b) + sin(wavePhase + b) * 6.dp.toPx()
        val by = lavaY + 8.dp.toPx() + cos(wavePhase * 2f + b) * 4.dp.toPx()
        drawCircle(color = DoodleColors.LavaYellow, radius = 5.dp.toPx(), center = Offset(bx, by))
        drawCircle(
            color = DoodleColors.InkBlack,
            radius = 5.dp.toPx(),
            center = Offset(bx, by),
            style = Stroke(width = 1.4.dp.toPx())
        )
        // Floating ember spark above lava
        val emberY = lavaY - ((wavePhase * 18f + b * 17f) % 36f).dp.toPx()
        drawCircle(
            color = DoodleColors.LavaYellow.copy(alpha = 0.75f),
            radius = 2.2.dp.toPx(),
            center = Offset(bx + cos(wavePhase + b) * 8.dp.toPx(), emberY)
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
// PERIL 4: GIANT_VENUS_FLYTRAP (Levels 51–66)
// Stickman suspended on a twisting vine being chewed by caterpillars;
// each wrong guess snaps a vine node. When lives <= 2, the plant wears a bib
// with a fork & knife and says: "Vegetarian option!"
// ----------------------------------------------------------------------------
private fun DrawScope.drawVenusFlytrapPeril(
    livesRemaining: Int,
    animatedLives: Float,
    popTrigger: Int,
    popProgress: Float,
    bobOffset: Float,
    wavePhase: Float,
    isGameOver: Boolean
) {
    val w = size.width
    val h = size.height
    val centerX = w * 0.5f
    val groundY = h * 0.88f
    val trapCenterY = h * 0.75f

    // 1. Twisting Jungle Vine from top canopy holding the stickman
    val topCanopyY = h * 0.06f
    val safeHandY = h * 0.25f
    val perilHandY = h * 0.54f
    val dropFraction = ((6f - animatedLives) / 6f).coerceIn(0f, 1f)
    val currentHandY = if (isGameOver || livesRemaining <= 0) {
        trapCenterY - 12.dp.toPx()
    } else {
        safeHandY + (perilHandY - safeHandY) * dropFraction + bobOffset * 0.6f
    }

    // Draw twisting vine segments with 6 leaf nodes (intact nodes = livesRemaining)
    val vinePath = Path().apply {
        moveTo(centerX, topCanopyY)
        val segments = 6
        val segH = (currentHandY - topCanopyY) / segments
        for (i in 0 until segments) {
            val sy = topCanopyY + i * segH
            val ey = sy + segH
            val curveDir = if (i % 2 == 0) 1f else -1f
            quadraticTo(
                centerX + curveDir * 12.dp.toPx(),
                (sy + ey) * 0.5f,
                centerX,
                ey
            )
        }
    }
    drawPath(
        path = vinePath,
        color = DoodleColors.VineLeafGreen,
        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
    )
    drawPath(
        path = vinePath,
        color = DoodleColors.InkBlack,
        style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
    )

    // 6 Vine Nodes + Chewing Caterpillars
    for (nodeIdx in 0 until 6) {
        val nodeFrac = (nodeIdx + 0.5f) / 6f
        val ny = topCanopyY + (currentHandY - topCanopyY) * nodeFrac
        val side = if (nodeIdx % 2 == 0) 1f else -1f
        val nx = centerX + side * 5.dp.toPx()
        val isIntact = nodeIdx < livesRemaining

        if (isIntact) {
            // Leaf node
            drawOval(
                color = DoodleColors.FlytrapGreen,
                topLeft = Offset(nx + side * 2.dp.toPx() - 6.dp.toPx(), ny - 4.dp.toPx()),
                size = Size(12.dp.toPx(), 8.dp.toPx())
            )
            drawOval(
                color = DoodleColors.InkBlack,
                topLeft = Offset(nx + side * 2.dp.toPx() - 6.dp.toPx(), ny - 4.dp.toPx()),
                size = Size(12.dp.toPx(), 8.dp.toPx()),
                style = Stroke(width = 1.3.dp.toPx())
            )
            // Chewing caterpillar on the lowest intact vine node
            if (nodeIdx == livesRemaining - 1) {
                for (seg in 0..2) {
                    val cx = nx + side * (10 + seg * 6).dp.toPx() + sin(wavePhase * 4f + seg) * 1.5.dp.toPx()
                    val cy = ny + cos(wavePhase * 3f + seg) * 1.5.dp.toPx()
                    drawCircle(
                        color = if (seg % 2 == 0) DoodleColors.DoodleYellow else DoodleColors.CorrectGreen,
                        radius = 4.dp.toPx(),
                        center = Offset(cx, cy)
                    )
                    drawCircle(
                        color = DoodleColors.InkBlack,
                        radius = 4.dp.toPx(),
                        center = Offset(cx, cy),
                        style = Stroke(width = 1.2.dp.toPx())
                    )
                }
            }
        }
    }

    // Snapping vine leaf burst on wrong guess
    if (popTrigger > 0 && popProgress < 0.9f) {
        for (leaf in 0 until 6) {
            val ang = (leaf * PI / 3.0).toFloat()
            val lx = centerX + cos(ang) * (10.dp.toPx() + popProgress * 28.dp.toPx())
            val ly = currentHandY - 18.dp.toPx() + sin(ang) * (8.dp.toPx() + popProgress * 22.dp.toPx())
            drawCircle(
                color = DoodleColors.CorrectGreen.copy(alpha = (1f - popProgress).coerceIn(0f, 1f)),
                radius = 3.5.dp.toPx(),
                center = Offset(lx, ly)
            )
        }
    }

    // 2. Draw Stickman hanging from the vine (or feet sticking out of Flytrap when Game Over)
    val stickmanScale = (w / 430f).coerceIn(0.64f, 1.04f)
    if (!isGameOver && livesRemaining > 0) {
        drawDoodleStickman(
            handAnchor = Offset(centerX, currentHandY),
            scale = stickmanScale,
            isPanicking = livesRemaining <= 2,
            isSplashed = false,
            wavePhase = wavePhase,
            handsUp = true
        )
    }

    // 3. Draw Giant Venus Flytrap at Bottom
    val plantScale = (w / 360f).coerceIn(0.72f, 1.15f)
    val jawSpread = when {
        isGameOver || livesRemaining <= 0 -> 6.dp.toPx() // snapped shut!
        livesRemaining <= 2 -> (28.dp.toPx() + sin(wavePhase * 3f) * 4.dp.toPx()) * plantScale
        else -> 18.dp.toPx() * plantScale
    }

    // Thick stalk from ground up to trap head
    drawLine(
        color = DoodleColors.VineLeafGreen,
        start = Offset(centerX, groundY),
        end = Offset(centerX, trapCenterY),
        strokeWidth = 9.dp.toPx() * plantScale,
        cap = StrokeCap.Round
    )
    drawLine(
        color = DoodleColors.InkBlack,
        start = Offset(centerX, groundY),
        end = Offset(centerX, trapCenterY),
        strokeWidth = 2.4.dp.toPx(),
        cap = StrokeCap.Round
    )

    // When lives <= 2: Plant wears a white dinner bib + holds a Fork & Knife!
    if (livesRemaining <= 2) {
        val bibW = 36.dp.toPx() * plantScale
        val bibH = 26.dp.toPx() * plantScale
        val bibPath = Path().apply {
            moveTo(centerX - bibW * 0.5f, trapCenterY + 6.dp.toPx())
            lineTo(centerX + bibW * 0.5f, trapCenterY + 6.dp.toPx())
            quadraticTo(
                centerX + bibW * 0.45f,
                trapCenterY + 6.dp.toPx() + bibH,
                centerX,
                trapCenterY + 6.dp.toPx() + bibH
            )
            quadraticTo(
                centerX - bibW * 0.45f,
                trapCenterY + 6.dp.toPx() + bibH,
                centerX - bibW * 0.5f,
                trapCenterY + 6.dp.toPx()
            )
            close()
        }
        drawPath(path = bibPath, color = Color.White)
        drawPath(
            path = bibPath,
            color = DoodleColors.InkBlack,
            style = Stroke(width = 1.8.dp.toPx(), join = StrokeJoin.Round)
        )
        // Red heart/stripe on bib
        drawCircle(
            color = DoodleColors.WrongRed,
            radius = 4.5.dp.toPx() * plantScale,
            center = Offset(centerX, trapCenterY + 18.dp.toPx())
        )

        // Left vine-leaf holding a silver Fork
        val forkX = centerX - 46.dp.toPx() * plantScale
        val forkY = trapCenterY + sin(wavePhase * 4f) * 4.dp.toPx()
        drawLine(
            color = DoodleColors.VineLeafGreen,
            start = Offset(centerX - 6.dp.toPx(), trapCenterY + 14.dp.toPx()),
            end = Offset(forkX, forkY + 10.dp.toPx()),
            strokeWidth = 4.dp.toPx(),
            cap = StrokeCap.Round
        )
        // Fork handle & 3 tines
        drawLine(
            color = DoodleColors.SubmarineSteel,
            start = Offset(forkX, forkY + 16.dp.toPx()),
            end = Offset(forkX, forkY - 8.dp.toPx()),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )
        for (tine in -1..1) {
            val tx = forkX + tine * 3.5.dp.toPx()
            drawLine(
                color = DoodleColors.InkBlack,
                start = Offset(tx, forkY - 4.dp.toPx()),
                end = Offset(tx, forkY - 13.dp.toPx()),
                strokeWidth = 1.6.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        // Right vine-leaf holding a silver Knife
        val knifeX = centerX + 46.dp.toPx() * plantScale
        val knifeY = trapCenterY - sin(wavePhase * 4f) * 4.dp.toPx()
        drawLine(
            color = DoodleColors.VineLeafGreen,
            start = Offset(centerX + 6.dp.toPx(), trapCenterY + 14.dp.toPx()),
            end = Offset(knifeX, knifeY + 10.dp.toPx()),
            strokeWidth = 4.dp.toPx(),
            cap = StrokeCap.Round
        )
        val bladePath = Path().apply {
            moveTo(knifeX - 2.dp.toPx(), knifeY + 6.dp.toPx())
            lineTo(knifeX - 2.dp.toPx(), knifeY - 14.dp.toPx())
            quadraticTo(
                knifeX + 6.dp.toPx(),
                knifeY - 6.dp.toPx(),
                knifeX + 3.dp.toPx(),
                knifeY + 6.dp.toPx()
            )
            close()
        }
        drawPath(path = bladePath, color = DoodleColors.UfoSilver)
        drawPath(path = bladePath, color = DoodleColors.InkBlack, style = Stroke(width = 1.5.dp.toPx()))
    }

    // Flytrap Jaws (Left & Right Lobe with red inner mouth and interlocking spikes)
    val lobeW = 48.dp.toPx() * plantScale
    // Red inner cavity
    drawOval(
        color = DoodleColors.FlytrapMouthPink,
        topLeft = Offset(centerX - lobeW, trapCenterY - jawSpread),
        size = Size(lobeW * 2f, jawSpread * 1.6f)
    )
    // Outer green lobes
    drawArc(
        color = DoodleColors.FlytrapGreen,
        startAngle = 0f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(centerX - lobeW, trapCenterY - jawSpread * 0.7f),
        size = Size(lobeW * 2f, jawSpread * 1.7f)
    )
    drawOval(
        color = DoodleColors.InkBlack,
        topLeft = Offset(centerX - lobeW, trapCenterY - jawSpread),
        size = Size(lobeW * 2f, jawSpread * 1.6f),
        style = Stroke(width = 2.4.dp.toPx())
    )
    // Spiky interlocking flytrap cilia teeth along top rim
    for (t in -4..4) {
        val tx = centerX + t * (lobeW * 0.2f)
        drawLine(
            color = DoodleColors.InkBlack,
            start = Offset(tx, trapCenterY - jawSpread * 0.85f),
            end = Offset(tx, trapCenterY - jawSpread * 0.25f),
            strokeWidth = 1.8.dp.toPx(),
            cap = StrokeCap.Round
        )
    }

    // If Game Over: Stickman's kicking legs sticking out of the closed Venus Flytrap!
    if (isGameOver || livesRemaining <= 0) {
        val kick = sin(wavePhase * 5f) * 8.dp.toPx()
        drawLine(
            color = DoodleColors.InkBlack,
            start = Offset(centerX - 6.dp.toPx(), trapCenterY - 4.dp.toPx()),
            end = Offset(centerX - 18.dp.toPx(), trapCenterY - 26.dp.toPx() + kick),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = DoodleColors.InkBlack,
            start = Offset(centerX + 6.dp.toPx(), trapCenterY - 4.dp.toPx()),
            end = Offset(centerX + 18.dp.toPx(), trapCenterY - 26.dp.toPx() - kick),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )
    }

    // Ground line
    drawLine(
        color = DoodleColors.InkBlack,
        start = Offset(w * 0.08f, groundY),
        end = Offset(w * 0.92f, groundY),
        strokeWidth = 2.4.dp.toPx(),
        cap = StrokeCap.Round
    )
}

// ----------------------------------------------------------------------------
// PERIL 5: DINO_STEALTH / SLEEPING_TREX (Levels 67–83)
// Stickman tiptoeing next to a giant sculpted sleeping/roaring T-Rex + Noise Meter
// ----------------------------------------------------------------------------
private fun DrawScope.drawDinoStealthPeril(
    livesRemaining: Int,
    animatedLives: Float,
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
    val meterStartX = w * 0.06f
    val meterTopY = h * 0.10f
    val barW = (w * 0.10f).coerceIn(12.dp.toPx(), 22.dp.toPx())
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

    // 2. Sketched Ground Line with pebbles
    drawLine(
        color = DoodleColors.InkBlack,
        start = Offset(w * 0.04f, groundY),
        end = Offset(w * 0.96f, groundY),
        strokeWidth = 2.6.dp.toPx(),
        cap = StrokeCap.Round
    )

    // 3. Sculpted Hand-Drawn T-Rex (Facing Left toward the tiptoeing Stickman!)
    val dinoScale = (w / 340f).coerceIn(0.68f, 1.15f)
    val jawPivotX = w * 0.82f
    val jawPivotY = groundY - 42.dp.toPx() * dinoScale
    val breathe = sin(wavePhase) * 2.5.dp.toPx()

    // Smoothly interpolated jaw opening angle in degrees
    val wakeProgress = ((6f - animatedLives) / 6f).coerceIn(0f, 1f)
    val upperJawTiltDeg = when {
        isGameOver || livesRemaining <= 0 -> 22f
        livesRemaining <= 2 -> 15f + sin(wavePhase * 4f) * 3f
        livesRemaining <= 4 -> 5f
        else -> 0f
    }
    val lowerJawTiltDeg = when {
        isGameOver || livesRemaining <= 0 -> -20f
        livesRemaining <= 2 -> -14f - sin(wavePhase * 4f) * 2f
        livesRemaining <= 4 -> -4f
        else -> 0f
    }

    // 3A. Muscular Curved T-Rex Neck & Back (extending down to ground on the right)
    val neckPath = Path().apply {
        moveTo(jawPivotX - 18.dp.toPx() * dinoScale, jawPivotY - 8.dp.toPx() * dinoScale)
        quadraticTo(
            jawPivotX - 24.dp.toPx() * dinoScale,
            groundY - 18.dp.toPx() * dinoScale,
            jawPivotX - 30.dp.toPx() * dinoScale,
            groundY
        )
        lineTo(w * 0.96f, groundY)
        lineTo(w * 0.96f, jawPivotY - 38.dp.toPx() * dinoScale)
        quadraticTo(
            jawPivotX + 18.dp.toPx() * dinoScale,
            jawPivotY - 34.dp.toPx() * dinoScale,
            jawPivotX - 18.dp.toPx() * dinoScale,
            jawPivotY - 8.dp.toPx() * dinoScale
        )
        close()
    }
    drawPath(neckPath, color = DoodleColors.DinoGreen)
    drawPath(
        neckPath,
        color = DoodleColors.InkBlack,
        style = Stroke(width = 2.4.dp.toPx(), join = StrokeJoin.Round)
    )

    // Light-green ribbed underbelly patches on neck
    for (rib in 0..2) {
        val ry = jawPivotY + (8 + rib * 11).dp.toPx() * dinoScale
        if (ry < groundY - 4.dp.toPx()) {
            drawLine(
                color = DoodleColors.DinoLightGreen,
                start = Offset(jawPivotX - 22.dp.toPx() * dinoScale, ry),
                end = Offset(jawPivotX - 6.dp.toPx() * dinoScale, ry + 2.dp.toPx()),
                strokeWidth = 4.dp.toPx() * dinoScale,
                cap = StrokeCap.Round
            )
        }
    }

    // Tiny comical T-Rex forearm & 2 claws near chest
    val armX = jawPivotX - 22.dp.toPx() * dinoScale
    val armY = groundY - 22.dp.toPx() * dinoScale
    val armPath = Path().apply {
        moveTo(armX + 8.dp.toPx() * dinoScale, armY - 4.dp.toPx() * dinoScale)
        lineTo(armX - 8.dp.toPx() * dinoScale, armY + 4.dp.toPx() * dinoScale)
        lineTo(armX - 13.dp.toPx() * dinoScale, armY + 1.dp.toPx() * dinoScale)
        moveTo(armX - 8.dp.toPx() * dinoScale, armY + 4.dp.toPx() * dinoScale)
        lineTo(armX - 12.dp.toPx() * dinoScale, armY + 8.dp.toPx() * dinoScale)
    }
    drawPath(
        armPath,
        color = DoodleColors.InkBlack,
        style = Stroke(width = 2.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
    )

    // 3B. Inner Red Mouth Cavity & Pink Tongue when jaws open
    if (wakeProgress > 0.2f || livesRemaining <= 4) {
        val mouthOpenH = (12.dp.toPx() + wakeProgress * 28.dp.toPx()) * dinoScale
        val cavityPath = Path().apply {
            moveTo(jawPivotX - 6.dp.toPx() * dinoScale, jawPivotY)
            lineTo(jawPivotX - 72.dp.toPx() * dinoScale, jawPivotY - mouthOpenH * 0.55f)
            quadraticTo(
                jawPivotX - 46.dp.toPx() * dinoScale,
                jawPivotY,
                jawPivotX - 68.dp.toPx() * dinoScale,
                jawPivotY + mouthOpenH * 0.55f
            )
            close()
        }
        drawPath(cavityPath, color = Color(0xFFB71C1C))

        // Wavy roaring tongue
        val tonguePath = Path().apply {
            moveTo(jawPivotX - 20.dp.toPx() * dinoScale, jawPivotY + 4.dp.toPx() * dinoScale)
            quadraticTo(
                jawPivotX - 50.dp.toPx() * dinoScale,
                jawPivotY - 4.dp.toPx() * dinoScale + sin(wavePhase * 3f) * 4.dp.toPx(),
                jawPivotX - 64.dp.toPx() * dinoScale,
                jawPivotY + 5.dp.toPx() * dinoScale
            )
        }
        drawPath(
            tonguePath,
            color = Color(0xFFFF8A80),
            style = Stroke(width = 5.dp.toPx() * dinoScale, cap = StrokeCap.Round)
        )
    }

    // 3C. Articulated Lower Jaw (rotates downward around jawPivot)
    withTransform({
        rotate(degrees = lowerJawTiltDeg, pivot = Offset(jawPivotX, jawPivotY))
    }) {
        val lowerJawLen = 84.dp.toPx() * dinoScale
        val lowerJawH = 20.dp.toPx() * dinoScale

        // Lower teeth (pointing UP)
        if (livesRemaining <= 4 || isGameOver) {
            val toothCount = 5
            val step = (lowerJawLen * 0.72f) / toothCount
            for (t in 0 until toothCount) {
                val tx = jawPivotX - lowerJawLen * 0.88f + t * step
                val toothH = (7 + (t % 2) * 3).dp.toPx() * dinoScale
                val tPath = Path().apply {
                    moveTo(tx, jawPivotY + 2.dp.toPx())
                    lineTo(tx + step * 0.42f, jawPivotY - toothH)
                    lineTo(tx + step * 0.84f, jawPivotY + 2.dp.toPx())
                    close()
                }
                drawPath(tPath, color = Color.White)
                drawPath(tPath, color = DoodleColors.InkBlack, style = Stroke(width = 1.4.dp.toPx()))
            }
        }

        // Sculpted lower mandible
        val mandiblePath = Path().apply {
            moveTo(jawPivotX + 4.dp.toPx() * dinoScale, jawPivotY)
            lineTo(jawPivotX - lowerJawLen, jawPivotY + 2.dp.toPx() * dinoScale)
            quadraticTo(
                jawPivotX - lowerJawLen - 4.dp.toPx() * dinoScale,
                jawPivotY + lowerJawH * 0.85f,
                jawPivotX - lowerJawLen * 0.65f,
                jawPivotY + lowerJawH
            )
            quadraticTo(
                jawPivotX - lowerJawLen * 0.25f,
                jawPivotY + lowerJawH * 1.05f,
                jawPivotX + 4.dp.toPx() * dinoScale,
                jawPivotY + lowerJawH * 0.55f
            )
            close()
        }
        drawPath(mandiblePath, color = DoodleColors.DinoLightGreen)
        drawPath(
            mandiblePath,
            color = DoodleColors.InkBlack,
            style = Stroke(width = 2.4.dp.toPx(), join = StrokeJoin.Round)
        )
    }

    // 3D. Articulated Upper Skull, Snout, Brow Ridge, Spikes & Eye
    withTransform({
        rotate(degrees = upperJawTiltDeg, pivot = Offset(jawPivotX, jawPivotY))
    }) {
        val snoutLen = 92.dp.toPx() * dinoScale
        val skullH = 44.dp.toPx() * dinoScale
        val breathY = if (livesRemaining >= 5) breathe else 0f

        // Upper serrated teeth (pointing DOWN along upper jaw line)
        if (livesRemaining <= 4 || isGameOver) {
            val toothCount = 6
            val step = (snoutLen * 0.76f) / toothCount
            for (t in 0 until toothCount) {
                val tx = jawPivotX - snoutLen * 0.90f + t * step
                val toothH = (9 + (t % 2) * 4).dp.toPx() * dinoScale
                val tPath = Path().apply {
                    moveTo(tx, jawPivotY - 1.dp.toPx() + breathY)
                    lineTo(tx + step * 0.45f, jawPivotY + toothH + breathY)
                    lineTo(tx + step * 0.88f, jawPivotY - 1.dp.toPx() + breathY)
                    close()
                }
                drawPath(tPath, color = Color.White)
                drawPath(tPath, color = DoodleColors.InkBlack, style = Stroke(width = 1.5.dp.toPx()))
            }
        }

        // Dorsal head spikes along back of skull
        for (s in 0..2) {
            val sx = jawPivotX - (10 + s * 14).dp.toPx() * dinoScale
            val sy = jawPivotY - skullH * (0.92f - s * 0.08f) + breathY
            val spikePath = Path().apply {
                moveTo(sx - 6.dp.toPx() * dinoScale, sy + 6.dp.toPx() * dinoScale)
                lineTo(sx + 2.dp.toPx() * dinoScale, sy - 9.dp.toPx() * dinoScale)
                lineTo(sx + 8.dp.toPx() * dinoScale, sy + 6.dp.toPx() * dinoScale)
                close()
            }
            drawPath(spikePath, color = DoodleColors.HintBulbOrange)
            drawPath(spikePath, color = DoodleColors.InkBlack, style = Stroke(width = 1.6.dp.toPx()))
        }

        // Organic Bezier T-Rex Upper Snout + Raised Brow Ridge
        val skullPath = Path().apply {
            moveTo(jawPivotX + 10.dp.toPx() * dinoScale, jawPivotY + breathY)
            lineTo(jawPivotX + 10.dp.toPx() * dinoScale, jawPivotY - skullH * 0.85f + breathY)
            cubicTo(
                jawPivotX - 8.dp.toPx() * dinoScale,
                jawPivotY - skullH * 1.18f + breathY,
                jawPivotX - 32.dp.toPx() * dinoScale,
                jawPivotY - skullH * 1.12f + breathY,
                jawPivotX - 44.dp.toPx() * dinoScale,
                jawPivotY - skullH * 0.78f + breathY
            )
            cubicTo(
                jawPivotX - 68.dp.toPx() * dinoScale,
                jawPivotY - skullH * 0.76f + breathY,
                jawPivotX - snoutLen - 4.dp.toPx() * dinoScale,
                jawPivotY - skullH * 0.65f + breathY,
                jawPivotX - snoutLen,
                jawPivotY - 3.dp.toPx() * dinoScale + breathY
            )
            quadraticTo(
                jawPivotX - snoutLen * 0.5f,
                jawPivotY + 3.dp.toPx() * dinoScale + breathY,
                jawPivotX + 10.dp.toPx() * dinoScale,
                jawPivotY + breathY
            )
            close()
        }
        drawPath(skullPath, color = DoodleColors.DinoGreen)
        drawPath(
            skullPath,
            color = DoodleColors.InkBlack,
            style = Stroke(width = 2.6.dp.toPx(), join = StrokeJoin.Round)
        )

        // Nostril
        val nostrilCenter = Offset(
            jawPivotX - snoutLen * 0.82f,
            jawPivotY - skullH * 0.44f + breathY
        )
        drawOval(
            color = DoodleColors.InkBlack,
            topLeft = Offset(nostrilCenter.x - 4.dp.toPx() * dinoScale, nostrilCenter.y - 2.5.dp.toPx() * dinoScale),
            size = Size(7.dp.toPx() * dinoScale, 5.dp.toPx() * dinoScale)
        )

        // Scale/freckle texture dots on snout
        drawCircle(
            color = DoodleColors.InkBlack.copy(alpha = 0.28f),
            radius = 2.5.dp.toPx() * dinoScale,
            center = Offset(jawPivotX - snoutLen * 0.56f, jawPivotY - skullH * 0.36f + breathY)
        )
        drawCircle(
            color = DoodleColors.InkBlack.copy(alpha = 0.28f),
            radius = 2.dp.toPx() * dinoScale,
            center = Offset(jawPivotX - snoutLen * 0.46f, jawPivotY - skullH * 0.25f + breathY)
        )

        // Expressive T-Rex Eye under the brow ridge (opens one giant angry eye when lives <= 2!)
        val eyeCenter = Offset(
            jawPivotX - 24.dp.toPx() * dinoScale,
            jawPivotY - skullH * 0.62f + breathY
        )
        if (livesRemaining >= 5) {
            val sleepEyePath = Path().apply {
                moveTo(eyeCenter.x - 8.dp.toPx() * dinoScale, eyeCenter.y)
                quadraticTo(
                    eyeCenter.x,
                    eyeCenter.y + 6.dp.toPx() * dinoScale,
                    eyeCenter.x + 8.dp.toPx() * dinoScale,
                    eyeCenter.y
                )
            }
            drawPath(
                sleepEyePath,
                color = DoodleColors.InkBlack,
                style = Stroke(width = 2.6.dp.toPx(), cap = StrokeCap.Round)
            )
            // Floating Zzz sleep bubbles
            for (z in 1..3) {
                val zx = eyeCenter.x - (z * 8).dp.toPx() + sin(wavePhase + z) * 3.dp.toPx()
                val zy = eyeCenter.y - (14 + z * 11).dp.toPx() + bobOffset * 0.6f
                drawCircle(
                    color = Color.White.copy(alpha = 0.85f),
                    radius = (2.5f + z * 1.5f).dp.toPx(),
                    center = Offset(zx, zy)
                )
                drawCircle(
                    color = DoodleColors.InkBlue,
                    radius = (2.5f + z * 1.5f).dp.toPx(),
                    center = Offset(zx, zy),
                    style = Stroke(width = 1.4.dp.toPx())
                )
            }
        } else {
            val eyeR = (if (livesRemaining <= 2) 10.5f else 7.5f).dp.toPx() * dinoScale
            drawCircle(
                color = if (livesRemaining <= 2) DoodleColors.DoodleYellow else Color.White,
                radius = eyeR,
                center = eyeCenter
            )
            drawCircle(
                color = DoodleColors.InkBlack,
                radius = eyeR,
                center = eyeCenter,
                style = Stroke(width = 2.2.dp.toPx())
            )
            drawOval(
                color = if (livesRemaining <= 2) DoodleColors.WrongRed else DoodleColors.InkBlack,
                topLeft = Offset(eyeCenter.x - eyeR * 0.45f, eyeCenter.y - eyeR * 0.65f),
                size = Size(eyeR * 0.42f, eyeR * 1.3f)
            )
            val browLine = Path().apply {
                moveTo(eyeCenter.x - eyeR * 1.15f, eyeCenter.y - eyeR * 0.35f)
                lineTo(eyeCenter.x + eyeR * 1.05f, eyeCenter.y - eyeR * 1.05f)
            }
            drawPath(
                browLine,
                color = DoodleColors.InkBlack,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
        }
    }

    // 4. Stickman Tiptoeing on the left side
    val stickmanX = w * 0.22f
    val stickmanScale = (w / 420f).coerceIn(0.66f, 1.02f)
    val stickmanHandY = groundY - 72.dp.toPx() * stickmanScale + bobOffset * 0.5f

    drawDoodleStickman(
        handAnchor = Offset(stickmanX, stickmanHandY),
        scale = stickmanScale,
        isPanicking = livesRemaining <= 2,
        isSplashed = isGameOver || livesRemaining <= 0,
        wavePhase = wavePhase,
        handsUp = false
    )

    // 5. Comic "CLANG!" starburst shockwave on wrong guess
    if (popTrigger > 0 && popProgress < 0.9f) {
        val clangCenter = Offset(stickmanX + 16.dp.toPx(), groundY - 14.dp.toPx())
        for (ring in 1..2) {
            drawCircle(
                color = DoodleColors.WrongRed.copy(alpha = (1f - popProgress).coerceIn(0f, 1f)),
                radius = (ring * 12.dp.toPx()) + popProgress * 24.dp.toPx(),
                center = clangCenter,
                style = Stroke(width = 2.4.dp.toPx())
            )
        }
    }
}

// ----------------------------------------------------------------------------
// PERIL 6: LEAKY_SUBMARINE (Levels 84–100)
// Stickman trapped inside a glass submarine tank with 6 water levels & 6 bolts.
// Each wrong guess pops a bolt and fills water by 1/6th. When lives <= 2, he is
// treading water at the top with puffed cheeks!
// ----------------------------------------------------------------------------
private fun DrawScope.drawLeakySubmarinePeril(
    livesRemaining: Int,
    animatedLives: Float,
    popTrigger: Int,
    popProgress: Float,
    bobOffset: Float,
    wavePhase: Float,
    isGameOver: Boolean
) {
    val w = size.width
    val h = size.height
    val centerX = w * 0.5f

    val tankW = (w * 0.62f).coerceIn(100.dp.toPx(), 180.dp.toPx())
    val tankH = (h * 0.72f).coerceIn(130.dp.toPx(), 230.dp.toPx())
    val tankLeft = centerX - tankW * 0.5f
    val tankTop = h * 0.14f
    val tankBottom = tankTop + tankH
    val cornerR = 20.dp.toPx()

    // 1. Submarine Periscope at top of tank
    val periPath = Path().apply {
        moveTo(centerX, tankTop)
        lineTo(centerX, tankTop - 16.dp.toPx())
        lineTo(centerX + 18.dp.toPx(), tankTop - 16.dp.toPx())
    }
    drawPath(
        path = periPath,
        color = DoodleColors.SubmarineYellow,
        style = Stroke(width = 7.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
    drawPath(
        path = periPath,
        color = DoodleColors.InkBlack,
        style = Stroke(width = 2.2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
    )

    // 2. Glass Tank Interior Background
    drawRoundRect(
        color = Color(0xFFE0F7FA).copy(alpha = 0.55f),
        topLeft = Offset(tankLeft, tankTop),
        size = Size(tankW, tankH),
        cornerRadius = CornerRadius(cornerR, cornerR)
    )

    // 3. Rising Water Level (fills 1/6th per wrong guess)
    val waterFillFraction = ((6f - animatedLives) / 6f).coerceIn(0f, 1f)
    val waterHeight = tankH * waterFillFraction
    val waterSurfaceY = tankBottom - waterHeight

    // Stickman inside the tank (rises to tread water at the top as water fills; puffed cheeks when lives <= 2!)
    val stickmanScale = (w / 440f).coerceIn(0.62f, 0.98f)
    val baseStandHandY = tankBottom - 76.dp.toPx() * stickmanScale
    val treadHandY = (waterSurfaceY - 22.dp.toPx() * stickmanScale).coerceAtLeast(tankTop + 8.dp.toPx())
    val stickmanHandY = if (waterFillFraction > 0.35f) {
        treadHandY + sin(wavePhase * 3f) * 3.dp.toPx()
    } else {
        baseStandHandY + bobOffset * 0.3f
    }

    drawDoodleStickman(
        handAnchor = Offset(centerX, stickmanHandY),
        scale = stickmanScale,
        isPanicking = livesRemaining <= 2,
        isSplashed = isGameOver || livesRemaining <= 0,
        wavePhase = wavePhase,
        handsUp = waterFillFraction > 0.5f,
        puffedCheeks = livesRemaining <= 2
    )

    // Draw Rising Water Body inside the Tank
    if (waterFillFraction > 0.01f) {
        val waterPath = Path().apply {
            moveTo(tankLeft + 3.dp.toPx(), tankBottom - 3.dp.toPx())
            lineTo(tankLeft + 3.dp.toPx(), waterSurfaceY)
            var wx = tankLeft + 3.dp.toPx()
            val step = 12.dp.toPx()
            while (wx <= tankLeft + tankW - 3.dp.toPx()) {
                val wy = waterSurfaceY + sin(wavePhase * 2f + wx * 0.08f) * 3.5.dp.toPx()
                lineTo(wx, wy)
                wx += step
            }
            lineTo(tankLeft + tankW - 3.dp.toPx(), tankBottom - 3.dp.toPx())
            close()
        }
        drawPath(
            path = waterPath,
            color = DoodleColors.WaterBlue.copy(alpha = 0.55f)
        )
        drawLine(
            color = DoodleColors.InkBlue,
            start = Offset(tankLeft + 4.dp.toPx(), waterSurfaceY),
            end = Offset(tankLeft + tankW - 4.dp.toPx(), waterSurfaceY),
            strokeWidth = 2.dp.toPx()
        )

        // Rising underwater air bubbles
        for (b in 0..4) {
            val bx = tankLeft + tankW * (0.2f + b * 0.15f) + sin(wavePhase * 2f + b) * 4.dp.toPx()
            val by = tankBottom - ((wavePhase * 22f + b * 19f) % (waterHeight.coerceAtLeast(12f)))
            if (by > waterSurfaceY + 4.dp.toPx()) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.8f),
                    radius = 3.dp.toPx(),
                    center = Offset(bx, by)
                )
                drawCircle(
                    color = DoodleColors.InkBlue,
                    radius = 3.dp.toPx(),
                    center = Offset(bx, by),
                    style = Stroke(width = 1.dp.toPx())
                )
            }
        }
    }

    // 4. 6 Water Level Gauge Tick Marks & 6 Submarine Frame Bolts
    for (lvl in 1..5) {
        val markY = tankBottom - (tankH * (lvl / 6f))
        drawLine(
            color = DoodleColors.InkBlack.copy(alpha = 0.35f),
            start = Offset(tankLeft + 6.dp.toPx(), markY),
            end = Offset(tankLeft + 18.dp.toPx(), markY),
            strokeWidth = 1.5.dp.toPx(),
            cap = StrokeCap.Round
        )
    }

    //Outer Steel Tank Frame
    drawRoundRect(
        color = DoodleColors.InkBlack,
        topLeft = Offset(tankLeft, tankTop),
        size = Size(tankW, tankH),
        cornerRadius = CornerRadius(cornerR, cornerR),
        style = Stroke(width = 3.5.dp.toPx())
    )

    // 6 Brass Bolts along the sides of the tank (each wrong guess pops a bolt & leaks water!)
    for (boltIdx in 0 until 6) {
        val isLeft = boltIdx % 2 == 0
        val row = boltIdx / 2
        val boltX = if (isLeft) tankLeft else tankLeft + tankW
        val boltY = tankBottom - tankH * (0.20f + row * 0.28f)
        val isBoltIntact = boltIdx < livesRemaining

        if (isBoltIntact) {
            drawCircle(
                color = DoodleColors.SubmarineYellow,
                radius = 5.5.dp.toPx(),
                center = Offset(boltX, boltY)
            )
            drawCircle(
                color = DoodleColors.InkBlack,
                radius = 5.5.dp.toPx(),
                center = Offset(boltX, boltY),
                style = Stroke(width = 1.8.dp.toPx())
            )
        } else {
            // Popped bolt hole spraying a high-pressure water jet into the tank!
            val jetDir = if (isLeft) 1f else -1f
            drawCircle(
                color = DoodleColors.InkBlack,
                radius = 3.5.dp.toPx(),
                center = Offset(boltX, boltY)
            )
            val jetPath = Path().apply {
                moveTo(boltX, boltY)
                quadraticTo(
                    boltX + jetDir * 18.dp.toPx(),
                    boltY - 6.dp.toPx() + sin(wavePhase * 5f + boltIdx) * 3.dp.toPx(),
                    boltX + jetDir * 28.dp.toPx(),
                    boltY + 10.dp.toPx()
                )
            }
            drawPath(
                path = jetPath,
                color = DoodleColors.WaterBlue,
                style = Stroke(width = 2.6.dp.toPx(), cap = StrokeCap.Round)
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
    handsUp: Boolean = true,
    puffedCheeks: Boolean = false
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

    if (puffedCheeks) {
        // Puffed out cheeks holding breath in Leaky Submarine!
        val cheekR = 4.2.dp.toPx() * scale
        val leftCheek = Offset(headCenter.x - headRadius * 0.62f, headCenter.y + headRadius * 0.25f)
        val rightCheek = Offset(headCenter.x + headRadius * 0.62f, headCenter.y + headRadius * 0.25f)
        drawCircle(color = Color(0xFFB3E5FC), radius = cheekR, center = leftCheek)
        drawCircle(color = DoodleColors.InkBlack, radius = cheekR, center = leftCheek, style = Stroke(width = 1.4.dp.toPx()))
        drawCircle(color = Color(0xFFB3E5FC), radius = cheekR, center = rightCheek)
        drawCircle(color = DoodleColors.InkBlack, radius = cheekR, center = rightCheek, style = Stroke(width = 1.4.dp.toPx()))
        drawLine(
            color = DoodleColors.InkBlack,
            start = Offset(leftCheek.x + cheekR * 0.7f, leftCheek.y),
            end = Offset(rightCheek.x - cheekR * 0.7f, rightCheek.y),
            strokeWidth = 1.6.dp.toPx(),
            cap = StrokeCap.Round
        )
    } else if (isPanicking || isSplashed) {
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
    val finH = 36.dp.toPx() * scale
    val finW = 32.dp.toPx() * scale
    val dir = if (facingRight) 1f else -1f
    val baseY = waterY + 6.dp.toPx()

    // Iconic Shark Dorsal Fin: solid slate-grey fill + crisp ink outline, no white lines inside
    val frontBaseX = x + finW * 0.53f * dir
    val rearBaseX = x - finW * 0.47f * dir
    val tipX = x - finW * 0.28f * dir
    val tipY = waterY - finH

    val finFillPath = Path().apply {
        moveTo(frontBaseX, baseY)
        quadraticTo(
            x + finW * 0.34f * dir,
            waterY - finH * 0.66f,
            tipX,
            tipY
        )
        quadraticTo(
            x - finW * 0.15f * dir,
            waterY - finH * 0.38f,
            rearBaseX,
            baseY
        )
        close()
    }

    val finOutlinePath = Path().apply {
        moveTo(frontBaseX, baseY - 1.dp.toPx())
        quadraticTo(
            x + finW * 0.34f * dir,
            waterY - finH * 0.66f,
            tipX,
            tipY
        )
        quadraticTo(
            x - finW * 0.15f * dir,
            waterY - finH * 0.38f,
            rearBaseX,
            baseY - 1.dp.toPx()
        )
    }

    drawPath(path = finFillPath, color = DoodleColors.SharkGrey)
    drawPath(
        path = finOutlinePath,
        color = DoodleColors.InkBlack,
        style = Stroke(width = 2.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
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
    drawPath(path = wavePath, color = Color(0xFFB3E5FC))

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

enum class VictoryCelebrationStyle(val badgeLabel: String, val cheerSubtitle: String) {
    DISCO_FEVER("🕺 Disco Party! 🎲", "✨ Funky Boogie Champion! ✨"),
    TROPHY_FLIP("🏆 Trophy Jump! 🎲", "🌟 Golden Cup Winner! 🌟"),
    BALLOON_FIESTA("🎈 Balloon Fiesta! 🎲", "🎉 Sky-High Party Time! 🎉"),
    SUPER_HERO("🦸 Superhero Flyby! 🎲", "🚀 Unstoppable Word Hero! 🚀")
}

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
    val styles = remember { VictoryCelebrationStyle.entries }
    var celebrationIndex by remember(level.number, level.word) {
        mutableIntStateOf(kotlin.random.Random.nextInt(styles.size))
    }
    val activeStyle = styles[celebrationIndex % styles.size]

    val infiniteTransition = rememberInfiniteTransition(label = "victory60fps")
    val animPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "victoryAnimPhase"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.52f))
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
            // Top dark chalkboard header with 60fps random winner animation & tap-to-switch badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(215.dp)
                    .background(DoodleColors.ChalkboardDark)
                    .clickable {
                        celebrationIndex = (celebrationIndex + 1) % styles.size
                    }
                    .padding(14.dp)
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
                            .weight(0.45f)
                            .fillMaxHeight()
                    ) {
                        drawCelebratingStickmanOnChalkboard(
                            style = activeStyle,
                            phase = animPhase
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(0.55f)
                            .padding(start = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = level.word.uppercase(),
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Black,
                            fontSize = 26.sp,
                            letterSpacing = 2.5.sp,
                            color = DoodleColors.DoodleYellow,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = level.clue,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = activeStyle.cheerSubtitle,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            color = DoodleColors.UfoBeamGreen,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Celebration Style Chip (Top-Right)
                SketchyContainer(
                    fillColor = DoodleColors.DoodleYellow,
                    cornerRadius = 12.dp,
                    strokeWidth = 1.5.dp,
                    shadowOffset = 1.5.dp,
                    onClick = { celebrationIndex = (celebrationIndex + 1) % styles.size },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .height(26.dp)
                ) {
                    Text(
                        text = activeStyle.badgeLabel,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        color = DoodleColors.InkBlack,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
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

private fun DrawScope.drawCelebratingStickmanOnChalkboard(
    style: VictoryCelebrationStyle = VictoryCelebrationStyle.DISCO_FEVER,
    phase: Float = 0f
) {
    val cx = size.width * 0.5f
    val cy = size.height * 0.55f
    val headR = 14.dp.toPx()
    val chalk = Color.White
    val sw = 3.2.dp.toPx()

    // Animated falling & swirling confetti particles
    for (i in 0 until 14) {
        val c = DoodleColors.BalloonPalette[i % DoodleColors.BalloonPalette.size]
        val rx = ((i * 0.19f + sin(phase + i) * 0.08f) % 1f).let { if (it < 0f) it + 1f else it }
        val ry = (((phase / (2f * PI.toFloat())) + i * 0.14f) % 1f)
        drawRoundRect(
            color = c,
            topLeft = Offset(rx * size.width, ry * size.height),
            size = Size(6.dp.toPx(), 4.dp.toPx()),
            cornerRadius = CornerRadius(1.5.dp.toPx(), 1.5.dp.toPx())
        )
    }

    when (style) {
        VictoryCelebrationStyle.DISCO_FEVER -> {
            // Disco floor tiles + groovy dance + party sunglasses
            for (ti in 0 until 5) {
                val tileW = (size.width - 16.dp.toPx()) / 5f
                val col = DoodleColors.BalloonPalette[(ti + (phase * 2f).toInt()) % DoodleColors.BalloonPalette.size]
                drawRoundRect(
                    color = col,
                    topLeft = Offset(8.dp.toPx() + ti * tileW, size.height - 10.dp.toPx()),
                    size = Size(tileW - 3.dp.toPx(), 6.dp.toPx()),
                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                )
            }
            val sway = sin(phase * 2f) * 8.dp.toPx()
            val bounce = kotlin.math.abs(sin(phase * 2f)) * 6.dp.toPx()
            val headCenter = Offset(cx + sway * 0.5f, cy - 28.dp.toPx() - bounce)
            val neckY = headCenter.y + headR
            val hipCenter = Offset(cx - sway * 0.4f, neckY + 28.dp.toPx())

            drawCircle(color = chalk, radius = headR, center = headCenter, style = Stroke(width = sw))
            // Cool golden sunglasses
            drawRect(
                color = DoodleColors.DoodleYellow,
                topLeft = Offset(headCenter.x - 10.dp.toPx(), headCenter.y - 4.dp.toPx()),
                size = Size(8.dp.toPx(), 5.dp.toPx())
            )
            drawRect(
                color = DoodleColors.DoodleYellow,
                topLeft = Offset(headCenter.x + 2.dp.toPx(), headCenter.y - 4.dp.toPx()),
                size = Size(8.dp.toPx(), 5.dp.toPx())
            )
            drawLine(chalk, Offset(headCenter.x, neckY), hipCenter, strokeWidth = sw, cap = StrokeCap.Round)
            val pointRight = sin(phase) >= 0f
            drawLine(
                chalk,
                Offset(headCenter.x, neckY + 6.dp.toPx()),
                if (pointRight) Offset(headCenter.x + 24.dp.toPx(), headCenter.y - 14.dp.toPx())
                else Offset(headCenter.x - 24.dp.toPx(), headCenter.y - 14.dp.toPx()),
                strokeWidth = sw,
                cap = StrokeCap.Round
            )
            drawLine(
                chalk,
                Offset(headCenter.x, neckY + 6.dp.toPx()),
                if (pointRight) Offset(headCenter.x - 20.dp.toPx(), neckY + 20.dp.toPx())
                else Offset(headCenter.x + 20.dp.toPx(), neckY + 20.dp.toPx()),
                strokeWidth = sw,
                cap = StrokeCap.Round
            )
            drawLine(chalk, hipCenter, Offset(cx - 16.dp.toPx(), size.height - 14.dp.toPx()), strokeWidth = sw, cap = StrokeCap.Round)
            drawLine(chalk, hipCenter, Offset(cx + 16.dp.toPx(), size.height - 14.dp.toPx()), strokeWidth = sw, cap = StrokeCap.Round)
        }

        VictoryCelebrationStyle.TROPHY_FLIP -> {
            // Golden podium + jumping champion hoisting trophy cup
            drawRoundRect(
                color = DoodleColors.DoodleYellow,
                topLeft = Offset(cx - 26.dp.toPx(), size.height - 22.dp.toPx()),
                size = Size(52.dp.toPx(), 18.dp.toPx()),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
            val jump = kotlin.math.abs(sin(phase * 2f)) * 16.dp.toPx()
            val headCenter = Offset(cx, cy - 22.dp.toPx() - jump)
            val neckY = headCenter.y + headR
            val hipY = neckY + 26.dp.toPx()

            drawCircle(color = chalk, radius = headR, center = headCenter, style = Stroke(width = sw))
            drawLine(chalk, Offset(cx, neckY), Offset(cx, hipY), strokeWidth = sw, cap = StrokeCap.Round)
            drawLine(chalk, Offset(cx, neckY + 5.dp.toPx()), Offset(cx - 18.dp.toPx(), headCenter.y - 14.dp.toPx()), strokeWidth = sw, cap = StrokeCap.Round)
            drawLine(chalk, Offset(cx, neckY + 5.dp.toPx()), Offset(cx + 18.dp.toPx(), headCenter.y - 14.dp.toPx()), strokeWidth = sw, cap = StrokeCap.Round)
            drawLine(chalk, Offset(cx, hipY), Offset(cx - 18.dp.toPx(), hipY + 20.dp.toPx()), strokeWidth = sw, cap = StrokeCap.Round)
            drawLine(chalk, Offset(cx, hipY), Offset(cx + 18.dp.toPx(), hipY + 20.dp.toPx()), strokeWidth = sw, cap = StrokeCap.Round)

            // Trophy cup above head
            drawRoundRect(
                color = DoodleColors.DoodleYellow,
                topLeft = Offset(cx - 11.dp.toPx(), headCenter.y - 34.dp.toPx()),
                size = Size(22.dp.toPx(), 16.dp.toPx()),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
        }

        VictoryCelebrationStyle.BALLOON_FIESTA -> {
            // Floating with party balloons
            val floatY = sin(phase) * 8.dp.toPx()
            val headCenter = Offset(cx + 6.dp.toPx(), cy - 10.dp.toPx() + floatY)
            val handPos = Offset(cx - 16.dp.toPx(), cy - 18.dp.toPx() + floatY)
            for (b in 0..2) {
                val bx = handPos.x + (b - 1) * 16.dp.toPx()
                val by = handPos.y - 34.dp.toPx() - (if (b == 1) 6.dp.toPx() else 0f)
                drawLine(chalk.copy(alpha = 0.7f), handPos, Offset(bx, by + 10.dp.toPx()), strokeWidth = 1.5.dp.toPx())
                drawOval(
                    color = DoodleColors.BalloonPalette[b],
                    topLeft = Offset(bx - 9.dp.toPx(), by - 11.dp.toPx()),
                    size = Size(18.dp.toPx(), 22.dp.toPx())
                )
            }
            drawCircle(color = chalk, radius = headR, center = headCenter, style = Stroke(width = sw))
            val neckY = headCenter.y + headR
            val hipY = neckY + 26.dp.toPx()
            drawLine(chalk, Offset(headCenter.x, neckY), Offset(headCenter.x, hipY), strokeWidth = sw, cap = StrokeCap.Round)
            drawLine(chalk, Offset(headCenter.x, neckY + 5.dp.toPx()), handPos, strokeWidth = sw, cap = StrokeCap.Round)
            drawLine(chalk, Offset(headCenter.x, neckY + 5.dp.toPx()), Offset(headCenter.x + 22.dp.toPx(), neckY - 8.dp.toPx()), strokeWidth = sw, cap = StrokeCap.Round)
            drawLine(chalk, Offset(headCenter.x, hipY), Offset(headCenter.x - 12.dp.toPx(), hipY + 22.dp.toPx()), strokeWidth = sw, cap = StrokeCap.Round)
            drawLine(chalk, Offset(headCenter.x, hipY), Offset(headCenter.x + 12.dp.toPx(), hipY + 22.dp.toPx()), strokeWidth = sw, cap = StrokeCap.Round)
        }

        VictoryCelebrationStyle.SUPER_HERO -> {
            // Soaring superhero stickman with fluttering red cape
            val flyX = cx + cos(phase) * 10.dp.toPx()
            val flyY = cy - 6.dp.toPx() + sin(phase * 2f) * 7.dp.toPx()
            val capePath = Path().apply {
                moveTo(flyX - 4.dp.toPx(), flyY - 4.dp.toPx())
                quadraticTo(
                    flyX - 26.dp.toPx(),
                    flyY + 6.dp.toPx() + sin(phase * 3f) * 8.dp.toPx(),
                    flyX - 42.dp.toPx(),
                    flyY + 20.dp.toPx()
                )
                lineTo(flyX - 30.dp.toPx(), flyY + 30.dp.toPx())
                close()
            }
            drawPath(capePath, color = DoodleColors.WrongRed)
            val headCenter = Offset(flyX + 14.dp.toPx(), flyY - 12.dp.toPx())
            drawCircle(color = chalk, radius = headR, center = headCenter, style = Stroke(width = sw))
            drawLine(chalk, Offset(flyX + 4.dp.toPx(), flyY - 2.dp.toPx()), Offset(flyX - 18.dp.toPx(), flyY + 18.dp.toPx()), strokeWidth = sw, cap = StrokeCap.Round)
            drawLine(chalk, Offset(flyX + 2.dp.toPx(), flyY), Offset(flyX + 34.dp.toPx(), flyY - 18.dp.toPx()), strokeWidth = sw, cap = StrokeCap.Round)
            drawLine(chalk, Offset(flyX - 18.dp.toPx(), flyY + 18.dp.toPx()), Offset(flyX - 38.dp.toPx(), flyY + 32.dp.toPx()), strokeWidth = sw, cap = StrokeCap.Round)
            drawLine(chalk, Offset(flyX - 18.dp.toPx(), flyY + 18.dp.toPx()), Offset(flyX - 32.dp.toPx(), flyY + 38.dp.toPx()), strokeWidth = sw, cap = StrokeCap.Round)
        }
    }
}

// ============================================================================
// 12. GAME OVER BOTTOM SHEET (NEVER BLOCKS STICKMAN ANIMATION) & TWO-PLAYER
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
    var peekMinimized by remember { mutableStateOf(false) }

    // Docked at BottomCenter with a clear upper viewport so the Stickman death animation in the top half is NEVER covered!
    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        SketchyContainer(
            fillColor = DoodleColors.PaperOffWhite,
            cornerRadius = 24.dp,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 440.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⬆️ Watch Stickman Above!",
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp,
                        color = DoodleColors.InkBlue
                    )
                    SketchyContainer(
                        fillColor = Color.White,
                        cornerRadius = 10.dp,
                        strokeWidth = 1.4.dp,
                        shadowOffset = 1.5.dp,
                        onClick = { peekMinimized = !peekMinimized },
                        modifier = Modifier.height(26.dp)
                    ) {
                        Text(
                            text = if (peekMinimized) "🔼 Expand" else "👁️ Peek Scene",
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = DoodleColors.InkBlack,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = peril.gameOverTitle,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    color = DoodleColors.WrongRed,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("game_over_title")
                )

                if (!peekMinimized) {
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = peril.gameOverDescription,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = DoodleColors.InkBlack,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Clue: ${level.clue}",
                        fontFamily = fontFamily,
                        fontStyle = FontStyle.Italic,
                        fontSize = 13.sp,
                        color = DoodleColors.InkBlue,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    SketchyContainer(
                        fillColor = DoodleColors.DoodleYellow,
                        cornerRadius = 22.dp,
                        onClick = onRescue,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("rescue_button")
                    ) {
                        Text(
                            text = "Rescue (+3 Lives • 250 Coins)",
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = DoodleColors.InkBlack
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SketchyContainer(
                            fillColor = Color.White,
                            cornerRadius = 22.dp,
                            onClick = onRetry,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("retry_button")
                        ) {
                            Text(
                                text = "Try Again",
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = DoodleColors.InkBlack
                            )
                        }

                        SketchyContainer(
                            fillColor = Color(0xFFF5F5F5),
                            cornerRadius = 22.dp,
                            onClick = onMainMenu,
                            modifier = Modifier
                                .weight(0.85f)
                                .height(44.dp)
                        ) {
                            Text(
                                text = "Back to Main Menu",
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = DoodleColors.InkBlack.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
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

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PerilType.entries.forEach { p ->
                        val lvlNum = p.representativeLevel
                        val selected = selectedPerilNum == lvlNum
                        SketchyContainer(
                            fillColor = if (selected) DoodleColors.DoodleYellow else DoodleColors.PaperOffWhite,
                            cornerRadius = 12.dp,
                            strokeWidth = 1.5.dp,
                            onClick = { selectedPerilNum = lvlNum },
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text(
                                text = p.displayName,
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
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
