package com.example.ponggameextra

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.provider.SyncStateContract.Helpers.update
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.content.ContextCompat


// PongView extends SurfaceView & Runnable.
// Runnable interface has a method run that we can execute in our thread
class PongView(context: Context, var screenSizeX: Int, var screenSizeY: Int) : SurfaceView(context),
    Runnable {

    // Our thread so we can run and do multiple task simultaneously
    var gameThread: Thread? = null

    //ourHolder used to get a reference to the surface on which the view is drawn
    private var ourHolder: SurfaceHolder = holder

    // A boolean which we will set and unset when the game is running or not.
    var running: Boolean = false
    private var paused = true

    // This variable tracks the game frame rate
    var mFPS: Long = 0

    // Create Upper & Lower bar, false = bottom padel
    var playerPadleBottom: Padle = Padle(screenSizeX, screenSizeY, false)

    // Create a pongball
    var pongBall: Ball = Ball(screenSizeX, screenSizeY)

    // Stats
    var score = 0
    var topScore = 0
    var topLives = 3
    var botLives = 3


    private fun restart() {
        //Put the pongball back to the start
        pongBall.reset(screenSizeX, screenSizeY)

        // Reset scores and lives if it's game over
        if (topLives == 0 || botLives == 0) {
            if (score > topScore) topScore = score
            score = 0
            topLives = 3
            botLives = 3
            pongBall.resetBallSpeed()
        }
    }


    // Run method that extends Thread class and implements the Runnable interface
    override fun run() {
        while (running) {
            // Capture the current time in milliseconds in startFps
            val startFps = System.currentTimeMillis()

            // Update the frame
            if (!paused) update()

            // Draw the frame
            draw()

            // Calculate FPS. mFPS variable will be updated only if the time take to update
            //and draw the frame is at least 1 millisecond, which helps to ensure that the FPS
            //is not artificially inflated by very fast frame time.
            val timeThisFrame = System.currentTimeMillis() - startFps
            if (timeThisFrame >= 1) {
                mFPS = 1000 / timeThisFrame
            }
        }
    }

    // Everything that needs to be updated
    fun update() {

        // Move the padle
        playerPadleBottom.update(mFPS)
        pongBall.update(mFPS)

        // PongBall hits bot padle. If ball hits the padel we update the ball speed and position and
        // and increase our score with +1
        if (RectF.intersects(playerPadleBottom.rect, pongBall.rect)) {
            pongBall.setRandomBallSpeedX()
            pongBall.reverseBallSpeedY()
            pongBall.clearObstacleY(playerPadleBottom.rect.top - 2)

            score++
            pongBall.increaseBallSpeed()
        }

        // Pongball hits bottom wall. If ball hit the bottom wall and goes out of "screenSizeY"
        //we use "clearObstacleY" to move the ball to the start position. Then we lose 1 of 3 life.
        // If we got 0 lives the game pause and we can restart it agian
        if (pongBall.rect.bottom > screenSizeY) {
            pongBall.reverseBallSpeedY()
            pongBall.clearObstacleY(screenSizeY - 2f)

            // Lose a life
            botLives--
            if (botLives == 0) {
                paused = true
            }
            pongBall.setRandomBallSpeedX()
            restart()

        }

        //PongBall hits top wall, increase ball speed every time.
        // clearObstacle is used to move the ball back to a valid position of the screen so that it
        // doesn't get stuck .
        if (pongBall.rect.top < 0) {
            pongBall.reverseBallSpeedY()
            pongBall.clearObstacleY(12f)
            pongBall.increaseBallSpeed()
        }

        //PongBall hits left wall
        if (pongBall.rect.left < 0) {
            pongBall.reverseBallSpeedX()
            pongBall.clearObstacleX(2f)

        }

        // PongBall hits right wall
        if (pongBall.rect.right > screenSizeX) {
            pongBall.reverseBallSpeedX()
            pongBall.clearObstacleX((screenSizeX - 22).toFloat())
        }

    }

    //for us to use are own colors
    // Draw the newly updated scene
    @SuppressLint("ResourceAsColor")
    private fun draw() {

        if (ourHolder.surface.isValid) {
            val canvas = ourHolder.lockCanvas()
            val colorObject = Paint()

            // Background color
            canvas.drawColor(R.color.background)

            //Padle color
            colorObject.color = Color.WHITE
            canvas.drawRect(playerPadleBottom.rect, colorObject)
            canvas.drawOval(pongBall.rect, colorObject)

            // Score Style
            val scoreViewStyle = Paint()
            scoreViewStyle.color = ContextCompat.getColor(context, R.color.text_color)
            scoreViewStyle.textSize = 200f

            val topScoreViewStyle = Paint()
            topScoreViewStyle.color = ContextCompat.getColor(context, R.color.text_color)
            topScoreViewStyle.textSize = 40f

            val livesViewStyle = Paint()
            livesViewStyle.color = ContextCompat.getColor(context, R.color.text_color)
            livesViewStyle.textSize = 40f

            // Draw the score
             canvas.drawText(
                "$score",
                screenSizeX / 2f - 53f,
                screenSizeY / 2f,
                scoreViewStyle

            )
            canvas.drawText(
                "Top score: $topScore",
                screenSizeX / 2f - 115f,
                screenSizeY / 2f + 45f,
                topScoreViewStyle
            )
            canvas.drawText(
                "Lives: $botLives",
                screenSizeX - (screenSizeX / 40f) - 140f,
                screenSizeY - (screenSizeY / 50f), livesViewStyle

            )
            // Draw everything on the screen
            ourHolder.unlockCanvasAndPost(canvas)

        }
    }

    // If GameView is paused/stopped. Turn off our thread with help of "join". Added a try catch
    //so we can handle the possibility of an InterruptedException.
    fun pause() {
        running = false
        try {
            gameThread!!.join()
        } catch (e: InterruptedException) {
            Log.e("Error:", "joining thread")
        }
    }

    // If GameView start. Start our thread
    fun resume() {
        running = true
        gameThread = Thread(this)
        gameThread!!.start()
    }

// What happen when we touch the screen. MotionEvent = represent an input event that is generated
    //by a touch.
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        paused = false
    // If this y-coordinate is greater than the height of the screen divided by 2, it means that the
    // touch occurred on the bottom half of the screen
        for (i in 0 until event.pointerCount) {
            if (event.getY(i) > height / 2) {

                //If the x-coordinate is greater than the screen size in the x-axis divided by
                //2, it means that the touch occurred on the right half of the screen
                if (event.getX(i) > screenSizeX / 2) {
                    playerPadleBottom.padleMovementState(playerPadleBottom.RIGHT)

                    //If the x-coordinate is not greater than the screen size in the x-axis divided by
                    //2, it means that the touch occurred on the left half of the screen
                } else {
                    playerPadleBottom.padleMovementState(playerPadleBottom.LEFT)
                }

                // ACTION_POINTER_UP & ACTION_UP is when we not touch the screen. If we not touch
                // we want the padel be set to "STOPPED".
                when (event.actionMasked) {
                    MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_UP -> {
                        playerPadleBottom.padleMovementState(playerPadleBottom.STOPPED)
                    }
                }
            }


        }
        return true
    }




}