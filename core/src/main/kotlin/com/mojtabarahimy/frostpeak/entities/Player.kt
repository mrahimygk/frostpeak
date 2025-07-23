package com.mojtabarahimy.frostpeak.entities

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.mojtabarahimy.frostpeak.collision.CollisionSystem
import com.mojtabarahimy.frostpeak.entities.tools.ToolInventory
import com.mojtabarahimy.frostpeak.entities.tools.ToolTarget
import com.mojtabarahimy.frostpeak.util.Constants

class Player(
    val texture: Texture,
    val walkSound: Sound,
    private val collisionSystem: CollisionSystem
) {

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

    private val collisionBounds = Rectangle()

    private val toolInventory = ToolInventory()

    fun update(delta: Float, dx: Float, dy: Float) {
        isMoving = dx != 0f || dy != 0f

        currentDirection =
            if (dx > 0) Direction.RIGHT
            else if (dx < 0) Direction.LEFT
            else if (dy > 0) Direction.UP
            else if (dy < 0) Direction.DOWN
            else currentDirection


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

        val newX = x + dx
        val newY = y + dy

        val w = downFrames[0].regionWidth
        val h = downFrames[0].regionHeight
        collisionBounds.set(Rectangle(newX + w / 4f, newY + h / 6f, w / 2f, h / 6f))
        if (!collisionSystem.checkCollision(collisionBounds)) {
            x = newX
            y = newY
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

    fun drawDebug(shapeRenderer: ShapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.MAGENTA

        shapeRenderer.rect(
            collisionBounds.x,
            collisionBounds.y,
            collisionBounds.width,
            collisionBounds.height
        )

        shapeRenderer.end()
    }

    fun getInteractionBounds(): Rectangle {
        val interactionRange = 10f
        collisionBounds.run {
            return when (currentDirection) {
                Direction.UP -> Rectangle(x, y + height, width, interactionRange)
                Direction.DOWN -> Rectangle(x, y - interactionRange, width, interactionRange)
                Direction.LEFT -> Rectangle(x - interactionRange, y, interactionRange, height)
                Direction.RIGHT -> Rectangle(x + width, y, interactionRange, height)
            }
        }
    }

    fun getCameraFocusX(): Float = x + Constants.PLAYER_WIDTH / 2f
    fun getCameraFocusY(): Float = y + Constants.PLAYER_HEIGHT / 2f

    fun setPosition(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    fun switchToolForward() {
        toolInventory.nextTool()
    }

    fun switchToolBackward() {
        toolInventory.previousTool()
    }

    fun useTool(targets: List<ToolTarget>) {
        toolInventory.useSelectedTool(detectToolTarget(targets))
    }

    private fun detectToolTarget(objects: List<ToolTarget>): ToolTarget? {
        val detectionDistance = 32f
        val playerFacingX = x + when (currentDirection) {
            Direction.LEFT -> -detectionDistance
            Direction.RIGHT -> detectionDistance
            else -> 0f
        }
        val playerFacingY = y + when (currentDirection) {
            Direction.UP -> detectionDistance
            Direction.DOWN -> -detectionDistance
            else -> 0f
        }

        return objects.firstOrNull {
            Vector2(it.x, it.y).dst(playerFacingX, playerFacingY) < 20f
        }
    }
}
