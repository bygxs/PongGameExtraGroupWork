package com.example.ponggameextra

import android.graphics.RectF
import java.util.*

// Ball takes 2 parameters. Used to initialize the ball's position.
class Ball(screenWidth: Int, screenHeight: Int) {

    // Access to the Rect
    val rect: RectF

    //Ball speed in x and y-axis.
    private var ballSpeedX: Float = 0f
    private var ballSpeedY: Float = 0f

    // Ball width and height to float
    private val ballWidth: Float
    private val ballHeight: Float

    // Variable of the screen height
    var screenYr = screenHeight

    // Init block is used to initialize objects. It is called after the object's primary constructor.
    init {
        //Ball is divided by 80 of your screen size
        ballWidth = screenWidth / 80f
        ballHeight = ballWidth

        // Start the ball travelling straight up at a quarter of the screen height per second
        ballSpeedY = screenHeight / 3f
        ballSpeedX = ballSpeedY

        //Initialize the rect so we can attach the ball to the surface
        rect = RectF()
    }

    //change the position each frame
    // This will move the ball in x and y-axis at a speed that is proportional to the value of
    // ballSpeedX & ballSpeedY and the frame rate of the game
    fun update(fps: Long) {
        rect.left = rect.left + ballSpeedX / fps
        rect.top = rect.top + ballSpeedY / fps
        rect.right = rect.left + ballWidth
        rect.bottom = rect.top - ballHeight
    }

    // Reverse the speed vertical
    fun reverseBallSpeedY() {
        ballSpeedY = -ballSpeedY
    }

    // Reverse the speed horizontal
    fun reverseBallSpeedX() {
        ballSpeedX = -ballSpeedX
    }

    // Here we set a random speed. Generate a number between 0 and 1. If the resulting value is 0,
    // the reverseBallSpeedX method is called so we can reverse the speed.
    fun setRandomBallSpeedX(){
        val randomSpeed = Random()
        val randomSpeedDecision = randomSpeed.nextInt(2)

        if (randomSpeedDecision == 0) {
            reverseBallSpeedX()
        }
    }

    fun increaseBallSpeed() {
        ballSpeedX += ballSpeedX / 40
        ballSpeedY += ballSpeedY /40
    }

    fun resetBallSpeed(){
        ballSpeedY = screenYr / 3f
        ballSpeedX = ballSpeedY
    }

    // Move the ball to a specific place on the y-axis
    fun clearObstacleY(y: Float){
        rect.bottom = y
        rect.top = y - ballWidth
    }

    // Move the ball to a specific place on the x-axis
    fun clearObstacleX(x: Float){
        rect.left = x
        rect.right = x + ballWidth
    }

    // When we reset we will place the ball on the middle of the screen
    fun reset(x: Int, y: Int) {
        rect.left = x / 2f
        rect.top = y / 2f
        rect.right = (x / 2f) + ballWidth
        rect.bottom = (y / 2f) - ballHeight
    }
}