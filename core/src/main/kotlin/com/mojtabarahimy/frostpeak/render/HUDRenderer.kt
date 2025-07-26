package com.mojtabarahimy.frostpeak.render

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.FitViewport
import com.mojtabarahimy.frostpeak.entities.tools.ToolInventory
import com.mojtabarahimy.frostpeak.time.GameClock
import com.mojtabarahimy.frostpeak.util.Constants

class HUDRenderer(
    private val clock: GameClock
) {
    private val uiCamera = OrthographicCamera()
    private val uiViewport: FitViewport =
        FitViewport(Constants.worldWidth, Constants.worldHeight, uiCamera)
    private val batch = SpriteBatch()

    private val selectedInventorySlot = Texture("inventory/selected_inventory_slot.png")

    private val font = BitmapFont().apply {
        color = Color.WHITE
    }

    private val projectionVector = Vector3()

    fun render(inventory: ToolInventory, playerY: Float) {
        uiCamera.update()
        batch.projectionMatrix = uiCamera.combined
        batch.begin()
        clock.draw(batch, font)
        drawInventory(inventory, playerY)
        batch.end()
    }


    fun resize(width: Int, height: Int) {
        uiViewport.update(width, height)
    }

    fun dispose() {
        batch.dispose()
    }

    private fun drawInventory(inventory: ToolInventory, playerY: Float) {
        val playerScreenY = uiCamera.project(projectionVector.set(0f, playerY, 0f)).y
        val inventoryX = -Constants.worldWidth / 2f + 32f
        val inventoryY =
            if (playerScreenY < Constants.worldHeight / 1.33f) Constants.worldHeight / 4f
            else -Constants.worldHeight / 2f + 32f
        batch.draw(inventory.texture, inventoryX, inventoryY)

        inventory.tools.forEachIndexed { index, tool ->
            val x = inventoryX + 22f + index * 44f
            val y = inventoryY + inventory.texture.height - 48f

            batch.draw(tool.texture, x, y)

            if (inventory.selectedTool?.name == tool.name) {
                batch.draw(selectedInventorySlot, x - 7, y - 7)
            }
        }
    }

}
