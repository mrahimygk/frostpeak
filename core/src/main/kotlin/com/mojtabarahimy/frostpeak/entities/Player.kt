package com.mojtabarahimy.frostpeak.entities

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion

class Player(val texture: Texture) {

    enum class Direction { DOWN, UP, LEFT, RIGHT }

    private val frameCols = 4
    private val frameRows = 4

    private val frames: Array<Array<TextureRegion>> = TextureRegion.split(
        texture,
        texture.width / frameCols,
        texture.height / frameRows
    )

    private val walkAnimations = mapOf(
        Direction.DOWN to Animation(
            0.2f,
            frames[0].copyOfRange(1, 4),
            Animation.PlayMode.LOOP_PINGPONG
        ),
        Direction.LEFT to Animation(
            0.2f,
            frames[1].copyOfRange(1, 4),
            Animation.PlayMode.LOOP_PINGPONG
        ),
        Direction.RIGHT to Animation(
            0.2f,
            frames[2].copyOfRange(1, 4),
            Animation.PlayMode.LOOP_PINGPONG
        ),
        Direction.UP to Animation(
            0.2f,
            frames[3].copyOfRange(1, 4),
            Animation.PlayMode.LOOP_PINGPONG
        ),
    )

    private val idleFrames = mapOf(
        Direction.DOWN to frames[0][0],
        Direction.LEFT to frames[1][0],
        Direction.RIGHT to frames[2][0],
        Direction.UP to frames[3][0],
    )

    var x = 100f
    var y = 100f
    private val width = texture.width.toFloat()
    private val height = texture.height.toFloat()
    private var stateTime = 0f
    private var currentDirection = Direction.DOWN
    private var moving = false

    fun update(dx: Float, dy: Float) {
        x += dx
        y += dy
    }

    fun draw(batch: SpriteBatch) {
        batch.draw(texture, x, y)
    }

    fun getCameraFocusX(): Float = x + width / 2f
    fun getCameraFocusY(): Float = y + height / 2f
}
