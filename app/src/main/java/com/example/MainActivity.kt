package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
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
import kotlin.random.Random
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    val DoodleYellowDark = Color(0xFFF4B942)

    val HintMagnifierGreen = Color(0xFF4CAF50)
    val HintPotionBlue = Color(0xFF2196F3)
    val HintBulbOrange = Color(0xFFFF9800)

    val ChalkboardDark = Color(0xFF263238)
    val ChalkboardGrid = Color(0xFF37474F)
    val WaterBlue = Color(0xFF4FC3F7)
    val WaterDeep = Color(0xFF0288D1)
    val SharkGrey = Color(0xFF546E7A)
    val SharkBelly = Color(0xFFCFD8DC)

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
// 2. DATA MODELS & PRE-POPULATED LEVELS
// ============================================================================

data class WordLevel(
    val number: Int,
    val word: String,
    val clue: String,
    val isAdventure: Boolean = false
)

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

val CAMPAIGN_LEVELS = listOf(
    WordLevel(1, "DIAMOND", "Guess a precious gemstone"),
    WordLevel(2, "CHINA", "What country was paper invented in?"),
    WordLevel(3, "PRANK", "April Fool's Day tradition"),
    WordLevel(4, "CAKE", "Sweet baked treat with birthday candles"),
    WordLevel(5, "GIRAFFE", "Tallest African mammal with a long neck"),
    WordLevel(6, "COMPASS", "Navigation tool that always points north"),
    WordLevel(7, "VOLCANO", "Mountain that erupts with glowing lava"),
    WordLevel(8, "PENGUIN", "Tuxedo-wearing bird that cannot fly"),
    WordLevel(9, "RAINBOW", "Colorful arc seen in the sky after rain"),
    WordLevel(10, "PIRATE", "High-seas sailor searching for buried treasure"),
    WordLevel(11, "CACTUS", "Spiky desert plant that stores water"),
    WordLevel(12, "GUITAR", "Six-stringed musical instrument"),
    WordLevel(13, "OCTOPUS", "Clever sea creature with eight tentacles"),
    WordLevel(14, "POPCORN", "Buttery snack eaten at the movie theater"),
    WordLevel(15, "ASTRONAUT", "Explorer who travels into outer space"),
    WordLevel(16, "IGLOO", "Dome-shaped shelter built from snow blocks"),
    WordLevel(17, "TORNADO", "Violently rotating column of wind"),
    WordLevel(18, "PANCAKE", "Flat breakfast cake served with maple syrup"),
    WordLevel(19, "DINOSAUR", "Prehistoric reptile that ruled the Earth"),
    WordLevel(20, "TREASURE", "Chest overflowing with gold coins and jewels")
)

val ADVENTURE_LEVELS = listOf(
    WordLevel(101, "LABYRINTH", "Devil Mode: An intricate maze of winding passages", true),
    WordLevel(102, "CHAMELEON", "Devil Mode: Lizard famous for shifting its colors", true),
    WordLevel(103, "AVALANCHE", "Devil Mode: Sudden rapid rush of snow down a cliff", true),
    WordLevel(104, "QUICKSAND", "Devil Mode: Loose wet sand that traps the unwary", true),
    WordLevel(105, "JELLYFISH", "Devil Mode: Translucent ocean drifter with stinging tentacles", true),
    WordLevel(106, "SAXOPHONE", "Devil Mode: Brass woodwind instrument iconic in jazz", true),
    WordLevel(107, "METEORITE", "Devil Mode: Space rock that survives falling to Earth", true),
    WordLevel(108, "KALEIDOSCOPE", "Devil Mode: Optical tube of mirrors and colorful glass", true)
)

