package com.mojtabarahimy.frostpeak.controller.dialog

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.JsonValue
import com.mojtabarahimy.frostpeak.data.DialogData
import com.mojtabarahimy.frostpeak.data.NpcNamesList

class DialogStore {

    val dialogs = mutableListOf<DialogData>()

    init {
        loadDialogs()
    }

    private fun loadDialogs() {

        dialogs.clear()

        NpcNamesList.entries.forEach {
            loadDialog(it.name.lowercase())
        }

        loadPlayerDialogs()
    }

    private fun loadPlayerDialogs() {
        val file = Gdx.files.internal("npc/dialogs/player.json")
        val json = JsonReader().parse(file)
        val playerDialogs = mutableMapOf<String, List<DialogLine>>()

        PlayerDialogs.entries.forEach { dialogEntry ->
            val key = dialogEntry.name.lowercase()
            val node = json.get(key)
            var isOneTime = false
            val linesArray: List<String> = if (node.isArray) {
                node
            } else {
                isOneTime = node.getBoolean("is_one_time")
                node.get("data")
            }.mapNotNull { it.asString() }
            val dialogLinesList: List<DialogLine> = linesArray.map {
                DialogLine(it, isOneTime = isOneTime)
            }
            playerDialogs[key] = dialogLinesList
        }

        dialogs.add(DialogData("player", playerDialogs))

    }

    private fun loadDialog(npcName: String) {
        val file = Gdx.files.internal("npc/dialogs/${npcName}.json")
        val json = JsonReader().parse(file)
        val npcDialogs = mutableMapOf<String, List<DialogLine>>()

        fun parseNode(path: String, node: JsonValue) {
            if (node.isArray) {
                val linesArray: List<String> = node.mapNotNull { it.asString() }
                val dialogLinesList: List<DialogLine> =
                    linesArray.map { DialogLine(it) } // TODO: add portrait or emotion
                npcDialogs[path] = dialogLinesList
            } else if (node.isObject) {
                var child = node.child
                while (child != null) {
                    val childPath = if (path.isEmpty()) child.name else "$path.${child.name}"
                    parseNode(childPath, child)
                    child = child.next
                }
            }
        }

        parseNode("", json)

        dialogs.add(DialogData(npcName, npcDialogs))

        // Docs:
        //dialogs.firstOrNull()?.dialogs?.get("introduction")       // ➜ List of introduction lines
        //dialogs.firstOrNull()?.dialogs?.get("spring.day_1")       // ➜ List of spring day 1 lines
        //dialogs.firstOrNull()?.dialogs?.get("festivals.alaki.end")
    }

    fun getPlayerDialog(dialogEntryKey: PlayerDialogs): List<DialogLine>? {
        return dialogs.firstOrNull { it.npc == "player" }?.dialogs?.get(dialogEntryKey.name.lowercase())
    }

}
