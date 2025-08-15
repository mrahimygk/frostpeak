package com.mojtabarahimy.frostpeak

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.mojtabarahimy.frostpeak.controller.dialog.DialogManager
import com.mojtabarahimy.frostpeak.controller.dialog.DialogStore
import com.mojtabarahimy.frostpeak.controller.npc.NpcScheduleStore
import com.mojtabarahimy.frostpeak.controller.quests.QuestManager
import com.mojtabarahimy.frostpeak.controller.quests.QuestStore
import com.mojtabarahimy.frostpeak.data.PlayerData
import com.mojtabarahimy.frostpeak.data.time.GameClock
import com.mojtabarahimy.frostpeak.entities.items.ItemInventory
import com.mojtabarahimy.frostpeak.entities.items.ItemStore
import com.mojtabarahimy.frostpeak.entities.tools.ToolInventory
import com.mojtabarahimy.frostpeak.entities.tools.ToolStore
import com.mojtabarahimy.frostpeak.music.MusicManager
import com.mojtabarahimy.frostpeak.render.DialogRenderer
import com.mojtabarahimy.frostpeak.render.HUDRenderer
import com.mojtabarahimy.frostpeak.render.WeatherSystem
import com.mojtabarahimy.frostpeak.render.WorldRenderer

class FrostPeakGame : ApplicationAdapter() {


    private lateinit var worldRenderer: WorldRenderer
    private lateinit var hudRenderer: HUDRenderer

    private val clock = GameClock()
    private val playerData: PlayerData = PlayerData(/*TODO: saved energy and money*/).apply {
        addMoney(500)
    }

    override fun create() {


        val dialogFont = BitmapFont()
        val dialogFrame = Texture("dialog/dialog_frame.png")
        val ninePatch = NinePatch(dialogFrame, 14, 25, 12, 9)

        val toolStore = ToolStore()
        val toolInventory = ToolInventory(toolStore)
        val itemStore = ItemStore()
        val itemInventory = ItemInventory(itemStore)

        val questStore = QuestStore(toolStore, itemStore)
        val questManager = QuestManager(questStore)

        val npcScheduleStore = NpcScheduleStore(clock)

        val dialogStore = DialogStore()
        val dialogRenderer = DialogRenderer(dialogFont, ninePatch)
        val dialogManager = DialogManager(dialogStore, dialogRenderer, clock, questManager)

        val weatherSystem = WeatherSystem()

        worldRenderer = WorldRenderer(
            clock,
            playerData,
            toolInventory,
            itemInventory,
            dialogManager,
            weatherSystem,
            questManager,
            npcScheduleStore
        )

        hudRenderer = HUDRenderer(
            clock,
            playerData,
            toolInventory,
            itemInventory,
            dialogRenderer,
            questManager
        )
    }

    override fun render() {
        val delta = Gdx.graphics.deltaTime
        Gdx.gl.glClearColor(0.5f, 0.8f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        clock.update(delta)

        worldRenderer.render(delta)
        hudRenderer.render(
            delta,
            worldRenderer.player.y,
            playerData
        )
    }

    override fun resize(width: Int, height: Int) {
        worldRenderer.resize(width, height)
        hudRenderer.resize(width, height)
    }

    override fun dispose() {
        worldRenderer.dispose()
        hudRenderer.dispose()
        MusicManager.dispose()
    }
}
