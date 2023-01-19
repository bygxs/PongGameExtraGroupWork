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
    //variable for sharedpref.
    private val sharedPrefFile = "HighScoreSaved"
    //Variable for the score
    var topScoreSaved = 0

    private lateinit var btnPlay: Button
    private lateinit var tvScore: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)

        // Load the resolution of the screen to screenWidth & screenHeight
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

    // "PLAY" button to choose single player or multiplayer.
    private fun startGame() {
        btnPlay.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Choose game mode")
            val modes = arrayOf("Single player", "Multiplayer")
            builder.setItems(modes) { _, which ->
                pongView.isMultiplayer = (which == 1)
                showDifficultySelectionDialog(which)
            }
            builder.show()
        }
    }

    // Set the difficult on the game, easy, normal or hard.
    private fun showDifficultySelectionDialog(mode: Int) {
        val difficultyBuilder = AlertDialog.Builder(this)
        difficultyBuilder.setTitle("Select Difficulty")
        val difficulties = arrayOf("Easy", "Normal", "Hard")
        difficultyBuilder.setItems(difficulties) { _, which ->
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("mode", mode)
            intent.putExtra("difficulty", which)

            startActivity(intent)
        }
        difficultyBuilder.show()
    }

    // get the score from sharedpref and set it to the textview "tvScore".
    private fun setScore() {
        val preferences = getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        topScoreSaved = preferences.getInt("topScore", 0)
        tvScore.text = topScoreSaved.toString()

    }

    // Set the pop animation for the image on the Homescreen
    private fun pongImageAnimation() {

        val homeScreen1: ConstraintLayout = findViewById(R.id.home_screen)

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