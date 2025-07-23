package com.mojtabarahimy.frostpeak.render

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
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

    fun render(inventory: ToolInventory) {
        uiCamera.update()
        batch.projectionMatrix = uiCamera.combined
        batch.begin()
        clock.draw(batch, font)
        drawInventory(inventory)
        batch.end()
    }


    fun resize(width: Int, height: Int) {
        uiViewport.update(width, height)
    }

    fun dispose() {
        batch.dispose()
    }

    private fun drawInventory(inventory: ToolInventory) {
        val inventoryX = -inventory.texture.width / 2f
        val inventoryY = -Constants.worldHeight / 2f + 32f
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
