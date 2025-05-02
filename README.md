# 🚗 Obstacle Race Game (Android, Kotlin)

This is a simple 3-lane endless obstacle race game built as part of a university assignment using **Android Studio** and **Kotlin**.

The player controls a car that can move left or right to avoid falling obstacles. The game ends when the player loses all 3 lives. Score increases over time!

---

## 📱 Features

- 🚘 Player-controlled car with left/right movement
- 🪨 Random falling obstacles
- ❤️ 3 lives system using heart icons
- 🎯 Score system that increases every second
- 🔁 Automatic game reset after losing
- 📱 Fully responsive for mobile devices
- 🔊 Vibration on collision

---

## 🎮 Gameplay

![Gameplay Screenshot](https://github.com/user-attachments/assets/16c1e560-798a-4f14-9634-7bb0c18b8c63)

---

## 🧩 How It Works

The game logic is handled in `MainActivity.kt`.

Here’s the core collision detection logic:

```kotlin
private fun checkCollision(obstacleLane: Int, obstacle: ImageView) {
    val obstacleY = obstacle.y + obstacle.height
    val playerY = binding.player.y

    if (obstacleLane == currentLane && obstacleY >= playerY) {
        lives--
        updateHeartsUI()
        vibrate()
        Toast.makeText(this, "Crash! Lives left: $lives", Toast.LENGTH_SHORT).show()

        if (lives <= 0) {
            gameOver = true
            Toast.makeText(this, "💀 You Lose! Final Score: $score", Toast.LENGTH_LONG).show()
            handler.removeCallbacks(scoreRunnable)
            handler.postDelayed({ startGame() }, 2000)
        }
    }

    binding.mainLayout.removeView(obstacle)
}
