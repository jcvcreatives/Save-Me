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
    fun `perilType rotates every 20 levels across all 15 perils and 20 rank tiers span 1000 levels`() {
        // 15 Peril Scenarios rotating every 20 levels
        assertEquals(PerilType.SHARK_BALLOONS, PerilType.forLevel(1))
        assertEquals(PerilType.SHARK_BALLOONS, PerilType.forLevel(20))
        assertEquals(PerilType.UFO_ABDUCTION, PerilType.forLevel(21))
        assertEquals(PerilType.VOLCANO_BLOCKS, PerilType.forLevel(41))
        assertEquals(PerilType.GIANT_VENUS_FLYTRAP, PerilType.forLevel(61))
        assertEquals(PerilType.DINO_STEALTH, PerilType.forLevel(81))
        assertEquals(PerilType.LEAKY_SUBMARINE, PerilType.forLevel(101))
        assertEquals(PerilType.TIGHTROPE_SAW, PerilType.forLevel(121))
        assertEquals(PerilType.METEOR_SHOWER, PerilType.forLevel(141))
        assertEquals(PerilType.MAD_SCIENTIST_POTION, PerilType.forLevel(161))
        assertEquals(PerilType.PIRATE_PLANK, PerilType.forLevel(181))
        assertEquals(PerilType.SPIDER_WEB, PerilType.forLevel(201))
        assertEquals(PerilType.QUICKSAND_PIT, PerilType.forLevel(221))
        assertEquals(PerilType.GHOST_MANOR, PerilType.forLevel(241))
        assertEquals(PerilType.TICKING_TIME_BOMB, PerilType.forLevel(261))
        assertEquals(PerilType.DRAGON_CAVE, PerilType.forLevel(281))
        assertEquals(PerilType.DRAGON_CAVE, PerilType.forLevel(300))
        // Cycles cleanly through 1,000 levels
        assertEquals(PerilType.SHARK_BALLOONS, PerilType.forLevel(301))

        // 20-Tier Rank Titles & Tier Progression Card Helpers across 1..1000
        assertEquals(20, RankTier.entries.size)
        val tier1 = RankTier.forLevel(1)
        assertEquals("Doodle Novice", tier1.title)
        assertEquals("Tier 1: Doodle Novice", tier1.displayTierTitle)
        assertEquals("Progress: Level 1 / 50 (Next Tier at Lv 51)", tier1.progressSubLabel(1))
        assertEquals(0.02f, tier1.tierProgressFraction(1), 0.001f)
        assertEquals(1.0f, tier1.tierProgressFraction(50), 0.001f)

        val tier2 = RankTier.forLevel(51)
        assertEquals("Tier 2: Ink Apprentice", tier2.displayTierTitle)
        assertEquals("Progress: Level 51 / 100 (Next Tier at Lv 101)", tier2.progressSubLabel(51))

        assertEquals("Sketch Master", RankTier.forLevel(101).title)
        assertEquals("Graph Paper Wizard", RankTier.forLevel(151).title)
        assertEquals("Epic Doodle Legend", RankTier.forLevel(201).title)
        assertEquals("Mythic Ink God", RankTier.forLevel(251).title)
        assertEquals("Dimension Sketcher", RankTier.forLevel(301).title)
        assertEquals("Infinity Paper Sovereign", RankTier.forLevel(451).title)

        val tier20 = RankTier.forLevel(951)
        assertEquals("Tier 20: The Supreme Doodle God", tier20.displayTierTitle)
        assertEquals("Progress: Level 1000 / 1000 (Max Rank Tier!)", RankTier.forLevel(1000).progressSubLabel(1000))

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

        // Level 41: VOLCANO_BLOCKS -> Wrong guess plays CRUNCH
        vm.selectCampaignLevelNumber(41)
        vm.onLetterTapped('Z')
        assertEquals(DoodleSoundEffect.CRUNCH, ProceduralSoundEngine.lastPlayedEffect)
    }

    @Test
    fun `campaign completion counter increments across 1000 campaign levels and frozen vowels thaw after 3 consonants`() {
        val vm = WordGameViewModel()
        assertEquals(0, vm.completedCampaignCount)
        assertEquals(1000, vm.totalCampaignCount)

        // Solve Level 1 (DIAMOND)
        vm.selectCampaignLevelNumber(1)
        "DIAMOND".toSet().forEach { vm.onLetterTapped(it) }
        assertEquals(RoundOutcome.VICTORY, vm.roundOutcome)
        assertEquals(1, vm.completedCampaignCount)

        // Level 201 has Frozen Vowels until 3 consonants are guessed
        vm.selectCampaignLevelNumber(201)
        assertTrue(vm.areVowelsFrozen)
        val targetWord = vm.activeLevel.word.uppercase()
        val firstVowel = targetWord.first { it in WordGameViewModel.VOWELS }
        vm.onLetterTapped(firstVowel) // Frozen vowel cannot be guessed yet
        assertTrue(firstVowel !in vm.revealedWordChars)

        // Guess 3 unique consonants from the word -> Vowels thaw!
        val threeConsonants = targetWord.filter { it in 'A'..'Z' && it !in WordGameViewModel.VOWELS }.toSet().take(3)
        threeConsonants.forEach { vm.onLetterTapped(it) }
        assertEquals(false, vm.areVowelsFrozen)
        vm.onLetterTapped(firstVowel)
        assertTrue(firstVowel in vm.revealedWordChars)
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
