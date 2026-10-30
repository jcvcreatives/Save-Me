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
    fun `perilType rotates every level mod 4 and contextual panic lines match`() {
        assertEquals(PerilType.SHARK_BALLOONS, PerilType.forLevel(1))
        assertEquals(PerilType.UFO_ABDUCTION, PerilType.forLevel(2))
        assertEquals(PerilType.VOLCANO_BLOCKS, PerilType.forLevel(3))
        assertEquals(PerilType.DINO_STEALTH, PerilType.forLevel(4))
        assertEquals(PerilType.SHARK_BALLOONS, PerilType.forLevel(5))

        assertEquals("Not the alien probe!", PerilType.UFO_ABDUCTION.panicBubbleText)
        assertEquals("My shoes are melting!", PerilType.VOLCANO_BLOCKS.panicBubbleText)
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

        // Level 3: VOLCANO_BLOCKS -> Wrong guess plays CRUNCH
        vm.selectCampaignLevelNumber(3)
        vm.onLetterTapped('Z')
        assertEquals(DoodleSoundEffect.CRUNCH, ProceduralSoundEngine.lastPlayedEffect)
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
