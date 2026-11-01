package com.example

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
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
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt
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
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.heightIn
import androidx.compose.ui.graphics.Brush
import com.example.data.JUNK_TEST_UIDS
import com.example.data.PlayerAccount
import com.example.data.PlayerAccountRepository
import com.example.data.computePasswordHash
import com.example.ui.RegistrationAuthScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject


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
    val LavaRed = Color(0xFFD84315)
    val LavaYellow = Color(0xFFFFEB3B)
    val CrateStone = Color(0xFFD7CCC8)
    val CrateStoneLight = Color(0xFFD7CCC8)
    val CrateStoneDark = Color(0xFFA1887F)
    val VineRopeBrown = Color(0xFF795548)

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
// 2. 20-TIER RANK TITLES & DYNAMIC AURA SYSTEM + 15 PERIL SCENARIOS (1,000 LEVELS)
// ============================================================================

/**
 * Complete 20-Tier Rank Progression & Animated Aura System across Levels 1–1,000.
 * Each tier spans 50 levels and drives both the Rank Banner and procedural Canvas aura.
 */
enum class RankTier(
    val tierNumber: Int,
    val title: String,
    val levelRange: IntRange,
    val auraDescription: String,
    val primaryColor: Color,
    val accentColor: Color
) {
    DOODLE_NOVICE(
        1, "Doodle Novice", 1..50,
        "Soft pale-yellow hand-drawn hatching lines pulse",
        Color(0xFFFFF59D), Color(0xFFFBC02D)
    ),
    INK_APPRENTICE(
        2, "Ink Apprentice", 51..100,
        "Gentle flowing blue ink-splash rings",
        Color(0xFF81D4FA), Color(0xFF0288D1)
    ),
    SKETCH_MASTER(
        3, "Sketch Master", 101..150,
        "Rotating green sketchy cross-hatch starburst rays",
        Color(0xFFA5D6A7), Color(0xFF2E7D32)
    ),
    GRAPH_PAPER_WIZARD(
        4, "Graph Paper Wizard", 151..200,
        "Shimmering purple geometric coordinate grid pulse",
        Color(0xFFCE93D8), Color(0xFF7B1FA2)
    ),
    EPIC_DOODLE_LEGEND(
        5, "Epic Doodle Legend", 201..250,
        "Radiant golden flame aura with floating doodle sparks",
        Color(0xFFFFD54F), Color(0xFFFF6F00)
    ),
    MYTHIC_INK_GOD(
        6, "Mythic Ink God", 251..300,
        "Multi-colored rainbow pulsating doodle aura with lightning",
        Color(0xFFFF80AB), Color(0xFF651FFF)
    ),
    DIMENSION_SKETCHER(
        7, "Dimension Sketcher", 301..350,
        "Matrix Green Code-Doodle Grid",
        Color(0xFF69F0AE), Color(0xFF00C853)
    ),
    COSMIC_DOODLE_OVERLORD(
        8, "Cosmic Doodle Overlord", 351..400,
        "Orbiting Planet & Star Doodles",
        Color(0xFF9FA8DA), Color(0xFF283593)
    ),
    QUANTUM_INK_ARCHITECT(
        9, "Quantum Ink Architect", 401..450,
        "Pulsing Atomic Rings",
        Color(0xFF80DEEA), Color(0xFF00838F)
    ),
    INFINITY_PAPER_SOVEREIGN(
        10, "Infinity Paper Sovereign", 451..500,
        "Endless Spiral Galaxy Sketch",
        Color(0xFFB39DDB), Color(0xFF4527A0)
    ),
    CELESTIAL_SCRIBBLER(
        11, "Celestial Scribbler", 501..550,
        "Glowing Constellation Lines",
        Color(0xFFFFE082), Color(0xFF1565C0)
    ),
    NEBULA_DOODLE_WALKER(
        12, "Nebula Doodle Walker", 551..600,
        "Swirling Purple/Pink Cosmic Dust",
        Color(0xFFF48FB1), Color(0xFFAD1457)
    ),
    VORTEX_LINE_MASTER(
        13, "Vortex Line Master", 601..650,
        "Spiraling Blackhole Doodle Ring",
        Color(0xFF90A4AE), Color(0xFF263238)
    ),
    SOLAR_PRISM_FORGER(
        14, "Solar Prism Forger", 651..700,
        "Refracting Prismatic Sunbeam Sketch",
        Color(0xFFFFCC80), Color(0xFFE65100)
    ),
    CHRONO_GRAPHITE_HERO(
        15, "Chrono Graphite Hero", 701..750,
        "Ticking Clock Gear & Pendulum Doodles",
        Color(0xFFD7CCC8), Color(0xFF4E342E)
    ),
    ETHER_SKETCH_WARDEN(
        16, "Ether Sketch Warden", 751..800,
        "Shimmering Ghostly Blue Flame",
        Color(0xFF80CBC4), Color(0xFF00695C)
    ),
    TITANIUM_INK_BERSERKER(
        17, "Titanium Ink Berserker", 801..850,
        "Heavy Angular Metallic Crosshatch Sparks",
        Color(0xFFB0BEC5), Color(0xFFD50000)
    ),
    ASTRAL_PAPER_SOVEREIGN(
        18, "Astral Paper Sovereign", 851..900,
        "Floating Golden Runes & Stars",
        Color(0xFFFFF176), Color(0xFFF57F17)
    ),
    OMNISCIENT_DOODLE_SAGE(
        19, "Omniscient Doodle Sage", 901..950,
        "Glowing Eye & Ancient Scroll Doodles",
        Color(0xFFC5E1A5), Color(0xFF33691E)
    ),
    SUPREME_DOODLE_GOD(
        20, "The Supreme Doodle God", 951..1000,
        "Ultimate Pulsing Gold & Rainbow Cosmos Aura",
        Color(0xFFFFD700), Color(0xFFD500F9)
    );

    val tierIndex: Int
        get() = tierNumber

    val secondaryColor: Color
        get() = accentColor

    val displayTierTitle: String
        get() = "Tier $tierNumber: $title"

    val nextTierStartLevel: Int?
        get() = if (tierNumber < entries.size) levelRange.last + 1 else null

    fun progressSubLabel(currentLevel: Int): String {
        val safeLevel = currentLevel.coerceAtLeast(1)
        val nextLv = nextTierStartLevel
        return if (nextLv != null) {
            "Progress: Level $safeLevel / ${levelRange.last} (Next Tier at Lv $nextLv)"
        } else {
            "Progress: Level $safeLevel / ${levelRange.last} (Max Rank Tier!)"
        }
    }

    fun tierProgressFraction(currentLevel: Int): Float {
        val span = (levelRange.last - levelRange.first + 1).coerceAtLeast(1)
        val levelInTier = (currentLevel - levelRange.first + 1).coerceIn(1, span)
        return (levelInTier.toFloat() / span.toFloat()).coerceIn(0f, 1f)
    }

    companion object {
        fun forLevel(levelNumber: Int): RankTier {
            val maxDefinedLevel = entries.last().levelRange.last
            val clamped = levelNumber.coerceIn(1, maxDefinedLevel)
            return entries.firstOrNull { clamped in it.levelRange }
                ?: entries[((clamped - 1) / 50).coerceIn(0, entries.lastIndex)]
        }
    }
}

/**
 * 15-Tier Dynamic Peril Scenarios System rotating every 20 levels and cycling across 1,000 levels:
 * 1. SHARK_BALLOONS (Levels 1–20)
 * 2. UFO_ABDUCTION (Levels 21–40)
 * 3. VOLCANO_BLOCKS / VOLCANO_LAVA (Levels 41–60)
 * 4. GIANT_VENUS_FLYTRAP (Levels 61–80)
 * 5. DINO_STEALTH / SLEEPING_TREX (Levels 81–100)
 * 6. LEAKY_SUBMARINE (Levels 101–120)
 * 7. TIGHTROPE_SAW (Levels 121–140)
 * 8. METEOR_SHOWER (Levels 141–160)
 * 9. MAD_SCIENTIST_POTION (Levels 161–180)
 * 10. PIRATE_PLANK (Levels 181–200)
 * 11. SPIDER_WEB (Levels 201–220)
 * 12. QUICKSAND_PIT (Levels 221–240)
 * 13. GHOST_MANOR (Levels 241–260)
 * 14. TICKING_TIME_BOMB (Levels 261–280)
 * 15. DRAGON_CAVE (Levels 281–300)
 */
enum class PerilType(
    val displayName: String,
    val worldRangeLabel: String,
    val panicBubbleText: String,
    val gameOverTitle: String,
    val gameOverDescription: String,
    val extraLifeLabel: String,
    val hitEffectLabel: String,
    val hitSound: DoodleSoundEffect,
    val overSound: DoodleSoundEffect
) {
    SHARK_BALLOONS(
        displayName = "Shark & Balloons",
        worldRangeLabel = "Levels 1–20",
        panicBubbleText = "Come on, you can do that!",
        gameOverTitle = "SPLASH! SHARK SNACK",
        gameOverDescription = "All 6 balloons popped! The shark caught up.",
        extraLifeLabel = "+1 Balloon attached!",
        hitEffectLabel = "POP!",
        hitSound = DoodleSoundEffect.POP,
        overSound = DoodleSoundEffect.SPLASH
    ),
    UFO_ABDUCTION(
        displayName = "UFO Abduction",
        worldRangeLabel = "Levels 21–40",
        panicBubbleText = "Beam me up... wait, no!",
        gameOverTitle = "ZAP! ABDUCTED!",
        gameOverDescription = "The UFO tractor beam pulled the stickman aboard!",
        extraLifeLabel = "+1 Gravity Anchor!",
        hitEffectLabel = "ZAP!",
        hitSound = DoodleSoundEffect.ZAP,
        overSound = DoodleSoundEffect.ZAP
    ),
    VOLCANO_BLOCKS(
        displayName = "Volcano Lava",
        worldRangeLabel = "Levels 41–60",
        panicBubbleText = "My shoes are melting!",
        gameOverTitle = "SIZZLE! MELTDOWN!",
        gameOverDescription = "All 6 stone crates crumbled into the bubbling lava!",
        extraLifeLabel = "+1 Stone Crate stacked!",
        hitEffectLabel = "CRUMBLE!",
        hitSound = DoodleSoundEffect.CRUNCH,
        overSound = DoodleSoundEffect.CRUNCH
    ),
    GIANT_VENUS_FLYTRAP(
        displayName = "Venus Flytrap",
        worldRangeLabel = "Levels 61–80",
        panicBubbleText = "Vegetarian option!",
        gameOverTitle = "CHOMP! PLANT FOOD!",
        gameOverDescription = "The caterpillars chewed through all 6 vine nodes!",
        extraLifeLabel = "+1 Vine Node regrown!",
        hitEffectLabel = "SNAP!",
        hitSound = DoodleSoundEffect.CRUNCH,
        overSound = DoodleSoundEffect.CRUNCH
    ),
    DINO_STEALTH(
        displayName = "Sleeping T-Rex",
        worldRangeLabel = "Levels 81–100",
        panicBubbleText = "Shhh! Don't wake Rex!",
        gameOverTitle = "ROOOAR! REX AWOKE!",
        gameOverDescription = "The Noise Meter maxed out and woke the giant T-Rex!",
        extraLifeLabel = "-1 Noise Bar hushed!",
        hitEffectLabel = "CLANG!",
        hitSound = DoodleSoundEffect.CLANG,
        overSound = DoodleSoundEffect.CLANG
    ),
    LEAKY_SUBMARINE(
        displayName = "Leaky Submarine",
        worldRangeLabel = "Levels 101–120",
        panicBubbleText = "Glub glub! Holding my breath!",
        gameOverTitle = "GLUB! TANK FLOODED!",
        gameOverDescription = "All 6 bolts popped and the glass tank filled with water!",
        extraLifeLabel = "+1 Tank Bolt sealed!",
        hitEffectLabel = "BURST!",
        hitSound = DoodleSoundEffect.POP,
        overSound = DoodleSoundEffect.SPLASH
    ),
    TIGHTROPE_SAW(
        displayName = "Tightrope Saw",
        worldRangeLabel = "Levels 121–140",
        panicBubbleText = "Watch the spinning blade!",
        gameOverTitle = "BZZZT! ROPE SLICED!",
        gameOverDescription = "The buzzsaw sliced through all 6 tightrope strands!",
        extraLifeLabel = "+1 Tightrope Strand woven!",
        hitEffectLabel = "BZZT!",
        hitSound = DoodleSoundEffect.CLANG,
        overSound = DoodleSoundEffect.CRUNCH
    ),
    METEOR_SHOWER(
        displayName = "Meteor Shower",
        worldRangeLabel = "Levels 141–160",
        panicBubbleText = "The sky is falling!",
        gameOverTitle = "KABOOM! METEOR STRIKE!",
        gameOverDescription = "All 6 dome shield layers shattered under the meteors!",
        extraLifeLabel = "+1 Dome Shield recharged!",
        hitEffectLabel = "CRASH!",
        hitSound = DoodleSoundEffect.CRUNCH,
        overSound = DoodleSoundEffect.CRUNCH
    ),
    MAD_SCIENTIST_POTION(
        displayName = "Mad Scientist Potion",
        worldRangeLabel = "Levels 161–180",
        panicBubbleText = "That cauldron smells fizzy!",
        gameOverTitle = "FIZZZ! MUTANT BREW!",
        gameOverDescription = "The rope dissolved and dropped the stickman into the vat!",
        extraLifeLabel = "+1 Lab Rope reinforced!",
        hitEffectLabel = "FIZZ!",
        hitSound = DoodleSoundEffect.POP,
        overSound = DoodleSoundEffect.SPLASH
    ),
    PIRATE_PLANK(
        displayName = "Pirate Plank",
        worldRangeLabel = "Levels 181–200",
        panicBubbleText = "Yarrr! Kraken below!",
        gameOverTitle = "SPLASH! WALKED THE PLANK!",
        gameOverDescription = "You ran out of plank steps above the hungry Kraken!",
        extraLifeLabel = "+1 Plank Step back!",
        hitEffectLabel = "CREAK!",
        hitSound = DoodleSoundEffect.CRUNCH,
        overSound = DoodleSoundEffect.SPLASH
    ),
    SPIDER_WEB(
        displayName = "Giant Spider Web",
        worldRangeLabel = "Levels 201–220",
        panicBubbleText = "Shoo, eight-legged fella!",
        gameOverTitle = "WRAP! SILK COCOON!",
        gameOverDescription = "The giant doodle spider descended all 6 web rings!",
        extraLifeLabel = "+1 Web Strand cleared!",
        hitEffectLabel = "TWANG!",
        hitSound = DoodleSoundEffect.POP,
        overSound = DoodleSoundEffect.CRUNCH
    ),
    QUICKSAND_PIT(
        displayName = "Quicksand Pit",
        worldRangeLabel = "Levels 221–240",
        panicBubbleText = "Don't wiggle, it sinks faster!",
        gameOverTitle = "GLOMP! SWALLOWED BY SAND!",
        gameOverDescription = "The desert quicksand pit pulled the stickman under!",
        extraLifeLabel = "+1 Vine Pull upward!",
        hitEffectLabel = "SINK!",
        hitSound = DoodleSoundEffect.CRUNCH,
        overSound = DoodleSoundEffect.CRUNCH
    ),
    GHOST_MANOR(
        displayName = "Ghost Manor",
        worldRangeLabel = "Levels 241–260",
        panicBubbleText = "Don't let the candle go out!",
        gameOverTitle = "BOO! SPOOKED SOLID!",
        gameOverDescription = "All 6 candle segments melted and the manor ghosts pounced!",
        extraLifeLabel = "+1 Candle Wax relit!",
        hitEffectLabel = "WHOOSH!",
        hitSound = DoodleSoundEffect.ZAP,
        overSound = DoodleSoundEffect.ZAP
    ),
    TICKING_TIME_BOMB(
        displayName = "Ticking Time Bomb",
        worldRangeLabel = "Levels 261–280",
        panicBubbleText = "Cut the red wire! Or blue?!",
        gameOverTitle = "BOOM! DYNAMITE BLAST!",
        gameOverDescription = "The 6-segment fuse burned all the way to the dynamite!",
        extraLifeLabel = "+1 Fuse Segment lengthened!",
        hitEffectLabel = "SIZZLE!",
        hitSound = DoodleSoundEffect.ZAP,
        overSound = DoodleSoundEffect.CRUNCH
    ),
    DRAGON_CAVE(
        displayName = "Dragon Cave",
        worldRangeLabel = "Levels 281–300",
        panicBubbleText = "Nice dragon... stay asleep!",
        gameOverTitle = "ROAR! DRAGON FIREBALL!",
        gameOverDescription = "The dragon's 6 rage embers ignited into a blazing inferno!",
        extraLifeLabel = "-1 Dragon Ember cooled!",
        hitEffectLabel = "ROAR!",
        hitSound = DoodleSoundEffect.CLANG,
        overSound = DoodleSoundEffect.CRUNCH
    );

    companion object {
        val VOLCANO_LAVA: PerilType get() = VOLCANO_BLOCKS
        val SLEEPING_TREX: PerilType get() = DINO_STEALTH

        /**
         * Rotates every 20 levels across all 15 Peril Scenarios (1–20, 21–40, ..., 281–300)
         * and cycles continuously up to Level 1,000.
         */
        fun forLevel(levelNumber: Int): PerilType {
            val safeLvl = if (levelNumber <= 0) 1 else levelNumber
            val tierIdx = ((safeLvl - 1) / 20) % entries.size
            return entries[tierIdx]
        }
    }

    val representativeLevel: Int
        get() = ordinal * 20 + 1
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

    val rankTier: RankTier
        get() = RankTier.forLevel(if (number <= 0) 1 else number)

    /** Every 5th level is a Boss Level with 3 bomb letters on the keyboard! */
    val isBossLevel: Boolean
        get() = isAdventure || (number > 0 && number % 5 == 0)

    /** Levels 150+ (and 200+) start with Frozen Vowels (A, E, I, O, U) locked until 3 consonants are guessed! */
    val hasFrozenVowels: Boolean
        get() = number >= 150
}

enum class ScreenState {
    REGISTRATION,
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
        writeIntLE(16)
        writeShortLE(1)
        writeShortLE(1)
        writeIntLE(sampleRate)
        writeIntLE(sampleRate * 2)
        writeShortLE(2)
        writeShortLE(16)
        writeAscii("data")
        writeIntLE(dataSize)
        for (s in samples) {
            writeShortLE(s.toInt())
        }
        return out.toByteArray()
    }
}

/**
 * Structured 1,000-Level Campaign Database Engine spanning 8 hand-crafted categories:
 * Animals, Science, History, Pop Culture, Geography, Food, Tech, and Mythology.
 */
object CampaignDatabase {
    private data class WordEntry(val word: String, val clue: String, val category: String)

    private val CORE_100: List<WordEntry> = listOf(
        WordEntry("DIAMOND", "Hard Science: Guess a precious carbon gemstone", "Science"),
        WordEntry("CHINA", "History: What country was paper invented in?", "History"),
        WordEntry("PRANK", "Pop Culture: April Fools' Day tradition", "Pop Culture"),
        WordEntry("CAKE", "Food: Sweet baked treat with birthday candles", "Food"),
        WordEntry("GIRAFFE", "Boss Level: Tallest African mammal with a long neck", "Animals"),
        WordEntry("COMPASS", "Tech: Magnetic navigation tool that points north", "Tech"),
        WordEntry("VOLCANO", "Geography: Mountain that erupts with glowing lava", "Geography"),
        WordEntry("PENGUIN", "Animals: Tuxedo-wearing flightless bird of Antarctica", "Animals"),
        WordEntry("RAINBOW", "Science: Colorful optical spectrum arc seen after rain", "Science"),
        WordEntry("PIRATE", "Boss Level: High-seas buccaneer seeking buried gold", "History"),
        WordEntry("CACTUS", "Animals: Spiky desert succulent that stores water", "Animals"),
        WordEntry("GUITAR", "Pop Culture: Six-stringed acoustic or electric instrument", "Pop Culture"),
        WordEntry("OCTOPUS", "Animals: Clever eight-armed cephalopod of the sea", "Animals"),
        WordEntry("POPCORN", "Food: Buttery popped corn snack at the cinema", "Food"),
        WordEntry("ASTRONAUT", "Boss Level: Explorer who travels into outer space", "Science"),
        WordEntry("IGLOO", "Geography: Dome-shaped Arctic shelter built from snow", "Geography"),
        WordEntry("TORNADO", "Science: Violently rotating funnel column of wind", "Science"),
        WordEntry("PANCAKE", "Food: Flat breakfast griddle cake served with syrup", "Food"),
        WordEntry("DINOSAUR", "Animals: Prehistoric Mesozoic reptile that ruled Earth", "Animals"),
        WordEntry("TREASURE", "Boss Level: Chest overflowing with doubloons and jewels", "Pop Culture"),
        WordEntry("ELEPHANT", "Animals: Largest land mammal with a trunk and tusks", "Animals"),
        WordEntry("BURRITO", "Food: Flour tortilla wrapped around savory fillings", "Food"),
        WordEntry("PYRAMIDS", "History: Ancient monumental pharaoh tombs in Giza", "History"),
        WordEntry("MOONWALK", "Pop Culture: Iconic backward gliding dance step", "Pop Culture"),
        WordEntry("ALGORITHM", "Boss Level: Step-by-step problem-solving code rules", "Tech"),
        WordEntry("EGYPT", "Geography: Land of the Nile River and Sphinx", "Geography"),
        WordEntry("SPACESHIP", "Tech: Interstellar vessel built for star travel", "Tech"),
        WordEntry("TELESCOPE", "Science: Optical instrument for viewing distant galaxies", "Science"),
        WordEntry("CHAMELEON", "Animals: Lizard famous for shifting its skin colors", "Animals"),
        WordEntry("CROISSANT", "Boss Level: Flaky crescent-shaped French pastry", "Food"),
        WordEntry("AMAZON", "Geography: Vast South American river and rainforest", "Geography"),
        WordEntry("CARNIVAL", "Pop Culture: Festive traveling fair with ferris wheels", "Pop Culture"),
        WordEntry("GRAVITY", "Science: Fundamental force keeping planets in orbit", "Science"),
        WordEntry("DOLPHIN", "Animals: Playful marine mammal that uses echolocation", "Animals"),
        WordEntry("SPAGHETTI", "Boss Level: Long thin Italian pasta served with sauce", "Food"),
        WordEntry("ATLANTIS", "Mythology: Legendary island kingdom lost beneath the sea", "Mythology"),
        WordEntry("FIREWORKS", "Pop Culture: Dazzling sky explosions on New Year's Eve", "Pop Culture"),
        WordEntry("MOLECULE", "Science: Group of atoms bonded together chemically", "Science"),
        WordEntry("KANGAROO", "Animals: Australian marsupial that hops on hind legs", "Animals"),
        WordEntry("PINEAPPLE", "Boss Level: Tropical fruit with a spiky crown", "Food"),
        WordEntry("GLACIER", "Geography: Massive slow-moving river of alpine ice", "Geography"),
        WordEntry("SUPERHERO", "Pop Culture: Masked comic book defender of justice", "Pop Culture"),
        WordEntry("SATELLITE", "Tech: Orbital craft relaying signals around Earth", "Tech"),
        WordEntry("BUTTERFLY", "Animals: Winged insect emerging from a chrysalis", "Animals"),
        WordEntry("CHOCOLATE", "Boss Level: Sweet treat made from roasted cacao beans", "Food"),
        WordEntry("COLOSSEUM", "History: Famous oval gladiator amphitheater in Rome", "History"),
        WordEntry("DETECTIVE", "Pop Culture: Investigator who solves tricky mysteries", "Pop Culture"),
        WordEntry("MAGNETISM", "Science: Force of attraction between iron poles", "Science"),
        WordEntry("FLAMINGO", "Animals: Pink wading bird that stands on one leg", "Animals"),
        WordEntry("AVOCADO", "Boss Level: Creamy green stone fruit in guacamole", "Food"),
        WordEntry("HIMALAYAS", "Geography: Towering mountain range home to Everest", "Geography"),
        WordEntry("MAGICIAN", "Pop Culture: Illusionist who pulls rabbits from a hat", "Pop Culture"),
        WordEntry("ELECTRON", "Science: Negatively charged subatomic particle", "Science"),
        WordEntry("CHEETAH", "Animals: Fastest spotted land feline on Earth", "Animals"),
        WordEntry("CINNAMON", "Boss Level: Warm aromatic spice from inner tree bark", "Food"),
        WordEntry("PARTHENON", "History: Marble temple on the Athenian Acropolis", "History"),
        WordEntry("SKATEBOARD", "Pop Culture: Four-wheeled deck used for kickflips", "Pop Culture"),
        WordEntry("SUPERNOVA", "Science: Brilliant stellar explosion of a dying star", "Science"),
        WordEntry("CROCODILE", "Animals: Armored reptile lurking in tropical rivers", "Animals"),
        WordEntry("MACARONI", "Boss Level: Curved elbow pasta baked with cheddar", "Food"),
        WordEntry("CLEOPATRA", "History: Famous last Ptolemaic ruler of ancient Egypt", "History"),
        WordEntry("SAXOPHONE", "Pop Culture: Brass woodwind instrument iconic in jazz", "Pop Culture"),
        WordEntry("QUANTUM", "Science: Physics of the smallest discrete energy packets", "Science"),
        WordEntry("JELLYFISH", "Animals: Translucent ocean drifter with tentacles", "Animals"),
        WordEntry("BLUEBERRY", "Boss Level: Small round indigo berry baked in muffins", "Food"),
        WordEntry("SAMURAI", "History: Noble armored sword warrior of feudal Japan", "History"),
        WordEntry("MARATHON", "Pop Culture: Grueling endurance footrace of 26.2 miles", "Pop Culture"),
        WordEntry("ROBOTICS", "Tech: Engineering of autonomous intelligent machines", "Tech"),
        WordEntry("PLATYPUS", "Animals: Duck-billed egg-laying mammal of Australia", "Animals"),
        WordEntry("ESPRESSO", "Boss Level: Concentrated Italian dark roast coffee shot", "Food"),
        WordEntry("VIKINGS", "History: Norse seafarers who sailed dragon longships", "History"),
        WordEntry("ORCHESTRA", "Pop Culture: Large ensemble of symphonic musicians", "Pop Culture"),
        WordEntry("MICROCHIP", "Tech: Tiny silicon integrated circuit inside phones", "Tech"),
        WordEntry("PORCUPINE", "Animals: Herbivorous rodent protected by sharp quills", "Animals"),
        WordEntry("WATERMELON", "Boss Level: Giant green picnic fruit with sweet red flesh", "Food"),
        WordEntry("STONEHENGE", "History: Prehistoric ring of standing Sarsen stones", "History"),
        WordEntry("SPOTLIGHT", "Pop Culture: Bright stage beam following the lead star", "Pop Culture"),
        WordEntry("CHROMOSOME", "Science: Thread-like structure carrying DNA genes", "Science"),
        WordEntry("CHIMPANZEE", "Animals: Clever great ape known to craft tools", "Animals"),
        WordEntry("CHEESECAKE", "Boss Level: Rich creamy dessert on a graham crust", "Food"),
        WordEntry("GALAPAGOS", "Geography: Pacific volcanic islands studied by Darwin", "Geography"),
        WordEntry("BOOMERANG", "Pop Culture: Curved throwing stick that returns to you", "Pop Culture"),
        WordEntry("FREQUENCY", "Science: Number of wave cycles per second in Hertz", "Science"),
        WordEntry("RHINOCEROS", "Animals: Heavy herbivore with an armored horn snout", "Animals"),
        WordEntry("PISTACHIO", "Boss Level: Green nut inside a naturally splitting shell", "Food"),
        WordEntry("ANTARCTICA", "Geography: Frozen southernmost continent on Earth", "Geography"),
        WordEntry("JACKPOT", "Pop Culture: Biggest grand prize won on a lucky spin", "Pop Culture"),
        WordEntry("ASTEROID", "Science: Rocky minor planet orbiting the inner sun", "Science"),
        WordEntry("WOLVERINE", "Animals: Fierce northern mammal of the weasel family", "Animals"),
        WordEntry("QUESADILLA", "Boss Level: Toasted tortilla filled with melted cheese", "Food"),
        WordEntry("LABYRINTH", "Mythology: Cretan maze built to hold the Minotaur", "Mythology"),
        WordEntry("ACROBATICS", "Pop Culture: High-flying trapeze stunts at the circus", "Pop Culture"),
        WordEntry("HOLOGRAM", "Tech: Three-dimensional projection made with lasers", "Tech"),
        WordEntry("ALBATROSS", "Animals: Giant ocean seabird with a massive wingspan", "Animals"),
        WordEntry("GUACAMOLE", "Boss Level: Zesty Mexican avocado dip mashed with lime", "Food"),
        WordEntry("MESOPOTAMIA", "History: Cradle of civilization between Tigris and Euphrates", "History"),
        WordEntry("CLIFFHANGER", "Pop Culture: Suspenseful ending that leaves you waiting", "Pop Culture"),
        WordEntry("EXOPLANET", "Science: World orbiting a star outside our solar system", "Science"),
        WordEntry("CAPPUCCINO", "Food: Espresso topped with steamed milk microfoam", "Food"),
        WordEntry("CYBERSPACE", "Final Century Boss: Global digital universe of computer networks", "Tech")
    )

