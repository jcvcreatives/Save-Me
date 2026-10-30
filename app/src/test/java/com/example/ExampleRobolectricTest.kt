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
    fun `viewModel balloon pop and victory mechanics work`() {
        val vm = WordGameViewModel()
        vm.startCampaignLevel()
        assertEquals(6, vm.balloonsRemaining)
        assertEquals("DIAMOND", vm.activeLevel.word)

        // Wrong letter 'Z' pops 1 balloon
        vm.onLetterTapped('Z')
        assertEquals(5, vm.balloonsRemaining)
        assertEquals(KeyStatus.WRONG, vm.keyStatuses['Z'])

        // Solve DIAMOND
        listOf('D', 'I', 'A', 'M', 'O', 'N').forEach { ch ->
            vm.onLetterTapped(ch)
        }
        assertEquals(RoundOutcome.VICTORY, vm.roundOutcome)
        assertTrue(vm.revealedChars.contains('D'))
    }
}
