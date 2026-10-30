package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Word Shark", appName)
    }

    @Test
    fun `perilType maps across 6 worlds for 100 levels and contextual panic lines match`() {
        assertEquals(PerilType.SHARK_BALLOONS, PerilType.forLevel(1))
        assertEquals(PerilType.SHARK_BALLOONS, PerilType.forLevel(16))
        assertEquals(PerilType.UFO_ABDUCTION, PerilType.forLevel(17))
        assertEquals(PerilType.VOLCANO_BLOCKS, PerilType.forLevel(34))
        assertEquals(PerilType.GIANT_VENUS_FLYTRAP, PerilType.forLevel(51))
        assertEquals(PerilType.DINO_STEALTH, PerilType.forLevel(67))
        assertEquals(PerilType.LEAKY_SUBMARINE, PerilType.forLevel(84))
        assertEquals(PerilType.LEAKY_SUBMARINE, PerilType.forLevel(100))

        assertEquals("Beam me up... wait, no!", PerilType.UFO_ABDUCTION.panicBubbleText)
        assertEquals("My shoes are melting!", PerilType.VOLCANO_BLOCKS.panicBubbleText)
        assertEquals("Vegetarian option!", PerilType.GIANT_VENUS_FLYTRAP.panicBubbleText)
        assertEquals("Shhh! Don't wake Rex!", PerilType.DINO_STEALTH.panicBubbleText)
    }

    @Test
    fun `procedural sound engine triggers pop splash and crunch for respective hazards`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        ProceduralSoundEngine.init(context)
        val vm = WordGameViewModel()

        // Level 1: SHARK_BALLOONS -> Wrong guess plays POP, losing all lives plays SPLASH
        vm.selectCampaignLevelNumber(1)
        vm.onLetterTapped('Z')
        assertEquals(DoodleSoundEffect.POP, ProceduralSoundEngine.lastPlayedEffect)

        listOf('Q', 'X', 'J', 'K', 'V').forEach { vm.onLetterTapped(it) }
        assertEquals(RoundOutcome.GAME_OVER, vm.roundOutcome)
        assertEquals(DoodleSoundEffect.SPLASH, ProceduralSoundEngine.lastPlayedEffect)

        // Level 34: VOLCANO_BLOCKS -> Wrong guess plays CRUNCH
        vm.selectCampaignLevelNumber(34)
        vm.onLetterTapped('Z')
        assertEquals(DoodleSoundEffect.CRUNCH, ProceduralSoundEngine.lastPlayedEffect)
    }

    @Test
    fun `campaign completion counter increments across 100 campaign levels and frozen vowels thaw after 3 consonants`() {
        val vm = WordGameViewModel()
        assertEquals(0, vm.completedCampaignCount)
        assertEquals(100, vm.totalCampaignCount)

        // Solve Level 1 (DIAMOND)
        vm.selectCampaignLevelNumber(1)
        "DIAMOND".toSet().forEach { vm.onLetterTapped(it) }
        assertEquals(RoundOutcome.VICTORY, vm.roundOutcome)
        assertEquals(1, vm.completedCampaignCount)

        // Level 51 (HIMALAYAS) has Frozen Vowels until 3 consonants are guessed
        vm.selectCampaignLevelNumber(51)
        assertTrue(vm.areVowelsFrozen)
        vm.onLetterTapped('A') // Frozen vowel cannot be guessed yet
        assertTrue('A' !in vm.revealedWordChars)

        // Guess 3 consonants: H, M, L -> Vowels thaw!
        listOf('H', 'M', 'L').forEach { vm.onLetterTapped(it) }
        assertEquals(false, vm.areVowelsFrozen)
        vm.onLetterTapped('A')
        assertTrue('A' in vm.revealedWordChars)
    }

    @Test
    fun `boss level 5 generates 3 bomb letters that cost 2 lives`() {
        val vm = WordGameViewModel()
        vm.selectCampaignLevelNumber(5) // Level 5 is GIRAFFE (Boss Level)
        assertTrue(vm.activeLevel.isBossLevel)
        assertEquals(3, vm.bombLetters.size)
        assertEquals(6, vm.balloonsRemaining)

        // Tap one of the bomb letters -> costs 2 lives (6 -> 4)
        val bombChar = vm.bombLetters.first()
        vm.onLetterTapped(bombChar)
        assertEquals(4, vm.balloonsRemaining)
        assertEquals(2, vm.lastDamageAmount)
    }
}
