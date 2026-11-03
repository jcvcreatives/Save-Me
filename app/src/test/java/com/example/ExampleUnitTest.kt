package com.example

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun perilType_rotatesEverySingleLevelAcrossAll15Perils() {
    assertEquals(PerilType.SHARK_BALLOONS, PerilType.forLevel(1))
    assertEquals(PerilType.UFO_ABDUCTION, PerilType.forLevel(2))
    assertEquals(PerilType.VOLCANO_BLOCKS, PerilType.forLevel(3))
    assertEquals(PerilType.GIANT_VENUS_FLYTRAP, PerilType.forLevel(4))
    assertEquals(PerilType.DINO_STEALTH, PerilType.forLevel(5))
    assertEquals(PerilType.LEAKY_SUBMARINE, PerilType.forLevel(6))
    assertEquals(PerilType.TIGHTROPE_SAW, PerilType.forLevel(7))
    assertEquals(PerilType.METEOR_SHOWER, PerilType.forLevel(8))
    assertEquals(PerilType.MAD_SCIENTIST_POTION, PerilType.forLevel(9))
    assertEquals(PerilType.PIRATE_PLANK, PerilType.forLevel(10))
    assertEquals(PerilType.SPIDER_WEB, PerilType.forLevel(11))
    assertEquals(PerilType.QUICKSAND_PIT, PerilType.forLevel(12))
    assertEquals(PerilType.GHOST_MANOR, PerilType.forLevel(13))
    assertEquals(PerilType.TICKING_TIME_BOMB, PerilType.forLevel(14))
    assertEquals(PerilType.DRAGON_CAVE, PerilType.forLevel(15))
    assertEquals(PerilType.SHARK_BALLOONS, PerilType.forLevel(16))
  }
}
