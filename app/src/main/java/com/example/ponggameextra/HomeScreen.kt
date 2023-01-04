package com.example.ponggameextra

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout

class HomeScreen : AppCompatActivity() {

    lateinit var pongView: PongView

    private lateinit var btnPlay: Button
    private lateinit var tvScore: TextView
    private lateinit var mainActivity: MainActivity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)

        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels
        pongView = PongView(this, screenWidth, screenHeight)


        btnPlay = findViewById(R.id.btn_play)
        tvScore = findViewById(R.id.tv_score)

        // Function for image animation, set score and start the game with "PLAY" button
        pongImageAnimation()
        setScore()
        startGame()
    }

    // "PLAY" button to start the game
    private fun startGame() {
        btnPlay.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Choose game mode")
            val modes = arrayOf("Single player", "Multiplayer")
            builder.setItems(modes) { _, which ->

                pongView.isMultiplayer = (which == 1)

                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("mode", which)
                startActivity(intent)
            }

            builder.show()
        }
    }

    private fun setScore() {
        val sharedPreferences = this.getSharedPreferences("topScores", Context.MODE_PRIVATE)
        val topScore = sharedPreferences.getInt("top", 0)
        tvScore.text = topScore.toString()

    }

    // Set the pop animation for the image on the Homescreen
    private fun pongImageAnimation() {

        val homeScreen1 : ConstraintLayout = findViewById(R.id.home_screen)

        homeScreen1.animate()
            .scaleX(1.2f)
            .scaleY(1.2f)
            .setDuration(500)
            .withEndAction {

                homeScreen1.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(500)
                    .start()
            }
            .start()

        //Add animation to play button
        val originalColor = btnPlay.backgroundTintList?.defaultColor
        val animator = ValueAnimator.ofArgb(Color.WHITE, originalColor!!)
        animator.duration = 1000
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.REVERSE

        animator.addUpdateListener {
            val color = it.animatedValue as Int
            btnPlay.setBackgroundColor(color)
        }

        animator.start()

    }


}