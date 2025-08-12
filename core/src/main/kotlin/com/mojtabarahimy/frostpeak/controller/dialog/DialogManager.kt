package com.mojtabarahimy.frostpeak.controller.dialog

import com.mojtabarahimy.frostpeak.controller.quests.QuestManager
import com.mojtabarahimy.frostpeak.data.quests.QuestDefinition
import com.mojtabarahimy.frostpeak.data.quests.QuestStatus
import com.mojtabarahimy.frostpeak.data.time.GameClock
import com.mojtabarahimy.frostpeak.data.time.Season
import com.mojtabarahimy.frostpeak.entities.Talkable
import com.mojtabarahimy.frostpeak.entities.items.ItemInventory
import com.mojtabarahimy.frostpeak.entities.tools.ChangeableState
import com.mojtabarahimy.frostpeak.entities.tools.ToolInventory
import com.mojtabarahimy.frostpeak.render.DialogRenderer

class DialogManager(
    private val dialogStore: DialogStore,
    val dialogRenderer: DialogRenderer,
    private val gameClock: GameClock,
    private val questManager: QuestManager
) {

    fun onInteract(
        talkable: Talkable,
        toolInventory: ToolInventory,
        itemInventory: ItemInventory,
        onStartQuest: (quest: QuestDefinition) -> Unit
    ): Boolean {
        getDialog(
            talkable,
            toolInventory,
            itemInventory,
            gameClock.year,
            gameClock.season,
            gameClock.day,
            onStartQuest
        )?.let {
            dialogRenderer.startDialog(it)
            return true
        }

        return false
    }

    private fun getDialog(
        talkable: Talkable,
        toolInventory: ToolInventory,
        itemInventory: ItemInventory,
        year: Int,
        season: Season,
        day: Int,
        onStartQuest: (quest: QuestDefinition) -> Unit
    ): List<DialogLine>? {

        val quests: List<QuestDefinition> = questManager
            .getAvailableQuests(talkable, year, season, day)

        val hasQuestsForMe = quests.any {
            it.state.status == QuestStatus.NOT_STARTED ||
                it.state.status == QuestStatus.IN_PROGRESS
        }
        val dialogs = dialogStore.dialogs.firstOrNull { it.npc == talkable.nameId }

        var currentDay = day

        if (!talkable.hasIntroduced) {
            dialogs?.let {
                talkable.hasIntroduced = true
                return it.dialogs["introduction"]
            }
        } else {
            if (hasQuestsForMe) {
                return fetchQuestDialogs(quests, toolInventory, itemInventory, onStartQuest)
            }
            while (currentDay >= 1) {
                val key = "${season.name.lowercase()}.day_$currentDay"
                dialogs?.dialogs?.get(key)?.let { return it }
                currentDay--
            }
        }

        return null
    }

    private fun fetchQuestDialogs(
        quests: List<QuestDefinition>,
        toolInventory: ToolInventory,
        itemInventory: ItemInventory,
        onStartQuest: (quest: QuestDefinition) -> Unit

    ): List<DialogLine>? {
        return quests.firstOrNull()?.let { questDefinition: QuestDefinition ->
            if (questDefinition.state.status == QuestStatus.NOT_STARTED) {
                val questDialogs =
                    questDefinition.questDialogs[questDefinition.state.status]
                questManager.startQuest(questDefinition.questId)
                onStartQuest.invoke(questDefinition)
                questDialogs
            } else {
                val changeableTools =
                    toolInventory.tools.filterIsInstance<ChangeableState>()
                val containsToolReq =
                    toolInventory.tools.map { tool -> tool.name.lowercase() }
                        .contains(questDefinition.questRequirement.itemId.lowercase())
                if (containsToolReq && changeableTools.map { tool -> tool.stateChanged }
                        .all { stateChanged -> stateChanged }) {
                    questManager.updateProgress(questDefinition.questId, 1)
                    changeableTools.forEach { tool -> tool.changeState(false) }
                }

                questDefinition.questDialogs[questDefinition.state.status]?.map { dialog ->
                    if (dialog.text.contains("AMOUNT")) {
                        dialog.text = dialog.text.replace(
                            "AMOUNT",
                            questDefinition.state.progress.toString()
                        )
                    }
                    dialog
                }

            }
        }
    }

    fun startPlayerDialog(noBucket: PlayerDialogs) {
        dialogStore.getPlayerDialog(noBucket)?.let {
            dialogRenderer.startDialog(it)
        }
    }
}
