package com.mojtabarahimy.frostpeak.data.quests

import com.mojtabarahimy.frostpeak.controller.dialog.DialogLine
import com.mojtabarahimy.frostpeak.data.time.Season
import com.mojtabarahimy.frostpeak.entities.items.Item
import com.mojtabarahimy.frostpeak.entities.tools.Tool

data class QuestDefinition(
    val questId: String,
    val state: QuestState,
    val nextQuest: QuestDefinition? = null,
    val giverName: String,
    val title: String,
    val description: String,
    val questPrerequisites: QuestPrerequisites,
    val questRequirement: QuestRequirement,
    val rewards: QuestReward,
    val availableTime: QuestTime,
    val questDialogs: Map<QuestStatus, List<DialogLine>>,
)

data class QuestRequirement(
    val itemId: String,
    val count: Int
)

data class QuestPrerequisites(
    val items: List<Item>,
    val tools: List<Tool>
)

data class QuestReward(
    val money: Int,
    val relationship: Map<String, Int>
)

data class QuestTime(
    val year: Int,
    val season: Season,
    val day: Int,
)
