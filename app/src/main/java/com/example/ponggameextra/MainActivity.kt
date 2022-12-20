package com.example.ponggameextra

import android.content.Context
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
    var topScoreSaved = 2


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

        // Initialize pongView and set it as the view
        pongView = PongView(this, screenWidth, screenHeight)
        pongView.topScore = topScoreSaved
        setContentView(pongView)
    }

    override fun onResume() {
        super.onResume()

        // Get the topscore
        val sharedPreferences = this.getPreferences(Context.MODE_PRIVATE)
        topScoreSaved = sharedPreferences.getInt("top", 0)
        pongView.topScore = topScoreSaved

        // Tell the pongView resume method to execute
        pongView.resume()
    }

    override fun onPause(){
        super.onPause()

        // Saving topscore
        val sharedPreferences = this.getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        if (pongView.topScore <= pongView.score) {
            if (topScoreSaved < pongView.score) editor.putInt("top", pongView.score)
            }else {
                if (topScoreSaved < pongView.topScore) editor.putInt("top", pongView.topScore)
        }
        editor.apply()

        // Tell the pongView pause method to execute
        pongView.pause()
    }
}