# Doodle Word Shark 🦈🎈🛸🌋🦖

A hand-drawn **Graph Paper Doodle** word guessing game built with **Kotlin** and **Jetpack Compose** where every graphic—from the notebook grid to the stickman, balloons, shark jaws, UFO tractor beam, volcano stone crates, and sleeping T-Rex—is rendered procedurally on the Compose `Canvas`.

## Features

- **Dynamic Rotating Perils (`PerilType`):**
  1. **Shark & Balloons (Levels 1, 5, 9...):** Stickman held aloft by 6 colorful balloons above a circling shark fin that opens its jaws when $\le 2$ lives remain.
  2. **UFO Abduction (Levels 2, 6, 10...):** Stickman pulled step-by-step upward through a glowing tractor beam toward a hovering flying saucer.
  3. **Volcano Crates (Levels 3, 7, 11...):** Stickman balancing on 6 sketched stone crates over bubbling molten lava.
  4. **Sleeping T-Rex (Levels 4, 8, 12...):** Stickman tiptoeing beside a sleeping T-Rex with a 6-bar **Noise Meter** (`Zzz...` $\rightarrow$ `CLANG!`).
- **Boss Levels (Every 5th Level & Adventure Mode):** 3 wrong letters on the keyboard are marked with a red bomb dot (`-2 ❤️` penalty if tapped).
- **Contextual Panic Speech Bubbles:** Hazard-specific comic speech bubbles when $\le 2$ lives remain.
- **Two-Player Pass & Play Duel:** Enter a custom secret word, clue, and hazard for a friend to solve.

## Getting the APK / Running the Project

### Option 1: Download Built APK from GitHub Actions
Every push to `main` (or manual trigger in the **Actions** tab) runs `.github/workflows/main.yml` and produces a ready-to-install **`Doodle-Word-Shark-debug-apk`** artifact.

### Option 2: Open in Android Studio
1. Clone this repository:
   ```bash
   git clone <your-repo-url>
   ```
2. Open the folder in **Android Studio** and click **Run (`app`)**.
