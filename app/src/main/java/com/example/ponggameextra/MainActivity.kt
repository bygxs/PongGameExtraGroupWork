package com.example.ponggameextra

import android.content.Context
import android.content.SharedPreferences.Editor
import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.graphics.Paint
import android.graphics.Point
import android.hardware.display.DisplayManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Size
import android.view.Display
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.ponggameextra.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var pongView: PongView
    //variable for sharedpref.
    private val sharedPrefFile = "HighScoreSaved"
    //Variable for the score
    var topScoreSaved = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hide statusBar
        WindowInsetsControllerCompat(
            window,
            window.decorView
        ).hide(WindowInsetsCompat.Type.statusBars())

        // Only use the device in Portrait Mode
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Load the resolution of the screen to screenWidth & screenHeight
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        val mode = intent.getIntExtra("mode", 0)

        // Initialize pongView and set it as the view
        pongView = PongView(this, screenWidth, screenHeight)
        //pongView.topScore = topScoreSaved

        //Game mode for multiplayer = 1.
        pongView.isMultiplayer = (mode == 1)

        //Receive the difficult and tell the pongView which speed the ball will have.
        val which = intent.getIntExtra("difficulty", 1)
        when (which) {
            0 -> {
                // Easy mode
                pongView.pongBall.difficultSpeed = 1f
            }
            1 -> {
                //Normal mode
                pongView.pongBall.difficultSpeed = 1.05f
            }
            2 -> {
                //Hard mode
                pongView.pongBall.difficultSpeed = 1.1f
            }
        }

        setContentView(pongView)
    }

    // Trying to save the highest score with fun "saveTopScore" when it's onResume and onPause.
    override fun onResume() {
        super.onResume()
        saveTopScore(topScoreSaved)
        pongView.resume()
    }

    override fun onPause() {
        super.onPause()
        saveTopScore(topScoreSaved)
        pongView.pause()
    }

    //Save the highest score with sharedpref.
    fun saveTopScore(score: Int) {
        val preferences = getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val savedScore = preferences.getInt("topScore", 0)
        if (score > savedScore) {
            topScoreSaved = score
            val editor = preferences.edit()
            editor.putInt("topScore", topScoreSaved)
            editor.apply()
        }

    }
}


