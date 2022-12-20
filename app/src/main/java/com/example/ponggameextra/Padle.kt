package com.example.ponggameextra

import android.graphics.RectF

class Padle(val screenSizeX: Int, val screenSizeY: Int, setPadleTop: Boolean) {

    // RectF represent rectangle floating point coordinates
    val rect: RectF

    val padleLength: Float = screenSizeX / 6f
    val padleHeight: Float = screenSizeY / 50f

    // X is the far left of the rectangle which forms our bar and Y is the top coordinate
    var padlePositionX: Float = screenSizeX / 2f - padleLength / 2f
    val padlePositionY: Float = screenSizeY - 20f

    //How fast our padel is
    val padleSpeed: Float = screenSizeX * 2f

    // How the padle can move
    val STOPPED = 0
    val LEFT = 1
    val RIGHT = 2

    //  Is the padle moving and which direction
    var padleSpeedMovement = STOPPED

    // init block that is executed when a object of the class is created. Set the padel
    init {
        rect = if (setPadleTop) {
            RectF(padlePositionX, 35f, padlePositionX + padleLength, 35f + padleHeight)
        } else {
            RectF(
                padlePositionX,
                padlePositionY - 55f,
                padlePositionX + padleLength,
                padlePositionY + padleHeight - 55f
            )
        }
    }

    // Used to change the direction of the padle
    fun padleMovementState(state: Int) {
        padleSpeedMovement = state
    }

    //This will bew called in GameView, update if padle change position
    fun update(fps: Long) {
        if (padleSpeedMovement == LEFT) {
            padlePositionX = padlePositionX - padleSpeed / fps
        }
        if (padleSpeedMovement == RIGHT) {
            padlePositionX = padlePositionX + padleSpeed / fps

        // Padle can't leave the screen
        }
        if (rect.left < 0) {
            padlePositionX = 0f
        }
        if (rect.right > screenSizeX) {
            padlePositionX = screenSizeX - (rect.right - rect.left)
        }

        //Update padle graphics. Padel will be positioned horizontally on the screen.
        rect.left = padlePositionX
        rect.right = padlePositionX + padleLength
    }


}