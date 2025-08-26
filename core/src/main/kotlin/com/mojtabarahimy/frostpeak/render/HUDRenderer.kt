package com.mojtabarahimy.frostpeak.render

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.FitViewport
import com.mojtabarahimy.frostpeak.controller.quests.QuestManager
import com.mojtabarahimy.frostpeak.data.PlayerData
import com.mojtabarahimy.frostpeak.data.time.GameClock
import com.mojtabarahimy.frostpeak.entities.items.ItemInventory
import com.mojtabarahimy.frostpeak.entities.tools.ToolInventory
import com.mojtabarahimy.frostpeak.util.Constants

class HUDRenderer(
    private val clock: GameClock,
    private val playerData: PlayerData,
    private val toolInventory: ToolInventory,
    private val itemInventory: ItemInventory,
    private val dialogRenderer: DialogRenderer,
    private val questManager: QuestManager
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

    private val shapeRenderer = ShapeRenderer()

    fun render(
        delta: Float,
        playerY: Float,
        playerData: PlayerData
    ) {

        uiCamera.update()
        batch.projectionMatrix = uiCamera.combined
        shapeRenderer.projectionMatrix = uiCamera.combined

        dialogRenderer.handleInput()
        dialogRenderer.update(delta)

        batch.begin()
        drawToolInventory(toolInventory, playerY)
        drawItemInventory(itemInventory, playerY)
        clock.draw(batch, font)
        drawMoney(playerData)
        dialogRenderer.draw(batch, uiViewport.worldWidth, uiViewport.worldHeight)
        batch.end()

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        renderEnergyBar(batch, playerData)
        shapeRenderer.end()
    }

    fun resize(width: Int, height: Int) {
        uiViewport.update(width, height)
    }

    fun dispose() {
        batch.dispose()
    }

    private fun renderEnergyBar(batch: SpriteBatch, playerData: PlayerData) {
        val x = Constants.worldWidth / 3f
        val y = Constants.worldHeight / 3f + 40f
        val width = 100f
        val height = 10f

        val energyRatio = playerData.energy / playerData.maxEnergy

        shapeRenderer.color = Color.DARK_GRAY
        shapeRenderer.rect(x, y, width, height)

        shapeRenderer.color = Color.GREEN
        shapeRenderer.rect(x, y, width * energyRatio, height)
    }

    private fun drawToolInventory(inventory: ToolInventory, playerY: Float) {
        val playerScreenY = calculatePlayerScreenY(playerY)
        val inventoryY = calculateInventoryHeight(playerScreenY)
        val inventoryX = -Constants.worldWidth / 2f + 32f
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

    private fun drawItemInventory(inventory: ItemInventory, playerY: Float) {
        val playerScreenY = calculatePlayerScreenY(playerY)
        val inventoryY = calculateInventoryHeight(playerScreenY)
        val inventoryX = Constants.worldWidth / 2f - inventory.texture.width - 32f
        batch.draw(inventory.texture, inventoryX, inventoryY)

        inventory.getSlots().forEachIndexed { index, item ->
            val x = inventoryX + 12 + item.item.icon.regionWidth + index * 44f
            val y = inventoryY + inventory.texture.height / 2f - item.item.icon.regionHeight / 3f

            batch.draw(item.item.icon, x, y)

            if (item.quantity > 1) {
                font.draw(batch, "${item.quantity}", x + 14f, y + 4f)
            }

            /*if (inventory.selectedItem?.name == item.name) {
                batch.draw(selectedInventorySlot, x - 7, y - 7)
            }*/
        }
    }

    private fun calculateInventoryHeight(playerScreenY: Float): Float {
        return if (playerScreenY < Gdx.graphics.height / 1.33f) {
            Constants.worldHeight / 3.6f
        } else {
            -Constants.worldHeight / 2f + 32f
        }
    }

    private fun calculatePlayerScreenY(playerY: Float): Float {
        return uiCamera.project(projectionVector.set(0f, playerY, 0f)).y
    }

    private fun drawMoney(playerData: PlayerData) {
        val moneyText = "${playerData.money}G"
        font.draw(
            batch, moneyText, Constants.worldWidth / 3f,
            Constants.worldHeight / 3f - 10f
        )
    }

}
