package com.mojtabarahimy.frostpeak.input

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.InputAdapter
import com.mojtabarahimy.frostpeak.entities.Player
import com.mojtabarahimy.frostpeak.entities.tools.ToolTarget

class PlayerInputProcessor(
    private val targets: List<ToolTarget>,
    private val player: Player,
    private val playerMovement: (delta: Float, dx: Float, dy: Float) -> Unit,
    private val onInteract: () -> Unit,
) : InputAdapter() {

    private var up = false
    private var down = false
    private var left = false
    private var right = false

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Keys.W, Keys.UP -> up = true
            Keys.D, Keys.RIGHT -> right = true
            Keys.S, Keys.DOWN -> down = true
            Keys.A, Keys.LEFT -> left = true
            Keys.E -> onInteract()
            Keys.Q -> player.switchToolBackward()
            Keys.R -> player.switchToolForward()
            Keys.Z -> player.useTool(targets)
        }

        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        when (keycode) {
            Keys.W, Keys.UP -> up = false
            Keys.D, Keys.RIGHT -> right = false
            Keys.S, Keys.DOWN -> down = false
            Keys.A, Keys.LEFT -> left = false
        }

        return true
    }

    fun update(delta: Float, speed: Float){
        var dx = 0f
        var dy = 0f
        if (up) dy += speed * delta
        if (down) dy -= speed * delta
        if (left) dx -= speed * delta
        if (right) dx += speed * delta
        playerMovement(delta, dx, dy)
    }
}
