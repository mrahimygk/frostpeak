package com.mojtabarahimy.frostpeak.controller.npc

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.JsonReader
import com.mojtabarahimy.frostpeak.data.NpcNamesList
import com.mojtabarahimy.frostpeak.data.time.GameClock
import com.mojtabarahimy.frostpeak.data.time.Season
import com.mojtabarahimy.frostpeak.entities.Direction

class NpcScheduleStore(clock: GameClock) {

    private val schedules = mutableMapOf<String, List<NpcSchedule>>()
    private val todaySchedules = mutableMapOf<String, List<NpcSchedule>?>()
    private val npcScheduleAtMoment = mutableMapOf<String, NpcSchedule?>()

    init {
        schedules.clear()

        NpcNamesList.entries.forEach {
            loadSchedules(it.name.lowercase())
        }

        fillTodaySchedules(clock)
    }

    fun fillTodaySchedules(clock: GameClock) {
        todaySchedules.clear()
        NpcNamesList.entries.forEach { npc ->
            todaySchedules[npc.name.lowercase()] = schedules[npc.name.lowercase()]
                ?.filter {
                    it.year == clock.year &&
                        it.season == clock.season &&
                        it.day == clock.day
                }
                ?.sortedBy {
                    it.hours * 60 + it.minutes
                }
        }
    }

    fun fillHourlySchedules(clock: GameClock) {
        npcScheduleAtMoment.clear()
        NpcNamesList.entries.forEach { npc ->
            npcScheduleAtMoment[npc.name.lowercase()] =
                getScheduleAtTime(npc.name.lowercase(), clock)
        }
    }

    private fun loadSchedules(name: String) {
        val file = Gdx.files.internal("npc/schedules/$name.json")
        val schedulesJsonArray = JsonReader().parse(file).get("schedules")
        val schedules = mutableListOf<NpcSchedule>()

        for (node in schedulesJsonArray) {
            val time = node.getString("time").split(":")
            val target = node.get("target")

            schedules.add(
                NpcSchedule(
                    year = node.getInt("year"),
                    season = Season.valueOf(node.getString("season").uppercase()),
                    day = node.getInt("day"),
                    hours = time[0].toInt(),
                    minutes = time[1].toInt(),
                    target = Vector2(target.getInt("x").toFloat(), target.getInt("y").toFloat()),
                    action = NpcAction.valueOf(node.getString("action").uppercase()),
                    facing = Direction.valueOf(node.getString("facing").uppercase())
                )
            )
        }

        this.schedules[name] = schedules

    }

    fun getCurrentSchedule(npcName: String) = npcScheduleAtMoment[npcName]

    private fun getScheduleAtTime(npcName: String, clock: GameClock): NpcSchedule? {
        val currentMinutes = clock.hours * 60 + clock.minutes

        return todaySchedules[npcName]?.lastOrNull { schedule ->
            val scheduleMinutes = schedule.hours * 60 + schedule.minutes
            scheduleMinutes <= currentMinutes
        }
    }
}
