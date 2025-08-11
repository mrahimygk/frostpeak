package com.mojtabarahimy.frostpeak.controller.quests

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.JsonValue
import com.mojtabarahimy.frostpeak.controller.dialog.DialogLine
import com.mojtabarahimy.frostpeak.data.quests.QuestDefinition
import com.mojtabarahimy.frostpeak.data.quests.QuestPrerequisites
import com.mojtabarahimy.frostpeak.data.quests.QuestRequirement
import com.mojtabarahimy.frostpeak.data.quests.QuestReward
import com.mojtabarahimy.frostpeak.data.quests.QuestState
import com.mojtabarahimy.frostpeak.data.quests.QuestStatus
import com.mojtabarahimy.frostpeak.data.quests.QuestTime
import com.mojtabarahimy.frostpeak.data.time.Season
import com.mojtabarahimy.frostpeak.entities.items.ItemStore
import com.mojtabarahimy.frostpeak.entities.tools.ToolStore

class QuestStore(
    private val toolStore: ToolStore,
    private val itemStore: ItemStore
) {

    private var questDefinitions: List<QuestDefinition>

    init {
        questDefinitions = loadQuests()
    }

    private fun loadQuests(): List<QuestDefinition> {
        val file: FileHandle = Gdx.files.internal("quests/quests.json")
        val root = JsonReader().parse(file)

        return root.map { questJson: JsonValue ->
            val id = questJson.getString("id")
            val relationshipRewards: JsonValue = questJson.get("rewards").get("relationship")
            val dialogs = questJson.get("dialog")
            val prerequisites = questJson.get("prerequisites")
            val toolNames: List<String> = prerequisites.get("tools")
                .map { it.asString().lowercase() }

            QuestDefinition(
                questId = id,
                state = QuestState(id, QuestStatus.NOT_STARTED),
                nextQuest = null,
                giverName = questJson.getString("giver").lowercase(),
                title = questJson.getString("title"),
                description = questJson.getString("description"),
                questPrerequisites = QuestPrerequisites(
                    listOf(), // TODO: get from itemStore
                    toolStore.allTools.filter { toolNames.contains(it.name.lowercase()) }
                ),
                questRequirement = QuestRequirement(
                    questJson.get("requirements").getString("item"),
                    questJson.get("requirements").getInt("count")
                ),
                rewards = QuestReward(
                    money = questJson.get("rewards").getInt("money"),
                    relationship = relationshipRewards.associate { it.name to it.asInt() }
                ),
                availableTime = QuestTime(
                    questJson.get("available_after").getInt("year"),
                    Season.valueOf(
                        questJson.get("available_after").getString("season").uppercase()
                    ),
                    questJson.get("available_after").getInt("day")
                ),
                questDialogs = dialogs.associate { node ->
                    val linesArray: List<String> = node.mapNotNull { it.asString() }
                    QuestStatus.valueOf(node.name.uppercase()) to linesArray.map { DialogLine(it) }
                }
            )
        }
    }

    fun getAvailable(
        name: String,
        year: Int,
        season: Season,
        day: Int
    ): List<QuestDefinition> = getByName(name).filter { item ->
        item.availableTime.let { time ->
            year >= time.year &&
                season == time.season &&
                day >= time.day
        }
    }

    private fun getByName(name: String): List<QuestDefinition> =
        questDefinitions.filter { it.giverName == name }

    fun getStates(): Map<String, QuestState> = questDefinitions.associate {
        it.questId to it.state
    }

    fun getById(id: String): QuestDefinition? {
        return questDefinitions.firstOrNull { it.questId == id }
    }
}
