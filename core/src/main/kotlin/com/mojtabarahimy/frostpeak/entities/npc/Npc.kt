package com.mojtabarahimy.frostpeak.entities.npc

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.mojtabarahimy.frostpeak.collision.BasicCollisionPerformer
import com.mojtabarahimy.frostpeak.collision.CollisionPerformer
import com.mojtabarahimy.frostpeak.collision.CollisionSystem
import com.mojtabarahimy.frostpeak.entities.Direction
import com.mojtabarahimy.frostpeak.entities.Person
import com.mojtabarahimy.frostpeak.entities.atTarget
import com.mojtabarahimy.frostpeak.render.Drawable
import com.mojtabarahimy.frostpeak.render.MultipleLayerDrawable
import com.mojtabarahimy.frostpeak.util.Constants
import com.mojtabarahimy.frostpeak.util.Constants.NPC_SPEED

open class Npc(
    private val inName: String,
    val texture: Texture,
    private val walkSound: Sound,
) : Person,
    Drawable by MultipleLayerDrawable(),
    CollisionPerformer by BasicCollisionPerformer() {

    override var hasIntroduced: Boolean = false
        get() = field //TODO("load from save file")
        set(value) {
            field = value

            //TODO: save
        }

    override val name: String
        get() = inName
    override val nameId: String
        get() = name

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

    override var x = 100f
    override var y = 100f
    private var stateTime = 0f
    override var currentDirection = Direction.DOWN
    private var isMoving = false

    private var footstepTimer = 0f
    private val footstepInterval = Constants.PLAYER_WALK_FRAME_DURATION * 2f

    val collisionBounds = Rectangle()

    override fun update(delta: Float, dx: Float, dy: Float, collisionSystem: CollisionSystem) {
        isMoving = dx != 0f || dy != 0f

        currentDirection =
            if (dx > 0) Direction.RIGHT
            else if (dx < 0) Direction.LEFT
            else if (dy > 0) Direction.UP
            else if (dy < 0) Direction.DOWN
            else currentDirection


        stateTime += delta

        updateFootstepTimer(delta)

        val newX = x + dx
        val newY = y + dy

        updatePosition(collisionSystem, newX, newY)
    }

    override fun moveToward(targetX: Float, targetY: Float, delta: Float, collisionSystem: CollisionSystem) {
        val dx = targetX - x
        val dy = targetY - y

        isMoving = dx != 0f || dy != 0f

        if (atTarget(targetX, targetY)) return

        val length = kotlin.math.sqrt(dx * dx + dy * dy)

        if (length != 0f) {
            val nx = dx / length
            val ny = dy / length

            currentDirection = when {
                kotlin.math.abs(nx) > kotlin.math.abs(ny) && nx > 0 -> Direction.RIGHT
                kotlin.math.abs(nx) > kotlin.math.abs(ny) && nx < 0 -> Direction.LEFT
                ny > 0 -> Direction.UP
                else -> Direction.DOWN
            }

            stateTime += delta

            updateFootstepTimer(delta)

            // Move toward target
            val newX = x + nx * NPC_SPEED * delta
            val newY = y + ny * NPC_SPEED * delta

            updatePosition(collisionSystem, newX, newY)
        }
    }

    private fun updateFootstepTimer(delta: Float) {
        if (isMoving) {
            footstepTimer += delta
            if (footstepTimer >= footstepInterval) {
                //walkSound.play(0.15f)
                footstepTimer = 0f
            }
        } else {
            footstepTimer = footstepInterval // reset to delay next play
        }
    }

    override fun draw(batch: SpriteBatch) {
        val frame: TextureRegion? = if (isMoving)
            walkAnimations[currentDirection]?.getKeyFrame(stateTime, true)
        else
            idleFrames[currentDirection]

        frame?.let {
            batch.draw(it, x, y)
        }
    }

    private fun calculateInteractionBounds(
        newX: Float,
        w: Int,
        newY: Float,
        h: Int
    ) = Rectangle(newX + w / 4f, newY + h / 6f, w / 2f, h / 6f)

    private fun updatePosition(collisionSystem: CollisionSystem, newX: Float, newY: Float) {
        val w = downFrames[0].regionWidth
        val h = downFrames[0].regionHeight
        collisionBounds.set(calculateInteractionBounds(newX, w, newY, h))
        collisionSystem.update(collisionBounds, this)
        if (!collisionSystem.checkCollision(collisionBounds, this)) {
            x = newX
            y = newY
        }
    }

    override fun drawDebug(shapeRenderer: ShapeRenderer) =
        drawDebug(shapeRenderer, collisionBounds)

    override fun getInteractionBounds(): Rectangle =
        getInteractionBounds(collisionBounds, currentDirection)
    override fun getPassiveInteractionBounds(): Rectangle =
        getPassiveInteractionBounds(collisionBounds)

    override fun getCameraFocusX(): Float = x + Constants.PLAYER_WIDTH / 2f

    override fun getCameraFocusY(): Float = y + Constants.PLAYER_HEIGHT / 2f

    override fun setPosition(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    override fun setDirection(direction: Direction) {
        currentDirection = direction
    }
}