// ============================================================================
// 3. GAME VIEWMODEL (STATE, COINS, STREAKS, POWER-UPS & ANIMATION TRIGGERS)
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
    var chestProgress by mutableIntStateOf(1) // 0..4 (awards bonus chest at 4/4)
        private set
    var themeProgress by mutableIntStateOf(1) // 0..10 (unlocks themes)
        private set

    // Power-up inventory & costs
    var freeShieldPotions by mutableIntStateOf(2)
        private set

    // Settings
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

    // Animation triggers for wrong guess / balloon pop / coin toast
    var lastPoppedBalloonIndex by mutableIntStateOf(-1)
        private set
    var popAnimationTrigger by mutableIntStateOf(0)
        private set
    var statusBannerMessage by mutableStateOf<String?>(null)
        private set

    val currentCampaignLevelNumber: Int
        get() = (campaignLevelIndex % CAMPAIGN_LEVELS.size) + 1

    fun startCampaignLevel() {
        val level = CAMPAIGN_LEVELS[campaignLevelIndex % CAMPAIGN_LEVELS.size].copy(
            number = campaignLevelIndex + 1
        )
        loadLevel(level)
        currentScreen = ScreenState.GAMEPLAY
    }

    fun startAdventureLevel() {
        val level = ADVENTURE_LEVELS[adventureLevelIndex % ADVENTURE_LEVELS.size]
        loadLevel(level)
        currentScreen = ScreenState.GAMEPLAY
    }

    fun openTwoPlayerSetup() {
        currentScreen = ScreenState.TWO_PLAYER_SETUP
    }

    fun startTwoPlayerGame(rawWord: String, rawClue: String) {
        val cleanedWord = rawWord.uppercase().filter { it in 'A'..'Z' }.take(12)
        val finalWord = cleanedWord.ifEmpty { "SHARK" }
        val finalClue = rawClue.trim().ifEmpty { "Two-Player Secret Word!" }
        loadLevel(
            WordLevel(
                number = 0,
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

    private fun loadLevel(level: WordLevel) {
        activeLevel = level
        maxBalloons = 6
        balloonsRemaining = 6
        mistakesThisRound = 0
        roundOutcome = RoundOutcome.PLAYING
        keyStatuses.clear()
        revealedChars.clear()
        lastPoppedBalloonIndex = -1
        statusBannerMessage = null
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
            checkVictoryCondition()
        } else {
            keyStatuses[upper] = KeyStatus.WRONG
            mistakesThisRound++
            if (balloonsRemaining > 0) {
                lastPoppedBalloonIndex = balloonsRemaining - 1
                balloonsRemaining--
                popAnimationTrigger++
            }
            if (balloonsRemaining <= 0) {
                currentStreak = 0
                roundOutcome = RoundOutcome.GAME_OVER
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

    // Power-Up 2: Blue Shield/Potion (Badge count or Cost: 200 coins) -> Adds 1 extra balloon
    fun useExtraBalloonPotion() {
        if (roundOutcome != RoundOutcome.PLAYING) return
        if (balloonsRemaining >= 6) {
            statusBannerMessage = "Already at max 6 balloons!"
            return
        }
        val cost = 200
        if (freeShieldPotions > 0) {
            freeShieldPotions--
            balloonsRemaining++
            statusBannerMessage = "+1 Balloon attached! (Used free potion)"
        } else if (coins >= cost) {
            coins -= cost
            balloonsRemaining++
            statusBannerMessage = "+1 Balloon attached!"
        } else {
            statusBannerMessage = "Need $cost coins for a Balloon Potion!"
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
        val toEliminate = candidateWrongs.shuffled().take(3)
        toEliminate.forEach { ch ->
            keyStatuses[ch] = KeyStatus.WRONG
        }
        statusBannerMessage = "Cleared ${toEliminate.size} wrong letters!"
    }

    fun proceedAfterVictory() {
        val reward = if (activeLevel.isAdventure) 50 else 25
        val chestBonus = if (chestProgress == 4) 100 else 0
        coins += reward + chestBonus

        if (activeLevel.isAdventure) {
            adventureLevelIndex++
            startAdventureLevel()
        } else if (activeLevel.number == 0) {
            // Two-player mode returns to setup or menu
            navigateToMainMenu()
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
            statusBannerMessage = "Rescued with 3 fresh balloons!"
        } else {
            statusBannerMessage = "Need $rescueCost coins! Tap + at top right."
        }
    }

    fun claimFreeCoinBonus(amount: Int = 150) {
        coins += amount
        statusBannerMessage = "+$amount Doodle Coins added!"
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

    // Auto-dismiss transient banner feedback
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
                        onOpenSettings = { viewModel.showSettingsDialog = true },
                        onOpenCoinShop = { viewModel.showCoinShopDialog = true }
                    )
                }

                ScreenState.TWO_PLAYER_SETUP -> {
                    BackHandler { viewModel.navigateToMainMenu() }
                    TwoPlayerSetupScreen(
                        fontFamily = activeFontFamily,
                        onBack = { viewModel.navigateToMainMenu() },
                        onStartGame = { word, clue ->
                            viewModel.startTwoPlayerGame(word, clue)
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

            // Floating Status Toast (e.g. power-up feedback)
            AnimatedVisibility(
                visible = viewModel.statusBannerMessage != null,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 68.dp)
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

        // 4. Game Over Overlay (0 Balloons Left - Stickman falls into shark water)
        AnimatedVisibility(
            visible = viewModel.currentScreen == ScreenState.GAMEPLAY &&
                viewModel.roundOutcome == RoundOutcome.GAME_OVER,
            enter = fadeIn(tween(250)) + scaleIn(),
            exit = fadeOut(tween(150))
        ) {
            GameOverOverlay(
                level = viewModel.activeLevel,
                coins = viewModel.coins,
                fontFamily = activeFontFamily,
                onRescue = { viewModel.rescueWithCoins() },
                onRetry = { viewModel.retryCurrentLevel() },
                onMainMenu = { viewModel.navigateToMainMenu() }
            )
        }

        // 5. Settings & Paper Theme Dialog
        if (viewModel.showSettingsDialog) {
            SettingsDoodleDialog(
                useCursive = viewModel.useCursiveFont,
                selectedTheme = viewModel.selectedPaperTheme,
                fontFamily = activeFontFamily,
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

        // Vertical grid lines
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

        // Horizontal grid lines
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

        // Subtle notebook red vertical margin line on the left
        val marginX = step * 1.6f
        drawLine(
            color = DoodleColors.MarginRed.copy(alpha = 0.45f),
            start = Offset(marginX, 0f),
            end = Offset(marginX, height),
            strokeWidth = 2f
        )
    }
}

/**
 * A reusable container that renders a solid pastel fill and a double-stroked,
 * slightly uneven hand-sketched black outline with a subtle ink shadow.
 */
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

            // Offset hand-drawn shadow
            drawRoundRect(
                color = borderColor.copy(alpha = 0.18f),
                topLeft = Offset(sh, sh),
                size = Size(size.width - sh, size.height - sh),
                cornerRadius = CornerRadius(r, r),
                style = Fill
            )

            // Main pastel fill
            drawRoundRect(
                color = fillColor,
                topLeft = Offset(0f, 0f),
                size = Size(size.width - sh * 0.6f, size.height - sh * 0.6f),
                cornerRadius = CornerRadius(r, r),
                style = Fill
            )

            // Primary outer sketchy stroke
            drawRoundRect(
                color = borderColor,
                topLeft = Offset(sw * 0.5f, sw * 0.5f),
                size = Size(size.width - sh * 0.6f - sw, size.height - sh * 0.6f - sw),
                cornerRadius = CornerRadius(r, r),
                style = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Second slightly tilted/offset stroke for authentic hand-sketched double-line look
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
// 6. SCREEN 1: MAIN MENU SCREEN
// ============================================================================

@Composable
fun MainMenuScreen(
    levelNumber: Int,
    coins: Int,
    fontFamily: FontFamily,
    onPlayCampaign: () -> Unit,
    onPlayAdventure: () -> Unit,
    onPlayTwoPlayer: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenCoinShop: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .widthIn(max = 600.dp)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Bar: Settings gear icon (left), Star/Coin counter pill with + button (right)
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

        // Center Hero: Animated Stickman floating on 6 balloons above circling shark + "W O R D" title
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
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                BalloonSharkSceneCanvas(
                    balloonsRemaining = 6,
                    lastPoppedBalloonIndex = -1,
                    popTrigger = 0,
                    isGameOver = false,
                    showSpeechBubble = false,
                    isMenuPreview = true,
                    fontFamily = fontFamily,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title "W O R D" with colored underlines beneath each letter
            DoodleWordTitleHeader(fontFamily = fontFamily)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bottom Stacked Buttons (Sketchy Pill Style)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 360.dp)
                .padding(bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
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
                Text(
                    text = "Level $levelNumber",
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    color = DoodleColors.InkBlack
                )
            }

            // 2. Adventure (White fill with a small red devil/horn doodle icon on the top-left corner)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp)
            ) {
                SketchyContainer(
                    fillColor = Color.White,
                    cornerRadius = 28.dp,
                    onClick = onPlayAdventure,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .align(Alignment.BottomCenter)
                        .testTag("adventure_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Adventure",
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 22.sp,
                            color = DoodleColors.InkBlack
                        )
                    }
                }

                // Small red devil/horn doodle badge on the top-left corner
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
                    .height(56.dp)
                    .testTag("two_player_button")
            ) {
                Text(
                    text = "Two Player",
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
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
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        letters.forEachIndexed { index, letter ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = letter.toString(),
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 42.sp,
                    color = DoodleColors.InkBlack
                )
                Canvas(
                    modifier = Modifier
                        .width(42.dp)
                        .height(10.dp)
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
// 7. SCREEN 2: GAMEPLAY SCREEN
// ============================================================================

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GameplayScreen(
    viewModel: WordGameViewModel,
    fontFamily: FontFamily
) {
    val level = viewModel.activeLevel
    val targetWord = level.word.uppercase()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .widthIn(max = 650.dp)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. Top Bar: Back arrow button, "Level X" text, and Coin balance pill (1231 + Watch Ad/Add icon)
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

            val headerTitle = when {
                level.isAdventure -> "Adventure"
                level.number == 0 -> "2 Player"
                else -> "Level ${level.number}"
            }

            Text(
                text = headerTitle,
                fontFamily = fontFamily,
                fontWeight = FontWeight.Black,
                fontSize = 26.sp,
                color = DoodleColors.InkBlack,
                modifier = Modifier.testTag("level_header_text")
            )

            CoinBalancePill(
                coins = viewModel.coins,
                fontFamily = fontFamily,
                onClickAdd = { viewModel.showCoinShopDialog = true },
                testTag = "game_coin_pill"
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 2. Upper Split Scene:
        // Left Half: Custom Compose Canvas rendering Stickman, remaining balloons, water line, shark
        // Right Half: The Clue/Category prompt centered vertically
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Half: Balloon & Shark Canvas
            Box(
                modifier = Modifier
                    .weight(0.52f)
                    .fillMaxHeight()
                    .padding(end = 6.dp)
            ) {
                BalloonSharkSceneCanvas(
                    balloonsRemaining = viewModel.balloonsRemaining,
                    lastPoppedBalloonIndex = viewModel.lastPoppedBalloonIndex,
                    popTrigger = viewModel.popAnimationTrigger,
                    isGameOver = viewModel.roundOutcome == RoundOutcome.GAME_OVER,
                    showSpeechBubble = viewModel.balloonsRemaining in 1..2,
                    isMenuPreview = false,
                    fontFamily = fontFamily,
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("balloon_shark_canvas")
                )
            }

            // Right Half: Clue / Category prompt centered vertically
            Box(
                modifier = Modifier
                    .weight(0.48f)
                    .fillMaxHeight()
                    .padding(start = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 6.dp)
                ) {
                    // Lives / Balloons indicator pill
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(bottom = 10.dp)
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

                    Text(
                        text = level.clue,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 21.sp,
                        lineHeight = 27.sp,
                        color = DoodleColors.InkBlack,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.testTag("clue_text")
                    )
                }
            }
        }

        // 3. Middle Area (Word Slots): Horizontal row of blank underlines (_) for each letter
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
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

        // 4. Keyboard Area: Custom 4-row alphabetical grid
        // Row 1: A B C D E F G
        // Row 2: H I J K L M N
        // Row 3: O P Q R S T U
        // Row 4: V W X Y Z
        DoodleAlphabetKeyboard(
            keyStatuses = viewModel.keyStatuses,
            fontFamily = fontFamily,
            onKeyPress = { ch -> viewModel.onLetterTapped(ch) }
        )

        Spacer(modifier = Modifier.height(10.dp))

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
            modifier = Modifier.height(38.dp)
        ) {
            if (isRevealed) {
                Text(
                    text = letter.toString(),
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 28.sp,
                    color = if (slotIndex % 2 == 0) DoodleColors.CorrectGreen else DoodleColors.InkBlue,
                    modifier = Modifier.scale(scale)
                )
            }
        }

        // Hand-sketched underline
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
// 8. CUSTOM 4-ROW ALPHABETICAL KEYBOARD WITH HAND-DRAWN CIRCLES & CROSSES
// ============================================================================

@Composable
fun DoodleAlphabetKeyboard(
    keyStatuses: Map<Char, KeyStatus>,
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
        verticalArrangement = Arrangement.spacedBy(6.dp),
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
                    DoodleLetterKey(
                        letter = ch,
                        status = status,
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
                contentDescription = "Letter $letter"
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
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Green Magnifying Glass (Cost: 150 coins) -> Reveals 1 random unguessed letter
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

        // 2. Blue Shield/Potion (Badge count / Cost: 200 coins) -> Adds 1 extra balloon
        PowerUpCircularButton(
            badgeText = if (freePotions > 0) "$freePotions" else "200",
            isCountBadge = freePotions > 0,
            accentColor = DoodleColors.HintPotionBlue,
            fontFamily = fontFamily,
            testTag = "hint_balloon_button",
            contentDescription = "Add 1 extra balloon life",
            onClick = onExtraBalloon
        ) {
            DrawDoodlePotionShieldIcon()
        }

        // 3. Orange Lightbulb (Cost: 125 coins) -> Eliminates 3 wrong letters
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
            .size(width = 86.dp, height = 74.dp)
            .testTag(testTag)
            .semantics { this.contentDescription = contentDescription }
    ) {
        // Circular Sketch Button
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(62.dp)
                .align(Alignment.TopCenter)
                .clip(CircleShape)
                .clickable(onClick = onClick)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val r = size.minDimension * 0.44f
                // Drop shadow
                drawCircle(
                    color = DoodleColors.InkBlack.copy(alpha = 0.16f),
                    radius = r,
                    center = Offset(center.x + 2.dp.toPx(), center.y + 3.dp.toPx())
                )
                // White fill
                drawCircle(
                    color = Color.White,
                    radius = r
                )
                // Colored inner ring tint
                drawCircle(
                    color = accentColor.copy(alpha = 0.18f),
                    radius = r * 0.84f
                )
                // Sketchy double black outline
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

        // Cost / Badge Pill at bottom-right / bottom-center
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
// 10. CORE PROCEDURAL CANVAS: STICKMAN, 6 BALLOONS, SHARK FIN/JAWS & "-1 ❤️"
// ============================================================================

@Composable
fun BalloonSharkSceneCanvas(
    balloonsRemaining: Int,
    lastPoppedBalloonIndex: Int,
    popTrigger: Int,
    isGameOver: Boolean,
    showSpeechBubble: Boolean,
    isMenuPreview: Boolean,
    fontFamily: FontFamily,
    modifier: Modifier = Modifier
) {
    // Continuous animations for bobbing, water waves, and shark circling
    val infiniteTransition = rememberInfiniteTransition(label = "sceneInfinite")

    val bobOffset by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "stickmanBob"
    )

    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waterWave"
    )

    val sharkPatrol by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sharkPatrol"
    )

    // Stickman altitude drops smoothly as balloons pop (6 -> high, 1 -> just above shark jaws, 0 -> in water)
    val dropProgress by animateFloatAsState(
        targetValue = when {
            isGameOver || balloonsRemaining <= 0 -> 1.0f
            else -> (6 - balloonsRemaining) / 6.2f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "altitudeDrop"
    )

    // Popping balloon & floating "-1 ❤️" animation state
    val popProgress = remember { Animatable(1f) }
    LaunchedEffect(popTrigger) {
        if (popTrigger > 0) {
            popProgress.snapTo(0f)
            popProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing)
            )
        }
    }

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val centerX = w * 0.5f
            val waterY = h * 0.80f

            // Calculate Stickman harness point (hands clasping the balloon strings)
            val topSafeY = h * 0.34f
            val dangerLowY = h * 0.62f
            val splashedY = h * 0.82f

            val baseHarnessY = if (balloonsRemaining <= 0 || isGameOver) {
                splashedY
            } else {
                topSafeY + (dangerLowY - topSafeY) * (dropProgress * 1.15f)
            }
            val harnessY = baseHarnessY + bobOffset

            // Balloon cluster layout (fan of 6 balloons above the stickman's hands)
            val balloonOffsets = listOf(
                Offset(-w * 0.24f, -h * 0.20f),
                Offset(-w * 0.14f, -h * 0.27f),
                Offset(-w * 0.04f, -h * 0.30f),
                Offset(w * 0.06f, -h * 0.29f),
                Offset(w * 0.16f, -h * 0.25f),
                Offset(w * 0.24f, -h * 0.18f)
            )

            // 1. Draw Active Balloons & Strings
            val handPoint = Offset(centerX, harnessY)
            for (i in 0 until 6) {
                val bCenter = Offset(
                    x = centerX + balloonOffsets[i].x + sin(wavePhase + i) * 3.dp.toPx(),
                    y = harnessY + balloonOffsets[i].y + cos(wavePhase + i) * 2.dp.toPx()
                )
                val balloonRadiusX = (w * 0.085f).coerceIn(13.dp.toPx(), 24.dp.toPx())
                val balloonRadiusY = balloonRadiusX * 1.18f

                if (i < balloonsRemaining) {
                    // Curvy string from stickman hand to balloon knot
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

                    // Balloon body
                    drawDoodleBalloon(
                        center = bCenter,
                        rx = balloonRadiusX,
                        ry = balloonRadiusY,
                        color = DoodleColors.BalloonPalette[i]
                    )
                } else if (i == lastPoppedBalloonIndex && popTrigger > 0 && popProgress.value < 0.95f) {
                    // Bursting balloon particles!
                    drawPoppingBalloonBurst(
                        center = bCenter,
                        progress = popProgress.value,
                        color = DoodleColors.BalloonPalette[i]
                    )
                }
            }

            // 2. Draw Stickman suspended from the strings
            drawDoodleStickman(
                handAnchor = handPoint,
                scale = (w / 420f).coerceIn(0.68f, 1.15f),
                isPanicking = balloonsRemaining in 1..2,
                isSplashed = balloonsRemaining <= 0 || isGameOver,
                wavePhase = wavePhase
            )

            // 3. Draw Shark (Jaws wide open if <= 2 balloons, or circling dorsal fin otherwise)
            val isJawsOpen = (!isMenuPreview && balloonsRemaining <= 2)
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

            // 4. Draw Animated Wavy Water Lines in Foreground
            drawDoodleWaterWaves(
                waterY = waterY,
                wavePhase = wavePhase
            )
        }

        // 5. Floating "-1 ❤️" Indicator on Wrong Guess
        if (popTrigger > 0 && popProgress.value < 0.95f) {
            val p = popProgress.value
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (36 - (p * 42)).dp)
                    .scale(1f + (1f - p) * 0.25f)
            ) {
                Text(
                    text = "-1 ❤️",
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    color = DoodleColors.WrongRed.copy(alpha = (1f - p).coerceIn(0f, 1f))
                )
            }
        }

        // 6. Speech Bubble when <= 2 balloons remain: "Come on, you can do that!"
        AnimatedVisibility(
            visible = showSpeechBubble && !isGameOver,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 2.dp, start = 4.dp, end = 4.dp)
        ) {
            SketchySpeechBubble(
                text = "Come on, you can do that!",
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

private fun DrawScope.drawDoodleBalloon(
    center: Offset,
    rx: Float,
    ry: Float,
    color: Color
) {
    // Oval fill
    drawOval(
        color = color,
        topLeft = Offset(center.x - rx, center.y - ry),
        size = Size(rx * 2f, ry * 2f)
    )

    // Glossy doodle highlight arc on top-left
    drawOval(
        color = Color.White.copy(alpha = 0.55f),
        topLeft = Offset(center.x - rx * 0.58f, center.y - ry * 0.62f),
        size = Size(rx * 0.42f, ry * 0.52f)
    )

    // Balloon knot triangle at bottom
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

    // Hand-drawn black outline
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
    wavePhase: Float
) {
    val strokeW = (3.2.dp.toPx() * scale).coerceAtLeast(2.5.dp.toPx())
    val headRadius = 13.dp.toPx() * scale

    val shoulderY = handAnchor.y + 22.dp.toPx() * scale
    val headCenter = Offset(handAnchor.x, shoulderY - headRadius - 4.dp.toPx() * scale)
    val hipY = shoulderY + 28.dp.toPx() * scale

    // Arms reaching up to hold the balloon strings at handAnchor
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

    // Right arm (either holding strings or waving in panic)
    if (isPanicking) {
        val waveArmY = shoulderY - 10.dp.toPx() * scale + sin(wavePhase * 3f) * 8.dp.toPx()
        drawLine(
            color = DoodleColors.InkBlack,
            start = Offset(handAnchor.x, shoulderY),
            end = Offset(handAnchor.x + 22.dp.toPx() * scale, waveArmY),
            strokeWidth = strokeW,
            cap = StrokeCap.Round
        )
    } else {
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
    }

    // Torso
    drawLine(
        color = DoodleColors.InkBlack,
        start = Offset(handAnchor.x, shoulderY),
        end = Offset(handAnchor.x, hipY),
        strokeWidth = strokeW,
        cap = StrokeCap.Round
    )

    // Legs kicking in air
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

    // Head (white fill + black outline)
    drawCircle(
        color = Color.White,
        radius = headRadius,
        center = headCenter
    )
    drawCircle(
        color = DoodleColors.InkBlack,
        radius = headRadius,
        center = headCenter,
        style = Stroke(width = strokeW * 0.85f)
    )

    // Facial Expression
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
        // Worried open "O" mouth + sweat drops
        drawCircle(
            color = DoodleColors.InkBlack,
            radius = 3.2.dp.toPx() * scale,
            center = Offset(headCenter.x, headCenter.y + headRadius * 0.35f),
            style = Stroke(width = 1.8.dp.toPx())
        )
        // Sweat drop
        drawCircle(
            color = DoodleColors.WaterBlue,
            radius = 2.5.dp.toPx() * scale,
            center = Offset(headCenter.x + headRadius + 5.dp.toPx(), headCenter.y - 4.dp.toPx())
        )
    } else {
        // Happy doodle smile
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

    // Outer shark snout rising out of water
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

    // Red inner mouth cavity
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

    // Sharp white jagged teeth along top & bottom of mouth
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

    drawPath(
        path = wavePath,
        color = DoodleColors.WaterBlue.copy(alpha = 0.35f)
    )

    // Bold hand-sketched top wave crest line
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
            // 1. Top splits into a chalkboard/dark header showing celebrating stickman + completed word & clue
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(DoodleColors.ChalkboardDark)
                    .padding(16.dp)
            ) {
                // Chalkboard subtle grid lines
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
                    // Celebrating Stickman Canvas on left
                    Canvas(
                        modifier = Modifier
                            .weight(0.42f)
                            .fillMaxHeight()
                    ) {
                        drawCelebratingStickmanOnChalkboard()
                    }

                    // Completed Word & Clue on right
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
                            fontSize = 28.sp,
                            letterSpacing = 3.sp,
                            color = DoodleColors.DoodleYellow,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = level.clue,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp,
                            lineHeight = 20.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // 2. Bottom white graph-paper card displays "WELL DONE!", stats, Chest/Theme bars, Next button
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

                    // Stats Row: Perfect: X, Streak: X, Max Streak: X
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        VictoryStatChip("Perfect: $perfectCount", fontFamily)
                        VictoryStatChip("Streak: $streak", fontFamily)
                        VictoryStatChip("Max Streak: $maxStreak", fontFamily)
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Progress Bar 1: Chest (X/4 progress towards a treasure chest reward)
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

                    // Progress Bar 2: Theme (X/10 progress towards unlocking a new visual theme)
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

                    // Large Yellow Next Button (->) to load the next level and award +25 coins
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
                                text = "NEXT  (+25 Coins)",
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

        // Progress Bar Container
        Box(
            modifier = Modifier
                .weight(1f)
                .height(26.dp)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val r = 12.dp.toPx()
                // White track
                drawRoundRect(
                    color = Color.White,
                    cornerRadius = CornerRadius(r, r)
                )
                // Filled progress
                drawRoundRect(
                    color = barColor,
                    size = Size(size.width * fraction, size.height),
                    cornerRadius = CornerRadius(r, r)
                )
                // Black hand-drawn border
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

        // Doodle Icon on the right
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

    // Confetti stars around stickman
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

    // Chalk white celebrating stickman with arms raised in "V" victory pose
    val chalk = Color.White
    val sw = 3.2.dp.toPx()
    val headCenter = Offset(cx, cy - 28.dp.toPx())

    drawCircle(color = chalk, radius = headR, center = headCenter, style = Stroke(width = sw))
    // Happy eyes & grin
    drawCircle(color = chalk, radius = 2.dp.toPx(), center = Offset(headCenter.x - 5.dp.toPx(), headCenter.y - 2.dp.toPx()))
    drawCircle(color = chalk, radius = 2.dp.toPx(), center = Offset(headCenter.x + 5.dp.toPx(), headCenter.y - 2.dp.toPx()))

    // Torso
    val neckY = headCenter.y + headR
    val hipY = neckY + 34.dp.toPx()
    drawLine(chalk, Offset(cx, neckY), Offset(cx, hipY), strokeWidth = sw, cap = StrokeCap.Round)

    // Raised cheering arms
    drawLine(chalk, Offset(cx, neckY + 8.dp.toPx()), Offset(cx - 24.dp.toPx(), neckY - 14.dp.toPx()), strokeWidth = sw, cap = StrokeCap.Round)
    drawLine(chalk, Offset(cx, neckY + 8.dp.toPx()), Offset(cx + 24.dp.toPx(), neckY - 14.dp.toPx()), strokeWidth = sw, cap = StrokeCap.Round)

    // Jumping legs
    drawLine(chalk, Offset(cx, hipY), Offset(cx - 18.dp.toPx(), hipY + 26.dp.toPx()), strokeWidth = sw, cap = StrokeCap.Round)
    drawLine(chalk, Offset(cx, hipY), Offset(cx + 18.dp.toPx(), hipY + 26.dp.toPx()), strokeWidth = sw, cap = StrokeCap.Round)
}

// ============================================================================
// 12. GAME OVER OVERLAY & TWO-PLAYER PASS-AND-PLAY SETUP
// ============================================================================

@Composable
fun GameOverOverlay(
    level: WordLevel,
    coins: Int,
    fontFamily: FontFamily,
    onRescue: () -> Unit,
    onRetry: () -> Unit,
    onMainMenu: () -> Unit
) {
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
                    text = "SPLASH! GAME OVER",
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 28.sp,
                    color = DoodleColors.WrongRed,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "All 6 balloons popped! The shark caught up.",
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

                // Rescue option (keep progress with +3 Balloons for 250 coins)
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
                        text = "Rescue (+3 Balloons • 250 Coins)",
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
    onStartGame: (word: String, clue: String) -> Unit
) {
    var secretWord by remember { mutableStateOf("") }
    var secretClue by remember { mutableStateOf("") }

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
                    text = "Player 1: Enter a secret word and clue, then hand the phone to Player 2!",
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = DoodleColors.InkBlack,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

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

                Spacer(modifier = Modifier.height(12.dp))

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

                Spacer(modifier = Modifier.height(20.dp))

                SketchyContainer(
                    fillColor = DoodleColors.DoodleYellow,
                    cornerRadius = 24.dp,
                    onClick = {
                        onStartGame(
                            secretWord.ifBlank { "SHARK" },
                            secretClue.ifBlank { "Ocean predator with sharp teeth" }
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

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ============================================================================
// 13. SETTINGS & COIN BONUS DIALOGS
// ============================================================================

@Composable
fun SettingsDoodleDialog(
    useCursive: Boolean,
    selectedTheme: PaperTheme,
    fontFamily: FontFamily,
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
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Notebook Settings",
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    color = DoodleColors.InkBlack
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Toggle Font Family
                SketchyContainer(
                    fillColor = Color.White,
                    cornerRadius = 18.dp,
                    onClick = onToggleFont,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = if (useCursive) "Font: Cursive Script (Tap to switch)" else "Font: Comic Bold Sans (Tap to switch)",
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = DoodleColors.InkBlack
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Graph Paper Theme:",
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = DoodleColors.InkBlack
                )

                Spacer(modifier = Modifier.height(8.dp))

                PaperTheme.entries.forEach { theme ->
                    val isSelected = theme == selectedTheme
                    SketchyContainer(
                        fillColor = if (isSelected) DoodleColors.DoodleYellow else theme.bg,
                        cornerRadius = 16.dp,
                        onClick = { onSelectTheme(theme) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .padding(vertical = 3.dp)
                    ) {
                        Text(
                            text = if (isSelected) "✓ ${theme.title}" else theme.title,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = DoodleColors.InkBlack
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                SketchyContainer(
                    fillColor = DoodleColors.DoodleYellow,
                    cornerRadius = 20.dp,
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
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
            // Hand-drawn star/coin icon
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

            // Yellow '+' circle badge
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

    // Small 5-point star inside coin
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

    // Left horn
    val leftHorn = Path().apply {
        moveTo(cx - r * 0.8f, cy - r * 0.2f)
        quadraticTo(cx - r * 1.35f, cy - r * 1.25f, cx - r * 0.35f, cy - r * 1.15f)
        close()
    }
    // Right horn
    val rightHorn = Path().apply {
        moveTo(cx + r * 0.8f, cy - r * 0.2f)
        quadraticTo(cx + r * 1.35f, cy - r * 1.25f, cx + r * 0.35f, cy - r * 1.15f)
        close()
    }
    drawPath(leftHorn, color = DoodleColors.WrongRed)
    drawPath(leftHorn, color = DoodleColors.InkBlack, style = Stroke(width = 1.6.dp.toPx()))
    drawPath(rightHorn, color = DoodleColors.WrongRed)
    drawPath(rightHorn, color = DoodleColors.InkBlack, style = Stroke(width = 1.6.dp.toPx()))

    // Little red devil head
    drawCircle(color = DoodleColors.WrongRed, radius = r, center = Offset(cx, cy))
    drawCircle(color = DoodleColors.InkBlack, radius = r, center = Offset(cx, cy), style = Stroke(width = 1.8.dp.toPx()))

    // Mischievous slanted eyes
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

        // Handle
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

        // Lens fill & rim
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
        // White '+' life cross inside shield
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

        // Bulb globe
        drawCircle(color = DoodleColors.HintBulbOrange, radius = r, center = bulbCenter)
        drawCircle(
            color = DoodleColors.InkBlack,
            radius = r,
            center = bulbCenter,
            style = Stroke(width = 2.2.dp.toPx())
        )

        // Screw base
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
        // Chest box
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
        // Band & Golden Lock
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