    private val CATEGORY_POOLS: List<Pair<String, List<Pair<String, String>>>> = listOf(
        "Animals" to listOf(
            "MEERKAT" to "Upright-standing desert mongoose sentry",
            "NARWHAL" to "Arctic whale famous for its spiral unicorn tusk",
            "AXOLOTL" to "Smiling Mexican walking salamander with feathery gills",
            "PANGOLIN" to "Scaly anteater mammal that rolls into an armored ball",
            "PELICAN" to "Coastal bird with a huge scoop-like throat pouch",
            "WALRUS" to "Whiskered Arctic marine mammal with long ivory tusks",
            "SCORPION" to "Desert arachnid with pincers and a venomous tail stinger",
            "HEDGEHOG" to "Small nocturnal mammal covered in protective spines",
            "ARMADILLO" to "Burrowing mammal with leathery bony armor shells",
            "MANATEE" to "Gentle aquatic herbivore nicknamed the sea cow",
            "CAPYBARA" to "World's largest semi-aquatic rodent from South America",
            "ORANGUTAN" to "Reddish-haired great ape of Borneo and Sumatra",
            "SALAMANDER" to "Moist-skinned amphibian capable of regrowing limbs",
            "DRAGONFLY" to "Agile quatre-winged insect hovering over ponds",
            "HUMMINGBIRD" to "Tiny bird whose wings beat in a rapid figure-eight",
            "KINGFISHER" to "Brightly colored bird that dives headfirst for fish",
            "BARRACUDA" to "Ray-finned predatory fish with fang-like teeth",
            "TARANTULA" to "Large hairy burrowing spider of warm climates",
            "KOALA" to "Eucalyptus-eating arboreal marsupial of Australia",
            "LEMUR" to "Ring-tailed primate endemic to the island of Madagascar",
            "KOMODO" to "Island home to the world's largest living monitor lizard",
            "FALCON" to "Raptor bird capable of diving at over 200 mph",
            "IGUANA" to "Crested herbivorous lizard native to tropical Americas",
            "GAZELLE" to "Swift antelopine grazer of the African savanna",
            "OPOSSUM" to "North American marsupial famous for playing dead"
        ),
        "Science" to listOf(
            "CATALYST" to "Substance that speeds up a chemical reaction",
            "NEUTRON" to "Subatomic particle in the nucleus with zero charge",
            "PHOTON" to "Elementary quantum packet of electromagnetic light",
            "ISOTOPE" to "Atoms of the same element with different neutron counts",
            "ENTROPY" to "Thermodynamic measure of disorder in a system",
            "NEBULA" to "Giant interstellar cloud of cosmic dust and ionized gas",
            "QUASAR" to "Extremely luminous active galactic nucleus powered by a black hole",
            "OSMOSIS" to "Diffusion of water molecules across a semipermeable membrane",
            "PROTON" to "Positively charged baryon found in every atomic nucleus",
            "SYNAPSE" to "Junction gap where neurons transmit electrical impulses",
            "ENZYME" to "Biological protein molecule that catalyzes cell reactions",
            "PLASMA" to "Ionized fourth state of matter found in stars and lightning",
            "DOPPLER" to "Shift in wave frequency caused by a moving source",
            "SPECTRUM" to "Continuous band of electromagnetic wavelengths",
            "FUSION" to "Nuclear reaction that powers the core of the Sun",
            "HALOGEN" to "Reactive nonmetal group on the periodic table including fluorine",
            "POLYMER" to "Large macromolecule composed of repeating monomer chains",
            "PENDULUM" to "Suspended weight swinging back and forth under gravity",
            "REFRACTION" to "Bending of light waves passing from air into water",
            "MITOSIS" to "Process of cell division producing two identical nuclei",
            "GENOME" to "Complete set of genetic DNA instructions in an organism",
            "TITRATION" to "Lab method used to determine the concentration of an acid",
            "BAROMETER" to "Meteorological instrument that measures atmospheric pressure",
            "SEISMOGRAPH" to "Instrument that records earthquake ground tremors",
            "KINETIC" to "Form of energy possessed by an object due to its motion"
        ),
        "History" to listOf(
            "GLADIATOR" to "Armed combatant who entertained audiences in the Roman Republic",
            "PHARAOH" to "Monarch and high priest of ancient dynastic Egypt",
            "RENAISSANCE" to "European cultural rebirth of art and science starting in Italy",
            "AQUEDUCT" to "Roman stone bridge channel built to carry fresh water",
            "CATAPULT" to "Ancient siege engine using tension to hurl heavy boulders",
            "CHARIOT" to "Two-wheeled horse-drawn vehicle of Bronze Age warfare",
            "EMPEROR" to "Sovereign ruler of a vast multi-nation empire",
            "ACROPOLIS" to "Elevated ancient citadel perched above the city of Athens",
            "BABYLON" to "Ancient Mesopotamian metropolis famed for its Hanging Gardens",
            "POMPEII" to "Roman city frozen in time under Mount Vesuvius volcanic ash",
            "SPARTA" to "Peloponnesian warrior city-state led by King Leonidas",
            "GALLION" to "Multi-decked sailing warship used by European explorers",
            "DYNASTY" to "Line of hereditary rulers from the same royal family",
            "SCROLL" to "Rolled papyrus or parchment manuscript of antiquity",
            "CITADEL" to "Core fortified stronghold protecting a medieval town",
            "CRUSADE" to "Medieval military expedition across the Mediterranean",
            "SHOGUN" to "Hereditary military dictator of historical Japan",
            "TAPESTRY" to "Heavy woven wall textile depicting historical battles",
            "ASTROLABE" to "Ancient astronomical inclinometer used by navigators",
            "MONARCHY" to "Form of government headed by a king or queen",
            "OBELISK" to "Tall four-sided tapered stone monument with a pyramid tip",
            "TREBUCHET" to "Counterweight siege engine capable of smashing castle walls",
            "SCRIBE" to "Ancient scholar trained in hieroglyphics or cuneiform",
            "LEGION" to "Principal military unit of the ancient Roman army",
            "GUILD" to "Medieval association of master craftsmen and merchants"
        ),
        "Pop Culture" to listOf(
            "BLOCKBUSTER" to "Box-office smash hit movie drawing huge crowds",
            "SLAPSTICK" to "Boisterous physical comedy style with exaggerated falls",
            "KARAOKE" to "Interactive entertainment singing along to backing tracks",
            "UNDERDOG" to "Competitor in a match expected to lose against the favorite",
            "PLOTTWIST" to "Surprise revelation that changes a story's direction",
            "SHOWDOWN" to "Climactic face-off between the hero and arch-villain",
            "FLASHMOB" to "Crowd assembling suddenly in public for a surprise dance",
            "PAPARAZZI" to "Freelance photographers pursuing famous celebrities",
            "COSPLAY" to "Dressing up in handcrafted costumes as fictional characters",
            "SPEEDRUN" to "Completing a video game from start to finish as fast as possible",
            "AUTOTUNE" to "Audio pitch-correction processor used in pop vocals",
            "BILLBOARD" to "Giant outdoor advertising poster or music chart ranking",
            "PREQUEL" to "Story installment set before the events of the original work",
            "SPINOFF" to "New show created around a popular side character",
            "CAMEO" to "Brief surprise appearance by a famous person in a film",
            "ENCORE" to "Extra song performed at the end of a concert by demand",
            "TRAILER" to "Short preview montage teasing an upcoming movie",
            "STUNTMAN" to "Trained performer who executes dangerous film leaps",
            "JOYSTICK" to "Pivoting handheld lever controller for arcade games",
            "JUKEBOX" to "Coin-operated retro machine that plays selected vinyl singles",
            "MASCOT" to "Costumed character who rallies fans at sports stadiums",
            "TRIVIA" to "Quiz game testing obscure facts and pop culture knowledge",
            "STAGECOACH" to "Four-wheeled horse carriage iconic in classic Western films",
            "SYNTHWAVE" to "Neon-drenched retro-futuristic electronic music genre",
            "MARIONETTE" to "Articulated wooden puppet controlled from above by strings"
        ),
        "Geography" to listOf(
            "EVEREST" to "Highest mountain peak on Earth at 8,848 meters",
            "SAHARA" to "Vast hot desert spanning across Northern Africa",
            "ARCHIPELAGO" to "Expansive chain or cluster of islands in the ocean",
            "PENINSULA" to "Landmass bordered by water on three sides",
            "SAVANNA" to "Tropical grassland plain dotted with acacia trees",
            "TUNDRA" to "Treeless polar biome with permanently frozen subsoil",
            "CANYON" to "Deep gorge carved through rock by a rushing river",
            "FJORD" to "Long narrow sea inlet flanked by steep glacial cliffs",
            "ATOLL" to "Ring-shaped coral reef enclosing a turquoise lagoon",
            "MONSOON" to "Seasonal prevailing wind bringing heavy tropical rains",
            "DELTA" to "Triangle-shaped sediment wetland at a river's mouth",
            "GEYSER" to "Geothermal hot spring that periodically erupts steam",
            "PLATEAU" to "Extensive flat-topped highland elevated above surrounding terrain",
            "EQUATOR" to "Imaginary zero-degree latitude circle dividing Earth's hemispheres",
            "MERIDIAN" to "Line of longitude running from the North Pole to South Pole",
            "ESTUARY" to "Tidal mouth of a large river where fresh and salt water mix",
            "SERENGETI" to "Tanzanian ecosystem famous for its annual wildebeest migration",
            "PATAGONIA" to "Wind-swept southern tip region of South America",
            "KILIMANJARO" to "Snow-capped dormant volcanic mountain in Tanzania",
            "DANUBE" to "Historic river flowing through ten European countries",
            "GIBRALTAR" to "Narrow strait connecting the Atlantic Ocean to the Mediterranean",
            "YOSEMITE" to "California valley famed for granite cliffs and waterfalls",
            "BAIKAL" to "World's deepest and oldest freshwater lake in Siberia",
            "MADAGASCAR" to "Fourth-largest island in the world off southeastern Africa",
            "CARIBBEAN" to "Tropical sea bordered by the Antilles island arc"
        ),
        "Food" to listOf(
            "TIRAMISU" to "Layered Italian dessert soaked in espresso and mascarpone",
            "DUMPLING" to "Savory dough parcel steamed, boiled, or pan-fried",
            "PRETZEL" to "Bavarian baked pastry twisted into a salted knot",
            "MACARON" to "Delicate French almond meringue sandwich cookie",
            "WAFFLE" to "Crisp batter cake cooked between patterned iron grids",
            "BROWNIE" to "Dense square baked chocolate fudge confection",
            "SMOOTHIE" to "Chilled blended beverage of fresh fruit and yogurt",
            "GELATO" to "Slow-churned dense Italian frozen cream dessert",
            "BAGUETTE" to "Long crisp-crusted loaf of traditional French bread",
            "TEMPURA" to "Japanese dish of seafood or vegetables in light crispy batter",
            "LASAGNA" to "Baked Italian dish of stacked wide pasta sheets and ricotta",
            "CHURRO" to "Ridged fried-dough pastry dusted in cinnamon sugar",
            "FOCACCIA" to "Oven-baked Italian flatbread drizzled with olive oil and herbs",
            "PAELLA" to "Valencian saffron rice dish cooked in a wide shallow pan",
            "RAMEN" to "Japanese wheat noodle soup served in savory broth",
            "FALAFEL" to "Spiced deep-fried ball made from ground chickpeas",
            "BRIOCHE" to "Rich golden French bread made with plenty of butter and eggs",
            "SORBET" to "Refreshing dairy-free frozen dessert made from fruit puree",
            "TORTELLINI" to "Ring-shaped stuffed pasta originally from Bologna",
            "GOUDA" to "Mild yellow cow's-milk cheese coated in red wax",
            "PARMESAN" to "Hard granular aged Italian cheese grated over pasta",
            "TAMALE" to "Mesoamerican dish of masa dough steamed inside a corn husk",
            "SCONE" to "Lightly sweetened British quick bread served with clotted cream",
            "BAKLAVA" to "Sweet layered filo pastry filled with chopped nuts and honey",
            "POUTINE" to "Canadian dish of french fries topped with cheese curds and gravy"
        ),
        "Tech" to listOf(
            "CYBERNETICS" to "Study of control and communication in machines and living things",
            "FIREWALL" to "Network security system that blocks unauthorized traffic",
            "BANDWIDTH" to "Maximum rate of data transfer across a network path",
            "ENCRYPTION" to "Encoding messages so only authorized parties can read them",
            "TRANSISTOR" to "Semiconductor device used to amplify or switch electronic signals",
            "BLOCKCHAIN" to "Distributed cryptographic ledger of linked digital blocks",
            "COMPILER" to "Program that translates human source code into machine code",
            "FIRMWARE" to "Permanent software programmed into a device's read-only memory",
            "HYPERLINK" to "Clickable reference connecting one web document to another",
            "ETHERNET" to "Standard wired local area network technology using twisted-pair cables",
            "PIXEL" to "Smallest addressable glowing color dot on a digital display",
            "SUPERCONDUCTOR" to "Material that conducts electricity with zero resistance when cooled",
            "GIGABYTE" to "Unit of digital information storage equal to roughly one billion bytes",
            "PERIPHERAL" to "External hardware device such as a keyboard, mouse, or printer",
            "BIOMETRICS" to "Authentication using unique physical traits like fingerprints",
            "METAVERSE" to "Shared immersive virtual 3D world space",
            "TELEMETRY" to "Automated remote measurement and transmission of sensor data",
            "NANOBOT" to "Microscopic machine engineered at the nanometer scale",
            "PROTOTYPE" to "Early working model built to test a new engineering concept",
            "CAPACITOR" to "Electronic component that stores energy in an electric field",
            "SOLENOID" to "Coil of wire that acts as a magnet when carrying electric current",
            "GYROSCOPE" to "Spinning wheel sensor that measures orientation and angular velocity",
            "FIBREOPTIC" to "Flexible glass strand that transmits data as pulses of light",
            "MAINFRAME" to "High-performance computer used for large-scale transaction processing",
            "DEBUGGER" to "Developer tool used to inspect code and squash software bugs"
        ),
        "Mythology" to listOf(
            "PHOENIX" to "Immortal firebird that rises reborn from its own ashes",
            "PEGASUS" to "Winged divine stallion of Greek mythology",
            "MINOTAUR" to "Bull-headed monster dwelling at the center of the Cretan maze",
            "VALKYRIE" to "Norse maiden spirit who guides fallen heroes to Valhalla",
            "POSEIDON" to "Olympian god of the sea who wields a three-pronged trident",
            "HERCULES" to "Legendary hero famed for completing twelve impossible labors",
            "CERBERUS" to "Three-headed hound guarding the gates of the Underworld",
            "MEDUSA" to "Snake-haired Gorgon whose gaze turns onlookers to stone",
            "OLYMPUS" to "Cloud-ringed mountain home of the twelve Greek gods",
            "KRAKEN" to "Colossal tentacled sea monster of Scandinavian folklore",
            "CENTAUR" to "Mythical woodland creature with a human torso and horse body",
            "GRIFFIN" to "Majestic guardian beast with the body of a lion and head of an eagle",
            "CHIMERA" to "Fire-breathing hybrid monster of Lycia",
            "EXCALIBUR" to "Legendary magical sword of King Arthur",
            "VALHALLA" to "Majestic golden hall ruled by Odin in Asgard",
            "CYCLOPS" to "One-eyed giant blacksmith who forged Zeus's thunderbolts",
            "SPHINX" to "Riddle-asking guardian with a lion's body and human head",
            "LEVIATHAN" to "Primordial sea serpent of ancient Near Eastern myth",
            "MJOLNIR" to "Enchanted thunder hammer wielded by the Norse god Thor",
            "BASILISK" to "Legendary serpent king hatched from a cockerel's egg",
            "UNICORN" to "Mythical woodland horse with a spiraling healing horn",
            "SIREN" to "Enchanting island singer who lured sailors toward rocky shores",
            "PROMETHEUS" to "Titan who gifted fire to humanity",
            "ANUBIS" to "Jackal-headed Egyptian deity of mummification and the afterlife",
            "ODYSSEY" to "Epic ten-year homeward sea voyage of the hero Odysseus"
        )
    )

    val LEVELS: List<WordLevel> by lazy {
        val result = ArrayList<WordLevel>(1000)
        // Levels 1..100: Core 100 hand-crafted levels
        for (i in 0 until 100) {
            val e = CORE_100[i]
            val lvlNum = i + 1
            result.add(
                WordLevel(
                    number = lvlNum,
                    word = e.word,
                    clue = e.clue,
                    category = e.category
                )
            )
        }

        // Levels 101..1000: 900 structured levels cycling across all 8 categories,
        // 20 Rank Tiers, and 15 Peril Scenarios with zero duplicates in adjacent windows
        val numCategories = CATEGORY_POOLS.size // 8
        for (lvlNum in 101..1000) {
            val offset = lvlNum - 101
            val catIdx = offset % numCategories
            val (catName, pool) = CATEGORY_POOLS[catIdx]
            val itemIdx = ((offset / numCategories) + (catIdx * 3)) % pool.size
            val (word, baseClue) = pool[itemIdx]
            val rank = RankTier.forLevel(lvlNum)
            val isBoss = lvlNum % 5 == 0
            val prefix = when {
                lvlNum == 1000 -> "Supreme Final Boss (1000) • $catName"
                isBoss -> "Boss • ${rank.title} ($catName)"
                else -> "$catName • ${rank.title}"
            }
            result.add(
                WordLevel(
                    number = lvlNum,
                    word = word,
                    clue = "$prefix: $baseClue",
                    category = catName
                )
            )
        }
        result
    }
}

val CAMPAIGN_LEVELS: List<WordLevel>
    get() = CampaignDatabase.LEVELS

