package com.mojtabarahimy.frostpeak.entities

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.mojtabarahimy.frostpeak.util.Constants

class Player(val texture: Texture, val walkSound: Sound) {

    enum class Direction { DOWN, UP, LEFT, RIGHT }

    private val frameCols = 4
    private val frameRows = 4

    private val frames = TextureRegion.split(
        texture,
        texture.width / frameCols,
        texture.height / frameRows
    )

    private val downFrames = frames[0]
    private val leftFrames = frames[1]
    private val rightFrames = frames[2]
    private val upFrames = frames[3]

    private val walkDown = Animation(Constants.PLAYER_WALK_FRAME_DURATION, *downFrames)
    private val walkLeft = Animation(Constants.PLAYER_WALK_FRAME_DURATION, *leftFrames)
    private val walkRight = Animation(Constants.PLAYER_WALK_FRAME_DURATION, *rightFrames)
    private val walkUp = Animation(Constants.PLAYER_WALK_FRAME_DURATION, *upFrames)

    private val walkAnimations = mapOf(
        Direction.DOWN to walkDown,
        Direction.UP to walkUp,
        Direction.LEFT to walkLeft,
        Direction.RIGHT to walkRight
    )

    private val idleFrames = mapOf(
        Direction.DOWN to frames[0][0],
        Direction.LEFT to frames[1][0],
        Direction.RIGHT to frames[2][0],
        Direction.UP to frames[3][0],
    )

    var x = 100f
    var y = 100f
    private var stateTime = 0f
    private var currentDirection = Direction.DOWN
    private var isMoving = false

    private var footstepTimer = 0f
    private val footstepInterval = Constants.PLAYER_WALK_FRAME_DURATION * 2f

    fun update(delta: Float, dx: Float, dy: Float) {
        isMoving = dx != 0f || dy != 0f

        currentDirection =
            if (dx > 0) Direction.RIGHT
            else if (dx < 0) Direction.LEFT
            else if (dy > 0) Direction.UP
            else if (dy < 0) Direction.DOWN
            else currentDirection

        x += dx
        y += dy

        stateTime += delta

        if (isMoving) {
            footstepTimer += delta
            if (footstepTimer >= footstepInterval) {
                walkSound.play(0.5f)
                footstepTimer = 0f
            }
        } else {
            footstepTimer = footstepInterval // reset to delay next play
        }
    }

    fun draw(batch: SpriteBatch) {
        val frame: TextureRegion? = if (isMoving)
            walkAnimations[currentDirection]?.getKeyFrame(stateTime, true)
        else
            idleFrames[currentDirection]

        frame?.let {
            batch.draw(it, x, y)
        }
    }

    fun getCameraFocusX(): Float = x + Constants.PLAYER_WIDTH / 2f
    fun getCameraFocusY(): Float = y + Constants.PLAYER_HEIGHT / 2f
}
