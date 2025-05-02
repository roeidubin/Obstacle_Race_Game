package com.example.exercise1

import android.content.Context
import android.os.*
import android.util.DisplayMetrics
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.exercise1.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentLane = 1
    private var screenWidth = 0
    private var screenHeight = 0
    private var lives = 3
    private var score = 0
    private var gameOver = false
    private val obstacleSize = 100
    private val handler = Handler(Looper.getMainLooper())
    private val obstacleInterval: Long = 1400

    private val scoreRunnable = object : Runnable {
        override fun run() {
            if (!gameOver) {
                score++
                updateScoreUI()
                handler.postDelayed(this, 1000)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        screenWidth = getScreenWidth()
        screenHeight = getScreenHeight()

        binding.btnLeft.setOnClickListener { movePlayer(-1) }
        binding.btnRight.setOnClickListener { movePlayer(1) }

        movePlayer(0)
        updateHeartsUI()
        updateScoreUI()

        startGame()
    }

    private fun movePlayer(direction: Int) {
        if (gameOver) return

        currentLane += direction
        currentLane = currentLane.coerceIn(0, 2)

        val laneWidth = screenWidth / 3
        val newX = laneWidth * currentLane + laneWidth / 2 - binding.player.width / 2

        binding.player.animate().x(newX.toFloat()).setDuration(150).start()
    }

    private fun startGame() {
        lives = 3
        score = 0
        gameOver = false
        updateHeartsUI()
        updateScoreUI()
        handler.post(scoreRunnable)
        spawnObstacles()
    }

    private fun spawnObstacles() {
        handler.post(object : Runnable {
            override fun run() {
                if (!gameOver) {
                    spawnObstacle()
                    handler.postDelayed(this, obstacleInterval)
                }
            }
        })
    }

    private fun spawnObstacle() {
        val lane = Random.nextInt(0, 3)
        val obstacle = ImageView(this)
        obstacle.setImageResource(R.drawable.stone)
        obstacle.layoutParams = ViewGroup.LayoutParams(obstacleSize, obstacleSize)

        val laneWidth = screenWidth / 3
        val startX = laneWidth * lane + laneWidth / 2 - obstacleSize / 2
        obstacle.x = startX.toFloat()
        obstacle.y = 0f

        binding.mainLayout.addView(obstacle)

        obstacle.animate()
            .y(screenHeight.toFloat())
            .setDuration(3000)
            .withEndAction {
                checkCollision(lane, obstacle)
            }
            .start()
    }

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

    private fun updateHeartsUI() {
        val hearts = listOf(
            findViewById<ImageView>(R.id.heart1),
            findViewById<ImageView>(R.id.heart2),
            findViewById<ImageView>(R.id.heart3)
        )
        for (i in hearts.indices) {
            if (i < lives) {
                hearts[i].setImageResource(R.drawable.heart)
            } else {
                hearts[i].setImageResource(android.R.color.transparent)
            }
        }
    }

    private fun updateScoreUI() {
        binding.scoreText.text = "Score: $score"
    }

    private fun vibrate() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(200)
        }
    }

    private fun getScreenWidth(): Int {
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        return metrics.widthPixels
    }

    private fun getScreenHeight(): Int {
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        return metrics.heightPixels
    }
}