val ADVENTURE_LEVELS = listOf(
    WordLevel(1, "LABYRINTH", "Devil Boss: An intricate maze of winding passages", true, "Mythology"),
    WordLevel(2, "CHAMELEON", "Devil Boss: Lizard famous for shifting its colors", true, "Animals"),
    WordLevel(3, "AVALANCHE", "Devil Boss: Sudden rapid rush of snow down a cliff", true, "Geography"),
    WordLevel(4, "QUICKSAND", "Devil Boss: Loose wet sand that traps the unwary", true, "Geography"),
    WordLevel(5, "JELLYFISH", "Devil Boss: Ocean drifter with stinging tentacles", true, "Animals"),
    WordLevel(6, "SAXOPHONE", "Devil Boss: Brass woodwind instrument iconic in jazz", true, "Pop Culture"),
    WordLevel(7, "METEORITE", "Devil Boss: Space rock that survives falling to Earth", true, "Science"),
    WordLevel(8, "KALEIDOSCOPE", "Devil Boss: Optical tube of mirrors and colorful glass", true, "Science")
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
    private var playerAccountRepo: PlayerAccountRepository? = null
    private var authInstance: FirebaseAuth? = null
    private var authStateListener: FirebaseAuth.AuthStateListener? = null
    private var accountSnapshotJob: Job? = null
    private var stateChangeWatcherJob: Job? = null
    private var isApplyingRemoteSnapshot: Boolean = false

    var currentScreen by mutableStateOf(ScreenState.REGISTRATION)
        private set

    // Firebase Auth & Cloud Profile State (Spark Plan)
    var currentUser by mutableStateOf<FirebaseUser?>(null)
        private set
    var cloudAccount by mutableStateOf<PlayerAccount?>(null)
        private set
    var isGuestSession by mutableStateOf(false)
        private set
    var isSyncingCloud by mutableStateOf(false)
        private set
    var lastCloudSyncEpochMs by mutableStateOf(0L)
        private set
    var authErrorMessage by mutableStateOf<String?>(null)
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

    val currentRankTier: RankTier
        get() = RankTier.forLevel(currentCampaignLevelNumber)

    var gameplayPerilOverride by mutableStateOf<PerilType?>(null)
        private set

    val activePerilType: PerilType
        get() = gameplayPerilOverride ?: activeLevel.perilType

    fun setPerilOverride(peril: PerilType?) {
        gameplayPerilOverride = peril
    }

    fun cycleGameplayPeril() {
        val all = PerilType.entries
        val nextIdx = (all.indexOf(activePerilType) + 1) % all.size
        gameplayPerilOverride = all[nextIdx]
        ProceduralSoundEngine.play(DoodleSoundEffect.POP)
    }

    /** Required consonants to thaw Frozen Vowels on Levels 150+ (capped at unique consonants in word) */
    val requiredConsonantsToThaw: Int
        get() {
            if (!activeLevel.hasFrozenVowels) return 0
            val uniqueConsonants = activeLevel.word.uppercase().filter { it in 'A'..'Z' && it !in VOWELS }.toSet()
            return minOf(3, uniqueConsonants.size)
        }

    /** Number of correct consonants guessed so far in the current round */
    val guessedConsonantCount: Int
        get() = revealedChars.count { it !in VOWELS }

    /** True when Levels 150+ still have their vowels locked in doodle ice cubes */
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

        // Also hydrate from canonical localStorage JSON snapshot if present and newer
        val localJsonRaw = prefs.getString("doodle_word_shark_1000_v1", null)
        if (!localJsonRaw.isNullOrBlank()) {
            runCatching {
                val obj = JSONObject(localJsonRaw)
                coins = obj.optInt("coins", coins)
                campaignLevelIndex = obj.optInt("campaignIdx", campaignLevelIndex).coerceIn(0, CAMPAIGN_LEVELS.lastIndex)
                perfectCount = obj.optInt("perfectCount", perfectCount)
                currentStreak = obj.optInt("streak", currentStreak)
                maxStreak = obj.optInt("maxStreak", maxStreak)
                freeShieldPotions = obj.optInt("freePotions", freeShieldPotions)
                unlockAllMapLevels = obj.optBoolean("freePlayAll", unlockAllMapLevels)
            }
        }

        startAutomaticStateChangeSyncWatcher()
        initFirebaseSync(context.applicationContext)
    }

    /**
     * Reactive state-change watcher: triggers whenever ANY game state property changes,
     * keeping localStorage and Firestore in strict parity when signed in via Registration.
     */
    private fun startAutomaticStateChangeSyncWatcher() {
        stateChangeWatcherJob?.cancel()
        stateChangeWatcherJob = viewModelScope.launch {
            snapshotFlow {
                listOf(
                    coins,
                    campaignLevelIndex,
                    perfectCount,
                    currentStreak,
                    maxStreak,
                    completedCampaignLevels.size,
                    freeShieldPotions,
                    chestProgress,
                    themeProgress,
                    if (unlockAllMapLevels) 1 else 0,
                    claimedMilestoneChests.size,
                    levelStarRatings.size
                )
            }
                .distinctUntilChanged()
                .drop(1) // Skip initial baseline emission on startup
                .collect {
                    if (!isApplyingRemoteSnapshot) {
                        saveStateToPrefs()
                    }
                }
        }
    }

    private var isExplicitSignInInFlight: Boolean = false
    var registeredPlayerUid by mutableStateOf<String?>(null)
        private set

    private fun buildDirectAccountUid(emailOrNick: String): String {
        val slug = emailOrNick.trim().lowercase()
            .replace(Regex("[^a-z0-9]"), "_")
            .trim('_')
            .take(32)
            .ifEmpty { "doodle" }
        return "usr_$slug"
    }

    private fun initFirebaseSync(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedUid = prefs.getString("registeredPlayerUid", null)?.takeIf { it.isNotBlank() }
        val savedName = prefs.getString("registeredPlayerName", null)?.takeIf { it.isNotBlank() }
        val savedEmail = prefs.getString("registeredPlayerEmail", null)?.takeIf { it.isNotBlank() }
        val savedMode = prefs.getString("registeredPlayerMode", "account") ?: "account"

        if (savedUid != null && savedName != null) {
            registeredPlayerUid = savedUid
            isGuestSession = false
            cloudAccount = PlayerAccount(
                id = savedUid,
                userId = savedUid,
                displayName = savedName,
                email = savedEmail,
                authMethod = savedMode,
                campaignLevelIndex = campaignLevelIndex.toLong(),
                coins = coins.toLong(),
                perfectCount = perfectCount.toLong(),
                currentStreak = currentStreak.toLong(),
                maxStreak = maxStreak.toLong(),
                completedLevelsCount = completedCampaignLevels.size.toLong()
            )
            if (currentScreen == ScreenState.REGISTRATION) {
                currentScreen = ScreenState.MAIN_MENU
            }
        }

        runCatching {
            val databaseId = context.getString(R.string.firestore_database_id)
            val db = FirebaseFirestore.getInstance(databaseId)
            playerAccountRepo = PlayerAccountRepository(db)
            val auth = Firebase.auth
            authInstance = auth
            val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                val user = firebaseAuth.currentUser
                currentUser = user
                if (user != null) {
                    isGuestSession = false
                    registeredPlayerUid = user.uid
                    if (!isExplicitSignInInFlight) {
                        startObservingCloudAccountByUid(
                            uid = user.uid,
                            fallbackName = user.displayName?.takeIf { it.isNotBlank() } ?: savedName ?: "Doodle Player",
                            fallbackEmail = user.email ?: savedEmail,
                            fallbackAuthMethod = "gmail"
                        )
                        if (currentScreen == ScreenState.REGISTRATION) {
                            currentScreen = ScreenState.MAIN_MENU
                        }
                    }
                } else if (!registeredPlayerUid.isNullOrBlank() && !isGuestSession) {
                    val activeUid = registeredPlayerUid!!
                    startObservingCloudAccountByUid(
                        uid = activeUid,
                        fallbackName = cloudAccount?.displayName ?: savedName ?: "Doodle Player",
                        fallbackEmail = cloudAccount?.email ?: savedEmail,
                        fallbackAuthMethod = cloudAccount?.authMethod ?: savedMode
                    )
                } else {
                    accountSnapshotJob?.cancel()
                    accountSnapshotJob = null
                    cloudAccount = null
                }
            }
            authStateListener = listener
            auth.addAuthStateListener(listener)
        }.onFailure {
            // Firebase not initialized in standalone JVM unit test environment
        }
    }

    private var lastLocalFirestoreWriteMs: Long = 0L

    private fun resetLocalStateToFresh() {
        isApplyingRemoteSnapshot = true
        try {
            coins = 100
            campaignLevelIndex = 0
            perfectCount = 0
            currentStreak = 0
            maxStreak = 0
            chestProgress = 0
            themeProgress = 0
            freeShieldPotions = 2
            unlockAllMapLevels = false
            completedCampaignLevels.clear()
            levelStarRatings.clear()
            claimedMilestoneChests.clear()
            writeLocalStorageOnly()
        } finally {
            isApplyingRemoteSnapshot = false
        }
    }

    private fun applyRemoteAccountToLocalState(account: PlayerAccount) {
        isApplyingRemoteSnapshot = true
        try {
            cloudAccount = account
            registeredPlayerUid = account.userId.ifBlank { account.id }
            val remoteIdx = account.campaignLevelIndex.toInt().coerceIn(0, CAMPAIGN_LEVELS.lastIndex)
            val remoteCoins = account.coins.toInt().coerceAtLeast(0)
            val remotePerfect = account.perfectCount.toInt().coerceAtLeast(0)
            val remoteStreak = account.currentStreak.toInt().coerceAtLeast(0)
            val remoteMaxStreak = account.maxStreak.toInt().coerceAtLeast(0)
            val remoteCompletedCount = maxOf(
                remoteIdx,
                account.completedLevelsCount.toInt().coerceIn(0, CAMPAIGN_LEVELS.size)
            )

            campaignLevelIndex = remoteIdx
            coins = remoteCoins
            perfectCount = remotePerfect
            currentStreak = remoteStreak
            maxStreak = remoteMaxStreak
            completedCampaignLevels.clear()
            for (lvl in 1..remoteCompletedCount) {
                completedCampaignLevels.add(lvl)
            }

            appContext?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)?.edit()?.apply {
                putString("registeredPlayerUid", account.userId.ifBlank { account.id })
                putString("registeredPlayerName", account.displayName)
                if (!account.email.isNullOrBlank()) {
                    putString("registeredPlayerEmail", account.email)
                }
                putString("registeredPlayerMode", account.authMethod)
                apply()
            }

            lastCloudSyncEpochMs = System.currentTimeMillis()
            writeLocalStorageOnly()
        } finally {
            isApplyingRemoteSnapshot = false
        }
    }

    private fun startObservingCloudAccount(user: FirebaseUser) {
        startObservingCloudAccountByUid(
            uid = user.uid,
            fallbackName = cloudAccount?.displayName ?: user.displayName?.takeIf { it.isNotBlank() } ?: "Doodle Player",
            fallbackEmail = cloudAccount?.email ?: user.email,
            fallbackAuthMethod = cloudAccount?.authMethod ?: "gmail"
        )
    }

    private fun startObservingCloudAccountByUid(
        uid: String,
        fallbackName: String,
        fallbackEmail: String?,
        fallbackAuthMethod: String
    ) {
        if (uid in JUNK_TEST_UIDS) {
            signOutFirebase()
            return
        }
        val repo = playerAccountRepo ?: return
        accountSnapshotJob?.cancel()
        accountSnapshotJob = viewModelScope.launch {
            var targetUid = uid
            val existingRes = repo.getPlayerAccount(targetUid)
            val existingDirect = existingRes.getOrNull()
            val canonicalAccount = existingDirect
                ?: repo.findPlayerAccountByIdentifier(fallbackEmail ?: fallbackName.ifBlank { targetUid }).getOrNull()

            if (canonicalAccount != null) {
                targetUid = canonicalAccount.userId.ifBlank { canonicalAccount.id }.ifBlank { uid }
                applyRemoteAccountToLocalState(canonicalAccount)
            } else {
                val initRes = repo.createOrSyncPlayerAccount(
                    displayName = fallbackName,
                    email = fallbackEmail,
                    authMethod = fallbackAuthMethod,
                    campaignLevelIndex = campaignLevelIndex,
                    coins = coins,
                    perfectCount = perfectCount,
                    currentStreak = currentStreak,
                    maxStreak = maxStreak,
                    completedLevelsCount = completedCampaignLevels.size,
                    userId = targetUid
                )
                initRes.onSuccess { synced ->
                    applyRemoteAccountToLocalState(synced)
                }
            }

            repo.observePlayerAccount(targetUid)
                .catch { e ->
                    authErrorMessage = e.localizedMessage ?: "Failed to sync profile"
                }
                .collect { account ->
                    if (account != null) {
                        if (System.currentTimeMillis() - lastLocalFirestoreWriteMs < 800L) {
                            return@collect
                        }
                        applyRemoteAccountToLocalState(account)
                    }
                }
        }
    }

    fun registerOrLoginDirectAccount(
        nickname: String,
        email: String?,
        password: String,
        authMethod: String,
        isCreate: Boolean
    ) {
        val rawIdent = (email?.takeIf { it.isNotBlank() } ?: nickname).trim()
        val cleanNick = nickname.trim().take(50).ifEmpty {
            email?.substringBefore("@")?.take(50)?.ifEmpty { "Doodle Player" } ?: "Doodle Player"
        }
        val cleanEmail = email?.trim()?.take(150)?.takeIf { it.isNotEmpty() }
        val cleanPassword = password.trim()
        val prefs = appContext?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val repo = playerAccountRepo

        if (!isCreate) {
            if (rawIdent.isEmpty()) {
                authErrorMessage = "Please enter your Email or Doodle Nickname to log in."
                return
            }
            if (cleanPassword.isEmpty()) {
                authErrorMessage = "Please enter your password to log in."
                return
            }
            if (repo == null) {
                authErrorMessage = "Cloud database is still connecting. Please try again in a moment."
                return
            }
            isSyncingCloud = true
            authErrorMessage = null
            viewModelScope.launch {
                val enteredHash = computePasswordHash(cleanPassword)
                val foundRes = repo.findPlayerAccountByIdentifier(rawIdent, enteredHash)
                val remoteAccount = foundRes.getOrNull()
                if (remoteAccount == null || remoteAccount.userId.isBlank()) {
                    isSyncingCloud = false
                    authErrorMessage = "❌ Account '$rawIdent' not found! Check your spelling or tap 'Create new account'."
                    return@launch
                }

                val resolvedUid = remoteAccount.userId
                val savedLocalPass = prefs?.getString("account_pwd_$resolvedUid", null)
                val remoteHash = remoteAccount.passwordHash?.trim().orEmpty()

                if (remoteHash.isNotEmpty()) {
                    if (remoteHash != enteredHash) {
                        isSyncingCloud = false
                        authErrorMessage = "❌ Incorrect password for '${remoteAccount.displayName}'. Tap 'Forgot password?' to reset."
                        return@launch
                    }
                } else if (!savedLocalPass.isNullOrEmpty() && savedLocalPass != cleanPassword) {
                    isSyncingCloud = false
                    authErrorMessage = "❌ Incorrect password for '${remoteAccount.displayName}'. Tap 'Forgot password?' to reset."
                    return@launch
                } else if (cleanPassword.length < 3) {
                    isSyncingCloud = false
                    authErrorMessage = "❌ Invalid password! Please enter your account password."
                    return@launch
                }

                isGuestSession = false
                prefs?.edit()?.putString("account_pwd_$resolvedUid", cleanPassword)?.apply()
                applyRemoteAccountToLocalState(remoteAccount)

                if (remoteHash.isEmpty()) {
                    repo.updateProgress(
                        displayName = remoteAccount.displayName,
                        email = remoteAccount.email,
                        passwordHash = enteredHash,
                        authMethod = remoteAccount.authMethod,
                        campaignLevelIndex = campaignLevelIndex,
                        coins = coins,
                        perfectCount = perfectCount,
                        currentStreak = currentStreak,
                        maxStreak = maxStreak,
                        completedLevelsCount = completedCampaignLevels.size,
                        userId = resolvedUid
                    ).onSuccess { updated ->
                        cloudAccount = updated
                    }
                }

                isSyncingCloud = false
                statusBannerMessage = "👋 Welcome back, ${remoteAccount.displayName}! (Synced Level ${campaignLevelIndex + 1})"
                currentScreen = ScreenState.MAIN_MENU
                startObservingCloudAccountByUid(
                    uid = resolvedUid,
                    fallbackName = remoteAccount.displayName,
                    fallbackEmail = remoteAccount.email,
                    fallbackAuthMethod = remoteAccount.authMethod
                )
            }
            return
        }

        // Create New Account flow (isCreate == true)
        if (cleanNick.length < 2) {
            authErrorMessage = "Please enter a Doodle Nickname (at least 2 characters)."
            return
        }
        if (cleanPassword.length < 4) {
            authErrorMessage = "Please enter a password (at least 4 characters) to protect your account."
            return
        }

        val pwdHash = computePasswordHash(cleanPassword)
        val uid = currentUser?.uid ?: buildDirectAccountUid(cleanEmail ?: cleanNick)
        if (uid in JUNK_TEST_UIDS) {
            authErrorMessage = "Please choose a valid nickname or email."
            return
        }

        if (repo != null) {
            isSyncingCloud = true
            authErrorMessage = null
            viewModelScope.launch {
                if (!isGuestSession) {
                    resetLocalStateToFresh()
                }

                isGuestSession = false
                registeredPlayerUid = uid
                prefs?.edit()?.apply {
                    putString("registeredPlayerUid", uid)
                    putString("registeredPlayerName", cleanNick)
                    if (cleanEmail != null) {
                        putString("registeredPlayerEmail", cleanEmail)
                    } else {
                        remove("registeredPlayerEmail")
                    }
                    putString("registeredPlayerMode", authMethod)
                    putString("account_pwd_$uid", cleanPassword)
                    apply()
                }

                val res = repo.createOrSyncPlayerAccount(
                    displayName = cleanNick,
                    email = cleanEmail,
                    passwordHash = pwdHash,
                    authMethod = authMethod,
                    campaignLevelIndex = campaignLevelIndex,
                    coins = coins,
                    perfectCount = perfectCount,
                    currentStreak = currentStreak,
                    maxStreak = maxStreak,
                    completedLevelsCount = completedCampaignLevels.size,
                    userId = uid,
                    overwriteIfExists = true
                )
                isSyncingCloud = false
                res.onSuccess { created ->
                    applyRemoteAccountToLocalState(created)
                    statusBannerMessage = "✨ Account created for $cleanNick!"
                    currentScreen = ScreenState.MAIN_MENU
                }.onFailure { err ->
                    authErrorMessage = err.localizedMessage ?: "Failed to create account."
                }
                startObservingCloudAccountByUid(
                    uid = uid,
                    fallbackName = cleanNick,
                    fallbackEmail = cleanEmail,
                    fallbackAuthMethod = authMethod
                )
            }
        }
    }

    fun openRegistrationScreen() {
        authErrorMessage = null
        currentScreen = ScreenState.REGISTRATION
    }

    fun continueAsGuest() {
        accountSnapshotJob?.cancel()
        accountSnapshotJob = null
        authInstance?.signOut()
        currentUser = null
        registeredPlayerUid = null
        cloudAccount = null
        if (!isGuestSession) {
            resetLocalStateToFresh()
        }
        isGuestSession = true
        authErrorMessage = null
        appContext?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)?.edit()
            ?.remove("registeredPlayerUid")
            ?.remove("registeredPlayerName")
            ?.remove("registeredPlayerEmail")
            ?.remove("registeredPlayerMode")
            ?.apply()
        currentScreen = ScreenState.MAIN_MENU
        statusBannerMessage = "🕵️ Logged in as Guest (Local Device Save)"
        writeLocalStorageOnly()
    }

    fun setAuthError(message: String?) {
        authErrorMessage = message
    }

    fun signInWithGoogleIdToken(
        idToken: String,
        preferredNickname: String?,
        preferredEmail: String?,
        authMethod: String
    ) {
        val auth = authInstance ?: runCatching { Firebase.auth }.getOrNull()
        if (auth == null) {
            authErrorMessage = "Authentication service is not initialized."
            return
        }
        isSyncingCloud = true
        isExplicitSignInInFlight = true
        authErrorMessage = null
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                val user = authResult.user
                currentUser = user
                isGuestSession = false
                viewModelScope.launch {
                    val repo = playerAccountRepo
                    if (user != null && repo != null) {
                        val resolvedName = preferredNickname?.takeIf { it.isNotBlank() }
                            ?: user.displayName?.takeIf { it.isNotBlank() }
                            ?: "Doodle Player"
                        val resolvedEmail = preferredEmail?.takeIf { it.isNotBlank() } ?: user.email

                        val existingCloud = repo.findPlayerAccountByIdentifier(resolvedEmail ?: resolvedName).getOrNull()
                        val targetUid = existingCloud?.userId?.takeIf { it.isNotBlank() } ?: user.uid
                        registeredPlayerUid = targetUid

                        if (existingCloud != null) {
                            applyRemoteAccountToLocalState(existingCloud)
                            statusBannerMessage = "👋 Welcome back, ${existingCloud.displayName}! (Level ${campaignLevelIndex + 1})"
                            currentScreen = ScreenState.MAIN_MENU
                        } else {
                            if (!isGuestSession) {
                                resetLocalStateToFresh()
                            }
                            val syncRes = repo.createOrSyncPlayerAccount(
                                displayName = resolvedName,
                                email = resolvedEmail,
                                authMethod = authMethod,
                                campaignLevelIndex = campaignLevelIndex,
                                coins = coins,
                                perfectCount = perfectCount,
                                currentStreak = currentStreak,
                                maxStreak = maxStreak,
                                completedLevelsCount = completedCampaignLevels.size,
                                userId = targetUid
                            )
                            syncRes.onSuccess { synced ->
                                applyRemoteAccountToLocalState(synced)
                                statusBannerMessage = "👋 Welcome, ${synced.displayName}!"
                                currentScreen = ScreenState.MAIN_MENU
                            }.onFailure { err ->
                                authErrorMessage = err.localizedMessage ?: "Failed to save profile."
                            }
                        }
                        isExplicitSignInInFlight = false
                        startObservingCloudAccountByUid(
                            uid = targetUid,
                            fallbackName = existingCloud?.displayName ?: resolvedName,
                            fallbackEmail = existingCloud?.email ?: resolvedEmail,
                            fallbackAuthMethod = authMethod
                        )
                    } else {
                        isExplicitSignInInFlight = false
                        currentScreen = ScreenState.MAIN_MENU
                    }
                    isSyncingCloud = false
                }
            }
            .addOnFailureListener { e ->
                isExplicitSignInInFlight = false
                isSyncingCloud = false
                authErrorMessage = e.localizedMessage ?: "Google Sign-In failed."
            }
    }

    fun updateCloudProfile(nickname: String, email: String?, authMethod: String) {
        val repo = playerAccountRepo ?: return
        val activeUid = currentUser?.uid ?: registeredPlayerUid ?: cloudAccount?.userId ?: return
        val cleanNick = nickname.trim().take(50).ifEmpty { "Doodle Player" }
        val cleanEmail = email?.trim()?.take(150)?.takeIf { it.isNotEmpty() }
        appContext?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)?.edit()?.apply {
            putString("registeredPlayerUid", activeUid)
            putString("registeredPlayerName", cleanNick)
            if (cleanEmail != null) putString("registeredPlayerEmail", cleanEmail)
            putString("registeredPlayerMode", authMethod)
            apply()
        }
        isSyncingCloud = true
        authErrorMessage = null
        viewModelScope.launch {
            lastLocalFirestoreWriteMs = System.currentTimeMillis()
            val res = repo.updateProgress(
                displayName = cleanNick,
                email = cleanEmail,
                passwordHash = cloudAccount?.passwordHash,
                authMethod = authMethod,
                campaignLevelIndex = campaignLevelIndex,
                coins = coins,
                perfectCount = perfectCount,
                currentStreak = currentStreak,
                maxStreak = maxStreak,
                completedLevelsCount = completedCampaignLevels.size,
                userId = activeUid
            )
            isSyncingCloud = false
            res.onSuccess { updated ->
                cloudAccount = updated
                statusBannerMessage = "✅ Saved profile for ${updated.displayName}!"
            }.onFailure { err ->
                authErrorMessage = err.localizedMessage ?: "Could not update profile."
            }
        }
    }

    fun signOutFirebase() {
        val activeUid = currentUser?.uid ?: registeredPlayerUid ?: cloudAccount?.userId
        val activeName = cloudAccount?.displayName
            ?: appContext?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)?.getString("registeredPlayerName", null)
            ?: "Doodle Player"
        val activeEmail = cloudAccount?.email
            ?: appContext?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)?.getString("registeredPlayerEmail", null)
        val activePwdHash = cloudAccount?.passwordHash
        val activeAuth = cloudAccount?.authMethod ?: "account"
        val snapIdx = campaignLevelIndex
        val snapCoins = coins
        val snapPerfect = perfectCount
        val snapStreak = currentStreak
        val snapMaxStreak = maxStreak
        val snapCompleted = completedCampaignLevels.size

        if (!activeUid.isNullOrBlank() && activeUid !in JUNK_TEST_UIDS && !isGuestSession) {
            val repo = playerAccountRepo
            if (repo != null) {
                viewModelScope.launch {
                    repo.updateProgress(
                        displayName = activeName,
                        email = activeEmail,
                        passwordHash = activePwdHash,
                        authMethod = activeAuth,
                        campaignLevelIndex = snapIdx,
                        coins = snapCoins,
                        perfectCount = snapPerfect,
                        currentStreak = snapStreak,
                        maxStreak = snapMaxStreak,
                        completedLevelsCount = snapCompleted,
                        userId = activeUid
                    )
                }
            }
        }

        accountSnapshotJob?.cancel()
        accountSnapshotJob = null
        authInstance?.signOut()
        currentUser = null
        registeredPlayerUid = null
        cloudAccount = null
        isGuestSession = false
        authErrorMessage = null
        resetLocalStateToFresh()
        appContext?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)?.edit()
            ?.remove("registeredPlayerUid")
            ?.remove("registeredPlayerName")
            ?.remove("registeredPlayerEmail")
            ?.remove("registeredPlayerMode")
            ?.apply()
        currentScreen = ScreenState.REGISTRATION
        statusBannerMessage = "Signed out of account"
    }

    override fun onCleared() {
        super.onCleared()
        authStateListener?.let { authInstance?.removeAuthStateListener(it) }
        accountSnapshotJob?.cancel()
        stateChangeWatcherJob?.cancel()
    }

    /**
     * Writes canonical game state to local storage (both SharedPreferences keys and
     * the localStorage-compatible JSON snapshot `doodle_word_shark_1000_v1`).
     */
    private fun writeLocalStorageOnly() {
        val prefs = appContext?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) ?: return
        val starsJson = JSONObject()
        levelStarRatings.forEach { (lvl, st) -> starsJson.put(lvl.toString(), st) }
        val canonicalJson = JSONObject().apply {
            put("coins", coins)
            put("campaignIdx", campaignLevelIndex)
            put("completed", JSONArray(completedCampaignLevels.toList()))
            put("completedLevelsCount", completedCampaignLevels.size)
            put("stars", starsJson)
            put("chests", JSONArray(claimedMilestoneChests.toList()))
            put("freePlayAll", unlockAllMapLevels)
            put("freePotions", freeShieldPotions)
            put("perfectCount", perfectCount)
            put("streak", currentStreak)
            put("maxStreak", maxStreak)
            put("chestProg", chestProgress)
            put("themeProg", themeProgress)
            put("updatedAt", System.currentTimeMillis())
        }.toString()

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
            .putString("doodle_word_shark_1000_v1", canonicalJson)
            .apply()
    }

    /**
     * Pushes the current canonical game state to Firestore when signed in via the Registration screen.
     */
    private fun syncCurrentStateToFirestore() {
        val activeUid = currentUser?.uid ?: registeredPlayerUid ?: cloudAccount?.userId
        val repo = playerAccountRepo
        if (!activeUid.isNullOrBlank() && activeUid !in JUNK_TEST_UIDS && repo != null && !isGuestSession) {
            isSyncingCloud = true
            lastLocalFirestoreWriteMs = System.currentTimeMillis()
            viewModelScope.launch {
                val res = repo.updateProgress(
                    displayName = cloudAccount?.displayName
                        ?: currentUser?.displayName?.takeIf { it.isNotBlank() }
                        ?: "Doodle Player",
                    email = cloudAccount?.email ?: currentUser?.email,
                    passwordHash = cloudAccount?.passwordHash,
                    authMethod = cloudAccount?.authMethod ?: "account",
                    campaignLevelIndex = campaignLevelIndex,
                    coins = coins,
                    perfectCount = perfectCount,
                    currentStreak = currentStreak,
                    maxStreak = maxStreak,
                    completedLevelsCount = completedCampaignLevels.size,
                    userId = activeUid
                )
                isSyncingCloud = false
                res.onSuccess { syncedAccount ->
                    cloudAccount = syncedAccount
                    lastCloudSyncEpochMs = System.currentTimeMillis()
                }.onFailure { err ->
                    authErrorMessage = err.localizedMessage ?: "Sync failed"
                }
            }
        }
    }

    private fun saveStateToPrefs() {
        writeLocalStorageOnly()
        syncCurrentStateToFirestore()
    }

    fun openWorldMap() {
        currentScreen = ScreenState.WORLD_MAP
    }

    fun toggleUnlockAllMapLevels() {
        unlockAllMapLevels = !unlockAllMapLevels
        statusBannerMessage = if (unlockAllMapLevels) {
            "🔓 All 1,000 Levels Unlocked for Free Play!"
        } else {
            "🔒 Sequential Campaign Progression Active"
        }
        saveStateToPrefs()
    }

    fun isLevelUnlocked(levelNumber: Int): Boolean {
        if (unlockAllMapLevels) return true
        // Level 1, the start of each 20-level Peril Chapter (1, 21, 41, ...), completed levels, or next level after highest completed
        if (levelNumber == 1 || (levelNumber - 1) % 20 == 0) return true
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
        coins += 250
        ProceduralSoundEngine.play(DoodleSoundEffect.VICTORY)
        statusBannerMessage = "🧰 Level $milestoneLevel Rank Milestone Chest! +250 Coins!"
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
        saveStateToPrefs()
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

        // Frozen Vowels Check (Levels 150+): Vowels cannot be pressed until 3 consonants are guessed!
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
                ProceduralSoundEngine.play(activePerilType.overSound)
                saveStateToPrefs()
            } else {
                ProceduralSoundEngine.play(activePerilType.hitSound)
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
            saveStateToPrefs()
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
                ScreenState.REGISTRATION -> {
                    BackHandler(enabled = viewModel.currentUser != null || viewModel.cloudAccount != null || viewModel.isGuestSession) {
                        viewModel.navigateToMainMenu()
                    }
                    RegistrationAuthScreen(
                        currentUser = viewModel.currentUser,
                        cloudAccount = viewModel.cloudAccount,
                        isGuestSession = viewModel.isGuestSession,
                        isSyncing = viewModel.isSyncingCloud,
                        authErrorMessage = viewModel.authErrorMessage,
                        localLevelNumber = viewModel.currentCampaignLevelNumber,
                        localCoins = viewModel.coins,
                        completedLevelsCount = viewModel.completedCampaignCount,
                        fontFamily = activeFontFamily,
                        onGoogleIdTokenReceived = { idToken, nickname, email, method ->
                            viewModel.signInWithGoogleIdToken(idToken, nickname, email, method)
                        },
                        onRegisterOrLoginAccount = { nickname, email, password, method, isCreate ->
                            viewModel.registerOrLoginDirectAccount(nickname, email, password, method, isCreate)
                        },
                        onUpdateCloudProfile = { nickname, email, method ->
                            viewModel.updateCloudProfile(nickname, email, method)
                        },
                        onContinueAsGuest = { viewModel.continueAsGuest() },
                        onContinueToGame = { viewModel.navigateToMainMenu() },
                        onSignOut = { viewModel.signOutFirebase() },
                        onSetAuthError = { viewModel.setAuthError(it) }
                    )
                }

                ScreenState.MAIN_MENU -> {
                    val accountLabel = when {
                        viewModel.currentUser != null || viewModel.cloudAccount != null -> {
                            val name = viewModel.cloudAccount?.displayName
                                ?: viewModel.currentUser?.displayName
                                ?: "Player"
                            "👤 ${name.take(10)} ✓"
                        }
                        viewModel.isGuestSession -> "🕵️ Guest"
                        else -> "👤 Register"
                    }
                    HomeScreen(
                        levelNumber = viewModel.currentCampaignLevelNumber,
                        completedLevels = viewModel.completedCampaignCount,
                        totalCampaignLevels = viewModel.totalCampaignCount,
                        coins = viewModel.coins,
                        accountBadgeLabel = accountLabel,
                        fontFamily = activeFontFamily,
                        onPlayCampaign = { chosenPeril ->
                            viewModel.setPerilOverride(chosenPeril)
                            viewModel.startCampaignLevel()
                        },
                        onOpenWorldMap = { viewModel.openWorldMap() },
                        onPlayAdventure = { viewModel.startAdventureLevel() },
                        onPlayTwoPlayer = { viewModel.openTwoPlayerSetup() },
                        onSelectPerilLevel = { lvl -> viewModel.selectCampaignLevelNumber(lvl) },
                        onOpenSettings = { viewModel.showSettingsDialog = true },
                        onOpenCoinShop = { viewModel.showCoinShopDialog = true },
                        onOpenRegistration = { viewModel.openRegistrationScreen() }
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
// 6. SCREEN 1: HOME SCREEN (DRAGON BALL CHARGING STICKMAN & 20-TIER AURA)
// ============================================================================

private data class KiBurstParticle(
    val id: Long,
    val startX: Float,
    val startY: Float,
    val vx: Float,
    val vy: Float,
    val color: Color,
    val radiusDp: Float,
    val isSparkStar: Boolean,
    val createdAtMs: Long,
    val durationMs: Long = 750L
)

private data class ComicSurgePopup(
    val id: Long,
    val text: String,
    val xFraction: Float,
    val yFraction: Float,
    val tiltDeg: Float,
    val createdAtMs: Long,
    val durationMs: Long = 850L
)

@Composable
fun MainMenuScreen(
    levelNumber: Int,
    completedLevels: Int,
    totalCampaignLevels: Int,
    coins: Int,
    accountBadgeLabel: String = "👤 Register",
    fontFamily: FontFamily,
    onPlayCampaign: (PerilType?) -> Unit,
    onOpenWorldMap: () -> Unit,
    onPlayAdventure: () -> Unit,
    onPlayTwoPlayer: () -> Unit,
    onSelectPerilLevel: (Int) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenCoinShop: () -> Unit,
    onOpenRegistration: () -> Unit = {}
) {
    HomeScreen(
        levelNumber = levelNumber,
        completedLevels = completedLevels,
        totalCampaignLevels = totalCampaignLevels,
        coins = coins,
        accountBadgeLabel = accountBadgeLabel,
        fontFamily = fontFamily,
        onPlayCampaign = onPlayCampaign,
        onOpenWorldMap = onOpenWorldMap,
        onPlayAdventure = onPlayAdventure,
        onPlayTwoPlayer = onPlayTwoPlayer,
        onSelectPerilLevel = onSelectPerilLevel,
        onOpenSettings = onOpenSettings,
        onOpenCoinShop = onOpenCoinShop,
        onOpenRegistration = onOpenRegistration
    )
}

@Composable
fun HomeScreen(
    levelNumber: Int,
    completedLevels: Int,
    totalCampaignLevels: Int,
    coins: Int,
    accountBadgeLabel: String = "👤 Register",
    fontFamily: FontFamily,
    onPlayCampaign: (PerilType?) -> Unit,
    onOpenWorldMap: () -> Unit,
    onPlayAdventure: () -> Unit,
    onPlayTwoPlayer: () -> Unit,
    onSelectPerilLevel: (Int) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenCoinShop: () -> Unit,
    onOpenRegistration: () -> Unit = {}
) {
    val activePeril = PerilType.forLevel(levelNumber)
    val activeRank = RankTier.forLevel(levelNumber)
    val currentTier = ((levelNumber - 1) / 50 + 1).coerceIn(1, 20)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .widthIn(max = 600.dp)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Bar (Settings, Map, Cloud Status, Star/Coin counter)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SketchyIconButton(
                    testTag = "settings_button",
                    contentDescription = "Settings",
                    onClick = onOpenSettings
                ) {
                    DrawDoodleGearIcon()
                }

                SketchyIconButton(
                    testTag = "world_map_button",
                    contentDescription = "1,000-Level World Map",
                    onClick = onOpenWorldMap
                ) {
                    Text(
                        text = "🗺️",
                        fontSize = 18.sp
                    )
                }

                SketchyContainer(
                    fillColor = Color(0xFFE3F2FD),
                    cornerRadius = 14.dp,
                    onClick = onOpenRegistration,
                    modifier = Modifier
                        .height(44.dp)
                        .testTag("account_status_button")
                ) {
                    Text(
                        text = accountBadgeLabel,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                        color = DoodleColors.InkBlack,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )
                }
            }

            CoinBalancePill(
                coins = coins,
                fontFamily = fontFamily,
                onClickAdd = onOpenCoinShop,
                testTag = "menu_coin_pill"
            )
        }

        // Center Hero: Evolving Dragon Ball Charging Stickman + Synchronized 20-Tier Aura + "W O R D" title
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
                    .heightIn(min = 185.dp)
                    .padding(vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                HomeChargingStickmanCenterpiece(
                    currentLevel = levelNumber,
                    currentTier = currentTier,
                    rankTier = activeRank,
                    fontFamily = fontFamily,
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("menu_peril_canvas")
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            DoodleWordTitleHeader(fontFamily = fontFamily)
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Dynamic Tier Progression Card (Current Rank Title, Tier Progress Bar & Vivid Animated Canvas Aura)
        TierProgressionCard(
            rankTier = activeRank,
            levelNumber = levelNumber,
            completedLevels = completedLevels,
            totalLevels = totalCampaignLevels,
            fontFamily = fontFamily,
            onClick = null,
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
            // 1. Level X (Yellow fill, primary action -> launches Level X directly, keeping upcoming peril name in subtitle!)
            SketchyContainer(
                fillColor = DoodleColors.DoodleYellow,
                cornerRadius = 28.dp,
                onClick = { onPlayCampaign(null) },
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
                        text = "${activePeril.displayName} • ${activeRank.title}",
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

/**
 * Interactive Home Screen Centerpiece featuring:
 * - 3-Stage Evolving Dragon Ball Stickman Stance (Tiers 1–5 Grounded Ki Charge, Tiers 6–12 Ascendant Hover, Tiers 13–20 Transcendent Cosmic Float)
 * - Double-layered jagged flickering Dragon Ball Energy Shell + upward-rising wind/ki lines
 * - 20 distinct Tier-synchronized aura visual themes matching the Tier Progress Card below it
 * - Tap-to-Power-Up Surge (`surgeMultiplier` 1.0f -> 2.5f decaying over 800ms, doubled pulse speed/scale,
 *   canvas micro-shake, radial burst particles, floating comic text, haptics, and procedural AudioTrack Ki Burst).
 */
@Composable
fun HomeChargingStickmanCenterpiece(
    currentLevel: Int,
    currentTier: Int = ((currentLevel - 1) / 50 + 1).coerceIn(1, 20),
    rankTier: RankTier = RankTier.forLevel(currentLevel),
    fontFamily: FontFamily,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    // Surge multiplier spikes from 1.0f to 2.5f on tap and decays smoothly over 800ms
    val surgeAnim = remember { Animatable(1.0f) }
    val surgeMultiplier = surgeAnim.value

    // Rapid tap combo tracking for rising AudioTrack Ki Burst pitch
    var rapidTapCombo by remember { mutableIntStateOf(0) }
    var lastTapTimestampMs by remember { mutableLongStateOf(0L) }
    var frameClockMs by remember { mutableLongStateOf(0L) }
    var accumulatedTimeSec by remember { mutableFloatStateOf(0f) }

    val burstParticles = remember { mutableStateListOf<KiBurstParticle>() }
    val comicPopups = remember { mutableStateListOf<ComicSurgePopup>() }

    // Continuous infinite transitions for Dragon Ball shell pulsing and aura cycles
    val infiniteTransition = rememberInfiniteTransition(label = "stickmanKiInfinite")
    val rawAuraPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "stickmanAuraPhase"
    )
    val baseShellPulse by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.14f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 360, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "stickmanShellPulse"
    )
    val fastFlicker by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 190, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "stickmanFastFlicker"
    )

    // 60fps frame loop that accelerates time when surging and cleans up expired burst particles/popups
    LaunchedEffect(Unit) {
        var prevNanos = 0L
        while (true) {
            withFrameNanos { nanos ->
                val dtSec = if (prevNanos == 0L) 0.016f else ((nanos - prevNanos) / 1_000_000_000f).coerceIn(0.001f, 0.05f)
                prevNanos = nanos
                // Double the aura pulse/animation speed during a surge
                val speedFactor = if (surgeAnim.value > 1.05f) {
                    1f + (surgeAnim.value - 1f) * 0.95f
                } else {
                    1f
                }
                accumulatedTimeSec += dtSec * speedFactor
                val nowMs = nanos / 1_000_000L
                frameClockMs = nowMs

                burstParticles.removeAll { nowMs - it.createdAtMs > it.durationMs }
                comicPopups.removeAll { nowMs - it.createdAtMs > it.durationMs }
            }
        }
    }

    val comicPhrases = remember {
        listOf("SURGE!", "+KI!", "MAX INK!", "POWER UP!", "OVERDRIVE!", "ULTRA DOODLE!")
    }

    BoxWithConstraints(
        modifier = modifier
            .pointerInput(currentTier, rankTier) {
                detectTapGestures { tapOffset ->
                    val now = System.currentTimeMillis()
                    rapidTapCombo = if (now - lastTapTimestampMs < 950L) {
                        (rapidTapCombo + 1).coerceAtMost(12)
                    } else {
                        1
                    }
                    lastTapTimestampMs = now

                    // 1. Haptic feedback
                    runCatching {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }

                    // 2. Procedural Dragon Ball Ki Burst / Power Hum AudioTrack synthesis (pitch rises with rapid taps)
                    coroutineScope.launch {
                        playProceduralKiBurstAudioTrack(
                            comboStep = rapidTapCombo,
                            tierIndex = currentTier
                        )
                    }

                    // 3. Spike surgeMultiplier to 2.5f and decay smoothly over 800ms
                    coroutineScope.launch {
                        surgeAnim.snapTo(2.5f)
                        surgeAnim.animateTo(
                            targetValue = 1.0f,
                            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
                        )
                    }

                    // 4. Spawn 20 radial burst particles matching the current Tier's color palette
                    val canvasW = size.width.toFloat().coerceAtLeast(1f)
                    val canvasH = size.height.toFloat().coerceAtLeast(1f)
                    val originX = if (tapOffset.x in (canvasW * 0.15f)..(canvasW * 0.85f)) {
                        tapOffset.x
                    } else {
                        canvasW * 0.5f
                    }
                    val originY = if (tapOffset.y in (canvasH * 0.15f)..(canvasH * 0.85f)) {
                        tapOffset.y
                    } else {
                        canvasH * 0.52f
                    }

                    val particleCount = Random.nextInt(18, 25)
                    val createdTime = frameClockMs.takeIf { it > 0L } ?: System.currentTimeMillis()
                    val palette = listOf(
                        rankTier.primaryColor,
                        rankTier.accentColor,
                        DoodleColors.DoodleYellow,
                        Color.White
                    )
                    for (i in 0 until particleCount) {
                        val baseAngle = (2.0 * PI * i / particleCount).toFloat() +
                            (Random.nextFloat() - 0.5f) * 0.28f
                        val speed = 135f + Random.nextFloat() * 210f
                        burstParticles.add(
                            KiBurstParticle(
                                id = createdTime * 100L + i + Random.nextInt(1000),
                                startX = originX,
                                startY = originY,
                                vx = cos(baseAngle) * speed,
                                vy = sin(baseAngle) * speed - 35f,
                                color = palette[i % palette.size],
                                radiusDp = 3.2f + Random.nextFloat() * 4.5f,
                                isSparkStar = i % 2 == 0,
                                createdAtMs = createdTime,
                                durationMs = 650L + Random.nextLong(220L)
                            )
                        )
                    }

                    // 5. Spawn floating sketchy comic text popup ("SURGE!", "+KI!", "MAX INK!")
                    val phrase = comicPhrases[(rapidTapCombo - 1 + Random.nextInt(comicPhrases.size)) % comicPhrases.size]
                    val xFrac = ((originX / canvasW) + (Random.nextFloat() - 0.5f) * 0.24f).coerceIn(0.20f, 0.80f)
                    val yFrac = ((originY / canvasH) - 0.12f + (Random.nextFloat() - 0.5f) * 0.10f).coerceIn(0.18f, 0.62f)
                    comicPopups.add(
                        ComicSurgePopup(
                            id = createdTime + Random.nextLong(9999L),
                            text = phrase,
                            xFraction = xFrac,
                            yFraction = yFrac,
                            tiltDeg = (Random.nextFloat() - 0.5f) * 18f,
                            createdAtMs = createdTime
                        )
                    )
                }
            },
        contentAlignment = Alignment.Center
    ) {
        val boxWidthPx = with(LocalDensity.current) { maxWidth.toPx() }
        val boxHeightPx = with(LocalDensity.current) { maxHeight.toPx() }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("home_charging_stickman_canvas")
        ) {
            val w = size.width
            val h = size.height
            val time = accumulatedTimeSec
            val surgeBoost = (surgeMultiplier - 1f).coerceAtLeast(0f) // 0.0f .. 1.5f
            val effectivePulse = baseShellPulse * (1f + surgeBoost * 0.38f)
            val effectivePhase = (rawAuraPhase + time * 1.9f) % (2f * PI).toFloat()

            // Canvas micro-shake when surging
            val shakeAmp = if (surgeBoost > 0.04f) (surgeBoost * 4.2.dp.toPx()) else 0f
            val shakeX = sin(time * 68f) * shakeAmp
            val shakeY = cos(time * 79f) * shakeAmp

            val groundY = h * 0.84f
            val centerX = w * 0.5f

            // Compute stickman vertical levitation offset based on 3-stage Dragon Ball evolution:
            // Tiers 1–5: Grounded Ki Charge (planted firmly on groundY)
            // Tiers 6–12: Ascendant Hover (levitating 25.dp above ground line + sin(time * 4f) * 8f)
            // Tiers 13–20: Transcendent Cosmic Float (levitating 40.dp above ground line + Zen float)
            val hoverOffsetPx = when {
                currentTier in 1..5 -> 0f
                currentTier in 6..12 -> 25.dp.toPx() + sin(time * 4f) * 8.dp.toPx() + surgeBoost * 6.dp.toPx()
                else -> 40.dp.toPx() + sin(time * 3.2f) * 10.dp.toPx() + surgeBoost * 9.dp.toPx()
            }

            val stickmanFootY = groundY - hoverOffsetPx
            val stickmanCoreY = stickmanFootY - 46.dp.toPx()

            withTransform({
                translate(left = shakeX, top = shakeY)
            }) {
                // 1. Draw Tier-Synchronized Background & Midground Aura (Tiers 1 to 20)
                drawStickmanTierAura(
                    currentTier = currentTier,
                    rankTier = rankTier,
                    center = Offset(centerX, stickmanCoreY),
                    groundY = groundY,
                    time = time,
                    phase = effectivePhase,
                    pulse = effectivePulse,
                    surgeMultiplier = surgeMultiplier
                )

                // 2. Draw Double-Layered Jagged Dragon Ball Energy Shell + Upward Wind/Ki Lines
                drawDragonBallEnergyShell(
                    center = Offset(centerX, stickmanCoreY),
                    footY = stickmanFootY,
                    groundY = groundY,
                    rankTier = rankTier,
                    time = time,
                    pulse = effectivePulse,
                    flicker = fastFlicker,
                    surgeMultiplier = surgeMultiplier
                )

                // 3. Draw Horizontal Sketchy Ground Line, Stage Shadows, Floating Pebbles & Shockwave Rings
                drawHomeGroundAndStageEffects(
                    currentTier = currentTier,
                    rankTier = rankTier,
                    centerX = centerX,
                    groundY = groundY,
                    hoverOffsetPx = hoverOffsetPx,
                    time = time,
                    surgeMultiplier = surgeMultiplier
                )

                // 4. Draw 3-Stage Evolving Dragon Ball Black Doodle Stickman
                drawEvolvingKiStickman(
                    currentTier = currentTier,
                    rankTier = rankTier,
                    centerX = centerX,
                    footY = stickmanFootY,
                    time = time,
                    surgeMultiplier = surgeMultiplier
                )

                // 5. Draw Interactive Tap-to-Surge Radial Burst Particles
                val nowMs = frameClockMs
                burstParticles.forEach { pt ->
                    val elapsedSec = ((nowMs - pt.createdAtMs).coerceAtLeast(0L)) / 1000f
                    val progress = ((nowMs - pt.createdAtMs).toFloat() / pt.durationMs.toFloat()).coerceIn(0f, 1f)
                    val alpha = (1f - progress).coerceIn(0f, 1f)
                    val px = pt.startX + pt.vx * elapsedSec
                    val py = pt.startY + pt.vy * elapsedSec + 110f * elapsedSec * elapsedSec
                    val rPx = pt.radiusDp.dp.toPx() * (1f + (1f - progress) * 0.45f)

                    if (pt.isSparkStar) {
                        drawDoodleSparkleStar(
                            center = Offset(px, py),
                            radius = rPx * 1.45f,
                            color = pt.color.copy(alpha = alpha),
                            rotationRad = elapsedSec * 9f
                        )
                    } else {
                        drawCircle(
                            color = pt.color.copy(alpha = alpha),
                            radius = rPx,
                            center = Offset(px, py)
                        )
                        drawCircle(
                            color = DoodleColors.InkBlack.copy(alpha = alpha * 0.7f),
                            radius = rPx,
                            center = Offset(px, py),
                            style = Stroke(width = 1.3.dp.toPx())
                        )
                    }
                }
            }
        }

        // 6. Floating Sketchy Comic Text Popups ("SURGE!", "+KI!", "MAX INK!")
        val nowMs = frameClockMs
        comicPopups.forEach { popup ->
            val progress = ((nowMs - popup.createdAtMs).toFloat() / popup.durationMs.toFloat()).coerceIn(0f, 1f)
            val riseOffsetPx = -progress * 54.dp.value
            val popupScale = if (progress < 0.2f) {
                0.7f + (progress / 0.2f) * 0.45f
            } else {
                1.15f - ((progress - 0.2f) / 0.8f) * 0.25f
            }
            val targetX = ((popup.xFraction - 0.5f) * boxWidthPx).roundToInt()
            val targetY = ((popup.yFraction - 0.5f) * boxHeightPx + with(LocalDensity.current) { riseOffsetPx.dp.toPx() }).roundToInt()

            Box(
                modifier = Modifier
                    .offset { IntOffset(targetX, targetY) }
                    .scale(popupScale)
            ) {
                SketchyContainer(
                    fillColor = rankTier.primaryColor,
                    borderColor = DoodleColors.InkBlack,
                    cornerRadius = 12.dp,
                    strokeWidth = 2.dp,
                    shadowOffset = 2.dp
                ) {
                    Text(
                        text = popup.text,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = DoodleColors.InkBlack,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

/**
 * Zero-asset procedural Dragon Ball-style "Ki Burst / Power Hum" frequency sweep
 * using Android's [AudioTrack] (16-bit PCM sine + sub-octave + filtered ki whoosh noise)
 * whose pitch rises with rapid consecutive taps.
 */
private suspend fun playProceduralKiBurstAudioTrack(
    comboStep: Int,
    tierIndex: Int
) {
    if (!ProceduralSoundEngine.soundEnabled) return
    withContext(Dispatchers.Default) {
        runCatching {
            val sampleRate = 22050
            val durationSec = 0.26
            val numSamples = (sampleRate * durationSec).toInt()
            val samples = ShortArray(numSamples)

            // Base frequency rises with both rapid tap combo and player's tier
            val pitchMultiplier = 1.0 + (comboStep.coerceIn(1, 12) - 1) * 0.085 + (tierIndex * 0.012)
            val startFreq = 115.0 * pitchMultiplier
            val peakFreq = 310.0 * pitchMultiplier
            val rng = Random(System.nanoTime())

            var phaseMain = 0.0
            var phaseSub = 0.0
            var phaseHarm = 0.0
            var lpNoise = 0.0

            for (i in 0 until numSamples) {
                val progress = i.toDouble() / numSamples
                val t = i.toDouble() / sampleRate

                // Frequency sweep: fast upward surge in first 35%, resonant power hum decay after
                val sweepFrac = if (progress < 0.35) {
                    progress / 0.35
                } else {
                    1.0 - (progress - 0.35) * 0.45
                }
                // Rapid Dragon Ball vibrato warble (28 Hz)
                val vibrato = sin(2.0 * PI * 28.0 * t) * 18.0 * pitchMultiplier
                val instFreq = startFreq + (peakFreq - startFreq) * sweepFrac + vibrato

                phaseMain += (2.0 * PI * instFreq) / sampleRate
                phaseSub += (2.0 * PI * (instFreq * 0.5)) / sampleRate
                phaseHarm += (2.0 * PI * (instFreq * 1.5)) / sampleRate

                // Filtered aura wind/flame roar noise
                val white = rng.nextDouble() * 2.0 - 1.0
                val cutoff = (0.18 + 0.25 * sin(progress * PI)).coerceIn(0.05, 0.55)
                lpNoise += cutoff * (white - lpNoise)

                val env = when {
                    progress < 0.08 -> progress / 0.08
                    else -> exp(-(progress - 0.08) * 3.4)
                }

                val kiWave = (
                    sin(phaseMain) * 0.46 +
                        sin(phaseSub) * 0.28 +
                        sin(phaseHarm) * 0.12 +
                        lpNoise * 0.34
                    ) * env

                samples[i] = (kiWave.coerceIn(-1.0, 1.0) * Short.MAX_VALUE * 0.82).toInt().toShort()
            }

            val bufferBytes = numSamples * 2
            val minBuf = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(bufferBytes)

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(minBuf)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(samples, 0, samples.size)
            audioTrack.play()
            delay((durationSec * 1000).toLong() + 60L)
            runCatching {
                audioTrack.stop()
                audioTrack.release()
            }
        }
    }
}

/**
 * Draws the double-layered, jagged, flickering Dragon Ball outer energy shell (`Path`)
 * around the stickman along with upward-rising sketchy wind/ki lines.
 */
private fun DrawScope.drawDragonBallEnergyShell(
    center: Offset,
    footY: Float,
    groundY: Float,
    rankTier: RankTier,
    time: Float,
    pulse: Float,
    flicker: Float,
    surgeMultiplier: Float
) {
    val surgeScale = 1f + (surgeMultiplier - 1f) * 0.35f
    val outerWidth = 58.dp.toPx() * pulse * surgeScale
    val outerHeight = 76.dp.toPx() * pulse * surgeScale
    val baseBottomY = max(footY + 6.dp.toPx(), groundY - 4.dp.toPx())
    val crownTopY = center.y - outerHeight * 0.88f

    // Soft radial core glow behind the energy shell
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                rankTier.primaryColor.copy(alpha = (0.48f * surgeScale).coerceIn(0f, 0.82f)),
                rankTier.accentColor.copy(alpha = (0.24f * surgeScale).coerceIn(0f, 0.55f)),
                Color.Transparent
            ),
            center = center,
            radius = outerHeight * 1.15f
        ),
        center = center,
        radius = outerHeight * 1.15f
    )

    // Helper to build a jagged flame-like Dragon Ball aura shell Path
    fun buildJaggedKiShell(scaleX: Float, scaleY: Float, phaseShift: Float, spikes: Int): Path {
        val path = Path()
        val halfW = outerWidth * scaleX
        val totalH = (baseBottomY - crownTopY) * scaleY
        val topY = baseBottomY - totalH

        path.moveTo(center.x - halfW * 0.62f, baseBottomY)

        // Left side jagged upward spikes
        for (i in 1..spikes) {
            val frac = i.toFloat() / spikes
            val y = baseBottomY - totalH * frac
            val profile = sin(frac * PI).toFloat()
            val jag = sin(time * 24f + i * 1.7f + phaseShift) * (6.dp.toPx() * surgeScale)
            val x = center.x - halfW * (0.45f + 0.65f * profile) + jag

            val ctrlY = y + (totalH / spikes) * 0.45f
            val ctrlX = x - 7.dp.toPx() * (0.6f + flicker * 0.5f)
            path.quadraticTo(ctrlX, ctrlY, x, y)
        }

        // Sharp flickering apex crown
        val apexX = center.x + sin(time * 30f + phaseShift) * 5.dp.toPx()
        val apexY = topY - (8.dp.toPx() * (0.7f + flicker * 0.6f) * surgeScale)
        path.lineTo(apexX, apexY)

        // Right side jagged downward spikes
        for (i in spikes downTo 1) {
            val frac = i.toFloat() / spikes
            val y = baseBottomY - totalH * frac
            val profile = sin(frac * PI).toFloat()
            val jag = cos(time * 24f + i * 1.9f + phaseShift) * (6.dp.toPx() * surgeScale)
            val x = center.x + halfW * (0.45f + 0.65f * profile) + jag

            val ctrlY = y - (totalH / spikes) * 0.35f
            val ctrlX = x + 7.dp.toPx() * (0.6f + flicker * 0.5f)
            path.quadraticTo(ctrlX, ctrlY, x, y)
        }

        path.lineTo(center.x + halfW * 0.62f, baseBottomY)
        path.close()
        return path
    }

    val outerShellPath = buildJaggedKiShell(scaleX = 1.05f, scaleY = 1.04f, phaseShift = 0f, spikes = 7)
    val innerShellPath = buildJaggedKiShell(scaleX = 0.74f, scaleY = 0.82f, phaseShift = 1.6f, spikes = 6)

    // Layer 1: Outer jagged aura fill & sketchy border
    drawPath(
        path = outerShellPath,
        brush = Brush.verticalGradient(
            colors = listOf(
                rankTier.accentColor.copy(alpha = 0.30f),
                rankTier.primaryColor.copy(alpha = 0.46f),
                rankTier.accentColor.copy(alpha = 0.22f)
            ),
            startY = crownTopY,
            endY = baseBottomY
        )
    )
    drawPath(
        path = outerShellPath,
        color = rankTier.accentColor.copy(alpha = 0.82f),
        style = Stroke(width = 2.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
    drawPath(
        path = outerShellPath,
        color = DoodleColors.InkBlack.copy(alpha = 0.55f),
        style = Stroke(width = 1.2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
    )

    // Layer 2: Inner intense core aura shell
    drawPath(
        path = innerShellPath,
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.58f),
                rankTier.primaryColor.copy(alpha = 0.55f),
                Color.White.copy(alpha = 0.32f)
            ),
            startY = crownTopY,
            endY = baseBottomY
        )
    )
    drawPath(
        path = innerShellPath,
        color = rankTier.primaryColor.copy(alpha = 0.92f),
        style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
    )

    // Upward-rising sketchy wind / ki lines around the stickman
    val windLineCount = if (surgeMultiplier > 1.2f) 14 else 9
    for (i in 0 until windLineCount) {
        val side = if (i % 2 == 0) -1f else 1f
        val lane = (i / 2) + 1
        val xSpread = (16.dp.toPx() + lane * 11.dp.toPx()) * side +
            sin(time * 9f + i) * 3.dp.toPx()
        val cycle = ((time * (2.4f + (i % 3) * 0.45f) + i * 0.21f) % 1f)
        val startY = baseBottomY - cycle * (outerHeight * 1.28f)
        val lineLen = (14.dp.toPx() + (i % 3) * 6.dp.toPx()) * surgeScale
        val alpha = sin(cycle * PI).toFloat().coerceIn(0f, 1f) * 0.78f

        drawLine(
            color = if (i % 3 == 0) DoodleColors.InkBlack.copy(alpha = alpha * 0.55f) else rankTier.accentColor.copy(alpha = alpha),
            start = Offset(center.x + xSpread, startY),
            end = Offset(center.x + xSpread * 0.94f, startY - lineLen),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

/**
 * Draws the horizontal ground line, stage-specific ground shadow, levitating pebbles (Tiers 6–12),
 * and rhythmic downward shockwave rings (Tiers 13–20).
 */
private fun DrawScope.drawHomeGroundAndStageEffects(
    currentTier: Int,
    rankTier: RankTier,
    centerX: Float,
    groundY: Float,
    hoverOffsetPx: Float,
    time: Float,
    surgeMultiplier: Float
) {
    val w = size.width
    val lineMargin = w * 0.08f

    // Sketchy double-stroked horizontal ground line
    drawLine(
        color = DoodleColors.InkBlack,
        start = Offset(lineMargin, groundY),
        end = Offset(w - lineMargin, groundY),
        strokeWidth = 3.dp.toPx(),
        cap = StrokeCap.Round
    )
    drawLine(
        color = DoodleColors.InkBlack.copy(alpha = 0.45f),
        start = Offset(lineMargin + 8.dp.toPx(), groundY + 3.dp.toPx()),
        end = Offset(w - lineMargin - 6.dp.toPx(), groundY + 2.dp.toPx()),
        strokeWidth = 1.4.dp.toPx(),
        cap = StrokeCap.Round
    )

    // Hand-drawn ground hatching ticks under the line
    val tickCount = 14
    val span = w - lineMargin * 2f
    for (i in 0..tickCount) {
        val tx = lineMargin + (span / tickCount) * i
        drawLine(
            color = DoodleColors.InkBlack.copy(alpha = 0.35f),
            start = Offset(tx, groundY + 1.dp.toPx()),
            end = Offset(tx - 5.dp.toPx(), groundY + 7.dp.toPx()),
            strokeWidth = 1.4.dp.toPx(),
            cap = StrokeCap.Round
        )
    }

    // Scaling sketchy oval shadow on the ground line
    val shadowScale = (1f - (hoverOffsetPx / 95.dp.toPx()).coerceIn(0f, 0.58f)) *
        (1f + (surgeMultiplier - 1f) * 0.18f)
    val shadowW = 54.dp.toPx() * shadowScale
    val shadowH = 10.dp.toPx() * shadowScale
    drawOval(
        color = DoodleColors.InkBlack.copy(alpha = 0.22f),
        topLeft = Offset(centerX - shadowW * 0.5f, groundY - shadowH * 0.5f),
        size = Size(shadowW, shadowH)
    )
    drawOval(
        color = DoodleColors.InkBlack.copy(alpha = 0.55f),
        topLeft = Offset(centerX - shadowW * 0.5f, groundY - shadowH * 0.5f),
        size = Size(shadowW, shadowH),
        style = Stroke(width = 1.4.dp.toPx())
    )

    // Tiers 6–12 (Ascendant Hover): Small pebble doodles floating upward off the ground line
    if (currentTier in 6..20) {
        val pebbleCount = if (currentTier >= 13) 10 else 7
        for (i in 0 until pebbleCount) {
            val side = if (i % 2 == 0) -1f else 1f
            val dist = 26.dp.toPx() + (i * 11.dp.toPx())
            val liftProgress = ((time * (0.65f + (i % 3) * 0.22f) + i * 0.17f) % 1f)
            val px = centerX + side * dist + sin(time * 3f + i) * 3.dp.toPx()
            val py = groundY - 4.dp.toPx() - liftProgress * 46.dp.toPx()
            val pRadius = (2.4.dp.toPx() + (i % 3) * 1.1.dp.toPx()) * (1f - liftProgress * 0.35f)

            drawCircle(
                color = DoodleColors.CrateStone,
                radius = pRadius,
                center = Offset(px, py)
            )
            drawCircle(
                color = DoodleColors.InkBlack,
                radius = pRadius,
                center = Offset(px, py),
                style = Stroke(width = 1.3.dp.toPx())
            )
        }
    }

    // Tiers 13–20 (Transcendent Cosmic Float) or active surge: Rhythmic downward shockwave rings rippling across the ground line
    if (currentTier >= 13 || surgeMultiplier > 1.15f) {
        val ringCount = 3
        for (r in 0 until ringCount) {
            val prog = ((time * 1.35f + r * (1f / ringCount)) % 1f)
            val ringW = (36.dp.toPx() + prog * 155.dp.toPx()) * (1f + (surgeMultiplier - 1f) * 0.3f)
            val ringH = (6.dp.toPx() + prog * 18.dp.toPx())
            val ringAlpha = (1f - prog).coerceIn(0f, 1f) * 0.78f

            drawOval(
                color = rankTier.accentColor.copy(alpha = ringAlpha),
                topLeft = Offset(centerX - ringW * 0.5f, groundY - ringH * 0.5f),
                size = Size(ringW, ringH),
                style = Stroke(width = 2.2.dp.toPx())
            )
            drawOval(
                color = DoodleColors.InkBlack.copy(alpha = ringAlpha * 0.5f),
                topLeft = Offset(centerX - ringW * 0.52f, groundY - ringH * 0.52f),
                size = Size(ringW * 1.04f, ringH * 1.04f),
                style = Stroke(width = 1.dp.toPx())
            )
        }
    }
}

/**
 * Draws the 3-Stage Evolving Dragon Ball Black Doodle Stickman:
 * - Tiers 1–5 (Lv 1–250 — Grounded Ki Charge): Wide bent knees planted firmly on the horizontal ground line,
 *   bent elbows with clenched fists at waist level, angled determined eyebrows, and high-frequency body vibration (`sin(time * 35f) * 2f`).
 * - Tiers 6–12 (Lv 251–600 — Ascendant Hover): Levitating 25.dp above the ground line with smooth hover,
 *   one knee raised, and energy crackling around the hands.
 * - Tiers 13–20 (Lv 601–1,000 — Transcendent Cosmic Float): High mid-air levitation (40.dp above the ground line)
 *   in an omnipotent arms-crossed Zen float pose with glowing eyes.
 */
private fun DrawScope.drawEvolvingKiStickman(
    currentTier: Int,
    rankTier: RankTier,
    centerX: Float,
    footY: Float,
    time: Float,
    surgeMultiplier: Float
) {
    val strokeW = 3.6.dp.toPx()
    val headRadius = 13.5.dp.toPx()

    // Stage 1 subtle high-frequency body vibration (`sin(time * 35f) * 2f`), amplified on surge
    val vibX = if (currentTier in 1..5 || surgeMultiplier > 1.1f) {
        sin(time * 35f) * 2f * surgeMultiplier
    } else {
        sin(time * 12f) * 0.8f
    }

    val cx = centerX + vibX

    when {
        currentTier in 1..5 -> {
            // ====================================================================
            // STAGE 1: TIERS 1–5 (GROUNDED KI CHARGE — HORSE STANCE & CLENCHED FISTS)
            // ====================================================================
            val hipY = footY - 26.dp.toPx()
            val shoulderY = hipY - 28.dp.toPx()
            val headCenter = Offset(cx, shoulderY - headRadius - 4.dp.toPx())

            // Wide bent knees planted firmly on the horizontal ground line
            val leftKnee = Offset(cx - 17.dp.toPx(), hipY + 12.dp.toPx())
            val leftFoot = Offset(cx - 22.dp.toPx(), footY)
            val rightKnee = Offset(cx + 17.dp.toPx(), hipY + 12.dp.toPx())
            val rightFoot = Offset(cx + 22.dp.toPx(), footY)

            // Torso
            drawLine(
                color = DoodleColors.InkBlack,
                start = Offset(cx, shoulderY),
                end = Offset(cx, hipY),
                strokeWidth = strokeW,
                cap = StrokeCap.Round
            )

            // Left & right bent legs
            drawLine(DoodleColors.InkBlack, Offset(cx, hipY), leftKnee, strokeW, StrokeCap.Round)
            drawLine(DoodleColors.InkBlack, leftKnee, leftFoot, strokeW, StrokeCap.Round)
            drawLine(DoodleColors.InkBlack, Offset(cx, hipY), rightKnee, strokeW, StrokeCap.Round)
            drawLine(DoodleColors.InkBlack, rightKnee, rightFoot, strokeW, StrokeCap.Round)

            // Bent elbows with clenched fists at waist level
            val leftElbow = Offset(cx - 21.dp.toPx(), shoulderY + 13.dp.toPx())
            val leftFist = Offset(cx - 13.dp.toPx(), hipY - 3.dp.toPx())
            val rightElbow = Offset(cx + 21.dp.toPx(), shoulderY + 13.dp.toPx())
            val rightFist = Offset(cx + 13.dp.toPx(), hipY - 3.dp.toPx())

            drawLine(DoodleColors.InkBlack, Offset(cx, shoulderY + 3.dp.toPx()), leftElbow, strokeW, StrokeCap.Round)
            drawLine(DoodleColors.InkBlack, leftElbow, leftFist, strokeW, StrokeCap.Round)
            drawLine(DoodleColors.InkBlack, Offset(cx, shoulderY + 3.dp.toPx()), rightElbow, strokeW, StrokeCap.Round)
            drawLine(DoodleColors.InkBlack, rightElbow, rightFist, strokeW, StrokeCap.Round)

            // Clenched fists with glowing ki aura knuckles
            listOf(leftFist, rightFist).forEach { fist ->
                drawCircle(
                    color = rankTier.primaryColor.copy(alpha = 0.78f),
                    radius = 6.2.dp.toPx() * (1f + (surgeMultiplier - 1f) * 0.3f),
                    center = fist
                )
                drawCircle(
                    color = DoodleColors.InkBlack,
                    radius = 4.2.dp.toPx(),
                    center = fist
                )
            }

            // Doodle Head (off-white fill + black ink outline)
            drawCircle(color = DoodleColors.PaperOffWhite, radius = headRadius, center = headCenter)
            drawCircle(
                color = DoodleColors.InkBlack,
                radius = headRadius,
                center = headCenter,
                style = Stroke(width = strokeW)
            )

            // Angled determined eyebrows & gritted charging mouth
            val eyeY = headCenter.y - 1.dp.toPx()
            drawCircle(DoodleColors.InkBlack, radius = 2.1.dp.toPx(), center = Offset(headCenter.x - 4.5.dp.toPx(), eyeY))
            drawCircle(DoodleColors.InkBlack, radius = 2.1.dp.toPx(), center = Offset(headCenter.x + 4.5.dp.toPx(), eyeY))

            // Sharp angled determined eyebrows
            drawLine(
                color = DoodleColors.InkBlack,
                start = Offset(headCenter.x - 8.dp.toPx(), eyeY - 4.5.dp.toPx()),
                end = Offset(headCenter.x - 1.8.dp.toPx(), eyeY - 1.8.dp.toPx()),
                strokeWidth = 2.2.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color = DoodleColors.InkBlack,
                start = Offset(headCenter.x + 8.dp.toPx(), eyeY - 4.5.dp.toPx()),
                end = Offset(headCenter.x + 1.8.dp.toPx(), eyeY - 1.8.dp.toPx()),
                strokeWidth = 2.2.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Fierce charging mouth line
            drawLine(
                color = DoodleColors.InkBlack,
                start = Offset(headCenter.x - 4.dp.toPx(), headCenter.y + 5.5.dp.toPx()),
                end = Offset(headCenter.x + 4.dp.toPx(), headCenter.y + 4.8.dp.toPx()),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        currentTier in 6..12 -> {
            // ====================================================================
            // STAGE 2: TIERS 6–12 (ASCENDANT HOVER — ONE KNEE RAISED & HAND CRACKLE)
            // ====================================================================
            val hipY = footY - 30.dp.toPx()
            val shoulderY = hipY - 28.dp.toPx()
            val headCenter = Offset(cx, shoulderY - headRadius - 4.dp.toPx())

            // Torso
            drawLine(DoodleColors.InkBlack, Offset(cx, shoulderY), Offset(cx, hipY), strokeW, StrokeCap.Round)

            // Left leg extended downward, Right knee raised in ascendant martial hover
            val leftKnee = Offset(cx - 10.dp.toPx(), hipY + 15.dp.toPx())
            val leftFoot = Offset(cx - 13.dp.toPx(), footY)
            val rightKnee = Offset(cx + 15.dp.toPx(), hipY + 3.dp.toPx())
            val rightFoot = Offset(cx + 9.dp.toPx(), hipY + 16.dp.toPx())

            drawLine(DoodleColors.InkBlack, Offset(cx, hipY), leftKnee, strokeW, StrokeCap.Round)
            drawLine(DoodleColors.InkBlack, leftKnee, leftFoot, strokeW, StrokeCap.Round)
            drawLine(DoodleColors.InkBlack, Offset(cx, hipY), rightKnee, strokeW, StrokeCap.Round)
            drawLine(DoodleColors.InkBlack, rightKnee, rightFoot, strokeW, StrokeCap.Round)

            // Outstretched energy-channeling arms
            val leftElbow = Offset(cx - 19.dp.toPx(), shoulderY + 10.dp.toPx())
            val leftHand = Offset(cx - 26.dp.toPx(), shoulderY + 2.dp.toPx())
            val rightElbow = Offset(cx + 19.dp.toPx(), shoulderY + 10.dp.toPx())
            val rightHand = Offset(cx + 26.dp.toPx(), shoulderY + 2.dp.toPx())

            drawLine(DoodleColors.InkBlack, Offset(cx, shoulderY + 3.dp.toPx()), leftElbow, strokeW, StrokeCap.Round)
            drawLine(DoodleColors.InkBlack, leftElbow, leftHand, strokeW, StrokeCap.Round)
            drawLine(DoodleColors.InkBlack, Offset(cx, shoulderY + 3.dp.toPx()), rightElbow, strokeW, StrokeCap.Round)
            drawLine(DoodleColors.InkBlack, rightElbow, rightHand, strokeW, StrokeCap.Round)

            // Energy crackling around both hands
            listOf(leftHand, rightHand).forEachIndexed { idx, hand ->
                val orbR = (6.5.dp.toPx() + sin(time * 16f + idx) * 2.dp.toPx()) * surgeMultiplier.coerceAtMost(1.8f)
                drawCircle(
                    color = rankTier.primaryColor.copy(alpha = 0.75f),
                    radius = orbR,
                    center = hand
                )
                drawCircle(
                    color = rankTier.accentColor,
                    radius = orbR * 0.65f,
                    center = hand,
                    style = Stroke(width = 1.6.dp.toPx())
                )
                // Mini lightning arcs crackling around the hand
                for (arc in 0..2) {
                    val a = time * 14f + arc * 2.1f + idx
                    val p1 = Offset(hand.x + cos(a) * orbR * 0.5f, hand.y + sin(a) * orbR * 0.5f)
                    val p2 = Offset(hand.x + cos(a + 0.4f) * orbR * 1.45f, hand.y + sin(a + 0.4f) * orbR * 1.45f)
                    drawLine(
                        color = rankTier.accentColor,
                        start = p1,
                        end = p2,
                        strokeWidth = 1.6.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
                drawCircle(DoodleColors.InkBlack, radius = 3.8.dp.toPx(), center = hand)
            }

            // Doodle Head
            drawCircle(color = DoodleColors.PaperOffWhite, radius = headRadius, center = headCenter)
            drawCircle(
                color = DoodleColors.InkBlack,
                radius = headRadius,
                center = headCenter,
                style = Stroke(width = strokeW)
            )

            // Confident ascendant eyes & eyebrows
            val eyeY = headCenter.y - 1.dp.toPx()
            drawCircle(rankTier.accentColor, radius = 2.8.dp.toPx(), center = Offset(headCenter.x - 4.5.dp.toPx(), eyeY))
            drawCircle(rankTier.accentColor, radius = 2.8.dp.toPx(), center = Offset(headCenter.x + 4.5.dp.toPx(), eyeY))
            drawCircle(DoodleColors.InkBlack, radius = 1.6.dp.toPx(), center = Offset(headCenter.x - 4.5.dp.toPx(), eyeY))
            drawCircle(DoodleColors.InkBlack, radius = 1.6.dp.toPx(), center = Offset(headCenter.x + 4.5.dp.toPx(), eyeY))

            drawLine(
                color = DoodleColors.InkBlack,
                start = Offset(headCenter.x - 8.dp.toPx(), eyeY - 4.5.dp.toPx()),
                end = Offset(headCenter.x - 2.dp.toPx(), eyeY - 2.dp.toPx()),
                strokeWidth = 2.2.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color = DoodleColors.InkBlack,
                start = Offset(headCenter.x + 8.dp.toPx(), eyeY - 4.5.dp.toPx()),
                end = Offset(headCenter.x + 2.dp.toPx(), eyeY - 2.dp.toPx()),
                strokeWidth = 2.2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        else -> {
            // ====================================================================
            // STAGE 3: TIERS 13–20 (TRANSCENDENT COSMIC FLOAT — OMNIPOTENT ARMS-CROSSED ZEN POSE & GLOWING EYES)
            // ====================================================================
            val hipY = footY - 26.dp.toPx()
            val shoulderY = hipY - 28.dp.toPx()
            val headCenter = Offset(cx, shoulderY - headRadius - 5.dp.toPx())

            // Cosmic Halo Ring behind head
            drawCircle(
                color = rankTier.primaryColor.copy(alpha = 0.85f),
                radius = headRadius * 1.55f,
                center = headCenter,
                style = Stroke(width = 2.4.dp.toPx())
            )

            // Torso
            drawLine(DoodleColors.InkBlack, Offset(cx, shoulderY), Offset(cx, hipY), strokeW, StrokeCap.Round)

            // Zen lotus / transcendent float legs
            val leftKnee = Offset(cx - 18.dp.toPx(), hipY + 9.dp.toPx())
            val rightKnee = Offset(cx + 18.dp.toPx(), hipY + 9.dp.toPx())
            val leftFoot = Offset(cx + 4.dp.toPx(), hipY + 15.dp.toPx())
            val rightFoot = Offset(cx - 4.dp.toPx(), hipY + 15.dp.toPx())

            drawLine(DoodleColors.InkBlack, Offset(cx, hipY), leftKnee, strokeW, StrokeCap.Round)
            drawLine(DoodleColors.InkBlack, leftKnee, leftFoot, strokeW, StrokeCap.Round)
            drawLine(DoodleColors.InkBlack, Offset(cx, hipY), rightKnee, strokeW, StrokeCap.Round)
            drawLine(DoodleColors.InkBlack, rightKnee, rightFoot, strokeW, StrokeCap.Round)

            // Omnipotent arms-crossed pose across the chest
            val leftElbow = Offset(cx - 16.dp.toPx(), shoulderY + 11.dp.toPx())
            val rightElbow = Offset(cx + 16.dp.toPx(), shoulderY + 11.dp.toPx())
            val rightShoulderCross = Offset(cx + 8.dp.toPx(), shoulderY + 9.dp.toPx())
            val leftShoulderCross = Offset(cx - 8.dp.toPx(), shoulderY + 9.dp.toPx())

            drawLine(DoodleColors.InkBlack, Offset(cx - 4.dp.toPx(), shoulderY + 2.dp.toPx()), leftElbow, strokeW, StrokeCap.Round)
            drawLine(DoodleColors.InkBlack, leftElbow, rightShoulderCross, strokeW, StrokeCap.Round)
            drawLine(DoodleColors.InkBlack, Offset(cx + 4.dp.toPx(), shoulderY + 2.dp.toPx()), rightElbow, strokeW, StrokeCap.Round)
            drawLine(DoodleColors.InkBlack, rightElbow, leftShoulderCross, strokeW, StrokeCap.Round)

            // Doodle Head
            drawCircle(color = DoodleColors.PaperOffWhite, radius = headRadius, center = headCenter)
            drawCircle(
                color = DoodleColors.InkBlack,
                radius = headRadius,
                center = headCenter,
                style = Stroke(width = strokeW)
            )

            // Radiant Glowing Cosmic Eyes with horizontal & vertical star flares
            val eyeY = headCenter.y - 0.5.dp.toPx()
            val leftEye = Offset(headCenter.x - 4.8.dp.toPx(), eyeY)
            val rightEye = Offset(headCenter.x + 4.8.dp.toPx(), eyeY)
            val flareLen = (7.dp.toPx() + sin(time * 18f) * 2.5.dp.toPx()) * surgeMultiplier.coerceAtMost(1.9f)

            listOf(leftEye, rightEye).forEach { eye ->
                drawCircle(
                    color = rankTier.primaryColor,
                    radius = 4.2.dp.toPx(),
                    center = eye
                )
                drawCircle(
                    color = Color.White,
                    radius = 2.2.dp.toPx(),
                    center = eye
                )
                drawLine(
                    color = rankTier.accentColor,
                    start = Offset(eye.x - flareLen, eye.y),
                    end = Offset(eye.x + flareLen, eye.y),
                    strokeWidth = 1.8.dp.toPx(),
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = rankTier.primaryColor,
                    start = Offset(eye.x, eye.y - flareLen * 0.7f),
                    end = Offset(eye.x, eye.y + flareLen * 0.7f),
                    strokeWidth = 1.5.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

/**
 * Renders the Stickman's synchronized 20-Tier Aura on the Home Screen Canvas so it matches
 * the exact visual theme and color palette of the player's Tier Progress Card (Tiers 1 to 20).
 */
private fun DrawScope.drawStickmanTierAura(
    currentTier: Int,
    rankTier: RankTier,
    center: Offset,
    groundY: Float,
    time: Float,
    phase: Float,
    pulse: Float,
    surgeMultiplier: Float
) {
    val w = size.width
    val h = size.height
    val scale = pulse * (1f + (surgeMultiplier - 1f) * 0.28f)
    val baseRadius = min(w, h) * 0.34f * scale

    when (currentTier) {
        1 -> {
            // Tier 1 (Lv 1–50): Soft pale-yellow hatching lines & subtle golden ki pulse
            drawCircle(
                color = rankTier.primaryColor.copy(alpha = 0.34f),
                radius = baseRadius * 1.1f,
                center = center
            )
            val hatchCount = 12
            for (i in -hatchCount..hatchCount) {
                val offset = i * 9.dp.toPx() + sin(time * 3f + i) * 3.dp.toPx()
                drawLine(
                    color = rankTier.accentColor.copy(alpha = 0.48f),
                    start = Offset(center.x + offset - 28.dp.toPx(), center.y + 48.dp.toPx()),
                    end = Offset(center.x + offset + 28.dp.toPx(), center.y - 48.dp.toPx()),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
            for (s in 0..4) {
                val ang = phase + s * 1.25f
                val r = baseRadius * (0.65f + (s % 2) * 0.25f)
                drawDoodleSparkleStar(
                    center = Offset(center.x + cos(ang) * r, center.y + sin(ang) * r),
                    radius = 5.dp.toPx() * pulse,
                    color = rankTier.accentColor,
                    rotationRad = ang
                )
            }
        }

        2 -> {
            // Tier 2 (Lv 51–100): Gentle flowing blue ink-splash rings
            for (i in 0..4) {
                val ringProg = ((time * 0.55f + i * 0.2f) % 1f)
                val r = baseRadius * (0.35f + ringProg * 0.95f)
                val alpha = (1f - ringProg).coerceIn(0.15f, 0.75f)
                drawCircle(
                    color = rankTier.primaryColor.copy(alpha = alpha),
                    radius = r,
                    center = center,
                    style = Stroke(width = 3.dp.toPx())
                )
                val dropAng = phase * 1.6f + i * 1.3f
                drawCircle(
                    color = rankTier.accentColor.copy(alpha = 0.78f),
                    radius = 4.2.dp.toPx() * pulse,
                    center = Offset(center.x + cos(dropAng) * r, center.y + sin(dropAng) * r)
                )
            }
        }

        3 -> {
            // Tier 3 (Lv 101–150): Rotating green sketchy cross-hatch starburst rays
            val rayCount = 18
            for (i in 0 until rayCount) {
                val ang = phase * 1.2f + (i * 2.0 * PI / rayCount).toFloat()
                val innerR = baseRadius * 0.28f
                val outerR = baseRadius * (if (i % 2 == 0) 1.22f else 0.86f)
                drawLine(
                    color = if (i % 2 == 0) rankTier.accentColor.copy(alpha = 0.65f) else rankTier.primaryColor.copy(alpha = 0.75f),
                    start = Offset(center.x + cos(ang) * innerR, center.y + sin(ang) * innerR),
                    end = Offset(center.x + cos(ang) * outerR, center.y + sin(ang) * outerR),
                    strokeWidth = 2.4.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }

        4 -> {
            // Tier 4 (Lv 151–200): Shimmering purple geometric coordinate grid pulse
            val gridHalf = baseRadius * 1.15f
            val step = 16.dp.toPx()
            val shift = (time * 18f) % step
            var d = -gridHalf + shift
            while (d <= gridHalf) {
                drawLine(
                    color = rankTier.primaryColor.copy(alpha = 0.50f),
                    start = Offset(center.x + d, center.y - gridHalf),
                    end = Offset(center.x + d, center.y + gridHalf),
                    strokeWidth = 1.6.dp.toPx()
                )
                drawLine(
                    color = rankTier.accentColor.copy(alpha = 0.42f),
                    start = Offset(center.x - gridHalf, center.y + d),
                    end = Offset(center.x + gridHalf, center.y + d),
                    strokeWidth = 1.6.dp.toPx()
                )
                d += step
            }
            for (k in 0..5) {
                val a = phase + k * 1.05f
                drawDoodleSparkleStar(
                    center = Offset(center.x + cos(a) * baseRadius * 0.85f, center.y + sin(a) * baseRadius * 0.85f),
                    radius = 5.5.dp.toPx() * pulse,
                    color = rankTier.accentColor,
                    rotationRad = -a
                )
            }
        }

        5 -> {
            // Tier 5 (Lv 201–250): Radiant golden flame aura with floating doodle sparks
            val flameCount = 12
            for (i in 0 until flameCount) {
                val baseX = center.x + (i - flameCount / 2f + 0.5f) * 14.dp.toPx()
                val flameH = (32.dp.toPx() + abs(sin(time * 4f + i * 0.8f)) * 42.dp.toPx()) * scale
                val tipX = baseX + sin(time * 5f + i) * 7.dp.toPx()
                val path = Path().apply {
                    moveTo(baseX - 9.dp.toPx(), groundY)
                    quadraticTo(baseX - 4.dp.toPx(), groundY - flameH * 0.6f, tipX, groundY - flameH)
                    quadraticTo(baseX + 4.dp.toPx(), groundY - flameH * 0.6f, baseX + 9.dp.toPx(), groundY)
                    close()
                }
                drawPath(
                    path = path,
                    color = if (i % 2 == 0) rankTier.primaryColor.copy(alpha = 0.52f) else rankTier.accentColor.copy(alpha = 0.42f)
                )
            }
            for (s in 0..9) {
                val lift = ((time * 0.85f + s * 0.11f) % 1f)
                val sx = center.x + sin(time * 2.5f + s * 1.7f) * (baseRadius * 0.95f)
                val sy = groundY - lift * (baseRadius * 2.0f)
                drawCircle(
                    color = if (s % 2 == 0) Color(0xFFFFEA00) else Color(0xFFFF6D00),
                    radius = 3.5.dp.toPx() * (1f - lift * 0.4f),
                    center = Offset(sx, sy)
                )
            }
        }

        6 -> {
            // Tier 6 (Lv 251–300): Multi-colored rainbow pulsating aura with crackling lightning doodles
            val rainbow = listOf(
                Color(0xFFFF1744),
                Color(0xFFFF9100),
                Color(0xFFFFEA00),
                Color(0xFF00E676),
                Color(0xFF2979FF),
                Color(0xFFD500F9)
            )
            rainbow.forEachIndexed { idx, col ->
                val r = baseRadius * (0.48f + idx * 0.12f)
                drawCircle(
                    color = col.copy(alpha = 0.52f),
                    radius = r,
                    center = center,
                    style = Stroke(width = 3.2.dp.toPx())
                )
            }
            // Crackling lightning bolts
            for (b in 0..5) {
                val ang = phase * 2f + b * (PI / 3.0).toFloat()
                val r1 = baseRadius * 0.35f
                val r2 = baseRadius * 0.75f
                val r3 = baseRadius * 1.18f
                val bolt = Path().apply {
                    moveTo(center.x + cos(ang) * r1, center.y + sin(ang) * r1)
                    lineTo(center.x + cos(ang + 0.18f) * r2, center.y + sin(ang + 0.18f) * r2)
                    lineTo(center.x + cos(ang - 0.12f) * r3, center.y + sin(ang - 0.12f) * r3)
                }
                drawPath(
                    path = bolt,
                    color = Color(0xFFFFEA00),
                    style = Stroke(width = 2.4.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }

        7 -> {
            // Tier 7 (Lv 301–350): Matrix green code-doodle grid raining down
            val cols = 10
            for (c in 0 until cols) {
                val colX = center.x + (c - cols / 2f + 0.5f) * 18.dp.toPx()
                val dropProg = ((time * (0.75f + (c % 3) * 0.25f) + c * 0.14f) % 1f)
                val headY = (center.y - baseRadius * 1.15f) + dropProg * (baseRadius * 2.2f)
                val tailTop = (headY - 28.dp.toPx()).coerceAtLeast(center.y - baseRadius * 1.2f)
                drawLine(
                    color = rankTier.accentColor.copy(alpha = 0.72f),
                    start = Offset(colX, tailTop),
                    end = Offset(colX, headY),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
                drawCircle(
                    color = Color.White,
                    radius = 2.8.dp.toPx(),
                    center = Offset(colX, headY)
                )
            }
        }

        8 -> {
            // Tier 8 (Lv 351–400): Orbiting mini planet doodles and twinkling star crosshairs
            drawCircle(
                color = rankTier.primaryColor.copy(alpha = 0.45f),
                radius = baseRadius * 0.95f,
                center = center,
                style = Stroke(width = 1.8.dp.toPx())
            )
            for (p in 0..4) {
                val ang = phase * (1.1f + p * 0.15f) + p * 1.25f
                val orbitR = baseRadius * (0.55f + (p % 2) * 0.42f)
                val px = center.x + cos(ang) * orbitR
                val py = center.y + sin(ang) * (orbitR * 0.68f)
                val pRad = (5.dp.toPx() + (p % 2) * 2.2.dp.toPx()) * pulse
                drawCircle(color = rankTier.primaryColor, radius = pRad, center = Offset(px, py))
                drawCircle(color = DoodleColors.InkBlack, radius = pRad, center = Offset(px, py), style = Stroke(width = 1.4.dp.toPx()))
                // Planet ring
                drawOval(
                    color = rankTier.accentColor,
                    topLeft = Offset(px - pRad * 1.7f, py - pRad * 0.5f),
                    size = Size(pRad * 3.4f, pRad),
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }
        }

        9 -> {
            // Tier 9 (Lv 401–450): Pulsing sketchy ink atomic electron rings
            for (ring in 0..2) {
                val tilt = ring * 60f + time * 25f
                rotate(degrees = tilt, pivot = center) {
                    val rx = baseRadius * 1.15f
                    val ry = baseRadius * 0.42f
                    drawOval(
                        color = rankTier.accentColor.copy(alpha = 0.68f),
                        topLeft = Offset(center.x - rx, center.y - ry),
                        size = Size(rx * 2f, ry * 2f),
                        style = Stroke(width = 2.2.dp.toPx())
                    )
                    val eAng = phase * 2.2f + ring * 2f
                    val ex = center.x + cos(eAng) * rx
                    val ey = center.y + sin(eAng) * ry
                    drawCircle(color = rankTier.primaryColor, radius = 5.dp.toPx(), center = Offset(ex, ey))
                    drawCircle(color = DoodleColors.InkBlack, radius = 5.dp.toPx(), center = Offset(ex, ey), style = Stroke(width = 1.4.dp.toPx()))
                }
            }
        }

        10 -> {
            // Tier 10 (Lv 451–500): Shimmering spiral galaxy sketch rotating infinitely
            val arms = 3
            for (arm in 0 until arms) {
                val path = Path()
                val steps = 28
                for (s in 0..steps) {
                    val frac = s.toFloat() / steps
                    val r = frac * baseRadius * 1.2f
                    val ang = phase * 1.5f + arm * (2.0 * PI / arms).toFloat() + frac * 4.2f
                    val px = center.x + cos(ang) * r
                    val py = center.y + sin(ang) * r * 0.78f
                    if (s == 0) path.moveTo(px, py) else path.lineTo(px, py)
                }
                drawPath(
                    path = path,
                    color = rankTier.accentColor.copy(alpha = 0.65f),
                    style = Stroke(width = 2.6.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }

        11 -> {
            // Tier 11 (Lv 501–550): Glowing constellation connecting-line star maps
            val pts = (0..7).map { idx ->
                val ang = phase * 0.55f + idx * (2.0 * PI / 8.0).toFloat()
                val r = baseRadius * (if (idx % 2 == 0) 1.08f else 0.72f)
                Offset(center.x + cos(ang) * r, center.y + sin(ang) * r)
            }
            for (i in pts.indices) {
                val next = pts[(i + 1) % pts.size]
                drawLine(
                    color = rankTier.accentColor.copy(alpha = 0.58f),
                    start = pts[i],
                    end = next,
                    strokeWidth = 1.8.dp.toPx()
                )
                drawDoodleSparkleStar(
                    center = pts[i],
                    radius = 5.5.dp.toPx() * pulse,
                    color = rankTier.primaryColor,
                    rotationRad = phase + i
                )
            }
        }

        12 -> {
            // Tier 12 (Lv 551–600): Swirling purple and magenta cosmic dust clouds
            for (cloud in 0..7) {
                val ang = phase * 0.9f + cloud * (2.0 * PI / 8.0).toFloat()
                val dist = baseRadius * (0.55f + 0.35f * sin(time * 2f + cloud))
                val cxCloud = center.x + cos(ang) * dist
                val cyCloud = center.y + sin(ang) * dist * 0.8f
                drawCircle(
                    color = if (cloud % 2 == 0) rankTier.primaryColor.copy(alpha = 0.42f) else rankTier.accentColor.copy(alpha = 0.35f),
                    radius = 20.dp.toPx() * pulse,
                    center = Offset(cxCloud, cyCloud)
                )
                drawCircle(
                    color = rankTier.accentColor.copy(alpha = 0.6f),
                    radius = 20.dp.toPx() * pulse,
                    center = Offset(cxCloud, cyCloud),
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }
        }

        13 -> {
            // Tier 13 (Lv 601–650): Spiraling blackhole doodle ring pulling ink particles inward
            drawCircle(
                color = rankTier.accentColor.copy(alpha = 0.75f),
                radius = baseRadius * 0.52f,
                center = center,
                style = Stroke(width = 4.dp.toPx())
            )
            for (p in 0..11) {
                val inwardProg = 1f - ((time * 0.75f + p * 0.083f) % 1f)
                val r = baseRadius * (0.25f + inwardProg * 0.95f)
                val ang = phase * 2.4f + p * 0.6f + (1f - inwardProg) * 3.5f
                val px = center.x + cos(ang) * r
                val py = center.y + sin(ang) * r
                drawCircle(
                    color = DoodleColors.InkBlack.copy(alpha = 0.75f),
                    radius = 3.2.dp.toPx() * (0.5f + inwardProg * 0.7f),
                    center = Offset(px, py)
                )
            }
        }

        14 -> {
            // Tier 14 (Lv 651–700): Exploding orange and yellow radiant starburst shockwaves
            val spikes = 20
            val burstPath = Path()
            for (i in 0 until spikes) {
                val ang = phase * 1.4f + (i * 2.0 * PI / spikes).toFloat()
                val r = baseRadius * (if (i % 2 == 0) 1.25f else 0.62f)
                val px = center.x + cos(ang) * r
                val py = center.y + sin(ang) * r
                if (i == 0) burstPath.moveTo(px, py) else burstPath.lineTo(px, py)
            }
            burstPath.close()
            drawPath(path = burstPath, color = rankTier.primaryColor.copy(alpha = 0.38f))
            drawPath(
                path = burstPath,
                color = rankTier.accentColor.copy(alpha = 0.82f),
                style = Stroke(width = 2.4.dp.toPx(), join = StrokeJoin.Round)
            )
        }

        15 -> {
            // Tier 15 (Lv 701–750): Ticking clock gear teeth and swinging pendulum sketches
            val gearR = baseRadius * 0.92f
            drawCircle(
                color = rankTier.accentColor.copy(alpha = 0.68f),
                radius = gearR,
                center = center,
                style = Stroke(width = 3.dp.toPx())
            )
            val teeth = 12
            val discreteStep = ((time * 4f).toInt() * (PI / 12.0)).toFloat()
            for (t in 0 until teeth) {
                val ang = discreteStep + (t * 2.0 * PI / teeth).toFloat()
                drawLine(
                    color = rankTier.accentColor,
                    start = Offset(center.x + cos(ang) * (gearR - 4.dp.toPx()), center.y + sin(ang) * (gearR - 4.dp.toPx())),
                    end = Offset(center.x + cos(ang) * (gearR + 9.dp.toPx()), center.y + sin(ang) * (gearR + 9.dp.toPx())),
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Square
                )
            }
            // Swinging pendulum sketch
            val pendAng = (PI / 2.0 + sin(time * 3.5f) * 0.48f).toFloat()
            val bobCenter = Offset(center.x + cos(pendAng) * gearR * 0.85f, center.y + sin(pendAng) * gearR * 0.85f)
            drawLine(DoodleColors.InkBlack, center, bobCenter, strokeWidth = 2.dp.toPx())
            drawCircle(rankTier.primaryColor, radius = 7.dp.toPx(), center = bobCenter)
            drawCircle(DoodleColors.InkBlack, radius = 7.dp.toPx(), center = bobCenter, style = Stroke(width = 1.6.dp.toPx()))
        }

        16 -> {
            // Tier 16 (Lv 751–800): Shimmering ghostly blue ethereal flame wisps
            for (wisp in 0..7) {
                val ang = phase + wisp * (2.0 * PI / 8.0).toFloat()
                val orbitR = baseRadius * 0.82f
                val wx = center.x + cos(ang) * orbitR
                val wy = center.y + sin(ang * 1.5f) * (orbitR * 0.7f)
                val wispPath = Path().apply {
                    moveTo(wx - 6.dp.toPx(), wy + 8.dp.toPx())
                    quadraticTo(wx - 8.dp.toPx(), wy - 6.dp.toPx(), wx + sin(time * 6f + wisp) * 4.dp.toPx(), wy - 16.dp.toPx())
                    quadraticTo(wx + 8.dp.toPx(), wy - 6.dp.toPx(), wx + 6.dp.toPx(), wy + 8.dp.toPx())
                    close()
                }
                drawPath(path = wispPath, color = rankTier.primaryColor.copy(alpha = 0.68f))
                drawPath(path = wispPath, color = rankTier.accentColor, style = Stroke(width = 1.5.dp.toPx()))
            }
        }

        17 -> {
            // Tier 17 (Lv 801–850): Heavy angular metallic crosshatch sparks and levitating iron shards
            for (s in 0..7) {
                val ang = phase * 1.4f + s * (2.0 * PI / 8.0).toFloat()
                val r = baseRadius * (0.68f + (s % 2) * 0.32f)
                val sx = center.x + cos(ang) * r
                val sy = center.y + sin(ang) * r
                val shard = Path().apply {
                    moveTo(sx, sy - 8.dp.toPx())
                    lineTo(sx + 6.dp.toPx(), sy + 4.dp.toPx())
                    lineTo(sx - 5.dp.toPx(), sy + 7.dp.toPx())
                    close()
                }
                drawPath(path = shard, color = rankTier.primaryColor)
                drawPath(path = shard, color = rankTier.accentColor, style = Stroke(width = 2.dp.toPx()))
            }
        }

        18 -> {
            // Tier 18 (Lv 851–900): Floating golden ancient runes and mystical sigil doodles
            val sigilR = baseRadius * 0.98f
            drawCircle(
                color = rankTier.accentColor.copy(alpha = 0.72f),
                radius = sigilR,
                center = center,
                style = Stroke(width = 2.2.dp.toPx())
            )
            for (r in 0..5) {
                val ang = phase * 0.9f + r * (PI / 3.0).toFloat()
                val rx = center.x + cos(ang) * sigilR
                val ry = center.y + sin(ang) * sigilR
                drawCircle(color = rankTier.primaryColor, radius = 8.dp.toPx(), center = Offset(rx, ry))
                drawCircle(color = DoodleColors.InkBlack, radius = 8.dp.toPx(), center = Offset(rx, ry), style = Stroke(width = 1.5.dp.toPx()))
                // Rune cross-stroke inside sigil node
                drawLine(
                    color = rankTier.accentColor,
                    start = Offset(rx - 4.dp.toPx(), ry - 4.dp.toPx()),
                    end = Offset(rx + 4.dp.toPx(), ry + 4.dp.toPx()),
                    strokeWidth = 1.8.dp.toPx()
                )
                drawLine(
                    color = rankTier.accentColor,
                    start = Offset(rx, ry - 5.dp.toPx()),
                    end = Offset(rx, ry + 5.dp.toPx()),
                    strokeWidth = 1.8.dp.toPx()
                )
            }
        }

        19 -> {
            // Tier 19 (Lv 901–950): Glowing third-eye doodle above the head with floating parchment scrolls
            val eyeCenter = Offset(center.x, center.y - baseRadius * 0.92f)
            val eyeW = 28.dp.toPx() * pulse
            val eyeH = 13.dp.toPx() * pulse
            val eyePath = Path().apply {
                moveTo(eyeCenter.x - eyeW, eyeCenter.y)
                quadraticTo(eyeCenter.x, eyeCenter.y - eyeH, eyeCenter.x + eyeW, eyeCenter.y)
                quadraticTo(eyeCenter.x, eyeCenter.y + eyeH, eyeCenter.x - eyeW, eyeCenter.y)
                close()
            }
            drawPath(path = eyePath, color = rankTier.primaryColor)
            drawPath(path = eyePath, color = DoodleColors.InkBlack, style = Stroke(width = 2.dp.toPx()))
            drawCircle(color = rankTier.accentColor, radius = 5.5.dp.toPx(), center = eyeCenter)
            drawCircle(color = DoodleColors.InkBlack, radius = 2.5.dp.toPx(), center = eyeCenter)

            // Floating parchment scrolls around stickman
            for (sc in 0..3) {
                val ang = phase * 0.8f + sc * (PI / 2.0).toFloat()
                val sx = center.x + cos(ang) * baseRadius * 0.88f
                val sy = center.y + sin(ang) * baseRadius * 0.55f
                drawRoundRect(
                    color = Color(0xFFFFF8E1),
                    topLeft = Offset(sx - 9.dp.toPx(), sy - 6.dp.toPx()),
                    size = Size(18.dp.toPx(), 12.dp.toPx()),
                    cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
                )
                drawRoundRect(
                    color = DoodleColors.InkBlack,
                    topLeft = Offset(sx - 9.dp.toPx(), sy - 6.dp.toPx()),
                    size = Size(18.dp.toPx(), 12.dp.toPx()),
                    cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx()),
                    style = Stroke(width = 1.4.dp.toPx())
                )
            }
        }

        else -> {
            // Tier 20 (Lv 951–1,000): Ultimate pulsating gold-and-rainbow cosmos aura with shooting stars and divine doodle light rays
            val divineRays = 16
            for (i in 0 until divineRays) {
                val ang = phase * 1.1f + (i * 2.0 * PI / divineRays).toFloat()
                val rOuter = baseRadius * (if (i % 2 == 0) 1.38f else 0.95f)
                drawLine(
                    color = if (i % 2 == 0) Color(0xFFFFD700).copy(alpha = 0.78f) else Color(0xFFD500F9).copy(alpha = 0.62f),
                    start = center,
                    end = Offset(center.x + cos(ang) * rOuter, center.y + sin(ang) * rOuter),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
            val rainbow = listOf(
                Color(0xFFFF1744),
                Color(0xFFFF9100),
                Color(0xFFFFEA00),
                Color(0xFF00E676),
                Color(0xFF2979FF),
                Color(0xFFD500F9)
            )
            rainbow.forEachIndexed { idx, col ->
                drawCircle(
                    color = col.copy(alpha = 0.55f),
                    radius = baseRadius * (0.45f + idx * 0.13f),
                    center = center,
                    style = Stroke(width = 2.6.dp.toPx())
                )
            }
            // Shooting stars streaking across the divine cosmos aura
            for (st in 0..4) {
                val ang = phase * 2.2f + st * 1.25f
                val r = baseRadius * (0.72f + (st % 2) * 0.35f)
                val starPos = Offset(center.x + cos(ang) * r, center.y + sin(ang) * r)
                val tailPos = Offset(center.x + cos(ang - 0.35f) * (r * 0.85f), center.y + sin(ang - 0.35f) * (r * 0.85f))
                drawLine(
                    color = Color.White.copy(alpha = 0.85f),
                    start = tailPos,
                    end = starPos,
                    strokeWidth = 2.4.dp.toPx(),
                    cap = StrokeCap.Round
                )
                drawDoodleSparkleStar(
                    center = starPos,
                    radius = 6.5.dp.toPx() * pulse,
                    color = Color(0xFFFFD700),
                    rotationRad = ang
                )
            }
        }
    }
}

@Composable
fun TierProgressionCard(
    rankTier: RankTier,
    levelNumber: Int,
    completedLevels: Int,
    totalLevels: Int,
    fontFamily: FontFamily,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val safeTotal = totalLevels.coerceAtLeast(1)
    val clampedCompleted = completedLevels.coerceIn(0, safeTotal)

    val infiniteTransition = rememberInfiniteTransition(label = "tierAuraInfiniteTransition")
    val auraPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "auraPhase"
    )
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.90f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 850, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "auraPulse"
    )

    val targetTierFraction = rankTier.tierProgressFraction(levelNumber)
    val animatedTierFraction by animateFloatAsState(
        targetValue = targetTierFraction,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "tierProgressFraction"
    )

    SketchyContainer(
        fillColor = DoodleColors.PaperOffWhite,
        borderColor = DoodleColors.InkBlack,
        cornerRadius = 18.dp,
        strokeWidth = 2.2.dp,
        shadowOffset = 3.dp,
        onClick = onClick,
        modifier = modifier
            .testTag("tier_progression_card")
            .semantics {
                contentDescription = "${rankTier.displayTierTitle}. ${rankTier.progressSubLabel(levelNumber)}. Campaign completed: $clampedCompleted of $safeTotal"
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .testTag("campaign_progress_indicator")
        ) {
            // Vivid 60fps Animated Canvas Aura Effect (Multi-layer Glow + Sweeping Shimmer + Sparkles + Border Halo)
            Canvas(
                modifier = Modifier
                    .matchParentSize()
                    .testTag("rank_aura_canvas")
            ) {
                drawRankTierAura(
                    rankTier = rankTier,
                    phase = auraPhase,
                    pulse = pulse
                )

                // Outer & inner glowing neon/ink aura border rings inside the 2.dp black sketch frame
                val borderRadius = 15.dp.toPx()
                drawRoundRect(
                    color = rankTier.primaryColor.copy(alpha = (0.55f * pulse).coerceIn(0.25f, 0.78f)),
                    topLeft = Offset(2.dp.toPx(), 2.dp.toPx()),
                    size = Size(size.width - 4.dp.toPx(), size.height - 4.dp.toPx()),
                    cornerRadius = CornerRadius(borderRadius, borderRadius),
                    style = Stroke(width = (3.4.dp.toPx() * pulse))
                )
                drawRoundRect(
                    color = rankTier.accentColor.copy(alpha = (0.45f * pulse).coerceIn(0.20f, 0.70f)),
                    topLeft = Offset(4.dp.toPx(), 4.dp.toPx()),
                    size = Size(size.width - 8.dp.toPx(), size.height - 8.dp.toPx()),
                    cornerRadius = CornerRadius(borderRadius - 2.dp.toPx(), borderRadius - 2.dp.toPx()),
                    style = Stroke(width = 1.6.dp.toPx())
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Row: Active Tier Title + Sub-label on Left, Animated Emblem + Total Solved Pill on Right
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = rankTier.displayTierTitle,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = DoodleColors.InkBlack,
                            modifier = Modifier.testTag("rank_title_text")
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = rankTier.progressSubLabel(levelNumber),
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = DoodleColors.InkBlue,
                            modifier = Modifier.testTag("tier_progress_subtext")
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Animated Rank Emblem Medallion with rotating aura rays
                        Canvas(
                            modifier = Modifier
                                .size(32.dp)
                                .scale(pulse)
                        ) {
                            val r = size.minDimension * 0.48f
                            drawCircle(
                                color = rankTier.primaryColor.copy(alpha = 0.75f),
                                radius = r
                            )
                            for (k in 0 until 8) {
                                val a = auraPhase + (k * PI / 4.0).toFloat()
                                drawLine(
                                    color = rankTier.secondaryColor.copy(alpha = 0.75f),
                                    start = Offset(center.x + cos(a) * r * 0.65f, center.y + sin(a) * r * 0.65f),
                                    end = Offset(center.x + cos(a) * r * 1.08f, center.y + sin(a) * r * 1.08f),
                                    strokeWidth = 1.6.dp.toPx(),
                                    cap = StrokeCap.Round
                                )
                            }
                            drawCircle(
                                color = rankTier.accentColor,
                                radius = size.minDimension * 0.44f,
                                style = Stroke(width = 2.dp.toPx())
                            )
                            drawDoodleMiniStar(filled = true)
                        }
                        Text(
                            text = "$clampedCompleted/$safeTotal ★",
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Black,
                            fontSize = 9.sp,
                            color = DoodleColors.InkBlack.copy(alpha = 0.80f),
                            modifier = Modifier.testTag("campaign_progress_text")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(7.dp))

                // Hand-Sketched Tier Progress Bar with Animated Aura Hatch Fill
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(15.dp)
                        .testTag("tier_progress_bar")
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val r = size.height * 0.5f
                        val sw = 2.dp.toPx()

                        // 1. Off-white graph-paper track background
                        drawRoundRect(
                            color = DoodleColors.PaperOffWhite,
                            cornerRadius = CornerRadius(r, r)
                        )

                        // 2. Dynamic Tier Progress Fill with animated diagonal sketch hatching
                        if (animatedTierFraction > 0f) {
                            val fillW = (size.width * animatedTierFraction)
                                .coerceAtLeast(size.height)
                                .coerceAtMost(size.width)

                            drawRoundRect(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        rankTier.primaryColor,
                                        rankTier.secondaryColor,
                                        rankTier.primaryColor
                                    )
                                ),
                                size = Size(fillW, size.height),
                                cornerRadius = CornerRadius(r, r)
                            )

                            // Animated diagonal hand-drawn hatch lines clipped to the fill bar
                            val hatchStep = 8.dp.toPx()
                            val hatchOffset = (auraPhase / (2f * PI).toFloat()) * hatchStep
                            var hx = -size.height + hatchOffset
                            while (hx < fillW) {
                                val startX = hx.coerceIn(4f, fillW - 4f)
                                val endX = (hx + size.height * 0.65f).coerceIn(4f, fillW - 4f)
                                if (endX > startX) {
                                    drawLine(
                                        color = rankTier.accentColor.copy(alpha = 0.62f),
                                        start = Offset(startX, size.height - 2.dp.toPx()),
                                        end = Offset(endX, 2.dp.toPx()),
                                        strokeWidth = 1.7.dp.toPx(),
                                        cap = StrokeCap.Round
                                    )
                                }
                                hx += hatchStep
                            }

                            // Glowing pulse highlight at the leading tip of the progress bar
                            val tipX = (fillW - r).coerceAtLeast(r)
                            drawCircle(
                                color = Color.White.copy(alpha = 0.88f),
                                radius = (r * 0.52f) * pulse,
                                center = Offset(tipX, size.height * 0.5f)
                            )
                        }

                        // 3. Hand-drawn 2.dp uneven black stroke around the progress bar
                        drawRoundRect(
                            color = DoodleColors.InkBlack,
                            cornerRadius = CornerRadius(r, r),
                            style = Stroke(width = sw, cap = StrokeCap.Round)
                        )
                        drawRoundRect(
                            color = DoodleColors.InkBlack.copy(alpha = 0.65f),
                            topLeft = Offset(1.dp.toPx(), 0.6.dp.toPx()),
                            size = Size(size.width - 2.dp.toPx(), size.height - 1.2.dp.toPx()),
                            cornerRadius = CornerRadius(r * 0.95f, r * 1.05f),
                            style = Stroke(width = sw * 0.5f, cap = StrokeCap.Round)
                        )
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawDoodleSparkleStar(
    center: Offset,
    radius: Float,
    color: Color,
    rotationRad: Float = 0f
) {
    val path = Path()
    for (i in 0 until 8) {
        val r = if (i % 2 == 0) radius else radius * 0.34f
        val angle = rotationRad + (i * PI / 4.0).toFloat()
        val px = center.x + cos(angle) * r
        val py = center.y + sin(angle) * r
        if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
    }
    path.close()
    drawPath(path = path, color = color)
    drawPath(
        path = path,
        color = DoodleColors.InkBlack.copy(alpha = 0.55f),
        style = Stroke(width = 1.dp.toPx())
    )
}

private fun DrawScope.drawRankTierAura(
    rankTier: RankTier,
    phase: Float,
    pulse: Float
) {
    val w = size.width
    val h = size.height
    val cx = w * 0.5f
    val cy = h * 0.5f

    // 1. Vivid multi-stop animated gradient background wash so the Tier Aura immediately pops
    val shiftX = sin(phase) * (w * 0.25f)
    drawRect(
        brush = Brush.linearGradient(
            colors = listOf(
                rankTier.primaryColor.copy(alpha = 0.42f),
                rankTier.secondaryColor.copy(alpha = 0.28f),
                rankTier.primaryColor.copy(alpha = 0.46f)
            ),
            start = Offset(shiftX, 0f),
            end = Offset(w + shiftX, h)
        )
    )

    // 2. Pulsing radial energy core behind the card
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                rankTier.primaryColor.copy(alpha = 0.48f * pulse),
                rankTier.secondaryColor.copy(alpha = 0.22f * pulse),
                Color.Transparent
            ),
            center = Offset(cx + cos(phase) * (w * 0.18f), cy),
            radius = (w * 0.55f) * pulse
        ),
        center = Offset(cx + cos(phase) * (w * 0.18f), cy),
        radius = (w * 0.55f) * pulse
    )

    // 3. Tier-specific high-contrast hand-drawn animations across all 20 tiers
    when (rankTier.tierIndex) {
        1 -> {
            // Tier 1 (Doodle Novice): Vivid golden-yellow cross-hatch lines + rising doodle stars
            val step = 13.dp.toPx()
            val flow = (phase / (2f * PI).toFloat()) * step
            var x = -h + flow
            while (x < w + h) {
                val wobble = sin(phase * 2f + x * 0.04f) * 3.dp.toPx()
                drawLine(
                    color = rankTier.secondaryColor.copy(alpha = 0.62f),
                    start = Offset(x + wobble, h),
                    end = Offset(x + h * 0.75f + wobble, 0f),
                    strokeWidth = 2.4.dp.toPx(),
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = rankTier.accentColor.copy(alpha = 0.30f),
                    start = Offset(x - wobble, 0f),
                    end = Offset(x + h * 0.55f - wobble, h),
                    strokeWidth = 1.4.dp.toPx(),
                    cap = StrokeCap.Round
                )
                x += step
            }
            for (i in 0..5) {
                val sx = w * (0.10f + i * 0.16f) + sin(phase * 2f + i) * 5.dp.toPx()
                val sy = h - ((phase * 18f + i * 15f) % h)
                drawDoodleSparkleStar(
                    center = Offset(sx, sy),
                    radius = (4.5.dp.toPx() + (i % 2) * 1.5.dp.toPx()) * pulse,
                    color = rankTier.primaryColor,
                    rotationRad = phase + i
                )
            }
        }
        2 -> {
            // Tier 2 (Ink Apprentice): Flowing cobalt & cyan ink-splash rings + orbiting droplets
            for (i in 0..5) {
                val progress = ((phase / (2f * PI).toFloat()) + i * 0.18f) % 1f
                val r = (8.dp.toPx() + progress * 34.dp.toPx()) * pulse
                val ringX = w * (0.12f + i * 0.15f) + cos(phase + i) * 6.dp.toPx()
                val ringY = cy + sin(phase * 1.5f + i) * 6.dp.toPx()
                val alpha = (1f - progress * 0.65f).coerceIn(0.22f, 0.78f)
                drawCircle(
                    color = rankTier.secondaryColor.copy(alpha = alpha),
                    radius = r,
                    center = Offset(ringX, ringY),
                    style = Stroke(width = 2.5.dp.toPx())
                )
                val dropAngle = phase * 2f + i * 1.1f
                drawCircle(
                    color = rankTier.accentColor.copy(alpha = 0.70f),
                    radius = 3.6.dp.toPx() * pulse,
                    center = Offset(ringX + cos(dropAngle) * (r + 4.dp.toPx()), ringY + sin(dropAngle) * (r + 4.dp.toPx()))
                )
            }
        }
        3 -> {
            // Tier 3 (Sketch Master): Twin counter-rotating emerald starburst rays
            val rayCount = 16
            val origins = listOf(Offset(w * 0.20f, cy), Offset(w * 0.80f, cy))
            origins.forEachIndexed { idx, origin ->
                val dir = if (idx == 0) 1.4f else -1.4f
                for (i in 0 until rayCount) {
                    val angle = phase * dir + (i * 2.0 * PI / rayCount).toFloat()
                    val rOuter = (if (i % 2 == 0) 36.dp.toPx() else 22.dp.toPx()) * pulse
                    drawLine(
                        color = if (i % 2 == 0) rankTier.secondaryColor.copy(alpha = 0.65f) else rankTier.accentColor.copy(alpha = 0.45f),
                        start = origin,
                        end = Offset(origin.x + cos(angle) * rOuter, origin.y + sin(angle) * rOuter),
                        strokeWidth = 2.2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
                drawDoodleSparkleStar(
                    center = origin,
                    radius = 7.dp.toPx() * pulse,
                    color = rankTier.primaryColor,
                    rotationRad = -phase * dir
                )
            }
        }
        4 -> {
            // Tier 4 (Graph Paper Wizard): Shimmering purple geometric coordinate grid + arcane nodes
            val step = 14.dp.toPx()
            val offset = (phase / (2f * PI).toFloat()) * step
            var gx = offset
            while (gx < w) {
                drawLine(
                    color = rankTier.secondaryColor.copy(alpha = 0.52f),
                    start = Offset(gx, 0f),
                    end = Offset(gx, h),
                    strokeWidth = 1.8.dp.toPx()
                )
                gx += step
            }
            var gy = offset
            while (gy < h) {
                drawLine(
                    color = rankTier.accentColor.copy(alpha = 0.42f),
                    start = Offset(0f, gy),
                    end = Offset(w, gy),
                    strokeWidth = 1.8.dp.toPx()
                )
                gy += step
            }
            for (i in 0..4) {
                val nx = w * (0.15f + i * 0.175f)
                val ny = cy + sin(phase * 2f + i) * (h * 0.25f)
                drawDoodleSparkleStar(
                    center = Offset(nx, ny),
                    radius = 6.dp.toPx() * pulse,
                    color = rankTier.secondaryColor,
                    rotationRad = phase * 1.5f + i
                )
            }
        }
        5 -> {
            // Tier 5 (Epic Doodle Legend): Roaring golden-orange flame tongues + rising embers
            val flameCount = 14
            for (i in 0 until flameCount) {
                val baseX = (w / flameCount) * (i + 0.5f)
                val flameH = (16.dp.toPx() + abs(sin(phase * 2.5f + i * 0.7f)) * 22.dp.toPx()) * pulse
                val tipX = baseX + sin(phase * 3f + i) * 6.dp.toPx()
                val flamePath = Path().apply {
                    moveTo(baseX - 9.dp.toPx(), h)
                    quadraticTo(baseX - 3.dp.toPx(), h - flameH * 0.6f, tipX, h - flameH)
                    quadraticTo(baseX + 3.dp.toPx(), h - flameH * 0.6f, baseX + 9.dp.toPx(), h)
                    close()
                }
                drawPath(
                    path = flamePath,
                    color = if (i % 2 == 0) rankTier.secondaryColor.copy(alpha = 0.55f) else rankTier.primaryColor.copy(alpha = 0.65f)
                )
            }
            for (i in 0..9) {
                val fx = w * (0.06f + i * 0.095f) + sin(phase * 2.5f + i) * 5.dp.toPx()
                val fy = h - ((phase * 22f + i * 13f) % h)
                drawCircle(
                    color = if (i % 2 == 0) Color(0xFFFFEA00) else Color(0xFFFF6D00),
                    radius = (3.8.dp.toPx() + (i % 3).dp.toPx()) * pulse,
                    center = Offset(fx, fy)
                )
            }
        }
        6, 20 -> {
            // Tier 6 (Mythic Ink God) & Tier 20 (Supreme Doodle God): Vivid 6-color rainbow waves + crackling lightning bolts
            val rainbow = listOf(
                Color(0xFFFF1744),
                Color(0xFFFF9100),
                Color(0xFFFFEA00),
                Color(0xFF00E676),
                Color(0xFF2979FF),
                Color(0xFFD500F9)
            )
            rainbow.forEachIndexed { idx, col ->
                val path = Path()
                val segments = 16
                for (s in 0..segments) {
                    val px = (w / segments) * s
                    val py = cy + sin(phase * 2f + idx * 0.55f + s * 0.45f) * (h * 0.36f)
                    if (s == 0) path.moveTo(px, py) else path.lineTo(px, py)
                }
                drawPath(
                    path = path,
                    color = col.copy(alpha = 0.55f),
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            // Crackling zig-zag lightning bolts
            for (b in 0..2) {
                val bx = w * (0.22f + b * 0.28f) + sin(phase * 3f + b) * 10.dp.toPx()
                val bolt = Path().apply {
                    moveTo(bx, 2.dp.toPx())
                    lineTo(bx - 7.dp.toPx(), h * 0.36f)
                    lineTo(bx + 5.dp.toPx(), h * 0.48f)
                    lineTo(bx - 5.dp.toPx(), h - 2.dp.toPx())
                }
                drawPath(
                    path = bolt,
                    color = Color(0xFFFFEA00).copy(alpha = 0.88f),
                    style = Stroke(width = 2.4.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }
        7 -> {
            // Tier 7 (Dimension Sketcher): Matrix Neon-Green Code Rain + Binary Grid
            for (i in 0..11) {
                val colX = w * (0.04f + i * 0.084f)
                val speed = 26f + (i % 4) * 7f
                val dropY = ((phase * speed + i * 19f) % (h + 18.dp.toPx()))
                val tailTop = (dropY - 22.dp.toPx()).coerceAtLeast(0f)
                drawLine(
                    color = rankTier.primaryColor.copy(alpha = 0.72f),
                    start = Offset(colX, tailTop),
                    end = Offset(colX, dropY.coerceAtMost(h)),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.9f),
                    radius = 2.6.dp.toPx() * pulse,
                    center = Offset(colX, dropY.coerceIn(0f, h))
                )
            }
        }
        8, 10, 11, 12 -> {
            // Tiers 8, 10, 11, 12: Cosmic / Galaxy / Constellation / Nebula orbiting ringed planets & star web
            for (i in 0..7) {
                val angle = phase * 1.3f + i * 0.82f
                val px = cx + cos(angle) * (w * 0.40f)
                val py = cy + sin(angle * 2f) * (h * 0.35f)
                if (i > 0) {
                    val prevAngle = phase * 1.3f + (i - 1) * 0.82f
                    val ppx = cx + cos(prevAngle) * (w * 0.40f)
                    val ppy = cy + sin(prevAngle * 2f) * (h * 0.35f)
                    drawLine(
                        color = rankTier.secondaryColor.copy(alpha = 0.58f),
                        start = Offset(ppx, ppy),
                        end = Offset(px, py),
                        strokeWidth = 2.dp.toPx()
                    )
                }
                val planetR = (3.8.dp.toPx() + (i % 2) * 2.dp.toPx()) * pulse
                drawCircle(
                    color = rankTier.primaryColor.copy(alpha = 0.85f),
                    radius = planetR,
                    center = Offset(px, py)
                )
                drawCircle(
                    color = rankTier.accentColor.copy(alpha = 0.75f),
                    radius = planetR * 1.65f,
                    center = Offset(px, py),
                    style = Stroke(width = 1.4.dp.toPx())
                )
            }
        }
        else -> {
            // Tiers 9, 13..19: Quantum / Vortex / Chrono / Prism / Runic counter-rotating sacred rings
            for (i in 0..4) {
                val rx = w * (0.12f + i * 0.19f)
                val ry = cy + sin(phase * 1.6f + i * 1.1f) * 6.dp.toPx()
                val rad = 14.dp.toPx() * pulse
                drawCircle(
                    color = rankTier.secondaryColor.copy(alpha = 0.65f),
                    radius = rad,
                    center = Offset(rx, ry),
                    style = Stroke(width = 2.2.dp.toPx())
                )
                val rot = phase * (if (i % 2 == 0) 1.8f else -1.8f) + i
                drawLine(
                    color = rankTier.accentColor.copy(alpha = 0.65f),
                    start = Offset(rx - cos(rot) * rad, ry - sin(rot) * rad),
                    end = Offset(rx + cos(rot) * rad, ry + sin(rot) * rad),
                    strokeWidth = 1.8.dp.toPx(),
                    cap = StrokeCap.Round
                )
                drawDoodleSparkleStar(
                    center = Offset(rx, ry),
                    radius = 4.5.dp.toPx() * pulse,
                    color = rankTier.primaryColor,
                    rotationRad = -rot
                )
            }
        }
    }

    // 4. Sweeping diagonal shimmer highlight wave across the card
    val sweepProgress = (phase / (2f * PI).toFloat())
    val sweepX = -w * 0.35f + sweepProgress * (w * 1.7f)
    val shimmerPath = Path().apply {
        moveTo(sweepX, 0f)
        lineTo(sweepX + 28.dp.toPx(), 0f)
        lineTo(sweepX - 18.dp.toPx(), h)
        lineTo(sweepX - 46.dp.toPx(), h)
        close()
    }
    drawPath(
        path = shimmerPath,
        color = Color.White.copy(alpha = 0.32f * pulse)
    )

    // 5. Four corner animated 4-point doodle sparkle stars so the card visibly shimmers
    val cornerOffsets = listOf(
        Offset(12.dp.toPx(), 12.dp.toPx()),
        Offset(w - 12.dp.toPx(), 12.dp.toPx()),
        Offset(12.dp.toPx(), h - 12.dp.toPx()),
        Offset(w - 12.dp.toPx(), h - 12.dp.toPx())
    )
    cornerOffsets.forEachIndexed { idx, corner ->
        val starScale = 0.75f + 0.35f * abs(sin(phase + idx * 0.8f))
        drawDoodleSparkleStar(
            center = corner,
            radius = 5.2.dp.toPx() * starScale,
            color = rankTier.secondaryColor.copy(alpha = 0.85f),
            rotationRad = phase + idx * 0.5f
        )
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
// 6B. SCREEN 1B: 1,000-LEVEL BOARD GAME WORLD MAP & LEVEL SELECT UI
// ============================================================================

private data class WorldChapterInfo(
    val worldNumber: Int,
    val title: String,
    val levelRange: IntRange,
    val perilType: PerilType,
    val rankTier: RankTier,
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
        val chapterColors = listOf(
            Color(0xFFE1F5FE),
            Color(0xFFE8F5E9),
            Color(0xFFFFF3E0),
            Color(0xFFF1F8E9),
            Color(0xFFF9FBE7),
            Color(0xFFE0F7FA),
            Color(0xFFF3E5F5),
            Color(0xFFFFEBEE),
            Color(0xFFE8EAF6),
            Color(0xFFFFF8E1)
        )
        (0 until 50).map { idx ->
            val startLvl = idx * 20 + 1
            val endLvl = (idx + 1) * 20
            val peril = PerilType.forLevel(startLvl)
            val rank = RankTier.forLevel(startLvl)
            WorldChapterInfo(
                worldNumber = idx + 1,
                title = "Chapter ${idx + 1}: ${peril.displayName}",
                levelRange = startLvl..endLvl,
                perilType = peril,
                rankTier = rank,
                bannerColor = chapterColors[idx % chapterColors.size]
            )
        }
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
                    text = "1,000-Level World Map",
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 21.sp,
                    color = DoodleColors.InkBlack,
                    modifier = Modifier.testTag("world_map_title")
                )
                Text(
                    text = "⭐ $totalStars / 3000 Stars • ${viewModel.completedCampaignCount}/1000 Beaten",
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
                    color = DoodleColors.InkBlue
                )
            }

            // Free Play Unlock Toggle so player can test any of the 1,000 levels or play sequentially
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

        // Scrollable Board Game Map of 1,000 Levels across 50 Peril Chapters (LazyColumn)
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
                                    fontSize = 17.sp,
                                    color = DoodleColors.InkBlack
                                )
                                Text(
                                    text = "Levels ${chapter.levelRange.first}–${chapter.levelRange.last} • Rank: ${chapter.rankTier.title}" +
                                        if (chapter.levelRange.last >= 150) " • 🧊 Frozen Vowels" else "",
                                    fontFamily = fontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = DoodleColors.InkBlue
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Winding Board-Game Level Nodes Grid (20 levels per chapter)
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
                                val isFrozenVowelLvl = lvlNum >= 150

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

                                // Checkpoint Milestone Chest Icon every 50 levels (50, 100, 150, ..., 1000)
                                if (lvlNum % 50 == 0) {
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
                                                viewModel.statusBannerMessage = "Beat Level $lvlNum to open this +250 Coin Rank Chest!"
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
                fontSize = if (levelNumber >= 100) 14.sp else 16.sp,
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
            .size(width = 68.dp, height = 64.dp)
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
                text = "+250🪙",
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
                    modifier = Modifier
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                        .testTag("peril_subtitle_text")
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
        // Left Half: Custom Compose Canvas rendering the active PerilType (all 15 perils)
        // Right Half: Category pill + Clue/Category prompt + Boss & Frozen Vowel badges
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .heightIn(min = 190.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Half: Dynamic Peril Canvas
            Box(
                modifier = Modifier
                    .weight(0.53f)
                    .fillMaxHeight()
                    .heightIn(min = 185.dp)
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

    // Continuous 60fps hazard motion wave on Menu Preview so all 15 Peril Scenarios visibly animate
    val previewWave = (sin(wavePhase) + 1f) * 0.5f
    val effectiveDangerProgress = if (isMenuPreview && !isGameOver) {
        0.10f + previewWave * 0.42f
    } else {
        dangerProgress
    }
    val effectiveAnimatedLives = if (isMenuPreview && !isGameOver) {
        6f - effectiveDangerProgress * 3.2f
    } else {
        animatedLives
    }

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
                    dangerProgress = effectiveDangerProgress,
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
                    dangerProgress = effectiveDangerProgress,
                    bobOffset = bobOffset,
                    wavePhase = wavePhase,
                    ufoSway = patrolPhase,
                    isGameOver = isGameOver
                )

                PerilType.VOLCANO_BLOCKS -> drawVolcanoBlocksPeril(
                    livesRemaining = livesRemaining,
                    animatedLives = effectiveAnimatedLives,
                    popTrigger = popTrigger,
                    popProgress = popProgress.value,
                    bobOffset = bobOffset,
                    wavePhase = wavePhase,
                    isGameOver = isGameOver
                )

                PerilType.GIANT_VENUS_FLYTRAP -> drawVenusFlytrapPeril(
                    livesRemaining = livesRemaining,
                    animatedLives = effectiveAnimatedLives,
                    popTrigger = popTrigger,
                    popProgress = popProgress.value,
                    bobOffset = bobOffset,
                    wavePhase = wavePhase,
                    isGameOver = isGameOver
                )

                PerilType.DINO_STEALTH -> drawDinoStealthPeril(
                    livesRemaining = livesRemaining,
                    animatedLives = effectiveAnimatedLives,
                    popTrigger = popTrigger,
                    popProgress = popProgress.value,
                    bobOffset = bobOffset,
                    wavePhase = wavePhase,
                    isGameOver = isGameOver
                )

                PerilType.LEAKY_SUBMARINE -> drawLeakySubmarinePeril(
                    livesRemaining = livesRemaining,
                    animatedLives = effectiveAnimatedLives,
                    popTrigger = popTrigger,
                    popProgress = popProgress.value,
                    bobOffset = bobOffset,
                    wavePhase = wavePhase,
                    isGameOver = isGameOver
                )

                PerilType.TIGHTROPE_SAW -> drawTightropeSawPeril(
                    livesRemaining = livesRemaining,
                    animatedLives = effectiveAnimatedLives,
                    bobOffset = bobOffset,
                    wavePhase = wavePhase,
                    isGameOver = isGameOver
                )

                PerilType.METEOR_SHOWER -> drawMeteorShowerPeril(
                    livesRemaining = livesRemaining,
                    animatedLives = effectiveAnimatedLives,
                    bobOffset = bobOffset,
                    wavePhase = wavePhase,
                    isGameOver = isGameOver
                )

                PerilType.MAD_SCIENTIST_POTION -> drawMadScientistPotionPeril(
                    livesRemaining = livesRemaining,
                    animatedLives = effectiveAnimatedLives,
                    bobOffset = bobOffset,
                    wavePhase = wavePhase,
                    isGameOver = isGameOver
                )

                PerilType.PIRATE_PLANK -> drawPiratePlankPeril(
                    livesRemaining = livesRemaining,
                    animatedLives = effectiveAnimatedLives,
                    bobOffset = bobOffset,
                    wavePhase = wavePhase,
                    isGameOver = isGameOver
                )

                PerilType.SPIDER_WEB -> drawSpiderWebPeril(
                    livesRemaining = livesRemaining,
                    animatedLives = effectiveAnimatedLives,
                    bobOffset = bobOffset,
                    wavePhase = wavePhase,
                    isGameOver = isGameOver
                )

                PerilType.QUICKSAND_PIT -> drawQuicksandPitPeril(
                    livesRemaining = livesRemaining,
                    animatedLives = effectiveAnimatedLives,
                    bobOffset = bobOffset,
                    wavePhase = wavePhase,
                    isGameOver = isGameOver
                )

                PerilType.GHOST_MANOR -> drawGhostManorPeril(
                    livesRemaining = livesRemaining,
                    animatedLives = effectiveAnimatedLives,
                    bobOffset = bobOffset,
                    wavePhase = wavePhase,
                    isGameOver = isGameOver
                )

                PerilType.TICKING_TIME_BOMB -> drawTickingTimeBombPeril(
                    livesRemaining = livesRemaining,
                    animatedLives = effectiveAnimatedLives,
                    bobOffset = bobOffset,
                    wavePhase = wavePhase,
                    isGameOver = isGameOver
                )

                PerilType.DRAGON_CAVE -> drawDragonCavePeril(
                    livesRemaining = livesRemaining,
                    animatedLives = effectiveAnimatedLives,
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
// PERIL 7: TIGHTROPE_SAW (Levels 121–140, etc.)
// ----------------------------------------------------------------------------
private fun DrawScope.drawTightropeSawPeril(
    livesRemaining: Int,
    animatedLives: Float,
    bobOffset: Float,
    wavePhase: Float,
    isGameOver: Boolean
) {
    val w = size.width
    val h = size.height
    val ropeY = h * 0.62f
    val dangerRatio = ((6f - animatedLives) / 6f).coerceIn(0f, 1f)

    // Left and right canyon posts
    drawLine(color = DoodleColors.CrateStoneDark, start = Offset(w * 0.10f, ropeY - 8.dp.toPx()), end = Offset(w * 0.10f, h * 0.92f), strokeWidth = 6.dp.toPx(), cap = StrokeCap.Round)
    drawLine(color = DoodleColors.CrateStoneDark, start = Offset(w * 0.90f, ropeY - 8.dp.toPx()), end = Offset(w * 0.90f, h * 0.92f), strokeWidth = 6.dp.toPx(), cap = StrokeCap.Round)

    // 6-strand tightrope; strands snap as lives decrease
    for (i in 0 until 6) {
        val alive = i < livesRemaining
        val strandY = ropeY + (i - 2.5f) * 2.dp.toPx()
        drawLine(
            color = if (alive) DoodleColors.VineRopeBrown else DoodleColors.WrongRed.copy(alpha = 0.35f),
            start = Offset(w * 0.10f, strandY),
            end = Offset(w * 0.90f, strandY + if (alive) 0f else 10.dp.toPx()),
            strokeWidth = 1.6.dp.toPx(),
            cap = StrokeCap.Round
        )
    }

    // Spinning circular saw blade creeping along the rope toward center
    val sawX = w * 0.16f + (w * 0.34f) * dangerRatio
    val sawR = 19.dp.toPx()
    val teeth = 12
    val sawPath = Path().apply {
        for (i in 0 until teeth * 2) {
            val r = if (i % 2 == 0) sawR else sawR * 0.72f
            val angle = wavePhase * 3f + (i * PI / teeth).toFloat()
            val px = sawX + cos(angle) * r
            val py = ropeY + sin(angle) * r
            if (i == 0) moveTo(px, py) else lineTo(px, py)
        }
        close()
    }
    drawPath(path = sawPath, color = Color(0xFFCFD8DC))
    drawPath(path = sawPath, color = DoodleColors.InkBlack, style = Stroke(width = 1.8.dp.toPx(), join = StrokeJoin.Round))
    drawCircle(color = DoodleColors.WrongRed, radius = 4.5.dp.toPx(), center = Offset(sawX, ropeY))

    // Stickman balancing on tightrope with a long balance pole
    val stickX = w * 0.54f
    val stickAnchorY = if (isGameOver || livesRemaining <= 0) h * 0.78f else ropeY - 62.dp.toPx() + bobOffset * 0.6f
    drawDoodleStickman(
        handAnchor = Offset(stickX, stickAnchorY),
        scale = 0.90f,
        isPanicking = livesRemaining <= 2,
        isSplashed = isGameOver || livesRemaining <= 0,
        wavePhase = wavePhase,
        handsUp = false
    )
    // Balance pole
    val tilt = sin(wavePhase * 2f) * 10.dp.toPx()
    drawLine(
        color = DoodleColors.InkBlue,
        start = Offset(stickX - 36.dp.toPx(), stickAnchorY + 26.dp.toPx() - tilt),
        end = Offset(stickX + 36.dp.toPx(), stickAnchorY + 26.dp.toPx() + tilt),
        strokeWidth = 3.dp.toPx(),
        cap = StrokeCap.Round
    )
}

// ----------------------------------------------------------------------------
// PERIL 8: METEOR_SHOWER (Levels 141–160, etc.)
// ----------------------------------------------------------------------------
private fun DrawScope.drawMeteorShowerPeril(
    livesRemaining: Int,
    animatedLives: Float,
    bobOffset: Float,
    wavePhase: Float,
    isGameOver: Boolean
) {
    val w = size.width
    val h = size.height
    val dangerRatio = ((6f - animatedLives) / 6f).coerceIn(0f, 1f)

    // Floating asteroid rock platform at bottom
    val rockY = h * 0.76f
    drawOval(
        color = DoodleColors.CrateStoneLight,
        topLeft = Offset(w * 0.20f, rockY),
        size = Size(w * 0.60f, 28.dp.toPx())
    )
    drawOval(
        color = DoodleColors.InkBlack,
        topLeft = Offset(w * 0.20f, rockY),
        size = Size(w * 0.60f, 28.dp.toPx()),
        style = Stroke(width = 2.2.dp.toPx())
    )

    // Giant flaming meteor descending from top-right
    val meteorX = w * 0.76f - (w * 0.24f) * dangerRatio
    val meteorY = h * 0.16f + (h * 0.42f) * dangerRatio + bobOffset * 0.5f
    val meteorR = (18.dp.toPx() + 8.dp.toPx() * dangerRatio)

    // Flame tail
    val flamePath = Path().apply {
        moveTo(meteorX - meteorR * 0.6f, meteorY - meteorR * 0.6f)
        lineTo(meteorX + meteorR * 1.9f, meteorY - meteorR * 1.8f)
        lineTo(meteorX + meteorR * 0.7f, meteorY + meteorR * 0.5f)
        close()
    }
    drawPath(path = flamePath, color = DoodleColors.LavaOrange)
    drawCircle(color = DoodleColors.LavaRed, radius = meteorR * 1.15f, center = Offset(meteorX, meteorY))
    drawCircle(color = DoodleColors.CrateStoneDark, radius = meteorR, center = Offset(meteorX, meteorY))
    drawCircle(color = DoodleColors.InkBlack, radius = meteorR, center = Offset(meteorX, meteorY), style = Stroke(width = 2.dp.toPx()))

    // Stickman on asteroid
    drawDoodleStickman(
        handAnchor = Offset(w * 0.46f, rockY - 62.dp.toPx() + bobOffset * 0.4f),
        scale = 0.90f,
        isPanicking = livesRemaining <= 2,
        isSplashed = isGameOver || livesRemaining <= 0,
        wavePhase = wavePhase,
        handsUp = livesRemaining <= 2
    )
}

// ----------------------------------------------------------------------------
// PERIL 9: MAD_SCIENTIST_POTION (Levels 161–180, etc.)
// ----------------------------------------------------------------------------
private fun DrawScope.drawMadScientistPotionPeril(
    livesRemaining: Int,
    animatedLives: Float,
    bobOffset: Float,
    wavePhase: Float,
    isGameOver: Boolean
) {
    val w = size.width
    val h = size.height
    val dangerRatio = ((6f - animatedLives) / 6f).coerceIn(0f, 1f)

    // Giant glass beaker flask
    val flaskLeft = w * 0.22f
    val flaskRight = w * 0.78f
    val flaskTop = h * 0.16f
    val flaskBottom = h * 0.86f
    val flaskH = flaskBottom - flaskTop

    // Bubbling green acid rising from bottom
    val acidH = (flaskH * (0.18f + 0.68f * dangerRatio)).coerceAtMost(flaskH)
    val acidTop = flaskBottom - acidH
    drawRoundRect(
        color = DoodleColors.UfoBeamGreen.copy(alpha = 0.55f),
        topLeft = Offset(flaskLeft + 4.dp.toPx(), acidTop),
        size = Size(flaskRight - flaskLeft - 8.dp.toPx(), acidH),
        cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx())
    )

    // Bubbles in acid
    for (i in 0..4) {
        val bx = flaskLeft + (flaskRight - flaskLeft) * (0.2f + i * 0.15f)
        val by = acidTop + 10.dp.toPx() + sin(wavePhase * 2f + i) * 5.dp.toPx()
        if (by < flaskBottom - 6.dp.toPx()) {
            drawCircle(color = Color.White.copy(alpha = 0.75f), radius = 3.5.dp.toPx(), center = Offset(bx, by))
        }
    }

    // Beaker outline & measurement ticks
    drawRoundRect(
        color = DoodleColors.InkBlack,
        topLeft = Offset(flaskLeft, flaskTop),
        size = Size(flaskRight - flaskLeft, flaskH),
        cornerRadius = CornerRadius(14.dp.toPx(), 14.dp.toPx()),
        style = Stroke(width = 2.6.dp.toPx())
    )

    val stickAnchorY = (acidTop - 68.dp.toPx() + bobOffset * 0.5f).coerceAtLeast(flaskTop + 6.dp.toPx())
    drawDoodleStickman(
        handAnchor = Offset(w * 0.50f, stickAnchorY),
        scale = 0.86f,
        isPanicking = livesRemaining <= 2,
        isSplashed = isGameOver || livesRemaining <= 0,
        wavePhase = wavePhase,
        handsUp = true
    )
}

// ----------------------------------------------------------------------------
// PERIL 10: PIRATE_PLANK (Levels 181–200, etc.)
// ----------------------------------------------------------------------------
private fun DrawScope.drawPiratePlankPeril(
    livesRemaining: Int,
    animatedLives: Float,
    bobOffset: Float,
    wavePhase: Float,
    isGameOver: Boolean
) {
    val w = size.width
    val h = size.height
    val waterY = h * 0.80f
    val plankY = h * 0.50f
    val dangerRatio = ((6f - animatedLives) / 6f).coerceIn(0f, 1f)

    // Wooden pirate plank extending from left ship rail
    drawRoundRect(
        color = DoodleColors.VineRopeBrown,
        topLeft = Offset(0f, plankY),
        size = Size(w * 0.68f, 12.dp.toPx()),
        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
    )
    drawRoundRect(
        color = DoodleColors.InkBlack,
        topLeft = Offset(0f, plankY),
        size = Size(w * 0.68f, 12.dp.toPx()),
        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
        style = Stroke(width = 2.dp.toPx())
    )

    // Kraken tentacles rising from water on the right
    for (t in 0..2) {
        val tx = w * (0.58f + t * 0.14f)
        val tentacleTopY = waterY - (26.dp.toPx() + 34.dp.toPx() * dangerRatio) + sin(wavePhase * 2f + t) * 5.dp.toPx()
        val path = Path().apply {
            moveTo(tx, waterY + 6.dp.toPx())
            quadraticTo(tx - 12.dp.toPx(), (waterY + tentacleTopY) * 0.5f, tx + 6.dp.toPx(), tentacleTopY)
        }
        drawPath(path = path, color = Color(0xFF8E24AA), style = Stroke(width = 7.dp.toPx(), cap = StrokeCap.Round))
        drawPath(path = path, color = DoodleColors.InkBlack, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))
    }

    // Stickman stepping closer to the edge of the plank with each wrong guess
    val stickX = w * 0.20f + (w * 0.44f) * dangerRatio
    val stickY = if (isGameOver || livesRemaining <= 0) waterY - 22.dp.toPx() else plankY - 64.dp.toPx() + bobOffset * 0.4f
    drawDoodleStickman(
        handAnchor = Offset(stickX, stickY),
        scale = 0.88f,
        isPanicking = livesRemaining <= 2,
        isSplashed = isGameOver || livesRemaining <= 0,
        wavePhase = wavePhase,
        handsUp = false
    )

    drawDoodleWaterWaves(waterY = waterY, wavePhase = wavePhase)
}

// ----------------------------------------------------------------------------
// PERIL 11: SPIDER_WEB (Levels 201–220, etc.)
// ----------------------------------------------------------------------------
private fun DrawScope.drawSpiderWebPeril(
    livesRemaining: Int,
    animatedLives: Float,
    bobOffset: Float,
    wavePhase: Float,
    isGameOver: Boolean
) {
    val w = size.width
    val h = size.height
    val cx = w * 0.50f
    val cy = h * 0.54f
    val dangerRatio = ((6f - animatedLives) / 6f).coerceIn(0f, 1f)

    // Radial spider web lines
    for (i in 0 until 8) {
        val angle = (i * PI / 4.0).toFloat()
        drawLine(
            color = DoodleColors.InkBlack.copy(alpha = 0.35f),
            start = Offset(cx, cy),
            end = Offset(cx + cos(angle) * w * 0.45f, cy + sin(angle) * h * 0.42f),
            strokeWidth = 1.4.dp.toPx()
        )
    }
    for (ring in 1..3) {
        drawCircle(
            color = DoodleColors.InkBlack.copy(alpha = 0.28f),
            radius = ring * 22.dp.toPx(),
            center = Offset(cx, cy),
            style = Stroke(width = 1.2.dp.toPx())
        )
    }

    // Stickman stuck in center of web
    drawDoodleStickman(
        handAnchor = Offset(cx, cy - 26.dp.toPx() + bobOffset * 0.3f),
        scale = 0.88f,
        isPanicking = livesRemaining <= 2,
        isSplashed = isGameOver || livesRemaining <= 0,
        wavePhase = wavePhase,
        handsUp = true
    )

    // Giant doodle spider descending on silk thread from top
    val spiderY = h * 0.14f + (h * 0.28f) * dangerRatio
    drawLine(color = DoodleColors.InkBlack, start = Offset(cx, 0f), end = Offset(cx, spiderY), strokeWidth = 2.dp.toPx())
    drawCircle(color = DoodleColors.InkBlack, radius = 14.dp.toPx(), center = Offset(cx, spiderY))
    // 8 spider legs
    for (side in listOf(-1f, 1f)) {
        for (leg in -1..2) {
            val ly = spiderY + leg * 4.dp.toPx()
            drawLine(
                color = DoodleColors.InkBlack,
                start = Offset(cx, ly),
                end = Offset(cx + side * 24.dp.toPx(), ly + sin(wavePhase * 3f + leg) * 4.dp.toPx()),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
    // Glowing red spider eyes
    drawCircle(color = DoodleColors.WrongRed, radius = 2.5.dp.toPx(), center = Offset(cx - 4.dp.toPx(), spiderY + 3.dp.toPx()))
    drawCircle(color = DoodleColors.WrongRed, radius = 2.5.dp.toPx(), center = Offset(cx + 4.dp.toPx(), spiderY + 3.dp.toPx()))
}

// ----------------------------------------------------------------------------
// PERIL 12: QUICKSAND_PIT (Levels 221–240, etc.)
// ----------------------------------------------------------------------------
private fun DrawScope.drawQuicksandPitPeril(
    livesRemaining: Int,
    animatedLives: Float,
    bobOffset: Float,
    wavePhase: Float,
    isGameOver: Boolean
) {
    val w = size.width
    val h = size.height
    val sandY = h * 0.68f
    val dangerRatio = ((6f - animatedLives) / 6f).coerceIn(0f, 1f)

    // Stickman sinking deeper into the sand pit with each wrong guess
    val sinkOffset = (56.dp.toPx()) * dangerRatio
    val stickAnchorY = sandY - 58.dp.toPx() + sinkOffset + bobOffset * 0.3f
    drawDoodleStickman(
        handAnchor = Offset(w * 0.50f, stickAnchorY),
        scale = 0.90f,
        isPanicking = livesRemaining <= 2,
        isSplashed = isGameOver || livesRemaining <= 0,
        wavePhase = wavePhase,
        handsUp = true
    )

    // Quicksand dune foreground covering lower body as stickman sinks
    drawRect(
        color = Color(0xFFE6C280),
        topLeft = Offset(0f, sandY),
        size = Size(w, h - sandY)
    )
    drawLine(
        color = DoodleColors.VineRopeBrown,
        start = Offset(0f, sandY),
        end = Offset(w, sandY),
        strokeWidth = 3.dp.toPx()
    )
    // Swirling quicksand rings
    for (i in 1..3) {
        drawOval(
            color = DoodleColors.VineRopeBrown.copy(alpha = 0.45f),
            topLeft = Offset(w * 0.5f - i * 24.dp.toPx(), sandY + 8.dp.toPx() + i * 4.dp.toPx()),
            size = Size(i * 48.dp.toPx(), 12.dp.toPx()),
            style = Stroke(width = 1.6.dp.toPx())
        )
    }
}

// ----------------------------------------------------------------------------
// PERIL 13: GHOST_MANOR (Levels 241–260, etc.)
// ----------------------------------------------------------------------------
private fun DrawScope.drawGhostManorPeril(
    livesRemaining: Int,
    animatedLives: Float,
    bobOffset: Float,
    wavePhase: Float,
    isGameOver: Boolean
) {
    val w = size.width
    val h = size.height
    val cx = w * 0.50f

    // 6-candle chandelier at top; candles blow out one by one
    val barY = h * 0.20f
    drawLine(color = DoodleColors.InkBlack, start = Offset(cx, 0f), end = Offset(cx, barY), strokeWidth = 2.5.dp.toPx())
    drawLine(color = DoodleColors.InkBlack, start = Offset(w * 0.18f, barY), end = Offset(w * 0.82f, barY), strokeWidth = 3.5.dp.toPx(), cap = StrokeCap.Round)

    for (i in 0 until 6) {
        val candleX = w * (0.20f + i * 0.12f)
        drawRect(
            color = Color.White,
            topLeft = Offset(candleX - 3.dp.toPx(), barY - 12.dp.toPx()),
            size = Size(6.dp.toPx(), 12.dp.toPx())
        )
        drawRect(
            color = DoodleColors.InkBlack,
            topLeft = Offset(candleX - 3.dp.toPx(), barY - 12.dp.toPx()),
            size = Size(6.dp.toPx(), 12.dp.toPx()),
            style = Stroke(width = 1.2.dp.toPx())
        )
        if (i < livesRemaining && !isGameOver) {
            drawCircle(
                color = DoodleColors.DoodleYellow,
                radius = 4.5.dp.toPx(),
                center = Offset(candleX, barY - 17.dp.toPx() + sin(wavePhase * 3f + i) * 1.5.dp.toPx())
            )
        }
    }

    // Swooping doodle ghost on left/right getting closer as candles go out
    val dangerRatio = ((6f - animatedLives) / 6f).coerceIn(0f, 1f)
    val ghostX = w * 0.18f + (w * 0.22f) * dangerRatio
    val ghostY = h * 0.44f + sin(wavePhase * 2f) * 8.dp.toPx()
    drawCircle(color = Color(0xFFE1F5FE), radius = 16.dp.toPx(), center = Offset(ghostX, ghostY))
    drawCircle(color = DoodleColors.InkBlack, radius = 16.dp.toPx(), center = Offset(ghostX, ghostY), style = Stroke(width = 2.dp.toPx()))
    drawCircle(color = DoodleColors.InkBlack, radius = 2.2.dp.toPx(), center = Offset(ghostX - 5.dp.toPx(), ghostY - 2.dp.toPx()))
    drawCircle(color = DoodleColors.InkBlack, radius = 2.2.dp.toPx(), center = Offset(ghostX + 5.dp.toPx(), ghostY - 2.dp.toPx()))

    // Stickman shivering on manor floor
    drawDoodleStickman(
        handAnchor = Offset(w * 0.62f, h * 0.50f + bobOffset * 0.4f),
        scale = 0.90f,
        isPanicking = livesRemaining <= 2,
        isSplashed = isGameOver || livesRemaining <= 0,
        wavePhase = wavePhase,
        handsUp = false
    )
}

// ----------------------------------------------------------------------------
// PERIL 14: TICKING_TIME_BOMB (Levels 261–280, etc.)
// ----------------------------------------------------------------------------
private fun DrawScope.drawTickingTimeBombPeril(
    livesRemaining: Int,
    animatedLives: Float,
    bobOffset: Float,
    wavePhase: Float,
    isGameOver: Boolean
) {
    val w = size.width
    val h = size.height
    val bombCenter = Offset(w * 0.66f, h * 0.62f)
    val bombR = 26.dp.toPx()
    val fuseRatio = (animatedLives / 6f).coerceIn(0f, 1f)

    // Giant cartoon round bomb
    drawCircle(color = DoodleColors.ChalkboardDark, radius = bombR, center = bombCenter)
    drawCircle(color = DoodleColors.InkBlack, radius = bombR, center = bombCenter, style = Stroke(width = 2.4.dp.toPx()))
    // Fuse burning down as lives drop
    val fuseTipX = bombCenter.x + (34.dp.toPx() * fuseRatio)
    val fuseTipY = bombCenter.y - bombR - (28.dp.toPx() * fuseRatio)
    drawLine(
        color = DoodleColors.VineRopeBrown,
        start = Offset(bombCenter.x, bombCenter.y - bombR),
        end = Offset(fuseTipX, fuseTipY),
        strokeWidth = 3.dp.toPx(),
        cap = StrokeCap.Round
    )
    // Fuse spark
    drawCircle(color = DoodleColors.LavaOrange, radius = 6.dp.toPx() + sin(wavePhase * 6f) * 2.dp.toPx(), center = Offset(fuseTipX, fuseTipY))
    drawCircle(color = DoodleColors.DoodleYellow, radius = 3.dp.toPx(), center = Offset(fuseTipX, fuseTipY))

    // Stickman next to bomb
    drawDoodleStickman(
        handAnchor = Offset(w * 0.30f, h * 0.48f + bobOffset * 0.4f),
        scale = 0.90f,
        isPanicking = livesRemaining <= 2,
        isSplashed = isGameOver || livesRemaining <= 0,
        wavePhase = wavePhase,
        handsUp = livesRemaining <= 2
    )
}

// ----------------------------------------------------------------------------
// PERIL 15: DRAGON_CAVE (Levels 281–300, etc.)
// ----------------------------------------------------------------------------
private fun DrawScope.drawDragonCavePeril(
    livesRemaining: Int,
    animatedLives: Float,
    bobOffset: Float,
    wavePhase: Float,
    isGameOver: Boolean
) {
    val w = size.width
    val h = size.height
    val dangerRatio = ((6f - animatedLives) / 6f).coerceIn(0f, 1f)

    // Dragon snout on the right breathing growing fire stream
    val snoutX = w * 0.82f
    val snoutY = h * 0.48f
    drawRoundRect(
        color = DoodleColors.WrongRed,
        topLeft = Offset(snoutX - 18.dp.toPx(), snoutY - 16.dp.toPx()),
        size = Size(34.dp.toPx(), 32.dp.toPx()),
        cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
    )
    drawRoundRect(
        color = DoodleColors.InkBlack,
        topLeft = Offset(snoutX - 18.dp.toPx(), snoutY - 16.dp.toPx()),
        size = Size(34.dp.toPx(), 32.dp.toPx()),
        cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
        style = Stroke(width = 2.2.dp.toPx())
    )

    // Fire stream reaching closer to Stickman's shield
    val fireLen = (w * 0.14f) + (w * 0.34f) * dangerRatio
    val firePath = Path().apply {
        moveTo(snoutX - 18.dp.toPx(), snoutY)
        lineTo(snoutX - 18.dp.toPx() - fireLen, snoutY - 14.dp.toPx() - dangerRatio * 8.dp.toPx())
        lineTo(snoutX - 18.dp.toPx() - fireLen, snoutY + 14.dp.toPx() + dangerRatio * 8.dp.toPx())
        close()
    }
    drawPath(path = firePath, color = DoodleColors.LavaOrange.copy(alpha = 0.82f))
    drawPath(path = firePath, color = DoodleColors.DoodleYellow.copy(alpha = 0.55f))

    // Stickman holding a knight shield on the left
    val stickX = w * 0.26f
    drawDoodleStickman(
        handAnchor = Offset(stickX, h * 0.46f + bobOffset * 0.4f),
        scale = 0.88f,
        isPanicking = livesRemaining <= 2,
        isSplashed = isGameOver || livesRemaining <= 0,
        wavePhase = wavePhase,
        handsUp = false
    )
    // Shield with 6 rivets
    val shieldCenter = Offset(stickX + 18.dp.toPx(), h * 0.56f)
    drawCircle(color = DoodleColors.SubmarineSteel, radius = 14.dp.toPx(), center = shieldCenter)
    drawCircle(color = DoodleColors.InkBlack, radius = 14.dp.toPx(), center = shieldCenter, style = Stroke(width = 2.dp.toPx()))
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
            // Top dark chalkboard header with 60fps random winner animation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(215.dp)
                    .background(DoodleColors.ChalkboardDark)
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
